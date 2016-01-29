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
package com.fujitsu.dc.common.es;

import java.util.Map;

/**
 * elasticseachのバルクリクエストクラス.
 */
public interface EsBulkRequest {

    /**
     * バルクリクエスト内の各リクエスト種別.
     */
    enum BULK_REQUEST_TYPE {
        /** バルクリクエストの登録・更新リクエストを示す識別子 .*/
        INDEX,
        /** バルクリクエストの削除リクエストを示す識別子 .*/
        DELETE;
    }

    /**
     * バルクリクエスト内の各リクエスト種別を取得する.
     * @return バルクリクエスト内の各リクエスト種別
     */
    BULK_REQUEST_TYPE getRequestType();

    /**
     * 登録先のタイプを取得する.
     * @return Type名
     */
    String getType();

    /**
     * 登録データのIDを取得する.
     * @return ID
     */
    String getId();

    /**
     * 登録データを取得する.
     * @return 登録データのHashMap
     */
    Map<String, Object> getSource();

}
