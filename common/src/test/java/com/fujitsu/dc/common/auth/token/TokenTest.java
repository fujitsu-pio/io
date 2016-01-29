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
package com.fujitsu.dc.common.auth.token;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.TokenDsigException;
import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.TokenParseException;
import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.TokenRootCrtException;

/**
 * トークン処理ライブラリのユニットテストクラス.
 */
public class TokenTest {
    /**
     * トークン処理ライブラリの初期設定.
     * @throws IOException IOException
     * @throws CertificateException CertificateException
     * @throws InvalidKeySpecException InvalidKeySpecException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws javax.security.cert.CertificateException CertificateException
     */
    @BeforeClass
    public static void beforeClass()
            throws NoSuchAlgorithmException, InvalidKeySpecException, CertificateException, IOException,
            javax.security.cert.CertificateException {
        TransCellAccessToken.configureX509(null, null, null);
        LocalToken.setKeyString("abcdef0123456789");
    }


    /**
     * testTranceCellRefreshTokenのテスト.
     * @throws MalformedURLException URLパースエラー
     */
    @Test
    public void testTranceCellRefreshToken() throws MalformedURLException {
        String base = "https://localhost:8080/dc1-core/testcell1/__role/__/";
        List<Role> roleList = new ArrayList<Role>();
        roleList.add(new Role(new URL(base + "admin")));
        roleList.add(new Role(new URL(base + "staff")));
        roleList.add(new Role(new URL(base + "doctor")));

        String id = "1234";
        TransCellRefreshToken token = new TransCellRefreshToken(id, "http://receiver.com/rcv",
                "http://orig.com/orig/#subj", "http://orig.com/orig", roleList, "http://schema.com/schema");
        String tokenStr = token.toTokenString();

        TransCellRefreshToken token2 = null;
        try {
            token2 = TransCellRefreshToken.parse(tokenStr, "http://receiver.com/rcv");
            assertEquals(tokenStr, token2.toTokenString());
        } catch (AbstractOAuth2Token.TokenParseException e) {
            fail(e.getMessage());
        }
    }

    /**
     * testTransCellAccessTokenのテスト.
     * @throws TokenParseException TokenParseException
     * @throws TokenRootCrtException TokenRootCrtException
     * @throws TokenDsigException TokenDsigException
     */
    @Test
    public void testTransCellAccessToken() throws TokenParseException, TokenDsigException, TokenRootCrtException {
        String cellRootUrl = "https://localhost/TranscellAccessTokenTestCell/";
        String target = "https://example.com/targetCell/";
        String schema = "https://example.com/schemaCell/";

        List<Role> roleList = new ArrayList<Role>();
        roleList.add(new Role("admin"));
        roleList.add(new Role("staff"));
        roleList.add(new Role("doctor"));

        TransCellAccessToken tcToken = new TransCellAccessToken(cellRootUrl, cellRootUrl + "#admin", target, roleList,
                schema);

        String token = tcToken.toTokenString();

        TransCellAccessToken tcToken2 = TransCellAccessToken.parse(token);
        assertEquals(target, tcToken2.getTarget());

        for (Role role : roleList) {
            boolean hit = false;
            for (Role role2 : tcToken2.getRoles()) {
                String roleUrl = role.schemeCreateUrl(cellRootUrl);
                if (roleUrl.equals(role2.createUrl())) {
                    hit = true;
                }
            }
            assertTrue(hit);
        }
    }
    /**
     * testCellLocalAccessTokenのテスト.
     * @throws MalformedURLException URLパースエラー
     */
    @Test
    public void testCellLocalAccessToken() throws MalformedURLException {
        String base = "https://localhost:8080/dc1-core/testcell1/__role/__/";
        List<Role> roleList = new ArrayList<Role>();
        roleList.add(new Role(new URL(base + "admin")));
        roleList.add(new Role(new URL(base + "staff")));
        roleList.add(new Role(new URL(base + "doctor")));

        CellLocalAccessToken token = new CellLocalAccessToken("http://hogte.com/", "http://hige.com", roleList,
                "http://example.com/schema");

        String tokenStr = token.toTokenString();

        CellLocalAccessToken token2 = null;
        try {
            token2 = CellLocalAccessToken.parse(tokenStr, "http://hogte.com/");
            assertEquals(tokenStr, token2.toTokenString());
        } catch (AbstractOAuth2Token.TokenParseException e) {
            fail(e.getMessage());
        }
    }
}
