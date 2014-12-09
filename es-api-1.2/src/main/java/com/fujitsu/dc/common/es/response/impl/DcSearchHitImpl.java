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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHitField;

import com.fujitsu.dc.common.es.response.DcSearchHit;
import com.fujitsu.dc.common.es.response.DcSearchHitField;

/**
 * IndexResponseのラッパークラス.
 */
public class DcSearchHitImpl implements DcSearchHit {
    private SearchHit searchHit;

    /**
     * .
     */
    private DcSearchHitImpl() {
        throw new IllegalStateException();
    }

    /**
     * GetResponseを指定してインスタンスを生成する.
     * @param hits ESからのレスポンスオブジェクト
     */
    private DcSearchHitImpl(SearchHit hit) {
        this.searchHit = hit;
    }

    /**
     * .
     * @param hit .
     * @return .
     */
    public static DcSearchHit getInstance(SearchHit hit) {
        if (hit == null) {
            return null;
        }
        return new DcSearchHitImpl(hit);
    }

    @Override
    public float score() {
        return this.searchHit.score();
    }

    @Override
    public float getScore() {
        return this.searchHit.getScore();
    }

    @Override
    public String index() {
        return this.searchHit.index();
    }

    @Override
    public String getIndex() {
        return this.searchHit.getIndex();
    }

    @Override
    public String id() {
        return this.searchHit.id();
    }

    @Override
    public String getId() {
        return this.searchHit.getId();
    }

    @Override
    public String type() {
        return this.searchHit.type();
    }

    @Override
    public String getType() {
        return this.searchHit.getType();
    }

    @Override
    public long version() {
        return this.searchHit.version();
    }

    @Override
    public long getVersion() {
        return this.searchHit.getVersion();
    }

    @Override
    public byte[] source() {
        return this.searchHit.source();
    }

    @Override
    public boolean isSourceEmpty() {
        return this.searchHit.isSourceEmpty();
    }

    @Override
    public Map<String, Object> getSource() {
        return this.searchHit.getSource();
    }

    @Override
    public String sourceAsString() {
        return this.searchHit.sourceAsString();
    }

    @Override
    public String getSourceAsString() {
        return this.searchHit.getSourceAsString();
    }

    @Override
    public Map<String, Object> sourceAsMap() {
        return this.searchHit.sourceAsMap();
    }

    @Override
    public Object field(String fieldName) {
        return this.searchHit.getSource().get(fieldName);
    }

    @Override
    public Map<String, DcSearchHitField> fields() {
        Map<String, DcSearchHitField> map = new HashMap<String, DcSearchHitField>();
        for (Map.Entry<String, SearchHitField> entry : this.searchHit.fields().entrySet()) {
            map.put(entry.getKey(), DcSearchHitFieldImpl.getInstance(entry.getValue()));
        }
        return map;
    }

    @Override
    public Map<String, DcSearchHitField> getFields() {
        return fields();
    }

    @Override
    public Object[] sortValues() {
        return this.searchHit.sortValues();
    }

    @Override
    public Object[] getSortValues() {
        return this.searchHit.getSortValues();
    }

    @Override
    public String[] matchedFilters() {
        return this.searchHit.getMatchedQueries();
    }

    @Override
    public String[] getMatchedFilters() {
        return this.searchHit.getMatchedQueries();
    }

    @Override
    public Iterator<DcSearchHitField> iterator() {
        Map<String, DcSearchHitField> map = new HashMap<String, DcSearchHitField>();
        for (Map.Entry<String, SearchHitField> entry : this.searchHit.fields().entrySet()) {
            map.put(entry.getKey(), DcSearchHitFieldImpl.getInstance(entry.getValue()));
        }
        return map.values().iterator();
    }
}
