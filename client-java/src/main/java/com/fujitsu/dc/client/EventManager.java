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

import java.util.HashMap;

import org.json.simple.JSONObject;

import com.fujitsu.dc.client.http.IRestAdapter;
import com.fujitsu.dc.client.http.RestAdapter;
import com.fujitsu.dc.client.http.RestAdapterFactory;

///**
// * Event登録のためのクラス.
// */
/**
 * It creates a new object of EventManager. This class performs the CRUD operations for Event object.
 */
public class EventManager {
    // /** アクセス主体. */
    /** Reference to Accessor. */
    Accessor accessor;

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument initializing the accessor.
     * @param as Accessor
     */
    public EventManager(Accessor as) {
        this.accessor = as.clone();
    }

    // /**
    // * イベントを登録.
    // * @param obj Eventオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to register the event using Event object.
     * @param obj Event object
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public void post(Event obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("level", obj.getLevel());
        body.put("action", obj.getAction());
        body.put("object", obj.getObject());
        body.put("result", obj.getResult());
        this.post(body);
    }

    // /**
    // * イベントを登録します.
    // * @param body 登録するJSONオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to register the event using request body.
     * @param body Request Body
     * @throws DaoException Exception thrown
     */
    public void post(JSONObject body) throws DaoException {
        this.post(body, null);
    }

    // /**
    // * イベントを登録します.
    // * @param body 登録するJSONオブジェクト
    // * @param dcRequestKey X-Dc-RequestKeyヘッダの値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to register the event using request body.
     * @param body Request Body
     * @param dcRequestKey X-Dc-RequestKey header
     * @throws DaoException Exception thrown
     */
    public void post(JSONObject body, String dcRequestKey) throws DaoException {
        String url = this.getEventUrl();
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        HashMap<String, String> header = new HashMap<String, String>();
        if (dcRequestKey != null) {
            header.put("X-Dc-RequestKey", dcRequestKey);
        }
        rest.post(url, header, JSONObject.toJSONString(body), RestAdapter.CONTENT_TYPE_JSON);
    }

    // /**
    // * イベントを登録します.
    // * @param level ログ出力レベル
    // * @param action イベントのアクション
    // * @param object イベントの対象オブジェクト
    // * @param result イベントの結果
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to register the event using level, action, object and result.
     * @param level Log Output Level
     * @param action Action Events
     * @param object Object Event
     * @param result Result Event
     * @throws DaoException Exception thrown
     */
    public void post(String level, String action, String object, String result) throws DaoException {
        JSONObject body = makeLogBody(level, action, object, result);
        this.post(body, null);
    }

    // /**
    // * イベントを登録します.
    // * @param level ログ出力レベル
    // * @param action イベントのアクション
    // * @param object イベントの対象オブジェクト
    // * @param result イベントの結果
    // * @param dcRequestKey X-Dc-RequestKeyヘッダの値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to register the event using level, action, object, result and dcRequestKey.
     * @param level Log Output Level
     * @param action Action Events
     * @param object Object Event
     * @param result Result Event
     * @param dcRequestKey X-Dc-RequestKey Header
     * @throws DaoException Exception thrown
     */
    public void post(String level, String action, String object, String result, String dcRequestKey)
            throws DaoException {
        JSONObject body = makeLogBody(level, action, object, result);
        this.post(body, dcRequestKey);
    }

    // /**
    // * イベント登録のリクエストURLを返却します.
    // * @return URL
    // */
    /**
     * This method generates and returns the Event URL.
     * @return URL value
     */
    protected String getEventUrl() {
        StringBuilder sb = new StringBuilder(this.accessor.getCurrentCell().getUrl());
        sb.append("__event/");
        return sb.toString();
    }

    /**
     * This method creates Log Body in the form of JSONObject.
     * @param level Log Output Level
     * @param action Action Events
     * @param object Object Event
     * @param result Result Event
     * @return
     */
    @SuppressWarnings("unchecked")
    private JSONObject makeLogBody(String level, String action, String object, String result) {
        JSONObject body = new JSONObject();
        body.put("level", level);
        body.put("action", action);
        body.put("object", object);
        body.put("result", result);
        return body;
    }

}
