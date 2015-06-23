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

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.admin.cluster.state.ClusterStateRequestBuilder;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
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
 * ESの設定更新のテストクラス.
 */
public class EsUpdateSettingsTest {
    private static final String INDEX_FOR_TEST = "index_for_test";
    private static EsTestNode node;
    private EsIndex index;

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
        index = esClient.idxAdmin(INDEX_FOR_TEST);
        index.create();
    }

    /**
     * 各テスト実行後のクリーンアップ処理.
     * @throws Exception 異常が発生した場合の例外
     */
    @After
    public void tearDown() throws Exception {
        try {
            index.delete();
        } catch (Exception ex) {
            System.out.println("");
        }
    }

    /**
     * Indexの設定が更新できること.
     */
    @Test
    public void Indexの設定が更新できること() {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("index.number_of_replicas", "1");
        TransportClient client = null;
        try {
            client = createTransportClient();

            assertEquals("0", getNumberOfReplicas(client, "index.number_of_replicas"));

            index.updateSettings(index.getName(), settings);
            assertEquals("1", getNumberOfReplicas(client, "index.number_of_replicas"));
        } finally {
            client.close();
        }
    }

    /**
     * 存在しないkeyを指定した場合EsClientExceptionがスローされること.
     */
    @Test(expected = EsClientException.class)
    public void 存在しないkeyを指定した場合EsClientExceptionがスローされること() {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("invalid_key", "0");
        TransportClient client = null;
        try {
            client = createTransportClient();

            index.updateSettings(index.getName(), settings);

        } finally {
            client.close();
        }
    }

    /**
     * 無効な値を指定した場合にEsClientExceptionがスローされること.
     */
    @Test(expected = EsClientException.class)
    public void 無効な値を指定した場合にEsClientExceptionがスローされること() {
        Map<String, String> settings = new HashMap<String, String>();
        settings.put("index.number_of_replicas", "invalid_value");
        TransportClient client = null;
        try {
            client = createTransportClient();

            index.updateSettings(index.getName(), settings);

        } finally {
            client.close();
        }
    }

    private TransportClient createTransportClient() {
        Settings sts = ImmutableSettings.settingsBuilder()
                .put("cluster.name", "testingCluster").build();
        TransportClient client = new TransportClient(sts);
        client.addTransportAddress(new InetSocketTransportAddress("localhost", 9399));
        return client;
    }

    private String getNumberOfReplicas(TransportClient client, String key) {
        ClusterStateRequestBuilder request = client.admin().cluster().prepareState();
        ClusterStateResponse response = request.setIndices(index.getName()).execute().actionGet();
        Settings retrievedSettings = response.getState().getMetaData().index(index.getName()).getSettings();
        String numberOfReplicas = retrievedSettings.get(key);
        return numberOfReplicas;
    }
}
