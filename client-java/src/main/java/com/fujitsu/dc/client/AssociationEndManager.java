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
// * AssociationEndのCRUDのためのクラス.
// */
/**
 * It creates a new object of AssociationEndManager. This class performs the CRUD operations for Association End.
 */
public class AssociationEndManager extends ODataManager {

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor taking one parameter and calling its parent constructor.
     * @param as Accessor
     */
    public AssociationEndManager(Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param col Davコレクションオブジェクト
    // */
    /**
     * This is the parameterized constructor taking two parameters and calling its parent constructor.
     * @param as Accessor
     * @param col DcCollection
     */
    public AssociationEndManager(Accessor as, DcCollection col) {
        super(as, col);
    }

    /**
     * This method is used to craete and return the URL for performing ACL operations.
     * @return URL value
     */
    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.collection.getPath());
        sb.append("/$metadata/AssociationEnd");
        return sb.toString();
    }

    // /**
    // * AssociationEndを作成.
    // * @param obj AssociationEndオブジェクト
    // * @return AssociationEndオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates an AssociationEnd from AssociationEnd object.
     * @param obj AssociationEnd object
     * @return AssociationEnd object
     * @throws DaoException DAO Exception thrown
     */
    @SuppressWarnings("unchecked")
    public AssociationEnd create(AssociationEnd obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("Name", obj.getName());
        body.put("_EntityType.Name", obj.getEntityTypeName());
        body.put("Multiplicity", obj.getMultiplicity());
        JSONObject json = internalCreate(body);
        obj.initialize(this.accessor, json);
        return obj;
    }

    // /**
    // * AssociationEndを作成.
    // * @param body リクエストボディ
    // * @return 作成したBoxオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates an AssociationEnd from body.
     * @param body Request body
     * @return AssociationEnd object that is created
     * @throws DaoException DAO Exception thrown
     */
    public AssociationEnd create(HashMap<String, Object> body) throws DaoException {
        JSONObject json = internalCreate(body);
        return new AssociationEnd(accessor, json);
    }

    // /**
    // * AssociationEndを取得.
    // * @param name 取得対象のAssociation名
    // * @param entityTypeName EntityType名
    // * @return 取得したしたAssociationEndオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method retrieves the specified AssociationEnd.
     * @param name Association Name
     * @param entityTypeName EntityType Name
     * @return AssociationEnd object
     * @throws DaoException DAO Exception thrown
     */
    public AssociationEnd retrieve(String name, String entityTypeName) throws DaoException {
        String key = String.format("Name='%s',_EntityType.Name='%s'", name, entityTypeName);
        JSONObject json = internalRetrieveMultikey(key);
        return new AssociationEnd(accessor, json);
    }

    // /**
    // * AssociationEndを削除.
    // * @param name 取得対象のAssociation名
    // * @param entityTypeName EntityType名
    // * @throws DaoException DAO例外
    // */
    /**
     * This method deletes the specified AssociationEnd.
     * @param name Association Name
     * @param entityTypeName EntityType Name
     * @throws DaoException DAO Exception thrown
     */
    public void del(String name, String entityTypeName) throws DaoException {
        String key = String.format("Name='%s',_EntityType.Name='%s'", name, entityTypeName);
        internalDelMultiKey(key, "*");
    }
}
