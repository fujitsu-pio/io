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
package com.fujitsu.dc.core.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Reader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;

import org.apache.http.HttpStatus;
import org.apache.wink.webdav.model.Multistatus;
import org.apache.wink.webdav.model.Propertyupdate;
import org.apache.wink.webdav.model.Propfind;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.utils.DcCoreUtils;
import com.fujitsu.dc.core.DcCoreException;
import com.fujitsu.dc.core.auth.AccessContext;
import com.fujitsu.dc.core.auth.BoxPrivilege;
import com.fujitsu.dc.core.auth.OAuth2Helper;
import com.fujitsu.dc.core.auth.Privilege;
import com.fujitsu.dc.core.rs.box.DavCollectionResource;
import com.fujitsu.dc.core.rs.box.DavFileResource;
import com.fujitsu.dc.core.rs.box.DcEngineSvcCollectionResource;
import com.fujitsu.dc.core.rs.box.NullResource;
import com.fujitsu.dc.core.rs.box.ODataSvcCollectionResource;
import com.fujitsu.dc.core.utils.ResourceUtils;

/**
 * JaxRS Resource オブジェクトから処理の委譲を受けてDav関連の永続化を除く処理を行うクラス.
 */
public class DavRsCmp {
    /**
     * ログ用オブジェクト.
     */
    private static Logger log = LoggerFactory.getLogger(DavRsCmp.class);

    DavCmp davCmp;
    DavRsCmp parent;
    String pathName;

    /**
     * コンストラクタ.
     * @param parent 親リソース
     * @param davCmp バックエンド実装に依存する処理を受け持つ部品
     */
    public DavRsCmp(final DavRsCmp parent, final DavCmp davCmp) {
        this.parent = parent;
        this.davCmp = davCmp;
        if (this.davCmp != null) {
            this.pathName = this.davCmp.getName();
        }
    }

    /**
     * 現在のリソースの一つ下位パスを担当するJax-RSリソースを返す.
     * @param nextPath 一つ下のパス名
     * @param request リクエスト
     * @return 下位パスを担当するJax-RSリソースオブジェクト
     */
    public Object nextPath(final String nextPath, final HttpServletRequest request) {

        // nextPathを確認し、タイプをしらべて、new して返す
        if (this.davCmp == null) {
            return new NullResource(this, null, true);
        }
        DavCmp nextCmp = this.davCmp.getChild(nextPath);
        String type = nextCmp.getType();

        if (DavCmp.TYPE_NULL.equals(type)) {
            // 現在リソースを判断する
            if (DavCmp.TYPE_NULL.equals(this.davCmp.getType())) {
                // 現在リソースが存在しないパスの場合、次リソースから見て親リソースはNullResorce
                return new NullResource(this, nextCmp, true);
            } else {
                return new NullResource(this, nextCmp, false);
            }
        } else if (DavCmp.TYPE_COL_WEBDAV.equals(type)) {
            return new DavCollectionResource(this, nextCmp);
        } else if (DavCmp.TYPE_DAV_FILE.equals(type)) {
            return new DavFileResource(this, nextCmp);
        } else if (DavCmp.TYPE_COL_ODATA.equals(type)) {
            return new ODataSvcCollectionResource(this, nextCmp);
        } else if (DavCmp.TYPE_COL_SVC.equals(type)) {
            return new DcEngineSvcCollectionResource(this, nextCmp);
        }

        return null;
    }

    /**
     * このリソースのURLを返します.
     * @return URL文字列
     */
    public String getUrl() {
        // 再帰的に最上位のBoxResourceまでいって、BoxResourceではここをオーバーライドしてルートURLを与えている。
        return this.parent.getUrl() + "/" + this.pathName;
    }

    /**
     * リソースが所属するCellを返す.
     * @return Cellオブジェクト
     */
    public Cell getCell() {
        // 再帰的に最上位のBoxResourceまでいって、そこからCellにたどりつくため、BoxResourceではここをオーバーライドしている。
        return this.parent.getCell();
    }

    /**
     * リソースが所属するBoxを返す.
     * @return Boxオブジェクト
     */
    public Box getBox() {
        // 再帰的に最上位のBoxResourceまでいって、そこからCellにたどりつくため、BoxResourceではここをオーバーライドしている。
        return this.parent.getBox();
    }

    /**
     * このリソースのdavCmpを返します.
     * @return davCmp
     */
    public DavCmp getDavCmp() {
        return this.davCmp;
    }

    /**
     * このリソースのparentを返します.
     * @return DavRsCmp
     */
    public DavRsCmp getParent() {
        return this.parent;
    }

    /**
     * @return AccessContext
     */
    public AccessContext getAccessContext() {
        return this.parent.getAccessContext();
    }

    /**
     * PROPFINDの処理. バックエンド実装に依らない共通的な振る舞い.
     * @param requestBodyXml requestBody
     * @param depth Depthヘッ ダ
     * @param contentLength Content-Lengthヘッダ
     * @param transferEncoding Transfer-Encodingヘッダ
     * @param requiredForPropfind PROPFIND実行に必要なPrivilege
     * @param requiredForReadAcl ACL読み出しに必要なPrivilege
     * @return Jax-RS 応答オブジェクト
     */
    public final Response doPropfind(final Reader requestBodyXml, final String depth,
            final Long contentLength, final String transferEncoding, final Privilege requiredForPropfind,
            final Privilege requiredForReadAcl) {

        // アクセス制御
        this.checkAccessContext(this.getAccessContext(), requiredForPropfind);

        // ユニットユーザもしくはACLのPrivilegeが設定せれている場合のみ、ACL設定の出力が可能
        boolean canAclRead = false;
        if (this.getAccessContext().isUnitUserToken()
                || this.hasPrivilege(this.getAccessContext(), requiredForReadAcl)) {
            canAclRead = true;
        }

        // リクエストをパースして pfオブジェクトを作成する
        Propfind propfind = null;
        if (ResourceUtils.hasApparentlyRequestBody(contentLength, transferEncoding)) {
            BufferedReader br = null;
            try {
                br = new BufferedReader(requestBodyXml);
                propfind = Propfind.unmarshal(br);
            } catch (Exception e1) {
                throw DcCoreException.Dav.XML_ERROR.reason(e1);
            }
        } else {
            log.debug("Content-Length 0");
        }

        // 実際の処理
        final Multistatus ms = this.davCmp.propfind(propfind, depth, this.getUrl(), canAclRead);

        // 処理結果を出力
        StreamingOutput str = new StreamingOutput() {
            @Override
            public void write(final OutputStream os) throws IOException {
                Multistatus.marshal(ms, os);
            }
        };
        return Response.status(HttpStatus.SC_MULTI_STATUS)
                .header(HttpHeaders.ETAG, this.davCmp.getEtag())
                .header("Content-Type", "application/xml")
                .entity(str).build();
    }

    /**
     * PROPPATCHの処理. 実サブクラスで必要に応じて呼び出すことを想定。 バックエンド実装に依らない共通的な振る舞い.
     * @param reqBodyXml requestBody
     * @return Jax-RS 応答オブジェクト
     */
    public final Response doProppatch(final Reader reqBodyXml) {

        // リクエストをパースして pu オブジェクトを作成する
        BufferedReader br = null;
        Propertyupdate pu = null;
        try {
            br = new BufferedReader(reqBodyXml);
            pu = Propertyupdate.unmarshal(br);
        } catch (Exception e1) {
            throw DcCoreException.Dav.XML_ERROR.reason(e1);
        }

        // 実際の処理
        final Multistatus ms = this.davCmp.proppatch(pu, this.getUrl());

        // 処理結果を出力
        StreamingOutput str = new StreamingOutput() {
            @Override
            public void write(final OutputStream os) throws IOException {
                Multistatus.marshal(ms, os);
            }
        };
        return Response.status(HttpStatus.SC_MULTI_STATUS)
                .header(HttpHeaders.ETAG, this.davCmp.getEtag())
                .header(HttpHeaders.CONTENT_TYPE, "application/xml")
                .entity(str).build();
    }

    /**
     * ACLメソッドの実処理. ACLの設定を行う. 実サブクラスで必要に応じて呼び出すことを想定。 バックエンド実装に依らない共通的な振る舞いをここに実装.
     * @param reader 設定XML
     * @return JAX-RS Response
     */
    public final Response doAcl(final Reader reader) {

        return this.davCmp.acl(reader).build();
    }

    /**
     * @return スキーマ認証レベル取得
     */
    public String getConfidentialLevel() {
        String confidentialStringTmp = null;
        if (this.davCmp == null) {
            confidentialStringTmp = this.parent.getConfidentialLevel();
        } else {
            confidentialStringTmp = this.davCmp.getConfidentialLevel();
        }

        if (confidentialStringTmp == null || "".equals(confidentialStringTmp)) {
            if (this.parent == null) {
                // BOXまで遡っても設定が存在しない場合はスキーマ認証は必要なしとみなす。
                return OAuth2Helper.SchemaLevel.NONE;
            }
            confidentialStringTmp = this.parent.getConfidentialLevel();
        }
        return confidentialStringTmp;
    }

    /**
     * 親のACL情報とマージし、アクセス可能か判断する.
     * @param ac アクセスコンテキスト
     * @param privilege ACLのプリビレッジ（readとかwrite）
     * @return boolean
     */
    public boolean hasPrivilege(AccessContext ac, Privilege privilege) {

        // davCmpが無い（存在しないリソースが指定された）場合はそのリソースのACLチェック飛ばす
        if (this.davCmp != null
                && this.getAccessContext().requirePrivilege(this.davCmp.getAcl(), privilege, this.getCell().getUrl())) {
            return true;
        }

        // 親の設定をチェックする。
        if (this.parent != null && this.parent.hasPrivilege(ac, privilege)) {
            return true;
        }

        return false;
    }

    /**
     * OPTIONSメソッド.
     * @return JAX-RS Response
     */
    @OPTIONS
    public Response options() {
        // アクセス制御
        this.checkAccessContext(this.getAccessContext(), BoxPrivilege.READ);

        return DcCoreUtils.responseBuilderForOptions(
                HttpMethod.GET,
                HttpMethod.PUT,
                HttpMethod.DELETE,
                com.fujitsu.dc.common.utils.DcCoreUtils.HttpMethod.MKCOL,
                com.fujitsu.dc.common.utils.DcCoreUtils.HttpMethod.PROPFIND,
                com.fujitsu.dc.common.utils.DcCoreUtils.HttpMethod.PROPPATCH,
                com.fujitsu.dc.common.utils.DcCoreUtils.HttpMethod.ACL
                ).build();
    }

    /**
     * アクセス制御を行う.
     * @param ac アクセスコンテキスト
     * @param privilege アクセス可能な権限
     */
    public void checkAccessContext(final AccessContext ac, Privilege privilege) {
        // ユニットユーザトークンチェック
        if (ac.isUnitUserToken()) {
            return;
        }

        // スキーマ認証チェック
        ac.checkSchemaAccess(this.getConfidentialLevel(), this.getBox());

        // アクセス権チェック
        if (!this.hasPrivilege(ac, privilege)) {
            // トークンの有効性チェック
            // トークンがINVALIDでもACL設定でPrivilegeがallに設定されているとアクセスを許可する必要があるのでこのタイミングでチェック
            if (AccessContext.TYPE_INVALID.equals(ac.getType())) {
                ac.throwInvalidTokenException();
            } else if (AccessContext.TYPE_ANONYMOUS.equals(ac.getType())) {
                throw DcCoreException.Auth.AUTHORIZATION_REQUIRED;
            }
            throw DcCoreException.Auth.NECESSARY_PRIVILEGE_LACKING;
        }
    }

    /**
     * ユニット昇格権限設定チェック.
     * @param account チェックするアカウント
     * @return 権限の有無
     */
    public boolean checkOwnerRepresentativeAccounts(final String account) {
        List<String> ownerRepresentativeAccountsSetting = this.davCmp.getOwnerRepresentativeAccounts();
        if (ownerRepresentativeAccountsSetting == null || account == null) {
            return false;
        }

        for (String ownerRepresentativeAccount : ownerRepresentativeAccountsSetting) {
            if (account.equals(ownerRepresentativeAccount)) {
                return true;
            }
        }
        return false;
    }
}
