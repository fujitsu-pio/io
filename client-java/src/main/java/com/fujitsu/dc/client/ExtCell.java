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

import org.json.simple.JSONObject;

import com.fujitsu.dc.client.utils.Utils;

///**
// * ExtCellのアクセスクラス.
// */
/**
 * It creates a new object of ExtCell. This class represents External Cell to access its related fields.
 */
public class ExtCell extends AbstractODataContext {
    // /** クラス名. */
    /** Class name in camel case. */
    private static final String CLASSNAME = "ExtCell";

    /** url. */
    private String url;

    // /** Roleとのリンクマネージャ. */
    /** Link manager for Role. */
    public ODataLinkManager role;
    // /** Relationとのリンクマネージャ. */
    /** Link manager for Relation. */
    public ODataLinkManager relation;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor calling its parent constructor internally.
     */
    public ExtCell() {
        super();
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument calling initialize method internally.
     * @param as Accessor
     */
    public ExtCell(final Accessor as) {
        this.initialize(as, null);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param body 生成するExtCellのJson
    // */
    /**
     * This is the parameterized constructor with two arguments calling initialize method internally.
     * @param as Accessor
     * @param body ExtCell Requset Body
     */
    public ExtCell(final Accessor as, JSONObject body) {
        this.initialize(as, body);
    }

    // /**
    // * オブジェクトを初期化.
    // * @param as アクセス主体
    // * @param json サーバーから返却されたJSONオブジェクト
    // */
    /**
     * This method initializes the class and its parent class variables.
     * @param as Accessor
     * @param json ExtCell Requset Body
     */
    public void initialize(Accessor as, JSONObject json) {
        super.initialize(as);
        if (json != null) {
            rawData = json;
            url = (String) json.get("Url");
        }
        this.role = new ODataLinkManager(as, this);
        this.relation = new ODataLinkManager(as, this);
    }

    // /**
    // * urlの設定.
    // * @param value URL値
    // */
    /**
     * This method sets the URL for ExtCell.
     * @param value URL
     */
    public void setUrl(String value) {
        this.url = value;
    }

    // /**
    // * urlの取得.
    // * @return Role名
    // */
    /**
     * This method gets the URL for ExtCell.
     * @return URL value
     */
    public String getUrl() {
        return url;
    }

    // /**
    // * ODataのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method formats and returns the key.
     * @return OData Key
     */
    public String getKey() {
        return String.format("('%s')", Utils.escapeURI(this.url));
    }

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns the ExtCell class name in camel case.
     * @return ExtCell class name
     */
    public String getClassName() {
        return CLASSNAME;
    }

}
