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

import java.net.MalformedURLException;
import java.net.URL;

///**
// * URL文字列を操作するクラス.
// */
/**
 * This is the Class to manipulate the URL string.
 */
public class UrlUtils {
    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor made private to disallow instantiation of objects of this class by other classes.
     */
    private UrlUtils() {
    }

    // /**
    // * URLにパスを追加する.
    // * @param url URL文字列
    // * @param name 追加するパス
    // * @return 生成したURL文字列
    // */
    /**
     * This method is used to add the path in the URL.
     * @param url URL string
     * @param name Added to path
     * @return Generated Path
     */
    public static String append(String url, String name) {
        StringBuilder sb = new StringBuilder(url);
        if (!url.endsWith("/")) {
            sb.append("/");
        }
        sb.append(name);
        return sb.toString();
    }

    // /**
    // * 対象urlが有効かチェックを行う.
    // * @param url チェック対象url文字列
    // * @return true： 有効/false：無効
    // */
    /**
     * This method is used to check the validity of the URL.
     * @param url URL string
     * @return true: Enable / false: Disable
     */
    public static boolean isUrl(String url) {
        try {
            new URL(url);
        } catch (MalformedURLException e) {
            return false;
        }
        return true;
    }
}
