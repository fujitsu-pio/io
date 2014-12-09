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
 * It creates a new object of ODataManager. This is the abstract class for generating / deleting the OData related
 * functions and serves as middle layer in API calls for CRUD operations.
 */
public class ODataManager implements IODataManager {
    // /** アクセス主体. */
    /** Reference to Accessor. */
    Accessor accessor;
    // /** DAVコレクション. */
    /** Reference to DcCollection. */
    DcCollection collection;
    // /** EntitySet名. */
    /** EntitySet Name. */
    String entitySetName;
    /** EntityID. */
    String keyPredicate;
    /** NavigationProperty. */
    String naviProperty;

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument initializing the accessor.
     * @param as Accessor
     */
    public ODataManager(Accessor as) {
        this.accessor = as.clone();
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param col DAVコレクション
    // */
    /**
     * This is the parameterized constructor with two arguments calling its own constructor and initializing collection.
     * @param as Accessor
     * @param col DcCollection
     */
    public ODataManager(Accessor as, DcCollection col) {
        this(as);
        this.collection = col;
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param col DAVコレクション
    // * @param name EntitySet名
    // */
    /**
     * This is the parameterized constructor with three arguments calling its own constructor and initializing other
     * class variables.
     * @param as Accessor
     * @param col DcCollection
     * @param name EntitySetName
     */
    public ODataManager(Accessor as, DcCollection col, String name) {
        this(as);
        this.collection = col;
        this.entitySetName = name;
    }

    // /**
    // * IDをEntitySet指定する.
    // * @param key keyPredicate
    // * @return EntitySetオブジェクト
    // */
    /**
     * This method sets key for EntityID.
     * @param key keyPredicate
     * @return EntitySet object
     */
    public ODataManager key(String key) {
        this.keyPredicate = key;
        return this;
    }

    // /**
    // * navigationPropertyをEntitySet指定する.
    // * @param navProp NavigationProperty
    // * @return EntitySetオブジェクト
    // */
    /**
     * This method specifies the EntitySet navigationProperty.
     * @param navProp NavigationProperty
     * @return EntitySet object
     */
    public ODataManager nav(String navProp) {
        this.naviProperty = navProp;
        return this;
    }

    // /**
    // * ベースURL取得.
    // * @return ベースURL
    // */
    /**
     * This method returns the Base URL for making a connection.
     * @return BaseURL value
     */
    public String getBaseUrl() {
        return this.accessor.getContext().getBaseUrl();
    }

    // /**
    // * ODataデータを作成.
    // * @param body POSTするリクエストボディ
    // * @return 対象となるODataContextを抽象クラスとして返却
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs create operation using request body.
     * @param body POST Request Body
     * @return JSONObject as response
     * @throws DaoException Exception thrown
     */
    JSONObject internalCreate(HashMap<String, Object> body) throws DaoException {
        String url = this.getUrl();
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        DcResponse res = rest.post(url, JSONObject.toJSONString(body), RestAdapter.CONTENT_TYPE_JSON);
        JSONObject json = (JSONObject) ((JSONObject) res.bodyAsJson().get("d")).get("results");
        return json;
    }

    // /**
    // * ODataデータを作成.
    // * @param body POSTするリクエストボディ
    // * @param headers POSTするリクエストヘッダー
    // * @return 対象となるODataContextを抽象クラスとして返却
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs create operation using request body and headers.
     * @param body POST Request Body
     * @param headers POST Request Headers
     * @return JSONObject as response
     * @throws DaoException Exception thrown
     */
    JSONObject internalCreate(HashMap<String, Object> body, HashMap<String, String> headers) throws DaoException {
        String url = this.getUrl();
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        DcResponse res = rest.post(url, headers, JSONObject.toJSONString(body), RestAdapter.CONTENT_TYPE_JSON);
        JSONObject json = (JSONObject) ((JSONObject) res.bodyAsJson().get("d")).get("results");
        return json;
    }

    // /**
    // * ODataデータを取得.
    // * @param id 対象となるID値
    // * @return １件取得した結果のオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs retrieve operation. It internally calls internalRetrieveMultikey.
     * @param id ID value
     * @return Object of the result(JSONObject)
     * @throws DaoException Exception thrown
     */
    JSONObject internalRetrieve(String id) throws DaoException {
        return this.internalRetrieveMultikey("'" + Utils.escapeURI(id) + "'");
    }

    // /**
    // * ODataデータを取得(複合キー).
    // * @param id 対象となる複合キー<br>
    // * urlエンコードが必要
    // * @return １件取得した結果のオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs retrieve operation.
     * @param id composite key url encoding the target
     * @return JSONObject as response
     * @throws DaoException Exception thrown
     */
    JSONObject internalRetrieveMultikey(String id) throws DaoException {
        DcResponse res = internalRetrieveMultikeyAsDcResponse(id);
        JSONObject json = (JSONObject) ((JSONObject) res.bodyAsJson().get("d")).get("results");
        return json;
    }

    // /**
    // * ODataデータを取得.
    // * @param id 対象となるID値
    // * @return １件取得した結果のDcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to retrieve the OData data for the specified ID. It internally calls
     * internalRetrieveMultikeyAsDcResponse.
     * @param id ID value
     * @return Object of the result
     * @throws DaoException Exception thrown
     */
    DcResponse internalRetrieveAsDcResponse(String id) throws DaoException {
        return this.internalRetrieveMultikeyAsDcResponse("'" + Utils.escapeURI(id) + "'");
    }

    // /**
    // * ODataデータを取得(複合キー).
    // * @param id 対象となる複合キー<br>
    // * urlエンコードが必要
    // * @return １件取得した結果のDcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to retrieve the OData data for the specified ID.
     * @param id composite key url encoding the target
     * @return Object of the result
     * @throws DaoException Exception thrown
     */
    DcResponse internalRetrieveMultikeyAsDcResponse(String id) throws DaoException {
        String url = this.getUrl() + "(" + id + ")";
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        return rest.get(url, RestAdapter.CONTENT_TYPE_JSON);
    }

    // /**
    // * ODataデータを更新.
    // * @param id 対象となるID値
    // * @param body PUTするリクエストボディ
    // * @param etag ETag値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs update operation using Request Body and Etag value. It internally calls
     * internalUpdateMultiKey.
     * @param id ID value
     * @param body PUT Request Body
     * @param etag ETag value
     * @throws DaoException Exception thrown
     */
    void internalUpdate(String id, HashMap<String, Object> body, String etag) throws DaoException {
        this.internalUpdateMultiKey("'" + Utils.escapeURI(id) + "'", body, etag);
    }

    // /**
    // * ODataデータを更新.
    // * @param id 対象となるID値
    // * @param body PUTするリクエストボディ
    // * @param etag ETag値
    // * @param headers PUTするリクエストヘッダー
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs update operation using Request Body, Header and Etag value.
     * @param id ID value
     * @param body PUT Request Body
     * @param etag ETag value
     * @param headers PUT Request Headers
     * @throws DaoException DAO例外
     */
    void internalUpdate(String id, JSONObject body, String etag, HashMap<String, String> headers) throws DaoException {
        String url = this.getUrl() + "('" + id + "')";
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        rest.put(url, body.toJSONString(), etag, headers, RestAdapter.CONTENT_TYPE_JSON);
    }

    // /**
    // * ODataデータを更新(複合キー).
    // * @param multiKey 対象となる複合キー<br>
    // * urlエンコードが必要
    // * @param body PUTするリクエストボディ
    // * @param etag ETag値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs update operation using Request Body and Etag value.
     * @param multiKeymultiKey composite key url encoding the target
     * @param body PUT Request Body
     * @param etag ETag Value
     * @throws DaoException Exception thrown
     */
    void internalUpdateMultiKey(String multiKey, HashMap<String, Object> body, String etag) throws DaoException {
        String url = this.getUrl() + "(" + multiKey + ")";
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        rest.put(url, JSONObject.toJSONString(body), etag, RestAdapter.CONTENT_TYPE_JSON);
    }

    // /**
    // * ODataデータを削除.
    // * @param id 削除するODataデータのID値
    // * @param etag ETag値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs delete operation. It internally calls internalDelMultiKey.
     * @param id ID value
     * @param etag ETag Value
     * @throws DaoException Exception thrown
     */
    void internalDel(String id, String etag) throws DaoException {
        this.internalDelMultiKey("'" + Utils.escapeURI(id) + "'", etag);
    }

    // /**
    // * ODataデータを削除(複合キー).
    // * @param id 削除するODataデータの複合キー<br>
    // * urlエンコードが必要
    // * @param etag ETag値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs delete operation.
     * @param id id composite key url encoding the target
     * @param etag ETag Value
     * @throws DaoException Exception thrown
     */
    void internalDelMultiKey(String id, String etag) throws DaoException {
        String url = this.getUrl() + "(" + id + ")";
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        rest.del(url, etag);
    }

    // /**
    // * ODataデータを登録.
    // * @param json 登録するJSONオブジェクト
    // * @return 登録結果のレスポンス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method registers the OData data and returns in JSON form.
     * @param json JSON object
     * @return Response of the registration result
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> createAsJson(HashMap<String, Object> json) throws DaoException {
        return this.internalCreate(json);
    }

    // /**
    // * ODataデータを取得.
    // * @param id 取得するID値
    // * @return 取得したJSONオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method retrieves data in JSON form.
     * @param id ID value
     * @return JSON object
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public HashMap<String, Object> retrieveAsJson(String id) throws DaoException {
        return (HashMap<String, Object>) this.internalRetrieve(id);
    }

    // /**
    // * ODataデータを更新.
    // * @param id 対象となるID値
    // * @param body PUTするリクエストボディ
    // * @param etag ETag値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs update operation.
     * @param id ID value
     * @param body PUT Request Body
     * @param etag ETagvalue
     * @throws DaoException Exception thrown
     */
    public void update(String id, HashMap<String, Object> body, String etag) throws DaoException {
        this.internalUpdate(id, body, etag);
    }

    // /**
    // * ODataデータを削除.
    // * @param id 削除するODataデータのID値
    // * @param etag ETag値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs delete operation with etag specified.
     * @param id ID value
     * @param etag ETag value
     * @throws DaoException Exception thrown
     */
    public void del(String id, String etag) throws DaoException {
        this.internalDel(id, etag);
    }

    // /**
    // * ODataデータを削除.
    // * @param id 削除するODataデータのID値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method performs delete operation without etag specified.
     * @param id ID value
     * @throws DaoException Exception thrown
     */
    public void del(String id) throws DaoException {
        this.internalDel(id, "*");
    }

    /**
     * This method appends query string to execute Query for Search.
     * @param query Query object
     * @return JSON response
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    @Override
    public HashMap<String, Object> doSearch(Query query) throws DaoException {
        StringBuilder sb = new StringBuilder(this.getUrl());
        String qry = query.makeQueryString();
        if (qry != null && !"".equals(qry)) {
            sb.append("?" + qry);
        }
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        DcResponse res = rest.get(sb.toString(), RestAdapter.CONTENT_TYPE_JSON);
        return (HashMap<String, Object>) ((JSONObject) res.bodyAsJson());
    }

    // /**
    // * クエリを生成.
    // * @return 生成したQueryオブジェクト
    // */
    /**
     * This method executes Query.
     * @return Query object generated
     */
    public Query query() {
        return new Query(this);
    }

    // /**
    // * ODataデータの生存確認.
    // * @param id 対象となるODataデータのID
    // * @return true:生存、false:不在
    // */
    /**
     * This method checks whether the specified Odata exists.
     * @param id ID value
     * @return true:Survival false:Absence
     */
    public Boolean exists(String id) {
        String url = this.getUrl() + "('" + id + "')";
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        try {
            rest.head(url);
            return true;
        } catch (DaoException e) {
            return false;
        }
    }

    /**
     * This method generates the URL for executing API calls.
     * @return URL value
     */
    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder();
        // $Batchモードの場合は、相対パス
        /** In the case of $ Batch mode, the relative path . */
        if (!accessor.isBatchMode()) {
            sb.append(this.collection.getPath());
            sb.append("/");
        }
        sb.append(this.entitySetName);
        // key()によりKeyPredicateが指定されていたら
        /** KeyPredicate if it has been specified by the key. */
        if (null != this.keyPredicate && !"".equals(this.keyPredicate)) {
            sb.append(String.format("('%s')", this.keyPredicate));
            // nav()によりnaviPropertyが指定されていたら
            /** naviProperty if it was given by nav. */
            if (null != this.naviProperty && !"".equals(this.naviProperty)) {
                sb.append(String.format("/_%s", this.naviProperty));
            }
        }
        return sb.toString();
    }
}
