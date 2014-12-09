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
 * It creates a new object dcc.ExtRoleManager.
 * @class This class performs CRUD operations for External Role.
 * @constructor
 * @augments dcc.ODataManager
 * @param {dcc.Accessor} Accessor
 */
dcc.cellctl.ExtRoleManager = function(as) {
  this.initializeProperties(this, as);
};
dcc.DcClass.inherit(dcc.cellctl.ExtRoleManager, dcc.box.odata.ODataManager);

/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.ExtRoleManager} self
 * @param {dcc.Accessor} as
 */
dcc.cellctl.ExtRoleManager.prototype.initializeProperties = function(self, as) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as);
};


/**
 * The purpose of this function is to make request URL for
 * creating External Role.
 * @return {String} URL
 */
dcc.cellctl.ExtRoleManager.prototype.getUrl = function() {
  var sb = this.getBaseUrl();
  sb += this.accessor.cellName;
  sb += "/__ctl/ExtRole";
  return sb;
};

/**
 * The purpose of this function is to create External Role.
 * @param {String} extRoleUrl external role URL
 * @param {String} relationName relation name
 * @param {String} boxName box name used in relation
 * @return {dcc.DcHttpClient} response
 * @throws {dcc.DaoException} Exception thrown
 */
dcc.cellctl.ExtRoleManager.prototype.create = function(extRoleUrl, relationName, boxName) {
  var body = {};
  body.ExtRole = extRoleUrl;
  body["_Relation.Name"] = relationName;
  body["_Relation._Box.Name"] = boxName;
  var requestBody = JSON.stringify(body);
  var response = this._internalCreate(requestBody);
  return response;
};
