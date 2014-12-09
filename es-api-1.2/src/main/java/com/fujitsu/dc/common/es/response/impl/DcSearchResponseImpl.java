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

import org.elasticsearch.action.search.SearchResponse;

import com.fujitsu.dc.common.es.response.DcSearchHits;
import com.fujitsu.dc.common.es.response.DcSearchResponse;

/**
 * IndexResponseのラッパークラス.
 */
public class DcSearchResponseImpl extends DcActionResponseImpl implements DcSearchResponse {
    private SearchResponse searchResponse;

    /**
     * .
     */
    private DcSearchResponseImpl() {
        super(null);
        throw new IllegalStateException();
    }

    /**
     * GetResponseを指定してインスタンスを生成する.
     * @param response ESからのレスポンスオブジェクト
     */
    private DcSearchResponseImpl(SearchResponse response) {
        super(response);
        this.searchResponse = response;
    }

    /**
     * .
     * @param response .
     * @return .
     */
    public static DcSearchResponse getInstance(SearchResponse response) {
        if (response == null) {
            return null;
        }
        return new DcSearchResponseImpl(response);
    }

    /*
     * (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcSearchResponse#getHits()
     */
    @Override
    public DcSearchHits getHits() {
        // TODO use factory class
        return DcSearchHitsImpl.getInstance(this.searchResponse.getHits());
    }

    /*
     * (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcSearchResponse#hits()
     */
    @Override
    public DcSearchHits hits() {
        // TODO use factory class
        return DcSearchHitsImpl.getInstance(this.searchResponse.getHits());
    }

    /*
     * (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcSearchResponse#isNullResponse()
     */
    @Override
    public boolean isNullResponse() {
        return searchResponse instanceof DcNullSearchResponse;
    }

    @Override
    public String getScrollId() {
        return this.searchResponse.getScrollId();
    }
}
