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

import org.json.simple.JSONObject;

///**
// * personium.ioの各機能を現したクラスの抽象クラス.
// */
/**
 * It creates a new object of AbstractODataContext. This is the super class inherited by other cell control classes
 * showing function of each entity.
 */
public abstract class AbstractODataContext implements ILinkageResource {
    // /** アクセス主体. */
    /** Accessor reference. */
    Accessor accessor;

    // /** 登録した時のJSONデータ . */
    /** JSON data at the time of the registration. */
    JSONObject rawData;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor calling its parent constructor.
     */
    public AbstractODataContext() {
        super();
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the overloaded version of constructor with parameter.
     * @param as Accessor
     */
    public AbstractODataContext(Accessor as) {
        this.accessor = as.clone();
    }

    // /**
    // * 初期化.
    // * @param as アクセス主体
    // */
    /**
     * This method is used to initialize the class variable accessor.
     * @param as Accessor
     */
    public void initialize(Accessor as) {
        this.accessor = as.clone();
    }

    // /**
    // * 登録した時のJSONデータ を取得する.
    // * @return 登録した時のJSONデータ
    // */
    /**
     * This method gets the JSON data after registration.
     * @return rawData - Class variable representing JSON data
     */
    public JSONObject getRawData() {
        return rawData;
    }

    // /**
    // * 登録した時のJSONデータを設定する.
    // * @param json 登録した時のJSONデータ
    // */
    /**
     * This method sets the JSON data during registration.
     * @param json JSON data at the time of registration
     */
    public void setRawData(JSONObject json) {
        this.rawData = json;
    }

    // /**
    // * アクセス主体を設定する.
    // * @param as アクセス主体
    // */
    /**
     * This method sets the Accessor object in the class variable.
     * @param as Accessor
     */
    public void setAccessor(Accessor as) {
        this.accessor = as;
    }

    // /**
    // * アクセス主体を取得する.
    // * @return アクセス主体
    // */
    /**
     * This method gets the Accessor object which is the class variable.
     * @return accessor
     */
    public Accessor getAccessor() {
        return this.accessor;
    }

    // /**
    // * ODataへのリンクを取得する.
    // * @return ODataへのリンク
    // */
    /**
     * This method gets the OData link in string format.
     * @return Link to OData
     */
    public String getODataLink() {
        return (String) ((JSONObject) rawData.get("__metadata")).get("uri");
    }

    // /**
    // * ODataのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This is an abstract method to return OData key which can be implemented by the sub classes as per their
     * behaviour.
     * @return OData Key
     */
    public abstract String getKey();

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This is an abstract method to return ClassName in camel case for its subclasses.
     * @return OData ClassName
     */
    public abstract String getClassName();

    // /**
    // * 引数で指定されたヘッダの値を取得.
    // * @param headerKey 取得するヘッダのキー
    // * @return ヘッダの値
    // */
    /**
     * This method gets the value of the header that is specified in the argument.
     * @param headerKey Header Key in string format
     * @return The value of header based on the key
     */
    public String getHeaderValue(String headerKey) {
        HashMap<String, String> resHeaders = this.accessor.getResHeaders();
        return resHeaders.get(headerKey);
    }

    // /**
    // * JSON文字列を返却.
    // * @return JSON文字列
    // */
    /**
     * This method converts the resposne from JSON form to String format.
     * @return JSON String
     */
    public String toJSONString() {
        return this.rawData.toJSONString();
    }

    // /**
    // * $linksへのURLを取得.
    // * @return URL文字列.
    // */
    /**
     * This method generates the URL for $link commands.
     * @return URL string.
     */
    public String makeUrlForLink() {
        StringBuilder sb = new StringBuilder(this.accessor.getCurrentCell().getUrl());
        sb.append("__ctl/");
        sb.append(this.getClassName());
        sb.append(this.getKey());
        sb.append("/$links/");
        return sb.toString();
    }
}
