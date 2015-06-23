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

import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.dsig.XMLSignatureException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.TokenDsigException;
import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.TokenParseException;
import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.TokenRootCrtException;

/**
 * x509証明書処理ライブラリのユニットテストクラス.
 */
public class X509KeySelecterTest {

    private static String x509Root = "x509/";
    private static String x509TestFolder = x509Root + "test/";
    private static String cellRootUrl = "https://example.com/X509TestCell/";
    private static String target = "https://example.com/targetCell/";
    private static String schema = "https://example.com/schemaCell/";

    private static List<Role> roleList = new ArrayList<Role>();

    /**
     * x509証明書処理ライブラリの初期設定.
     */
    @BeforeClass
    public static void beforeClass() {
        roleList.add(new Role("admin"));
        roleList.add(new Role("staff"));
        roleList.add(new Role("doctor"));
    }

    /**
     * 有効な証明書でのトランスセルトークン検証確認.
     */
    @Test
    public void 有効な証明書でのトランスセルトークン検証確認() {

        // 証明書設定
        String folderPath = x509Root + "effective/";
        String privateKeyFileName = ClassLoader.getSystemResource(folderPath + "pio.key").getPath();
        String certificateFileName = ClassLoader.getSystemResource(folderPath + "pio.crt").getPath();
        String[] rootCertificateFileNames = new String[1];
        rootCertificateFileNames[0] = ClassLoader.getSystemResource(folderPath + "cacert.crt").getPath();
        try {
            TransCellAccessToken.configureX509(privateKeyFileName, certificateFileName, rootCertificateFileNames);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // トランスセルトークンの生成
        TransCellAccessToken tcToken = new TransCellAccessToken("https://localhost/X509TestCell/", cellRootUrl
                + "#admin", target, roleList, schema);
        String token = tcToken.toTokenString();

        try {
            // トランスセルトークンのパース、証明書検証
            TransCellAccessToken tcToken2 = TransCellAccessToken.parse(token);
            // 正常にパース・検証が出来る事
            assertEquals(target, tcToken2.getTarget());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * 証明書設定がされていない場合でのトランスセルトークン検証確認.
     */
    @Test
    public void 証明書設定がされていない場合でのトランスセルトークン検証確認() {
        // デフォルト証明書を使用するため、全ての証明書パスにnullを設定
        try {
            TransCellAccessToken.configureX509(null, null, null);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        TransCellAccessToken tcToken = new TransCellAccessToken("https://localhost/X509TestCell/", cellRootUrl
                + "#admin", target, roleList, schema);
        String token = tcToken.toTokenString();

        try {
            TransCellAccessToken tcToken2 = TransCellAccessToken.parse(token);
            // 正常にパース・検証が出来る事
            assertEquals(target, tcToken2.getTarget());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * ルートCA証明書設定が空の場合でのトランスセルトークン検証確認.
     */
    @Test
    public void ルートCA証明書設定が空の場合でのトランスセルトークン検証確認() {
        // デフォルト証明書を使用するため、全ての証明書パスにnullを設定
        try {
            TransCellAccessToken.configureX509(null, null, new String[0]);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        TransCellAccessToken tcToken = new TransCellAccessToken("https://localhost/X509TestCell/", cellRootUrl
                + "#admin", target, roleList, schema);
        String token = tcToken.toTokenString();

        try {
            TransCellAccessToken tcToken2 = TransCellAccessToken.parse(token);
            // 正常にパース・検証が出来る事
            assertEquals(target, tcToken2.getTarget());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * ルートCA証明書とサーバ証明書の組み合わせが異なる場合の確認.
     */
    @Test
    public void ルートCA証明書とサーバ証明書の組み合わせが異なる場合の確認() {

        String folderPath = x509Root + "effective/";
        String privateKeyFileName = ClassLoader.getSystemResource(folderPath + "pio.key").getPath();
        String certificateFileName = ClassLoader.getSystemResource(folderPath + "pio.crt").getPath();
        // ルートCA証明書はデフォルトを使用するため、サーバ証明書とは異なる
        try {
            TransCellAccessToken.configureX509(privateKeyFileName, certificateFileName, null);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        TransCellAccessToken tcToken = new TransCellAccessToken("https://localhost/X509TestCell/", cellRootUrl
                + "#admin", target, roleList, schema);

        String token = tcToken.toTokenString();

        try {
            TransCellAccessToken.parse(token);
            fail("KeySelectorExceptionにならなかった");
        } catch (TokenDsigException e) {
            // KeySelectorExceptionでCA証明書のsubjectとの比較時にエラー
            assertEquals(new KeySelectorException().getClass(), e.getCause().getClass());
            // ログに出力されるメッセージの確認（真因メッセージはキャッチ出来る例外の１階層目に含まれている事）
            assertEquals("CA subject not match.", e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * サーバ証明書が有効期限切れの場合の確認.
     */
    @Test
    public void サーバ証明書が有効期限切れの場合の確認() {

        String folderPath = x509TestFolder + "server_expiration/";
        String privateKeyFileName = ClassLoader.getSystemResource(folderPath + "pio2.key").getPath();
        String certificateFileName = ClassLoader.getSystemResource(folderPath + "pio4.crt").getPath();
        String[] rootCertificateFileNames = new String[1];
        rootCertificateFileNames[0] = ClassLoader.getSystemResource(folderPath + "cacert.crt").getPath();
        try {
            TransCellAccessToken.configureX509(privateKeyFileName, certificateFileName, rootCertificateFileNames);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        TransCellAccessToken tcToken = new TransCellAccessToken("https://localhost/X509TestCell/", cellRootUrl
                + "#admin", target, roleList, schema);

        String token = tcToken.toTokenString();

        try {
            TransCellAccessToken.parse(token);
            fail("CertificateExpiredExceptionにならなかった");
        } catch (TokenDsigException e) {
            // CertificateExpiredExceptionで、メッセージに"NotAfter~"となる
            assertEquals(new CertificateExpiredException().getClass(), e.getCause().getCause().getClass());
            // ログに出力されるメッセージの確認（真因メッセージはキャッチ出来る例外の１階層目に含まれている事）
            // 証明書の有効期限切れの場合「NotAfter」で始まるメッセージになる
            assertTrue(e.getMessage(), e.getMessage().startsWith("NotAfter"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * ルートCA証明書が有効期限切れの場合の確認.
     */
    @Test
    public void ルートCA証明書が有効期限切れの場合の確認() {

        String folderPath = x509TestFolder + "ca_expiration/";
        String privateKeyFileName = ClassLoader.getSystemResource(folderPath + "pio.key").getPath();
        String certificateFileName = ClassLoader.getSystemResource(folderPath + "pio2.crt").getPath();
        String[] rootCertificateFileNames = new String[1];
        rootCertificateFileNames[0] = ClassLoader.getSystemResource(folderPath + "cacert.crt").getPath();
        try {
            TransCellAccessToken.configureX509(privateKeyFileName, certificateFileName, rootCertificateFileNames);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        TransCellAccessToken tcToken = new TransCellAccessToken("https://hoo/X509TestCell/", cellRootUrl + "#admin",
                target, roleList, schema);

        String token = tcToken.toTokenString();

        try {
            TransCellAccessToken.parse(token);
            fail("CertificateExpiredExceptionにならなかった");
        } catch (TokenParseException e) {
            fail(e.getMessage());
        } catch (TokenDsigException e) {
            fail(e.getMessage());
        } catch (TokenRootCrtException e) {
            // CA有効期限切れの場合、CertificateExpiredException
            assertEquals(new CertificateExpiredException().getClass(), e.getCause().getClass());
            // ログに出力されるメッセージの確認（真因メッセージはキャッチ出来る例外の１階層目に含まれている事）
            // 証明書の有効期限切れの場合「NotAfter」で始まるメッセージになる
            assertTrue(e.getMessage(), e.getMessage().startsWith("NotAfter"));
        }
    }

    /**
     * サーバ秘密鍵が異なる場合の確認.
     */
    @Test
    public void サーバ秘密鍵が異なる場合の確認() {

        String folderPath = x509TestFolder + "ca_expiration/";
        String privateKeyFileName = ClassLoader.getSystemResource(folderPath + "pio.key").getPath();
        folderPath = x509Root + "effective/";
        String certificateFileName = ClassLoader.getSystemResource(folderPath + "pio.crt").getPath();
        String[] rootCertificateFileNames = new String[1];
        rootCertificateFileNames[0] = ClassLoader.getSystemResource(folderPath + "cacert.crt").getPath();
        try {
            TransCellAccessToken.configureX509(privateKeyFileName, certificateFileName, rootCertificateFileNames);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        TransCellAccessToken tcToken = new TransCellAccessToken("https://localhost/X509TestCell/", cellRootUrl
                + "#admin", target, roleList, schema);

        String tokenStr = tcToken.toTokenString();

        try {
            TransCellAccessToken.parse(tokenStr);
            fail("シグネチャ検証でエラーにならなかった");
        } catch (TokenDsigException e) {
            // サーバ秘密鍵が異なる場合、シグネチャ検証でエラーになる
            assertEquals(new XMLSignatureException().getClass(), e.getCause().getClass());
            // ログに出力されるメッセージの確認（真因メッセージはキャッチ出来る例外の１階層目に含まれている事）
            assertTrue(e.getMessage(), e.getMessage().startsWith("java.security.SignatureException:"
                                                            + " Signature length not correct:"));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * ルートCA証明書が重複している場合の確認.
     */
    @Test
    public void ルートCA証明書が重複している場合の確認() {

        String folderPath = x509Root + "effective/";
        String privateKeyFileName = ClassLoader.getSystemResource(folderPath + "pio.key").getPath();
        String certificateFileName = ClassLoader.getSystemResource(folderPath + "pio.crt").getPath();
        String[] rootCertificateFileNames = new String[2];
        rootCertificateFileNames[0] = ClassLoader.getSystemResource(folderPath + "cacert.crt").getPath();
        rootCertificateFileNames[1] = ClassLoader.getSystemResource(folderPath + "cacert.crt").getPath();
        try {
            TransCellAccessToken.configureX509(privateKeyFileName, certificateFileName, rootCertificateFileNames);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        TransCellAccessToken tcToken = new TransCellAccessToken("https://pio/X509TestCell/", cellRootUrl + "#admin",
                target, roleList, schema);

        String tokenStr = tcToken.toTokenString();

        try {
            TransCellAccessToken.parse(tokenStr);
            fail("重複チェックでエラーにならなかった");

        } catch (TokenRootCrtException e) {
            assertEquals(new CertificateException().getClass(), e.getCause().getClass());
            // ログに出力されるメッセージの確認（真因メッセージはキャッチ出来る例外の１階層目に含まれている事）
            // CA証明書重複の場合、重複チェックでエラーになる
            assertEquals("ca subject name already use.", e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * サーバ証明書の署名が異なる場合の確認.
     */
    @Test
    public void サーバ証明書の署名が異なる場合の確認() {

        String folderPath = x509TestFolder + "server_diff/";
        // pio.crtはcacrt.crtで署名した後、1バイト修正しているため、無効な証明書になった
        String certificateFileName = ClassLoader.getSystemResource(folderPath + "pio.crt").getPath();
        String privateKeyFileName = ClassLoader.getSystemResource(folderPath + "pio.key").getPath();
        String[] rootCertificateFileNames = new String[1];
        rootCertificateFileNames[0] = ClassLoader.getSystemResource(folderPath + "cacert.crt").getPath();
        try {
            TransCellAccessToken.configureX509(privateKeyFileName, certificateFileName, rootCertificateFileNames);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        TransCellAccessToken tcToken = new TransCellAccessToken("https://localhost/X509TestCell/", cellRootUrl
                + "#admin", target, roleList, schema);
        String token = tcToken.toTokenString();

        try {
            TransCellAccessToken.parse(token);
            fail("署名エラーにならなかった");
        } catch (TokenDsigException e) {
            // 署名検証例外になる
            assertEquals(new SignatureException().getClass(), e.getCause().getCause().getClass());
            // ログに出力されるメッセージの確認（真因メッセージはキャッチ出来る例外の１階層目に含まれている事）
            assertEquals("Signature does not match.", e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    /**
     * issuerが異なる場合の確認.
     */
    @Test
    public void issuerが異なる場合の確認() {

        String folderPath = x509TestFolder + "server_diff/";
        String certificateFileName = ClassLoader.getSystemResource(folderPath + "pio.crt").getPath();
        String privateKeyFileName = ClassLoader.getSystemResource(folderPath + "pio.key").getPath();
        String[] rootCertificateFileNames = new String[1];
        rootCertificateFileNames[0] = ClassLoader.getSystemResource(folderPath + "cacert.crt").getPath();
        try {
            TransCellAccessToken.configureX509(privateKeyFileName, certificateFileName, rootCertificateFileNames);
        } catch (Exception e) {
            fail(e.getMessage());
        }

        // トークンのissuerにサーバ証明書のCN（localhost）と異なる値を設定する
        TransCellAccessToken tcToken = new TransCellAccessToken("https://hogehuga/X509TestCell/", cellRootUrl
                + "#admin", target, roleList, schema);
        String token = tcToken.toTokenString();

        try {
            TransCellAccessToken.parse(token);
            fail("トークンのissuerとサーバ証明書のCN検証でエラーにならなかった");
        } catch (TokenDsigException e) {
            assertEquals(new KeySelectorException().getClass(), e.getCause().getClass());
            // ログに出力されるメッセージの確認（真因メッセージはキャッチ出来る例外の１階層目に含まれている事）
            // CA証明書重複の場合、重複チェックでエラーになる
            assertEquals("issure not equals.", e.getMessage());
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }
}
