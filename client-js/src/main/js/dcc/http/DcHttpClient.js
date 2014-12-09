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
/*global dcc:false, XMLHttpRequest:false*/

/**
 * It creates a new object dcc.http.DcHttpClient.
 * @class This class is the abstraction Layer of HTTP Client.
 * @param {Boolean} async true value represents asynchronous mode
 */
dcc.http.DcHttpClient = function(async) {
  this.requestHeaders = [];
  this.httpClient = new XMLHttpRequest();
  /** Global level synchronous/asynchronous mode for library. */
  this.defaultAsync = false;
  this.overrideMimeType = "";
  /** async refers to current execution mode, default to synchronous mode. */
  this.async = false;
  if(async){
    /** set the mode to asynchronous. */
    this.async = async;
  }
};

/**
 * This method sets the HTTP Request Header.
 * @param {String} header key
 * @param {String} header value
 */
dcc.http.DcHttpClient.prototype.setRequestHeader = function(key, value) {
  var header = {};
  header[key] = value;
  this.requestHeaders.push(header);
};

/**
 * This method sets overrideMimeType.
 * @param {String} MimeType value
 */
dcc.http.DcHttpClient.prototype.setOverrideMimeType = function(value) {
  this.overrideMimeType = value;
};

/**
 * This method is the getter for HTTP Status Code.
 * @returns {String} HTTP Status Code
 */
dcc.http.DcHttpClient.prototype.getStatusCode = function() {
  return this.httpClient.status;
};

/**
 * Thi smethod gets the specified response header value.
 * @param {String} header name
 * @returns {String} header value
 */
dcc.http.DcHttpClient.prototype.getResponseHeader = function(key) {
  var header = this.httpClient.getResponseHeader(key);
  if (header === null) {
    header = "";
  }
  return header;
};

/**
 * This method gets all the response headers.
 * @returns {Object} response header
 */
dcc.http.DcHttpClient.prototype.getAllResponseHeaders = function() {
  var headersStr = this.httpClient.getAllResponseHeaders();
  var headers = {};
  var headersArray = headersStr.split("\n");
  for (var i = 0; i < headersArray.length; i++) {
    var arr = headersArray[i].split(":");
    var headerName = arr.shift();
    if (headerName === "") {
      continue;
    }
    var headerValue = arr.join(":");
    headerValue = headerValue.replace(/(^\s+)|(\s+$)/g, "");
    headers[headerName] = headerValue;
  }
  return headers;
};

/**
 * This method retrieves the response body in the form of string.
 * @returns {String} responseText
 */
dcc.http.DcHttpClient.prototype.bodyAsString = function() {
  return this.httpClient.responseText;
};

/**
 * This method retrieves the response body in the form of binary.
 * @returns {Object} response object
 */
dcc.http.DcHttpClient.prototype.bodyAsBinary = function() {
  return this.httpClient.response;
};

/**
 * This method retrieves the response body in the form of JSON object.
 * @returns {Object} responseText JSON format
 */
dcc.http.DcHttpClient.prototype.bodyAsJson = function() {
  try {
    if (this.httpClient.responseText === "") {
      return {};
    }
    return JSON.parse(this.httpClient.responseText);
  } catch (e) {
    throw new Error("json parse exception: " + e.message);
  }
};

/**
 * This method retrieves the response body in the form of XML.
 * @return {String} XML DOM Object
 */
dcc.http.DcHttpClient.prototype.bodyAsXml = function() {
  return this.httpClient.responseXML;
};

/**
 * Execute method is used to send an HTTP Request.
 * @private
 * @param {String} method
 * @param {String} requestUrl
 * @param {Object} requestBody
 * @param {Object} options optional callback, header and async parameters
 * @returns {dcc.Promise} Promise object
 */
dcc.http.DcHttpClient.prototype._execute = function(method, requestUrl, requestBody, options) {
  var self = this;
  var xhr = this.httpClient;
  var promise = new dcc.Promise();

  if (options !== undefined) {
    xhr.open(method, requestUrl, true);
    xhr.requestUrl = method + ":" + requestUrl;
  } else {
    xhr.open(method, requestUrl, false);
  }

  if (this.overrideMimeType !== "") {
    xhr.overrideMimeType(this.overrideMimeType);
  }
  for (var index in this.requestHeaders) {
    var header = this.requestHeaders[index];
    for (var key in header) {
      xhr.setRequestHeader(key, header[key]);
    }
  }
  xhr.onload = function () {
    //if(xhr.responseText !== ""){
    //var results = xhr.responseText;//JSON.parse(xhr.responseText);
    if (200 <= xhr.status && xhr.status < 300) {
      promise.resolve(xhr);
    }else{
      promise.reject(xhr);
    }
    if (options !== undefined) {
      options(self);
    }
    //}
  };
  xhr.onerror = function (e) {
    promise.reject(e.target.status);
  };
  xhr.send(requestBody);
  return promise;
};

/**
 * Execute method is used to send an HTTP Request, 
 * decides request mode based on this.async.
 * @private
 * @param {String} method GET, POST, PUT,DELETE
 * @param {String} requestUrl
 * @param {Object} options contains body and callback success, error and complete
 * @param {accessor} to set response header
 * @returns {dcc.Promise} Promise object
 */
dcc.http.DcHttpClient.prototype._execute2 = function(method, requestUrl, options, accessor) {
  var self = this;
  var xhr = this.httpClient;
  var promise = new dcc.Promise();
  var requestBody = "";

  xhr.open(method, requestUrl, this.async);
  if(options.body !== undefined && options.body !== null){
    requestBody = options.body;
  }
  if(this.async){
    xhr.requestUrl = method + ":" + requestUrl;
  }
  if (this.overrideMimeType !== "") {
    xhr.overrideMimeType(this.overrideMimeType);
  }
  for (var index in this.requestHeaders) {
    var header = this.requestHeaders[index];
    for (var key in header) {
      xhr.setRequestHeader(key, header[key]);
    }
  }
  xhr.onload = function () {
    /** handle the promise based on status code. */
    if (200 <= xhr.status && xhr.status <= 400) {
      promise.resolve(xhr);
    }else{
      promise.reject(xhr);
    }
    //set the response headers
    if(accessor){
      accessor.setResHeaders(xhr.getAllResponseHeaders());
    }
    /** handle the callback based on status code. */
    if(options!== null && options !== undefined){
      /** if status code is between 200 to 300, execute success callback. */
      if(200 <= xhr.status && xhr.status <= 300){
        if(options.success){
          options.success(self);
        }
      }
      else if(300 <= xhr.status && xhr.status < 400){
        //no response body may exist in this scenario, has to be handled by calling code
        if(options.success){
          options.success(self);
        }
      }
      else{
        /** execute error callback. */
        if(options.error){
          options.error(self);
        }
      }
      /** execute complete callback. */
      if(options.complete){
        options.complete(self);
      }
    }
  };
  xhr.onerror = function (e) {
    promise.reject(e.target.status);
  };
  xhr.send(requestBody);
  return promise;
};

/**
 * This method sets Asynchronous mode.
 * @param {Boolean} async true to set mode as asynchronous
 */
dcc.http.DcHttpClient.prototype.setAsync = function(async){
  this.async = async;
};

/**
 * This method is intended to send HTTP request along with provision of promise.
 * @param method {String} method GET, POST, PUT,DELETE
 * @param requestUrl
 * @param options optional parameters
 * @param accessor
 * @param resolve method to be execute when request is resolved
 * @param reject method to be execute when request is reject
 * @returns {XMLHttpRequest}
 */
dcc.http.DcHttpClient.prototype.httpGet = function(method, requestUrl, options, accessor, resolve, reject) {
  var xhr = this.httpClient;
  var requestBody = "";

  xhr.open(method, requestUrl, this.async);
  if(options.body !== undefined && options.body !== null){
    requestBody = options.body;
  }

  if(this.async){
    xhr.requestUrl = method + ":" + requestUrl;
  }
  if (this.overrideMimeType !== "") {
    xhr.overrideMimeType(this.overrideMimeType);
  }
  for (var index in this.requestHeaders) {
    var header = this.requestHeaders[index];
    for (var key in header) {
      xhr.setRequestHeader(key, header[key]);
    }
  }
  xhr.onload = resolve;
  xhr.onerror = reject;
  xhr.send(requestBody);
  return xhr;
};

/**
 * This method is intended to send HTTP request along with provision of promise.
 * @param method {String} method GET, POST, PUT,DELETE
 * @param requestUrl
 * @param options optional parameters
 * @param accessor
 * @returns JSON then able JSON
 */
dcc.http.DcHttpClient.prototype.httGetAsThenable  = function(method, requestUrl, options, accessor){
  var self = this;
  return {
    "then" : function(resolve,reject){
      self.httpGet(method, requestUrl, options, accessor, resolve, reject);
    }
  };
};

dcc.http.DcHttpClient.prototype._executeReturnThenable  = function(method, requestUrl, options, accessor, filter) {
  var self = this;
  var xhr = this.httpClient;
  var resAsThenable = new dcc.http.DcResponseAsThenable();
  var requestBody = "";
  xhr.open(method, requestUrl, this.async);
  if(options.body !== undefined && options.body !== null){
    requestBody = options.body;
  }
  if(this.async){
    xhr.requestUrl = method + ":" + requestUrl;
  }
  if (this.overrideMimeType !== "") {
    xhr.overrideMimeType(this.overrideMimeType);
  }
  for (var index in this.requestHeaders) {
    var header = this.requestHeaders[index];
    for (var key in header) {
      xhr.setRequestHeader(key, header[key]);
    }
  }
  xhr.onload = function () {
      var resObj = null;
      if (200 <= xhr.status && xhr.status < 400) {
        //var json = self.bodyAsJson(xhr);
        resObj = xhr;
        if(filter !== null && filter !== undefined){
          var json = self.bodyAsJson(xhr);
          resObj = filter(accessor,json);
        }
        
        resAsThenable.resolve(resObj);
        if(options!== null && options !== undefined && options.success){
          options.success(resObj);
        }
        /** execute complete callback. */
        /*if(options!== null && options !== undefined && options.complete){
          options.complete(resObj);
        }*/
      }else{
        resAsThenable.reject(self);
        /** execute error callback. */
        if(options!== null && options !== undefined && options.error){
          options.error(self);
        }
      }
      /** execute complete callback. */
      if(options!== null && options !== undefined && options.complete){
        options.complete(resObj);
      }
    //set the response headers
    if(accessor){
      accessor.setResHeaders(xhr.getAllResponseHeaders());
    }
  };
  xhr.onerror = function () {
    resAsThenable.reject(xhr);
    //error(new Error(xhr.statusText));
  };
  xhr.send(requestBody);
  return resAsThenable;
};
