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
//* @class アクセサクラス. データクラウドへアクセスするＡＰＩを呼び出す際のアクセス主体となります。
//* @constructor
//* @property {number} expiresIn トークンの有効期限.
//*/
/**
 * It creates a new object dcc.Accessor.
 * @class This is the base class for setting the access parameters to access Cloud data.
 * @constructor
 * @property {number} expiresIn Expiration date of the token.
 * @param {dcc.DcContext} dcContext
 */
dcc.Accessor = function(dcContext) {
    this.initializeProperties(this, dcContext);
};

///** asメソッドに利用する type. */
/** as accessor key type. *
//var ACCESSOR_KEY_SELF = "self";
///** asメソッドに利用する type. */
/** as accessor key client type. */
//var ACCESSOR_KEY_CLIENT = "client";
///** asメソッドに利用する type. */
/** @param {String} accessor token */
dcc.Accessor.ACCESSOR_KEY_TOKEN = "token";

///**
//* プロパティを初期化する.
//* @param {dcc.Accessor} self
//* @param　{?} dcContext DCコンテキスト
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.Accessor} self
 * @param　{dcc.DcContext} dcContext
 */
dcc.Accessor.prototype.initializeProperties = function(self, dcContext) {

//  /** トークンの有効期限. */
    /** Expiration date of the token. */
    self.expiresIn = 0;
//  /** アクセストークン. */
    /** access token. */
    self.accessToken = null;
//  /** リフレッシュトークンの有効期限. */
    /** Expiration date of the refresh token. */
    self.refreshExpiresIn = 0;
//  /** リフレッシュトークン. */
    /** refresh token. */
    self.refreshToken = null;
//  /** トークンタイプ. */
    /** token type. */
    self.tokenType = null;

    self.schemaAuth = {};

//  /** アクセス種別. */
    /** access type. */
    self.accessType = "";

//  /** Cellの名前. */
    /** cell name. */
    self.cellName = "";

//  /** 認証ID. */
    /** user id. */
    self.userId = "";
//  /** 認証パスワード. */
    /** @param {String} password. */
    self.password = "";

//  /** スキーマ. */
    /** schema. */
    self.schema = "";
//  /** スキーマ認証ID. */
    /** schema user id. */
    self.schemaUserId = "";
//  /** スキーマ認証パスワード. */
    /** schema password. */
    self.schemaPassword = "";

//  /** 対象Cellの名前. */
    /** target cell name. */
    self.targetCellName = "";

//  /** トランスセルトークン. */
    /** Transformer cell token. */
    self.transCellToken = "";
//  /** トランスセルリフレッシュトークン. */
    /** Transformer cell refresh token.. */
    self.transCellRefreshToken = "";

//  /** オーナー. */
    /** owner. */
    self.owner = false;

//  /** 基底URL. */
    /** base url. */
    self.baseUrl = "";
//  /** 現在のBox Schema. */
    /** Current box schema. */
    self.boxSchema = "";
//  /** 現在のBox Name. */
    /** current box name. */
    self.boxName = "";

//  /** DCコンテキスト. */
    /** DcContext. */
    self.context = null;
    /** Cell. */
    self.currentCell = "";

//  /** バッチモード. */
    /** batch moode. */
    self.batch = false;

    /** BatchAdapter. */
    self.batchAdapter = null;

//  /** デフォルトヘッダ. */
    /** default header. */
    self.defaultHeaders = "";

//  /** サーバーのレスポンスから取得したレスポンスヘッダ. */
    /** Response headers that are retrieved from the server response. */
    self.resHeaders = {};

    /** Variable to hold dc_cookie_peer key */
    self.cookiePeer = "";

    if (dcContext !== undefined) {
        self.context = dcContext;
        self.baseUrl = dcContext.getBaseUrl();
        self.cellName = dcContext.getCellName();
        self.boxSchema = dcContext.getBoxSchema();
        self.boxName = dcContext.getBoxName();
    }
};

///**
//* Accessorのクローンを生成する.
//* @return {?} コピーしたAccessorオブジェクト
//*/
/**
 * This method generates the clone of Accessor.
 * @return {Object} Copied accessor object
 */
dcc.Accessor.prototype.clone = function() {
    var dest = {};
    for (var prop in this) {
        dest[prop] = this[prop];
    }
    return dest;
};

///**
//* 他のCellを指定します.
//* @param {dcc.unitctl.Cell} 接続先Cell URL
//* @param {Object} opts object with useCookie
//* @return {dcc.unitctl.Cell} CellへアクセスするためのCellインスタンス
//* @throws ClientException DAO例外
//*/
/**
 * This method specifies the cell through accessor.
 * @param {dcc.unitctl.Cell} cellUrl Destination Cell URL
 * @param {Object} opts object with useCookie
 * @return {dcc.unitctl.Cell} Cell Instance
 * @throws {dcc.ClientException} DAO exception
 */
dcc.Accessor.prototype.cell = function(cellUrl, opts) {
    if (opts === undefined){
        opts = {};
    }
    var useCookie = opts.useCookie === undefined ? false : opts.useCookie;
    // create a clone so that the original accessor (this object)
    // should be kept untouched. 
    // In the rest of this method, only cloned accessor is to be handled.
    var clone = this.clone();

    var targetCellUrl = cellUrl;
    var myCellUrl = clone.getCellUrl();

    //If target cell URL exist but is not a valid URL then make it a valid URL
    if (targetCellUrl && !dcc.UrlUtils.isUrl(targetCellUrl)) {
        targetCellUrl = dcc.UrlUtils.append(this.baseUrl, targetCellUrl) + "/";
    }

    // Compare the given cellUrl parameter and home cell URL () if provided, if they are same or parameter is not provided
    // Returns the home Cell client object. 
    if (!targetCellUrl || myCellUrl === targetCellUrl) {
        if (!this.schema){
            //Pattern 1
            clone._obtainAccessTokenFromMyCell(myCellUrl , useCookie);
        }else{
            //Pattern 3
            clone._obtainAccessTokenFromMyCellWithSchema(myCellUrl , useCookie);
        }
        //return myCell instance
        var myCell = new dcc.unitctl.Cell(clone);
        console.log(myCell);
        return myCell;
    }
    clone.targetCellName = dcc.UrlUtils.extractFirstPath(cellUrl);
    //Fetch tokens unless this accessor is elevated to Unit User.
    //create target URL
    if (dcc.UrlUtils.isUrl(clone.targetCellName)) {
        targetCellUrl = clone.targetCellName + "/";
    } else {
        targetCellUrl = dcc.UrlUtils.append(this.baseUrl, clone.targetCellName) + "/";
    }
    if (!clone.owner) {
        if(!this.schema){
            //Pattern 2
            //clone.authenticate(useCookie);
            clone._obtainAccessTokenFromExtCell(myCellUrl , targetCellUrl , useCookie);
        }else{
            //Pattern 4
            clone._obtainAccessTokenFromExtCellWithSchema(myCellUrl , targetCellUrl , useCookie);
            //clone.authenticate(useCookie);
        }
    }
    //Tentative implementation since cross-unit access is not supported.
    //TODO need to support cross-unit access. Probably it is better to 
    //get rid of cellName and baseUrl from Accessor members and introduce cell URL. 
    clone.cellName = dcc.UrlUtils.extractFirstPath(cellUrl);
    //return extCell instance
    var extCell =  new dcc.unitctl.Cell(clone, dcc.UrlUtils.extractFirstPath(cellUrl));
    return extCell;
};

///**
//* パスワード変更.
//* @param newPassword 変更するパスワード
//* @throws ClientException DAO例外
//*/
/**
 * This method changes the current password.
 * @param {String} newPassword New Password
 * @throws {dcc.ClientException} DAO exception
 */
dcc.Accessor.prototype.changePassword = function(newPassword) {
    if (this.accessToken === null) {
        // accessTokenが無かったら自分で認証する
        /** if access token is not present then authenticate. */
        this.authenticate();
    }

    // パスワード変更
    /** Password change. */
    var headers = {};
    headers["X-Dc-Credential"] = newPassword;

    // パスワード変更のURLを作成
    /** Create the URL for password change. */
    var url = dcc.UrlUtils.append(this.getBaseUrl(), this.getCellName() + "/__mypassword");

    var rest = dcc.http.RestAdapterFactory.create(this);
    rest.put(url, "", "*", "application/json", headers);
    // password変更でエラーの場合は例外がthrowされるので例外で無い場合は、
    // Accessorのpasswordを新しいのに変えておく
    /** Password is set to new password. */
    this.password = newPassword;
};

///**
//* $Batchモードの取得.
//* @return {?} batchモード
//*/
/**
 * This method is used for Acquisition of $ Batch mode.
 * @return {Boolean} batch mode
 */
dcc.Accessor.prototype.isBatchMode = function() {
    return this.batch;
};

///**
//* $Batchモードの設定.
//* @param {?} batch $Batchモード
//*/
/**
 * THis method is used to set $ Batch mode.
 * @param {Boolean} batch $Batch mode
 */
dcc.Accessor.prototype.setBatch = function(batch) {
    if (typeof batch !== "boolean") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.batchAdapter = new dcc.box.odata.BatchAdapter(this);
    this.batch = batch;
};

///**
//* BatchAdaptrの取得. インスタンスが生成されていない場合生成する
//* @return {dcc.box.odata.BatchAdapter} BatchAdapterオブジェクト
//*/
/**
 * This method creates an instance to batch adapter if it does not exists.
 * @return {dcc.box.odata.BatchAdapter} BatchAdapter object
 */
dcc.Accessor.prototype.getBatchAdapter = function() {
    if (this.batchAdapter === null) {
        this.batchAdapter = new dcc.box.odata.BatchAdapter(this);
    }
    return this.batchAdapter;
};

///**
//* Unit昇格.
//* @return {?} 昇格後のAccessor(OwnerAccessor)
//* @throws {ClientException} DAO例外
//*/
/**
 * This method returns instance of OwnerAccessor with default headers.
 * @return {dcc.OwnerAccesssor} Accessor after promotions(OwnerAccessor)
 * @throws {dcc.ClientException} DAO exception
 */
dcc.Accessor.prototype.asCellOwner = function() {
    var ownerAccessor = new dcc.OwnerAccessor(this.context, this);
    ownerAccessor.defaultHeaders = this.defaultHeaders;
    return ownerAccessor;
};

///**
//* グローバルトークンの取得.
//* @return {dcc.ClientException} グローバルトークン
//*/
/**
 * It returns the access token.
 * @return {dcc.ClientException} access token
 * @deprecated replaced by {Cell#getAccessToken}
 */
dcc.Accessor.prototype.getAccessToken = function() {
    return this.accessToken;
};

///**
//* デフォルトヘッダを設定.
//* @param {Object} value デフォルトヘッダ
//*/
/**
 * This method sets the default headers.
 * @param {Object} default header value
 */
dcc.Accessor.prototype.setDefaultHeaders = function(value) {
    if (typeof value !== "object") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.defaultHeaders = value;
};

///**
//* デフォルトヘッダを取得.
//* @return {Object} デフォルトヘッダ
//*/
/**
 * This method returns the default headers.
 * @return {Object} default header value
 */
dcc.Accessor.prototype.getDefaultHeaders = function() {
    return this.defaultHeaders;
};

///**
//* グローバルトークンの設定.
//* @param {String} token グローバルトークン
//*/
/**
 * This method sets the access token value.
 * @param {String} token access token
 * @deprecated deprecated method
 */
dcc.Accessor.prototype.setAccessToken = function(token) {
    if (typeof token !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.accessToken = token;
};

///**
//* ClientConfigオブジェクトを取得.
//* @return {dcc.ClientConfig} ClientConfigオブジェクト
//*/
/**
 * This method gets the ClientConfig object.
 * @return {dcc.ClientConfig} ClientConfig object
 */
dcc.Accessor.prototype.getClientConfig = function() {
    return this.context.getClientConfig();
};

///**
//* DCコンテキストの取得.
//* @return {?} DCコンテキスト
//*/
/**
 * This method acquires the DcContext.
 * @return {dcc.DcContext} DCContext
 */
dcc.Accessor.prototype.getContext = function() {
    return this.context;
};

///**
//* DCコンテキストの設定.
//* @param {Object} dcContext DCコンテキスト
//*/
/**
 * This method sets the DcContext.
 * @param {Object} dcContext DCContext
 */
dcc.Accessor.prototype.setContext = function(dcContext) {
    if (typeof dcContext !== "object") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.context = dcContext;
};

///**
//* 現在アクセス中のCell取得.
//* @return {?} Cellクラスインスタンス
//*/
/**
 * This method returns the cell currently being accessed.
 * @return {dcc.unitctl.Cell} Cell instance
 */
dcc.Accessor.prototype.getCurrentCell = function() {
    return this.currentCell;
};

///**
//* 現在アクセス中のCell設定.
//* @param {?} cell Cellクラスインスタンス
//*/
/**
 * This method sets the current cell.
 * @param {dcc.unitctl.Cell} Cell instance
 */
dcc.Accessor.prototype.setCurrentCell = function(cell) {
    this.currentCell = cell;
};

///**
//* トークンの有効期限の取得.
//* @return {?} トークンの有効期限
//*/
/**
 * This method returns the expiration value of token.
 * @return {String} token expiration value
 */
dcc.Accessor.prototype.getExpiresIn = function() {
    return this.expiresIn;
};

///**
//* リフレッシュトークンの設定.
//* @return {?} リフレッシュトークン
//*/
/**
 * This method returns the refresh token.
 * @return {String} refresh token
 * @deprecated deprecated method
 */
dcc.Accessor.prototype.getRefreshToken = function() {
    return this.refreshToken;
};

///**
//* リフレッシュトークンの設定.
//* @return {?} リフレッシュトークン
//*/
/**
 * This method returns the token type.
 * @return {String} token type
 */
dcc.Accessor.prototype.getTokenType = function() {
    return this.tokenType;
};

///**
//* リフレッシュトークンの有効期限の取得.
//* @return {?} リフレッシュトークンの有効期限
//*/
/**
 * This method gets the refresh token expiration value.
 * @return {String} refresh token expiration value
 */
dcc.Accessor.prototype.getRefreshExpiresIn = function() {
    return this.refreshExpiresIn;
};

///**
//* CellName値の取得.
//* @return {String} CellName値
//*/
/**
 * This method gets the cell name.
 * @return {String} CellName value
 */
dcc.Accessor.prototype.getCellName = function() {
    return this.cellName;
};

///**
//* CellName値の設定.
//* @param {String} name CellName値
//*/
/**
 * This method sets the cell name.
 * @param {String} name CellName value
 */
dcc.Accessor.prototype.setCellName = function(name) {
    if (typeof name !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.cellName = name;
};

/**
 * This method sets the clientSecret.
 * @param {String} clientSecret client secret value
 */
dcc.Accessor.prototype.setClientSecret = function(clientSecret) {
    this.clientSecret = clientSecret;
};

///**
//* Box Schemaの取得.
//* @return {String} Schema名
//*/
/**
 * This method gets the box schema value.
 * @return {String} Box Schema value
 */
dcc.Accessor.prototype.getBoxSchema = function() {
    return this.boxSchema;
};

///**
//* Box Schemaの設定.
//* @param {String} uri Box Schema名
//*/
/**
 * This method sets the box schema value.
 * @param {String} uri Box Schema value
 */
dcc.Accessor.prototype.setBoxSchema = function(uri) {
    if (typeof uri !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.boxSchema = uri;
};

///**
//* Box Nameを取得.
//* @return {String} Box Name
//*/
/**
 * This method returns the box name.
 * @return {String} Box Name
 */
dcc.Accessor.prototype.getBoxName = function() {
    return this.boxName;
};

///**
//* Box Nameの設定.
//* @param {String} value Box Name
//*/
/**
 * This method sets the box name.
 * @param {String} value Box Name
 */
dcc.Accessor.prototype.setBoxName = function(value) {
    if (typeof value !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.boxName = value;
};

///**
//* 基底URLを設定.
//* @return {String} URL文字列
//*/
/**
 * This method returns the base url value.
 * @return {String} Base URL value
 */
dcc.Accessor.prototype.getBaseUrl = function() {
    return this.baseUrl;
};

///**
//* 基底URLを取得.
//* @param {String} value URL文字列
//*/
/**
 * This method sets the base url value.
 * @param {String} value base URL value
 */
dcc.Accessor.prototype.setBaseUrl = function(value) {
    if (typeof value !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.baseUrl = value;
};

///**
//* ユーザIDを取得する.
//* @return {String} the userId
//*/
/**
 * This method returns the user id.
 * @return {String} the userId
 */
dcc.Accessor.prototype.getUserId = function() {
    return this.userId;
};

///**
//* ユーザIDを設定する.
//* @param {String} userId the userId to set
//*/
/**
 * This method sets the user id.
 * @param {String} userId the userId to set
 */
dcc.Accessor.prototype.setUserId = function(userId) {
    if (typeof userId !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.userId = userId;
};

///**
//* パスワードを取得する.
//* @return {String} the password
//*/
/**
 * This method gets the password.
 * @return {String} the password
 */
dcc.Accessor.prototype.getPassword = function() {
    return this.password;
};

///**
//* パスワードを設定する.
//* @param {String} password the password to set
//*/
/**
 * This method sets the password.
 * @param {String} password the password to set
 */
dcc.Accessor.prototype.setPassword = function(password) {
    if (typeof password !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.password = password;
};

///**
//* Schemaを取得する.
//* @return {String} the schema
//*/
/**
 * This method gets the schema.
 * @return {String} the schema
 */
dcc.Accessor.prototype.getSchema = function() {
    return this.schema;
};

///**
//* Schemaを設定する.
//* @param {String} schema the schema to set
//*/
/**
 * This method sets the schema.
 * @param {String} schema the schema to set
 */
dcc.Accessor.prototype.setSchema = function(schema) {
    if (typeof schema !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.schema = schema;
};

///**
//* 外部Cellを取得する.
//* @return {String} the targetCellName
//*/
/**
 * This method returns the target cell name.
 * @return {String} the targetCellName
 */
dcc.Accessor.prototype.getTargetCellName = function() {
    return this.targetCellName;
};

///**
//* 外部Cellを設定する.
//* @param {String} targetCellName the targetCellName to set
//*/
/**
 * This method returns the target cell name.
 * @param {String} targetCellName the targetCellName to set
 */
dcc.Accessor.prototype.setTargetCellName = function(targetCellName) {
    if (typeof targetCellName !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.targetCellName = targetCellName;
};

///**
//* "self","client"等のタイプを設定する.
//* @param {String} accessType the accessType to set
//*/
/**
 * This method sets the type of client.
 * @param {String} accessType the accessType to set
 */
dcc.Accessor.prototype.setAccessType = function(accessType) {
    if (typeof accessType !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.accessType = accessType;
};

///**
//* "self","client"等のタイプを返却.
//* @return {String} タイプ
//*/
/**
 * This method gets the type of client.
 * @return {String} タイプ
 */
dcc.Accessor.prototype.getAccessType = function() {
    return this.accessType;
};

///**
//* トランスセルを設定する.
//* @param {String} transCellToken the trancCellToken to set
//*/
/**
 * This method sets the transformer cell token value.
 * @param {String} transCellToken the trancCellToken to set
 * @deprecated deprecated method
 */
dcc.Accessor.prototype.setTransCellToken = function(trancCellToken) {
    if (typeof trancCellToken !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.transCellToken = trancCellToken;
};

///**
//* トランスセルを取得する.
//* @returns {String} the transCellToken
//*/
/**
 * This method gets the transformer cell token value.
 * @returns {String} the transCellToken
 * @deprecated deprecated method
 */
dcc.Accessor.prototype.getTransCellToken = function() {
    return this.transCellToken;
};

///**
//* トランスセルリフレッシュトークンを設定する.
//* @param {String} trancCellRefreshToken the trancCellRefreshToken to set
//*/
/**
 * This method sets the transformer cell refresh token value.
 * @param {String} trancCellRefreshToken the trancCellRefreshToken to set
 * @deprecated deprecated method
 */
dcc.Accessor.prototype.setTransCellRefreshToken = function(trancCellRefreshToken) {
    if (typeof trancCellRefreshToken !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.transCellRefreshToken = trancCellRefreshToken;
};

///**
//* トランスセルリフレッシュトークンを取得する.
//* @returns {String} the trancCellRefreshToken
//*/
/**
 * This method gets the transformer cell refresh token value.
 * @returns {String} the trancCellRefreshToken
 * @deprecated deprecated method
 */
dcc.Accessor.prototype.getTransCellRefreshToken = function() {
    return this.transCellRefreshToken;
};

///**
//* スキーマユーザIDを設定する.
//* @param {String} schemaUserId the schemaUserId to set
//*/
/**
 * This method sets the schema user ID.
 * @param {String} schemaUserId the schemaUserId to set
 */
dcc.Accessor.prototype.setSchemaUserId = function(schemaUserId) {
    if (typeof schemaUserId !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.schemaUserId = schemaUserId;
};

///**
//* スキーマユーザIDを取得する.
//* @returns {String} the schemaUserId
//*/
/**
 * This method gets the schema user ID.
 * @returns {String} the schemaUserId
 */
dcc.Accessor.prototype.getSchemaUserId = function() {
    return this.schemaUserId;
};

///**
//* スキーマユーザパスワードを設定する.
//* @param {String} schemaPassword the schemaPassword to set
//*/
/**
 * This method sets the schema password.
 * @param {String} schemaPassword the schemaPassword to set
 */
dcc.Accessor.prototype.setSchemaPassword = function(schemaPassword) {
    if (typeof schemaPassword !== "string") {
        throw new dcc.ClientException("InvalidParameter");
    }
    this.schemaPassword = schemaPassword;
};

///**
//* スキーマユーザパスワードを取得する.
//* @returns {String} the schemaPassword
//*/
/**
 * This method gets the schema password.
 * @returns {String} the schemaPassword
 */
dcc.Accessor.prototype.getSchemaPassword = function() {
    return this.schemaPassword;
};

///**
//* サーバーのレスポンスから取得したレスポンスヘッダを設定.
//* @param headers 設定するヘッダ
//*/
/**
 * This method sets the response header acquired from the server response.
 * @param {Object} headers response headers
 * @deprecated deprecated method
 */
dcc.Accessor.prototype.setResHeaders = function(headers) {
    this.resHeaders = headers;
};

///**
//* レスポンスヘッダの取得.
//* @return レスポンスヘッダの一覧
//*/
/**
 * This method gets the response header acquired from the server response.
 * @return {Object} response headers
 * @deprecated replaced by {DcHttpClient#getAllResponseHeaders}
 */
dcc.Accessor.prototype.getResHeaders = function() {
    return this.resHeaders;
};

/**
 * Get the cookie peer key.
 * @return {String} Cookie Peer Key
 */
dcc.Accessor.prototype.getCookiePeer = function() {
    return this.cookiePeer;
};

///**
//* 認証を行う.
//* @param {Boolean} useCookie to check dc_cookie
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs authentication of current user based on token etc.
 * @param {Boolean} useCookie to check dc_cookie
 * @throws {dcc.ClientException} DAO exception
 */
dcc.Accessor.prototype.authenticate = function(useCookie) {

    if (this.accessType === "token") {
        return;
    }
    var httpClient = new dcc.http.DcHttpClient(false);

    // 認証するurlを作成する
    /** Create URL for authentication */
    var authUrl = this.getCellUrl();

    // 認証するためのリクエストボディを作る
    /** Create request body for authentication */
    var requestBody = "";
    if (this.transCellToken !== null  && this.transCellToken !== "") {
        // トランスセルトークン認証
        /** Trans-cell token authentication */
        requestBody += "grant_type=urn:ietf:params:oauth:grant-type:saml2-bearer&assertion=";
        requestBody += this.transCellToken;
    } else if (this.transCellRefreshToken !== null && this.transCellRefreshToken !== "") {
        // リフレッシュトークン認証
        /** Refresh token authentication */
        requestBody += "grant_type=refresh_token&refresh_token=";
        requestBody += this.transCellRefreshToken;
    } else if (this.userId !== null && this.userId !== "") {
        // パスワード認証
        /** Password authentication */
        requestBody = "grant_type=password";
        requestBody += "&username=" + this.userId;
        requestBody += "&password=" + this.password;
    }

    // targetのURLを作る
    /** Create target URL */
    if (this.targetCellName !== null && this.targetCellName !== "") {
        requestBody += "&dc_target=";
        if (dcc.UrlUtils.isUrl(this.targetCellName)) {
            requestBody += this.targetCellName + "/";
        } else {
            requestBody += dcc.UrlUtils.append(this.baseUrl, this.targetCellName) + "/";
        }
    }

    // スキーマ付き認証のためにスキーマ情報を付加する
    /** Add schema information */
    if ((this.schemaUserId !== null && this.schemaUserId !== "") &&
            (this.schemaPassword !== null && this.schemaPassword !== "")) {
        // スキーマ認証
        /** Schema Authentication  */
        var schemaRequestBody = "";
        schemaRequestBody += "grant_type=password&username=";
        schemaRequestBody += this.schemaUserId;
        schemaRequestBody += "&password=";
        schemaRequestBody += this.schemaPassword;
        schemaRequestBody += "&dc_target=";
        schemaRequestBody += authUrl;
        // Urlでない場合は、BaseURLにスキーマ名を足す
        /** If this is not the Url, and add the schema name to BaseURL */
        if (!dcc.UrlUtils.isUrl(this.schema)) {
            this.schema = dcc.UrlUtils.append(this.baseUrl, this.schema);
        }
        this.schema = dcc.UrlUtils.addTrailingSlash(this.schema);

        // Perform Schema Authentication
        httpClient._execute2("POST", dcc.UrlUtils.append(this.schema, "__auth"), {
            body : schemaRequestBody,
            headers: {"Content-Type": "application/x-www-form-urlencoded"}
        });
        this.schemaAuth =httpClient.bodyAsJson();
        requestBody += "&client_id=";
        requestBody += this.schema;
        requestBody += "&client_secret=";
        requestBody += this.schemaAuth.access_token;
    }

    //if cookie is set and dc_target is not specified
    if(useCookie !== undefined){
        if(useCookie && (this.targetCellName === null || this.targetCellName === "")){
            requestBody += "&dc_cookie=true";
        }
    }

    if (this.owner) {
        requestBody += "&dc_owner=true";
    }

    // 認証してトークンを保持する
    /** authenticate and hold the token  */
    var requestUrl = dcc.UrlUtils.append(authUrl, "__auth");
    httpClient._execute2("POST", requestUrl, {
        body : requestBody,
        headers: {"Content-Type": "application/x-www-form-urlencoded"}
    });
    var responseJson = httpClient.bodyAsJson();

    if (httpClient.getStatusCode() >= 400){
        //invalid grant - authentication failed
        throw new dcc.ClientException(responseJson.error, httpClient.getStatusCode());
    }
    this.accessToken = responseJson.access_token;
    this.expiresIn = responseJson.expires_in;
    this.refreshToken = responseJson.refresh_token;
    this.refreshExpiresIn = responseJson.refresh_token_expires_in;
    this.tokenType = responseJson.token_type;

    //if dc_cookie_peer value is received in response
    if(responseJson.dc_cookie_peer !== undefined && responseJson.dc_cookie_peer !== "" && responseJson.dc_cookie_peer !== null){
        this.cookiePeer = responseJson.dc_cookie_peer;
    }
};

///**
//* 認証先Cellのburlを作成する.
//* @return {String} 認証先Cellのurl
//* @throws {ClientException} DAO例外
//*/
/**
 * This method creates certification URL.
 * @return {String} Url of authentication destination Cell
 * @throws {dcc.ClientException} DAOexception
 */
dcc.Accessor.prototype.createCertificatUrl = function() {
    var authUrl = "";
    if (dcc.UrlUtils.isUrl(this.cellName)) {
        authUrl = this.cellName;
    } else {
        authUrl = dcc.UrlUtils.append(this.baseUrl, this.cellName);
    }
    return authUrl;
};

///**
//* Cellへのグローバルトークンを取得する.
//* @return トークン
//*/
/**
 * This method returns the global token of the cell.
 * @return {String} global token
 * @deprecated
 */
dcc.Accessor.prototype.loadGlobalToken = function() {
    return "";
};

///**
//* Boxへのローカルトークンを取得する.
//* @return トークン
//*/
/**
 * This method returns the local token of Box.
 * @return {String} local token
 * @deprecated
 */
dcc.Accessor.prototype.loadClientToken = function() {
    return "";
};

/**
 * This method is used for Acquisition of CellUrl.
 * @return {String} cellUrl
 */
dcc.Accessor.prototype.getCellUrl = function() {
    if (dcc.UrlUtils.isUrl(this.cellName)) {
        return this.cellName;
    } else {
        return this.getBaseUrl() + this.cellName + "/";
    }
};

/**
 * @private
 * This method performs no-schema authentication of current user based on token.
 * This is a scenario when authentication is done on same cell & without schema authentication. 
 * @param {String} myCellUrl cell URL to be authenticated
 * @param {Boolean} useCookie to check dc_cookie
 * @throws {dcc.ClientException} DAO exception
 */
dcc.Accessor.prototype._obtainAccessTokenFromMyCell = function (myCellUrl , useCookie) {
    //create request options for authentication
    var opts = {};

    if (this.transCellToken !== null  && this.transCellToken !== "") {
        // トランスセルトークン認証
        /** Trans-cell token authentication */
        opts.grant_type = "urn:ietf:params:oauth:grant-type:saml2-bearer";
        opts.assertion = this.transCellToken;
    }else if (this.transCellRefreshToken !== null && this.transCellRefreshToken !== "") {
        // リフレッシュトークン認証
        /** Refresh token authentication */
        opts.grant_type = "refresh_token";
        opts.refresh_token = this.transCellRefreshToken;
    }  else if (this.userId !== null && this.userId !== "") {
        //Password authentication
        opts.grant_type = "password";
        opts.username = this.userId;
        opts.password = this.password;
    }

    //if cookie is set and dc_target is not specified
    if(useCookie === true){
        opts.dc_cookie = "true";
    } else if(useCookie === false){
        opts.dc_cookie = "false";
    }

    if (this.owner) {
        opts.dc_owner = "true";
    }

    //prepare request URL for authentication
    var requestUrl = dcc.UrlUtils.append(myCellUrl, "__auth");

    //execute the request
    var responseJson = this._makeAccessTokenRequest(requestUrl, opts);
    this.accessToken = responseJson.access_token;
    this.expiresIn = responseJson.expires_in;
    this.refreshToken = responseJson.refresh_token;
    this.refreshExpiresIn = responseJson.refresh_token_expires_in;
    this.tokenType = responseJson.token_type;
    //if dc_cookie_peer value is received in response
    if(responseJson.dc_cookie_peer !== undefined && responseJson.dc_cookie_peer !== "" && responseJson.dc_cookie_peer !== null){
        this.cookiePeer = responseJson.dc_cookie_peer;
    }
};

/**
 * @private
 * This method performs schema authentication of current user based on token.
 * This is a scenario when authentication is done on same cell with schema authentication. 
 * @param {String} myCellUrl cell URL to be authenticated
 * @param {Boolean} useCookie to check dc_cookie
 * @throws {dcc.ClientException} DAO exception
 */
dcc.Accessor.prototype._obtainAccessTokenFromMyCellWithSchema = function (myCellUrl , useCookie) {
    //create request options for authentication
    var opts = {};
    if (this.transCellToken !== null  && this.transCellToken !== "") {
        // トランスセルトークン認証
        /** Trans-cell token authentication */
        opts.grant_type = "urn:ietf:params:oauth:grant-type:saml2-bearer";
        opts.assertion = this.transCellToken;
    }else if (this.transCellRefreshToken !== null && this.transCellRefreshToken !== "") {
        // リフレッシュトークン認証
        /** Refresh token authentication */
        opts.grant_type = "refresh_token";
        opts.refresh_token = this.transCellRefreshToken;
    }  else if (this.userId !== null && this.userId !== "") {
        //Password authentication
        opts.grant_type = "password";
        opts.username = this.userId;
        opts.password = this.password;
    }

//  スキーマ付き認証のためにスキーマ情報を付加する
    /** Add schema information */
    if ((this.schemaUserId !== null && this.schemaUserId !== "") &&
            (this.schemaPassword !== null && this.schemaPassword !== "")) {
        var schemaOpts = {};
        // スキーマ認証
        /** Schema Authentication  */
        schemaOpts.grant_type = "password";
        schemaOpts.username = this.schemaUserId;
        schemaOpts.password = this.schemaPassword;
        schemaOpts.dc_target = myCellUrl;

        // Urlでない場合は、BaseURLにスキーマ名を足す
        /** If this is not the URL, and add the schema name to BaseURL */
        if (!dcc.UrlUtils.isUrl(this.schema)) {
            this.schema = dcc.UrlUtils.append(this.baseUrl, this.schema);
        }
        this.schema = dcc.UrlUtils.addTrailingSlash(this.schema);

        // Perform Schema Authentication
        this.schemaAuth =this._makeAccessTokenRequest(dcc.UrlUtils.append(this.schema, "__auth"), schemaOpts);
        opts.client_id = this.schema;
        opts.client_secret = this.schemaAuth.access_token;
    }
    //if cookie is set and dc_target is not specified
    if(useCookie === true){
        opts.dc_cookie = "true";
    } else if(useCookie === false){
        opts.dc_cookie = "false";
    }

    if (this.owner) {
        opts.dc_owner = "true";
    }
    //prepare request URL for authentication
    var requestUrl = dcc.UrlUtils.append(myCellUrl, "__auth");
    //execute the request
    var responseJson = this._makeAccessTokenRequest(requestUrl, opts);
    this.accessToken = responseJson.access_token;
    this.expiresIn = responseJson.expires_in;
    this.refreshToken = responseJson.refresh_token;
    this.refreshExpiresIn = responseJson.refresh_token_expires_in;
    this.tokenType = responseJson.token_type;
    //if dc_cookie_peer value is received in response
    if(responseJson.dc_cookie_peer !== undefined && responseJson.dc_cookie_peer !== "" && responseJson.dc_cookie_peer !== null){
        this.cookiePeer = responseJson.dc_cookie_peer;
    }
};

/**
 * @private
 * This method performs no-schema authentication of current user based on token. 
 * This is a scenario when authentication is done on external cell & without schema authentication. 
 * @param {String} myCellUrl
 * @param {String} targetCellUrl
 * @param {Boolean} useCookie to check dc_cookie
 * @throws {dcc.ClientException} DAO exception
 */
dcc.Accessor.prototype._obtainAccessTokenFromExtCell = function (myCellUrl, targetCellUrl, useCookie) {
    if (this.accessType === "token") {
        return;
    }
    //create request options for authentication
    var opts = {};
    if (this.transCellToken !== null  && this.transCellToken !== "") {
        // トランスセルトークン認証
        /** Trans-cell token authentication */
        opts.grant_type = "urn:ietf:params:oauth:grant-type:saml2-bearer";
        opts.assertion = this.transCellToken;
    }else if (this.transCellRefreshToken !== null && this.transCellRefreshToken !== "") {
        // リフレッシュトークン認証
        /** Refresh token authentication */
        opts.grant_type = "refresh_token";
        opts.refresh_token = this.transCellRefreshToken;
    }
    if (this.userId !== null && this.userId !== "") {
        //Password authentication
        opts.grant_type = "password";
        opts.username = this.userId;
        opts.password = this.password;
    }

    if (this.targetCellName !== null && this.targetCellName !== "") {
        opts.dc_target = targetCellUrl;
    }

    //prepare request url
    var requestUrl = dcc.UrlUtils.append(myCellUrl, "__auth");

    //execute the request
    var responseJson = this._makeAccessTokenRequest(requestUrl, opts);
    this.accessToken = responseJson.access_token;
    this.refreshToken = responseJson.refresh_token;
    this.refreshExpiresIn = responseJson.refresh_token_expires_in;
    this.tokenType = responseJson.token_type;
    //if dc_cookie_peer value is received in response
    if(responseJson.dc_cookie_peer !== undefined && responseJson.dc_cookie_peer !== "" && responseJson.dc_cookie_peer !== null){
        this.cookiePeer = responseJson.dc_cookie_peer;
    }

    //TCAT should be thrown for the request to change the AT, re-instantiate opts for second request
    opts = {};
    opts.grant_type = "urn:ietf:params:oauth:grant-type:saml2-bearer";
    //access token from last request
    opts.assertion = this.accessToken;
    //request URL refers to targetCell
    requestUrl = dcc.UrlUtils.append(targetCellUrl, "__auth");
    if(useCookie === true){
        opts.dc_cookie = "true";
    } else if(useCookie === false){
        opts.dc_cookie = "false";
    }
    //if cookie is set and dc_target is not specified
    if (this.owner) {
        opts.dc_owner = "true";
    }
    //execute the request
    responseJson = this._makeAccessTokenRequest(requestUrl, opts);
    this.accessToken = responseJson.access_token;
    this.refreshToken = responseJson.refresh_token;
    this.expiresIn = responseJson.expires_in;
    this.refreshExpiresIn = responseJson.refresh_token_expires_in;
    this.tokenType = responseJson.token_type;
    //if dc_cookie_peer value is received in response
    if(responseJson.dc_cookie_peer !== undefined && responseJson.dc_cookie_peer !== "" && responseJson.dc_cookie_peer !== null){
        this.cookiePeer = responseJson.dc_cookie_peer;
    }
};

/**
 * @private
 * This method performs schema authentication of current user based on token. 
 * This is a scenario when authentication is done with schema authentication on same cell. 
 * @param {String} myCellUrl
 * @param {String} targetCellUrl
 * @param {Boolean} useCookie to check dc_cookie
 * @throws {dcc.ClientException} DAO exception
 */
dcc.Accessor.prototype._obtainAccessTokenFromExtCellWithSchema = function (myCellUrl ,targetCellUrl, useCookie) { if (this.accessType === "token") {
    return;
}
//create request options for authentication
var opts = {};

/** Create request body for authentication */
if (this.transCellToken !== null  && this.transCellToken !== "") {
    // トランスセルトークン認証
    /** Trans-cell token authentication */
    opts.grant_type = "urn:ietf:params:oauth:grant-type:saml2-bearer";
    opts.assertion = this.transCellToken;
}else if (this.transCellRefreshToken !== null && this.transCellRefreshToken !== "") {
    // リフレッシュトークン認証 ,  Refresh token authentication
    opts.grant_type = "refresh_token";
    opts.refresh_token = this.transCellRefreshToken;
}
if (this.userId !== null && this.userId !== "") {
    //Password authentication
    opts.grant_type = "password";
    opts.username = this.userId;
    opts.password = this.password;
}

//targetのURLを作る , create target URL
if (this.targetCellName !== null && this.targetCellName !== "") {
    opts.dc_target = targetCellUrl;
}
//スキーマ付き認証のためにスキーマ情報を付加する
/** Add schema information */
if ((this.schemaUserId !== null && this.schemaUserId !== "") &&
        (this.schemaPassword !== null && this.schemaPassword !== "")) {
    // スキーマ認証
    /** Schema Authentication  */
    var schemaOpts = {};
    schemaOpts.grant_type = "password";
    schemaOpts.username = this.schemaUserId;
    schemaOpts.password = this.schemaPassword;
    schemaOpts.dc_target = targetCellUrl;
    // Urlでない場合は、BaseURLにスキーマ名を足す
    /** If this is not the Url, and add the schema name to BaseURL */
    if (!dcc.UrlUtils.isUrl(this.schema)) {
        this.schema = dcc.UrlUtils.append(this.baseUrl, this.schema);
    }
    this.schema = dcc.UrlUtils.addTrailingSlash(this.schema);

    // Perform Schema Authentication
    this.schemaAuth =this._makeAccessTokenRequest(dcc.UrlUtils.append(this.schema, "__auth"), schemaOpts);
}

//prepare request url
var requestUrl = dcc.UrlUtils.append(myCellUrl, "__auth");
//execute the request
var responseJson = this._makeAccessTokenRequest(requestUrl, opts);
this.accessToken = responseJson.access_token;


requestUrl = dcc.UrlUtils.append(targetCellUrl, "__auth");
opts = {};
opts.grant_type = "urn:ietf:params:oauth:grant-type:saml2-bearer";
opts.assertion = this.accessToken;
opts.client_id = this.schema;
opts.client_secret = this.schemaAuth.access_token;
//if cookie is set
if(useCookie === true){
    opts.dc_cookie = "true";
} else if(useCookie === false){
    opts.dc_cookie = "false";
}
if (this.owner) {
    opts.dc_owner = "true";
}
responseJson = this._makeAccessTokenRequest(requestUrl, opts);
this.accessToken = responseJson.access_token;
this.expiresIn = responseJson.expires_in;
this.refreshToken = responseJson.refresh_token;
this.refreshExpiresIn = responseJson.refresh_token_expires_in;
this.tokenType = responseJson.token_type;
//if dc_cookie_peer value is received in response
if(responseJson.dc_cookie_peer !== undefined && responseJson.dc_cookie_peer !== "" && responseJson.dc_cookie_peer !== null){
    this.cookiePeer = responseJson.dc_cookie_peer;
}
};

/**
 * @private
 * This is a common method responsible for executing authentication request from _obtainAccessTokenFromMyCell, _obtainAccessTokenFromExtCell
 * _obtainAccessTokenFromMyCellWithSchema,_obtainAccessTokenFromExtCellWithSchema.
 * @param {String} cellUrl
 * @param {Object} opts optional parameters
 * @return {Object} responseJSON json response of auth call.
 * @throws {dcc.ClientException} DAO exception
 */
dcc.Accessor.prototype._makeAccessTokenRequest = function (cellUrl , opts) {
    var formatedRequestBody  = dcc.UrlUtils.jsonToW3Form(opts);
    var httpClient = new dcc.http.DcHttpClient(false);
    
    //Creation of request headers
    //Setting requestheader's value to builder instance.
    var builder = new dcc.http.DcRequestHeaderBuilder();
    builder.contentTypeHeaderValue = "application/x-www-form-urlencoded";
    builder.defaultHeaders(this.getDefaultHeaders());
    
    //incorporate request header in builder
    builder.build(httpClient);
    httpClient._execute2("POST", cellUrl, {
        body : formatedRequestBody
    });
    var responseJson = httpClient.bodyAsJson();
    if (httpClient.getStatusCode() >= 400){
        //invalid grant - authentication failed
        throw new dcc.ClientException(responseJson.error, httpClient.getStatusCode());
    }
    return responseJson;
};
