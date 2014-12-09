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
//* @class RoleのCRUDのためのクラス.
//* @constructor
//* @augments dcc.box.odata.ODataManager
//*/
/**
 * It creates a new object dcc.cellctl.RoleManager.
 * @class This class performs CRUD operations for Role object.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @param {dcc.Accessor} as Accessor
 */
dcc.cellctl.RoleManager = function(as) {
  this.initializeProperties(this, as);
};
dcc.DcClass.inherit(dcc.cellctl.RoleManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//* @param {dcc.Accessor} self
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.Accessor} self
 * @param {dcc.Accessor} as Accessor
 */
dcc.cellctl.RoleManager.prototype.initializeProperties = function(self, as) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as);
};

///**
//* RoleのリクエストURLを取得する.
//* @returns {String} URL
//*/
/**
 * This method gets the URL for Role operations.
 * @returns {String} URL
 */
dcc.cellctl.RoleManager.prototype.getUrl = function() {
  var sb = "";
  sb += this.getBaseUrl();
  // HCL:-Changes done to get the cellName

  sb += this.accessor.cellName;
  sb += "/__ctl/Role";
  return sb;
};

///**
//* Roleを作成.
//* @param {dcc.cellctl.Role} obj Roleオブジェクト
//* @return {dcc.cellctl.Role} Roleオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method creates a Role.
 * @param {dcc.cellctl.Role} obj Role object
 * @param {Object} options object
 * @return {dcc.cellctl.Role} Role object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.RoleManager.prototype.create = function(obj, options) {
  var json = null;
  var responseJson = null;
  var requestBody = JSON.stringify(obj);
  var headers = {};
  var thenableResponse = null;
  var roleInstantiaton = function(accessor, json){
    return new dcc.cellctl.Role(accessor, json.d.results);
  };
  
  if (obj.getClassName !== undefined && obj.getClassName() === "Role") {
    var body = {};
    body.Name = obj.getName();
    body["_Box.Name"] = obj.getBoxName();
    json = this._internalCreate(JSON.stringify(body),headers,options);
    //responseJson = json.bodyAsJson().d.results;
    responseJson = JSON.parse(json.resolvedValue.responseText).d.results;
    obj.initializeProperties(obj, this.accessor, responseJson);
    return obj;
  }
  var async = this._isAsynchronous(options);
  if(async){
    //asynchronous call
    thenableResponse =  this._internalCreate(requestBody,headers,options, roleInstantiaton);
    return thenableResponse;
  }
  else{
    //synchronous call, return role instance
    thenableResponse = this._internalCreate(requestBody,headers,options,roleInstantiaton);
    var role = thenableResponse.resolvedValue;
    return role;
  }
};

/*dcc.cellctl.RoleManager.prototype.create = function(obj, options) {
  var json = null;
  var responseJson = null;
  var requestBody = JSON.stringify(obj);
  var headers = {};
  if (obj.getClassName !== undefined && obj.getClassName() === "Role") {
    var body = {};
    body.Name = obj.getName();
    body["_Box.Name"] = obj.getBoxName();
    json = this._internalCreate(JSON.stringify(body),headers,options);
    responseJson = json.bodyAsJson().d.results;
    obj.initializeProperties(obj, this.accessor, responseJson);
    return obj;
  }
  var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);
  if (callbackExist) {
    this._internalCreate(requestBody,headers,options);
  } else {
    json = this._internalCreate(requestBody);
        if(json.getStatusCode() >= 400){
      var response = json.bodyAsJson();
      throw new dcc.ClientException(response.message.value, response.code);
    }
    return new dcc.cellctl.Role(this.accessor, json.bodyAsJson().d.results);
  }
};*/


///**
//* Roleを取得(複合キー).
//* @param {String} roleName 取得対象のRole名
//* @param {String}boxName 取得対象のBox名
//* @return {dcc.cellctl.Role} 取得したしたRoleオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method retrieves a Role object.
 * @param {String} roleName Role Name
 * @param {String}boxName Box name
 * @param {Object} options object has callback and headers 
 * @return {dcc.cellctl.Role} Role object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.RoleManager.prototype.retrieve = function(roleName, boxName, options) {
  var async = this._isAsynchronous(options);
  var roleInstantiation = function(accessor, json){
    return new dcc.cellctl.Role(accessor, json.d.results);
  };
  var thenable = null;
  var key = "Name='" + roleName + "',_Box.Name='" + boxName + "'";
  if (typeof boxName === "undefined") {
    thenable = this._internalRetrieve(roleName, options, roleInstantiation);
  }else{
    thenable = this._internalRetrieveMultikey(key, options, roleInstantiation );
  }
  if (async) {
    return thenable;
  }else{
    return thenable.resolvedValue;//role instance
  }
};

/*dcc.cellctl.RoleManager.prototype.retrieve = function(roleName, boxName, options) {
  valid option is present with atleast one callback
  var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);
  var json = null;
  var key = "Name='" + roleName + "',_Box.Name='" + boxName + "'";
  if (callbackExist) {
    if (typeof boxName === "undefined") {
      this._internalRetrieve(roleName, options);
      return;
    }
    this._internalRetrieveMultikey(key, options);
    return;
  }

  if (typeof boxName === "undefined") {
    json = this._internalRetrieve(roleName);

    //role doesn't exist and can be created.
    //if (json === true) {
    //  return json;
    //} else {
    return new dcc.cellctl.Role(this.accessor, json);
    //}
  }
  json = this._internalRetrieveMultikey(key);
  //role doesn't exist and can be created.
  return new dcc.cellctl.Role(this.accessor, json);
};*/


/**
 * The purpose of this function is to update role details.
 * @param {String} roleName
 * @param {String} boxName
 * @param {Object} body
 * @param {String} etag ETag value
 * @param {Object} options object optional containing callback, headers
 * @return {Object} response DcHttpClient
 */
dcc.cellctl.RoleManager.prototype.update = function(roleName, boxName, body, etag, options) {
  var response = null;
  var headers = {};
  if (boxName !== undefined && boxName !== null) {
    var key = "Name='" + roleName + "',_Box.Name='" + boxName + "'";
    response = this._internalUpdateMultiKey(key, body, etag, headers, options);
  } else {
    response = this._internalUpdate(roleName, body , etag, headers, options);
  }
  return response;
};


///**
//* Roleを削除.
//* @param {String} roleName 削除対象のRole名
//* @param {String} boxName 削除対象のBox名
//* @return response promise
//* @throws {ClientException} DAO例外
//*/
/**
 * This method deletes a Role.
 * @param {String} roleName Role Name
 * @param {String} boxName Box Name
 * @param {String} etagOrOptions ETag value or options object having callback and headers
 * @return {Object} response
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.RoleManager.prototype.del = function(roleName, boxName, etagOrOptions) {
  var key = "Name='" + roleName + "'";
  if (boxName !== undefined && boxName !== null && boxName !=="undefined") {
    key += ",_Box.Name='" + boxName + "'";
  }
  var response = this._internalDelMultiKey(key, etagOrOptions);
  return response;
};
