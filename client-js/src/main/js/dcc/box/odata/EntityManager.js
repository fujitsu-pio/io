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
//* @class EntityTypeのCRUDのためのクラス.
//* @constructor
//* @augments jEntitySet 
//*/
/**
 * It creates a new object dcc.box.odata.EntityManager.
 * @class This class is used for performing CRUD operations for Entity.
 * @constructor
 * @augments jEntitySet
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcCollection} collection
 */
dcc.box.odata.EntityManager = function(as, collection) {
  this.initializeProperties(this, as, collection);
};
dcc.DcClass.inherit(dcc.box.odata.EntityManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//*/
/**
 * This method initializes the properties of this class.
 * @param {Object} self
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcCollection} collection object
 */
dcc.box.odata.EntityManager.prototype.initializeProperties = function(self, as, collection) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as, collection);
};

/**
 * This method creates and returns the URL for performing Entity related operations.
 * @return {String} URL
 */
dcc.box.odata.EntityManager.prototype.getUrl = function() {
  var sb = "";
  sb += this.collection.getPath();
  return sb;
};

///**
//* Entityを作成.
//* @param jsonData Entityオブジェクト
//* @return {dcc.http.DcHttpClient} response
//*/
/**
 * This method creates an Entity for the data.
 * @param {Object} jsonData Entity object
 * @param {Object} options Callback object
 * @return {dcc.http.DcHttpClient} response
 */
dcc.box.odata.EntityManager.prototype.create = function(jsonData,options) {
  var headers ={};
  //var response = this.internalCreate(JSON.stringify(jsonData));
  var response = this._internalCreate(JSON.stringify(jsonData),headers,options);
  return response;
};

/**
 * This method retrieves the entity list.
 * @param {String} id Key to URL
 * return {Object} JSON response
 */
dcc.box.odata.EntityManager.prototype.retrieve = function(id) {
  var json = this._internalRetrieve(id);
  return json;
};

/**
 * The purpose of this method is to update entity.
 * @param {String} entityName entity Name
 * @param {Object} body data
 * @param {String} etag for backward compatibility,recommended replacement options.headers
 * @param {Object} options object optional containing callback, headers
 * @return {dcc.http.DcHttpClient} response
 */
dcc.box.odata.EntityManager.prototype.update = function(entityName, body, etag, options) {
  var response = null;
  var headers = {};
  response = this._internalUpdate(entityName, body, etag, headers, options);
  return response;
};

/**
 * The purpose of the following method is to delete an entity.
 * @param {String} entityTypeName
 * @param {String} etagOrOptions ETag value or options object having callback and headers
 * @return {dcc.Promise} promise
 */
dcc.box.odata.EntityManager.prototype.del = function(entityTypeName, etagOrOptions) {
  var key = encodeURIComponent("'" + entityTypeName + "'");
  var response = this._internalDelMultiKey(key, etagOrOptions);
  return response;
};
