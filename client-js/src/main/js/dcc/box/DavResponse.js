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
//* @class DAVリソースへアクセスするためのクラス.
//* @constructor
//* @augments dcc.box.DavCollection
//*/
/**
 * It creates a new object dcc.box.DavResponse.
 * @class This class is used to access the DAV resource.
 * @constructor
 * @augments dcc.box.DavCollection
 * @param {dcc.Accessor} as Accessor
 * @param {Object} json
 */
dcc.box.DavResponse = function(as, json) {
  this.initializeProperties(this, as, json);
};
dcc.DcClass.inherit(dcc.box.DavResponse, dcc.box.DavCollection);

///**
//* プロパティを初期化する.
//* @param {dcc.box.DavResponse} self
//* @param {dcc.Accessor} as アクセス主体
//* @param {Object} json JSONオブジェクト
//* @param {?} path
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.box.DavResponse} self
 * @param {dcc.Accessor} as Accessor
 * @param {String} body
 */
dcc.box.DavResponse.prototype.initializeProperties = function(self, as, body) {
  this.uber = dcc.box.DavCollection.prototype;
  this.uber.initializeProperties(self, as);

  if (body !== undefined) {
    self.body = body;
  }
};

///**
//* ボディを取得.
//* @return {?} ボディ
//*/
/**
 * This method is used to return the body.
 * @return {Object} Body
 */
dcc.box.DavResponse.prototype.getBody = function() {
  return this.body;
};
