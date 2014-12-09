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
//* @class ODataへアクセスするためのクラス.
//* @constructor
//* @augments dcc.DcCollection
//*/
/**
 * It creates a new object dcc.box.ODataCollection.
 * @class This class represents the OData collections for performing OData related operations.
 * @constructor
 * @augments dcc.DcCollection
 * @param {dcc.Accessor} Accessor
 * @param {String} name
 */
dcc.box.ODataCollection = function(as, name) {
  this.initializeProperties(this, as, name);
};
dcc.DcClass.inherit(dcc.box.ODataCollection, dcc.DcCollection);

///**
//* プロパティを初期化する.
//* @param {dcc.box.ODataCollection} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {String} name コレクション名
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.box.ODataCollection} self
 * @param {dcc.Accessor} as Accessor
 * @param {String} name URL for path
 */
dcc.box.ODataCollection.prototype.initializeProperties = function(self, as, name) {
  this.uber = dcc.DcCollection.prototype;
  this.uber.initializeProperties(self, as, name);

  if (as !== undefined) {
//  /** EntitySetアクセスするためのクラス. */
    /** Manager to access EntityType. */
    self.entityType = new dcc.box.odata.schema.EntityTypeManager(as, this);
//  /** assosiationendアクセスのためのクラス. */
    /** Manager to access AssociationEnd. */
    self.associationEnd = new dcc.box.odata.schema.AssociationEndManager(as, this);
  }
};

///**
//* EntitySetの指定.
//* @param {String} name EntitySet名
//* @return {dcc.EntitySet} 生成したEntitySetオブジェクト
//*/
/**
 * This method returns an EntitySet.
 * @param {String} name EntitySet Name
 * @return {dcc.EntitySet} EntitySet object
 */
dcc.box.ODataCollection.prototype.entitySet = function(name) {
  return new dcc.EntitySet(this.accessor, this, name);
};

///**
//* Batch生成.
//* @return {dcc.box.odata.ODataBatch} 生成したODataBatchオブジェクト
//*/
/**
 * This method generates the ODataBatch.
 * @return {dcc.box.odata.ODataBatch} ODataBatch object
 */
dcc.box.ODataCollection.prototype.makeODataBatch = function() {
  return new dcc.box.odata.ODataBatch(this.accessor, this.getPath());
};

