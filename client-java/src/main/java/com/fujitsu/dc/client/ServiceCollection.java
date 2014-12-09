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
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;

import com.fujitsu.dc.client.http.DcRequestBuilder;
import com.fujitsu.dc.client.http.RestAdapter;
import com.fujitsu.dc.client.http.RestAdapterFactory;
import com.fujitsu.dc.client.utils.UrlUtils;

///**
// * ServiceのCURDのためのクラス.
// */
/**
 * It creates a new object of ServiceCollection. This class performs CRUD operations for ServiceCollection.
 */
public class ServiceCollection extends DcCollection {
    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor with one argument calling its parent constructor internally.
     * @param as Accessor
     */
    public ServiceCollection(Accessor as) {
        super(as);
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // * @param path パス文字列
    // */
    /**
     * This is the parameterized constructor with two arguments calling its parent constructor internally.
     * @param as Accessor
     * @param path string
     */
    public ServiceCollection(Accessor as, String path) {
        super(as, path);
    }

    // /**
    // * サービスの設定.
    // * @param key プロパティ名
    // * @param value プロパティの値
    // * @param subject サービスサブジェクトの値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method configures a set of services.
     * @param key Property Name
     * @param value Property Value
     * @param subject Value of the service subject
     * @throws DaoException Exception thrown
     */
    public void configure(String key, String value, String subject) throws DaoException {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        rest.setService(this.getPath(), key, value, subject);
    }

    // /**
    // * Call the Engine Service.
    // * @param method HTTP Request Method
    // * @param name 実行するサービス名
    // * @param body HTTP Request Body
    // * @return DcResponseオブジェクト
    // */
    /**
     * This method is used to call the Engine Service. It internally calls its overloaded version.
     * @param method HTTP Request Method
     * @param name Service name to be executed
     * @param body HTTP Request Body
     * @return HttpResponse object
     */
    public HttpResponse call(String method, String name, String body) {
        return this.call(method, name, body, null);
    }

    // /**
    // * Call the Engine Service with extra argument header map.
    // * @param method Http Method
    // * @param name 実行するサービス名
    // * @param body リクエストボディ
    // * @param headers header map key value pair
    // * @return DcResponseオブジェクト
    // */
    /**
     * This method is used to call the Engine Service with extra argument header map.
     * @param method Http Method
     * @param name Service name to be executed
     * @param body HTTP Request Body
     * @param headers header map key value pair
     * @return HttpResponse object
     */
    public HttpResponse call(String method, String name, String body, Map<String, String> headers) {
        RestAdapter rest = (RestAdapter) RestAdapterFactory.create(this.accessor);
        String url = UrlUtils.append(this.getPath(), name);
        DcRequestBuilder drb = new DcRequestBuilder().url(url).method(method).token(this.accessor.getAccessToken());

        /** add the headers to request builder */
        if (headers != null && headers.size() > 0) {
            Set<Entry<String, String>> entrySet = headers.entrySet();
            for (Entry<String, String> entry : entrySet) {
                drb.header(entry.getKey(), entry.getValue());
            }
        }

        if (body != null && !"".equals(body)) {
            drb.body(body);
        }
        HttpResponse response = null;
        try {
            HttpUriRequest req = drb.build();
            response = rest.getHttpClient().execute(req);
        } catch (DaoException e) {
            throw new RuntimeException(e);
        } catch (ClientProtocolException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return response;
    }

    // /**
    // * 指定Pathに任意の文字列データをPUTします.
    // * @param pathValue DAVのパス
    // * @param contentType メディアタイプ
    // * @param data PUTするデータ
    // * @param etagValue PUT対象のETag。新規または強制更新の場合は "*" を指定する
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to PUT a string of data to any specified Path.
     * @param pathValue DAV Path
     * @param contentType Media type
     * @param data PUT data
     * @param etagValue ETag of PUT target. Specify "*" for forcing new or updated
     * @throws DaoException Exception thrown
     */
    public void put(String pathValue, String contentType, String data, String etagValue) throws DaoException {
        byte[] bs;
        try {
            bs = data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new DaoException("UnsupportedEncodingException", e);
        }
        InputStream is = new ByteArrayInputStream(bs);
        this.put(pathValue, contentType, is, etagValue);
    }

    // /**
    // * 指定pathに任意のInputStreamの内容をPUTします. 指定IDのオブジェクトが既に存在すればそれを書き換え、存在しない場合はあらたに作成する.
    // * @param pathValue DAVのパス
    // * @param contentType メディアタイプ
    // * @param is InputStream
    // * @param etagValue ETag値
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used To PUT the contents of the InputStream of any specified path. Rewrites it, if the specified
     * object ID already exists, or creates a new one if it does not exist.
     * @param pathValue DAV Path
     * @param contentType Media type
     * @param is InputStream
     * @param etagValue ETag value
     * @throws DaoException Exception thrown
     */
    public void put(String pathValue, String contentType, InputStream is, String etagValue) throws DaoException {
        String url = UrlUtils.append(this.getPath(), "__src/" + pathValue);
        ((RestAdapter) RestAdapterFactory.create(this.accessor)).putStream(url, contentType, is, etagValue);
    }

    // /**
    // * 指定PathのデータをDeleteします.
    // * @param pathValue DAVのパス
    // * @throws DaoException DAO例外
    // */
    /**
     * This method deletes the data in the path specified.
     * @param pathValue DAV Path
     * @throws DaoException Exception thrown
     */
    public void del(String pathValue) throws DaoException {
        String url = UrlUtils.append(this.getPath(), "__src/" + pathValue);
        RestAdapterFactory.create(this.accessor).del(url, "*");
    }
}
