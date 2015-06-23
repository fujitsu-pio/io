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

import org.elasticsearch.action.index.IndexResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fujitsu.dc.common.es.EsClient;
import com.fujitsu.dc.common.es.EsIndex;
import com.fujitsu.dc.common.es.response.EsClientException;
import com.fujitsu.dc.common.es.test.util.EsTestNode;

/**
 * EsModelの単体テストケース.
 */
public class EsTest {
    static EsTestNode node;

    /**
     * テストケース共通の初期化処理. テスト用のElasticsearchのNodeを初期化する
     * @throws Exception 異常が発生した場合の例外
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
        node = new EsTestNode();
        node.create();
    }

    /**
     * テストケース共通のクリーンアップ処理. テスト用のElasticsearchのNodeをクローズする
     * @throws Exception 異常が発生した場合の例外
     */
    @AfterClass
    public static void tearDownAfterClass() throws Exception {
        node.close();
    }

    private EsClient esClient;

    /**
     * 各テスト実行前の初期化処理.
     * @throws Exception 異常が発生した場合の例外
     */
    @Before
    public void setUp() throws Exception {
        esClient = new EsClient("testingCluster", "localhost:9399");
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

    //
    // /**
    // * EsClientのインスタンス生成.
    // */
    // @Test
    // public void EsClientのインスタンス生成() {
    // EsClient client = new EsClient("testingCluster", "localhost:9399");
    // client.closeConnection();
    // }
    //
    // /**
    // * クライアントオブジェクトの初期化とクローズ.
    // */
    // @Test
    // public void クライアントオブジェクトの初期化とクローズ() {
    // EsClient client = new EsClient("testingCluster", "localhost:9399");
    // client.closeConnection();
    // }
    //
    // // /**
    // // * クライアントオブジェクトの初期化とクローズ.
    // // */
    // // @Test
    // // public void getInstanceメソッドを複数回呼び出した場合に同一のインスタンスが返却される() {
    // // EsClient clientExpected = new EsClient("testingCluster", "localhost:9399");
    // // EsClient clientActual = new EsClient();
    // // assertEquals(clientExpected, clientActual);
    // // assertTrue(clientExpected == clientActual);
    // // clientActual.closeConnection();
    // // clientExpected.closeConnection();
    // // }
    //
    // // /**
    // // * TransportClientの準備.
    // // */
    // // @Test
    // // public void TransportClientの準備() {
    // // EsClient client = new EsClient("testingCluster", "localhost:9399");
    // // client.prepareClient();
    // // client.closeConnection();
    // // }
    //
    // /**
    // * 接続先に不正なホスト名を指定した場合にEsModelExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void 接続先に不正なホスト名を指定した場合にEsModelExceptionをスローする() {
    // EsClient client = new EsClient("testingCluster", "255.255.255.255:9399");
    // client.closeConnection();
    // }
    //
    // /**
    // * 接続先に不正ホスト名と正しいホスト名の順で指定した場合に正しく接続できる.
    // */
    // @Test
    // public void 接続先に不正ホスト名と正しいホスト名の順で指定した場合に正しく接続できる() {
    // EsClient client = new EsClient("testingCluster", "255.255.255.255:9399,localhost:9399");
    // client.closeConnection();
    // }
    //
    // /**
    // * 接続先に正しいホスト名と不正ホスト名の順で指定した場合に正しく接続できる.
    // */
    // @Test
    // public void 接続先に正しいホスト名と不正ホスト名の順で指定した場合に正しく接続できる() {
    // EsClient client = new EsClient("testingCluster", "localhost:9399,255.255.255.255:9399");
    // client.closeConnection();
    // }
    //
    // /**
    // * 接続先に不正なポート番号を指定した場合にEsModelExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void 接続先に不正なポート番号を指定した場合にEsModelExceptionをスローする() {
    // EsClient client = new EsClient("testingCluster", "localhost:9350");
    // client.closeConnection();
    // }
    //
    // /**
    // * ポート番号を省略した場合にデフォルト値(9300)で接続する. 処理結果は実行環境に依存するため結果は見ない
    // */
    // @Test
    // public void ポート番号を省略した場合にデフォルト値で接続する() {
    // try {
    // EsClient client = new EsClient("testingCluster", "localhost");
    // client.closeConnection();
    // } catch (Exception ex) {
    // System.out.println("");
    // }
    // }
    //
    // /**
    // * 接続先に不正なクラスタ名を指定した場合にEsModelExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void 接続先に不正なクラスタ名を指定した場合にEsModelExceptionをスローする() {
    // EsClient client = new EsClient("invalidCluster", "localhost:9399");
    // client.closeConnection();
    // }
    //
    // /**
    // * idxAdminメソッドでオブジェクトが正常に取得できる.
    // */
    // @Test
    // public void idxAdminメソッドでオブジェクトが正常に取得できる() {
    // EsIndex index = esClient.idxAdmin("u0");
    // assertNotNull(index);
    // assertTrue(index instanceof EsIndex);
    // assertEquals(EsIndex.CATEGORY_AD, index.getCategory());
    // }
    //
    // /**
    // * idxUserメソッドでnullを指定した場合にオブジェクトが正常に取得できる.
    // */
    // @Test
    // public void idxUserメソッドでnullを指定した場合にオブジェクトが正常に取得できる() {
    // EsIndex index = esClient.idxUser("u0", null);
    // assertNotNull(index);
    // assertTrue(index.getName().endsWith("anon"));
    // assertEquals(EsIndex.CATEGORY_USR, index.getCategory());
    // }
    //
    // /**
    // * idxUserメソッドでオブジェクトが正常に取得できる.
    // */
    // @Test
    // public void idxUserメソッドでオブジェクトが正常に取得できる() {
    // EsIndex index = esClient.idxUser("u0", "UriStringForTest");
    // assertNotNull(index);
    // assertTrue(index.getName().endsWith("uristringfortest"));
    // assertEquals(EsIndex.CATEGORY_USR, index.getCategory());
    // }
    //
    // // /**
    // // * インデックス名を指定してEsIndexインスタンスを生成する.
    // // */
    // // @Test
    // // public void インデックス名を指定してEsIndexインスタンスを生成する() {
    // // EsIndex index = new EsIndex("index_for_test");
    // // assertEquals("", index.getCategory());
    // // }
    //
    // /**
    // * ESインデックス名用Uriの井桁記号のEncodeを行う.
    // */
    // @Test
    // public void ESインデックス名用Uriの井桁記号のEncodeを行う() {
    // String uri = "http://localhost:9300#abc";
    // String encUri = EsIndex.encodeEsIndexName(uri);
    // assertEquals("abc", encUri);
    // }
    //
    // /**
    // * ESインデックス名用Uriのアンダースコア記号のEncodeを行う.
    // */
    // @Test
    // public void ESインデックス名用Uriのアンダースコア記号のEncodeを行う() {
    // String uri = "http://localhost:9300/";
    // String encUri = EsIndex.encodeEsIndexName(uri);
    // assertEquals("localhost__9300", encUri);
    // }
    //
    // /**
    // * ピリオドを含むURLからインデックス名を生成して_に変換される.
    // */
    // @Test
    // public void ピリオドを含むURLからインデックス名を生成して_に変換される() {
    // String uri = "http://local.host.domain:9300/";
    // String encUri = EsIndex.encodeEsIndexName(uri);
    // assertEquals("local_host_domain__9300", encUri);
    // }
    //
    // /**
    // * ESインデックス名用Uriのて大文字が小文字に変換される.
    // */
    // @Test
    // public void ESインデックス名用Uriのて大文字が小文字に変換される() {
    // String uri = "http://localhost:9300/XYZ#ABC";
    // String encUri = EsIndex.encodeEsIndexName(uri);
    // assertEquals("abc", encUri);
    // }
    //
    // /**
    // * インデックス名生成結果が64バイトを超えた場合に例外をスローする.
    // */
    // @Test(expected = TooLongIndexNameException.class)
    // public void インデックス名生成結果が64バイトを超えた場合に例外をスローする() {
    // String uri = "http://too.looooooooooooooooooooooooooooooooooooooooooooooooooooooooong.hostname/";
    // EsIndex.encodeEsIndexName(uri);
    // }
    //
    // /**
    // * 不正なインデックスカテゴリを指定した場合にドキュメント作成処理でEsClientExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void 不正なインデックスカテゴリを指定した場合にドキュメント作成処理でEsClientExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", "InvalidCategory", 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // }
    //
    // /**
    // * EsTypeオブジェクトからEsIndexオブジェクトを取得できる.
    // */
    // @Test
    // public void EsTypeオブジェクトからEsIndexオブジェクトを取得できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // assertEquals(index.getName(), type.getIndexName());
    // }
    //
    // /**
    // * EsTypeオブジェクトからタイプ名を取得できる.
    // */
    // @Test
    // public void EsTypeオブジェクトからタイプ名を取得できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // assertEquals("TypeForTest", type.getType());
    // }
    //
    // /**
    // * Routing指定ありでEsTypeオブジェクトを取得できる.
    // */
    // @Test
    // public void Routing指定ありでEsTypeオブジェクトを取得できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // }
    //
    // /**
    // * 正常にドキュメントを作成できる.
    // */
    // @Test
    // public void 正常にドキュメントを作成できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // IndexResponse response = type.create("id00001", new HashMap<Object, Object>());
    // assertNotNull(response);
    // assertEquals("id00001", response.getId());
    // }
    //
    // /**
    // * ID省略で正常にドキュメントを作成できる.
    // */
    // @Test
    // public void ID省略で正常にドキュメントを作成できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // IndexResponse response = type.create(new HashMap<Object, Object>());
    // assertNotNull(response);
    // assertFalse(response.getId().equals(""));
    // }
    //
    // /**
    // * mappingConfigsを再作成しドキュメントを正常に作成できる.
    // */
    // @Test
    // public void mappingConfigsを再作成しドキュメントを正常に作成できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // IndexResponse response = type.create("id00001", new HashMap<Object, Object>());
    // EsIndex.loadMappingConfigs();
    // response = type.create("id00002", new HashMap<Object, Object>());
    // assertNotNull(response);
    // assertEquals("id00002", response.getId());
    // }
    //
    // /**
    // * 既に存在するIDでドキュメントを作成した場合にEsModelExceptionをスローする.
    // */
    // @Test
    // public void 既に存在するIDでドキュメントを作成した場合にEsModelExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // Throwable expectedCause = null;
    // try {
    // type.create("id00001", new HashMap<Object, Object>());
    // } catch (EsClientException ex) {
    // expectedCause = ex.getCause();
    // }
    // assertTrue(expectedCause instanceof DocumentAlreadyExistsException);
    // }
    //
    // /**
    // * ドキュメント作成処理で異常が発生した場合にEsClientExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void ドキュメント作成処理で異常が発生した場合にEsClientExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.setEsClient(new EsClientForTest());
    // type.create("id00001", new HashMap<Object, Object>());
    // }
    //
    // /**
    // * インデックスが存在しない場合にドキュメント更新処理でEsIndexMissingExceptionをスローする.
    // */
    // @Test(expected = EsClient.EsIndexMissingException.class)
    // public void インデックスが存在しない場合にドキュメント更新処理でEsIndexMissingExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.update("id00001", new HashMap<Object, Object>());
    // }
    //
    // /**
    // * インデックスが存在しない場合に不正なバージョンを指定したドキュメント更新処理でEsIndexMissingExceptionをスローする.
    // */
    // @Test(expected = EsClient.EsIndexMissingException.class)
    // public void インデックスが存在しない場合に不正なバージョンを指定したドキュメント更新処理でEsIndexMissingExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.update("id00001", new HashMap<Object, Object>(), 2);
    // }
    //
    // /**
    // * 不正なバージョンを指定した場合にドキュメント更新処理でEsVersionConflictExceptionをスローする.
    // */
    // @Test(expected = EsClient.EsVersionConflictException.class)
    // public void 不正なバージョンを指定した場合にドキュメント更新処理でEsVersionConflictExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // type.update("id00001", new HashMap<Object, Object>(), 2);
    // }
    //
    // /**
    // * バージョンを省略した場合にドキュメントを更新できる.
    // */
    // @Test
    // public void バージョンを省略した場合にドキュメントを更新できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // IndexResponse response = type.update("id00001", new HashMap<Object, Object>());
    // assertNotNull(response);
    // assertEquals("id00001", response.getId());
    // }
    //
    // /**
    // * 正しいバージョンを指定した場合にドキュメントを更新できる.
    // */
    // @Test
    // public void 正しいバージョンを指定した場合にドキュメントを更新できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // IndexResponse response = type.update("id00001", new HashMap<Object, Object>(), 1);
    // assertNotNull(response);
    // assertEquals("id00001", response.getId());
    // }
    //
    // /**
    // * ドキュメント更新処理で異常が発生した場合にEsClientExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void ドキュメント更新処理で異常が発生した場合にEsClientExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.setEsClient(new EsClientForTest());
    // type.update("id00001", new HashMap<Object, Object>());
    // }
    //
    // /**
    // * インデックスが存在しない場合に削除処理でEsIndexMissingExceptionをスローする.
    // */
    // @Test(expected = EsClient.EsIndexMissingException.class)
    // public void インデックスが存在しない場合に削除処理でEsIndexMissingExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.delete("id00005");
    // }
    //
    // /**
    // * インデックスが存在しない場合にバージョンを指定した削除処理でEsIndexMissingExceptionをスローする.
    // */
    // @Test(expected = EsClient.EsIndexMissingException.class)
    // public void インデックスが存在しない場合にバージョンを指定した削除処理でEsIndexMissingExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.delete("id00001", 2);
    // }
    //
    // /**
    // * ドキュメントが存在しない場合に削除処理のレスポンスにisNotFoundが返る.
    // */
    // @Test
    // public void ドキュメントが存在しない場合に削除処理のレスポンスにisNotFoundが返る() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // DeleteResponse response = type.delete("id00005");
    // assertNotNull(response);
    // assertTrue(response.isNotFound());
    // }
    //
    // /**
    // * ドキュメントが存在しない場合にバージョンを指定した削除処理でEsVersionConflictExceptionをスローする.
    // */
    // @Test(expected = EsClient.EsVersionConflictException.class)
    // public void ドキュメントが存在しない場合にバージョンを指定した削除処理でEsVersionConflictExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // type.delete("id00001", 2);
    // }
    //
    // /**
    // * バージョンを省略してドキュメントを正常に削除できる.
    // */
    // @Test
    // public void バージョンを省略してドキュメントを正常に削除できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // DeleteResponse response = type.delete("id00001");
    // assertNotNull(response);
    // assertFalse(response.isNotFound());
    // }
    //
    // /**
    // * バージョンを指定してドキュメントを正常に削除できる.
    // */
    // @Test
    // public void バージョンを指定してドキュメントを正常に削除できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // DeleteResponse response = type.delete("id00001", 1);
    // assertNotNull(response);
    // assertFalse(response.isNotFound());
    // }
    //
    // /**
    // * インデックスが存在しない場合にドキュメント取得処理のレスポンスにnullが返る.
    // */
    // @Test
    // public void インデックスが存在しない場合にドキュメント取得処理のレスポンスにnullが返る() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // GetResponse response = type.get("id00001");
    // assertNull(response);
    // }
    //
    // /**
    // * ドキュメントが存在しない場合にドキュメント取得処理のレスポンスにnullが返る.
    // */
    // @Test
    // public void ドキュメントが存在しない場合にドキュメント取得処理のレスポンスにnullが返る() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // GetResponse response = type.get("id00002");
    // assertNull(response);
    // }
    //
    // /**
    // * ドキュメントが存在する場合にドキュメント取得処理のレスポンスが取得できる.
    // */
    // @Test
    // public void ドキュメントが存在する場合にドキュメント取得処理のレスポンスが取得できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // GetResponse response = type.get("id00001");
    // assertNotNull(response);
    // }
    //
    // /**
    // * ドキュメント取得処理で異常が発生した場合にEsClientExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void ドキュメント取得処理で異常が発生した場合にEsClientExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.setEsClient(new EsClientForTest());
    // type.get("id00001");
    // }
    //
    // /**
    // * テスト用に常に例外をスローするEsClient.
    // */
    // private class EsClientForTest extends EsClient {
    // @Override
    // public ActionFuture<GetResponse> asyncGet(String index, String type, String id, String routingId,
    // boolean realtime) {
    // throw new ElasticSearchException("asyncGet Failed.");
    // }
    //
    // @Override
    // public ActionFuture<SearchResponse> asyncSearch(
    // String index,
    // String type,
    // String routingId,
    // SearchSourceBuilder builder) {
    // throw new ElasticSearchException("asyncSearch Failed.");
    // }
    //
    // @Override
    // public ActionFuture<SearchResponse> asyncSearch(
    // String index,
    // String type,
    // String routingId,
    // Map<String, Object> query) {
    // if (query == null) {
    // throw new SearchPhaseExecutionException("", "", null);
    // } else {
    // throw new ElasticSearchException("asyncSearch Failed.");
    // }
    // }
    //
    // @Override
    // public ActionFuture<MultiSearchResponse> asyncMultiSearch(
    // String index,
    // String type,
    // String routingId,
    // List<Map<String, Object>> queryList) {
    // if (queryList == null) {
    // throw new SearchPhaseExecutionException("", "", null);
    // } else {
    // throw new ElasticSearchException("asyncMultiSearch Failed.");
    // }
    // }
    //
    // @Override
    // public ActionFuture<IndexResponse> asyncIndex(String index,
    // String type,
    // String id,
    // String routingId,
    // Map<String, Object> data,
    // OpType opType,
    // long version) {
    // throw new ElasticSearchException("asyncIndex Failed.");
    // }
    //
    // @Override
    // public ActionFuture<DeleteResponse> asyncDelete(String index, String type, String id, String routingId,
    // long version) {
    // throw new ElasticSearchException("asyncDelete Failed.");
    // }
    //
    // @Override
    // public ListenableActionFuture<PutMappingResponse> putMapping(String index,
    // String type, Map<String, Object> mappings) {
    // throw new ElasticSearchException("Put Mapping Failed");
    // }
    // }
    //
    // /**
    // * インデックスが存在しない場合にMapを指定した検索処理のレスポンスに０件のSerchResponseが返る.
    // */
    // @Test
    // public void インデックスが存在しない場合にMapを指定した検索処理のレスポンスに０件のSerchResponseが返る() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // SearchResponse response = type.search(new HashMap<String, Object>());
    // assertEquals(0, response.getHits().hits().length);
    // }
    //
    // /**
    // * インデックスが存在する場合にMapを指定した検索処理のレスポンスが取得できる.
    // */
    // @Test
    // public void インデックスが存在する場合にMapを指定した検索処理のレスポンスが取得できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // SearchResponse response = type.search(new HashMap<String, Object>());
    // assertNotNull(response);
    // }
    //
    // /**
    // * Mapを指定した検索処理で異常が発生した場合にEsClientExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void Mapを指定した検索処理で異常が発生した場合にEsClientExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.setEsClient(new EsClientForTest());
    // type.search(new HashMap<String, Object>());
    // }
    //
    // /**
    // * ドキュメント検索処理でSearchPhaseExecutionExceptionがスローされた場合にEsClientExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void ドキュメント検索処理でSearchPhaseExecutionExceptionがスローされた場合にEsClientExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.setEsClient(new EsClientForTest());
    // type.search((Map<String, Object>) null);
    // }
    //
    // /**
    // * インデックスが存在しない場合にマルチ検索してEsClientExceptionが返る.
    // */
    // @Test(expected = EsClientException.class)
    // public void インデックスが存在しない場合にマルチ検索してEsClientExceptionが返る() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // List<Map<String, Object>> queryList = new ArrayList<Map<String, Object>>();
    // Map<String, Object> query = new HashMap<String, Object>();
    // queryList.add(query);
    // type.multiSearch(queryList);
    // }
    //
    // /**
    // * nullを指定したマルチ検索処理をした場合にEsMultiSearchQueryParseExceptionが返る.
    // */
    // @Test(expected = EsClient.EsMultiSearchQueryParseException.class)
    // public void nullを指定したマルチ検索処理をした場合にEsMultiSearchQueryParseExceptionが返る() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.multiSearch(null);
    // }
    //
    // /**
    // * 空のリクエストを指定したマルチ検索処理をした場合にEsMultiSearchQueryParseExceptionが返る.
    // */
    // @Test(expected = EsClient.EsMultiSearchQueryParseException.class)
    // public void 空のリクエストを指定したマルチ検索処理をした場合にEsMultiSearchQueryParseExceptionが返る() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.multiSearch(new ArrayList<Map<String, Object>>());
    // }
    //
    // /**
    // * インデックスが存在する場合にMapを指定したマルチ検索処理のレスポンスが取得できる.
    // */
    // @Test
    // public void インデックスが存在する場合にMapを指定したマルチ検索処理のレスポンスが取得できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // type.create("id00002", new HashMap<Object, Object>());
    // type.create("id00003", new HashMap<Object, Object>());
    // List<Map<String, Object>> queryList = new ArrayList<Map<String, Object>>();
    // Map<String, Object> query1 = new HashMap<String, Object>();
    // Map<String, Object> query2 = new HashMap<String, Object>();
    // Map<String, Object> id3GetQuery = new HashMap<String, Object>();
    // Map<String, Object> termQuery = new HashMap<String, Object>();
    // termQuery.put("_id", "id00003");
    // id3GetQuery.put("term", termQuery);
    // query2.put("query", id3GetQuery);
    // queryList.add(query1);
    // queryList.add(query2);
    // MultiSearchResponse response = type.multiSearch(queryList);
    // assertNotNull(response);
    // assertEquals(2, response.getResponses().length);
    // assertEquals(3, response.getResponses()[0].getResponse().hits().getTotalHits());
    // assertEquals(1, response.getResponses()[1].getResponse().hits().getTotalHits());
    // }
    //
    // /**
    // * Mapを指定したマルチ検索処理で異常が発生した場合にEsClientExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void Mapを指定したマルチ検索処理で異常が発生した場合にEsClientExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.setEsClient(new EsClientForTest());
    // type.multiSearch(new ArrayList<Map<String, Object>>());
    // }
    //
    // /**
    // * ドキュメントマルチ検索処理でSearchPhaseExecutionExceptionがスローされた場合にEsClientExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void ドキュメントマルチ検索処理でSearchPhaseExecutionExceptionがスローされた場合にEsClientExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.setEsClient(new EsClientForTest());
    // type.multiSearch((ArrayList<Map<String, Object>>) null);
    // }
    //
    //
    // /**
    // * 不正なバージョンを指定して削除処理をした場合にEsVersionConflictExceptionをスローする.
    // */
    // @Test(expected = EsClient.EsVersionConflictException.class)
    // public void 不正なバージョンを指定して削除処理をした場合にEsVersionConflictExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.create("id00001", new HashMap<Object, Object>());
    // type.delete("id00001", 2);
    // }
    //
    // /**
    // * ドキュメント削除処理で異常が発生した場合ににEsClientExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void ドキュメント削除処理で異常が発生した場合ににEsClientExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // type.setEsClient(new EsClientForTest());
    // type.delete("id00001", 2);
    // }
    //
    // /**
    // * bilk処理で現状nullが返される.
    // */
    // @Test
    // public void bilk処理で現状nullが返される() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNull(type.bulkGet(null));
    // }
    //
    // /**
    // * インデックス単位の検索でデータを取得できる.
    // */
    // @Test
    // public void インデックス単位の検索でデータを取得できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    // IndexResponse iResponse = type.create("id00001", new HashMap<Object, Object>());
    // assertNotNull(iResponse);
    // assertEquals("id00001", iResponse.getId());
    // Map<String, Object> query = new HashMap<String, Object>();
    // Map<String, Object> term = new HashMap<String, Object>();
    // Map<String, Object> value = new HashMap<String, Object>();
    // value.put("s.Name.untouched", "xxx");
    // term.put("term", value);
    // query.put("filter", term);
    // SearchResponse sResponse = index.search("index_for_test", query);
    // assertNotNull(sResponse);
    // }
    //
    // /**
    // * 存在しないインデックスを指定してデータを取得した場合NULLが返却されること.
    // */
    // @Test
    // public void 存在しないインデックスを指定してデータを取得した場合NULLが返却されること() {
    // EsIndex index = EsIndex.index("index_for_test_dummy", EsIndex.CATEGORY_AD);
    // Map<String, Object> query = new HashMap<String, Object>();
    // Map<String, Object> term = new HashMap<String, Object>();
    // Map<String, Object> value = new HashMap<String, Object>();
    // value.put("s.Name.untouched", "xxx");
    // term.put("term", value);
    // query.put("filter", term);
    // SearchResponse sResponse = index.search("dummyRoutingId", query);
    // assertNull(sResponse);
    // }
    //
    // /**
    // * 不正なクエリを指定してデータを取得した場合EsClientExceptionをスローすること.
    // */
    // @Test(expected = EsClient.EsClientException.class)
    // public void 不正なクエリを指定してデータを取得した場合EsClientExceptionをスローすること() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // Map<String, Object> query = new HashMap<String, Object>();
    // query.put("test", "test");
    // index.search("dummyRoutingId", query);
    // }
    //
    // /**
    // * バルク登録テスト用データクラス.
    // */
    // class BulkRequest implements EsBulkRequest {
    //
    // String type = "type";
    //
    // public BulkRequest() {
    // }
    //
    // public String getType() {
    // return this.type;
    // }
    //
    // public void setType(String type) {
    // this.type = type;
    // }
    //
    // public String getId() {
    // return "id";
    // }
    //
    // public Map<String, Object> getSource() {
    // return new HashMap<String, Object>();
    // }
    // }
    //
    // /**
    // * インデックス単位の一括データ登録でバルク結果が取得できる.
    // */
    // @Test
    // public void インデックス単位の一括データ登録でバルク結果が取得できる() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // List<EsBulkRequest> datas = new ArrayList<EsBulkRequest>();
    // datas.add(new BulkRequest());
    // BulkResponse response = index.bulkCreate("routingID_for_test", datas);
    // assertNotNull(response);
    // }
    //
    // /**
    // * タイプを指定せずにインデックス単位の一括データ登録を行いEsClientExceptionがスローされること.
    // */
    // @Test(expected = EsClient.EsClientException.class)
    // public void タイプを指定せずにインデックス単位の一括データ登録を行いEsClientExceptionがスローされること() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // List<EsBulkRequest> datas = new ArrayList<EsBulkRequest>();
    // BulkRequest req = new BulkRequest();
    // req.setType(null);
    // datas.add(req);
    // BulkResponse response = index.bulkCreate("routingID_for_test", datas);
    // assertNotNull(response);
    // }
    //
    // /**
    // * Mapping情報を更新し取得できること.
    // */
    // @Test
    // public void Mapping情報を更新し取得できること() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    //
    // Map<String, Object> putMap = new HashMap<String, Object>();
    // Map<String, Object> expected = new HashMap<String, Object>();
    // Map<String, Object> properties = new HashMap<String, Object>();
    // Map<String, Object> item = new HashMap<String, Object>();
    // item.put("type", "string");
    // properties.put("item", item);
    // expected.put("properties", properties);
    // putMap.put("TypeForTest", expected);
    //
    // type.putMapping(putMap);
    // MappingMetaData mmd = type.getMapping();
    //
    // Map<String, Object> actual = null;
    // try {
    // actual = mmd.getSourceAsMap();
    // } catch (IOException e) {
    // fail(e.getMessage());
    // }
    // assertEquals(expected, actual);
    // }
    //
    // /**
    // * インデックスが存在しない場合にMapping更新処理でEsIndexMissingExceptionをスローする.
    // */
    // @Test(expected = EsClient.EsIndexMissingException.class)
    // public void インデックスが存在しない場合にMapping情報更新処理でEsIndexMissingExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    // assertNotNull(type);
    //
    // Map<String, Object> putMap = new HashMap<String, Object>();
    // Map<String, Object> expected = new HashMap<String, Object>();
    // Map<String, Object> properties = new HashMap<String, Object>();
    // Map<String, Object> item = new HashMap<String, Object>();
    // item.put("type", "string");
    // properties.put("item", item);
    // expected.put("properties", properties);
    // putMap.put("TypeForTest", expected);
    //
    // type.putMapping(putMap);
    // }
    //
    // /**
    // * Mapping更新処理で異常が発生した場合にEsClientExceptionをスローする.
    // */
    // @Test(expected = EsClientException.class)
    // public void Mapping更新処理で異常が発生した場合にEsClientExceptionをスローする() {
    // EsIndex index = esClient.index("index_for_test", EsIndex.CATEGORY_AD, 0, 0);
    // index.create();
    // EsType type = esClient.type("index_for_test", "TypeForTest", "routingId", 0, 0);
    //
    // Map<String, Object> putMap = new HashMap<String, Object>();
    // Map<String, Object> expected = new HashMap<String, Object>();
    // Map<String, Object> properties = new HashMap<String, Object>();
    // Map<String, Object> item = new HashMap<String, Object>();
    // item.put("type", "string");
    // properties.put("item", item);
    // expected.put("properties", properties);
    // putMap.put("TypeForTest", expected);
    //
    // type.setEsClient(new EsClientForTest());
    // type.putMapping(putMap);
    // }
    //
    // /**
    // * インデックス単位の非同期バルク登録で正常終了すること.
    // */
    // @Test
    // public void インデックス単位の非同期バルク登録で正常終了すること() {
    // String indexName = "index_for_test";
    // EsClient client = null;
    // try {
    // // Indexの作成
    // EsIndex index = EsIndex.index(indexName, EsIndex.CATEGORY_USR);
    // index.create();
    //
    // // バルク登録
    // client = new EsClient("testingCluster", "localhost:9399");
    // List<EsBulkRequest> datas = new ArrayList<EsBulkRequest>();
    // EsBulkEntity req = new EsBulkEntity("testid", "testType");
    // datas.add(req);
    // Map<String, List<EsBulkRequest>> bulkMap = new HashMap<String, List<EsBulkRequest>>();
    // bulkMap.put("routingId", datas);
    // ListenableActionFuture<BulkResponse> response = client.asyncBulkCreate(indexName, bulkMap);
    // assertNotNull(response);
    //
    // // レスポンスチェック（正常終了すること）
    // BulkResponse actionResponse = response.actionGet();
    // assertFalse(actionResponse.hasFailures());
    //
    // // リフレッシュ（正常終了すること）
    // RefreshResponse refershResponse = client.refresh(indexName);
    // assertEquals(0, refershResponse.failedShards());
    // } catch (Exception e) {
    // fail(e.getMessage());
    // e.printStackTrace();
    // } finally {
    // client.deleteIndex(indexName).actionGet();
    // client.closeConnection();
    // }
    // }
    //
    // /**
    // * EsBulkEntity.
    // */
    // class EsBulkEntity implements EsBulkRequest {
    // private String id;
    // private String type;
    // public EsBulkEntity(String id, String type) {
    // this.type = type;
    // }
    // @Override
    // public String getType() {
    // return this.type;
    // }
    //
    // @Override
    // public String getId() {
    // return this.id;
    // }
    //
    // @Override
    // public Map<String, Object> getSource() {
    // Map<String, Object> source = new HashMap<String, Object>();
    // source.put("id", id);
    // return source;
    // }
    //
    // }

    /**
     * ドキュメント登録チェックでドキュメントがすでに存在している場合に正常終了すること.
     * @throws ParseException ParseException
     */
    @Test
    public void ドキュメント登録チェックでドキュメントがすでに存在している場合に正常終了すること() throws ParseException {
        String id = "id00001";
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsTypeImpl type = (EsTypeImpl) esClient.type(index.getName(), "TypeForTest", "TestRoutingId", 5, 500);
        assertNotNull(type);
        JSONObject data = (JSONObject) new JSONParser()
        .parse("{\"u\":1406596187938,\"t\":\"K0QK5DXWT5qKIPDU2eTdhA\",\"b\":\"IKv5hMRPRDGc68BnIcVx6g\","
                + "\"s\":{\"P003\":\"secondDynamicPropertyValue\",\"P002\":\"true\",\"P001\":\"false\","
                + "\"P011\":\"null\",\"P012\":\"123.0\",\"P007\":\"123\",\"P006\":\"false\",\"P005\":null,"
                + "\"P004\":\"dynamicPropertyValue\",\"P009\":\"123.123\",\"P008\":\"true\",\"__id\":\"userdata001:\","
                + "\"P010\":\"123.123\"},\"c\":\"Q1fp4zrWTm-gSSs7zVCJQg\",\"p\":1406596187938,"
                + "\"n\":\"vWy9OQj2ScykYize2d7Z5A\",\"l\":[],\"h\":{}}");
        type.create(id, data);
        IndexResponse response = type.checkDocumentCreated(id, data, null);
        assertNotNull(response);
        assertEquals(id, response.getId());
    }

    /**
     * ドキュメント登録チェックでドキュメントが存在しない場合に例外が発生すること.
     * @throws ParseException ParseException
     */
    @Test(expected = EsClientException.class)
    public void ドキュメント登録チェックでドキュメントが存在しない場合に例外が発生すること() throws ParseException {
        String id = "id00001";
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsTypeImpl type = (EsTypeImpl) esClient.type(index.getName(), "TypeForTest", "TestRoutingId", 5, 500);
        assertNotNull(type);
        JSONObject data = (JSONObject) new JSONParser()
        .parse("{\"u\":1406596187938,\"t\":\"K0QK5DXWT5qKIPDU2eTdhA\",\"b\":\"IKv5hMRPRDGc68BnIcVx6g\","
                + "\"s\":{\"P003\":\"secondDynamicPropertyValue\",\"P002\":\"true\",\"P001\":\"false\","
                + "\"P011\":\"null\",\"P012\":\"123.0\",\"P007\":\"123\",\"P006\":\"false\",\"P005\":null,"
                + "\"P004\":\"dynamicPropertyValue\",\"P009\":\"123.123\",\"P008\":\"true\",\"__id\":\"userdata001:\","
                + "\"P010\":\"123.123\"},\"c\":\"Q1fp4zrWTm-gSSs7zVCJQg\",\"p\":1406596187938,"
                + "\"n\":\"vWy9OQj2ScykYize2d7Z5A\",\"l\":[],\"h\":{}}");
        type.checkDocumentCreated(id, data, null);
    }

    /**
     * ドキュメント登録チェックでElasticsearchに登録されたデータの更新日時が異なる場合に例外が発生すること.
     * @throws ParseException ParseException
     */
    @Test(expected = EsClientException.class)
    public void ドキュメント登録チェックでElasticsearchに登録されたデータの更新日時が異なる場合に例外が発生すること() throws ParseException {
        String id = "id00001";
        EsIndex index = esClient.idxAdmin("index_for_test");
        index.create();
        EsTypeImpl type = (EsTypeImpl) esClient.type(index.getName(), "TypeForTest", "TestRoutingId", 5, 500);
        assertNotNull(type);
        JSONObject data = (JSONObject) new JSONParser()
        .parse("{\"u\":1406596187938,\"t\":\"K0QK5DXWT5qKIPDU2eTdhA\",\"b\":\"IKv5hMRPRDGc68BnIcVx6g\","
                + "\"s\":{\"P003\":\"secondDynamicPropertyValue\",\"P002\":\"true\",\"P001\":\"false\","
                + "\"P011\":\"null\",\"P012\":\"123.0\",\"P007\":\"123\",\"P006\":\"false\",\"P005\":null,"
                + "\"P004\":\"dynamicPropertyValue\",\"P009\":\"123.123\",\"P008\":\"true\",\"__id\":\"userdata001:\","
                + "\"P010\":\"123.123\"},\"c\":\"Q1fp4zrWTm-gSSs7zVCJQg\",\"p\":1406596187938,"
                + "\"n\":\"vWy9OQj2ScykYize2d7Z5A\",\"l\":[],\"h\":{}}");
        type.create(id, data);

        data = (JSONObject) new JSONParser()
        .parse("{\"u\":123456789,\"t\":\"K0QK5DXWT5qKIPDU2eTdhA\",\"b\":\"IKv5hMRPRDGc68BnIcVx6g\","
                + "\"s\":{\"P003\":\"secondDynamicPropertyValue\",\"P002\":\"true\",\"P001\":\"false\","
                + "\"P011\":\"null\",\"P012\":\"123.0\",\"P007\":\"123\",\"P006\":\"false\",\"P005\":null,"
                + "\"P004\":\"dynamicPropertyValue\",\"P009\":\"123.123\",\"P008\":\"true\",\"__id\":\"userdata001:\","
                + "\"P010\":\"123.123\"},\"c\":\"Q1fp4zrWTm-gSSs7zVCJQg\",\"p\":1406596187938,"
                + "\"n\":\"vWy9OQj2ScykYize2d7Z5A\",\"l\":[],\"h\":{}}");

        type.checkDocumentCreated(id, data, null);
    }
}
