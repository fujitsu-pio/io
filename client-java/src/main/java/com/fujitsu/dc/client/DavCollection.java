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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpStatus;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.fujitsu.dc.client.http.CacheEntry;
import com.fujitsu.dc.client.http.CacheMap;
import com.fujitsu.dc.client.http.DcResponse;
import com.fujitsu.dc.client.http.IRestAdapter;
import com.fujitsu.dc.client.http.RestAdapter;
import com.fujitsu.dc.client.http.RestAdapterFactory;
import com.fujitsu.dc.client.utils.UrlUtils;

///**
// * DAVコレクションへアクセスするクラス.
// */
/**
 * It creates a new object of DavCollection. This class is used to access the DAV collection for performing Odata
 * operations.
 */
public class DavCollection extends DcCollection {

    private static final int REDIRECTION_CODE = 300;

    // CHECKSTYLE:OFF
    // /** boxレベルACLへアクセスするためのクラス. */
    /** Reference variable to access the box level ACL. */
    public AclManager acl;

    // CHECKSTYLE:ON

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor and calls its parent constructor internally.
     */
    public DavCollection() {
        super();
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param pathValue これまでのパス
    // */
    /**
     * This is the parameterized constructor with two parameters. It calls its parent constructor internally.
     * @param as Accessor
     * @param pathValue Path
     */
    public DavCollection(Accessor as, String pathValue) {
        super(as, pathValue);
        this.acl = new AclManager(accessor, this);
    }

    // /**
    // * コレクションの生成.
    // * @param name 生成するCollection名
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates a Collection.
     * @param name CollectionName
     * @throws DaoException Exception thrown
     */
    public void mkCol(String name) throws DaoException {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        rest.mkcol(UrlUtils.append(this.getPath(), name));
    }

    // /**
    // * ODataコレクションの生成.
    // * @param name 生成するODataCollection名
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates a OData Collection.
     * @param name ODataCollectionName
     * @throws DaoException Exception thrown
     */
    public void mkOData(String name) throws DaoException {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        rest.mkOData(UrlUtils.append(this.getPath(), name));
    }

    // /**
    // * Serviceコレクションの生成.
    // * @param name 生成するServiceCollection名
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates a Service Collection.
     * @param name ServiceCollectionName
     * @throws DaoException Exception thrown
     */
    public void mkService(String name) throws DaoException {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        rest.mkService(UrlUtils.append(this.getPath(), name));
    }

    // /**
    // * Calendarコレクションの生成.
    // * @param name 生成するCalendarCollectoin名
    // * @throws DaoException DAO例外
    // */
    /**
     * This method creates a Calendar Collection.
     * @param name CalendarCollectoinName
     * @throws DaoException Exception thrown
     */
    public void mkCalendar(String name) throws DaoException {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        rest.mkCalendar(UrlUtils.append(this.getPath(), name));
    }

    // /**
    // * コレクション内のリソースの一覧を取得する.
    // * @return リソースの一覧
    // * @throws DaoException DAO例外
    // */
    /**
     * This method gets the list of resources in the collection by calling getResourceList method internally.
     * @return List of resources
     * @throws DaoException Exception thrown
     */
    public String[] getFileList() throws DaoException {
        return getResourceList(false);
    }

    // /**
    // * コレクション内のサブコレクションの一覧を取得する.
    // * @return サブコレクションの一覧
    // * @throws DaoException DAO例外
    // */
    /**
     * This method gets the list of sub-collection in the collection by calling getResourceList method internally.
     * @return List of sub-collection
     * @throws DaoException Exception thrown
     */
    public String[] getColList() throws DaoException {
        return getResourceList(true);
    }

    /**
     * This method fetches the list of resource for the box/collection as specified in the path URL.
     * @param folder value
     * @return List of resources
     * @throws DaoException Exception thrown
     */
    private String[] getResourceList(boolean folder) throws DaoException {
        ArrayList<String> folderList = new ArrayList<String>();
        ArrayList<String> fileList = new ArrayList<String>();
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        DcResponse res = rest.propfind(this.url.toString());
        Document doc = res.bodyAsXml();
        NodeList nl = doc.getElementsByTagName("response");
        String name;
        for (int i = 1; i < nl.getLength(); i++) {
            Element elm = (Element) nl.item(i);
            Node href = elm.getElementsByTagName("href").item(0);
            name = href.getFirstChild().getNodeValue();
            NodeList col = elm.getElementsByTagName("collection");
            if (col.getLength() > 0) {
                folderList.add(name);
            } else {
                fileList.add(name);
            }
        }
        if (folder) {
            return folderList.toArray(new String[0]);
        } else {
            return fileList.toArray(new String[0]);
        }
    }

    // /**
    // * コレクションにプロパティをセットする.
    // * @param key プロパティ名
    // * @param value プロパティの値
    // */
    /**
     * This method sets the property in a collection.
     * @param key Key value
     * @param value Value
     */
    public void setProp(String key, String value) {

    }

    // /**
    // * コレクションからプロパティを取得する.
    // * @param key プロパティ名
    // * @return 取得したプロパティ値
    // */
    /**
     * This method gets the property from a collection based on specified key.
     * @param key Key value
     * @return Value Empty string
     */
    public String getProp(String key) {
        return "";
    }

    // /**
    // * サブコレクションを指定.
    // * @param name コレクション名
    // * @return 指定したコレクション名のDavCollectionオブジェクト
    // */
    /**
     * This method specifies the sub-collection.
     * @param name Collection Name
     * @return DavCollection object
     */
    public DavCollection col(String name) {
        return new DavCollection(this.accessor, UrlUtils.append(this.getPath(), name));
    }

    // /**
    // * ODataコレクションを指定.
    // * @param name ODataコレクション名
    // * @return 取得したODataCollectionオブジェクト
    // */
    /**
     * This method specifies the odata collection.
     * @param name ODataCollection Name
     * @return ODataCollection object
     */
    public ODataCollection odata(String name) {
        return new ODataCollection(this.accessor, UrlUtils.append(this.getPath(), name));
    }

    // /**
    // * Serviceコレクションを指定.
    // * @param name Serviceコレクション名
    // * @return 取得したSerivceコレクションオブジェクト
    // */
    /**
     * This method specifies the service collection.
     * @param name ServiceCollection Name
     * @return SerivceCollection object
     */
    public ServiceCollection service(String name) {
        return new ServiceCollection(this.accessor, UrlUtils.append(this.getPath(), name));
    }

    // /**
    // * DAVに対するGETメソッドをリクエストする.
    // * @param pathValue 取得するパス
    // * @return GETした文字列
    // * @throws DaoException DAO例外
    // */
    /**
     * This method requests the GET method for the DAV by calling its overloaded version.
     * @param pathValue Path
     * @return GET Response in string form
     * @throws DaoException Exception thrown
     */
    public String getString(String pathValue) throws DaoException {
        return this.getString(pathValue, "utf-8");
    }

    // /**
    // * DAVに対するGETメソッドをリクエストする.
    // * @param pathValue 取得するパス
    // * @param charset 文字コード
    // * @return GETした文字列
    // * @throws DaoException DAO例外
    // */
    /**
     * This method requests the GET method for the DAV and returns in string format.
     * @param pathValue Path
     * @param charset Character Code
     * @return GET Response in string form
     * @throws DaoException Exception thrown
     */
    public String getString(String pathValue, String charset) throws DaoException {
        WebDAV webDAV = getStringWebDAV(pathValue, charset);
        return webDAV.getStringBody();
    }

    // /**
    // * DAVに対するGETメソッドをリクエストする.
    // * @param pathValue 取得するパス
    // * @return GETしたストリーム
    // * @throws DaoException DAO例外
    // */
    /**
     * This method requests the GET method for the DAV and returns in stream format.
     * @param pathValue Path
     * @return GET Response as Stream
     * @throws DaoException Exception thrown
     */
    public InputStream getStream(String pathValue) throws DaoException {
        String url = UrlUtils.append(this.getPath(), pathValue);
        // リクエスト
        /** Request. */
        DcResponse res = RestAdapterFactory.create(this.accessor).get(url, "application/octet-stream");
        // レスポンスボディをストリームとして返却
        /** Return stream as the response body. */
        return res.bodyAsStream();
    }

    // /**
    // * DAVに対するGETメソッドをリクエストする.
    // * @param pathValue 取得するパス
    // * @return WebDAV GETした文字列を含むWebDAVオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method requests the GET method for the DAV by calling its overloaded version.
     * @param pathValue Path
     * @return WebDAV GET WebDAV object that contains a string
     * @throws DaoException Exception thrown
     */
    public WebDAV getStringWebDAV(String pathValue) throws DaoException {
        return this.getStringWebDAV(pathValue, "utf-8");
    }

    // /**
    // * DAVに対するGETメソッドをリクエストする.
    // * @param pathValue 取得するパス
    // * @param charset 文字コード
    // * @return WebDAV GETした文字列を含むWebDAVオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method requests the GET method for the DAV and retruns in WebDAV object.
     * @param pathValue Path
     * @param charset Character Code
     * @return WebDAV GET WebDAV object that contains a string
     * @throws DaoException Exception thrown
     */
    public WebDAV getStringWebDAV(String pathValue, String charset) throws DaoException {
        String url = UrlUtils.append(this.getPath(), pathValue);

        // まずはキャッシュから検索する
        /** First search from cache. */
        CacheMap cm = this.accessor.getContext().getCacheMap();
        CacheEntry ce = cm.search(url);

        IRestAdapter rest = RestAdapterFactory.create(this.accessor);
        DcResponse res;
        try {
            if (ce == null) {
                // キャッシュになければ新規取得
                /** If not found in cache, then acquire. */
                res = rest.get(url, "text/plain");
            } else {
                // キャッシュにあれば、IF_NONE_MATCHにETag指定
                /** If you are on the cache and ETag specified in IF_NONE_MATCH. */
                res = rest.get(url, "text/plain", ce.getEtag());
            }
        } catch (DaoException e) {
            // 304 NOT_MODIFIEDの場合は、キャッシュの値を返却する
            /** In the case of 304 NOT_MODIFIED, to return the value of the cache. */
            if (Integer.parseInt(e.getCode()) == HttpStatus.SC_NOT_MODIFIED && ce != null) {
                WebDAV webDAVCache = new WebDAV();
                webDAVCache.setStringBody(ce.getBody());
                webDAVCache.setResHeaders(ce.getHeaders());
                webDAVCache.setStatusCode(HttpStatus.SC_NOT_MODIFIED);
                return webDAVCache;
            }
            throw e;
        }

        // レスポンスボディを取得
        /** Get the response body. */
        String body = res.bodyAsString(charset);

        WebDAV webDAV = new WebDAV();
        webDAV.setStringBody(body);
        webDAV.setResHeaders(res.getHeaderList());
        webDAV.setStatusCode(res.getStatusCode());

        if (ce == null) {
            // キャッシュになければ新規にキャッシュに保存
            /** Save the new one to cache. */
            cm.appendEntry(new CacheEntry(url, res.getHeaderList(), body));
        } else {
            // キャッシュにあれば、キャッシュ情報を更新
            /** If was present on the cache, then update its new value. */
            ce.setHeaders(res.getHeaderList());
            ce.setBody(body);
        }
        return webDAV;
    }

    // /**
    // * DAVに対するGETメソッドをリクエストする<br>
    // * ETag値がnull以外の場合は、If-None-Matchヘッダを付加する.
    // * @param pathValue 取得するパス
    // * @param eTag ETag値
    // * @param charset 文字コード
    // * @return WebDAV GETした文字列を含むWebDAVオブジェクト<br>
    // * 更新なしの場合はnullを返却する
    // * @throws DaoException DAO例外
    // */
    /**
     * DAVに対するGETメソッドをリクエストする<br>
     * ETag値がnull以外の場合は、If-None-Matchヘッダを付加する.
     * @param pathValue 取得するパス
     * @param eTag ETag値
     * @param charset 文字コード
     * @return WebDAV GETした文字列を含むWebDAVオブジェクト<br>
     *         更新なしの場合はnullを返却する
     * @throws DaoException DAO例外
     */
    /**
     * This method is used to request the GET method for DAV. If the ETag value is non-null, add an If-None-Match
     * header.
     * @param pathValue Path
     * @param eTag ETag value
     * @param charset character code
     * @return WebDAV objects that contain the string If no update, then return null.
     * @throws DaoException DAO exception
     */
    public WebDAV getStringWebDAV(String pathValue, String eTag, String charset) throws DaoException {
        String url = UrlUtils.append(this.getPath(), pathValue);
        DcResponse res = null;
        int statusCode = 0;
        try {
            res = RestAdapterFactory.create(this.accessor).get(url, "text/plain", eTag);
            statusCode = res.getStatusCode();
        } catch (DaoException e) {
            // TODO 300系をエラーとして処理することの可否は検討が必要
            /** TODO Propriety of treating it as an error 300 system needs to be considered. */
            if (Integer.parseInt(e.getCode()) != HttpStatus.SC_NOT_MODIFIED) {
                throw e;
            } else {
                statusCode = Integer.parseInt(e.getCode());
            }
        }

        WebDAV webDAV = new WebDAV();
        webDAV.setStatusCode(statusCode);
        if (statusCode < REDIRECTION_CODE) {
            webDAV.setResHeaders(res.getHeaderList());
            // レスポンスボディを取得
            /** Get the response body. */
            webDAV.setStringBody(res.bodyAsString(charset));
        }

        return webDAV;
    }

    // /**
    // * DAVに対するGETメソッドをリクエストする.
    // * @param pathValue 取得するパス
    // * @return WebDAV GETしたストリームを含むWebDAVオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method requests the GET method for the DAV.
     * @param pathValue Path
     * @return WebDAV object that contains the stream
     * @throws DaoException Exception thrown
     */
    public WebDAV getStreamWebDAV(String pathValue) throws DaoException {
        String url = UrlUtils.append(this.getPath(), pathValue);
        DcResponse res = RestAdapterFactory.create(this.accessor).get(url, "application/octet-stream");

        // レスポンスボディのストリームを持つWebDAVを返却
        /** Return WebDAV with a stream of the response body. */
        WebDAV webDAV = new WebDAV();
        webDAV.setStreamBody(res.bodyAsStream());
        webDAV.setResHeaders(res.getHeaderList());
        webDAV.setStatusCode(res.getStatusCode());
        return webDAV;
    }

    // /**
    // * DAVに対するGETメソッドをリクエストする<br>
    // * ETag値がnull以外の場合は、If-None-Matchヘッダを付加する.
    // * @param pathValue 取得するパス
    // * @param eTag ETag値
    // * @return WebDAV GETしたストリームを含むWebDAVオブジェクト<br>
    // * 更新なしの場合はnullを返却する
    // * @throws DaoException DAO例外
    // */
    /**
     * This method requests the GET method for the DAV. If the ETag value is non-null, add an If-None-Match header.
     * @param pathValue Path
     * @param eTag ETag Value
     * @return WebDAV object that contains the stream If no update, then return null.
     * @throws DaoException Exception thrown
     */
    public WebDAV getStreamWebDAV(String pathValue, String eTag) throws DaoException {
        String url = UrlUtils.append(this.getPath(), pathValue);
        DcResponse res = null;
        int statusCode = 0;
        try {
            res = RestAdapterFactory.create(this.accessor).get(url, "application/octet-stream", eTag);
            statusCode = res.getStatusCode();
        } catch (DaoException e) {
            // TODO 300系をエラーとして処理することの可否は検討が必要
            /** TODO Propriety of treating it as an error 300 system needs to be considered. */
            if (Integer.parseInt(e.getCode()) != HttpStatus.SC_NOT_MODIFIED) {
                throw e;
            } else {
                statusCode = Integer.parseInt(e.getCode());
            }
        }

        WebDAV webDAV = new WebDAV();
        webDAV.setStatusCode(statusCode);
        if (statusCode < REDIRECTION_CODE) {
            webDAV.setResHeaders(res.getHeaderList());
            // レスポンスボディを取得
            /** Get the response body. */
            webDAV.setStreamBody(res.bodyAsStream());
        }

        return webDAV;
    }

    // /**
    // * 指定pathに任意のInputStreamの内容をPUTします. 指定IDのオブジェクトが既に存在すればそれを書き換え、存在しない場合はあらたに作成する.
    // * @param pathValue DAVのパス
    // * @param contentType メディアタイプ
    // * @param enc 文字コード(使用しない)
    // * @param is InputStream
    // * @param etag ETag値
    // * @return WebDAV WebDAVオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * The purpose of this method is to PUT the contents of the InputStream of any specified path. If ID already exists,
     * it rewrites the specified object , else creates a new one if it does not exist.
     * @param pathValue DAV path
     * @param contentType Media Type
     * @param enc character code (not used)
     * @param is InputStream
     * @param etag ETag Value
     * @return WebDAV WebDAV object
     * @throws DaoException Exception thrown
     */
    public WebDAV put(String pathValue, String contentType, String enc, InputStream is, String etag)
            throws DaoException {
        // ストリームの場合はエンコーディング指定は使用しない
        /** Do not use the encoding specified in the case of stream. */
        return put(pathValue, contentType, is, etag);
    }

    // /**
    // * 指定pathに任意のInputStreamの内容をPUTします. 指定IDのオブジェクトが既に存在すればそれを書き換え、存在しない場合はあらたに作成する.
    // * @param pathValue DAVのパス
    // * @param contentType メディアタイプ
    // * @param is InputStream
    // * @param etagValue ETag値
    // * @return WebDAV WebDAVオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * The purpose of this method is to PUT the contents of the InputStream of any specified path. If ID already exists,
     * it rewrites the specified object , else creates a new one if it does not exist.
     * @param pathValue DAV Path
     * @param contentType Media Type
     * @param is InputStream
     * @param etagValue ETag value
     * @return WebDAV WebDAV object
     * @throws DaoException Exception thrown
     */
    public WebDAV put(String pathValue, String contentType, InputStream is, String etagValue) throws DaoException {
        String url = UrlUtils.append(this.getPath(), pathValue);
        DcResponse res = ((RestAdapter) RestAdapterFactory.create(this.accessor)).putStream(url, contentType, is,
                etagValue);
        WebDAV webDAV = new WebDAV();
        webDAV.setResHeaders(res.getHeaderList());
        webDAV.setStatusCode(res.getStatusCode());
        return webDAV;
    }

    // /**
    // * 指定Pathに任意の文字列データをPUTします.
    // * @param pathValue DAVのパス
    // * @param contentType メディアタイプ
    // * @param data PUTするデータ
    // * @param etagValue PUT対象のETag。新規または強制更新の場合は "*" を指定する
    // * @return WebDAV WebDAVオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * The purpose of this method is to PUT a string of data to any specified Path.
     * @param pathValue DAV Path
     * @param contentType Media Type
     * @param data PUT data
     * @param etagValue Etag, specify "*" for forcing new or updated
     * @return WebDAV WebDAV object
     * @throws DaoException Exception thrown
     */
    public WebDAV put(String pathValue, String contentType, String data, String etagValue) throws DaoException {
        byte[] bs;
        try {
            bs = data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new DaoException("UnsupportedEncodingException", e);
        }
        InputStream is = new ByteArrayInputStream(bs);
        String url = UrlUtils.append(this.getPath(), pathValue);
        DcResponse res = ((RestAdapter) RestAdapterFactory.create(this.accessor)).putStream(url, contentType, is,
                etagValue);
        WebDAV webDAV = new WebDAV();
        webDAV.setResHeaders(res.getHeaderList());
        webDAV.setStatusCode(res.getStatusCode());
        return webDAV;
    }

    // /**
    // * 指定Pathに任意の文字列データをPUTします.
    // * @param pathValue DAVのパス
    // * @param contentType メディアタイプ
    // * @param enc 文字コード
    // * @param data PUTするデータ
    // * @param etag PUT対象のETag。新規または強制更新の場合は "*" を指定する
    // * @return WebDAV WebDAVオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * The purpose of this method is to PUT a string of data to any specified Path.
     * @param pathValue DAV Path
     * @param contentType Media Type
     * @param enc character code
     * @param data PUT Data
     * @param etag Etag, specify "*" for forcing new or updated
     * @return WebDAV WebDAV object
     * @throws DaoException Exception thrown
     */
    public WebDAV put(String pathValue, String contentType, String enc, String data, String etag) throws DaoException {
        byte[] bs;
        try {
            if (!enc.isEmpty()) {
                bs = data.getBytes(enc);
            } else {
                bs = data.getBytes("UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            throw new DaoException("UnsupportedEncodingException", e);
        }
        InputStream is = new ByteArrayInputStream(bs);
        String url = UrlUtils.append(this.getPath(), pathValue);
        DcResponse res = ((RestAdapter) RestAdapterFactory.create(this.accessor)).putStream(url, contentType, is, etag);
        WebDAV webDAV = new WebDAV();
        webDAV.setResHeaders(res.getHeaderList());
        webDAV.setStatusCode(res.getStatusCode());
        return webDAV;
    }

    // /**
    // * 指定PathのデータをDeleteします.
    // * @param pathValue DAVのパス
    // * @throws DaoException DAO例外
    // */
    /**
     * The purpose of this method is to Delete the data in the specified Path.
     * @param pathValue DAV Path
     * @throws DaoException Exception thrown
     */
    public void del(String pathValue) throws DaoException {
        String url = UrlUtils.append(this.getPath(), pathValue);
        RestAdapterFactory.create(this.accessor).del(url, "*");
    }

    // /**
    // * 指定PathのデータをDeleteします(ETag指定).
    // * @param pathValue DAVのパス
    // * @param etagValue PUT対象のETag。新規または強制更新の場合は "*" を指定する
    // * @throws DaoException DAO例外
    // */
    /**
     * The purpose of this method is to Delete the data in the specified Path(ETag specified).
     * @param pathValue DAV Path
     * @param etagValue Etag, specify "*" for forcing new or updated
     * @throws DaoException Exception thrown
     */
    public void del(String pathValue, String etagValue) throws DaoException {
        String url = UrlUtils.append(this.getPath(), pathValue);
        RestAdapterFactory.create(this.accessor).del(url, etagValue);
    }
}
