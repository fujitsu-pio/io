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
package com.fujitsu.dc.client.http;

import java.util.HashMap;

///**
// * CacheMapクラス.
// */
/**
 * It creates a new object of CacheMap.
 */
public class CacheMap {
    // /** キャッシュハッシュ. */
    /** Cache hash. */
    private HashMap<String, CacheEntry> map;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor used to initialize map.
     */
    public CacheMap() {
        map = new HashMap<String, CacheEntry>();
    }

    // /**
    // * キャッシュハッシュから、URLをキーとしてCacheEntryを検索.
    // * @param key キーとなるURL
    // * @return 取り出したCacheEntryオブジェクト
    // */
    /**
     * This method is used to search a URL as key from the cache hash as CacheEntry.
     * @param key URL as Key
     * @return CacheEntry as the extracted object
     */
    public final CacheEntry search(final String key) {
        return map.get(key);
    }

    // /**
    // * キャッシュハッシュにエントリーを追加する.
    // * @param value CacheEntryオブジェクト
    // */
    /**
     * This method is used to add an entry to the cache hash.
     * @param value CacheEntry object
     */
    public final void appendEntry(final CacheEntry value) {
        CacheEntry ce = map.get(value.getUrl());
        if (ce == null) {
            map.put(value.getUrl(), value);
        } else {
            ce.setBody(value.getBody());
            ce.setHeaders(value.getHeaders());
        }
    }
}
