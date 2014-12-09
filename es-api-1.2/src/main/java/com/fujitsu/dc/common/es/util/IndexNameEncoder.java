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
package com.fujitsu.dc.common.es.util;

import com.fujitsu.dc.common.es.impl.EsIndexImpl.TooLongIndexNameException;

/**
 * ElasticsearchのIndex名エンコードクラス.
 */
public class IndexNameEncoder {

    // インデックス名の許容される最大長
    private static final int MAX_INDEX_NAME_LENGTH = 64;

    private IndexNameEncoder() {
    }

    /**
     * Unit User名からDataBundle名（ESインデックス/Ads RDBのDB名）を生成する.
     * TODO メソッド名をtoDataBundleNameに変更する。
     * デコード処理は存在せず一方通行であり、不可逆なエンコードである。
     * 文字列に#が含まれたら#以降の文字列を採用する。
     * @param uri UnitUserUri / : # は含んで良いその他の記号は含めないで欲しい。
     * @return エンコード後の文字列
     */
    public static String encodeEsIndexName(final String uri) {
        String in = uri.toLowerCase();
        StringBuilder ret = new StringBuilder();
        int fragIdx = in.indexOf('#');
        if (fragIdx > 0) {
            in = in.substring(fragIdx + 1);
        }
        if (in.endsWith("/")) {
            in = in.substring(0, in.length() - 1);
        }
        in = in.replaceAll("https\\:\\/\\/", "")
                .replaceAll("http\\:\\/\\/", "")
                .replaceAll("\\/", "__")
                .replaceAll("\\:", "__")
                .replaceAll("\\#", "__")
                .replaceAll("\\.", "_");
        ret.append(in);
        if (ret.length() > MAX_INDEX_NAME_LENGTH) {
            throw new TooLongIndexNameException(ret.toString());
        }
        return ret.toString();
    }

}
