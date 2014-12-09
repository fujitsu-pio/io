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

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.ScriptableObject;

/**
 * Extension用クラスの基底クラス.
 * ログ機能を提供するために作成。
 */
@SuppressWarnings("serial")
public abstract class AbstractExtensionScriptableObject extends ScriptableObject {

    /**
     * 外部から setLogger()を呼ばれることでロガー設定が行われるが、万が一失敗した時に備え、何もしないロガーを設定しておく。
     */
    private static NullLogger nullLogger = new NullLogger(AbstractExtensionScriptableObject.class);

    /**
      * ロガーオブジェクトマップ.
      */
    private static Map<Class<? extends AbstractExtensionScriptableObject>, IExtensionLogger> loggerMap
        = new ConcurrentHashMap<Class<? extends AbstractExtensionScriptableObject>, IExtensionLogger>();

    /**
     * プロパティ.
     */
    private Properties properties = null;

    /**
     * コンストラクタ.
     */
    public AbstractExtensionScriptableObject() {
        // クラス名を基にプロパティファイルをロードする。
        properties = new Properties();
        String propFileName = String.format("Ext_%s.properties", this.getClassName());
        InputStream propStream = null;
        try {
            // クラスローダ―内のクラスパスを基に、プロパティを読み込む。
            // JavaScript公開のクラス名が {xxxx}だった場合、EXT_{xxxx}.properties を読み込む。
            propStream = this.getClass().getClassLoader().getResourceAsStream(propFileName);
            if (null != propStream) {
                properties.load(propStream);
            }
        } catch (IOException e) {
            // そもそもファイルを作成しない/必要ないケースがあるため、読めなくてもエラーとしない。
            getLogger().debug(
                 String.format("Property file for %s does not exist. Ignoring...", propFileName));
        } finally {
            IOUtils.closeQuietly(propStream);
        }
    }


    /**
     * JavaScript内で利用するクラス名を返却する.
     * @return クラス名(JavaScript)
     */
    public abstract String getClassName();

    /**
     * ロガーオブジェクトを設定する.
     * @param clazz ロガーが属するクラス
     * @param log ロガーオブジェクト
     */
    public static void setLogger(Class<? extends AbstractExtensionScriptableObject> clazz, IExtensionLogger log) {
        loggerMap.put(clazz, log);
    }

    /**
     * ロガーオブジェクトを返却する.
     * @return ロガーオブジェクト
     */
    public IExtensionLogger getLogger() {
        if (loggerMap.containsKey(this.getClass())) {
            return loggerMap.get(this.getClass());
        } else {
            return nullLogger;
        }
    }

    /**
     * プロパティを返却する.
     * @return プロパティ
     */
    public Properties getProperties() {
        return properties;
    }
}
