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
import java.util.Iterator;

import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.ShardSearchFailure;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.facet.Facets;

/**
 * インデックスが存在しない時に０件の検索結果をダミーで返すSearchResponse.
 * indexにカスタムのMapping定義が必ず存在することを保証するため、
 * ESのIndex自動生成をOFFにして運用する一方で、
 * 本アプリで、存在しないIndex指定があったときは自動生成する枠組みを提供するようにしたかった。
 * しかし、検索・取得系でIndexMissingExceptionが発生した直後にIndexを作成する処理を書いても、なぜか
 * ElasticSearchがエラーとなって、動作しなかったため、やむを得ずこれをあきらめた。
 * そのため、０件である旨をしめすResponseをシミュレートして返す。
 * Deprecatedとなっているメソッドは使わないこと。
 */
public class DcNullSearchResponse extends SearchResponse {

    /**
     * コンストラクタ.
     */
    public DcNullSearchResponse() {
        super();
    }

    @Override
    public SearchHits getHits() {
        return new SearchHits() {
            @Override
            @Deprecated
            public Iterator<SearchHit> iterator() {
                return null;
            }

            @Override
            @Deprecated
            public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
                return null;
            }

            @Override
            @Deprecated
            public void writeTo(StreamOutput out) throws IOException {
            }

            @Override
            @Deprecated
            public void readFrom(StreamInput in) throws IOException {
            }

            @Override
            public long totalHits() {
                return 0;
            }

            @Override
            public float maxScore() {
                return 0;
            }

            @Override
            public SearchHit[] hits() {
                return new SearchHit[0];
            }

            @Override
            public long getTotalHits() {
                return 0;
            }

            @Override
            public float getMaxScore() {
                return 0;
            }

            @Override
            public SearchHit[] getHits() {
                return new SearchHit[0];
            }

            @Override
            @Deprecated
            public SearchHit getAt(int position) {
                return null;
            }
        };
    }


    /**
     * hits.
     * @return SearchHits
     */
    public SearchHits hits() {
        return getHits();
    }

    @Override
    @Deprecated
    public RestStatus status() {
        return super.status();
    }

    /**
     * facets.
     * @return Facets.
     */
    @Deprecated
    public Facets facets() {
        return getFacets();
    }

    @Override
    @Deprecated
    public Facets getFacets() {
        return super.getFacets();
    }

    /**
     * timeOut.
     * @return true or false
     */
    @Deprecated
    public boolean timedOut() {
        return isTimedOut();
    }

    @Override
    @Deprecated
    public boolean isTimedOut() {
        return super.isTimedOut();
    }

    /**
     * took.
     * @return TimeValue
     */
    @Deprecated
    public TimeValue took() {
        return getTook();
    }

    @Override
    @Deprecated
    public TimeValue getTook() {
        return super.getTook();
    }

    /**
     * tookInMillis.
     * @return long
     */
    @Deprecated
    public long tookInMillis() {
        return getTookInMillis();
    }

    @Override
    @Deprecated
    public long getTookInMillis() {
        return super.getTookInMillis();
    }

    /**
     * totalShards.
     * @return int
     */
    @Deprecated
    public int totalShards() {
        return getTotalShards();
    }

    @Override
    @Deprecated
    public int getTotalShards() {
        return super.getTotalShards();
    }

    /**
     * successfulShards.
     * @return int
     */
    @Deprecated
    public int successfulShards() {
        return getSuccessfulShards();
    }

    @Override
    @Deprecated
    public int getSuccessfulShards() {
        return super.getSuccessfulShards();
    }

    /**
     * failedShards.
     * @return int
     */
    @Deprecated
    public int failedShards() {
        return getFailedShards();
    }

    @Override
    @Deprecated
    public int getFailedShards() {
        return super.getFailedShards();
    }

    /**
     * shardFailures.
     * @return ShardSearchFailure[]
     */
    @Deprecated
    public ShardSearchFailure[] shardFailures() {
        return getShardFailures();
    }

    @Override
    @Deprecated
    public ShardSearchFailure[] getShardFailures() {
        return super.getShardFailures();
    }

    /**
     * scrollId.
     * @return String
     */
    @Deprecated
    public String scrollId() {
        return getScrollId();
    }

    @Override
    @Deprecated
    public String getScrollId() {
        return super.getScrollId();
    }

    @Override
    @Deprecated
    public XContentBuilder toXContent(XContentBuilder builder, Params params) throws IOException {
        return super.toXContent(builder, params);
    }

    @Override
    @Deprecated
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
    }

    @Override
    @Deprecated
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
    }

    @Override
    @Deprecated
    public String toString() {
        return super.toString();
    }
}
