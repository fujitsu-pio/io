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

///**
// * ODataへアクセスするためのクラス.
// */
/**
 * It creates a new object of ODataCollection. This class represents the OData collections for performing OData related
 * operations.
 */
public class ODataCollection extends DcCollection {
    // CHECKSTYLE:OFF
    // /** EntitySetアクセスするためのクラス. */
    /** Manager to access EntityType. */
    public EntityTypeManager entityType;
    // /** assosiationendアクセスのためのクラス. */
    /** Manager to access AssociationEnd. */
    public AssociationEndManager associationEnd;
    // /** ComplexTypeアクセスのためのクラス. */
    /** Manager to access ComplexType. */
    public ComplexTypeManager complexType;
    // /** Propertyアクセスのためのクラス. */
    /** Manager to access Property. */
    public PropertyManager property;
    // /** ComplexTypePropertyアクセスのためのクラス. */
    /** Manager to access ComplexTypeProperty. */
    public ComplexTypePropertyManager complexTypeProperty;

    // CHECKSTYLE:ON

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument and calling its parent constructor internally.
     * @param as Accessor
     */
    public ODataCollection(Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param name パス文字列
    // */
    /**
     * This is the parameterized constructor with two arguments and calling its parent constructor internally. It
     * initializes its various class variables.
     * @param as Accessor
     * @param name Path string
     */
    public ODataCollection(Accessor as, String name) {
        super(as, name);
        this.entityType = new EntityTypeManager(as, this);
        this.associationEnd = new AssociationEndManager(as, this);
        this.complexType = new ComplexTypeManager(as, this);
        this.property = new PropertyManager(as, this);
        this.complexTypeProperty = new ComplexTypePropertyManager(as, this);
    }

    // /**
    // * EntitySetの指定.
    // * @param name EntitySet名
    // * @return 生成したEntitySetオブジェクト
    // */
    /**
     * This method returns the specified EntitySet.
     * @param name EntitySetName
     * @return EntitySet object that is created
     */
    public EntitySet entitySet(String name) {
        return new EntitySet(this.accessor, this, name);
    }

    // /**
    // * Batch生成.
    // * @param sync 非同期指定
    // * @return 生成したODataBatchオブジェクト
    // */
    /**
     * This method generates the ODataBatch.
     * @param sync Asynchronous specified
     * @return ODataBatch object that is created
     */
    public ODataBatch makeODataBatch(final boolean sync) {
        return new ODataBatch(this.accessor, super.getPath());
    }
}
