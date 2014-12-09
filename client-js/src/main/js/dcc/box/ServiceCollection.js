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
//* @class ServiceのCURDのためのクラス.
//* @constructor
//* @augments dcc.DcCollection
//*/
/**
 * It creates a new object dcc.box.ServiceCollection.
 * @class This class performs CRUD operations for ServiceCollection.
 * @constructor
 * @augments dcc.DcCollection
 * @param {dcc.Accessor} as Accessor
 * @param {String} path
 */
dcc.box.ServiceCollection = function(as, path) {
    this.initializeProperties(this, as, path);
};
dcc.DcClass.inherit(dcc.box.ServiceCollection, dcc.DcCollection);

///**
//* プロパティを初期化する.
//* @param {dcc.box.ServiceCollection} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {?} path
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.box.ServiceCollection} self
 * @param {dcc.Accessor} as Accessor
 * @param {String} path
 */
dcc.box.ServiceCollection.prototype.initializeProperties = function(self, as, path) {
    this.uber = dcc.DcCollection.prototype;
    this.uber.initializeProperties(self, as, path);
};

///**
//* サービスの設定.
//* @param {String} key プロパティ名
//* @param {String} value プロパティの値
//* @param {String} subject サービスサブジェクトの値
//* @throws {ClientException} DAO例外
//*/
/**
 * This method configures a set of services.
 * @param {String} key Property Name
 * @param {String} value Property values
 * @param {String} subject Value of the service subject
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.ServiceCollection.prototype.configure = function(key, value, subject) {
    var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
    var response = restAdapter.setService(this.getPath(), key, value, subject);
    return response;
};

/**
 * This method contains common call back logic.
 * @param {Object} resp response received
 * @param {Object} options optional callback, header and async parameters
 * @private
 */
dcc.box.ServiceCollection.prototype.processCallback = function(resp, options) {
    if (resp.getStatusCode() >= 300) {
        if (options.error !== undefined) {
            options.error(resp);
        }
    } else {
        if (options.success !== undefined) {
            options.success(resp);
        }
    }
    if (options.complete !== undefined) {
        options.complete(resp);
    }
};

///**
//* Method is responsible for deciding which implementation of call is to be used.
//* decision will be taken based on the type of bodyOrHeader parameter
//* @param method メソッド
//* @param name 実行するサービス名
//* @param bodyOrOptions can contain either options or body
//* @param callback contains call back, not required if options is specified
//* @return DcResponseオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is responsible for deciding which implementation of call is to be used.
 * Decision will be taken based on the type of bodyOrHeader parameter
 * @param {String} method Method
 * @param {String} name Service name to be executed
 * @param {Object} bodyOrOptions can contain either options or body
 * @param {Object} callback optional callback, header and async parameters
 * @return {dcc.http.DcResponse} DcResponse object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.ServiceCollection.prototype.call = function(method, name, bodyOrOptions,
        callback) {
    var response = null;
    var options = {};
    if (!(bodyOrOptions !== null && typeof bodyOrOptions === "object")) {
        //bodyOrOptions is body
        options.body = bodyOrOptions;
    }else{
        options = bodyOrOptions;
    }
    if (!options.headers){
        options.headers ={};
    }
    if(!options.headers.Accept){
        options.headers.Accept = "text/plain";//default
    }
    if(!options.headers.ContentType){
        options.headers.ContentType = "text/plain";//default 
    }
    if(!options.headers["If-Match"]){
        options.headers["If-Match"] = "*";//default
    }
    options.name = name;
    /** backward compatibility. */
    if (callback){
        options.success = callback.success;
        options.error = callback.error;
        options.complete = callback.complete;
    }

    response = this.request(method, options);
    return response;
};

///**
//* サービスの実行.
//* @param method メソッド
//* @param name 実行するサービス名
//* @param body リクエストボディ
//* @return DcResponseオブジェクト
//* @throws {ClientException} DAO例外
//* @private
//*/
/**
 * Method _callWithNOOptions - an overloaded version with option containing only body.
 * @param {String} method Method
 * @param {String} name Service name to be executed
 * @param {Object} body Request Body
 * @param {Object} options optional parameters and callbacks
 * @return {dcc.http.DcResponse} DcResponse object
 * @throws {dcc.ClientException} DAO exception
 * @private
 */
/*dcc.box.ServiceCollection.prototype._callWithNoOptions = function(method, name,
        body, options) {
    var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
    var url = dcc.UrlUtils.append(this.getPath(), name);
    var defaultContentType = "text/plain";
    var response = null;
    var callbackExist = options !== undefined &&
    (options.success !== undefined ||
            options.error !== undefined ||
            options.complete !== undefined);
    //var self = this;
    if (method === "GET") {
        //if (callback !== undefined) {/*
        if (this.accessor.getContext().getAsync()) {
            //process asynchronous mode

      restAdapter.get(url, defaultContentType, null, function(resp) {
        self.processCallback(resp, callback);
      });


            //pass the callback,callback execution will be invoked at DcHttpClient layer
            restAdapter.get(url, defaultContentType, null, options);
        }
        else {
            //requested mode is synchronous
            response = restAdapter.get(url, defaultContentType);
            return response;
        }
    }
    if (method === "POST") {
            if (callback !== undefined) {
      restAdapter.post(url, body, defaultContentType, {}, function(resp) {
        self.processCallback(resp, callback);
      });
    } 
        //if(this.accessor.getContext().getAsync()){
        if (callbackExist) {
            restAdapter.post(url, body, defaultContentType, {},options);
        }
        else {
            response = restAdapter.post(url, body, defaultContentType, {});
            return response;
        }
    }
    if (method === "PUT") {
        if (callbackExist) {
            restAdapter.put(url, body, "*", defaultContentType, {}, function(
          resp) {
        self.processCallback(resp, callback);
      });
            restAdapter.put(url, body, "*", defaultContentType, {},options);
        } else {
            response = restAdapter.put(url, body, "*", defaultContentType, {});
            return response;
        }
    }
    if (method === "DELETE") {
        if (options !== undefined) {
                  restAdapter.del(url, "*", function(resp) {
        self.processCallback(resp, callback);
      });
            restAdapter.del(url, "*", options);
        } else {
            response = restAdapter.del(url, "*");
            return response;
        }
    }
};
 */
///**
//* Method _callWithOptions - an overloaded version with option containing header,body and callback.
//* @param method メソッド
//* @param name 実行するサービス名
//* @param options containing header,body and success, error, complete callback
//* @return DcResponseオブジェクト
//* @throws {ClientException} DAO例外
//* @private
//*/
/**
 * Method request - an overloaded version with option containing header,body and callback.
 * @param {String} method method
 * @param {Object} options containing header,body and success, error, complete callback
 * @return {dcc.http.DcResponse} DcResponse object
 * @throws {dcc.ClientException} DAO exception
 * @private
 */
dcc.box.ServiceCollection.prototype.request = function(method, options) {
    var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
    var url = dcc.UrlUtils.append(this.getPath(), options.name);
    var response = null;
    var async = this._isAsynchronous(options);
    if (method === "GET") {
        response = restAdapter.get(url, null, null,options);
    }
    if (method === "POST") {
        response = restAdapter.post(url, null, null, null, options);
    }
    if (method === "PUT") {
        response = restAdapter.put(url, null, null, null, null,options);
    }
    if (method === "DELETE") {
        response =  restAdapter.del(url, options,options);
    }
    if(async){
        return response;
    }else{
        return response.resolvedValue;
    }
};

///**
//* 指定Pathに任意の文字列データをPUTします.
//* @param pathValue DAVのパス
//* @param contentType メディアタイプ
//* @param data PUTするデータ
//* @param etagValue PUT対象のETag。新規または強制更新の場合は "*" を指定する
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to upload a file or update a string of data.
 * @param {String} pathValue Path of DAV
 * @param {Object} options contains callback, headers and body
 * @returns {dcc.Promise} promise
 * @throws {dcc.ClientException} exception
 */
//public void put(String pathValue, String contentType, String data, String etagValue) throws ClientException {
/*dcc.box.ServiceCollection.prototype.put = function(pathValue, contentType, data,
    etag) {*/
dcc.box.ServiceCollection.prototype.put = function(pathValue, options) {
    if(!options){
        options = {};
    }
    //byte[] bs;
    //try {
    //bs = data.getBytes("UTF-8");
    //} catch (UnsupportedEncodingException e) {
    //throw new ClientException("UnsupportedEncodingException", e);
    //}
    //InputStream is = new ByteArrayInputStream(bs);
    //this.put(pathValue, contentType, is, etagValue);
    var url = dcc.UrlUtils.append(this.getPath(), "__src/" + pathValue);
    //TODO: Change put to except only 2 parameters- URL and options
    var thenable = dcc.http.RestAdapterFactory.create(this.accessor).put(url, null, null,
            null, null, options);
    if(this._isAsynchronous(options)){
        return thenable;
    }else{
        return thenable.resolvedValue;
    }
};

///**
//* 指定pathに任意のInputStreamの内容をPUTします. 指定IDのオブジェクトが既に存在すればそれを書き換え、存在しない場合はあらたに作成する.
//* @throws {ClientException} DAO例外
//*/
//public void put(String pathValue, String contentType, InputStream is, String etagValue) throws ClientException {
//dcc.box.ServiceCollection.prototype.put = function() {
//var url = dcc.UrlUtils.append(this.getPath(), "__src/" + pathValue);
//var restAdapter = new dcc.http.RestAdapterFactory().create(this.accessor);
//restAdapter.putStream(url, contentType, is, etagValue);
//};
///**
//* 指定PathのデータをDeleteします.
//* @param {String} pathValue DAVのパス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method deletes the data in the path specified.
 * @param {String} pathValue DAV Path
 * @param {String} etagOrOptions ETag value or options object having callback and headers
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.ServiceCollection.prototype.del = function(pathValue, etagOrOptions) {
    var url = dcc.UrlUtils.append(this.getPath(), "__src/" + pathValue);
    var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
    restAdapter.del(url, etagOrOptions);
};

/**
 * This method calls PROPFIND API for specified path to get 
 * registered service file detail. 
 * @returns {dcc.DcHttpClient} response.
 * @throws {dcc.ClientException} Exception thrown
 */
dcc.box.ServiceCollection.prototype.propfind = function () {
    var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
    var response = restAdapter.propfind(this.url);
    return response;
};

/**
 * The purpose of this method is to perform service configure operation for multiple service
 * in one API call. 
 * @param {String[]} arrServiceNameAndSrcFile service list in combination of service name and source file
 * @param {String} subject Service
 * @param {Object} options refers to optional parameters - callback, headers.
 * @throws {dcc.ClientException} Exception
 */
dcc.box.ServiceCollection.prototype.multipleServiceConfigure = function(arrServiceNameAndSrcFile, subject, options) {
    var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
    var response = restAdapter.setMultipleService(this.getPath(), arrServiceNameAndSrcFile, subject, options);
    return response;
};