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
//* @class Class for CRUD of Property.
//* @constructor
//* @augments jEntitySet
//*/
/**
 * It creates a new object dcc.box.odata.schema.PropertyManager.
 * @class This Class is used for performing CRUD operations of Property.
 * @constructor
 * @augments jEntitySet
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcCollection} collection
 */
dcc.box.odata.schema.PropertyManager = function(as, collection) {
  this.initializeProperties(this, as, collection);
};
dcc.DcClass.inherit(dcc.box.odata.schema.PropertyManager, dcc.box.odata.ODataManager);

/** The purpose of this function is to initialize properties.
 * @param {Object} self
 * @param {dcc.Accessor} as
 * @param {dcc.DcCollection} collection
 */
dcc.box.odata.schema.PropertyManager.prototype.initializeProperties = function(self, as, collection) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as, collection);
};

/**
 * The purpose of this function is to create URL.
 * @returns {String} URL
 */
dcc.box.odata.schema.PropertyManager.prototype.getUrl = function() {
  var sb = "";
  sb = this.getBaseUrl();
  sb += this.accessor.cellName;
  sb +="/";
  sb += this.accessor.boxName;
  sb +="/";
  sb += this.collection;
  sb += "/$metadata/Property";
  return sb;
};

/**
 * The purpose of this function is to create Property URI.
 * @param {String} entityTypeName
 * @returns {String} URL
 */
dcc.box.odata.schema.PropertyManager.prototype.getPropertyUri = function (entityTypeName) {
  var sb = "";
  sb = this.getBaseUrl();
  sb += this.accessor.cellName;
  sb +="/";
  sb += this.accessor.boxName;
  sb +="/";
  sb += this.collection;
  sb += "/$metadata/EntityType(";
  sb += "'"+entityTypeName+"'";
//sb += escape("'"+entityTypeName+"'");
  sb += ")/_Property";
  return sb;
};

/**
 * The purpose of this function is to create Property.
 * @param {Object} obj
 * @param {Object} options callback object.
 * @return {Object} json DcHttpClient
 */
dcc.box.odata.schema.PropertyManager.prototype.create = function (obj,options) {
  var json = null;
  var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);
  if (callbackExist) {
    var headers ={};
    this._internalCreate(obj,headers,options);
  }
  else {
    json = this._internalCreate(obj);
    /*    if (json.response !== undefined) {
      if (json.response.status === 409 || json.response.status === 400) {
        return json.response.status;
      }
    }*/
    return json;
  }
};

/**
 * The purpose of this function is to retrieve Property.
 * @param {String} propertyName
 * @param {String} entityTypeName
 * @return {Object} JSON response
 */
dcc.box.odata.schema.PropertyManager.prototype.retrieve = function (propertyName, entityTypeName) {
  var json = null;
  var key = null;
  key = "Name='"+propertyName+"',_EntityType.Name='"+entityTypeName+"'";
  if (propertyName !== undefined && entityTypeName !== undefined) {
    json = this._internalRetrieveMultikey(key);
  }
  return json;
};

/**
 * The purpose of this function is to retrieve Property List.
 * @param {String} entityTypeName
 * @return {Object} JSON response
 */
dcc.box.odata.schema.PropertyManager.prototype.retrievePropertyList = function (entityTypeName) {
  if(entityTypeName !== null || entityTypeName !== undefined) {
    var uri = this.getPropertyUri(entityTypeName);
    var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
    var response = restAdapter.get(uri, "application/json");
    var json = response.bodyAsJson().d.results;
    return json;
  }
};

/**
 * The purpose of this function is to delete Property.
 * @param {String} key
 * * @param {Object} options optional parameters having callback and headers
 * @returns {Object} response
 */
dcc.box.odata.schema.PropertyManager.prototype.del = function(key, options) {
  /*if (typeof etag === "undefined") {
    etag = "*";
  }*/
  var response = this._internalDelMultiKey(key, options);
  return response;
};
