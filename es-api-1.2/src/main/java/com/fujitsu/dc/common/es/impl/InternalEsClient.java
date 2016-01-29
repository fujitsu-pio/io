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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.WriteConsistencyLevel;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthRequest;
import org.elasticsearch.action.admin.cluster.health.ClusterHealthResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequestBuilder;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.flush.FlushRequest;
import org.elasticsearch.action.admin.indices.flush.FlushResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.admin.indices.refresh.RefreshRequest;
import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;
import org.elasticsearch.action.admin.indices.status.IndicesStatusRequestBuilder;
import org.elasticsearch.action.admin.indices.status.IndicesStatusResponse;
import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequestBuilder;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.action.index.IndexRequestBuilder;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchRequest;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.ClusterState;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.fujitsu.dc.common.es.EsBulkRequest;
import com.fujitsu.dc.common.es.EsClient.Event;
import com.fujitsu.dc.common.es.EsClient.EventHandler;
import com.fujitsu.dc.common.es.EsRequestLogInfo;
import com.fujitsu.dc.common.es.response.DcBulkResponse;
import com.fujitsu.dc.common.es.response.DcRefreshResponse;
import com.fujitsu.dc.common.es.response.EsClientException;
import com.fujitsu.dc.common.es.response.EsClientException.EsMultiSearchQueryParseException;
import com.fujitsu.dc.common.es.response.impl.DcBulkResponseImpl;
import com.fujitsu.dc.common.es.response.impl.DcRefreshResponseImpl;

/**
 * ElasticSearchのアクセサクラス.
 */
public class InternalEsClient {
    private static final int DEFAULT_ES_PORT = 9300;

    private TransportClient esTransportClient;
    private boolean routingFlag;

    /**
     * デフォルトコンストラクタ.
     */
    protected InternalEsClient() {
    }

    /**
     * コンストラクタ.
     * @param cluster クラスタ名
     * @param hosts ホスト
     */
    protected InternalEsClient(String cluster, String hosts) {
        routingFlag = true;
        prepareClient(cluster, hosts);
    }

    /**
     * クラスタ名、接続先情報を指定してEsClientのインスタンスを返す.
     * 既に生成されているインスタンスは破棄する
     * @param cluster クラスタ名
     * @param hosts 接続先情報
     * @return EsClientのインスタンス
     */
    public static InternalEsClient getInstance(String cluster, String hosts) {
        return new InternalEsClient(cluster, hosts);
    }

    /**
     * ESとのコネクションを一度明示的に閉じる.
     */
    public void closeConnection() {
        if (esTransportClient == null) {
            return;
        }
        esTransportClient.close();
        esTransportClient = null;
    }

    private void prepareClient(String clusterName, String hostNames) {
        if (esTransportClient != null) {
            return;
        }

        if (clusterName == null || hostNames == null) {
            return;
        }

        Settings st = ImmutableSettings.settingsBuilder()
                .put("cluster.name", clusterName)
                .put("client.transport.sniff", true)
                .build();
        ImmutableList<DiscoveryNode> connectedNodes = null;
        esTransportClient = new TransportClient(st);

        List<EsHost> hostList = parseConfigAndInitializeHostsList(hostNames);
        for (EsHost host : hostList) {
            esTransportClient.addTransportAddress(new InetSocketTransportAddress(host.getName(), host.getPort()));
            connectedNodes = esTransportClient.connectedNodes();
        }
        if (connectedNodes.isEmpty()) {
            throw new EsClientException("Datastore Connection Error.");
        }
        loggingConnectedNode(connectedNodes);
    }

    private List<EsHost> parseConfigAndInitializeHostsList(String hostNames) {
        List<EsHost> hostList = new ArrayList<EsHost>();
        StringTokenizer tokenizer = new StringTokenizer(hostNames, ",");
        while (tokenizer.hasMoreTokens()) {
            String host = tokenizer.nextToken();
            hostList.add(createEsHost(host));
        }
        return hostList;
    }

    private EsHost createEsHost(String host) {
        EsHost hostInfo = null;
        if (hasPortNumber(host)) {
            int index = host.indexOf(":");
            hostInfo = new EsHost(host.substring(0, index), Integer.parseInt(host.substring(index + 1)));
        } else {
            hostInfo = new EsHost(host, DEFAULT_ES_PORT);
        }
        return hostInfo;
    }

    private boolean hasPortNumber(String host) {
        return host.indexOf(":") > 0;
    }

    private void loggingConnectedNode(ImmutableList<DiscoveryNode> list) {
        DiscoveryNode node = list.get(0);
        this.fireEvent(Event.connected, node.address().toString());
    }

    /**
     * elasticsearchのノード情報（ホスト名、ポート番号）を保持するコンテナクラス.
     */
    private static class EsHost {
        private String name;
        private int port;

        public EsHost(String name, int port) {
            this.name = name;
            this.port = port;
        }

        public String getName() {
            return name;
        }

        public int getPort() {
            return port;
        }
    }

    static Map<Event, EventHandler> eventHandlerMap = new HashMap<Event, EventHandler>();

    /**
     * Eventハンドラの登録.
     * @param ev イベントの種類
     * @param handler ハンドラ
     */
    public static void setEventHandler(Event ev, EventHandler handler) {
        eventHandlerMap.put(ev, handler);
    }

    void fireEvent(Event ev, final Object... params) {
        this.fireEvent(ev, null, params);
    }

    void fireEvent(Event ev, EsRequestLogInfo logInfo, final Object... params) {
        EventHandler handler = eventHandlerMap.get(ev);
        if (handler != null) {
            handler.handleEvent(logInfo, params);
        }
    }

    /**
     * Clusterの状態取得.
     * @return 状態Map
     */
    public Map<String, Object> checkHealth() {
        ClusterHealthResponse clusterHealth;
        clusterHealth = esTransportClient.admin().cluster().health(new ClusterHealthRequest()).actionGet();
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("cluster_name", clusterHealth.getClusterName());
        map.put("status", clusterHealth.getStatus().name());
        map.put("timed_out", clusterHealth.isTimedOut());
        map.put("number_of_nodes", clusterHealth.getNumberOfNodes());
        map.put("number_of_data_nodes", clusterHealth.getNumberOfDataNodes());
        map.put("active_primary_shards", clusterHealth.getActivePrimaryShards());
        map.put("active_shards", clusterHealth.getActiveShards());
        map.put("relocating_shards", clusterHealth.getRelocatingShards());
        map.put("initializing_shards", clusterHealth.getInitializingShards());
        map.put("unassigned_shards", clusterHealth.getUnassignedShards());
        return map;
    }

    /**
     * インデックスを作成する.
     * @param index インデックス名
     * @param mappings マッピング情報
     * @return 非同期応答
     */
    public ActionFuture<CreateIndexResponse> createIndex(String index, Map<String, JSONObject> mappings) {
        this.fireEvent(Event.creatingIndex, index);
        CreateIndexRequestBuilder cirb =
                new CreateIndexRequestBuilder(esTransportClient.admin().indices()).setIndex(index);

        // cjkアナライザ設定
        ImmutableSettings.Builder indexSettings = ImmutableSettings.settingsBuilder();
        indexSettings.put("analysis.analyzer.default.type", "cjk");
        cirb.setSettings(indexSettings);

        if (mappings != null) {
            for (Map.Entry<String, JSONObject> ent : mappings.entrySet()) {
                cirb = cirb.addMapping(ent.getKey(), ent.getValue().toString());
            }
        }
        return cirb.execute();
    }

    /**
     * インデックスを削除する.
     * @param index インデックス名
     * @return 非同期応答
     */
    public ActionFuture<DeleteIndexResponse> deleteIndex(String index) {
        DeleteIndexRequest dir = new DeleteIndexRequest(index);
        return esTransportClient.admin().indices().delete(dir);
    }

    /**
     * インデックスの設定を更新する.
     * @param index インデックス名
     * @param settings 更新するインデックス設定
     * @return Void
     */
    public Void updateIndexSettings(String index, Map<String, String> settings) {
        Settings settingsForUpdate = ImmutableSettings.settingsBuilder().put(settings).build();
        esTransportClient.admin().indices().prepareUpdateSettings(index).setSettings(settingsForUpdate).execute()
                .actionGet();
        return null;
    }

    /**
     * Mapping定義を取得する.
     * @param index インデックス名
     * @param type タイプ名
     * @return Mapping定義
     */
    public MappingMetaData getMapping(String index, String type) {
        ClusterState cs = esTransportClient.admin().cluster().prepareState().
                setIndices(index).execute().actionGet().getState();
        return cs.getMetaData().index(index).mapping(type);
    }

    /**
     * Mapping定義を更新する.
     * @param index インデックス名
     * @param type タイプ名
     * @param mappings マッピング情報
     * @return 非同期応答
     */
    public ListenableActionFuture<PutMappingResponse> putMapping(String index,
            String type,
            Map<String, Object> mappings) {
        PutMappingRequestBuilder builder = new PutMappingRequestBuilder(esTransportClient.admin().indices())
                .setIndices(index)
                .setType(type)
                .setSource(mappings);
        return builder.execute();
    }

    /**
     * インデックスステータスを取得する.
     * @return 非同期応答
     */
    public ActionFuture<IndicesStatusResponse> indicesStatus() {
        IndicesStatusRequestBuilder cirb =
                new IndicesStatusRequestBuilder(esTransportClient.admin().indices());
        return cirb.execute();
    }

    /**
     * 非同期でドキュメントを取得.
     * @param index インデックス名
     * @param type タイプ名
     * @param id ドキュメントのID
     * @param routingId routingId
     * @param realtime リアルタイムモードなら真
     * @return 非同期応答
     */
    public ActionFuture<GetResponse> asyncGet(String index, String type, String id, String routingId,
            boolean realtime) {
        GetRequest req = new GetRequest(index, type, id);

        if (routingFlag) {
            req = req.routing(routingId);
        }

        req.realtime(realtime);
        ActionFuture<GetResponse> ret = esTransportClient.get(req);
        this.fireEvent(Event.afterRequest, index, type, id, null, "Get");
        return ret;
    }

    /**
     * 非同期でドキュメントを検索.
     * @param index インデックス名
     * @param type タイプ名
     * @param routingId routingId
     * @param builder クエリ情報
     * @return 非同期応答
     */
    public ActionFuture<SearchResponse> asyncSearch(
            String index,
            String type,
            String routingId,
            SearchSourceBuilder builder) {
        SearchRequest req = new SearchRequest(index).types(type).searchType(SearchType.DEFAULT).source(builder);
        if (routingFlag) {
            req = req.routing(routingId);
        }
        ActionFuture<SearchResponse> ret = esTransportClient.search(req);
        this.fireEvent(Event.afterRequest, index, type, null,
                new String(builder.buildAsBytes().toBytes()), "Search");
        return ret;
    }

    /**
     * 非同期でドキュメントを検索.
     * @param index インデックス名
     * @param type タイプ名
     * @param routingId routingId
     * @param query クエリ情報
     * @return 非同期応答
     */
    public ActionFuture<SearchResponse> asyncSearch(
            String index,
            String type,
            String routingId,
            Map<String, Object> query) {
        SearchRequest req = new SearchRequest(index).types(type).searchType(SearchType.DEFAULT);
        if (query != null) {
            req.source(query);
        }
        if (routingFlag) {
            req = req.routing(routingId);
        }
        ActionFuture<SearchResponse> ret = esTransportClient.search(req);
        this.fireEvent(Event.afterRequest, index, type, null, JSONObject.toJSONString(query), "Search");
        return ret;
    }

    /**
     * 非同期でドキュメントを検索. <br />
     * Queryの指定方法をMapで直接記述せずにQueryBuilderにするため、非推奨とする.
     * @param index インデックス名
     * @param routingId routingId
     * @param query クエリ情報
     * @return 非同期応答
     */
    public ActionFuture<SearchResponse> asyncSearch(
            String index,
            String routingId,
            Map<String, Object> query) {
        SearchRequest req = new SearchRequest(index).searchType(SearchType.DEFAULT);
        if (query != null) {
            req.source(query);
        }
        if (routingFlag) {
            req = req.routing(routingId);
        }
        ActionFuture<SearchResponse> ret = esTransportClient.search(req);
        this.fireEvent(Event.afterRequest, index, null, null, JSONObject.toJSONString(query), "Search");
        return ret;
    }

    /**
     * 非同期でドキュメントを検索.
     * @param index インデックス名
     * @param routingId routingId
     * @param query クエリ情報
     * @return 非同期応答
     */
    public ActionFuture<SearchResponse> asyncSearch(
            String index,
            String routingId,
            QueryBuilder query) {
        SearchRequest req = new SearchRequest(index).searchType(SearchType.DEFAULT);

        String queryString = "null";
        if (query != null) {
            req.source(new SearchSourceBuilder().query(query));
            queryString = query.buildAsBytes().toUtf8();
        }
        if (routingFlag) {
            req = req.routing(routingId);
        }
        ActionFuture<SearchResponse> ret = esTransportClient.search(req);
        this.fireEvent(Event.afterRequest, index, null, null, queryString, "Search");
        return ret;
    }

    /**
     * 非同期でインデックスに対してドキュメントをマルチ検索.
     * 存在しないインデックスに対して本メソッドを使用すると、TransportSerializationExceptionがスローされるので注意すること
     * @param index インデックス名
     * @param routingId routingId
     * @param queryList マルチ検索用のクエリ情報リスト
     * @return 非同期応答
     */
    public ActionFuture<MultiSearchResponse> asyncMultiSearch(
            String index,
            String routingId,
            List<Map<String, Object>> queryList) {
        return this.asyncMultiSearch(index, null, routingId, queryList);
    }

    /**
     * 非同期でドキュメントをマルチ検索.
     * 存在しないインデックスに対して本メソッドを使用すると、TransportSerializationExceptionがスローされるので注意すること
     * @param index インデックス名
     * @param type タイプ名
     * @param routingId routingId
     * @param queryList マルチ検索用のクエリ情報リスト
     * @return 非同期応答
     */
    public ActionFuture<MultiSearchResponse> asyncMultiSearch(
            String index,
            String type,
            String routingId,
            List<Map<String, Object>> queryList) {
        MultiSearchRequest mrequest = new MultiSearchRequest();
        if (queryList == null || queryList.size() == 0) {
            throw new EsMultiSearchQueryParseException();
        }
        for (Map<String, Object> query : queryList) {
            SearchRequest req = new SearchRequest(index).searchType(SearchType.DEFAULT);
            if (type != null) {
                req.types(type);
            }
            // クエリ指定なしの場合はタイプに対する全件検索を行う
            if (query != null) {
                req.source(query);
            }
            if (routingFlag) {
                req = req.routing(routingId);
            }
            mrequest.add(req);
        }

        ActionFuture<MultiSearchResponse> ret = esTransportClient.multiSearch(mrequest);
        this.fireEvent(Event.afterRequest, index, type, null, JSONArray.toJSONString(queryList), "MultiSearch");
        return ret;
    }

    private static final int SCROLL_SEARCH_KEEP_ALIVE_TIME = 1000 * 60 * 5;

    /**
     * クエリを指定してスクロールサーチを実行する.
     * @param index インデックス名
     * @param type タイプ名
     * @param query 検索クエリ
     * @return 非同期応答
     */
    public ActionFuture<SearchResponse> asyncScrollSearch(String index, String type, Map<String, Object> query) {
        SearchRequest req = new SearchRequest(index)
                .searchType(SearchType.SCAN)
                .scroll(new TimeValue(SCROLL_SEARCH_KEEP_ALIVE_TIME));
        if (type != null) {
            req.types(type);
        }
        if (query != null) {
            req.source(query);
        }

        ActionFuture<SearchResponse> ret = esTransportClient.search(req);
        return ret;
    }

    /**
     * スクロールIDを指定してスクロールサーチを継続する.
     * @param scrollId スクロールID
     * @return 非同期応答
     */
    public ActionFuture<SearchResponse> asyncScrollSearch(String scrollId) {
        ActionFuture<SearchResponse> ret = esTransportClient.prepareSearchScroll(scrollId)
                .setScroll(new TimeValue(SCROLL_SEARCH_KEEP_ALIVE_TIME))
                .execute();
        return ret;
    }

    /**
     * 非同期でドキュメントを検索.
     * @param index インデックス名
     * @param query クエリ情報
     * @return 非同期応答
     */
    public ActionFuture<SearchResponse> asyncSearch(String index, Map<String, Object> query) {
        SearchRequest req = new SearchRequest(index).searchType(SearchType.DEFAULT);
        if (query != null) {
            req.source(query);
        }
        ActionFuture<SearchResponse> ret = esTransportClient.search(req);
        this.fireEvent(Event.afterRequest, index, null, null, JSONObject.toJSONString(query), "Search");
        return ret;
    }

    /**
     * 非同期でドキュメントを登録する.
     * @param index インデックス名
     * @param type タイプ名
     * @param id ドキュメントのid
     * @param routingId routingId
     * @param data データ
     * @param opType 操作タイプ
     * @param version version番号
     * @return 非同期応答
     */
    public ActionFuture<IndexResponse> asyncIndex(String index,
            String type,
            String id,
            String routingId,
            Map<String, Object> data,
            OpType opType,
            long version) {
        IndexRequestBuilder req = esTransportClient.prepareIndex(index, type, id).setSource(data).setOpType(opType)
                .setConsistencyLevel(WriteConsistencyLevel.DEFAULT).setRefresh(true);
        if (routingFlag) {
            req = req.setRouting(routingId);
        }
        if (version > -1) {
            req.setVersion(version);
        }

        ActionFuture<IndexResponse> ret = req.execute();
        EsRequestLogInfo logInfo = new EsRequestLogInfo(index, type, id, routingId, data, opType.toString(),
                version);
        this.fireEvent(Event.afterCreate, logInfo);

        return ret;
    }

    /**
     * 非同期でversionつきでdocumentを削除します.
     * @param index インデックス名
     * @param type タイプ名
     * @param id Document id to delete
     * @param routingId routingId
     * @param version The version of the document to delete
     * @return 非同期応答
     */
    public ActionFuture<DeleteResponse> asyncDelete(String index, String type,
            String id, String routingId, long version) {
        DeleteRequestBuilder req = esTransportClient.prepareDelete(index, type, id)
                .setRefresh(true);
        if (routingFlag) {
            req = req.setRouting(routingId);
        }
        if (version > -1) {
            req.setVersion(version);
        }
        ActionFuture<DeleteResponse> ret = req.execute();
        this.fireEvent(Event.afterRequest, index, type, id, null, "Delete");
        return ret;
    }

    /**
     * バルクでドキュメントを登録/更新/削除.
     * @param index インデックス名
     * @param routingId routingId
     * @param datas バルクドキュメント
     * @param isWriteLog リクエスト情報のログ出力有無
     * @return ES応答
     */
    @SuppressWarnings("unchecked")
    public BulkResponse bulkRequest(String index, String routingId, List<EsBulkRequest> datas, boolean isWriteLog) {
        BulkRequestBuilder bulkRequest = esTransportClient.prepareBulk();
        List<Map<String, Object>> bulkList = new ArrayList<Map<String, Object>>();
        for (EsBulkRequest data : datas) {

            if (EsBulkRequest.BULK_REQUEST_TYPE.DELETE == data.getRequestType()) {
                bulkRequest.add(createDeleteRequest(index, routingId, data));
            } else {
                bulkRequest.add(createIndexRequest(index, routingId, data));
            }
            JSONObject logData = new JSONObject();
            logData.put("reqType", data.getRequestType().toString());
            logData.put("type", data.getType());
            logData.put("id", data.getId());
            logData.put("source", data.getSource());
            bulkList.add(logData);
        }
        Map<String, Object> debug = new HashMap<String, Object>();
        debug.put("bulk", bulkList);

        BulkResponse ret = bulkRequest.setRefresh(true).execute().actionGet();
        if (isWriteLog) {
            this.fireEvent(Event.afterRequest, index, "none", "none", debug, "bulkRequest");
        }
        return ret;
    }

    /**
     * バルクリクエストのINDEXリクエストを作成する.
     * @param index インデックス名
     * @param routingId ルーティングID
     * @param data バルクドキュメント情報
     * @return 作成したINDEXリクエスト
     */
    private IndexRequestBuilder createIndexRequest(String index, String routingId, EsBulkRequest data) {
        IndexRequestBuilder request = esTransportClient.
                prepareIndex(index, data.getType(), data.getId()).setSource(data.getSource());
        if (routingFlag) {
            request = request.setRouting(routingId);
        }
        return request;
    }

    /**
     * バルクリクエストのDELETEリクエストを作成する.
     * @param index インデックス名
     * @param routingId ルーティングID
     * @param data バルクドキュメント情報
     * @return 作成したDELETEリクエスト
     */
    private DeleteRequestBuilder createDeleteRequest(String index, String routingId, EsBulkRequest data) {
        DeleteRequestBuilder request = esTransportClient.prepareDelete(index, data.getType(), data.getId());
        if (routingFlag) {
            request = request.setRouting(routingId);
        }
        return request;
    }

    /**
     * ルーティングIDに関係なくバルクでドキュメントを登録.
     * @param index インデックス名
     * @param bulkMap バルクドキュメント
     * @return ES応答
     */
    public DcBulkResponse asyncBulkCreate(
            String index, Map<String, List<EsBulkRequest>> bulkMap) {
        BulkRequestBuilder bulkRequest = esTransportClient.prepareBulk();
        // ルーティングIDごとにバルク登録を行うと効率が悪いため、引数で渡されたEsBulkRequestは全て一括登録する。
        // また、バルク登録後にactionGet()すると同期実行となるため、ここでは実行しない。
        // このため、execute()のレスポンスを返却し、呼び出し側でactionGet()してからレスポンスチェック、リフレッシュすること。
        for (Entry<String, List<EsBulkRequest>> ents : bulkMap.entrySet()) {
            for (EsBulkRequest data : ents.getValue()) {
                IndexRequestBuilder req = esTransportClient.
                        prepareIndex(index, data.getType(), data.getId()).setSource(data.getSource());
                if (routingFlag) {
                    req = req.setRouting(ents.getKey());
                }
                bulkRequest.add(req);
            }
        }
        DcBulkResponse response = DcBulkResponseImpl.getInstance(bulkRequest.execute().actionGet());
        return response;
    }

    /**
     * 引数で指定されたインデックスに対してrefreshする.
     * @param index インデックス名
     * @return レスポンス
     */
    public DcRefreshResponse refresh(String index) {
        RefreshResponse response = esTransportClient.admin().indices()
                .refresh(new RefreshRequest(index)).actionGet();
        return DcRefreshResponseImpl.getInstance(response);
    }

    /**
     * 指定されたクエリを使用してデータの削除を行う.
     * @param index 削除対象のインデックス
     * @param deleteQuery 削除対象を指定するクエリ
     * @return ES応答
     */
    public DeleteByQueryResponse deleteByQuery(String index, QueryBuilder deleteQuery) {
        DeleteByQueryResponse response = esTransportClient.prepareDeleteByQuery(index)
                .setQuery(deleteQuery).execute().actionGet();
        return response;
    }

    /**
     * flushを行う.
     * @param index flush対象のindex名
     * @return 非同期応答
     */
    public ActionFuture<FlushResponse> flushTransLog(String index) {
        ActionFuture<FlushResponse> ret = esTransportClient.admin().indices().flush(new FlushRequest(index));
        this.fireEvent(Event.afterRequest, index, null, null, null, "Flush");
        return ret;
    }
}
