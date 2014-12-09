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
//* @class UNITレベルAPI/SAMレベルAPI.
//* @constructor
//*/
/**
 * It creates a new object dcc.UnitManager.
 * @class UNIT level API / SAM level API.
 * @constructor
 * @param {dcc.Accessor} as Accessor
 */
dcc.UnitManager = function(as) {
  this.initializeProperties(this, as);
};

///**
//* プロパティを初期化する.
//* @param {dcc.UnitManager} self
//* @param {dcc.Accessor} as アクセス主体
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.UnitManager} self
 * @param {dcc.Accessor} as Accessor
 */
dcc.UnitManager.prototype.initializeProperties = function(self, as) {
  if (as !== undefined) {
//  /** アクセス主体. */
    /** Accessor. */
    self.accessor = as.clone();
//  /** CellのCRUDを行う. */
    /** CellManager object to perform Cell related CRUD. */
    self.cell = new dcc.unitctl.CellManager(as);
  }
};

///**
//* 指定した名称のCellオブジェクトを生成して返す.
//* @param {string} cellName セル名
//* @return {dcc.unitctl.Cell} 生成したCellオブジェクトのインスタンス
//* @throws {ClientException} DAO例外
//*/
/**
 * This method creates and returns a Cell object with the specified name.
 * @param {string} cellName Cellname
 * @return {dcc.unitctl.Cell} Cell object
 * @throws {dcc.ClientException} DAO exception
 */
dcc.UnitManager.prototype.cellCtl = function(cellName) {
  this.accessor.setCellName(cellName);
  return new dcc.unitctl.Cell(this.accessor, cellName);
};
