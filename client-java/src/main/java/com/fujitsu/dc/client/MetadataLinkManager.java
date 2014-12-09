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
 * It creates a new object of MetadataLinkManager. This class performs link/unlink operations on metadata.
 */
public class MetadataLinkManager {

    // /** アクセス主体. */
    /** Reference to Accessor. */
    Accessor accessor;

    // /** リンク主体. */
    /** Link subject. */
    AbstractODataContext context;

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param cx リンク主体
    // */
    /**
     * This is the parameterized constructor with two arguments initializing the class variables.
     * @param as Accessor
     * @param cx AbstractODataContext
     */
    public MetadataLinkManager(Accessor as, AbstractODataContext cx) {
        this.accessor = as;
        this.context = cx;
    }

    // /**
    // * リンクを削除.
    // * @param cx リンク削除するターゲットオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to remove the link.
     * @param cx Target object to delete link
     * @throws DaoException Exception thrown
     */
    public void unLink(AbstractODataContext cx) throws DaoException {

        String uri = getLinkUrl(cx);

        IRestAdapter rest = RestAdapterFactory.create(accessor);
        rest.del(uri + cx.getKey());
    }

    // /**
    // * リンクを作成.
    // * @param cx リンクさせるターゲットオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to create the link.
     * @param cx Target object to create link
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public void link(AbstractODataContext cx) throws DaoException {
        String uri = getLinkUrl(cx);

        JSONObject body = new JSONObject();
        body.put("uri", cx.getODataLink());

        IRestAdapter rest = RestAdapterFactory.create(accessor);
        rest.post(uri, body.toJSONString(), RestAdapter.CONTENT_TYPE_JSON);
    }

    // /**
    // * $linkへのリクエストurlを生成する.
    // * @param cx ターゲットのODataオブジェクト
    // * @return 生成したurl
    // */
    /**
     * This method is used to generates a request to the url $ link.
     * @param cx OData target object
     * @return URL value
     */
    private String getLinkUrl(final AbstractODataContext cx) {

        StringBuilder sb = new StringBuilder();
        sb.append(context.getODataLink());
        sb.append("/$links/");
        sb.append("_" + cx.getClassName());
        return sb.toString();
    }

}
