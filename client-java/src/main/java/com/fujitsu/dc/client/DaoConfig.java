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
package com.fujitsu.dc.client;

import org.apache.http.client.HttpClient;

import com.fujitsu.dc.client.http.RestAdapter;

///**
// * DAO動作のためのカスタマイズ可能な情報を管理するクラス.
// */
/**
 * It creates a new object of DaoConfig. This class is used for holding the operation setting information of JS-DAO and
 * manages a customizable information for the DAO action.
 */
public class DaoConfig {

    // /** デフォルトのProxyポート. */
    /** Default proxy Port. */
    public static final int DEFAULT_PROXY_PORT = 8080;

    // /** HTTPタイムアウト値. */
    /** HTTP time-out value. */
    private int connectionTimeout;
    // /** PUT/POST時にChunked指定をするかどうか. */
    /** Chunked specified in PUT/POST request. */
    private Boolean chunked;
    // /** テスト時に実通信を抑止するためのモッククラス. */
    /** Mock class to suppress the actual communication during the test. */
    private RestAdapter mockRestAdapter;
    // /** HttpClientクラス. */
    /** HttpClient class. */
    private HttpClient httpClient = null;
    // /** 通信を非同期を行うかどうか(現時点ではLogの書き込みのみ対応). */
    /** Threadable to decide for Asynchronous request. */
    private Boolean threadable = false;
    // /** Proxyホスト名. */
    /** Proxy host name. */
    private String proxyHostname = null;
    // /** Proxyポート. */
    /** Proxy Port. */
    private int proxyPort = DEFAULT_PROXY_PORT;
    /** Proxy user name. */
    private String proxyUsername = null;
    /** Proxy password. */
    private String proxyPassword = null;

    // /**
    // * HTTPタイムアウト値を習得.
    // * @return タイムアウト値
    // */
    /**
     * This method returns the HTTP timeout value.
     * @return HTTP timeout value
     */
    public final int getConnectionTimeout() {
        return connectionTimeout;
    }

    // /**
    // * HTTPタイムアウト値をセット.
    // * @param value タイムアウト値(ミリ秒)
    // */
    /**
     * This method sets the HTTP timeout value.
     * @param value Time-out
     */
    public final void setConnectionTimeout(final int value) {
        if (value < 0) {
            throw new IllegalArgumentException("timeout can't be negative");
        }
        this.connectionTimeout = value;
    }

    // /**
    // * HttpClientオブジェクト取得.
    // * @return HttpClientオブジェクト
    // */
    /**
     * This method returns the HttpClient object.
     * @return HttpClient object
     */
    public final HttpClient getHttpClient() {
        return this.httpClient;
    }

    // /**
    // * HttpClientオブジェクト設定.
    // * @param value HttpClientオブジェクト
    // */
    /**
     * This method sets the HttpClient object.
     * @param value HttpClient object
     */
    public final void setHttpClient(final HttpClient value) {
        this.httpClient = value;
    }

    // /**
    // * Chunked値の取得.
    // * @return Chunked値
    // */
    /**
     * This method returns the Chunked value.
     * @return Chunked value
     */
    public final Boolean getChunked() {
        return chunked;
    }

    // /**
    // * Chunked値の設定.
    // * @param value Chunked値
    // */
    /**
     * This method sets the Chunked value.
     * @param value Chunked
     */
    public final void setChunked(final Boolean value) {
        this.chunked = value;
    }

    // /**
    // * 非同期通信を行う旨のフラグを取得.
    // * @return 非同期フラグ
    // */
    /**
     * This method returns the Threadable value.
     * @return Threadable value
     */
    public final Boolean getThreadable() {
        return this.threadable;
    }

    // /**
    // * 非同期通信を行う旨のフラグを設定.
    // * @param value 非同期フラグ
    // */
    /**
     * This method sets the Threadable value.
     * @param value Threadable
     */
    public final void setThreadable(final Boolean value) {
        this.threadable = value;
    }

    // /**
    // * Proxyホスト名を取得する.
    // * @return Proxyホスト名
    // */
    /**
     * This method returns the Proxy host name.
     * @return Proxy Host name
     */
    public String getProxyHostname() {
        return proxyHostname;
    }

    // /**
    // * Proxyホスト名を設定する.
    // * @param value Proxyホスト名
    // */
    /**
     * This method sets the Proxy host name.
     * @param value Proxy Host name
     */
    public void setProxyHostname(String value) {
        this.proxyHostname = value;
    }

    // /**
    // * Proxyポート番号を取得する.
    // * @return Proxyポート番号
    // */
    /**
     * This method returns the proxy port number.
     * @return Proxy Port Number
     */
    public int getProxyPort() {
        return proxyPort;
    }

    // /**
    // * Proxyポート番号を設定する.
    // * @param value Proxyポート番号
    // */
    /**
     * This method sets the proxy port number.
     * @param value Proxy Port Number
     */
    public void setProxyPort(int value) {
        this.proxyPort = value;
    }

    /**
     * This method returns the Proxy user name.
     * @return Proxy User name
     */
    public String getProxyUsername() {
        return proxyUsername;
    }

    /**
     * This method sets the Proxy user name.
     * @param value Proxy User name
     */
    public void setProxyUsername(String value) {
        this.proxyUsername = value;
    }

    /**
     * This method returns the Proxy password.
     * @return Proxy Password
     */
    public String getProxyPassword() {
        return proxyPassword;
    }

    /**
     * This method sets the Proxy password.
     * @param value Proxy Password
     */
    public void setProxyPassword(String value) {
        this.proxyPassword = value;
    }

    // /**
    // * RestAdapterのモッククラスを取得.
    // * @return RestAdapterモッククラス
    // */
    /**
     * This method gets the mock class of RestAdapter.
     * @return RestAdapter object
     */
    public final RestAdapter getMockRestAdapter() {
        return mockRestAdapter;
    }

    // /**
    // * RestAdapterのモッククラスを設定.
    // * @param value RestAdapterモッククラス
    // */
    /**
     * This method sets the mock class of RestAdapter.
     * @param value RestAdapter object
     */
    public final void setMockRestAdapter(final RestAdapter value) {
        this.mockRestAdapter = value;
    }
}
