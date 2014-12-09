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

import org.json.simple.JSONObject;

import com.fujitsu.dc.client.http.IRestAdapter;
import com.fujitsu.dc.client.http.RestAdapter;
import com.fujitsu.dc.client.http.RestAdapterFactory;

///**
// * OData関連の各機能を生成/削除するためのクラスの抽象クラス.
// */
/**
 * It creates a new object of LinkManager. This class performs CRUD operations on link between two cell control objects.
 */
public class LinkManager {

    // /** アクセス主体. */
    /** Reference to Accessor. */
    Accessor accessor;

    // /** リンク主体. */
    /** Link Subject. */
    ILinkageResource context;

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param cx リンク主体
    // */
    /**
     * This is the parameterized constructor with two arguments initializing the class variables.
     * @param as Accessor
     * @param cx ILinkageResource
     */
    public LinkManager(Accessor as, ILinkageResource cx) {
        this.accessor = as;
        this.context = cx;
    }

    // /**
    // * リンクを作成.
    // * @param cx リンクさせるターゲットオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to create a link between two cell control objects.
     * @param cx ILinkageResource
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public void link(ILinkageResource cx) throws DaoException {
        String uri = getLinkUrl(cx);

        String linksUri = null;
        if (this.accessor.isBatchMode()) {
            linksUri = ((BatchLinksEntity) this.context).getCollectionUrl() + cx.getODataLink();
        } else {
            linksUri = cx.getODataLink();
        }

        JSONObject body = new JSONObject();
        body.put("uri", linksUri);

        IRestAdapter rest = RestAdapterFactory.create(accessor);
        rest.post(uri, body.toJSONString(), RestAdapter.CONTENT_TYPE_JSON);
    }

    // /**
    // * $linkへのリクエストurlを生成する.
    // * @param cx ターゲットのODataオブジェクト
    // * @return 生成したurl
    // */
    /**
     * This method is used to generate and return the link URL.
     * @param cx ILinkageResource
     * @return Link URL value
     */
    protected String getLinkUrl(final ILinkageResource cx) {
        StringBuilder sb = new StringBuilder();
        sb.append(this.context.makeUrlForLink());
        sb.append("_" + cx.getClassName());
        return sb.toString();
    }
}
