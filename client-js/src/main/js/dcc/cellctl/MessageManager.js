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
 * It creates a new object dcc.cellctl.MessageManager.
 * @class This class is used for sending and receiving messages.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @param {dcc.Accessor} as Accessor
 */
dcc.cellctl.MessageManager = function(as) {
  this.initializeProperties(this, as);
};
dcc.DcClass.inherit(dcc.cellctl.MessageManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.MessageManager} self
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.MessageManager} self
 * @param {dcc.Accessor} as Accessor
 */
dcc.cellctl.MessageManager.prototype.initializeProperties = function(self, as) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as);

///** 送信メッセージのマネージャクラス. */
  /** Manager class of outgoing messages. */
  self.sent = null;
///** 受信メッセージのマネージャクラス. */
  /** Manager class of incoming messages. */
  self.received = null;

  if (as !== undefined) {
    self.sent = new dcc.cellctl.SentMessageManager(as, this);
    self.received = new dcc.cellctl.ReceivedMessageManager(as);
  }
};

///**
//* URLを取得する.
//* @returns {String} URL
//*/
/**
 * This method returns the URL for sending messages.
 * @returns {String} URL
 */
dcc.cellctl.MessageManager.prototype.getUrl = function() {
  var sb = "";
  sb += this.getBaseUrl();
  sb += this.accessor.getCurrentCell().getName();
  sb += "/__message/send";
  return sb;
};

///**
//* 送信メッセージオブジェクトを作成.
//* @param {object} json Jsonオブジェクト
//* @return {dcc.cellctl.Message} メッセージオブジェクト
//* @throws {ClientException} DAO例外
//*/
//dcc.cellctl.MessageManager.prototype.sendMail = function(json) {

////String boxBound
////,String inReplyTo
////,String to
////,String toRelation
////,String title
////,String body
////,int priority

//return new dcc.cellctl.Message(this.accessor, json);
//};

///**
//* メッセージを送信する.
//* @param {dcc.cellctl.Message} message 送信するメッセージオブジェクト
//* @return {dcc.cellctl.Message} 取得したメッセージオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to send a message.
 * @param {dcc.cellctl.Message} message Message object to be sent
 * @param {Object} options Callback object.
 * @return {dcc.cellctl.Message} Message object received
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.MessageManager.prototype.send = function(message,options) {
  var async = this._isAsynchronous(options);
  var messageInstantiation = function(accessor, json){
    return new dcc.cellctl.Message(accessor, json.d.results);
  };
  //var responseJson = {};
  var requestBody = JSON.stringify(message);
  var thenable = this._internalCreate(requestBody,{},options, messageInstantiation);
  if (async) {
    return thenable;
  } else {
    return thenable.resolvedValue;
  }
/*  var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);
  if (callbackExist) {
    this._internalCreate(requestBody,{},options);
  } else {
    var json = this._internalCreate(requestBody);
    var responseBody = json.bodyAsJson();
    if (responseBody.d !== undefined && responseBody.d.results !== undefined) {
      responseJson = responseBody.d.results;
    }
    return new dcc.cellctl.Message(this.accessor, responseJson);
  }*/
};

///**
//* メッセージを既読にする.
//* @param {String} messageId メッセージID
//* @return {dcc.cellctl.Message} 取得したメッセージオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to read a message.
 * @param {String} messageId messageID
 * @param {Object} options Callback object.
 * @return {dcc.cellctl.Message} Message object obtained
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.MessageManager.prototype.changeMailStatusForRead = function(messageId,options) {
  var statusManager = new dcc.cellctl.MessageStatusManager(this.accessor, messageId);
  return statusManager.changeMailStatusForRead(options);
};

///**
//* メッセージを未読にする.
//* @param {String} messageId メッセージID
//* @return {dcc.cellctl.Message} 取得したメッセージオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to unread a message.
 * @param {String} messageId messageID
 * @param {Object} options Callback object.
 * @return {dcc.cellctl.Message} Message object obtained
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.MessageManager.prototype.changeMailStatusForUnRead = function(messageId,options) {
  var statusManager = new dcc.cellctl.MessageStatusManager(this.accessor, messageId);
  return statusManager.changeMailStatusForUnRead(options);
};

///**
//* メッセージを承認する.
//* @param {String} messageId メッセージID
//* @return {dcc.cellctl.Message} 取得したメッセージオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to approve a message.
 * @param {String} messageId messageID
 * @param {Object} options Callback object.
 * @return {dcc.cellctl.Message} Message object obtained
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.MessageManager.prototype.approveConnect = function(messageId,options) {
  var statusManager = new dcc.cellctl.MessageStatusManager(this.accessor, messageId);
  return statusManager.approveConnect(options);
};

///**
//* メッセージを拒否する.
//* @param {String} messageId メッセージID
//* @return {dcc.cellctl.Message} 取得したメッセージオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to reject a message.
 * @param {String} messageId messageID
 * @param {Object} options Callback object.
 * @return {dcc.cellctl.Message} Message object obtained
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.MessageManager.prototype.rejectConnect = function(messageId,options) {
  var statusManager = new dcc.cellctl.MessageStatusManager(this.accessor, messageId);
  return statusManager.rejectConnect(options);
};
