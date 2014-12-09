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

///**
//* @namespace PCSクライアントライブラリクラス群を内包する名前空間。
//*/
/**
 * @namespace Namespace enclosing the PCS client library classes.
 */
var dcc = {};

/** Code group that operates HTTP.*/
dcc.http = {};

/** Code group to which browser is required as execution environment. */
dcc.browser = {};

/** Code group to which engine is required as execution environment. */
dcc.engine = {};

/** Code group used for Cell control objects except Box and the operations. */
dcc.cellctl = {};

/** Code group for unit control objects. */
dcc.unitctl = {};

/** Code group for processing in Box. */
dcc.box = {};

/** Code group for user OData processing. */
dcc.box.odata = {};

/** Code group for schema control processing of user OData. */
dcc.box.odata.schema = {};

///**
//* @class JS-DAOの動作設定情報を保持するオブジェクト.
//* @constructor
//*/
/**
 * It creates a new object dcc.ClientConfig.
 * @class This class is used for holding the operation setting information of JS-DAO.
 * @constructor
 */
dcc.ClientConfig = function() {
  this.initializeProperties();
};

///**
//* プロパティを初期化する.
//*/
/**
 * This method initializes the properties of this class.
 */
dcc.ClientConfig.prototype.initializeProperties = function() {
///** HTTPタイムアウト値 (number). */
  /** HTTP time-out value (number). */
  this.connectionTimeout = 0;

///** PUT/POST時にChunked指定をするかどうか (boolean). */
  /** Whether Chunked is specified at PUT / POST (boolean).*/
  this.chunked = true;

///** 通信を非同期で行うかどうか (boolean). */
  /** Whether is asynchronous communication (boolean). */
  // 現時点ではLogの書き込みのみ対応
  this.async = undefined;

///** HttpClientクラス. */
  /** HttpClient class. */
  this.httpClient = null;

///** テスト時に実通信を抑止するためのモッククラス. */
//this.mockRestAdapter = null;
};


///**
//* HTTPタイムアウト値を取得する.
//* @return {Number} タイムアウト値(ms)
//*/
/**
 * This method is used to get the HTTP time-out value.
 * @return {Number} Time-out value (ms)
 */
dcc.ClientConfig.prototype.getConnectionTimeout = function() {
  return this.connectionTimeout;
};

///**
//* HTTPタイムアウト値を設定する.
//* number型以外を指定した場合は例外をスローする.
//* @param {Number} value タイムアウト値(ms)
//*/
/**
 * This method is used to set the HTTP time-out value.
 * It throws an exception if a non-type is specified.
 * @param {Number} value Time-out value (ms)
 */
dcc.ClientConfig.prototype.setConnectionTimeout = function(value) {
  if (typeof value !== "number") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.connectionTimeout = value;
};

///**
//* Chunked指定の有無を取得する.
//* @return {boolean} Chunked値
//*/
/**
 * This method gets the Chunked attribute as specified.
 * @return {boolean} Chunked value
 */
dcc.ClientConfig.prototype.getChunked = function() {
  return this.chunked;
};

///**
//* Chunked指定の有無を設定する.
//* boolean型以外を指定した場合は例外をスローする.
//* @param {boolean} value Chunked値
//*/
/**
 * This method sets the Chunked attribute.
 * It throws an exception if a non-boolean type is specified.
 * @param {boolean} value Chunked value
 */
dcc.ClientConfig.prototype.setChunked = function(value) {
  if (typeof value !== "boolean") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.chunked = value;
};

///**
//* 非同期通信を行うかどうかを取得する.
//* @return {?} 非同期フラグ
//*/
/**
 * This method gets the asynchronous attribute as specified.
 * You will be prompted to asynchronous communication.
 * @return {Boolean} Asynchronous flag
 */
dcc.ClientConfig.prototype.getAsync = function() {
  return this.async;
};

///**
//* 非同期通信を行うか否かを設定する.
//* boolean型以外を指定した場合は例外をスローする.
//* @param {?} value 非同期フラグ
//*/
/**
 * This method sets the asynchronous attribute.
 * It throws an exception if a non-boolean type is specified.
 * @param {Boolean} value Asynchronous flag
 */
dcc.ClientConfig.prototype.setAsync = function(value) {
  if (typeof value !== "boolean") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.async = value;
};

///**
//* HttpClientオブジェクト取得.
//* @return {?} HttpClientオブジェクト
//*/
/**
 * This method acquires HttpClient object.
 * @return {dcc.http.DcHttpClient} HttpClient object
 */
dcc.ClientConfig.prototype.getHttpClient = function() {
  return this.httpClient;
};

///**
//* HttpClientオブジェクト設定.
//* @param {?} value HttpClientオブジェクト
//*/
/**
 * This method sets HttpClient object.
 * @param {dcc.http.DcHttpClient} value HttpClient object
 */
dcc.ClientConfig.prototype.setHttpClient = function(value) {
  this.httpClient = value;
};

///**
//* RestAdapterのモッククラスを取得.
//* @return RestAdapterモッククラス
//*/
////public final RestAdapter getMockRestAdapter() {
//dcc.ClientConfig.prototype.getMockRestAdapter = function() {
//return mockRestAdapter;
//};

///**
//* RestAdapterのモッククラスを設定.
//* @param value RestAdapterモッククラス
//*/
////public final void setMockRestAdapter(final RestAdapter value) {
//dcc.ClientConfig.prototype.setMockRestAdapter = function() {
//this.mockRestAdapter = value;
//};

