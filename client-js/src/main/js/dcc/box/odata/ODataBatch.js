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
//* コマンドを$Batchフォーマットに生成する.
//* @class Represents ODataBatch.
//*/
/**
 * This class is used to generate the $ Batch format command.
 * @class Represents ODataBatch.
 * @param {dcc.Accessor} Accessor
 * @param {String} name
 */
dcc.box.odata.ODataBatch = function(as, name) {
  this.initializeProperties(this, as, name);
};
dcc.DcClass.inherit(dcc.box.odata.ODataBatch, dcc.box.ODataCollection);

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
 * @param {String} name Path to URL
 */
dcc.box.odata.ODataBatch.prototype.initializeProperties = function(self, as, name) {
  this.uber = dcc.box.ODataCollection.prototype;
  this.uber.initializeProperties(self, as, name);
  this.accessor.setBatch(true);

  self.odataResponses = [];
};

///**
//* Batchコマンドの実行.
//* @throws ClientException DAO例外
//*/
/**
 * This method is responsible for Batch execution of commands.
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataBatch.prototype.send = function() {
  var url = dcc.UrlUtils.append(this.getPath(), "$batch");
  var boundary = this.accessor.getBatchAdapter().getBatchBoundary();
  var contentType = "multipart/mixed; boundary=" + boundary;

  var rest = new dcc.http.RestAdapter(this.accessor);
  var res = rest.post(url, this.accessor.getBatchAdapter().getBody(), contentType);

  var parser = new dcc.box.odata.ODataBatchResponseParser();

  this.odataResponses = parser.parse(res.resolvedValue.response, boundary);
};

///**
//* BatchBoundaryを挿入する.
//* @throws ClientException Dao例外
//*/
/**
 * This method is responsible for inserting the BatchBoundary.
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.ODataBatch.prototype.insertBoundary = function() {
  this.accessor.getBatchAdapter().insertBoundary();
};

///**
//* batch実行結果の取得.
//* @return batch実行結果オブジェクト
//*/
/**
 * This method acquires batch execution result.
 * @return {Object} batch execution result object
 */
dcc.box.odata.ODataBatch.prototype.getBatchResponses = function() {
  return this.odataResponses;
};

/**
 * Batch $links Generate link.
 * @param {String} name EntitySet Name
 * @param {String} id Of User data __id
 * @returns {dcc.box.odata.BatchLinksEntity} BatchLinksEntity
 */
dcc.box.odata.ODataBatch.prototype.batchLinksEntity = function(name, id) {
  return new dcc.box.odata.BatchLinksEntity(name, id, this.accessor, this.getPath());
};

///**
//* Batchの$links登録用リンクターゲットオブジェクトを生成する.
//* @param name EntitySet名
//* @param id ユーザデータの __id
//* @return 生成したリンクターゲットオブジェクト
//*/
/**
 * This method generates a Batch of $ links registration link target object.
 * @param {String} name EntitySet name
 * @param {String} id __id Of user data
 * @return {Object} Generated Link target object
 */
dcc.box.odata.ODataBatch.prototype.batchLinksTarget = function(name, id) {
  return new dcc.box.odata.BatchLinksEntity(name, id);
};