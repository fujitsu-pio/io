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
package com.fujitsu.dc.unit.engine.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fujitsu.dc.engine.utils.DcEngineConfig;
import com.fujitsu.dc.jersey.engine.test.categories.Integration;

/**
 * DcEngineConfig ユニットテストクラス.
 */
@Category({Integration.class })
public class DcEngineConfigTest {

    /**
     * ユニットテスト用クラス.
     */
    public class UnitDcEngineConfig extends DcEngineConfig {
        /**
         * コンストラクタ.
         */
        public UnitDcEngineConfig() {
            super();
        }

        /**
         * 設定ファイルを読み込む.
         * @param configFilePath 設定ファイルパス
         * @return 設定ファイルIS
         */
        public InputStream unitGetConfigFileInputStream(String configFilePath) {
            return this.getConfigFileInputStream(configFilePath);
        }
    }

    /**
     * 存在するプロパティファイルのパスを指定した場合_指定したパスのプロパティファイルを読み込むこと.
     */
    @Test
    public void 存在するプロパティファイルのパスを指定した場合_指定したパスのプロパティファイルを読み込むこと() {
        UnitDcEngineConfig dcEngineConfig = new UnitDcEngineConfig();
        Properties properties = new Properties();
        String configFilePath = ClassLoader.getSystemResource("dc-config.properties.unit").getPath();
        try {
            properties.load(dcEngineConfig.unitGetConfigFileInputStream(configFilePath));
        } catch (IOException e) {
            fail("properties load failuer");
        }
        assertEquals("unitTest", properties.getProperty("io.personium.engine.testkey"));
    }

    /**
     * 存在しないプロパティファイルのパスを指定した場合_クラスパス上のプロパティを読み込むこと.
     */
    @Test
    @Ignore
    public void 存在しないプロパティファイルのパスを指定した場合_クラスパス上のプロパティを読み込むこと() {
        UnitDcEngineConfig dcEngineConfig = new UnitDcEngineConfig();
        Properties properties = new Properties();
        try {
            properties.load(dcEngineConfig.unitGetConfigFileInputStream("dc-config.properties.unitx"));
        } catch (IOException e) {
            fail("properties load failuer");
        }
        assertEquals("unitTestDefault", properties.getProperty("io.personium.engine.testkey"));
    }

    /**
     * com.fujitsu.dc.engnie系プロパティで定義された内容が、com.fujitsu.dc.coreプロパティとして取得できること.
     */
    @Test
    public void com_fujitsu_dc_engnie系プロパティで定義された内容が_com_fujitsu_dc_coreプロパティとして取得できること() {
        System.setProperty("io.personium.configurationFile", "src/test/resources/dc-config.properties.unit");
        DcEngineConfig.reload();
        assertEquals("unitTest", DcEngineConfig.get("io.personium.engine.testkey"));
        assertEquals("unitTest", DcEngineConfig.get("io.personium.core.testkey"));

        assertEquals("keyWithCorePrefix", DcEngineConfig.get("io.personium.core.testKey2"));
        assertEquals("keyWithEnginePrefix", DcEngineConfig.get("io.personium.engine.testKey3"));
        assertEquals("keyWithEnginePrefix", DcEngineConfig.get("io.personium.core.testKey3"));
    }
}
