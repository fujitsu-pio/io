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
// * PropertyのCRUDのためのクラス.
// */
/**
 * It creates a new object of PropertyManager. This Class is used for performing CRUD operations of Property.
 */
public class PropertyManager extends ODataManager {

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument and calling its parent constructor internally.
     * @param as Accessor
     */
    public PropertyManager(Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param col Davコレクションオブジェクト
    // */
    /**
     * This is the parameterized constructor with two arguments and calling its parent constructor internally.
     * @param as Accessor
     * @param col DcCollection
     */
    public PropertyManager(Accessor as, DcCollection col) {
        super(as, col);
    }

    /**
     * This method generates and returns the URL for performing Property related operations.
     * @return URL value
     */
    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.collection.getPath());
        sb.append("/$metadata/Property");
        return sb.toString();
    }

    // /**
    // * Propertyを作成.
    // * @param obj Propertyオブジェクト
    // * @return Propertyオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * The purpose of this function is to create Property using Property object.
     * @param obj Property object
     * @return Property object that is created
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public Property create(Property obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("Name", obj.getName());
        body.put("_EntityType.Name", obj.getEntityTypeName());
        body.put("Type", obj.getType());
        body.put("Nullable", obj.getNullable());
        body.put("DefaultValue", obj.getDefaultValue());
        body.put("CollectionKind", obj.getCollectionKind());
        body.put("IsKey", obj.getIsKey());
        body.put("UniqueKey", obj.getUniqueKey());
        JSONObject json = internalCreate(body);
        obj.initialize(this.accessor, json);
        return obj;
    }

    // /**
    // * Propertyを作成.
    // * @param body リクエストボディ
    // * @return 作成したPropertyオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * The purpose of this function is to create Property using Request Body.
     * @param body Request Body
     * @return Property object that is created
     * @throws DaoException Exception thrown
     */
    public Property create(HashMap<String, Object> body) throws DaoException {
        JSONObject json = internalCreate(body);
        return new Property(accessor, json);
    }

    // /**
    // * Propertyを取得.
    // * @param name 取得対象のProperty名
    // * @param entityTypeName EntityType名
    // * @return 取得したしたPropertyオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * The purpose of this function is to retrieve Property.
     * @param name PropertyName
     * @param entityTypeName EntityTypeName
     * @return Property object
     * @throws DaoException Exception thrown
     */
    public Property retrieve(String name, String entityTypeName) throws DaoException {
        String key = String.format("Name='%s',_EntityType.Name='%s'", name, entityTypeName);
        JSONObject json = internalRetrieveMultikey(key);
        return new Property(accessor, json);
    }

    // /**
    // * Propertyを削除.
    // * @param name 取得対象のProperty名
    // * @param entityTypeName EntityType名
    // * @throws DaoException DAO例外
    // */
    /**
     * The purpose of this function is to delete Property.
     * @param name PropertyName
     * @param entityTypeName EntityTypeName
     * @throws DaoException Exception thrown
     */
    public void del(String name, String entityTypeName) throws DaoException {
        String key = String.format("Name='%s',_EntityType.Name='%s'", name, entityTypeName);
        internalDelMultiKey(key, "*");
    }
}
