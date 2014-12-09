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

import org.elasticsearch.action.search.MultiSearchResponse;

import com.fujitsu.dc.common.es.response.DcItem;
import com.fujitsu.dc.common.es.response.DcMultiSearchResponse;


/**
 * IndexResponseのラッパークラス.
 */
public class DcMultiSearchResponseImpl extends DcActionResponseImpl implements Iterable<DcItem>,
        DcMultiSearchResponse {
    private MultiSearchResponse multiSearchResponse;

    /**
     * .
     */
    private DcMultiSearchResponseImpl() {
        super(null);
        throw new IllegalStateException();
    }

    /**
     * GetResponseを指定してインスタンスを生成する.
     * @param response ESからのレスポンスオブジェクト
     */
    private DcMultiSearchResponseImpl(MultiSearchResponse response) {
        super(response);
        this.multiSearchResponse = response;
    }

    /**
     * .
     * @param response .
     * @return .
     */
    public static DcMultiSearchResponse getInstance(MultiSearchResponse response) {
        if (response == null) {
            return null;
        }
        return new DcMultiSearchResponseImpl(response);
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcMultiSearchResponse#getResponses()
     */
    @Override
    public DcItem[] getResponses() {
        List<DcItemImpl> list = new ArrayList<DcItemImpl>();
        for (MultiSearchResponse.Item item : this.multiSearchResponse.getResponses()) {
            list.add((DcItemImpl) DcItemImpl.getInstance(item));
        }
        return list.toArray(new DcItemImpl[0]);
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcMultiSearchResponse#iterator()
     */
    @Override
    public Iterator<DcItem> iterator() {
        List<DcItem> list = new ArrayList<DcItem>();
        for (MultiSearchResponse.Item item : this.multiSearchResponse.getResponses()) {
            list.add(DcItemImpl.getInstance(item));
        }
        return list.iterator();
    }
}
