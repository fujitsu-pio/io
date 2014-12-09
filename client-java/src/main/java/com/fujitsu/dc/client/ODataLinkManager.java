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

import com.fujitsu.dc.client.http.IRestAdapter;
import com.fujitsu.dc.client.http.RestAdapterFactory;

///**
// * OData関連の各機能を生成/削除するためのクラスの抽象クラス.
// */
/**
 * It creates a new object of ODataLinkManager. This is the abstract class for generating / deleting the OData related
 * functions.
 */
public class ODataLinkManager extends LinkManager {

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param cx リンク主体
    // */
    /**
     * This is the parameterized constructor with two arguments and calling its parent constructor internally.
     * @param as Accessor
     * @param cx ILinkageResource
     */
    public ODataLinkManager(Accessor as, ILinkageResource cx) {
        super(as, cx);
    }

    // /**
    // * リンクを削除.
    // * @param cx リンク削除するターゲットオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to remove a link.
     * @param cx Target object for removing the link.
     * @throws DaoException Exception thrown
     */
    public void unLink(ILinkageResource cx) throws DaoException {

        String uri = getLinkUrl(cx);

        IRestAdapter rest = RestAdapterFactory.create(accessor);
        rest.del(uri + cx.getKey());
    }
}
