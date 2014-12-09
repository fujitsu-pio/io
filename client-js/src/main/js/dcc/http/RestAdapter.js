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
//* @class RESTアクセスのためのクラス.
//* @param as ACCESSOR object
//* @constructor
//*/
/**
 * It creates a new object dcc.http.RestAdapter.
 * @class This class is used for REST access.
 * @param {dcc.Accessor} as ACCESSOR object
 * @constructor
 * @param {dcc.Accessor} Accessor
 */
dcc.http.RestAdapter = function(as) {
  this.initializeProperties(as);
};

if (typeof exports === "undefined") {
  exports = {};
}
exports.RestAdapter = dcc.http.RestAdapter;

///**
//* プロパティを初期化する.
//* @param as ACCESSOR object
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.Accessor} as ACCESSOR object
 */
dcc.http.RestAdapter.prototype.initializeProperties = function(as) {
///** アクセス主体. */
  /** Accessor object. */
  this.accessor = as;

  /** HTTPClient. */
  var config = as.getClientConfig();
  this.httpConnectionTimeout = config.getConnectionTimeout();

  this.httpClient = this.createHttpClient();
};

///**
//* HTTPクライアントのインスタンスを返す.
//* @return {object} HTTPクライアントオブジェクト
//*/
/**
 * This method returns an instance of the HTTP client.
 * @return {dcc.http.DcHttpClient} HTTP Client object
 */
dcc.http.RestAdapter.prototype.createHttpClient = function() {
  // TODO ファクトリクラス化する
  return new dcc.http.DcHttpClient();
};

///**
//* レスポンスボディを受け取るGETメソッド(If-None-Macth指定).
//* @param requestUrl リクエスト対象URL
//* @param accept Acceptヘッダ値
//* @param etag 取得するEtag
//* @param callback object
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * GET method to receive the response body (If-None-Macth specified).
 * @param {String} requestUrl
 * @param {String} accept
 * @param {String} etag
 * @param {Object} options  object contains success, error, complete callback, headers and body
 * @return {dcc.http.DcHttpClient} DcHttpClient
 * @throws {dcc.ClientException} DAO exception
 */
dcc.http.RestAdapter.prototype.get = function(requestUrl, accept, etag, options, filter) {
  var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.accept(accept);
  builder.token(this.accessor.accessToken);
  builder.ifNoneMatch(etag);
  builder.defaultHeaders(this.accessor.getDefaultHeaders());

  var xhr = this.httpClient;

  //Validate and prepare the option considering backward compatibility also
  var opts = {};
  if(!options){
    //if no valid options present, instantiate empty opts
    opts  = {};
    opts.headers = {};
  }else{
    opts = options;
    if(!options.headers){
      opts.headers = {};
    }
  }

  /** backward compatibility - if header parameters are not passed on options. */
  if(typeof accept === "string"){
    /**  If a string comes, it will be sent as Accept header. */
    opts.headers.Accept = accept;
  }
  if(typeof etag === "string"){
    /**  If a string comes, it will be sent as If-Match header. */
    opts.headers["If-Match"] = etag;
  }
  //End prepare option object

  //this.request(xhr, "GET", requestUrl, "", builder, {}, callback);
  var response = this._request(xhr, "GET", requestUrl, opts, filter);
  //if(options!== undefined && options.thenable){
    return response;//return thenable
  //}
  //if (!this.accessor.getContext().getAsync()) {
  /*if (!this.accessor.getContext().getAsync()) {
    return this.httpClient;
  }*/
};

///**
//* レスポンスボディをバイナリ型式で受け取るGETメソッド(If-None-Macth指定).
//* @param requestUrl リクエスト対象URL
//* @param etag 取得するEtag
//* @param callback object
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * GET method that takes a binary model in the request body (If-None-Match specified).
 * @param {String} requestUrl
 * @param {String} etag
 * @param {Object} options optional callback, header and async parameters
 * @return {dcc.http.DcHttpClient} DcHttpClient
 * @throws {dcc.ClientException} DAO exception
 */
dcc.http.RestAdapter.prototype.getBinary = function(requestUrl, etag, options) {
  var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.token(this.accessor.accessToken);
  builder.ifNoneMatch(etag);
  builder.defaultHeaders(this.accessor.getDefaultHeaders());

  //Validate and prepare the option considering backward compatibility also
  var opts = {};
  if(!options){
    //if no valid options present, instantiate empty opts
    opts  = {};
    opts.headers = {};
  }else{
    opts = options;
    if(!options.headers){
      opts.headers = {};
    }
  }

  /** backward compatibility - if header parameters are not passed on options. */
  if(typeof etag === "string"){
    /**  If a string comes, it will be sent as If-Match header. */
    opts.headers["If-Match"] = etag;
  }
  //End prepare option object
  var xhr = this.httpClient;
  xhr.setOverrideMimeType("text/plain; charset=x-user-defined");
  //this.request(xhr, "GET", requestUrl, "", builder, {}, options);
  this._request(xhr, "GET", requestUrl, opts);
  if (options === undefined) {
    return this.httpClient;
  }
};

///**
//* HEADメソッド.
//* @param requestUrl リクエスト対象URL
//* @param {string} etag Used for if-none-match condition
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * This method uses default headers to fetch the data.
 * @param {String} requestUrl
 * @param {string} etag Used for if-none-match condition
 * @return {dcc.http.DcHttpClient} DcHttpClient
 * @throws {dcc.ClientException} DAO exception
 */
dcc.http.RestAdapter.prototype.head = function(requestUrl, etag) {
  return this.get(requestUrl, "application/json", etag);
};

///**
//* レスポンスボディを受ける PUTメソッド.
//* @param requestUrl リクエスト対象URL
//* @param requestBody data 書き込むデータ
//* @param etag ETag
//* @param contentType CONTENT-TYPE値
//* @param headers header object
//* @param callback object
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * PUT method to receive the response body.
 * @param {String} requestUrl
 * @param {Object} requestBody data
 * @param {String} ETag value
 * @param {String} CONTENT-TYPE value
 * @param {Object} header object
 * @param {Object} options object
 * @return {dcc.http.DcHttpClient} DcHttpClient
 * @throws {dcc.ClientException} DAO exception
 */
dcc.http.RestAdapter.prototype.put = function(requestUrl, requestBody, etag, contentType, headers, options, filter) {
  var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.contentType(contentType);
  builder.ifMatch(etag);
  builder.token(this.accessor.accessToken);
  builder.defaultHeaders(this.accessor.getDefaultHeaders());

  if(!options){
    options = {};
    options.body = requestBody;
    if(headers){
      options.headers = headers;
    }else{
      options.headers = {};
    }
    if(contentType && contentType !== null){
      options.headers["Content-Type"] = contentType;
    }
    if(etag && etag !== null){
      options.headers["If-Match"] = etag;
    }
  }else{
    if(!options.body){
      options.body = requestBody;
    }
    if(!options.headers){
      if(!headers){
        options.headers = {};
      } else{
        options.headers = headers;
      }
      if(contentType){
        options.headers["Content-Type"] = contentType;
      }
      if(etag){
        options.headers["If-Match"] = etag;
      }
    }
  }
  var xhr = this.httpClient;
  //this.request(xhr, "PUT", requestUrl, requestBody, builder, headers, options);
  return this._request(xhr, "PUT", requestUrl, options, filter);
  //valid option is present with at least one callback
  //return this.httpClient;
};

///**
//* リクエストボディを受け取る POSTメソッド.
//* @param requestUrl リクエスト対象URL
//* @param requestBody data 書き込むデータ
//* @param contentType CONTENT-TYPE値
//* @param headers header object
//* @param callback object
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * POST method that receives the request body.
 * @param {String} requestUrl
 * @param {Object} requestBody data
 * @param {String} CONTENT-TYPE value
 * @param {Object} header object
 * @param {Object} options object
 * @param {Object} filter instance of an entity
 * @return {dcc.http.DcHttpClient} DcHttpClient
 * @throws {dcc.ClientException} DAO exceptionn
 */
dcc.http.RestAdapter.prototype.post = function(requestUrl, requestBody, contentType, headers, options, filter) {
  var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.contentType(contentType);
  builder.token(this.accessor.accessToken);
  builder.defaultHeaders(this.accessor.getDefaultHeaders());
  var xhr = this.httpClient;
  //this.request(xhr, "POST", requestUrl, requestBody, builder, headers, callback);
  if(!options){
    options = {};
    options.body = requestBody;
    if(headers){
      options.headers = headers;
    }else{
      options.headers = {};
    }
    options.headers["Content-Type"] = contentType;
  }else{
    if(!options.body){
      options.body = requestBody;
    }
    if(!options.headers){
      if(!headers){
        options.headers = {};
      } else{
        options.headers = headers;
      }
      options.headers["Content-Type"] = contentType;
    }
  }
  return this._request(xhr, "POST", requestUrl, options, filter);
/*  if (options.success === undefined && options.error === undefined && options.complete === undefined) {
    return this.httpClient;
  }*/
};

///**
//* レスポンスボディを受けるMERGEメソッド.
//* @param requestUrl リクエスト対象URL
//* @param requestBody data 書き込むデータ
//* @param etag ETag
//* @param contentType CONTENT-TYPE値
//* @param callback object
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * MERGE method to receive the response body.
 * @param {String} requestUrl
 * @param {Object} requestBody data
 * @param {String} ETag
 * @param {String} CONTENT-TYPE value
 * @param {Object} options optional callback, header and async parameters
 * @return {dcc.Promise} promise
 * @throws {dcc.ClientException} DAO exception
 */
dcc.http.RestAdapter.prototype.merge = function(requestUrl, requestBody, etag, contentType, options) {
  var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.contentType(contentType);
  builder.ifMatch(etag);
  builder.token(this.accessor.accessToken);
  builder.defaultHeaders(this.accessor.getDefaultHeaders());

  var xhr = this.httpClient;
  //this.request(xhr, "MERGE", requestUrl, requestBody, builder, {}, options);
  var opts = {};
  if(!options){
    //if no valid options present, instantiate empty opts
    opts  = {};
    opts.headers = {};
  }else{
    opts = options;
    if(!options.headers){
      opts.headers = {};
    }
  }
  if(etag !== undefined){
    opts.headers["If-Match"] = etag;
  }
  if(contentType !== undefined){
    opts.headers["Content-Type"] = contentType;
  }
  opts.body = requestBody;
  return this._request(xhr, "MERGE", requestUrl, opts);
  /*if (options === undefined) {
    return this.httpClient;
  }*/
};

/**
 * This method issues DELETE HTTP request.
 * @param {String} requestUrl target URL to issue DELETE method.
 * @param {String/Object} optionsOrEtag non-mandatory options. If a string is sent it will be sent as If-Match header value for backward compatibility.
 * @param {Object} options.headers Any Extra HTTP request headers to send.
 * @param {Function} options.success Callback function for successful result.
 * @param {Function} options.error Callback function for error response.
 * @param {Function} options.complete Callback function for any response, either successful or error.
 * @param {Object} options optional callback, header and async parameters
 * @return {dcc.Promise} Promise
 * @throws {dcc.ClientException} DAO exception
 */
dcc.http.RestAdapter.prototype.del = function(requestUrl, optionsOrEtag, options) {
  var optionsParam = {};
  if(!optionsOrEtag){
    optionsOrEtag = {};
  }
  if(!optionsOrEtag.headers){
    optionsOrEtag.headers = {};
  }
  /** backward compatibility. */
  if(typeof optionsOrEtag === "string"){
    /**  If a string comes, it will be sent as If-Match header. */
    optionsParam.headers = {};
    optionsParam.headers["If-Match"] = optionsOrEtag;
  }else{
    optionsParam = optionsOrEtag;
  }
  /** backward compatibility. */
  if (options){
    optionsParam.success = options.success;
    optionsParam.error = options.error;
    optionsParam.complete = options.complete;
  }
  
  if(options && options.async){
      optionsParam.async = options.async;
  }
  /** use the new version of internal request method. */
  return this._request(this.httpClient, "DELETE", requestUrl, optionsParam);
};

///**
//* ACLメソッド.
//* @param requestUrl リクエスト対象URL
//* @param requestBody リクエストボディ
//* @param callback (deprecated) for backward compatibility.
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * ACL method to retrieve ACL settings.
 * @param {String} requestUrl
 * @param {Object} requestBody
 * @param {Object} options contains callback and headers.
 * @return {dcc.http.DcHttpClient} httpClient
 * @throws {dcc.ClientException} exception
 */
dcc.http.RestAdapter.prototype.acl = function(requestUrl, requestBody, options) {
  //var builder = new dcc.http.DcRequestHeaderBuilder();
  //builder.contentType("application/xml");
  //builder.accept("application/xml");
  //builder.token(this.accessor.accessToken);
  //builder.defaultHeaders(this.accessor.getDefaultHeaders());
  //Validate and prepare the option
  var opts = {};
  if(!options){
    //if no valid options present, instantiate empty opts
    opts  = {};
    opts.headers = {};
  }else{
    opts = options;
    if(!options.headers){
      opts.headers = {};
    }
  }
  /** if header parameters are not passed on options, set default value. */
  if(!opts.headers.Accept){
    opts.headers.Accept = "application/xml";
  }
  if(!opts.headers["Content-Type"]){
    opts.headers["Content-Type"] = "application/xml";
  }
  //End prepare option object
  opts.body = requestBody;
  var xhr = this.httpClient;
  this._request(xhr, "ACL", requestUrl, opts);
  //this.request(xhr, "ACL", requestUrl, requestBody, builder, {}, objects);
  var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);
  if (!callbackExist) {
    return this.httpClient;
  }
};

///**
//* MKCOLメソッド.
//* @param requestUrl リクエスト対象URL
//* @param callback (deprecated) for backward compatibility.
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * MKCOL method for creating collections.
 * @param {String} requestUrl
 * @param {Object} options optional callback, header and async parameters
 * @return {dcc.http.DcHttpClient} DcHttpClient
 * @throws {dcc.ClientException} DAO exception
 */
dcc.http.RestAdapter.prototype.mkcol = function(requestUrl, options) {
  /** MKCol用リクエストボディ. */
  var REQUEST_BODY_MKCOL_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
  "<D:mkcol xmlns:D=\"DAV:\" xmlns:dc=\"urn:x-dc1:xmlns\"><D:set><D:prop><D:resourcetype><D:collection/>" +
  "</D:resourcetype></D:prop></D:set></D:mkcol>";

  var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.contentType("application/xml");
  builder.accept("application/xml");
  builder.token(this.accessor.accessToken);
  builder.defaultHeaders(this.accessor.getDefaultHeaders());

  var xhr = this.httpClient;
  if (!options) {
      options = {};
      options.body = REQUEST_BODY_MKCOL_XML;
      if(!options.headers){
          options.headers = {};
          options.headers.Accept = "application/xml";
          options.headers["Content-Type"] = "application/xml";
      }
  } else {
      if(!options.body){
          options.body = REQUEST_BODY_MKCOL_XML;
        }
        if(!options.headers){
            options.headers = {};
            options.headers.Accept = "application/xml";
            options.headers["Content-Type"] = "application/xml";
        }
  }
  //this.request(xhr, "MKCOL", requestUrl, REQUEST_BODY_MKCOL_XML, builder, {}, options);
  return this._request(xhr, "MKCOL", requestUrl, options);
  /*if (options === undefined || options === "") {
    return this.httpClient;
  }*/
};

///**
//* MKCOL拡張メソッド(ODataコレクション作成).
//* @param requestUrl リクエスト対象URL
//* @param callback (deprecated) for backward compatibility.
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * MKCOL method for creating odata collections.
 * @param {String} requestUrl
 * @param {Object} options optional callback, header and async parameters
 * @return {dcc.http.DcHttpClient} DcHttpClient
 * @throws {dcc.ClientException} DAO exception
 */
dcc.http.RestAdapter.prototype.mkOData = function(requestUrl, options) {
  /** MKOData用リクエストボディ. */
  var REQUEST_BODYMKODATA_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
  "<D:mkcol xmlns:D=\"DAV:\" xmlns:dc=\"urn:x-dc1:xmlns\"><D:set><D:prop><D:resourcetype><D:collection/>" +
  "<dc:odata/></D:resourcetype></D:prop></D:set></D:mkcol>";

  var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.contentType("application/xml");
  builder.accept("application/xml");
  builder.token(this.accessor.accessToken);
  builder.defaultHeaders(this.accessor.getDefaultHeaders());

  var xhr = this.httpClient;
  if (!options) {
      options = {};
      options.body = REQUEST_BODYMKODATA_XML;
      if(!options.headers){
          options.headers = {};
          options.headers.Accept = "application/xml";
          options.headers["Content-Type"] = "application/xml";
      }
  } else {
      if(!options.body){
          options.body = REQUEST_BODYMKODATA_XML;
        }
        if(!options.headers){
            options.headers = {};
            options.headers.Accept = "application/xml";
            options.headers["Content-Type"] = "application/xml";
        }
  }
  //this.request(xhr, "MKCOL", requestUrl, REQUEST_BODYMKODATA_XML, builder, {}, options);
return this._request(xhr, "MKCOL", requestUrl, options);

  /*if (options === undefined) {
    return this.httpClient;
  }*/
};

///**
//* MKCOL拡張メソッド(Serviceコレクション作成).
//* @param requestUrl リクエスト対象URL
//* @param callback (deprecated) for backward compatibility.
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * MKCOL method for creating service collections.
 * @param {String} requestUrl
 * @param {Object} options optional callback, header and async parameters
 * @return {dcc.http.DcHttpClient} DcHttpClient
 * @throws {dcc.ClientException} DAO exception
 */
dcc.http.RestAdapter.prototype.mkService = function(requestUrl, options) {
  /** サービスコレクション用リクエストボディ. */
  var REQUEST_BODY_SERVICE_XML = "<?xml version=\"1.0\" encoding=\"utf-8\"?>" +
  "<D:mkcol xmlns:D=\"DAV:\" xmlns:dc=\"urn:x-dc1:xmlns\"><D:set><D:prop><D:resourcetype>" +
  "<D:collection/><dc:service/></D:resourcetype></D:prop></D:set></D:mkcol>";

  var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.contentType("application/xml");
  builder.accept("application/xml");
  builder.token(this.accessor.accessToken);
  builder.defaultHeaders(this.accessor.getDefaultHeaders());

  var xhr = this.httpClient;
  if (!options) {
      options = {};
      options.body = REQUEST_BODY_SERVICE_XML;
      if(!options.headers){
          options.headers = {};
          options.headers.Accept = "application/xml";
          options.headers["Content-Type"] = "application/xml";
      }
  } else {
      if(!options.body){
          options.body = REQUEST_BODY_SERVICE_XML;
        }
        if(!options.headers){
            options.headers = {};
            options.headers.Accept = "application/xml";
            options.headers["Content-Type"] = "application/xml";
        }
  }
  //this.request(xhr, "MKCOL", requestUrl, REQUEST_BODY_SERVICE_XML, builder, {}, options);
  return this._request(xhr, "MKCOL", requestUrl, options);
  /*if (options === undefined) {
    return this.httpClient;
  }*/
};

///**
//* サービス登録専用PROPPATCHメソッド.
//* @param requestUrl リクエスト対象URL
//* @param key プロパティ名
//* @param value プロパティの値
//* @param subject サービスサブジェクトの値
//* @param callback (deprecated) for backward compatibility.
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * Service registration only PROPPATCH method.
 * @param {String} requestUrl
 * @param {String} key
 * @param {String} value
 * @param {String} subject
 * @param {Object} callback (deprecated) for backward compatibility.
 * @return {dcc.http.DcHttpClient} DcHttpClient
 * @throws {dcc.ClientException} DAO exception
 */
/*dcc.http.RestAdapter.prototype.setService = function(requestUrl, key, value, subject, callback) {
  var sb = "";
  sb += "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";
  sb += "<D:propertyupdate xmlns:D=\"DAV:\" xmlns:dc=\"urn:x-dc1:xmlns\" xmlns:Z=\"http://www.w3.com/standards/z39.50/\"><D:set><D:prop>";
  sb += "<dc:service language=\"JavaScript\" subject=\"" + subject + "\">";
  sb += "<dc:path name=\"" + key + "\" src=\"" + value + "\"/>";
  sb += "</dc:service></D:prop></D:set></D:propertyupdate>";

  var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.contentType("application/xml");
  builder.accept("application/xml");
  builder.token(this.accessor.accessToken);
  builder.defaultHeaders(this.accessor.getDefaultHeaders());

  var xhr = this.httpClient;
  this.request(xhr, "PROPPATCH", requestUrl, sb, builder, {}, callback);

  if (callback === undefined) {
    return this.httpClient;
  }
};*/

/**
 * The purpose of this method is to set service(s) single/multiple in one API call
 * through PROPATCH.
 * @param {String} requestUrl target URL
 * @param {String[]} arrServiceNameAndSrcFile service list in combination of service name and source file 
 * example {"serviceName":"name","sourceFileName" : "filename.js"}.
 * @param {String} subject Service
 * @param {Object} options refers to optional parameters - callback, headers.
 * @return {dcc.http.DcHttpClient} response
 * @throws {dcc.ClientException} Exception
 */
dcc.http.RestAdapter.prototype.setService = function(requestUrl, arrServiceNameAndSrcFile, subject, options) {
  var key = null;
  var value = null;
  var xhr = this.httpClient;
  var sb = "";
  sb += "<?xml version=\"1.0\" encoding=\"utf-8\" ?>";
  sb += "<D:propertyupdate xmlns:D=\"DAV:\" xmlns:dc=\"urn:x-dc1:xmlns\" xmlns:Z=\"http://www.w3.com/standards/z39.50/\"><D:set><D:prop>";
  sb += "<dc:service language=\"JavaScript\" subject=\"" + subject + "\">";
  var len = arrServiceNameAndSrcFile.length;
  if(!options){
    options = {};
  }
  //instantiate headers if not present
  if(!options.headers){
    options.headers = {};
  }
  for (var i = 0; i < len; i++) {
    key = arrServiceNameAndSrcFile[i].serviceName;
    value = arrServiceNameAndSrcFile[i].sourceFileName;
    sb += "<dc:path name=\"" + key + "\" src=\"" + value + "\"/>";
  }
  sb += "</dc:service></D:prop></D:set></D:propertyupdate>";

  //set options.body
  options.body = sb;

  //set default headers, if not present
  if(!options.headers["Content-Type"]){
    options.headers["Content-Type"] = "application/xml";
  }
  if(!options.headers.Accept){
    options.headers.Accept = "application/xml";
  }
  return this._request(xhr, "PROPPATCH", requestUrl, options);

  //this.request(xhr, "PROPPATCH", requestUrl, sb, builder, {}, options);
 /* if (options.success === undefined &&
      options.error === undefined &&
      options.complete === undefined) {
    //none of the callback is present
    return this.httpClient;
  }*/
};

///**
//* PROPPATCHメソッド.
//* @param requestUrl リクエスト対象URL
//* @param key プロパティ名
//* @param value プロパティの値
//* @param callback (deprecated) for backward compatibility.
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * PROPPATCH method.
 * @param {String} requestUrl
 * @param {String} key
 * @param {String} value
 * @param {Object} options (deprecated) for backward compatibility.
 * @return {dcc.http.DcHttpClient} DcHttpClient
 * @throws {dcc.ClientException} DAO exception
 */
dcc.http.RestAdapter.prototype.proppatch = function(requestUrl, key, value, options) {
  var sb = "";
  sb += "<D:propertyupdate xmlns:D=\"DAV:\" xmlns:dc=\"urn:x-dc1:xmlns\"><D:set><D:prop>";
  sb += "<" + key + ">";
  sb += value;
  sb += "</" + key + ">";
  sb += "</D:prop></D:set></D:propertyupdate>";

  var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.contentType("application/xml");
  builder.accept("application/xml");
  builder.token(this.accessor.accessToken);
  builder.defaultHeaders(this.accessor.getDefaultHeaders());

  var xhr = this.httpClient;
  this.request(xhr, "PROPPATCH", requestUrl, sb, builder, {}, options);

  if (options === undefined) {
    return this.httpClient;
  }
};

/**
 * The purpose of this method is to perform multiple set and remove property operation
 * through PROPPATCH, set or remove property list is an array of key value JSON.
 * @param {String} requestUrl
 * @param {Object} options contains - callback, headers, set(proplist) and remove(proplist)
 * @return {dcc.DcHttpClient} response
 * @throws {dcc.ClientException} Exception
 */
dcc.http.RestAdapter.prototype.multiProppatch = function(requestUrl, options) {
  var key = null;
  var value = null;

  //if options not present or it has no set or remove prop list, do nothing
  if(!options || (!options.set && !options.remove)){
    return;
  }

  var propertyToSet = options.set;
  var propertiesToRemove = options.remove;
  var sb = "<D:propertyupdate xmlns:D=\"DAV:\" xmlns:dc=\"urn:x-dc1:xmlns\" xmlns:Z=\"http://www.w3.com/standards/z39.50/\"><D:set><D:prop>";
  //instantiate headers if not present
  if(!options.headers){
    options.headers = {};
  }

  for ( var i = 0; i < propertyToSet.length; i++) {
    key = propertyToSet[i].propName;
    value = propertyToSet[i].propValue;
    sb += "<" + key + ">";
    sb += value;
    sb += "</" + key + ">";
  }
  sb += "</D:prop></D:set><D:remove><D:prop>";
  for ( var j = 0; j < propertiesToRemove.length; j++) {
    key = propertiesToRemove[j].propName;
    value = propertiesToRemove[j].propValue;
    sb += "<" + key + ">";
    sb += value;
    sb += "</" + key + ">";
  }
  sb += "</D:prop></D:remove></D:propertyupdate>";
  /* var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.contentType("application/xml");
  builder.accept("application/xml");
  builder.token(this.accessor.accessToken);
  builder.defaultHeaders(this.accessor.getDefaultHeaders());*/

  var xhr = this.httpClient;
  //set options.body
  options.body = sb;
  this._request(xhr, "PROPPATCH", requestUrl, options);
  //this.request(xhr, "PROPPATCH", requestUrl, sb, builder, {}, callback);

  if (options.success === undefined &&
      options.error === undefined &&
      options.complete === undefined) {
    //none of the callback is present
    return this.httpClient;
  }
};

///**
//* PROPFINDメソッド.
//* @param requestUrl リクエスト対象URL
//* @param callback (deprecated) for backward compatibility.
//* @return DcHttpClient
//* @throws ClientException DAO例外
//*/
/**
 * PROPFind method.
 * @param {String} requestUrl
 * @param {Object} options optional parameters.
 * @return {dcc.http.DcHttpClient} DcHttpClient
 * @throws {dcc.ClientException} DAO exception
 */
dcc.http.RestAdapter.prototype.propfind = function(requestUrl, options) {
  /* var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.contentType("application/xml");
  builder.accept("application/xml");
  builder.token(this.accessor.accessToken);
  builder.depth("1");
  builder.defaultHeaders(this.accessor.getDefaultHeaders());*/

  if(!options){
    options ={};
  }
  if(!options.headers){
    options.headers = {};
    options.headers.depth="1";
  }
  var xhr = this.httpClient;
  //this.request(xhr, "PROPFIND", requestUrl, "", builder, {}, callback);
  return this._request(xhr, "PROPFIND", requestUrl, options);
  /*if (options.success === undefined && options.error === undefined && options.complete === undefined) {
    return this.httpClient;
  }*/
};

///**
//* Responseボディを受ける場合のHTTPリクエストを行う.
//* @param xhr
//* @param method Http request method
//* @param requestUrl リクエスト対象URL
//* @param requestBody data request body
//* @param builder DcRequestHeaderBuilder
//* @param headers headers parameters for request
//* @param callback (deprecated) for backward compatibility.
//* @throws ClientException DAO例外
//*/
/**
 * This method is used to make HTTP requests may be subject to Response body.
 * @param {dcc.http.DcHttpClient} xhr
 * @param {String} Http request method
 * @param {String} requestUrl
 * @param {Object} data request body
 * @param {dcc.DcRequestHeadreBuilder} builder DcRequestHeaderBuilder
 * @param {Object} headers parameters for request
 * @param {Object} options optional callback, header and async parameters
 * @throws {dcc.ClientException} DAO exception
 */
dcc.http.RestAdapter.prototype.request = function(xhr, method, requestUrl, requestBody, builder, headers, options) {
  var self = this;
  builder.build(xhr, headers);

  // check added for empty callback

  if (options !== undefined && options !== "") {
    xhr._execute(method, requestUrl, requestBody, function() {
      self.accessor.setResHeaders(xhr.getAllResponseHeaders());
      options(self);
    });
  } else {
    xhr._execute(method, requestUrl, requestBody);
    this.accessor.setResHeaders(xhr.getAllResponseHeaders());
    if (xhr.getStatusCode() >= 300 && xhr.getStatusCode() <= 400) {
      var response = JSON.parse(xhr.bodyAsString());
      if(xhr.getStatusCode() === 401){
        //authentication error case,when response does not contain response.message instead contains response.error
        throw new dcc.ClientException(response.error, response.error_description);
      }else{
        throw new dcc.ClientException(response.message.value, response.code);
      }
    }
  }
};

/**
 * This is the new version of internal request method to send HTTP request.
 * @private
 * @param {dcc.http.DcHttpClient} http client.
 * @param {String} HTTP request method.
 * @param {String} HTTP request url.
 * @param {Object} options
 * @param {Object} options.headers Request headers
 * @param {Object} options.body Request body
 * @param {Object} filter instance of an entity
 * @param {Function} options.success Callback function for successful result.
 * @param {Function} options.error Callback function for error response.
 * @param {Function} options.complete Callback function for any response, either successful or error.
 * @return {dcc.Promise} response or promise
 * @throws {dcc.ClientException} ClientException
 */
dcc.http.RestAdapter.prototype._request = function(client, method, requestUrl, options, filter) {
  var self = this;
  var builder = new dcc.http.DcRequestHeaderBuilder();
  builder.token(this.accessor.accessToken);
  builder.defaultHeaders(this.accessor.getDefaultHeaders());
  var promise = null;
  /** put all req headers to client. */
  builder.build(client, options.headers);
  /** check if valid option is present with any/all callback present. */

  if (this._isAsynchronous(client, options)) {
    //Asynchronous mode of execution, set async to true
    client.setAsync(true);
    //if(options!== undefined && options.thenable){
      //return the thenable
    promise = client._executeReturnThenable(method, requestUrl, options, self.accessor, filter);
    //}
    //else{
    //  promise = client._execute2(method, requestUrl, options, self.accessor);
    //}
  } else {
    //synchronous mode of execution, set async to false
    client.setAsync(false);
    promise = client._executeReturnThenable(method, requestUrl, options, self.accessor, filter);
    if(options === undefined ||
        (options.success === undefined &&
            options.error === undefined &&
            options.complete === undefined) ){
      //start:Exception Handling, if callback are not present then throw the exception
      if(client.getStatusCode() >= 300 && client.getStatusCode() <400){
        //no exception to the thrown
      }
      else if(client.getStatusCode() >= 400){
        var response = JSON.parse(client.bodyAsString());
        //For unauthorized access token.
        if (client.getStatusCode() === 401){
          // throw the exception with exception code
          throw new dcc.ClientException(response.error, response.code);
        }
        // throw the exception with exception code
        throw new dcc.ClientException(response.message.value, response.code);
      }
    }
    //end:Exception Handling
  }
  return promise;
};


///**
//* HTTPステータスコードを返却する.
//* return status code
//*/
/**
 * This method returns HHTP status code.
 * @returns {String} status code
 */
dcc.http.RestAdapter.prototype.getStatusCode = function() {
  return this.httpClient.getStatusCode();
};

///**
//* 指定したレスポンスヘッダの値を返却する.
//* return responseHeader against the key
//*/
/**
 * This method returns response headers.
 * @param {String} key
 * @returns {String} responseHeader against the key
 */
dcc.http.RestAdapter.prototype.getResponseHeader = function(key) {
  return this.httpClient.getResponseHeader(key);
};

///**
//* レスポンスボディを文字列で返却する.
//* @return bodyAsString
//*/
/**
 * This method returns the response body in string format.
 * @return {String} bodyAsString
 */
dcc.http.RestAdapter.prototype.bodyAsString = function() {
  return this.httpClient.bodyAsString();
};

///**
//* レスポンスボディをJSONオブジェクトで返却する.
//* @return bodyAsJson
//*/
/**
 * This method returns the response body in JSON format.
 * @return {Object} bodyAsJson
 */
dcc.http.RestAdapter.prototype.bodyAsJson = function() {
  return this.httpClient.bodyAsJson();
};

///**
//* レスポンスボディをXMLで取得.
//* @return XML DOMオブジェクト
//*/
/**
 * This method returns the response body in XML format.
 * @returns {String} XML response
 */
dcc.http.RestAdapter.prototype.bodyAsXml = function() {
  return this.httpClient.bodyAsXml();
};

/**
 * This method determine whether the requested execution mode is synchronous or asynchronous.
 * Preference to mode specification is evaluated in order of precedence as options, accessor, library default.
 * @param {dcc.http.DcHttpClient} client
 * @param {JSON} options
 * @returns {Boolean} response true if call is asynchronous else false
 */
dcc.http.RestAdapter.prototype._isAsynchronous = function(client, options) {
  //if options.async is present then use async as specified mode
  if(options !== undefined && options !== null && options.async !== undefined){
    //true or false
    return options.async;
  }else if(this.accessor.getContext().getAsync() !== undefined){
    //if no specification is found at option level, use accessor level settings
    return this.accessor.getContext().getAsync();
  }else{
    //if mode is not specified in any of case - options and accessor level, then use library level settings
    return client.defaultAsync;
  }
};

