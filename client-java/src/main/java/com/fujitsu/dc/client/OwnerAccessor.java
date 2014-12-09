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
// * ユニット昇格後のAccessor.
// */
/**
 * It creates a new object of OwnerAccessor. This class represents Accessor of the unit after promotion.
 */
public class OwnerAccessor extends Accessor {

    // CHECKSTYLE:OFF
    /** ユニットレベルAPI へアクセスするためのクラスインスタンス。cell().unit でアクセス. */
    /** Class instance for access to the unit level API. */
    public UnitManager unit;

    // CHECKSTYLE:ON

    // /**
    // * コンストラクタ.
    // * @param dcContext DCContext
    // */
    /**
     * This is the parameterized constructor with one argument calling its parent constructor internally.
     * @param dcContext DCContext object
     */
    public OwnerAccessor(DcContext dcContext) {
        super(dcContext);
    }

    // /**
    // * コンストラクタ.
    // * @param dcContext DCContext
    // * @param as アクセス主体
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the parameterized constructor with two arguments calling its parent constructor internally and setting
     * their class variables.
     * @param dcContext DCContext
     * @param as Accessor
     * @throws DaoException Exception thrown
     */
    public OwnerAccessor(DcContext dcContext, Accessor as) throws DaoException {
        super(dcContext);
        this.setAccessToken(as.getAccessToken());
        this.setAccessType(as.getAccessType());
        this.setCellName(as.getCellName());
        this.setUserId(as.getUserId());
        this.setPassword(as.getPassword());
        this.setSchema(as.getSchema());
        this.setSchemaUserId(as.getSchemaUserId());
        this.setSchemaPassword(as.getSchemaPassword());
        this.setTargetCellName(as.getTargetCellName());
        this.setTransCellToken(as.getTransCellToken());
        this.setTransCellRefreshToken(as.getTransCellRefreshToken());
        this.setBoxSchema(as.getBoxSchema());
        this.setBoxName(as.getBoxName());
        this.setBaseUrl(as.getBaseUrl());
        this.setContext(as.getContext());
        this.setCurrentCell(as.getCurrentCell());
        this.setDefaultHeaders(as.getDefaultHeaders());

        // Unit昇格
        /** Unit promotion. */
        this.owner = true;

        certification();

        this.unit = new UnitManager(this);
    }
}
