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

import org.elasticsearch.action.delete.DeleteResponse;

import com.fujitsu.dc.common.es.response.DcDeleteResponse;

/**
 * IndexResponseのラッパークラス.
 */
public class DcDeleteResponseImpl extends DcActionResponseImpl implements DcDeleteResponse {
    private DeleteResponse deleteResponse;

    /**
     * .
     */
    private DcDeleteResponseImpl() {
        super(null);
        throw new IllegalStateException();
    }

    /**
     * GetResponseを指定してインスタンスを生成する.
     * @param response ESからのレスポンスオブジェクト
     */
    private DcDeleteResponseImpl(DeleteResponse response) {
        super(response);
        this.deleteResponse = response;
    }

    /**
     * .
     * @param response .
     * @return .
     */
    public static DcDeleteResponse getInstance(DeleteResponse response) {
        if (response == null) {
            return null;
        }
        return new DcDeleteResponseImpl(response);
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcDeleteResponse#getId()
     */
    @Override
    public String getId() {
        return this.deleteResponse.getId();
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcDeleteResponse#version()
     */
    @Override
    public long version() {
        return this.deleteResponse.getVersion();
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcDeleteResponse#getVersion()
     */
    @Override
    public long getVersion() {
        return this.deleteResponse.getVersion();
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcDeleteResponse#isNotFound()
     */
    @Override
    public boolean isNotFound() {
        return !this.deleteResponse.isFound();
    }
}
