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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.AbstractHttpClient;

import com.fujitsu.dc.client.Accessor;
import com.fujitsu.dc.client.DaoConfig;
import com.fujitsu.dc.client.DaoException;
import com.fujitsu.dc.client.DcContext;

///**
// * RESTアクセスのためのクラス.
// */
/**
 * It creates a new object of RestAdapter. This class is used for REST access.
 */
public class RestAdapter implements IRestAdapter {
    // /** ログオブジェクト. */
    // private static Log log = LogFactory.getLog(RestAdapter.class);

    /** HTTPClient. */
    private HttpClient httpClient;
    // /** アクセス主体. */
    /** Reference to Accessor. */
    private Accessor accessor;

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor to initialize various fields.
     * @param as Accessor
     */
    public RestAdapter(Accessor as) {
        this.accessor = as;
        DaoConfig config = accessor.getDaoConfig();
        httpClient = config.getHttpClient();
        if (httpClient == null) {
            httpClient = HttpClientFactory.create(DcContext.getPlatform(), config.getConnectionTimeout());
        }
        String proxyHost = config.getProxyHostname();
        int proxyPort = config.getProxyPort();
        if (proxyHost != null) {
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            httpClient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
            // ID/Passが共にnullでなければ認証Proxyをセット
            String proxyUsername = config.getProxyUsername();
            String proxyPassword = config.getProxyPassword();
            if (httpClient instanceof AbstractHttpClient && proxyUsername != null && proxyPassword != null) {
                ((AbstractHttpClient) httpClient).getCredentialsProvider().setCredentials(
                        new AuthScope(proxyHost, proxyPort),
                        new UsernamePasswordCredentials(proxyUsername, proxyPassword));
            }
        }
    }

    // /**
    // * HttpClientを置き換える(ユニットテスト用).
    // * @param value HttpClientオブジェクト
    // */
    /**
     * This method is used to set the HttpClient value.
     * @param value HttpClient object
     */
    public void setHttpClient(HttpClient value) {
        this.httpClient = value;
    }

    // /**
    // * HttpClientを取得する.
    // * @return HttpClientオブジェクト
    // */
    /**
     * This method is used to get the HttpClient value.
     * @return HttpClient object
     */
    public HttpClient getHttpClient() {
        return this.httpClient;
    }

    // /**
    // * レスポンスボディを受け取るGETメソッド.
    // * @param url リクエスト対象URL
    // * @param accept Acceptヘッダ値
    // * @return DcResponse型
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the GET method to receive the response body.
     * @param url Target Request URL
     * @param accept Accept Header Value
     * @return DcResponse value
     * @throws DaoException Exception thrown
     */
    public DcResponse get(String url, String accept) throws DaoException {
        return get(url, accept, null);
    }

    // /**
    // * レスポンスボディを受け取るGETメソッド(If-None-Macth指定).
    // * @param url リクエスト対象URL
    // * @param accept Acceptヘッダ値
    // * @param etag 取得するEtag
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the GET method to receive the response body (If-None-Match specified).
     * @param url Target Request URL
     * @param accept Accept Header Value
     * @param etag Etag value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse get(String url, String accept, String etag) throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method(HttpMethods.GET).acceptEncoding("gzip")
                .accept(accept).token(getToken()).ifNoneMatch(etag).defaultHeaders(this.accessor.getDefaultHeaders())
                .build();
        return this.request(req);
    }

    // /**
    // * レスポンスボディを受け取るGETメソッド(If-None-Macth指定).
    // * @param url リクエスト対象URL
    // * @param headers リクエストヘッダ
    // * @param etag 取得するEtag
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the GET method to receive the response body using headers (If-None-Match specified).
     * @param url Target Request URL
     * @param headers Request header
     * @param etag Etag value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse get(String url, Map<String, String> headers, String etag) throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method(HttpMethods.GET).acceptEncoding("gzip")
                .token(getToken()).ifNoneMatch(etag).defaultHeaders(this.accessor.getDefaultHeaders()).build();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            req.setHeader(entry.getKey(), entry.getValue());
        }
        return this.request(req);
    }

    // /**
    // * HEADメソッド.
    // * @param url リクエスト対象URL
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the HEAD method.
     * @param url Target Request URL
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse head(String url) throws DaoException {
        return get(url, CONTENT_TYPE_JSON, null);
    }

    // /**
    // * レスポンスボディを受ける PUTメソッド.
    // * @param url リクエスト対象URL
    // * @param map リクエストヘッダーのハッシュマップ
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the PUT method that receives the response body.
     * @param url Target Request URL
     * @param map Hash map of Request Header
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse put(String url, HashMap<String, String> map) throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method(HttpMethods.PUT).token(getToken())
                .defaultHeaders(this.accessor.getDefaultHeaders()).build();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            req.setHeader((String) entry.getKey(), (String) entry.getValue());
        }
        return this.request(req);
    }

    // /**
    // * レスポンスボディを受ける PUTメソッド.
    // * @param url リクエスト対象URL
    // * @param data 書き込むデータ
    // * @param etag ETag
    // * @param contentType CONTENT-TYPE値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the PUT method that receives the response body and uses Etag value.
     * @param url Target Request URL
     * @param data Data to be sent
     * @param etag ETag value
     * @param contentType CONTENT-TYPE value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse put(String url, String data, String etag, String contentType) throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method(HttpMethods.PUT).contentType(contentType)
                .ifMatch(etag).body(data).token(getToken()).defaultHeaders(this.accessor.getDefaultHeaders()).build();
        return this.request(req);
    }

    // /**
    // * レスポンスボディを受ける PUTメソッド.
    // * @param url リクエスト対象URL
    // * @param data 書き込むデータ
    // * @param etag ETag
    // * @param map リクエストヘッダーのハッシュマップ
    // * @param contentType CONTENT-TYPE値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the PUT method that receives the response body and uses Etag value and header map.
     * @param url Target Request URL
     * @param data Data to be sent
     * @param etag ETag value
     * @param map HashMap of Request Header
     * @param contentType CONTENT-TYPE value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse put(String url, String data, String etag, HashMap<String, String> map, String contentType)
            throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method(HttpMethods.PUT).contentType(contentType)
                .ifMatch(etag).body(data).token(getToken()).defaultHeaders(this.accessor.getDefaultHeaders()).build();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            req.setHeader((String) entry.getKey(), (String) entry.getValue());
        }
        return this.request(req);
    }

    // /**
    // * Stream登録を行うPUTメソッド.
    // * @param url PUT対象のURL
    // * @param contentType メディアタイプ
    // * @param is PUTするデータストリーム
    // * @param etag PUT対象のETag
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the PUT methods for Stream registration.
     * @param url PUT Target URL
     * @param contentType Media Type
     * @param is Data Stream to be PUT
     * @param etag Etag value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse putStream(String url, String contentType, InputStream is, String etag) throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method(HttpMethods.PUT).contentType(contentType)
                .ifMatch(etag).body(is).token(getToken()).defaultHeaders(this.accessor.getDefaultHeaders()).build();
        return this.request(req);
    }

    // /**
    // * リクエストボディを受け取る POSTメソッド.
    // * @param url リクエスト対象URL
    // * @param data 書き込むデータ
    // * @param contentType CONTENT-TYPE値
    // * @param needAuthorization 認証が必要かどうか
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the POST method that receives the request body and need authorization.
     * @param url PUT Target URL
     * @param data Data to be written
     * @param contentType CONTENT-TYPE value
     * @param needAuthorization For authentication
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse post(String url, String data, String contentType, Boolean needAuthorization) throws DaoException {
        String token = null;
        if (needAuthorization) {
            token = getToken();
        }
        HttpUriRequest req = new DcRequestBuilder().url(url).method(HttpMethods.POST).contentType(contentType)
                .body(data).token(token).defaultHeaders(this.accessor.getDefaultHeaders()).build();
        return this.request(req);
    }

    // /**
    // * リクエストボディを受け取る POSTメソッド.
    // * @param url リクエスト対象URL
    // * @param data 書き込むデータ
    // * @param contentType CONTENT-TYPE値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the POST method that receives the request body and does not need authorization.
     * @param url PUT Target URL
     * @param data Data to be written
     * @param contentType CONTENT-TYPE value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse post(String url, String data, String contentType) throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method(HttpMethods.POST).contentType(contentType)
                .body(data).token(getToken()).defaultHeaders(this.accessor.getDefaultHeaders()).build();
        return this.request(req);
    }

    // /**
    // * リクエストヘッダを指定するPOSTメソッド.
    // * @param url リクエスト対象URL
    // * @param map リクエストヘッダーのハッシュマップ
    // * @param data 書き込むデータ
    // * @param contentType CONTENT-TYPE値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the POST method that receives the request body that does not need authorization and uses header map.
     * @param url Target URL
     * @param map HashMap of Request Header
     * @param data Data to be written
     * @param contentType CONTENT-TYPE value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse post(String url, HashMap<String, String> map, String data, String contentType)
            throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method(HttpMethods.POST).contentType(contentType)
                .body(data).token(getToken()).defaultHeaders(this.accessor.getDefaultHeaders()).build();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            req.setHeader(entry.getKey(), entry.getValue());
        }
        return this.request(req);
    }

    // /**
    // * レスポンスボディを受けるMERGEメソッド.
    // * @param url リクエスト対象URL
    // * @param data 書き込むデータ
    // * @param etag ETag
    // * @param contentType CONTENT-TYPE値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the MERGE method that receives the response body.
     * @param url Target URL
     * @param data Data to be written
     * @param etag ETag value
     * @param contentType CONTENT-TYPE value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse merge(String url, String data, String etag, String contentType) throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method("MERGE").contentType(contentType).ifMatch(etag)
                .body(data).token(getToken()).defaultHeaders(this.accessor.getDefaultHeaders()).build();
        return this.request(req);
    }

    // /**
    // * DELETEメソッド.
    // * @param url リクエスト対象URL
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the DELETE method that internally calls its overloaded version.
     * @param url Target URL
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse del(String url) throws DaoException {
        Map<String, String> headers = null;
        return this.del(url, headers);
    }

    // /**
    // * DELETEメソッド.
    // * @param url リクエスト対象URL
    // * @param etag DELETE対象のETag
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the DELETE method that uses Etag value.
     * @param url Target URL
     * @param etag ETag value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse del(String url, String etag) throws DaoException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("If-Match", etag);
        return this.del(url, headers);
    }

    /**
     * This is the overloaded version of delete method with provision for arbitrary header.
     * @param url to be deleted
     * @param headers request headers
     * @return DcResponse response
     * @throws DaoException Library Exception
     */
    public DcResponse del(String url, Map<String, String> headers) throws DaoException {
        DcRequestBuilder requestBuilder = new DcRequestBuilder().url(url).method(HttpMethods.DELETE).token(getToken())
                .defaultHeaders(this.accessor.getDefaultHeaders());
        /** add the headers to request builder. */
        if (headers != null && headers.size() > 0) {
            Set<Entry<String, String>> entrySet = headers.entrySet();
            for (Entry<String, String> entry : entrySet) {
                if (entry.getValue() != null) {
                    requestBuilder.header(entry.getKey(), entry.getValue());
                }
            }
        }
        HttpUriRequest req = requestBuilder.build();
        return this.request(req);
    }

    // /**
    // * ACLメソッド.
    // * @param url リクエスト対象URL
    // * @param body リクエストボディ
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the ACL method for setting the ACL.
     * @param url Target URL
     * @param body Request Body
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse acl(String url, String body) throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method("ACL").contentType(CONTENT_TYPE_XML)
                .accept(CONTENT_TYPE_XML).body(body).token(getToken())
                .defaultHeaders(this.accessor.getDefaultHeaders()).build();
        return this.request(req);
    }

    // /**
    // * MKCOLメソッド.
    // * @param url リクエスト対象URL
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the MKCOL method to create a collection.
     * @param url Target URL
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse mkcol(String url) throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method("MKCOL").contentType(CONTENT_TYPE_XML)
                .accept(CONTENT_TYPE_XML).body(REQUEST_BODY_MKCOL_XML).token(getToken())
                .defaultHeaders(this.accessor.getDefaultHeaders()).build();
        return this.request(req);
    }

    // /**
    // * MKCOL拡張メソッド(ODataコレクション作成).
    // * @param url リクエスト対象URL
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the MKODATA method to create a OData collection.
     * @param url Target URL
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse mkOData(String url) throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method("MKCOL").contentType(CONTENT_TYPE_XML)
                .accept(CONTENT_TYPE_XML).body(REQUEST_BODYMKODATA_XML).token(getToken())
                .defaultHeaders(this.accessor.getDefaultHeaders()).build();
        return this.request(req);
    }

    // /**
    // * MKCOL拡張メソッド(Serviceコレクション作成).
    // * @param url リクエスト対象URL
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the MKSERVICE method to create a service.
     * @param url Target URL
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse mkService(String url) throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method("MKCOL").contentType(CONTENT_TYPE_XML)
                .accept(CONTENT_TYPE_XML).body(REQUEST_BODY_SERVICE_XML).token(getToken())
                .defaultHeaders(this.accessor.getDefaultHeaders()).build();
        return this.request(req);
    }

    // /**
    // * MKCOL拡張メソッド(Calendarコレクション作成).
    // * @param url リクエスト対象URL
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the MKCALENDAR method to create a calendar.
     * @param url Target URL
     * @throws DaoException Exception thrown
     */
    public void mkCalendar(String url) throws DaoException {
        this.mkcol(url);
    }

    // /**
    // * サービス登録専用PROPPATCHメソッド.
    // * @param url リクエスト対象URL
    // * @param key プロパティ名
    // * @param value プロパティの値
    // * @param subject サービスサブジェクトの値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the PROPPATCH method for service registration with subject.
     * @param url Target URL
     * @param key Property Name
     * @param value Property Value
     * @param subject Subject service
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse setService(String url, String key, String value, String subject) throws DaoException {
        String body = REQUEST_BODY_PROPPATCH_XML.replace("SERVICE_KEY", key).replace("SERVICE_VALUE", value)
                .replace("SERVICE_SUBJECT", subject);
        HttpUriRequest req = new DcRequestBuilder().url(url).method("PROPPATCH").contentType(CONTENT_TYPE_XML)
                .accept(CONTENT_TYPE_XML).body(body).token(getToken())
                .defaultHeaders(this.accessor.getDefaultHeaders()).build();
        return this.request(req);
    }

    // /**
    // * PROPPATCHメソッド.
    // * @param url リクエスト対象URL
    // * @param key プロパティ名
    // * @param value プロパティの値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the PROPPATCH method for service registration without subject.
     * @param url Target URL
     * @param key Property Name
     * @param value Property Value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse proppatch(String url, String key, String value) throws DaoException {
        StringBuilder sb = new StringBuilder();
        sb.append("<D:propertyupdate xmlns:D=\"DAV:\" xmlns:dc=\"urn:x-dc1:xmlns\"><D:set><D:prop>");
        sb.append("<");
        sb.append(key);
        sb.append(">");
        sb.append(value);
        sb.append("</");
        sb.append(key);
        sb.append(">");
        sb.append("</D:prop></D:set></D:propertyupdate>");
        HttpUriRequest req = new DcRequestBuilder().url(url).method("PROPPATCH").contentType(CONTENT_TYPE_XML)
                .accept(CONTENT_TYPE_XML).body(sb.toString()).token(getToken())
                .defaultHeaders(this.accessor.getDefaultHeaders()).build();
        return this.request(req);
    }

    // /**
    // * PROPFINDメソッド.
    // * @param url リクエスト対象URL
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This is the PROPFIND method.
     * @param url Target URL
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse propfind(String url) throws DaoException {
        HttpUriRequest req = new DcRequestBuilder().url(url).method("PROPFIND").contentType(CONTENT_TYPE_XML)
                .token(getToken()).accept(CONTENT_TYPE_XML).depth("1")
                .defaultHeaders(this.accessor.getDefaultHeaders()).build();
        return this.request(req);
    }

    // /**
    // * Reponseボディを受ける場合のHTTPリクエストを行う.
    // * @param httpReq HTTPリクエスト
    // * @return DCレスポンスオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to make HTTP requests may be subject to response body.
     * @param httpReq HTTPRequest
     * @return DCReaponse object
     * @throws DaoException Exception thrown
     */
    private DcResponse request(HttpUriRequest httpReq) throws DaoException {
        try {
            HttpResponse objResponse = httpClient.execute(httpReq);
            DcResponse dcRes = new DcResponse(objResponse);

            this.accessor.setResHeaders(objResponse.getAllHeaders());
            int statusCode = objResponse.getStatusLine().getStatusCode();
            if (statusCode >= STATUS300 && statusCode != HttpStatus.SC_MOVED_TEMPORARILY) {
                throw DaoException.create(dcRes.bodyAsString(), statusCode);
            }
            return dcRes;
        } catch (IOException ioe) {
            throw DaoException.create("io exception : " + ioe.getMessage(), 0);
        }
    }

    // /**
    // * Accessorからトークンを取得する.
    // * @return トークン
    // */
    /**
     * This method gets the token from Accessor.
     * @return Token value
     */
    private String getToken() {
        String token = this.accessor.getAccessToken();
        return token;
    }

    /**
     * This method is used to make a MKCOL request through actual bar file.
     * @param url box path
     * @param requestBody HTTP Request Body to be sent in the form of InputStream.
     * @param contentType ContentType Header Value
     * @return DcResponse response
     * @throws DaoException Library Exception
     */
    public DcResponse mkcol(String url, InputStream requestBody, String contentType) throws DaoException {
        DcRequestBuilder requestBuilder = new DcRequestBuilder().url(url).method("MKCOL").contentType(contentType)
                .body(requestBody).token(getToken()).defaultHeaders(this.accessor.getDefaultHeaders());
        HttpUriRequest req = requestBuilder.build();
        return this.request(req);
    }
}
