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

/**
 * It creates a new object dcc.box.odata.EntityLinkManager.
 * @class This class is used for performing CRUD operations on Entity links.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcCollection} Collection
 */
dcc.box.odata.EntityLinkManager = function(as, collection) {
    this.initializeProperties(this, as, collection);
};
dcc.DcClass.inherit(dcc.box.odata.EntityLinkManager, dcc.box.odata.ODataManager);

/**
 * The purpose of this function is to initialize properties.
 * @param {Object} self
 * @param {dcc.Accessor} as
 * @param {dcc.DcCollection} collection
 */
dcc.box.odata.EntityLinkManager.prototype.initializeProperties = function(self, as, collection) {
    this.uber = dcc.EntitySet.prototype;
    this.uber.initializeProperties(self, as, collection);
};

/**
 * The purpose of this method is to create link between entities.
 * @param {String} uri URL value
 * @param {Object} options callback
 * @param {String} targetURI Request Body
 */
dcc.box.odata.EntityLinkManager.prototype.create = function(uri,targetURI,options) {
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var headers = {};
  //var response = restAdapter.post(uri, JSON.stringify(targetURI), "application/json");
  var response = restAdapter.post(uri, JSON.stringify(targetURI), "application/json", headers, options);
  return response;
};

/**
 * The purpose of this method is to retrieve link between entities.
 * @param {String} uri URL value
 * @returns {dcc.DcHttpClient} response
 * */
dcc.box.odata.EntityLinkManager.prototype.get = function(uri) {
    var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
    var response = restAdapter.get(uri, "", "application/json");
    return response;
};

/**
 * The purpose of this method is to create url for entity link operations.
 * @param {String} entityLinkID
 * @returns {String} Created URL
 */
dcc.box.odata.EntityLinkManager.prototype.getUrl = function(entityLinkID) {
    var sb = "";
    sb += this.collection.getPath() + "('" + entityLinkID + "')";
    return sb;
};

/**
 * The purpose of the following method is to delete an entity link.
 * @param {String} entityLinkID
 * @param {String} etag
 * @returns {dcc.Promise} response
 */
dcc.box.odata.EntityLinkManager.prototype.del = function(entityLinkID, etag) {
    if (typeof etag === undefined) {
        etag = "*";
    }
    var url = this.getUrl(entityLinkID);
    var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
    var response = restAdapter.del(url, etag,"");
    return response;
};
