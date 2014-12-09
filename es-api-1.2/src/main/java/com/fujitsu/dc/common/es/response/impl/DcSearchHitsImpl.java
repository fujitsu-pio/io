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
package com.fujitsu.dc.common.es.response.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import com.fujitsu.dc.common.es.response.DcSearchHit;
import com.fujitsu.dc.common.es.response.DcSearchHits;

/**
 * IndexResponseのラッパークラス.
 */
public class DcSearchHitsImpl implements DcSearchHits {
    private SearchHits searchHits;

    /**
     * .
     */
    private DcSearchHitsImpl() {
        throw new IllegalStateException();
    }

    /**
     * GetResponseを指定してインスタンスを生成する.
     * @param hits ESからのレスポンスオブジェクト
     */
    private DcSearchHitsImpl(SearchHits hits) {
        this.searchHits = hits;
    }

    /**
     * .
     * @param hits .
     * @return .
     */
    public static DcSearchHits getInstance(SearchHits hits) {
        if (hits == null) {
            return null;
        }
        return new DcSearchHitsImpl(hits);
    }

    @Override
    public long allPages() {
        return this.searchHits.totalHits();
    }

    @Override
    public long getAllPages() {
        return this.searchHits.getTotalHits();
    }

    @Override
    public long getCount() {
        return this.searchHits.getHits().length;
    }

    @Override
    public float maxScore() {
        return this.searchHits.maxScore();
    }

    @Override
    public float getMaxScore() {
        return this.searchHits.getMaxScore();
    }

    @Override
    public DcSearchHit[] hits() {
        return getHits();
    }

    @Override
    public DcSearchHit getAt(int position) {
        List<DcSearchHit> list = new ArrayList<DcSearchHit>();
        for (SearchHit hit : this.searchHits.hits()) {
            list.add((DcSearchHit) DcSearchHitImpl.getInstance(hit));
        }
        return list.get(position);
    }

    @Override
    public DcSearchHit[] getHits() {
        List<DcSearchHit> list = new ArrayList<DcSearchHit>();
        for (SearchHit hit : this.searchHits.hits()) {
            list.add((DcSearchHit) DcSearchHitImpl.getInstance(hit));
        }
        return list.toArray(new DcSearchHit[0]);
    }

    @Override
    public Iterator<DcSearchHit> iterator() {
        List<DcSearchHit> list = new ArrayList<DcSearchHit>();
        for (SearchHit hit : this.searchHits.hits()) {
            list.add((DcSearchHit) DcSearchHitImpl.getInstance(hit));
        }
        return list.iterator();
    }
}
