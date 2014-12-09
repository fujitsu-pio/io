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
 * It creates a new object dcc.http.DcResponse.
 * @class This class is used to handle DAV Response.
 * @constructor
 * @param {Object} resObj response object
 */
dcc.http.DcResponse = function(resObj) {
  this.initializeProperties(resObj);
};

///**
//* プロパティを初期化する.
//* @param resObj response object
//*/
/**
 * This method initializes the properties of this class.
 * @param {Object} resObj response object
 */
dcc.http.DcResponse.prototype.initializeProperties = function(resObj) {
  this.response = resObj;
  this.debugHttpResponse(resObj);
};

/**
 * This method returns the HTTP Status Code.
 * @return {String} HTTP Status Code
 */
dcc.http.DcResponse.prototype.getStatusCode = function() {
  return this.response.status;
};

/**
 * The purpose of this method is to generate header
 * @param {String} key
 * @returns {String} header
 */
dcc.http.DcResponse.prototype.getHeader = function(key) {
  var header = this.response.getResponseHeader(key);
  if (header === null) {
    header = "";
  }
  return header;
};

//jDcResponse.prototype.bodyAsStream = function() {
//InputStream is = null;
//try {
//is = this.getResponseBodyInputStream(response);
//} catch (IOException e) {
//throw new RuntimeException(e);
//}
//return is;
//};

/**
 * The purpose of this method is to return the body in string form.
 * @returns {String} responseText
 */
dcc.http.DcResponse.prototype.bodyAsString = function() {
  return this.response.responseText;
//if (typeof enc == "undefined") {
//enc = "utf-8";
//}
//InputStream is = null;
//InputStreamReader isr = null;
//BufferedReader reader = null;
//try {
//is = this.getResponseBodyInputStream(response);
//if (is == null) {
//return "";
//}
//isr = new InputStreamReader(is, enc);
//reader = new BufferedReader(isr);
//StringBuffer sb = new StringBuffer();
//int chr;
//while ((chr = reader.read()) != -1) {
//sb.append((char) chr);
//}
//return sb.toString();
//} catch (IOException e) {
//throw ClientException.create("io exception", 0);
//} finally {
//try {
//if (is != null) {
//is.close();
//}
//if (isr != null) {
//isr.close();
//}
//if (reader != null) {
//reader.close();
//}
//} catch (Exception e) {
//throw ClientException.create("io exception", 0);
//} finally {
//try {
//if (isr != null) {
//isr.close();
//}
//if (reader != null) {
//reader.close();
//}
//} catch (Exception e2) {
//throw ClientException.create("io exception", 0);
//} finally {
//try {
//if (reader != null) {
//reader.close();
//}
//} catch (Exception e3) {
//throw ClientException.create("io exception", 0);
//}
//}
//}
//}
};

/**
 * This method retrieves and parses the response body with a JSON format.
 * @return {Object} parsed response JSON object.
 * @throws {dcc.ClientException} Client Exception
 */
dcc.http.DcResponse.prototype.bodyAsJson = function() {
  try {
    //this.response.bodyAsString
    return JSON.parse(this.response.responseText);

  } catch (e) {
    throw new dcc.ClientException("parse exception: " + e.message, 0);
  }
};

/**
 * This method retrieves and parses the response body with an XML format.
 * @return {String} XML parsed response body as XML DOM.
 */
dcc.http.DcResponse.prototype.bodyAsXml = function() {
  return this.response.responseXML;
};



//jDcResponse.prototype.getResponseBodyInputStream = function(res) {
//Header[] contentEncodingHeaders = res.getHeaders("Content-Encoding");
//if (contentEncodingHeaders.length > 0 && "gzip".equalsIgnoreCase(contentEncodingHeaders[0].getValue())) {
//return new GZIPInputStream(res.getEntity().getContent());
//} else {
//HttpEntity he = res.getEntity();
//if (he != null) {
//return he.getContent();
//} else {
//return null;
//}
//}
//};

/**
 * The purpose of this method is to perform logging operations while debugging
 * @param {dcc.http.DcHttpClient} res httpResponse
 */
dcc.http.DcResponse.prototype.debugHttpResponse = function(res) {
  if (res !== null) {
    console.log("(Response) ResponseCode: " + res.statusText + "(" + res.status + ")");
    var headers = res.getAllResponseHeaders();
    var array = headers.split("\n");
    for (var i = 0; i < array.length; i++) {
      var keyValue = array[i].split(": ");
      console.log("ResponseHeader[" + keyValue[0] + "] : " + keyValue[1]);
    }
  }
};

