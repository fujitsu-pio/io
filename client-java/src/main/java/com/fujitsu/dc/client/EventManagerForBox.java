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
// * Event登録のためのクラス.
// */
/**
 * It creates a new object of EventManagerForBox. This is the class for the Event registration.
 */
public class EventManagerForBox extends EventManager {

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor calling its parent constructor internally.
     * @param as Accessor
     */
    public EventManagerForBox(Accessor as) {
        super(as);
    }

    /**
     * This method creates and returns the Event URL for Box.
     * @return Event URL for Box
     */
    @Override
    protected String getEventUrl() {
        StringBuilder sb = new StringBuilder(this.accessor.getCurrentCell().getUrl());
        sb.append("__event/").append(accessor.getBoxName());
        return sb.toString();
    }
}
