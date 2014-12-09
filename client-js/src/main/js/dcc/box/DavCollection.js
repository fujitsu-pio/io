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
//* @class DAVコレクションへアクセスするクラス.
//* @constructor
//* @augments dcc.DcCollection
//*/
/**
 * It creates a new object dcc.box.DavCollection.
 * @class This class is used to access the DAV collection for Odata operations.
 * @constructor
 * @augments dcc.DcCollection
 * @param {dcc.Accessor} Accessor
 * @param {String} path
 */
dcc.box.DavCollection = function(as, path) {
  this.initializeProperties(this, as, path);
};
dcc.DcClass.inherit(dcc.box.DavCollection, dcc.DcCollection);

///**
//* プロパティを初期化する.
//* @param {dcc.box.DavCollection} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {?} pathValue
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.box.DavCollection} self
 * @param {dcc.Accessor} as Accessor
 * @param {String} pathValue
 */
dcc.box.DavCollection.prototype.initializeProperties = function(self, as, pathValue) {
  this.uber = dcc.DcCollection.prototype;
  this.uber.initializeProperties(self, as, pathValue);

  if (as !== undefined) {
//  /** boxレベルACLへアクセスするためのクラス. */
    /** class to access the box level ACL. */
    self.acl = new dcc.cellctl.AclManager(as, this);
  }
};

///**
//* コレクションの生成.
//* @param {?} name 生成するCollection名
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to create a collection.
 * @param {String} name Collection name
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.mkCol = function(name, options) {
  var async = this._isAsynchronous(options);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.mkcol(dcc.UrlUtils.append(this.getPath(), name), options);
  /*if (response.getStatusCode() >= 400) {
    var responseJSON = response.bodyAsJson();
    throw new dcc.ClientException(responseJSON.message.value,
        responseJSON.code);
  }*/
  if (async) {
      return response;
  } else {
      return response.resolvedValue;
  }
};

///**
//* ODataコレクションの生成.
//* @param name 生成するODataCollection名
//* @throws ClientException DAO例外
//*/
/**
 * This method is used to create a odata collection.
 * @param {String} name ODataCollection name
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.mkOData = function(name, options) {
  var async = this._isAsynchronous(options);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.mkOData(dcc.UrlUtils
      .append(this.getPath(), name));
  /*if (response.getStatusCode() >= 400) {
    var responseJSON = response.bodyAsJson();
    throw new dcc.ClientException(responseJSON.message.value,
        responseJSON.code);
  }*/
  if (async) {
      return response;
  } else {
      return response.resolvedValue;
  }
};

///**
//* Serviceコレクションの生成.
//* @param name 生成するServiceCollection名
//* @throws ClientException DAO例外
//*/
/**
 * This method is used to create a service collection.
 * @param {String} name ServiceCollection name
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.mkService = function(name, options) {
  var async = this._isAsynchronous(options);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.mkService(dcc.UrlUtils.append(this.getPath(), name));
  /*if (response.getStatusCode() >= 400) {
    var responseJSON = response.bodyAsJson();
    throw new dcc.ClientException(responseJSON.message.value,
        responseJSON.code);
  }*/
  if (async) {
      return response;
  } else{
      return response.resolvedValue;
  }
  
};

///**
//* Calendarコレクションの生成.
//* @param name 生成するCalendarCollectoin名
//* @throws ClientException DAO例外
//*/
/**
 * This method is used to create a Calendar.
 * @param {String[]} name CalendarCollection name
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.mkCalendar = function(name) {
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  restAdapter.mkCalendar(dcc.UrlUtils.append(this.getPath(), name), "");
};

///**
//* コレクション内のリソースの一覧を取得する.
//* @return リソースの一覧
//* @throws ClientException DAO例外
//*/
/**
 * This method gets the list of resources in the collection.
 * @return {String[]} List of resources
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.getFileList = function() {
  return this.getResourceList();
};

///**
//* コレクション内のサブコレクションの一覧を取得する.
//* @return サブコレクションの一覧
//* @throws ClientException DAO例外
//*/
/**
 * This method gets a list of sub-collection in the collection.
 * @return {String} List of resources
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.getColList = function() {
  return this.getResourceList();
};

///**
//* コレクション内のリソースまたはサブコレクションの一覧を取得する.
//* @return {?} リソースまたはサブコレクションの一覧
//* @throws {ClientException} DAO例外
//*/
/**
 * This method calls propfind API to fetch the list of resources.
 * @return {String[]} List of sub-collection or resource
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.getResourceList = function() {
  var folderList = [];
  var type = "";
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.propfind(this.url);
  var doc = response.resolvedValue.responseXML;//response.bodyAsXml();
  var nl = doc.getElementsByTagName("response");
  var name = "";
  for ( var i = 1; i < nl.length; i++) {
    var elm = nl[i];
    var href = elm.getElementsByTagName("href")[0];
    var lastModifiedDate = elm.getElementsByTagName("getlastmodified")[0].textContent;
    var resourceType = elm.getElementsByTagName("resourcetype")[0];
    if (resourceType.textContent !== "") {
      var collectionType = elm.getElementsByTagName("resourcetype")[0].firstElementChild.tagName;
      var temp = elm.getElementsByTagName("resourcetype")[0].firstElementChild;
      if (collectionType === "collection") {
        if (elm.getElementsByTagName("resourcetype")[0].firstElementChild.nextElementSibling !== null) {
          type = temp.nextElementSibling.nodeName;
        } else {
          type = "folder";
        }
      }
    } else {
      type = "file";
    }
    var epochDateTime = new Date(lastModifiedDate).getTime();
    epochDateTime = "/Date(" + epochDateTime + ")/";
    name = {
        "Name" : href.firstChild.nodeValue,
        "Date" : epochDateTime,
        "Type" : type
    };
    if (name === this.url) {
      continue;
    }
    var col = elm.getElementsByTagName("collection");
    if (col.length > 0 || type === "file") {
      folderList.push(name);
    }
  }
  return folderList;
};

///**
//* コレクションにプロパティをセットする.
//* @param key プロパティ名
//* @param value プロパティの値
//*/
//dcc.box.DavCollection.prototype.setProp = function(key, value) {
//};
///**
//* コレクションからプロパティを取得する.
//* @param key プロパティ名
//* @return 取得したプロパティ値
//*/
//dcc.box.DavCollection.prototype.getProp = function(key) {
//return "";
//};
///**
//* サブコレクションを指定.
//* @param name コレクション名
//* @return {dcc.box.DavCollection} 指定したコレクション名のDavCollectionオブジェクト
//*/
/**
 * This method specifies and retrieves the collection.
 * @param {String} name Collection name
 * @return {dcc.box.DavCollection} DavCollection object
 */
dcc.box.DavCollection.prototype.col = function(name) {
  return new dcc.box.DavCollection(this.accessor, dcc.UrlUtils.append(this
      .getPath(), name));
};

///**
//* ODataコレクションを指定.
//* @param name ODataコレクション名
//* @return {dcc.box.ODataCollection} 取得したODataCollectionオブジェクト
//*/
/**
 * This method specifies and retrieves the odata collection.
 * @param {String} name Odata Collection name
 * @return {dcc.box.ODataCollection} ODataCollection object
 */
dcc.box.DavCollection.prototype.odata = function(name) {
  return new dcc.box.ODataCollection(this.accessor, dcc.UrlUtils.append(this
      .getPath(), name));
};

///**
//* Serviceコレクションを指定.
//* @param name Serviceコレクション名
//* @return {dcc.box.ServiceCollection} 取得したSerivceコレクションオブジェクト
//*/
/**
 * This method specifies and retrieves the service collection.
 * @param {String} name Service Collection name
 * @return {dcc.box.ServiceCollection} SerivceCollection object
 */
dcc.box.DavCollection.prototype.service = function(name) {
  return new dcc.box.ServiceCollection(this.accessor, dcc.UrlUtils.append(this
      .getPath(), name));
};

///**
//* DAVに対するGETメソッドをリクエストする.
//* @param {String} pathValue 取得するパス
//* @param {String} charset 文字コード
//* @param {string} etag Used for if-none-match condition
//* @return {String} GETした文字列
//* @throws {ClientException} DAO例外
//*/
/**
 * This method returns the DAV collection details in string format.
 * @param {String} pathValue Path
 * @param {String} charset Character code
 * @param {Object} options optional callback, header and async parameters
 * @param {String} etag Used for if-none-match condition
 * @return {String} GET String
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.getString = function(pathValue, charset, options,
    etag) {
  if (charset === undefined) {
    charset = "utf-8";
  }
  var url = dcc.UrlUtils.append(this.getPath(), pathValue);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  //if (options !== undefined) {
    /* restAdapter.get(url, "text/plain", etag, function(resp) {
      if (resp.getStatusCode() >= 300) {
        if (callback.error !== undefined) {
          callback.error(resp);
        }
      } else {
        if (callback.success !== undefined) {
          var body = resp.bodyAsString(charset);
          callback.success(body);
        }
      }
      if (callback.complete !== undefined) {
        callback.complete(resp);
      }
    });*/
   // restAdapter.get(url, "text/plain", etag,options);
  //} else {
    restAdapter.get(url, "text/plain", etag, options);
    var body = restAdapter.bodyAsString(charset);
    return body;
  //}
};

///**
//* バイナリデータのGETメソッドをリクエストする.
//* @param {String} pathValue 取得するパス
//* @param {String} callback コールバックメソッド
//* @param {string} etag Used for if-none-match condition
//* @return {String} GETしたバイナリデータ
//* @throws {ClientException} DAO例外
//*/
/**
 * This method returns the DAV collection details in binary format.
 * @param {String} pathValue Path
 * @param {Object} options optional callback, header and async parameters
 * @param {String} etag Used for if-none-match condition
 * @return {String} GET Binary data
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.getBinary = function(pathValue, options, etag) {
  var url = dcc.UrlUtils.append(this.getPath(), pathValue);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  if (options !== undefined) {
    restAdapter.getBinary(url, etag, options);
    /*restAdapter.getBinary(url, etag, function(resp) {
      if (resp.getStatusCode() >= 300) {
        if (options.error !== undefined) {
          options.error(resp);
        }
      } else {
        if (options.success !== undefined) {
          var body = resp.bodyAsBinary();
          options.success(body);
        }
      }
      if (options.complete !== undefined) {
        options.complete(resp);
      }
    });*/
  } else {
    var httpclient = restAdapter.getBinary(url, etag);
    return httpclient.bodyAsBinary();
  }
};

///**
//* バイナリデータのGETメソッドを実行しレスポンスボディをBase64エンコードして返却する.
//* @param {String} pathValue 取得するパス
//* @param {String} contentType 取得するバイナリデータのContent-Type
//* @param {String} callback コールバックメソッド
//* @param {string} etag Used for if-none-match condition
//* @return {String} GETしたバイナリデータ
//* @throws {ClientException} DAO例外
//*/
/**
 * This method return the DAV collection details in binary format encoded with Base64.
 * @param {String} pathValue Path
 * @param {String} contentType Content-Type value
 * @param {Object} options optional callback, header and async parameters
 * @param {String} etag Used for if-none-match condition
 * @return {String} GET Binary data
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.getBinaryAsBase64 = function(pathValue,
    contentType, options, etag) {
  var body = this.getBinary(pathValue, options, etag);
  return "data:" + contentType + ";base64," + this.base64encoder(body);
};

dcc.box.DavCollection.prototype.base64encoder = function(s) {
  var base64list = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=";
  var t = "", p = -6, a = 0, i = 0, v = 0, c;

  while ((i < s.length) || (p > -6)) {
    if (p < 0) {
      if (i < s.length) {
        c = s.charCodeAt(i++);
        v += 8;
      } else {
        c = 0;
      }
      a = ((a & 255) << 8) | (c & 255);
      p += 8;
    }
    t += base64list.charAt((v > 0) ? (a >> p) & 63 : 64);
    p -= 6;
    v -= 6;
  }
  return t;
};

///**
//* DAVに対するGETメソッドをリクエストする.
//* @param {String} pathValue 取得するパス
//* @param {String} charset 文字コード
//* @param {string} etag Used for if-none-match condition
//* @return {dcc.box.DavResponse} GETした文字列を保持するレスポンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method returns the DAV collection data in response format.
 * @param {String} pathValue Path
 * @param {String} charset Character code
 * @param {Object} options optional callback, header and async parameters
 * @param {String} etag Used for if-none-match condition
 * @return {dcc.box.DavResponse} GET Response holding string
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.getAsResponse = function(pathValue, charset,
    options, etag) {
  if (charset === undefined) {
    charset = "utf-8";
  }
  var url = dcc.UrlUtils.append(this.getPath(), pathValue);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  //if (options !== undefined) {
    /*    restAdapter.get(url, "text/plain", etag, function(resp) {
      if (resp.getStatusCode() >= 300) {
        if (callback.error !== undefined) {
          callback.error(resp);
        }
      } else {
        if (callback.success !== undefined) {
          var body = resp.bodyAsString(charset);
          callback.success(new dcc.box.DavResponse(resp.accessor, body));
        }
      }
      if (callback.complete !== undefined) {
        callback.complete(resp);
      }
    });*/
    //restAdapter.get(url, "text/plain", etag,options);
  //} else {
    restAdapter.get(url, "text/plain", etag, options);
    var body = restAdapter.bodyAsString(charset);
    return new dcc.box.DavResponse(this.accessor, body);
 // }
};

///**
//* DAVに対するGETメソッドをリクエストする.
//* @param pathValue 取得するパス
//* @return GETしたストリーム
//* @throws ClientException DAO例外
//*/
//dcc.box.DavCollection.prototype.getStream = function(pathValue) {
//String url = dcc.UrlUtils.append(this.getPath(), pathValue);
////リクエスト
//DcResponse res = RestAdapterFactory.create(this.accessor).get(url,
//"application/octet-stream");
////レスポンスボディをストリームとして返却
//return res.bodyAsStream();
//};

///**
//* 指定pathに任意のInputStreamの内容をPUTします.
//指定IDのオブジェクトが既に存在すればそれを書き換え、存在しない場合はあらたに作成する.
//* @param pathValue DAVのパス
//* @param contentType メディアタイプ
//* @param enc 文字コード(使用しない)
//* @param is InputStream
//* @param etag ETag値
//* @throws ClientException DAO例外
//*/
////public void put(String pathValue, String contentType, String enc,
//InputStream is, String etag) throws ClientException {
//dcc.box.DavCollection.prototype.initializeProperties = function() {
////ストリームの場合はエンコーディング指定は使用しない
//put(pathValue, contentType, is, etag);
//};

///**
//* 指定pathに任意のInputStreamの内容をPUTします.
//指定IDのオブジェクトが既に存在すればそれを書き換え、存在しない場合はあらたに作成する.
//* @param pathValue DAVのパス
//* @param contentType メディアタイプ
//* @param is InputStream
//* @param etagValue ETag値
//* @throws ClientException DAO例外
//*/
////public void put(String pathValue, String contentType, InputStream is,
//String etagValue) throws ClientException {
//dcc.box.DavCollection.prototype.put = function() {
//String url = dcc.UrlUtils.append(this.getPath(), pathValue);
//((RestAdapter) RestAdapterFactory.create(this.accessor)).putStream(url,
//contentType, is, etagValue);
//};

///**
//* 指定Pathに任意の文字列データをPUTします.
//* @param {String} pathValue DAVのパス
//* @param contentType メディアタイプ
//* @param data PUTするデータ
//* @param etagValue PUT対象のETag。新規または強制更新の場合は "*" を指定する
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to update the DAV collection.
 * @param {String} pathValue DAV Path
 * @param {String} contentType Character code
 * @param {String} data PUT data
 * @param {String} etagValue ETag of PUT target. Specify "*" for forcing new or updated
 * @param {Object} options object optional contains callback, body, headers
 * @returns {dcc.DavResponse/dcc.DcHttpClient} response
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.put = function(pathValue, options) {
  var url = dcc.UrlUtils.append(this.getPath(), pathValue);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = "";
  var async = this._isAsynchronous(options);
  /*valid option is present with atleast one callback*/
  /*var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);

  if (!options) {
    options = {};
  }
  if(!options.headers){
    options.headers = {};
  }*/
  //TODO: Remove extra parameters from restAdapter put,as options itself contains all optional params 
  response = restAdapter.put(url, null, null,
      null, null, options);
  if (async) {
      return response;
  } else {
      return response.resolvedValue;
  }
  /*if(!callbackExist){
    return new dcc.box.DavResponse(this.accessor, response);
  }*/

  /*  if (callback !== undefined) {
    response = restAdapter.put(url, data, etag, contentType, {}, function(
        resp) {
      if (resp.getStatusCode() >= 300) {
        if (callback.error !== undefined) {
          callback.error(resp);
        }
      } else {
        if (callback.success !== undefined) {
          callback.success(new dcc.box.DavResponse(resp.accessor, ""));
        }
      }
      if (callback.complete !== undefined) {
        callback.complete(resp);
      }
    });
  } else {
    response = restAdapter.put(url, data, etag, contentType);
    return new dcc.box.DavResponse(this.accessor, response);
  }*/
  return response;
};

///**
//* 指定Pathに任意の文字列データをPUTします.
//* @param pathValue DAVのパス
//* @param contentType メディアタイプ
//* @param enc 文字コード
//* @param data PUTするデータ
//* @param etag PUT対象のETag。新規または強制更新の場合は "*" を指定する
//* @throws ClientException DAO例外
//*/
//dcc.box.DavCollection.prototype.put = function(pathValue, contentType, enc, data,
//etag) {
//byte[] bs;
//try {
//if (!enc.isEmpty()) {
//bs = data.getBytes(enc);
//} else {
//bs = data.getBytes("UTF-8");
//}
//} catch (UnsupportedEncodingException e) {
//throw new ClientException("UnsupportedEncodingException", e);
//}
//InputStream is = new ByteArrayInputStream(bs);
//String url = dcc.UrlUtils.append(this.getPath(), pathValue);
//((RestAdapter) RestAdapterFactory.create(this.accessor)).putStream(url,
//contentType, is, etag);
//};
///**
//* 指定PathのデータをDeleteします(ETag指定).
//* @param {String} pathValue DAVのパス
//* @param {String} etagValue PUT対象のETag。新規または強制更新の場合は "*" を指定する
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to delete the data in the specified Path (ETag specified).
 * @param {String} pathValue DAV Path
 * @param {String} etagValue ETag of PUT target. Specify "*" for forcing new or updated
 * @param {Object} options optional callback, header and async parameters
 * @returns {dcc.DavResponse/dcc.DcHttpClient} response
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.DavCollection.prototype.del = function(pathValue, etagValue, options) {
  if (typeof etagValue === "undefined") {
    etagValue = "*";
  }
  var async = this._isAsynchronous(options);
  var url = dcc.UrlUtils.append(this.getPath(), pathValue);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.del(url, etagValue,options);
  if (async) {
      return response;
  } else {
      return response.resolvedValue;
  }
  //var response = "";
  /*if(this.accessor.getContext().getAsync()){
    //asynchronous
    return restAdapter.del(url, etagValue,options);
  }else{
    var response = restAdapter.del(url, etagValue);
    return new dcc.box.DavResponse(this.accessor, response);
  }*/

  /* if (callback !== undefined) {
        restAdapter.del(url, etagValue, function(resp) {
            if (resp.getStatusCode() >= 300) {
                if (callback.error !== undefined) {
                    callback.error(resp);
                }
            } else {
                if (callback.success !== undefined) {
                    callback.success(new dcc.box.DavResponse(resp.accessor, ""));
                }
            }
            if (callback.complete !== undefined) {
                callback.complete(resp);
            }
        });
    } else {
        restAdapter.del(url, etagValue);
        return new dcc.box.DavResponse(this.accessor, "");
    }*/

  //Commented out response since both conditions either call callback or return DavResponse
  //return response;
};

///**
//* 引数で指定されたヘッダの値を取得.
//* @param headerKey 取得するヘッダのキー
//* @return ヘッダの値
//*/
/**
 * This method is used to get the value of the header that is specified in the argument.
 * @param {String} headerKey Key of the header
 * @return {String} value of the header
 */
dcc.box.DavCollection.prototype.getHeaderValue = function(headerKey) {
  return this.accessor.getResHeaders()[headerKey];
};

/**
 * The purpose of this function is to get JSON of cell profile information.
 * @param {String} pathValue
 * @param {string} etag Used for if-none-match condition
 * @returns
 */
dcc.box.DavCollection.prototype.getJSON = function(pathValue, etag) {
  var url = dcc.UrlUtils.append(this.getPath(), pathValue);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.get(url, "application/json", etag);
  return response;
};

/**
 * This method calls PROPFIND API for specified path to get 
 * registered service file detail. 
 * @param {String} name filename
 * @param {Object} options optional parameters
 * @returns {dcc.DcHttpClient} response.
 * @throws {dcc.ClientException} Exception thrown
 */
dcc.box.DavCollection.prototype.propfind = function (name,options) {
  var url = dcc.UrlUtils.append(this.getPath(), name);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.propfind(url,options);
  return response;
};