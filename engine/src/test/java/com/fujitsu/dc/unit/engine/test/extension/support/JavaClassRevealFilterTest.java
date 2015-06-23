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
package com.fujitsu.dc.unit.engine.test.extension.support;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import com.fujitsu.dc.engine.extension.support.JavaClassRevealFilter;
import com.fujitsu.dc.unit.Unit;

/**
 * JavaClassRevealFilterのユニットテスト.
 */
@Category({ Unit.class })
public class JavaClassRevealFilterTest {

    /**
     * Extension用の正しいパッケージ_クラス名が許容されること.
     */
    @Test
    public void Extension用の正しいパッケージ_クラス名が許容されること() {
        assertTrue(new JavaClassRevealFilter().accept("foo.dc.extention", "Ext_Foo"));
    }

    /**
     * Extension用のパッケージ_クラス名に許容されないパッケージパターンが指定された場合_拒否されること.
     */
    @Test
    @Ignore // パッケージのチェックを行わない仕様となったため。
    public void Extension用のパッケージ_クラス名に許容されないパッケージパターンが指定された場合_拒否されること() {
        assertFalse(new JavaClassRevealFilter().accept("java.lang", "String"));
    }

    /**
     * Extension用のパッケージ_クラス名に許容されないクラス名パターンが指定された場合_拒否されること.
     */
    @Test
    @Ignore // パッケージのチェックを行わない仕様となったため。
    public void Extension用のパッケージ_クラス名に許容されないクラス名パターンが指定された場合_拒否されること() {
        assertFalse(new JavaClassRevealFilter().accept("foo.dc.extention", "Foo"));
    }

    /**
     * Extension用のパッケージ_クラス名に空文字のパッケージが指定された場合_拒否されること.
     */
    @Test
    @Ignore // パッケージのチェックを行わない仕様となったため。
    public void Extension用のパッケージ_クラス名に空文字のパッケージが指定された場合_拒否されること() {
        assertFalse(new JavaClassRevealFilter().accept("", "Foo"));
    }

    /**
     * Extension用のパッケージ_クラス名に空文字のパッケージが指定された場合_拒否されること.
     */
    @Test
    @Ignore // パッケージのチェックを行わない仕様となったため。
    public void Extension用のパッケージ_クラス名に空文字のクラス名が指定された場合_拒否されること() {
        assertFalse(new JavaClassRevealFilter().accept("foo.dc.extention", ""));
    }

    /**
     * Extension用のパッケージ_クラス名にnullパッケージが指定された場合_拒否されること.
     */
    @Test
    public void Extension用のパッケージ_クラス名にnullパッケージが指定された場合_拒否されること() {
        assertFalse(new JavaClassRevealFilter().accept(null, "Ext_Foo"));
    }

    /**
     * Extension用のパッケージ_クラス名に空文字のパッケージが指定された場合_拒否されること.
     */
    @Test
    @Ignore // パッケージのチェックを行わない仕様となったため。
    public void Extension用のパッケージ_クラス名にnullクラス名が指定された場合_拒否されること() {
        assertFalse(new JavaClassRevealFilter().accept("", null));
    }

}
