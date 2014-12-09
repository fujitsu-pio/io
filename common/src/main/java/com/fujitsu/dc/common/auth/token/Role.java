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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Roleの定義体.
 */
public class Role {
    /**
     * Edm EntityType名.
     */
    public static final String EDM_TYPE_NAME = "Role";

    /**
     * ロールの名称.
     */
    private String name;
    /**
     * ロールの属するボックスオブジェクト.
     */
    private String boxName;
    /**
     * ロールの属するボックスのSchema.
     */
    private String boxSchema;
    /**
     * ロールリソースのベースURL.
     */
    private String baseUrl;

    /**
     * コンストラクタ.
     * @param url ロールリソースのURL
     * @throws MalformedURLException URLが不正な場合に発生
     */
    public Role(URL url) throws MalformedURLException {
        // ロールリソースのURL↓
        // https://localhost:8080/dc1-core/testcell1/__role/box1/rolename
        Pattern pattern = Pattern.compile("(.+/)__role/([^/]+)/(.+)");
        Matcher matcher = pattern.matcher(url.toString());
        if (!matcher.find()) {
            throw new MalformedURLException("No match found.");
        }
        this.name = matcher.group(INDEX_ROLE_URL_ROLE_NAME);
        this.boxName = matcher.group(INDEX_ROLE_URL_BOX_NAME);
        this.baseUrl = matcher.group(INDEX_ROLE_URL_BASE);
    }

    /**
     * コンストラクタ.
     * @param name ロール名.
     * @param boxName ロールの属するボックス名.
     * @param boxSchema ロールの属するボックスのSchema
     * @param baseUrl ロールの属するセルのURL
     */
    public Role(final String name, final String boxName, final String boxSchema, final String baseUrl) {
        this.name = name;
        this.boxName = boxName;
        this.boxSchema = boxSchema;
        this.baseUrl = baseUrl;
    }

    /**
     * コンストラクタ.
     * @param name ロール名.
     * @param boxName ロールの属するボックス名.
     * @param boxSchema ロールの属するボックスのSchema
     */
    public Role(final String name, final String boxName, final String boxSchema) {
        this(name, boxName, boxSchema, null);
    }

    /**
     * コンストラクタ.
     * @param name ロール名.
     * @param boxName ロールの属するボックス名.
     */
    public Role(final String name, final String boxName) {
        this(name, boxName, null, null);
    }

    /**
     * コンストラクタ.
     * @param name ロール名.
     */
    public Role(final String name) {
        this(name, null, null, null);
    }

    /**
     * スキーマ用ロールリソースのURLを返す.
     * @param url ロールリソースのベースURL
     * @return String ロールリソースのURL
     */
    public String schemeCreateUrl(String url) {
        // ロールに紐付くBox判断
        String boxName2 = null;
        if (this.boxName != null) {
            boxName2 = this.boxName;
        } else {
            // 紐付かない場合、デフォルトボックス名を使用する
            boxName2 = DEFAULT_BOX_NAME;
        }
        String url3 = createBaseUrl(url);
        return String.format(ROLE_RESOURCE_FORMAT, url3, boxName2, this.name);
    }

    /**
     * ロールクラスURLを返す.
     * @param url ロールリソースのベースURL
     * @return String ロールリソースのURL
     */
    public String schemeCreateUrlForTranceCellToken(String url) {
        String url3 = createBaseUrl(url);
        return String.format(ROLE_RESOURCE_FORMAT, url3, DEFAULT_BOX_NAME, this.name);
    }

    /**
     * BaseURLを作成する.
     * @param url ロールリソースのベースURL
     * @return String ロールのベースURL
     */
    private String createBaseUrl(String url) {
        String url2 = null;
        if (this.boxName != null && this.boxSchema != null && !"null".equals(this.boxSchema)) {
            // BOXに紐付いている場合BOXに設定されているスキーマURLをBaseURLに使う
            // なお、BOXにスキーマURLが設定されていない場合は設定ミスの可能性があるので紐付いていないとみなす。
            url2 = this.boxSchema;
        } else {
            // BOXに紐付いていない場合ISSUERをBaseURLに使う
            url2 = url;
        }
        // 連結でスラッシュつけてるので、URLの最後がスラッシュだったら消す。
        String url3 = url2.replaceFirst("/$", "");
        return url3;
    }

    /**
     * ローカル用ロールリソースのURLを返す.
     * @param url ロールリソースのベースURL
     * @return String ロールリソースのURL
     */
    public String localCreateUrl(String url) {
        // ロールに紐付くBox判断
        String boxName2 = null;
        if (this.boxName != null) {
            boxName2 = this.boxName;
        } else {
            // 紐付かない場合、デフォルトボックス名を使用する
            boxName2 = DEFAULT_BOX_NAME;
        }
        // 連結でスラッシュつけてるので、URLの最後がスラッシュだったら消す。
        String url3 = url.replaceFirst("/$", "");
        return String.format(ROLE_RESOURCE_FORMAT, url3, boxName2, this.name);
    }

    /**
     * ロールリソースのURLを返す.
     * @return String ロールリソースのURL
     */
    public String createUrl() {
        return schemeCreateUrl(this.baseUrl);
    }

    /**
     * ロール名を取得する.
     * @return name ロール名
     */
    public String getName() {
        return name;
    }

    /**
     * 属するボックス名を取得する.
     * @return boxName ボックス名.
     */
    public String getBoxName() {
        return boxName;
    }

    /**
     * 属するボックスのスキーマ名を取得する.
     * @return boxSchema スキーマ名.
     */
    public String getBoxSchema() {
        return boxSchema;
    }

    /**
     * 属するBaseUrlを取得する.
     * @return baseUrl ベースURL.
     */
    public String getBaseUrl() {
        return baseUrl;
    }


    private static final int INDEX_ROLE_URL_BASE = 1;
    private static final int INDEX_ROLE_URL_BOX_NAME = 2;
    private static final int INDEX_ROLE_URL_ROLE_NAME = 3;
    /**
     * RoleリソースURLフォーマット.
     */
    public static final String ROLE_RESOURCE_FORMAT = "%s/__role/%s/%s";
    /**
     * デフォルトボックス名.
     */
    public static final String DEFAULT_BOX_NAME = "__";

}
