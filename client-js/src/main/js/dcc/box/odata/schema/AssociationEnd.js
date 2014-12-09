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
//* @class AssociationEndのアクセスクラス.
//* @constructor
//* @augments dcc.AbstractODataContext
//*/
/**
 * It creates a new object dcc.box.odata.schema.AssociationEnd.
 * @class This is the access class of Association End.
 * @constructor
 * @augments dcc.AbstractODataContext
 * @param {dcc.Accessor} as accessor
 * @param {Object} json
 * @param {String} path
 */
dcc.box.odata.schema.AssociationEnd = function(as, json, path) {
  this.initializeProperties(this, as, json, path);
};
dcc.DcClass.inherit(dcc.box.odata.schema.AssociationEnd, dcc.AbstractODataContext);

///**
//* プロパティを初期化する.
//* @param {dcc.AbstractODataContext} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {Object} json JSONオブジェクト
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.AbstractODataContext} self
 * @param {dcc.Accessor} as accessor
 * @param {Object} json JSON object
 * @param {String} path 
 */
dcc.box.odata.schema.AssociationEnd.prototype.initializeProperties = function(self, as, json, path) {
  this.uber = dcc.AbstractODataContext.prototype;
  this.uber.initializeProperties(self, as);

///** キャメル型で表現したクラス名. */
  /** Class name */
  this.CLASSNAME = "AssociationEnd";
///** EntityType名. */
  /** EntityType name */
  self.entityTypeName = null;
///** AssociationEnd名. */
  /** AssociationEnd name */
  self.name = null;
///** 多重度. */
  /** Multiplicity */
  self.multiplicity = null;
///** コレクションのパス. */
  /** URL */
  self.url = path;

  /** Link manager of the Account. */
  self.associationEnd = null;
  self.associationEnd = new dcc.cellctl.MetadataLinkManager(as, this);

  if (json !== undefined && json !== null) {
    self.rawData = json;
    self.name = json.Name;
    self.entityTypeName = json["_EntityType.Name"];
    self.multiplicity = json.Multiplicity;
  }
};

///**
//* AssociationEnd名の設定.
//* @param {String} value AssociationEnd名
//*/
/**
 * This method sets the name for AssociationEnd.
 * @param {String} value AssociationEnd name
 */
dcc.box.odata.schema.AssociationEnd.prototype.setName = function(value) {
  this.name = value;
};

///**
//* AssociationEnd名の取得.
//* @return {String} AssociationEnd名
//*/
/**
 * This method gets the name of AssociationEnd.
 * @return {String} AssociationEnd name
 */
dcc.box.odata.schema.AssociationEnd.prototype.getName = function() {
  return this.name;
};

///**
//* EntityType名の設定.
//* @param {String} value EntityType名
//*/
/**
 * This method sets the EntityType name.
 * @param {String} value EntityType name
 */
dcc.box.odata.schema.AssociationEnd.prototype.setEntityTypeName = function(value) {
  this.entityTypeName = value;
};

///**
//* EntityType名の取得.
//* @return {String} EntityType名
//*/
/**
 * This method gets the EntityType name.
 * @return {String} EntityType name
 */
dcc.box.odata.schema.AssociationEnd.prototype.getEntityTypeName = function() {
  return this.entityTypeName;
};

///**
//* multiplicityの設定.
//* @param {String} value 多重度
//*/
/**
 * This method sets the multiplicity.
 * @param {String} value multiplicity
 */
dcc.box.odata.schema.AssociationEnd.prototype.setMultiplicity = function(value) {
  this.multiplicity = value;
};

///**
//* multiplicityの取得.
//* @return {String} 多重度
//*/
/**
 * This method gets the multiplicity.
 * @return {String} multiplicity
 */
dcc.box.odata.schema.AssociationEnd.prototype.getMultiplicity = function() {
  return this.multiplicity;
};

///**
//* ODataのキーを取得する.
//* @return {String} ODataのキー情報
//*/
/**
 * This method gets the Odata key.
 * @return {String} OData key
 */
//public String getKey() {
dcc.box.odata.schema.AssociationEnd.prototype.getKey = function() {
  return "(Name='" + this.name + "',_EntityType.Name='" + this.entityTypeName + "')";
};

///**
//* クラス名をキャメル型で取得する.
//* @return {?} ODataのキー情報
//*/
/**
 * This method gets the class name.
 * @return {String} OData class name
 */
dcc.box.odata.schema.AssociationEnd.prototype.getClassName = function() {
  return this.CLASSNAME;
};

///**
//* URLを取得.
//* @return URL文字列
//*/
/**
 * This method gets the URL.
 * @return {String} URL value.
 */
dcc.box.odata.schema.AssociationEnd.prototype.getPath = function() {
  return this.url;
};
