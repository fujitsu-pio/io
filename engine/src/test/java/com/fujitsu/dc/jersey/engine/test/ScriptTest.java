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

import javax.ws.rs.core.HttpHeaders;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.util.EntityUtils;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;

import com.fujitsu.dc.client.DaoException;
import com.fujitsu.dc.client.http.DcRequestBuilder;
import com.fujitsu.dc.client.http.DcResponse;
import com.fujitsu.dc.jersey.engine.test.categories.Integration;

/**
 * ユーザースクリプトの自動テスト.
 */
@RunWith(DcRunner.class)
@Category({Integration.class })
public class ScriptTest extends ScriptTestBase {
    /**
     * コンストラクタ.
     */
    public ScriptTest() {
        super("com.fujitsu.dc.engine");
    }

    /**
     * CellのCURDテスト.
     */
    @Test
    public final void cellTest() {
        callService("cell.js");
    }

    /**
     * BoxのCURDテスト.
     */
    @Test
    public final void boxTest() {
        callService("box.js");
    }

    /**
     * AccountのCURDテスト.
     */
    @Test
    public final void accountTest() {
        callService("account.js");
    }

    /**
     * AccountのバリデートCURDテスト.
     */
    @Test
    public final void accountValidateTest() {
        callService("accountValidate.js");
    }

    /**
     * ServiceSubjectのテスト.
     */
    @Test
    public final void serviceSubjectTest() {
        callService("serviceSubject.js");
    }

    /**
     * ChangePasswordのCURDテスト.
     */
    @Test
    public final void changePasswordTest() {
        callService("changePassword.js");
    }

    /**
     * ChangeMyPasswordのCURDテスト.
     */
    @Test
    public final void changeMyPasswordTest() {
        callService("changeMyPassword.js");
    }

    /**
     * Eventの登録テスト.
     */
    @Test
    public final void eventTest() {
        callService("event.js");
    }

    /**
     * CellLevelEventの登録テスト.
     */
    @Test
    public final void cellLevelEventTest() {
        callService("cellLevelEvent.js");
    }

    /**
     * ログファイル取得のテスト.
     */
    @Test
    public final void cellLevelEventLogTest() {
        callService("cellLevelEventLog.js");
    }

    /**
     * RoleのCRUDテスト.
     */
    @Test
    public final void roleTest() {
        callService("role.js");
    }

    /**
     * Role(複合キー)のCRUDテスト.
     */
    @Test
    public final void roleComplexTest() {
        callService("roleComplex.js");
    }

    /**
     * RelationのCRUDテスト.
     */
    @Test
    public final void relationTest() {
        callService("relation.js");
    }

    /**
     * Relation(複合キー)のCRUDテスト.
     */
    @Test
    public final void relationComplexTest() {
        callService("relationComplex.js");
    }

    /**
     * ExtRoleのCRUDテスト.
     */
    @Test
    public final void extRoleTest() {
        callService("extrole.js");
    }

    /**
     * ExtCellのCRUDテスト.
     */
    @Test
    public final void extCellTest() {
        callService("extcell.js");
    }

    /**
     * MKCOLのテスト.
     */
    @Test
    public final void mkColTest() {
        callService("mkcol.js");
    }

    /**
     * MKODATAのテスト.
     */
    @Test
    public final void mkOdataTest() {
        callService("mkodata.js");
    }

    /**
     * MKSERVICEのテスト.
     */
    @Test
    public final void mkServiceTest() {
        callService("mkservice.js");
    }

    /**
     * asExtCellのテスト.
     */
    @Test
    public final void asExtCellTest() {
        callService("asExtCell.js");
    }

    /**
     * asTransCellAccessTokenのテスト.
     */
    @Test
    public final void asTransCellAccessTokenTest() {
        callService("asTransCellAccessToken.js");
    }

    /**
     * asRefreshTokenのテスト.
     */
    @Test
    public final void asRefreshTokenTest() {
        callService("asRefreshToken.js");
    }

    /**
     * asRefreshTokenErrorのテスト.
     */
    @Test
    public final void asRefreshTokenErrorTest() {
        callService("asRefreshTokenError.js");
    }

    /**
     * asSchemaのテスト.
     */
    @Test
    public final void asSchemaTest() {
        callService("asSchema.js");
    }

    /**
     * asSchemaByTokenのテスト.
     */
    @Test
    public final void asSchemaByTokenTest() {
        callService("asSchemaByToken.js");
    }

    /**
     * asSelf & asClientのテスト.
     */
    @Test
    public final void asSelfClientTest() {
        callService("asSelfClient.js");
    }

    /**
     * linkRelationRoleのテスト.
     */
    @Test
    public final void linkRelationRoleTest() {
        callService("linkRelationRole.js");
    }

    /**
     * linkRelationExtCellのテスト.
     */
    @Test
    public final void linkRelationExtCellTest() {
        callService("linkRelationExtCell.js");
    }

    /**
     * linkExtCellRelationのテスト.
     */
    @Test
    public final void linkExtCellRelationTest() {
        callService("linkExtCellRelation.js");
    }

    /**
     * linkExtCellRoleのテスト.
     */
    @Test
    public final void linkExtCellRoleTest() {
        callService("linkExtCellRole.js");
    }

    /**
     * linkRoleAccountのテスト.
     */
    @Test
    public final void linkRoleAccountTest() {
        callService("linkRoleAccount.js");
    }

    /**
     * linkRoleExtCellのテスト.
     */
    @Test
    public final void linkRoleExtCellTest() {
        callService("linkRoleExtCell.js");
    }

    /**
     * linkRoleRelationのテスト.
     */
    @Test
    public final void linkRoleRelationTest() {
        callService("linkRoleRelation.js");
    }

    /**
     * linkExtRoleRoleのテスト.
     */
    @Test
    public final void linkExtRoleRoleTest() {
        callService("linkExtRoleRole.js");
    }

    /**
     * AssociationEndのテスト.
     */
    @Test
    public final void associationEndTest() {
        callService("associationEnd.js");
    }

    /**
     * ComplexTypeのテスト.
     */
    @Test
    public final void complexTypeTest() {
        callService("complexType.js");
    }

    /**
     * Propertyのテスト.
     */
    @Test
    public final void propertyTest() {
        callService("property.js");
    }

    /**
     * ComplexTypePropertyのテスト.
     */
    @Test
    public final void complexTypePropertyTest() {
        callService("complexTypeProperty.js");
    }

    /**
     * linkAssociationEndのテスト.
     */
    @Test
    public final void linkAssociationEndTest() {
        callService("linkAssociationEnd.js");
    }

    /**
     * ユーザーデータテスト.
     */
    @Test
    public final void userDataTest() {
        callService("userDataTest.js");
    }

    /**
     * ACLテスト.
     */
    @Test
    public final void aclTest() {
        callService("acl.js");
    }

    /**
     * ACL設定の正常系バリエーションテスト.
     */
    @Test
    public final void aclNormalVariationTest() {
        callService("aclNormalVariation.js");
    }

    /**
     * ACL設定の異常系バリエーションテスト.
     */
    @Test
    public final void aclErrorVariationTest() {
        callService("aclErrorVariation.js");
    }

    /**
     * NavigationPropertyPostのテスト.
     */
    @Test
    public final void navigationPropertyPostTest() {
        callService("navigationPropertyPost.js");
    }

    /**
     * Unit昇格のテスト.
     */
    @Test
    public final void upgradeUnitTest() {
        callService("upgradeUnit.js");
    }

    /**
     * UserDataのQueryテスト.
     */
    @Test
    public final void userDetaQueryTest() {
        callService("userDataQuery.js");
    }

    /**
     * UserDataのQueryテスト.
     */
    @Test
    public final void userDetaQueryExpandTest() {
        callService("userDataQueryExpand.js");
    }

    /**
     * Javaライブラリが呼び出せないこと.
     */
    @Test
    public final void Javaライブラリが呼び出せないこと() {
        callService("accessDenyJavaAPI.js");
    }

    /**
     * 呼び出しを許しているwrapperパッケージのクラスのコンストラクタが呼び出せないこと.
     */
    @Test
    public final void 呼び出しを許しているwrapperパッケージのクラスのコンストラクタが呼び出せないこと() {
        callService("cantCallConstructor.js");
    }

    /**
     * requireしたファイルが異常だった場合にエラーになること.
     */
    @Test
    public final void requireしたファイルが異常だった場合にエラーになること() {
        if (isServiceTest) {
            // スクリプトの登録 （Davのput）
            putScript("requireEvalErrorSub.js", "requireEvalErrorSub.js");
            callService("requireEvalError.js");
        }
        if (isServiceTest) {
            // スクリプトの削除（Davのdel）
            try {
                testSvcCol.del("requireEvalErrorSub.js");
            } catch (DaoException e) {
                fail(e.getMessage());
            }
        }
    }

    /**
     * requireしたファイルが存在しなかった場合にエラーになること.
     */
    @Test
    public final void requireしたファイルが存在しなかった場合にエラーになること() {
        callService("requireNoFile.js");
    }

    /**
     * WebDavファイルのバイナリでのアップロード及びダウンロード.
     */
    @Test
    public final void WebDavファイルのバイナリでのアップロード及びダウンロード() {

        String url;
        String testSrc = "DAOBinaryIO.js";
        HttpUriRequest req = null;
        String reqBody = "reqbodydata------\naaa\nbbb\nあいうえお\n";
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }

            // サービスの実行
            req = new DcRequestBuilder().url(url).method("POST").body(reqBody).token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Dc-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            DcResponse dcRes = new DcResponse(objResponse);

            assertEquals(HttpStatus.SC_OK, dcRes.getStatusCode());
            assertEquals(reqBody, dcRes.bodyAsString());
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
     * ファイルのバイナリからの読み込み.
     */
    @Test
    public final void ファイルのバイナリからの読み込み() {

        String url;
        String testSrc = "DAOBinaryRead.js";
        HttpUriRequest req = null;
        String reqBody = "reqbodydata------\naaa\nbbb\nあいうえお\n";
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }

            // サービスの実行
            req = new DcRequestBuilder().url(url).method("POST").body(reqBody).token(token).build();
            req.setHeader(KEY_HEADER_BASEURL, baseUrl);
            String version = getVersion();
            if (version != null && !(version.equals(""))) {
                req.setHeader("X-Dc-Version", version);
            }

            HttpResponse objResponse;
            objResponse = httpClient.execute(req);
            DcResponse dcRes = new DcResponse(objResponse);

            assertEquals(HttpStatus.SC_OK, dcRes.getStatusCode());
            assertEquals(reqBody, dcRes.bodyAsString());
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
     * 存在しないServiceのテスト.
     */
    @Test
    public final void serviceNotFound() {
        if (isServiceTest) {
            String url = String.format("%s/%s/%s/%s/test?cell=%s", baseUrl, cellName, boxName, "notfoundsvccol",
                    cellName);
            try {
                HttpUriRequest req = new DcRequestBuilder().url(url).method("GET").token(token).build();
                req.setHeader(KEY_HEADER_BASEURL, baseUrl);
                String version = getVersion();
                if (version != null && !(version.equals(""))) {
                    req.setHeader("X-Dc-Version", version);
                }
                request(req);
                fail();
            } catch (DaoException e) {
                assertEquals("404", e.getCode());
            }
        }
    }

    /**
     * スクリプトが空の場合のService実行テスト.
     */
    @Test
    public final void serviceEmpty() {
        String url;
        HttpUriRequest req = null;
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript("empty.js", "test.js");
                url = requestUrl();
            } else {
                url = requestUrl("empty.js");
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

            // ステータスコードが５００＆Content-Lengh 返却されていることを確認
            int statusCode = objResponse.getStatusLine().getStatusCode();
            assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, statusCode);
            String contentLength = dcRes.getHeader(HttpHeaders.CONTENT_LENGTH);
            if (contentLength == null || contentLength.length() <= 0) {
                fail("Content-Lengh header value does not exist");
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
    }

    /**
     * スクリプトに日本語が含まれる場合に正常にサービス実行できること.
     */
    @Test
    public final void serviceJapanese() {
        String url;
        String testSrc = "japanese.js";
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
            assertEquals("テストです。", dcRes.bodyAsString());
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
     * スクリプトから未サポートのレスポンスコードが返却された場合不正なHTTPレスポンスが指定されたエラーとなること.
     */
    @Test
    public final void nonSupportedResponseCode() {
        String url;
        HttpUriRequest req = null;
        try {
            String[][] jsList = {{"nonSupportedResponseCode105.js", "105" },
                    {"nonSupportedResponseCode301.js", "301" },
                    {"nonSupportedResponseCode303.js", "303" }, {"nonSupportedResponseCode307.js", "307" } };
            for (String[] testSrc : jsList) {
                // スクリプトの登録 （Davのput）
                if (isServiceTest) {
                    putScript(testSrc[0], "test.js");
                    url = requestUrl();
                } else {
                    url = requestUrl(testSrc[0]);
                }
                // サービスの実行
                req = new DcRequestBuilder().url(url).method("GET").token(token).build();
                req.setHeader(KEY_HEADER_BASEURL, baseUrl);
                String version = getVersion();
                if (version != null && !(version.equals(""))) {
                    req.setHeader("X-Dc-Version", version);
                }
                // レスポンスのチェック
                HttpResponse objResponse = httpClient.execute(req);
                DcResponse dcRes = new DcResponse(objResponse);
                assertEquals(500, dcRes.getStatusCode());
                String expectedMessage = String.format("Server Error : response status illegal type. status: %s",
                        testSrc[1]);
                assertEquals(expectedMessage, dcRes.bodyAsString());
                EntityUtils.consume(objResponse.getEntity());
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
    }

    /**
     * スクリプトから未サポートのレスポンスコードが返却された場合不正なHTTPレスポンスが指定されたエラーとなること(レスポンスコードはクエリで指定).
     */
    @Test
    public final void nonSupportedResponseCodeWithQuery() {
        String url;
        HttpUriRequest req = null;
        try {
            String[][] jsList = {{"returnResponseCodewithQuery.js", "105" },
                    {"returnResponseCodewithQuery.js", "301" },
                    {"returnResponseCodewithQuery.js", "303" }, {"returnResponseCodewithQuery.js", "307" } };
            for (String[] testSrc : jsList) {
                // スクリプトの登録 （Davのput）
                if (isServiceTest) {
                    putScript(testSrc[0], "test.js");
                    url = requestUrl() + "&status=" + testSrc[1];
                } else {
                    url = requestUrl(testSrc[0]) + "&status=" + testSrc[1];
                }
                // サービスの実行
                req = new DcRequestBuilder().url(url).method("GET").token(token).build();
                req.setHeader(KEY_HEADER_BASEURL, baseUrl);
                String version = getVersion();
                if (version != null && !(version.equals(""))) {
                    req.setHeader("X-Dc-Version", version);
                }
                // レスポンスのチェック
                HttpResponse objResponse = httpClient.execute(req);
                DcResponse dcRes = new DcResponse(objResponse);
                assertEquals(500, dcRes.getStatusCode());
                String expectedMessage = String.format("Server Error : response status illegal type. status: %s",
                        testSrc[1]);
                assertEquals(expectedMessage, dcRes.bodyAsString());
                EntityUtils.consume(objResponse.getEntity());
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
    }

    /**
     * スクリプトから未サポートのレスポンスヘッダが返却された場合指定したヘッダが取得できること.
     */
    @Test
    public final void nonSupportedResponseHeader() {
        String url;
        String testSrc = "nonSupportedResponseHeader.js";

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

            assertEquals(200, dcRes.getStatusCode());
            assertEquals("header value", dcRes.getHeader("Invalid-custom-header"));
            assertEquals("テストです。", dcRes.bodyAsString());

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
     * スクリプトからTransfer-Encodingヘッダが返却された場合無視されること.
     */
    @Test
    public final void nonSupportedResponseHeaderTransferEncoding() {
        String url;
        String testSrc = "nonSupportedResponseHeaderTransferEncoding.js";

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

            assertEquals(200, dcRes.getStatusCode());
            assertEquals("", dcRes.getHeader("Transfer-Encoding"));
            assertEquals("テストです。", dcRes.bodyAsString());

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
     * スクリプトからContent-typeとレスポンスボディが不一致の場合でもレスポンスが取得できること.
     */
    @Test
    public final void unmatchedBodyAndContentType() {
        String url;
        String testSrc = "unmatchedBodyAndContentType.js";

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

            String bodyAsString = dcRes.bodyAsString();
            assertEquals(200, dcRes.getStatusCode());
            assertEquals("テストです。", bodyAsString);

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
     * レスポンスにIllegalな値を設定した場合エラーになること.
     */
    @Test
    public final void レスポンスにIllegalな値を設定した場合エラーになること() {

        // 異常なレスポンスパターンとレスポンスボディのテストセット
        IllegalResponseFixture[] datas = {
                // レスポンスがJSGIの形式になっていない。
                new IllegalResponseFixture("Server Error : not NativeObject", "[]"),
                // ステータスコードが数値以外。
                new IllegalResponseFixture("Server Error : response status illegal type.",
                        "{status:\"hoge\",headers:{},body:[]}"),
                // ステータスコードが4桁。
                new IllegalResponseFixture("Server Error : response status illegal type. status: 2000",
                        "{status:2000,headers:{},body:[]}"),
                // ヘッダーが未定義。
                new IllegalResponseFixture("Server Error : not headers", "{status:200,body:[]}"),
                // ヘッダーのキーが文字列ではない。
                new IllegalResponseFixture("Server Error : header key format error",
                        "{status:200,headers:{100:\"hoge\"},body:[]}"),
                // ヘッダーの値が文字列ではない。
                new IllegalResponseFixture("Server Error : header value format error",
                        "{status:200,headers:{head:1000},body:[]}"),
                // ヘッダーのContent-Typeのメディア・タイプが存在しない値。
                new IllegalResponseFixture("Server Error : Response header parsing media type.",
                        "{status:200,headers:{\"Content-Type\":\"hoge\"},body:[]}"),
                // ヘッダーのContent-Typeのcharsetが存在しない値。
                new IllegalResponseFixture("Server Error : response charset illegal type.",
                        "{status:200,headers:{\"Content-Type\":\"plain/text;charset=hoge\"},body:[]}"),
                // レスポンスデータが空。
                new IllegalResponseFixture("Server Error : response body undefined forEach.",
                        "{status:200,headers:{}}"),
                // レスポンスデータのforEachが未実装。
                new IllegalResponseFixture("Server Error : response body undefined forEach.",
                        "{status:200,headers:{},body:{}}"),
                // レスポンスデータで返す値が文字列ではない。
                new IllegalResponseFixture("Server Error : response body illegal type.",
                        "{status:200,headers:{},body:[1]}") };

        String url;
        String testSrc = "responseIllegal.js";
        HttpUriRequest req = null;
        try {
            if (isServiceTest) {
                // スクリプトの登録 （Davのput）
                putScript(testSrc, "test.js");
                url = requestUrl();
            } else {
                url = requestUrl(testSrc);
            }

            for (int i = 0; i < datas.length; i++) {
                // サービスの実行
                req = new DcRequestBuilder().url(url).method("POST").body(datas[i].requestJson).token(token).build();
                req.setHeader(KEY_HEADER_BASEURL, baseUrl);
                String version = getVersion();
                if (version != null && !(version.equals(""))) {
                    req.setHeader("X-Dc-Version", version);
                }

                HttpResponse objResponse;
                objResponse = httpClient.execute(req);
                DcResponse dcRes = new DcResponse(objResponse);

                assertEquals(HttpStatus.SC_INTERNAL_SERVER_ERROR, dcRes.getStatusCode());
                assertEquals(datas[i].responseMessage, dcRes.bodyAsString());
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
    }

    /**
     * キャッシュ機構の確認のために2回呼び出しするテスト.
     */
    @Test
    public final void キャッシュ機構の確認のために2回呼び出しするテスト() {
        callService("cell.js");
        callService("cell.js");
    }

}
