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

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.dumbster.smtp.SimpleSmtpServer;
import com.fujitsu.dc.client.DaoException;
import com.fujitsu.dc.client.http.DcRequestBuilder;
import com.fujitsu.dc.client.http.DcResponse;
import com.fujitsu.dc.engine.extension.support.ExtensionJarLoader;
import com.fujitsu.dc.jersey.engine.test.categories.Integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * ExtensionとEngineの結合テスト. <br />
 * 本テストを実行する際は、/fj/dc-engine/extensions配下に必要なExtensionとプロパティファイルを配備すること.<br />
 * 本テストを実行するにはsrc/test/resources配下に「extension-test-config.properties」を配置してください.<br />
 * （内容は「extension-test-config.properties.sample」を参考にしてください）
 */
@RunWith(DcRunner.class)
@Category({ Integration.class })
public class ExtensionScriptIntegrationTest extends ScriptTestBase {
    /**
     * コンストラクタ.
     */
    public ExtensionScriptIntegrationTest() {
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
     * スクリプトからExt_MailSenderクラスを呼び出せること. Extensionの MailSenderテスト用。 <br />
     * 本テストを動作させるためには、Ext_MailSenderの jarファイル、プロパティファイルが必要。<br />
     * <br />
     */
    @Test
    public final void スクリプトからExt_MailSenderクラスを呼び出せること() {

        SimpleSmtpServer smtpServer = SimpleSmtpServer.start(1025);

        try {
            String url;
            String testSrc = "callExtensionMailSender.js";
            HttpUriRequest req = null;
            String requestJson = "{\"to\":[{\"address\":\"hoge1@hoge.com\",\"name\":\"テストユーザ１\"},"
                    + "{\"address\":\"hoge2@hoge.com\",\"name\":\"テストユーザ２\"}],"
                    + "\"cc\":[{\"address\":\"hoge3@hoge.com\",\"name\":\"テストユーザ３\"}],"
                    + "\"bcc\":[{\"address\":\"hoge4@hoge.com\",\"name\":\"テストユーザ４\"}],"
                    + "\"from\":{\"address\":\"hoge5@hoge.com\",\"name\":\"テストユーザ５\"},"
                    + "\"reply-to\":[{\"address\":\"hoge6@hoge.com\",\"name\":\"テストユーザ６\"}],"
                    + "\"subject\":\"タイトル\",\"text\":\"メール本文の内容\",\"charset\":\"ISO-2022-JP\","
                    + "\"envelope-from\":\"hoge7@hoge.com\",\"headers\":{\"Organization\":\"personium\"}}";

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
                assertEquals("Successfully sent a mail.", dcRes.bodyAsString());

                if (!isServiceTest) {
                    // ITの場合はローカル以外のSMTPサーバに送信するためチェック不可
                    assertEquals(1, smtpServer.getReceivedEmailSize());
                }

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
        } finally {
            smtpServer.stop();
        }
    }

}
