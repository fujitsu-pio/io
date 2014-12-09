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
package com.fujitsu.dc.core.model;

import java.util.Collections;
import java.util.List;

import org.core4j.Enumerable;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;

import com.fujitsu.dc.common.auth.token.IExtRoleContainingToken;
import com.fujitsu.dc.common.auth.token.Role;
import com.fujitsu.dc.core.event.EventBus;
import com.fujitsu.dc.core.model.ctl.Common;
import com.fujitsu.dc.core.odata.OEntityWrapper;

import java.util.Arrays;

/**
 * Cellのモデルクラス.
 */
public interface Cell {

    /**
     * Cell名を取得します.
     * @return Cell名
     */
    String getName();

    /**
     * このCellの内部IDを返します.
     * @return 内部ID文字列
     */
    String getId();

    /**
     * このCellのURLを返します.
     * @return URL文字列
     */
    String getUrl();

    /**
     * CellのOwner Unit User URIを取得します.
     * @return Cell名
     */
    String getOwner();

    /**
     * CellのプレフィックスなしのUnit User名を取得します.
     * @return .
     */
    String getUnitUserNameWithOutPrefix();

    /**
     * CellのUnit User名を取得します.
     * @return ユニットユーザ名
     */
    String getUnitUserName();

    /**
     * CellのEventBusを取得します.
     * @return EventBus
     */
    EventBus getEventBus();

    /**
     * Cellの作成時間を取得します.
     * @return EventBus
     */
    long getPublished();

    /**
     * 配下にデータや制御オブジェクト(Box,Account等)がない場合はtrueを返す.
     * デフォルトボックスはあってもよい。
     * @return 配下にデータや制御オブジェクト(Box,Account等)がない場合はtrue.
     */
    boolean isEmpty();

    /**
     * 配下にあるデータや制御オブジェクト(Box,Account等)をすべて削除する.
     */
    void makeEmpty();

    /**
     * Box名を指定してBoxを取得します.
     * @param boxName Box名
     * @return Box
     */
    Box getBoxForName(String boxName);

    /**
     * Box名を指定してBoxを取得します.
     * @param boxSchema box schema uri
     * @return Box
     */
    Box getBoxForSchema(String boxSchema);

    /**
     * Account名を指定してAccountを取得します.
     * @param username Account名
     * @return Account
     */
    OEntityWrapper getAccount(final String username);

    /**
     * @param oew account
     * @param password password
     * @return true if authentication is successful.
     */
    boolean authenticateAccount(final OEntityWrapper oew, String password);

    // public abstract void createAccount(String username, String schema) throws Cell.ManipulationException;
    // public abstract void createConnector(String name, String schema) throws Cell.ManipulationException;
    /**
     * @param username access account id
     * @return List of Roles
     */
    List<Role> getRoleListForAccount(String username);

    /**
     * このCellで与えられるべきロールリストを返します.
     * @param token トランスセルアクセストークン
     * @return ロールリスト
     */
    List<Role> getRoleListHere(IExtRoleContainingToken token);

    // スキーマ情報

    /**
     * Edm.Entity Type名.
     */
    String EDM_TYPE_NAME = "Cell";

    /**
     * Nameプロパティの定義体.
     */
    EdmProperty.Builder P_PATH_NAME = EdmProperty.newBuilder("Name")
            .setNullable(false)
            .setAnnotations(Common.DC_FORMAT_NAME)
            .setType(EdmSimpleType.STRING);

    /**
     * プロパティ一覧.
     */
    List<EdmProperty.Builder> PROPS = Collections.unmodifiableList(Arrays.asList(
            new EdmProperty.Builder[] {
                    P_PATH_NAME, Common.P_PUBLISHED, Common.P_UPDATED}
            ));
    /**
     * キー一覧.
     */
    List<String> KEYS = Collections.unmodifiableList(Arrays.asList(
            new String[] {P_PATH_NAME.getName()}
            ));;

    /**
     * Cellのエンティティタイプビルダー.
     */
    EdmEntityType.Builder EDM_TYPE_BUILDER = EdmEntityType.newBuilder().setNamespace(Common.EDM_NS_UNIT_CTL)
            .setName(EDM_TYPE_NAME).addProperties(Enumerable.create(PROPS).toList()).addKeys(KEYS);
}
