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
//* @class ExtCellのアクセスクラス.
//* @constructor
//* @augments dcc.AbstractODataContext
//*/
/**
 * It creates a new object dcc.cellctl.ExtCell.
 * @class This class represents External Cell to access its related fields.
 * @constructor
 * @augments dcc.AbstractODataContext
 * @param {dcc.Accessor} as Accessor
 * @param {Object} body
 */
dcc.cellctl.ExtCell = function(as, body) {
  this.initializeProperties(this, as, body);
};
dcc.DcClass.inherit(dcc.cellctl.ExtCell, dcc.AbstractODataContext);

///**
//* コンストラクタ.
//*/
//public ExtCell() {
//super();
//}

///**
//* コンストラクタ.
//* @param as アクセス主体
//*/
//public ExtCell(final Accessor as) {
//this.initialize(as, null);
//}

///**
//* コンストラクタ.
//* @param as アクセス主体
//* @param body 生成するExtCellのJson
//*/
//public ExtCell(final Accessor as, JSONObject body) {
//this.initialize(as, body);
//}

///**
//* オブジェクトを初期化.
//* @param as アクセス主体
//* @param json サーバーから返却されたJSONオブジェクト
//*/
//public void initialize(Accessor as, JSONObject json) {

///**
//* プロパティを初期化する.
//* @param {dcc.cellctl.ExtCell} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {?} json
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.cellctl.ExtCell} self
 * @param {dcc.Accessor} as Accessor
 * @param {Object} json
 */
dcc.cellctl.ExtCell.prototype.initializeProperties = function(self, as, json) {
  this.uber = dcc.AbstractODataContext.prototype;
  this.uber.initializeProperties(self, as);

  /** クラス名. */
  self.CLASSNAME = "ExtCell";

  /** url. */
  self.url = null;

  /** Roleとのリンクマネージャ. */
  self.role = null;
  /** Relationとのリンクマネージャ. */
  self.relation = null;

  if (json !== null) {
    self.rawData = json;
    self.url = json.Url;
  }
  if (as !== undefined) {
    self.role = new dcc.cellctl.LinkManager(as, this, "Role");
    self.relation = new dcc.cellctl.LinkManager(as, this, "Relation");
  }
};

///**
//* urlの設定.
//* @param {String} value URL値
//*/
/**
 * This method sets the URL to perform operations on External Cell.
 * @param {String} value URL value
 */
dcc.cellctl.ExtCell.prototype.setUrl = function(value) {
  this.url = value;
};

///**
//* urlの取得.
//* @return {String} Role名
//*/
/**
 * This method gets the URL to perform operations on External Cell.
 * @return {String} URL value
 */
dcc.cellctl.ExtCell.prototype.getUrl = function() {
  return this.url;
};

///**
//* ODataのキーを取得する.
//* @return {String} ODataのキー情報
//*/
/**
 * This method returns the Odata key.
 * @return {String} Key information of OData
 */
dcc.cellctl.ExtCell.prototype.getKey = function() {
  return "('" + encodeURIComponent(this.url) +"')";
};

///**
//* クラス名をキャメル型で取得する.
//* @return ODataのキー情報
//*/
/**
 * This method returns the class name.
 * @return {String} OData class name
 */
dcc.cellctl.ExtCell.prototype.getClassName = function() {
  return this.CLASSNAME;
};

