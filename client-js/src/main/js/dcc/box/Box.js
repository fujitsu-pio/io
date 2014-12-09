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
//* @class Boxへアクセスするためのクラス.
//* @constructor
//* @augments dcc.box.DavCollection
//*/
/**
 * It creates a new object dcc.box.Box.
 * @class This class represents Box to access box related fields.
 * @constructor
 * @augments dcc.box.DavCollection
 * @param {dcc.Accessor} as Accessor
 * @param {Object} json
 * @param {String} path 
 */
dcc.box.Box = function(as, json, path) {
  this.initializeProperties(this, as, json, path);
};
dcc.DcClass.inherit(dcc.box.Box, dcc.box.DavCollection);

///**
//* プロパティを初期化する.
//* @param {dcc.box.Box} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {Object} json JSONオブジェクト
//* @param {?} path
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.box.Box} self
 * @param {dcc.Accessor} as Accessor
 * @param {Object} json JSON object
 * @param {String} path
 */
dcc.box.Box.prototype.initializeProperties = function(self, as, json, path) {
  this.uber = dcc.box.DavCollection.prototype;
  this.uber.initializeProperties(self, as, path);

  self.name = "";
  self.schema = "";
  self.acl = null;
  self.event = null;

  if (json !== undefined) {
    self.name = json.Name;
    self.schema = json.Schema;
  }
  if (as !== undefined) {
    self.acl = new dcc.cellctl.AclManager(as, this);
    self.event = new dcc.cellctl.EventManager(as);
  }
};

///**
//* Box名を取得.
//* @return {String} Box名
//*/
/**
 * This method gets the box name.
 * @return {String} Box name
 */
dcc.box.Box.prototype.getName = function() {
  return this.name;
};

///**
//* Boxを設定.
//* @param {String} value Box名
//*/
/**
 * This method sets the box name.
 * @param {String} value Box name
 */
dcc.box.Box.prototype.setName = function(value) {
  this.name = value;
};

///**
//* スキーマを取得.
//* @return {?} スキーマ
//*/
/**
 * This method gets the box schema.
 * @return {String} value Box schema
 */
dcc.box.Box.prototype.getSchema = function() {
  return this.schema;
};

///**
//* スキーマを設定.
//* @param {String} value スキーマ
//*/
/**
 * This method sets the box schema.
 * @param {String} value Box schema
 */
dcc.box.Box.prototype.setSchema = function(value) {
  this.schema = value;
};

///**
//* JSONオブジェクトを生成する.
//* @return {?} 生成したJSONオブジェクト
//*/
/**
 * This method generates the json for Box.
 * @return {Object} JSON object
 */
dcc.box.Box.prototype.toJSON = function() {
  var json = {};
  json.Name = this.name;
  json.Schema = this.schema;
  return json;
};

