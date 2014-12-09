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
//* @class AssociationEndのCRUDのためのクラス.
//* @constructor
//* @augments dcc.box.odata.ODataManager
//*/
/**
 * It creates a new object dcc.box.odata.schema.AssociationEndManager.
 * @class This class performs the CRUD operations for Association End.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcCollection} col
 */
dcc.box.odata.schema.AssociationEndManager = function(as, col) {
  this.initializeProperties(this, as, col);
};
dcc.DcClass.inherit(dcc.box.odata.schema.AssociationEndManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//* @param {dcc.box.odata.schema.AssociationEndManager} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {?} col ?
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.box.odata.schema.AssociationEndManager} self
 * @param {dcc.Accessor} as accessor
 * @param {dcc.box.DavCollection} col 
 */
dcc.box.odata.schema.AssociationEndManager.prototype.initializeProperties = function(self, as, col) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as, col);
};

///**
//* AssociationEndのURLを取得する.
//* @returns {String} URL
//*/
/**
 * This method returns the URL.
 * @returns {String} URL
 */
dcc.box.odata.schema.AssociationEndManager.prototype.getUrl = function() {
  var sb = "";
  sb += this.collection.getPath();
  sb += "/$metadata/AssociationEnd";
  return sb;
};

///**
//* AssociationEndを作成.
//* @param {dcc.box.odata.schema.AssociationEnd} obj AssociationEndオブジェクト
//* @return {dcc.box.odata.schema.AssociationEnd} AssociationEndオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method creates an AssociationEnd.
 * @param {dcc.box.odata.schema.AssociationEnd} obj AssociationEnd object
 * @param {Object} options optional parameters and callback
 * @return {dcc.box.odata.schema.AssociationEnd} AssociationEnd object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.schema.AssociationEndManager.prototype.create = function(obj,options) {
  var async = this._isAsynchronous(options);
  //var json = null;
  var headers = {};
  //var responseJson = null;
  var thenable = null;
  var associationEndInstantiation = function(accessor, json) {
      return new dcc.box.odata.schema.AssociationEnd(accessor, json.d.results);
    };
  if (obj.getClassName !== undefined && obj.getClassName() === "AssociationEnd") {
    var body = {};
    body.Name = obj.getName();
    body["_EntityType.Name"] = obj.getEntityTypeName();
    body.Multiplicity = obj.getMultiplicity();
    thenable = this._internalCreate(JSON.stringify(body),headers,options, associationEndInstantiation);
    //obj.initializeProperties(obj, this.accessor, json);
    //return obj;
  } else {
    var requestBody = JSON.stringify(obj);
   /* var callbackExist = options !== undefined &&
    (options.success !== undefined ||
        options.error !== undefined ||
        options.complete !== undefined);*/
   /* if (callbackExist) {
      if (!("Name" in obj)) {
        throw new dcc.ClientException("Name is required.", "PR400-OD-0009");
      }
      if (!("Multiplicity" in obj)) {
        throw new dcc.ClientException("Multiplicity is required.",
        "PR400-OD-0009");
      }
      if (!("_EntityType.Name" in obj)) {
        throw new dcc.ClientException("_EntityType.Name is required.",
        "PR400-OD-0009");
      }
      this._internalCreate(requestBody,headers,options);

    } else {*/
      if (!("Name" in obj)) {
        throw new dcc.ClientException("Name is required.", "PR400-OD-0009");
      }
      if (!("Multiplicity" in obj)) {
        throw new dcc.ClientException("Multiplicity is required.",
        "PR400-OD-0009");
      }
      if (!("_EntityType.Name" in obj)) {
        throw new dcc.ClientException("_EntityType.Name is required.",
        "PR400-OD-0009");
      }

      //json = this._internalCreate(requestBody);
      thenable = this._internalCreate(requestBody,headers,options, associationEndInstantiation);
    /*  if (json.getStatusCode() >= 400) {
        var response = json.bodyAsJson();
        throw new dcc.ClientException(response.message.value, response.code);
      }*/
      //responseJson = json.bodyAsJson().d.results;
      //return new dcc.box.odata.schema.AssociationEnd(this.accessor, responseJson);
    //}
      if (async) {
          return thenable;
      } else {
          return thenable.resolvedValue;
      }
  }
};
///**
//* AssociationEndを取得.
//* @param {String} name 取得対象のAssociation名
//* @param {String} entityTypeName EntityType名
//* @return {dcc.box.odata.schema.AssociationEnd} 取得したしたAssociationEndオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method fetches the AssociationEnd.
 * @param {String} name AssociationEnd
 * @param {String} entityTypeName EntityType name
 * @param {Object} options object has callback and headers
 * @return {dcc.box.odata.schema.AssociationEnd} AssociationEnd object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.schema.AssociationEndManager.prototype.retrieve = function(name, entityTypeName, options) {
    var async = this._isAsynchronous(options);
  /*valid option is present with atleast one callback*/
    var associationEndInstantiation = function(accessor, json) {
        return new dcc.box.odata.schema.AssociationEnd(accessor, json.d.results);
      };
  /*var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);*/
  var key = "Name='" + name + "',_EntityType.Name='" + entityTypeName + "'";
  var thenable = this._internalRetrieveMultikey(key, options, associationEndInstantiation);
  if (async) {
      //asynchronous mode of execution, return DcResponseAsThenable
      return thenable;
    } else {
      //synchronous call execution
      return thenable.resolvedValue;//box instance
    }
  
  
  /*if (callbackExist) {
    this._internalRetrieveMultikey(key, options);
    return;
  }
  var json = this._internalRetrieveMultikey(key);
  return new dcc.box.odata.schema.AssociationEnd(this.accessor, json);*/
};

/**
 * To create url for assocend_navpro_list
 * @param {String} ascName
 * @param {String} entityTypeName
 * @param {String} associationEndView
 * @returns {String} URL
 */
dcc.box.odata.schema.AssociationEndManager.prototype.getNavProListUrl = function(ascName,
    entityTypeName, associationEndView) {
  var sb = "";
  sb += this.collection.getPath();
  sb += "/$metadata/AssociationEnd";
  sb += "(Name='" + ascName + "',_EntityType.Name='" + entityTypeName + "')/";
  if (associationEndView === true) {
    sb += "$links/";
    associationEndView = false;
  }
  sb += "_AssociationEnd";
  return sb;
};

///**
//* AssociationEndを削除.
//* @param {String} name 取得対象のAssociation名
//* @param {String} entityTypeName EntityType名
//* @return {dcc.Promise} promise
//* @throws {ClientException} DAO例外
//*/
/**
 * This method deletes the AssociationEnd.
 * @param {String} name AssociationEnd
 * @param {String} entityTypeName EntityType name
 * @param {Object} options having callback and headers
 * @return {dcc.Promise} promise
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.schema.AssociationEndManager.prototype.del = function(name, entityTypeName, options) {
  var async = this._isAsynchronous(options);
  var key = "Name='" + name + "',_EntityType.Name='" + entityTypeName + "'";
  //var response = this._internalDelMultiKey(key, options);
  var thenable = this._internalDelMultiKey(key, options);
  var response = thenable;
  if(!async){
      response = thenable.resolvedValue;
  }
  return response;
};

/**
 * To create assocend_navpro_list
 * @param {dcc.box.odata.schema.AssociationEnd} obj
 * @param {String} fromEntityTypeName
 * @param {String} fromAssEnd 
 * @param {Object} options
 * @return {dcc.http.DcHttpClient} response
 */
dcc.box.odata.schema.AssociationEndManager.prototype.createNavProList = function(obj, fromEntityTypeName, fromAssEnd, options) {
  if (obj.getClassName !== undefined && obj.getClassName() === "AssociationEnd") {
    var body = {};
    body.Name = obj.getName();
    body.Multiplicity = obj.getMultiplicity();
    body["_EntityType.Name"] = obj.getEntityTypeName();
    var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
    var url = this.getNavProListUrl(fromAssEnd, fromEntityTypeName);
    var callbackExist = options !== undefined &&
    (options.success !== undefined ||
        options.error !== undefined ||
        options.complete !== undefined);
    if (callbackExist) {
      restAdapter.post(url, JSON.stringify(body), "application/json", {}, options);
    } else {
      var response = restAdapter.post(url, JSON.stringify(body), "application/json");
      return response;
    }
  }
};

/**
 * The purpose of this function is to create association URI 
 * for particular entityType.
 * @param {String} entityTypeName
 * @return {String} URL
 */
dcc.box.odata.schema.AssociationEndManager.prototype.getAssociationUri = function (entityTypeName) {
  var sb = "";
  sb += this.collection.getPath();
  sb += "/$metadata/EntityType(";
  sb += "'"+entityTypeName+"'";
  sb += ")/_AssociationEnd";
  return sb;
};

/**
 * The purpose of this function is to retrieve association
 * list against one entity type.
 * @param {String} entityTypeName 
 * @param {String} associationEndName
 * @return {Object} JSON
 */
dcc.box.odata.schema.AssociationEndManager.prototype.retrieveAssociationList = function (entityTypeName, associationEndName) {
  var uri = null;
  if(entityTypeName !== null && entityTypeName !== undefined) {
    uri = this.getAssociationUri(entityTypeName);
    if (associationEndName !== undefined && associationEndName !== null){
      uri = this.getNavProListUrl(associationEndName, entityTypeName, true);
    }
  }
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.get(uri, "application/json");
  var json = response.bodyAsJson().d.results;
  return json;
};

/**
 * The purpose of this function is to delete association link
 * @param {String} fromAssociationName
 * @param {String} fromEntityTypeName
 * @param {String} toAssociationName
 * @param {String} toEntityTypeName
 * @param {Object} options having callback and headers
 * @return {dcc.Promise} promise
 */
dcc.box.odata.schema.AssociationEndManager.prototype.delAssociationLink = function(fromAssociationName, fromEntityTypeName, toAssociationName, toEntityTypeName, options) {
  var uri = this.getNavProListUrl(fromAssociationName, fromEntityTypeName, true);
  uri += "(Name='" + toAssociationName + "'";
  uri += ",_EntityType.Name='" + toEntityTypeName + "')";
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.del(uri, options,"");
  return response;
};