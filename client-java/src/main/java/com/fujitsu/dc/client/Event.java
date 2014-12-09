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

import org.json.simple.JSONObject;

///**
// * ExtCellのアクセスクラス.
// */
/**
 * It creates a new object of Event. This class represents Event object.
 */
public class Event {
    /** Variable action. */
    String action = "";
    /** Variable level. */
    String level = "";
    /** Variable object. */
    String object = "";
    /** Variable result. */
    String result = "";

    /**
     * コンストラクタ.
     */
    /**
     * This is the default constructor calling its parent constructor internally.
     */
    public Event() {
        super();
    }

    // /**
    // * action値の取得.
    // * @return action値
    // */
    /**
     * This method returns the action value.
     * @return action value
     */
    public String getAction() {
        return action;
    }

    // /**
    // * action値の設定.
    // * @param value action値
    // */
    /**
     * This method sets the action value.
     * @param value action
     */
    public void setAction(String value) {
        this.action = value;
    }

    // /**
    // * level値の取得.
    // * @return level値
    // */
    /**
     * This method returns the level value.
     * @return level value
     */
    public String getLevel() {
        return level;
    }

    // /**
    // * level値の設定.
    // * @param value level値
    // */
    /**
     * This method sets the level value.
     * @param value level
     */
    public void setLevel(String value) {
        this.level = value;
    }

    // /**
    // * object値の取得.
    // * @return object値
    // */
    /**
     * This method returns the object value.
     * @return object value
     */
    public String getObject() {
        return object;
    }

    // /**
    // * object値の設定.
    // * @param value object値
    // */
    /**
     * This method sets the object value.
     * @param value object
     */
    public void setObject(String value) {
        this.object = value;
    }

    // /**
    // * result値の取得.
    // * @return result値
    // */
    /**
     * This method returns the result value.
     * @return result value
     */
    public String getResult() {
        return result;
    }

    // /**
    // * result値のセット.
    // * @param value result値
    // */
    /**
     * This method sets the result value.
     * @param value result
     */
    public void setResult(String value) {
        this.result = value;
    }

    // /**
    // * JSON文字列化.
    // * @return JSON文字列
    // */
    /**
     * This method creates a new JSON object for Event.
     * @return JSON object
     */
    @SuppressWarnings("unchecked")
    public JSONObject toJSON() {
        JSONObject json = new JSONObject();
        json.put("action", this.action);
        json.put("level", this.level);
        json.put("object", this.object);
        json.put("result", this.result);
        return json;
    }
}
