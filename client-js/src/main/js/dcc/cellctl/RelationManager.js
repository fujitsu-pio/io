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
//* @class RelationのCRUDのためのクラス.
//* @constructor
//* @augments dcc.box.odata.ODataManager
//*/
/**
 * It creates a new object dcc.cellctl.RelationManager.
 * @class This class performs CRUD operations for Relation object.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @param {dcc.Accessor} as Accessor
 */
dcc.cellctl.RelationManager = function(as) {
  this.initializeProperties(this, as);
};
dcc.DcClass.inherit(dcc.cellctl.RelationManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//* @param {dcc.AbstractODataContext} self
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.AbstractODataContext} self
 * @param {dcc.Accessor} as Accessor
 */
dcc.cellctl.RelationManager.prototype.initializeProperties = function(self, as) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as);
};

///**
//* RelationのURLを取得する.
//* @returns {String} URL
//*/
/**
 * This method generates the URL for relation operations.
 * @returns {String} URL
 */
dcc.cellctl.RelationManager.prototype.getUrl = function() {
  var sb = this.getBaseUrl();
  // HCL:-Changes done to get the cellName
  sb += this.accessor.cellName;
  sb += "/__ctl/Relation";
  return sb;
};

///**
//* Relationを作成.
//* @param {dcc.cellctl.Relation} obj Relationオブジェクト
//* @return {dcc.cellctl.Relation} Relationオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs create operation.
 * @param {dcc.cellctl.Relation} obj Relation object
 * @param {Object} options object
 * @return {dcc.cellctl.Relation} Relation object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.RelationManager.prototype.create = function(obj,options) {
  var async = this._isAsynchronous(options);
  var relationInstantiation = function(accessor, json) {
    return new dcc.cellctl.Relation(accessor, json.d.results);
  };
  //var json = null;
  var requestBody = JSON.stringify(obj);
  var headers = {};
  var thenable = null;
  if (obj.getClassName !== undefined && obj.getClassName() === "Relation") {
    var body = {};
    body.Name = obj.getName();
    body["_Box.Name"] = obj.getBoxName();
    thenable = this._internalCreate(JSON.stringify(body), headers, options, relationInstantiation);
    /*obj.initializeProperties(obj, this.accessor, json);
    return obj;*/
  } else {
    thenable = this._internalCreate(requestBody, headers, options, relationInstantiation);
/*  var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);
  if (callbackExist) {
    this._internalCreate(requestBody,headers,options);
    //return new dcc.cellctl.Relation(this.accessor, json.bodyAsJson().d.results);
  } else {
    json = this._internalCreate(requestBody);
        if (json.getStatusCode() >= 400) {
      var response = json.bodyAsJson();// throw exception with code
      throw new dcc.ClientException(response.message.value, response.code);
    }
    return new dcc.cellctl.Relation(this.accessor, json.bodyAsJson().d.results);
  }*/
  }
  if (async) {
    return thenable;
  } else {
    return thenable.resolvedValue;
  }
};

///**
//* Relationを作成.
//* @param {Object} body リクエストボディ
//* @return {dcc.cellctl.Relation} 作成したRelationオブジェクト
//* @throws {ClientException} DAO例外
//*/
//dcc.cellctl.RelationManager.prototype.createAsMap = function(body) {
//var json = _internalCreate(body);
//return new dcc.cellctl.Relation(accessor, json);
//};

///**
//* Relationを取得(複合キー).
//* @param {String} relationName 取得対象のRelation名
//* @param {String}boxName 取得対象のBox名
//* @return {dcc.cellctl.Relation} 取得したRelationオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs retrieve operation.
 * @param {String} relationName Relation name
 * @param {String} boxName Box name
 * @param {Object} options object has callback and headers 
 * @return {dcc.cellctl.Relation} Relation object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.RelationManager.prototype.retrieve = function(relationName, boxName, options) {
  var async = this._isAsynchronous(options);
  var relationInstantiation = function(accessor, json) {
    return new dcc.cellctl.Relation(accessor, json.d.results);
  };
  var key = "Name='" + relationName + "',_Box.Name='" + boxName + "'";
  var thenable = null;
  if (typeof boxName === "undefined") {
    thenable = this._internalRetrieve(relationName, options, relationInstantiation);
  } else {
    thenable = this._internalRetrieveMultikey(key, options, relationInstantiation);
  }
  if (async) {
    //asynchronous mode of execution, return DcResponseAsThenable
    return thenable;
  } else {
    //synchronous call execution
    return thenable.resolvedValue;//relation instance
  }
  /*valid option is present with atleast one callback*/
  /*var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);
  var json = null;
  var key = "Name='" + relationName + "',_Box.Name='" + boxName + "'";
  if (callbackExist) {
    if (typeof boxName === "undefined") {
      this._internalRetrieve(relationName, options);
      return;
    }
    json = this._internalRetrieveMultikey(key, options);
    return;
  }
  if (typeof boxName === "undefined") {
    json = this._internalRetrieve(relationName);
    //relation doesn't exist and can be created.
    //if (json === true) {
    //  return json;
    //} else {
    return new dcc.cellctl.Relation(this.accessor, json);
    //}
  }
  json = this._internalRetrieveMultikey(key);
  //relation doesn't exist and can be created.
  //if (json === true) {
  //  return json;
  //} else {
  return new dcc.cellctl.Relation(this.accessor, json);*/
  //}
};

///**
//* Relation update.
//* @param {String} relationName 削除対象のRelation名
//* @param {String} boxName 削除対象のBox名
//* @param body
//* @param etag
//* @return promise
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs update operation.
 * @param {String} relationName Relation name
 * @param {String} boxName Box name
 * @param {Object} body
 * @param {String} etag
 * @param {Object} options object optional containing callback, headers
 * @return {dcc.Promise} promise
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.RelationManager.prototype.update = function(relationName, boxName, body, etag, options) {
  var async = this._isAsynchronous(options);
  var headers = {};
  if (typeof boxName === "object") {
    etag = body;
    body = boxName;
    boxName = null;
  }
  var thenable = null;
  if (boxName !== undefined && boxName !== null) {
    var key = "Name='" + relationName + "',_Box.Name='" + boxName + "'";
    thenable = this._internalUpdateMultiKey(key, body, etag, headers, options);
  } else {
    thenable = this._internalUpdate(relationName, body, etag, headers, options);
  }
  var response = thenable;
  if (!async) {
    response = thenable.resolvedValue;
  }
  return response;
};

///**
//* Relationを削除(複合キー).
//* @param {String} relationName 削除対象のRelation名
//* @param {String} boxName 削除対象のBox名
//* @return promise
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs delete operation.
 * @param {String} relationName Relation name
 * @param {String} boxName Box name
 * @param {String} etagOrOptions ETag value or options object having callback and headers
 * @return {dcc.Promise} promise
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.RelationManager.prototype.del = function(relationName, boxName, etagOrOptions) {
  var async = this._isAsynchronous(etagOrOptions);
  var key = "Name='"+relationName+"'";
  if (boxName !== undefined && boxName !== null && boxName !== "__") {
    key += ",_Box.Name='"+boxName+"'";
  }
  var thenable = this._internalDelMultiKey(key, etagOrOptions);
  var response = thenable;
  if(!async){
      response = thenable.resolvedValue;
  }
  return response;
};

