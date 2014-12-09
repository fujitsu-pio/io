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
// * AssociationEndのアクセスクラス.
// */
/**
 * It creates a new object of AssociationEnd. This is the access class of Association End.
 */
public class AssociationEnd extends AbstractODataContext {
    // /** キャメル型で表現したクラス名. */
    /** Classname in camel case. */
    private static final String CLASSNAME = "AssociationEnd";
    // /** EntityType名. */
    /** EntityType name. */
    private String entityTypeName;
    // /** AssociationEnd名. */
    /** AssociationEnd name. */
    private String name;
    // /** 多重度. */
    /** Multiplicity. */
    private String multiplicity;

    // /** Accountとのリンクマネージャ. */
    /** Account class for EventLog acquisition. */
    public MetadataLinkManager associationEnd;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor.
     */
    public AssociationEnd() {
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor taking one parameter and calling its parent constructor. It also
     * initializes the class variable associationEnd.
     * @param as Accessor
     */
    public AssociationEnd(final Accessor as) {
        super(as);
        this.associationEnd = new MetadataLinkManager(as, this);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param body 生成するEntityTypeのJson
    // */
    /**
     * This is the parameterized constructor taking two parameters. It calls initialize method internally.
     * @param as Accessor
     * @param body JSON object
     */
    public AssociationEnd(final Accessor as, JSONObject body) {
        this.initialize(as, body);
    }

    // /**
    // * オブジェクトを初期化.
    // * @param as アクセス主体
    // * @param json サーバーから取得したJSONオブジェクト
    // */
    /**
     * This method initializes various class variables.
     * @param as Accessor
     * @param json JSON object
     */
    public void initialize(Accessor as, JSONObject json) {
        super.initialize(as);
        this.associationEnd = new MetadataLinkManager(as, this);
        rawData = json;
        name = (String) json.get("Name");
        entityTypeName = (String) json.get("_EntityType.Name");
        multiplicity = (String) json.get("Multiplicity");
    }

    // /**
    // * AssociationEnd名の設定.
    // * @param value AssociationEnd名
    // */
    /**
     * This method sets the AssociationEnd name value.
     * @param value AssociationEnd Name
     */
    public void setName(String value) {
        this.name = value;
    }

    // /**
    // * AssociationEnd名の取得.
    // * @return AssociationEnd名
    // */
    /**
     * This method gets the AssociationEnd name value.
     * @return AssociationEnd Name
     */
    public String getName() {
        return this.name;
    }

    // /**
    // * EntityType名の設定.
    // * @param value EntityType名
    // */
    /**
     * This method sets EntityType Name value.
     * @param value EntityType Name
     */
    public void setEntityTypeName(String value) {
        this.entityTypeName = value;
    }

    // /**
    // * EntityType名の取得.
    // * @return EntityType名
    // */
    /**
     * This method gets EntityType Name value.
     * @return EntityType Name
     */
    public String getEntityTypeName() {
        return this.entityTypeName;
    }

    // /**
    // * multiplicityの設定.
    // * @param value 多重度
    // */
    /**
     * This method sets multiplicity.
     * @param value Multiplicity
     */
    public void setMultiplicity(String value) {
        this.multiplicity = value;
    }

    // /**
    // * multiplicityの取得.
    // * @return 多重度
    // */
    /**
     * This method gets multiplicity.
     * @return Multiplicity
     */
    public String getMultiplicity() {
        return this.multiplicity;
    }

    // /**
    // * ODataのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method formats and returns the OData key.
     * @return OData Key
     */
    public String getKey() {
        return String.format("(Name='%s',_EntityType.Name='%s')", this.name, this.entityTypeName);
    }

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns the class name in camel case.
     * @return OData ClassName
     */
    public String getClassName() {
        return CLASSNAME;
    }

}
