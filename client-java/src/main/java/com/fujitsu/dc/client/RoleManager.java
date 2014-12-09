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
// * RoleのCRUDのためのクラス.
// */
/**
 * It creates a new object of RoleManager. This class performs CRUD operations for Role object.
 */
public class RoleManager extends ODataManager {
    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument calling its parent constructor internally.
     * @param as Accessor
     */
    public RoleManager(Accessor as) {
        super(as);
    }

    /**
     * This method creates and returns the URL for performing Role related operations.
     * @return URL value
     */
    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(accessor.getCurrentCell().getUrl());
        sb.append("__ctl/Role");
        return sb.toString();
    }

    // /**
    // * Roleを作成.
    // * @param obj Roleオブジェクト
    // * @return Roleオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates a Role using a Role object.
     * @param obj Role object
     * @return Role object that is created
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public Role create(Role obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("Name", obj.getName());
        body.put("_Box.Name", obj.getBoxName());
        JSONObject json = internalCreate(body);
        obj.initialize(this.accessor, json);
        return obj;
    }

    // /**
    // * Roleを作成.
    // * @param body リクエストボディ
    // * @return 作成したBoxオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates a Role using Request Body.
     * @param body Request Body
     * @return Role object that is created
     * @throws DaoException Exception thrown
     */
    public Role create(HashMap<String, Object> body) throws DaoException {
        JSONObject json = (JSONObject) internalCreate(body);
        return new Role(accessor, json);
    }

    // /**
    // * Roleを取得.
    // * @param roleName 取得対象のRole名
    // * @return 取得したしたAccountオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method retrieves a Role object without specifying the box name i.e. for default/main box.
     * @param roleName RoleName
     * @return Role object
     * @throws DaoException Exception thrown
     */
    public Role retrieve(String roleName) throws DaoException {
        JSONObject json = internalRetrieve(roleName);
        return new Role(accessor, json);
    }

    // /**
    // * Roleを取得(複合キー).
    // * @param roleName 取得対象のRole名
    // * @param boxName 取得対象のBox名
    // * @return 取得したしたAccountオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method retrieves a Role object for the specific box name.
     * @param roleName RoleName
     * @param boxName BoxName
     * @return Role object
     * @throws DaoException Exception thrown
     */
    public Role retrieve(String roleName, String boxName) throws DaoException {
        String key;
        key = String.format("Name='%s',_Box.Name='%s'", roleName, boxName);
        JSONObject json = this.internalRetrieveMultikey(key);
        return new Role(accessor, json);
    }

    // /**
    // * Roleを削除(複合キー).
    // * @param roleName 削除対象のRole名
    // * @param boxName 削除対象のBox名
    // * @throws DaoException DAO例外
    // */
    /**
     * This method deletes the specified Role object.
     * @param roleName RoleName
     * @param boxName BoxName
     * @throws DaoException Exception thrown
     */
    public void del(String roleName, String boxName) throws DaoException {
        String key;
        key = String.format("Name='%s',_Box.Name='%s'", roleName, boxName);
        internalDelMultiKey(key, "*");
    }
}
