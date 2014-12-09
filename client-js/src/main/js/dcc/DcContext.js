/*
=================================================================
personium.io
Copyright 2014 FUJITSU LIMITED

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
=================================================================
 */
/*global dcc:false */

///**
//* @class データクラウドコンテキスト.
//* @constructor
//* @param url 基底URL (string)
//* @param name Cell Name (string)
//* @param boxSchema Box DataSchemaURI (string)
//* @param bName Box Name (string)
//*/
/**
 * It creates a new object dcc.DcContext.
 * @class This class is the Data cloud context used as the package for all the files.
 * @constructor
 * @param {String} Base URL
 * @param {String} Cell Name
 * @param {String} Box DataSchemaURI
 * @param {String} Box Name
 */
dcc.DcContext = function(url, name, boxSchema, bName) {
  this.initializeProperties(this, url, name, boxSchema, bName);
};

if (typeof exports === "undefined") {
  exports = {};
}
exports.DcContext = dcc.DcContext;

///**
//* バージョン情報を指定するヘッダ.
//*/
/** Header that specifies the version information. */
var DC_VERSION = "X-Dc-Version";

///**
//* プロパティを初期化する.
//* @param {dcc.DcContext} self
//* @param {?} url
//* @param {?} name
//* @param {?} boxSchema
//* @param {?} bName
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.DcContext} self
 * @param {String} url
 * @param {String} name
 * @param {String} boxSchema
 * @param {String} bName
 */
dcc.DcContext.prototype.initializeProperties = function(self, url, name, boxSchema, bName) {
///** 基底URL. */
  /** {String} Base URL. */
  self.baseUrl = url;
  if (self.baseUrl === undefined || self.baseUrl === null) {
    self.baseUrl = "";
  }
  if (self.baseUrl !== "" && !self.baseUrl.endsWith("/")) {
    self.baseUrl += "/";
  }

///** 現在のCellName. */
  /** {String} Current cell name. */
  self.cellName = name;
  if (self.cellName === undefined || self.cellName === null) {
    self.cellName = "";
  }

///** 現在のBoxのDataSchemaURI. */
  /** {String} DataSchemaURI of current Box. */
  self.schema = boxSchema;
  if (self.schema === undefined || self.schema === null) {
    self.schema = "";
  }

///** 現在のBoxName. */
  /** {String} Current box name. */
  self.boxName = bName;
  if (self.boxName === undefined || self.boxName === null) {
    self.boxName = "";
  }

///** クライアントトークン. */
  /** {String} Client token. */
  self.clientToken = "";

///** デフォルトリクエストヘッダ. */
  /** {Object} Default request header. */
//HashMap<String, String> defaultHeaders = new HashMap<String, String>();
  self.defaultHeaders = {};

///** 動作対象プラットフォーム. */
  /** {String} Operating platforms. */
  self.platform = "insecure";

///** カスタマイズ可能な情報を管理するオブジェクト. */
  /** {dcc.ClientConfig} Object that manages a customizable information. */
  self.config = new dcc.ClientConfig();

///** キャッシュ用クラス. */
//this.cacheMap = new CacheMap();
};

///**
//* 基底URLの取得.
//* @return {String} URL文字列 (string)
//*/
/**
 * This method gets the base URL.
 * @return {String} Base URL (string)
 */
dcc.DcContext.prototype.getBaseUrl = function() {
  return this.baseUrl;
};

///**
//* 基底URLを設定する.
//* @param {String} URL文字列 (string)
//*/
/**
 * This method sets the base URL.
 * @param {String} Base URL (string)
 */
dcc.DcContext.prototype.setBaseUrl = function(value) {
  if (typeof value !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.baseUrl = value;
};

///**
//* CellのIDを取得.
//* @return {String} CellのID値 (string)
//*/
/**
 * This method gets the ID/name of the Cell.
 * @return {String} Cell name(string)
 */
dcc.DcContext.prototype.getCellName = function() {
  return this.cellName;
};

///**
//* CellのIDを設定.
//* @param {String} value CellのID値 (string)
//*/
/**
 * This method sets the ID/name of the Cell.
 * @param {String} value Cell name (string)
 */
dcc.DcContext.prototype.setCellName = function(value) {
  if (typeof value !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.cellName = value;
};

///**
//* BoxのDataSchemaURIの取得.
//* @return {String} BoxのDataSchemaURI値 (string)
//*/
/**
 * This method gets the Box DataSchemaURI.
 * @return {String} Box DataSchemaURI (string)
 */
dcc.DcContext.prototype.getBoxSchema = function() {
  return this.schema;
};

///**
//* BoxのDataSchemaURIの設定.
//* @param {String} value BoxのDataSchemaURI値 (string)
//*/
/**
 * This method sets the Box DataSchemaURI.
 * @param {String} value Box DataSchemaURI (string)
 */
dcc.DcContext.prototype.setBoxSchema = function(value) {
  if (typeof value !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.schema = value;
};

///**
//* Box Nameの取得.
//* @return {String} Box Nmae (string)
//*/
/**
 * This method gets the Box Name.
 * @return {String} Box Nmae (string)
 */
dcc.DcContext.prototype.getBoxName = function() {
  return this.boxName;
};

///**
//* Box Nameの設定.
//* @param {String} value Box Name (string)
//*/
/**
 * This method sets the Box Name.
 * @param {String} value Box Name (string)
 */
dcc.DcContext.prototype.setBoxName = function(value) {
  if (typeof value !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.boxName = value;
};

///**
//* CellのURLを取得.
//* @return {String} CellのURL
//*/
/**
 * This method gets the Cell URL.
 * @return {String} Cell URL
 */
dcc.DcContext.prototype.getCellUrl = function() {
  return this.baseUrl + this.cellName + "/";
};

///**
//* クライアントトークンを取得する.
//* @return {String} クライアントトークン (string)
//*/
/**
 * This method acquires the client access token.
 * @return {String} access token (string)
 */
dcc.DcContext.prototype.getClientToken = function() {
  return this.clientToken;
};

///**
//* クライアントトークンを設定する.
//* @param {String} value クライアントトークン (string)
//*/
/**
 * This method sets the client access token.
 * @param {String} value access token (string)
 */
dcc.DcContext.prototype.setClientToken = function(value) {
  if (typeof value !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.clientToken = value;
  if (this.clientToken === null) {
    this.clientToken = "";
  }
};

///**
//* リクエストを行う際に付加するリクエストヘッダのデフォルト値をセット.
//* @param {String} key ヘッダ名
//* @param {String} value 値
//*/
/**
 * This method sets the default value of the request header to
 *  be added when making the request.
 * @param {String} key Header name
 * @param {String} value value
 */
//public final void setDefaultHeader(String key, String value) {
dcc.DcContext.prototype.setDefaultHeader = function(key, value) {
  this.defaultHeaders[key] = value;
};

///**
//* リクエストを行う際に付加するリクエストヘッダのデフォルト値を削除.
//* @param {String} key ヘッダ名
//*/
/**
 * This method removes the default value of the request
 *  header to be added when making the request.
 * @param {String} key Header name
 */
//public final void removeDefaultHeader(String key) {
dcc.DcContext.prototype.removeDefaultHeader = function(key) {
  delete this.defaultHeaders[key];
};

///**
//* DCの利用バージョンを設定.
//* @param {?} value バージョン
//*/
/**
 * This method sets the Dc Version.
 * @param {String} value Version
 */
//public final void setDcVersion(final String value) {
dcc.DcContext.prototype.setDcVersion = function(value) {
  this.setDefaultHeader(DC_VERSION,value);
};

///**
//* DCの利用バージョンを取得.
//* @return {?} データクラウドバージョン
//*/
/**
 * This method gets the Dc Version.
 * @return {String} value Version
 */
//public final String getDcVersion() {
dcc.DcContext.prototype.getDcVersion = function() {
  return this.defaultHeaders[DC_VERSION];
};

///**
//* 動作対象プラットフォームを取得.
//* @return {String} プラットフォーム名 (string)
//*/
/**
 * This method gets the operating platforms.
 * @return {String} Platform name (string)
 */
dcc.DcContext.prototype.getPlatform = function() {
  return this.platform;
};

///**
//* 動作対象プラットフォームをセット.
//* @param {String} value プラットフォーム名 (string)
//*/
/**
 * This method sets the operating platforms.
 * @param {String} value Platform name (string)
 */
dcc.DcContext.prototype.setPlatform = function(value) {
  if (typeof value !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.platform = value;
};

///**
//* キャッシュ用オブジェクト(CacheMap)の設定.
//* @param value CacheMapオブジェクト
//*/
////public final void setCacheMap(final CacheMap value) {
//dcc.DcContext.prototype.setCacheMap = function() {
//this.cacheMap = value;
//};

///**
//* キャッシュ用オブジェクト(CacheMap)の取得.
//* @return CacheMapオブジェクト
//*/
////public final CacheMap getCacheMap() {
//dcc.DcContext.prototype.getCacheMap = function() {
//return this.cacheMap;
//};

///**
//* ClientConfigオブジェクトの取得.
//* @return {dcc.ClientConfig} ClientConfigオブジェクト
//*/
/**
 * This method acquires the ClientConfig object.
 * @return {dcc.ClientConfig} ClientConfig object
 */
//public final ClientConfig getClientConfig() {
dcc.DcContext.prototype.getClientConfig = function() {
  return this.config;
};

///**
//* HTTPのタイムアウト値を設定.
//* @param {Number} value タイムアウト値
//*/
/**
 * This method sets the timeout value for HTTP.
 * @param {Number} value Time-out value
 */
//public final void setConnectionTimeout(final int value) {
dcc.DcContext.prototype.setConnectionTimeout = function(value) {
  this.config.setConnectionTimeout(value);
};

///**
//* Chunked値を設定.
//* @param {boolean} value Chunked値
//*/
/**
 * This method sets the Chunked value.
 * @param {boolean} value Chunked value
 */
//public final void setChunked(final Boolean value) {
dcc.DcContext.prototype.setChunked = function(value) {
  this.config.setChunked(value);
};

///**
//* 非同期通信フラグを設定.
//* @param {?} value 非同期フラグ
//*/
/**
 * This method sets the asynchronous communication flag.
 * @param {Boolean} value Asynchronous flag
 */
//public final void setThreadable(final Boolean value) {
dcc.DcContext.prototype.setAsync = function(value) {
  this.config.setAsync(value);
};

///**
//* 非同期通信フラグを取得.
//* @return {?} 非同期通信フラグ
//*/
/**
 * This method gets the asynchronous communication flag.
 * @return {Boolean} value Asynchronous flag
 */
//public final Boolean getThreadable() {
dcc.DcContext.prototype.getAsync = function() {
  return this.config.getAsync();
};

///**
//* HttpClientオブジェクトを設定.
//* @param value HttpClientオブジェクト
//*/
////public final void setHttpClient(final HttpClient value) {
//dcc.DcContext.prototype.setHttpClient = function() {
//this.config.setHttpClient(value);
//};

///**
//* アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
//* @param {String} cellUrl 認証先Cell (string)
//* @param {String} userId ユーザID (string)
//* @param {String} password ユーザパスワード (string)
//* @return {dcc.Accessor} 生成したAccessorインスタンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method generates accessor and utilizes token of the request header.
 * @param {String} cellUrl Cell URL (string)
 * @param {String} userId UserID (string)
 * @param {String} password Password (string)
 * @param {Object} options json object having schemaUrl, schemaId, schemaPwd
 * @return {dcc.Accessor} Accessor object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.DcContext.prototype.asAccount = function(cellUrl, userId, password, options) {
  return this.getAccessorWithAccount(cellUrl, userId, password, options);
};

///**
//* アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
//* @param {String} cellUrl 認証先Cell (string)
//* @param {String} userId ユーザID (string)
//* @param {String} password ユーザパスワード (string)
//* @return {dcc.Accessor} 生成したAccessorインスタンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method generates accessor and utilizes token of the request header.
 * @param {String} cellUrl Cell URL (string)
 * @param {String} userId UserID (string)
 * @param {String} password Password (string)
 * @param {Object} options json object having schemaUrl, schemaId, schemaPwd
 * @return {dcc.Accessor} Accessor object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.DcContext.prototype.getAccessorWithAccount = function(cellUrl, userId, password, options) {
  var as = new dcc.Accessor(this);
  if(options !== undefined){
    if(options.schemaUrl !== undefined){
      as.setSchema(options.schemaUrl);
    }
    if(options.schemaId !== undefined){
      as.setSchemaUserId(options.schemaId);
    }
    if(options.schemaPwd !== undefined){
      as.setSchemaPassword(options.schemaPwd);
    }
  }
  as.setCellName(cellUrl);
  as.setUserId(userId);
  as.setPassword(password);
  as.setDefaultHeaders(this.defaultHeaders);
  return as;
};

///**
//* アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
//* @param {String} cellUrl 認証先Cell
//* @param {String} token トランスセルアクセストークン
//* @return {dcc.Accessor} 生成したAccessorインスタンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method generates accessor and utilizes transformer
 * cell token of the request header.
 * @param {String} cellUrl Cell URL
 * @param {String} token Transformer cell access token
 * @return {dcc.Accessor} Accessor object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.DcContext.prototype.getAccessorWithTransCellToken = function(cellUrl, token) {
  var as = new dcc.Accessor(this);
  as.setCellName(cellUrl);
  as.setTransCellToken(token);
  as.setDefaultHeaders(this.defaultHeaders);
  return as;
};

///**
//* アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
//* @param {String} token トークン
//* @return {dcc.Accessor} 生成したAccessorインスタンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method generates accessor and utilizes token of the request header.
 * @param {String} token Token value
 * @return {dcc.Accessor} Accessor object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.DcContext.prototype.withToken = function(token) {
  var as = new dcc.Accessor(this);
  as.setAccessType(dcc.Accessor.ACCESSOR_KEY_TOKEN);
  as.setAccessToken(token);
  as.setDefaultHeaders(this.defaultHeaders);
  return as;
};

///**
//* アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
//* @param {String} cellUrl 認証先Cell
//* @param {String} token リフレッシュトークン
//* @return {dcc.Accessor} 生成したAccessorインスタンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method generates accessor and utilizes refresh token of the request header.
 * @param {String} cellUrl Cell URL
 * @param {String} token Refresh token
 * @return {dcc.Accessor} Accessor object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.DcContext.prototype.getAccessorWithRefreshToken = function(cellUrl, token) {
  var as = new dcc.Accessor(this);
  as.setCellName(cellUrl);
  as.setTransCellRefreshToken(token);
  as.setDefaultHeaders(this.defaultHeaders);
  return as;
};

///**
//* アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
//* @param {String} cellUrl 認証先Cell
//* @param {String} userId ユーザID
//* @param {String} password ユーザパスワード
//* @param {String} schemaUrl スキーマセルurl
//* @param {String} schemaUserId スキーマセルユーザID
//* @param {String} schemaPassword スキーマセルユーザパスワード
//* @return {dcc.Accessor} 生成したAccessorインスタンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method generates accessor with schema authentication.
 * @param {String} cellUrl Cell URL
 * @param {String} userId UserID
 * @param {String} password Password
 * @param {String} schemaUrl Schema url
 * @param {String} schemaUserId Schema UserID
 * @param {String} schemaPassword Schema password
 * @return {dcc.Accessor} Accessor object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.DcContext.prototype.asAccountWithSchemaAuthn = function(cellUrl, userId, password, schemaUrl, schemaUserId, schemaPassword) {
  return this.getAccessorWithAccountAndSchemaAuthn(cellUrl, userId, password, schemaUrl, schemaUserId,
      schemaPassword);
};

///**
//* アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
//* @param {String} cellUrl 認証先Cell
//* @param {String} userId ユーザID
//* @param {String} password ユーザパスワード
//* @param {String} schemaUrl スキーマセルurl
//* @param {String} schemaUserId スキーマセルユーザID
//* @param {String} schemaPassword スキーマセルユーザパスワード
//* @return {dcc.Accessor} 生成したAccessorインスタンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method generates accessor with account and schema authentication.
 * @param {String} cellUrl Cell URL
 * @param {String} userId userid
 * @param {String} password Password
 * @param {String} schemaUrl Schema url
 * @param {String} schemaUserId Schema User ID
 * @param {String} schemaPassword Schema Password
 * @return {dcc.Accessor} Accessor object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.DcContext.prototype.getAccessorWithAccountAndSchemaAuthn = function(cellUrl, userId, password, schemaUrl, schemaUserId, schemaPassword) {
  var as = new dcc.Accessor(this);
  as.setCellName(cellUrl);
  as.setUserId(userId);
  as.setPassword(password);
  as.setSchema(schemaUrl);
  as.setSchemaUserId(schemaUserId);
  as.setSchemaPassword(schemaPassword);
  as.setDefaultHeaders(this.defaultHeaders);
  return as;
};

///**
//* アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
//* @param {String} cellUrl 認証先Cell
//* @param {String} token トランスセルトークン
//* @param {String} schemaUrl スキーマセルurl
//* @param {String} schemaUserId スキーマセルユーザID
//* @param {String} schemaPassword スキーマセルユーザパスワード
//* @return {dcc.Accessor} 生成したAccessorインスタンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method generates accessor with transformer cell access token
 * and schema authentication.
 * @param {String} cellUrl Cell URL
 * @param {String} token Transformer cell token
 * @param {String} schemaUrl Schema url
 * @param {String} schemaUserId Schema userid
 * @param {String} schemaPassword Schema Password
 * @return {dcc.Accessor} Accessor object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.DcContext.prototype.getAccessorWithTransCellTokenAndSchemaAuthn = function(cellUrl, token, schemaUrl, schemaUserId, schemaPassword) {
  var as = new dcc.Accessor(this);
  as.setCellName(cellUrl);
  as.setTransCellToken(token);
  as.setSchema(schemaUrl);
  as.setSchemaUserId(schemaUserId);
  as.setSchemaPassword(schemaPassword);
  as.setDefaultHeaders(this.defaultHeaders);
  return as;
};

///**
//* JSONObjectオブジェクトを生成.
//* @return {?} 生成したJSONObjectオブジェクト
//*/
/**
 * This method generates JSONObject object.
 * @return {Object} JSONObject object
 */
//public final JSONObject newJson() {
dcc.DcContext.prototype.newJson = function() {
  return {};
};

///**
//* JSON文字列から、JSONObjectオブジェクトを生成.
//* @param {String} jsonStr JSON文字列
//* @return {?} 変換後のJSONObject
//* @throws org.json.simple.parser.ParseException JSONパース例外
//*/
/**
 * This method generates JSONObject object from JSON string. 
 * @param {String} jsonStr JSON string
 * @return {Object} JSONObject
 * @throws org.json.simple.parser.ParseException JSON object
 */
//public final JSONObject newJson(final String jsonStr) throws org.json.simple.parser.ParseException {
dcc.DcContext.prototype.newJson = function(jsonStr) {
  return JSON.parse(jsonStr);
};

///**
//* TODO Java DAO の本来の機能ではないため、別のクラスに移動する必要がある.
//* @param str デコード対象の文字列
//* @param charset 文字コード
//* @return デコード後の文字列
//* @throws UnsupportedEncodingException 例外
//* @throws DecoderException 例外
//*/
////public final String decodeURI(final String str, final String charset) throws UnsupportedEncodingException,
////DecoderException {
//dcc.DcContext.prototype.decodeURI = function() {
//URLCodec codec = new URLCodec();
//return codec.decode(str, charset);
//};

///**
//* TODO Java DAO の本来の機能ではないため、別のクラスに移動する必要がある.
//* @param str デコード対象の文字列
//* @return デコード後の文字列
//* @throws UnsupportedEncodingException 例外
//* @throws DecoderException 例外
//*/
////public final String decodeURI(final String str) throws UnsupportedEncodingException, DecoderException {
//dcc.DcContext.prototype.decodeURI = function() {
//URLCodec codec = new URLCodec();
//return codec.decode(str, "utf-8");
//};


//private String serviceSubject;

///**
//* サービスサブジェクトのsetter.
//* @param serviceSubject サービスサブジェクト
//*/
//public void setServiceSubject(String serviceSubject) {
//this.serviceSubject = serviceSubject;
//}

//private String schemaUrl;

///**
//* ボックスのスキーマURLのsetter.
//* @param schemaUrl ボックススキーマURL
//*/
//public void setSchemaUrl(String schemaUrl) {
//this.schemaUrl = schemaUrl;
//}

///**
//* コンストラクタ.
//* @param url 基底URL
//* @param name Cell Name
//* @param boxSchema Box DataSchemaURI
//* @param bName Box-Name
//*/
//public DcEngineDao(final String url, final String name, final String boxSchema, final String bName) {
//super(url, name, boxSchema, bName);
//}

///**
//* アクセッサを生成します. マスタトークンを利用し、アクセッサを生成します。（正式実装は セルフトークンを利用する）
//* @return 生成したAccessorインスタンス
//* @throws ClientException DAO例外
//*/
//public final Accessor asServiceSubject() throws ClientException {
////サービスサブジェクト設定が未設定
//if ("".equals(this.serviceSubject)) {
//throw ClientException.create("ServiceSubject undefined.", 0);
//}

////設定されたアカウントが、存在することをチェックする。

////トークン生成
//long issuedAt = new Date().getTime();
//AccountAccessToken localToken = new AccountAccessToken(
//issuedAt,
//AccountAccessToken.ACCESS_TOKEN_EXPIRES_HOUR * AccountAccessToken.MILLISECS_IN_AN_HOUR,
//this.getCellUrl(),
//this.serviceSubject,
//this.schemaUrl);

//Accessor as = this.withToken(localToken.toTokenString());
//as.setAccessType(Accessor.KEY_SELF);
//return as;
//}

///**
//* アクセッサを生成します. リクエストヘッダのトークンを利用し、アクセッサを生成します。
//* @return {dcc.Accessor} 生成したAccessorインスタンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method generates accessor using client access token of request header.
 * @return {dcc.Accessor} Accessor object
 * @throws {dcc.ClientException} DAO exception
 */
//public final Accessor withClientToken() throws ClientException {
dcc.DcContext.prototype.withClientToken = function() {
  return this.withToken(this.getClientToken());
};

/**
 * This method generates accessor and utilizes refresh token of the request header along with schema authentication.
 * @param {String} cellUrl Cell URL
 * @param {String} refreshToken Refresh token
 * @param {String} schemaUrl Schema url
 * @param {String} schemaUserId Schema UserID
 * @param {String} schemaPassword Schema password
 * @return {dcc.Accessor} Accessor object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.DcContext.prototype.getAccessorWithRefreshTokenAndSchemaAuthn = function(cellUrl , refreshToken , schemaUrl , schemaUserId , schemaPassword) {
  var as = new dcc.Accessor(this);
  as.setCellName(cellUrl);
  as.setTransCellRefreshToken(refreshToken);
  as.setSchema(schemaUrl);
  as.setSchemaUserId(schemaUserId);
  as.setSchemaPassword(schemaPassword);
  as.setDefaultHeaders(this.defaultHeaders);
  return as;
};

