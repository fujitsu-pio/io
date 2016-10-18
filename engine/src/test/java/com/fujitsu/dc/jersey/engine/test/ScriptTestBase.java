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
package com.fujitsu.dc.jersey.engine.test;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.simple.JSONObject;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.fujitsu.dc.client.Accessor;
import com.fujitsu.dc.client.Box;
import com.fujitsu.dc.client.Cell;
import com.fujitsu.dc.client.DaoException;
import com.fujitsu.dc.client.DcContext;
import com.fujitsu.dc.client.ServiceCollection;
import com.fujitsu.dc.client.http.DcRequestBuilder;
import com.fujitsu.dc.client.http.DcResponse;
import com.fujitsu.dc.client.http.HttpClientFactory;
import com.fujitsu.dc.engine.utils.DcEngineConfig;
import com.sun.jersey.test.framework.AppDescriptor;
import com.sun.jersey.test.framework.JerseyTest;
import com.sun.jersey.test.framework.spi.container.TestContainerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * engineスクリプトのテストを実行するための基底クラス.
 */
public abstract class ScriptTestBase extends JerseyTest {

    /** ローカルテスト用EngineリクエストUrl. */
    public static final String LOCAL_TEST_SERVICE_URL = "http://localhost:9998";
    /** デフォルトのリクエスト送信先URL. */
    public static final String DEFAULT_TARGET_URL = "http://localhost:8080/dc1-core";
    /** リクエスト送信先URLを取得するプロパティのキー. */
    public static final String PROP_TARGET_URL = "io.personium.test.target";
    /** システムプロパティから接続先のURLを取得する。 指定がない場合はデフォルトのURLを使用する. */
    static String baseUrl = System.getProperty(PROP_TARGET_URL, DEFAULT_TARGET_URL);
    /** サービステスト実施フラグ. */
    public static final String PROP_SERVICE_CALL_TEST_FLAG = "io.personium.test.service";
    /** システムプロパティからサービステストの実施フラグを取得する。 指定がない場合はFalseを使用する. */
    static boolean isServiceTest = Boolean.parseBoolean(System.getProperty(PROP_SERVICE_CALL_TEST_FLAG, "false"));
    /** マスタートークン無し運用時、最初のCellは、ユニットユーザー管理Cellにて発行したトークンを利用. */
    public static final String PROP_UNITUSER_CELL = "io.personium.test.unituser.cell";
    /** システムプロパティからユニットユーザーCell(URL)を取得する。 指定がない場合は未指定を意味する空文字をセットする. */
    private static String unitUserCell = System.getProperty(PROP_UNITUSER_CELL, "");
    /** ユニットユーザー管理Cellのアカウント. */
    public static final String PROP_UNITUSER_CELL_ACCOUNT = "io.personium.test.unituser.account";
    /** システムプロパティからユニットユーザーCell(URL)を取得する。 指定がない場合は未指定を意味する空文字をセットする. */
    private static String unitUserAccount = System.getProperty(PROP_UNITUSER_CELL_ACCOUNT, "");
    /** ユニットユーザー管理Cellのパスワード. */
    public static final String PROP_UNITUSER_CELL_PASSWORD = "io.personium.test.unituser.password";
    /** システムプロパティからユニットユーザーCell(URL)を取得する。 指定がない場合は未指定を意味する空文字をセットする. */
    private static String unitUserPassword = System.getProperty(PROP_UNITUSER_CELL_PASSWORD, "");
    /** 基底URLが格納されているヘッダ名. */
    protected static final String KEY_HEADER_BASEURL = "X-Baseurl";
    /** 共通で作成されたCellオブジェクトの個数. */
    private static final int CELL_COUNT = 3;
    /** 共通で作成されたCellオブジェクト. */
    private static Cell[] testCells = new Cell[CELL_COUNT];
    /** 共通で作成されたBoxオブジェクト. */
    private static Box[] testBoxs = new Box[CELL_COUNT];
    /** 共通で作成されたServiceCollectionオブジェクト. */
    static ServiceCollection testSvcCol;
    /** テストで利用するAccessor. */
    private static Accessor testAs = null;
    /** Cell名. */
    static String cellName = "enginetest";
    /** Box. */
    static String boxName = "boxname";
    /** Box. */
    private static String boxSchema = "box";
    /** Account. */
    private static String accountName = "user001";
    /** サービスサブジェクトアカウント. */
    private static String engineAccountName = "engine";
    /** Account password. */
    private static String accountPassword = "pass001";
    /** ServiceCollection名. */
    private static String serviceCollectionName = "svccol";
    /** サービス名. */
    private static String serviceName = "test";
    /** サービスパス名. */
    private static String serviceScriptPath = "test.js";
    /** Httpクライアント. */
    HttpClient httpClient = HttpClientFactory.create("insecure", 0);
    /** マスタートークン. */
    private static String masterToken = DcEngineConfig.getMasterToken();
    /** トークン. */
    static String token = "";
    /** DCコンテキスト. */
    private static DcContext dc;

    /**
     * コンストラクタ.
     */
    public ScriptTestBase() {
        super();
    }

    /**
     * コンストラクタ.
     * @param testContainerFactory .
     */
    public ScriptTestBase(TestContainerFactory testContainerFactory) {
        super(testContainerFactory);
    }

    /**
     * コンストラクタ.
     * @param ad .
     */
    public ScriptTestBase(AppDescriptor ad) {
        super(ad);
    }

    /**
     * コンストラクタ.
     * @param packages .
     */
    public ScriptTestBase(String... packages) {
        super(packages);
    }

    /**
     * 最初に実行される処理.
     * @throws DaoException DAO例外
     */
    @BeforeClass
    public static final void beforeClass() throws DaoException {
        // ＤＡＯインスタンスを生成
        dc = new DcContext(baseUrl, cellName, boxSchema, boxSchema);
        String version = getVersion();
        if (version != null && !(version.equals(""))) {
            dc.setDcVersion(version);
        }

        // 利用するトークンを特定
        if ("".equals(unitUserCell) || "".equals(unitUserAccount) || "".equals(unitUserPassword)) {
            // ユニットユーザーとして認証する情報がなければマスタートークンを利用する
            token = masterToken;
            dc.setDefaultHeader("X-Dc-Unit-User", "https://example.com/test#UnitUser");
        } else {
            // ユニットユーザーが指定されていれば、ユニットユーザーで認証したトークンを利用する
            Accessor as = dc.asAccount(unitUserCell, unitUserAccount, unitUserPassword);
            token = as.cell(baseUrl).getAccessToken();
        }
        // CLIENTトークンとして、上で取得したトークンをセットする
        dc.setClientToken(token);

        // テストで利用するAccessorを取得
        testAs = dc.withToken(token);

        // 前回のテストでリソースが残っている可能性があるので、まずは削除処理を行う
        destroyResources();

        // テストで利用するリソースを生成する
        makeResources();
    }

    /**
     * 最後に実行される処理.
     */
    @AfterClass
    public static final void afterClass() {
        destroyResources();
    }

    /**
     * テスト用に利用するリソースを生成する.
     */
    @SuppressWarnings("unchecked")
    static void makeResources() {
        // Account作成のJSON
        JSONObject accountJson = new JSONObject();
        accountJson.put("Name", accountName);

        // 昇格用アカウント作成のJSON
        JSONObject engineAccountJson = new JSONObject();
        engineAccountJson.put("Name", engineAccountName);

        // Box作成のJSON
        JSONObject boxJson = new JSONObject();
        boxJson.remove("Name");
        boxJson.put("Name", boxName);

        // Cell作成のJSON
        JSONObject json = new JSONObject();
        json.put("Name", cellName);

        for (int i = 0; i < testCells.length; i++) {
            try {
                testCells[i] = testAs.asCellOwner().unit.cell.create(json);
            } catch (DaoException e) {
                // CONFLICT(409)の場合は、すでにあるCellを利用する
                if (Integer.parseInt(e.getCode()) != HttpStatus.SC_CONFLICT) {
                    fail(e.getMessage());
                }
            }
            // Accountを作成
            try {
                testCells[i].account.create(accountJson, accountPassword);
                // サービスサブジェクトアカウントの作成
                testCells[i].account.create(engineAccountJson, null);
            } catch (DaoException e) {
                // CONFLICT(409)の場合は、すでにあるCellを利用する
                if (Integer.parseInt(e.getCode()) != HttpStatus.SC_CONFLICT) {
                    fail(e.getMessage());
                }
            }
            // Boxを作成
            try {
                testBoxs[i] = testCells[i].box.create(boxJson);
            } catch (DaoException e) {
                if (Integer.parseInt(e.getCode()) != HttpStatus.SC_CONFLICT) {
                    fail(e.getMessage());
                }
            }
            json.remove("Name");
            json.put("Name", cellName + (i + 1));
        }
        try {
            if (isServiceTest) {
                // サービスコレクションの作成 serviceCollectionName
                testBoxs[0].mkService(serviceCollectionName);
                testSvcCol = testBoxs[0].service(serviceCollectionName);
                // サービスの登録 （PROPPATCH）
                testSvcCol.configure(serviceName, serviceScriptPath, engineAccountName);
                // スクリプトの登録 （Davのput）
                putScript("testCommon.js", "testCommon.js");
            }
        } catch (DaoException e) {
            if (Integer.parseInt(e.getCode()) != HttpStatus.SC_CONFLICT) {
                fail(e.getMessage());
            }
        }
    }

    /**
     * テスト用リソースの削除.
     */
    static void destroyResources() {
        String name = cellName;
        for (int i = 0; i < testCells.length; i++) {
            removeCell(name);
            name = cellName + (i + 1);
        }
    }

    /**
     * CellおよびCell配下のリソースを削除する.
     * @param name 対象のCell名
     */
    static void removeCell(String name) {
        Cell cell = null;
        Box box = null;
        ServiceCollection svc = null;
        try {
            if (!testAs.asCellOwner().unit.cell.exists(name)) {
                return;
            }
            cell = testAs.cell(name);
            box = cell.box(boxName);
            svc = cell.box(boxName).service(serviceCollectionName);
        } catch (DaoException e) {
            fail(e.getMessage());
        }

        try {
            if (isServiceTest) {
                // スクリプトの削除 （Davのdelete）
                svc.del("testCommon.js");
                // サービスコレクションの削除
                box.del(serviceCollectionName);
            }
        } catch (DaoException e) {
            if (Integer.parseInt(e.getCode()) != HttpStatus.SC_NOT_FOUND) {
                fail(e.getMessage());
            }
        }

        // Box削除
        try {
            cell.box.del(boxName);
        } catch (DaoException e) {
            if (Integer.parseInt(e.getCode()) != HttpStatus.SC_NOT_FOUND) {
                fail(e.getMessage());
            }
        }

        // Account削除
        try {
            cell.account.del(accountName);
            // サービスサブジェクトアカウントの削除
            cell.account.del(engineAccountName);
        } catch (DaoException e) {
            if (Integer.parseInt(e.getCode()) != HttpStatus.SC_NOT_FOUND) {
                fail(e.getMessage());
            }
        }

        // Cell削除
        try {
            testAs.asCellOwner().unit.cell.del(name);
        } catch (DaoException e) {
            if (Integer.parseInt(e.getCode()) != HttpStatus.SC_NOT_FOUND) {
                fail(e.getMessage());
            }
        }
    }

    /**
     * 異常なレスポンスのテストパターン.
     */
    protected static class IllegalResponseFixture {
        String responseMessage;
        String requestJson;

        IllegalResponseFixture(String responseMessage, String requestJson) {
            this.responseMessage = responseMessage;
            this.requestJson = requestJson;
        }
    }

    /**
     * スクリプトの登録.
     * @param resourceName リソース名
     * @param fileName ファイル名
     */
    protected static void putScript(final String resourceName, final String fileName) {
        // スクリプトの登録 （Davのput）
        String testFilename = ScriptTest.class.getResource("/service/" + resourceName).getFile();

        // DAVファイル登録
        // dc.setChunked(false);
        try {
            FileInputStream fis = new FileInputStream(testFilename);
            testSvcCol.put(fileName, "text/plain", fis, "*");
        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (FileNotFoundException e) {
            fail(e.getMessage());
        }
    }

    /**
     * サービスを実行.
     * @param name サービス名
     */
    protected void callService(final String name) {
        String url;
        if (isServiceTest) {
            // スクリプトの登録 （Davのput）
            putScript(name, "test.js");
            url = requestUrl();
        } else {
            url = requestUrl(name);
        }
        // サービスの実行
        callServiceTest(url);
        if (isServiceTest) {
            // スクリプトの削除（Davのdel）
            try {
                testSvcCol.del("test.js");
            } catch (DaoException e) {
                fail(e.getMessage());
            }
        }
    }

    /**
     * テスト実施.
     * @param url 実施スクリプトurl
     */
    private void callServiceTest(final String url) {
        try {
            HttpUriRequest req = new DcRequestBuilder().url(url).method("GET").token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Dc-Version", version);
            }
            DcResponse res = request(req);
            assertEquals(HttpStatus.SC_OK, res.getStatusCode());
            assertEquals("OK", res.bodyAsString());
        } catch (DaoException e) {
            fail(e.getMessage());
        }
    }

    /**
     * テスト用のリクエストURL文字列を組み立てる.
     * @return 生成したURL文字列
     */
    protected String requestUrl() {
        return String.format("%s/%s/%s/%s/test?cell=%s", baseUrl, cellName, boxName, serviceCollectionName, cellName);
    }

    /**
     * テスト用のリクエストURL文字列を組み立てる.
     * @param name 実行するスクリプト名
     * @return 生成したURL文字列
     */
    protected String requestUrl(final String name) {
        return String.format("%s/%s/%s/test/%s?cell=%s", LOCAL_TEST_SERVICE_URL, cellName, boxName, name, cellName);
    }

    /**
     * Reponseボディを受ける場合のHTTPリクエストを行う.
     * @param httpReq HTTPリクエスト
     * @return DCレスポンスオブジェクト
     * @throws DaoException DAO例外
     */
    protected DcResponse request(final HttpUriRequest httpReq) throws DaoException {
        try {
            HttpResponse objResponse = httpClient.execute(httpReq);
            DcResponse dcRes = new DcResponse(objResponse);
            int statusCode = objResponse.getStatusLine().getStatusCode();
            if (statusCode >= HttpStatus.SC_MULTIPLE_CHOICES) {
                throw DaoException.create("{\"io exception\" : " + dcRes.bodyAsString() + "}", statusCode);
            }
            return dcRes;
        } catch (IOException ioe) {
            throw DaoException.create("{\"io exception\" : " + ioe.getMessage() + "}", 0);
        }
    }

    /**
     * テスト対象のバージョンを取得する.
     * @return テスト対象のバージョン
     */
    protected static String getVersion() {
        return DcEngineTestConfig.getVersion();
    }

}
