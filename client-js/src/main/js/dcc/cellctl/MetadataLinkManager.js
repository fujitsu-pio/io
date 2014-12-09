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
 * It creates a new object dcc.cellctl.MetadataLinkManager.
 * @class This class performs link/unlink operations on metadata.
 * @constructor
 * @param {dcc.Accessor} as Accessor
 * @param {dcc.AbstractODataContext} cx
 */
dcc.cellctl.MetadataLinkManager = function(as, cx) {
  this.initializeProperties(this, as, cx);
};

///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.MetadataLinkManager} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {?} cx ターゲットオブジェクト
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.MetadataLinkManager} self
 * @param {dcc.Accessor} as Accessor
 * @param {Object} cx Target object
 */
dcc.cellctl.MetadataLinkManager.prototype.initializeProperties = function(self, as, cx) {
///** アクセス主体. */
  /** Accessor. */
  self.accessor = as;

///** リンク主体. */
  /** Link subject. */
  self.context = cx;
};

///**
//* リンクを削除.
//* @param cx リンク削除するターゲットオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to remove a link.
 * @param {Object} cx Target object for removing the link.
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.MetadataLinkManager.prototype.unlink = function(cx) {
  var uri = this.getLinkUrl(cx);
  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  restAdapter.del(uri + cx.getKey());
};

///**
//* リンクを作成.
//* @param cx リンクさせるターゲットオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to create a link.
 * @param {Object} cx Target object for creating the link.
 * @param {Object} options Callback object.
 * @throws {dcc.ClientException} DAO exception
 */
dcc.cellctl.MetadataLinkManager.prototype.link = function(cx,options) {
  var uri = this.getLinkUrl(cx);
  var headers ={};
  var body = {};
  body.uri = cx.getODataLink();

  var restAdapter = dcc.http.RestAdapterFactory.create(this.accessor);
  //restAdapter.post(uri, JSON.stringify(body), "application/json");
  restAdapter.post(uri, JSON.stringify(body), "application/json", headers, options);
};

///**
//* $linkへのリクエストurlを生成する.
//* @param cx ターゲットのODataオブジェクト
//* @return {String} 生成したurl
//*/
/**
 * This method generates a request to the URL $ link.
 * @param {Object} cx Target object
 * @return {String} Generated URL
 */
dcc.cellctl.MetadataLinkManager.prototype.getLinkUrl = function(cx) {
  var sb = "";
  sb += this.context.getODataLink();
  sb += "/$links/";
  sb += "_" + cx.getClassName();
  return sb;
};

