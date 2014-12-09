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
//* ＄Batchアクセスのためのリクエストを作成するクラス..
//* @class Represents BatchAdapter.
//*/
/**
 * It creates a new object dcc.box.odata.BatchAdapter.
 * @class This class is used to create a request for $ Batch access .
 * @constructor
 * @param {dcc.Accessor} Accessor
 */
dcc.box.odata.BatchAdapter = function(as) {
  this.initializeProperties(this, as);
};

///**
//* プロパティを初期化する.
//*/
/**
 * This method initializes the properties of this class.
 * @param {Object} self
 * @param {dcc.Accessor} as Accessor
 */
dcc.box.odata.BatchAdapter.prototype.initializeProperties = function(self, as) {
  this.accessor = as;
  this.batchBoundary = "batch_" + this.getUUID();
  this.changeSet = null;
  this.batch = new dcc.box.odata.Batch(this.batchBoundary);
};

/**
 * This method returns the reference to the accessor.
 * @return {dcc.Accessor} the accessor
 */
dcc.box.odata.BatchAdapter.prototype.getAccessor = function() {
  return this.accessor;
};

/**
 * This method returns the batch boundary.
 * @return {String} the batchBoundary
 */
dcc.box.odata.BatchAdapter.prototype.getBatchBoundary = function() {
  return this.batchBoundary;
};

/**
 * This method appends the value to existing or new ChangeSet.
 * @param {String} value
 * @returns {String} value
 */
dcc.box.odata.BatchAdapter.prototype.appendChangeSet = function(value) {
  if (null === this.changeSet) {
    this.changeSet = new dcc.box.odata.ChangeSet("changeset_" + this.getUUID(), this.batchBoundary);
  }
  this.changeSet.append(value);
};

/**
 * This method appends value of ChangeSet to Batch and overwrites ChangeSet. 
 */
dcc.box.odata.BatchAdapter.prototype.writeChangeSet = function() {
  if ( (null !== this.changeSet) && (undefined !== this.changeSet)) {
    this.batch.append(this.changeSet.get());
    this.changeSet = null;
  }
};

///**
//* BatchBoundaryを挿入する.
//* @throws ClientException Dao例外
//*/
/**
 * This method inserts the BatchBoundary.
 * @throws {dcc.ClientException} Dao exception
 */
dcc.box.odata.BatchAdapter.prototype.insertBoundary = function() {
  this.writeChangeSet();
};

/**
 * This method appends the ChangeSet and returns DCBatchResponse.
 * @param {String} url
 * @param {String} accept
 * @param {String} etag
 * @returns {dcc.box.odata.DcBatchResponse} Response
 */
dcc.box.odata.BatchAdapter.prototype.get = function(url, accept, etag) {
  // 溜めたChangeSetを吐き出す
  /** Update ChangeSet. */
  this.writeChangeSet();
  var cmd = new dcc.box.odata.Command(this.batchBoundary);
  cmd.method = "GET";
  cmd.url = url;
//cmd.addHeader("Accept-Encoding", "gzip");
  cmd.addHeader("Accept", accept);
  cmd.etag = etag;
  this.batch.append(cmd.get());
  return new dcc.box.odata.DcBatchResponse();
};

/**
 * This method retrieves the ChangeSet.
 * @param {String} url
 * @returns {dcc.box.odata.DcBatchResponse} Response
 */
dcc.box.odata.BatchAdapter.prototype.head = function(url) {
  // 溜めたChangeSetを吐き出す
  this.writeChangeSet();
  return this.get(url, "application/json", null);
};

/**
 * This method updates the ChangeSet.
 * @param {String} url
 * @param {String} data
 * @param {String} etag
 * @param {String} contentType
 * @param {Array} map
 * @returns {dcc.box.odata.DcBatchResponse} response
 */
dcc.box.odata.BatchAdapter.prototype.put = function(url, data, etag, contentType, map) {
  var cmd = new dcc.box.odata.Command();
  cmd.method = "PUT";
  cmd.url = url;
  cmd.addHeader("Content-Type", contentType);
  cmd.etag = etag;
  cmd.setBody(data);
  if( (map !== undefined) && (map !== null)){
    for (var entry in map) {
      cmd.addHeader(entry, map[entry]);
    }
  }
  this.appendChangeSet(cmd.get());
  return new dcc.box.odata.DcBatchResponse();
};

/**
 * This method creates a ChangeSet.
 * @param {String} url
 * @param {String} data
 * @param {String} contentType
 * @param {Array} map
 * @returns {dcc.box.odata.DcBatchResponse} response
 */
dcc.box.odata.BatchAdapter.prototype.post = function(url, data, contentType, map) {
  var cmd = new dcc.box.odata.Command();
  cmd.method = "POST";
  cmd.url = url;
  cmd.addHeader("Content-Type", contentType);
  cmd.setBody(data);
  if( (map !== undefined) && (map !== null)){
    for (var entry in map) {
      cmd.addHeader(entry, map[entry]);
    }
  }
  this.appendChangeSet(cmd.get());
  return new dcc.box.odata.DcBatchResponse();
};

/**
 * This method deletes the ChangeSet.
 * @param {String} url
 * @param {String} etag
 */
dcc.box.odata.BatchAdapter.prototype.del = function(url, etag) {
  var cmd = new dcc.box.odata.Command();
  cmd.method = "DELETE";
  cmd.url = url;
  cmd.etag = etag;
  this.appendChangeSet(cmd.get());
};

///**
//* $Batchのボディ情報を取得する.
//* @return Batch登録するボディ.
//* @throws ClientException DAO例外
//*/
/**
 * This method gets the body of information $ Batch.
 * @return {dcc.box.odata.DcBatchResponse} Body to Batch registration.
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.BatchAdapter.prototype.getBody = function() {
  // 溜めたChangeSetを吐き出す
  /** Update ChangeSet. */
  this.writeChangeSet();
  return this.batch.get();
};

///**
//* レスポンスボディを受けるMERGEメソッド.
//* @param url リクエスト対象URL
//* @param data 書き込むデータ
//* @param etag ETag
//* @param contentType CONTENT-TYPE値
//* @return DcResponseオブジェクト
//* @throws ClientException DAO例外
//*/
//dcc.box.odata.BatchAdapter.prototype.merge = function(url, data, etag, contentType) {
////TODO バッチ経由のMERGEメソッドの処理を実装する
//var res = null;
//return res;
//};

///**
//* UUIDを返却する
//* @returns {String}
//*/
/**
 * This method returns the UUID.
 * @returns {String} UUID
 */
dcc.box.odata.BatchAdapter.prototype.getUUID = function() {
  var S4 = function() {
    return (((1+Math.random())*0x10000)|0).toString(16).substring(1);
  };
  return (S4()+S4()+"-"+S4()+"-"+S4()+"-"+S4()+"-"+S4()+S4() +S4());
};

///**
//* レスポンスボディをJSONで取得.
//* @return JSONオブジェクト
//* @throws ClientException DAO例外
//*/
/**
 * This method returns response body in JSON format.
 * @return {Object} JSON object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.BatchAdapter.prototype.bodyAsJson = function() {
  return {"d":{"results":[]}};
};