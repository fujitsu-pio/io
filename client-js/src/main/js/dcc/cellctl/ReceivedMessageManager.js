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
 * It creates a new object dcc.cellctl.ReceivedMessageManager.
 * @class This class is used for sending and receiving messages.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @param {dcc.Accessor} as Accessor
 * @param {String} message
 */
dcc.cellctl.ReceivedMessageManager = function(as, message) {
  this.initializeProperties(this, as, message);
};
dcc.DcClass.inherit(dcc.cellctl.ReceivedMessageManager, dcc.box.odata.ODataManager);

///**
//* クラス名をキャメル型で取得する.
//* @return {?} ODataのキー情報
//*/
/**
 * This method gets the class name.
 * @return {String} OData Class name
 */
dcc.cellctl.ReceivedMessageManager.prototype.getClassName = function() {
  return this.CLASSNAME;
};

///**
//* ReceivedMessageManagerオブジェクトのキーを取得する.
//* @return {String} ODataのキー情報
//*/
/**
 * This method returns the key.
 * @return {String} OData Key
 */
dcc.cellctl.ReceivedMessageManager.prototype.getKey = function() {
  return "('" + this.message.messageId + "')";
};


///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.ReceivedMessageManager} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {dcc.cellctl.Message} メッセージオブジェクト
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.ReceivedMessageManager} self
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.cellctl.Message} message Message object
 */
dcc.cellctl.ReceivedMessageManager.prototype.initializeProperties = function(self, as, message) {

///** クラス名. */
  /** Class name in camel case. */
  self.CLASSNAME = "ReceivedMessage";


  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as);
  this.message = message;
};

///**
//* URLを取得する.
//* @returns {String} URL
//*/
/**
 * This method returns the URL.
 * @returns {String} URL
 */
dcc.cellctl.ReceivedMessageManager.prototype.getUrl = function() {
  var sb = "";
  sb += this.getBaseUrl();
  sb += this.accessor.getCurrentCell().getName();
  sb += "/__ctl/ReceivedMessage";
  return sb;
};

///**
//* 受信メッセージを取得.
//* @param {String} messageId メッセージID
//* @return {dcc.cellctl.Message} 取得したメッセージオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method gets the received message.
 * @param {String} messageId MessageID
 * @param {Object} options object has callback and headers
 * @return {dcc.cellctl.Message} Message object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.ReceivedMessageManager.prototype.retrieve = function(messageId, options) {
  var async = this._isAsynchronous(options);
  var messageInstantiation = function(accessor, json){
    return new dcc.cellctl.Message(accessor, json.d.results);
  };
  var thenable = this._internalRetrieve(messageId, options, messageInstantiation);
  if (async) {
    return thenable;
  } else {
    return thenable.resolvedValue;
  }
  /*valid option is present with atleast one callback*/
  /*var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);

  if(callbackExist) {
    this._internalRetrieve(messageId, options);
    return;
  }
  var json = this._internalRetrieve(messageId);
  return new dcc.cellctl.Message(this.accessor, json);*/
};

///**
//* ReceivedMessageManager Accountに紐づく受信メッセージ一覧または受信メッセージに紐付くAccount一覧.
//* @param {dcc.cellctl.Account} account メッセージを取得するAccount
//* accountがundefinedの場合は受信メッセージに紐付くAccount一覧を取得
//* @return {dcc.box.odata.ODataResponse} 一覧取得のレスポンス
//* @throws {ClientException} DAO例外
//*/
/**
 * Account list associated with their incoming messages or incoming message
 * list brute string to ReceivedMessageManager Account.
 * @param {dcc.cellctl.Account} account Message Account
 * Get the Account list tied with the received message if account is undefined
 * @return {dcc.box.odata.ODataResponse} Response List
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.ReceivedMessageManager.prototype.listOfReadStatus = function(account) {
  var linkManager;
  if(account === undefined){
    linkManager = new dcc.cellctl.LinkManager(this.accessor, this, "AccountRead");
  }else{
    linkManager = new dcc.cellctl.LinkManager(account.accessor, account, "ReceivedMessageRead");
  }

  // $linksのinlinecountは取得できない(coreで対応していないため)
  var res = linkManager.query().inlinecount("allpages").runAsResponse();
  return res;
};

///**
//* changeMailStatusForRead Account毎の既読.
//* @param {dcc.cellctl.Account} account 既読にするAccount
//* @throws {ClientException} DAO例外
//*/
/**
 * This method reads each of changeMailStatusForRead Account.
 * @param {dcc.cellctl.Account} account Account object
 * @param {Object} options object callback object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.ReceivedMessageManager.prototype.changeMailStatusForRead = function(account,options) {
  var linkManager = new dcc.cellctl.LinkManager(this.accessor, this, "AccountRead");
  linkManager.link(account,options);
/*  if (options !== undefined) {
    linkManager.link(account,options);
    return;
  }
  linkManager.link(account);*/
};

///**
//* changeMailStatusForUnRead Account毎の未読.
//* @param {dcc.cellctl.Account} account 既読にするAccount
//* @throws {ClientException} DAO例外
//*/
/**
 * This method unreads each of changeMailStatusForRead Account.
 * @param {dcc.cellctl.Account} account Account object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.ReceivedMessageManager.prototype.changeMailStatusForUnRead = function(account) {
  var linkManager = new dcc.cellctl.LinkManager(this.accessor, this, "AccountRead");
  linkManager.unlink(account);
};

/**
 * This method delete message on the basis of messageID.
 * @param {String} messageId
 * @param {String} options
 * @returns {dcc.Promise} response
 */
dcc.cellctl.ReceivedMessageManager.prototype.del = function(messageId, options) {
  var async = this._isAsynchronous(options);
  var key = "'" + messageId + "'";
  var thenable = this._internalDelMultiKey(key, options);
  var response = thenable;
  if(!async){
      response = thenable.resolvedValue;
  }
  return response;
};