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
// * ComplexTypeのCRUDのためのクラス.
// */
/**
 * It creates a new object of ComplexTypeManager. This class performs CRUD operations for CmplexType.
 */
public class ComplexTypeManager extends ODataManager {

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one parameter. It calls its parent constructor internally.
     * @param as Accessor
     */
    public ComplexTypeManager(Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param col Davコレクションオブジェクト
    // */
    /**
     * This is the parameterized constructor with one parameter. It calls its parent constructor internally.
     * @param as Accessor
     * @param col DcCollection
     */
    public ComplexTypeManager(Accessor as, DcCollection col) {
        super(as, col);
    }

    /**
     * This method generates and returns the URL for performing ComplexType operations.
     * @return URL value
     */
    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.collection.getPath());
        sb.append("/$metadata/ComplexType");
        return sb.toString();
    }

    // /**
    // * ComplexTypeを作成.
    // * @param obj ComplexTypeオブジェクト
    // * @return ComplexTypeオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates a ComplexType using ComplexType object.
     * @param obj ComplexType object
     * @return ComplexType object that is created
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public ComplexType create(ComplexType obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("Name", obj.getName());
        JSONObject json = internalCreate(body);
        obj.initialize(this.accessor, json);
        return obj;
    }

    // /**
    // * ComplexTypeを作成.
    // * @param body リクエストボディ
    // * @return 作成したComplexTypeオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates a ComplexType using request body.
     * @param body Request Body
     * @return ComplexType object that is created
     * @throws DaoException Exception thrown
     */
    public ComplexType create(HashMap<String, Object> body) throws DaoException {
        JSONObject json = internalCreate(body);
        return new ComplexType(accessor, json);
    }

    // /**
    // * ComplexTypeを取得.
    // * @param name 取得対象のComplexType名
    // * @return 取得したしたComplexTypeオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method retrieves the details of specified ComplexType.
     * @param name ComplexType Name
     * @return ComplexType object
     * @throws DaoException Exception thrown
     */
    public ComplexType retrieve(String name) throws DaoException {
        String key = String.format("Name='%s'", name);
        JSONObject json = internalRetrieveMultikey(key);
        return new ComplexType(accessor, json);
    }

    // /**
    // * ComplexTypeを削除.
    // * @param name 取得対象のComplexType名
    // * @throws DaoException DAO例外
    // */
    /**
     * This method deletes the specified ComplexType.
     * @param name ComplexType Name
     * @throws DaoException Exception thrown
     */
    public void del(String name) throws DaoException {
        String key = String.format("Name='%s'", name);
        internalDelMultiKey(key, "*");
    }
}
