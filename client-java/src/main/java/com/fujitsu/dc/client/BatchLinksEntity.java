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
// * ユーザデータの$batchの$links登録用のアクセスクラス.
// */
/**
 * It creates a new object of BatchLinksEntity. This is the access class of user data in the $ batch of $ links for
 * registration.
 */
public class BatchLinksEntity implements ILinkageResource {

    // /** Entityとのリンクマネージャ. */
    /** Link manager with Entity. */
    public LinkManager entity;
    // /** ODataコレクションのURL. */
    /** OData collection URL. */
    private String collectionUrl;
    // /** EntitySet名. */
    /** EntitySet Name. */
    private String entitySetName;
    // /** ID値. */
    /** ID Value. */
    private String id;

    // /**
    // * コンストラクタ($links先用).
    // * @param entitySetName 生成するEntityのEntitySet名
    // * @param id 生成するEntityの__id
    // */
    /**
     * This is the parameterized constructor with two parameters used for initializing the class variables.
     * @param entitySetName EntitySet Name
     * @param id Entity ID
     */
    public BatchLinksEntity(String entitySetName, String id) {
        this.entitySetName = entitySetName;
        this.id = id;
    }

    // /**
    // * コンストラクタ($links元用).
    // * @param as アクセス主体.
    // * @param collectionUrl ODataコレクションのURL
    // * @param entitySetName 生成するEntityのEntitySet名
    // * @param id 生成するEntityの__id
    // */
    /**
     * This is the parameterized constructor with four parameters used for initializing the class variables.
     * @param as Accessor
     * @param collectionUrl OData Collection URL
     * @param entitySetName EntitySet Name
     * @param id EntityID
     */
    BatchLinksEntity(Accessor as, String collectionUrl, String entitySetName, String id) {
        this.collectionUrl = collectionUrl;
        this.entitySetName = entitySetName;
        this.id = id;
        this.entity = new LinkManager(as, this);
    }

    /**
     * This method returns the Collection URL.
     * @return collectionUrl value
     */
    String getCollectionUrl() {
        return this.collectionUrl;
    }

    /**
     * This method formats and returns the key.
     * @return Key value
     */
    @Override
    public String getKey() {
        return String.format("('%s')", this.id);
    }

    /**
     * This method returns the entitySetName.
     * @return entitySetName
     */
    @Override
    public String getClassName() {
        return this.entitySetName;
    }

    /**
     * This method returns the OData Link URL.
     * @return ODataLink URL.
     */
    @Override
    public String getODataLink() {
        if (this.collectionUrl == null) {
            // $links先用
            return "/" + this.entitySetName + getKey();
        } else {
            // $links元用
            return this.entitySetName + getKey();
        }
    }

    /**
     * This method creates and returns the URL for BatchLinkEntity.
     * @return Link URL
     */
    @Override
    public String makeUrlForLink() {
        String url = this.getODataLink();
        url += "/$links/";
        return url;
    }
}
