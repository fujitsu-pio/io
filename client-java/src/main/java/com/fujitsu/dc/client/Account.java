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

///**
// * Accountのアクセスクラス.
// */
/**
 * It creates a new object of Account. This class creates an Account as cell control object.
 */
public class Account extends AbstractODataContext {
    // /** クラス名. */
    /** Class name in camel case. */
    private static final String CLASSNAME = "Account";

    // /** Account名. */
    /** Account name. */
    private String name;
    // /** パスワード.オブジェクト渡しでAccountを作成する時にだけ利用できる.その後は削除する. */
    /** It is available only when you create the Account. */
    private String password;

    // /** Roleとのリンクマネージャ. */
    /** Link Manager for Role. */
    public ODataLinkManager role;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor calling its parent constructor.
     */
    public Account() {
        super();
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the overloaded version of constructor with one parameter.
     * @param as Accessor
     */
    public Account(final Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param body 生成するAccountのJson
    // */
    /**
     * This is the overloaded version of constructor with two parameters.
     * @param as Accessor
     * @param body JSONObject
     */
    public Account(final Accessor as, JSONObject body) {
        this.initialize(as, body);
    }

    // /**
    // * オブジェクトを初期化.
    // * @param as アクセス主体
    // * @param json サーバーから取得したJSONオブジェクト
    // */
    /**
     * This method is used to initialize the various class variables.
     * @param as Accessor
     * @param json JSON object
     */
    public void initialize(Accessor as, JSONObject json) {
        super.initialize(as);
        rawData = json;
        name = (String) json.get("Name");
        // パスワードは保持しない
        /** Reset password. */
        this.setPassword("");
        this.role = new ODataLinkManager(as, this);
    }

    // /**
    // * Account名の設定.
    // * @param value Account名
    // */
    /**
     * This method sets theAccount name value.
     * @param value Account Name
     */
    public void setName(String value) {
        this.name = value;
    }

    // /**
    // * Account名の取得.
    // * @return Account名
    // */
    /**
     * This method gets the Account Name value.
     * @return Account Name value
     */
    public String getName() {
        return name;
    }

    // /**
    // * パスワードの設定.
    // * @param value パスワード文字列
    // */
    /**
     * This method sets the password.
     * @param value Password
     */
    public void setPassword(String value) {
        this.password = value;
    }

    // /**
    // * パスワードの取得.
    // * @return パスワード文字列
    // */
    /**
     * This method gets the password.
     * @return Password
     */
    public String getPassword() {
        return this.password;
    }

    // /**
    // * ODataのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method formats and returns the key.
     * @return OData Key value
     */
    public String getKey() {
        return String.format("('%s')", this.name);
    }

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns the class name in camel case.
     * @return OData Classname
     */
    public String getClassName() {
        return CLASSNAME;
    }

}
