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
 * It creates a new object dcc.cellctl.SentMessageManager.
 * @class This class performs CRUD for SEnt Messages.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @param {dcc.Accessor} as Accessor
 */
dcc.cellctl.SentMessageManager = function(as) {
  this.initializeProperties(this, as);
};
dcc.DcClass.inherit(dcc.cellctl.SentMessageManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.SentMessageManager} self
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.SentMessageManager} self
 * @param {dcc.Accessor} as Accessor
 */
dcc.cellctl.SentMessageManager.prototype.initializeProperties = function(self, as) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as);
};

///**
//* URLを取得する.
//* @returns {String} URL
//*/
/**
 * This method returns the URL.
 * @returns {String} URL
 */
dcc.cellctl.SentMessageManager.prototype.getUrl = function() {
  var sb = "";
  sb += this.getBaseUrl();
  sb += this.accessor.getCurrentCell().getName();
  sb += "/__ctl/SentMessage";
  return sb;
};

///**
//* 送信メッセージを取得.
//* @param {String} messageId メッセージID
//* @return {dcc.cellctl.Message} 取得したメッセージオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method gets the outgoing messages.
 * @param {String} messageId MessageID
 * @param {Object} options object has callback and headers
 * @return {dcc.cellctl.Message} Message object 
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.SentMessageManager.prototype.retrieve = function(messageId, options) {
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
 /* var callbackExist = options !== undefined &&
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

/**
 * This method delete message on the basis of messageID
 * @param {String} messageId
 * @param {String} options
 * @returns {dcc.Promise} response
 */
dcc.cellctl.SentMessageManager.prototype.del = function(messageId, options) {
  var async = this._isAsynchronous(options);
  var key = "'" + messageId + "'";
  var thenable = this._internalDelMultiKey(key, options);
  var response = thenable;
  if(!async){
      response = thenable.resolvedValue;
  }
  return response;
};
