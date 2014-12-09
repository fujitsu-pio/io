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
// * Boxへアクセスするためのクラス.
// */
/**
 * It creates a new object of Box. This class represents Box to access box related fields.
 */
public class Box extends DavCollection implements ILinkageResource {
    // /** クラス名. */
    /** Class Name in camel case. */
    private static final String CLASSNAME = "Box";

    // /** Box名. */
    /** Box name. */
    private String name;
    // /** schema名. */
    /** Schema Name. */
    private String schema;

    // CHECKSTYLE:OFF
    // /** boxレベルEventへアクセスするためのクラス. */
    /** Class to access the box level Event. */
    public EventManagerForBox event;
    // CHECKSTYLE:ON

    // /** Roleとのリンクマネージャ. */
    /** Link manager of the Role. */
    public ODataLinkManager role;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor calling its parent constructor.
     */
    public Box() {
        super();
    }

    // /**
    // * オブジェクトを初期化.
    // * @param as アクセスス主体
    // * @param json サーバーから取得したJSONオブジェクト
    // * @param path BoxまでのURLパス
    // */
    /**
     * This method is used to initialize various class variables.
     * @param as Accessor
     * @param json JSON object
     * @param path BoxURL
     */
    public void initialize(Accessor as, JSONObject json, String path) {
        super.initialize(as);
        url = new StringBuilder(path);
        this.name = (String) json.get("Name");
        this.schema = (String) json.get("Schema");
        this.acl = new AclManager(as, this);
        this.event = new EventManagerForBox(as);
        this.role = new ODataLinkManager(as, this);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param boxName Box名
    // * @param schemaValue スキーマ名
    // * @param path BoxまでのURLパス
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the parameterized constructor with four parameters and setting the various class variables.
     * @param as Accessor
     * @param boxName Box Name
     * @param schemaValue Schema Value
     * @param path Box URL
     * @throws DaoException Exception thrown
     */
    public Box(Accessor as, String boxName, String schemaValue, String path) throws DaoException {
        super(as, path);
        this.name = boxName;
        this.schema = schemaValue;

        this.acl = new AclManager(accessor, this);
        this.event = new EventManagerForBox(accessor);
        this.role = new ODataLinkManager(as, this);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param body 生成するBoxのJson
    // * @param path BoxまでのURLパス
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the parameterized constructor with three parameters and calling another constructor.
     * @param as Accessor
     * @param body JSONObject
     * @param path Box URL
     * @throws DaoException Exception thrown
     */
    public Box(Accessor as, JSONObject body, String path) throws DaoException {
        this(as, (String) body.get("Name"), (String) body.get("Schema"), path);
    }

    // /**
    // * Box名を取得.
    // * @return Box名
    // */
    /**
     * This method returns the box name.
     * @return Box Name value
     */
    public String getName() {
        return this.name;
    }

    // /**
    // * Boxを設定.
    // * @param value Box名
    // */
    /**
     * This method sets the box name.
     * @param value Box Name
     */
    public void setName(String value) {
        this.name = value;
    }

    // /**
    // * スキーマを取得.
    // * @return スキーマ
    // */
    /**
     * This method returns the schema.
     * @return schema
     */
    public String getSchema() {
        return this.schema;
    }

    // /**
    // * スキーマを設定.
    // * @param value スキーマ
    // */
    /**
     * This method sets the schema.
     * @param value Schema
     */
    public void setSchema(String value) {
        this.schema = value;
    }

    // /**
    // * JSONオブジェクトを生成する.
    // * @return 生成したJSONオブジェクト
    // */
    /**
     * This method create the JSON object for Box.
     * @return JSON object
     */
    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("Name", this.name);
        json.put("Schema", this.schema);
        return json;
    }

    /**
     * This method formats and returns the key for Box.
     * @return value Key
     */
    @Override
    public String getKey() {
        if (this.schema != null) {
            return String.format("(Schema='%s',Name='%s')", this.schema, this.name);
        } else {
            return String.format("('%s')", this.name);
        }
    }

    /**
     * This method returns the class name in camel case.
     * @return ClassName
     */
    @Override
    public String getClassName() {
        return CLASSNAME;
    }

    /**
     * This method returns the ODataLink in String format.
     * @return Link Value
     */
    @Override
    public String getODataLink() {
        return (String) ((JSONObject) rawData.get("__metadata")).get("uri");
    }

    /**
     * This method creates the URL for performing box related operations.
     * @return URL value
     */
    @Override
    public String makeUrlForLink() {
        StringBuilder sb = new StringBuilder(this.accessor.getCurrentCell().getUrl());
        sb.append("__ctl/");
        sb.append(this.getClassName());
        sb.append(this.getKey());
        sb.append("/$links/");
        return sb.toString();
    }
}
