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
package com.fujitsu.dc.engine.jsgi;

import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.engine.adapter.DcRequestBodyStream;


/**
 * JSGIのリクエストオブジェクト. http://wiki.commonjs.org/wiki/JSGI/Level0/A/Draft2
 */
public final class JSGIRequest {
    private static Logger log = LoggerFactory.getLogger(JSGIRequest.class);

    // JSGIバージョン情報
    static final int JSGI_VERSION_MAJOR = 0;
    static final int JSGI_VERSION_MINOR = 3;

    HttpServletRequest req;
    DcRequestBodyStream input;

    /**
     * コンストラクタ.
     * @param req ServletRequest
     * @param input リクエストボディ
     */
    public JSGIRequest(HttpServletRequest req, DcRequestBodyStream input) {
        this.req = req;
        this.input = input;
    }

    /**
     * サーブレットのリクエストからJavaScriptのJSGIオブジェクトを生成する.
     * @return JavaScript用JSGIオブジェクト
     */
    public NativeObject getRequestObject() {
        NativeObject request = new NativeObject();
        request.put("method", request, this.req.getMethod());
        request.put("host", request, this.req.getAttribute("host").toString());
        request.put("port", request, this.req.getAttribute("port").toString());
        request.put("scriptName", request, this.req.getAttribute("scriptName").toString());
        request.put("pathInfo", request, this.req.getPathInfo());
        // サーバ動作時のPOSTでservlet.getQueryStringでクエリが取れないために以下の取り方で実現。
        String[] urlItems = this.req.getAttribute("env.requestUri").toString().split("\\?");
        String queryString = "";
        if (urlItems.length > 1) {
            queryString = urlItems[1];
        }
        request.put("queryString", request, queryString);
        request.put("scheme", request, this.req.getAttribute("scheme").toString());

        request.put("headers", request, this.headers());

        request.put("env", request, this.env());

        request.put("jsgi", request, this.makeJSGI());

        request.put("input", request, this.input);

        dump(request);
        return request;
    }

    private static void dump(NativeObject request) {
        String format = "[%s]-[%s]";
        log.debug("-jsgi-request-object-dump-start-");
        log.debug(String.format(format, "method", request.get("method", request)));
        log.debug(String.format(format, "scriptName", request.get("scriptName", request)));
        log.debug(String.format(format, "pathInfo", request.get("pathInfo", request)));
        log.debug(String.format(format, "queryString", request.get("queryString", request)));
        log.debug(String.format(format, "host", request.get("host", request)));
        log.debug(String.format(format, "port", request.get("port", request)));
        log.debug(String.format(format, "scheme", request.get("scheme", request)));

        log.debug(request.get("headers", request).toString());
        log.debug(request.get("jsgi", request).toString());
        log.debug(request.get("env", request).toString());
        log.debug("-jsgi-request-object-dump-finish-");
    }

    private NativeObject headers() {
        NativeObject headers = new NativeObject();

        Enumeration<String> headernames = this.req.getHeaderNames();
        while (headernames.hasMoreElements()) {
            String headerKey = headernames.nextElement();
            // ホストヘッダはEngineのローカルのホスト名になっているので、サーブレットリクエストから本来のホスト名を取得
            if (headerKey.equalsIgnoreCase("host")) {
                headers.put(headerKey.toLowerCase(), headers, req.getAttribute("HostHeader"));
            } else {
                headers.put(headerKey.toLowerCase(), headers, this.req.getHeader(headerKey));
            }
        }
        return headers;
    }

    private NativeObject env() {
        NativeObject env = new NativeObject();
        env.put("requestUri", env, req.getAttribute("env.requestUri").toString());
        return env;
    }

    private NativeObject makeJSGI() {
        NativeObject jsgi = new NativeObject();
        NativeArray version = new NativeArray(new Object[]{JSGI_VERSION_MAJOR, JSGI_VERSION_MINOR});
        jsgi.put("version", jsgi, version);
        jsgi.put("errors", jsgi, "");
        jsgi.put("multithread", jsgi, "");
        jsgi.put("multiprocess", jsgi, "");
        jsgi.put("run_once", jsgi, "");
        jsgi.put("cgi", jsgi, "");
        jsgi.put("ext", jsgi, "");
        return jsgi;
    }
}
