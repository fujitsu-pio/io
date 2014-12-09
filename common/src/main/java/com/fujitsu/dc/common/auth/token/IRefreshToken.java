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


/**
 *  DC1で用いるOAuth2.0リフレッシュトークンのインターフェース.
 */
public interface IRefreshToken {
    /**
     * IDを返します.
     * @return ID文字列
     */
    String getId();

    /**
     * Schema URL(OAuth2 クライアント認証のClientId相当）を返します.
     * @return Schema URL
     */
    String getSchema();

    /**
     * Subject文字列を返します.
     * @return Subject文字列
     */
    String getSubject();

    /**
     * トークン文字列を返します.
     * @return トークン文字列
     */
    String toTokenString();

    /**
     * このトークンを使って、新たにアクセストークンをリフレッシュ生成する.
     * @param issuedAt 発行時刻
     * @param target 宛先Cell URL
     * @param cellUrl 発行主体CellUrl
     * @param roleList ロールのリスト
     * @return アクセストークン
     */
    IAccessToken refreshAccessToken(long issuedAt, String target, final String cellUrl, List<Role> roleList);

    /**
     * このトークンを使って、新たにリフレッシュトークンをリフレッシュ生成する.
     * @param issuedAt 発行時刻
     * @return リフレッシュトークン
     */
    IRefreshToken refreshRefreshToken(long issuedAt);

    /**
     *  リフレッシュトークン失効までの秒数を返します.
     * @return 失効までの秒数
     */
    int refreshExpiresIn();
}
