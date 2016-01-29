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

package com.fujitsu.dc.common.es.test.util;

import java.io.File;

import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;

/**
 * テスト用のElasticsearchのNodeを扱うクラス.
 */
public class EsTestNode {
    private Node node;

    /**
     * テスト用のElasticsearchのNodeを初期化する.
     */
    public void create() {
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
        this.node = NodeBuilder.nodeBuilder().settings(settings).node();
    }

    /**
     * テスト用のElasticsearchのNodeをクローズする.
     * @throws Exception 異常が発生した場合の例外
     */
    public void close() throws Exception {
        node.close();
        // EsClient.clearEsClient();
        deleteDirectory(new File("data"));
    }

    /**
     * ディレクトリを再帰的に削除する.
     * @param target 削除対象
     */
    private static void deleteDirectory(File target) {
        if (!target.exists()) {
            return;
        }

        if (target.isFile()) {
            target.delete();
        }

        if (target.isDirectory()) {
            File[] files = target.listFiles();
            for (int i = 0; i < files.length; i++) {
                deleteDirectory(files[i]);
            }
            target.delete();
        }
    }
}
