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
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.support.broadcast.BroadcastShardOperationFailedException;
import org.elasticsearch.index.engine.FlushNotAllowedEngineException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ESのTranslogをflushするリクエストを飛ばす機能を追加するための基底クラス.
 */
public class EsTranslogHandler {

    static Logger log = LoggerFactory.getLogger(EsTranslogHandler.class);

    private int retryCount;
    private int retryInterval;
    private InternalEsClient esClient;
    private String indexName;

    /**
     * コンストラクタ.
     * @param retryCount リトライ回数
     * @param retryInterval リトライ間隔
     * @param esClient elasticsearchに接続するクライアント
     * @param indexName インデックス名
     */
    public EsTranslogHandler(int retryCount, int retryInterval, InternalEsClient esClient, String indexName) {
        this.retryCount = retryCount;
        this.retryInterval = retryInterval;
        this.esClient = esClient;
        this.indexName = indexName;
    }

    /**
     * translogをflushする.
     */
    protected void flushTranslog() {
        FlushTranslogRetryableRequest request = new FlushTranslogRetryableRequest(retryCount, retryInterval);
        request.doRequest();
    }

    /**
     * Elasticsearchへの flush処理実装.
     */
    class FlushTranslogRetryableRequest extends AbstractRetryableEsRequest<FlushResponse> {
        public FlushTranslogRetryableRequest(int retryCount, long retryInterval) {
            super(retryCount, retryInterval, "Es translog flush");
        }

        @Override
        FlushResponse doProcess() {
            FlushResponse res = esClient.flushTransLog(indexName).actionGet();
            int failedShards = res.getFailedShards();
            // 例外が発生しないがflushのエラーになった場合は、他のリクエストでのflushにまかせるため、ここではエラーとはしていない。
            if (failedShards > 0) {
                String message = String.format("ES translog flush failed. index[%s] failedShardsCount[%d]", indexName,
                        failedShards);
                log.info(message);
            }
            return res;
        }

        /**
         * flushの失敗時のエラーの場合分け.< br/>
         * 以下の場合はすでに他のスレッドからflushが依頼されたとみなし、リトライは行わない。< br/>
         * <ul>
         * <li>BroadcastShardOperationFailedException</li>
         * <li>FlushNotAllowedEngineException</li>
         * </ul>
         * @param e 検査対象の例外
         * @return true: 正常終了として扱う場合, false: 左記以外の場合
         */
        @Override
        boolean isParticularError(ElasticsearchException e) {
            return e instanceof BroadcastShardOperationFailedException
                    || e instanceof FlushNotAllowedEngineException;
        }

        @Override
        FlushResponse onParticularError(ElasticsearchException e) {
            String message = String.format("ES translog flush failed. index[%s] cause[%s]", indexName,
                    e.toString());
            log.debug(message);
            return null;
        }

        /**
         * translogをflushする.
         */
        @Override
        protected void flushTransLog() {
            // Flushに失敗した場合、更にFlushしても意味がないため、何もしない。
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            // 本メソッドは、flushTranslogメソッドから呼び出されるが、本クラス内からさらにflushすることはないため、nullを返す。
            return null;
        }
    }

}
