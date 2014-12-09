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
//* @class Cellへアクセスするためのクラス.
//* @constructor
//* @augments dcc.AbstractODataContext
//*/
/**
 * It creates a new object dcc.unitctl.Cell.
 * @class This class represents Cell object to perform cell related operations.
 * @constructor
 * @property {dcc.Acl} acl class instance to access ACL settings.
 * @property {dcc.cellctl.Account} account class instance to access Account.
 * @property {dcc.box.Box} box class instance to access Box.
 * @property {dcc.cellctl.Message} message Manager classes for sending and receiving messages.
 * @property {dcc.cellctl.Relation} relation class instance to access Relation.
 * @property {dcc.cellctl.Role} role class instance to access Role. 
 * @property {dcc.cellctl.ExtRole} extRole class instance to access External Role.
 * @property {dcc.cellctl.ExtCell} extCell class instance to access External Cell.
 * @property {dcc.cellctl.Event} event class instance to access Event.  
 * @augments dcc.AbstractODataContext
 * @param {dcc.Accessor} as Accessor
 * @param {String} key
 */
dcc.unitctl.Cell = function(as, key) {
  this.ctl = {};
  this.initializeProperties(this, as, key);
};
dcc.DcClass.inherit(dcc.unitctl.Cell, dcc.AbstractODataContext);

///**
//* プロパティを初期化する.
//* @param {dcc.unitctl.Cell} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {Object} body
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.unitctl.Cell} self
 * @param {dcc.Accessor} as Accessor
 * @param {Object} body
 */
dcc.unitctl.Cell.prototype.initializeProperties = function(self, as, body) {
  this.uber = dcc.AbstractODataContext.prototype;
  this.uber.initializeProperties(self, as);

///** キャメル方で表現したクラス名. */
  /** Class name for Cell. */
  self.CLASSNAME = "Cell";

///** Cell名 (string). */
  /** Cell name (string). */
  self.name = "";
  if (typeof body === "string") {
    self.name = body;
  }
  if (typeof body === "undefined" && self.accessor !== undefined) {
    self.name = self.accessor.getCellName();
  }

  /** location. */
  self.location = null;

///** CellレベルACLへアクセスするためのクラス. */
  /** To access cell level ACL. */
  
  self.ctl.acl = null;
///** メンバーへアクセスするためのクラスインスタンス。cell().accountでアクセス. */
  /** Class instance to access Account. */
  self.ctl.account = null;
///** BoxのCRUDを行うマネージャクラス. */
  /** Manager class to perform CRUD of Box. */
  self.ctl.box = null;
///** メッセージ送受信を行うマネージャクラス. */
  /** Manager classes for sending and receiving messages. */
  self.ctl.message = null;
///** Relation へアクセスするためのクラス. */
  /** Class to access the Relation. */
  self.ctl.relation = null;
///** Role へアクセスするためのクラス. */
  /** Class to access the Role. */
  self.ctl.role = null;
///** ExtRole へアクセスするためのクラス. */
  /** Class to access the External Role. */
  self.ctl.extRole = null;
///** ExtCell へアクセスするためのクラス. */
  /** Class to access the External Cell. */
  self.ctl.extCell = null;
///** Event へアクセスするためのクラス. */
  /** Class to access the Event. */
  self.ctl.event = null;

//if (this.json !== null) {
//this.rawData = this.json;
//this.name = this.json.Name;
//this.location = this.json.__metadata.uri;
//}

  if (self.accessor !== undefined) {
    self.accessor.setCurrentCell(this);
    self.ctl.relation = new dcc.cellctl.RelationManager(self.accessor);
    self.ctl.role = new dcc.cellctl.RoleManager(self.accessor);
    self.ctl.message = new dcc.cellctl.MessageManager(self.accessor);
//  this.acl = new AclManager(this.accessor);
    self.ctl.account = new dcc.cellctl.AccountManager(self.accessor);
    self.ctl.box = new dcc.box.BoxManager(self.accessor);
//  this.extRole = new ExtRoleManager(this.accessor);
    self.ctl.extCell = new dcc.cellctl.ExtCellManager(self.accessor);
    self.ctl.event = new dcc.cellctl.EventManager(self.accessor);
    self.ctl.extRole = new dcc.cellctl.ExtRoleManager(self.accessor);
  }
};

///**
//* Cell名を取得.
//* @return {String} Cell名
//*/
/**
 * This method gets the Cell name.
 * @return {String} Cell name
 */
dcc.unitctl.Cell.prototype.getName = function() {
  return this.name;
};

///**
//* Cell名を設定.
//* @param {String} value Cell名
//*/
/**
 * This method sets the Cell name.
 * @param {String} value Cell name
 */
dcc.unitctl.Cell.prototype.setName = function(value) {
  if (typeof value !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.name = value;
};


///**
//* CellのURLを取得する.
//* @return {String} 取得した CellのURL
//*/
/**
 * This method gets the URL for performing cell related operations.
 * @return {String} URL of the cell
 */
dcc.unitctl.Cell.prototype.getUrl = function() {
  return this.accessor.getBaseUrl() + encodeURI(this.name) + "/";
};

///**
//* アクセストークンを取得.
//* @return {?} アクセストークン
//* @throws {ClientException} DAO例外
//*/
/**
 * This method gets the access token.
 * @return {String} Access Token
 * @throws {dcc.ClientException} DAO exception
 */
dcc.unitctl.Cell.prototype.getAccessToken = function() {
  if (this.accessor.getAccessToken() !== null) {
    return this.accessor.getAccessToken();
  } else {
    throw new dcc.ClientException.create("Unauthorized");
  }
};

///**
//* アクセストークンの有効期限を取得.
//* @return {?} アクセストークンの有効期限
//*/
/**
 * This method gets the expiration date of the access token.
 * @return {String} expiration date of the access token
 */
dcc.unitctl.Cell.prototype.getExpiresIn = function() {
  return this.accessor.getExpiresIn();
};

///**
//* アクセストークンのタイプを取得.
//* @return {?} アクセストークンのタイプ
//*/
/**
 * This method gets the access token type.
 * @return {String} access token type
 */
dcc.unitctl.Cell.prototype.getTokenType = function() {
  return this.accessor.getTokenType();
};

///**
//* リフレッシュトークンを取得.
//* @return {?} リフレッシュトークン
//* @throws ClientException DAO例外
//*/
/**
 * This method gets the refresh token.
 * @return {String} Refreash token
 * @throws {dcc.ClientException} DAO exception
 */
dcc.unitctl.Cell.prototype.getRefreshToken = function() {
  if (this.accessor.getRefreshToken() !== null) {
    return this.accessor.getRefreshToken();
  } else {
    throw new dcc.ClientException("Unauthorized");
  }
};

///**
//* リフレッシュの有効期限を取得.
//* @return {?} リフレッシュトークンの有効期限
//*/
/**
 * This method gets the expiration date of the refresh token.
 * @return {String} expiration date of the refresh token
 */
dcc.unitctl.Cell.prototype.getRefreshExpiresIn = function() {
  return this.accessor.getRefreshExpiresIn();
};

/**
 * This method returns the location.
 * @return {String} location
 */
dcc.unitctl.Cell.prototype.getLocation = function() {
  return this.location;
};

///**
//* CellのownerRepresentativeAccountsを設定.
//* @param user アカウント名
//* @throws ClientException DAO例外
//*/
//dcc.unitctl.Cell.prototype.setOwnerRepresentativeAccounts = function(user) {
//var value = "<dc:account>" + user + "</dc:account>";
//RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
//rest.proppatch(this.getUrl(), "dc:ownerRepresentativeAccounts", value);
//};

///**
//* CellのownerRepresentativeAccountsを設定(複数アカウント登録).
//* @param accountName アカウント名の配列
//* @throws ClientException DAO例外
//*/
//public void setOwnerRepresentativeAccounts(String[] accountName) throws ClientException {
//dcc.unitctl.Cell.prototype.setOwnerRepresentativeAccounts = function(accountName) {
//StringBuilder sb = new StringBuilder();
//for (Object an : accountName) {
//sb.append("<dc:account>");
//sb.append(an);
//sb.append("</dc:account>");
//}
//RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
//rest.proppatch(this.getUrl(), "dc:ownerRepresentativeAccounts", sb.toString());
//};

///**
//* Boxへアクセスするためのクラスを生成.
//* @param {?} boxName Box Name
//* @param {?} schemaValue スキーマ名
//* @return {dcc.box.Box} 生成したBoxインスタンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method generates classes to access the Box.
 * @param {String} boxName Box Name
 * @param {String} schemaValue Schema value
 * @return {dcc.box.Box} Box object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.unitctl.Cell.prototype.box = function(boxName, schemaValue) {
  this.accessor.setBoxName(boxName);
  var url = dcc.UrlUtils.append(this.accessor.getCurrentCell().getUrl(), this.accessor.getBoxName());
  return new dcc.box.Box(this.accessor, {"Name":boxName, "Schema":schemaValue}, url);
};

///**
//* BaseUrl を取得.
//* @return {String} baseUrl 基底URL文字列
//*/
/**
 * This method gets the Base URL.
 * @return {String} baseUrl Base URL
 */
dcc.unitctl.Cell.prototype.getBaseUrlString = function() {
  return this.accessor.getBaseUrl();
};

///**
//* ODataのキーを取得する.
//* @return {String} ODataのキー情報
//*/
/**
 * This method gets the key of OData.
 * @return {String} OData key
 */
dcc.unitctl.Cell.prototype.getKey = function() {
  return "('" + this.name + "')";
};

///**
//* クラス名をキャメル型で取得する.
//* @return {?} ODataのキー情報
//*/
/**
 * This method gets the class name.
 * @return {String} OData class name
 */
dcc.unitctl.Cell.prototype.getClassName = function() {
  return this.CLASSNAME;
};

/**
 * Get the cookie peer key.
 * @returns {String} Cookie Peer key
 */
dcc.unitctl.Cell.prototype.getCookiePeer = function(){
  return this.accessor.getCookiePeer();
};