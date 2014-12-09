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

import com.fujitsu.dc.common.es.response.DcActionResponse;

/**
 * ActionResponseのラッパークラス.
 */
public class DcActionResponseImpl implements DcActionResponse {
    private ActionResponse actionResponse;

    /**
     * GetResponseを指定してインスタンスを生成する.
     * @param response ESからのレスポンスオブジェクト
     */
    protected DcActionResponseImpl(ActionResponse response) {
        this.actionResponse = response;
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.dcActionResponse#isNull()
     */
    @Override
    public boolean isNull() {
        return actionResponse == null;
    }
}
