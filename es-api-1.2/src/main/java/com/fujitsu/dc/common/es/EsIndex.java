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

import com.fujitsu.dc.common.es.query.DcQueryBuilder;
import com.fujitsu.dc.common.es.response.DcBulkResponse;
import com.fujitsu.dc.common.es.response.DcMultiSearchResponse;
import com.fujitsu.dc.common.es.response.DcSearchResponse;

/**
 * Elasticsearch Index操作用のI/F.
 */
public interface EsIndex {

    /**
     * cell登録／検索用のルーティングキーワード.
     */
    String CELL_ROUTING_KEY_NAME = "pcsCell";
    /**
     * 管理Indexを示すIndexカテゴリ.
     */
    String CATEGORY_AD = "ad";
    /**
     * Unit User Indexを示すIndexカテゴリ.
     */
    String CATEGORY_USR = "usr";

    /**
     * タイプ名を指定してタイプ操作用オブジェクトを取得する.
     * @return name
     */
    String getName();

    /**
     * @return カテゴリ文字列
     */
    String getCategory();

    /**
     * Indexを生成する.
     */
    void create();

    /**
     * Indexを削除します.
     */
    void delete();

    /**
     * ドキュメントを検索.
     * @param routingId routingId
     * @param query クエリ情報
     * @return ES応答
     */
    DcSearchResponse search(String routingId, Map<String, Object> query);

    /**
     * ドキュメントを検索.
     * @param routingId routingId
     * @param query クエリ情報
     * @return ES応答
     */
    DcSearchResponse search(String routingId, DcQueryBuilder query);

    /**
     * ドキュメントをマルチ検索.
     * @param routingId routingId
     * @param queryList クエリ情報一覧
     * @return ES応答
     */
    DcMultiSearchResponse multiSearch(String routingId, List<Map<String, Object>> queryList);

    /**
     * クエリ指定の一括削除機能.
     * @param routingId routingId
     * @param deleteQuery 削除対象を指定するクエリ
     */
    void deleteByQuery(String routingId, DcQueryBuilder deleteQuery);

    /**
     * バルクでドキュメントを登録/更新/削除する.
     * @param routingId routingId
     * @param datas バルクドキュメント
     * @param isWriteLog リクエスト情報のログ出力有無
     * @return ES応答
     */
    DcBulkResponse bulkRequest(final String routingId, final List<EsBulkRequest> datas, boolean isWriteLog);

    /**
     * インデックスの設定を更新する.
     * @param index インデックス名
     * @param settings 更新するインデックス設定
     * @return Void
     */
    Void updateSettings(String index, Map<String, String> settings);
}
