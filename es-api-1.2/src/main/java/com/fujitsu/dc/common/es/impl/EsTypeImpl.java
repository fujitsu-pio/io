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

import java.util.List;
import java.util.Map;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.MultiSearchResponse;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.Strings;
import org.elasticsearch.index.engine.DocumentAlreadyExistsException;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.es.EsType;
import com.fujitsu.dc.common.es.response.DcDeleteResponse;
import com.fujitsu.dc.common.es.response.DcGetResponse;
import com.fujitsu.dc.common.es.response.DcIndexResponse;
import com.fujitsu.dc.common.es.response.DcMappingMetaData;
import com.fujitsu.dc.common.es.response.DcMultiSearchResponse;
import com.fujitsu.dc.common.es.response.DcPutMappingResponse;
import com.fujitsu.dc.common.es.response.DcSearchResponse;
import com.fujitsu.dc.common.es.response.EsClientException;
import com.fujitsu.dc.common.es.response.EsClientException.DcSearchPhaseExecutionException;
import com.fujitsu.dc.common.es.response.impl.DcDeleteResponseImpl;
import com.fujitsu.dc.common.es.response.impl.DcGetResponseImpl;
import com.fujitsu.dc.common.es.response.impl.DcIndexResponseImpl;
import com.fujitsu.dc.common.es.response.impl.DcMappingMetaDataImpl;
import com.fujitsu.dc.common.es.response.impl.DcMultiSearchResponseImpl;
import com.fujitsu.dc.common.es.response.impl.DcNullSearchResponse;
import com.fujitsu.dc.common.es.response.impl.DcPutMappingResponseImpl;
import com.fujitsu.dc.common.es.response.impl.DcSearchResponseImpl;

/**
 * Type 操作用 Class.
 */
public class EsTypeImpl extends EsTranslogHandler implements EsType {

    /**
     * ログ.
     */
    static Logger log = LoggerFactory.getLogger(EsType.class);

    private InternalEsClient esClient;

    // Typeが属するindex
    private String indexName;
    // Type名
    private String name;
    // routing ID
    private String routingId;
    // エラー発生時のリトライ回数
    private int retryCount;
    // エラー発生時のリトライ間隔
    private int retryInterval;

    private EsTranslogHandler requestOwner;

    /**
     * コンストラクタ.
     * @param index インデックス名
     * @param name type名
     * @param routingId routing ID
     * @param times ESでエラーが発生した場合のリトライ回数
     * @param interval ESでエラーが発生した場合のリトライ間隔(ミリ秒)
     * @param client EsClientオブジェクト
     */
    public EsTypeImpl(String index, String name, String routingId, int times, int interval, InternalEsClient client) {
        super(times, interval, client, index);
        // バッチコマンド群から参照されているためpublicとしているが参照しないこと
        // （EsClientのファクトリメソッドを使用してインスタンス化すること）
        this.indexName = index;
        this.name = name;
        this.routingId = routingId;
        this.esClient = client;
        this.retryCount = times;
        this.retryInterval = interval;

        this.requestOwner = this;
    }

    @Override
    public String getIndexName() {
        return this.indexName;
    }

    @Override
    public String getType() {
        return this.name;
    }

    @Override
    public DcGetResponse get(final String id) {
        // Realtime指定はtrue, // 高速さよりも一貫性を取得
        return this.get(id, true);
    }

    @Override
    public DcGetResponse get(final String id, final boolean realtime) {
        // indexにカスタムのMapping定義が必ず存在することを保証するため、
        // ESのIndex自動生成をOFFにして運用する一方で、
        // 本アプリで、存在しないIndex指定があったときは自動生成する枠組みを提供するようにしたかった。
        // しかし、検索・取得系でIndexMissingExceptionが発生した直後にIndexを作成する処理を書いても、なぜか
        // ElasticSearchがエラーとなって、動作しなかったため、やむを得ずこれをあきらめた。
        // 一方、そのときも、０件である旨をしめすResponseをシミュレートして返せればよかったが、
        // ElasticSearchのレスポンスクラスを手動で作成する実装が困難そうなので、やむをえず、nullを返すようにした。
        // そのため、get()メソッドを使う場合は、これがnullを返すことがあることを前提としたコードを書かなくてはならない。
        GetRetryableRequest request = new GetRetryableRequest(retryCount, retryInterval, id, realtime);
        // 必要な場合、メソッド内でリトライが行われる.
        return DcGetResponseImpl.getInstance(request.doRequest());
    }

    @Override
    @SuppressWarnings("rawtypes")
    public DcIndexResponse create(final Map data) {
        String id = Strings.randomBase64UUID();
        return this.create(id, data);
    }

    @Override
    public DcIndexResponse create(final String id, @SuppressWarnings("rawtypes") final Map data) {
        CreateRetryableRequest request = new CreateRetryableRequest(retryCount, retryInterval, id, data);
        // 必要な場合、メソッド内でリトライが行われる.
        return DcIndexResponseImpl.getInstance(request.doRequest());
    }

    @Override
    public DcIndexResponse update(final String id, @SuppressWarnings("rawtypes") final Map data, final long version) {
        UpdateRetryableRequest request = new UpdateRetryableRequest(retryCount, retryInterval, id, data, version);
        // 必要な場合、メソッド内でリトライが行われる.
        return DcIndexResponseImpl.getInstance(request.doRequest());
    }

    @Override
    @SuppressWarnings("rawtypes")
    public DcIndexResponse update(final String id, final Map data) {
        return this.update(id, data, -1);
    }

    @Override
    public DcSearchResponse search(final Map<String, Object> query) {
        SearchRetryableRequest request = new SearchRetryableRequest(retryCount, retryInterval, query);
        // 必要な場合、メソッド内でリトライが行われる.
        return DcSearchResponseImpl.getInstance(request.doRequest());
    }

    @Override
    public DcMultiSearchResponse multiSearch(final List<Map<String, Object>> queryList) {
        MultiSearchRetryableRequest request = new MultiSearchRetryableRequest(retryCount, retryInterval, queryList);
        // 必要な場合、メソッド内でリトライが行われる.
        return DcMultiSearchResponseImpl.getInstance(request.doRequest());
    }

    @Override
    public DcDeleteResponse delete(final String docId) {
        return this.delete(docId, -1);
    }

    @Override
    public DcDeleteResponse delete(final String docId, final long version) {
        DeleteRetryableRequest request = new DeleteRetryableRequest(retryCount, retryInterval, docId, version);
        // 必要な場合、メソッド内でリトライが行われる.
        return DcDeleteResponseImpl.getInstance(request.doRequest());
    }

    @Override
    public DcMappingMetaData getMapping() {
        return DcMappingMetaDataImpl.getInstance(esClient.getMapping(this.indexName, this.name));
    }

    @Override
    public DcPutMappingResponse putMapping(Map<String, Object> mappings) {
        PutMappingRetryableRequest request = new PutMappingRetryableRequest(retryCount, retryInterval, mappings);
        // 必要な場合、メソッド内でリトライが行われる.
        return DcPutMappingResponseImpl.getInstance(request.doRequest());
    }

    /**
     * Elasticsearchへの GET処理実装.
     */
    class GetRetryableRequest extends AbstractRetryableEsRequest<GetResponse> {
        String id;
        boolean realTime;

        public GetRetryableRequest(int retryCount, long retryInterval,
                String argId, boolean argRealTime) {
            super(retryCount, retryInterval, "ES get");
            id = argId;
            realTime = argRealTime;
        }

        @Override
        GetResponse doProcess() {
            GetResponse response = asyncGet(id, realTime).actionGet();
            if (!response.isExists()) {
                // データがなかったらｎullを返す
                return null;
            }
            return response;
        }

        @Override
        boolean isParticularError(ElasticsearchException e) {
            return e instanceof IndexMissingException || e.getCause() instanceof IndexMissingException;
        }

        @Override
        GetResponse onParticularError(ElasticsearchException e) {
            if (e instanceof IndexMissingException || e.getCause() instanceof IndexMissingException) {
                return null;
            }
            throw e;
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            return requestOwner;
        }
    }

    /**
     * Elasticsearchへの create処理実装.
     */
    class CreateRetryableRequest extends AbstractRetryableEsRequest<IndexResponse> {
        String id;
        @SuppressWarnings("rawtypes")
        Map data;

        public CreateRetryableRequest(int retryCount, long retryInterval,
                String argId, @SuppressWarnings("rawtypes") Map argData) {
            super(retryCount, retryInterval, "EsType create");
            id = argId;
            data = argData;
        }

        @SuppressWarnings("unchecked")
        @Override
        IndexResponse doProcess() {
            return asyncIndex(id, data, OpType.CREATE, -1).actionGet();
        }

        @Override
        boolean isParticularError(ElasticsearchException e) {
            return e instanceof DocumentAlreadyExistsException
                    || e instanceof IndexMissingException
                    || e.getCause() instanceof IndexMissingException
                    || e instanceof MapperParsingException;
        }

        @Override
        IndexResponse onParticularError(ElasticsearchException e) {
            if (e instanceof IndexMissingException || e.getCause() instanceof IndexMissingException) {
                throw new EsClientException.EsIndexMissingException(e);
            }
            if (e instanceof MapperParsingException) {
                throw new EsClientException.EsSchemaMismatchException(e);
            }
            // 既知のExceptionの場合はINFOログ
            // 新規のExceptionの場合はWARNログ
            if (e instanceof DocumentAlreadyExistsException) {
                if (e.getClass() != null) {
                    log.info(e.getClass().getName() + " : " + e.getMessage());
                } else {
                    log.info(e.getMessage());
                }
            } else {
                if (e.getClass() != null) {
                    log.warn(e.getClass().getName() + " : " + e.getMessage());
                } else {
                    log.warn(e.getMessage());
                }
            }
            // 例外が発生した場合でもドキュメントが登録されている可能性がある。
            // そのため、登録チェックを行い、データが登録済の場合は正常なレスポンスを返却する。
            return checkDocumentCreated(id, data, e);
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            return requestOwner;
        }
    }

    /**
     * ドキュメントが登録されているかのチェックを行う.
     * @param id UUID
     * @param data Request body
     * @param ese ElasticSearchException
     * @return ES応答
     */
    @SuppressWarnings("rawtypes")
    protected IndexResponse checkDocumentCreated(String id, Map data, ElasticsearchException ese) {
        DcGetResponse getResponse = get(id);
        if (getResponse != null) {
            Object reqUpdated = data.get("u");
            Object getUpdated = getResponse.getSource().get("u");
            if (reqUpdated == null && getUpdated == null) {
                // uがnullになることはありえないが、静的チェックの指摘回避
                log.info("Request data is already registered. Then, return success response. But value is null");
                return new IndexResponse(indexName, getType(), id, 1, false);
            } else if ((reqUpdated != null && getUpdated != null) && reqUpdated.equals(getUpdated)) {
                log.info("Request data is already registered. Then, return success response.");
                return new IndexResponse(indexName, getType(), id, 1, false);
            }
        }
        throw new EsClientException("create failed", ese);
    }

    /**
     * Elasticsearchへの update処理実装.
     */
    @SuppressWarnings("rawtypes")
    class UpdateRetryableRequest extends AbstractRetryableEsRequest<IndexResponse> {
        String id;
        Map data;
        long version;

        public UpdateRetryableRequest(int retryCount, long retryInterval,
                String argId, Map argData, long argVersion) {
            super(retryCount, retryInterval, "EsType update");
            id = argId;
            data = argData;
            version = argVersion;
        }

        @SuppressWarnings("unchecked")
        @Override
        IndexResponse doProcess() {
            return asyncIndex(id, data, OpType.INDEX, version).actionGet();
        }

        @Override
        boolean isParticularError(ElasticsearchException e) {
            return e instanceof IndexMissingException
                    || e.getCause() instanceof IndexMissingException
                    || e instanceof VersionConflictEngineException
                    || e instanceof MapperParsingException;
        }

        @Override
        IndexResponse onParticularError(ElasticsearchException e) {
            if (e instanceof IndexMissingException || e.getCause() instanceof IndexMissingException) {
                throw new EsClientException.EsIndexMissingException(e);
            }
            if (e instanceof VersionConflictEngineException) {
                throw new EsClientException.EsVersionConflictException(e);
            }
            if (e instanceof MapperParsingException) {
                throw new EsClientException.EsSchemaMismatchException(e);
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
        Map<String, Object> query;

        public SearchRetryableRequest(int retryCount, long retryInterval, Map<String, Object> argQuery) {
            super(retryCount, retryInterval, "EsType search");
            query = argQuery;
        }

        @Override
        SearchResponse doProcess() {
            return asyncSearch(query).actionGet();
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
                return new DcNullSearchResponse();
            }
            if (e instanceof SearchPhaseExecutionException) {
                throw new EsClientException("unknown property was appointed.", new DcSearchPhaseExecutionException(e));
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
        List<Map<String, Object>> queryList;

        public MultiSearchRetryableRequest(int retryCount, long retryInterval, List<Map<String, Object>> argQueryList) {
            super(retryCount, retryInterval, "EsType multisearch");
            queryList = argQueryList;
        }

        @Override
        MultiSearchResponse doProcess() {
            return asyncMultiSearch(queryList).actionGet();
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
     * Elasticsearchへの delete処理実装.
     */
    class DeleteRetryableRequest extends AbstractRetryableEsRequest<DeleteResponse> {
        String docId;
        long version;

        public DeleteRetryableRequest(int retryCount, long retryInterval, String argDocId, long argVersion) {
            super(retryCount, retryInterval, "EsType delete");
            docId = argDocId;
            version = argVersion;
        }

        @Override
        DeleteResponse doProcess() {
            return asyncDelete(docId, version).actionGet();
        }

        @Override
        boolean isParticularError(ElasticsearchException e) {
            return e instanceof IndexMissingException
                    || e.getCause() instanceof IndexMissingException
                    || e instanceof VersionConflictEngineException;
        }

        @Override
        DeleteResponse onParticularError(ElasticsearchException e) {
            if (e instanceof IndexMissingException || e.getCause() instanceof IndexMissingException) {
                throw new EsClientException.EsIndexMissingException(e);
            }
            if (e instanceof VersionConflictEngineException) {
                throw new EsClientException.EsVersionConflictException(e);
            }
            throw e;
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            return requestOwner;
        }
    }

    /**
     * Elasticsearchへの mapping put処理実装.
     */
    class PutMappingRetryableRequest extends AbstractRetryableEsRequest<PutMappingResponse> {
        Map<String, Object> mappings;

        public PutMappingRetryableRequest(int retryCount, long retryInterval, Map<String, Object> argMappings) {
            super(retryCount, retryInterval, "EsType putMapping");
            mappings = argMappings;
        }

        @Override
        PutMappingResponse doProcess() {
            return asyncPutMapping(mappings).actionGet();
        }

        @Override
        boolean isParticularError(ElasticsearchException e) {
            return e instanceof IndexMissingException
                    || e.getCause() instanceof IndexMissingException
                    || e instanceof MapperParsingException;
        }

        @Override
        PutMappingResponse onParticularError(ElasticsearchException e) {
            if (e instanceof IndexMissingException || e.getCause() instanceof IndexMissingException) {
                throw new EsClientException.EsIndexMissingException(e);
            }
            if (e instanceof MapperParsingException) {
                throw new EsClientException.EsSchemaMismatchException(e);
            }
            throw e;
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            return requestOwner;
        }
    }

    /**
     * 非同期でドキュメントを取得.
     * @param id ドキュメントのID
     * @param realtime リアルタイムモードなら真
     * @return 非同期応答
     */
    public ActionFuture<GetResponse> asyncGet(final String id, final boolean realtime) {
        return esClient.asyncGet(this.indexName, this.name, id, this.routingId, realtime);
    }

    /**
     * 非同期でドキュメントを検索.
     * @param builder クエリ情報
     * @return 非同期ES応答
     */
    public ActionFuture<SearchResponse> asyncSearch(final SearchSourceBuilder builder) {
        return esClient.asyncSearch(this.indexName, this.name, this.routingId, builder);
    }

    /**
     * 非同期でドキュメントを検索.
     * @param query クエリ情報
     * @return ES応答
     */
    public ActionFuture<SearchResponse> asyncSearch(final Map<String, Object> query) {
        return esClient.asyncSearch(this.indexName, this.name, this.routingId, query);
    }

    /**
     * 非同期でドキュメントをマルチ検索.
     * @param queryList マルチ検索用のクエリ情報リスト
     * @return ES応答
     */
    public ActionFuture<MultiSearchResponse> asyncMultiSearch(final List<Map<String, Object>> queryList) {
        return esClient.asyncMultiSearch(this.indexName, this.name, this.routingId, queryList);
    }

    /**
     * 非同期でドキュメントを登録する.
     * @param id ドキュメントのid
     * @param data データ
     * @param opType 操作タイプ
     * @param version version番号
     * @return ES非同期応答
     */
    public ActionFuture<IndexResponse> asyncIndex(final String id,
            final Map<String, Object> data,
            final OpType opType,
            final long version) {

        return esClient.asyncIndex(this.indexName, this.name, id, this.routingId, data, opType, version);
    }

    /**
     * 非同期でversionつきでdocumentを削除します.
     * @param id Document id to delete
     * @param version The version of the document to delete
     * @return 非同期応答
     */
    public ActionFuture<DeleteResponse> asyncDelete(final String id, final long version) {
        return esClient.asyncDelete(this.indexName, this.name, id, this.routingId, version);
    }

    /**
     * Bulk Get.
     * @param ids IDのリスト
     * @return 応答
     */
    public JSONObject bulkGet(final List<String> ids) {
        // BulkRequest req = new BulkRequest();
        return null;
    }

    /**
     * 非同期でMapping定義を更新する.
     * @param mappings Mapping定義
     * @return Mapping更新レスポンス
     */
    public ListenableActionFuture<PutMappingResponse> asyncPutMapping(Map<String, Object> mappings) {
        return esClient.putMapping(this.indexName, this.name, mappings);
    }

}
