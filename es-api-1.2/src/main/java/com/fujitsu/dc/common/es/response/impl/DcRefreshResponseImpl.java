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

import org.elasticsearch.action.admin.indices.refresh.RefreshResponse;

import com.fujitsu.dc.common.es.response.DcRefreshResponse;

/**
 * BulkItemResponseのラッパークラス.
 */
public class DcRefreshResponseImpl extends DcActionResponseImpl implements
        DcRefreshResponse {

    private RefreshResponse refreshResponse;

    /**
     * RefreshResponseを指定してインスタンスを生成する.
     * @param response
     *            ESからのレスポンスオブジェクト
     */
    private DcRefreshResponseImpl(RefreshResponse response) {
        super(response);
        this.refreshResponse = response;
    }

    /**
     * .
     * @param response
     *            .
     * @return .
     */
    public static DcRefreshResponse getInstance(RefreshResponse response) {
        if (response == null) {
            return null;
        }
        return new DcRefreshResponseImpl(response);
    }

    @Override
    public int getSuccessfulShards() {
        return this.refreshResponse.getSuccessfulShards();
    }

    @Override
    public int getFailedShards() {
        return this.refreshResponse.getFailedShards();
    }

}
