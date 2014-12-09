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
// * 標準出力ログクラス.
// */
/**
 * This is the Standard output log class.
 */
public class StdLogger implements DcLogger {
    @SuppressWarnings("rawtypes")
    /** Variable Clazz. */
    private Class clazz;

    // /**
    // * コンストラクタ.
    // * @param clazz クラス
    // */
    /**
     * This is the parameterized constructor used for initializing clazz variable.
     * @param clazz Class
     */
    @SuppressWarnings("rawtypes")
    public StdLogger(Class clazz) {
        this.clazz = clazz;
    }

    // /**
    // * デバッグ情報出力.
    // * @param msg 出力メッセージ
    // */
    /**
     * This method is used for logging the debugging information.
     * @param msg Output Message
     */
    public void debug(String msg) {
        System.out.println(msg);
    }

    // /**
    // * class取得.
    // * @return clazz クラス
    // */
    /**
     * This method returns the clazz.
     * @return clazz Class
     */
    @SuppressWarnings("rawtypes")
    public Class getClassStd() {
        return clazz;
    }
}
