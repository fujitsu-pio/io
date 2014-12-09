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
//* @class ODataのBatchLinksEntityクラス.
//* @constructor
//*/
/**
 * It creates a new object dcc.box.odata.BatchLinksEntity.
 * @class BatchLinksEntity class of OData.
 * @constructor
 * @param {String} entitySetName
 * @param {String} id
 * @param {dcc.Accessor} as Accessor
 * @param {String} collectionUrl 
 */
dcc.box.odata.BatchLinksEntity = function(entitySetName, id, as, collectionUrl) {
  this.initializeProperties(this, entitySetName, id, as, collectionUrl);
};

///**
//* プロパティを初期化する.
//* @param {dcc.AbstractODataContext} self
//* @param {String} entitySetName EntitySet名
//* @param {String} id Entityの__id
//* @param {dcc.Accessor} as アクセス主体
//* @param {String} collectionUrl ODataコレクションのURL
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.AbstractODataContext} self
 * @param {String} entitySetName EntitySet Name
 * @param {String} id __id Of Entity
 * @param {dcc.Accessor} as Accessor
 * @param {String} collectionUrl URL of OData collection
 */
dcc.box.odata.BatchLinksEntity.prototype.initializeProperties = function(self,
    entitySetName, id, as, collectionUrl) {
  if (typeof entitySetName !== "string" || typeof id !== "string") {
    throw new dcc.ClientException("InvalidParameter");
  }
  this.entitySetName = entitySetName;
  this.id = id;
  if (typeof as !== "undefined") {
    if (typeof collectionUrl !== "string") {
      throw new dcc.ClientException("InvalidParameter");
    }
    this.collectionUrl = collectionUrl;
    this.entity = new dcc.cellctl.LinkManager(as, this);
  }
};

///**
//* ODataコレクションのURLを取得.
//* @return ODataコレクションのURL
//*/
/**
 * This method gets the URL of the OData collection.
 * @return {String} collectionUrl URL of OData collection
 */
dcc.box.odata.BatchLinksEntity.prototype.getCollectionUrl = function() {
  return this.collectionUrl;
};
/**
 * This method gets the key of the OData collection.
 * @return {String} key of OData collection
 */
dcc.box.odata.BatchLinksEntity.prototype.getKey = function() {
  return "('" + this.id + "')";
};
/**
 * This method gets the class name of the OData collection.
 * @return {String} entitySetName of OData collection
 */
dcc.box.odata.BatchLinksEntity.prototype.getClassName = function() {
  return this.entitySetName;
};
/**
 * This method gets the odata link of the OData collection.
 * @return {String} odata link of OData collection
 */
dcc.box.odata.BatchLinksEntity.prototype.getODataLink = function() {
  if (this.collectionUrl == null) {
    // $links先用
    /** $ links-destination */
    return "/" + this.entitySetName + this.getKey();
  } else {
    // $links元用
    /** $ links for sources */
    return this.entitySetName + this.getKey();
  }
};
/**
 * This method creates url for odata collection.
 * @returns {String} URL
 */
dcc.box.odata.BatchLinksEntity.prototype.makeUrlForLink = function() {
  var url = this.getODataLink();
  url += "/$links/";
  return url;
};
