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
package com.fujitsu.dc.common.es.response;

import java.util.Map;

/**
 * IndexResponseのラッパークラス.
 */
public interface DcSearchHit extends Iterable<DcSearchHitField> {

    /**
     * The score.
     * @return .
     */
    float score();

    /**
     * The score.
     * @return .
     */
    float getScore();

    /**
     * The index of the hit.
     * @return .
     */
    String index();

    /**
     * The index of the hit.
     * @return .
     */
    String getIndex();

    /**
     * The id of the document.
     * @return .
     */
    String id();

    /**
     * The id of the document.
     * @return .
     */
    String getId();

    /**
     * The type of the document.
     * @return .
     */
    String type();

    /**
     * The type of the document.
     * @return .
     */
    String getType();

    /**
     * The version of the hit.
     * @return .
     */
    long version();

    /**
     * The version of the hit.
     * @return .
     */
    long getVersion();

    /**
     * The source of the document (can be <tt>null</tt>). Note, its a copy of the source
     * into a byte array, consider using {@link #sourceRef()} so there won't be a need to copy.
     * @return .
     */
    byte[] source();

    /**
     * Is the source empty (not available) or not.
     * @return .
     */
    boolean isSourceEmpty();

    /**
     * The source of the document as a map (can be <tt>null</tt>).
     * @return .
     */
    Map<String, Object> getSource();

    /**
     * The source of the document as string (can be <tt>null</tt>).
     * @return .
     */
    String sourceAsString();

    /**
     * The source of the document as string (can be <tt>null</tt>).
     * @return .
     */
    String getSourceAsString();

    /**
     * The source of the document as a map (can be <tt>null</tt>).
     * @return .
     */
    Map<String, Object> sourceAsMap();

    /**
     * The hit field matching the given field name.
     * @param fieldName .
     * @return .
     */
    Object field(String fieldName);

    /**
     * A map of hit fields (from field name to hit fields) if additional fields
     * were required to be loaded.
     * @return .
     */
    Map<String, DcSearchHitField> fields();

    /**
     * A map of hit fields (from field name to hit fields) if additional fields
     * were required to be loaded.
     * @return .
     */
    Map<String, DcSearchHitField> getFields();

    /**
     * An array of the sort values used.
     * @return .
     */
    Object[] sortValues();

    /**
     * An array of the sort values used.
     * @return .
     */
    Object[] getSortValues();

    /**
     * The set of filter names the query matched. Mainly makes sense for OR filters.
     * @return .
     */
    String[] matchedFilters();

    /**
     * The set of filter names the query matched. Mainly makes sense for OR filters.
     * @return .
     */
    String[] getMatchedFilters();

}
