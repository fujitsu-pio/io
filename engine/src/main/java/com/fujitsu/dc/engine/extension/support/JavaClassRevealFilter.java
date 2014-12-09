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
 * Extensionとして JavaScriptに公開するクラスを切り分けるためのフィルターI/F.
 */
public class JavaClassRevealFilter implements ExtensionClassFilter {

    private static final String REVEAL_CLASS_PREFIX = "Ext_";

    /**
     * Extensionとして JavaScriptに公開するクラスを切り分けるためのフィルター.
     * @param packageName パッケージ名
     * @param className クラス名
     * @return Extensionとして公開するクラスであった場合は、true, そうでない場合は falseを返す。
     */
    public boolean accept(String packageName, String className) {
        if (null == packageName || null == className) {
            return false;
        }
        // クラス名が "Ext_"で開始するもののみ JavaScriptに公開する。（認める。)
        return className.startsWith(REVEAL_CLASS_PREFIX);
    }

    /**
     * このフィルタの説明を返す.
     * @return 説明文
     */
    public String getDescription() {
        return "クラス名が \"Ext_\"で開始するもののみ JavaScriptに公開する。";
    }
}
