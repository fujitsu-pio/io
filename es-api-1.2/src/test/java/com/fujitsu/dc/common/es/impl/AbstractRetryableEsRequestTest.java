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
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;

import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.NoShardAvailableActionException;
import org.elasticsearch.action.support.broadcast.BroadcastShardOperationFailedException;
import org.elasticsearch.client.transport.NoNodeAvailableException;
import org.elasticsearch.cluster.block.ClusterBlockException;
import org.elasticsearch.common.settings.SettingsException;
import org.elasticsearch.common.util.concurrent.UncategorizedExecutionException;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.engine.FlushNotAllowedEngineException;
import org.elasticsearch.indices.IndexMissingException;
import org.elasticsearch.transport.NodeDisconnectedException;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

import com.fujitsu.dc.common.es.EsClient;
import com.fujitsu.dc.common.es.EsIndex;
import com.fujitsu.dc.common.es.impl.EsTranslogHandler.FlushTranslogRetryableRequest;
import com.fujitsu.dc.common.es.response.EsClientException;
import com.fujitsu.dc.common.es.test.util.EsTestNode;

/**
 * AbstractRetryableEsRequestクラスのリトライテスト.
 */
@PrepareForTest(EsClientException.class)
public class AbstractRetryableEsRequestTest {

    static final int RETRY_COUNT = 5;
    static final long RETRY_INTERVAL = 500;

    static final String SUCCESS_RESPONSE = "REQUEST_SUCCESS";
    static final String ON_ERROR_RESPONSE = "REQUEST_SUCCESS_ON_PARTICULOR_ERROR";

    private static final String TESTING_HOSTS = "localhost:9399";
    private static final String TESTING_CLUSTER = "testingCluster";
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

    private EsClient esClientforTest;

    /**
     * 各テスト実行前の初期化処理.
     * @throws Exception 異常が発生した場合の例外
     */
    @Before
    public void setUp() throws Exception {
        esClientforTest = new EsClient(TESTING_CLUSTER, TESTING_HOSTS);
        index = esClientforTest.idxAdmin(INDEX_FOR_TEST);
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
     * テスト用の例外.
     */
    class EsExceptionForTest extends ElasticsearchException {
        private static final long serialVersionUID = 1L;

        public EsExceptionForTest(String msg) {
            super(msg);
        }

    }

    /**
     * テスト用のリクエストクラス.
     */
    class TestRequest extends AbstractRetryableEsRequest<String> {
        @Override
        public boolean isParticularError(ElasticsearchException e) {
            return (e instanceof EsExceptionForTest || e instanceof SettingsException);
        }

        @Override
        public String onParticularError(ElasticsearchException e) {
            if (e instanceof EsExceptionForTest) {
                return ON_ERROR_RESPONSE;
            }
            if (e instanceof SettingsException) {
                throw new ContinueRetry();
            }
            throw e;
        }

        public TestRequest() {
            super(RETRY_COUNT, RETRY_INTERVAL, "TestRequest");
        }

        @Override
        String doProcess() {
            return SUCCESS_RESPONSE;
        }

        @Override
        EsTranslogHandler getEsTranslogHandler() {
            InternalEsClient esClient = new InternalEsClient(TESTING_CLUSTER, TESTING_HOSTS);
            return new EsIndexImpl(index.getName(), EsIndex.CATEGORY_AD, retryCount, 1, esClient);
        }
    }

    /**
     * 初回リクエストが成功した場合、適切な復帰値が返ること.
     */
    @Test
    public void 初回リクエストが成功した場合_適切な復帰値が返ること() {
        TestRequest requestMock = Mockito.spy(new TestRequest());

        String result = requestMock.doRequest();
        assertEquals(SUCCESS_RESPONSE, result);
        Mockito.verify(requestMock, Mockito.times(1)).doProcess();
        Mockito.verify(requestMock, Mockito.times(0)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * 初回リクエストでリトライ対象外の例外が発生した場合、リトライせずに初回例外を投げること.
     */
    @Test(expected = EsClientException.class)
    public void 初回リクエストでリトライ対象外の例外が発生した場合_リトライせずに初回例外を投げること() {
        TestRequest requestMock = Mockito.spy(new TestRequest());

        Mockito.doThrow(new IndexMissingException(new Index("abc"))) // なぜかモック例外だとうまく動かなかった.
                .when(requestMock)
                .doProcess();

        try {
            requestMock.doRequest();
            fail("Should not return");
        } finally {
            Mockito.verify(requestMock, Mockito.times(1)).doProcess();
            Mockito.verify(requestMock, Mockito.times(0)).onParticularError(Mockito.any(ElasticsearchException.class));
        }
    }

    /**
     * 初回リクエストでNodeDisconnectedException、リトライ1回目で成功した場合、適切な復帰値が返ること.
     */
    @Test
    public void 初回リクエストでNodeDisconnectedException_リトライ1回目で成功した場合_適切な復帰値が返ること() {

        TestRequest requestMock = Mockito.spy(new TestRequest());

        NodeDisconnectedException toBeThrown = Mockito.mock(NodeDisconnectedException.class);
        Mockito.doThrow(toBeThrown)
                .doReturn(SUCCESS_RESPONSE)
                .when(requestMock)
                .doProcess();

        String result = requestMock.doRequest();
        assertEquals(SUCCESS_RESPONSE, result);
        Mockito.verify(requestMock, Mockito.times(2)).doProcess();
        Mockito.verify(requestMock, Mockito.times(0)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * 初回リクエストでNoNodeAvailableException、リトライ1回目で成功した場合、適切な復帰値が返ること.
     */
    @Test
    public void 初回リクエストでNoNodeAvailableException_リトライ1回目で成功した場合_適切な復帰値が返ること() {

        TestRequest requestMock = Mockito.spy(new TestRequest());

        NoNodeAvailableException toBeThrown = Mockito.mock(NoNodeAvailableException.class);
        Mockito.doThrow(toBeThrown)
                .doReturn(SUCCESS_RESPONSE)
                .when(requestMock)
                .doProcess();

        String result = requestMock.doRequest();
        assertEquals(SUCCESS_RESPONSE, result);
        Mockito.verify(requestMock, Mockito.times(2)).doProcess();
        Mockito.verify(requestMock, Mockito.times(0)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * 初回リクエストでNoShardAvailableActionException、リトライ1回目で成功した場合、適切な復帰値が返ること.
     */
    @Test
    public void 初回リクエストでNoShardAvailableActionException_リトライ1回目で成功した場合_適切な復帰値が返ること() {

        TestRequest requestMock = Mockito.spy(new TestRequest());

        NoShardAvailableActionException toBeThrown = Mockito.mock(NoShardAvailableActionException.class);
        Mockito.doThrow(toBeThrown)
                .doReturn(SUCCESS_RESPONSE)
                .when(requestMock)
                .doProcess();

        String result = requestMock.doRequest();
        assertEquals(SUCCESS_RESPONSE, result);
        Mockito.verify(requestMock, Mockito.times(2)).doProcess();
        Mockito.verify(requestMock, Mockito.times(0)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * 初回リクエストでClusterBlockException、リトライ1回目で成功した場合、適切な復帰値が返ること.
     */
    @Test
    public void 初回リクエストでClusterBlockException_リトライ1回目で成功した場合_適切な復帰値が返ること() {

        TestRequest requestMock = Mockito.spy(new TestRequest());

        ClusterBlockException toBeThrown = Mockito.mock(ClusterBlockException.class);
        Mockito.doThrow(toBeThrown)
                .doReturn(SUCCESS_RESPONSE)
                .when(requestMock)
                .doProcess();

        String result = requestMock.doRequest();
        assertEquals(SUCCESS_RESPONSE, result);
        Mockito.verify(requestMock, Mockito.times(2)).doProcess();
        Mockito.verify(requestMock, Mockito.times(0)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * リトライ１回目でNodeDisconnectedException, リトライ2回目で成功した場合、適切な復帰値が返ること.
     */
    @Test
    public void リトライ１回目でNodeDisconnectedException_リトライ2回目で成功した場合_適切な復帰値が返ること() {

        TestRequest requestMock = Mockito.spy(new TestRequest());

        NodeDisconnectedException toBeThrown = Mockito.mock(NodeDisconnectedException.class);
        Mockito.doThrow(toBeThrown) // 初回
                .doThrow(toBeThrown) // リトライ1回目
                .doReturn(SUCCESS_RESPONSE) // リトライ2回目で正常復帰
                .when(requestMock)
                .doProcess();

        String result = requestMock.doRequest();
        assertEquals(SUCCESS_RESPONSE, result);
        // doProcessが3回呼び出されるはず
        Mockito.verify(requestMock, Mockito.times(3)).doProcess();
        Mockito.verify(requestMock, Mockito.times(0)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * NoNodeAvailableExceptionが続き, リトライ3回目で成功した場合、適切な復帰値が返ること.
     */
    @Test
    public void NoNodeAvailableExceptionが続き_リトライ3回目で成功した場合_適切な復帰値が返ること() {

        TestRequest requestMock = Mockito.spy(new TestRequest());

        NoNodeAvailableException toBeThrown = Mockito.mock(NoNodeAvailableException.class);
        Mockito.doThrow(toBeThrown) // 初回
                .doThrow(toBeThrown) // リトライ1回目
                .doThrow(toBeThrown) // リトライ2回目
                .doReturn(SUCCESS_RESPONSE) // リトライ3回目で正常復帰
                .when(requestMock)
                .doProcess();

        String result = requestMock.doRequest();
        assertEquals(SUCCESS_RESPONSE, result);
        // doProcessが4回呼び出されるはず
        Mockito.verify(requestMock, Mockito.times(4)).doProcess();
        Mockito.verify(requestMock, Mockito.times(0)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * NoNodeAvailableExceptionが続き, リトライ3回目で成功した場合、適切な復帰値が返ること.
     */
    @Test
    public void NoShardAvailableActionExceptionが続き_リトライ4回目で成功した場合_適切な復帰値が返ること() {

        TestRequest requestMock = Mockito.spy(new TestRequest());

        NoShardAvailableActionException toBeThrown = Mockito.mock(NoShardAvailableActionException.class);
        Mockito.doThrow(toBeThrown) // 初回
                .doThrow(toBeThrown) // リトライ1回目
                .doThrow(toBeThrown) // リトライ2回目
                .doThrow(toBeThrown) // リトライ3回目
                .doReturn(SUCCESS_RESPONSE) // リトライ4回目で正常復帰
                .when(requestMock)
                .doProcess();

        String result = requestMock.doRequest();
        assertEquals(SUCCESS_RESPONSE, result);
        // doProcessが5回呼び出されるはず
        Mockito.verify(requestMock, Mockito.times(5)).doProcess();
        Mockito.verify(requestMock, Mockito.times(0)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * ClusterBlockExceptionが続き_リトライ5回目で成功した場合_適切な復帰値が返ること.
     */
    @Test
    public void ClusterBlockExceptionが続き_リトライ5回目で成功した場合_適切な復帰値が返ること() {

        TestRequest requestMock = Mockito.spy(new TestRequest());

        ClusterBlockException toBeThrown = Mockito.mock(ClusterBlockException.class);
        Mockito.doThrow(toBeThrown) // 初回
                .doThrow(toBeThrown) // リトライ1回目
                .doThrow(toBeThrown) // リトライ2回目
                .doThrow(toBeThrown) // リトライ3回目
                .doThrow(toBeThrown) // リトライ4回目
                .doReturn(SUCCESS_RESPONSE) // リトライ5回目で正常復帰
                .when(requestMock)
                .doProcess();

        String result = requestMock.doRequest();
        assertEquals(SUCCESS_RESPONSE, result);
        // doProcessが6回呼び出されるはず
        Mockito.verify(requestMock, Mockito.times(6)).doProcess();
        Mockito.verify(requestMock, Mockito.times(0)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * リトライ対象例外が続き_リトライ5回目でもNGな場合_EsNoResponseExceptionが投げられること.
     */
    @Test(expected = EsClientException.EsNoResponseException.class)
    public void リトライ対象例外が続き_リトライ5回目でもNGな場合_EsNoResponseExceptionが投げられること() {

        TestRequest requestMock = Mockito.spy(new TestRequest());

        ClusterBlockException toBeThrown = Mockito.mock(ClusterBlockException.class);
        Mockito.doThrow(toBeThrown) // 初回
                .doThrow(toBeThrown) // リトライ1回目
                .doThrow(toBeThrown) // リトライ2回目
                .doThrow(toBeThrown) // リトライ3回目
                .doThrow(toBeThrown) // リトライ4回目
                .doThrow(toBeThrown) // リトライ5回目
                .when(requestMock)
                .doProcess();

        try {
            requestMock.doRequest();
            fail("Should not return");
        } finally {
            // doProcessが6回呼び出されるはず
            Mockito.verify(requestMock, Mockito.times(6)).doProcess();
            Mockito.verify(requestMock, Mockito.times(0)).onParticularError(Mockito.any(ElasticsearchException.class));
        }
    }

    /**
     * リトライ対象例外が続き_リトライ5回目でリトライ対象外の例外が発生した場合、EsClientExceptionが投げられること.
     */
    @Test(expected = EsClientException.class)
    public void リトライ対象例外が続き_リトライ5回目でリトライ対象外の例外が発生した場合_EsClientExceptionが投げられること() {

        TestRequest requestMock = Mockito.spy(new TestRequest());

        ClusterBlockException toBeThrown = Mockito.mock(ClusterBlockException.class);
        IndexMissingException toBeThrown2 = Mockito.mock(IndexMissingException.class);
        Mockito.doThrow(toBeThrown) // 初回
                .doThrow(toBeThrown) // リトライ1回目
                .doThrow(toBeThrown) // リトライ2回目
                .doThrow(toBeThrown) // リトライ3回目
                .doThrow(toBeThrown) // リトライ4回目
                .doThrow(toBeThrown2) // リトライ5回目
                .when(requestMock)
                .doProcess();

        try {
            requestMock.doRequest();
            fail("Should not return");
        } finally {
            // doProcessが6回呼び出されるはず
            Mockito.verify(requestMock, Mockito.times(6)).doProcess();
            Mockito.verify(requestMock, Mockito.times(0)).onParticularError(Mockito.any(ElasticsearchException.class));
        }
    }

    /**
     * 初回リクエスト時に特定例外が発生した場合、特定例外用処理が呼び出されてレスポンスが返ること.
     */
    @Test
    public void 初回リクエスト時に特定例外が発生した場合_特定例外用処理が呼び出されてレスポンスが返ること() {

        TestRequest requestMock = Mockito.spy(new TestRequest());

        EsExceptionForTest toBeThrown = Mockito.mock(EsExceptionForTest.class);
        Mockito.doThrow(toBeThrown) // 初回
                .when(requestMock)
                .doProcess();

        String result = requestMock.doRequest();
        assertEquals(ON_ERROR_RESPONSE, result);
        Mockito.verify(requestMock, Mockito.times(1)).doProcess();
        Mockito.verify(requestMock, Mockito.times(1)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * リトライ中に特定例外が発生した場合、特定例外用処理が呼び出されてレスポンスが返ること.
     */
    @Test
    public void リトライ中にに特定例外が発生した場合_特定例外用処理が呼び出されてレスポンスが返ること() {

        TestRequest requestMock = Mockito.spy(new TestRequest());

        NodeDisconnectedException toBeThrown = Mockito.mock(NodeDisconnectedException.class);
        EsExceptionForTest toBeThrown2 = Mockito.mock(EsExceptionForTest.class);
        Mockito.doThrow(toBeThrown) // 初回
                .doThrow(toBeThrown) // リトライ1回目
                .doThrow(toBeThrown) // リトライ2回目
                .doThrow(toBeThrown2) // リトライ3回目 特定例外
                .when(requestMock)
                .doProcess();

        String result = requestMock.doRequest();
        assertEquals(ON_ERROR_RESPONSE, result);
        Mockito.verify(requestMock, Mockito.times(4)).doProcess();
        Mockito.verify(requestMock, Mockito.times(1)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * 初回リクエストの特定例外処理からContinueRetryが投げられた後、リトライ処理に移行すること.
     */
    @Test
    public void 初回リクエストの特定例外処理からContinueRetryが投げられた後_リトライ処理に移行すること() {
        TestRequest requestMock = Mockito.spy(new TestRequest());

        NodeDisconnectedException toBeThrown = Mockito.mock(NodeDisconnectedException.class);

        Mockito.doThrow(new SettingsException("foo")) // 初回リクエスト
                .doThrow(toBeThrown) // リトライ1回目
                .doReturn(SUCCESS_RESPONSE)
                .when(requestMock)
                .doProcess();

        String result = requestMock.doRequest();
        assertEquals(SUCCESS_RESPONSE, result);
        Mockito.verify(requestMock, Mockito.times(3)).doProcess();
        Mockito.verify(requestMock, Mockito.times(1)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * リトライ処理中の特定例外処理からContinueRetryが投げられた後、リトライ処理に移行すること.
     */
    @Test
    public void リトライ処理中の特定例外処理からContinueRetryが投げられた後_リトライ処理に移行すること() {
        TestRequest requestMock = Mockito.spy(new TestRequest());

        NodeDisconnectedException toBeThrown = Mockito.mock(NodeDisconnectedException.class);

        Mockito.doThrow(toBeThrown) // 初回リクエスト
                .doThrow(toBeThrown) // リトライ1回目
                .doThrow(new SettingsException("foo")) // リトライ2回目. この時は、 #onParticularError()でリトライ継続のために
                                                       // ContinueRetryが投げられる.
                .doThrow(toBeThrown) // リトライ3回目
                .doReturn(SUCCESS_RESPONSE)
                .when(requestMock)
                .doProcess();

        String result = requestMock.doRequest();
        assertEquals(SUCCESS_RESPONSE, result);
        // 初回 + リトライ3回 + 処理成功で、5回呼ばれるはず.
        Mockito.verify(requestMock, Mockito.times(5)).doProcess();
        Mockito.verify(requestMock, Mockito.times(1)).onParticularError(Mockito.any(ElasticsearchException.class));
    }

    /**
     * translog読み込み時にUncategorizedExecutionExceptionが発生した場合にflushが実行されること.
     */
    @Test
    public void translog読み込み時にUncategorizedExecutionExceptionが発生した場合にflushが実行されること() {
        TestRequest requestMock = Mockito.spy(new TestRequest());
        UncategorizedExecutionException toBeThrown = Mockito.mock(UncategorizedExecutionException.class);
        Mockito.doThrow(toBeThrown) // 初回リクエスト
                .doThrow(toBeThrown) // リトライ1回目
                .doReturn(SUCCESS_RESPONSE)
                .when(requestMock)
                .doProcess();
        requestMock.doRequest();
        Mockito.verify(requestMock, Mockito.times(3)).doProcess();
        Mockito.verify(requestMock, Mockito.times(2)).flushTransLog();
    }

    /**
     * translogのflush時にNodeDisconnectedExceptionが発生した場合にflushのリトライをすること.
     * @throws Exception 実行中の例外
     */
    @Test
    public void translogのflush時にNodeDisconnectedExceptionが発生した場合にflushのリトライをすること()
            throws Exception {
        Constructor<FlushTranslogRetryableRequest> constructor = FlushTranslogRetryableRequest.class
                .getDeclaredConstructor(new Class[] {
                EsTranslogHandler.class, Integer.TYPE, Long.TYPE });
        constructor.setAccessible(true);
        EsTranslogHandler handler = new EsTranslogHandler(RETRY_COUNT, 0, null, INDEX_FOR_TEST);
        FlushTranslogRetryableRequest flushMock = Mockito.spy((FlushTranslogRetryableRequest) constructor.newInstance(
                handler, 5, 0L));

        NodeDisconnectedException toBeThrown2 = Mockito.mock(NodeDisconnectedException.class);
        Mockito.doThrow(toBeThrown2) // 初回リクエストの例外投入
                .doReturn(null)
                .when(flushMock)
                .doProcess();

        flushMock.doRequest();
        Mockito.verify(flushMock, Mockito.times(2)).doProcess(); // ParticularErroではないのでリトライしないこと
        Mockito.verify(flushMock, Mockito.times(0)).flushTransLog();
    }

    /**
     * translog読み込み時にUncategorizedExecutionExceptionが発生した場合にflushが実行されること.
     * @throws Exception 実行時例外
     */
    @Test
    public void translogのflush時にBroadcastShardOperationFailedExceptionが発生した場合にflushのリトライをしないこと()
            throws Exception {
        Constructor<FlushTranslogRetryableRequest> constructor = FlushTranslogRetryableRequest.class
                .getDeclaredConstructor(new Class[] {EsTranslogHandler.class, Integer.TYPE, Long.TYPE });
        constructor.setAccessible(true);
        EsTranslogHandler handler = new EsTranslogHandler(RETRY_COUNT, 0, null, INDEX_FOR_TEST);
        FlushTranslogRetryableRequest flushMock = Mockito.spy((FlushTranslogRetryableRequest) constructor.newInstance(
                handler, 5, 0L));

        BroadcastShardOperationFailedException toBeThrown = Mockito.mock(BroadcastShardOperationFailedException.class);
        Mockito.doThrow(toBeThrown) // 初回リクエストの例外投入
                .doReturn(null)
                .when(flushMock)
                .doProcess();

        flushMock.doRequest();
        Mockito.verify(flushMock, Mockito.times(1)).doProcess(); // ParticularErrorのためリトライしないこと
        Mockito.verify(flushMock, Mockito.times(0)).flushTransLog();
    }

    /**
     * translog読み込み時にFlushNotAllowedEngineExceptionが発生した場合にflushが実行されること.
     * @throws Exception 実行時例外
     */
    @Test
    public void translogのflush時にFlushNotAllowedEngineExceptionが発生した場合にflushのリトライをしないこと()
            throws Exception {

        Constructor<FlushTranslogRetryableRequest> constructor = FlushTranslogRetryableRequest.class
                .getDeclaredConstructor(new Class[] {EsTranslogHandler.class, Integer.TYPE, Long.TYPE });
        constructor.setAccessible(true);
        EsTranslogHandler handler = new EsTranslogHandler(RETRY_COUNT, 0, null, INDEX_FOR_TEST);
        FlushTranslogRetryableRequest flushMock = Mockito.spy((FlushTranslogRetryableRequest) constructor.newInstance(
                handler, 5, 0L));

        FlushNotAllowedEngineException toBeThrown = Mockito.mock(FlushNotAllowedEngineException.class);
        Mockito.doThrow(toBeThrown) // 初回リクエストの例外投入
                .doReturn(null)
                .when(flushMock)
                .doProcess();

        flushMock.doRequest();
        Mockito.verify(flushMock, Mockito.times(1)).doProcess(); // ParticularErrorのためリトライしないこと
        Mockito.verify(flushMock, Mockito.times(0)).flushTransLog();
    }
}
