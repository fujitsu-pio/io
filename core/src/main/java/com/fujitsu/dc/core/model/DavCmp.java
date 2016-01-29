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

import java.io.InputStream;
import java.io.Reader;
import java.util.List;

import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.wink.webdav.model.Multistatus;
import org.apache.wink.webdav.model.Propertyupdate;
import org.apache.wink.webdav.model.Propfind;

import com.fujitsu.dc.core.DcCoreException;
import com.fujitsu.dc.core.model.jaxb.Acl;
import com.fujitsu.dc.core.odata.DcODataProducer;

/**
 * JaxRS Resource オブジェクトから処理の委譲を受けてDav関連の永続化処理を行うインターフェース.
 */
public interface DavCmp {
    /**
     * 存在しないパスを表すType.
     */
    String TYPE_NULL = "null";
    /**
     * プレーンなWebDAVコレクションを表すType.
     */
    String TYPE_COL_WEBDAV = "col.webdav";
    /**
     * ODataSvcとして拡張されたWebDAVコレクションを表すType.
     */
    String TYPE_COL_ODATA = "col.odata";
    /**
     * Boxとして拡張されたWebDAVコレクションを表すType.
     */
    String TYPE_COL_BOX = "col.box";
    /**
     * Engine Serviceとして拡張されたWebDAVコレクションを表すType.
     */
    String TYPE_COL_SVC = "col.svc";
    /**
     * WebDAVファイルを表すType.
     */
    String TYPE_DAV_FILE = "dav.file";
    /**
     * Cellを表すType.
     */
    String TYPE_CELL = "Cell";

    /**
     * サービスのソースコレクション.
     */
    String SERVICE_SRC_COLLECTION = "__src";

    /**
     * DavNodeがDB上に存在するかどうか.
     * @return 存在する場合はtrue
     */
    boolean isExists();

    /**
     * Davの管理データ情報を最新化する.
     */
    void load();

    /**
     * Davの管理データ情報を最新化する.<br />
     * 管理データが存在しない場合はエラーとする.
     */
    void loadAndCheckDavInconsistency();

    /**
     * ACLのgetter.
     * @return acl
     */
    Acl getAcl();

    /**
     * スキーマ認証レベル設定のgetter.
     * @return スキーマ認証レベル
     */
    String getConfidentialLevel();

    /**
     * ユニット昇格許可ユーザ設定取得のgetter.
     * @return ユニット昇格許可ユーザ設定
     */
    List<String> getOwnerRepresentativeAccounts();

    /**
     * 指定した名前の子パスを担当する部品を返す.
     * @param name 子供パスのパスコンポーネント名
     * @return 子パスを担当する部品
     */
    DavCmp getChild(String name);

    /**
     * 親パスを担当する部品を返す.
     * @return 親パスを担当する部品
     */
    DavCmp getParent();

    /**
     * 子供パスの部品の数を返す.
     * @return 子供パスの部品の数
     */
    int getChildrenCount();

    /**
     * タイプ文字列を返す.
     * @return タイプ文字列
     */
    String getType();

    /**
     * このオブジェクトが担当するパス文字列を返す.
     * @return このオブジェクトが担当するパス文字列
     */
    String getName();

    /**
     * このオブジェクトのboxIdを返す.
     * @return nodeId
     */
    String getBoxId();

    /**
     * このオブジェクトのnodeIdを返す.
     * @return nodeId
     */
    String getNodeId();

    /**
     * 配下にデータがない場合はtrueを返す.
     * @return 配下にデータがない場合はtrue.
     */
    boolean isEmpty();

    /**
     * 配下にあるデータをすべて削除する.
     */
    void makeEmpty();

    /**
     * MKCOLメソッドの処理.
     * @param type タイプ
     * @return JAX-RS ResponseBuilder
     */
    ResponseBuilder mkcol(String type);

    /**
     * ACLメソッドの処理.
     * @param reader Reader
     * @return JAX-RS ResponseBuilder
     */
    ResponseBuilder acl(Reader reader);

    /**
     * PUTメソッドによるファイルの更新処理.
     * @param contentType Content-Typeヘッダ
     * @param inputStream リクエストボディ
     * @param etag Etag
     * @return JAX-RS ResponseBuilder
     */
    ResponseBuilder putForUpdate(String contentType, InputStream inputStream, String etag);

    /**
     * PUTメソッドによるファイルの作成処理.
     * @param contentType Content-Typeヘッダ
     * @param inputStream リクエストボディ
     * @return JAX-RS ResponseBuilder
     */
    ResponseBuilder putForCreate(String contentType, InputStream inputStream);

    /**
     * 子リソースとの紐づける.
     * @param name 子リソースのパスコンポーネント名
     * @param nodeId 子リソースのノードID
     * @param asof 更新時刻として残すべき時刻
     * @return JAX-RS ResponseBuilder
     */
    ResponseBuilder linkChild(String name, String nodeId, Long asof);

    /**
     * 子リソースとの紐づきを削除する.
     * @param name 子リソース名
     * @param asof 削除時刻として残すべき時刻
     * @return JAX-RS ResponseBuilder
     */
    ResponseBuilder unlinkChild(String name, Long asof);

    /**
     * PROPFINDメソッドの処理.
     * @param propfind Propfind要求オブジェクト
     * @param depth Depthヘッダ
     * @param url URL
     * @param isAclRead ACL情報取得
     * @return 応答オブジェクト
     */
    Multistatus propfind(Propfind propfind, String depth, String url, boolean isAclRead);

    /**
     * PROPPATCHメソッドの処理.
     * @param propUpdate PROPPATCH要求オブジェクト
     * @param url URL
     * @return 応答オブジェクト
     */
    Multistatus proppatch(Propertyupdate propUpdate, String url);

    /**
     * 削除処理を行う.
     * @param ifMatch If-Matchヘッダ
     * @return JAX-RS ResponseBuilder
     */
    ResponseBuilder delete(String ifMatch);

    /**
     * GETメソッドを処理する.
     * @param ifNoneMatch If-None-Matchヘッダ
     * @param rangeHeaderField Rangeヘッダ
     * @return JAX-RS ResponseBuilder
     */
    ResponseBuilder get(String ifNoneMatch, String rangeHeaderField);

    /**
     * データ操作用ODataProducerを返します.
     * @return ODataProducer
     */
    DcODataProducer getODataProducer();

    /**
     * スキーマ操作用ODataProducerを返します.
     * @param cell Cell
     * @return ODataProducer
     */
    DcODataProducer getSchemaODataProducer(Cell cell);

    /**
     * @return ETag文字列.
     */
    String getEtag();

    /**
     * MOVE処理を行う.
     * @param etag ETag値
     * @param overwrite 移動先のリソースを上書きするかどうか
     * @param davDestination 移動先の階層情報
     * @return ResponseBuilder レスポンス
     */
    ResponseBuilder move(String etag, String overwrite, DavDestination davDestination);

    /**
     * このDavNodeリソースのURLを返します.
     * @return URL文字列
     */
    String getUrl();

    /**
     * リソースに合わせてNotFoundの例外を返却する. <br />
     * リソースによってメッセージがことなるため、各リソースのクラスはこのメソッドをオーバーライドしてメッセージを定義すること。 <br />
     * メッセージの付加情報は、ここでは設定せずに呼び出し元で設定すること。
     * @return NotFound例外
     */
    DcCoreException getNotFoundException();
}
