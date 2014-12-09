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

///**
// * ODataを操作するためのI/F.
// */
/**
 * This is the interface to work with OData class.
 */
public interface IODataManager {
    // /**
    // * Query実行.
    // * @param query クエリオブジェクト
    // * @return 実行した結果のJSONオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to perform Query execution.
     * @param query Query object
     * @return JSON object of the result
     * @throws DaoException Exception thrown
     */
    HashMap<String, Object> doSearch(Query query) throws DaoException;

    // /**
    // * URL文字列を生成.
    // * @return 生成したURL文字列
    // */
    /**
     * This method generates a URL string.
     * @return URL value
     */
    String getUrl();
}
