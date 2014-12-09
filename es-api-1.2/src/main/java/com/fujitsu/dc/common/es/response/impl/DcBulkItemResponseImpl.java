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

import org.elasticsearch.action.ActionResponse;
import org.elasticsearch.action.bulk.BulkItemResponse;

import com.fujitsu.dc.common.es.response.DcBulkItemResponse;

/**
 * BulkItemResponseのラッパークラス.
 */
public class DcBulkItemResponseImpl extends BulkItemResponse implements DcBulkItemResponse {

    /**
     * .
     * @param id .
     * @param opType .
     * @param response .
     */
    public DcBulkItemResponseImpl(int id, String opType, ActionResponse response) {
        super(id, opType, response);
    }

    /**
     *  .
     * @param id .
     * @param opType .
     * @param failure .
     */
    public DcBulkItemResponseImpl(int id, String opType, Failure failure) {
        super(id, opType, failure);
    }

    /**
     * .
     * @param response .
     * @return .
     */
    public static DcBulkItemResponse getInstance(BulkItemResponse response) {
        if (response == null) {
            return null;
        }
        return new DcBulkItemResponseImpl(response.getItemId(), response.getOpType(), response.getResponse());
    }

    @Override
    public long version() {
        return super.getVersion();
    }
}
