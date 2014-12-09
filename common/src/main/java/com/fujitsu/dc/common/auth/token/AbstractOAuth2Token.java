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
package com.fujitsu.dc.common.auth.token;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;



/**
 * 本パッケージ内で定義する様々なTokenクラスの基底抽象クラス.
 */
public abstract class AbstractOAuth2Token {
    /**
     * 一時間あたりのミリ秒数. 3600000
     */
    public static final int MILLISECS_IN_AN_HOUR = 3600000;
    /**
     * 一秒あたりのミリ秒数. 1000
     */
    public static final int MILLISECS_IN_A_SEC = 1000;
    /**
     * 一時間あたりの秒数.
     */
    public static final int SECS_IN_A_HOUR = 60 * 60;
    /**
     * 一日あたり秒数.
     */
    public static final int SECS_IN_AN_DAY = 24 * 60 * 60;
    /**
     * 本パッケージで用いるトークンパース例外クラス.
     */
    @SuppressWarnings("serial")
    public static class TokenParseException extends Exception {
        /**
         * コンストラクタ.
         * @param msg メッセージ
         */
        public TokenParseException(final String msg) {
            super(msg);
        }
        /**
         * コンストラクタ.
         * @param e 原因 Throwable
         */
        public TokenParseException(final Throwable e) {
            super(e);
        }
        /**
         * コンストラクタ.
         * @param msg メッセージ
         * @param e 原因 Throwable
         */
        public TokenParseException(final String msg, final Throwable e) {
            super(msg, e);
        }
    }
    /**
     * 本パッケージで用いる署名検証例外クラス.
     */
    @SuppressWarnings("serial")
    public static class TokenDsigException extends Exception {
        /**
         * コンストラクタ.
         * @param msg メッセージ
         */
        public TokenDsigException(final String msg) {
            super(msg);
        }
        /**
         * コンストラクタ.
         * @param e 原因 Throwable
         */
        public TokenDsigException(final Throwable e) {
            super(e);
        }
        /**
         * コンストラクタ.
         * @param msg メッセージ
         * @param e 原因 Throwable
         */
        public TokenDsigException(final String msg, final Throwable e) {
            super(msg, e);
        }
    }
    /**
     * 本パッケージで用いるルートCA証明書例外クラス.
     */
    @SuppressWarnings("serial")
    public static class TokenRootCrtException extends Exception {
        /**
         * コンストラクタ.
         * @param msg メッセージ
         */
        public TokenRootCrtException(final String msg) {
            super(msg);
        }
        /**
         * コンストラクタ.
         * @param e 原因 Throwable
         */
        public TokenRootCrtException(final Throwable e) {
            super(e);
        }
        /**
         * コンストラクタ.
         * @param msg メッセージ
         * @param e 原因 Throwable
         */
        public TokenRootCrtException(final String msg, final Throwable e) {
            super(msg, e);
        }
    }

    long issuedAt;
    long lifespan;
    String issuer;
    String subject;
    String schema;
    List<Role> roleList = new ArrayList<Role>();

    /**
     * トークンの発行者 CELL URLを返します.
     * @return トークンの発行者URL
     */
    public final String getIssuer() {
        return this.issuer;
    }

    /**
     * トークンが表す Subject (アクセス主体) のURLを返します.
     * @return Subject URL
     */
    public final String getSubject() {
        return this.subject;
    }

    /**
     * スキーマURLを返します.
     * @return Schema Url
     */
    public final String getSchema() {
        return this.schema;
    }

    /**
     * ロールリストを返します.
     * @return ロールリスト
     */
    public final List<Role> getRoles() {
        return this.roleList;
    }

    final void addRole(final Role role) {
        this.roleList.add(role);
    }

    final String makeRolesString() {
        if (this.roleList == null || this.roleList.size() == 0) {
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (Role rl : this.roleList) {
            sb.append(rl.createUrl());
            sb.append(" ");
        }
        return sb.substring(0, sb.length() - 1);
    }

    static List<Role> parseRolesString(final String rolesStr) throws MalformedURLException {
        List<Role> ret = new ArrayList<Role>();
        if ("".equals(rolesStr)) {
            return ret;
        }
        for (String s : rolesStr.split(" ")) {
            ret.add(new Role(new URL(s)));
        }
        return ret;
    }

    static final TokenParseException PARSE_EXCEPTION = new TokenParseException("failed to parse token");

    /**
     * トークン失効までの秒数を返します.
     * @return トークン失効までの秒数
     */
    public final int expiresIn() {
        return SECS_IN_A_HOUR;
    }

    /**
     * トークンが失効しているかどうかチェック.
     * @return boolean
     */
    public final boolean isExpired() {
        long now = new Date().getTime();

        // 有効期限のリミット=認証した時刻＋有効期限
        long expiresLimit = this.issuedAt + this.expiresIn() * MILLISECS_IN_A_SEC;

        if (now > expiresLimit) {
            return true;
        }
        return false;
    }

    /**
     * リフレッシュトークン失効までの秒数を返します.
     * @return リフレッシュトークン失効までの秒数
     */
    public final int refreshExpiresIn() {
        return SECS_IN_AN_DAY;
    }

    /**
     * リフレッシュトークンが失効しているかどうかチェック.
     * @return boolean
     */
    public final boolean isRefreshExpired() {
        long now = new Date().getTime();

        // 有効期限のリミット=認証した時刻＋有効期限
        long expiresLimit = this.issuedAt + this.refreshExpiresIn() * MILLISECS_IN_A_SEC;

        if (now > expiresLimit) {
            return true;
        }
        return false;
    }


    /**
     * トークン文字列をissuerで指定されたCellとしてパースする.
     * @param token Token String
     * @param issuer Cell Root URL
     * @param host リクエストヘッダHostの値
     * @return パースされたCellLocalTokenオブジェクト
     * @throws TokenParseException トークンのパースに失敗したときに投げられる例外
     * @throws TokenDsigException トークンの署名検証に失敗した時に投げられる例外
     * @throws TokenRootCrtException ルートCA証明書の検証に失敗した時に投げられる例外
     */
    public static AbstractOAuth2Token parse(final String token, final String issuer, final String host)
            throws TokenParseException, TokenDsigException, TokenRootCrtException {
        if (token.startsWith(AccountAccessToken.PREFIX_ACCESS)) {
            return AccountAccessToken.parse(token, issuer);
        } else if (token.startsWith(CellLocalAccessToken.PREFIX_ACCESS)) {
            return CellLocalAccessToken.parse(token, issuer);
        } else if (token.startsWith(CellLocalRefreshToken.PREFIX_REFRESH)) {
            return CellLocalRefreshToken.parse(token, issuer);
        } else if (token.startsWith(TransCellRefreshToken.PREFIX_TC_REFRESH)) {
            return TransCellRefreshToken.parse(token, issuer);
        } else if (token.startsWith(UnitLocalUnitUserToken.PREFIX_UNIT_LOCAL_UNIT_USER)) {
            return UnitLocalUnitUserToken.parse(token, host);
        } else {
            return TransCellAccessToken.parse(token);
        }
    }

    final String toDebugStr() {
        Map<String, String> map = new HashMap<String, String>();
        map.put("issuedAt", new Date(this.issuedAt).toString());
        map.put("expiresAt", new Date(this.issuedAt + this.lifespan).toString());
        map.put("issuer", this.issuer);
        map.put("subject", this.subject);
        map.put("schema", this.schema);
        if (this.makeRolesString() != null) {
            map.put("roles", this.makeRolesString());
        }
        return map.toString();
    }

}
