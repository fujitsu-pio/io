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
// * ComplexTypePropertyのCRUDのためのクラス.
// */
/**
 * It creates a new object of ComplexTypePropertyManager. This class represents ComplexTypePropertyManager object to
 * perform ComplexTypePropertyManager related operations.
 */
public class ComplexTypePropertyManager extends ODataManager {

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one parameter. It calls its parent constructor internally.
     * @param as Accessor
     */
    public ComplexTypePropertyManager(Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param col Davコレクションオブジェクト
    // */
    /**
     * This is the parameterized constructor with two parameters. It calls its parent constructor internally.
     * @param as Accessor
     * @param col DcCollection
     */
    public ComplexTypePropertyManager(Accessor as, DcCollection col) {
        super(as, col);
    }

    /**
     * This method generates and returns the URL for performing ComplexTypeProperty operations.
     * @return URL value
     */
    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.collection.getPath());
        sb.append("/$metadata/ComplexTypeProperty");
        return sb.toString();
    }

    // /**
    // * ComplexTypePropertyを作成.
    // * @param obj ComplexTypePropertyオブジェクト
    // * @return ComplexTypePropertyオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates a ComplexTypeProperty using ComplexTypeProperty object.
     * @param obj ComplexTypeProperty object
     * @return ComplexTypeProperty object that is created
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public ComplexTypeProperty create(ComplexTypeProperty obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("Name", obj.getName());
        body.put("_ComplexType.Name", obj.getComplexTypeName());
        body.put("Type", obj.getType());
        body.put("Nullable", obj.getNullable());
        body.put("DefaultValue", obj.getDefaultValue());
        body.put("CollectionKind", obj.getCollectionKind());
        JSONObject json = internalCreate(body);
        obj.initialize(this.accessor, json);
        return obj;
    }

    // /**
    // * ComplexTypePropertyを作成.
    // * @param body リクエストボディ
    // * @return 作成したComplexTypePropertyオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates a ComplexTypeProperty using Request Body.
     * @param body Request Body
     * @return ComplexTypeProperty object that is created
     * @throws DaoException Exception thrown
     */
    public ComplexTypeProperty create(HashMap<String, Object> body) throws DaoException {
        JSONObject json = internalCreate(body);
        return new ComplexTypeProperty(accessor, json);
    }

    // /**
    // * ComplexTypePropertyを取得.
    // * @param name 取得対象のComplexTypeProperty名
    // * @param complexTypeName ComplexType名
    // * @return 取得したしたComplexTypePropertyオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method retrieves the details of specified ComplexTypeProperty.
     * @param name ComplexTypeProperty Name
     * @param complexTypeName ComplexType Name
     * @return ComplexTypeProperty object
     * @throws DaoException Exception thrown
     */
    public ComplexTypeProperty retrieve(String name, String complexTypeName) throws DaoException {
        String key = String.format("Name='%s',_ComplexType.Name='%s'", name, complexTypeName);
        JSONObject json = internalRetrieveMultikey(key);
        return new ComplexTypeProperty(accessor, json);
    }

    // /**
    // * ComplexTypePropertyを削除.
    // * @param name 取得対象のComplexTypeProperty名
    // * @param complexTypeName ComplexType名
    // * @throws DaoException DAO例外
    // */
    /**
     * This method deletes the specified ComplexTypeProperty.
     * @param name ComplexTypeProperty Name
     * @param complexTypeName ComplexType Name
     * @throws DaoException Exception thrown
     */
    public void del(String name, String complexTypeName) throws DaoException {
        String key = String.format("Name='%s',_ComplexType.Name='%s'", name, complexTypeName);
        internalDelMultiKey(key, "*");
    }
}
