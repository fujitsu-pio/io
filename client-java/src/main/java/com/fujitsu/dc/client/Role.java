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
// * Roleのアクセスクラス.
// */
/**
 * It creates a new object of Role. This class represents Role object.
 */
public class Role extends AbstractODataContext implements Principal {

    // /** クラス名. */
    /** Class name in camel case. */
    private static final String CLASSNAME = "Role";

    // /** Role名. */
    /** Role Name. */
    private String name;
    /** _box.name. */
    private String boxname;

    // /** Accountとのリンクマネージャ. */
    /** Link Manager for Account. */
    public ODataLinkManager account;
    // /** Relationとのリンクマネージャ. */
    /** Link Manager for Relation. */
    public ODataLinkManager relation;
    // /** ExtCellとのリンクマネージャ. */
    /** Link Manager for ExtCell. */
    public ODataLinkManager extCell;
    // /** ExtRoleとのリンクマネージャ. */
    /** Link Manager for ExtRole. */
    public ODataLinkManager extRole;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor calling its parent constructor internally.
     */
    public Role() {
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
    public Role(final Accessor as) {
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
     * @param body Role JSONObject
     */
    public Role(final Accessor as, JSONObject body) {
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
     * @param json JSONObject
     */
    public void initialize(Accessor as, JSONObject json) {
        super.initialize(as);
        if (json != null) {
            rawData = json;
            name = (String) json.get("Name");
            boxname = (String) json.get("_Box.Name");
        }
        this.account = new ODataLinkManager(as, this);
        this.relation = new ODataLinkManager(as, this);
        this.extCell = new ODataLinkManager(as, this);
        this.extRole = new ODataLinkManager(as, this);
    }

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns the Role class name in camel case.
     * @return Class name
     */
    public String getClassName() {
        return Role.CLASSNAME;
    }

    // /**
    // * Role名の設定.
    // * @param value Role名
    // */
    /**
     * This method sets the RoleName value.
     * @param value RoleName
     */
    public void setName(String value) {
        this.name = value;
    }

    // /**
    // * Role名の取得.
    // * @return Role名
    // */
    /**
     * This method gets the RoleName value.
     * @return RoleName value
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
    // * Roleオブジェクトのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method formats and returns the URL key for Role operations.
     * @return OData Key for URL
     */
    public String getKey() {
        if (this.boxname != null) {
            return String.format("(_Box.Name='%s',Name='%s')", this.boxname, this.name);
        } else {
            return String.format("(_Box.Name=null,Name='%s')", this.name);
        }
    }

    // /**
    // * RoleResourceのURLを取得.
    // * @return RoleResouceURL
    // */
    /**
     * This method returns the Resource BaseURL for Role.
     * @return RoleResouceURL value
     */
    public String getResourceBaseUrl() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.accessor.getCurrentCell().getUrl());
        sb.append("__role/");
        if (this.boxname != null) {
            sb.append(this.boxname);
        } else {
            sb.append("__");
        }
        sb.append("/");
        return sb.toString();
    }

    /**
     * This method creates and returns the relative URL for Role operations.
     * @param baseBoxName value
     * @return Relative URL
     */
    String getRelativeUrl(String baseBoxName) {
        boolean validBoxName = this.boxname == null && baseBoxName == null;
        boolean validBaseBox = this.boxname != null && this.boxname.equals(baseBoxName);
        if (validBoxName || validBaseBox) {
            return this.name;
        } else {
            StringBuilder sb = new StringBuilder("../");
            String boxPath = this.boxname;
            if (boxPath == null) {
                boxPath = "__";
            }
            return sb.append(boxPath).append("/").append(this.name).toString();
        }

    }
}
