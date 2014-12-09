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

import java.io.UnsupportedEncodingException;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

/**
 * Unit Local Unit UserToken の生成・パースを行うクラス.
 */
public class UnitLocalUnitUserToken extends AbstractOAuth2Token implements IAccessToken {

    /**
     * トークンのプレフィックス.
     */
    public static final String PREFIX_UNIT_LOCAL_UNIT_USER = "AU~";
    /**
     * トークンの有効時間.
     */
    public static final int ACCESS_TOKEN_EXPIRES_HOUR = 1;

    /**
     * AES/CBC/PKCS5Padding.
     */
    private static final String SEPARATOR = "\t";
    static final int IV_BYTE_LENGTH = 16;

    /**
     * 明示的な有効期間を設定してトークンを生成する.
     * @param issuedAt 発行時刻(epochからのミリ秒)
     * @param lifespan 有効時間(ミリ秒)
     * @param subject ユニットユーザ名
     * @param issuer 発行者(自ホスト名)
     */
    public UnitLocalUnitUserToken(final long issuedAt, final long lifespan, final String subject, final String issuer) {
        this.issuedAt = issuedAt;
        this.lifespan = lifespan;
        this.subject = subject;
        this.issuer = issuer;
    }

    final String doCreateTokenString() {
        StringBuilder raw = new StringBuilder();

        // 発行時刻のEpochからのミリ秒を逆順にした文字列が先頭から入るため、推測しづらい。
        String iaS = Long.toString(this.issuedAt);
        String iaSr = StringUtils.reverse(iaS);
        raw.append(iaSr);

        raw.append(SEPARATOR);
        raw.append(Long.toString(this.lifespan));
        raw.append(SEPARATOR);
        raw.append(this.subject);
        raw.append(SEPARATOR);
        raw.append(this.issuer);
        return LocalToken.encode(raw.toString(), getIvBytes(issuer));
    }

    /**
     * 指定のIssuer向けのIV(Initial Vector)を生成して返します.
     * IVとしてissuerの最初の１６文字を用います。 これにより、違うIssuerを想定してパースすると、パースに失敗する。
     * @param issuer Issuer URL
     * @return Initial Vectorの Byte配列
     */
    public static byte[] getIvBytes(final String issuer) {
        try {
            return (issuer + "123456789abcdefg")
                    .substring(0, IV_BYTE_LENGTH).getBytes(CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * パース処理.
     * @param token トークン
     * @param issuer 発行者
     * @param numFields フィールド数
     * @return パースされたトークン
     * @throws AbstractOAuth2Token.TokenParseException トークン解釈に失敗したとき
     */
    static String[] doParse(final String token, final String issuer,
      final int numFields) throws AbstractOAuth2Token.TokenParseException {
        String tokenDecoded = LocalToken.decode(token, getIvBytes(issuer));

        String[] frag = tokenDecoded.split(SEPARATOR);

        // 正常な形式でなければパース失敗とする
        if (frag.length != numFields || !issuer.equals(frag[numFields - 1])) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        }
        return frag;
    }

    static final int IDX_COUNT = 4;
    static final int IDX_ISSUED_AT = 0;
    static final int IDX_LIFESPAN = 1;
    static final int IDX_SUBJECT = 2;
    static final int IDX_ISSUER = 3;
    /**
     * トークン文字列をissuerで指定されたCellとしてパースする.
     * @param token Token String
     * @param issuer Cell Root URL
     * @return パースされたCellLocalTokenオブジェクト
     * @throws AbstractOAuth2Token.TokenParseException トークンのパースに失敗したとき投げられる例外
     */
    public static UnitLocalUnitUserToken parse(final String token, final String issuer)
            throws AbstractOAuth2Token.TokenParseException {
        if (!token.startsWith(PREFIX_UNIT_LOCAL_UNIT_USER) || issuer == null) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        }
        String[] frag = doParse(token.substring(PREFIX_UNIT_LOCAL_UNIT_USER.length()), issuer, IDX_COUNT);

        try {
            UnitLocalUnitUserToken ret = new UnitLocalUnitUserToken(
                    Long.valueOf(StringUtils.reverse(frag[IDX_ISSUED_AT])),
                    Long.valueOf(frag[IDX_LIFESPAN]),
                    frag[IDX_SUBJECT],
                    frag[IDX_ISSUER]);
            return ret;
        } catch (Exception e) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        }
    }

    @Override
    public String toTokenString() {
        StringBuilder ret = new StringBuilder(PREFIX_UNIT_LOCAL_UNIT_USER);
        ret.append(this.doCreateTokenString());
        return ret.toString();
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
