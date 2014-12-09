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
//* @class Accountのアクセスクラス.
//* @constructor
//* @augments dcc.AbstractODataContext
//*/
/**
 * It creates a new object dcc.cellctl.Account.
 * @class This class creates an Account as cell control object.
 * @constructor
 * @augments dcc.AbstractODataContext
 * @param {dcc.Accessor} Accessor
 * @param {Object} body 
 */
dcc.cellctl.Account = function(as, body) {
  this.initializeProperties(this, as, body);
};
dcc.DcClass.inherit(dcc.cellctl.Account, dcc.AbstractODataContext);

///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.Account} self
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.Account} self
 * @param {dcc.Accessor} as accessor
 * @param {Object} json object
 */
dcc.cellctl.Account.prototype.initializeProperties = function(self, as, json) {
  this.uber = dcc.AbstractODataContext.prototype;
  this.uber.initializeProperties(self, as);

///** クラス名. */
  /** Class name. */
  self.CLASSNAME = "Account";

  if (json !== undefined && json !== null) {
    self.rawData = json;
//  /** Account名. */
    /** account name. */
    self.name = json.Name;
  }
  if (typeof self.name === "undefined") {
    self.name = "";
  }
///** パスワード.オブジェクト渡しでAccountを作成する時にだけ利用できる.その後は削除する. */
  /** It is available only when you create the Account. */
  self.setPassword("");
};

///**
//* Account名の設定.
//* @param {String} value
//*/
/**
 * This method sets the account name.
 * @param {String} value
 */
dcc.cellctl.Account.prototype.setName = function(value) {
  this.name = value;
};

///**
//* Account名の取得.
//* @return {String} Account名
//*/
/**
 * This method gets the account name.
 * @return {String} Account name
 */
dcc.cellctl.Account.prototype.getName = function() {
  return this.name;
};

///**
//* パスワードの設定.
//* @param {String} value パスワード文字列
//*/
/**
 * This method sets the password.
 * @param {String} value password
 */
dcc.cellctl.Account.prototype.setPassword = function(value) {
  this.password = value;
};

///**
//* パスワードの取得.
//* @return {String} パスワード文字列
//*/
/**
 * This method gets the password.
 * @return {String} password value
 */
dcc.cellctl.Account.prototype.getPassword = function() {
  return this.password;
};

///**
//* ODataのキーを取得する.
//* @return {String} ODataのキー情報
//*/
/**
 * This method gets the Odata key.
 * @return {String} OData key information
 */
dcc.cellctl.Account.prototype.getKey = function() {
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
dcc.cellctl.Account.prototype.getClassName = function() {
  return this.CLASSNAME;
};

