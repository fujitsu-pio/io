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

import com.fujitsu.dc.client.http.DcResponse;
import com.fujitsu.dc.client.http.IRestAdapter;
import com.fujitsu.dc.client.http.RestAdapterFactory;

///**
// * EventLog取得のためのクラス.
// */
/**
 * THis is the class for EventLog acquisition.
 */
public abstract class LogManager {
    // /** アクセス主体. */
    /** Reference to Accessor. */
    Accessor accessor;

    // /**
    // * イベントログのURLを取得します.
    // * @param filename イベントログのファイル名
    // * @return イベントログのURL
    // */
    /**
     * This method is used to get the URL of the event log.
     * @param filename File Name of the event log
     * @return Event Log URL
     */
    protected abstract String getLogUrl(String filename);

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument initializing the accessor.
     * @param as Accessor
     */
    public LogManager(Accessor as) {
        this.accessor = as.clone();
    }

    // /**
    // * イベントログをString形式で取得します.
    // * @param filename 取得するファイル名
    // * @return イベントログ情報
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to get a String representation of the event log.
     * @param filename File Name
     * @return Event Log Information
     * @throws DaoException Exception thrown
     */
    public WebDAV getString(String filename) throws DaoException {
        return getString(filename, null);
    }

    // /**
    // * イベントログをString形式で取得します.
    // * @param filename 取得するファイル名
    // * @param dcRequestKey X-Dc-RequestKeyヘッダの値
    // * @return イベントログ情報
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to get a String representation of the event log.
     * @param filename File Name
     * @param dcRequestKey X-Dc-RequestKey Header
     * @return Event Log Information
     * @throws DaoException Exception thrown
     */
    public WebDAV getString(String filename, String dcRequestKey) throws DaoException {
        String url = this.getLogUrl(filename);
        return getStringEventLog(url, dcRequestKey);
    }

    // /**
    // * イベントログをStream形式で取得します.
    // * @param filename 取得するファイル名
    // * @return イベントログ情報
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to get event log in the Stream format.
     * @param filename File Name
     * @return Event Log Information
     * @throws DaoException Exception thrown
     */
    public WebDAV getStream(String filename) throws DaoException {
        return getStream(filename, null);
    }

    // /**
    // * イベントログをStream形式で取得します.
    // * @param filename 取得するファイル名
    // * @param dcRequestKey X-Dc-RequestKeyヘッダの値
    // * @return イベントログ情報
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to get event log in the Stream format.
     * @param filename File Name
     * @param dcRequestKey X-Dc-RequestKey Header
     * @return Event Log Information
     * @throws DaoException Exception thrown
     */
    public WebDAV getStream(String filename, String dcRequestKey) throws DaoException {
        String url = this.getLogUrl(filename);
        return getStreamEventLog(url, dcRequestKey);
    }

    /**
     * This method is used to get event log in the Stream format.
     * @param url Value
     * @param dcRequestKey X-Dc-RequestKey Header
     * @return Event Log Information
     * @throws DaoException Exception thrown
     */
    private WebDAV getStreamEventLog(String url, String dcRequestKey) throws DaoException {
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        HashMap<String, String> headers = new HashMap<String, String>();
        if (dcRequestKey != null) {
            headers.put("X-Dc-RequestKey", dcRequestKey);
        }

        DcResponse res;
        try {
            res = rest.get(url, headers, "application/octet-stream");
        } catch (DaoException e) {
            throw e;
        }

        WebDAV webDAV = new WebDAV();
        webDAV.setStreamBody(res.bodyAsStream());
        webDAV.setResHeaders(res.getHeaderList());

        return webDAV;
    }

    /**
     * This method is used to get event log in the String format.
     * @param url Value
     * @param dcRequestKey X-Dc-RequestKey Header
     * @return Event Log Information
     * @throws DaoException Exception thrown
     */
    private WebDAV getStringEventLog(String url, String dcRequestKey) throws DaoException {
        IRestAdapter rest = RestAdapterFactory.create(accessor);
        HashMap<String, String> headers = new HashMap<String, String>();
        if (dcRequestKey != null) {
            headers.put("X-Dc-RequestKey", dcRequestKey);
        }

        DcResponse res;
        try {
            res = rest.get(url, headers, "text/plain");
        } catch (DaoException e) {
            throw e;
        }

        // レスポンスボディを取得
        /** Get the response body. */
        String body = res.bodyAsString();

        WebDAV webDAV = new WebDAV();
        webDAV.setStringBody(body);
        webDAV.setResHeaders(res.getHeaderList());

        return webDAV;
    }
}
