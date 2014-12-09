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

import java.util.ArrayList;

///**
// * 文字列操作の拡張クラス.
// */
/**
 * This is the Extension class of string manipulation.
 */
public final class StringUtils {
    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor made private to disallow instantiation of objects of this class by other classes.
     */
    private StringUtils() {
    }

    // /**
    // * 配列を文字列へ連結する.
    // * @param arry 対象となる文字列の配列
    // * @param with 連結する文字列
    // * @return 連結した文字列
    // */
    /**
     * This method is used to link to string array.
     * @param arry Target array of string
     * @param with String consolidated
     * @return Linked string
     */
    public static String join(final ArrayList<String> arry, final String with) {
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < arry.size(); i++) {
            if (buf.length() > 0) {
                buf.append(with);
            }
            buf.append(arry.get(i));
        }
        return buf.toString();
    }
}
