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
//* Batchのレスポンス型.
//* @class Represents BatchResponse. 
//*/
/**
 * It creates a new object dcc.box.odata.DcBatchResponse.
 * @class This class represents the response class for Batch. 
 * @constructor
 */
dcc.box.odata.DcBatchResponse = function() {
};

///**
//* レスポンスボディをJSONで取得.
//* @return JSONオブジェクト
//* @throws ClientException DAO例外
//*/
/**
 * This method returns the response body in JSON format.
 * @return {Object} JSON object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.DcBatchResponse.prototype.bodyAsJson = function() {
  return {"d":{"results":[]}};
};
