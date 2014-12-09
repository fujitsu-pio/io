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
package com.fujitsu.dc.client.http;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

///**
// * HttpClientの実装を切り替えてNewする.
// */
/**
 * This class is used for switching the implementation of HttpClient.
 */
public class HttpClientFactory extends DefaultHttpClient {
    // /** HTTP通信のタイプ. */
    /** Default Type of HTTP communication. */
    public static final String TYPE_DEFAULT = "default";
    // /** HTTP通信のタイプ. */
    /** Insecure Type of HTTP communication. */
    public static final String TYPE_INSECURE = "insecure";
    // /** HTTP通信のタイプ. */
    /** Android Type of HTTP communication. */
    public static final String TYPE_ANDROID = "android";

    /** PORT SSL. */
    private static final int PORTHTTPS = 443;
    /** PORT HTTP. */
    private static final int PORTHTTP = 80;

    // /** デフォルトの接続タイムアウト値(0の場合はタイムアウトしない). */
    /** (No time-out in the case of 0) connection timeout value of default. */
    private static final int TIMEOUT = 0;

    // /**
    // * HTTPClientオブジェクトを作成.
    // * @param type 通信タイプ
    // * @param connectionTimeout タイムアウト値(ミリ秒)。0の場合はデフォルト値を利用する。
    // * @return 作成したHttpClientクラスインスタンス
    // */
    /**
     * This method is used to create a HTTPClient object.
     * @param type Type of communication
     * @param connectionTimeout Iime-out value (in milliseconds). Use the default value of 0.
     * @return HttpClient class instance that is created
     */
    @SuppressWarnings("deprecation")
    public static HttpClient create(final String type, final int connectionTimeout) {
        if (TYPE_DEFAULT.equalsIgnoreCase(type)) {
            return new DefaultHttpClient();
        }

        SSLSocketFactory sf = null;
        Scheme httpScheme = null;
        Scheme httpsScheme = null;
        if (TYPE_INSECURE.equalsIgnoreCase(type)) {
            sf = createInsecureSSLSocketFactory();
            httpScheme = new Scheme("https", PORTHTTPS, sf);
            httpsScheme = new Scheme("http", PORTHTTP, PlainSocketFactory.getSocketFactory());
        } else if (TYPE_ANDROID.equalsIgnoreCase(type)) {
            try {
                sf = new InsecureSSLSocketFactory(null);
            } catch (KeyManagementException e) {
                return null;
            } catch (UnrecoverableKeyException e) {
                return null;
            } catch (NoSuchAlgorithmException e) {
                return null;
            } catch (KeyStoreException e) {
                return null;
            }
            httpScheme = new Scheme("https", sf, PORTHTTPS);
            httpsScheme = new Scheme("http", PlainSocketFactory.getSocketFactory(), PORTHTTP);
        }

        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpScheme);
        schemeRegistry.register(httpsScheme);
        HttpParams params = new BasicHttpParams();
        ClientConnectionManager cm = null;
        if (TYPE_INSECURE.equalsIgnoreCase(type)) {
            cm = new SingleClientConnManager(schemeRegistry);
        } else if (TYPE_ANDROID.equalsIgnoreCase(type)) {
            cm = new SingleClientConnManager(params, schemeRegistry);
        }
        HttpClient hc = new DefaultHttpClient(cm, params);

        HttpParams params2 = hc.getParams();
        int timeout = TIMEOUT;
        if (connectionTimeout != 0) {
            timeout = connectionTimeout;
        }
        // 接続のタイムアウト
        /** Connection timed out. */
        HttpConnectionParams.setConnectionTimeout(params2, timeout);
        // データ取得のタイムアウト
        /** Time-out of the data acquisition. */
        HttpConnectionParams.setSoTimeout(params2, timeout);
        // リダイレクトしない
        /** Do Not redirect. */
        HttpClientParams.setRedirecting(params2, false);
        return hc;
    }

    // /**
    // * SSLSocketを生成.
    // * @return 生成したSSLSocket
    // */
    /**
     * This method is used to generate SSLSocket.
     * @return SSLSocket that is generated
     */
    private static SSLSocketFactory createInsecureSSLSocketFactory() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("SSL");
        } catch (NoSuchAlgorithmException e1) {
            throw new RuntimeException(e1);
        }

        try {
            sslContext.init(null, new TrustManager[] {new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    // System.out.println("getAcceptedIssuers =============");
                    X509Certificate[] ret = new X509Certificate[0];
                    return ret;
                }

                public final void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                    // System.out.println("checkClientTrusted =============");
                }

                public final void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                    // System.out.println("checkServerTrusted =============");
                }
            } }, new SecureRandom());
        } catch (KeyManagementException e1) {
            throw new RuntimeException(e1);
        }

        HostnameVerifier hostnameVerifier = SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
        SSLSocketFactory socketFactory = new SSLSocketFactory(sslContext, (X509HostnameVerifier) hostnameVerifier);
        // socketFactory.setHostnameVerifier((X509HostnameVerifier) hostnameVerifier);

        return socketFactory;
    }
}
