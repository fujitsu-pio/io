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
package com.fujitsu.dc.jersey.engine.test;

import com.fujitsu.dc.engine.utils.DcEngineConfig;

/**
 * Engineのテスト用設定を定義するクラス.
 */
public class DcEngineTestConfig {

    private static final String PROP_TARGET_VERSION = "com.fujitsu.dc.test.target.version";

    private DcEngineTestConfig() {
    }

    /**
     * テスト時に指定するターゲットバージョンを取得する. <br />
     * システムプロパティで明示している場合は、システムプロパティの値を優先する.
     * @return テスト時に指定するターゲットバージョン
     */
    public static String getVersion() {
        return System.getProperty(PROP_TARGET_VERSION, DcEngineConfig.getVersion());
    }

}
