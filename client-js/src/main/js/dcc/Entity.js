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
//* @class コレクションの抽象クラス.
//* @constructor
//* @augments jAbstractODataContext
//*/
/**
 * It creates a new object dcc.Entity.
 * @class This is the abstract class for a collection.
 * @constructor
 * @augments jAbstractODataContext
 * @param {dcc.Accessor} as Accessor
 * @param {String} path
 */
dcc.Entity = function(as, path) {
  this.initializeProperties(this, as, path);
};
dcc.DcClass.inherit(dcc.Entity, dcc.AbstractODataContext);

///**
//* プロパティを初期化する.
//*/
/**
 * This method initializes the properties of this class.
 */
dcc.Entity.prototype.initializeProperties = function(self, as, path) {
  this.uber = dcc.AbstractODataContext.prototype;
  this.uber.initializeProperties(self, as);

  if (as !== undefined) {
    self.accessor = as.clone();
  }

///** キャメル方で表現したクラス名. */
  /** Class name in camel case. */
  self.CLASSNAME = "";
///** コレクションのパス. */
  /** Path of collection. */
  self.url = path;

};

///**
//* URLを取得.
//* @return URL文字列
//*/
/**
 * This method gets the path.
 * @return {String} URL Path
 */
dcc.Entity.prototype.getPath = function() {
  return this.url;
};

///**
//* ODataのキーを取得する.
//* @return ODataのキー情報
//*/
/**
 * This method gets the Odata key.
 * @return {String} OData key.
 */
dcc.Entity.prototype.getKey = function() {
  return "";
};

///**
//* クラス名をキャメル型で取得する.
//* @return ODataのキー情報
//*/
/**
 * This method gets the odata class name.
 * @return {String} OData class name
 */
dcc.Entity.prototype.getClassName = function() {
  return this.CLASSNAME;
};

