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
import java.util.List;

import org.elasticsearch.action.bulk.BulkItemResponse;
import org.elasticsearch.action.bulk.BulkResponse;

import com.fujitsu.dc.common.es.response.DcBulkItemResponse;
import com.fujitsu.dc.common.es.response.DcBulkResponse;

/**
 * IndexResponseのラッパークラス.
 */
public class DcBulkResponseImpl extends DcActionResponseImpl implements DcBulkResponse {
    private BulkResponse bulkResponse;

    /**
     * .
     */
    private DcBulkResponseImpl() {
        super(null);
        throw new IllegalStateException();
    }

    /**
     * GetResponseを指定してインスタンスを生成する.
     * @param response ESからのレスポンスオブジェクト
     */
    private DcBulkResponseImpl(BulkResponse response) {
        super(response);
        this.bulkResponse = response;
    }

    /**
     * .
     * @param response .
     * @return .
     */
    public static DcBulkResponse getInstance(BulkResponse response) {
        if (response == null) {
            return null;
        }
        return new DcBulkResponseImpl(response);
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcBulkResponse#items()
     */
    @Override
    public DcBulkItemResponse[] items() {
        List<DcBulkItemResponse> list = new ArrayList<DcBulkItemResponse>();
        for (BulkItemResponse response : this.bulkResponse.getItems()) {
            list.add(DcBulkItemResponseImpl.getInstance(response));
        }
        return list.toArray(new DcBulkItemResponse[0]);
    }

    @Override
    public boolean hasFailures() {
        return this.bulkResponse.hasFailures();
    }

    @Override
    public String buildFailureMessage() {
        return this.bulkResponse.buildFailureMessage();
    }
}
