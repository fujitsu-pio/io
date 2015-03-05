/**
 * personium.io
 * Copyright 2014 FUJITSU LIMITED
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fujitsu.dc.common.es.impl;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.NoShardAvailableActionException;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.common.util.concurrent.UncategorizedExecutionException;
import org.elasticsearch.transport.NodeDisconnectedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.es.response.EsClientException;

/**
 * Elasticsearchへリトライ可能な処理を記述する抽象クラス.
 * リクエストの実際の実装は、抽象メソッド {@link #doProcess()} に記述すること.
 * 呼出し元は、#doRequest()メソッドを呼び出すこと。
 * @param <T> 正常終了時のレスポンスの型. void の場合は Void
 */
abstract class AbstractRetryableEsRequest<T> {

    static Logger log = LoggerFactory.getLogger(AbstractRetryableEsRequest.class);

    int retryCount = 0;
    long retryInterval = 0;
    String description;

    // 初回呼び出し時のみ true. リトライに入っている間は falseとなる。
    boolean firstAttempt = true;

    /**
     * コンストラクタ.
     * @param argRetryCount リトライ回数
     * @param argRetryInterval リトライ間隔
     * @param requestDesc ログ出力時に利用されるメソッド記述.
     */
    public AbstractRetryableEsRequest(int argRetryCount, long argRetryInterval, String requestDesc) {
        retryCount = argRetryCount;
        retryInterval = argRetryInterval;
        description = requestDesc;
    }

    /**
     * #onParticularError()メソッド処理後に、リトライを継続させるために投げるオブジェクト.
     */
    @SuppressWarnings("serial")
    public static class ContinueRetry extends RuntimeException {
        /**
         * コンストラクタ.
         */
        public ContinueRetry() {
            super();
        }
    }

    /**
     * ESへのリクエストを実行する.
     * リクエスト初回に以下の4種の例外のいずれかが発生した場合、リトライ処理を行う.
     * <ul>
     * <li>NodeDisconnectedException</li>
     * <li>NoNodeAvailableException</li>
     * <li>NoShardAvailableActionException</li>
     * <li>ClusterBlockException</li>
     * </ul>
     * @return レスポンスオブジェクト
     */
    public T doRequest() {
        firstAttempt = true;
        boolean continueRetry = false;
        try {
            return doProcess();
        } catch (ElasticsearchException e) {
            if (isParticularError(e)) {
                // 検出された例外を特別扱いする場合の処理呼び出し
                try {
                    return onParticularError(e);
                } catch (ContinueRetry e2) {
                    // リトライ処理へ移行する.
                    continueRetry = true;
                } catch (ElasticsearchException e2) {
                    // #onParticulorError()内で適切に対処されなかった ElasticsearchExceptionは
                    // EsClientExceptionラップして投げる.
                    throw new EsClientException(description + " failed", e);
                }
            }
            // translogのRead時のポインタ位置不正による例外(ES1.2.1のバグ)の場合には、flushを実行しリトライする
            // UncategorizedExecutionExceptionはtranslog読込以外の例外の場合にもスローされてくる可能性があるが、
            // 判別できないため、それらの場合にも本ルートに乗せる
            if (e instanceof UncategorizedExecutionException) {
                flushTransLog();
            }
            log.info(e.getClass().getName() + " : " + e.getMessage());
            // 以下の例外の場合はリトライをする。
            if (continueRetry
                    || e instanceof NodeDisconnectedException || e instanceof NoNodeAvailableException
                    || e instanceof NoShardAvailableActionException || e instanceof ClusterBlockException
                    || e instanceof UncategorizedExecutionException) {
                log.info("Proceed to retry loop.");
                continueRetry = false; // 念のため
                return retryRequest();
            }
            // 上記以外の場合、リトライの意味はないため、EsClientExceptionにラップしてそのまま投げる。
            throw new EsClientException(description + " failed", e);
        }
    }

    /**
     * Elasticsearchへのリクエストを実装するための抽象メソッド.
     * 利用者はこのメソッドをオーバーライドすること.
     * @return レスポンス
     */
    abstract T doProcess();

    /**
     * リトライ時、引数に指定された例外を特別扱いする場合、trueを返すようにオーバーライドすること.
     * これにより、#onParticularErrorメソッドが呼び出される.
     * 標準実装では, 常に falseを返す.
     * @param e 検査対象の例外
     * @return true: 正常終了として扱う場合, false: 左記以外の場合
     */
    boolean isParticularError(ElasticsearchException e) {
        return false;
    }

    /**
     * 特定の例外が発生した場合に、正常終了で復帰させる等、特定の処理を行うためにはこのメソッドをオーバーライドすること.
     * 本メソッドは、#isParticularError() が trueを返した時のみ呼び出される.
     * <ul>
     * <li>本メソッドから AbstractRetryableEsRequest.ContinueRetry例外が投げられた場合、引き続きリトライ処理が行われる。</li>
     * <li>本メソッドから ElasticsearchExceptionが投げられた場合、#doRequest()の呼び出し元には、EsClientExceptionに ラップされた例外が返される。</li>
     * <li>例外を返さずに何らかの復帰値を返した場合は、呼び出し元にはその値が返される。</li>
     * </ul>
     * 標準実装では引数に与えられた例外をそのまま投げ返す.
     * @param e 特定例外
     * @return レスポンス
     */
    T onParticularError(ElasticsearchException e) {
        throw e;
    }

    /**
     * Elasticsearchへのリクエストをリトライする.
     * 以下の4つの例外が発生した場合のみリトライし、それ以外は、EsClientExceptionを投げて中断する。
     * <ul>
     * <li>NodeDisconnectedException</li>
     * <li>NoNodeAvailableException</li>
     * <li>NoShardAvailableActionException</li>
     * <li>ClusterBlockException</li>
     * </ul>
     * @return レスポンス
     */
    private T retryRequest() {
        firstAttempt = false;
        Exception lastError = null;
        for (int i = 0; i < retryCount; i++) {
            log.info(description + ": retry " + (i + 1));
            try {
                // 少し待機
                Thread.sleep(retryInterval);
                // 再度リクエストを実行する。
                return doProcess();
            } catch (ElasticsearchException e) {
                lastError = e;
                if (isParticularError(e)) {
                    // 検出された例外を特別扱いする場合の処理呼び出し
                    try {
                        return onParticularError(e);
                    } catch (ContinueRetry e2) {
                        continue;
                    } catch (ElasticsearchException e2) {
                        // #onParticulorError()内で適切に対処されなかった ElasticsearchExceptionは
                        // EsClientExceptionラップして投げる.
                        throw new EsClientException(description + " failed", e);
                    }
                } else if (e instanceof UncategorizedExecutionException) {
                    // translogのRead時のポインタ位置不正による例外(ES1.2.1のバグ)の場合には、flushを実行しリトライする
                    // UncategorizedExecutionExceptionはtranslog読込以外の例外の場合にもスローされてくる可能性があるが、
                    // 判別できないため、それらの場合にも本ルートに乗せる
                    flushTransLog();
                    continue;
                } else if (e instanceof NodeDisconnectedException || e instanceof NoNodeAvailableException
                        || e instanceof NoShardAvailableActionException || e instanceof ClusterBlockException) {
                    // これらの例外の場合、ESの状態が不正か通信エラー等の原因が考えられるため、リトライを継続。
                    continue;
                }
                // 上記以外の例外は、明確なエラー発生と考えられるため、例外を返す。
                throw new EsClientException(description + " failed", e);
            } catch (InterruptedException e) {
                // #sleep()中の例外。外部から中断された場合などが想定される。
                throw new EsClientException(description + " failed", e);
            }
        }
        // リトライ回数を超えた場合、最後のエラーを返却する。
        throw new EsClientException.EsNoResponseException(description + " failed", lastError);
    }

    abstract EsTranslogHandler getEsTranslogHandler();

    /**
     * translogをflushする.
     */
    protected void flushTransLog() {
        getEsTranslogHandler().flushTranslog();
    }
}
