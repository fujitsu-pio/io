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
//* @class OData関連の各機能を生成/削除するためのクラスの抽象クラス.
//* @constructor
//*/
/**
 * It creates a new object dcc.EntitySet.
 * @class This is the abstract class for performing the merge functions.
 * @param {dcc.Accessor} Accessor
 * @param {dcc.DcCollection} collection
 * @param {String} name
 */
dcc.EntitySet = function(as, col, name) {
  this.initializeProperties(this, as, col, name);
};
dcc.DcClass.inherit(dcc.EntitySet, dcc.box.odata.ODataManager);

///**
//* プロパティを初期化する.
//* @param {dcc.AbstractODataContext} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {?} col
//* @param {?} name
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.AbstractODataContext} self
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcCollection} col
 * @param {String} name
 */
dcc.EntitySet.prototype.initializeProperties = function(self, as, col, name) {
  this.uber = dcc.box.odata.ODataManager.prototype;
  this.uber.initializeProperties(self, as, col, name);
};

///**
//* Odataデータを部分更新.
//* @param {String} id 部分更新するOdataデータのID値
//* @param {Object} body 部分更新するリクエストボディ
//* @param {String} etag Etag値
//* @throws {ClientException} DAO例外
//*/
/**
 * This method performs the partial update of the Odata data.
 * @param {String} id ID value of the data
 * @param {Object} body Request body
 * @param {String} etag Etag value
 * @param {Object} options optional callback, header and async parameters
 * @throws {dcc.ClientException} DAO exception
 */
dcc.EntitySet.prototype.internalMerge = function(id, body, etag, options) {
  var url = this.getUrl() + "('" + id + "')";
  var rest = dcc.http.RestAdapterFactory.create(this.accessor);
  /*if (options !== undefined) {
    rest.merge(url, JSON.stringify(body), etag, "application/json", function(resp) {
        options(resp);
    });
    rest.merge(url, JSON.stringify(body), etag, "application/json", options);
  } else {*/
    return rest.merge(url, JSON.stringify(body), etag, "application/json", options);
  //}
};

///**
//* Odataデータを部分更新.
//* @param {String} id 対象となるID値
//* @param {Object} body PUTするリクエストボディ
//* @param {String} etag ETag値
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is exposed to perform the partial update of the Odata data.
 * @param {String} id ID value of the data
 * @param {Object} body Request body
 * @param {String} etag ETag value
 * @param {Object} options optional callback, header and async parameters
 * @throws {dcc.ClientException} DAO exception
 */
dcc.EntitySet.prototype.merge = function(id, body, etag, options) {
  //if (options !== undefined) {
    var async = this._isAsynchronous(options);
    var response = this.internalMerge(id, body, etag,options);
    var thenable = null;
    if (async) {
        thenable = response;
    } else {
        thenable = response.resolvedValue;
    }
    return thenable;
    /*this.internalMerge(id, body, etag, function(resp) {
      if (resp.getStatusCode() >= 300) {
        if (options.error !== undefined) {
          options.error(resp);
        }
      } else {
        var odataResponse = new dcc.box.odata.ODataResponse(resp.accessor, "");
        if (options.success !== undefined) {
          options.success(odataResponse);
        }
      }
      if (options.complete !== undefined) {
        options.complete(resp);
      }
    });*/
  /*} else {
    this.internalMerge(id, body, etag);
    return new dcc.box.odata.ODataResponse(this.accessor, "");
  }*/
};

///**
//* Odataデータを取得.
//* @param {String} id 対象となるID値
//* @throws {ClientException} DAO例外
//*/
/**
 * This method gets Odata as response.
 * @param {String} id ID value
 * @param {Object} options optional callback, header and async parameters
 * @throws {dcc.ClientException} DAO exception
 */
dcc.EntitySet.prototype.retrieveAsResponse = function(id, options) {
    var async = this._isAsynchronous(options);
    var response = this._internalRetrieve(id,options);
    var thenable = null;
    if (async) {
        thenable = response;
    } else {
        thenable = response.resolvedValue;
    }
    return thenable;
  /*  if (callback !== undefined) {
    this._internalRetrieve(id, function(resp) {
      if (resp.getStatusCode() >= 300) {
        if (callback.error !== undefined) {
          callback.error(resp);
        }
      } else {
        var responseBody = resp.bodyAsJson();
        var json = responseBody.d.results;
        var odataResponse = new dcc.box.odata.ODataResponse(resp.accessor, json);
        if (callback.success !== undefined) {
          callback.success(odataResponse);
        }
      }
      if (callback.complete !== undefined) {
        callback.complete(resp);
      }
    });
  } */
 // if(this.accessor.getContext().getAsync()){
    //return this._internalRetrieve(id,options);
  //}
  /*else {
    var body = this._internalRetrieve(id);
    return new dcc.box.odata.ODataResponse(this.accessor, body);
  }*/
};
