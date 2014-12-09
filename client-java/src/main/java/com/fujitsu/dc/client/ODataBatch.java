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

import java.util.List;

import com.fujitsu.dc.client.http.DcResponse;
import com.fujitsu.dc.client.http.RestAdapter;
import com.fujitsu.dc.client.utils.UrlUtils;

///**
// * $BatchにてODataへアクセスするためのクラス.
// */
/**
 * It creates a new object of ODataBatch. This class is used to generate the $ Batch format command.
 */
public class ODataBatch extends ODataCollection {

    private List<ODataResponse> oDataResponses;

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param name パス文字列
    // */
    /**
     * This is the parameterized constructor with two parameters calling its parent constructor internally.
     * @param as Accessor
     * @param name Name
     */
    public ODataBatch(Accessor as, String name) {
        super(as, name);
        accessor.setBatch(true);
    }

    // /**
    // * Batchコマンドの実行.
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is ued for Batch execution of commands.
     * @throws DaoException Exception thrown
     */
    public void send() throws DaoException {
        String url = UrlUtils.append(super.getPath(), "$batch");
        String boundary = accessor.getBatchAdapter().getBatchBoundary();
        String contentType = "multipart/mixed; boundary=" + boundary;

        RestAdapter rest = new RestAdapter(accessor);
        DcResponse res = rest.post(url, accessor.getBatchAdapter().get(), contentType);
        ODataBatchResponseParser parser = new ODataBatchResponseParser();

        this.oDataResponses = parser.parse(res.bodyAsString(), boundary);
    }

    // /**
    // * BatchBoundaryを挿入する.
    // * @throws DaoException Dao例外
    // */
    /**
     * This method is used to insert the BatchBoundary.
     * @throws DaoException Exception thrown
     */
    public void insertBoundary() throws DaoException {
        accessor.getBatchAdapter().insertBoundary();
    }

    // /**
    // * batch実行結果の取得.
    // * @return batch実行結果オブジェクト
    // */
    /**
     * This method is used for acquisition of batch execution result.
     * @return Batch execution result object
     */
    public ODataResponse[] getResponses() {
        return (ODataResponse[]) oDataResponses.toArray(new ODataResponse[0]);
    }

    // /**
    // * Batchの$links登録用Entityを生成する.
    // * @param name EntitySet名
    // * @param id ユーザデータの __id
    // * @return 生成したBatchLinksEntityオブジェクト
    // */
    /**
     * This method is used to generate a Batch of $ links registration Entity.
     * @param name EntitySetName
     * @param id ID of user data
     * @return BatchLinksEntity object that is generated
     */
    public BatchLinksEntity batchLinksEntity(String name, String id) {
        return new BatchLinksEntity(this.accessor, this.getPath(), name, id);
    }

    // /**
    // * Batchの$links登録用リンクターゲットオブジェクトを生成する.
    // * @param name EntitySet名
    // * @param id ユーザデータの __id
    // * @return 生成したBatchLinksEntityオブジェクト
    // */
    /**
     * This method is used to generate a Batch of $ links registration link target object.
     * @param name EntitySetName
     * @param id ID of user data
     * @return BatchLinksEntity object that is generated
     */
    public BatchLinksEntity batchLinksTarget(String name, String id) {
        return new BatchLinksEntity(name, id);
    }

}
