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
//* @class AccountのCRUDを行うためのクラス.
//* @constructor
//* @augments dcc.box.odata.ODataManager
//* @property {Object} uber スーパークラスのプロトタイプへの参照.
//*/
/**
 * It creates a new object dcc.cellctl.AccountManager.
 * @class This class is used for performing CRUD operations of Account.
 * @constructor
 * @augments dcc.box.odata.ODataManager
 * @property {Object} uber A reference to the prototype of the superclass.
 * @param {dcc.Accessor} Accessor
 */
dcc.cellctl.AccountManager = function(as) {
  this.initializeProperties(this, as);
};
dcc.DcClass.inherit(dcc.cellctl.AccountManager, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.AccountManager} self
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.AccountManager} self
 * @param {dcc.Accessor} as accessor
 */
dcc.cellctl.AccountManager.prototype.initializeProperties = function(self, as) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as);

///**
//* パスワード用ヘッダーキー.
//*/
  /** Password for header key. */
  self.HEADER_KEY_CREDENTIAL = "X-Dc-Credential";
};

/**
 * This method returns the URL.
 * @returns {String} URL
 */
dcc.cellctl.AccountManager.prototype.getUrl = function() {
  var sb = "";
  sb += this.getBaseUrl();
  sb += this.accessor.cellName;
  // sb += this.accessor.getCurrentCell().getName();
  sb += "/__ctl/Account";
  return sb;
};

///**
//* Accountを作成.
//* @param {dcc.cellctl.Account} obj Accountオブジェクト
//* @param {?} password パスワード
//* @return {dcc.cellctl.Account} Accountオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method creates an account.
 * @param {dcc.cellctl.Account} obj Account object
 * @param {String} password password
 * @param {Object} options object
 * @return {dcc.cellctl.Account} Account object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.AccountManager.prototype.create = function(obj, password, options) {
  var async = this._isAsynchronous(options);

  var accountInstantiation = function(accessor, json){
    return new dcc.cellctl.Account(accessor, json.d.results);
  };
  
  var headers = {};
  //var responseJson = null;
  var requestBody = JSON.stringify(obj);
  var thenable = null;
  if (obj.getClassName !== undefined && obj.getClassName() === "Account") {
    var body = {};
    body.Name = obj.getName();
    headers[this.HEADER_KEY_CREDENTIAL] = password;// obj.getPassword();
    thenable = this._internalCreate(JSON.stringify(body), headers, options, accountInstantiation);
    //responseJson = json.bodyAsJson().d.results;
    //var response = obj.initializeProperties(obj, this.accessor, responseJson);
  } else {
    if (password !== null) {
      headers[this.HEADER_KEY_CREDENTIAL] = password;
    }
    thenable = this._internalCreate(requestBody, headers, options, accountInstantiation);
    /*var callbackExist = options !== undefined &&
    (options.success !== undefined ||
        options.error !== undefined ||
        options.complete !== undefined);
    if (callbackExist) {
      this._internalCreate(requestBody,headers,options);
    }
    else {
      json = this._internalCreate(requestBody, headers);
            if(json.getStatusCode() >= 400){
        var response = json.bodyAsJson();//throw exception with code
        throw new dcc.ClientException(response.message.value, response.code);
      }
      responseJson = json.bodyAsJson().d.results;
      return new dcc.cellctl.Account(this.accessor, responseJson);
    }*/
  }
  if (async) {
    return thenable;
  } else {
    return thenable.resolvedValue;
  }
};

/*function accountRefresh() {
	var contextRoot = sessionStorage.contextRoot;
	$("#mainContent").html('');
	$("#mainContent").load(contextRoot+'/htmls/accountListView.html', function() {
		if (navigator.userAgent.indexOf("Firefox") != -1) {
			loadAccountPage();
		}
	});
}
 */
///**
//* Accountを取得.
//* @param {String} name 取得対象のAccount名
//* @return {dcc.cellctl.Account} 取得したしたAccountオブジェクト
//* @throws ClientException DAO例外
//*/
/**
 * This method fetches the account information.
 * @param {String} name account name
 * @param {Object} options object has callback and headers
 * @return {dcc.cellctl.Account} Account objecct
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.AccountManager.prototype.retrieve = function(name, options) {
  var async = this._isAsynchronous(options);

  //dynamic account instantiation function, returns Account instance
  var accountInstantiation = function(accessor, json) {
    return new dcc.cellctl.Account(accessor, json.d.results);
  };

  var thenable = this._internalRetrieve(name, options, accountInstantiation);

  if (async) {
    //asynchronous mode of execution, return DcResponseAsThenable
    return thenable;
  } else {
    //synchronous call execution
    return thenable.resolvedValue;//box instance
  }
  
  /*valid option is present with atleast one callback
  var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);

  if (callbackExist) {
    this._internalRetrieve(name, options);
    return;
  }
  var json = this._internalRetrieve(name);
  //if (json === true) {
  //return true;
  //}
  //else {
  // returns response in JSON format.
  return new dcc.cellctl.Account(this.accessor, json);
  //}
*/};

///**
//* Passwordを変更.
//* @param {String} name Accountの名前
//* @param {String} password Accountパスワード
//* @throws ClientException DAO例外
//*/
/**
 * This method changes the account password.
 * @param {String} name Account
 * @param {String} password Account
 * @param {Object} options object optional containing callback, headers
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.AccountManager.prototype.changePassword = function(name, password, options) {
  var headers = {};
  headers[this.HEADER_KEY_CREDENTIAL] = password;
  var body = {};
  body.Name = name;
  this._internalUpdate(name, body, "*", headers, options);
};

///**
//* Delete the account.
//* @param {String} accountName account name
//* @return promise
//* @throws ClientException DAO例外
//*/
/**
 * Delete the account.
 * @param {String} accountName account name
 * @param {String} etagOrOptions ETag value or options object having callback and headers
 * @return {Object} response
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.AccountManager.prototype.del = function(accountName, etagOrOptions) {
  var async = this._isAsynchronous(etagOrOptions);
  var key = "Name='" + accountName + "'";
  var thenable = this._internalDelMultiKey(key, etagOrOptions);
  var response = thenable;
  if(!async){
      response = thenable.resolvedValue;
  }
  return response;
};

/**
 * The purpose of this method is to retrieve get etag on the basis of account name.
 * @param {String} name
 * @returns {String} etag
 */
dcc.cellctl.AccountManager.prototype.getEtag = function(name) {
  var json = this._internalRetrieve(name);
  return json.__metadata.etag;
};


/**
 * The purpose of this method is to perform update operation or an account.
 * @param {String} accountName
 * @param {Object} body
 * @param {String} etag
 * @param {String} password
 * @param {Object} options object optional containing callback, headers
 * @return {dcc.box.odata.ODataResponse} response
 */
dcc.cellctl.AccountManager.prototype.update = function(accountName, body, etag, password, options) {
  var async = this._isAsynchronous(options);
  var headers = {};
  if (password !== "" && password !== undefined && password !== null) {
    headers[this.HEADER_KEY_CREDENTIAL] = password;
    //response = this._internalUpdate(accountName, body, etag, headers, options);
  }
  var thenable = this._internalUpdate(accountName, body, etag, headers, options);
  var response = thenable;
  if (!async) {
    response = thenable.resolvedValue;
  }
  /*else {
    response = this._internalUpdate(accountName, body, etag, headers, options);
  }*/
  return response;
};

