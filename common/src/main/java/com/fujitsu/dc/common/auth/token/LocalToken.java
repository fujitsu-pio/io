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
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;

import com.fujitsu.dc.common.utils.DcCoreUtils;

/**
 * Cell Local Token の生成・パースを行うクラス.
 */
public abstract class LocalToken extends AbstractOAuth2Token {

    /**
     * AES/CBC/PKCS5Padding.
     */
    public static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";
    private static final String SEPARATOR = "\t";
    static final int IV_BYTE_LENGTH = 16;

    private static byte[] keyBytes;
    private static SecretKey aesKey;

    /**
     * Key文字列を設定します。
     * @param keyString キー文字列.
     */
    public static void setKeyString(String keyString) {
        keyBytes = keyString.getBytes(); // 16/24/32バイトの鍵バイト列
        aesKey = new SecretKeySpec(keyBytes, "AES");
    }

    /**
     * 明示的な有効期間を設定してトークンを生成する.
     * @param issuedAt 発行時刻(epochからのミリ秒)
     * @param lifespan 有効時間(ミリ秒)
     * @param issuer 発行者
     * @param subject 主体
     * @param schema スキーマ
     */
    public LocalToken(final long issuedAt, final long lifespan, final String issuer,
             final String subject, final String schema) {
        this.issuedAt = issuedAt;
        this.lifespan = lifespan;
        this.issuer = issuer;
        this.subject = subject;
        this.schema = schema;
    }

    final String doCreateTokenString(final String[] contents) {
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
        if (this.schema != null) {
            raw.append(this.schema);
        }

        if (contents != null) {
            for (String cont : contents) {
                raw.append(SEPARATOR);
                if (cont != null) {
                    raw.append(cont);
                }
            }
        }

        raw.append(SEPARATOR);
        raw.append(this.issuer);
        return encode(raw.toString(), getIvBytes(issuer));
    }

    /**
     * 指定のIssuer向けのIV(Initial Vector)を生成して返します.
     * IVとしてissuerの最後の最後の１６文字を逆転させた文字列を用います。 これにより、違うIssuerを想定してパースすると、パースに失敗する。
     * @param issuer Issuer URL
     * @return Initial Vectorの Byte配列
     */
    protected static byte[] getIvBytes(final String issuer) {
        try {
            return StringUtils.reverse("123456789abcdefg" + issuer)
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
        String tokenDecoded = decode(token, getIvBytes(issuer));

        String[] frag = tokenDecoded.split(SEPARATOR);

        // 正常な形式でなければパース失敗とする
        if (frag.length != numFields || !issuer.equals(frag[numFields - 1])) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        }
        return frag;
    }

    /**
     * 文字列を暗号化する.
     * @param in 入力文字列
     * @param ivBytes イニシャルベクトル
     * @return 暗号化された文字列
     */
    public static String encode(final String in, final byte[] ivBytes) {
        // IVに、発行CELLのURL逆順を入れることで、より短いトークンに。
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, aesKey, new IvParameterSpec(ivBytes));
            byte[] cipherBytes = cipher.doFinal(in.getBytes(CharEncoding.UTF_8));
            return DcCoreUtils.encodeBase64Url(cipherBytes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 復号する.
     * @param in 暗号化文字列
     * @param ivBytes イニシャルベクトル
     * @return 復号された文字列
     * @throws AbstractOAuth2Token.TokenParseException 例外
     */
    public static String decode(final String in, final byte[] ivBytes) throws AbstractOAuth2Token.TokenParseException {
        byte[] inBytes = DcCoreUtils.decodeBase64Url(in);
        Cipher cipher;
        try {
            cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
        } catch (NoSuchAlgorithmException e) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        } catch (NoSuchPaddingException e) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, aesKey, new IvParameterSpec(ivBytes));
        } catch (InvalidKeyException e) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        } catch (InvalidAlgorithmParameterException e) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        }
        byte[] plainBytes;
        try {
            plainBytes = cipher.doFinal(inBytes);
        } catch (IllegalBlockSizeException e) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        } catch (BadPaddingException e) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        }
        try {
            return new String(plainBytes, CharEncoding.UTF_8);
        } catch (UnsupportedEncodingException e) {
            throw AbstractOAuth2Token.PARSE_EXCEPTION;
        }
    }

}
