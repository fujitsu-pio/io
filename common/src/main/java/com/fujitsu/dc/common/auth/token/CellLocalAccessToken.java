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
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Cell Local Token の生成・パースを行うクラス.
 */
public class CellLocalAccessToken extends LocalToken implements IAccessToken {

    /**
     * ログ.
     */
    static Logger log = LoggerFactory.getLogger(CellLocalAccessToken.class);

    /**
     * トークンのプレフィックス.
     */
    public static final String PREFIX_ACCESS = "AL~";
    /**
     * トークンの有効時間.
     */
    public static final int ACCESS_TOKEN_EXPIRES_HOUR = 1;

    /**
     * 明示的な有効期間を設定してトークンを生成する.
     * @param issuedAt 発行時刻(epochからのミリ秒)
     * @param lifespan トークンの有効時間（ミリ秒）
     * @param issuer 発行 Cell URL
     * @param subject アクセス主体URL
     * @param roleList ロールリスト
     * @param schema クライアント認証されたデータスキーマ
     */
    public CellLocalAccessToken(final long issuedAt,
            final long lifespan,
            final String issuer,
            final String subject,
            final List<Role> roleList,
            final String schema) {
        super(issuedAt, lifespan, issuer, subject, schema);
        this.roleList = roleList;
    }

    /**
     * 既定値の有効期間を設定してトークンを生成する.
     * @param issuedAt 発行時刻(epochからのミリ秒)
     * @param issuer 発行 Cell URL
     * @param subject アクセス主体URL
     * @param roleList ロールリスト
     * @param schema クライアント認証されたデータスキーマ
     */
    public CellLocalAccessToken(
            final long issuedAt,
            final String issuer,
            final String subject,
            final List<Role> roleList,
            final String schema) {
        this(issuedAt, ACCESS_TOKEN_EXPIRES_HOUR * MILLISECS_IN_AN_HOUR, issuer, subject, roleList, schema);
    }

    /**
     * 既定値の有効期間と現在を発行日時と設定してトークンを生成する.
     * @param issuer 発行 Cell URL
     * @param subject アクセス主体URL
     * @param roleList ロールリスト
     * @param schema クライアント認証されたデータスキーマ
     */
    public CellLocalAccessToken(final String issuer, final String subject,
            final List<Role> roleList, final String schema) {
        this(new DateTime().getMillis(), issuer, subject, roleList, schema);
    }

    @Override
    public String toTokenString() {
        StringBuilder ret = new StringBuilder(PREFIX_ACCESS);
        ret.append(this.doCreateTokenString(new String[] {this.makeRolesString()}));
        return ret.toString();
    }

    static final int IDX_COUNT = 6;
    static final int IDX_ISSUED_AT = 0;
    static final int IDX_LIFESPAN = 1;
    static final int IDX_ISSUER = 5;
    static final int IDX_SUBJECT = 2;
    static final int IDX_ROLE_LIST = 4;
    static final int IDX_SCHEMA = 3;

    /**
     * トークン文字列をissuerで指定されたCellとしてパースする.
     * @param token Token String
     * @param issuer Cell Root URL
     * @return パースされたCellLocalTokenオブジェクト
     * @throws AbstractOAuth2Token.TokenParseException トークンのパースに失敗したとき投げられる例外
     */
    public static CellLocalAccessToken parse(final String token, final String issuer)
            throws AbstractOAuth2Token.TokenParseException {
        if (!token.startsWith(PREFIX_ACCESS) || issuer == null) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        }
        String[] frag = LocalToken.doParse(token.substring(PREFIX_ACCESS.length()), issuer, IDX_COUNT);

        try {
            CellLocalAccessToken ret = new CellLocalAccessToken(
                    Long.valueOf(StringUtils.reverse(frag[IDX_ISSUED_AT])),
                    Long.valueOf(frag[IDX_LIFESPAN]),
                    frag[IDX_ISSUER],
                    frag[IDX_SUBJECT],
                    AbstractOAuth2Token.parseRolesString(frag[IDX_ROLE_LIST]),
                    frag[IDX_SCHEMA]);

            return ret;
        } catch (MalformedURLException e) {
            throw new TokenParseException(e.getMessage(), e);
        } catch (IllegalStateException e) {
            throw new TokenParseException(e.getMessage(), e);
        }
    }

    @Override
    public String getTarget() {
        return null;
    }

    @Override
    public String getId() {
        return this.subject + ":" + this.issuedAt;
    }

}
