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
package com.fujitsu.dc.common.es.util.impl;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * ElasticSearchのアクセサクラス.
 */
public class InternalNodeImpl {
    /**
     * デフォルトコンストラクタ.
     */
    private InternalNodeImpl() {
    }

    private static Node internalNode = null;

    /**
     * テスト用のElasticsearchノードを起動する.
     */
    public static void startInternalNode() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("node.http.enabled", false)
                .put("cluster.name", "testingCluster")
                .put("node.name", "node1")
                .put("gateway.type", "none")
                .put("action.auto_create_index", "false")
                .put("index.store.type", "memory")
                .put("index.number_of_shards", 1)
                .put("index.number_of_replicas", 0)
                .put("transport.tcp.port", "9399")
                .build();
        internalNode = NodeBuilder.nodeBuilder().settings(settings).node();
    }

    /**
     * テスト用のElasticsearchノードを停止する.
     */
    public static void stopInternalNode() {
        if (internalNode != null) {
            internalNode.close();
        }
    }
}
