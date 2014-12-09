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

import java.util.HashMap;

import org.json.simple.JSONObject;

///**
// * ExtCellのCRUDのためのクラス.
// */
/**
 * It creates a new object of ExtCellManager. This class performs CRUD operations for External Cell.
 */
public class ExtCellManager extends ODataManager {

    /**
     * This is the parameterized constructor with one argument calling its parent constructor internally.
     * @param as Accessor
     */
    public ExtCellManager(Accessor as) {
        super(as);
    }

    /**
     * This method generates and returns the URL for ExtCell.
     * @return URL value
     */
    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(accessor.getCurrentCell().getUrl());
        sb.append("__ctl/ExtCell");
        return sb.toString();
    }

    // /**
    // * ExtCellを作成.
    // * @param obj ExtCellオブジェクト
    // * @return ExtCellオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to create an ExtCell using an ExtCell object.
     * @param obj ExtCell object
     * @return ExtCell object that is created
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public ExtCell create(ExtCell obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("Url", obj.getUrl());
        JSONObject json = internalCreate(body);
        obj.initialize(this.accessor, json);
        return obj;
    }

    // /**
    // * ExtCellを作成.
    // * @param body リクエストボディ
    // * @return 作成したExtCellオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to create an ExtCell using Requset Body.
     * @param body Request Body
     * @return ExtCell object that is created
     * @throws DaoException Exception thrown
     */
    public ExtCell create(HashMap<String, Object> body) throws DaoException {
        JSONObject json = (JSONObject) internalCreate(body);
        return new ExtCell(accessor, json);
    }

    // /**
    // * ExtCellを取得.
    // * @param roleId 取得対象のRoleId
    // * @return 取得したしたExtCellオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to fetch the specified ExtCell.
     * @param roleId Role ID
     * @return ExtCell object
     * @throws DaoException Exception thrown
     */
    public ExtCell retrieve(String roleId) throws DaoException {
        JSONObject json = internalRetrieve(roleId);
        return new ExtCell(accessor, json);
    }
}
