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
package com.fujitsu.dc.core.auth;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.UriInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token;
import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.TokenDsigException;
import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.TokenParseException;
import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.TokenRootCrtException;
import com.fujitsu.dc.common.auth.token.AccountAccessToken;
import com.fujitsu.dc.common.auth.token.CellLocalAccessToken;
import com.fujitsu.dc.common.auth.token.IAccessToken;
import com.fujitsu.dc.common.auth.token.LocalToken;
import com.fujitsu.dc.common.auth.token.Role;
import com.fujitsu.dc.common.auth.token.TransCellAccessToken;
import com.fujitsu.dc.common.auth.token.TransCellRefreshToken;
import com.fujitsu.dc.common.auth.token.UnitLocalUnitUserToken;
import com.fujitsu.dc.core.DcCoreConfig;
import com.fujitsu.dc.core.DcCoreException;
import com.fujitsu.dc.core.DcCoreLog;
import com.fujitsu.dc.core.model.Box;
import com.fujitsu.dc.core.model.Cell;
import com.fujitsu.dc.core.model.jaxb.Ace;
import com.fujitsu.dc.core.model.jaxb.Acl;

/**
 * アクセス文脈情報.
 * Authorization ヘッダから取り出した情報に基づいて 誰がどいう役割で、どういうアプリからアクセスしているのかといったAccess文脈情報を 生成し、これをこのオブジェクトに保持する。
 * ACLとの突合でpermissionを生成する。 cell, id, pw → AC AC → Token(issuer, subj, roles) Token → AC AC + ACL → permissions
 */
public final class AccessContext {
    private Cell cell;
    private String accessType;
    private String subject;
    private String issuer;
    private String schema;
    private String confidentialLevel;
    private List<Role> roles = new ArrayList<Role>();
    private String baseUri;
    private InvalidReason invalidReason;

    /**
     * ログ.
     */
    static Logger log = LoggerFactory.getLogger(AccessContext.class);

    /**
     * 匿名アクセス. Authorization ヘッダなしでのアクセスです.
     */
    public static final String TYPE_ANONYMOUS = "anon";
    /**
     * 無効な権限でのアクセス. Authorization ヘッダがあったものの、認証されなかったアクセス.
     */
    public static final String TYPE_INVALID = "invalid";
    /**
     * マスタートークンでのアクセス. Authorization ヘッダ内容がマスタトークンであるアクセス.
     */
    public static final String TYPE_UNIT_MASTER = "unit-master";
    /**
     * Basic認証によるアクセス.
     */
    public static final String TYPE_BASIC = "basic";
    /**
     * Cell Local Accessトークンによるアクセス.
     */
    public static final String TYPE_LOCAL = "local";
    /**
     * TransCell Accessトークンによるアクセス.
     */
    public static final String TYPE_TRANS = "trans";
    /**
     * Unit User Access トークンによるアクセス.
     */
    public static final String TYPE_UNIT_USER = "unit-user";
    /**
     * Unit Local Unit User トークンによるアクセス.
     */
    public static final String TYPE_UNIT_LOCAL = "unit-local";
    /**
     * Unit Admin Role ユニット管理者ロール.
     */
    public static final String TYPE_UNIT_ADMIN_ROLE = "unitAdmin";

    // TODO V1.1 Basic認証対応時に解除
    // private static final String AUTHZ_BASIC = "Basic ";

    /**
     * 無効なトークンの原因.
     */
    enum InvalidReason {
        /**
         * 有効期限切れ.
         */
        expired,
        /**
         * Authentication Schemeが無効.
         */
        authenticationScheme,
        /**
         * ベーシック認証ヘッダのフォーマットが無効.
         */
        basicAuthFormat,
        /**
         * 認証エラー.
         */
        authError,
        /**
         * トークンパースエラー.
         */
        tokenParseError,
        /**
         * トークン署名エラー.
         */
        tokenDsigError,
        /**
         * リフレッシュトークンでのアクセス.
         */
        refreshToken,
    }

    private AccessContext(final String type, final Cell cell, final String baseUri, final InvalidReason invalidReason) {
        this.accessType = type;
        this.cell = cell;
        this.baseUri = baseUri;
        this.invalidReason = invalidReason;
    }

    private AccessContext(final String type, final Cell cell, final String baseUri) {
        this(type, cell, baseUri, null);
    }

    /**
     * @return the cell
     */
    public Cell getCell() {
        return this.cell;
    }

    /**
     * アクセスタイプを表す文字列を返します.
     * @return アクセスタイプを表す文字列
     */
    public String getType() {
        return this.accessType;
    }

    /**
     * SUBJECT文字列を返します.
     * @return SUBJECT文字列
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * ISSUER文字列を返します.
     * @return ISSUER文字列
     */
    public String getIssuer() {
        return this.issuer;
    }

    /**
     * SCHEMA URL文字列を返します. クライアント(SCHEMA)認証されたときだけ（OAuth2認証のときだけ）この値が入ります。
     * @return SCHEMA URL文字列
     */
    public String getSchema() {
        return this.schema;
    }

    /**
     * スキーマ認証のレベルを返す.
     * @return スキーマ認証レベル
     */
    public String getConfidentialLevel() {
        return this.confidentialLevel;
    }

    void addRole(final Role role) {
        this.roles.add(role);
    }

    /**
     * BaseUriを返す.
     * @return baseUri
     */
    public String getBaseUri() {
        return baseUri;
    }

    /**
     * ロールリストを返します.
     * @return ロールリスト
     */
    public List<Role> getRoleList() {
        return this.roles;
    }

    /*
     * public static AccessContext authenticate(Cell cell, String username, String password) { return null; } public
     * static AccessContext create(HttpServletRequest request, Cell cell) { return
     * create(request.getHeader("Authorization"), cell); }
     */

    /**
     * ファクトリメソッド. アクセスしているCellとAuthorizationヘッダの値を元にオブジェクトを生成して返します.
     * @param authzHeaderValue Authorizationヘッダの値
     * @param requestURIInfo リクエストのURI情報
     * @param dcCookiePeer リクエストパラメタに指定された dc_cookie_peerの値
     * @param dcCookieAuthValue クッキー内 dc_cookieに指定されている値
     * @param cell アクセスしているCell
     * @param baseUri アクセスしているbaseUri
     * @param host リクエストヘッダのHostの値
     * @param xDcUnitUser X-Dc-UnitUserヘッダ
     * @return 生成されたAccessContextオブジェクト
     */
    public static AccessContext create(final String authzHeaderValue,
                final UriInfo requestURIInfo, final String dcCookiePeer, final String dcCookieAuthValue,
                final Cell cell, final String baseUri, final String host, String xDcUnitUser) {
        if (authzHeaderValue == null) {
            if (dcCookiePeer == null  || 0 == dcCookiePeer.length()) {
                return new AccessContext(TYPE_ANONYMOUS, cell, baseUri);
            }
            // クッキー認証の場合
            // クッキー内の値を復号化した値を取得
            try {
                if (null == dcCookieAuthValue) {
                    throw DcCoreException.Auth.COOKIE_AUTHENTICATION_FAILED;
                }
                String decodedCookieValue = LocalToken.decode(dcCookieAuthValue,
                        UnitLocalUnitUserToken.getIvBytes(
                                AccessContext.getCookieCryptKey(requestURIInfo.getBaseUri())));
                int separatorIndex = decodedCookieValue.indexOf("\t");
                String peer = decodedCookieValue.substring(0, separatorIndex);
                // クッキー内の情報から authorizationHeader相当のトークンを取得
                String authToken = decodedCookieValue.substring(separatorIndex + 1);
                if (dcCookiePeer.equals(peer)) {
                    // 再帰呼び出しで適切な AccessContextを生成する。
                    return create(OAuth2Helper.Scheme.BEARER + " " + authToken,
                            requestURIInfo, null, null, cell, baseUri, host, xDcUnitUser);
                } else {
                    throw DcCoreException.Auth.COOKIE_AUTHENTICATION_FAILED;
                }
            } catch (TokenParseException e) {
                throw DcCoreException.Auth.COOKIE_AUTHENTICATION_FAILED;
            }
        }

        // TODO V1.1 ここはキャッシュできる部分。ここでキャッシュから取得すればいい。

        // まずは認証方式によって分岐
        // TODO V1.1 Basic認証であるとき。
        // if (authzHeaderValue.startsWith(AUTHZ_BASIC)) {
        // return AccessContext.createBasicAuthz(authzHeaderValue, cell, baseUri);
        // } else if (authzHeaderValue.startsWith(OAuth2Helper.Scheme.BEARER)) {
        // return createBearerAuthz(authzHeaderValue, cell, baseUri, host, xDcUnitUser);
        // }
        if (authzHeaderValue.startsWith(OAuth2Helper.Scheme.BEARER)) {
            return createBearerAuthz(authzHeaderValue, cell, baseUri, host, xDcUnitUser);
        }
        return new AccessContext(TYPE_INVALID, cell, baseUri, InvalidReason.authenticationScheme);
    }

    // TODO V1.1 Basic認証対応時に解除
    // /**
    // * ファクトリメソッド. アクセスしているCellとAuthorizationヘッダの値を元にBasic認証にてオブジェクトを生成して返します.
    // * @param authzHeaderValue Authorizationヘッダの値
    // * @param cell アクセスしているCell
    // * @param baseUri アクセスしているbaseUri
    // * @return 生成されたAccessContextオブジェクト
    // */
    // private static AccessContext createBasicAuthz(final String authzHeaderValue, final Cell cell,
    // final String baseUri) {
    // String[] idpw = DcCoreUtils.parseBasicAuthzHeader(authzHeaderValue);
    // if (idpw == null) {
    // return new AccessContext(TYPE_INVALID, cell, baseUri, InvalidReason.basicAuthFormat);
    // }
    //
    // String username = idpw[0];
    // String password = idpw[1];
    // boolean authnSuccess = cell.authenticateAccount(username, password);
    // if (!authnSuccess) {
    // return new AccessContext(TYPE_INVALID, cell, baseUri, InvalidReason.authError);
    // }
    // // 認証して成功なら
    // AccessContext ret = new AccessContext(TYPE_BASIC, cell, baseUri);
    // ret.subject = username;
    // // ロール情報を取得
    // ret.roles = cell.getRoleListForAccount(username);
    // return ret;
    // }

    /**
     * ファクトリメソッド. アクセスしているCellとAuthorizationヘッダの値を元にBearer認証にてオブジェクトを生成して返します.
     * @param authzHeaderValue Authorizationヘッダの値
     * @param cell アクセスしているCell
     * @param baseUri アクセスしているbaseUri
     * @param xDcUnitUser X-DC-UnitUserヘッダ
     * @return 生成されたAccessContextオブジェクト
     */
    private static AccessContext createBearerAuthz(final String authzHeaderValue, final Cell cell,
            final String baseUri, final String host, String xDcUnitUser) {
        // Bearer
        // 認証トークンの値が[Bearer ]で開始していなければ不正なトークンと判断する
        if (!authzHeaderValue.startsWith(OAuth2Helper.Scheme.BEARER_CREDENTIALS_PREFIX)) {
            return new AccessContext(TYPE_INVALID, cell, baseUri, InvalidReason.tokenParseError);
        }
        String accessToken = authzHeaderValue.substring(OAuth2Helper.Scheme.BEARER.length() + 1);
        // マスタートークンの検出
        // マスタートークン指定で、X-Dc-UnitUserヘッダがなかった場合はマスタートークン扱い
        if (DcCoreConfig.getMasterToken().equals(accessToken) && xDcUnitUser == null) {
            AccessContext ret = new AccessContext(TYPE_UNIT_MASTER, cell, baseUri);
            return ret;
        } else if (DcCoreConfig.getMasterToken().equals(accessToken) && xDcUnitUser != null) {
            // X-Dc-UnitUserヘッダ指定だとマスターからユニットユーザトークンへの降格
            AccessContext ret = new AccessContext(TYPE_UNIT_USER, cell, baseUri);
            ret.subject = xDcUnitUser;
            return ret;
        }
        // 以降、Cellレベル。
        AbstractOAuth2Token tk = null;
        try {
            String issuer = null;
            if (cell != null) {
                issuer = cell.getUrl();
            }
            tk = AbstractOAuth2Token.parse(accessToken, issuer, host);
        } catch (TokenParseException e) {
            // パースに失敗したので
            DcCoreLog.Auth.TOKEN_PARSE_ERROR.params(e.getMessage()).writeLog();
            return new AccessContext(TYPE_INVALID, cell, baseUri, InvalidReason.tokenParseError);
        } catch (TokenDsigException e) {
            // 証明書検証に失敗したので
            DcCoreLog.Auth.TOKEN_DISG_ERROR.params(e.getMessage()).writeLog();
            return new AccessContext(TYPE_INVALID, cell, baseUri, InvalidReason.tokenDsigError);
        } catch (TokenRootCrtException e) {
            // ルートCA証明書の設定エラー
            DcCoreLog.Auth.ROOT_CA_CRT_SETTING_ERROR.params(e.getMessage()).writeLog();
            throw DcCoreException.Auth.ROOT_CA_CRT_SETTING_ERROR;
        }
        log.debug(tk.getClass().getCanonicalName());
        // AccessTokenではない場合、すなわちリフレッシュトークン。
        if (!(tk instanceof IAccessToken) || tk instanceof TransCellRefreshToken) {
            // リフレッシュトークンでのアクセスは認めない。
            return new AccessContext(TYPE_INVALID, cell, baseUri, InvalidReason.refreshToken);
        }

        // トークンの有効期限チェック
        if (tk.isExpired()) {
            return new AccessContext(TYPE_INVALID, cell, baseUri, InvalidReason.expired);
        }

        AccessContext ret = new AccessContext(null, cell, baseUri);
        if (tk instanceof AccountAccessToken) {
            ret.accessType = TYPE_LOCAL;
            // ロール情報をとってくる。
            String acct = tk.getSubject();
            ret.roles = cell.getRoleListForAccount(acct);
            if (ret.roles == null) {
                throw DcCoreException.Auth.AUTHORIZATION_REQUIRED;
            }
            // AccessContextではSubjectはURLに正規化。
            ret.subject = cell.getUrl() + "#" + tk.getSubject();
            ret.issuer = tk.getIssuer();
        } else if (tk instanceof CellLocalAccessToken) {
            CellLocalAccessToken clat = (CellLocalAccessToken) tk;
            ret.accessType = TYPE_LOCAL;
            // ロール情報を取得して詰める。
            ret.roles = clat.getRoles();
            ret.subject = tk.getSubject();
            ret.issuer = tk.getIssuer();
        } else if (tk instanceof UnitLocalUnitUserToken) {
            ret.accessType = TYPE_UNIT_LOCAL;
            ret.subject = tk.getSubject();
            ret.issuer = tk.getIssuer();
            // ユニットローカルユニットユーザトークンはスキーマ認証関係無いのでここで復帰
            return ret;
        } else {
            TransCellAccessToken tca = (TransCellAccessToken) tk;

            // TCATの場合はユニットユーザトークンである可能性をチェック
            // TCATがユニットユーザトークンである条件１：Targetが自分のユニットであること。
            // TCATがユニットユーザトークンである条件２：Issuerが設定に存在するUnitUserCellであること。
            if (tca.getTarget().equals(baseUri) && DcCoreConfig.checkUnitUserIssuers(tca.getIssuer())) {

                // ロール情報をとってきて、ユニットアドミンロールがついていた場合、ユニットアドミンに昇格させる。
                List<Role> roles = tca.getRoles();
                Role unitAdminRole = new Role(TYPE_UNIT_ADMIN_ROLE, Box.DEFAULT_BOX_NAME, null, tca.getIssuer());
                String unitAdminRoleUrl = unitAdminRole.createUrl();
                for (Role role : roles) {
                    if (role.createUrl().equals(unitAdminRoleUrl)) {
                        return new AccessContext(TYPE_UNIT_MASTER, cell, baseUri);
                    }
                }

                // ユニットユーザトークンの処理
                ret.accessType = TYPE_UNIT_USER;
                ret.subject = tca.getSubject();
                ret.issuer = tca.getIssuer();
                // ユニットユーザトークンはスキーマ認証関係無いのでここで復帰
                return ret;
            } else if (cell == null) {
                // ユニットレベルでCellが空のトークンを許すのはマスタートークンと、ユニットユーザトークンだけなので無効なトークン扱いにする。
                throw DcCoreException.Auth.UNITUSER_ACCESS_REQUIRED;
            } else {
                // TCATの処理
                ret.accessType = TYPE_TRANS;
                ret.subject = tca.getSubject();
                ret.issuer = tca.getIssuer();

                // トークンに対応するRoleの取得
                ret.roles = cell.getRoleListHere((TransCellAccessToken) tk);
            }
        }
        ret.schema = tk.getSchema();
        if (ret.schema == null || "".equals(ret.schema)) {
            ret.confidentialLevel = OAuth2Helper.SchemaLevel.NONE;
        } else if (ret.schema.endsWith(OAuth2Helper.Key.CONFIDENTIAL_MARKER)) {
            ret.confidentialLevel = OAuth2Helper.SchemaLevel.CONFIDENTIAL;
        } else {
            ret.confidentialLevel = OAuth2Helper.SchemaLevel.PUBLIC;
        }

        // TODO Cache Cell Level
        return ret;
    }

    /**
     * 親のACL情報とマージし、アクセス可能か判断する.
     * @param acl リソースに設定されているALC
     * @param resourcePrivilege リソースにアクセスするために必要なPrivilege
     * @param cellUrl セルURL
     * @return boolean
     */
    public boolean requirePrivilege(Acl acl, Privilege resourcePrivilege, String cellUrl) {
        // ACLが未設定だったらアクセス不可
        if (acl == null || acl.getAceList() == null) {
            return false;
        }
        // Privilegeが未定義だったらアクセス不可
        if (resourcePrivilege == null) {
            return false;
        }

        // ACLからROLE情報を取得し、権限を取得
        if (acl.getAceList() == null) {
            return false;
        }
        for (Ace ace : acl.getAceList()) {
            // 空のaceが設定されている場合はチェックの必要がないためがcontinueする
            if (ace.getGrantedPrivilegeList().size() == 0 && ace.getPrincipalHref() == null) {
                continue;
            }
            // Principalがallの場合、アクセス可
            if (ace.getPrincipalAll() != null) {
                if (requireAcePrivilege(ace.getGrantedPrivilegeList(), resourcePrivilege)) {
                    return true;
                }
                continue;
            }
            // Accountに紐付いたRoleが存在しない場合は、アクセス不可
            if (this.roles == null) {
                return false;
            }
            for (Role role : this.roles) {
                // 相対パスロールURL対応
                String principalHref = getPrincipalHrefUrl(acl.getBase(), ace.getPrincipalHref());
                if (principalHref == null) {
                    return false;
                }
                // ロールに対応している設定を検出
                if (role.localCreateUrl(cellUrl).equals(principalHref)
                        && requireAcePrivilege(ace.getGrantedPrivilegeList(), resourcePrivilege)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 必要な権限がACLのPrivilegeに設定されているかチェックする.
     * @param acePrivileges ACEに設定されたPrivilege設定のリスト
     * @param Privilege 必要な権限
     * @return チェック可否
     */
    private boolean requireAcePrivilege(List<String> acePrivileges, Privilege resourcePrivilege) {
        for (String aclPrivilege : acePrivileges) {
            Privilege priv = Privilege.get(resourcePrivilege.getClass(), aclPrivilege);
            if (priv != null
                    && priv.includes(resourcePrivilege)) {
                // メモ
                // Privilege.get(${ACLの設定}).includes(${リソースにアクセスするために必要な値})) {
                return true;
            }
        }
        return false;
    }

    /**
     * 設定ロールURLの相対パス解決.
     * @param base ACLのxml:base属性の値
     * @param principalHref ACLのprincipal-Href
     * @return
     */
    private String getPrincipalHrefUrl(String base, String principalHref) {
        String result = null;
        if (base != null && !"".equals(base)) {
            // 相対パスの解決
            try {
                URI url = new URI(base);
                result = url.resolve(principalHref).toString();
            } catch (URISyntaxException e) {
                return null;
            }
        } else {
            // xml:baseが未設定の場合、hrefにフルパス設定されていると扱う
            result = principalHref;
        }
        return result;
    }

    /**
     * アクセス制御を行う(マスタートークン、ユニットユーザトークン、ユニットローカルユニットユーザトークンのみアクセス可能とする).
     * @return アクセス可能かどうか
     */
    public boolean isUnitUserToken() {
        if (this.getType() == AccessContext.TYPE_UNIT_MASTER) {
            return true;

        } else if ((this.getType() == AccessContext.TYPE_UNIT_USER || this.getType() == AccessContext.TYPE_UNIT_LOCAL)
                && this.getSubject().equals(this.getCell().getOwner())) {
            // ↑ユニットユーザ、ユニットローカルユニットユーザの場合は処理対象のセルオーナーとトークンに含まれるユニットユーザ名が一致した場合のみ有効。
            return true;
        }
        return false;
    }

    /**
     * アクセス制御を行う(SubjectがCELLのトークンのみアクセス可能とする).
     */
    public void checkCellIssueToken() {
        if (AccessContext.TYPE_TRANS.equals(this.getType())
                && this.getSubject().equals(this.getIssuer())) {
            // トークンのISSUER（発行者）とSubject（トークンの持ち主）が一致した場合のみ有効。
            return;

        } else if (AccessContext.TYPE_INVALID.equals(this.getType())) {
            this.throwInvalidTokenException();

        } else if (AccessContext.TYPE_ANONYMOUS.equals(this.getType())) {
            throw DcCoreException.Auth.AUTHORIZATION_REQUIRED;

        } else {
            throw DcCoreException.Auth.NECESSARY_PRIVILEGE_LACKING;
        }
    }

    /**
     * トークンが自分セルローカルトークンか確認する.
     * @param cellname cell
     */
    public void checkMyLocalToken(Cell cellname) {
        // 不正なトークンorトークン指定がない場合401を返却
        // 自分セルローカルトークン以外のトークンの場合403を返却
        if (AccessContext.TYPE_INVALID.equals(this.getType())) {
            this.throwInvalidTokenException();
        } else if (AccessContext.TYPE_ANONYMOUS.equals(this.getType())) {
            throw DcCoreException.Auth.AUTHORIZATION_REQUIRED;
        } else if (!(this.getType() == AccessContext.TYPE_LOCAL
        && this.getCell().getName().equals(cellname.getName()))) {
            throw DcCoreException.Auth.NECESSARY_PRIVILEGE_LACKING;
        }
    }

    /**
     * スキーマ設定をチェックしアクセス可能か判断する.
     * @param settingConfidentialLevel スキーマレベル設定
     * @param box box
     */
    public void checkSchemaAccess(String settingConfidentialLevel, Box box) {
        // マスタートークンかユニットユーザ、ユニットローカルユニットユーザの場合はスキーマ認証をスルー。
        if (this.isUnitUserToken()) {
            return;
        }

        String tokenConfidentialLevel = this.getConfidentialLevel();

        // スキーマ認証レベルが未設定（空）かNONEの場合はスキーマ認証チェック不要。
        if (("".equals(settingConfidentialLevel) || OAuth2Helper.SchemaLevel.NONE.equals(settingConfidentialLevel))) {
            return;
        }

        // トークンの有効性チェック
        // トークンがINVALIDでもスキーマレベル設定が未設定だとアクセスを許可する必要があるのでこのタイミングでチェック
        if (AccessContext.TYPE_INVALID.equals(this.getType())) {
            this.throwInvalidTokenException();
        } else if (AccessContext.TYPE_ANONYMOUS.equals(this.getType())) {
            throw DcCoreException.Auth.AUTHORIZATION_REQUIRED;
        }

        // トークン内のスキーマチェック(Boxレベル以下かつマスタートークン以外のアクセスの場合のみ)
        if (box != null) {
            String boxSchema = box.getSchema();
            String tokenSchema = this.getSchema();
            // ボックスのスキーマが未設定の場合チェックしない
            if (boxSchema != null) {
                if (tokenSchema == null) {
                    throw DcCoreException.Auth.SCHEMA_AUTH_REQUIRED;
                    // トークンスキーマがConfidentialの場合、#cを削除して比較する
                } else if (!tokenSchema.replaceAll(OAuth2Helper.Key.CONFIDENTIAL_MARKER, "").equals(boxSchema)) {
                    // 認証・ボックスのスキーマが設定済かつ等しいくない場合
                    throw DcCoreException.Auth.SCHEMA_MISMATCH;
                }
            }
        }

        if (OAuth2Helper.SchemaLevel.PUBLIC.equals(settingConfidentialLevel)) {
            // 設定がPUBLICの場合はトークン（ac）のスキーマがPUBLICとCONFIDENTIALならOK
            if (OAuth2Helper.SchemaLevel.PUBLIC.equals(tokenConfidentialLevel)
                    || OAuth2Helper.SchemaLevel.CONFIDENTIAL.equals(tokenConfidentialLevel)) {
                return;
            }
        } else if (OAuth2Helper.SchemaLevel.CONFIDENTIAL.equals(settingConfidentialLevel)
                && OAuth2Helper.SchemaLevel.CONFIDENTIAL.equals(tokenConfidentialLevel)) {
            // 設定がCONFIDENTIALの場合はトークン（ac）のスキーマがCONFIDENTIALならOK
            return;
        }
        throw DcCoreException.Auth.INSUFFICIENT_SCHEMA_AUTHZ_LEVEL;
    }

    /**
     * 無効なトークンの例外を投げ分ける.
     */
    public void throwInvalidTokenException() {
        switch (this.invalidReason) {
        case expired:
            throw DcCoreException.Auth.EXPIRED_ACCESS_TOKEN;
        case authenticationScheme:
            throw DcCoreException.Auth.INVALID_AUTHN_SCHEME;
        case basicAuthFormat:
            throw DcCoreException.Auth.BASIC_AUTH_FORMAT_ERROR;
        case authError:
            throw DcCoreException.Auth.AUTHENTICATION_FAILED;
        case tokenParseError:
            throw DcCoreException.Auth.TOKEN_PARSE_ERROR;
        case refreshToken:
            throw DcCoreException.Auth.ACCESS_WITH_REFRESH_TOKEN;
        case tokenDsigError:
            throw DcCoreException.Auth.TOKEN_DISG_ERROR;
        default:
            throw DcCoreException.Server.UNKNOWN_ERROR;
        }
    }

    /**
     * クッキー認証の際の、トークン暗号化・複合化のキーを生成する.
     * @param uri リクエストURI
     * @return 暗号化・複合化に用いるためのキー
     */
    public static String getCookieCryptKey(URI uri) {
        // PCSではステートレスアクセスなので、ユーザ毎にキーを変更することは難しいため、
        // URIのホスト名を基にキーを生成する。
        // ホスト名を加工する。
        return uri.getHost().replaceAll("[aiueo]", "#");
    }
}
