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

import org.elasticsearch.action.index.IndexResponse;

import com.fujitsu.dc.common.es.response.DcIndexResponse;

/**
 * IndexResponseのラッパークラス.
 */
public class DcIndexResponseImpl extends DcActionResponseImpl implements DcIndexResponse {
    private IndexResponse indexResponse;

    /**
     * .
     */
    private DcIndexResponseImpl() {
        super(null);
        throw new IllegalStateException();
    }

    /**
     * GetResponseを指定してインスタンスを生成する.
     * @param response ESからのレスポンスオブジェクト
     */
    private DcIndexResponseImpl(IndexResponse response) {
        super(response);
        this.indexResponse = response;
    }

    /**
     * .
     * @param response .
     * @return .
     */
    public static DcIndexResponse getInstance(IndexResponse response) {
        if (response == null) {
            return null;
        }
        return new DcIndexResponseImpl(response);
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcIndexResponse#getIndex()
     */
    @Override
    public String getIndex() {
        return this.indexResponse.getIndex();
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcIndexResponse#getType()
     */
    @Override
    public String getType() {
        return this.indexResponse.getType();
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcIndexResponse#getId()
     */
    @Override
    public String getId() {
        return this.indexResponse.getId();
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcIndexResponse#version()
     */
    @Override
    public long version() {
        return this.indexResponse.getVersion();
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcIndexResponse#getVersion()
     */
    @Override
    public long getVersion() {
        return this.indexResponse.getVersion();
    }
}
