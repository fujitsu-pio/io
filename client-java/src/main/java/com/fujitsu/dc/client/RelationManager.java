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
// * RelationのCRUDのためのクラス.
// */
/**
 * It creates a new object of RelationManager. This class performs CRUD operations for Relation object.
 */
public class RelationManager extends ODataManager {

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument and calling its parent constructor internally.
     * @param as Accessor
     */
    public RelationManager(Accessor as) {
        super(as);
    }

    /**
     * This method creates and returns the URL for performing Relation operations.
     * @return URL value
     */
    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(accessor.getCurrentCell().getUrl());
        sb.append("__ctl/Relation");
        return sb.toString();
    }

    // /**
    // * Relationを作成.
    // * @param obj Relationオブジェクト
    // * @return Relationオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs create operation using Relation object.
     * @param obj Relation object
     * @return Relation object that is created
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public Relation create(Relation obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("Name", obj.getName());
        body.put("_Box.Name", obj.getBoxName());
        JSONObject json = internalCreate(body);
        obj.initialize(this.accessor, json);
        return obj;
    }

    // /**
    // * Relationを作成.
    // * @param body リクエストボディ
    // * @return 作成したBoxオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs create operation using Request Body.
     * @param body Request Body
     * @return Relation object that is created
     * @throws DaoException Exception thrown
     */
    public Relation create(HashMap<String, Object> body) throws DaoException {
        JSONObject json = (JSONObject) internalCreate(body);
        return new Relation(accessor, json);
    }

    // /**
    // * Relationを取得.
    // * @param relationName 取得対象のRelation名
    // * @return 取得したしたAccountオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method retrieves the Relation when box name is not specified i.e. for main/default box.
     * @param relationName RelationName
     * @return Relation object
     * @throws DaoException Exception thrown
     */
    public Relation retrieve(String relationName) throws DaoException {
        JSONObject json = internalRetrieve(relationName);
        return new Relation(accessor, json);
    }

    // /**
    // * Relationを取得(複合キー).
    // * @param relationName 取得対象のRelation名
    // * @param boxName 取得対象のBox名
    // * @return 取得したしたAccountオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method retrieves the Relation when box name is specified.
     * @param relationName RelationName
     * @param boxName BoxName
     * @return Relation object
     * @throws DaoException Exception thrown
     */
    public Relation retrieve(String relationName, String boxName) throws DaoException {
        String key;
        key = String.format("Name='%s',_Box.Name='%s'", relationName, boxName);
        JSONObject json = this.internalRetrieveMultikey(key);
        return new Relation(accessor, json);
    }

    // /**
    // * Relationを削除(複合キー).
    // * @param relationName 削除対象のRelation名
    // * @param boxName 削除対象のBox名
    // * @throws DaoException DAO例外
    // */
    /**
     * This method deletes the Relation for specified relation and box name.
     * @param relationName RelationName
     * @param boxName BoxName
     * @throws DaoException Exception thrown
     */
    public void del(String relationName, String boxName) throws DaoException {
        String key;
        if (null == boxName) {
            key = String.format("Name='%s'", relationName);
        } else {
            key = String.format("Name='%s',_Box.Name='%s'", relationName, boxName);
        }
        internalDelMultiKey(key, "*");
    }
}
