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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.HashMap;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.engine.DocumentAlreadyExistsException;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.transport.NodeDisconnectedException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;

import com.fujitsu.dc.common.es.EsClient;
import com.fujitsu.dc.common.es.EsIndex;
import com.fujitsu.dc.common.es.EsType;
import com.fujitsu.dc.common.es.response.DcIndexResponse;
import com.fujitsu.dc.common.es.response.EsClientException;

/**
 * EsTypeクラスのリトライテスト. 初版では、createメソッドのみ対応
 */
public class EsRetry2Test {

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
                .put("node.http.enabled", true)
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


    /**
     * ドキュメント新規作成時_初回でDocumentAlreadyExistsExceptionが発生した場合にEsClient_EsClientExceptionが返されること. ※
     * DocumentAlreadyExistsExceptionに限らず、初回処理でキャッチ対象でない例外が発生した場合の処理を対象としたテスト
     */
    @Test(expected = EsClientException.class)
    public void ドキュメント新規作成時_初回でDocumentAlreadyExistsExceptionが発生した場合にEsClient_EsClientExceptionが返されること() {
        EsIndex index = esClient.idxUser("index_for_test", EsIndex.CATEGORY_AD);
        try {
            index.create();
            EsType type = esClient.type("index_for_test_" + EsIndex.CATEGORY_AD,
                    "TypeForTest", "TestRoutingId", 5, 500);
            EsTypeImpl esTypeObject = (EsTypeImpl) Mockito.spy(type);

            // EsType#asyncIndex()が呼ばれた場合に、DocumentAlreadyExistsExceptionを投げる。
            // 送出する例外オブジェクトのモックを作成
            DocumentAlreadyExistsException toBeThrown = Mockito.mock(DocumentAlreadyExistsException.class);
            Mockito.doThrow(toBeThrown)
                    .when(esTypeObject)
                    .asyncIndex(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                            Mockito.any(OpType.class), Mockito.anyLong());
            // メソッド呼び出し
            esTypeObject.create("dummyId", null);
            fail("EsClientException should be thrown.");
        } finally {
            index.delete();
        }
    }

    /**
     * ドキュメント新規作成時_初回でIndexMissingExceptionが発生した場合にEsClient_EsIndexMissingExceptionが返されること.
     */
    @Test(expected = EsClientException.EsIndexMissingException.class)
    public void ドキュメント新規作成時_初回でIndexMissingExceptionが発生した場合にEsClient_EsIndexMissingExceptionが返されること() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncIndex()が呼ばれた場合に、IndexMissingExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        IndexMissingException toBeThrown = Mockito.mock(IndexMissingException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncIndex(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                        Mockito.any(OpType.class), Mockito.anyLong());
        // メソッド呼び出し
        esTypeObject.create("dummyId", null);
        fail("EsIndexMissingException should be thrown.");
    }

    /**
     * ドキュメント新規作成時_初回で根本例外IndexMissingExceptionが発生した場合にEsClient_EsIndexMissingExceptionが返されること.
     */
    @Test(expected = EsClientException.EsIndexMissingException.class)
    public void ドキュメント新規作成時_初回で根本例外IndexMissingExceptionが発生した場合にEsClient_EsIndexMissingExceptionが返されること() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));
        // EsType#asyncIndex()が呼ばれた場合に、根本例外にIndexMissingExceptionを含むElasticsearchExceptionを投げる。
        ElasticsearchException toBeThrown = new ElasticsearchException("dummy", new IndexMissingException(new Index(
                "foo")));
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncIndex(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                        Mockito.any(OpType.class), Mockito.anyLong());
        esTypeObject.create("dummyId", null);
        fail("EsIndexMissingException should be thrown.");
    }

    /**
     * ドキュメント新規作成時_初回でMapperParsingExceptionが発生した場合にEsClient_EsSchemaMismatchExceptionが返されること.
     */
    @Test(expected = EsClientException.EsSchemaMismatchException.class)
    public void ドキュメント新規作成時_初回でMapperParsingExceptionが発生した場合にEsClient_EsSchemaMismatchExceptionが返されること() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncIndex()が呼ばれた場合に、MapperParsingExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        MapperParsingException toBeThrown = Mockito.mock(MapperParsingException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncIndex(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                        Mockito.any(OpType.class), Mockito.anyLong());
        // メソッド呼び出し
        esTypeObject.create("dummyId", new HashMap<Object, Object>());
        fail("EsSchemaMismatchException should be thrown.");
    }

    /**
     * ドキュメント新規作成時_初回NodeDisconnectedExceptionが発生した場合にリトライを繰り返し最終的にEsClient_EsNoResponseExceptionが返されること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void ドキュメント新規作成時_初回NodeDisconnectedExceptionが発生した場合にリトライを繰り返し最終的にEsClient_EsNoResponseExceptionが返されること() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 5, 500, null));

        // EsType#asyncIndex()が呼ばれた場合に、NodeDisconnectedExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        NodeDisconnectedException toBeThrown = Mockito.mock(NodeDisconnectedException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncIndex(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                        Mockito.any(OpType.class), Mockito.anyLong());
        // メソッド呼び出し
        esTypeObject.create("dummyId", null);
        fail("EsNoResponseException should be thrown.");
    }

    /**
     * ドキュメント新規作成時_初回NoNodeAvailableExceptionが発生した場合にリトライを繰り返し最終的にEsClient_EsNoResponseExceptionが返されること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void ドキュメント新規作成時_初回NoNodeAvailableExceptionが発生した場合にリトライを繰り返し最終的にEsClient_EsNoResponseExceptionが返されること() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 5, 500, null));

        // EsType#asyncIndex()が呼ばれた場合に、NodeDisconnectedExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        NoNodeAvailableException toBeThrown = Mockito.mock(NoNodeAvailableException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncIndex(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                        Mockito.any(OpType.class), Mockito.anyLong());
        // メソッド呼び出し
        esTypeObject.create("dummyId", null);
        fail("EsNoResponseException should be thrown.");
    }

    /**
     * ドキュメント新規作成時_リトライ処理初回でDocumentAlreadyExistsExceptionが返された場合に正常なIndexResponseが返されること.
     */
    @Test
    public void ドキュメント新規作成時_リトライ処理初回でDocumentAlreadyExistExceptionが返された場合に正常なIndexResponseが返されること() {
        EsIndex index = esClient.idxUser("index_for_test", EsIndex.CATEGORY_AD);
        try {
            index.create();
            EsType type = esClient.type("index_for_test_" + EsIndex.CATEGORY_AD,
                    "TypeForTest", "TestRoutingId", 5, 500);
            type.create("dummyId", new HashMap<Object, Object>());
            EsTypeImpl esTypeObject = (EsTypeImpl) Mockito.spy(type);

            // EsType#asyncIndex()が呼ばれた場合に、NodeDisconnectedExceptionを投げる。
            // 送出する例外オブジェクトのモックを作成
            NodeDisconnectedException esDisconnectedException = Mockito.mock(NodeDisconnectedException.class);
            DocumentAlreadyExistsException documentAlreadyExists = Mockito.mock(DocumentAlreadyExistsException.class);
            Mockito.doThrow(esDisconnectedException)
                    // 本来のリクエスト
                    .doThrow(documentAlreadyExists)
                    // リトライ1回目
                    .when(esTypeObject)
                    .asyncIndex(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                            Mockito.any(OpType.class), Mockito.anyLong());
            // メソッド呼び出し
            DcIndexResponse response = esTypeObject.create("dummyId", new HashMap<Object, Object>());
            assertNotNull(response);
            assertEquals("index_for_test_" + EsIndex.CATEGORY_AD, response.getIndex());
            assertEquals("dummyId", response.getId());
            assertEquals("TypeForTest", response.getType());
            assertEquals(1, response.getVersion());
        } finally {
            index.delete();
        }
    }

    /**
     * ドキュメント新規作成時_リトライ処理の最大回数終了時点でDocumentAlreadyExistsExceptionが返された場合に正常なIndexResponseが返されること.
     */
    @Test
    public void ドキュメント新規作成時_リトライ処理の最大回数終了時点でDocumentAlreadyExistExceptionが返された場合に正常なIndexResponseが返されること() {
        EsIndex index = esClient.idxUser("index_for_test", EsIndex.CATEGORY_AD);
        try {
            index.create();
            EsType type = esClient.type("index_for_test_" + EsIndex.CATEGORY_AD,
                    "TypeForTest", "TestRoutingId", 5, 500);
            type.create("dummyId", new HashMap<Object, Object>());
            EsTypeImpl esTypeObject = (EsTypeImpl) Mockito.spy(type);

            // EsType#asyncIndex()が呼ばれた場合に、NodeDisconnectedExceptionを投げる。

            // 送出する例外オブジェクトのモックを作成
            NodeDisconnectedException esDisconnectedException = Mockito.mock(NodeDisconnectedException.class);
            NoNodeAvailableException esNoNodeAvailableException = Mockito.mock(NoNodeAvailableException.class);
            DocumentAlreadyExistsException documentAlreadyExists = Mockito.mock(DocumentAlreadyExistsException.class);
            Mockito.doThrow(esDisconnectedException)
                    // 本来のリクエスト時の例外
                    .doThrow(esNoNodeAvailableException)
                    // リトライ１回目
                    .doThrow(esNoNodeAvailableException)
                    // リトライ2回目
                    .doThrow(esNoNodeAvailableException)
                    // リトライ3回目
                    .doThrow(esNoNodeAvailableException)
                    // リトライ4回目
                    .doThrow(documentAlreadyExists)
                    .when(esTypeObject)
                    .asyncIndex(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                            Mockito.any(OpType.class), Mockito.anyLong());
            // メソッド呼び出し
            DcIndexResponse response = esTypeObject.create("dummyId", new HashMap<Object, Object>());
            assertNotNull(response);
            assertEquals("index_for_test_" + EsIndex.CATEGORY_AD, response.getIndex());
            assertEquals("dummyId", response.getId());
            assertEquals("TypeForTest", response.getType());
            assertEquals(1, response.getVersion());
        } finally {
            index.delete();
        }
    }

}
