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

///**
// * ログクラス.
// */
/**
 * This class represents Log class used for logging purposes.
 */
public class Log {
    // ログオブジェクト
    /** Log object. */
    private DcLogger log;

    // /**
    // * コンストラクタ.
    // * @param clazz クラス
    // */
    /**
     * This is the parameterized constructor used for initializing log variable.
     * @param clazz Class
     */
    @SuppressWarnings("rawtypes")
    public Log(Class clazz) {
        log = DcLoggerFactory.getLogger(clazz);
    }

    // /**
    // * ログ情報を出力.
    // * @param value メッセージ
    // */
    /**
     * This method is used for logging the output information.
     * @param value Message
     */
    public void debug(String value) {
        log.debug(value);
    }
}
