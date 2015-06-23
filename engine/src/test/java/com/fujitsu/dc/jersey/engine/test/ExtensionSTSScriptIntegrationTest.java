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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.json.simple.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.fujitsu.dc.client.DaoException;
import com.fujitsu.dc.client.http.DcRequestBuilder;
import com.fujitsu.dc.client.http.DcResponse;
import com.fujitsu.dc.engine.extension.support.ExtensionJarLoader;
import com.fujitsu.dc.jersey.engine.test.categories.Integration;

/**
 * ExtensionとEngineの結合テスト. <br />
 * 本テストを実行する際は、/fj/dc-engine/extensions配下に必要なExtensionとプロパティファイルを配備すること.<br />
 * 本テストを実行するにはsrc/test/resources配下に「extension-test-config.properties」を配置してください.<br />
 * （内容は「extension-test-config.properties.sample」を参考にしてください）
 */
@RunWith(DcRunner.class)
@Category({ Integration.class })
public class ExtensionSTSScriptIntegrationTest extends ScriptTestBase {
    /**
     * コンストラクタ.
     */
    public ExtensionSTSScriptIntegrationTest() {
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
     * スクリプトからExt_AWSSecurityTokenServiceのSessionToken取得が行えること. Extensionの AWSSecurityTokenServiceテスト用。
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void スクリプトからExt_AWSSecurityTokenServiceのSessionToken取得が行えること() {

        Properties properties = new Properties();
        InputStream configFileStream = ClassLoader.getSystemResourceAsStream("extension-test-config.properties");
        try {
            properties.load(configFileStream);
        } catch (IOException e) {
            fail("properties load failuer");
        }

        String url;
        String testSrc = "callExtension_AWSSecurityTokenService_getSessionToken.js";
        HttpUriRequest req = null;
        JSONObject requestBody = new JSONObject();
        requestBody.put("AccessKeyId", properties.getProperty("AccessKeyId"));
        requestBody.put("SecretAccessKey", properties.getProperty("SecretAccessKey"));
        requestBody.put("ProxyHost", properties.getProperty("ProxyHost", null));
        requestBody.put("ProxyPort", Integer.parseInt(properties.getProperty("ProxyPort", "80")));
        requestBody.put("ProxyUser", properties.getProperty("ProxyUser", null));
        requestBody.put("ProxyPassword", properties.getProperty("ProxyPassword", null));
        requestBody.put("durationSeconds", null);

        System.out.println(requestBody.toJSONString());
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }
            // サービスの実行（durationSeconds省略）
            req = new DcRequestBuilder().url(url).method("POST").body(requestBody.toJSONString()).token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Dc-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            DcResponse dcRes = new DcResponse(objResponse);

            assertEquals(HttpStatus.SC_OK, dcRes.getStatusCode());
            JSONObject credentials = (JSONObject) dcRes.bodyAsJson().get("Credentials");
            System.out.println(credentials.toJSONString());
            assertNotNull(credentials);

            // サービスの実行（durationSeconds指定）
            requestBody.put("durationSeconds", 900);
            req = new DcRequestBuilder().url(url).method("POST").body(requestBody.toJSONString()).token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Dc-Version", version);
            }

            objResponse = httpClient.execute(req);
            dcRes = new DcResponse(objResponse);

            assertEquals(HttpStatus.SC_OK, dcRes.getStatusCode());
            credentials = (JSONObject) dcRes.bodyAsJson().get("Credentials");
            System.out.println(credentials.toJSONString());
            assertNotNull(credentials);

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
     * Ext_AWSSecurityTokenServiceクラスのSesssionToken取得でエラーが発生した場合エラーメッセージが返却されること.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void Ext_AWSSecurityTokenServiceクラスのSesssionToken取得でエラーが発生した場合エラーメッセージが返却されること() {

        Properties properties = new Properties();
        InputStream configFileStream = ClassLoader.getSystemResourceAsStream("extension-test-config.properties");
        try {
            properties.load(configFileStream);
        } catch (IOException e) {
            fail("properties load failuer");
        }

        String url;
        String testSrc = "callExtension_AWSSecurityTokenService_getSessionToken.js";
        HttpUriRequest req = null;
        JSONObject requestBody = new JSONObject();
        requestBody.put("AccessKeyId", properties.getProperty("AccessKeyId"));
        requestBody.put("SecretAccessKey", "dummySecretAccessKey");
        requestBody.put("ProxyHost", properties.getProperty("ProxyHost", null));
        requestBody.put("ProxyPort", Integer.parseInt(properties.getProperty("ProxyPort", "80")));
        requestBody.put("ProxyUser", properties.getProperty("ProxyUser", null));
        requestBody.put("ProxyPassword", properties.getProperty("ProxyPassword", null));
        requestBody.put("durationSeconds", null);

        System.out.println(requestBody.toJSONString());
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }
            // サービスの実行（durationSeconds省略）
            req = new DcRequestBuilder().url(url).method("POST").body(requestBody.toJSONString()).token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Dc-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            DcResponse dcRes = new DcResponse(objResponse);

            assertEquals(418, dcRes.getStatusCode());
            System.out.println(dcRes.bodyAsString());
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
     * スクリプトからExt_AWSSecurityTokenServiceのFederationToken取得が行えること. Extensionの AWSSecurityTokenServiceテスト用。
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void スクリプトからExt_AWSSecurityTokenServiceのFederationToken取得が行えること() {

        Properties properties = new Properties();
        InputStream configFileStream = ClassLoader.getSystemResourceAsStream("extension-test-config.properties");
        try {
            properties.load(configFileStream);
        } catch (IOException e) {
            fail("properties load failuer");
        }

        String url;
        String testSrc = "callExtension_AWSSecurityTokenService_getFederationToken.js";
        HttpUriRequest req = null;
        JSONObject requestBody = new JSONObject();
        requestBody.put("AccessKeyId", properties.getProperty("AccessKeyId"));
        requestBody.put("SecretAccessKey", properties.getProperty("SecretAccessKey"));
        requestBody.put("ProxyHost", properties.getProperty("ProxyHost", null));
        requestBody.put("ProxyPort", Integer.parseInt(properties.getProperty("ProxyPort", "80")));
        requestBody.put("ProxyUser", properties.getProperty("ProxyUser", null));
        requestBody.put("ProxyPassword", properties.getProperty("ProxyPassword", null));
        requestBody.put("durationSeconds", null);

        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }
            // サービスの実行（durationSeconds省略）
            req = new DcRequestBuilder().url(url).method("POST").body(requestBody.toJSONString()).token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Dc-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            DcResponse dcRes = new DcResponse(objResponse);

            assertEquals(HttpStatus.SC_OK, dcRes.getStatusCode());
            JSONObject res = (JSONObject) dcRes.bodyAsJson();
            JSONObject credentials = (JSONObject) res.get("Credentials");
            System.out.println(credentials.toJSONString());
            assertNotNull(credentials);
            JSONObject federatedUser = (JSONObject) res.get("FederatedUser");
            System.out.println(federatedUser.toJSONString());
            assertNotNull(federatedUser);
            Number packedPolicySize = (Number) res.get("PackedPolicySize");
            System.out.println(packedPolicySize);
            assertNotNull(packedPolicySize);

            // サービスの実行（durationSeconds指定）
            requestBody.put("durationSeconds", 900);
            req = new DcRequestBuilder().url(url).method("POST").body(requestBody.toJSONString()).token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Dc-Version", version);
            }

            objResponse = httpClient.execute(req);
            dcRes = new DcResponse(objResponse);

            assertEquals(HttpStatus.SC_OK, dcRes.getStatusCode());
            res = (JSONObject) dcRes.bodyAsJson();
            credentials = (JSONObject) res.get("Credentials");
            System.out.println(credentials.toJSONString());
            assertNotNull(credentials);
            federatedUser = (JSONObject) res.get("FederatedUser");
            System.out.println(federatedUser.toJSONString());
            assertNotNull(federatedUser);
            packedPolicySize = (Number) res.get("PackedPolicySize");
            System.out.println(packedPolicySize);
            assertNotNull(packedPolicySize);
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
     * Ext_AWSSecurityTokenServiceクラスのFederationToken取得でエラーが発生した場合エラーメッセージが返却されること.
     */
    @SuppressWarnings("unchecked")
    @Test
    public final void Ext_AWSSecurityTokenServiceクラスのFederationToken取得でエラーが発生した場合エラーメッセージが返却されること() {

        Properties properties = new Properties();
        InputStream configFileStream = ClassLoader.getSystemResourceAsStream("extension-test-config.properties");
        try {
            properties.load(configFileStream);
        } catch (IOException e) {
            fail("properties load failuer");
        }

        String url;
        String testSrc = "callExtension_AWSSecurityTokenService_getFederationToken.js";
        HttpUriRequest req = null;
        JSONObject requestBody = new JSONObject();
        requestBody.put("AccessKeyId", properties.getProperty("AccessKeyId"));
        requestBody.put("SecretAccessKey", "dummySecretAccessKey");
        requestBody.put("ProxyHost", properties.getProperty("ProxyHost", null));
        requestBody.put("ProxyPort", Integer.parseInt(properties.getProperty("ProxyPort", "80")));
        requestBody.put("ProxyUser", properties.getProperty("ProxyUser", null));
        requestBody.put("ProxyPassword", properties.getProperty("ProxyPassword", null));
        requestBody.put("durationSeconds", null);

        System.out.println(requestBody.toJSONString());
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }
            // サービスの実行（durationSeconds省略）
            req = new DcRequestBuilder().url(url).method("POST").body(requestBody.toJSONString()).token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Dc-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            DcResponse dcRes = new DcResponse(objResponse);

            assertEquals(418, dcRes.getStatusCode());
            System.out.println(dcRes.bodyAsString());
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
