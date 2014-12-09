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
package com.fujitsu.dc.client.http;

import com.fujitsu.dc.client.Accessor;

///**
// * RestAdapterを生成するクラス.
// */
/**
 * It creates a new object of RestAdapterFactory. This class generates RestAdapter.
 */
public class RestAdapterFactory {

    // /**
    // * 使用されないデフォルトコンストラクタ.
    // */
    /**
     * This is the default constructor marked as private to disallow its direct instantiation by other classes.
     */
    private RestAdapterFactory() {
    }

    // /**
    // * ResrAdapterかBatchAdapterを生成する.
    // * @param accessor アクセス主体
    // * @return RestAdapter
    // */
    /**
     * It generate a BatchAdapter or ResrAdapter.
     * @param accessor Accessor
     * @return RestAdapter/BatchAdapter
     */
    public static IRestAdapter create(Accessor accessor) {
        if (accessor.isBatchMode()) {
            return accessor.getBatchAdapter();
        } else {
            return (IRestAdapter) new RestAdapter(accessor);
        }
    }
}
