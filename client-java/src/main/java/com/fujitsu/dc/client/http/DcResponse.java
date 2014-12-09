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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.fujitsu.dc.client.DaoException;
import com.fujitsu.dc.client.utils.Log;

///**
// * DAVのレスポンス型.
// */
/**
 * It creates a new object of DcResponse. This class is used to handle DAV Response.
 */
public class DcResponse {
    // /** ログオブジェクト. */
    /** Log object. */
    private Log log = new Log(DcResponse.class);

    // /** レスポンスオブジェクト. */
    /** Response object. */
    private HttpResponse response;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor.
     */
    public DcResponse() {
    }

    // /**
    // * コンストラクタ.
    // * @param resObj レスポンスオブジェクト
    // */
    /**
     * This is the parameterized constructor used to initialize response.
     * @param resObj Response object
     */
    public DcResponse(final HttpResponse resObj) {
        this.response = resObj;
        debugHttpResponse(resObj);
    }

    // /**
    // * ステータスコードの取得.
    // * @return ステータスコード
    // */
    /**
     * This method is used to return status code value.
     * @return Status Code value
     */
    public final int getStatusCode() {
        return response.getStatusLine().getStatusCode();
    }

    // /**
    // * 指定したレスポンスヘッダの値を取得する.
    // * @param key ヘッダのキー
    // * @return 指定したキーの値
    // */
    /**
     * This method is used to get the value of the specified response header.
     * @param key Header Key
     * @return Value of the key specified
     */
    public final String getHeader(final String key) {
        Header[] headers = response.getHeaders(key);
        if (headers.length > 0) {
            return headers[0].getValue();
        } else {
            return "";
        }
    }

    // /**
    // * レスポンスヘッダの一覧を取得する.
    // * @return レスポンスヘッダの一覧
    // */
    /**
     * This method is used to get a list of response headers.
     * @return List of response headers
     */
    public final Header[] getHeaderList() {
        return response.getAllHeaders();
    }

    // /**
    // * レスポンスボディをストリームで取得.
    // * @return ストリーム
    // */
    /**
     * This method is used to the response body in stream format.
     * @return Stream
     */
    public final InputStream bodyAsStream() {
        InputStream is = null;
        try {
            is = this.getResponseBodyInputStream(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return is;
    }

    // /**
    // * レスポンスボディを文字列で取得.
    // * @return ボディテキスト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to the response body in string format. It internally calls its overloaded version.
     * @return Body text
     * @throws DaoException Exception thrown
     */
    public final String bodyAsString() throws DaoException {
        return this.bodyAsString("utf-8");
    }

    // /**
    // * レスポンスボディを文字列で取得.
    // * @param enc 文字コード
    // * @return ボディテキスト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to the response body in string format.
     * @param enc Character code
     * @return Body text
     * @throws DaoException Exception thrown
     */
    public final String bodyAsString(final String enc) throws DaoException {
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader reader = null;
        try {
            is = this.getResponseBodyInputStream(response);
            if (is == null) {
                return "";
            }
            isr = new InputStreamReader(is, enc);
            reader = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            int chr;
            while ((chr = reader.read()) != -1) {
                sb.append((char) chr);
            }
            return sb.toString();
        } catch (IOException e) {
            throw DaoException.create("io exception", 0);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (isr != null) {
                    isr.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (Exception e) {
                throw DaoException.create("io exception", 0);
            } finally {
                try {
                    if (isr != null) {
                        isr.close();
                    }
                    if (reader != null) {
                        reader.close();
                    }
                } catch (Exception e2) {
                    throw DaoException.create("io exception", 0);
                } finally {
                    try {
                        if (reader != null) {
                            reader.close();
                        }
                    } catch (Exception e3) {
                        throw DaoException.create("io exception", 0);
                    }
                }
            }
        }
    }

    // /**
    // * レスポンスボディをJSONで取得.
    // * @return JSONオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to the response body in JSON format.
     * @return JSONObject
     * @throws DaoException Exception thrown
     */
    public JSONObject bodyAsJson() throws DaoException {
        String res = bodyAsString();
        try {
            return (JSONObject) new JSONParser().parse(res);
        } catch (ParseException e) {
            throw DaoException.create("parse exception: " + e.getMessage(), 0);
        }
    }

    // /**
    // * レスポンスボディをXMLで取得.
    // * @return XML DOMオブジェクト
    // */
    /**
     * This method is used to the response body in XML format.
     * @return XML DOM Object
     */
    public final Document bodyAsXml() {
        String str = "";
        try {
            str = bodyAsString();
        } catch (DaoException e1) {
            throw new RuntimeException(e1);
        }
        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = null;
        InputStream is = new ByteArrayInputStream(str.getBytes());
        try {
            document = builder.parse(is);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return document;
    }

    // /**
    // * レスポンスボディのストリームを受け取る.
    // * @param res Responseオブジェクト
    // * @return ストリーム
    // * @throws IOException IO例外
    // */
    /**
     * This method is used to receive a stream of response body.
     * @param res Response object
     * @return Stream
     * @throws IOException Exception thrown
     */
    protected final InputStream getResponseBodyInputStream(final HttpResponse res) throws IOException {
        // GZip 圧縮されていたら解凍する。
        /** thaw if it is GZip compression. */
        Header[] contentEncodingHeaders = res.getHeaders("Content-Encoding");
        if (contentEncodingHeaders.length > 0 && "gzip".equalsIgnoreCase(contentEncodingHeaders[0].getValue())) {
            return new GZIPInputStream(res.getEntity().getContent());
        } else {
            HttpEntity he = res.getEntity();
            if (he != null) {
                return he.getContent();
            } else {
                return null;
            }
        }
    }

    // /**
    // * デバッグ用.
    // * @param res デバッグ出力するResponseオブジェクト
    // */
    /**
     * This method is used for debugging purpose.
     * @param res Response object with debugging output
     */
    private void debugHttpResponse(HttpResponse res) {
        if (res != null) {
            log.debug("【Response】 ResponseCode: " + res.getStatusLine().getStatusCode());
            Header[] headers = res.getAllHeaders();
            for (int i = 0; i < headers.length; i++) {
                log.debug("ResponseHeader[" + headers[i].getName() + "] : " + headers[i].getValue());
            }
        }
    }
}
