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
//* @class ユニット昇格後のAccessor.
//* @constructor
//* @augments dcc.Accessor
//*/
/**
 * It creates a new object dcc.OwnerAccessor.
 * @class This class represents Accessor of the unit after promotion.
 * @constructor
 * @augments dcc.Accessor
 * @param {dcc.DcContext} dcContext
 * @param {dcc.Accessor} as Accessor
 */
dcc.OwnerAccessor = function(dcContext, as) {
  this.initializeProperties(this, dcContext, as);
};
dcc.DcClass.inherit(dcc.OwnerAccessor, dcc.Accessor);

///**
//* プロパティを初期化する.
//* @param {dcc.OwnerAccessor} self
//* @param {?} dcContext コンテキスト
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.OwnerAccessor} self
 * @param {dcc.DcContext} dcContext Context
 * @param {dcc.Accessor} as Accessor
 */
dcc.OwnerAccessor.prototype.initializeProperties = function(self, dcContext, as) {
  this.uber = dcc.Accessor.prototype;
  this.uber.initializeProperties(self, dcContext);

  if (as !== undefined) {
    self.setAccessToken(as.getAccessToken());
    self.setAccessType(as.getAccessType());
    self.setCellName(as.getCellName());
    self.setUserId(as.getUserId());
    self.setPassword(as.getPassword());
    self.setSchema(as.getSchema());
    self.setSchemaUserId(as.getSchemaUserId());
    self.setSchemaPassword(as.getSchemaPassword());
    self.setTargetCellName(as.getTargetCellName());
    self.setTransCellToken(as.getTransCellToken());
    self.setTransCellRefreshToken(as.getTransCellRefreshToken());
    self.setBoxSchema(as.getBoxSchema());
    self.setBoxName(as.getBoxName());
    self.setBaseUrl(as.getBaseUrl());
    self.setContext(as.getContext());
    self.setCurrentCell(as.getCurrentCell());
    self.setDefaultHeaders(as.getDefaultHeaders());
  }

  // Unit昇格
  /** Unit promotion. */
  self.owner = true;

  if (dcContext !== undefined && as !== undefined) {
    self.authenticate();
    self.unit = new dcc.UnitManager(this);
  }
};

