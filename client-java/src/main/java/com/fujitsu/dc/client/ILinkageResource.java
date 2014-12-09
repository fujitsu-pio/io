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
// * $links対象のリソースためのインターフェースクラス.
// */
/**
 * This is the interface class of resources for the $ links target.
 */
public interface ILinkageResource {
    // /**
    // * ODataのキーを取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method is used to return the OData key.
     * @return OData key
     */
    String getKey();

    // /**
    // * クラス名をキャメル型で取得する.
    // * @return ODataのキー情報
    // */
    /**
     * This method is used to return the class name in camel case.
     * @return OData ClassName
     */
    String getClassName();

    // /**
    // * ODataへのリンクを取得する.
    // * @return ODataへのリンク
    // */
    /**
     * This method is used to return the OData Link.
     * @return ODataLink
     */
    String getODataLink();

    // /**
    // * $linksのURLを生成.
    // * @return 生成したURL文字列.
    // */
    /**
     * This method is used to create URL for $links.
     * @return URL value
     */
    String makeUrlForLink();
}
