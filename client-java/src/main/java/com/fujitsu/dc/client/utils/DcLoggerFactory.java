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
// * 抽象DCログファクトリークラス.
// */
/**
 * This is the abstract DC log factory class.
 */
public abstract class DcLoggerFactory {
    private static DcLoggerFactory dcLoggerFactory = new StdLoggerFactory();

    // /**
    // * デフォルトログファクトリー設定.
    // * @param loggerFactory ログファクトリー
    // */
    /**
     * This method is used for Default log factory setting.
     * @param loggerFactory Log Factory
     */
    public static void setDefaultFactory(DcLoggerFactory loggerFactory) {
        dcLoggerFactory = loggerFactory;
    }

    // /**
    // * ログファクトリー取得.
    // * @param clazz クラス
    // * @return DcLogger DCログ
    // */
    /**
     * This method is used for Log factory acquisition.
     * @param clazz class
     * @return DcLogger DC log
     */
    @SuppressWarnings("rawtypes")
    public static DcLogger getLogger(Class clazz) {
        return dcLoggerFactory.newInstance(clazz);
    }

    // /**
    // * インスタンス生成.
    // * @param clazz クラス
    // * @return DcLogger DCログ
    // */
    /**
     * This is the declaration for new instantiation.
     * @param clazz Class
     * @return DcLogger DC Log
     */
    @SuppressWarnings("rawtypes")
    protected abstract DcLogger newInstance(Class clazz);
}
