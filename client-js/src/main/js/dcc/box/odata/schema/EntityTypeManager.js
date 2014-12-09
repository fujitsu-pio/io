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
//* @augments dcc.box.odata.ODataManager
//*/
/**
 * It creates a new object dcc.box.odata.schema.EntityTypeManager.
 * @class This class performs The CRUD operations for EntityType.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcCollection} collection
 */
dcc.box.odata.schema.EntityTypeManager = function(as, collection) {
  this.initializeProperties(this, as, collection);
};
dcc.DcClass.inherit(dcc.box.odata.schema.EntityTypeManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//* @param {dcc.AbstractODataContext} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {?} collection
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.AbstractODataContext} self
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcCollection} collection
 */
dcc.box.odata.schema.EntityTypeManager.prototype.initializeProperties = function(self, as, collection) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as, collection);
};

///**
//* EntityTypeのURLを取得する.
//* @returns {String} URL
//*/
/**
 * This method generates the URL for performing EntityType operations.
 * @returns {String} URL
 */
dcc.box.odata.schema.EntityTypeManager.prototype.getUrl = function() {
  var sb = "";
  sb += this.collection.getPath();
  sb += "/$metadata/EntityType";
  return sb;
};

///**
//* EntityTypeを作成.
//* @param {dcc.box.odata.schema.EntityType} obj EntityTypeオブジェクト
//* @return {dcc.box.odata.schema.EntityType} EntityTypeオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used for performing create operation on EntityType.
 * @param {dcc.box.odata.schema.EntityType} obj EntityType object
 * @param {Object} options Callback object
 * @return {dcc.box.odata.schema.EntityType} EntityType object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.schema.EntityTypeManager.prototype.create = function(obj,options) {
  var async = this._isAsynchronous(options);
  var entityTypeInstantiation = function(accessor, json) {
      return new dcc.box.odata.schema.EntityType(accessor, json.d.results);
    };
  var json = null;
  //var responseJson = null;
  var headers = {};
  var thenable = null;
  if (obj.getClassName !== undefined && obj.getClassName() === "EntityType") {
    var body = {};
    body.Name = obj.getName();
    thenable = this._internalCreate(JSON.stringify(body), headers, options, entityTypeInstantiation);
    obj.initializeProperties(obj, this.accessor, json);
    return obj;
  } else {
    var requestBody = JSON.stringify(obj);
    thenable = this._internalCreate(requestBody,headers,options, entityTypeInstantiation);
    if (async) {
        return thenable;
    } else {
        return thenable.resolvedValue;
    }
    /*var callbackExist = options !== undefined &&
    (options.success !== undefined ||
        options.error !== undefined ||
        options.complete !== undefined);*/
    /*if (callbackExist) {
      this._internalCreate(requestBody,headers,options);
    } else {
      json = this._internalCreate(requestBody);
      if (json.getStatusCode() >= 400) {
        var response = json.bodyAsJson();
        throw new dcc.ClientException(response.message.value, response.code);
      }
      responseJson = json.bodyAsJson().d.results;
      return new dcc.box.odata.schema.EntityType(this.accessor, responseJson);
    }*/
  }
};

///**
//* EntityTypeを取得.
//* @param {String} name 取得対象のbox名
//* @return {dcc.box.odata.schema.EntityType} 取得したしたEntityTypeオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used for retrieve operation for EntityType.
 * @param {String} name EntityType name
 * @param {Object} options JSON object has callback and headers
 * @return {dcc.box.odata.schema.EntityType} EntityType object
 * @throws {dcc.ClientException} exception
 */
dcc.box.odata.schema.EntityTypeManager.prototype.retrieve = function(name, options) {
  var async = this._isAsynchronous(options);
  /*valid option is present with at least one callback*/
  /*var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);*/
    var entityTypeInstantiation = function(accessor, json) {
        return new dcc.box.odata.schema.EntityType(accessor, json.d.results);
      };
  var thenable = this._internalRetrieve(name, options, entityTypeInstantiation);
  if (async) {
      //asynchronous mode of execution, return DcResponseAsThenable
      return thenable;
    } else {
      //synchronous call execution
      return thenable.resolvedValue;//box instance
    }
  /*if (!callbackExist) {
    return new dcc.box.odata.schema.EntityType(this.accessor, json);
  }*/
};

/**
 * The purpose of this method is to update entity type.
 * @param {String} entityName name of the entity
 * @param {String} body
 * @param {String} etag value
 * @param {Object} options optional parameters containing callback, headers
 * @return {Object} response
 */
dcc.box.odata.schema.EntityTypeManager.prototype.update = function(entityName, body, etag, options) {
  var async = this._isAsynchronous(options);
  //var response = null;
  var headers = {};
  var thenable = this._internalUpdate(entityName, body, etag, headers, options);
  var response = thenable;
  if (!async) {
      response = thenable.resolvedValue;
    }
  return response;
};

/**
 * The purpose of this method is to return etag for
 * particular entity type.
 * @param {String} entityName name
 * @return {String} etag
 */
dcc.box.odata.schema.EntityTypeManager.prototype.getEtag = function(entityName) {
  var json = this._internalRetrieve(entityName);
  return json.__metadata.etag;
};

/**
 * The purpose of this method is to delete entity type.
 * @param {String} entityTypeName name of the entity
 * @param {String} etagOrOptions ETag value or options object having callback and headers
 * @return {Object} response
 */
dcc.box.odata.schema.EntityTypeManager.prototype.del = function(entityTypeName, etagOrOptions) {
  var async = this._isAsynchronous(etagOrOptions);
  var key = "Name='" + entityTypeName + "'";
  var thenable = this._internalDelMultiKey(key, etagOrOptions);
  var response = thenable;
  if(!async){
      response = thenable.resolvedValue;
  }
  return response;
};