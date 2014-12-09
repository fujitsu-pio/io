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

import static com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.MILLISECS_IN_AN_HOUR;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.POST;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.UriInfo;

import org.json.simple.JSONObject;
import org.odata4j.core.OEntityKey;
import org.odata4j.edm.EdmEntitySet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token;
import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.TokenDsigException;
import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.TokenParseException;
import com.fujitsu.dc.common.auth.token.AbstractOAuth2Token.TokenRootCrtException;
import com.fujitsu.dc.common.auth.token.AccountAccessToken;
import com.fujitsu.dc.common.auth.token.CellLocalAccessToken;
import com.fujitsu.dc.common.auth.token.CellLocalRefreshToken;
import com.fujitsu.dc.common.auth.token.IAccessToken;
import com.fujitsu.dc.common.auth.token.IExtRoleContainingToken;
import com.fujitsu.dc.common.auth.token.IRefreshToken;
import com.fujitsu.dc.common.auth.token.LocalToken;
import com.fujitsu.dc.common.auth.token.Role;
import com.fujitsu.dc.common.auth.token.TransCellAccessToken;
import com.fujitsu.dc.common.auth.token.TransCellRefreshToken;
import com.fujitsu.dc.common.auth.token.UnitLocalUnitUserToken;
import com.fujitsu.dc.common.utils.DcCoreUtils;
import com.fujitsu.dc.core.DcCoreAuthnException;
import com.fujitsu.dc.core.DcCoreConfig;
import com.fujitsu.dc.core.DcCoreException;
import com.fujitsu.dc.core.DcCoreLog;
import com.fujitsu.dc.core.auth.AccessContext;
import com.fujitsu.dc.core.auth.OAuth2Helper;
import com.fujitsu.dc.core.auth.OAuth2Helper.Key;
import com.fujitsu.dc.core.model.Box;
import com.fujitsu.dc.core.model.Cell;
import com.fujitsu.dc.core.model.DavRsCmp;
import com.fujitsu.dc.core.model.ModelFactory;
import com.fujitsu.dc.core.model.ctl.Account;
import com.fujitsu.dc.core.odata.DcODataProducer;
import com.fujitsu.dc.core.odata.OEntityWrapper;

/**
 * 認証処理を司るJAX-RSリソース.
 */
public class TokenEndPointResource {

    /**
     * ログ.
     */
    static Logger log = LoggerFactory.getLogger(TokenEndPointResource.class);

    private final Cell cell;
    private final DavRsCmp davRsCmp;
    private boolean issueCookie = false;
    private UriInfo requestURIInfo;
    // パスワード認証時に使用するAccountのUUID。パスワード認証後に最終ログイン時刻の更新に使用する。
    private String accountId;

    /**
     * コンストラクタ.
     * @param cell Cell
     * @param davRsCmp davRsCmp
     */
    public TokenEndPointResource(final Cell cell, final DavRsCmp davRsCmp) {
        this.cell = cell;
        this.davRsCmp = davRsCmp;
    }

    /**
     * 認証のエンドポイント. <h2>トークンの発行しわけ</h2>
     * <ul>
     * <li>dc_targetにURLが書いてあれば、そのCELLをTARGETのCELLとしてtransCellTokenを発行する。</li>
     * <li>scopeがなければCellLocalを発行する。</li>
     * </ul>
     * @param uriInfo URI情報
     * @param authzHeader Authorization ヘッダ
     * @param grantType クエリパラメタ
     * @param username クエリパラメタ
     * @param password クエリパラメタ
     * @param dcTarget クエリパラメタ
     * @param dcOwner クエリパラメタ
     * @param assertion クエリパラメタ
     * @param refreshToken クエリパラメタ
     * @param clientId クエリパラメタ
     * @param clientSecret クエリパラメタ
     * @param dcCookie クエリパラメタ
     * @param host Hostヘッダ
     * @return JAX-RS Response Object
     */
    @POST
    public final Response auth(@Context final UriInfo uriInfo,
            @HeaderParam(HttpHeaders.AUTHORIZATION) final String authzHeader,
            @FormParam(Key.GRANT_TYPE) final String grantType,
            @FormParam(Key.USERNAME) final String username,
            @FormParam(Key.PASSWORD) final String password,
            @FormParam(Key.TARGET) final String dcTarget,
            @FormParam(Key.OWNER) final String dcOwner,
            @FormParam(Key.ASSERTION) final String assertion,
            @FormParam(Key.REFRESH_TOKEN) final String refreshToken,
            @FormParam(Key.CLIENT_ID) final String clientId,
            @FormParam(Key.CLIENT_SECRET) final String clientSecret,
            @FormParam("dc_cookie") final String dcCookie,
            @HeaderParam(HttpHeaders.HOST) final String host) {

        // dc_target がURLでない場合はヘッダInjectionの脆弱性を産んでしまう。(改行コードが入っているなど)
        String target = this.checkDcTarget(dcTarget);

        if (null != dcTarget) {
            issueCookie = false;
        } else {
            issueCookie = Boolean.parseBoolean(dcCookie);
            requestURIInfo = uriInfo;
        }

        String schema = null;
        // まずはClient認証したいのかを確認
        // ScopeもauthzHeaderもclientIdもない場合はClient認証しないとみなす。
        if (clientId != null || authzHeader != null) {
            schema = this.clientAuth(clientId, clientSecret, authzHeader);
        }

        if (OAuth2Helper.GrantType.PASSWORD.equals(grantType)) {
            // 通常のパスワード認証
            Response response = this.handlePassword(target, dcOwner, host, schema, username, password);

            // パスワード認証が成功した場合はアカウントの最終ログイン時刻を更新する
            // パスワード認証が成功した場合のみ、ここを通る(handlePassword内でエラーが発生すると、例外がthrowされる)
            if (DcCoreConfig.getAccountLastAuthenticatedEnable()) {
                // Accountのスキーマ情報を取得する
                DcODataProducer producer = ModelFactory.ODataCtl.cellCtl(cell);
                EdmEntitySet esetAccount = producer.getMetadata().getEdmEntitySet(Account.EDM_TYPE_NAME);
                OEntityKey originalKey = OEntityKey.parse("('" + username + "')");
                // 最終ログイン時刻の変更をProducerに依頼(このメソッド内でロックを取得・解放)
                producer.updateLastAuthenticated(esetAccount, originalKey, accountId);
            }
            return response;
        } else if (OAuth2Helper.GrantType.SAML2_BEARER.equals(grantType)) {
            return this.receiveSaml2(target, dcOwner, schema, assertion);
        } else if (OAuth2Helper.GrantType.REFRESH_TOKEN.equals(grantType)) {
            return this.receiveRefresh(target, dcOwner, host, refreshToken);
        } else {
            throw DcCoreAuthnException.UNSUPPORTED_GRANT_TYPE;
        }
    }

    private String checkDcTarget(final String dcTarget) {
        String target = dcTarget;
        if (target != null) {
            try {
                new URL(target);
                if (!target.endsWith("/")) {
                    target = target + "/";
                }
                if (target.contains("\n") || target.contains("\r")) {
                    // dc_targetがURLでない場合はエラー
                    throw DcCoreAuthnException.INVALID_TARGET;
                }
            } catch (MalformedURLException e) {
                // dc_targetがURLでない場合はエラー
                throw DcCoreAuthnException.INVALID_TARGET;
            }
        }
        return target;
    }

    /**
     * クライアント認証処理.
     * @param scope
     * @param clientId
     * @param clientSecret
     * @param authzHeader
     * @return null： Client認証に失敗した場合。
     */
    private String clientAuth(final String clientId, final String clientSecret, final String authzHeader) {
        String id = clientId;
        String pw = clientSecret;
        if (pw == null) {
            pw = "";
        }

        // authzHeaderのパース
        if (authzHeader != null) {
            String[] idpw = DcCoreUtils.parseBasicAuthzHeader(authzHeader);
            if (idpw != null) {
                // authzHeaderの指定を優先
                id = idpw[0];
                pw = idpw[1];
            } else {
                throw DcCoreAuthnException.AUTH_HEADER_IS_INVALID.realm(cell.getUrl());
            }
        }

        // pwのチェック
        // ・PWはSAMLトークンなので、これをパースする。
        TransCellAccessToken tcToken = null;
        try {
            tcToken = TransCellAccessToken.parse(pw);
        } catch (TokenParseException e) {
            // パースの失敗
            DcCoreLog.Auth.TOKEN_PARSE_ERROR.params(e.getMessage()).writeLog();
            throw DcCoreAuthnException.CLIENT_SERCRET_PARSE_ERROR.realm(cell.getUrl()).reason(e);
        } catch (TokenDsigException e) {
            // 署名検証エラー
            DcCoreLog.Auth.TOKEN_DISG_ERROR.params(e.getMessage()).writeLog();
            throw DcCoreAuthnException.TOKEN_DSIG_INVALID;
        } catch (TokenRootCrtException e) {
            // ルートCA証明書の設定エラー
            DcCoreLog.Auth.ROOT_CA_CRT_SETTING_ERROR.params(e.getMessage()).writeLog();
            throw DcCoreException.Auth.ROOT_CA_CRT_SETTING_ERROR;
        }

        // ・有効期限チェック
        if (tcToken.isExpired()) {
            throw DcCoreAuthnException.CLIENT_SERCRET_EXPIRED.realm(cell.getUrl());
        }

        // ・IssuerがIDと等しいことを確認
        if (!id.equals(tcToken.getIssuer())) {
            throw DcCoreAuthnException.CLIENT_SERCRET_ISSUER_MISMATCH.realm(cell.getUrl());
        }

        // トークンのターゲットが自分でない場合はエラー応答
        if (!tcToken.getTarget().equals(cell.getUrl())) {
            throw DcCoreAuthnException.CLIENT_SERCRET_TARGET_WRONG.realm(cell.getUrl());
        }

        // ロールが特殊(confidential)な値だったら#cを付与
        String confidentialRoleUrl = String.format(OAuth2Helper.Key.CONFIDENTIAL_ROLE_URL_FORMAT, tcToken.getIssuer(),
                Box.DEFAULT_BOX_NAME);
        for (Role role : tcToken.getRoles()) {
            if (confidentialRoleUrl.equals(role.createUrl())) {
                // 認証成功。
                return id + OAuth2Helper.Key.CONFIDENTIAL_MARKER;
            }
        }
        // 認証成功。
        return id;
    }

    private Response receiveSaml2(final String target,
            final String owner, final String schema, final String assertion) {
        if (Key.TRUE_STR.equals(owner)) {
            // トークン認証でのユニットユーザ昇格はさせない
            throw DcCoreAuthnException.TC_ACCESS_REPRESENTING_OWNER;
        }

        // assertionのnullチェック
        if (assertion == null) {
            // assertionが未設定の場合、パースエラーとみなす
            throw DcCoreAuthnException.TOKEN_PARSE_ERROR;
        }

        // まずはパースする
        TransCellAccessToken tcToken = null;
        try {
            tcToken = TransCellAccessToken.parse(assertion);
        } catch (TokenParseException e) {
            // パース失敗時
            DcCoreLog.Auth.TOKEN_PARSE_ERROR.params(e.getMessage()).writeLog();
            throw DcCoreAuthnException.TOKEN_PARSE_ERROR;
        } catch (TokenDsigException e) {
            // 署名検証でエラー
            DcCoreLog.Auth.TOKEN_DISG_ERROR.params(e.getMessage()).writeLog();
            throw DcCoreAuthnException.TOKEN_DSIG_INVALID;
        } catch (TokenRootCrtException e) {
            // ルートCA証明書の設定エラー
            DcCoreLog.Auth.ROOT_CA_CRT_SETTING_ERROR.params(e.getMessage()).writeLog();
            throw DcCoreException.Auth.ROOT_CA_CRT_SETTING_ERROR;
        }

        // Tokenの検証
        // 1．有効期限チェック
        if (tcToken.isExpired()) {
            throw DcCoreAuthnException.TOKEN_EXPIRED;
        }

        // トークンのターゲットが自分でない場合はエラー応答
        try {
            if (!(AuthResourceUtils.checkTargetUrl(this.cell, tcToken))) {
                throw DcCoreAuthnException.TOKEN_TARGET_WRONG.params(tcToken.getTarget());
            }
        } catch (MalformedURLException e) {
            throw DcCoreAuthnException.TOKEN_TARGET_WRONG.params(tcToken.getTarget());
        }

        // 認証は成功 -------------------------------

        // 認証できた情報をもとにリフレッシュトークンを作成
        long issuedAt = new Date().getTime();
        TransCellRefreshToken rToken = new TransCellRefreshToken(tcToken.getId(), // IDは受領SAMLのものを保存
                issuedAt, cell.getUrl(), tcToken.getSubject(), tcToken.getIssuer(), // 受領SAMLのものを保存
                tcToken.getRoles(), // 受領SAMLのものを保存
                schema);

        // CELLに依頼してTC発行元のロールから自分のところのロールを決定する。
        List<Role> rolesHere = cell.getRoleListHere(tcToken);

        // TODO schema は指定のものをつかっていい？
        // TODO schema認証は必要。
        String schemaVerified = schema;

        // 認証トークン発行処理
        // ターゲットは自由に決めてよい。
        IAccessToken aToken = null;
        if (target == null) {
            aToken = new CellLocalAccessToken(issuedAt, cell.getUrl(), tcToken.getSubject(), rolesHere, schemaVerified);
        } else {
            aToken = new TransCellAccessToken(UUID.randomUUID().toString(), issuedAt, cell.getUrl(),
                    tcToken.getSubject(), target, rolesHere, schemaVerified);
        }
        return this.responseAuthSuccess(aToken, rToken);
    }

    /**
     * リフレッシュ認証処理.
     * @param target
     * @param owner
     * @param host
     * @param refreshToken
     * @return
     */
    private Response receiveRefresh(final String target, String owner, final String host, final String refreshToken) {
        try {
            // refreshTokenのnullチェック
            if (refreshToken == null) {
                // refreshTokenが未設定の場合、パースエラーとみなす
                throw DcCoreAuthnException.TOKEN_PARSE_ERROR;
            }

            AbstractOAuth2Token token = AbstractOAuth2Token.parse(refreshToken, cell.getUrl(), host);

            if (!(token instanceof IRefreshToken)) {
                throw DcCoreAuthnException.NOT_REFRESH_TOKEN;
            }

            // リフレッシュトークンの有効期限チェック
            if (token.isRefreshExpired()) {
                throw DcCoreAuthnException.TOKEN_EXPIRED;
            }

            long issuedAt = new Date().getTime();

            if (Key.TRUE_STR.equals(owner)) {
                // 自分セルリフレッシュの場合のみ昇格できる。
                if (token.getClass() != CellLocalRefreshToken.class) {
                    throw DcCoreAuthnException.TC_ACCESS_REPRESENTING_OWNER;
                }
                // ユニット昇格権限設定のチェック
                if (!this.davRsCmp.checkOwnerRepresentativeAccounts(token.getSubject())) {
                    throw DcCoreAuthnException.NOT_ALLOWED_REPRESENT_OWNER;
                }
                // セルのオーナーが未設定のセルに対しては昇格させない。
                if (cell.getOwner() == null) {
                    throw DcCoreAuthnException.NO_CELL_OWNER;
                }

                // uluut発行処理
                UnitLocalUnitUserToken uluut = new UnitLocalUnitUserToken(
                        issuedAt, UnitLocalUnitUserToken.ACCESS_TOKEN_EXPIRES_HOUR * MILLISECS_IN_AN_HOUR,
                        cell.getOwner(), host);

                return this.responseAuthSuccess(uluut, null);
            }

            // 受け取ったRefresh Tokenから AccessTokenとRefreshTokenを再生成
            IRefreshToken rToken = (IRefreshToken) token;
            rToken = rToken.refreshRefreshToken(issuedAt);

            IAccessToken aToken = null;
            if (rToken instanceof CellLocalRefreshToken) {
                String subject = rToken.getSubject();
                List<Role> roleList = cell.getRoleListForAccount(subject);
                aToken = rToken.refreshAccessToken(issuedAt, target, cell.getUrl(), roleList);
            } else {
                // CELLに依頼してトークン発行元のロールから自分のところのロールを決定する。
                List<Role> rolesHere = cell.getRoleListHere((IExtRoleContainingToken) rToken);
                aToken = rToken.refreshAccessToken(issuedAt, target, cell.getUrl(), rolesHere);
            }

            if (aToken instanceof TransCellAccessToken) {
                log.debug("reissuing TransCell Token");
                // aToken.addRole("admin");
                // return this.responseAuthSuccess(tcToken);
            }
            return this.responseAuthSuccess(aToken, rToken);
        } catch (TokenParseException e) {
            // パースに失敗したので
            DcCoreLog.Auth.TOKEN_PARSE_ERROR.params(e.getMessage()).writeLog();
            throw DcCoreAuthnException.TOKEN_PARSE_ERROR.reason(e);
        } catch (TokenDsigException e) {
            // 証明書検証に失敗したので
            DcCoreLog.Auth.TOKEN_DISG_ERROR.params(e.getMessage()).writeLog();
            throw DcCoreAuthnException.TOKEN_DSIG_INVALID;
        } catch (TokenRootCrtException e) {
            // ルートCA証明書の設定エラー
            DcCoreLog.Auth.ROOT_CA_CRT_SETTING_ERROR.params(e.getMessage()).writeLog();
            throw DcCoreException.Auth.ROOT_CA_CRT_SETTING_ERROR;
        }
    }

    @SuppressWarnings("unchecked")
    private Response responseAuthSuccess(final IAccessToken accessToken, final IRefreshToken refreshToken) {
        JSONObject resp = new JSONObject();
        resp.put(OAuth2Helper.Key.ACCESS_TOKEN, accessToken.toTokenString());
        resp.put(OAuth2Helper.Key.EXPIRES_IN, accessToken.expiresIn());
        if (refreshToken != null) {
            resp.put(OAuth2Helper.Key.REFRESH_TOKEN, refreshToken.toTokenString());
            resp.put(OAuth2Helper.Key.REFRESH_TOKEN_EXPIRES_IN, refreshToken.refreshExpiresIn());
        }
        resp.put(OAuth2Helper.Key.TOKEN_TYPE, OAuth2Helper.Scheme.BEARER);
        ResponseBuilder rb = Response.ok().type(MediaType.APPLICATION_JSON_TYPE);
        if (accessToken.getTarget() != null) {
            resp.put(OAuth2Helper.Key.TARGET, accessToken.getTarget());
            rb.header(HttpHeaders.LOCATION, accessToken.getTarget() + "__auth");
        }

        if (issueCookie) {
            String tokenString = accessToken.toTokenString();
            // dc_cookie_peerとして、ランダムなUUIDを設定する
            String dcCookiePeer = UUID.randomUUID().toString();
            String cookieValue = dcCookiePeer + "\t" + tokenString;
            // ヘッダに返却するdc_cookie値は、暗号化する
            String encodedCookieValue = LocalToken.encode(cookieValue,
                    UnitLocalUnitUserToken.getIvBytes(AccessContext.getCookieCryptKey(requestURIInfo.getBaseUri())));
            // Cookieのバージョン(0)を指定
            int version = 0;
            String path = getCookiePath();

            // Cookieを作成し、レスポンスヘッダに返却する
            Cookie cookie = new Cookie("dc_cookie", encodedCookieValue, path, requestURIInfo.getBaseUri().getHost(),
                    version);
            rb.cookie(new NewCookie(cookie, "", -1, DcCoreConfig.isHttps()));
            // レスポンスボディの"dc_cookie_peer"を返却する
            resp.put("dc_cookie_peer", dcCookiePeer);
        }
        return rb.entity(resp.toJSONString()).build();
    }

    /**
     * Cookieに設定するパスを作成する.
     * @return Cookieに設定するパス
     */
    private String getCookiePath() {
        String cellUrl = cell.getUrl();
        try {
            URL url = new URL(cellUrl);
            return url.getPath();
        } catch (MalformedURLException e) {
            throw DcCoreAuthnException.AUTHN_FAILED;
        }
    }

    private Response handlePassword(final String target,
            final String owner,
            final String host,
            final String schema,
            final String username,
            final String password) {

        // パスワードのCheck処理
        if (username == null) {
            throw DcCoreAuthnException.REQUIRED_PARAM_MISSING.params(Key.USERNAME);
        } else if (password == null) {
            throw DcCoreAuthnException.REQUIRED_PARAM_MISSING.params(Key.PASSWORD);
        }

        OEntityWrapper oew = cell.getAccount(username);
        if (oew == null) {
            throw DcCoreAuthnException.AUTHN_FAILED;
        }
        // 最終ログイン時刻を更新するために、UUIDをクラス変数にひかえておく
        accountId = (String) oew.getUuid();

        // ロックのチェック
        Boolean isLock = AuthResourceUtils.isLockedAccount(accountId);
        if (isLock) {
            // memcachedのロック時間を更新
            AuthResourceUtils.registAccountLock(accountId);
            throw DcCoreAuthnException.ACCOUNT_LOCK_ERROR;
        }

        boolean authSuccess = cell.authenticateAccount(oew, password);

        if (!authSuccess) {
            // memcachedにロックを作成
            AuthResourceUtils.registAccountLock(accountId);
            throw DcCoreAuthnException.AUTHN_FAILED;
        }

        long issuedAt = new Date().getTime();

        if (Key.TRUE_STR.equals(owner)) {
            // ユニット昇格権限設定のチェック
            if (!this.davRsCmp.checkOwnerRepresentativeAccounts(username)) {
                throw DcCoreAuthnException.NOT_ALLOWED_REPRESENT_OWNER;
            }
            // セルのオーナーが未設定のセルに対しては昇格させない。
            if (cell.getOwner() == null) {
                throw DcCoreAuthnException.NO_CELL_OWNER;
            }

            // uluut発行処理
            UnitLocalUnitUserToken uluut = new UnitLocalUnitUserToken(
                    issuedAt, UnitLocalUnitUserToken.ACCESS_TOKEN_EXPIRES_HOUR * MILLISECS_IN_AN_HOUR,
                    cell.getOwner(), host);
            return this.responseAuthSuccess(uluut, null);
        }

        CellLocalRefreshToken rToken = new CellLocalRefreshToken(issuedAt,
                CellLocalRefreshToken.REFRESH_TOKEN_EXPIRES_HOUR * MILLISECS_IN_AN_HOUR, cell.getUrl(), username,
                schema);

        // レスポンスをつくる。
        if (target == null) {
            AccountAccessToken localToken = new AccountAccessToken(issuedAt,
                    AccountAccessToken.ACCESS_TOKEN_EXPIRES_HOUR * MILLISECS_IN_AN_HOUR, cell.getUrl(), username,
                    schema);
            return this.responseAuthSuccess(localToken, rToken);
        } else {
            // TODO SCHEMAがURLであることのチェック
            // TODO TARGETがURLであることのチェック

            List<Role> roleList = cell.getRoleListForAccount(username);

            TransCellAccessToken tcToken = new TransCellAccessToken(cell.getUrl(), cell.getUrl() + "#" + username,
                    target, roleList, schema);
            return this.responseAuthSuccess(tcToken, rToken);
        }
    }

    /**
     * OPTIONSメソッド.
     * @return JAX-RS Response
     */
    @OPTIONS
    public Response options() {
        return DcCoreUtils.responseBuilderForOptions(HttpMethod.POST).build();
    }
}
