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

import org.mozilla.javascript.Scriptable;

/**
 * Extensionクラスに公開するロガークラスの実装.
 */
public class NullLogger implements IExtensionLogger {

    /**
     * コンストラクタ.
     * @param clazz クラス
     */
    public NullLogger(Class<? extends Scriptable> clazz) {
    }

    @Override
    public void trace(String message) {
        trace(message, null);
    }

    @Override
    public void trace(String message, Throwable t) {
    }

    @Override
    public void debug(String message) {
        debug(message, null);
    }

    @Override
    public void debug(String message, Throwable t) {
    }

    @Override
    public void info(String message) {
        info(message, null);
    }

    @Override
    public void info(String message, Throwable t) {
    }

    @Override
    public void warn(String message) {
        warn(message, null);
    }

    @Override
    public void warn(String message, Throwable t) {
    }

    @Override
    public void error(String message) {
        error(message, null);
    }

    @Override
    public void error(String message, Throwable t) {
    }
}
