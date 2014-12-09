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
//* @class ComplexTypeのCRUDのためのクラス.
//* @constructor
//* @augments jEntitySet
//*/
/**
 * It creates a new object dcc.box.odata.schema.ComplexTypeManager.
 * @class This class performs CRUD operations for CmplexType.
 * @constructor
 * @augments jEntitySet
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcCollection} collection
 */
dcc.box.odata.schema.ComplexTypeManager = function(as, collection) {
  this.initializeProperties(this, as, collection);
};
dcc.DcClass.inherit(dcc.box.odata.schema.ComplexTypeManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//*/
/**
 * This method initializes the properties of this class.
 * @param {Object} self
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcCollection} collection object
 */
dcc.box.odata.schema.ComplexTypeManager.prototype.initializeProperties = function(self, as, collection) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as, collection);
};

/**
 * This method gets the URL. 
 * @return {String} URL
 */
dcc.box.odata.schema.ComplexTypeManager.prototype.getUrl = function() {
  var sb = "";
  sb += this.collection.getPath();
  sb += "/$metadata/ComplexType";
  return sb;
};

///**
//* EntityTypeを作成.
//* @param obj EntityTypeオブジェクト
//* @return EntityTypeオブジェクト
//* @throws ClientException DAO例外
//*/
/**
 * This method performs create operation for ComplexType.
 * @param {Object} obj ComplexType object
 * @param {Object} options Callback
 * @return {Object} ComplexType object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.schema.ComplexTypeManager.prototype.create = function(obj,options) {
  var json = null;
  var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);
  if (callbackExist) {
    var headers = {};
    this._internalCreate(JSON.stringify(obj),headers,options);
  } else {
    json = this._internalCreate(JSON.stringify(obj));
    return json;
  }
};

///**
//* Boxを取得.
//* @param name 取得対象のbox名
//* @return {dcc.box.odata.schema.EntityType} object
//* @throws ClientException DAO例外
//*/
/**
 * This method performs retrieve operation for ComplexType.
 * @param {String} name ComplexType name
 * @return {dcc.box.odata.schema.EntityType} object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.schema.ComplexTypeManager.prototype.retrieve = function(name) {
  var json = this._internalRetrieve(name);
  return new dcc.box.odata.schema.EntityType(this.accessor, json);
};

/**
 * The purpose of this method is to create URL for delete operation.
 * @param {String} path
 * @param {String} complexType
 * @returns {String} URL
 */
dcc.box.odata.schema.ComplexTypeManager.prototype.getPath = function(path, complexType){
  var url = path + "/$metadata/ComplexType('" + complexType + "')";
  return url;
};

///**
//* 指定PathのデータをDeleteします(ETag指定).
//* @param pathValue DAVのパス
//* @param etagValue PUT対象のETag。新規または強制更新の場合は "*" を指定する
//* @return {dcc.Promise} promise
//* @throws ClientException DAO例外
//*/
/**
 * This method performs delete operation for ComplexType.
 * @param {String} path DAV path
 * @param {String} complexType
 * @param {Object} options optional parameters having callback and headers
 * @return {dcc.Promise} promise
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.schema.ComplexTypeManager.prototype.del = function(path, complexType, options) {
  /*if (typeof etagValue === undefined) {
    etagValue = "*";
  }*/
  var url = this.getPath(path, complexType);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.del(url, options);
  return response;
};