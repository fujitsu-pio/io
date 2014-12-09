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
package com.fujitsu.dc.engine.rs;

import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.json.simple.JSONObject;

import com.fujitsu.dc.engine.DcEngineException;
import com.fujitsu.dc.engine.utils.DcEngineConfig;

/**
 * DC-Engineステータス用サーブレットクラス.
 */
@Path("__status")
public class StatusResource {
    /** ログオブジェクト. */
    private static Log log = LogFactory.getLog(AbstractService.class);

    /**
     * GETメソッドに対する処理.
     * @return JAS-RS Response
     */
    @SuppressWarnings("unchecked")
    @GET
    @Produces("application/json")
    public Response get() {
        StringBuilder sb = new StringBuilder();

        // プロパティ一覧
        Properties props = DcEngineConfig.getProperties();
        JSONObject responseJson = new JSONObject();
        JSONObject propertiesJson = new JSONObject();
        for (String key : props.stringPropertyNames()) {
            String value = props.getProperty(key);
            propertiesJson.put(key, value);
        }
        responseJson.put("properties", propertiesJson);

        sb.append(responseJson.toJSONString());
        return Response.status(HttpStatus.SC_OK).entity(sb.toString()).build();
    }

    /**
     * POSTメソッド.
     * @param path リソース名
     * @param request リクエストオブジェクト
     * @param response レスポンスオブジェクト
     * @param is リクエストストリームオブジェクト
     * @return Responseオブジェクト
     */
    @POST
    public final Response post(@PathParam("id") final String path,
            @Context final HttpServletRequest request,
            @Context final HttpServletResponse response,
            final InputStream is) {
        return run(path, request, response, is);
    }

    /**
     * Service実行.
     * @param path リソース名
     * @param req Requestオブジェクト
     * @param res Responseオブジェクト
     * @param is リクエストストリームオブジェクト
     * @return Response
     */
    public final Response run(final String path,
            final HttpServletRequest req,
            final HttpServletResponse res,
            final InputStream is) {
        StringBuilder msg = new StringBuilder();
        msg.append(">>> Request Started ");
        msg.append(" method:");
        msg.append(req.getMethod());
        msg.append(" method:");
        msg.append(req.getRequestURL());
        msg.append(" url:");
        log.info(msg);

        // デバッグ用 すべてのヘッダをログ出力
        Enumeration<String> multiheaders = req.getHeaderNames();
        for (String headerName : Collections.list(multiheaders)) {
            Enumeration<String> headers = req.getHeaders(headerName);
            for (String header : Collections.list(headers)) {
                log.debug("RequestHeader['" + headerName + "'] = " + header);
            }
        }
        try {
            DcEngineConfig.reload();
        } catch (Exception e) {
            log.warn(" unknown Exception(" + e.getMessage() + ")");
            return errorResponse(new DcEngineException("500 Internal Server Error (Unknown Error)",
                    DcEngineException.STATUSCODE_SERVER_ERROR));
        }
        return Response.status(HttpStatus.SC_NO_CONTENT).build();
    }

    /**
     * エラーレスポンス生成.
     * @param e Exceptionオブジェクト
     * @return Response
     */
    final Response errorResponse(final DcEngineException e) {
        return makeErrorResponse(e.getMessage(), e.getStatusCode());
    }

    /**
     * エラー時のレスポンスを生成.
     * @param msg メッセージ本文
     * @param code ステータスコード
     * @return Responseオブジェクト
     */
    private Response makeErrorResponse(final String msg, final int code) {
        return Response.status(code).entity(msg).build();
    }
}
