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
package com.fujitsu.dc.client;

///**
// * EventLog取得のためのクラス.
// */
/**
 * This class is used for EventLog acquisition.
 */
public class ArchiveLogManager extends LogManager {

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor calling its parent constructor.
     * @param as Accessor
     */
    public ArchiveLogManager(Accessor as) {
        super(as);
    }

    /**
     * This method creates and returns the log URL for the specified file name.
     * @param filename Filename
     * @return URL File URL
     */
    @Override
    protected String getLogUrl(String filename) {
        StringBuilder sb = new StringBuilder(super.accessor.getCurrentCell().getUrl());
        sb.append("__log/archive/");
        sb.append(filename);
        return sb.toString();
    }

}
