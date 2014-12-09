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
// * EntityTypeのCRUDのためのクラス.
// */
/**
 * It creates a new object of EntityTypeManager. This class performs The CRUD operations for EntityType.
 */
public class EntityTypeManager extends ODataManager {

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param col Davコレクションオブジェクト
    // */
    /**
     * /** This is the parameterized constructor with two arguments and calling its parent constructor internally.
     * @param as Accessor
     * @param col DcCollection object
     */
    public EntityTypeManager(Accessor as, DcCollection col) {
        super(as, col);
    }

    /**
     * Thi smethod generates and returns the URL for performing operations on EntityType.
     * @return URL value
     */
    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.collection.getPath());
        sb.append("/$metadata/EntityType");
        return sb.toString();
    }

    // /**
    // * EntityTypeを作成.
    // * @param obj EntityTypeオブジェクト
    // * @return EntityTypeオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates an EntityType from EntityType object.
     * @param obj EntityType object
     * @return EntityType object that is created
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public EntityType create(EntityType obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("Name", obj.getName());
        JSONObject json = internalCreate(body);
        obj.initialize(this.accessor, json);
        return obj;
    }

    // /**
    // * EntityTypeを作成.
    // * @param body リクエストボディ
    // * @return 作成したBoxオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates an EntityType from request body.
     * @param body Request Body
     * @return EntityType object that is created
     * @throws DaoException Exception thrown
     */
    public EntityType create(HashMap<String, Object> body) throws DaoException {
        JSONObject json = (JSONObject) internalCreate(body);
        return new EntityType(accessor, json);
    }

    // /**
    // * Boxを取得.
    // * @param name 取得対象のbox名
    // * @return 取得したしたBoxオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method retrieves a specified EntityType.
     * @param name EntityTypeName
     * @return EntityType object
     * @throws DaoException Exception thrown
     */
    public EntityType retrieve(String name) throws DaoException {
        JSONObject json = internalRetrieve(name);
        return new EntityType(accessor, json);
    }
}
