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
package com.fujitsu.dc.core.rs.cell;

import java.text.MessageFormat;

import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.utils.DcCoreUtils;
import com.fujitsu.dc.core.DcCoreMessageUtils;
import com.fujitsu.dc.core.auth.OAuth2Helper.Key;

/**
 * HTMLによるエラーレスポンス処理を司るJAX-RSリソース.
 */
public class ErrorHtmlResource {

    /**
     * ログ.
     */
    static Logger log = LoggerFactory.getLogger(ErrorHtmlResource.class);

    /**
     * コンストラクタ.
     */
    public ErrorHtmlResource() {
    }

    /**
     * ImplicitFlow認証エラーのエンドポイント.
     * @param host Hostヘッダ
     * @param code クエリパラメタ
     * @param uriInfo コンテキスト
     * @return JAX-RS Response Object
     */
    @GET
    public final Response implicitError(@HeaderParam(HttpHeaders.HOST) final String host,
            @QueryParam(Key.CODE) final String code,
            @Context final UriInfo uriInfo) {

        // エラーHTMLの返却
        return returnError(code);
    }

    /**
     * ImplicitFlowパスワード認証フォーム.
     * @param code メッセージコード
     * @return
     */
    private String createFrom(String code) {
        String params0 = DcCoreMessageUtils.getMessage("PS-ER-0001");
        String params1 = DcCoreMessageUtils.getMessage("PS-ER-0001");
        String params2 = DcCoreMessageUtils.getMessage("PS-ER-0002");
        String params3 = null;
        try {
            params3 = DcCoreMessageUtils.getMessage(code);
        } catch (Exception e) {
            log.info(e.getMessage());
            params3 = DcCoreMessageUtils.getMessage("PS-ER-0003");
        }

        String html = DcCoreUtils.readStringResource("html/error.html", CharEncoding.UTF_8);
        html = MessageFormat.format(html, params0, params1, params2, params3);
        return html;
    }

    /**
     * ImplicitFlow_cookieでの認証時のエラー処理.
     * @param code メッセージコード
     * @return
     */
    private Response returnError(String code) {
        ResponseBuilder rb = Response.ok().type(MediaType.TEXT_HTML);
        return rb.entity(this.createFrom(code))
                .header("Content-Type", "text/html; charset=UTF-8").build();
    }

}
