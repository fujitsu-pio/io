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
//* @class EntityTypeのアクセスクラス.
//* @constructor
//* @augments dcc.AbstractODataContext
//*/
/**
 * It creates a new object dcc.box.odata.schema.EntityType.
 * @class This class represents the EntityType object.
 * @constructor
 * @augments dcc.AbstractODataContext
 * @param {dcc.Accessor} Accessor
 * @param {Object} body
 */
dcc.box.odata.schema.EntityType = function(as, body) {
  this.initializeProperties(this, as, body);
};
dcc.DcClass.inherit(dcc.box.odata.schema.EntityType, dcc.AbstractODataContext);

///**
//* オブジェクトを初期化.
//* @param {dcc.box.odata.schema.EntityType} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {Object} json サーバーから返却されたJSONオブジェクト
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.box.odata.schema.EntityType} self
 * @param {dcc.Accessor} as Accessor
 * @param {Object} json JSON object returned from server
 */
dcc.box.odata.schema.EntityType.prototype.initializeProperties = function(self, as, json) {
  this.uber = dcc.AbstractODataContext.prototype;
  this.uber.initializeProperties(self, as);

///** キャメル方で表現したクラス名. */
  /** Class name in camel case. */
  this.CLASSNAME = "EntityType";

///** EntityType名. */
  /** EntityType name. */
  self.name = "";

  if (json !== undefined && json !== null) {
    self.rawData = json;
    self.name = json.Name;
  }
};

///**
//* EntityType名の設定.
//* @param {String} value EntityType名
//*/
/**
 * This method sets the EntityType name.
 * @param {String} value EntityType name
 */
dcc.box.odata.schema.EntityType.prototype.setName = function(value) {
  this.name = value;
};

///**
//* EntityType名の取得.
//* @return {String} EntityType名
//*/
/**
 * This method gets the EntityType name.
 * @return {String} EntityType name
 */
dcc.box.odata.schema.EntityType.prototype.getName = function() {
  return this.name;
};

///**
//* ODataのキーを取得する.
//* @return {String} ODataのキー情報
//*/
/**
 * This method gets the Odata key.
 * @return {String} OData key
 */
dcc.box.odata.schema.EntityType.prototype.getKey = function() {
  return "('" + this.name + "')";
};

///**
//* クラス名をキャメル型で取得する.
//* @return {?} ODataのキー情報
//*/
/**
 * This method gets the class name.
 * @return {String} OData class name
 */
dcc.box.odata.schema.EntityType.prototype.getClassName = function() {
  return this.CLASSNAME;
};

