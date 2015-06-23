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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.fujitsu.dc.client.DaoException;
import com.fujitsu.dc.client.http.DcRequestBuilder;
import com.fujitsu.dc.client.http.DcResponse;
import com.fujitsu.dc.engine.extension.support.ExtensionJarLoader;
import com.fujitsu.dc.unit.Unit;

/**
 * ユーザースクリプトの自動テスト.
 */
@RunWith(DcRunner.class)
@Category({Unit.class })
public class ExtensionScriptTest extends ScriptTestBase {
    /**
     * コンストラクタ.
     */
    public ExtensionScriptTest() {
        super("com.fujitsu.dc.engine");
    }

    /**
     * 各テストの実効前に実行する処理.
     * @throws Exception テスト実行前の前提条件設定に失敗した場合
     */
    @Before
    public void before() throws Exception {
        // クラスローダが singleton化されたことにより、一部のテスト実施でエラーがでるため、
        // テスト中は毎回 singletonを消して、新たにクラスローダが作成されるように修正。
        Field field = ExtensionJarLoader.class.getDeclaredField("singleton");
        field.setAccessible(true);
        field.set(null, null);
    }

    /**
     * 各テストの最後に実行する処理.
     */
    @After
    public void after() {
        // Extention用の jarファイルの置場所をデフォルト設定に戻す
        System.setProperty(ExtensionJarLoader.ENGINE_EXTENSION_DIR_KEY, ExtensionJarLoader.DEFAULT_EXTENSION_DIR);

    }

    /**
     * スクリプトから Extensionクラスのメソッドを呼び出せること.
     */
    @Test
    public final void スクリプトからExtensionクラスのメソッドを呼び出せること() {
        // テスト用 Extension jarの呼び出しディレクトリ設定
        String fileName = this.getClass().getClassLoader().getResource("extension").getFile();
        System.out.println("#####>>>> " + fileName);

        // Extention用の jarファイルの置場所を指定。この下にはテスト用の jarファイルが置かれているものとする。
        System.setProperty(ExtensionJarLoader.ENGINE_EXTENSION_DIR_KEY, fileName);

        String url;
        String testSrc = "callExtension.js";
        HttpUriRequest req = null;
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }
            // サービスの実行
            req = new DcRequestBuilder().url(url).method("GET").token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Dc-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            DcResponse dcRes = new DcResponse(objResponse);

            assertEquals(HttpStatus.SC_OK, dcRes.getStatusCode());
            assertEquals("Extensionの processメソッドを呼出します。←呼び出しました。"
                    + "Reading property file from extension class succeeded.", dcRes.bodyAsString());
        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    /**
     * スクリプトから Extensionクラスのメソッドを呼び出した結果の例外をハンドリングできること.
     */
    @Test
    public final void スクリプトからExtensionクラスのメソッドを呼び出した結果の例外をハンドリングできること() {
        // テスト用 Extension jarの呼び出しディレクトリ設定
        String fileName = this.getClass().getClassLoader().getResource("extension").getFile();
        System.out.println("#####>>>> " + fileName);

        // Extention用の jarファイルの置場所を指定。この下にはテスト用の jarファイルが置かれているものとする。
        System.setProperty(ExtensionJarLoader.ENGINE_EXTENSION_DIR_KEY, fileName);

        String url;
        String testSrc = "callExtensionAbort.js";
        HttpUriRequest req = null;
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }
            // サービスの実行
            req = new DcRequestBuilder().url(url).method("GET").token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Dc-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            DcResponse dcRes = new DcResponse(objResponse);

            assertEquals(418, dcRes.getStatusCode());
            String expected = "Aborted with following message: [I'm a teapot.] name:[Error] javaException: [undefined]";
            assertEquals(expected, dcRes.bodyAsString());
        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

    /**
     * スクリプトからExtensionクラスのメソッドを呼び出せること_引数JSON_復帰値JSON.
     * ついでに複数引数の Javaメソッド呼び出しを試みる。
     */
    @Test
    public final void スクリプトからExtensionクラスのメソッドを呼び出せること_引数JSON_復帰値JSON() {
        // テスト用 Extension jarの呼び出しディレクトリ設定
        String fileName = this.getClass().getClassLoader().getResource("extension").getFile();
        System.out.println("#####>>>> " + fileName);

        // Extention用の jarファイルの置場所を指定。この下にはテスト用の jarファイルが置かれているものとする。
        System.setProperty(ExtensionJarLoader.ENGINE_EXTENSION_DIR_KEY, fileName);

        String url;
        String testSrc = "callExtension_JSON_JSON.js";
        HttpUriRequest req = null;
        String requestJson = "{\"FirstName\":\"Miore\",\"MiddleName\":\"Claudia\",\"LastName\":\"Kondo\"}";

        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }
            // サービスの実行
            req = new DcRequestBuilder().url(url).method("POST").body(requestJson).token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Dc-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            DcResponse dcRes = new DcResponse(objResponse);

            assertEquals(HttpStatus.SC_OK, dcRes.getStatusCode());
            assertEquals("{\"result\":\"Ms. Miore Claudia Kondo\"}", dcRes.bodyAsString());
        } catch (DaoException e) {
            fail(e.getMessage());
        } catch (ClientProtocolException e) {
            fail(e.getMessage());
        } catch (IOException e) {
            fail(e.getMessage());
        } finally {
            if (isServiceTest) {
                // スクリプトの削除（Davのdel）
                try {
                    testSvcCol.del("test.js");
                } catch (DaoException e) {
                    fail(e.getMessage());
                }
            }
        }
    }

}
