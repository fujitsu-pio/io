/**
 * personium.io
 * Copyright 2014 FUJITSU LIMITED
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.fujitsu.dc.client;

///**
// * UNITレベルAPI/SAMレベルAPI.
// */
/**
 * It creates a new object of UnitManager. UNIT level API / SAM level API.
 */
public class UnitManager {
    // /** アクセス主体. */
    /** Reference to Accessor. */
    private Accessor accessor;
    // /** CellのCRUDを行う. */
    /** Reference to CellManager to perform Cell related CRUD. */
    public CellManager cell;

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument and initializes its class variables.
     * @param as アクセス主体
     */
    public UnitManager(Accessor as) {
        this.accessor = as.clone();
        this.cell = new CellManager(this.accessor);
    }
}
