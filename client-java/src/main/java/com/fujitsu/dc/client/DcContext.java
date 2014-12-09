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

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.net.URLCodec;
import org.apache.http.client.HttpClient;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.fujitsu.dc.client.http.CacheMap;

///**
// * personium.ioコンテキスト.
// */
/**
 * It creates a new object of DcContext. This class is the Data cloud context used as the package for all the files.
 */
public class DcContext {
    // /** 基底URL. */
    /** Base URL. */
    private String baseUrl;
    // /** 現在のCellName. */
    /** Cell Name. */
    private String cellName;
    // /** 現在のBoxのDataSchemaURI. */
    /** DataSchemaURI of the current Box. */
    private String schema;
    // /** 現在のBoxName. */
    /** Box Name. */
    private String boxName;
    // /** クライアントトークン. */
    /** Client Token. */
    private String clientToken;
    // /** サーバーのレスポンスから取得したAPIのバージョン. */
    /** Version of the API that is obtained from the server response. */
    private String serverVersion = null;

    // /** デフォルトリクエストヘッダ. */
    /** Default Headers. */
    HashMap<String, String> defaultHeaders = new HashMap<String, String>();

    // /**
    // * バージョン情報を指定するヘッダ.
    // */
    /** Header that specifies the version information. */
    public static final String DC_VERSION = "X-Dc-Version";

    // /**
    // * クライアントトークンのgetter.
    // * @return クライアントトークン
    // */
    /**
     * This method returns the client token value.
     * @return clientToken
     */
    public String getClientToken() {
        return clientToken;
    }

    // /** カスタマイズ可能な情報を管理するオブジェクト. */
    /** DaoConfig object managing a customizable information. */
    private DaoConfig config;
    // /** キャッシュ用クラス. */
    /** Cache for class. */
    private CacheMap cacheMap;
    // /** 動作対象プラットフォーム. */
    /** Operating platforms. */
    private static String platform = "insecure";

    // /**
    // * コンストラクタ.
    // * @param url 基底URL
    // * @param name Cell Name
    // * @param boxSchema Box DataSchemaURI
    // * @param bName Box Name
    // */
    /**
     * This is the parameterized constructor with four arguments. It initializes various class variables.
     * @param url Path URL
     * @param name Cell Name
     * @param boxSchema Box DataSchemaURI
     * @param bName Box Name
     */
    public DcContext(final String url, final String name, final String boxSchema, String bName) {
        this.baseUrl = url;
        String c = this.baseUrl.substring(this.baseUrl.length() - 1);
        if (!c.equals("/")) {
            this.baseUrl += "/";
        }
        this.cellName = name;
        this.schema = boxSchema;
        this.boxName = bName;
        if (this.cellName == null) {
            this.cellName = "";
        }
        if (this.schema == null) {
            this.schema = "";
        }
        if (this.boxName == null) {
            this.boxName = "";
        }
        this.config = new DaoConfig();
        this.cacheMap = new CacheMap();
    }

    // /**
    // * CellのIDを取得.
    // * @return CellのID値
    // */
    /**
     * This method gets the cell name.
     * @return Cell Name
     */
    public final String getCellName() {
        return cellName;
    }

    // /**
    // * CellのIDを設定.
    // * @param value CellのID値
    // */
    /**
     * This method sets the cell name.
     * @param value Cell Name
     */
    public final void setCellName(final String value) {
        this.cellName = value;
    }

    // /**
    // * CellのURLを取得.
    // * @return CellのURL
    // */
    /**
     * This method gets the cell URL.
     * @return CellURL value
     */
    public final String getCellUrl() {
        return this.baseUrl + this.cellName + "/";
    }

    // /**
    // * BoxのDataSchemaURIの取得.
    // * @return BoxのDataSchemaURI値
    // */
    /**
     * This method gets the DataSchemaURI of the current box.
     * @return DataSchemaURI of Current Box
     */
    public final String getBoxSchema() {
        return schema;
    }

    // /**
    // * BoxのDataSchemaURIの設定.
    // * @param value BoxのDataSchemaURI値
    // */
    /**
     * This method sets the DataSchemaURI of the current box.
     * @param value DataSchemaURI of Current Box
     */
    public final void setBoxSchema(final String value) {
        this.schema = value;
    }

    // /**
    // * Box Nameの取得.
    // * @return Box Nmae
    // */
    /**
     * This method gets the Box Name.
     * @return Box Name value
     */
    public String getBoxName() {
        return this.boxName;
    }

    // /**
    // * Box Nameの設定.
    // * @param value Box Name
    // */
    /**
     * This method sets the Box Name.
     * @param value Box Name value
     */
    public void setBoxName(String value) {
        this.boxName = value;
    }

    // /**
    // * 基底URLの取得.
    // * @return URL文字列
    // */
    /**
     * This method gets the BaseURL value.
     * @return BaseURL value
     */
    public final String getBaseUrl() {
        return this.baseUrl;
    }

    // /**
    // * クライアントトークの設定.
    // * @param value クライアントトークン
    // */
    /**
     * This method sets the client token value.
     * @param value Client Token
     */
    public final void setClientToken(final String value) {
        this.clientToken = value;
        if (this.clientToken == null) {
            this.clientToken = "";
        }
    }

    // /**
    // * HTTPのタイムアウト値を設定.
    // * @param value タイムアウト値
    // */
    /**
     * This method sets the HTTP Connection timeout value.
     * @param value HTTP Connection timeout
     */
    public final void setConnectionTimeout(final int value) {
        config.setConnectionTimeout(value);
    }

    // /**
    // * Chunked値を設定.
    // * @param value Chunked値
    // */
    /**
     * This method sets the Chunked value.
     * @param value Chunked
     */
    public final void setChunked(final Boolean value) {
        config.setChunked(value);
    }

    // /**
    // * DCの利用バージョンを設定.
    // * @param value バージョン
    // */
    /**
     * This method sets the DcVersion value.
     * @param value DcVersion
     */
    public final void setDcVersion(final String value) {
        defaultHeaders.put(DC_VERSION, value);
        // リクエストを処理したcoreのバージョン情報を初期化
        /** Initialize the version information of the core that processed the request. */
        this.serverVersion = null;
    }

    // /**
    // * DCの利用バージョンを取得.
    // * @return personium.ioバージョン
    // */
    /**
     * This method gets the DcVersion value.
     * @return DcVersion value
     */
    public final String getDcVersion() {
        return defaultHeaders.get(DC_VERSION);
    }

    // /**
    // * 非同期通信フラグを設定.
    // * @param value 非同期フラグ
    // */
    /**
     * This method sets the Threadable value.
     * @param value Threadable
     */
    public final void setThreadable(final Boolean value) {
        config.setThreadable(value);
    }

    // /**
    // * 非同期通信フラグを取得.
    // * @return 非同期通信フラグ
    // */
    /**
     * This method gets the Threadable value.
     * @return Threadable value
     */
    public final Boolean getThreadable() {
        return config.getThreadable();
    }

    // /**
    // * 動作対象プラットフォームをセット.
    // * @param value プラットフォーム名
    // */
    /**
     * This method sets the Platform value.
     * @param value Platform
     */
    public static void setPlatform(String value) {
        platform = value;
    }

    // /**
    // * 動作対象プラットフォームを取得.
    // * @return プラットフォーム名
    // */
    /**
     * This method gets the Platform value.
     * @return Platform value
     */
    public static String getPlatform() {
        return platform;
    }

    // /**
    // * キャッシュ用オブジェクト(CacheMap)の設定.
    // * @param value CacheMapオブジェクト
    // */
    /**
     * This method sets cache of objects (CacheMap).
     * @param value CacheMap object
     */
    public final void setCacheMap(final CacheMap value) {
        this.cacheMap = value;
    }

    // /**
    // * キャッシュ用オブジェクト(CacheMap)の取得.
    // * @return CacheMapオブジェクト
    // */
    /**
     * This method gets cache of objects (CacheMap).
     * @return CacheMap object
     */
    public final CacheMap getCacheMap() {
        return this.cacheMap;
    }

    // /**
    // * DaoConfigオブジェクトの取得.
    // * @return DaoConfigオブジェクト
    // */
    /**
     * This method gets DaoConfig object.
     * @return DaoConfig object
     */
    public final DaoConfig getDaoConfig() {
        return config;
    }

    // /**
    // * HttpClientオブジェクトを設定.
    // * @param value HttpClientオブジェクト
    // */
    /**
     * This method sets the HttpClien object.
     * @param value HttpClient object
     */
    public final void setHttpClient(final HttpClient value) {
        config.setHttpClient(value);
    }

    // /**
    // * サーバーのレスポンスから取得したAPIのバージョンを取得.
    // * @return APIのバージョン リクエストを送信するまではnullを返す。 setDcVersionメソッドを利用してバージョンを設定するとnullに初期化される。
    // */
    /**
     * This method gets the version of the API that is retrieved from the server response. Returns null until you send a
     * request.
     * @return serverVersion, initialized to null if you set the version by using the method setDcVersion.
     */
    public final String getServerVersion() {
        return this.serverVersion;
    }

    // /**
    // * サーバーのレスポンスから取得したAPIのバージョンを設定.
    // * @param version リクエストを処理したAPIのバージョン
    // */
    /**
     * This method sets the version of the API that is retrieved from the server response.
     * @param version Version of the API that processed the request
     */
    void setServerVersion(String version) {
        this.serverVersion = version;
    }

    // /**
    // * アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
    // * @param cellUrl 認証先Cell
    // * @param userId ユーザID
    // * @param password ユーザパスワード
    // * @return 生成したAccessorインスタンス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method generates a accessor. Utilizes token in the request header, to generate an accessor.
     * @param cellUrl authentication destination Cell
     * @param userId User ID
     * @param password User Password
     * @return Accessor that is generated
     * @throws DaoException Exception thrown
     */
    public final Accessor asAccount(String cellUrl, String userId, String password) throws DaoException {
        return this.getAccessorWithAccount(cellUrl, userId, password);
    }

    // /**
    // * アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
    // * @param cellUrl 認証先Cell
    // * @param userId ユーザID
    // * @param password ユーザパスワード
    // * @return 生成したAccessorインスタンス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method generates a accessor. Utilizes token in the request header, to generate an accessor.
     * @param cellUrl authentication destination Cell
     * @param userId User ID
     * @param password User Password
     * @return Accessor that is generated
     * @throws DaoException Exception thrown
     */
    public final Accessor getAccessorWithAccount(String cellUrl, String userId, String password) throws DaoException {
        Accessor as = new Accessor(this);
        if (!as.getCellName().equals(cellUrl)) {
            as.setBoxName("");
        }
        as.setCellName(cellUrl);
        as.setUserId(userId);
        as.setPassword(password);
        as.setDefaultHeaders(this.defaultHeaders);
        return as;
    }

    // /**
    // * アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
    // * @param cellUrl 認証先Cell
    // * @param token トランスセルアクセストークン
    // * @return 生成したAccessorインスタンス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method generates a accessor. Utilizes transformer cell token in the request header, to generate an accessor.
     * @param cellUrl authentication destination Cell
     * @param token Transformer cell access token
     * @return Accessor that is generated
     * @throws DaoException Exception thrown
     */
    public final Accessor getAccessorWithTransCellToken(String cellUrl, String token) throws DaoException {
        Accessor as = new Accessor(this);
        if (!as.getCellName().equals(cellUrl)) {
            as.setBoxName("");
        }
        as.setCellName(cellUrl);
        as.setTransCellToken(token);
        as.setDefaultHeaders(this.defaultHeaders);
        return as;
    }

    // /**
    // * アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
    // * @param token ストークン
    // * @return 生成したAccessorインスタンス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method generates a accessor. Utilizes token in the request header, to generate an accessor.
     * @param token value
     * @return Accessor that is generated
     * @throws DaoException Exception thrown
     */
    public final Accessor withToken(String token) throws DaoException {
        Accessor as = new Accessor(this);
        as.setAccessType(Accessor.KEY_TOKEN);
        as.setAccessToken(token);
        as.setDefaultHeaders(this.defaultHeaders);
        return as;
    }

    // /**
    // * アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
    // * @param cellUrl 認証先Cell
    // * @param token リフレッシュトークン
    // * @return 生成したAccessorインスタンス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method generates a accessor. Utilizes refresh cell token in the request header, to generate an accessor.
     * @param cellUrl authentication destination Cell
     * @param token Refresh Token
     * @return Accessor that is generated
     * @throws DaoException Exception thrown
     */
    public final Accessor getAccessorWithRefreshToken(String cellUrl, String token) throws DaoException {
        Accessor as = new Accessor(this);
        if (!as.getCellName().equals(cellUrl)) {
            as.setBoxName("");
        }
        as.setCellName(cellUrl);
        as.setTransCellRefreshToken(token);
        as.setDefaultHeaders(this.defaultHeaders);
        return as;
    }

    // /**
    // * アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
    // * @param cellUrl 認証先Cell
    // * @param userId ユーザID
    // * @param password ユーザパスワード
    // * @param schemaUrl スキーマセルurl
    // * @param schemaUserId スキーマセルユーザID
    // * @param schemaPassword スキーマセルユーザパスワード
    // * @return 生成したAccessorインスタンス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method generates a accessor. Utilizes schema authentication to generate an accessor.
     * @param cellUrl authentication destination Cell
     * @param userId User ID
     * @param password User Password
     * @param schemaUrl Schema Cell URL
     * @param schemaUserId Schema Cell User ID
     * @param schemaPassword Cell Schema User Password
     * @return Accessor that is generated
     * @throws DaoException Exception thrown
     */
    public final Accessor asAccountWithSchemaAuthn(String cellUrl,
            String userId,
            String password,
            String schemaUrl,
            String schemaUserId,
            String schemaPassword) throws DaoException {
        return this.getAccessorWithAccountAndSchemaAuthn(cellUrl, userId, password, schemaUrl, schemaUserId,
                schemaPassword);
    }

    // /**
    // * アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
    // * @param cellUrl 認証先Cell
    // * @param userId ユーザID
    // * @param password ユーザパスワード
    // * @param schemaUrl スキーマセルurl
    // * @param schemaUserId スキーマセルユーザID
    // * @param schemaPassword スキーマセルユーザパスワード
    // * @return 生成したAccessorインスタンス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method generates a accessor. Utilizes account and schema authentication to generate an accessor.
     * @param cellUrl authentication destination Cell
     * @param userId User ID
     * @param password User Password
     * @param schemaUrl Schema Cell URL
     * @param schemaUserId Schema Cell User ID
     * @param schemaPassword Cell Schema User Password
     * @return Accessor that is generated
     * @throws DaoException Exception thrown
     */
    public final Accessor getAccessorWithAccountAndSchemaAuthn(String cellUrl,
            String userId,
            String password,
            String schemaUrl,
            String schemaUserId,
            String schemaPassword) throws DaoException {
        Accessor as = new Accessor(this);
        if (!as.getCellName().equals(cellUrl)) {
            as.setBoxName("");
        }
        as.setCellName(cellUrl);
        as.setUserId(userId);
        as.setPassword(password);
        as.setSchema(schemaUrl);
        as.setSchemaUserId(schemaUserId);
        as.setSchemaPassword(schemaPassword);
        as.setDefaultHeaders(this.defaultHeaders);
        return as;
    }

    // /**
    // * アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
    // * @param cellUrl 認証先Cell
    // * @param token トランスセルトークン
    // * @param schemaUrl スキーマセルurl
    // * @param schemaUserId スキーマセルユーザID
    // * @param schemaPassword スキーマセルユーザパスワード
    // * @return 生成したAccessorインスタンス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method generates a accessor. Utilizes transformer cell access token and schema authentication to generate an
     * accessor.
     * @param cellUrl authentication destination Cell
     * @param token Transformer Cell Token
     * @param schemaUrl Schema Cell URL
     * @param schemaUserId Schema Cell User ID
     * @param schemaPassword Cell Schema User Password
     * @return Accessor that is generated
     * @throws DaoException Exception thrown
     */
    public final Accessor getAccessorWithTransCellTokenAndSchemaAuthn(String cellUrl,
            String token,
            String schemaUrl,
            String schemaUserId,
            String schemaPassword) throws DaoException {
        Accessor as = new Accessor(this);
        if (!as.getCellName().equals(cellUrl)) {
            as.setBoxName("");
        }
        as.setCellName(cellUrl);
        as.setTransCellToken(token);
        as.setSchema(schemaUrl);
        as.setSchemaUserId(schemaUserId);
        as.setSchemaPassword(schemaPassword);
        as.setDefaultHeaders(this.defaultHeaders);
        return as;
    }

    // /**
    // * JSONObjectオブジェクトを生成.
    // * @return 生成したJSONObjectオブジェクト
    // */
    /**
     * This method creates a new JSONObject.
     * @return JSONObject object
     */
    public final JSONObject newJson() {
        return new JSONObject();
    }

    // /**
    // * JSON文字列から、JSONObjectオブジェクトを生成.
    // * @param jsonStr JSON文字列
    // * @return 変換後のJSONObject
    // * @throws org.json.simple.parser.ParseException JSONパース例外
    // */
    /**
     * This method creates a new JSONObject object from JSON string.
     * @param jsonStr JSON string
     * @return JSONObject object
     * @throws org.json.simple.parser.ParseException ParseException
     */
    public final JSONObject newJson(final String jsonStr) throws org.json.simple.parser.ParseException {
        return (JSONObject) (new JSONParser().parse(jsonStr));
    }

    // /**
    // * TODO Java DAO の本来の機能ではないため、別のクラスに移動する必要がある.
    // * @param str デコード対象の文字列
    // * @param charset 文字コード
    // * @return デコード後の文字列
    // * @throws UnsupportedEncodingException 例外
    // * @throws DecoderException 例外
    // */
    /**
     * TODO This is not a feature of the original JAVA DAO. There is a need to move to a different class.
     * @param str String decoded
     * @param charset Character Code
     * @return Dedoded URI
     * @throws UnsupportedEncodingException exception
     * @throws DecoderException exception
     */
    public final String decodeURI(final String str, final String charset) throws UnsupportedEncodingException,
            DecoderException {
        URLCodec codec = new URLCodec();
        return codec.decode(str, charset);
    }

    // /**
    // * TODO Java DAO の本来の機能ではないため、別のクラスに移動する必要がある.
    // * @param str デコード対象の文字列
    // * @return デコード後の文字列
    // * @throws UnsupportedEncodingException 例外
    // * @throws DecoderException 例外
    // */
    /**
     * TODO This is not a feature of the original JAVA DAO. There is a need to move to a different class.
     * @param str String decoded
     * @return Dedoded URI
     * @throws UnsupportedEncodingException exception
     * @throws DecoderException exception
     */
    public final String decodeURI(final String str) throws UnsupportedEncodingException, DecoderException {
        URLCodec codec = new URLCodec();
        return codec.decode(str, "utf-8");
    }

    // /**
    // * リクエストを行う際に付加するリクエストヘッダのデフォルト値をセット.
    // * @param key ヘッダ名
    // * @param value 値
    // */
    /**
     * This method adds the key value pair to default header.
     * @param key Key Value
     * @param value Value
     */
    public final void setDefaultHeader(String key, String value) {
        this.defaultHeaders.put(key, value);
    }

    // /**
    // * リクエストを行う際に付加するリクエストヘッダのデフォルト値を削除.
    // * @param key ヘッダ名
    // */
    /**
     * This method removes the specified key value pair from the default header list.
     * @param key Key value
     */
    public final void removeDefaultHeader(String key) {
        this.defaultHeaders.remove(key);
    }
}
