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

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.CharEncoding;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.indices.IndexAlreadyExistsException;
import org.elasticsearch.indices.IndexMissingException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.es.EsBulkRequest;
import com.fujitsu.dc.common.es.EsIndex;
import com.fujitsu.dc.common.es.query.DcQueryBuilder;
import com.fujitsu.dc.common.es.response.DcBulkResponse;
import com.fujitsu.dc.common.es.response.DcMultiSearchResponse;
import com.fujitsu.dc.common.es.response.DcSearchResponse;
import com.fujitsu.dc.common.es.response.EsClientException;
import com.fujitsu.dc.common.es.response.impl.DcBulkResponseImpl;
import com.fujitsu.dc.common.es.response.impl.DcMultiSearchResponseImpl;
import com.fujitsu.dc.common.es.response.impl.DcSearchResponseImpl;

/**
 * Index 操作用 Class.
 */
public class EsIndexImpl extends EsTranslogHandler implements EsIndex {
    private InternalEsClient esClient;

    /**
     * ログ.
     */
    static Logger log = LoggerFactory.getLogger(EsIndexImpl.class);

    // エラー発生時のリトライ回数
    private int retryCount;
    // エラー発生時のリトライ間隔
    private int retryInterval;

    String name;
    String category;

    private EsTranslogHandler requestOwner;

    /**
     * インデックス名とカテゴリを指定してインスタンスを生成する.
     * @param name インデックス名
     * @param category カテゴリ
     * @param times ESでエラーが発生した場合のリトライ回数
     * @param interval ESでエラーが発生した場合のリトライ間隔(ミリ秒)
     * @param client EsClientオブジェクト
     */
    public EsIndexImpl(final String name, final String category, int times, int interval, InternalEsClient client) {
        super(times, interval, client, name);
        // バッチコマンド群から参照されているためpublicとしているが参照しないこと
        // （EsClientのファクトリメソッドを使用してインスタンス化すること）
        this.name = name;
        this.category = category;
        this.retryCount = times;
        this.retryInterval = interval;
        this.esClient = client;

        this.requestOwner = this;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getCategory() {
        return this.category;
    }

    @Override
    public void create() {
        if (mappingConfigs == null) {
            loadMappingConfigs();
        }
        Map<String, JSONObject> mappings = mappingConfigs.get(this.category);
        if (mappings == null) {
            throw new EsClientException("NO MAPPINGS DEFINED for " + this.category + this.name);
        }

        CreateRetryableRequest request = new CreateRetryableRequest(retryCount, retryInterval, name, mappings);
        // 必要な場合、メソッド内でリトライが行われる.
        request.doRequest();
    }

    @Override
    public void delete() {
        DeleteRetryableRequest request = new DeleteRetryableRequest(retryCount, retryInterval, this.name);
        // 必要な場合、メソッド内でリトライが行われる.
        request.doRequest();
    }

    @Override
    public DcSearchResponse search(String routingId, final Map<String, Object> query) {
        SearchWithMapRetryableRequest request = new SearchWithMapRetryableRequest(retryCount, retryInterval, routingId,
                query);
        // 必要な場合、メソッド内でリトライが行われる.
        return DcSearchResponseImpl.getInstance(request.doRequest());
    }

    @Override
    public DcSearchResponse search(String routingId, final DcQueryBuilder query) {
        SearchRetryableRequest request = new SearchRetryableRequest(retryCount, retryInterval, routingId,
                getQueryBuilder(query));
        // 必要な場合、メソッド内でリトライが行われる.
        return DcSearchResponseImpl.getInstance(request.doRequest());
    }

    @Override
    public DcMultiSearchResponse multiSearch(String routingId, final List<Map<String, Object>> queryList) {
        MultiSearchRetryableRequest request =
                new MultiSearchRetryableRequest(retryCount, retryInterval, routingId, queryList);
        // 必要な場合、メソッド内でリトライが行われる.
        return DcMultiSearchResponseImpl.getInstance(request.doRequest());
    }

    @Override
    public void deleteByQuery(String routingId, DcQueryBuilder queryBuilder) {
        QueryBuilder deleteQuery = getQueryBuilder(queryBuilder);
        DeleteByQueryRetryableRequest request = new DeleteByQueryRetryableRequest(retryCount, retryInterval,
                this.name, deleteQuery);
        request.doRequest();

        // 削除クエリと同一の検索を実行して、全件削除されていることを確認する
        DcSearchResponse response = this.search(routingId, queryBuilder);
        long failedCount = response.getHits().getAllPages();
        if (failedCount != 0) {
            throw new EsClientException.EsDeleteByQueryException(failedCount);
        }
    }

    private QueryBuilder getQueryBuilder(DcQueryBuilder dcQueryBuilder) {
        QueryBuilder queryBuilder = null;
        if (dcQueryBuilder != null) {
            queryBuilder = dcQueryBuilder.getQueryBuilder();
        }
        if (queryBuilder == null) {
            log.info("Query is not specified.");
        }
        return queryBuilder;
    }

    @Override
    public DcBulkResponse bulkRequest(final String routingId, final List<EsBulkRequest> datas, boolean isWriteLog) {
        BulkRetryableRequest request = new BulkRetryableRequest(retryCount, retryInterval,
                this.name, routingId, datas, isWriteLog);
        // 必要な場合、メソッド内でリトライが行われる.
        return DcBulkResponseImpl.getInstance(request.doRequest());
    }

    /**
     * インデックスの設定を更新する.
     * @param index インデックス名
     * @param settings 更新するインデックス設定
     * @return Void
     */
    public Void updateSettings(String index, Map<String, String> settings) {
        UpdateSettingsRetryableRequest request = new UpdateSettingsRetryableRequest(retryCount, retryInterval, index,
                settings);
        // 必要な場合、メソッド内でリトライが行われる.
        return request.doRequest();
    }

    /**
     * Elasticsearchへの index create処理実装.
     */
    class CreateRetryableRequest extends AbstractRetryableEsRequest<CreateIndexResponse> {
        String name;
        Map<String, JSONObject> mappings;

        public CreateRetryableRequest(int retryCount, long retryInterval,
                String argName, Map<String, JSONObject> argMappings) {
            super(retryCount, retryInterval, "EsIndex create");
            name = argName;
            mappings = argMappings;
        }

        @Override
        CreateIndexResponse doProcess() {
            return esClient.createIndex(name, mappings).actionGet();
        }

        /**
         * リトライ時、引数に指定された例外を特別扱いする場合、trueを返すようにオーバーライドすること.
         * これにより、#onParticularErrorメソッドが呼び出される.
         * 標準実装では, 常に falseを返す.
         * @param e 検査対象の例外
         * @return true: 正常終了として扱う場合, false: 左記以外の場合
         */
        @Override
        boolean isParticularError(ElasticsearchException e) {
            return e instanceof IndexAlreadyExistsException || e.getCause() instanceof IndexAlreadyExistsException;
        }

        @Override
        CreateIndexResponse onParticularError(ElasticsearchException e) {
            if (e instanceof IndexAlreadyExistsException
                    || e.getCause() instanceof IndexAlreadyExistsException) {
                throw new EsClientException.EsIndexAlreadyExistsException(e);
            }
            throw e;
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            return requestOwner;
        }
    }

    /**
     * Elasticsearchへの index delete処理実装.
     */
    class DeleteRetryableRequest extends AbstractRetryableEsRequest<DeleteIndexResponse> {
        String name;

        public DeleteRetryableRequest(int retryCount, long retryInterval, String argName) {
            super(retryCount, retryInterval, "EsIndex delete");
            name = argName;
        }

        @Override
        DeleteIndexResponse doProcess() {
            return esClient.deleteIndex(this.name).actionGet();
        }

        @Override
        boolean isParticularError(ElasticsearchException e) {
            return e instanceof IndexMissingException || e.getCause() instanceof IndexMissingException;
        }

        @Override
        DeleteIndexResponse onParticularError(ElasticsearchException e) {
            if (e instanceof IndexMissingException || e.getCause() instanceof IndexMissingException) {
                throw new EsClientException.EsIndexMissingException(e);
            }
            throw e;
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            return requestOwner;
        }
    }

    static Map<String, Map<String, JSONObject>> mappingConfigs = null;

    static synchronized void loadMappingConfigs() {
        if (mappingConfigs != null) {
            return;
        }
        mappingConfigs = new HashMap<String, Map<String, JSONObject>>();
        loadMappingConfig(EsIndex.CATEGORY_AD, "Domain", "es/mapping/domain.json");
        loadMappingConfig(EsIndex.CATEGORY_AD, "Cell", "es/mapping/cell.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "link", "es/mapping/link.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "Account", "es/mapping/account.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "Box", "es/mapping/box.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "Role", "es/mapping/role.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "Relation", "es/mapping/relation.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "SentMessage", "es/mapping/sentMessage.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "ReceivedMessage", "es/mapping/receivedMessage.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "EntityType", "es/mapping/entityType.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "AssociationEnd", "es/mapping/associationEnd.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "Property", "es/mapping/property.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "ComplexType", "es/mapping/complexType.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "ComplexTypeProperty", "es/mapping/complexTypeProperty.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "ExtCell", "es/mapping/extCell.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "ExtRole", "es/mapping/extRole.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "dav", "es/mapping/dav.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "UserData", "es/mapping/userdata.json");
        loadMappingConfig(EsIndex.CATEGORY_USR, "_default_", "es/mapping/default.json");
    }

    static void loadMappingConfig(String indexCat, String typeCat, String resPath) {
        JSONObject json = readJsonResource(resPath);
        Map<String, JSONObject> idxMappings = mappingConfigs.get(indexCat);
        if (idxMappings == null) {
            idxMappings = new HashMap<String, JSONObject>();
            mappingConfigs.put(indexCat, idxMappings);
        }
        idxMappings.put(typeCat, json);
    }

    /**
     * プログラムリソース中のJSONで書かれた設定情報を読み出します.
     * @param resPath リソースパス
     * @return 読み出したJSONオブジェクト
     */
    private static JSONObject readJsonResource(final String resPath) {
        JSONParser jp = new JSONParser();
        JSONObject json = null;
        InputStream is = null;
        try {
            is = EsIndexImpl.class.getClassLoader().getResourceAsStream(resPath);
            json = (JSONObject) jp.parse(new InputStreamReader(is, CharEncoding.UTF_8));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return json;
    }

    /**
     * インデックス名が最大長を超えた場合にスローする例外.
     */
    public static class TooLongIndexNameException extends RuntimeException {
        /**
         * デフォルトシリアルバージョンID.
         */
        private static final long serialVersionUID = 1L;

        /**
         * コンストラクタ.
         * @param msg メッセージ
         */
        public TooLongIndexNameException(final String msg) {
            super(msg);
        }
    }

    /**
     * Elasticsearchへの search処理実装. <br />
     * Queryの指定方法をMapで直接記述せずにQueryBuilderにするため、非推奨とする.
     */
    @Deprecated
    class SearchWithMapRetryableRequest extends AbstractRetryableEsRequest<SearchResponse> {
        String routingId;
        Map<String, Object> query;

        public SearchWithMapRetryableRequest(int retryCount, long retryInterval,
                String argRoutingId, Map<String, Object> argQuery) {
            super(retryCount, retryInterval, "EsIndex search");
            query = argQuery;
            routingId = argRoutingId;
        }

        @Override
        SearchResponse doProcess() {
            return asyncIndexSearch(routingId, query).actionGet();
        }

        @Override
        boolean isParticularError(ElasticsearchException e) {
            return e instanceof IndexMissingException
                    || e.getCause() instanceof IndexMissingException
                    || e instanceof SearchPhaseExecutionException;
        }

        @Override
        SearchResponse onParticularError(ElasticsearchException e) {
            if (e instanceof IndexMissingException || e.getCause() instanceof IndexMissingException) {
                return null;
            }
            if (e instanceof SearchPhaseExecutionException) {
                throw new EsClientException("unknown property was appointed.", e);
            }
            throw e;
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            return requestOwner;
        }
    }

    /**
     * Elasticsearchへの search処理実装.
     */
    class SearchRetryableRequest extends AbstractRetryableEsRequest<SearchResponse> {
        String routingId;
        QueryBuilder query;

        public SearchRetryableRequest(int retryCount, long retryInterval,
                String argRoutingId, QueryBuilder argQuery) {
            super(retryCount, retryInterval, "EsIndex search");
            routingId = argRoutingId;
            query = argQuery;
        }

        @Override
        boolean isParticularError(ElasticsearchException e) {
            return e instanceof IndexMissingException
                    || e.getCause() instanceof IndexMissingException
                    || e instanceof SearchPhaseExecutionException;
        }

        @Override
        SearchResponse doProcess() {
            return asyncIndexSearch(routingId, query).actionGet();
        }

        @Override
        SearchResponse onParticularError(ElasticsearchException e) {
            if (e instanceof IndexMissingException || e.getCause() instanceof IndexMissingException) {
                return null;
            }
            if (e instanceof SearchPhaseExecutionException) {
                throw new EsClientException("unknown property was appointed.", e);
            }
            throw e;
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            return requestOwner;
        }
    }

    /**
     * Elasticsearchへの multisearch処理実装.
     */
    class MultiSearchRetryableRequest extends AbstractRetryableEsRequest<MultiSearchResponse> {
        String routingId;
        List<Map<String, Object>> queryList;

        public MultiSearchRetryableRequest(int retryCount, long retryInterval,
                String argRoutingId, List<Map<String, Object>> argQueryList) {
            super(retryCount, retryInterval, "EsIndex search");
            routingId = argRoutingId;
            queryList = argQueryList;
        }

        @Override
        MultiSearchResponse doProcess() {
            return asyncMultiIndexSearch(routingId, queryList).actionGet();
        }

        @Override
        boolean isParticularError(ElasticsearchException e) {
            return e instanceof SearchPhaseExecutionException;
        }

        @Override
        MultiSearchResponse onParticularError(ElasticsearchException e) {
            if (e instanceof SearchPhaseExecutionException) {
                throw new EsClientException("unknown property was appointed.", e);
            }
            throw e;
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            return requestOwner;
        }
    }

    /**
     * Elasticsearchへの delete by query処理実装.
     */
    class DeleteByQueryRetryableRequest extends AbstractRetryableEsRequest<DeleteByQueryResponse> {
        String name;
        QueryBuilder deleteQuery;

        public DeleteByQueryRetryableRequest(int retryCount, long retryInterval,
                String argName, QueryBuilder argDeleteQuery) {
            super(retryCount, retryInterval, "EsIndex deleteByQuery");
            name = argName;
            deleteQuery = argDeleteQuery;
        }

        @Override
        DeleteByQueryResponse doProcess() {
            return esClient.deleteByQuery(name, deleteQuery);
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            return requestOwner;
        }
    }

    /**
     * Elasticsearchへの update index settings処理実装.
     */
    class UpdateSettingsRetryableRequest extends AbstractRetryableEsRequest<Void> {
        String index;
        Map<String, String> settings;

        public UpdateSettingsRetryableRequest(int retryCount, long retryInterval,
                String index, Map<String, String> settings) {
            super(retryCount, retryInterval, "EsIndex updateSettings");
            this.index = index;
            this.settings = settings;
        }

        @Override
        Void doProcess() {
            return esClient.updateIndexSettings(index, settings);
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            return requestOwner;
        }
    }

    /**
     * 非同期でドキュメントを検索. <br />
     * Queryの指定方法をMapで直接記述せずにQueryBuilderにするため、非推奨とする.
     * @param routingId routingId
     * @param query クエリ情報
     * @return ES応答
     */
    public ActionFuture<SearchResponse> asyncIndexSearch(String routingId, final Map<String, Object> query) {
        return esClient.asyncSearch(this.name, routingId, query);
    }

    /**
     * 非同期でドキュメントを検索.
     * @param routingId routingId
     * @param query クエリ情報
     * @return ES応答
     */
    public ActionFuture<SearchResponse> asyncIndexSearch(String routingId, final QueryBuilder query) {
        return esClient.asyncSearch(this.name, routingId, query);
    }

    /**
     * 非同期でドキュメントをマルチ検索.
     * @param routingId routingId
     * @param queryList クエリ情報一覧
     * @return ES応答
     */
    public ActionFuture<MultiSearchResponse> asyncMultiIndexSearch(String routingId,
            final List<Map<String, Object>> queryList) {
        return esClient.asyncMultiSearch(this.name, routingId, queryList);
    }

    /**
     * Elasticsearchへの bulk create処理実装.
     */
    class BulkRetryableRequest extends AbstractRetryableEsRequest<BulkResponse> {
        String name;
        String routingId;
        List<EsBulkRequest> datas;
        boolean isWriteLog;

        public BulkRetryableRequest(int retryCount, long retryInterval,
                String argName, String argRoutingId, List<EsBulkRequest> argDatas, boolean isWriteLog) {
            super(retryCount, retryInterval, "EsIndex bulkCreate");
            this.name = argName;
            this.routingId = argRoutingId;
            this.datas = argDatas;
            this.isWriteLog = isWriteLog;
        }

        @Override
        BulkResponse doProcess() {
            return esClient.bulkRequest(name, routingId, datas, isWriteLog);
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            return requestOwner;
        }
    }
}
