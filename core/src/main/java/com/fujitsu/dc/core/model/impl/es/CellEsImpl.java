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
package com.fujitsu.dc.core.model.impl.es;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import net.spy.memcached.internal.CheckedOperationTimeoutException;

import org.apache.commons.lang.StringUtils;
import org.odata4j.core.OEntity;
import org.odata4j.core.OEntityKey;
import org.odata4j.expression.BoolCommonExpression;
import org.odata4j.producer.EntitiesResponse;
import org.odata4j.producer.EntityResponse;
import org.odata4j.producer.InlineCount;
import org.odata4j.producer.ODataProducer;
import org.odata4j.producer.QueryInfo;
import org.odata4j.producer.resources.OptionsQueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.auth.token.IExtRoleContainingToken;
import com.fujitsu.dc.common.auth.token.Role;
import com.fujitsu.dc.common.es.response.DcGetResponse;
import com.fujitsu.dc.common.es.response.DcSearchHit;
import com.fujitsu.dc.common.es.response.DcSearchHits;
import com.fujitsu.dc.common.es.response.DcSearchResponse;
import com.fujitsu.dc.common.es.util.IndexNameEncoder;
import com.fujitsu.dc.core.DcCoreConfig;
import com.fujitsu.dc.core.DcCoreException;
import com.fujitsu.dc.core.auth.AccessContext;
import com.fujitsu.dc.core.auth.AuthUtils;
import com.fujitsu.dc.core.event.EventBus;
import com.fujitsu.dc.core.model.Box;
import com.fujitsu.dc.core.model.BoxCmp;
import com.fujitsu.dc.core.model.Cell;
import com.fujitsu.dc.core.model.ModelFactory;
import com.fujitsu.dc.core.model.ctl.Account;
import com.fujitsu.dc.core.model.ctl.Common;
import com.fujitsu.dc.core.model.ctl.ExtCell;
import com.fujitsu.dc.core.model.ctl.ExtRole;
import com.fujitsu.dc.core.model.ctl.ReceivedMessage;
import com.fujitsu.dc.core.model.ctl.Relation;
import com.fujitsu.dc.core.model.ctl.SentMessage;
import com.fujitsu.dc.core.model.impl.es.accessor.EntitySetAccessor;
import com.fujitsu.dc.core.model.impl.es.accessor.ODataLinkAccessor;
import com.fujitsu.dc.core.model.impl.es.cache.BoxCache;
import com.fujitsu.dc.core.model.impl.es.cache.CellCache;
import com.fujitsu.dc.core.model.impl.es.doc.OEntityDocHandler;
import com.fujitsu.dc.core.model.impl.es.odata.CellCtlODataProducer;
import com.fujitsu.dc.core.odata.OEntityWrapper;

/**
 * CellのEs上での表現.
 */
public final class CellEsImpl implements Cell {
    private String id;
    private String name;
    private String url;
    private String owner;
    private Long published;
    private Map<String, Object> json;

    /**
     * Esの検索結果出力上限.
     */
    private static final int TOP_NUM = DcCoreConfig.getEsTopNum();

    /**
     * ログ.
     */
    static Logger log = LoggerFactory.getLogger(CellEsImpl.class);

    /**
     * コンストラクタ.
     */
    public CellEsImpl() {
    }

    @Override
    public EventBus getEventBus() {
        return new EventBus(this);
    }

    @Override
    public boolean isEmpty() {
        CellCtlODataProducer producer = new CellCtlODataProducer(this);
        // セル配下のボックスの存在を確認.
        QueryInfo queryInfo = new QueryInfo(InlineCount.ALLPAGES, null, null,
                null, null, null, null, null, null);
        if (producer.getEntitiesCount(Box.EDM_TYPE_NAME, queryInfo).getCount() > 0) {
            return false;
        }

        // デフォルトボックス配下にデータが無いことを確認
        Box defaultBox = this.getBoxForName(Box.DEFAULT_BOX_NAME);
        BoxCmp defaultBoxCmp = ModelFactory.boxCmp(defaultBox);
        if (!defaultBoxCmp.isEmpty()) {
            return false;
        }

        // セル管理リソースが配下に存在していない事を確認
        // TODO v1.1 性能を向上させるため、Type横断でc:（セルのuuid）の値を検索して、チェックするように変更する
        if (producer.getEntitiesCount(Account.EDM_TYPE_NAME, queryInfo).getCount() > 0
                || producer.getEntitiesCount(Role.EDM_TYPE_NAME, queryInfo).getCount() > 0
                || producer.getEntitiesCount(ExtCell.EDM_TYPE_NAME, queryInfo).getCount() > 0
                || producer.getEntitiesCount(ExtRole.EDM_TYPE_NAME, queryInfo).getCount() > 0
                || producer.getEntitiesCount(Relation.EDM_TYPE_NAME, queryInfo).getCount() > 0
                || producer.getEntitiesCount(SentMessage.EDM_TYPE_NAME, queryInfo).getCount() > 0
                || producer.getEntitiesCount(ReceivedMessage.EDM_TYPE_NAME, queryInfo).getCount() > 0) {
            return false;
        }
        // TODO v1.1 Messageが存在していたら409エラー
        return true;
    }

    @Override
    public void makeEmpty() {
        // TODO 実装
    }

    @Override
    public Box getBoxForName(String boxName) {
        if (Box.DEFAULT_BOX_NAME.equals(boxName)) {
            return new Box(this, null);
        }

        // URlに指定されたBox名のフォーマットチェックをする。不正の場合Boxが存在しないためnullを返却する
        if (!validatePropertyRegEx(boxName, Common.PATTERN_NAME)) {
            return null;
        }
        // キャッシュされたBoxの取得を試みる。
        Box cachedBox = BoxCache.get(boxName, this);
        if (cachedBox != null) {
            return cachedBox;
        }

        Box loadedBox = null;
        try {
            ODataProducer op = ModelFactory.ODataCtl.cellCtl(this);
            EntityResponse er = op.getEntity("Box", OEntityKey.create(boxName), null);
            loadedBox = new Box(this, er.getEntity());
            BoxCache.cache(loadedBox);
            return loadedBox;
        } catch (RuntimeException e) {
            if (e.getCause() instanceof CheckedOperationTimeoutException) {
                return loadedBox;
            } else {
                return null;
            }
        }
    }

    @Override
    public Box getBoxForSchema(String boxSchema) {
        ODataProducer op = ModelFactory.ODataCtl.cellCtl(this);
        BoolCommonExpression filter = OptionsQueryParser.parseFilter("Schema eq '" + boxSchema + "'");
        QueryInfo qi = QueryInfo.newBuilder().setFilter(filter).build();
        try {
            EntitiesResponse er = op.getEntities("Box", qi);
            List<OEntity> entList = er.getEntities();
            if (entList.size() != 1) {
                return null;
            }
            return new Box(this, entList.get(0));
        } catch (RuntimeException e) {
            return null;
        }
    }

    /**
     * @param uriInfo UriInfo
     * @return Cell オブジェクト 該当するCellが存在しないときはnull
     */
    public static Cell load(final UriInfo uriInfo) {
        URI reqUri = uriInfo.getRequestUri();
        URI baseUri = uriInfo.getBaseUri();

        String rPath = reqUri.getPath();
        String bPath = baseUri.getPath();
        rPath = rPath.substring(bPath.length());
        String[] paths = StringUtils.split(rPath, "/");

        return findCell("s.Name.untouched", paths[0], uriInfo);
    }

    /**
     * @param id id
     * @param uriInfo UriInfo
     * @return Cell オブジェクト 該当するCellが存在しないときはnull
     */
    public static Cell load(final String id, final UriInfo uriInfo) {
        EntitySetAccessor esCells = EsModel.cell();
        DcGetResponse resp = esCells.get(id);
        if (resp.exists()) {
            CellEsImpl ret = new CellEsImpl();
            ret.setJson(resp.getSource());
            ret.id = resp.getId();
            ret.url = getBaseUri(uriInfo, ret.name);
            return ret;
        } else {
            return null;
        }
    }

    private static String getBaseUri(final UriInfo uriInfo, String cellName) {
        // URLを生成してSet
        StringBuilder urlSb = new StringBuilder();
        UriBuilder uriBuilder = uriInfo.getBaseUriBuilder();
        uriBuilder.scheme(DcCoreConfig.getUnitScheme());
        urlSb.append(uriBuilder.build().toASCIIString());
        urlSb.append(cellName);
        urlSb.append("/");
        return urlSb.toString();
    }

    /**
     * ID 又はCell名Cellを検索しCellオブジェクトを返却する.
     * @param queryKey Cellを検索する際のキー(Cell名)
     * @param queryValue Cellを検索する際のキーに対する値
     * @param uriInfo UriInfo
     * @return Cell オブジェクト 該当するCellが存在しないとき、又はqueryKeyの値が無効な場合はnull
     */
    public static Cell findCell(String queryKey, String queryValue, UriInfo uriInfo) {
        if (!queryKey.equals("_id") && !queryKey.equals("s.Name.untouched")) {
            return null;
        }
        // URlに指定されたCell名のフォーマットチェックをする。不正の場合はCellが存在しないためnullを返却する
        if (!validatePropertyRegEx(queryValue, Common.PATTERN_NAME)) {
            return null;
        }

        EntitySetAccessor ecCells = EsModel.cell();
        CellEsImpl ret = new CellEsImpl();

        Map<String, Object> cache = CellCache.get(queryValue);
        if (cache == null) {
            Map<String, Object> source = new HashMap<String, Object>();
            Map<String, Object> filter = new HashMap<String, Object>();
            Map<String, Object> term = new HashMap<String, Object>();

            term.put(queryKey, queryValue);
            filter.put("term", term);
            source.put("query", QueryMapFactory.filteredQuery(null, filter));

            DcSearchResponse resp = ecCells.search(source);
            if ((resp == null) || (resp.getHits().getCount() == 0)) {
                return null;
            }
            DcSearchHit hit = resp.getHits().getAt(0);
            ret.setJson(hit.getSource());
            ret.id = hit.getId();

            cache = hit.getSource();
            cache.put("_id", hit.getId());
            try {
                CellCache.cache(queryValue, cache);
            } catch (RuntimeException e) {
                if (e.getCause() instanceof CheckedOperationTimeoutException) {
                    // memcachedへの接続でタイムアウトした場合はログだけ出力し、続行する
                    log.info("Faild to cache Cell info.");
                } else {
                    // その他のエラーの場合、サーバエラーとする
                    throw DcCoreException.Server.SERVER_CONNECTION_ERROR;
                }
            }
        } else {
            ret.setJson(cache);
            ret.id = (String) cache.get("_id");
        }
        ret.url = getBaseUri(uriInfo, ret.name);
        return ret;
    }

    /**
     * Mapからオブジェクトのメンバを設定する.
     * @param json 実はMap
     */
    @SuppressWarnings({"unchecked", "rawtypes" })
    public void setJson(Map json) {
        this.json = json;
        if (this.json == null) {
            return;
        }
        Map<String, String> urlJson = (Map<String, String>) json.get("s");
        Map<String, String> hJson = (Map<String, String>) json.get("h");
        this.published = (Long) json.get("p");
        this.name = urlJson.get("Name");
        this.owner = hJson.get("Owner");

    }

    @Override
    public OEntityWrapper getAccount(final String username) {
        ODataProducer op = ModelFactory.ODataCtl.cellCtl(this);
        OEntityKey key = OEntityKey.create(username);
        OEntityWrapper oew = null;
        try {
            EntityResponse resp = op.getEntity("Account", key, null);
            oew = (OEntityWrapper) resp.getEntity();
        } catch (DcCoreException dce) {
            log.debug(dce.getMessage());
        }
        return oew;
    }

    @Override
    public boolean authenticateAccount(final OEntityWrapper oew, final String password) {
        // TODO 時間をはかる攻撃（名前忘れた） に対処するため、IDがみつからなくても、無駄に処理はする。
        String cred = null;
        if (oew != null) {
            cred = (String) oew.get("HashedCredential");
        }
        String hCred = AuthUtils.hashPassword(password);
        if (hCred.equals(cred)) {
            return true;
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Role> getRoleListForAccount(final String username) {
        // Accountを取得
        EntitySetAccessor accountType = EsModel.cellCtl(this, Account.EDM_TYPE_NAME);

        List<Map<String, Object>> filters = new ArrayList<Map<String, Object>>();
        filters.add(QueryMapFactory.termQuery("s.Name.untouched", username));

        List<Map<String, Object>> queries = new ArrayList<Map<String, Object>>();
        queries.add(QueryMapFactory.termQuery("c", this.getId()));

        Map<String, Object> query = QueryMapFactory.filteredQuery(null, QueryMapFactory.mustQuery(queries));

        Map<String, Object> source = new HashMap<String, Object>();
        source.put("filter", QueryMapFactory.andFilter(filters));
        source.put("query", query);

        DcSearchHits hits = accountType.search(source).getHits();

        if (hits.getCount() == 0) {
            return null;
        }

        DcSearchHit hit = hits.getHits()[0];

        List<Role> ret = new ArrayList<Role>();
        ODataLinkAccessor links = EsModel.cellCtlLink(this);

        // アカウントに結びつくロールの検索
        List<Map<String, Object>> searchRoleQueries = new ArrayList<Map<String, Object>>();
        searchRoleQueries.add(QueryMapFactory.termQuery("t1", "Account"));
        searchRoleQueries.add(QueryMapFactory.termQuery("t2", "Role"));

        List<Map<String, Object>> searchRoleFilters = new ArrayList<Map<String, Object>>();
        searchRoleFilters.add(QueryMapFactory.termQuery("k1", hit.getId()));
        Map<String, Object> and = new HashMap<String, Object>();
        and.put("filters", searchRoleFilters);
        Map<String, Object> searchRoleFilter = new HashMap<String, Object>();
        searchRoleFilter.put("and", and);

        Map<String, Object> searchRoleSource = new HashMap<String, Object>();
        searchRoleSource.put("filter", searchRoleFilter);
        searchRoleSource.put("query", QueryMapFactory.filteredQuery(null,
                QueryMapFactory.mustQuery(searchRoleQueries)));

        // 検索結果件数設定
        searchRoleSource.put("size", TOP_NUM);

        DcSearchResponse res = links.search(searchRoleSource);
        if (res == null) {
            return ret;
        }
        DcSearchHit[] hits2 = res.getHits().getHits();
        for (DcSearchHit hit2 : hits2) {
            Map<String, Object> row = hit2.getSource();
            String role = (String) row.get("k2");
            log.debug(this.id);
            EntitySetAccessor roleDao = EsModel.cellCtl(this, Role.EDM_TYPE_NAME);
            DcGetResponse gRes = roleDao.get(role);
            if (gRes == null) {
                continue;
            }
            Map<String, Object> src = gRes.getSource();
            Map<String, Object> s = (Map<String, Object>) src.get("s");
            Map<String, Object> l = (Map<String, Object>) src.get("l");
            String roleName = (String) s.get(KEY_NAME);
            String boxId = (String) l.get("Box");
            String boxName = null;
            String schema = null;
            if (boxId != null) {
                // Boxの検索
                EntitySetAccessor box = EsModel.box(this);
                DcGetResponse getRes = box.get(boxId);
                if (getRes == null || !getRes.isExists()) {
                    continue;
                }
                Map<String, Object> boxsrc = getRes.getSource();
                Map<String, Object> boxs = (Map<String, Object>) boxsrc.get("s");
                boxName = (String) boxs.get(KEY_NAME);
                schema = (String) boxs.get(KEY_SCHEMA);
            }
            Role roleObj = new Role(roleName, boxName, schema);

            ret.add(roleObj);
        }
        return ret;
    }

    @Override
    public List<Role> getRoleListHere(final IExtRoleContainingToken token) {
        List<Role> ret = new ArrayList<Role>();

        // ExtCellとRoleの結びつけ設定から払い出すRoleをリストアップ
        this.addRoleListExtCelltoRole(token, ret);

        // ExtCellとRelationとRoleの結びつけから払い出すRoleをリストアップ
        // と
        // ExtCellとRelationとExtRoleとRoleの結びつけから払い出すRoleをリストアップ
        this.addRoleListExtCelltoRelationAndExtRole(token, ret);

        return ret;
    }

    /**
     * ExtCellとRoleの突き合わせを行い払い出すRoleを決める.
     * @param token トランスセルアクセストークン
     * @param roles 払い出すロールのリスト。ここに追加する（破壊的メソッド）
     */
    private void addRoleListExtCelltoRole(final IExtRoleContainingToken token, List<Role> roles) {
        // ExtCell-Role結びつけに対応するRoleの取得
        String extCell = token.getExtCellUrl();
        String principal = token.getSubject();
        String principalCell;
        if (principal.contains("#")) {
            principalCell = token.getSubject().substring(0, principal.indexOf("#"));
        } else {
            principalCell = token.getSubject();
        }

        // アクセス主体がExtCellと異なる場合（2段階以上のトランスセルトークン認証）は許さない。
        if (extCell.equals(principalCell)) {
            ODataProducer op = ModelFactory.ODataCtl.cellCtl(this);
            EntitiesResponse response = null;
            // 検索結果出力件数設定
            QueryInfo qi = QueryInfo.newBuilder().setTop(TOP_NUM).setInlineCount(InlineCount.NONE).build();
            try {
                // ExtCell-Roleのリンク情報取得
                response = (EntitiesResponse) op.getNavProperty(ExtCell.EDM_TYPE_NAME, OEntityKey.create(extCell), "_"
                        + Role.EDM_TYPE_NAME, qi);
            } catch (DcCoreException dce) {
                if (DcCoreException.OData.NO_SUCH_ENTITY != dce) {
                    throw dce;
                }
            }
            if (response == null) {
                return;
            }

            // ExtCell-Roleのリンク情報をすべて見て今回アクセスしてきたセル向けのロールを洗い出す。
            List<OEntity> entList = response.getEntities();
            for (OEntity ent : entList) {
                OEntityWrapper entRole = (OEntityWrapper) ent;
                this.addRole(entRole.getUuid(), roles);
            }
        }
    }

    /**
     * ExtCellとRelationとRoleの結びつけから払い出すRoleをリストアップ.
     * と
     * ExtCellとRelationとExtRoleとRoleの結びつけから払い出すRoleをリストアップ.
     * @param token トランスセルアクセストークン
     * @param roles 払い出すロールのリスト。ここに追加する（破壊的メソッド）
     */
    @SuppressWarnings("unchecked")
    private void addRoleListExtCelltoRelationAndExtRole(final IExtRoleContainingToken token, List<Role> roles) {
        String extCell = token.getExtCellUrl();

        // ExtCell-Role結びつけに対応するRoleの取得
        ODataProducer op = ModelFactory.ODataCtl.cellCtl(this);
        EntitiesResponse response = null;
        try {
            // 検索結果出力件数設定
            QueryInfo qi = QueryInfo.newBuilder().setTop(TOP_NUM).setInlineCount(InlineCount.NONE).build();
            // ExtCell-Relationのリンク情報取得
            response = (EntitiesResponse) op.getNavProperty(ExtCell.EDM_TYPE_NAME,
                    OEntityKey.create(extCell), "_" + Relation.EDM_TYPE_NAME, qi);
        } catch (DcCoreException dce) {
            if (DcCoreException.OData.NO_SUCH_ENTITY != dce) {
                throw dce;
            }
        }
        if (response == null) {
            return;
        }

        List<OEntity> entList = response.getEntities();
        for (OEntity ent : entList) {
            OEntityWrapper entRelation = (OEntityWrapper) ent;

            // ExtCell-Relationのリンク情報をすべて見て今回アクセスしてきたセル向けのロールを洗い出す。
            DcSearchResponse res = serchRoleLinks(Relation.EDM_TYPE_NAME, entRelation.getUuid());
            if (res == null) {
                continue;
            }
            this.addRoles(res.getHits().getHits(), roles);
            // ↑ ここまででExtCellとRelationとRoleの結びつけから払い出すRoleをリストアップ.は完了
            // ↓ こっからはExtCellとRelationとExtRoleとRoleの結びつけから払い出すRoleをリストアップの処理.

            // RelationからExtRoleの情報取得。
            EntitySetAccessor extRoleType = EsModel.cellCtl(this, ExtRole.EDM_TYPE_NAME);

            // Relationに結びつくExtRoleの検索
            // 現在の登録件数を取得してから一覧取得する
            Map<String, Object> source = new HashMap<String, Object>();

            // 暗黙フィルタを指定して、検索対象を検索条件の先頭に設定する（絞りこみ）
            List<Map<String, Object>> implicitFilters =
                    QueryMapFactory.getImplicitFilters(this.id, null, null, null, extRoleType.getType());
            String linksKey = OEntityDocHandler.KEY_LINK + "." + Relation.EDM_TYPE_NAME;
            implicitFilters.add(0, QueryMapFactory.termQuery(linksKey, entRelation.getUuid()));
            Map<String, Object> query = QueryMapFactory.mustQuery(implicitFilters);
            Map<String, Object> filteredQuery = QueryMapFactory.filteredQuery(null, query);
            source.put("query", filteredQuery);
            long hitNum = extRoleType.count(source);
            // ExtCellの設定が存在しないときは飛ばす
            if (hitNum == 0) {
                continue;
            }
            source.put("size", hitNum);

            DcSearchHits extRoleHits = extRoleType.search(source).getHits();
            // ExtCellの設定が存在しないときは飛ばす
            // 件数取得後に削除される場合があるため、検索結果を再度確認しておく
            if (extRoleHits.getCount() == 0) {
                continue;
            }
            for (DcSearchHit extRoleHit : extRoleHits.getHits()) {
                Map<String, Object> extRoleSource = extRoleHit.getSource();
                Map<String, Object> extRoleS = (Map<String, Object>) extRoleSource.get("s");
                String esExtRole = (String) extRoleS.get(ExtRole.EDM_TYPE_NAME);

                // トークンに入ってるロールと突き合わせ
                for (Role tokenRole : token.getRoleList()) {
                    if (!tokenRole.createUrl().equals(esExtRole)) {
                        continue;
                    }
                    // ExtCell-Roleのリンク情報をすべて見て今回アクセスしてきたセル向けのロールを洗い出す。
                    DcSearchResponse resExtRoleToRole = serchRoleLinks(ExtRole.EDM_TYPE_NAME, extRoleHit.getId());
                    if (resExtRoleToRole == null) {
                        continue;
                    }
                    this.addRoles(resExtRoleToRole.getHits().getHits(), roles);
                }
            }
        }
    }

    /**
     * Roleと他のエンティテセットのリンクテーブルから対応するデータを取得する.
     * @param searchKey 検索条件のエンティティセット名
     * @param searchValue 検索するuuid
     * @return 検索結果
     */
    private DcSearchResponse serchRoleLinks(final String searchKey, final String searchValue) {

        ODataLinkAccessor links = EsModel.cellCtlLink(this);
        // Relationに結びつくロールの検索
        Map<String, Object> source = new HashMap<String, Object>();
        Map<String, Object> filter = new HashMap<String, Object>();
        Map<String, Object> and = new HashMap<String, Object>();
        List<Map<String, Object>> filters = new ArrayList<Map<String, Object>>();

        List<Map<String, Object>> queries = new ArrayList<Map<String, Object>>();
        queries.add(QueryMapFactory.termQuery("t1", searchKey));
        queries.add(QueryMapFactory.termQuery("t2", "Role"));

        Map<String, Object> query = QueryMapFactory.filteredQuery(null, QueryMapFactory.mustQuery(queries));

        filters.add(QueryMapFactory.termQuery("k1", searchValue));
        and.put("filters", filters);
        filter.put("and", and);
        source.put("filter", filter);
        source.put("query", query);
        // 検索結果件数設定
        source.put("size", TOP_NUM);

        return links.search(source);
    }

    /**
     * Roleが含まれたSearchHitの配列からロールの値を取得する.
     * @param hits Roleを検索し結果
     * @param roles 払い出すロールのリスト。ここに追加する（破壊的メソッド）
     */
    private void addRoles(DcSearchHit[] hits, List<Role> roles) {
        for (DcSearchHit hit : hits) {
            Map<String, Object> src = hit.getSource();
            String roleUuid = (String) src.get("k2");

            // Relation-Roleのリンク情報をすべて見て今回アクセスしてきたセル向けのロールを洗い出す。
            this.addRole(roleUuid, roles);
        }
    }

    /**
     * ロールの値を取得する.
     * @param uuid RoleのUUID
     * @param roles 払い出すロールのリスト。ここに追加する（破壊的メソッド）
     */
    @SuppressWarnings("unchecked")
    private void addRole(String uuid, List<Role> roles) {
        EntitySetAccessor roleDao = EsModel.cellCtl(this, Role.EDM_TYPE_NAME);
        DcGetResponse gRes = roleDao.get(uuid);
        if (gRes == null) {
            return;
        }
        Map<String, Object> src = gRes.getSource();
        Map<String, Object> s = (Map<String, Object>) src.get("s");
        Map<String, Object> l = (Map<String, Object>) src.get("l");
        String roleName = (String) s.get(KEY_NAME);
        String schema = (String) s.get(KEY_SCHEMA);
        String boxId = (String) l.get(Box.EDM_TYPE_NAME);
        String boxName = null;
        if (boxId != null) {
            // Boxの検索
            Map<String, Object> boxsrc = DavCmpEsImpl.searchBox(this, boxId);
            Map<String, Object> boxs = (Map<String, Object>) boxsrc.get("s");
            boxName = (String) boxs.get(KEY_NAME);
        }

        roles.add(new Role(roleName, boxName, schema, this.url));
    }

    @Override
    public String getOwner() {
        return this.owner;
    }

    @Override
    public String getUnitUserNameWithOutPrefix() {
        String unitUserName;
        if (this.owner == null) {
            unitUserName = AccessContext.TYPE_ANONYMOUS;
        } else {
            unitUserName = IndexNameEncoder.encodeEsIndexName(owner);
        }
        return unitUserName;
    }

    @Override
    public String getUnitUserName() {
        String unitUserName = DcCoreConfig.getEsUnitPrefix() + "_" + getUnitUserNameWithOutPrefix();
        return unitUserName;
    }

    /**
     * Cell名を取得します.
     * @return Cell名
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * このCellの内部IDを返します.
     * @return 内部ID文字列
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * このCellの内部IDを設定します.
     * @param id 内部ID文字列
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * このCellのURLを返します.
     * @return URL文字列
     */
    @Override
    public String getUrl() {
        return this.url;
    }

    /**
     * このCellのURLを設定します.
     * @param url URL文字列
     */
    public void setUrl(String url) {
        this.url = url;
    }

    static final String KEY_NAME = "Name";
    static final String KEY_SCHEMA = "Schema";

    /**
     * プロパティ項目の値を正規表現でチェックする.
     * @param propValue プロパティ値
     * @param dcFormat dcFormatの値
     * @return フォーマットエラーの場合、falseを返却
     */
    private static boolean validatePropertyRegEx(String propValue, String dcFormat) {
        // フォーマットのチェックを行う
        Pattern pattern = Pattern.compile(dcFormat);
        Matcher matcher = pattern.matcher(propValue);
        if (!matcher.matches()) {
            return false;
        }
        return true;
    }

    /**
     * Cellの作成時間を返却する.
     * @return Cellの作成時間
     */
    public long getPublished() {
        return this.published;
    }
}
