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
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.security.InvalidAlgorithmParameterException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.xml.crypto.KeySelectorException;
import javax.xml.crypto.MarshalException;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.DigestMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignatureMethod;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignature;
import javax.xml.crypto.dsig.XMLSignatureException;
import javax.xml.crypto.dsig.XMLSignatureFactory;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.oauth.signature.pem.PEMReader;
import net.oauth.signature.pem.PKCS1EncodedKeySpec;

import org.apache.commons.lang.CharEncoding;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fujitsu.dc.common.utils.DcCoreUtils;

/**
 * TransCellのAccessTokenを扱うクラス.
 */
@SuppressWarnings({ "unchecked", "rawtypes" })
public final class TransCellAccessToken extends AbstractOAuth2Token implements IExtRoleContainingToken {

    private SignedInfo signedInfo;

    private static final String URN_OASIS_NAMES_TC_SAML_2_0_ASSERTION = "urn:oasis:names:tc:SAML:2.0:assertion";

    /**
     * ログ.
     */
    static Logger log = LoggerFactory.getLogger(TransCellAccessToken.class);

    String id;
    String target;

    /**
     * トークンの有効期間.
     */
    public static final long LIFESPAN = 1 * MILLISECS_IN_AN_HOUR; // 1時間

    private static List<String> x509RootCertificateFileNames;
    private static XMLSignatureFactory xmlSignatureFactory;
    private static X509Certificate x509Certificate;
    private static KeyInfo keyInfo;
    private static PrivateKey privKey;

    /**
     * コンストラクタ.
     * @param id トークンの一意識別子
     * @param issuedAt 発行時刻(epochからのミリ秒)
     * @param lifespan トークンの有効時間（ミリ秒）
     * @param issuer 発行 Cell URL
     * @param subject アクセス主体URL
     * @param target ターゲットURL
     * @param roleList ロールリスト
     * @param schema クライアント認証されたデータスキーマ
     */
    public TransCellAccessToken(final String id,
            final long issuedAt,
            final long lifespan,
            final String issuer,
            final String subject,
            final String target,
            final List<Role> roleList,
            final String schema) {
        this.issuedAt = issuedAt;
        this.lifespan = lifespan;
        this.id = id;
        this.issuer = issuer;
        this.subject = subject;
        this.target = target;
        this.roleList = roleList;
        this.schema = schema;

        try {
            /*
             * creates the Reference object, which identifies the data that will be digested and signed. The Reference
             * object is assembled by creating and passing as parameters each of its components: the URI, the
             * DigestMethod, and a list of Transforms
             */
            DigestMethod digestMethod = xmlSignatureFactory.newDigestMethod(DigestMethod.SHA1, null);
            Transform transform = xmlSignatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
            Reference reference = xmlSignatureFactory.newReference("", digestMethod,
                    Collections.singletonList(transform), null, null);

            /*
             * creates the SignedInfo object that the signature is calculated over. Like the Reference object, the
             * SignedInfo object is assembled by creating and passing as parameters each of its components: the
             * CanonicalizationMethod, the SignatureMethod, and a list of References
             */
            CanonicalizationMethod c14nMethod = xmlSignatureFactory.newCanonicalizationMethod(
                    CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
            SignatureMethod signatureMethod = xmlSignatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
            signedInfo = xmlSignatureFactory.newSignedInfo(c14nMethod, signatureMethod,
                    Collections.singletonList(reference));
        } catch (NoSuchAlgorithmException e) {
            // 重大な異常なので非チェックにして上に上げる
            throw new RuntimeException(e);
        } catch (InvalidAlgorithmParameterException e) {
            // 重大な異常なので非チェックにして上に上げる
            throw new RuntimeException(e);
        }

    }

    /**
     * コンストラクタ.
     * @param id トークンの一意識別子
     * @param issuedAt 発行時刻(epochからのミリ秒)
     * @param issuer 発行 Cell URL
     * @param subject アクセス主体URL
     * @param target ターゲットURL
     * @param roleList ロールリスト
     * @param schema クライアント認証されたデータスキーマ
     */
    public TransCellAccessToken(final String id,
            final long issuedAt,
            final String issuer,
            final String subject,
            final String target,
            final List<Role> roleList,
            final String schema) {
        this(id, issuedAt, LIFESPAN, issuer, subject, target, roleList, schema);
    }

    /**
     * IDにUUIDを自動採番するコンストラクタ.
     * @param issuedAt 発行時刻(epochからのミリ秒)
     * @param issuer 発行 Cell URL
     * @param subject アクセス主体URL
     * @param target ターゲットURL
     * @param roleList ロールリスト
     * @param schema クライアント認証されたデータスキーマ
     */
    public TransCellAccessToken(
            final long issuedAt,
            final String issuer,
            final String subject,
            final String target,
            final List<Role> roleList,
            final String schema) {
        this(UUID.randomUUID().toString(), issuedAt, issuer, subject, target, roleList, schema);
    }

    /**
     * コンストラクタ.
     * @param issuer 発行 Cell URL
     * @param subject アクセス主体URL
     * @param target ターゲットURL
     * @param roleList ロールリスト
     * @param schema クライアント認証されたデータスキーマ
     */
    public TransCellAccessToken(
            final String issuer,
            final String subject,
            final String target,
            final List<Role> roleList,
            final String schema) {
        this(UUID.randomUUID().toString(), new Date().getTime(), issuer, subject, target, roleList, schema);
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.core.auth.token.AbstractOAuth2Token#toTokenString()
     */
    @Override
    public String toTokenString() {
        String samlStr = this.toSamlString();
        try {
            // Base64urlする
            String token = DcCoreUtils.encodeBase64Url(samlStr.getBytes(CharEncoding.UTF_8));
            return token;
        } catch (UnsupportedEncodingException e) {
            // UTF8が処理できないはずがない。
            throw new RuntimeException(e);
        }
    }

    /**
     * トークンからSAML文字列を生成します.
     * @return SAML文字列
     */
    public String toSamlString() {

        /*
         * Creation of SAML2.0 Document
         * http://docs.oasis-open.org/security/saml/v2.0/saml-core-2.0-os.pdf
         */

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = null;
        try {
            builder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            // 重大な異常なので非チェックにして上に上げる
            throw new RuntimeException(e);
        }
        Document doc = builder.newDocument();
        Element assertion = doc.createElementNS(URN_OASIS_NAMES_TC_SAML_2_0_ASSERTION, "Assertion");
        doc.appendChild(assertion);
        assertion.setAttribute("ID", this.id);
        assertion.setAttribute("Version", "2.0");

        // Dummy Date
        DateTime dateTime = new DateTime(this.issuedAt);

        assertion.setAttribute("IssueInstant", dateTime.toString());

        // Issuer
        Element issuer = doc.createElement("Issuer");
        issuer.setTextContent(this.issuer);
        assertion.appendChild(issuer);

        // Subject
        Element subject = doc.createElement("Subject");
        Element nameId = doc.createElement("NameID");
        nameId.setTextContent(this.subject);
        Element subjectConfirmation = doc.createElement("SubjectConfirmation");
        subject.appendChild(nameId);
        subject.appendChild(subjectConfirmation);
        assertion.appendChild(subject);

        // Conditions
        Element conditions = doc.createElement("Conditions");
        Element audienceRestriction = doc.createElement("AudienceRestriction");
        for (String aud : new String[] {this.target, this.schema}) {
            Element audience = doc.createElement("Audience");
            audience.setTextContent(aud);
            audienceRestriction.appendChild(audience);
        }
        conditions.appendChild(audienceRestriction);
        assertion.appendChild(conditions);

        // AuthnStatement
        Element authnStmt = doc.createElement("AuthnStatement");
        authnStmt.setAttribute("AuthnInstant", dateTime.toString());
        Element authnCtxt = doc.createElement("AuthnContext");
        Element authnCtxtCr = doc.createElement("AuthnContextClassRef");
        authnCtxtCr.setTextContent("urn:oasis:names:tc:SAML:2.0:ac:classes:PasswordProtectedTransport");
        authnCtxt.appendChild(authnCtxtCr);
        authnStmt.appendChild(authnCtxt);
        assertion.appendChild(authnStmt);

        // AttributeStatement
        Element attrStmt = doc.createElement("AttributeStatement");
        Element attribute = doc.createElement("Attribute");
        for (Role role : this.roleList) {
            Element attrValue = doc.createElement("AttributeValue");
            Attr attr = doc.createAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "type");
            attr.setPrefix("xsi");
            attr.setValue("string");
            attrValue.setAttributeNodeNS(attr);
            attrValue.setTextContent(role.schemeCreateUrlForTranceCellToken(this.issuer));
            attribute.appendChild(attrValue);
        }
        attrStmt.appendChild(attribute);
        assertion.appendChild(attrStmt);

        // Normalization を実施
        doc.normalizeDocument();

        // Dsigをつける。
        // Create a DOMSignContext and specify the RSA PrivateKey and
        // location of the resulting XMLSignature's parent element.
        DOMSignContext dsc = new DOMSignContext(privKey, doc.getDocumentElement());

        // Create the XMLSignature, but don't sign it yet.
        XMLSignature signature = xmlSignatureFactory.newXMLSignature(signedInfo, keyInfo);

        // Marshal, generate, and sign the enveloped signature.
        try {
            signature.sign(dsc);
            // 文字列化する。
            return DcCoreUtils.nodeToString(doc.getDocumentElement());
        } catch (MarshalException e1) {
            // DOMのシリアライズに失敗するのは重大な異常
            throw new RuntimeException(e1);
        } catch (XMLSignatureException e1) {
            // 署名できないような事態は異常
            throw new RuntimeException(e1);
        }

        /*
         * ------------------------------------------------------------
         * http://tools.ietf.org/html/draft-ietf-oauth-saml2-bearer-10
         * ------------------------------------------------------------ 2.1. Using SAML Assertions as Authorization
         * Grants To use a SAML Bearer Assertion as an authorization grant, use the following parameter values and
         * encodings. The value of "grant_type" parameter MUST be "urn:ietf:params:oauth:grant-type:saml2-bearer" The
         * value of the "assertion" parameter MUST contain a single SAML 2.0 Assertion. The SAML Assertion XML data MUST
         * be encoded using base64url, where the encoding adheres to the definition in Section 5 of RFC4648 [RFC4648]
         * and where the padding bits are set to zero. To avoid the need for subsequent encoding steps (by "application/
         * x-www-form-urlencoded" [W3C.REC-html401-19991224], for example), the base64url encoded data SHOULD NOT be
         * line wrapped and pad characters ("=") SHOULD NOT be included.
         */
    }

    /**
     * TransCellAccessTokenをパースしてオブジェクト生成する.
     * @param token トークン文字列
     * @return TransCellAccessTokenオブジェクト(パース成功時)
     * @throws AbstractOAuth2Token.TokenParseException トークンのパース失敗
     * @throws AbstractOAuth2Token.TokenDsigException 証明書の署名検証エラー
     * @throws AbstractOAuth2Token.TokenRootCrtException ルートCA証明書の検証エラー
     */
    public static TransCellAccessToken parse(final String token) throws AbstractOAuth2Token.TokenParseException,
    AbstractOAuth2Token.TokenDsigException, AbstractOAuth2Token.TokenRootCrtException {
        try {
            byte[] samlBytes = DcCoreUtils.decodeBase64Url(token);
            ByteArrayInputStream bais = new ByteArrayInputStream(samlBytes);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder builder = null;
            try {
                builder = dbf.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                // 重大な異常なので非チェックにして上に上げる
                throw new RuntimeException(e);
            }

            Document doc = builder.parse(bais);

            Element assertion = doc.getDocumentElement();
            Element issuer = (Element) (doc.getElementsByTagName("Issuer").item(0));
            Element subject = (Element) (assertion.getElementsByTagName("Subject").item(0));
            Element subjectNameID = (Element) (subject.getElementsByTagName("NameID").item(0));
            String id = assertion.getAttribute("ID");
            String issuedAtStr = assertion.getAttribute("IssueInstant");

            DateTime dt = new DateTime(issuedAtStr);

            NodeList audienceList = assertion.getElementsByTagName("Audience");
            Element aud1 = (Element) (audienceList.item(0));
            String target = aud1.getTextContent();
            String schema = null;
            if (audienceList.getLength() > 1) {
                Element aud2 = (Element) (audienceList.item(1));
                schema = aud2.getTextContent();
            }

            List<Role> roles = new ArrayList<Role>();
            NodeList attrList = assertion.getElementsByTagName("AttributeValue");
            for (int i = 0; i < attrList.getLength(); i++) {
                Element attv = (Element) (attrList.item(i));
                roles.add(new Role(new URL(attv.getTextContent())));
            }

            NodeList nl = assertion.getElementsByTagName("Signature");
            if (nl.getLength() == 0) {
                throw new TokenParseException("Cannot find Signature element");
            }
            Element signatureElement = (Element) nl.item(0);

            // 署名の有効性を確認する。以下の例外はTokenDsigException（署名検証エラー）
            // Create a DOMValidateContext and specify a KeySelector
            // and document context.
            X509KeySelector x509KeySelector = new X509KeySelector(issuer.getTextContent());
            DOMValidateContext valContext = new DOMValidateContext(x509KeySelector, signatureElement);

            // Unmarshal the XMLSignature.
            XMLSignature signature;
            try {
                signature = xmlSignatureFactory.unmarshalXMLSignature(valContext);
            } catch (MarshalException e) {
                throw new TokenDsigException(e.getMessage(), e);
            }

            // ルートCA証明書読み込み
            try {
                x509KeySelector.readRoot(x509RootCertificateFileNames);
            } catch (CertificateException e) {
                // ルートCA証明書設定エラーのため、重大であり、500
                throw new TokenRootCrtException(e.getMessage(), e);
            }

            // Validate the XMLSignature x509証明書検証.
            boolean coreValidity;
            try {
                coreValidity = signature.validate(valContext);
            } catch (XMLSignatureException e) {
                if (e.getCause().getClass() == new KeySelectorException().getClass()) {
                    throw new TokenDsigException(e.getCause().getMessage(), e.getCause());
                }
                throw new TokenDsigException(e.getMessage(), e);
            }


            // http://www.w3.org/TR/xmldsig-core/#sec-CoreValidation

            // Check core validation status.
            if (!coreValidity) {
                // シグネチャ検証
                boolean isDsigValid;
                try {
                    isDsigValid = signature.getSignatureValue().validate(valContext);
                } catch (XMLSignatureException e) {
                    throw new TokenDsigException(e.getMessage(), e);
                }
                if (!isDsigValid) {
                    throw new TokenDsigException("Failed signature validation");
                }

                // リファレンス検証
                Iterator i = signature.getSignedInfo().getReferences().iterator();
                for (int j = 0; i.hasNext(); j++) {
                    boolean refValid;
                    try {
                        refValid = ((Reference) i.next()).validate(valContext);
                    } catch (XMLSignatureException e) {
                        throw new TokenDsigException(e.getMessage(), e);
                    }
                    if (!refValid) {
                        throw new TokenDsigException("Failed to validate reference [" + j + "]");
                    }
                }
                throw new TokenDsigException("Signature failed core validation. unkwnon reason.");
            }
            return new TransCellAccessToken(id,
                    dt.getMillis(), issuer.getTextContent(), subjectNameID.getTextContent(),
                    target, roles, schema);
        } catch (UnsupportedEncodingException e) {
            throw new TokenParseException(e.getMessage(), e);
        } catch (SAXException e) {
            throw new TokenParseException(e.getMessage(), e);
        } catch (IOException e) {
            throw new TokenParseException(e.getMessage(), e);
        }
    }

    @Override
    public String getTarget() {
        return this.target;
    }

    @Override
    public String getId() {
        return this.id;
    }

    /**
     * X509の設定をする.
     * @param privateKeyFileName 秘密鍵ファイル名
     * @param certificateFileName 証明書ファイル名
     * @param rootCertificateFileNames ルート証明書ファイル名
     * @throws IOException IOException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws InvalidKeySpecException InvalidKeySpecException
     * @throws CertificateException CertificateException
     */
    public static void configureX509(String privateKeyFileName, String certificateFileName,
            String[] rootCertificateFileNames)
            throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, CertificateException {

        xmlSignatureFactory = XMLSignatureFactory.getInstance("DOM");

        // Read RootCA Certificate
        x509RootCertificateFileNames = new ArrayList<String>();
        if (rootCertificateFileNames != null) {
            for (String fileName : rootCertificateFileNames) {
                x509RootCertificateFileNames.add(fileName);
            }
        }

        // Read Private Key
        InputStream is = null;
        if (privateKeyFileName == null) {
            is = TransCellAccessToken.class.getClassLoader().getResourceAsStream(
                    X509KeySelector.DEFAULT_SERVER_KEY_PATH);
        } else {
            is = new FileInputStream(privateKeyFileName);
        }

        PEMReader privateKeyPemReader = new PEMReader(is);
        byte[] privateKeyDerBytes = privateKeyPemReader.getDerBytes();
        PKCS1EncodedKeySpec keySpecRSAPrivateKey = new PKCS1EncodedKeySpec(privateKeyDerBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        privKey = keyFactory.generatePrivate(keySpecRSAPrivateKey.getKeySpec());

        // Read Certificate
        if (certificateFileName == null) {
            is = TransCellAccessToken.class.getClassLoader().getResourceAsStream(
                    X509KeySelector.DEFAULT_SERVER_CRT_PATH);
        } else {
            is = new FileInputStream(certificateFileName);
        }
        PEMReader serverCertificatePemReader;
        serverCertificatePemReader = new PEMReader(is);
        byte[] serverCertificateBytesCert = serverCertificatePemReader.getDerBytes();
        CertificateFactory cf = CertificateFactory.getInstance(X509KeySelector.X509KEY_TYPE);
        x509Certificate = (X509Certificate) cf.generateCertificate(
                new ByteArrayInputStream(serverCertificateBytesCert));

        // Create the KeyInfo containing the X509Data
        KeyInfoFactory keyInfoFactory = xmlSignatureFactory.getKeyInfoFactory();
        List x509Content = new ArrayList();
        x509Content.add(x509Certificate.getSubjectX500Principal().getName());
        x509Content.add(x509Certificate);
        X509Data xd = keyInfoFactory.newX509Data(x509Content);
        keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(xd));

        // http://java.sun.com/developer/technicalArticles/xml/dig_signature_api/

    }



    @Override
    public String getExtCellUrl() {
        return this.getIssuer();
    }

    @Override
    public List<Role> getRoleList() {
        return this.getRoles();
    }

}
