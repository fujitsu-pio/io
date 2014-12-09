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
// * Roleのアクセスクラス.
// */
/**
 * It creates a new object of ExtRole. This class represents External Role to access its related fields.
 */
public class ExtRole extends AbstractODataContext {

    // /** クラス名. */
    /** Class name in camel case. */
    private static final String CLASSNAME = "ExtRole";

    // /** ExtRole. */
    /** ExtRole Name. */
    private String name;
    // /** _Relation.Name. */
    /** Relation Name. */
    private String relationName;
    /** Relation._Box.Name. */
    private String relationBoxName;

    // /** Roleとのリンクマネージャ. */
    /** Link Manager for Role. */
    public ODataLinkManager role;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor calling its parent constructor internally.
     */
    public ExtRole() {
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
    public ExtRole(final Accessor as) {
        this.initialize(as, null);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param body 生成するAccountのJson
    // */
    /**
     * This is the parameterized constructor with two arguments calling initialize method internally.
     * @param as Accessor
     * @param body ExtRole Request Body
     */
    public ExtRole(final Accessor as, JSONObject body) {
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
     * @param json ExtRole Request Body
     */
    public void initialize(Accessor as, JSONObject json) {
        super.initialize(as);
        if (json != null) {
            rawData = json;
            name = (String) json.get("ExtRole");
            relationName = (String) json.get("_Relation.Name");
            relationBoxName = (String) json.get("_Relation._Box.Name");
        }
        this.role = new ODataLinkManager(as, this);
    }

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns the ExtRole class name in camel case.
     * @return ExtRole class name
     */
    public String getClassName() {
        return ExtRole.CLASSNAME;
    }

    // /**
    // * ExtRoleの設定.
    // * @param value ExtRole
    // */
    /**
     * This method sets the ExtRole Name value.
     * @param value ExtRole
     */
    public void setName(String value) {
        this.name = value;
    }

    // /**
    // * ExtRoleの取得.
    // * @return ExtRole
    // */
    /**
     * This method gets the ExtRole Name value.
     * @return ExtRole value
     */
    public String getName() {
        return name;
    }

    // /**
    // * _Relation.Name値の設定.
    // * @param value _Relation.Name値
    // */
    /**
     * This method sets the _Relation.Name.
     * @param value _Relation.Name
     */
    public void setRelationName(String value) {
        this.relationName = value;
    }

    // /**
    // * _Relation.Name値の取得.
    // * @return _Relation.Name値
    // */
    /**
     * This method gets the _Relation.Name.
     * @return _Relation.Name value
     */
    public String getRelationName() {
        return relationName;
    }

    // /**
    // * __Relation._Box.Name値の設定.
    // * @param value __Relation._Box.Name値
    // */
    /**
     * This method sets the __Relation._Box.Name.
     * @param value __Relation._Box.Name
     */
    public void setRelationBoxName(String value) {
        this.relationBoxName = value;
    }

    // /**
    // * __Relation._Box.Name値の取得.
    // * @return __Relation._Box.Name値
    // */
    /**
     * This method gets the __Relation._Box.Name.
     * @return __Relation._Box.Name value
     */
    public String getRelationBoxName() {
        return relationBoxName;
    }

    // /**
    // * ExtRoleオブジェクトのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method prepares the key for ExtRole as per fields.
     * @return ExtRole key value
     */
    public String getKey() {

        StringBuilder sb = new StringBuilder();
        sb.append("(ExtRole='").append(Utils.escapeURI(this.name)).append("'");
        if (this.relationName != null) {
            sb.append(",_Relation.Name='").append(this.relationName).append("'");
        }
        if (this.relationBoxName != null) {
            sb.append(",_Relation._Box.Name='").append(this.relationBoxName).append("'");
        }
        sb.append(")");
        return sb.toString();
    }
}
