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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * EsQueryHandlerのHelperクラス.
 * Es0.19とEs1.X系とで異なるEsクエリの差異を吸収するためのHelperクラス
 * 現状は0.19版で実装している
 */
public class EsQueryHandlerHelper {

    private EsQueryHandlerHelper() {
    }

    /**
     * selectのfield指定方法の差異を吸収するためのメソッド.
     * @param baseSource Query文本体
     * @param fields 取得対象のフィールドのリスト
     */
    @SuppressWarnings("unchecked")
    public static void composeSourceFilter(Map<String, Object> baseSource, List<String> fields) {
        // Es1.2.0対応
        // 0.19.9では_sourceに取得したいフィールドのリストを指定する
        baseSource.put("_source", fields);

        Map<String, Object> filter = (Map<String, Object>) baseSource.get("filter");
        if (filter != null && filter.size() != 0) {
            List<Map<String, Object>> andList = new ArrayList<Map<String, Object>>();
            andList.add(filter);
            Map<String, Object> newFilter = new HashMap<String, Object>();
            newFilter.put("and", andList);

            baseSource.put("filter", newFilter);
        }
    }
}
