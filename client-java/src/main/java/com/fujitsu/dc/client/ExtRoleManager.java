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

import com.fujitsu.dc.client.utils.Utils;

///**
// * ExtRoleのCRUDのためのクラス.
// */
/**
 * It creates a new object of ExtRoleManager. This class performs CRUD operations for External Role.
 */
public class ExtRoleManager extends ODataManager {

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument calling its parent constructor internally.
     * @param as Accessor
     */
    public ExtRoleManager(Accessor as) {
        super(as);
    }

    /**
     * This method generates and returns the URL for ExtRole.
     * @return URL value
     */
    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(accessor.getCurrentCell().getUrl());
        sb.append("__ctl/ExtRole");
        return sb.toString();
    }

    // /**
    // * ExtRoleを作成.
    // * @param obj ExtRoleオブジェクト
    // * @return ExtRoleオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to create an ExtRole using an ExtRole object.
     * @param obj ExtRole object
     * @return ExtRole object that is created
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public ExtRole create(ExtRole obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("ExtRole", obj.getName());
        body.put("_Relation.Name", obj.getRelationName());
        body.put("_Relation._Box.Name", obj.getRelationBoxName());
        JSONObject json = internalCreate(body);
        obj.initialize(this.accessor, json);
        return obj;
    }

    // /**
    // * ExtRoleを作成.
    // * @param body リクエストボディ
    // * @return 作成したExtRoleオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to create an ExtRole using request body.
     * @param body Request Body
     * @return ExtRole object that is created
     * @throws DaoException Exception thrown
     */
    public ExtRole create(HashMap<String, Object> body) throws DaoException {
        JSONObject json = (JSONObject) internalCreate(body);
        return new ExtRole(accessor, json);
    }

    // /**
    // * ExtRoleを取得(複合キー).
    // * @param extRole 取得対象のExtRole
    // * @param relationName 取得対象のRelation名
    // * @param relationBoxName 取得対象のRelationBox名
    // * @return 取得したしたAccountオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method retrieves the specified ExtRole.
     * @param extRole ExtRole object
     * @param relationName Relation Name
     * @param relationBoxName RelationBox Name
     * @return ExtRole object
     * @throws DaoException Exception thrown
     */
    public ExtRole retrieve(String extRole, String relationName, String relationBoxName) throws DaoException {

        String key = createKey(Utils.escapeURI(extRole), relationName, relationBoxName);
        JSONObject json = this.internalRetrieveMultikey(key);
        return new ExtRole(accessor, json);
    }

    // /**
    // * ExtRoleを削除(複合キー).
    // * @param extRole 削除対象のExtRole
    // * @param relationName 削除対象のRelation名
    // * @param relationBoxName 削除対象のRelationBox名
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to delete the specified ExtRole.
     * @param extRole ExtRole object
     * @param relationName Relation Name
     * @param relationBoxName RelationBox Name
     * @throws DaoException Exception thrown
     */
    public void del(String extRole, String relationName, String relationBoxName) throws DaoException {
        String key = createKey(Utils.escapeURI(extRole), relationName, relationBoxName);
        internalDelMultiKey(key, "*");
    }

    /**
     * This method is used to create the key for performing ExtRole operations.
     * @param extRole value
     * @param relationName value
     * @param relationBoxName value
     * @return Key value
     */
    private String createKey(String extRole, String relationName, String relationBoxName) {
        StringBuilder sb = new StringBuilder();
        sb.append("ExtRole='").append(extRole).append("'");
        if (relationName != null) {
            sb.append(",_Relation.Name='").append(relationName).append("'");
        }
        if (relationBoxName != null) {
            sb.append(",_Relation._Box.Name='").append(relationBoxName).append("'");
        }
        return sb.toString();
    }

    // /**
    // * ODataデータを削除.
    // * @param extRole 削除するODataデータのextRole
    // * @param etag ETag値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to delete the specified ExtRole.
     * @param extRole ExtRole object
     * @param etag ETag Value
     * @throws DaoException Exception thrown
     */
    public void del(String extRole, String etag) throws DaoException {
        this.internalDel(Utils.escapeURI(extRole), etag);
    }

    // /**
    // * ODataデータを更新.
    // * @param extRole 更新対象のExtRole
    // * @param relationName 更新対象のRelation名
    // * @param relationBoxName 更新対象のRelationBox名
    // * @param body PUTするリクエストボディ
    // * @param etag ETag値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to update the specified ExtRole.
     * @param extRole ExtRole object
     * @param relationName Relation Name
     * @param relationBoxName RelationBox Name
     * @param body Request Body to be PUT
     * @param etag ETag Value
     * @throws DaoException Exception thrown
     */
    public void update(String extRole,
            String relationName,
            String relationBoxName,
            HashMap<String, Object> body,
            String etag) throws DaoException {
        String key = createKey(Utils.escapeURI(extRole), relationName, relationBoxName);
        this.internalUpdateMultiKey(key, body, etag);
    }

}
