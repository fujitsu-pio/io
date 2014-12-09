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
package com.fujitsu.dc.common.es.response;


/**
 * IndexResponseのラッパークラス.
 */
public interface DcSearchHits extends Iterable<DcSearchHit> {
    /**
     * 検索でヒットすべきデータの総件数を取得. <br />
     * 検索で取得したデータの件数ではない
     * @return 検索でヒットすべきデータの総件数.
     */
    long allPages();

    /**
     * 検索でヒットすべきデータの総件数を取得. <br />
     * 検索で取得したデータの件数ではない
     * @return 検索でヒットすべきデータの総件数.
     */
    long getAllPages();

    /**
     * 検索で取得したデータの件数. <br />
     * 検索でヒットすべきデータの総件数ではない
     * @return 検索でヒットすべきデータの総件数.
     */
    long getCount();

    /**
     * The maximum score of this query.
     * @return .
     */
    float maxScore();

    /**
     * The maximum score of this query.
     * @return .
     */
    float getMaxScore();

    /**
     * The hits of the search request (based on the search type, and from / size provided).
     * @return .
     */
    DcSearchHit[] hits();

    /**
     * Return the hit as the provided position.
     * @param position .
     * @return .
     */
    DcSearchHit getAt(int position);

    /**
     * The hits of the search request (based on the search type, and from / size provided).
     * @return .
     */
    DcSearchHit[] getHits();
}
