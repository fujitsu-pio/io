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
//* @class DAOで発生するException.
//* @constructor
//*/
/**
 * It creates a new object dcc.ClientException.
 * @class This class represents the exceptions that occur in DAO.
 * @constructor
 * @param {String} msg
 * @param {String} code
 */
dcc.ClientException = function(msg, code) {
  this.uber = Error.prototype;
  this.initializeProperties(this, msg, code);
};
dcc.DcClass.inherit(dcc.ClientException, Error);

if (typeof exports === "undefined") {
  exports = {};
}
exports.ClientException = dcc.ClientException;

///**
//* プロパティを初期化する.
//* @param {dcc.ClientException} self
//* @param {String} msg エラーメッセージ
//* @param {String} code エラーコード
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.ClientException} self
 * @param {String} msg Error message
 * @param {String} code Status code
 */
dcc.ClientException.prototype.initializeProperties = function(self, msg, code) {
  self.name = "DcClientException";
  self.message = msg;
  /** Status Code. */
  self.code = code;
};

///**
//* 例外発生時のステータスコードを取得.
//* @return {String} ステータスコード
//*/
/**
 * This method is used to get the status code at the time of the exception.
 * @return {String} Status code
 */
dcc.ClientException.prototype.getCode = function() {
  return this.code;
};

