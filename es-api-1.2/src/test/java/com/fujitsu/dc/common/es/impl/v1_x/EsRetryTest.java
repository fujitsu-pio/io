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
package com.fujitsu.dc.common.es.impl.v1_x;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.ListenableActionFuture;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingResponse;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.deletebyquery.DeleteByQueryResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.json.simple.JSONObject;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fujitsu.dc.common.es.EsBulkRequest;
import com.fujitsu.dc.common.es.EsClient;
import com.fujitsu.dc.common.es.EsIndex;
import com.fujitsu.dc.common.es.EsType;
import com.fujitsu.dc.common.es.impl.EsIndexImpl;
import com.fujitsu.dc.common.es.impl.EsTypeImpl;
import com.fujitsu.dc.common.es.impl.InternalEsClient;
import com.fujitsu.dc.common.es.query.DcQueryBuilder;
import com.fujitsu.dc.common.es.query.DcQueryBuilders;
import com.fujitsu.dc.common.es.response.DcDeleteResponse;
import com.fujitsu.dc.common.es.response.DcGetResponse;
import com.fujitsu.dc.common.es.response.DcIndexResponse;
import com.fujitsu.dc.common.es.response.DcPutMappingResponse;
import com.fujitsu.dc.common.es.response.DcSearchResponse;
import com.fujitsu.dc.common.es.response.EsClientException;

/**
 * EsModelの単体テストケース.
 */
public class EsRetryTest {

    private static final String TESTING_HOSTS = "localhost:9399";
    private static final String TESTING_CLUSTER = "testingCluster";
    private static Node node;
    private static EsClient esClient;

    /**
     * テストケース共通の初期化処理. テスト用のElasticsearchのNodeを初期化する
     * @throws Exception 異常が発生した場合の例外
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        Settings settings = ImmutableSettings.settingsBuilder()
                // .put("node.http.enabled", false)
                .put("cluster.name", TESTING_CLUSTER)
                .put("node.name", "node1")
                .put("gateway.type", "none")
                .put("action.auto_create_index", "false")
                .put("index.store.type", "memory")
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
                .put("transport.tcp.port", "9399")
                .build();
        node = NodeBuilder.nodeBuilder().settings(settings).node();

        esClient = new EsClient(TESTING_CLUSTER, TESTING_HOSTS);
    }

    /**
     * テストケース共通のクリーンアップ処理. テスト用のElasticsearchのNodeをクローズする
     * @throws Exception 異常が発生した場合の例外
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        node.close();
        deleteDirectory(new File("data"));
    }

    private static void deleteDirectory(File target) {
        if (!target.exists()) {
            return;
        }

        if (target.isFile()) {
            target.delete();
        }

        if (target.isDirectory()) {
            File[] files = target.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDirectory(files[i]);
            }
            target.delete();
        }
    }

    /**
     * 各テスト実行前の初期化処理.
     * @throws Exception 異常が発生した場合の例外
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * 各テスト実行後のクリーンアップ処理.
     * @throws Exception 異常が発生した場合の例外
     */
    @After
    public void tearDown() throws Exception {
        EsIndex index = esClient.idxAdmin("index_for_test");
        try {
            index.delete();
        } catch (Exception ex) {
            System.out.println("");
        }
    }

    private EsType createEsTypeInstance(int retryCount) {
        InternalEsClient client = EsClientForNoNodeAvailableExceptionTestForFail.getInstance(
                TESTING_CLUSTER, TESTING_HOSTS);
        EsIndex index = new EsIndexImpl("index_for_test_ad", EsIndex.CATEGORY_AD, retryCount, 1, client);
        EsType type = new EsTypeImpl(index.getName(), "TypeForTest", "routingId", retryCount, 1, client);
        return type;
    }

    private EsType createEsTypeInstanceForSuccess(int retryCount) {
        InternalEsClient client = EsClientForNoNodeAvailableExceptionTestForSuccess.getInstance(
                TESTING_CLUSTER, TESTING_HOSTS);
        EsIndex index = new EsIndexImpl("index_for_test_ad", EsIndex.CATEGORY_AD, retryCount, 1, client);
        EsType type = new EsTypeImpl(index.getName(), "TypeForTest", "routingId", retryCount, 1, client);
        return type;
    }

    private EsIndex createEsIndexInstance(int retryCount) {
        InternalEsClient client = EsClientForNoNodeAvailableExceptionTestForFail.getInstance(
                TESTING_CLUSTER, TESTING_HOSTS);
        EsIndex index = new EsIndexImpl("index_for_test_ad", EsIndex.CATEGORY_AD, retryCount, 1, client);
        return index;
    }

    private EsIndex createEsIndexInstanceForSuccess(int retryCount) {
        InternalEsClient client = EsClientForNoNodeAvailableExceptionTestForSuccess.getInstance(
                TESTING_CLUSTER, TESTING_HOSTS);
        EsIndex index = new EsIndexImpl("index_for_test_ad", EsIndex.CATEGORY_AD, retryCount, 1, client);
        return index;
    }

    /**
     * EsTypeのsearchメソッドでリトライなしでエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsTypeのsearchメソッドでリトライなしでエラーとなること() {
        EsType type = createEsTypeInstance(0);
        type.search(new HashMap<String, Object>());
    }

    /**
     * EsTypeのsearchメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsTypeのsearchメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "routingId", 0, 0);
        assertNotNull(type);

        type.create("id00001", new HashMap<Object, Object>());
        // client.closeConnection();

        type = createEsTypeInstanceForSuccess(4);
        Map<String, Object> query = new HashMap<String, Object>();
        Map<String, Object> matchAll = new HashMap<String, Object>();
        matchAll.put("match_all", new HashMap<String, Object>());
        query.put("query", matchAll);
        DcSearchResponse sResponse = type.search(query);
        assertEquals(1, sResponse.getHits().getCount());
    }

    /**
     * EsTypeのsearchメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsTypeのsearchメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<Object, Object>());

        type = createEsTypeInstanceForSuccess(3);
        Map<String, Object> query = new HashMap<String, Object>();
        Map<String, Object> matchAll = new HashMap<String, Object>();
        matchAll.put("match_all", new HashMap<String, Object>());
        query.put("query", matchAll);
        DcSearchResponse sResponse = type.search(query);
        assertEquals(1, sResponse.getHits().getCount());
    }

    /**
     * EsTypeのsearchメソッドでリトライ2回でエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsTypeのsearchメソッドでリトライ2回でエラーとなること() {
        EsType type = createEsTypeInstance(2);
        type.search(new HashMap<String, Object>());
    }

    /**
     * EsTypeのsearchメソッドでリトライ3回でエラーとならないこと.
     * 確認用のEsClientオブジェクトから4回目の呼び出しでIllegalStateExceptionをスローされることを確認している。
     */
    @Test(expected = IllegalStateException.class)
    public void EsTypeのsearchメソッドでリトライ3回でエラーとならないこと() {
        EsType type = createEsTypeInstance(3);
        type.search(new HashMap<String, Object>());
    }

    /**
     * EsTypeのcreateメソッドでリトライなしでエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsTypeのcreateメソッドでリトライなしでエラーとなること() {
        EsType type = createEsTypeInstance(0);
        type.create(new HashMap<String, Object>());
    }

    /**
     * EsTypeのcreateメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsTypeのcreateメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();

        EsType type = createEsTypeInstanceForSuccess(4);
        assertNotNull(type);
        DcIndexResponse iResponse = type.create("id00001", new HashMap<Object, Object>());

        assertEquals("id00001", iResponse.getId());
    }

    /**
     * EsTypeのcreateメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsTypeのcreateメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();

        EsType type = createEsTypeInstanceForSuccess(3);
        assertNotNull(type);
        DcIndexResponse iResponse = type.create("id00001", new HashMap<Object, Object>());

        assertEquals("id00001", iResponse.getId());
    }

    /**
     * EsTypeのcreateメソッドでリトライ2回でエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsTypeのcreateメソッドでリトライ2回でエラーとなること() {
        EsType type = createEsTypeInstance(2);
        type.create(new HashMap<String, Object>());
    }

    /**
     * EsTypeのcreateメソッドでリトライ3回でエラーとならないこと.
     * 確認用のEsClientオブジェクトから4回目の呼び出しでIllegalStateExceptionをスローされることを確認している。
     */
    @Test(expected = IllegalStateException.class)
    public void EsTypeのcreateメソッドでリトライ3回でエラーとならないこと() {
        EsType type = createEsTypeInstance(3);
        type.create(new HashMap<String, Object>());
    }

    /**
     * EsTypeのupdateメソッドでリトライなしでエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsTypeのupdateメソッドでリトライなしでエラーとなること() {
        EsType type = createEsTypeInstance(0);
        type.update("", new HashMap<String, Object>());
    }

    /**
     * EsTypeのupdateメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsTypeのupdateメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<Object, Object>());

        type = createEsTypeInstanceForSuccess(4);
        DcIndexResponse iResponse = type.update("id00001", new HashMap<String, Object>());
        assertEquals("id00001", iResponse.getId());
    }

    /**
     * EsTypeのupdateメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsTypeのupdateメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<Object, Object>());

        type = createEsTypeInstanceForSuccess(3);
        DcIndexResponse iResponse = type.update("id00001", new HashMap<String, Object>());
        assertEquals("id00001", iResponse.getId());
    }

    /**
     * EsTypeのupdateメソッドでリトライ2回でエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsTypeのupdateメソッドでリトライ2回でエラーとなること() {
        EsType type = createEsTypeInstance(2);
        type.update("", new HashMap<String, Object>());
    }

    /**
     * EsTypeのupdateメソッドでリトライ3回でエラーとならないこと.
     * 確認用のEsClientオブジェクトから4回目の呼び出しでIllegalStateExceptionをスローされることを確認している。
     */
    @Test(expected = IllegalStateException.class)
    public void EsTypeのupdateメソッドでリトライ3回でエラーとならないこと() {
        EsType type = createEsTypeInstance(3);
        type.update("", new HashMap<String, Object>());
    }

    /**
     * EsTypeのdeleteメソッドでリトライなしでエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsTypeのdeleteメソッドでリトライなしでエラーとなること() {
        EsType type = createEsTypeInstance(0);
        type.delete("", 0);
    }

    /**
     * EsTypeのdeleteメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsTypeのdeleteメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<Object, Object>());

        type = createEsTypeInstanceForSuccess(4);
        DcDeleteResponse dResponse = type.delete("id00001", 0);
        assertEquals("id00001", dResponse.getId());
    }

    /**
     * EsTypeのdeleteメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsTypeのdeleteメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<Object, Object>());

        type = createEsTypeInstanceForSuccess(3);
        DcDeleteResponse dResponse = type.delete("id00001", 0);
        assertEquals("id00001", dResponse.getId());
    }

    /**
     * EsTypeのdeleteメソッドでリトライ2回でエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsTypeのdeleteメソッドでリトライ2回でエラーとなること() {
        EsType type = createEsTypeInstance(2);
        type.delete("", 0);
    }

    /**
     * EsTypeのdeleteメソッドでリトライ3回でエラーとならないこと.
     * 確認用のEsClientオブジェクトから4回目の呼び出しでIllegalStateExceptionをスローされることを確認している。
     */
    @Test(expected = IllegalStateException.class)
    public void EsTypeのdeleteメソッドでリトライ3回でエラーとならないこと() {
        EsType type = createEsTypeInstance(3);
        type.delete("", 0);
    }

    /**
     * EsTypeのgetメソッドでリトライなしでエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsTypeのgetメソッドでリトライなしでエラーとなること() {
        EsType type = createEsTypeInstance(0);
        type.get("");
    }

    /**
     * EsTypeのgetメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsTypeのgetメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<Object, Object>());

        type = createEsTypeInstanceForSuccess(4);
        DcGetResponse response = type.get("id00001");
        assertEquals("TypeForTest", response.getType());
    }

    /**
     * EsTypeのgetメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsTypeのgetメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<Object, Object>());

        type = createEsTypeInstanceForSuccess(3);
        DcGetResponse response = type.get("id00001");
        assertEquals("TypeForTest", response.getType());
        // assertEquals("index_for_test", response.getIndex());
    }

    /**
     * EsTypeのgetメソッドでリトライ2回でエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsTypeのgetメソッドでリトライ2回でエラーとなること() {
        EsType type = createEsTypeInstance(2);
        type.get("");
    }

    /**
     * EsTypeのgetメソッドでリトライ3回でエラーとならないこと.
     * 確認用のEsClientオブジェクトから4回目の呼び出しでIllegalStateExceptionをスローされることを確認している。
     */
    @Test(expected = IllegalStateException.class)
    public void EsTypeのgetメソッドでリトライ3回でエラーとならないこと() {
        EsType type = createEsTypeInstance(3);
        type.get("");
    }

    /**
     * EsTypeのputMappingメソッドでリトライなしでエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsTypeのputMappingメソッドでリトライなしでエラーとなること() {
        EsType type = createEsTypeInstance(0);
        type.putMapping(new HashMap<String, Object>());
    }

    /**
     * EsTypeのputMappingメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsTypeのputMappingメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<Object, Object>());

        type = createEsTypeInstanceForSuccess(4);
        Map<String, Object> putMap = new HashMap<String, Object>();
        Map<String, Object> expected = new HashMap<String, Object>();
        Map<String, Object> properties = new HashMap<String, Object>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("type", "string");
        properties.put("item", item);
        expected.put("properties", properties);
        putMap.put("TypeForTest", expected);

        DcPutMappingResponse res = type.putMapping(putMap);
        assertNotNull(res);
    }

    /**
     * EsTypeのputMappingメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsTypeのputMappingメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<Object, Object>());

        type = createEsTypeInstanceForSuccess(3);
        type = createEsTypeInstanceForSuccess(4);
        Map<String, Object> putMap = new HashMap<String, Object>();
        Map<String, Object> expected = new HashMap<String, Object>();
        Map<String, Object> properties = new HashMap<String, Object>();
        Map<String, Object> item = new HashMap<String, Object>();
        item.put("type", "string");
        properties.put("item", item);
        expected.put("properties", properties);
        putMap.put("TypeForTest", expected);

        DcPutMappingResponse res = type.putMapping(putMap);
        assertNotNull(res);
    }

    /**
     * EsTypeのputMappingメソッドでリトライ2回でエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsTypeのputMappingメソッドでリトライ2回でエラーとなること() {
        EsType type = createEsTypeInstance(2);
        type.putMapping(new HashMap<String, Object>());
    }

    /**
     * EsTypeのputMappingメソッドでリトライ3回でエラーとならないこと.
     * 確認用のEsClientオブジェクトから4回目の呼び出しでIllegalStateExceptionをスローされることを確認している。
     */
    @Test(expected = IllegalStateException.class)
    public void EsTypeのputMappingメソッドでリトライ3回でエラーとならないこと() {
        EsType type = createEsTypeInstance(3);
        type.putMapping(new HashMap<String, Object>());
    }

    /**
     * EsIndexのcreateメソッドでリトライなしでエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsIndexのcreateメソッドでリトライなしでエラーとなること() {
        EsIndex index = createEsIndexInstance(0);
        index.create();
    }

    /**
     * EsIndexのcreateメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsIndexのcreateメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = createEsIndexInstanceForSuccess(4);
        index.create();
    }

    /**
     * EsIndexのcreateメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsIndexのcreateメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = createEsIndexInstanceForSuccess(3);
        index.create();
    }

    /**
     * EsIndexのcreateメソッドでリトライ2回でエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsIndexのcreateメソッドでリトライ2回でエラーとなること() {
        EsIndex index = createEsIndexInstance(2);
        index.create();
    }

    /**
     * EsIndexのcreateメソッドでリトライ3回でエラーとならないこと.
     * 確認用のEsClientオブジェクトから4回目の呼び出しでIllegalStateExceptionをスローされることを確認している。
     */
    @Test(expected = IllegalStateException.class)
    public void EsIndexのcreateメソッドでリトライ3回でエラーとならないこと() {
        EsIndex index = createEsIndexInstance(3);
        index.create();
    }

    /**
     * EsIndexのsearchメソッドでリトライなしでエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsIndexのsearchメソッドでリトライなしでエラーとなること() {
        EsIndex index = createEsIndexInstance(0);
        index.search("dummyRoutingId", new HashMap<String, Object>());
    }

    /**
     * EsIndexのsearchメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsIndexのsearchメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<Object, Object>());

        index = createEsIndexInstanceForSuccess(4);
        Map<String, Object> query = new HashMap<String, Object>();
        Map<String, Object> matchAll = new HashMap<String, Object>();
        matchAll.put("match_all", new HashMap<String, Object>());
        query.put("query", matchAll);
        DcSearchResponse sResponse = index.search("index_for_test", query);
        assertEquals(1, sResponse.getHits().getCount());
    }

    /**
     * EsIndexのsearchメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsIndexのsearchメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<Object, Object>());

        index = createEsIndexInstanceForSuccess(3);
        Map<String, Object> query = new HashMap<String, Object>();
        Map<String, Object> matchAll = new HashMap<String, Object>();
        matchAll.put("match_all", new HashMap<String, Object>());
        query.put("query", matchAll);
        DcSearchResponse sResponse = index.search("index_for_test", query);
        assertEquals(1, sResponse.getHits().getCount());
    }

    /**
     * EsIndexのsearchメソッドでリトライ2回でエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsIndexのsearchメソッドでリトライ2回でエラーとなること() {
        EsIndex index = createEsIndexInstance(2);
        index.search("dummyRoutingId", new HashMap<String, Object>());
    }

    /**
     * EsIndexのsearchメソッドでリトライ3回でエラーとならないこと.
     * 確認用のEsClientオブジェクトから4回目の呼び出しでIllegalStateExceptionをスローされることを確認している。
     */
    @Test(expected = IllegalStateException.class)
    public void EsIndexのsearchメソッドでリトライ3回でエラーとならないこと() {
        EsIndex index = createEsIndexInstance(3);
        index.search("dummyRoutingId", new HashMap<String, Object>());
    }

    /**
     * EsIndexのdeleteメソッドでリトライなしでエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsIndexのdeleteメソッドでリトライなしでエラーとなること() {
        EsIndex index = createEsIndexInstance(0);
        index.delete();
    }

    /**
     * EsIndexのdeleteメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsIndexのdeleteメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();

        index = createEsIndexInstanceForSuccess(4);
        index.delete();
    }

    /**
     * EsIndexのdeleteメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsIndexのdeleteメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();

        index = createEsIndexInstanceForSuccess(3);
        index.delete();
    }

    /**
     * EsIndexのdeleteメソッドでリトライ2回でエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsIndexのdeleteメソッドでリトライ2回でエラーとなること() {
        EsIndex index = createEsIndexInstance(2);
        index.delete();
    }

    /**
     * EsIndexのdeleteメソッドでリトライ3回でエラーとならないこと.
     * 確認用のEsClientオブジェクトから4回目の呼び出しでIllegalStateExceptionをスローされることを確認している。
     */
    @Test(expected = IllegalStateException.class)
    public void EsIndexのdeleteメソッドでリトライ3回でエラーとならないこと() {
        EsIndex index = createEsIndexInstance(3);
        index.delete();
    }

    /**
     * EsIndexのdeleteByQueryメソッドでリトライなしでエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsIndexのdeleteByQueryメソッドでリトライなしでエラーとなること() {
        EsIndex index = createEsIndexInstance(0);
        index.deleteByQuery("dummyRoutingId", null);
    }

    /**
     * EsIndexのdeleteByQueryメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsIndexのdeleteByQueryメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<String, Object>());

        index = createEsIndexInstanceForSuccess(4);
        DcQueryBuilder queryBuilder = DcQueryBuilders.matchQuery("_id", "id00001");

        index.deleteByQuery("index_for_test", queryBuilder);
    }

    /**
     * EsIndexのdeleteByQueryメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsIndexのdeleteByQueryメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsType type = esClient.type(index.getName(), "TypeForTest", "", 0, 0);
        assertNotNull(type);
        type.create("id00001", new HashMap<Object, Object>());

        index = createEsIndexInstanceForSuccess(3);
        DcQueryBuilder queryBuilder = DcQueryBuilders.matchQuery("_id", "id00001");
        index.deleteByQuery("index_for_test", queryBuilder);
    }

    /**
     * EsIndexのdeleteByQueryメソッドでリトライ2回でエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsIndexのdeleteByQueryメソッドでリトライ2回でエラーとなること() {
        EsIndex index = createEsIndexInstance(2);
        index.deleteByQuery("dummyRoutingId", null);
    }

    /**
     * EsIndexのdeleteByQueryメソッドでリトライ3回でエラーとならないこと.
     * 確認用のEsClientオブジェクトから4回目の呼び出しでIllegalStateExceptionをスローされることを確認している。
     */
    @Test(expected = IllegalStateException.class)
    public void EsIndexのdeleteByQueryメソッドでリトライ3回でエラーとならないこと() {
        EsIndex index = createEsIndexInstance(3);
        index.deleteByQuery("dummyRoutingId", null);
    }

    /**
     * EsIndexのbulkCreateメソッドでリトライなしでエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsIndexのbulkCreateメソッドでリトライなしでエラーとなること() {
        EsIndex index = createEsIndexInstance(0);
        index.bulkRequest("", null, false);
    }

    /**
     * バルク登録テスト用データクラス.
     */
    class BulkRequest implements EsBulkRequest {

        String type = "type";

        public BulkRequest() {
        }

        public String getType() {
            return this.type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getId() {
            return "id";
        }

        public Map<String, Object> getSource() {
            return new HashMap<String, Object>();
        }

        @Override
        public BULK_REQUEST_TYPE getRequestType() {
            return BULK_REQUEST_TYPE.INDEX;
        }
    }

    /**
     * EsIndexのbulkCreateメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsIndexのbulkCreateメソッドでリトライ回数に最大値までにデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();

        index = createEsIndexInstanceForSuccess(4);
        List<EsBulkRequest> datas = new ArrayList<EsBulkRequest>();
        datas.add(new BulkRequest());
        index.bulkRequest("index_for_test", datas, false);
    }

    /**
     * EsIndexのbulkCreateメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと.
     */
    @Test
    public void EsIndexのbulkCreateメソッドで最後のリトライでデータが取得できた場合にExceptionが発生しないこと() {
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();

        index = createEsIndexInstanceForSuccess(3);
        List<EsBulkRequest> datas = new ArrayList<EsBulkRequest>();
        datas.add(new BulkRequest());
        index.bulkRequest("index_for_test", datas, false);
    }

    /**
     * EsIndexのbulkCreateメソッドでリトライ2回でエラーとなること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void EsIndexのbulkCreateメソッドでリトライ2回でエラーとなること() {
        EsIndex index = createEsIndexInstance(2);
        index.bulkRequest("", null, false);
    }

    /**
     * EsIndexのbulkCreateメソッドでリトライ3回でエラーとならないこと.
     * 確認用のEsClientオブジェクトから4回目の呼び出しでIllegalStateExceptionをスローされることを確認している。
     */
    @Test(expected = IllegalStateException.class)
    public void EsIndexのbulkCreateメソッドでリトライ3回でエラーとならないこと() {
        EsIndex index = createEsIndexInstance(3);
        index.bulkRequest("", null, false);
    }

    /**
     * テスト用に常に例外をスローするEsClient.
     */
    private static class EsClientForNoNodeAvailableExceptionTestForSuccess extends InternalEsClient {
        // 4回目の時のリトライ動作
        private boolean flag = true;
        private int retryTimes = 0;

        protected EsClientForNoNodeAvailableExceptionTestForSuccess(String cluster, String hosts) {
            super(cluster, hosts);
        }

        public static InternalEsClient getInstance(String cluster, String hosts) {
            return new EsClientForNoNodeAvailableExceptionTestForSuccess(cluster, hosts);
        }

        @Override
        public ActionFuture<GetResponse> asyncGet(String index, String type, String id, String routingId,
                boolean realtime) {
            throwException();
            return super.asyncGet(index, type, id, routingId, realtime);
        }

        @Override
        public ActionFuture<SearchResponse> asyncSearch(
                String index,
                String type,
                String routingId,
                SearchSourceBuilder builder) {
            throwException();
            return super.asyncSearch(index, type, routingId, builder);
        }

        @Override
        public ActionFuture<SearchResponse> asyncSearch(
                String index,
                String routingId,
                Map<String, Object> query) {
            throwException();
            return super.asyncSearch(index, routingId, query);
        }

        @Override
        public ActionFuture<SearchResponse> asyncSearch(
                String index,
                String type,
                String routingId,
                Map<String, Object> query) {
            throwException();
            return super.asyncSearch(index, type, routingId, query);
        }

        @Override
        public ActionFuture<IndexResponse> asyncIndex(String index,
                String type,
                String id,
                String routingId,
                Map<String, Object> data,
                OpType opType,
                long version) {
            throwException();
            return super.asyncIndex(index, type, id, routingId, data, opType, version);
        }

        @Override
        public ActionFuture<DeleteResponse> asyncDelete(String index, String type, String id, String routingId,
                long version) {
            throwException();
            return super.asyncDelete(index, type, id, routingId, version);
        }

        @Override
        public ListenableActionFuture<PutMappingResponse> putMapping(String index,
                String type, Map<String, Object> mappings) {
            throwException();
            return super.putMapping(index, type, mappings);
        }

        @Override
        public ActionFuture<CreateIndexResponse> createIndex(String index, Map<String, JSONObject> mappings) {
            throwException();
            return super.createIndex(index, mappings);
        }

        @Override
        public ActionFuture<DeleteIndexResponse> deleteIndex(String index) {
            throwException();
            return super.deleteIndex(index);
        }

        @Override
        public BulkResponse bulkRequest(String index, String routingId, List<EsBulkRequest> datas, boolean isWriteLog) {
            throwException();
            return super.bulkRequest(index, routingId, datas, isWriteLog);
        }

        @Override
        public DeleteByQueryResponse deleteByQuery(String index, QueryBuilder deleteQuery) {
            throwException();
            return super.deleteByQuery(index, deleteQuery);
        }

        void throwException() {
            // 3回目の呼び出しまではNoNodeAvailableExceptionをスローする
            if (this.retryTimes == 3) {
                this.retryTimes = 0;
                // 4回目の呼び出しでthis.successがtrueの場合、例外を上げない
                // 呼出しもとで正常な処理を行う
                if (this.flag) {
                    return;
                }
                // 4回目の呼び出しでthis.successがfalseの場合、IllegalStateExceptionをスローする
                throw new IllegalStateException();
            }
            this.retryTimes++;
            throw new NoNodeAvailableException("retry error");
        }
    }

    /**
     * テスト用に常に例外をスローするEsClient.
     */
    private static class EsClientForNoNodeAvailableExceptionTestForFail extends InternalEsClient {
        // 4回目の時のリトライ動作
        private boolean flag = false;
        private int retryTimes = 0;

        protected EsClientForNoNodeAvailableExceptionTestForFail(String cluster, String hosts) {
            super(cluster, hosts);
        }

        public static InternalEsClient getInstance(String cluster, String hosts) {
            return new EsClientForNoNodeAvailableExceptionTestForFail(cluster, hosts);
        }

        @Override
        public ActionFuture<GetResponse> asyncGet(String index, String type, String id, String routingId,
                boolean realtime) {
            throwException();
            return super.asyncGet(index, type, id, routingId, realtime);
        }

        @Override
        public ActionFuture<SearchResponse> asyncSearch(
                String index,
                String type,
                String routingId,
                SearchSourceBuilder builder) {
            throwException();
            return super.asyncSearch(index, type, routingId, builder);
        }

        @Override
        public ActionFuture<SearchResponse> asyncSearch(
                String index,
                String routingId,
                Map<String, Object> query) {
            throwException();
            return super.asyncSearch(index, routingId, query);
        }

        @Override
        public ActionFuture<SearchResponse> asyncSearch(
                String index,
                String type,
                String routingId,
                Map<String, Object> query) {
            throwException();
            return super.asyncSearch(index, type, routingId, query);
        }

        @Override
        public ActionFuture<IndexResponse> asyncIndex(String index,
                String type,
                String id,
                String routingId,
                Map<String, Object> data,
                OpType opType,
                long version) {
            throwException();
            return super.asyncIndex(index, type, id, routingId, data, opType, version);
        }

        @Override
        public ActionFuture<DeleteResponse> asyncDelete(String index, String type, String id, String routingId,
                long version) {
            throwException();
            return super.asyncDelete(index, type, id, routingId, version);
        }

        @Override
        public ListenableActionFuture<PutMappingResponse> putMapping(String index,
                String type, Map<String, Object> mappings) {
            throwException();
            return super.putMapping(index, type, mappings);
        }

        @Override
        public ActionFuture<CreateIndexResponse> createIndex(String index, Map<String, JSONObject> mappings) {
            throwException();
            return super.createIndex(index, mappings);
        }

        @Override
        public ActionFuture<DeleteIndexResponse> deleteIndex(String index) {
            throwException();
            return super.deleteIndex(index);
        }

        @Override
        public BulkResponse bulkRequest(String index, String routingId, List<EsBulkRequest> datas, boolean isWriteLog) {
            throwException();
            return super.bulkRequest(index, routingId, datas, isWriteLog);
        }

        @Override
        public DeleteByQueryResponse deleteByQuery(String index, QueryBuilder deleteQuery) {
            throwException();
            return super.deleteByQuery(index, deleteQuery);
        }

        void throwException() {
            // 3回目の呼び出しまではNoNodeAvailableExceptionをスローする
            if (this.retryTimes == 3) {
                this.retryTimes = 0;
                // 4回目の呼び出しでthis.successがtrueの場合、例外を上げない
                // 呼出しもとで正常な処理を行う
                if (this.flag) {
                    return;
                }
                // 4回目の呼び出しでthis.successがfalseの場合、IllegalStateExceptionをスローする
                throw new IllegalStateException();
            }
            this.retryTimes++;
            throw new NoNodeAvailableException("retry error");
        }
    }
}
