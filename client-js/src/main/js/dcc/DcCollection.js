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
//* @class コレクションの抽象クラス.
//* @constructor
//* @augments dcc.AbstractODataContext
//*/
/**
 * It creates a new object dcc.DcCollection.
 * @class This is an abstract class for a collection.
 * @constructor
 * @augments dcc.AbstractODataContext
 * @param {dcc.Accessor} Accessor
 * @param {String} path
 */
dcc.DcCollection = function(as, path) {
  this.initializeProperties(this, as, path);
};
dcc.DcClass.inherit(dcc.DcCollection, dcc.AbstractODataContext);

///**
//* プロパティを初期化する.
//* @param {dcc.DcCollection} self
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.DcCollection} self
 * @param {dcc.Accessor} as Accessor
 */
dcc.DcCollection.prototype.initializeProperties = function(self, as, path) {
  this.uber = dcc.AbstractODataContext.prototype;
  this.uber.initializeProperties(self, as);

  if (as !== undefined) {
    self.accessor = as.clone();
  }

///** キャメル方で表現したクラス名. */
  /** Class name in camel case. */
  self.CLASSNAME = "";
///** コレクションのパス. */
  /**  path of the collection. */
  self.url = path;

};

///**
//* URLを取得.
//* @return {String} URL文字列
//*/
/**
 * This method returns the URL.
 * @return {String} URL Stirng
 */
dcc.DcCollection.prototype.getPath = function() {
  return this.url;
};

///**
//* ODataのキーを取得する.
//* @return {String} ODataのキー情報
//*/
/**
 * This method gets the key for Odata.
 * @return {String} OData Key information
 */
dcc.DcCollection.prototype.getKey = function() {
  return "";
};

///**
//* クラス名をキャメル型で取得する.
//* @return {?} ODataのキー情報
//*/
/**
 * This method returns the class name.
 * @return {String} OData class name
 */
dcc.DcCollection.prototype.getClassName = function() {
  return this.CLASSNAME;
};

/**
 * The purpose of this method is to perform service configure operation for both single or multiple service
 * in one API call. 
 * @param {array} arrServiceNameAndSrcFile service list in combination of service name and source file
 * example {"serviceName":"name","sourceFileName" : "filename.js"}.
 * @param {String} subject Service
 * @param {Object} options refers to optional parameters - callback, headers.
 * @return {dcc.http.DcHttpClient} response
 * @throws {dcc.ClientException} Exception
 */
dcc.DcCollection.prototype.proppatch = function(arrServiceNameAndSrcFile, subject, options) {
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.setService(this.getPath(), arrServiceNameAndSrcFile, subject, options);
  return response;
};

/**
 * This method determine whether the requested execution mode is synchronous or asynchronous.
 * Preference to mode specification is evaluated in order of precedence as options, accessor, library default.
 * @param {JSON} options
 * @returns {Boolean} response true if call is asynchronous else false
 */
dcc.DcCollection.prototype._isAsynchronous = function(options) {
  //if options.async is present then use async as specified mode
  if(options !== undefined && options !== null && options.async !== undefined){
    //true or false
    return options.async;
  }else if (this.accessor.getContext().getAsync() !== undefined){
    //if no specification is found at option level, use accessor level settings
    return this.accessor.getContext().getAsync();
  } else{
    return false;
  }
};

