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
//* @class EventのCRUDためのクラス.
//* @constructor
//*/
/**
 * It creates a new object dcc.cellctl.EventManager.
 * @class This class performs the CRUD operations for Event object.
 * @constructor
 * @param {dcc.Accessor} as Accessor
 */
dcc.cellctl.EventManager = function(as) {
  this.initializeProperties(this, as);
};

///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.EventManager} self
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.EventManager} self
 * @param {dcc.Accessor} as Accessor
 */
dcc.cellctl.EventManager.prototype.initializeProperties = function(self, as) {
  if (as !== undefined) {
//  /** アクセス主体. */
    /** Access subject. */
    self.accessor = as.clone();
  }
};

///**
//* イベントを登録します.
//* @param {Object} body 登録するJSONオブジェクト
//* @param {String} requestKey RequestKeyフィールド
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to register the event.
 * @param {Object} body JSON object to be registered
 * @param {String} requestKey RequestKey object
 * @param {Object} options Callback object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.EventManager.prototype.post = function(body, requestKey,options) {
  var url = this.getUrl();
  var headers = {};
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  if ((requestKey === undefined) || (requestKey === null)) {
    restAdapter.post(url, JSON.stringify(body), "application/json",headers,options);
  } else {
    restAdapter.post(url, JSON.stringify(body), "application/json", {"X-Dc-RequestKey": requestKey},options);
  }
};

///**
//* イベントのURLを取得する.
//* @return {String} URL
//*/
/**
 * This method generates the URL for performing Event related operations.
 * @return {String} URL
 */
dcc.cellctl.EventManager.prototype.getUrl = function() {
  var sb = this.accessor.getCurrentCell().getUrl();
  sb += "__event";
  return sb;
};

