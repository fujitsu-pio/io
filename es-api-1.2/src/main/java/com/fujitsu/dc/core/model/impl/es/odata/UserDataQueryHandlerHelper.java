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
package com.fujitsu.dc.core.model.impl.es.odata;

import java.util.HashMap;
import java.util.Map;

/**
 * UserDataQueryHandlerのHelperクラス.
 * Es0.19とEs1.X系とで異なるEsクエリの差異を吸収するためのHelperクラス
 * 現状は0.19版で実装している
 */
public class UserDataQueryHandlerHelper {

    private UserDataQueryHandlerHelper() {
    }

    /**
     * UserDataの文字列型プロパティのOrderByキーを取得する.
     * @param key sortKey
     * @return UserDataの文字列型プロパティのOrderByキー
     */
    public static String getOrderByKey(String key) {
        return key;
    }

    /**
     * UserDataの文字列型プロパティのOrderByに指定する値を取得する.
     * @param orderOption asc/desc
     * @param key sortKey
     * @return UserDataの文字列型プロパティのOrderByに指定する値
     */
    public static Map<String, Object> getOrderByValue(String orderOption, String key) {
        Map<String, Object> sortOption = new HashMap<String, Object>();
        sortOption.put("order", orderOption);
        sortOption.put("ignore_unmapped", true);
        return sortOption;
    }

    /**
     * UserDataの文字列型プロパティのOrderByに指定する値を取得する.<br />
     * 既存の動作と互換性のあるソートの順序にしたい場合は本メソッドを使用する.<br />
     * ※1.2.1でNULLの場所が変わっている
     * @param orderOption asc/desc
     * @param key sortKey
     * @return UserDataの文字列型プロパティのOrderByに指定する値
     */
    public static Map<String, Object> getOrderByValueForMissingFirst(String orderOption, String key) {
        Map<String, Object> orderByValue = getOrderByValue(orderOption, key);
        // ascの場合はnullを先頭にする
        if (orderOption.equals("asc")) {
            orderByValue.put("missing", "_first");
        }
        return orderByValue;
    }
}
