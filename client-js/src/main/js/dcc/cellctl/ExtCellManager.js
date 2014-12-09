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
//* @class ExtCellのCRUDのためのクラス.
//* @constructor
//* @augments dcc.box.odata.ODataManager
//*/
/**
 * It creates a new object dcc.cellctl.ExtCellManager.
 * @class This class performs CRUD operations for External Cell.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @param {dcc.Accessor} as Accessor
 */
dcc.cellctl.ExtCellManager = function(as) {
  this.initializeProperties(this, as);
};
dcc.DcClass.inherit(dcc.cellctl.ExtCellManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.ExtCellManager} self
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.ExtCellManager} self
 * @param {dcc.Accessor} as Accessor object
 */
dcc.cellctl.ExtCellManager.prototype.initializeProperties = function(self, as) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as);
};

///**
//* URLを取得する.
//* @return {String} URL
//*/
/**
 * This method returns the URL for performing operations on External Cell.
 * @return {String} URL
 */
dcc.cellctl.ExtCellManager.prototype.getUrl = function() {
  var sb = "";
  sb += this.getBaseUrl();
  sb += this.accessor.cellName;
  sb += "/__ctl/ExtCell";
  return sb;
};

///**
//* ExtCellを作成.
//* @param body requestBody
//* @return {dcc.cellctl.ExtCell}ExtCellオブジェクト
//* @throws {ClientException} DAO例外
//*/

/**
 * This method creates an External Cell.
 * @param {Object} body requestBody
 * @param {Object} options object
 * @return {dcc.cellctl.ExtCell}ExtCell object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.ExtCellManager.prototype.create = function(body, options) {
  var async = this._isAsynchronous(options);
  var extCellInstantiation = function(accessor, json){
    return new dcc.cellctl.ExtCell(accessor, json.d.results);
  };
  var requestBody = JSON.stringify(body);
  var headers = {};
  var thenable = this._internalCreate(requestBody, headers, options, extCellInstantiation);
  if (async) {
    return thenable;
  } else {
    return thenable.resolvedValue;
  }
  /*var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);
  if (callbackExist) {
    var headers = {};
    this._internalCreate(requestBody, headers, options);
  } else {
    var json = this._internalCreate(requestBody);
    //if(json.response !== undefined){if(json.response.status === 409){return json.response.status;}}
        if(json.getStatusCode() >= 400){
      var response = json.bodyAsJson();
      //throw exception with code PR409-OD-0003
      throw new dcc.ClientException(response.message.value, response.code);
    }
    else if(json !== undefined){
      //showMessage(idModalWindow,isExternalCellCreatedFromSameUser,cellName);
    } 
    var responseJson = json.bodyAsJson().d.results;
    return new dcc.cellctl.ExtCell(this.accessor, responseJson);
  }*/
};
///**
//* The purpose of this method is to display messages on successful registration of external cell.
//*/
/*function showMessage(idModalWindow,isExternalCellCreatedFromSameUser,entity) {
	addSuccessClass();
	inlineMessageBlock();
	var objCommon = new common();
	var shorterExternalCellName = objCommon.getShorterEntityName(entity);
	if (isExternalCellCreatedFromSameUser === true) {
		document.getElementById("successmsg").innerHTML = "External Cell "+ shorterExternalCellName + " successfully registered!";
		document.getElementById("successmsg").title = entity;
	}
	else if (isExternalCellCreatedFromSameUser === false) {
		document.getElementById("successmsg").innerHTML = "External Cell 'Library' successfully registered !";
	}
	$(idModalWindow + ", .window").hide();
	autoHideMessage();
}*/
///**
//* ExtCellを作成.
//* @param body リクエストボディ
//* @return 作成したExtCellオブジェクト
//* @throws {ClientException} DAO例外
//*/
//dcc.cellctl.ExtCellManager.prototype.createAsMap = function() {
//var json = internalCreate(body);
//return new dcc.cellctl.ExtCell(accessor, json);
//};
///**
//* ExtCellを取得.
//* @param {String} roleId 取得対象のRoleId
//* @return {dcc.cellctl.ExtCell} 取得したしたExtCellオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs retrieve operation on External Cell.
 * @param {String} roleId RoleId
 * @param {Object} options object has callback and headers 
 * @return {dcc.cellctl.ExtCell} ExtCell object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.ExtCellManager.prototype.retrieve = function(roleId, options) {
  var async = this._isAsynchronous(options);
  var extCellInstantiation = function(accessor, json){
    return new dcc.cellctl.ExtCell(accessor, json.d.results);
  };
  var thenable = this._internalRetrieve(roleId, options, extCellInstantiation);
  if (async) {
    return thenable;
  } else {
    return thenable.resolvedValue;
  }
  /*valid option is present with atleast one callback*/
  /*var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);

  if (callbackExist) {
    this._internalRetrieve(roleId, options);
    return;
  }
  var json = this._internalRetrieve(roleId);
  return new dcc.cellctl.ExtCell(this.accessor, json);*/
};

/**
 * The purpose of this function is to get etag
 * @param {String} id
 * @return {String} etag
 */
dcc.cellctl.ExtCellManager.prototype.getEtag = function (id) {
  var json = this._internalRetrieve(id);
  return json.__metadata.etag;
};

/**
 * The purpose of this method is to perform delete operation for external cell.
 * @param {String} externalCellUrl
 * @param {String} etagOrOptions ETag value or options object having callback and headers
 * return {dcc.Promise} promise
 */
dcc.cellctl.ExtCellManager.prototype.del = function(externalCellUrl, etagOrOptions) {
  var async = this._isAsynchronous(etagOrOptions);
  var key = externalCellUrl;
  var thenable = this._internalDel(key, etagOrOptions);
  var response = thenable;
  if(!async){
      response = thenable.resolvedValue;
  }
  return response;
};
