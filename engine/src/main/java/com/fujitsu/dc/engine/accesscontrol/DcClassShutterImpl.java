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
package com.fujitsu.dc.engine.accesscontrol;

import java.util.HashSet;
import java.util.Set;

import org.mozilla.javascript.ClassShutter;

/**
 * ClassShutterの実装. DcEngine上で呼び出し可能なJavaパッケージの制御
 */
public final class DcClassShutterImpl implements ClassShutter {
    private static final Set<String> ACCEPTED = new HashSet<String>();

    private static String[] allow = {
            // EngineがJavaScriptに対して提供しているクラス群(パッケージ)
            "com.fujitsu.dc.client.",
            "com.fujitsu.dc.engine.wrapper.",
            "com.fujitsu.dc.engine.adapter.",

            // testでログ出力しているため消せない(クラス)
            "ch.qos.logback.classic.Logger" };

    @Override
    // 可視判定メソッド
    public boolean visibleToScripts(final String fullClassName) {
        if (ACCEPTED.contains(fullClassName)) {
            return true;
        }
        for (String clazz : allow) {
            if (fullClassName.startsWith(clazz)) {
                ACCEPTED.add(fullClassName);
                return true;
            }
        }
        return false;
    }
}
