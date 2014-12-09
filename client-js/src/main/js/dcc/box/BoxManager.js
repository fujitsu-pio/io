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
//* @class BoxのCRUDのためのクラス.
//* @constructor
//* @augments dcc.box.odata.ODataManager
//*/
/**
 * It creates a new object dcc.box.BoxManager.
 * @class This class performs CRUD operations for Box.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @param {dcc.Accessor} as Accessor
 */
dcc.box.BoxManager = function(as) {
  this.initializeProperties(this, as);
};
dcc.DcClass.inherit(dcc.box.BoxManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//* @param {dcc.box.BoxManager} self
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.box.BoxManager} self
 * @param {dcc.Accessor} as Accessor
 */
dcc.box.BoxManager.prototype.initializeProperties = function(self, as) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as);
};

///**
//* BoxのURLを取得する.
//* @returns {String} URL
//*/
/**
 * This method generates the URL for performing operations on Box.
 * @returns {String} URL
 */
dcc.box.BoxManager.prototype.getUrl = function() {
  var sb = "";
  sb += this.getBaseUrl();
  sb += this.accessor.cellName;
  sb += "/__ctl/Box";
  return sb;
};

///**
//* Boxを作成.
//* @param {dcc.box.Box} obj Boxオブジェクト
//* @return responseJson
//* @throws {ClientException} DAO例外
//*/
/**
 * This method creates a new Box.
 * @param {dcc.box.Box} obj Box object
 * @param {Object} options object
 * @return {Object} responseJson
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.BoxManager.prototype.create = function(obj,options) {
  var async = this._isAsynchronous(options);

  //dynamic box instantiation function, returns Box instance
  var boxInstantiation = function(accessor, json) {
    return new dcc.box.Box(accessor, json.d.results);
  };

  var body = {};
  body.Name = obj.accessor.boxName;
  // var boxName = body.Name;
  var schema = obj.accessor.boxSchema;
  var schemaLen = schema.length;
  if (schemaLen !== 0) {
    body.Schema = schema;
  }
  var requestBody = JSON.stringify(body);
  var headers = {};

  var thenable = this._internalCreate(requestBody, headers, options,
      boxInstantiation);

  if (async) {
    //asynchronous mode of execution, return DcResponseAsThenable
    return thenable;
  } else {
    //synchronous call execution
    return thenable.resolvedValue;//box instance
  }

 /* var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);
  if (callbackExist) {
    var headers = {};
    this._internalCreate(requestBody,headers,options);
  } else {
    var response = this._internalCreate(requestBody);
    var responseJson = null;
        if (response.getStatusCode() >= 400) {
      responseJson = response.bodyAsJson();
      throw new dcc.ClientException(responseJson.message.value,responseJson.code);
    }
    responseJson = response.bodyAsJson().d.results;
    return responseJson;
  }*/
  
  /*if(json !== undefined && json.response === undefined) {
		var objCommon = new common();
		addSuccessClass();
		inlineMessageBlock();
		boxTableRefresh();
		var shorterBoxName = objCommon.getShorterEntityName(boxName);
		document.getElementById("successmsg").innerHTML = "Box "+shorterBoxName+" created successfully!";
		document.getElementById("successmsg").title = boxName;
		$('#createBoxModal, .window').hide();
		autoHideMessage();
	}*/
  /*if(json.response !== undefined) {
		return json;
	}*/
  /*var path = dcc.UrlUtils.append(accessor.getCurrentCell().getUrl(), body.Name);
	obj.initialize(this.accessor, json, path);
	return obj;*/
//var body = {};
//body.Name = obj.getName();
//body.Schema = obj.getSchema();
//var json = this.internalCreate(body);
//var path = dcc.UrlUtils.append(accessor.getCurrentCell().getUrl(), body.Name);
//obj.initialize(this.accessor, json, path);
//return obj;
//var requestBody = JSON.stringify(obj);
//var json = this.internalCreate(requestBody);
//return new dcc.box.Box(this.accessor, json, dcc.UrlUtils.append(this.accessor.getCurrentCell().getUrl(), obj.Name));
};

///**
//* The purpose of this function is to refresh the boxList.
//*/
/*function boxTableRefresh() {
	var contextRoot = sessionStorage.contextRoot;
	$("#mainContent").html('');
	$("#mainContent").load(contextRoot+'/htmls/boxListView.html', function() {
		if(navigator.userAgent.indexOf("Firefox") != -1) {
			createBoxTable();
		}
	});
}*/

///**
//* Boxを作成.
//* @param {Object} body リクエストボディ
//* @return {dcc.box.Box} 作成したBoxオブジェクト
//* @throws {ClientException} DAO例外
//*/
//dcc.box.BoxManager.prototype.createAsMap = function(body) {
//var requestBody = JSON.stringify(body);
//var json = this.internalCreate(requestBody);
//return new dcc.box.Box(this.accessor, json, dcc.UrlUtils.append(this.accessor.getCurrentCell().getUrl(), body.Name));
//};

///**
//* Boxを取得.
//* @param {String} name 取得対象のbox名
//* @return {dcc.box.Box} 取得したしたBoxオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method fetches the box details.
 * @param {String} name Box name
 * @param {Object} options callback parameters
 * @return {dcc.box.Box} Box object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.BoxManager.prototype.retrieve = function(name, options) {
  /*valid option is present with atleast one callback*/
  /*  var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);

  if (callbackExist) {
    this._internalRetrieve(name,options);
    return;
  }*/
  var async = this._isAsynchronous(options);

  // dynamic box instantiation function, returns Box instance
  var boxInstantiation = function(accessor, json) {
    var path = dcc.UrlUtils.append(accessor.getCurrentCell().getUrl(), name);
    return new dcc.box.Box(accessor, json.d.results, path);
  };

  var thenable = this._internalRetrieve(name, options, boxInstantiation);

  if (async) {
    //asynchronous mode of execution, return DcResponseAsThenable
    return thenable;
  } else {
    //synchronous call execution
    return thenable.resolvedValue;//box instance
  }
  //box doesn't exist and can be created.
  //return new dcc.box.Box(this.accessor, json);
  //return new dcc.box.Box(this.accessor, json,dcc.UrlUtils.append(this.accessor.getCurrentCell().getUrl(), name));
  //var json = this._internalRetrieve(name);
  //return new dcc.box.Box(this.accessor, json, dcc.UrlUtils.append(this.accessor.getCurrentCell().getUrl(), name)); 
};
/**
 * The purpose of this function is to return array of boxes.
 * @param {String} name
 * @returns {Object] json
 */
dcc.box.BoxManager.prototype.getBoxes = function(name) {
  var json = this._internalRetrieve(name);
  return json;
};

/**
 * This method deletes a BOx against a cellName and etag.
 * @param {String} boxName
 * @param {String} etagOrOptions ETag value or options object having callback and headers
 * @returns {Object} json
 */
dcc.box.BoxManager.prototype.del = function(boxName, etagOrOptions) {
  var async = this._isAsynchronous(etagOrOptions);
  var key = "Name='" + boxName + "'";
  var thenable = this._internalDelMultiKey(key, etagOrOptions);
  var response = thenable;
  if(!async){
      response = thenable.resolvedValue;
  }
  return response;
};

/**
 * This method gets Etag of the Box.
 * @param {String} name
 * @return {String} Etag
 */
dcc.box.BoxManager.prototype.getEtag = function(name) {
  var json = this._internalRetrieve(name);
  return json.__metadata.etag;
};

/**
 * This method update the box details.
 * @param {String} boxName name 
 * @param {Object} body request 
 * @param {String} etag value
 * @param {Object} options object optional containing callback, headers
 * @return {dcc.box.odata.ODataResponse} response
 */
dcc.box.BoxManager.prototype.update = function(boxName, body, etag, options) {
  //id, body, etag, headers, callback
  var async = this._isAsynchronous(options);
  var thenable = this._internalUpdate(boxName, body, etag, null, options);
  var response = thenable;
  if (!async) {
    response = thenable.resolvedValue;
  }
  return response;
};