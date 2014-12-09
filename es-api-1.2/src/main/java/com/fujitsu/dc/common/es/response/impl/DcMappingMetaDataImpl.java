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

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.cluster.metadata.MappingMetaData;

import com.fujitsu.dc.common.es.response.DcMappingMetaData;

/**
 * IndexResponseのラッパークラス.
 */
public class DcMappingMetaDataImpl implements DcMappingMetaData {
    private MappingMetaData mappingMetaData;

    /**
     * .
     */
    private DcMappingMetaDataImpl() {
        throw new IllegalStateException();
    }

    /**
     * GetResponseを指定してインスタンスを生成する.
     * @param response ESからのレスポンスオブジェクト
     */
    private DcMappingMetaDataImpl(MappingMetaData meta) {
        this.mappingMetaData = meta;
    }

    /**
     * .
     * @param meta .
     * @return .
     */
    public static DcMappingMetaData getInstance(MappingMetaData meta) {
        if (meta == null) {
            return null;
        }
        return new DcMappingMetaDataImpl(meta);
    }

    /* (non-Javadoc)
     * @see com.fujitsu.dc.common.es.response.impl.DcMappingMetaData#getSourceAsMap()
     */
    @Override
    public Map<String, Object> getSourceAsMap() throws IOException {
        return this.mappingMetaData.getSourceAsMap();
    }
}

