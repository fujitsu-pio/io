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

import java.util.HashMap;

import org.apache.http.Header;
import org.json.simple.JSONObject;

///**
// * ユーザデータのアクセスクラス.
// */
/**
 * It creates a new object of Entity. This is the abstract class for a collection.
 */
public class Entity implements ILinkageResource {
    // /** レスポンスヘッダー一覧. */
    /** Response header list. */
    HashMap<String, String> headers = new HashMap<String, String>();
    // /** Accountとのリンクマネージャ. */
    /** Link Manager for OData. */
    public ODataLinkManager entity;
    // /** ID値. */
    /** ID value. */
    private String id;
    // /** 登録した時のJSONデータ . */
    /** JSON data at the time of registration. */
    JSONObject rawData;

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体.
    // * @param body 生成するEntityのJson
    // */
    /**
     * This is the parameterized constructor with two arguments. It initializes various class variables.
     * @param as Accessor
     * @param body JSONObject
     */
    public Entity(Accessor as, JSONObject body) {
        this.rawData = body;
        this.id = (String) body.get("__id");
        this.entity = new ODataLinkManager(as, this);
    }

    // /**
    // * サーバーのレスポンスから取得したレスポンスボディを取得.
    // * @return レスポンスボディー
    // */
    /**
     * This method gets the response body that is retrieved from the server response.
     * @return Response Body
     */
    public JSONObject getBody() {
        return rawData;
    }

    // /**
    // * サーバーのレスポンスから取得したレスポンスボディーを設定.
    // * @param body レスポンスボディー
    // */
    /**
     * This method sets the response body that is retrieved from the server response.
     * @param body Response Body
     */
    public void setBody(JSONObject body) {
        this.rawData = body;
    }

    // /**
    // * サーバーのレスポンスから取得したレスポンスヘッダを取得.
    // * @return レスポンスヘッダの一覧
    // */
    /**
     * This method gets the response headers that are retrieved from the server response.
     * @return List of response headers
     */
    public HashMap<String, String> getHeaders() {
        return headers;
    }

    // /**
    // * サーバーのレスポンスから取得したレスポンスヘッダを設定.
    // * @param headerlist 設定するヘッダ
    // */
    /**
     * This method sets the response headers that are retrieved from the server response.
     * @param headerlist List of response headers
     */
    public void setResHeaders(Header[] headerlist) {
        for (Header header : headerlist) {
            this.headers.put(header.getName(), header.getValue());
        }
    }

    // /**
    // * 引数で指定されたヘッダの値を取得.
    // * @param headerKey 取得するヘッダのキー
    // * @return ヘッダの値
    // */
    /**
     * This method gets the value of the header that is specified in the argument.
     * @param headerKey Key of the header
     * @return Value of the header
     */
    public String getHeaderValue(String headerKey) {
        return headers.get(headerKey);
    }

    /**
     * This method formats and returns the key.
     * @return key value
     */
    @Override
    public String getKey() {
        return String.format("('%s')", this.id);
    }

    /**
     * This method returns the entity name fetched from response.
     * @return EntityName value
     */
    @Override
    public String getClassName() {
        String type = (String) ((JSONObject) rawData.get("__metadata")).get("type");
        String entityName = type.substring(type.indexOf(".") + 1);
        return entityName;
    }

    /**
     * This method returns the URL fetched from response.
     * @return Data URI value
     */
    @Override
    public String getODataLink() {
        return (String) ((JSONObject) rawData.get("__metadata")).get("uri");
    }

    /**
     * This method creates URL for Entity operations.
     * @return URL value
     */
    @Override
    public String makeUrlForLink() {
        String url = this.getODataLink();
        url += "/$links/";
        return url;
    }
}
