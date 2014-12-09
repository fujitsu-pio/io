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
package com.fujitsu.dc.engine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.es.EsClient;
import com.fujitsu.dc.common.es.EsClient.Event;
import com.fujitsu.dc.common.es.EsIndex;
import com.fujitsu.dc.common.es.EsRequestLogInfo;
import com.fujitsu.dc.common.es.EsType;
import com.fujitsu.dc.engine.utils.DcEngineConfig;

/**
 * 本アプリでElasticSearchを扱うモデル.
 */
public final class EsModel {
    static Logger log = LoggerFactory.getLogger(EsModel.class);
    private static EsClient esClient;

    static {
        EsClient.setEventHandler(Event.connected, new EsClient.EventHandler() {
            @Override
            public void handleEvent(EsRequestLogInfo logInfo, Object... params) {
                String msg = String.format("Connected to %s", params);
                log.info(msg);
            }
        });
        EsClient.setEventHandler(Event.afterRequest, new EsClient.EventHandler() {
            @Override
            public void handleEvent(EsRequestLogInfo logInfo, Object... params) {
                String msg = String.format("ESReq index=%s type=%s node=%s reqType=%s data=%s", params);
                log.info(msg);
            }
        });
        EsClient.setEventHandler(Event.creatingIndex, new EsClient.EventHandler() {
            @Override
            public void handleEvent(EsRequestLogInfo logInfo, Object... params) {
                String msg = String.format("Creating index [%s].", params);
                log.info(msg);
            }
        });

        esClient = new EsClient(DcEngineConfig.getEsClusterName(), DcEngineConfig.getEsHosts());
    }

    /**
     * コンストラクタ.
     */
    private EsModel() {
    }

    /**
     * 指定された名前のIndex操作オブジェクトを返します.
     * @param name index名
     * @return Indexオブジェクト
     */
    public static EsIndex idxUser(String name) {
        return esClient.idxUser(name, 0, 0);
    }

    /**
     * 指定された名前のIndex操作オブジェクトを返します.
     * @param indexName index名
     * @param typeName indexの種類
     * @param routingId indexの種類
     * @param times indexの種類
     * @param interval indexの種類
     * @return EsTypeオブジェクト
     */
    public static EsType type(String indexName, String typeName, String routingId, int times, int interval) {
        return esClient.type(indexName, typeName, routingId, times, interval);
    }
}
