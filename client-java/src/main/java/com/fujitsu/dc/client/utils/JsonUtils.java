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
package com.fujitsu.dc.client.utils;

import java.util.Map;

import org.json.simple.JSONObject;

///**
// * JSON関連ユーティリティクラス.
// */
/**
 * This is the JSON-related utility class.
 */
public final class JsonUtils {
    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor made private to disallow instantiation of objects of this class by other classes.
     */
    private JsonUtils() {
    }

    // /**
    // * JSON文字列を返却する.
    // * @param jsonMap 対象となるマップ
    // * @return JSON文字列
    // */
    /**
     * This method converts the Map values to string form.
     * @param jsonMap Target Map
     * @return JSON string
     */
    public static String toJsonString(final Map<String, Object> jsonMap) {
        return JSONObject.toJSONString(jsonMap);
    }
}
