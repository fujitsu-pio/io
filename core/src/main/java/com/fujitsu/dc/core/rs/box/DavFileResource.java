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
package com.fujitsu.dc.core.rs.box;

import java.io.InputStream;
import java.io.Reader;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.PUT;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.wink.webdav.WebDAVMethod;

import com.fujitsu.dc.common.utils.DcCoreUtils;
import com.fujitsu.dc.core.annotations.ACL;
import com.fujitsu.dc.core.annotations.RequirePrivilege;
import com.fujitsu.dc.core.auth.BoxPrivilege;
import com.fujitsu.dc.core.model.DavCmp;
import com.fujitsu.dc.core.model.DavMoveResource;
import com.fujitsu.dc.core.model.DavRsCmp;

/**
 * プレーンなWebDAVのファイルリソースに対応するJAX-RS Resource クラス.
 */
public class DavFileResource {

    DavRsCmp davRsCmp;

    /**
     * コンストラクタ.
     * @param parent 親
     * @param davCmp 部品
     */
    public DavFileResource(final DavRsCmp parent, final DavCmp davCmp) {
        this.davRsCmp = new DavRsCmp(parent, davCmp);
    }

    /**
     * PUT メソッドを処理し、ファイルを更新します.
     * @param contentType Content-Typeヘッダ
     * @param ifMatch If-Matchヘッダ
     * @param inputStream リクエストボディ
     * @return JAX-RS応答オブジェクト
     */
    @PUT
    public Response put(@HeaderParam(HttpHeaders.CONTENT_TYPE) final String contentType,
            @HeaderParam(HttpHeaders.IF_MATCH) final String ifMatch,
            final InputStream inputStream) {

        // アクセス制御
        this.davRsCmp.checkAccessContext(this.davRsCmp.getAccessContext(), BoxPrivilege.WRITE);

        // If None Matchがあれば、それを使う 。なければ使わない
        ResponseBuilder rb = this.davRsCmp.getDavCmp().putForUpdate(contentType, inputStream, ifMatch);
        return rb.build();
    }

    /**
     * GETメソッドを処理します. ファイルを取得します.
     * @param ifNoneMatch If-None-Matchヘッダ
     * @param rangeHeaderField Rangeヘッダ
     * @return JAX-RS応答オブジェクト
     */
    @GET
    @RequirePrivilege("readContent")
    public Response get(
            @HeaderParam(HttpHeaders.IF_NONE_MATCH) final String ifNoneMatch,
            @HeaderParam("Range") final String rangeHeaderField
            ) {

        // アクセス制御
        this.davRsCmp.checkAccessContext(this.davRsCmp.getAccessContext(), BoxPrivilege.READ);

        ResponseBuilder rb = this.davRsCmp.getDavCmp().get(ifNoneMatch, rangeHeaderField);
        return rb.build();
    }

    /**
     * DELETEメソッドを処理します. このリソースを削除します.
     * @param ifMatch If-Match ヘッダ
     * @return JAX-RS応答オブジェクト
     */
    @DELETE
    public Response delete(@HeaderParam(HttpHeaders.IF_MATCH) final String ifMatch) {

        // アクセス制御
        // DavFileResourceは必ず親(最上位はBox)を持つため、this.davRsCmp.getParent()の結果がnullになることはない
        this.davRsCmp.getParent().checkAccessContext(this.davRsCmp.getAccessContext(), BoxPrivilege.WRITE);

        ResponseBuilder rb = this.davRsCmp.getDavCmp().delete(ifMatch);
        return rb.build();
    }

    /**
     * PROPPATCHの処理.
     * @param requestBodyXml リクエストボディ
     * @return JAX-RS応答オブジェクト
     */
    @WebDAVMethod.PROPPATCH
    public Response proppatch(final Reader requestBodyXml) {
        // アクセス制御
        this.davRsCmp.checkAccessContext(
                this.davRsCmp.getAccessContext(), BoxPrivilege.WRITE_PROPERTIES);
        return this.davRsCmp.doProppatch(requestBodyXml);
    }

    /**
     * PROPFINDの処理.
     * @param requestBodyXml リクエストボディ
     * @param depth Depth Header
     * @param contentLength Content-Length Header
     * @param transferEncoding Transfer-Encoding Header
     * @return JAX-RS応答オブジェクト
     */
    @WebDAVMethod.PROPFIND
    public Response propfind(final Reader requestBodyXml,
            @HeaderParam(DcCoreUtils.HttpHeaders.DEPTH) final String depth,
            @HeaderParam(HttpHeaders.CONTENT_LENGTH) final Long contentLength,
            @HeaderParam("Transfer-Encoding") final String transferEncoding) {

        return this.davRsCmp.doPropfind(requestBodyXml, depth, contentLength, transferEncoding,
                BoxPrivilege.READ_PROPERTIES, BoxPrivilege.READ_ACL);
    }

    /**
     * ACLメソッドの処理. ACLの設定を行う.
     * @param reader 設定XML
     * @return JAX-RS Response
     */
    @ACL
    public Response acl(final Reader reader) {

        // アクセス制御
        this.davRsCmp.checkAccessContext(this.davRsCmp.getAccessContext(), BoxPrivilege.WRITE_ACL);
        return this.davRsCmp.doAcl(reader);
    }

    /**
     * MOVEメソッドの処理.
     * @param headers ヘッダ情報
     * @return JAX-RS応答オブジェクト
     */
    @WebDAVMethod.MOVE
    public Response move(
            @Context HttpHeaders headers) {
        // 移動元に対するアクセス制御
        // DavFileResourceは必ず親(最上位はBox)を持つため、this.davRsCmp.getParent()の結果がnullになることはない
        this.davRsCmp.getParent().checkAccessContext(this.davRsCmp.getAccessContext(), BoxPrivilege.WRITE);
        return new DavMoveResource(this.davRsCmp.getParent(), this.davRsCmp.getDavCmp(), headers).doMove();
    }

    /**
     * OPTIONSメソッドの処理.
     * @return JAX-RS応答オブジェクト
     */
    @OPTIONS
    public Response options() {
        // 移動元に対するアクセス制御
        this.davRsCmp.checkAccessContext(this.davRsCmp.getAccessContext(), BoxPrivilege.READ);
        return DcCoreUtils.responseBuilderForOptions(
                HttpMethod.GET,
                HttpMethod.PUT,
                HttpMethod.DELETE,
                com.fujitsu.dc.common.utils.DcCoreUtils.HttpMethod.MOVE,
                com.fujitsu.dc.common.utils.DcCoreUtils.HttpMethod.PROPFIND,
                com.fujitsu.dc.common.utils.DcCoreUtils.HttpMethod.PROPPATCH,
                com.fujitsu.dc.common.utils.DcCoreUtils.HttpMethod.ACL
                ).build();
    }
}
