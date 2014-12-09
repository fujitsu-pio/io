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
// * ComplexTypeのアクセスクラス.
// */
/**
 * It creates a new object of ComplexType. This class represents ComplexType object to perform ComplexType related
 * operations.
 */
public class ComplexType extends AbstractODataContext {
    // /** キャメル型で表現したクラス名. */
    /** Class Name in camel case. */
    private static final String CLASSNAME = "ComplexType";
    // /** ComplexType名. */
    /** ComplexType Name. */
    private String name;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the deafult constructor for ComplexType.
     */
    public ComplexType() {
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one parameter. It calls its parent constructor internally.
     * @param as Accessor
     */
    public ComplexType(final Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param body 生成するComplexTypeのJson
    // */
    /**
     * This is the parameterized constructor with two parameters. It calls initialize method internally.
     * @param as Accessor
     * @param body ComplexType Json
     */
    public ComplexType(final Accessor as, JSONObject body) {
        this.initialize(as, body);
    }

    // /**
    // * オブジェクトを初期化.
    // * @param as アクセス主体
    // * @param json サーバーから取得したJSONオブジェクト
    // */
    /**
     * This method is used to initialize class and super class variables.
     * @param as Accessor
     * @param json JSON object
     */
    public void initialize(Accessor as, JSONObject json) {
        super.initialize(as);
        rawData = json;
        name = (String) json.get("Name");
    }

    // /**
    // * ComplexType名の設定.
    // * @param value ComplexType名
    // */
    /**
     * This method sets the ComplexType Name value.
     * @param value ComplexType Name
     */
    public void setName(String value) {
        this.name = value;
    }

    // /**
    // * ComplexType名の取得.
    // * @return ComplexType名
    // */
    /**
     * This method gets the ComplexType Name value.
     * @return ComplexType Name value
     */
    public String getName() {
        return this.name;
    }

    // /**
    // * ODataのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method formats and returns the key for ComplexType.
     * @return Key value
     */
    public String getKey() {
        return String.format("(Name='%s')", this.name);
    }

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns the class name for ComplexType in camel case.
     * @return ComplexType Class name
     */
    public String getClassName() {
        return CLASSNAME;
    }

}
