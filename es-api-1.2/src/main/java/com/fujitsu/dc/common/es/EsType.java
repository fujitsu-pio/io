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

import java.util.List;
import java.util.Map;

import com.fujitsu.dc.common.es.response.DcDeleteResponse;
import com.fujitsu.dc.common.es.response.DcGetResponse;
import com.fujitsu.dc.common.es.response.DcIndexResponse;
import com.fujitsu.dc.common.es.response.DcMappingMetaData;
import com.fujitsu.dc.common.es.response.DcMultiSearchResponse;
import com.fujitsu.dc.common.es.response.DcPutMappingResponse;
import com.fujitsu.dc.common.es.response.DcSearchResponse;

/**
 * Elasticsearch Type操作用のI/F.
 */
public interface EsType {

    /**
     * インデックスを取得する.
     * @return 応答
     */
    String getIndexName();

    /**
     * Typeを取得する.
     * @return 応答
     */
    String getType();

    /**
     * realtimeモード指定でドキュメントを取得する. ドキュメントを取得する.
     * @param id ドキュメントのID
     * @return 応答
     */
    DcGetResponse get(String id);

    /**
     * ドキュメントの１件取得.
     * @param id ドキュメントのID
     * @param realtime リアルタイムモードなら真
     * @return 応答
     */
    DcGetResponse get(String id, boolean realtime);

    /**
     * ドキュメント新規作成.
     * @param data ドキュメント
     * @return ES応答
     */
    DcIndexResponse create(@SuppressWarnings("rawtypes") Map data);

    /**
     * ドキュメント新規作成.
     * @param id ID
     * @param data ドキュメント
     * @return ES応答
     */
    DcIndexResponse create(String id, @SuppressWarnings("rawtypes") Map data);

    /**
     * ドキュメント更新.
     * @param id ID
     * @param data ドキュメント
     * @param version version番号
     * @return ES応答
     */
    DcIndexResponse update(String id, @SuppressWarnings("rawtypes") Map data, long version);

    /**
     * ドキュメント更新.
     * @param id ID
     * @param data ドキュメント
     * @return ES応答
     */
    DcIndexResponse update(String id, @SuppressWarnings("rawtypes") Map data);

    /**
     * ドキュメントを検索.
     * @param query クエリ情報
     * @return ES応答
     */
    DcSearchResponse search(Map<String, Object> query);

    /**
     * ドキュメントをマルチ検索.
     * @param queryList マルチ検索用のクエリ情報リスト
     * @return ES応答
     */
    DcMultiSearchResponse multiSearch(List<Map<String, Object>> queryList);

    /**
     * Delete a document.
     * @param docId Document id to delete
     * @return 応答
     */
    DcDeleteResponse delete(String docId);

    /**
     * Delete a document.
     * @param docId Document id to delete
     * @param version The version of the document to delete
     * @return 応答
     */
    DcDeleteResponse delete(String docId, long version);

    /**
     * Mapping定義を取得する.
     * @return Mapping定義
     */
    DcMappingMetaData getMapping();

    /**
     * Mapping定義を更新する.
     * @param mappings Mapping定義
     * @return ES応答
     */
    DcPutMappingResponse putMapping(Map<String, Object> mappings);
}
