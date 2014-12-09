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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

///**
// * ユーティリティクラス.
// */
/**
 * This is the generic Utility class for providing methods used throughout the library.
 */
public final class Utils {

    // /** デフォルトエンコード. */
    /** Default encoding. */
    private static String encoding = "utf-8";

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor made private to disallow instantiation of objects of this class by other classes.
     */
    private Utils() {
    }

    // /**
    // * ＵＲＬエンコードを行う.
    // * @param in エンコードを行う文字列
    // * @return エンコード後の文字列
    // */
    /**
     * This method is used for encoding ＵＲＬ.
     * @param in Character string to be encoded
     * @return String returned after encoding
     */
    public static String escapeURI(final String in) {
        try {
            return URLEncoder.encode(in, Utils.encoding);
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }
}
