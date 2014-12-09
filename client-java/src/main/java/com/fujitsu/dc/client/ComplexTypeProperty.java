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
// * ComplexTypePropertyのアクセスクラス.
// */
/**
 * It creates a new object of ComplexTypeProperty. This class represents ComplexTypeProperty object to perform
 * ComplexTypeProperty related operations.
 */
public class ComplexTypeProperty extends AbstractODataContext {
    // /** キャメル型で表現したクラス名. */
    /** Class Name in camel case. */
    private static final String CLASSNAME = "ComplexTypeProperty";
    // /** ComplexTypeProperty名. */
    /** ComplexTypeProperty Name. */
    private String name;
    // /** ComplexType名. */
    /** ComplexType Name. */
    private String complexTypeName;
    /** Type. */
    private String type;
    /** Nullable. */
    private boolean nullable = true;
    /** DefaultValue. */
    private Object defaultValue = null;
    /** CollectionKind. */
    private String collectionKind = "None";

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor.
     */
    public ComplexTypeProperty() {
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one parameter. It calls its parent constructor internally.
     * @param as Accessor
     */
    public ComplexTypeProperty(final Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param body 生成するComplexTypePropertyのJson
    // */
    /**
     * This is the parameterized constructor with two parameters. It calls initialize method internally.
     * @param as Accessor
     * @param body ComplexTypeProperty Json
     */
    public ComplexTypeProperty(final Accessor as, JSONObject body) {
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
        complexTypeName = (String) json.get("_ComplexType.Name");
        type = (String) json.get("Type");
        nullable = ((Boolean) json.get("Nullable")).booleanValue();
        defaultValue = json.get("DefaultValue");
        collectionKind = (String) json.get("CollectionKind");
    }

    // /**
    // * ComplexTypeProperty名の取得.
    // * @return AssociationEnd名
    // */
    /**
     * This method gets the ComplexTypeProperty Name.
     * @return ComplexTypeProperty Name
     */
    public String getName() {
        return this.name;
    }

    // /**
    // * ComplexType名の取得.
    // * @return EntityType名
    // */
    /**
     * This method gets the ComplexType Name.
     * @return ComplexType Name
     */
    public String getComplexTypeName() {
        return this.complexTypeName;
    }

    // /**
    // * Typeの取得.
    // * @return type
    // */
    /**
     * This method gets the type.
     * @return type value
     */
    public String getType() {
        return this.type;
    }

    // /**
    // * Nullableの取得.
    // * @return Nullable
    // */
    /**
     * This method gets the value of Nullable field.
     * @return Nullable value
     */
    public boolean getNullable() {
        return this.nullable;
    }

    // /**
    // * DefaultValueの取得.
    // * @return DefaultValue
    // */
    /**
     * This method gets the value of DefaultValue field.
     * @return DefaultValue
     */
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    // /**
    // * CollectionKindの取得.
    // * @return CollectionKind
    // */
    /**
     * This method gets the CollectionKind value.
     * @return CollectionKind
     */
    public String getCollectionKind() {
        return this.collectionKind;
    }

    // /**
    // * ComplexTypeProperty名の設定.
    // * @param value AssociationEnd名
    // */
    /**
     * This method sets the ComplexTypeProperty Name.
     * @param value ComplexTypeProperty Name
     */
    public void setName(String value) {
        this.name = value;
    }

    // /**
    // * ComplexType名の設定.
    // * @param value EntityType名
    // */
    /**
     * This method sets the ComplexType Name.
     * @param value ComplexType Name
     */
    public void setComplexTypeName(String value) {
        this.complexTypeName = value;
    }

    // /**
    // * Typeの設定.
    // * @param value type
    // */
    /**
     * This method sets the type value.
     * @param value type
     */
    public void setType(String value) {
        this.type = value;
    }

    // /**
    // * Nullableの設定.
    // * @param value Nullable
    // */
    /**
     * This method sets the Nullable value.
     * @param value Nullable
     */
    public void setNullable(boolean value) {
        this.nullable = value;
    }

    // /**
    // * DefaultValueの設定.
    // * @param value DefaultValue
    // */
    /**
     * This method sets the DefaultValue.
     * @param value DefaultValue
     */
    public void setDefaultValue(Object value) {
        this.defaultValue = value;
    }

    // /**
    // * CollectionKindの設定.
    // * @param value CollectionKind
    // */
    /**
     * This method sets the CollectionKind value.
     * @param value CollectionKind
     */
    public void setCollectionKind(String value) {
        this.collectionKind = value;
    }

    // /**
    // * ODataのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method formats and returns the key for ComplexTypeProperty.
     * @return ComplexTypeProperty Key
     */
    public String getKey() {
        return String.format("(Name='%s',_EntityType.Name='%s')", this.name, this.complexTypeName);
    }

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns the class name for ComplexTypeProperty in camel case.
     * @return ComplexTypeProperty Class Name
     */
    public String getClassName() {
        return CLASSNAME;
    }

}
