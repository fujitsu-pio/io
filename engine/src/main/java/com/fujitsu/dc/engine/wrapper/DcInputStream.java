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
package com.fujitsu.dc.engine.wrapper;

import java.io.BufferedInputStream;
import java.io.InputStream;

import org.mozilla.javascript.Scriptable;

/**
 * InputStreamのラッパー.
 * EngineクライアントライブラリにInputStreamを直接渡すとClassShutterエラーになるため、継承した独自クラスに置き換える。
 */
public final class DcInputStream extends BufferedInputStream implements Scriptable {

    /**
     * InputStreamをラップする.
     * @param in InputStream
     */
    public DcInputStream(InputStream in) {
        super(in);
    }

    // ここ以下はScriptableインターフェース実装の為に必要なメソッド群
    @Override
    public String getClassName() {
        return this.getClass().getName();
    }

    @Override
    public Object get(String name, Scriptable start) {
        return null;
    }

    @Override
    public Object get(int index, Scriptable start) {
        return null;
    }

    @Override
    public boolean has(String name, Scriptable start) {
        return false;
    }

    @Override
    public boolean has(int index, Scriptable start) {
        return false;
    }

    @Override
    public void put(String name, Scriptable start, Object value) {
    }

    @Override
    public void put(int index, Scriptable start, Object value) {
    }

    @Override
    public void delete(String name) {
    }

    @Override
    public void delete(int index) {
    }

    @Override
    public Scriptable getPrototype() {
        return null;
    }

    @Override
    public void setPrototype(Scriptable prototype) {
    }

    @Override
    public Scriptable getParentScope() {
        return null;
    }

    @Override
    public void setParentScope(Scriptable parent) {
    }

    @Override
    public Object[] getIds() {
        return null;
    }

    @Override
    public Object getDefaultValue(Class<?> hint) {
        return null;
    }

    @Override
    public boolean hasInstance(Scriptable instance) {
        return false;
    }

}
