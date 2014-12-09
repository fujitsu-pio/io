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

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Cell Local Token の生成・パースを行うクラス.
 */
public final class CellLocalRefreshToken extends LocalToken implements IRefreshToken {

    /**
     * ログ.
     */
    static Logger log = LoggerFactory.getLogger(CellLocalRefreshToken.class);

    /**
     * この種類のトークンのプレフィックス.
     */
    public static final String PREFIX_REFRESH = "RA~";
    /**
     * トークンの有効時間.
     */
    public static final int REFRESH_TOKEN_EXPIRES_HOUR = 24;

    /**
     * 明示的な有効期間を設定してトークンを生成する.
     * @param issuedAt 発行時刻(epochからのミリ秒)
     * @param lifespan トークンの有効時間（ミリ秒）
     * @param issuer 発行 Cell URL
     * @param subject アクセス主体URL
     * @param schema クライアント認証されたデータスキーマ
     */
    public CellLocalRefreshToken(
            final long issuedAt,
            final long lifespan,
            final String issuer,
            final String subject,
            final String schema) {
        super(issuedAt, lifespan, issuer, subject, schema);
    }

    /**
     * 既定値の有効期間を設定してトークンを生成する.
     * @param issuedAt 発行時刻(epochからのミリ秒)
     * @param issuer 発行 Cell URL
     * @param subject アクセス主体URL
     * @param schema クライアント認証されたデータスキーマ
     */
    public CellLocalRefreshToken(
            final long issuedAt,
            final String issuer,
            final String subject,
            final String schema) {
        super(issuedAt, REFRESH_TOKEN_EXPIRES_HOUR * MILLISECS_IN_AN_HOUR, issuer, subject, schema);
    }

    /**
     * コンストラクタ.
     * 既定値の有効期間と現在を発行日時と設定してトークンを生成する.
     * @param issuer 発行 Cell URL
     * @param subject アクセス主体URL
     * @param schema クライアント認証されたデータスキーマ
     */
    public CellLocalRefreshToken(final String issuer, final String subject, final String schema) {
        this(new DateTime().getMillis(), issuer, subject, schema);
    }

    @Override
    public String toTokenString() {
        StringBuilder ret = new StringBuilder(PREFIX_REFRESH);
        ret.append(this.doCreateTokenString(null));
        return ret.toString();
    }

    static final int IDX_COUNT = 5;
    static final int IDX_ISSUED_AT = 0;
    static final int IDX_LIFESPAN = 1;
    static final int IDX_ISSUER = 4;
    static final int IDX_SUBJECT = 2;
    static final int IDX_SCHEMA = 3;
    /**
     * トークン文字列をissuerで指定されたCellとしてパースする.
     * @param token Token String
     * @param issuer Cell Root URL
     * @return パースされたCellLocalTokenオブジェクト
     * @throws AbstractOAuth2Token.TokenParseException トークンのパースに失敗したとき投げられる例外
     */
    public static CellLocalRefreshToken parse(final String token, final String issuer)
            throws AbstractOAuth2Token.TokenParseException {
        if (!token.startsWith(PREFIX_REFRESH) || issuer == null) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        }
        String[] frag = LocalToken.doParse(token.substring(PREFIX_REFRESH.length()), issuer, IDX_COUNT);

        try {
            CellLocalRefreshToken ret = new CellLocalRefreshToken(
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

    @Override
    public String getId() {
        return this.subject + this.issuedAt;
    }

    @Override
    public IAccessToken refreshAccessToken(final long issuedAt,
            final String target, final String cellUrl, List<Role> roleList) {
        if (target == null) {
            return new AccountAccessToken(issuedAt, this.issuer, this.getSubject(), this.getSchema());
        } else {
            // 自分セルローカル払い出し時に払い出されるリフレッシュトークンにはロール入ってないので取得する。
            return new TransCellAccessToken(issuedAt, this.issuer, cellUrl + "#" + this.getSubject(),
                    target, roleList, this.getSchema());
        }
    }

    @Override
    public IRefreshToken refreshRefreshToken(final long issuedAt) {
        return new CellLocalRefreshToken(issuedAt, this.issuer, this.subject, this.schema);
    }

}
