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
package com.fujitsu.dc.common.es;

import java.util.Map;

import org.json.simple.JSONObject;

/**
 * Elasticsearchへのリクエストに応じたログ種別を扱うモデル.
 */
public class EsRequestLogInfo {

    /** インデックス名. */
    private String index;
    /** ESタイプ名 . */
    private String type;
    /** ドキュメントのUUID. */
    private String id;
    /** ルーティングID. */
    private String routingId;
    /** ドキュメントのリクエストボディ. */
    private Map<String, Object> data;
    /** 操作タイプ名. */
    private String opType;
    /** ドキュメントのバージョン. */
    private long version;

    /**
     * コンストラクタ.
     * @param index インデックス名
     * @param type ESタイプ名
     * @param id ドキュメントのid
     * @param routingId routingId
     * @param data データ
     * @param opType 操作タイプ
     * @param version version番号
     */
    public EsRequestLogInfo(String index,
            String type,
            String id,
            String routingId,
            Map<String, Object> data,
            String opType,
            long version) {
        this.index = index;
        this.type = type;
        this.id = id;
        this.routingId = routingId;
        this.data = data;
        this.opType = opType;
        this.version = version;
    }

    /**
     * @return the data
     */
    public String getDataAsString() {
        return JSONObject.toJSONString(data);
    }

    /**
     * @return the data
     */
    public Map<String, Object> getData() {
        return data;
    }

    /**
     * @return the index
     */
    public String getIndex() {
        return this.index;
    }

    /**
     * @return the id
     */
    public String getId() {
        return this.id;
    }

    /**
     * @return the routingId
     */
    public String getRoutingId() {
        return this.routingId;
    }

    /**
     * @return the opType
     */
    public String getOpType() {
        return this.opType;
    }

    /**
     * @return the version
     */
    public long getVersion() {
        return this.version;
    }

    /**
     * @return the type
     */
    public String getType() {
        return this.type;
    }

}
