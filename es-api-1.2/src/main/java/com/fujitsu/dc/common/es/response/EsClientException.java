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
package com.fujitsu.dc.common.es.response;

/**
 * EsModel関連の例外を扱うクラス. これが発生したときはすべて500系エラーとして扱うので、RuntimeExceptionを継承しており、 利用者はこれをcatchしてもよいがせずに放置してもよいという考え方としている。
 */
public class EsClientException extends RuntimeException {
    /**
     * デフォルトシリアルバージョンID.
     */
    private static final long serialVersionUID = 1L;

    private Throwable causeThrowable;

    /**
     * コンストラクタ.
     * @param msg メッセージ
     * @param cause 親例外
     */
    public EsClientException(final String msg, final Throwable cause) {
        super(msg, cause);
        // if (cause instanceof DocumentAlreadyExistsException) {
        // this.causeThrowable = new DcDocumentAlreadyExistsException(cause);
        // } else if (cause instanceof SearchPhaseExecutionException) {
        // this.causeThrowable = new DcSearchPhaseExecutionException(cause);
        // } else if (cause instanceof ElasticSearchException) {
        // this.causeThrowable = new DcElasticsearchException(cause);
        // } else {
        this.causeThrowable = cause;
        // }
    }

    /**
     * コンストラクタ.
     * @param msg メッセージ
     */
    public EsClientException(final String msg) {
        super(msg);
        this.causeThrowable = null;
    }

    @Override
    public Throwable getCause() {
        return this.causeThrowable;
    }

    /**
     * ESのIndexが存在しない場合の例外を扱うクラス.
     */
    public static class DcDocumentAlreadyExistsException extends RuntimeException {
        /**
         * デフォルトシリアルバージョンID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * コンストラクタ.
         * @param cause 親例外
         */
        public DcDocumentAlreadyExistsException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * ESのIndexが存在しない場合の例外を扱うクラス.
     */
    public static class DcSearchPhaseExecutionException extends RuntimeException {
        /**
         * デフォルトシリアルバージョンID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * コンストラクタ.
         * @param cause 親例外
         */
        public DcSearchPhaseExecutionException(Throwable cause) {
            super(cause);
        }
    }

    /**
     * ESのクエリ指定削除にて全データの削除に失敗した場合の例外を扱うクラス.
     */
    public static class EsDeleteByQueryException extends EsClientException {
        /**
         * デフォルトシリアルバージョンID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * 例外メッセージ定義.
         */
        public static final String ES_DELETE_BY_QUERY_MSG = "Es delete by query failed [FailedCount: %d].";

        /**
         * コンストラクタ.
         * @param failedCount 失敗件数
         */
        public EsDeleteByQueryException(long failedCount) {
            super(String.format(ES_DELETE_BY_QUERY_MSG, failedCount));
        }
    }

    /**
     * ESのIndexが存在しない場合の例外を扱うクラス.
     */
    public static class EsIndexMissingException extends EsClientException {
        /**
         * デフォルトシリアルバージョンID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * 例外メッセージ定義.
         */
        public static final String ES_IDX_MISSING_MSG = "Es index missing";

        /**
         * コンストラクタ.
         * @param cause 親例外
         */
        public EsIndexMissingException(final Throwable cause) {
            super(ES_IDX_MISSING_MSG, cause);
        }
    }

    /**
     * ESのマルチ検索でクエリに空の配列やnullといった不正な検索条件を指定した場合の例外を扱うクラス.
     */
    public static class EsMultiSearchQueryParseException extends EsClientException {
        /**
         * デフォルトシリアルバージョンID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * 例外メッセージ定義.
         */
        public static final String MESSAGE = "Es query parse error to multiSearch.";

        /**
         * コンストラクタ.
         */
        public EsMultiSearchQueryParseException() {
            super(String.format(MESSAGE));
        }
    }

    /**
     * ESから応答がない場合の例外を扱うクラス.
     */
    public static class EsNoResponseException extends EsClientException {
        /**
         * デフォルトシリアルバージョンID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * 例外メッセージ定義.
         */
        public static final String ES_NO_RESPONSE_MSG = "Es no response";

        /**
         * コンストラクタ.
         * @param msg 例外発生時のメッセージ
         * @param cause 親例外
         */
        public EsNoResponseException(final String msg, final Throwable cause) {
            super(msg, cause);
        }
    }

    /**
     * ESのIndexが存在しない場合の例外を扱うクラス.
     */
    public static class EsSchemaMismatchException extends EsClientException {
        /**
         * デフォルトシリアルバージョンID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * 例外メッセージ定義.
         */
        public static final String ES_SCHEMA_MIOSMATCH_MSG = "Es shema mismatch";

        /**
         * コンストラクタ.
         * @param cause 親例外
         */
        public EsSchemaMismatchException(final Throwable cause) {
            super(ES_SCHEMA_MIOSMATCH_MSG, cause);
        }
    }

    /**
     * ESのバージョン不一致の場合の例外を扱うクラス.
     */
    public static class EsVersionConflictException extends EsClientException {
        /**
         * デフォルトシリアルバージョンID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * 例外メッセージ定義.
         */
        public static final String ES_VERSION_CONFLICT_MSG = "Es version conflict";

        /**
         * コンストラクタ.
         * @param cause 親例外
         */
        public EsVersionConflictException(final Throwable cause) {
            super(ES_VERSION_CONFLICT_MSG, cause);
        }
    }

    /**
     * EsのIndexがすでに存在している場合にの例外を扱うクラス.
     */
    public static class EsIndexAlreadyExistsException extends EsClientException {
        /**
         * デフォルトシリアルバージョンID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * 例外メッセージ定義.
         */
        public static final String ES_INDEX_ALREADY_EXIST_MSG = "Es index already exists";

        /**
         * コンストラクタ.
         * @param cause 親例外
         */
        public EsIndexAlreadyExistsException(final Throwable cause) {
            super(ES_INDEX_ALREADY_EXIST_MSG, cause);
        }
    }
}

