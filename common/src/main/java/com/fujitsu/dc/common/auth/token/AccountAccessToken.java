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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Account Access Token の生成・パースを行うクラス.
 */
public final class AccountAccessToken extends CellLocalAccessToken implements IAccessToken {

    /**
     * ログ.
     */
    static Logger log = LoggerFactory.getLogger(AccountAccessToken.class);

    /**
     * トークンのPREFIX文字列.
     */
    public static final String PREFIX_ACCESS = "AA~";

    static final int IDX_COUNT = 5;
    static final int IDX_ISSUED_AT = 0;
    static final int IDX_LIFESPAN = 1;
    static final int IDX_ISSUER = 4;
    static final int IDX_SUBJECT = 2;
    static final int IDX_SCHEMA = 3;

    /**
     * トークンの有効期間.
     */
    public static final int ACCESS_TOKEN_EXPIRES_HOUR = 1;

    /**
     * 明示的な有効期間を設定してトークンを生成する.
     * @param issuedAt 発行時刻(epochからのミリ秒)
     * @param lifespan 有効期間(ミリ秒)
     * @param issuer 発行者
     * @param subject Subject
     * @param schema Schema
     */
    public AccountAccessToken(final long issuedAt, final long lifespan, final String issuer,
            final String subject, final String schema) {
        super(issuedAt, lifespan, issuer, subject, null, schema);
    }

    /**
     * 既定値の有効期間を設定してトークンを生成する.
     * @param issuedAt 発行時刻(epochからのミリ秒)
     * @param issuer 発行者
     * @param subject Subject
     * @param schema Schema
     */
    public AccountAccessToken(final long issuedAt, final String issuer, final String subject, final String schema) {
        this(issuedAt, ACCESS_TOKEN_EXPIRES_HOUR * MILLISECS_IN_AN_HOUR, issuer, subject, schema);
    }

    @Override
    public String toTokenString() {
        StringBuilder ret = new StringBuilder(PREFIX_ACCESS);
        ret.append(this.doCreateTokenString(null));
        return ret.toString();
    }

    /**
     * トークン文字列をissuerで指定されたCellとしてパースする.
     * @param token Token String
     * @param issuer Cell Root URL
     * @return パースされたCellLocalTokenオブジェクト
     * @throws AbstractOAuth2Token.TokenParseException トークンのパースに失敗したとき投げられる例外
     */
    public static AccountAccessToken parse(final String token, final String issuer)
            throws AbstractOAuth2Token.TokenParseException {
        if (!token.startsWith(PREFIX_ACCESS) || issuer == null) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        }
        String[] frag = LocalToken.doParse(token.substring(PREFIX_ACCESS.length()), issuer, IDX_COUNT);

        try {
            AccountAccessToken ret = new AccountAccessToken(
                    Long.valueOf(StringUtils.reverse(frag[IDX_ISSUED_AT])),
                    Long.valueOf(frag[IDX_LIFESPAN]),
                    frag[IDX_ISSUER],
                    frag[IDX_SUBJECT],
                    frag[IDX_SCHEMA]);
            return ret;
        } catch (Exception e) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        }
    }
}
