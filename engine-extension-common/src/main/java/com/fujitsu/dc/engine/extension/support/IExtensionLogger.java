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
package com.fujitsu.dc.engine.extension.support;

/**
 * Extensionクラス内で利用可能なロガーインタフェース.
 */
public interface IExtensionLogger {

    /**
     * TRACEレベルでログを出力する.
     * @param message メッセージ
     */
    void trace(String message);
    /**
     * TRACEレベルでログを出力する.
     * @param message メッセージ
     * @param t 原因
     */
    void trace(String message, Throwable t);

    /**
     * DEBUGレベルでログを出力する.
     * @param message メッセージ
     */
    void debug(String message);
    /**
     * DEBUGレベルでログを出力する.
     * @param message メッセージ
     * @param t 原因
     */
    void debug(String message, Throwable t);

    /**
     * INFOレベルでログを出力する.
     * @param message メッセージ
     */
    void info(String message);
    /**
     * INFOレベルでログを出力する.
     * @param message メッセージ
     * @param t 原因
     */
    void info(String message, Throwable t);

    /**
     * WARNレベルでログを出力する.
     * @param message メッセージ
     */
    void warn(String message);
    /**
     * WARNレベルでログを出力する.
     * @param message メッセージ
     * @param t 原因
     */
    void warn(String message, Throwable t);

    /**
     * ERRORレベルでログを出力する.
     * @param message メッセージ
     */
    void error(String message);
    /**
     * ERRORレベルでログを出力する.
     * @param message メッセージ
     * @param t 原因
     */
    void error(String message, Throwable t);
}
