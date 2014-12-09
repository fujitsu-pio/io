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
// * EntityTypeのアクセスクラス.
// */
/**
 * It creates a new object of EntityType. This class represents the EntityType object.
 */
public class EntityType extends AbstractODataContext {

    // /** キャメル方で表現したクラス名. */
    /** Class name in camel case. */
    private static final String CLASSNAME = "";

    // /** EntityType名. */
    /** EntityType name. */
    private String name;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor calling its parent constructor internally.
     */
    public EntityType() {
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
    public EntityType(final Accessor as) {
        this.initialize(as, null);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param body 生成するEntityTypeのJson
    // */
    /**
     * This is the parameterized constructor with two arguments calling initialize method internally.
     * @param as Accessor
     * @param body Json of EntityType
     */
    public EntityType(final Accessor as, JSONObject body) {
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
     * @param json Json of EntityType
     */
    public void initialize(Accessor as, JSONObject json) {
        super.initialize(as);
        if (json != null) {
            rawData = json;
            name = (String) json.get("Name");
        }
    }

    // /**
    // * EntityType名の設定.
    // * @param value EntityType名
    // */
    /**
     * This method sets the EntityType Name value.
     * @param value EntityTypeName
     */
    public void setName(String value) {
        this.name = value;
    }

    // /**
    // * EntityType名の取得.
    // * @return EntityType名
    // */
    /**
     * This method gets the EntityType Name value.
     * @return EntityTypeName value
     */
    public String getName() {
        return name;
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
        return String.format("('%s')", this.name);
    }

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns the EntityType class name in camel case.
     * @return EntityType class name
     */
    public String getClassName() {
        return CLASSNAME;
    }

}
