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
// * Relationのアクセスクラス.
// */
/**
 * It creates a new object of Relation. This class represents Relation object.
 */
public class Relation extends AbstractODataContext {

    // /** クラス名. */
    /** Class name in camel case. */
    private static final String CLASSNAME = "Relation";

    // /** Relation名. */
    /** Relation Name. */
    private String name;
    /** _box.name. */
    private String boxname;

    // /** Roleとのリンクマネージャ. */
    /** Link Manager for Role. */
    public ODataLinkManager role;
    // /** ExtCellとのリンクマネージャ. */
    /** Link Manager for ExtCell. */
    public ODataLinkManager extCell;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor.
     */
    public Relation() {
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument and calling initialize method internally.
     * @param as Accessor
     */
    public Relation(final Accessor as) {
        this.initialize(as, null);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param body 生成するAccountのJson
    // */
    /**
     * This is the parameterized constructor with two arguments and calling initialize method internally.
     * @param as Accessor
     * @param body Request Body
     */
    public Relation(final Accessor as, JSONObject body) {
        this.initialize(as, body);
    }

    // /**
    // * オブジェクトを初期化.
    // * @param as アクセス主体
    // * @param json サーバーから返却されたJSONオブジェクト
    // */
    /**
     * This method is use to initialize various class variables and parent class variables.
     * @param as Accessor
     * @param json Relation JSONObject
     */
    public void initialize(Accessor as, JSONObject json) {
        super.initialize(as);
        if (json != null) {
            rawData = json;
            name = (String) json.get("Name");
            boxname = (String) json.get("_Box.Name");
        }
        this.role = new ODataLinkManager(as, this);
        this.extCell = new ODataLinkManager(as, this);
    }

    // /**
    // * Relation名の設定.
    // * @param value Relation名
    // */
    /**
     * This method sets the RelationName value.
     * @param value RelationName
     */
    public void setName(String value) {
        this.name = value;
    }

    // /**
    // * Relation名の取得.
    // * @return Relation名
    // */
    /**
     * This method gets the RelationName value.
     * @return RelationName value
     */
    public String getName() {
        return name;
    }

    // /**
    // * _box.name値の設定.
    // * @param value _box.name値
    // */
    /**
     * This method sets the _box.name value.
     * @param value _box.name
     */
    public void setBoxName(String value) {
        this.boxname = value;
    }

    // /**
    // * _box.name値の取得.
    // * @return _box.name値
    // */
    /**
     * This method gets the _box.name value.
     * @return _box.name value
     */
    public String getBoxName() {
        return boxname;
    }

    // /**
    // * Relationオブジェクトのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method formats and returns the key value for Relation operations.
     * @return OData Key URL for Relation
     */
    public String getKey() {
        if (this.boxname != null) {
            return String.format("(_Box.Name='%s',Name='%s')", this.boxname, this.name);
        } else {
            return String.format("(_Box.Name=null,Name='%s')", this.name);
        }
    }

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns the Relation class name in camel case.
     * @return ClassName
     */
    public String getClassName() {
        return Relation.CLASSNAME;
    }

}
