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

import java.io.InputStream;
import java.util.HashMap;

import org.apache.http.Header;

///**
// * WebDAVのアクセスクラス.
// */
/**
 * It creates a new object of WebDAV. It is the access class of WebDAV.
 */
public class WebDAV {
    /** Variable string body. */
    String stringBody;
    /** Variable stream body. */
    InputStream streamBody;
    /** Variable Headers. */
    HashMap<String, String> headers;
    // /** ステータスコード. */
    /** Variable Status Code. */
    int statusCode;

    // /**
    // * ステータスコードの取得.
    // * @return ステータスコード
    // */
    /**
     * This method returns the response status code value.
     * @return Status Code Value
     */
    public int getStatusCode() {
        return this.statusCode;
    }

    // /**
    // * ステータスコードの設定.
    // * @param code 設定するステータスコード
    // */
    /**
     * This method sets the response the status code value.
     * @param code value
     */
    void setStatusCode(int code) {
        this.statusCode = code;
    }

    // /**
    // * サーバーのレスポンスから取得した文字列を取得.
    // * @return 取得した文字列
    // */
    /**
     * This method returns the string body.
     * @return string body value
     */
    public String getStringBody() {
        return stringBody;
    }

    // /**
    // * サーバーのレスポンスから取得した文字列を設定.
    // * @param body 取得した文字列
    // */
    /**
     * This method sets the string body.
     * @param body string value
     */
    public void setStringBody(String body) {
        this.stringBody = body;
    }

    // /**
    // * サーバーのレスポンスから取得したストリームを取得.
    // * @return 取得したストリーム
    // */
    /**
     * This method returns the stream body.
     * @return Stream body value
     */
    public InputStream getStreamBody() {
        return streamBody;
    }

    // /**
    // * サーバーのレスポンスから取得したストリームを設定.
    // * @param body 取得したストリーム
    // */
    /**
     * This method sets the stream body.
     * @param body stream value
     */
    public void setStreamBody(InputStream body) {
        this.streamBody = body;
    }

    // /**
    // * サーバーのレスポンスから取得したレスポンスヘッダを取得.
    // * @return レスポンスヘッダの一覧
    // */
    /**
     * This method returns the headers list.
     * @return Headers
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    // /**
    // * サーバーのレスポンスから取得したレスポンスヘッダを設定.
    // * @param headerlist 設定するヘッダ
    // */
    /**
     * This method sets the response headers that are retrieved from the server response using header array.
     * @param headerlist Header to be set
     */
    public void setResHeaders(Header[] headerlist) {
        headers = new HashMap<String, String>();
        for (Header header : headerlist) {
            this.headers.put(header.getName(), header.getValue());
        }
    }

    // /**
    // * サーバーのレスポンスから取得したレスポンスヘッダを設定.
    // * @param headerlist 設定するヘッダ
    // */
    /**
     * This method sets the response headers that are retrieved from the server response using hashmap.
     * @param headerlist HashMap
     */
    public void setResHeaders(HashMap<String, String> headerlist) {
        this.headers = headerlist;
    }

    // /**
    // * 引数で指定されたヘッダの値を取得.
    // * @param headerKey 取得するヘッダのキー
    // * @return ヘッダの値
    // */
    /**
     * This method gets the value of the header that is specified in the argument.
     * @param headerKey Key
     * @return Header value
     */
    public String getHeaderValue(String headerKey) {
        return headers.get(headerKey);
    }
}
