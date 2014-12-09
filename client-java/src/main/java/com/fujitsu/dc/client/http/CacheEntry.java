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

import java.util.HashMap;

import org.apache.http.Header;
import org.apache.http.HttpHeaders;

///**
// * CacheEntryクラス.
// */
/**
 * It creates a new object of CacheEntry.
 */
public class CacheEntry {
    // /** レスポンスヘッダー値. */
    /** Response header value. */
    private HashMap<String, String> headers = new HashMap<String, String>();
    // /** URL値. */
    /** URL value. */
    private String url;
    // /** Body値. */
    /** Body value. */
    private String body;

    // /**
    // * コンストラクタ.
    // * @param paramUrl URL値
    // * @param paramHeader レスポンスヘッダー値
    // * @param paramBody Body値
    // */
    /**
     * This is the parameterized constructor used for initializing various class variables.
     * @param paramUrl URL Value
     * @param paramHeader Response header value
     * @param paramBody Body Value
     */
    public CacheEntry(final String paramUrl, final Header[] paramHeader, final String paramBody) {
        this.url = paramUrl;
        for (Header header : paramHeader) {
            this.headers.put(header.getName(), header.getValue());
        }
        this.body = paramBody;
    }

    // /**
    // * Etagの取得.
    // * @return Etag値
    // */
    /**
     * This method is used to get etag value.
     * @return Etag value
     */
    public final String getEtag() {
        return headers.get(HttpHeaders.ETAG);
    }

    // /**
    // * レスポンスヘッダーの取得.
    // * @return レスポンスヘッダー値
    // */
    /**
     * This method is used to get response headers.
     * @return Response header value
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    // /**
    // * レスポンスヘッダーの設定.
    // * @param value レスポンスヘッダー値
    // */
    /**
     * This method is used to set the response headers using Header Array.
     * @param value Response header value
     */
    public void setHeaders(Header[] value) {
        this.headers.clear();
        for (Header header : value) {
            this.headers.put(header.getName(), header.getValue());
        }
    }

    // /**
    // * レスポンスヘッダーの設定.
    // * @param value レスポンスヘッダー値
    // */
    /**
     * This method is used to set the response headers using Header Map.
     * @param value Response header value
     */
    public void setHeaders(HashMap<String, String> value) {
        this.headers = value;
    }

    // /**
    // * URLの取得.
    // * @return URL値
    // */
    /**
     * This method is used to get the URL value.
     * @return URL value
     */
    public final String getUrl() {
        return url;
    }

    // /**
    // * URLの設定.
    // * @param value URL値
    // */
    /**
     * This method is used to set the URL value.
     * @param value URL
     */
    public final void setUrl(final String value) {
        this.url = value;
    }

    // /**
    // * Bodyの取得.
    // * @return Body値
    // */
    /**
     * This method is used to get the Body value.
     * @return Body value
     */
    public final String getBody() {
        return body;
    }

    // /**
    // * Bodyの設定.
    // * @param value Body値
    // */
    /**
     * This method is used to set the Body value.
     * @param value Body
     */
    public final void setBody(final String value) {
        this.body = value;
    }

}
