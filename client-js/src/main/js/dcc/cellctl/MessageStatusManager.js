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
//* @class メッセージの送受信のためのクラス.
//* @constructor
//* @augments dcc.box.odata.ODataManager
//*/
/**
 * It creates a new object dcc.cellctl.MessageStatusManager.
 * @class This class is used for sending and receiving messages.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @param {dcc.Accessor} as Accessor
 * @param {String} messageId
 */
dcc.cellctl.MessageStatusManager = function(as, messageId) {
  this.initializeProperties(this, as, messageId);
};
dcc.DcClass.inherit(dcc.cellctl.MessageStatusManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.MessageStatusManager} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {string} messageId メッセージID
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.MessageStatusManager} self
 * @param {dcc.Accessor} as Accessor
 * @param {string} messageId messageID
 */
dcc.cellctl.MessageStatusManager.prototype.initializeProperties = function(self, as, messageId) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as);

  self.messageId = messageId;
};

///**
//* URLを取得する.
//* @returns {String} URL
//*/
/**
 * This method returns the URL for receiving messages.
 * @returns {String} URL
 */
dcc.cellctl.MessageStatusManager.prototype.getUrl = function() {
  var sb = "";
  sb += this.getBaseUrl();
  sb += this.accessor.getCurrentCell().getName();
  sb += "/__message/received/";
  sb += this.messageId;
  return sb;
};

///**
//* メッセージを既読にする.
//* @return {dcc.cellctl.Message} 取得したメッセージオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to read a message.
 * @param {Object} options Callback object.
 * @return {dcc.cellctl.Message} Message object obtained
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.MessageStatusManager.prototype.changeMailStatusForRead = function(options) {
  var requestBody = {"Command" : "read"};
  if (options!== undefined) {
    this._internalCreate(JSON.stringify(requestBody),{},options);
  } else {
    var json = this._internalCreate(JSON.stringify(requestBody));
    return new dcc.cellctl.Message(this.accessor, json);
  }
};

///**
//* メッセージを未読にする.
//* @return {dcc.cellctl.Message} 取得したメッセージオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to unread a message.
 * @param {Object} options Callback object.
 * @return {dcc.cellctl.Message} Message object obtained
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.MessageStatusManager.prototype.changeMailStatusForUnRead = function(options) {
  var requestBody = {"Command" : "unread"};
  if (options!== undefined) {
    this._internalCreate(JSON.stringify(requestBody),{},options);
  } else {
    var json = this._internalCreate(JSON.stringify(requestBody));
    return new dcc.cellctl.Message(this.accessor, json);
  }
};

///**
//* メッセージを承認する.
//* @return {dcc.cellctl.Message} 取得したメッセージオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to approve a message.
 * @param {Object} options Callback object.
 * @return {dcc.cellctl.Message} Message object obtained
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.MessageStatusManager.prototype.approveConnect = function(options) {
  var requestBody = {"Command" : "approved"};
  if (options!== undefined) {
    this._internalCreate(JSON.stringify(requestBody),{},options);
  } else {
    var json = this._internalCreate(JSON.stringify(requestBody));
    return new dcc.cellctl.Message(this.accessor, json);
  }
};

///**
//* メッセージを拒否する.
//* @return {dcc.cellctl.Message} 取得したメッセージオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to reject a message.
 * @param {Object} options Callback object.
 * @return {dcc.cellctl.Message} Message object obtained
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.MessageStatusManager.prototype.rejectConnect = function(options) {
  var requestBody = {"Command" : "rejected"};
  if (options!== undefined) {
    this._internalCreate(JSON.stringify(requestBody),{},options);
  } else {
    var json = this._internalCreate(JSON.stringify(requestBody));
    return new dcc.cellctl.Message(this.accessor, json);
  }
};
