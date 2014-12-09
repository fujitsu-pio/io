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

import com.fujitsu.dc.client.http.DcResponse;
import com.fujitsu.dc.client.http.IRestAdapter;
import com.fujitsu.dc.client.http.RestAdapter;
import com.fujitsu.dc.client.http.RestAdapterFactory;
import com.fujitsu.dc.client.utils.Utils;

///**
// * OData関連の各機能を生成/削除するためのクラスの抽象クラス.
// */
/**
 * It creates a new object of EntitySet. This is the abstract class for performing the merge functions.
 */
public class EntitySet extends ODataManager {

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument and calls its parent constructor internally.
     * @param as Accessor
     */
    public EntitySet(Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param col DAVコレクション
    // */
    /**
     * This is the parameterized constructor with two arguments and calls its parent constructor internally.
     * @param as Accessor
     * @param col DcCollection
     */
    public EntitySet(Accessor as, DcCollection col) {
        super(as, col);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param col DAVコレクション
    // * @param name EntitySet名
    // */
    /**
     * This is the parameterized constructor with three arguments and calls its parent constructor internally.
     * @param as Accessor
     * @param col DcCollection
     * @param name EntitySet Name
     */
    public EntitySet(Accessor as, DcCollection col, String name) {
        super(as, col, name);
    }

    // /**
    // * ユーザーデータを部分更新.
    // * @param id 部分更新するOdataデータのID値
    // * @param body 部分更新するリクエストボディ
    // * @param etag Etag値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used for the partial update of user data.
     * @param id ID value of the data
     * @param body Request body
     * @param etag Etag value
     * @throws DaoException Exception thrown
     */
    private void internalMerge(String id, HashMap<String, Object> body, String etag) throws DaoException {
        String url = this.getUrl() + "('" + id + "')";
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        rest.merge(url, JSONObject.toJSONString(body), etag, RestAdapter.CONTENT_TYPE_JSON);
    }

    // /**
    // * ユーザーデータを部分更新.
    // * @param id 対象となるID値
    // * @param body PUTするリクエストボディ
    // * @param etag ETag値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used for the partial update of user data.
     * @param id ID value of the data
     * @param body Request body to be PUT
     * @param etag ETag value
     * @throws DaoException Exception thrown
     */
    public void merge(String id, HashMap<String, Object> body, String etag) throws DaoException {
        this.internalMerge(id, body, etag);
    }

    // /**
    // * ユーザーデータを部分更新.
    // * @param id 対象となるID値
    // * @param body PUTするリクエストボディ
    // * @param etag ETag値
    // * @return 部分更新したEntityオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used for the partial update of user data.
     * @param id ID value of the data
     * @param body Request body to be PUT
     * @param etag ETag value
     * @return Partially updated Entity object
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public Entity mergeAsEntity(String id, HashMap<String, Object> body, String etag) throws DaoException {
        String url = this.getUrl() + "('" + id + "')";
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        DcResponse res = rest.merge(url, JSONObject.toJSONString(body), etag, RestAdapter.CONTENT_TYPE_JSON);
        JSONObject json = new JSONObject();
        json.putAll(body);
        Entity entity = new Entity(accessor, json);
        entity.setResHeaders(res.getHeaderList());
        return entity;
    }

    // /**
    // * ユーザーデータを作成.
    // * @param body リクエストボディ
    // * @return 作成したEntityオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to create the user data.
     * @param body Request body
     * @return Entity object that is created
     * @throws DaoException Exception thrown
     */
    public Entity createAsEntity(HashMap<String, Object> body) throws DaoException {
        String url = this.getUrl();
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        DcResponse res = rest.post(url, JSONObject.toJSONString(body), RestAdapter.CONTENT_TYPE_JSON);
        JSONObject resbody = (JSONObject) ((JSONObject) res.bodyAsJson().get("d")).get("results");
        Entity entity = new Entity(accessor, (JSONObject) resbody);
        entity.setBody(resbody);
        entity.setResHeaders(res.getHeaderList());
        return entity;
    }

    // /**
    // * ユーザーデータを更新.
    // * @param key 更新するユーザデータのid
    // * @param body リクエストボディ
    // * @param etag 更新対象のバージョンのetag
    // * @return 作成したEntityオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to update the user data.
     * @param key ID value of the user data
     * @param body Request body
     * @param etag Etag value
     * @return Entity object that is updated
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public Entity updateAsEntity(String key, HashMap<String, Object> body, String etag) throws DaoException {
        String escapekey = "'" + Utils.escapeURI(key) + "'";
        String url = this.getUrl() + "(" + escapekey + ")";
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        DcResponse res = rest.put(url, JSONObject.toJSONString(body), etag, RestAdapter.CONTENT_TYPE_JSON);
        JSONObject json = new JSONObject();
        json.putAll(body);
        Entity entity = new Entity(accessor, json);
        entity.setResHeaders(res.getHeaderList());
        return entity;
    }

    // /**
    // * ユーザーデータを更新.
    // * @param id 更新するユーザデータのid
    // * @return 作成したEntityオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to retrieve the specified user data.
     * @param id ID value of the user data
     * @return Entity object
     * @throws DaoException Exception thrown
     */
    public Entity retrieveAsEntity(String id) throws DaoException {
        DcResponse res = internalRetrieveAsDcResponse(id);
        JSONObject json = (JSONObject) ((JSONObject) res.bodyAsJson().get("d")).get("results");
        Entity entity = new Entity(accessor, json);
        entity.setResHeaders(res.getHeaderList());
        return entity;
    }
}
