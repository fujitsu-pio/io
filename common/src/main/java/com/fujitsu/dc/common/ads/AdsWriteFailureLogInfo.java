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
package com.fujitsu.dc.common.ads;

/**
 * ADS書き込み失敗ログへの出力情報クラス.
 */
public class AdsWriteFailureLogInfo {

    private static final String LOG_DELIMITER = "\t";
    private static final int LOG_FIELD_NUMBER = 8;

    private static final int LOG_FIELD_INDEX_NAME = 0;
    private static final int LOG_FIELD_TYPE = 1;
    private static final int LOG_FIELD_LOCK_KEY = 2;
    private static final int LOG_FIELD_ROUTING_ID = 3;
    private static final int LOG_FIELD_UUID = 4;
    private static final int LOG_FIELD_OPERATION = 5;
    private static final int LOG_FIELD_ES_VERSION = 6;
    private static final int LOG_FIELD_UPDATED = 7;

    /**
     * ADSへの書き込みに失敗した際の操作種別.
     */
    public enum OperationKind {
        /** データの新規作成. */
        CREATE,
        /** データの更新. */
        UPDATE,
        /** データの削除. */
        DELETE,
        /** 管理DBへのレコード作成. */
        PCS_MANAGEMENT_INSERT;

        /**
         * 文字列表現からenumを取得する.
         * @param value enumの文字列表現
         * @return valueに対応するenum値
         */
        public static OperationKind fromValue(String value) {
            for (OperationKind item : OperationKind.values()) {
                if (item.toString().equals(value)) {
                    return item;
                }
            }
            throw new IllegalArgumentException(value);
        }
    };

    /** 書き込み先インデックス名 . */
    private String indexName;
    /** Elasticsearch上のタイプ名. */
    private String type;
    /** MySQLへの書き込み時にPCSとの排他制御をするためのロックキー. */
    private String lockKey;
    /** 補正データ取得時に使用するroutingId. */
    private String routingId;
    /** 補正データのUUID. */
    private String uuid;
    /** 操作種別. */
    private OperationKind operation;
    /** Elasticsearch上に存在するドキュメントのバージョン情報. */
    private long esVersion;
    /** PCSで発行したドキュメントの更新時刻 . */
    private long updated;

    /**
     * デフォルトコンストラクタ.
     */
    private AdsWriteFailureLogInfo() {
    }

    /**
     * コンストラクタ.
     * @param indexName 書き込み先インデックス名
     * @param type Elasticsearch上のタイプ名
     * @param lockKey MySQLへの書き込み時にPCSとの排他制御をするためのロックキー
     * @param routingId 補正データ取得時に使用するroutingId
     * @param uuid 補正データのUUID
     * @param operation 操作種別
     * @param esVersion Elasticsearch上に存在するドキュメントのバージョン情報
     * @param updated PCSで発行したドキュメントの更新時刻
     */
    public AdsWriteFailureLogInfo(String indexName,
            String type,
            String lockKey,
            String routingId,
            String uuid,
            OperationKind operation,
            long esVersion,
            long updated) {
        this.indexName = indexName;
        this.type = type;
        this.lockKey = lockKey;
        this.routingId = routingId;
        this.uuid = uuid;
        this.operation = operation;
        this.esVersion = esVersion;
        this.updated = updated;
    }

    /**
     * 書き込み先インデックス名を取得する.
     * @return 書き込み先インデックス名
     */
    public String getIndexName() {
        if (null == indexName) {
            return "";
        } else {
            return indexName;
        }
    }

    /**
     * Elasticsearch上のタイプ名を取得する.
     * @return Elasticsearch上のタイプ名
     */
    public String getType() {
        if (null == type) {
            return "";
        } else {
            return type;
        }
    }

    /**
     * MySQLへの書き込み時にPCSとの排他制御をするためのロックキーを取得する.
     * @return MySQLへの書き込み時にPCSとの排他制御をするためのロックキー
     */
    public String getLockKey() {
        if (null == lockKey) {
            return "";
        } else {
            return lockKey;
        }
    }

    /**
     * 補正データ取得時に使用するroutingIdを取得する.
     * @return 補正データのUUID
     */
    public String getRoutingId() {
        if (null == routingId) {
            return "";
        } else {
            return routingId;
        }
    }

    /**
     * 補正データのUUIDを取得する.
     * @return 補正データのUUID
     */
    public String getUuid() {
        if (null == uuid) {
            return "";
        } else {
            return uuid;
        }
    }

    /**
     * 操作種別を取得する.
     * @return 操作種別
     */
    public String getOperation() {
        if (null == operation) {
            return "";
        } else {
            return operation.toString();
        }
    }

    /**
     * Elasticsearch上に存在するドキュメントのバージョン情報を取得する.
     * @return Elasticsearch上に存在するドキュメントのバージョン情報
     */
    public long getEsVersion() {
        return esVersion;
    }

    /**
     * PCSで発行したドキュメントの更新時刻を取得する.
     * @return PCSで発行したドキュメントの更新時刻
     */
    public long getUpdated() {
        return updated;
    }

    /**
     * PCSで発行したドキュメントの更新時刻の文字列表現における桁数が同じかどうかを判定する.<br />
     * 文字列のパーズ時には、本フィールドが途中で切れている場合があるため、現在時刻との文字列表現における桁数を比較し、
     * 全てのデータが読み込まれているかどうかを判定する。
     * リペア処理では、異常が発生した数分後に実行されるため、文字列表現における桁数が異なる場合は考えられにくいため、この手法でチェックする。
     * @return 現在時刻との文字列表現における桁数が同じ場合は trueを、それ以外は falseを返す。
     */
    public boolean isSameUpdatedStringLength() {
        return String.valueOf(System.currentTimeMillis()).length() == this.updated;
    }

    /**
     * ADS書き込み失敗ログへの出力文字列を取得する.
     * @return 生成した出力文字列
     */
    public String toString() {
        return String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n", this.getIndexName(), this.getType(), this.getLockKey(),
                this.getRoutingId(), this.getUuid(), this.getOperation(), this.getEsVersion(), this.getUpdated());
    }

    /**
     * 引数で渡された文字列をパーズする.
     * @param input 入力文字列
     * @return 生成したADS書き込み失敗情報
     * @throws AdsWriteFailureLogException 文字列のパーズに失敗した場合
     */
    public static AdsWriteFailureLogInfo parse(String input) throws AdsWriteFailureLogException {
        AdsWriteFailureLogInfo newObj = new AdsWriteFailureLogInfo();
        if (null == input) {
            return newObj;
        }
        String indata = input.trim();
        String[] fields = indata.split(LOG_DELIMITER);
        if (fields.length != LOG_FIELD_NUMBER) {
            String message = "Invalid field number. [%s]";
            throw new AdsWriteFailureLogException(String.format(message, indata));
        }
        // 各フィールドの格納
        // dc1-coreにて自動的に作成するログであるため、詳細なチェックは実施しない。
        newObj.indexName = fields[LOG_FIELD_INDEX_NAME].trim();
        newObj.type = fields[LOG_FIELD_TYPE].trim();
        newObj.lockKey = fields[LOG_FIELD_LOCK_KEY].trim();
        newObj.routingId = fields[LOG_FIELD_ROUTING_ID].trim();
        newObj.uuid = fields[LOG_FIELD_UUID].trim();
        try {
            newObj.operation = OperationKind.fromValue(fields[LOG_FIELD_OPERATION].trim());
        } catch (IllegalArgumentException e) {
            String message = "Invalid field item. [%s, 'operation kind']";
            throw new AdsWriteFailureLogException(String.format(message, indata), e);
        }
        try {
            newObj.esVersion = Long.parseLong(fields[LOG_FIELD_ES_VERSION].trim());
        } catch (NumberFormatException e) {
            String message = "Invalid field item. [%s, 'ES document version']";
            throw new AdsWriteFailureLogException(String.format(message, indata), e);
        }
        try {
            newObj.updated = Long.parseLong(fields[LOG_FIELD_UPDATED].trim());
        } catch (NumberFormatException e) {
            String message = "Invalid field item. [%s, 'document updated']";
            throw new AdsWriteFailureLogException(String.format(message, indata), e);
        }
        return newObj;
    }
}
