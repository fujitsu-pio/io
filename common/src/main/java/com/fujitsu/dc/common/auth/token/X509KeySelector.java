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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PublicKey;
import java.security.SignatureException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.crypto.AlgorithmMethod;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.KeySelectorResult;
import javax.xml.crypto.XMLCryptoContext;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.X509Data;

import net.oauth.signature.pem.PEMReader;

/**
 * X509のKeySelectorクラス.
 */
public class X509KeySelector extends KeySelector {

    /**
     * コンストラクタ.
     * @param issure issureのURL
     */
    public X509KeySelector(String issure) {
        super();
        this.issure = issure;
    }

    private String issure;

    private Map<String, X509Certificate> caCerts = new HashMap<String, X509Certificate>();

    /**
     * Default root CA certificate path.
     */
    public static final String DEFAULT_ROOT_CA_PATH = "x509/personium_ca.crt";

    /**
     * Default server certificate key path.
     */
    public static final String DEFAULT_SERVER_KEY_PATH = "x509/localhost.key";

    /**
     * Default server certificate path.
     */
    public static final String DEFAULT_SERVER_CRT_PATH = "x509/localhost.crt";

    /**
     * X509certificate Type.
     */
    public static final String X509KEY_TYPE = "X.509";

    @SuppressWarnings("rawtypes")
    @Override
    public final KeySelectorResult select(
            final KeyInfo keyInfoToUse,
            final KeySelector.Purpose purpose,
            final AlgorithmMethod method,
            final XMLCryptoContext context) throws KeySelectorException {
        Iterator ki = keyInfoToUse.getContent().iterator();
        while (ki.hasNext()) {
            XMLStructure info = (XMLStructure) ki.next();
            if (!(info instanceof X509Data)) {
                continue;
            }
            X509Data x509Data = (X509Data) info;
            Iterator xi = x509Data.getContent().iterator();
            while (xi.hasNext()) {
                Object o = xi.next();
                if (!(o instanceof X509Certificate)) {
                    continue;
                }
                X509Certificate x509Certificate = (X509Certificate) o;
                final PublicKey key = x509Certificate.getPublicKey();
                // Make sure the algorithm is compatible
                // with the method.
                if (algEquals(method.getAlgorithm(), key.getAlgorithm())) {
                    // x509証明書検証
                    cheakX509validate(x509Certificate);
                    return new KeySelectorResult() {
                        @Override
                        public Key getKey() {
                            return key;
                        }
                    };
                }
            }
        }
        throw new KeySelectorException("No key found!");
    }
    /*
     * アルゴリズムが同じである場合trueを返す.
     * @param algURI
     * @param algName
     */
    static boolean algEquals(final String algURI, final String algName) {
        return  ((algName.equalsIgnoreCase("DSA") && algURI.equalsIgnoreCase(SignatureMethod.DSA_SHA1))
                || (algName.equalsIgnoreCase("RSA") && algURI.equalsIgnoreCase(SignatureMethod.RSA_SHA1)));
    }

    /**
     * x509証明書検証.
     * @param certificate x509certificate
     * @throws KeySelectorException KeySelectorException
     */
    private void cheakX509validate(X509Certificate certificate) throws KeySelectorException {

        // サーバ証明書。検証対象
        String issuerDn = certificate.getIssuerX500Principal().getName();

        // サーバ証明書のSubject（CN=）の取得
        Map<String, Object> map = new HashMap<String, Object>();
        String subjectDn = certificate.getSubjectX500Principal().getName();
        // サンプル）1.2.840.113549.1.9.1=#1603706373,CN=pcs,OU=pcs,O=pcs,L=pcs,ST=pcs,C=JP
        String[] pvs = subjectDn.split(",");
        for (int i = 0; i < pvs.length; i++) {
            String[] pv = pvs[i].split("=");
            if (pv.length == 2) {
                map.put(pv[0].toUpperCase().trim(), pv[1].trim());
            }
        }
        String cnStr = (String) map.get("CN");

        // トークンのissureからドメイン名を取得
        URL issureUrl = null;
        try {
            issureUrl = new URL(issure);
        } catch (MalformedURLException e) {
            throw new KeySelectorException(e.getMessage(), e);
        }
        if (cnStr == null || !cnStr.equals(issureUrl.getHost())) {
            // トークンとルートCA証明書のissureが等しくない時
            throw new KeySelectorException("issure not equals.");
        }

        // サーバ証明書の検証
        // ■ 1 ■  有効期限切れチェック
        try {
            certificate.checkValidity();
        } catch (CertificateExpiredException e) {
            // 証明書の有効期限が切れている場合
            throw new KeySelectorException(e.getMessage(), e);
        } catch (CertificateNotYetValidException e) {
            // 証明書がまだ有効になっていない場合
            throw new KeySelectorException(e.getMessage(), e);
        }

        //  ■ ２  ■  証明書の発行者が信頼するRootCAリストにあるかチェック
        X509Certificate rootCrt = caCerts.get(issuerDn);
        // なかったらエラー
        if (rootCrt == null) {
            throw new KeySelectorException("CA subject not match.");
        }

        //  ■ ３ ■ 実際に証明書発行者の公開鍵で検証対象証明書の署名を検証。
        try {
            PublicKey keyRoot = rootCrt.getPublicKey();
            certificate.verify(keyRoot);
        } catch (NoSuchAlgorithmException e) {
            // サポートされていない署名アルゴリズムの場合
            throw new KeySelectorException(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            // 無効な鍵の場合
            throw new KeySelectorException(e.getMessage(), e);
        } catch (NoSuchProviderException e) {
            // デフォルトのプロバイダがない場合
            throw new KeySelectorException(e.getMessage(), e);
        } catch (SignatureException e) {
            // 署名エラーの場合
            throw new KeySelectorException(e.getMessage(), e);
        } catch (CertificateException e) {
            // 符号化エラーの場合
            throw new KeySelectorException(e.getMessage(), e);
        }
    }
    /**
     * ルートCA証明書の読み込み.
     * @param rootCaFileName ルートCA証明書ファイルパス
     * @throws IOException IOException
     * @throws CertificateException CertificateException
     */
    public void readRoot(List<String> rootCaFileName) throws IOException, CertificateException {

        // 設定が無い場合はデフォルトルートCA証明書を使う
        if (rootCaFileName == null || rootCaFileName.size() == 0) {
            readCaFile(TransCellAccessToken.class.getClassLoader().getResourceAsStream(DEFAULT_ROOT_CA_PATH));
            return;
        }

        // Read root Certificate
        for (String caCertFileName : rootCaFileName) {
            InputStream is = new FileInputStream(caCertFileName);
            readCaFile(is);
        }
    }

    private void readCaFile(InputStream is) throws IOException, CertificateException {

        PEMReader pemReader;
        pemReader = new PEMReader(is);
        byte[] bytesCert = pemReader.getDerBytes();
        CertificateFactory cf = CertificateFactory.getInstance(X509KEY_TYPE);
        X509Certificate x509Root = (X509Certificate) cf.generateCertificate(new ByteArrayInputStream(bytesCert));
        // ルートCA証明書の期限切れチェック
        x509Root.checkValidity();
        // ルートCA証明書の重複チェック
        if (caCerts.get(x509Root.getIssuerX500Principal().getName()) != null) {
            throw new CertificateException("ca subject name already use.");
        }
        caCerts.put(x509Root.getIssuerX500Principal().getName(), x509Root);
    }
}
