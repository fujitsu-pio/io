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

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpEntityEnclosingRequest;
import org.apache.http.HttpHeaders;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.entity.StringEntity;

import com.fujitsu.dc.client.DaoException;
import com.fujitsu.dc.client.utils.Log;

///**
// * リクエストオブジェクトを生成するBuilderクラス.
// */
/**
 * It creates a new object of DcHttpClient. This is a builder class that generates a request header object.
 */
public class DcRequestBuilder {
    // /** ログオブジェクト. */
    /** Log object. */
    private Log log = new Log(DcRequestBuilder.class);
    // /** URL文字列. */
    /** URL string. */
    private String urlValue = null;
    /** Http Request Headers. */
    private Map<String, String> headers = new HashMap<String, String>();
    // /** Method値. */
    /** Method Value. */
    private String methodValue = null;
    // /** Token値. */
    /** Token Value. */
    private String tokenValue = null;
    // /** Body値. */
    /** Body Value. */
    private String bodyValue = null;
    // /** Body(InputStream)値. */
    /** Body(InputStream) Value. */
    private InputStream bodyStream = null;
    // /** デフォルトヘッダ. */
    /** Default Headers. */
    private HashMap<String, String> defaultHeaders;
    // /** 日本語UTFのためのマスク値. */
    /** Mask value for Japanese UTF. */
    public static final int CHAR_MASK = 0x7f;
    // /** 日本語UTFのためのマスク値. */
    /** Mask value for Japanese UTF. */
    public static final int CHAR_JPUTF_MASK = 0x10000;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor.
     */
    public DcRequestBuilder() {
    }

    /**
     * This method is used to get Http Request Headers.
     * @return map of headers
     */
    public Map<String, String> getHeaders() {
        return headers;
    }

    // /**
    // * URL文字列の取得.
    // * @return URL文字列
    // */
    /**
     * This method is used to get the URL value.
     * @return URL string.
     */
    public String getUrl() {
        return this.urlValue;
    }

    /**
     * This method is used to get the ContentType.
     * @return ContentType Value
     */
    public String getContentType() {
        return this.headers.get(HttpHeaders.CONTENT_TYPE);
    }

    /**
     * This method is used to get the ContentLength.
     * @return ContentLength value
     */
    public String getContentLength() {
        return this.headers.get(HttpHeaders.CONTENT_LENGTH);
    }

    // /**
    // * Acceptの取得.
    // * @return Accept Value
    // */
    /**
     * This method is used to get the accept parameter in header.
     * @return accept value
     */
    public String getAccept() {
        return this.headers.get(HttpHeaders.ACCEPT);
    }

    // /**
    // * Methodの取得.
    // * @return Method
    // */
    /**
     * This method is used to get the method value.
     * @return Method value
     */
    public String getMethod() {
        return this.methodValue;
    }

    // /**
    // * Tokenの取得.
    // * @return Token値
    // */
    /**
     * This method is used to get the token value.
     * @return Token value
     */
    public String getToken() {
        return this.tokenValue;
    }

    // /**
    // * AcceptEncodingを取得.
    // * @return AcceptEncoding Header Value
    // */
    /**
     * This method is used to get the accept encoding parameter in header.
     * @return Accept Encoding value
     */
    public String getAcceptEncoding() {
        return this.headers.get(HttpHeaders.ACCEPT_ENCODING);
    }

    // /**
    // * Depth値を取得.
    // * @return Depth Header Value
    // */
    /**
     * This method is used to get the depth parameter in header.
     * @return Depth value
     */
    public String getDepth() {
        return this.headers.get(HttpHeaders.DEPTH);
    }

    // /**
    // * IF-MATCH値を取得.
    // * @return IF-MATCH Header Value
    // */
    /**
     * This method is used to get the if match parameter in header.
     * @return IfMatch value
     */
    public String getIfMatch() {
        return this.headers.get(HttpHeaders.IF_MATCH);
    }

    // /**
    // * IF-NONE-MATCH値を取得.
    // * @return IF-NONE-MATCH Header Value
    // */
    /**
     * This method is used to get the if none match parameter in header.
     * @return IfNoneMatch value
     */
    public String getIfNoneMatch() {
        return this.headers.get(HttpHeaders.IF_NONE_MATCH);
    }

    // /**
    // * Body(String)の取得.
    // * @return Body値
    // */
    /**
     * This method is used to get the Body(String).
     * @return Body value
     */
    public String getBody() {
        return this.bodyValue;
    }

    // /**
    // * Body(InputStream)の取得.
    // * @return Body値
    // */
    /**
     * This method is used to get the Body(InputStream).
     * @return BodyStream value
     */
    public InputStream getBodyStream() {
        return this.bodyStream;
    }

    // /**
    // * URLをセットする.
    // * @param value URL文字列
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the URL string.
     * @param value URL string
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder url(String value) {
        this.urlValue = value;
        return this;
    }

    // /**
    // * Acceptをセットする.
    // * @param value Accept Header Value
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the accept Header value.
     * @param value Accept Header Value
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder accept(String value) {
        if (value != null) {
            this.headers.put(HttpHeaders.ACCEPT, value);
        }
        return this;
    }

    // /**
    // * ContentTypeをセットする.
    // * @param value ContentType Header Value
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the ContentType Header value.
     * @param value ContentType Header Value
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder contentType(String value) {
        if (value != null) {
            this.headers.put(HttpHeaders.CONTENT_TYPE, value);
        }
        return this;
    }

    // /**
    // * ContentLength.
    // * @param value content length Header Value
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the ContentLength Header value.
     * @param value content length Header Value
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder contentLength(String value) {
        if (value != null) {
            this.headers.put(HttpHeaders.CONTENT_LENGTH, value);
        }
        return this;
    }

    // /**
    // * Methodをセットする.
    // * @param value Method値
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the method value.
     * @param value Method
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder method(String value) {
        this.methodValue = value;
        return this;
    }

    // /**
    // * Tokenをセットする.
    // * @param value Token値
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the token value.
     * @param value TokenValue
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder token(String value) {
        if (value != null) {
            this.tokenValue = value;
        }
        return this;
    }

    // /**
    // * AcceptEncodingをセットする.
    // * @param value AcceptEncoding Header Value
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the acceptEncoding Header value.
     * @param value AcceptEncoding Header Value
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder acceptEncoding(String value) {
        if (value != null) {
            this.headers.put(HttpHeaders.ACCEPT_ENCODING, value);
        }
        return this;
    }

    // /**
    // * IF-MATCHをセットする.
    // * @param value IF-MATCH Header Value
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the IF-MATCH Header value.
     * @param value IF-MATCH Header Value
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder ifMatch(String value) {
        if (value != null) {
            this.headers.put(HttpHeaders.IF_MATCH, value);
        }
        return this;
    }

    // /**
    // * IF-NONE-MATCHをセットする.
    // * @param value IF-NONE-MATCH Header Value
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the IF-NONE-MATCH Header value.
     * @param value IF-NONE-MATCH Header Value
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder ifNoneMatch(String value) {
        if (value != null) {
            this.headers.put(HttpHeaders.IF_NONE_MATCH, value);
        }
        return this;
    }

    // /**
    // * Depthをセットする.
    // * @param value Depth Header Value
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the Depth Header value.
     * @param value Depth Header Value
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder depth(String value) {
        if (value != null) {
            this.headers.put(HttpHeaders.DEPTH, value);
        }
        return this;
    }

    // /**
    // * Body(文字列)をセットする.
    // * @param value Body値
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the Body string value.
     * @param value Body String
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder body(String value) {
        this.bodyValue = value;
        return this;
    }

    // /**
    // * Body(Stream)をセットする.
    // * @param is Body値
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the Body stream value.
     * @param is Body Stream
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder body(InputStream is) {
        this.bodyStream = is;
        return this;
    }

    // /**
    // * デフォルトヘッダをセットする.
    // * @param value デフォルトヘッダ
    // * @return 自分自身のオブジェクト
    // */
    /**
     * This method is used to set the Default Headers value.
     * @param value Default Headers
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder defaultHeaders(HashMap<String, String> value) {
        this.defaultHeaders = value;
        return this;
    }

    /**
     * This method sets an arbitrary HTTP request header.
     * @param name arbitrary header name
     * @param value value for the header
     * @return DcRequestBuilder Its own object
     */
    public DcRequestBuilder header(String name, String value) {
        this.headers.put(name, value);
        return this;
    }

    // /**
    // * HttpUriRequestオブジェクトを生成する.
    // * @return 生成したHttpUriRequestオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to generate a HttpUriRequest object by setting the parameters in request header.
     * @return HttpUriRequest object that is generated
     * @throws DaoException Exception thrown
     */
    public HttpUriRequest build() throws DaoException {
        HttpUriRequest req = null;
        if (HttpMethods.PUT.equals(this.methodValue)) {
            req = new HttpPut(this.urlValue);
        } else if (HttpMethods.POST.equals(this.methodValue)) {
            req = new HttpPost(this.urlValue);
        } else if (HttpMethods.DELETE.equals(this.methodValue)) {
            req = new HttpDelete(this.urlValue);
        } else if (HttpMethods.ACL.equals(this.methodValue)) {
            req = new HttpAclMethod(this.urlValue);
        } else if (HttpMethods.MKCOL.equals(this.methodValue)) {
            req = new HttpMkColMethod(this.urlValue);
        } else if (HttpMethods.PROPPATCH.equals(this.methodValue)) {
            req = new HttpPropPatchMethod(this.urlValue);
        } else if (HttpMethods.PROPFIND.equals(this.methodValue)) {
            req = new HttpPropfindMethod(this.urlValue);
        } else if (HttpMethods.GET.equals(this.methodValue)) {
            req = new HttpGet(this.urlValue);
        } else if (HttpMethods.MERGE.equals(this.methodValue)) {
            req = new HttpMergeMethod(this.urlValue);
        }

        if (this.tokenValue != null) {
            req.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + this.tokenValue);
        }

        /** include header parameters if any. */
        for (String key : headers.keySet()) {
            String value = headers.get(key);
            req.addHeader(key, value);
        }

        // デフォルトヘッダがセットされていれば、それらを設定。
        /** If Default header is set, configure them. */
        // 最初にセットしない理由は、リクエストヘッダは、同名ヘッダが複数登録されてしまうため
        /**
         * The reason you do not want to set for the first time, since the request header, would have been more than one
         * registration is the same name header
         */
        if (this.defaultHeaders != null) {
            for (String key : this.defaultHeaders.keySet()) {
                String val = this.defaultHeaders.get(key);
                Header[] headerItems = req.getHeaders(key);
                if (headerItems.length == 0) {
                    req.addHeader(key, val);
                }
            }
        }
        if (this.bodyValue != null) {
            HttpEntity body = null;
            try {
                if (this.getContentType() != "" && RestAdapter.CONTENT_TYPE_JSON.equals(this.getContentType())) {
                    String bodyStr = toUniversalCharacterNames(this.bodyValue);
                    body = new StringEntity(bodyStr);
                } else {
                    body = new StringEntity(this.bodyValue, RestAdapter.ENCODE);
                }
            } catch (UnsupportedEncodingException e) {
                throw DaoException.create("error while request body encoding : " + e.getMessage(), 0);
            }
            ((HttpEntityEnclosingRequest) req).setEntity(body);
        }
        if (this.bodyStream != null) {
            InputStreamEntity body = new InputStreamEntity(this.bodyStream, -1);
            body.setChunked(true);
            this.bodyValue = "[stream]";
            ((HttpEntityEnclosingRequest) req).setEntity(body);
        }
        if (req != null) {
            log.debug("");
            log.debug("【Request】 " + req.getMethod() + "  " + req.getURI());
            Header[] allheaders = req.getAllHeaders();
            for (int i = 0; i < allheaders.length; i++) {
                log.debug("RequestHeader[" + allheaders[i].getName() + "] : " + allheaders[i].getValue());
            }
            log.debug("RequestBody : " + bodyValue);
        }
        return req;
    }

    // /**
    // * 日本語文字列エンコード.
    // * @param inStr エンコード対象の文字列
    // * @return エンコード後の文字列
    // */
    /**
     * This method i used for Japanese string encoding.
     * @param inStr String to be encoded
     * @return String after encoding
     */
    private String toUniversalCharacterNames(String inStr) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < inStr.length(); i++) {
            int c = inStr.charAt(i);
            if (c > CHAR_MASK) {
                sb.append("\\u");
                sb.append(Integer.toHexString(CHAR_JPUTF_MASK + c).substring(1));
            } else {
                sb.append((char) c);
            }
        }
        return sb.substring(0, sb.length());
    }

}
