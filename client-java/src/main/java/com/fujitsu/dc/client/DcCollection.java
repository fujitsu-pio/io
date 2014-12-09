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
// * コレクションの抽象クラス.
// */
/**
 * It creates a new object of DcCollection. This is an abstract class for a collection.
 */
public class DcCollection extends AbstractODataContext {
    // /** キャメル方で表現したクラス名. */
    /** Class Name in camel case. */
    private static final String CLASSNAME = "";

    // /** コレクションのパス. */
    /** Collection Path. */
    StringBuilder url;

    /**
     * コンストラクタ.
     */
    /**
     * This is the default constructor calling its parent constructor internally.
     */
    public DcCollection() {
        super();
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument calling its parent constructor internally.
     * @param as Accessor
     */
    public DcCollection(Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param path URL文字列
    // */
    /**
     * This is the parameterized constructor with two arguments calling its parent constructor internally.
     * @param as Accessor
     * @param path URL value
     */
    public DcCollection(Accessor as, String path) {
        super(as);
        url = new StringBuilder(path);
    }

    /**
     * This is the overridden method and it initializes the accessor.
     * @param as Accessor
     */
    @Override
    public void initialize(Accessor as) {
        this.accessor = as.clone();
    }

    // /**
    // * URLを取得.
    // * @return URL文字列
    // */
    /**
     * This method returns the Path URL.
     * @return Path URL
     */
    public String getPath() {
        return this.url.toString();
    }

    // /**
    // * ODataのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns the key.
     * @return OData Key
     */
    public String getKey() {
        return "";
    }

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method returns DcCollection class name in camel case.
     * @return DcCollection class name
     */
    public String getClassName() {
        return CLASSNAME;
    }

}
