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
import java.util.Map;

import org.json.simple.JSONObject;

import com.fujitsu.dc.client.http.DcResponse;
import com.fujitsu.dc.client.http.RestAdapter;
import com.fujitsu.dc.client.http.RestAdapterFactory;
import com.fujitsu.dc.client.utils.Utils;

///**
// * CellのCRUDを行うクラス.
// */
/**
 * It creates a new object of CellManager. This class performs CRUD operations for Cell.
 */
public class CellManager extends ODataManager {
    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor calling its parent constructor.
     * @param as Accessor
     */
    public CellManager(Accessor as) {
        super(as);
    }

    // /**
    // * URLを生成する.
    // * @return URL文字列
    // */
    /**
     * This method generates and returns the URL for Cell.
     * @return URL value
     */
    @Override
    public String getUrl() {
        return this.getBaseUrl() + "__ctl/Cell";
    }

    // /**
    // * Cellを作成.
    // * @param obj Cellオブジェクト
    // * @return Cellオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates a cell using Cell object.
     * @param obj Cell object
     * @return Cell object that is created
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public Cell create(Cell obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("Name", obj.getName());
        JSONObject json = this.cellCreate(body);
        obj.initialize(this.accessor, json);
        return obj;
    }

    // /**
    // * Cellを作成.
    // * @param body リクエストボディ
    // * @return 作成したCellオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates a cell using requset body.
     * @param body Request Body
     * @return Cell object that is created
     * @throws DaoException Exception thrown
     */
    public Cell create(HashMap<String, Object> body) throws DaoException {
        JSONObject json = this.cellCreate(body);
        return new Cell(accessor, json);
    }

    /**
     * This is the private method to execute the API for cell creation.
     * @param body Request Body
     * @return JSON object
     * @throws DaoException Exception thrown
     */
    private JSONObject cellCreate(HashMap<String, Object> body) throws DaoException {
        String url = this.getUrl();
        RestAdapter rest = new RestAdapter(accessor);
        HashMap<String, String> headers = new HashMap<String, String>();

        DcResponse res = rest.post(url, headers, JSONObject.toJSONString(body), RestAdapter.CONTENT_TYPE_JSON);
        JSONObject json = (JSONObject) ((JSONObject) res.bodyAsJson().get("d")).get("results");
        return json;
    }

    // /**
    // * Cellを取得.
    // * @param id 取得対象のID
    // * @return 取得したしたCellオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method retrieves the specified cell.
     * @param id Cell Name
     * @return Cell object
     * @throws DaoException Eception thrown
     */
    public Cell retrieve(String id) throws DaoException {
        JSONObject json = internalRetrieve(id);
        return new Cell(accessor, json);
    }

    // public Cell[] search(DcQuery query) throws DaoException {
    // return null;
    // }

    /**
     * This method is used for Cell Recursive/force delete.
     * @param cellName cellName name of cell to be deleted
     * @return DcResponse response
     * @throws DaoException Library Exception
     */
    public DcResponse recursiveDelete(String cellName) throws DaoException {
        String url = this.getBaseUrl() + Utils.escapeURI(cellName);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-Dc-Recursive", "true");
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        return rest.del(url, headers);
    }
}
