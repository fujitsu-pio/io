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
//* @class Boxへアクセスするためのクラス.
//* @constructor
//* @augments dcc.AbstractODataContext
//*/
/**
 * It creates a new object dcc.cellctl.Message.
 * @class This class represents Message object for Sent and Received Messages functionality.
 * @constructor
 * @augments dcc.AbstractODataContext
 * @param {dcc.Accessor} as Accessor
 * @param {Object} json
 */
dcc.cellctl.Message = function(as, json) {
  this.initializeProperties(this, as, json);
};
dcc.DcClass.inherit(dcc.cellctl.Message, dcc.AbstractODataContext);

///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.Message} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {Object} json JSONオブジェクト
//* @param {?} path
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.Message} self
 * @param {dcc.Accessor} as Accessor
 * @param {Object} json JSON object
 */
dcc.cellctl.Message.prototype.initializeProperties = function(self, as, json) {
  this.uber = dcc.AbstractODataContext.prototype;
  this.uber.initializeProperties(self, as);

///** 送信メッセージのマネージャクラス. */
  /** Manager class of outgoing messages. */
  self.sent = null;
///** 受信メッセージのマネージャクラス. */
  /** Manager class of the incoming messages. */
  self.received = null;

  if (json !== undefined && json !== null) {
    self.body = json;
    if (json.__id !== undefined) {
      self.messageId = json.__id;
    }
  }

  if (as !== undefined) {
    self.sent = new dcc.cellctl.SentMessageManager(as, this);
    self.received = new dcc.cellctl.ReceivedMessageManager(as, this);
  }
};

///**
//* ボディを取得.
//* @return {?} ボディ
//*/
/**
 * This method returns the json as body.
 * @return {Object} Body
 */
dcc.cellctl.Message.prototype.getBody = function() {
  return this.body;
};
