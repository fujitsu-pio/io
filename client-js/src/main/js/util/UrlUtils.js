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
/*global dcc:false
 */

///**
//* URL文字列を操作するクラス.
//* @constructor
//*/
/**
 * Class to manipulate the URL string.
 * @constructor
 */
dcc.UrlUtils = function() {
};

///**
//* URLにパスを追加する.
//* @static
//* @param baseUrl URL文字列
//* @param path 追加するパス
//* @return 生成したURL文字列
//*/
/**
 * This method appends the path to BaseURL.
 * @static
 * @param {String} baseUrl BaseURL
 * @param {String} path Path
 * @return {String} Complete URL
 */
dcc.UrlUtils.append = function(baseUrl, path) {
  var url = baseUrl;
  if (!baseUrl.endsWith("/")) {
    url += "/";
  }
  url += path;
  return url;
};

///**
//* 対象urlが有効かチェックを行う.
//* @static
//* @param url チェック対象url文字列
//* @return true： 有効/false：無効
//*/
/**
 * This method checks whether the target URL is valid or not.
 * @static
 * @param {String} url Target URL
 * @return {Boolean} true： Enable/false: Disable
 */
dcc.UrlUtils.isUrl = function(url) {
  if (url.match(/^(http|https):\/\//i)) {
    return true;
  }
  return false;
};

/**
 * This method adds trailing slash character if not present.
 * @static
 * @param {String} url URL
 * @return {String} url URL with trailing slash character.
 */
dcc.UrlUtils.addTrailingSlash = function(url) {
  if (url.endsWith("/")) {
    return url;
  }
  return url + "/";
};

/**
 * This method extracts the first path from a URL.
 * @static
 * @param {String} url URL
 * @return {String} path string
 */
dcc.UrlUtils.extractFirstPath = function(url) {
  if (dcc.UrlUtils.isUrl(url)) {
    return url.replace(/https?\:\/\/[^\/]+\/([^\/]+).*/,"$1");
  }
  return url;
};

/**
 * This method returns the character string of x-www-form-urlencoded format of json request.
 * @static
 * @param {Object} json optional parameters to be extracted from response
 * @return {String} character string in key = value format.
 */
dcc.UrlUtils.jsonToW3Form = function(json) {
  var requestBody = "";
  for(var key in json){
    if(json[key]){
      requestBody += key + "=" + json[key] + "&";
    }
  }
  console.log(typeof requestBody);
  if(dcc.UrlUtils.endsWith(requestBody, "&")){
    requestBody = requestBody.substr(0, requestBody.lastIndexOf("&"));
  }
  return requestBody;
};

/**
 * This method checks if a string ends with a specified symbol.
 * If it ends with a specified symbol it returns true otherwise false.
 * @static
 * @return {Boolean} true or false.
 */
dcc.UrlUtils.endsWith = function(string,symbol){
  var lastIndex = string.lastIndexOf(symbol);
  return (lastIndex !== -1) && (lastIndex + symbol.length === string.length);
};