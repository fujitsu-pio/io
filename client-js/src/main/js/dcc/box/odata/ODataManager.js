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
 * It creates a new object dcc.box.odata.ODataManager.
 * @class This is the abstract class for generating / deleting the OData related functions and serves
 * as middle layer in API calls for CRUD operations.
 * @constructor
 * @param {dcc.Accessor} Accessor
 * @param {Object} col
 * @param {String} name
 */
dcc.box.odata.ODataManager = function(as, col, name) {
  this.initializeProperties(this, as, col, name);
};

///**
//* プロパティを初期化する.
//* @param {dcc.AbstractODataContext} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {dcc.DcCollection} col
//* @param name entitySetName
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.AbstractODataContext} self
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcCollection} col
 * @param name entitySetName
 */
dcc.box.odata.ODataManager.prototype.initializeProperties = function(self, as, col, name) {
  if (typeof as !== "undefined") {
//  /** アクセス主体. */
    /** Accessor. */
    self.accessor = as.clone();
  }

///** DAVコレクション. */
  /** DAV Collection. */
  self.collection = null;
  if (typeof col !== "undefined") {
    self.collection = col;
  }

///** EntitySet名. */
  /** EntitySetName. */
  self.entitySetName = null;
  if (typeof name !== "undefined") {
    self.entitySetName = name;
  }

  /** EntityID. */
  self.keyPredicate = null;

  /** NavigationProperty. */
  self.naviProperty = null;
};

///**
//* IDをEntitySet指定する.
//* @param {String} key keyPredicate
//* @return {dcc.box.odata.ODataManager} EntitySetオブジェクト
//*/
/**
 * This method sets key for EntityID.
 * @param {String} key keyPredicate
 * @return {dcc.box.odata.ODataManager} EntitySet object
 */
dcc.box.odata.ODataManager.prototype.key = function(key) {
  if (typeof key !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.keyPredicate = key;
  return this;
};

///**
//* navigationPropertyをEntitySet指定する.
//* @param {String} navProp NavigationProperty
//* @return {dcc.box.odata.ODataManager} EntitySetオブジェクト
//*/
/**
 * This method specifies the EntitySet navigationProperty.
 * @param {String} navProp NavigationProperty
 * @return {dcc.box.odata.ODataManager} EntitySet object
 */
dcc.box.odata.ODataManager.prototype.nav = function(navProp) {
  if (typeof navProp !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.naviProperty = navProp;
  return this;
};

///**
//* ベースURL取得.
//* @return {String} ベースURL
//*/
/**
 * This method returns the Base URL for making a connection.
 * @return {String} Base URL
 */
dcc.box.odata.ODataManager.prototype.getBaseUrl = function() {
  return this.accessor.getContext().getBaseUrl();
};

///**
//* ODataデータを作成.
//* @private
//* @param {Object} body POSTするリクエストボディ
//* @param {String} headers POSTするリクエストヘッダー
//* @param callback object optional
//* @return {Ob} 対象となるODataContextを抽象クラスとして返却
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs create operation.
 * @private
 * @param {Object} body POST Request Body
 * @param {String} headers POST Request Header
 * @param {Object} options optional callback, header and async parameters
 * @param {Object} filter instance of entity
 * @return {Object} Response
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataManager.prototype._internalCreate = function(body, headers, options, filter) {
    var url = this.getUrl();
    var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
    return restAdapter.post(url, body, "application/json", headers, options, filter);
  /*  if (options !== undefined) {
  //  restAdapter.post(url, body, "application/json", headers, function(resp) {
  //  var json = {};
  //  var responseBody = resp.bodyAsJson();
  //  if (responseBody.d !== undefined && responseBody.d.results !== undefined) {
  //  json = responseBody.d.results;
  //  }
  //  callback(json);
  //  });
      restAdapter.post(url, body, "application/json", headers, options);
    } else {
      var response = restAdapter.post(url, body, "application/json", headers);
       if (response.getStatusCode() === 409 || response.getStatusCode() === 400) {
              return response;
              }
          var json = response.bodyAsJson().d.results;
          return json;
      return response;
    }*/
};

///**
//* ODataデータを取得.
//* @private
//* @param {String} id 対象となるID値
//* @param callback object optional
//* @return {?} １件取得した結果のオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs retrieve operation. It internally calls _internalRetrieveMultikey.
 * @private
 * @param {String} id ID value
 * @param {Object} options optional callback, header and async parameters
 * @return {Object} Object of the result
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataManager.prototype._internalRetrieve = function(id, options, filter) {
  return this._internalRetrieveMultikey("'" + encodeURIComponent(id) + "'", options, filter);
};

///**
//* ODataデータを取得(複合キー).
//* @private
//* @param {String} id 対象となる複合キー urlエンコードが必要
//* @param callback object optional
//* @return １件取得した結果のオブジェクト response as json
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs retrieve operation.
 * @private
 * @param {String} id composite key URL encoding the target
 * @param {Object} options object optional, required in case of ASYNC call
 * @return {Object} response as JSON
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataManager.prototype._internalRetrieveMultikey = function(id, options, filter) {
  var url=null;
  if(id === undefined || id === "''"){
    url = this.getUrl();
  }
  else{
    url = this.getUrl() + "(" + id + ")";
  }

  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  return restAdapter.get(url, "application/json", "*", options, filter);
  //if (this.accessor.getContext().getAsync() || options!== undefined) {
 /* if(async){
    //asynchronous execution mode, invoke callback method
    var thenable = restAdapter.get(url, "application/json", "*", options, filter);
    return thenable;
  } else {
    //synchronous execution mode, return response or throw client exception
    var response = restAdapter.get(url, "application/json", "*" , options, filter);
    if(this.accessor.batch !== true && response.resolvedValue.status >= 300){
      //error case
      var res = JSON.parse(response.resolvedValue.response);
      throw new dcc.ClientException(response.bodyAsJson().message.value, response.getStatusCode());
    }
    
    else{
      //return the response
      return response.bodyAsJson().d.results;
    }
    return response;
  }*/
};

///**
//* ODataデータを更新.
//* @private
//* @param {String} id 対象となるID値
//* @param {Object} body PUTするリクエストボディ
//* @param {String} etag ETag値
//* @param headers
//* @param callback object optional
//* @return response DcHttpClient
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs update operation. It internally calls _internalUpdateMultiKey.
 * @private
 * @param {String} id ID value
 * @param {Object} body PUT Request Body
 * @param {String} etag ETag value
 * @param {Object} headers
 * @param {Object} options optional callback, header and async parameters
 * @return {Object} response DcHttpClient
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataManager.prototype._internalUpdate = function(id, body, etag, headers, options) {
  var response = this._internalUpdateMultiKey("'" + encodeURIComponent(id) + "'", body, etag, headers, options);
  return response;
};

///**
//* ODataデータを更新.
//* @param id 対象となるID値
//* @param body PUTするリクエストボディ
//* @param etag ETag値
//* @param headers PUTするリクエストヘッダー
//* @throws ClientException DAO例外
//*/
////void _internalUpdate(String id, JSONObject body, String etag, HashMap<String, String> headers) throws ClientException {
//dcc.box.odata.ODataManager.prototype._internalUpdate = function() {
//var url = this.getUrl() + "('" + id + "')";
//var factory = new dcc.http.RestAdapterFactory();
//var restAdapter = factory.create(this.accessor);
//restAdapter.put(url, body.toJSONString(), etag, headers, RestAdapter.CONTENT_TYPE_JSON);
//};

///**
//* ODataデータを更新(複合キー).
//* @private
//* @param {String} multiKey 対象となる複合キー<br> urlエンコードが必要
//* @param {Object} body PUTするリクエストボディ
//* @param {String} etag ETag値
//* @param callback object optional
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs update operation.
 * @private
 * @param {String} multiKey composite key url encoding the target
 * @param {Object} body PUT Request Body
 * @param {String} etag ETag value
 * @param {Object} options optional callback, header and async parameters
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataManager.prototype._internalUpdateMultiKey = function(multiKey, body, etag, headers, options) {
  var url = this.getUrl() + "(" + multiKey + ")";
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = "";
  //if (callback !== undefined) {
  response = restAdapter.put(url, JSON.stringify(body), etag, "application/json", headers, options);
  //} else {
  //response = restAdapter.put(url, JSON.stringify(body), etag, "application/json", headers);
  //}
  return response;
};

///**
//* ODataデータを削除.
//* @private
//* @param {String} id 削除するODataデータのID値
//* @param {String} etag ETag値
//* @param callback object optional
//* @return promise
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs delete operation. It internally calls _internalDelMultiKey.
 * @private
 * @param {String} id ID value
 * @param {String} etagOrOptions ETag value or options object having callback and headers
 * @param {Object} options optional callback, header and async parameters
 * @return {dcc.Promise} promise
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataManager.prototype._internalDel = function(id, etagOrOptions, options) {
  var response = this._internalDelMultiKey("'" + encodeURIComponent(id) + "'", etagOrOptions, options);
  return response;
};

///**
//* ODataデータを削除(複合キー).
//* @private
//* @param {String} id 削除するODataデータの複合キー<br> urlエンコードが必要
//* @param {String} etag ETag値
//* @param callback object optional
//* @return promise
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs delete operation.
 * @private
 * @param {String} id composite key url encoding the target
 * @param {String} etagOrOptions ETag value or options having callback and headers
 * @param {Object} options optional callback, header and async parameters
 * @return {dcc.Promise} promise
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataManager.prototype._internalDelMultiKey = function(id, etagOrOptions, options) {
  var url = this.getUrl() + "(" + id + ")";
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.del(url, etagOrOptions, options);
  return response;
};

///**
//* ODataデータを登録.
//* @param {Object} body 登録するJSONオブジェクト
//* @param callback object optional
//* @return {?} 登録結果のレスポンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method registers the OData data and returns in JSON form.
 * @param {Object} body JSON object
 * @param {Object} options optional callback, header and async parameters
 * @return {Object} Response of the registration result
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataManager.prototype.createAsJson = function(body, options) {
  if (typeof body !== "object") {
    throw new dcc.ClientException("InvalidParameter");
  }
  var async = this._isAsynchronous(options);
  var response = this._internalCreate(JSON.stringify(body), {}, options);
  var thenable = null;
  if (async) {
      thenable = response;
  } else {
      thenable = response.resolvedValue;
      
  }
  return thenable;
  /*if (callback !== undefined) {
    this._internalCreate(JSON.stringify(body), {}, function(resp) {
      if (resp.getStatusCode() >= 300) {
        if (callback.error !== undefined) {
          callback.error(resp);
        }
      } else {
        if (callback.success !== undefined) {
          var responseBody = resp.bodyAsJson();
          var json = responseBody.d.results;
          callback.success(json);
        }
      }
      if (callback.complete !== undefined) {
        callback.complete(resp);
      }
    });
  }*/
  //if(this.accessor.getContext().getAsync() && options !== undefined){
   //var response = this._internalCreate(JSON.stringify(body), {}, options);
 /* }
  else {
    var responseJson ={};
    var response = this._internalCreate(JSON.stringify(body));
    var responseBody = response.bodyAsJson();
    if (responseBody.d !== undefined && responseBody.d.results !== undefined) {
      responseJson = responseBody.d.results;
    }
    return responseJson;
    // return this._internalCreate(JSON.stringify(body));
  }*/
};

///**
//* ODataデータを登録 createAsResponse.
//* @param {Object} json 登録するJSONオブジェクト
//* @param callback object optional
//* @return {?} 登録結果のレスポンス dcc.box.odata.ODataResponse
//* @throws {ClientException} DAO例外
//*/
/**
 * This method registers the OData data and returns in ODataResponse form.
 * @param {Object} json JSON object
 * @param {Object} options optional callback, header and async parameters
 * @return {object} Response of the registration result
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataManager.prototype.createAsResponse = function(body, options) {
  if (typeof body !== "object") {
    throw new dcc.ClientException("InvalidParameter");
  }
  var async = this._isAsynchronous(options);
  var response = this._internalCreate(JSON.stringify(body), null, options);
  var thenable = null;
  if (async) {
      thenable = response;
  } else {
      thenable = response.resolvedValue;
  }
  return thenable;
  /*if (options !== undefined) {
    this._internalCreate(JSON.stringify(body), {}, options);
    this._internalCreate(JSON.stringify(body), {}, function(resp) {
      if (resp.getStatusCode() >= 300) {
        if (options.error !== undefined) {
          options.error(resp);
        }
      } else {
        if (options.success !== undefined) {
          var responseBody = resp.bodyAsJson();
          var json = responseBody.d.results;
          var odataResponse = new dcc.box.odata.ODataResponse(this.accessor, json);
          options.success(odataResponse);
        }
      }
      if (options.complete !== undefined) {
        options.complete(resp);
      }
    });
  } else {
    //var resJson = this._internalCreate(JSON.stringify(body));
    var responseJson ={};
    var response = this._internalCreate(JSON.stringify(body));
    var responseBody = response.bodyAsJson();
    if (responseBody.d !== undefined && responseBody.d.results !== undefined) {
      responseJson = responseBody.d.results;
    }
    return new dcc.box.odata.ODataResponse(this.accessor, responseJson);
  }*/
};

///**
//* ODataデータを取得.
//* @param {String} id 取得するID値
//* @param callback object optional
//* @return {Object} 取得したJSONオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method retrieves data in JSON form.
 * @param {String} id ID value
 * @param {Object} options optional callback, header and async parameters
 * @return {Object} JSON object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataManager.prototype.retrieveAsJson = function(id, options) {
  if (typeof id !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  var async = this._isAsynchronous(options);
  var response = this._internalRetrieve(id, options);
  var thenable = null;
  if (async) {
      thenable = response;
  } else {
      thenable = response.resolvedValue;
  }
  return thenable;
  //if (options !== undefined) {
    //this._internalRetrieve(id,options);
    /*this._internalRetrieve(id, function(resp) {
      if (resp.getStatusCode() >= 300) {
        if (options.error !== undefined) {
          options.error(resp);
        }
      } else {
        if (options.success !== undefined) {
          var responseBody = resp.bodyAsJson();
          var json = responseBody.d.results;
          options.success(json);
        }
      }
      if (options.complete !== undefined) {
        options.complete(resp);
      }
    });*/
  //} else {
    //return this._internalRetrieve(id, options);
  //}
};

///**
//* ODataデータを更新.
//* @param {String} id 対象となるID値
//* @param {Object} body PUTするリクエストボディ
//* @param {String} etag ETag値
//* @param callback object optional
//* @return dcc.box.odata.ODataResponse
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs update operation.
 * @param {String} id ID value
 * @param {Object} body PUT Request Body
 * @param {String} etag ETag value
 * @param {Object} options object optional
 * @return {dcc.box.odata.ODataResponse} Response
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataManager.prototype.update = function(id, body, etag, options) {
  if (typeof id !== "string" || typeof etag !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }

  /*valid option is present with atleast one callback*/
  /*var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);*/
  /*  
  if (callback !== undefined) {
    this._internalUpdate(id, body, etag, {}, function(resp) {
      if (resp.getStatusCode() >= 300) {
        if (callback.error !== undefined) {
          callback.error(resp);
        }
      } else {
        if (callback.success !== undefined) {
          var odataResponse = new dcc.box.odata.ODataResponse(resp.accessor, "");
          callback.success(odataResponse);
        }
      }
      if (callback.complete !== undefined) {
        callback.complete(resp);
      }
    });
  } 
   */

 // if (callbackExist) {
    //no return type expected, callback will be executed
    return this._internalUpdate(id, body, etag, {}, options);
  //}
  /*else {
    //no callback exists
    this._internalUpdate(id, body, etag);
    return new dcc.box.odata.ODataResponse(this.accessor, "");
  }*/
};

///**
//* ODataデータを削除.
//* @param {String} id 削除するODataデータのID値
//* @param {String} etag ETag値
//* @param callback object optional
//* @return promise
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs delete operation.
 * @param {String} id ID value
 * @param {String} etag ETag value
 * @param {Object} options object optional
 * @return {dcc.Promise} promise
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataManager.prototype.del = function(id, etag, options) {
  if (typeof id !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  if (typeof etag === "undefined") {
    etag = "*";
  }
  /*valid option is present with atleast one callback*/
 /* var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);*/
  /*  if (callback !== undefined) {
    this._internalDel(id, etag, function(resp) {
      if (resp.getStatusCode() >= 300) {
        if (callback.error !== undefined) {
          callback.error(resp);
        }
      } else {
        if (callback.success !== undefined) {
          var odataResponse = new dcc.box.odata.ODataResponse(resp.accessor, "");
          callback.success(odataResponse);
        }
      }
      if (callback.complete !== undefined) {
        callback.complete(resp);
      }
    });
  }*/
  //if (callbackExist) {
    return this._internalDel(id, etag, options);
  /*} else {
    this._internalDel(id, etag);
  }*/
};

/**
 * This method appends query string to execute Query for Search.
 * @param {dcc.box.odata.DcQuery} query
 * @param {Object} options object optional
 * @return {Object} JSON response
 */
dcc.box.odata.ODataManager.prototype.doSearch = function(query, options) {
  var url = this.getUrl();
  var qry = query.makeQueryString();
  var response = null;
  if ((qry !== null) && (qry !== "")) {
    url += "?" + qry;
  }
  if(!options){
      options = {};
  }
  if(!options.headers){
      options.headers = {};
      options.headers["Content-Type"] =  "application/json";
      options.headers["If-Match"] = "*";
      options.headers.Accept = "application/json";
  }
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  response = restAdapter.get(url, null, null, options);
  if(this._isAsynchronous(options)){
      return response;
  }else {
      return response.resolvedValue;
  }
  /*
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  if (options !== undefined) {
    restAdapter.get(url, null, "*", options);
  } else {
    restAdapter.get(url, "application/json", "*" );
    if(restAdapter.getStatusCode() > 300){
      throw new dcc.ClientException(restAdapter.bodyAsJson().message.value, restAdapter.getStatusCode());
    }
    var json = restAdapter.bodyAsJson().d.results;
    return json;
  }*/
};


/**
 * This method appends query string to execute Query for Search.
 * @param {dcc.box.odata.DcQuery} query
 * @param {Object} options optional parameters callbacks, headers and async
 * @return {dcc.box.odata.ODataResponse} response
 */
dcc.box.odata.ODataManager.prototype.doSearchAsResponse = function(query, options) {
  var url = this.getUrl();
  var qry = query.makeQueryString();
  if ((qry !== null) && (qry !== "")) {
    url += "?" + qry;
  }
  if(!options){
      options = {};
  }
  if(!options.headers){
      options.headers = {};
      options.headers["Content-Type"] =  "application/json";
      options.headers["If-Match"] = "*";
      options.headers.Accept = "application/json";
  }
  var response = null;
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  response = restAdapter.get(url, null, null, options);
  if(this._isAsynchronous(options)){
      return response;
  }else {
      return response.resolvedValue;
  }
 /* if (options !== undefined) {
    restAdapter.get(url, "application/json", "*", options);
  } else {
    restAdapter.get(url, "application/json", "*" );
    return new dcc.box.odata.ODataResponse(this.accessor, "", restAdapter.bodyAsJson());
  }*/
};

///**
//* クエリを生成.
//* @return {dcc.box.odata.DcQuery} 生成したQueryオブジェクト
//*/
/**
 * This method executes Query.
 * @return {dcc.box.odata.DcQuery} Query object generated
 */
dcc.box.odata.ODataManager.prototype.query = function() {
  return new dcc.box.odata.DcQuery(this);
};

///**
//* ODataデータの生存確認.
//* @param {String} id 対象となるODataデータのID
//* @return {boolean} true:生存、false:不在
//*/
/**
 * This method checks whether the specified Odata exists.
 * @param {String} id ID value
 * @return {DcHttpClient} response object
 * @throws {dcc.ClientException} exception
 */
dcc.box.odata.ODataManager.prototype.exists = function(id) {
  //var status = true;
  if (typeof id !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }

  var url = this.getUrl() + "('" + id + "')";
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.head(url);
  return response;
 /* try {
    var response = restAdapter.head(url);
    if(response.getStatusCode() === 404){
      status = false;
    }
  } catch (e) {
    status = false;
  }
  return status;*/
};

///**
//* URLを取得する.
//* @returns {String}　URL
//*/
/**
 * This method generates the URL for executing API calls.
 * @returns {String}　URL
 */
dcc.box.odata.ODataManager.prototype.getUrl = function() {
  var sb = "";
  // $Batchモードの場合は、相対パス
  /** In the case of $ Batch mode, the relative path . */
  if (!this.accessor.isBatchMode()) {
    sb += this.collection.getPath() + "/";
  }
  sb += this.entitySetName;
  // key()によりKeyPredicateとnav()によりnaviPropertyが指定されていたら
  /** naviProperty if it has been specified by the nav and KeyPredicate. */
  if ((this.keyPredicate !== null && this.keyPredicate !== "") &&
      (this.naviProperty !== null && this.naviProperty !== "")) {
    sb += "('" + this.keyPredicate + "')/_" + this.naviProperty;
  }
  return sb;
};
/**
 * This method determine whether the requested execution mode is synchronous or asynchronous.
 * Preference to mode specification is evaluated in order of precedence as options, accessor, library default.
 * @param {JSON} options
 * @returns {Boolean} response true if call is asynchronous else false
 */
dcc.box.odata.ODataManager.prototype._isAsynchronous = function(options) {
  //if options.async is present then use async as specified mode
  if(options !== undefined && options !== null && options.async !== undefined){
    //true or false
    return options.async;
  }else if (this.accessor.getContext().getAsync() !== undefined){
    //if no specification is found at option level, use accessor level settings
    return this.accessor.getContext().getAsync();
  } else{
    return false;
  }
};
