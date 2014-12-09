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
package com.fujitsu.dc.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fujitsu.dc.client.http.BatchAdapter;
import com.fujitsu.dc.client.http.DcResponse;
import com.fujitsu.dc.client.http.RestAdapter;
import com.fujitsu.dc.client.http.RestAdapterFactory;
import com.fujitsu.dc.client.utils.UrlUtils;

///**
// * アクセッッサクラス. personium.ioへアクセスするＡＰＩを呼び出す際のアクセス主体となります。
// */
/**
 * It creates a new object of Accessor. This is the base class for setting the access parameters to access Cloud data.
 */
public class Accessor implements Cloneable {

    // /// Accessor 内部で使用するフィールド
    // /** asメソッドに利用する type. */
    /** Final variable for holding key for SELF. */
    public static final String KEY_SELF = "self";
    // /** asメソッドに利用する type. */
    /** Final variable for holding key for CLIENT. */
    public static final String KEY_CLIENT = "client";
    // /** asメソッドに利用する type. */
    /** Final variable for holding key for TOKEN. */
    public static final String KEY_TOKEN = "token";

    // /// 認証してもらった情報を保持するフィールド
    // /** トークンの有効期限. */
    /** Expiration date of the token. */
    private Number expiresIn = null;
    // /** アクセストークン. */
    /** Access token. */
    private String accessToken = null;
    // /** リフレッシュトークンの有効期限. */
    /** Expiration date of the refresh token. */
    private Number refreshExpiresIn = null;
    // /** リフレッシュトークン. */
    /** Refresh Token. */
    private String refreshToken = null;
    // /** トークンタイプ. */
    /** Token type. */
    private String tokenType = null;

    /** Parameter to represent level of schema authorization. */
    private JSONObject schemaAuth;

    // /// クライアントから渡されるフィールド
    // /** "self","client"等のタイプを保持. */
    /** Holds the type of self "," client ", etc. */
    private String accessType = "";
    // /** Cellの名前. */
    /** Authorised cell URL. */
    private String authCellUrl;
    // /** 認証ID. */
    /** Authentication User ID. */
    private String userId;
    // /** 認証パスワード. */
    /** Authentication password. */
    private String password;

    // /** スキーマ. */
    /** Schema. */
    private String schema;
    // /** スキーマ認証ID. */
    /** Schema authentication ID. */
    private String schemaUserId;
    // /** スキーマ認証パスワード. */
    /** Schema authentication password. */
    private String schemaPassword;

    // /** 対象Cellの名前. */
    /** Cell name. */
    private String targetCellName;

    // /** トランスセルトークン. */
    /** Transformer cell token. */
    private String transCellToken;
    // /** トランスセルリフレッシュトークン. */
    /** Transformer cell refresh token. */
    private String transCellRefreshToken;

    // /** オーナー. */
    /** Owner. */
    protected boolean owner = false;

    // /////
    // /** 現在のBox Schema. */
    /** Current Box Schema. */
    private String boxSchema = "";
    // /** 現在のBox Name. */
    /** Current Box Name. */
    private String boxName = "";
    // /** 基底URL. */
    /** Base URL. */
    private String baseUrl = "";

    // /** DCコンテキスト. */
    /** Reference to DcContext. */
    private DcContext context;
    /** Cell. */
    private Cell currentCell;

    // /** バッチモード. */
    /** Batch Mode. */
    private boolean batch;

    /** BatchAdapter. */
    private BatchAdapter batchAdapter;

    // /** デフォルトヘッダ. */
    /** Default header. */
    HashMap<String, String> defaultHeaders;

    // /** サーバーのレスポンスから取得したレスポンスヘッダ. */
    /** Response header acquired from the server response. */
    private HashMap<String, String> resHeaders = new HashMap<String, String>();

    // /**
    // * コンストラクタ.
    // * @param dcContext DCコンテキスト
    // */
    /**
     * This is the parameterized constructor initializing the various class variables.
     * @param dcContext DcContext
     */
    public Accessor(DcContext dcContext) {
        this.expiresIn = 0;
        this.refreshExpiresIn = 0;
        this.context = dcContext;
        this.baseUrl = dcContext.getBaseUrl();
        this.authCellUrl = dcContext.getCellName();
        this.boxSchema = dcContext.getBoxSchema();
        this.boxName = dcContext.getBoxName();
    }

    // /**
    // * Accessorのクローンを生成する.
    // * @return コピーしたAccessorオブジェクト
    // */
    /**
     * This method is used to initialize the class variable accessor.
     * @return Accessor object
     */
    public Accessor clone() {
        try {
            return (Accessor) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    // /**
    // * Cell を指定します.
    // * @return CellへアクセスするためのCellインスタンス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method returns the specified cell. It does not take any parameter.
     * @return Cell object
     * @throws DaoException Exception thrown
     */
    public Cell cell() throws DaoException {
        return this.cell(this.getCellName());
    }

    // /**
    // * 他のCellを指定します.
    // * @param cell 接続先Cell URL
    // * @return CellへアクセスするためのCellインスタンス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method returns the cell specified in the parameter.
     * @param cell Destination Cell URL
     * @return Cell instance
     * @throws DaoException Exception thrown
     */
    public Cell cell(String cell) throws DaoException {
        if (!this.authCellUrl.equals(cell)) {
            this.targetCellName = cell;
        }
        // Unit昇格時はこのタイミングで認証を行わない
        /** Authentication is not performed. */
        if (!this.owner) {
            certification();
        }
        return new Cell(this, cell);
    }

    // /**
    // * パスワード変更.
    // * @param newPassword 変更するパスワード
    // * @throws DaoException DAO例外
    // */
    /**
     * This method changes the current password.
     * @param newPassword Password new value
     * @throws DaoException Exception thrown
     */
    public void changePassword(String newPassword) throws DaoException {
        if (this.accessToken == null) {
            // accessTokenが無かったら自分で認証する
            /** authentication is performed when accessToken is not present. */
            certification();
        }
        // パスワード変更
        /** Password change. */
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put("X-Dc-Credential", newPassword);
        // パスワード変更のURLを作成
        /** Create the URL for password change. */
        String cellUrl = this.getCellName();
        if (!UrlUtils.isUrl(cellUrl)) {
            cellUrl = UrlUtils.append(this.getBaseUrl(), this.getCellName());
        }
        String url = UrlUtils.append(cellUrl, "__mypassword");

        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this);
        rest.put(url, headers);
        // password変更でエラーの場合は例外がthrowされるので例外で無い場合は、
        // Accessorのpasswordを新しいのに変えておく
        this.password = newPassword;
    }

    // /**
    // * $Batchモードの取得.
    // * @return batchモード
    // */
    /**
     * This method returns $Batch - batch mode.
     * @return batch mode
     */
    public final boolean isBatchMode() {
        return batch;
    }

    // /**
    // * $Batchモードの設定.
    // * @param batch $Batchモード
    // */
    /**
     * This method sets $Batch - batch mode.
     * @param batch mode
     */
    public final void setBatch(boolean batch) {
        this.batchAdapter = new BatchAdapter(this);
        this.batch = batch;
    }

    // /**
    // * BatchAdaptrの取得. インスタンスが生成されていない場合生成する
    // * @return BatchAdapterオブジェクト
    // */
    /**
     * This method generates BatchAdapter instance if not created before.
     * @return BatchAdapter object
     */
    public BatchAdapter getBatchAdapter() {
        if (null == this.batchAdapter) {
            this.batchAdapter = new BatchAdapter(this);
        }
        return this.batchAdapter;
    }

    // /**
    // * Unit昇格.
    // * @return 昇格後のAccessor(OwnerAccessor)
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs Unit Promotion by creating and initializing OwnerAccessor.
     * @return Promoted OwnerAccessor
     * @throws DaoException Exception thrown
     */
    public OwnerAccessor asCellOwner() throws DaoException {
        OwnerAccessor oas;
        oas = new OwnerAccessor(this.context, this);
        oas.defaultHeaders = this.defaultHeaders;
        return oas;
    }

    // /**
    // * グローバルトークンの取得.
    // * @return グローバルトークン
    // */
    /**
     * This method returns the global access token.
     * @return global token
     */
    public String getAccessToken() {
        return this.accessToken;
    }

    // /**
    // * デフォルトヘッダを設定.
    // * @param value デフォルトヘッダ
    // */
    /**
     * This method sets the default header.
     * @param value Default header Map
     */
    public void setDefaultHeaders(HashMap<String, String> value) {
        this.defaultHeaders = value;
    }

    // /**
    // * デフォルトヘッダを取得.
    // * @return デフォルトヘッダ
    // */
    /**
     * This method gets the default header.
     * @return Default header
     */
    public HashMap<String, String> getDefaultHeaders() {
        return this.defaultHeaders;
    }

    // /**
    // * グローバルトークンの設定.
    // * @param token グローバルトークン
    // */
    /**
     * This method sets the global access token.
     * @param token Access Token
     */
    void setAccessToken(String token) {
        this.accessToken = token;
    }

    // /**
    // * DaoConfigオブジェクトを取得.
    // * @return DaoConfigオブジェクト
    // */
    /**
     * This method gets the DaoConfig object.
     * @return DaoConfig object
     */
    public DaoConfig getDaoConfig() {
        return context.getDaoConfig();
    }

    // /**
    // * DCコンテキストの取得.
    // * @return DCコンテキスト
    // */
    /**
     * This method returns the DcContext object.
     * @return DcContext
     */
    DcContext getContext() {
        return this.context;
    }

    // /**
    // * DCコンテキストの設定.
    // * @param c DCコンテキスト
    // */
    /**
     * This method sets the DcContext object.
     * @param c DcCOntext
     */
    void setContext(DcContext c) {
        this.context = c;
    }

    // /**
    // * 現在アクセス中のCell取得.
    // * @return Cellクラスインスタンス
    // */
    /**
     * This method returns the current cell being accessed.
     * @return Current cell object
     */
    Cell getCurrentCell() {
        return this.currentCell;
    }

    // /**
    // * 現在アクセス中のCell設定.
    // * @param cell Cellクラスインスタンス
    // */
    /**
     * This method sets the current cell as specified.
     * @param cell Cell object
     */
    void setCurrentCell(Cell cell) {
        this.currentCell = cell;
    }

    // /**
    // * トークンの有効期限の取得.
    // * @return トークンの有効期限
    // */
    /**
     * This method returns the expiration value of token.
     * @return Expiration date of the token
     */
    public Number getExpiresIn() {
        return this.expiresIn;
    }

    // /**
    // * リフレッシュトークンの設定.
    // * @return リフレッシュトークン
    // */
    /**
     * This method returns the refresh token value.
     * @return Refresh token value
     */
    public String getRefreshToken() {
        return refreshToken;
    }

    // /**
    // * リフレッシュトークンの設定.
    // * @return リフレッシュトークン
    // */
    /**
     * This method returns the token type.
     * @return token type
     */
    public String getTokenType() {
        return tokenType;
    }

    // /**
    // * リフレッシュトークンの有効期限の取得.
    // * @return リフレッシュトークンの有効期限
    // */
    /**
     * This method returns the expiration date of refresh token.
     * @return expiration date of refresh token
     */
    public Number getRefreshExpiresIn() {
        return refreshExpiresIn;
    }

    // /**
    // * CellName値の取得.
    // * @return CellName値
    // */
    /**
     * This method returns the authorized cell URL.
     * @return CellName URL
     */
    String getCellName() {
        return this.authCellUrl;
    }

    // /**
    // * CellName値の設定.
    // * @param name CellName値
    // */
    /**
     * This method sets the CellName value.
     * @param name CellName value
     */
    void setCellName(String name) {
        this.authCellUrl = name;
    }

    // /**
    // * Box Schemaの取得.
    // * @return Schema名
    // */
    /**
     * This method gets the Box Schema value.
     * @return BoxSchema value
     */
    String getBoxSchema() {
        return this.boxSchema;
    }

    // /**
    // * Box Schemaの設定.
    // * @param uri Box Schema名
    // */
    /**
     * This method sets the Box Schema value.
     * @param uri Box Schema value
     */
    void setBoxSchema(String uri) {
        this.boxSchema = uri;
    }

    // /**
    // * Box Nameを取得.
    // * @return Box Name
    // */
    /**
     * This method returns theBox Name value.
     * @return Box Name
     */
    String getBoxName() {
        return this.boxName;
    }

    // /**
    // * Box Nameの設定.
    // * @param value Box Name
    // */
    /**
     * This method sets the Box Name value.
     * @param value Box Name
     */
    void setBoxName(String value) {
        this.boxName = value;
    }

    // /**
    // * 基底URLを設定.
    // * @return URL文字列
    // */
    /**
     * This method gets the base URL value.
     * @return BaseURL value
     */
    String getBaseUrl() {
        return this.baseUrl;
    }

    // /**
    // * 基底URLを取得.
    // * @param value URL文字列
    // */
    /**
     * This method sets the base URL value.
     * @param value BaseURL value
     */
    void setBaseUrl(String value) {
        this.baseUrl = value;
    }

    /**
     * This method returns the userId.
     * @return the userId
     */
    String getUserId() {
        return userId;
    }

    /**
     * This method sets the userId.
     * @param userId the userId to set
     */
    void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * This method retruns the password.
     * @return the password
     */
    String getPassword() {
        return password;
    }

    /**
     * This method sets the password.
     * @param password the password to set
     */
    void setPassword(String password) {
        this.password = password;
    }

    /**
     * This method returns the schema.
     * @return the schema
     */
    String getSchema() {
        return schema;
    }

    /**
     * This method sets the schema.
     * @param schema the schema to set
     */
    void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * This method returns the target cell name.
     * @return the targetCellName
     */
    String getTargetCellName() {
        return targetCellName;
    }

    /**
     * This method sets the target cell name.
     * @param targetCellName the targetCellName to set
     */
    void setTargetCellName(String targetCellName) {
        this.targetCellName = targetCellName;
    }

    /**
     * This method sets the access type.
     * @param accessType the accessType to set
     */
    public void setAccessType(String accessType) {
        this.accessType = accessType;
    }

    // /**
    // * "self","client"等のタイプを返却.
    // * @return タイプ
    // */
    /**
     * This method gets the access type "self", "client", etc.
     * @return access type
     */
    String getAccessType() {
        return this.accessType;
    }

    /**
     * This method sets the transformer cell token.
     * @param transCellToken the trancCellToken to set
     */
    void setTransCellToken(String trancCellToken) {
        this.transCellToken = trancCellToken;
    }

    /**
     * This method gets the transformer cell token.
     * @param the transCellToken
     */
    String getTransCellToken() {
        return this.transCellToken;
    }

    /**
     * This method sets the transformer cell refresh token.
     * @param trancCellRefreshToken the trancCellRefreshToken to set
     */
    void setTransCellRefreshToken(String trancCellRefreshToken) {
        this.transCellRefreshToken = trancCellRefreshToken;
    }

    /**
     * This method gets the transformer cell refresh token.
     * @param the trancCellRefreshToken
     */
    String getTransCellRefreshToken() {
        return this.transCellRefreshToken;
    }

    /**
     * This method sets the schema user ID.
     * @param schemaUserId the schemaUserId to set
     */
    void setSchemaUserId(String schemaUserId) {
        this.schemaUserId = schemaUserId;
    }

    /**
     * This method gets the schema user ID.
     * @param the schemaUserId
     */
    String getSchemaUserId() {
        return this.schemaUserId;
    }

    /**
     * This method sets the schema password.
     * @param schemaPassword the schemaPassword to set
     */
    void setSchemaPassword(String schemaPassword) {
        this.schemaPassword = schemaPassword;
    }

    /**
     * This method gets the schema password.
     * @param the schemaPassword
     */
    String getSchemaPassword() {
        return this.schemaPassword;
    }

    // /**
    // * サーバーのレスポンスから取得したレスポンスヘッダを設定.
    // * @param headers 設定するヘッダ
    // */
    /**
     * This method sets the response headers that are retrieved from the server response.
     * @param headers Response headers set
     */
    public void setResHeaders(Header[] headers) {
        for (Header header : headers) {
            this.resHeaders.put(header.getName(), header.getValue());
            if (header.getName().equals(DcContext.DC_VERSION)) {
                this.context.setServerVersion(header.getValue());
            }
        }
    }

    // /**
    // * レスポンスヘッダの取得.
    // * @return レスポンスヘッダの一覧
    // */
    /**
     * This method gets the response headers retrieved from the server response.
     * @return Response Headers
     */
    public HashMap<String, String> getResHeaders() {
        return this.resHeaders;
    }

    // /**
    // * 認証を行う.
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs authentication of user credentails based on token type etc.
     * @throws DaoException Exception thrown
     */
    protected void certification() throws DaoException {

        // アクセスタイプがselfかクライアントの場合は、認証処理は行わない
        /** If the access type of client or self, then authentication process is not performed. */
        if (this.accessType.equals(Accessor.KEY_CLIENT) || this.accessType.equals(Accessor.KEY_SELF)
                || this.accessType.equals(Accessor.KEY_TOKEN)) {
            return;
        }

        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this);
        // 認証するurlを作成する
        /** Create a url to authenticate. */
        String authUrl = createCertificatUrl();

        // 認証するためのリクエストボディを作る
        /** Make a request body to authenticate. */
        StringBuilder requestBody = new StringBuilder();
        if (this.transCellToken != null) {
            // トランスセルトークン認証
            /** Transformer cell token authentication. */
            requestBody.append("grant_type=urn:ietf:params:oauth:grant-type:saml2-bearer&assertion=");
            requestBody.append(this.transCellToken);
        } else if (this.transCellRefreshToken != null) {
            // リフレッシュトークン認証
            /** Refresh token authentication. */
            requestBody.append("grant_type=refresh_token&refresh_token=");
            requestBody.append(this.transCellRefreshToken);
        } else if (userId != null) {
            // パスワード認証
            /** Password authentication. */
            requestBody.append("grant_type=password&username=");
            requestBody.append(this.userId);
            requestBody.append("&password=");
            requestBody.append(this.password);
        }

        // targetのURLを作る
        /** Create Target URL. */
        if (this.targetCellName != null) {
            requestBody.append("&dc_target=");
            if (UrlUtils.isUrl(this.targetCellName)) {
                requestBody.append(this.targetCellName);
            } else {
                requestBody.append(UrlUtils.append(baseUrl, this.targetCellName));
            }
        }

        // スキーマ付き認証のためにスキーマ情報を付加する
        /** Add the schema information for authentication schema. */
        if (this.schemaUserId != null && this.schemaPassword != null) {
            // スキーマ認証
            /** Authentication schema. */
            StringBuilder schemaRequestBody = new StringBuilder();
            schemaRequestBody.append("grant_type=password&username=");
            schemaRequestBody.append(this.schemaUserId);
            schemaRequestBody.append("&password=");
            schemaRequestBody.append(this.schemaPassword);
            schemaRequestBody.append("&dc_target=");
            schemaRequestBody.append(authUrl);
            // Urlでない場合は、BaseURLにスキーマ名を足す
            /** If this is not the Url, add the schema name to BaseURL. */
            if (!UrlUtils.isUrl(this.schema)) {
                this.schema = UrlUtils.append(baseUrl, this.schema);
            }

            if (!this.schema.endsWith("/")) {
                this.schema += "/";
            }

            DcResponse res = rest.post(UrlUtils.append(this.schema, "__auth"), schemaRequestBody.toString(),
                    RestAdapter.CONTENT_FORMURLENCODE, false);
            this.schemaAuth = res.bodyAsJson();

            requestBody.append("&client_id=");
            requestBody.append(this.schema);
            requestBody.append("&client_secret=");
            requestBody.append((String) this.schemaAuth.get("access_token"));
        }
        if (owner) {
            requestBody.append("&dc_owner=true");
        }
        // 認証してトークンを保持する
        /** To hold the token to authenticate. */
        DcResponse res = rest.post(UrlUtils.append(authUrl, "__auth"), requestBody.toString(),
                RestAdapter.CONTENT_FORMURLENCODE, false);
        JSONObject json = res.bodyAsJson();
        this.accessToken = (String) json.get("access_token");
        this.expiresIn = (Number) json.get("expires_in");
        this.refreshToken = (String) json.get("refresh_token");
        this.refreshExpiresIn = (Number) json.get("refresh_token_expires_in");
        this.tokenType = (String) json.get("token_type");
    }

    // /**
    // * レスポンスボディをXMLで取得.
    // * @return XML DOMオブジェクト
    // */
    /**
     * This method returns the transformer cell token as XML.
     * @return XML DOM object
     */
    private Document trancCellTokenAsXml() throws DaoException {
        String saml;
        try {
            saml = new String(Base64.decodeBase64(this.transCellToken), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw DaoException.create(e.getMessage(), HttpStatus.SC_BAD_REQUEST);
        }
        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = null;
        InputStream is = new ByteArrayInputStream(saml.getBytes());
        try {
            document = builder.parse(is);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return document;
    }

    // /**
    // * 認証先Cellのburlを作成する.
    // * @return 認証先Cellのurl
    // * @throws DaoException
    // */
    /**
     * This method creates a url of authentication destination Cell.
     * @return authentication destination Cell URL
     * @throws DaoException Exception thrown
     */
    private String createCertificatUrl() throws DaoException {
        // 認証するurlを作成する
        /** Create a url to authenticate. */
        String authUrl;
        if (this.transCellToken != null) {
            // トークンから接続先urlを取得する
            /** Get the connection destination url from the token. */
            Document doc = trancCellTokenAsXml();
            authUrl = doc.getElementsByTagName("Audience").item(0).getFirstChild().getNodeValue();
        } else if (UrlUtils.isUrl(this.authCellUrl)) {
            authUrl = this.authCellUrl;
        } else {
            authUrl = UrlUtils.append(baseUrl, this.authCellUrl);
        }
        return authUrl;
    }

    // /**
    // * Cellへのグローバルトークンを取得する.
    // * @return トークン
    // */
    /**
     * This method gets the global token of the cell.
     * @return token Empty string
     */
    protected String loadGlobalToken() {
        return "";
    }

    // /**
    // * Boxへのローカルトークンを取得する.
    // * @return トークン
    // */
    /**
     * This method gets the local token for the box.
     * @return token Empty string
     */
    protected String loadClientToken() {
        return "";
    }

}
