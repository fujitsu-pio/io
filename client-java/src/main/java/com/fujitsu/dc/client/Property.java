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
// * Propertyのアクセスクラス.
// */
/**
 * It creates a new object of Property. This class represents Property object in OData.
 */
public class Property extends AbstractODataContext {
    // /** キャメル型で表現したクラス名. */
    /** Class name in camel case. */
    private static final String CLASSNAME = "Property";
    // /** Property名. */
    /** Property Name. */
    private String name;
    // /** EntityType名. */
    /** EntityType Name. */
    private String entityTypeName;
    /** Type. */
    private String type;
    /** Nullable. */
    private boolean nullable = true;
    /** DefaultValue. */
    private Object defaultValue = null;
    /** CollectionKind. */
    private String collectionKind = "None";
    /** IsKey. */
    private boolean isKey = false;
    /** UniqueKey. */
    private String uniqueKey = null;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor.
     */
    public Property() {
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument and calling its parent constructor internally.
     * @param as Accessor
     */
    public Property(final Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param body 生成するPropertyのJson
    // */
    /**
     * This is the parameterized constructor with two arguments and calling initialize method internally.
     * @param as Accessor
     * @param body PropertyJson
     */
    public Property(final Accessor as, JSONObject body) {
        this.initialize(as, body);
    }

    // /**
    // * オブジェクトを初期化.
    // * @param as アクセス主体
    // * @param json サーバーから取得したJSONオブジェクト
    // */
    /**
     * This method is used to initialize various class variables and parent class variables.
     * @param as Accessor
     * @param json JSON object
     */
    public void initialize(Accessor as, JSONObject json) {
        super.initialize(as);
        rawData = json;
        name = (String) json.get("Name");
        entityTypeName = (String) json.get("_EntityType.Name");
        type = (String) json.get("Type");
        nullable = ((Boolean) json.get("Nullable")).booleanValue();
        defaultValue = json.get("DefaultValue");
        collectionKind = (String) json.get("CollectionKind");
        isKey = ((Boolean) json.get("IsKey")).booleanValue();
        uniqueKey = (String) json.get("UniqueKey");
    }

    // /**
    // * Property名の設定.
    // * @param value AssociationEnd名
    // */
    /**
     * This method is used to set the Property Name.
     * @param value PropertyName
     */
    public void setName(String value) {
        this.name = value;
    }

    // /**
    // * Property名の取得.
    // * @return Property名
    // */
    /**
     * This method is used to get the Property Name.
     * @return PropertyName value
     */
    public String getName() {
        return this.name;
    }

    // /**
    // * EntityType名の設定.
    // * @param value EntityType名
    // */
    /**
     * This method is used to set the EntityType Name.
     * @param value EntityTypeName
     */
    public void setEntityTypeName(String value) {
        this.entityTypeName = value;
    }

    // /**
    // * EntityType名の取得.
    // * @return EntityType名
    // */
    /**
     * This method is used to get the EntityType Name.
     * @return EntityTypeName value
     */
    public String getEntityTypeName() {
        return this.entityTypeName;
    }

    // /**
    // * Typeの設定.
    // * @param value type
    // */
    /**
     * This method is used to set the Property Type.
     * @param value type
     */
    public void setType(String value) {
        this.type = value;
    }

    // /**
    // * Typeの取得.
    // * @return type
    // */
    /**
     * This method is used to get the Property Type.
     * @return type value
     */
    public String getType() {
        return this.type;
    }

    // /**
    // * Nullableの設定.
    // * @param value Nullable
    // */
    /**
     * This method is used to set the Nullable field.
     * @param value Nullable
     */
    public void setNullable(boolean value) {
        this.nullable = value;
    }

    // /**
    // * Nullableの取得.
    // * @return Nullable
    // */
    /**
     * This method is used to get the Nullable field.
     * @return Nullable value
     */
    public boolean getNullable() {
        return this.nullable;
    }

    // /**
    // * DefaultValueの設定.
    // * @param value DefaultValue
    // */
    /**
     * This method is used to set the DefaultValue field.
     * @param value DefaultValue
     */
    public void setDefaultValue(Object value) {
        this.defaultValue = value;
    }

    // /**
    // * DefaultValueの取得.
    // * @return DefaultValue
    // */
    /**
     * This method is used to get the DefaultValue field.
     * @return DefaultValue value
     */
    public Object getDefaultValue() {
        return this.defaultValue;
    }

    // /**
    // * CollectionKindの設定.
    // * @param value CollectionKind
    // */
    /**
     * This method is used to set the CollectionKind field.
     * @param value CollectionKind
     */
    public void setCollectionKind(String value) {
        this.collectionKind = value;
    }

    // /**
    // * CollectionKindの取得.
    // * @return CollectionKind
    // */
    /**
     * This method is used to get the CollectionKind field.
     * @return CollectionKind value
     */
    public String getCollectionKind() {
        return this.collectionKind;
    }

    // /**
    // * IsKeyの設定.
    // * @param value IsKey
    // */
    /**
     * This method is used to set the IsKey field.
     * @param value IsKey
     */
    public void setIsKey(boolean value) {
        this.isKey = value;
    }

    // /**
    // * IsKeyの取得.
    // * @return IsKey
    // */
    /**
     * This method is used to get the IsKey field.
     * @return IsKey value
     */
    public boolean getIsKey() {
        return this.isKey;
    }

    // /**
    // * UniqueKeyの設定.
    // * @param value UniqueKey
    // */
    /**
     * This method is used to set the UniqueKey field.
     * @param value UniqueKey
     */
    public void setUniqueKey(String value) {
        this.uniqueKey = value;
    }

    // /**
    // * UniqueKeyの取得.
    // * @return UniqueKey
    // */
    /**
     * This method is used to get the UniqueKey field.
     * @return UniqueKey value
     */
    public String getUniqueKey() {
        return this.uniqueKey;
    }

    // /**
    // * ODataのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method is used to format and return key URL for Property related operations.
     * @return OData Key URL value
     */
    public String getKey() {
        return String.format("(Name='%s',_EntityType.Name='%s')", this.name, this.entityTypeName);
    }

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns the Property class name in camel case.
     * @return OData Property ClassName value
     */
    public String getClassName() {
        return CLASSNAME;
    }

}
