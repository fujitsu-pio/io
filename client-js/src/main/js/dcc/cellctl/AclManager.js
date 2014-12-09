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
//* @class ACLのCRUDを行うためのクラス.
//* @constructor
//*/
/**
 * It creates a new object dcc.cellctl.AclManager.
 * @class This class performs the CRUD operations for ACL.
 * @constructor
 * @param {dcc.Accessor} as accessor
 * @param {dcc.box.DavCollection} dav
 */
dcc.cellctl.AclManager = function(as, dav) {
  this.initializeProperties(this, as, dav);
};

///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.AclManager} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {?} dav
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.AclManager} self
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.DcCollection} dav
 */
dcc.cellctl.AclManager.prototype.initializeProperties = function(self, as, dav) {
  self.accessor = as;
///** DAVコレクション. */
  /** DAV Collection */
  self.collection = dav;
};


///**
//* ACLを登録する.
//* @param {Object} body リクエストボディ(XML形式)
//* @throws {ClientException} DAO例外
//*/
/**
 * This method registers the ACL.
 * @param {Object} body Request body (XML format)
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.AclManager.prototype.set = function(body) {
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  restAdapter.acl(this.getUrl(), body);
};

///**
//* ACLオブジェクトとしてACLをセットする.
//* @param {dcc.Acl} obj Aclオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method sets the ACL object.
 * @param {dcc.Acl} aclObject ACL object
 * @param {Object} options contains callback and headers
 * @returns {dcc.DcHttpClient} response
 * @throws {dcc.ClientException} exception
 */
dcc.cellctl.AclManager.prototype.setAsAcl = function(aclObject, options) {
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  var response = restAdapter.acl(this.getUrl(), aclObject.toXmlString(), options);
  return response;
};


///**
//* ACL情報をAclオブジェクトとして取得.
//* @return {dcc.Acl} Aclオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method gets ACL information.
 * @return {dcc.Acl} Acl object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.AclManager.prototype.get = function() {
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  restAdapter.propfind(this.getUrl());
  return dcc.Acl.parse(restAdapter.bodyAsString());
};

///**
//* URLを生成.
//* @return {?} 現在のコレクションへのURL
//*/
/**
 * This method returns the URL.
 * @return {String} URL of current collection
 */
dcc.cellctl.AclManager.prototype.getUrl = function() {
  return this.collection.getPath();
};

