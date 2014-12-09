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
//* @class Entityへアクセスするためのクラス.
//* @constructor
//* @augments dcc.box.DavCollection
//*/
/**
 * It creates a new object dcc.box.odata.ODataBatchResponse.
 * @class This class is the Response class for ODataBatch.
 * @constructor
 * @augments dcc.box.DavCollection
 * @param {Object} header
 * @param {Object} body
 */
dcc.box.odata.ODataBatchResponse = function(header, body) {
  this.initializeProperties(this, header, body);
};
dcc.DcClass.inherit(dcc.box.odata.ODataBatchResponse, dcc.AbstractODataContext);

///**
//* プロパティを初期化する.
//* @param {dcc.box.odata.ODataBatchResponse} self
//* @param {String} header ヘッダー情報
//* @param {String} body ボディ情報
//* @param {?} path
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.box.odata.ODataBatchResponse} self
 * @param {String} header Header information
 * @param {String} body Body information
 */
dcc.box.odata.ODataBatchResponse.prototype.initializeProperties = function(self, header, body) {
  self.rawHeader = header;
  self.body = body;
  self.headers = this.parseHeaders(header);
};

///**
//* ステータスコードの取得.
//* @return ステータスコード
//*/
/**
 * This method gets the statuc code of response.
 * @return {String} Status code
 */
dcc.box.odata.ODataBatchResponse.prototype.getStatusCode = function() {
  return this.statusCode;
};

///**
//* 解析前のヘッダー文字列を取得.
//* @return ヘッダー文字列
//*/
/**
 * This method gets the header string before analysis is performed.
 * @return {String} Header string
 */
dcc.box.odata.ODataBatchResponse.prototype.getRawHeaders = function() {
  return this.rawHeaders;
};

///**
//* レスポンスヘッダのハッシュマップを取得.
//* @return ヘッダのハッシュマップ
//*/
/**
 * This method gets a hash map of the response header.
 * @return {Array} Hash map of header
 */
dcc.box.odata.ODataBatchResponse.prototype.getHeaders = function() {
  return this.headers;
};

///**
//* 指定したレスポンスヘッダの値を取得する.
//* @param key ヘッダのキー
//* @return 指定したキーの値
//*/
/**
 * This method gets the value of the specified response header.
 * @param {String} key Key in header
 * @return {Array} Value of the specified key
 */
dcc.box.odata.ODataBatchResponse.prototype.getHeader = function(key) {
  return this.headers[key];
};

///**
//* レスポンスボディを文字列で取得.
//* @return ボディテキスト
//*/
/**
 * This method returns response body as String.
 * @return {String} Body text
 */
dcc.box.odata.ODataBatchResponse.prototype.bodyAsString = function() {
  return this.body;
};

///**
//* レスポンスボディをJSONで取得.
//* @return JSONオブジェクト
//* @throws ClientException DAO例外
//*/
/**
 * This method returns response body as Object.
 * @return {Object} JSON object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataBatchResponse.prototype.bodyAsJson = function() {
  return JSON.parse(this.body);
};
/**
 * This method parses the headers to return values in Array format.
 * @param {String} value Headers
 * @returns {Array} map
 */
dcc.box.odata.ODataBatchResponse.prototype.parseHeaders = function(value) {
  var lines = value.split("\n");
  // １行目がから ステータスコードを取得
  if (lines[0].startsWith("HTTP")) {
    this.statusCode = parseInt(lines[0].split(" ")[1], 10);
  }
  // ２行目以降のレスポンスヘッダをハッシュマップにセット
  var map = [];
  for (var i = 0; i < lines.length; i++) {
    var line = lines[i];
    var key = line.split(":");
    if (key.length > 1) {
      // 前後に空白が含まれている可能性があるため、トリムしてからセットする
      map[key[0].trim()] = key[1].trim();
    }
  }
  return map;
};
