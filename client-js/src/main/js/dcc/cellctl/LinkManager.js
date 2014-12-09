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
 * It creates a new object dcc.cellctl.LinkManager.
 * @class This class performs CRUD operations on link between two cell control objects.
 * @constructor
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.AbstractODataContext} cx context reference
 * @param {String} className name of the class
 */
dcc.cellctl.LinkManager = function(as, cx, className) {
  this.initializeProperties(this, as, cx, className);
};

///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.LinkManager} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {dcc.DcContext} cx ターゲットオブジェクト
//* @param className
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.LinkManager} self
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcContext} cx Target object
 * @param {String} className
 */
dcc.cellctl.LinkManager.prototype.initializeProperties = function(self, as, cx, className) {
///** アクセス主体. */
  /** Accessor object. */
  self.accessor = as;

///** リンク主体. */
  /** Link subject. */
  self.context = cx;

///** リンク先名. */
  /** Class name in camel case. */
  self.className = className;
};

///**
//* リンクを作成.
//* @param {?} cx リンクさせるターゲットオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method creates a link between two cell control objects.
 * @param {Object} cx Target object to be linked
 * @param {Object} options Callback options
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.LinkManager.prototype.link = function(cx,options) {
  var uri = this.getLinkUrl(cx);
  var body = {};
  body.uri = cx.getODataLink();
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var callbackExist = options !== undefined &&
  (options.success !== undefined ||
      options.error !== undefined ||
      options.complete !== undefined);
  if (callbackExist) {
    restAdapter.post(uri, JSON.stringify(body), "application/json",{},options);
  } else {
    restAdapter.post(uri, JSON.stringify(body), "application/json");
  }
};


///**
//* リンクを削除unlink.
//* @param {?} cx リンク削除するターゲットオブジェクト
//* @param callback parameter
//* @throws {ClientException} DAO例外
//*/
/**
 * This method deletes the link between two cell control objects.
 * @param {Object} cx Target object for which link is to be deleted
 * @param {Object} options optional callback, header and async parameters
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.LinkManager.prototype.unlink = function(cx,options) {
  var uri = this.getLinkUrl(cx);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var optionsOrEtag = null;
  restAdapter.del(uri + cx.getKey(),optionsOrEtag,options);
};

/**
 * This method performs Query search by appending query string to URL and
 * returns object.
 * @param {dcc.box.odata.DcQuery} query
 * @param {Object} options optional callback, header and async parameters
 * @return {Object} JSON object
 */
dcc.cellctl.LinkManager.prototype.doSearch = function(query, options) {
  var url = this.getLinkUrl();
  var qry = query.makeQueryString();
  if ((qry !== null) && (qry !== "")) {
    url += "?" + qry;
  }
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  if (options !== undefined) {
    restAdapter.get(url, "application/json", "*",options);
    /*restAdapter.get(url, "application/json", "*", function(resp) {
      var responseBody = resp.bodyAsJson();
      var json = responseBody.d.results;
      options(json);
    });*/
  } else {
    restAdapter.get(url, "application/json", "*" );
    var json = restAdapter.bodyAsJson().d.results;
    return json;
  }
};

/**
 * This method performs Query search by appending query string to URL and
 * returns ODataResponse.
 * @param {dcc.box.odata.DcQuery} query
 * @param {Object} options optional callback, header and async parameters
 * @return {dcc.box.odata.ODataResponse} Response
 */
dcc.cellctl.LinkManager.prototype.doSearchAsResponse = function(query, options) {
  var url = this.getLinkUrl();
  var qry = query.makeQueryString();
  if ((qry !== null) && (qry !== "")) {
    url += "?" + qry;
  }
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  if (options !== undefined) {
    restAdapter.get(url, "application/json", "*",options);
    /*restAdapter.get(url, "application/json", "*", function(resp) {
      var responseBody = resp.bodyAsJson();
      options(new dcc.box.odata.ODataResponse(this.accessor, "", responseBody));
    });*/
  } else {
    restAdapter.get(url, "application/json", "*" );
    return new dcc.box.odata.ODataResponse(this.accessor, "", restAdapter.bodyAsJson());
  }
};

/**
 * This method generates a query.
 * @return {dcc.box.odata.DcQuery} Query object
 */
dcc.cellctl.LinkManager.prototype.query = function() {
  return new dcc.box.odata.DcQuery(this);
};

/**
 * The purpose of this method is to create URL for calling link API's.
 * @param {Object} cx
 * @return {String} URL
 */dcc.cellctl.LinkManager.prototype.getLinkUrl = function(cx) {
   var sb = this.accessor.getBaseUrl();
   var classNameForURL = null;
   sb += this.accessor.getCurrentCell().getName();
   sb += "/__ctl/";
   sb += this.context.getClassName();
   sb += this.context.getKey();
   sb += "/$links/";

   if (cx !== undefined) {
     classNameForURL = cx.getClassName();//check style fix
   } /*else {
		sb += "_" + this.className;
	}*/
   classNameForURL = this.className;
   sb += "_" + classNameForURL;
   return sb;
 };

 /**
  * The purpose of this method is to retrieve link between two entities
  * for box profile page.
  * @param {Object} cx
  * @param {String} source
  * @param {String} destination
  * @param {String} key
  * @return {Object} response
  */
 dcc.cellctl.LinkManager.prototype.retrieveBoxProfileLinks = function(cx, source,destination,key) {
   var uri = this.getLinkUrlWithKey(cx, source, destination, key);
   var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
   var response = restAdapter.get(uri, "", "application/json");
   return response;
 };

 /**
  * The purpose of this method is to create link url between two entities..
  * @param {String} cx
  * @param {String} source
  * @param {String} destination
  * @param {String} key
  * @return {String} URL
  */
 dcc.cellctl.LinkManager.prototype.getLinkUrlWithKey = function(cx, source, destination, key) {
   var sb = this.accessor.getBaseUrl();
   sb += this.accessor.cellName;
   sb += "/__ctl/";
   sb += source;
   sb += "('" + key + "')"; // account name
   sb += "/$links/";
   sb += "_" + destination;
   return sb;
 };

 /**
  * The purpose of the following method is to create Role URI.
  * @param {Object} cx
  * @param {String} source
  * @param {String} destination
  * @param {String} boxName
  * @param {String} rolename
  * @return {String} role URI
  */
 dcc.cellctl.LinkManager.prototype.getRoleUri = function(cx, source, destination,
     boxName, rolename) {
   var cBoxName = boxName;
   if (cBoxName !== null) {
     cBoxName = "'" + cBoxName + "'";
   }
   rolename = "'" + rolename + "'";
   var key = "(Name=" + rolename + ",_Box.Name=" + cBoxName + ")";
   key = key.split(" ").join("");
   var sb = this.accessor.getBaseUrl();
   sb += this.accessor.cellName;
   sb += "/__ctl/";
   sb += destination;
   if(destination === "ExtCell"){
     key = "("+rolename+ ")";
   }
   if(destination === "ExtRole"){
     var relation_box_pair = boxName.split(",");
     key = "(ExtRole=" + rolename + ",_Relation.Name='" + relation_box_pair[0].split(" ").join("") + "',_Relation._Box.Name='"+ relation_box_pair[1].split(" ").join("")+"')";
   }
   sb += key;
   //var roleuri = "{\"uri\":" + "'" + sb;
   var roleuri = "{\"uri\":" + "'" + sb;
   //var roleuri = '{\"uri\":' + '"' + sb;
   roleuri += "'}";
   // roleuri += "'}";
   return roleuri;
 };

 /**
  * The purpose of the following method is to establish link between role and account.
  * @param {Object} cx
  * @param {String} source
  * @param {String} destination
  * @param {String} key
  * @param {String} boxName
  * @param {String} rolename
  * @param {Boolean} isMultiKey
  * @param {Object} options
  * @return {Object} response
  */
 dcc.cellctl.LinkManager.prototype.establishLink = function(cx, source,
     destination, key, boxName, rolename, isMultiKey,options) {
   var uri = this.getLinkUrlWithKey(cx, source, destination, key);
   if (isMultiKey === true) {
     uri = this.getLinkUrlWithMultiKey(cx, source, destination, key);
     isMultiKey = false;
   }
   var roleuri = this.getRoleUri(cx, source, destination, boxName,
       rolename);
   var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
   var callbackExist = options !== undefined &&
   (options.success !== undefined ||
       options.error !== undefined ||
       options.complete !== undefined);
   if (callbackExist) {
     restAdapter.post(uri, roleuri, "application/json",{},options);
   } else {
     var response = restAdapter.post(uri, roleuri, "application/json");
     return response;
   }
 };

 /**
  * The purpose of the following method is to fetch the linkages between an account and roles.
  * @param {Object} cx
  * @param {String} source
  * @param {String} destination
  * @param {String} key
  * @param {String} boxName
  * @param {String} rolename
  * @return {Object} response
  */
 dcc.cellctl.LinkManager.prototype.retrieveAccountRoleLinks = function(cx, source,
     destination, key, boxName, rolename) {
   var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
   var response = "";
   var uri = "";
   if (boxName === "" || rolename === "") {
     uri = this.getLinkUrlWithKey(cx, source, destination,key);
     response = restAdapter.get(uri, "application/json");
   } else {
     uri = this.getLinkUrlWithKey(cx, source, destination,key);
     var roleuri = this.getRoleUri(cx, source, destination,boxName, rolename);
     response = restAdapter.get(uri, "application/json", roleuri);
   }
   return response;
 };

 /**
  * The purpose of the following method is to fetch the linkages between an role and account.
  * @param {Object} cx
  * @param {String} source
  * @param {String} destination
  * @param {String} key
  * @return {Object} response
  */
 dcc.cellctl.LinkManager.prototype.retrieveRoleAccountLinks = function(cx, source,destination, key) {
   var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
   var response;
   var uri = this.getLinkUrlWithMultiKey(cx, source, destination,key);
   response = restAdapter.get(uri, "", "application/json");
   return response;
 };

 /**
  * The purpose of the following method is to make the uri for retrieve account linkage.
  * @param {Object} cx
  * @param {String} source
  * @param {String} destination
  * @param {String} key
  * @return {String} link URL
  */
 dcc.cellctl.LinkManager.prototype.getLinkUrlWithMultiKey = function(cx, source, destination, key) {
   var sb = this.accessor.getBaseUrl();
   sb += this.accessor.cellName;
   sb += "/__ctl/";
   sb += source;
   sb += key ; // Combination of role and box name
   sb += "/$links/";
   sb += "_" + destination;
   return sb;
 };


 /**
  * The purpose of the following method is to unlink two cell control objects.
  * @param {Object} cx
  * @param {String} source end of the mapping - Relation, ExtRole,Role
  * @param {String} destination end of the mapping - ExtCell, Relation
  * @param {String} key is component of final URL
  * @param {String} boxName is associated box name
  * @param {String} roleName optional
  * @param {Object} options optional
  * @return {Object} response
  */
 dcc.cellctl.LinkManager.prototype.delLink = function(cx, source,
     destination, key, boxName, roleName, options) {
   var uri = null;
   var response = "";
   var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
   //source is Relation or ExtRole and destination is ExtCell
   if(source === "Relation" || source === "ExtRole"){
     uri = this.getLinkUrlWithMultiKey(cx, source, destination, key);
     if (destination === "ExtCell"){
       uri += "(Url='" + boxName  + "')";
       response = restAdapter.del(uri, options, "");
       return response;
     }//unlink Role to Relation or ExtCell Mapping
   } else if (source === "Role") {
     uri = this.getLinkUrlWithMultiKey(cx, source, destination, key, boxName);
     //Role-Relation delete scenario
     if(destination === "Relation"){
       uri += boxName  ;
       response = restAdapter.del(uri, options, "");
       return response;
     }
     if(destination === "ExtCell"){
       uri += "(Url='" + boxName  + "')";
       response = restAdapter.del(uri, options, "");
       return response;
     }
     uri += "(Name='" + boxName  + "')";
     response = restAdapter.del(uri, options, "");
     return response;
   } else{
     uri = this.getLinkUrlWithKey(cx, source, destination,key);
     uri += "(Name='" + roleName + "'";
     if (boxName == "null") {
       uri += ",_Box.Name=" + boxName + ")";
     }
     else {
       uri += ",_Box.Name='" + boxName + "')";
     }
     response = restAdapter.del(uri, options, "");
     return response;
   }
 };

 /**
  * The purpose of this method is to generate url for creating a link between external cell and relation.
  * @param {Object} cx
  * @param {String} source
  * @param {String} destination
  * @param {String} extCellURL
  * @return {String} URL
  */
 dcc.cellctl.LinkManager.prototype.getLinkUrlForExtCell = function(cx, source, destination, extCellURL) {
   var sb = this.accessor.getBaseUrl();
   sb += this.accessor.cellName;
   sb += "/__ctl/";
   sb += source;
   var key = "'"+ extCellURL +"'";
   sb +=  "(" + encodeURIComponent( key) + ")";
   sb += "/$links/";
   sb += "_" + destination;
   return sb;
 };

 /**
  * The purpose of the following method is to establish link between role and account.
  * @param {Object} cx
  * @param {String} source
  * @param {String} destination
  * @param {String} extCellURL
  * @param {String} boxName
  * @param {String} relName
  * @param {Object} options
  * @return {dcc.http.DcHttpClient} response
  */
 dcc.cellctl.LinkManager.prototype.externalCellRelationlink = function(cx, source,
     destination, extCellURL, boxName, relName,options) {
   var uri = this
   .getLinkUrlForExtCell(cx, source, destination, extCellURL, relName, boxName);
   var externalCellURI = this.getRoleUri(cx, source, destination, boxName, relName);
   var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
   if (options !== undefined) {
     restAdapter.post(uri, externalCellURI, "application/json",{},options);
   } else {
     var response = restAdapter.post(uri, externalCellURI, "application/json");
     return response;
   }
 };

 /**
  * The purpose of the following method is to fetch the linkages between an external cell and relation.
  * @param {Object} cx
  * @param {String} source
  * @param {String} destination
  * @param {String} extCellURL
  * @param {String} boxName
  * @param {String] relName
  * @return {dcc.http.DcHttpClient} response
  */
 dcc.cellctl.LinkManager.prototype.retrieveExtCellRelLinks = function(cx, source,
     destination, extCellURL, boxName, relName) {
   var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
   var response;
   var uri = this.getLinkUrlForExtCell(cx, source, destination, extCellURL, relName, boxName);
   response = restAdapter.get(uri, "", "application/json");
   return response;
 };