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
//* ＄Batchアクセスのためのリクエストを作成するクラス..
//* @class Represents a command. 
//*/
/**
 * It creates a new object dcc.box.odata.Command.
 * @class This class is used to create a request for $ Batch access.
 * @constructor
 * @param {String} batchBoundary
 */
dcc.box.odata.Command = function(batchBoundary) {
  this.initializeProperties(this, batchBoundary);
};

///**
//* オブジェクトを初期化.
//* @param {dcc.box.odata.schema.EntityType} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {Object} json サーバーから返却されたJSONオブジェクト
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.box.odata.schema.EntityType} self
 * @param {String} as BatchBoundary
 */
dcc.box.odata.Command.prototype.initializeProperties = function(self, batchBoundary) {
  self.url = null;
  self.method = null;
  self.etag = null;
  self.body = null;
  self.contentLength = 0;
  self.headers = [];
  self.batchBoundary = batchBoundary;
};

/**
 * This method sets the Body.
 * @param {String} body to set.
 */
dcc.box.odata.Command.prototype.setBody = function(value) {
  this.body = value;
//this.body = value.replace(/\"/g,"\\\"");
  try {
    this.contentLength = this.body  .length;
  } catch (e) {
    throw e;
  }
};

/**
 * This method adds key value pair in array to create headers.
 * @param {String} key
 * @param {String} value
 */
dcc.box.odata.Command.prototype.addHeader = function(key, value) {
  this.headers[key] = value;
};

/**
 * This method gets the Batch string data.
 * @returns {String} sb
 */
dcc.box.odata.Command.prototype.get = function() {
  var sb = "";
  // GET
  if (this.method === "GET") {
    sb += ("--" + this.batchBoundary + "\r\n");
    sb += ("Content-Type: application/http" + "\r\n");
    sb += ("Content-Transfer-Encoding:binary" + "\r\n");
    sb += "\r\n";
  }

  // method url http-ver
  sb += (this.method + " " + this.url + " HTTP/1.1" + "\r\n");
  // host
  sb += ("Host: " + "\r\n");
  // header
  for (var header in this.headers) {
    sb += header + ":" + (this.headers[header] + "\r\n");
  }
  // Content-Length
  sb += ("Content-Length: " + this.contentLength + "\r\n");
  // If-Match
  if (null !== this.etag) {
    sb += ("If-Match: " + this.etag + "\r\n");
  }
  if (("POST" === this.method) || ("PUT" === this.method)) {
    sb += ("\r\n");
    sb += (this.body + "\r\n");
  }
  return sb;
};
