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
//* @class CellのCRUDを行うクラス.
//* @constructor
//* @augments dcc.box.odata.ODataManager
//*/
/**
 * It creates a new object dcc.unitctl.CellManager.
 * @class This class performs CRUD operations for Cell.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @param {dcc.Accessor} as Accessor
 */
dcc.unitctl.CellManager = function(as) {
  this.initializeProperties(this, as);
};
dcc.DcClass.inherit(dcc.unitctl.CellManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//* @param {dcc.unitctl.CellManager} self
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.unitctl.CellManager} self
 * @param {dcc.Accessor} as Accessor
 */
dcc.unitctl.CellManager.prototype.initializeProperties = function(self, as) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as);
};

///**
//* URLを生成する.
//* @return {String} URL文字列
//*/
/**
 * This method gets the URL for performing cell related operations.
 * @return {String} URL for Cell
 */
dcc.unitctl.CellManager.prototype.getUrl = function() {
  return this.getBaseUrl() + "__ctl/Cell";
};

///**
//* Cellを作成.
//* @param {Object} body リクエストボディ
//* @param {dcc.unitctl.Cell} cell
//* @param callback object
//* @return {dcc.unitctl.Cell} 作成したCellオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs create operation for Cell.
 * @param {Object} body Request body
 * @param {dcc.unitctl.Cell} cell
 * @param {Object} options object
 * @return {dcc.unitctl.Cell} Cell object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.unitctl.CellManager.prototype.create = function(body, cell, options) {
  //dynamic cell instantiation function, return Cell instance
  var cellInstantiation = function(accessor, json){
    accessor.cellName = json.d.results.Name;
    return new dcc.unitctl.Cell(accessor, json.Name);
  };

  if (typeof cell !== "undefined") {
    var newBody = {};
    newBody.Name = cell.accessor.cellName;
    var json = this.cellCreate(newBody);
    cell.initializeProperties(cell, this.accessor, json.Name);
    return cell;
  }
  
  var thenable = this.cellCreate(body, options, cellInstantiation);

  if(this._isAsynchronous(options)){
    //asynchronous mode return then-able
    return thenable;
  } else {
    //synchronous call execution
    return thenable.resolvedValue;
  }
};

///**
//* Cellを作成.
//* @param {Object} body リクエストボディ
//* @param callback object
//* @return response JSON object
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs Create operation for cell.
 * @param {Object} body Request body
 * @param {Object} options object
 * @return {Object} response JSON object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.unitctl.CellManager.prototype.cellCreate = function(body, options, filter) {
  var url = this.getUrl();
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var headers = {};
  var requestBody = JSON.stringify(body);
  var response = restAdapter.post(url, requestBody, "application/json", headers, options, filter);
  return response;
  /*var responseBody = response.bodyAsJson();
    if(responseBody.d === undefined){
      throw new dcc.ClientException(responseBody.message.value,responseBody.code);
    }
    var json = responseBody.d.results;*/
  //return json;

};

///**
//* retrieve cell.
//* @param {String} id 取得対象のID
//* @return {dcc.unitctl.Cell} 取得したしたCellオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs the retrieve operation for cell.
 * @param {String} id ID of cell
 * @param {Object} options callback parameters
 * @return {dcc.unitctl.Cell} Cell object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.unitctl.CellManager.prototype.retrieve = function(id,options) {
  var async = this._isAsynchronous(options);

  //dynamic cell instantiation function, return Cell instance
  var cellInstantiation = function(accessor, json){
    accessor.cellName = json.d.results.Name;
    return new dcc.unitctl.Cell(accessor, json.Name);
  };

  //id validation
  if (typeof id !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  
  var thenable = this._internalRetrieve(id, options, cellInstantiation);
  
  if(async){
    //asynchronous mode of execution, return DcResponseAsThenable
    return thenable;
  } else {
    //synchronous call execution
    return thenable.resolvedValue;//cell instance
  }
};

/**
 * Delete Cell.
 * @param {String} cellName
 * @param {Object} etagOrOptions etag value or options having callback and headers
 * @return {dcc.Promise} response
 */
dcc.unitctl.CellManager.prototype.del = function(cellName, etagOrOptions) {
  var key = "Name='" + cellName + "'";
  var thenable =  this._internalDelMultiKey(key, etagOrOptions);
  var response = thenable;
  if(!this._isAsynchronous(etagOrOptions)){
      response = thenable.resolvedValue;
  }
  return response;
};

/**
 * This method gets the unique Etag.
 * @param {String} name
 * @return {String} etag
 */
dcc.unitctl.CellManager.prototype.getEtag = function(name) {
  var json = this._internalRetrieve(name);
  return json.__metadata.etag;
};

/**
 * RECURSIVE DELETE FUNCTION FOR CELL.
 * @param {String} cellName Name of cell to delete.
 * @param {Object} options arbitrary options to call this method.
 * @param {Function} options.success Callback function for successful result.
 * @param {Function} options.error Callback function for error response.
 * @param {Function} options.complete Callback function for any response,  either successful or error.
 * @param {Object} options.headers any extra HTTP request headers to send.
 * @returns {Object} response(sync) or promise(async) (TODO not implemented) depending on the sync/async model.
 */
dcc.unitctl.CellManager.prototype.recursiveDelete = function(cellName, options) {
  var url = this.getBaseUrl() + cellName;
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  if(!options){
    options = {};
  }
  if(!options.headers){
    options.headers = {};
  }
  options.headers["X-Dc-Recursive"] = "true";
  var response = restAdapter.del(url, options);
  return response;
};
/**
 * This method performs update operation.
 * @param {String} id ID value
 * @param {Object} body PUT Request Body
 * @param {String} etag ETag value
 * @param {Object} options object optional
 * @return {dcc.box.odata.ODataResponse} Response
 * @throws {dcc.ClientException} DAO exception
 */
dcc.unitctl.CellManager.prototype.update = function(id, body, etag, options) {
  if (typeof id !== "string" || typeof etag !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  var async = this._isAsynchronous(options);
  
  var thenable = this._internalUpdate(id, body, etag, {}, options);
  if(async){
    //asynchronous mode of execution, return DcResponseAsThenable
    return thenable;
  } else {
    //synchronous call execution
    return thenable.resolvedValue;//cell instance
  }
  
};

