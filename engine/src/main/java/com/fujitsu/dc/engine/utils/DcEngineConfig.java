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
package com.fujitsu.dc.engine.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 設定情報を保持するクラス. このクラスからクラスパス上にある dc-config.propertiesの内容にアクセスできます。
 */
public class DcEngineConfig {
    /**
     * dc-config.propertiesの設定ファイルパスキー.
     */
    static final String KEY_CONFIG_FILE = "io.personium.configurationFile";

    /**
     * 本アプリで使うプロパティキーのプレフィクス.
     */
    static final String KEY_ROOT = "io.personium.core.";
    static final String COMPATIBLE_KEY_ROOT = "io.personium.engine.";

    // For property compatibility.
    // Previous version of personium.io engine module had been used property names
    // starting with COMPATIBLE_KEY_ROOT.
    // Now, KEY_ROOT is used instead of COMPATIBLE_KEY_ROOT.
    static Map<String, String> compattibleToNormalKeyMap = new HashMap<String, String>();

    /**
     * Converts old style property name to current style property name.
     * If the format is different from old style property name, returns the argument value as is.
     * @param compatibleKey Stale property name
     * @return Current property name
     */
    static String convertCompatibleKeyToNormalKey(String compatibleKey) {
        if (compattibleToNormalKeyMap.containsKey(compatibleKey)) {
            return compattibleToNormalKeyMap.get(compatibleKey);
        }
        if (compatibleKey.startsWith(COMPATIBLE_KEY_ROOT)) {
            String normalKey = compatibleKey.replaceFirst(COMPATIBLE_KEY_ROOT, KEY_ROOT);
            compattibleToNormalKeyMap.put(compatibleKey, normalKey);
            return normalKey;
        }
        return compatibleKey;
    }

    /**
     * Elastic Search 関連の設定.
     */
    public static final class ES {
        /**
         * Elastic Search ホスト設定のプロパティキー.
         */
        public static final String HOSTS = KEY_ROOT + "elasticsearch.hosts";

        /**
         * Elastic Search クラスタ名設定のプロパティキー.
         */
        public static final String CLUSTERNAME = KEY_ROOT + "elasticsearch.cluster";
        /**
         * ルーティングフラグ .
         */
        public static final String ROUTING_FLAG = KEY_ROOT + "es.routingFlag";
        /**
         * ユニットプレフィックス .
         */
        public static final String UNIT_PREFIX = KEY_ROOT + "es.unitPrefix";
    }

    /**
     * debug.
     */
    public static final class DebugProp {
        /** 基底URL. */
        public static final String KEY_DEFAULT_BASE_URL = KEY_ROOT + "defaultBaseUrl";
    }

    /**
     * BinaryDataの設定.
     */
    public static final class BinaryData {
        /**
         * ファイルへの書き込み時にfsyncを有効にするか否か(true:有効 false:無効(デフォルト)).
         */
        public static final String FSYNC_ENABLED = KEY_ROOT + "binaryData.fsync.enabled";
    }

    /**
     * Blobの設定.
     */
    public static final class BlobStore {
        /**
         * Elastic Search を使用する際、blobデータを格納する方式設定のプロパティキー. 有効値: fs
         */
        public static final String TYPE = KEY_ROOT + "blobStore.type";

        /**
         * Elastic Search を使用する際、blobデータを格納するルート(URL, PATH)設定のプロパティキー.
         */
        public static final String ROOT = KEY_ROOT + "blobStore.root";
    }

    /**
     * マスタートークン設定のキー.
     */
    public static final String MASTER_TOKEN = KEY_ROOT + "masterToken";

    /**
     * X509廻りの設定.
     */
    public static final class X509 {
        /**
         * X509ルート証明書を配置したパス設定のプロパティキー.
         */
        public static final String ROOT_CRT = KEY_ROOT + "x509.root";
        /**
         * X509秘密鍵を配置したパス設定のプロパティキー.
         */
        public static final String KEY = KEY_ROOT + "x509.key";
        /**
         * X509証明書を配置したパス設定のプロパティキー.
         */
        public static final String CRT = KEY_ROOT + "x509.crt";
    }

    /**
     * Security廻りの設定.
     */
    public static final class Security {
        /**
         * トークンを暗号化する際に利用している秘密鍵.
         */
        public static final String TOKEN_SECRET_KEY = KEY_ROOT + "security.sercret16";

    }

    /**
     * バージョン廻りの設定.
     */
    public static final String CONFIG_VERSION = KEY_ROOT + "version";

    /**
     * バージョン情報を取得します.
     * @return バージョン情報
     */
    public static String getVersion() {
        return get(CONFIG_VERSION);
    }

    /**
     * ユニットマスタートークンの値を取得します.
     * @return マスタートークンの値
     */
    public static String getMasterToken() {
        return get(MASTER_TOKEN);
    }

    /**
     * 本UNITのX509秘密鍵ファイルのパスの設定値を取得します.
     * @return 設定値
     */
    public static String getX509PrivateKey() {
        return get(X509.KEY);
    }

    /**
     * 本UNITのX509ルート証明書ファイルのパスの設定値を取得します.
     * @return 設定値
     */
    public static String[] getX509RootCertificate() {
        String[] x509RootCertificate = null;
        String value = get(X509.ROOT_CRT);
        if (value != null) {
            x509RootCertificate = value.split(" ");
        }
        return x509RootCertificate;
    }

    /**
     * 本UNITのX509証明書ファイルのパスの設定値を取得します.
     * @return 設定値
     */
    public static String getX509Certificate() {
        return get(X509.CRT);
    }

    /**
     * トークンを暗号化する際に利用している秘密鍵設定.
     * @return 設定値
     */
    public static String getTokenSecretKey() {
        return get(Security.TOKEN_SECRET_KEY);
    }

    /**
     * singleton.
     */
    private static DcEngineConfig singleton = new DcEngineConfig();

    // static Logger log = LoggerFactory.getLogger(DcCoreConfig.class);

    /**
     * 設定値を格納するプロパティ実体.
     */
    private final Properties props = new Properties();

    /**
     * オーバーライドする設定値を格納するプロパティ実体.
     */
    private final Properties propsOverride = new Properties();

    /**
     * protectedなコンストラクタ.
     */
    protected DcEngineConfig() {
        this.doReload();
    }

    /**
     * 設定のリロード.
     */
    private synchronized void doReload() {

        // Clear the compatible key map.
        compattibleToNormalKeyMap.clear();

        Logger log = LoggerFactory.getLogger(DcEngineConfig.class);
        Properties properties = getDcConfigDefaultProperties();
        Properties propertiesOverride = getDcConfigProperties();
        // 読み込みに成功した場合、メンバ変数へ置換する
        if (!properties.isEmpty()) {
            this.props.clear();
            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                if (!(entry.getKey() instanceof String)) {
                    continue;
                }
                String key = convertCompatibleKeyToNormalKey((String) entry.getKey());
                this.props.setProperty(key, (String) entry.getValue());
            }
        }
        if (!propertiesOverride.isEmpty()) {
            this.propsOverride.clear();
            for (Map.Entry<Object, Object> entry : propertiesOverride.entrySet()) {
                if (!(entry.getKey() instanceof String)) {
                    continue;
                }
                String key = convertCompatibleKeyToNormalKey((String) entry.getKey());
                this.propsOverride.setProperty(key, (String) entry.getValue());
            }
        }
        for (Object keyObj : propsOverride.keySet()) {
            String key = (String) keyObj;
            String value = this.propsOverride.getProperty(key);
            if (value == null) {
                continue;
            }
            log.debug("Overriding Config " + key + "=" + value);
            this.props.setProperty(key, value);
        }
    }

    /**
     * dc-config-default.propertiesファイルを読み込む.
     * @return dc-config-default.properties
     */
    protected Properties getDcConfigDefaultProperties() {
        Properties properties = new Properties();
        InputStream is = DcEngineConfig.class.getClassLoader().getResourceAsStream("dc-config-default.properties");
        try {
            properties.load(is);
        } catch (IOException e) {
            throw new RuntimeException("failed to load config!", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                throw new RuntimeException("failed to close config stream", e);
            }
        }
        return properties;
    }

    /**
     * dc-config.propertiesファイルを読み込む.
     * @return dc-config.properties
     */
    protected Properties getDcConfigProperties() {
        Logger log = LoggerFactory.getLogger(DcEngineConfig.class);
        Properties properties = new Properties();
        String configFilePath = System.getProperty(KEY_CONFIG_FILE);
        InputStream is = getConfigFileInputStream(configFilePath);
        try {
            if (is != null) {
                properties.load(is);
            } else {
                log.debug("[dc-config.properties] file not found on the classpath. using default config.");
            }
        } catch (IOException e) {
            log.debug("IO Exception when loading [dc-config.properties] file.");
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                log.debug("IO Exception when closing [dc-config.properties] file.");
            }
        }
        return properties;
    }

    /**
     * dc-config.propertiesをInputStream形式で取得する.
     * @param configFilePath 設定ファイルパス
     * @return dc-config.properties
     */
    protected InputStream getConfigFileInputStream(String configFilePath) {
        Logger log = LoggerFactory.getLogger(DcEngineConfig.class);
        InputStream configFileInputStream = null;
        if (configFilePath == null) {
            configFileInputStream = DcEngineConfig.class.getClassLoader().getResourceAsStream("dc-config.properties");
            return configFileInputStream;
        }

        try {
            // 設定ファイルを指定されたパスから読み込む
            File configFile = new File(configFilePath);
            configFileInputStream = new FileInputStream(configFile);
            log.info("dc-config.properties from system properties.");
        } catch (FileNotFoundException e) {
            // 指定されたパスにファイルが存在しない場合は、クラスパス上のファイルを読み込む
            configFileInputStream = DcEngineConfig.class.getClassLoader().getResourceAsStream("dc-config.properties");
            log.info("dc-config.properties from class path.");
        }
        return configFileInputStream;
    }

    /**
     * 設定値の取得.
     * @param key キー
     * @return 設定値
     */
    private String doGet(final String key) {
        // For compatibility of old style property name prefix.
        String propKey = convertCompatibleKeyToNormalKey(key);
        return props.getProperty(propKey);
    }

    /**
     * すべてのプロパティを取得します。
     * @return プロパティ一覧オブジェクト
     */
    public static Properties getProperties() {
        return singleton.props;
    }

    /**
     * Key文字列を指定して設定情報を取得します.
     * @param key 設定キー
     * @return 設定値
     */
    public static String get(final String key) {
        return singleton.doGet(key);
    }

    /**
     * ElasticSearchのホスト名の設定値を取得します.
     * @return 設定値
     */
    public static String getEsHosts() {
        return get(ES.HOSTS);
    }

    /**
     * ElasticSearchのクラスタ名の設定値を取得します.
     * @return 設定値
     */
    public static String getEsClusterName() {
        return get(ES.CLUSTERNAME);
    }

    /**
     * 設定情報をリロードします.
     */
    public static void reload() {
        singleton.doReload();
    }

    /**
     * @return 基底URL.
     */
    public static String getDefaultBaseUrl() {
        return get(DebugProp.KEY_DEFAULT_BASE_URL);
    }

    /**
     * @return blobデータを格納する方式.
     */
    public static String getBlobStoreType() {
        return get(BlobStore.TYPE);
    }

    /**
     * @return blobデータを格納するルート(URL, PATH).
     */
    public static String getBlobStoreRoot() {
        return get(BlobStore.ROOT);
    }

    /**
     * @return ルーティングフラグ (trueの場合、ルーティング処理を行う).
     */
    public static boolean getRoutingFlag() {
        return Boolean.parseBoolean(get(ES.ROUTING_FLAG));
    }

    /**
     * @return ユニットプレフィックス
     */
    public static String getUnitPrefix() {
        return get(ES.UNIT_PREFIX);
    }

    /**
     * @return 有効である場合はtrue
     */
    public static boolean getFsyncEnabled() {
        return Boolean.parseBoolean(get(BinaryData.FSYNC_ENABLED));
    }
}
