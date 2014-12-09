/*
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
/**
 * @fileOverview
 * @name dc-dao.js
 * @version 1.0.0
 */

/**
 * PCSを操作するためのDAOライブラリ.
 * @class dc
 */
var dc = {};
dc.extension = {};

/**
 * アクセス主体を指定.
 * @param {Object} param アクセス主体を指定するパラメタ
 * @returns {dc.Accessor} 生成したAccessorオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.as = function(param) {
    try {
        if (typeof param == 'string') {
            if ( param == 'serviceSubject' ) {
            	var as = dcjvm.asServiceSubject();
                return new dc.Accessor(as);
            } else if ( param == 'client' ) {
                return new dc.Accessor(dcjvm.withClientToken());
            }
        } else {
        	if ((typeof param.cellUrl == "string") && (typeof param.userId == "string") &&
        			(typeof param.password == "string") && (typeof param.schemaUrl == "string") &&
        			(typeof param.schemaUserId == "string") && (typeof param.schemaPassword == "string")) {
                // スキーマ付きパスワード認証
                return new dc.Accessor(dcjvm.asAccountWithSchemaAuthn(param.cellUrl, param.userId, param.password,
                        param.schemaUrl, param.schemaUserId, param.schemaPassword));
            } else if ((typeof param.cellUrl == "string") && (typeof param.accessToken == "string") &&
                    	(typeof param.schemaUrl == "string") && (typeof param.schemaUserId == "string") &&
                    	(typeof param.schemaPassword == "string")) {
                // スキーマ付きトークン認証
                return new dc.Accessor(dcjvm.getAccessorWithTransCellTokenAndSchemaAuthn(param.cellUrl, param.accessToken,
                        param.schemaUrl, param.schemaUserId, param.schemaPassword));
            } else if ((typeof param.cellUrl == "string") && (typeof param.userId == "string") &&
            		(typeof param.password == "string")) {
                // パスワード認証
                return new dc.Accessor(dcjvm.asAccount(param.cellUrl, param.userId, param.password));
            } else if ((typeof param.cellUrl == "string") && (typeof param.accessToken == "string")) {
                // トークン認証
                return new dc.Accessor(dcjvm.getAccessorWithTransCellToken(param.cellUrl, param.accessToken));
            } else if ((typeof param.cellUrl == "string") && (typeof param.refreshToken == "string")) {
            	// フレッシュトークン認証
                return new dc.Accessor(dcjvm.getAccessorWithRefreshToken(param.cellUrl, param.refreshToken));
            } else if (typeof param.accessToken == "string") {
            	// トークン指定
                return new dc.Accessor(dcjvm.withToken(param.accessToken));
            }
        }
        throw new dc.DcException("Parameter Invalid");
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * personium.ioのバージョン指定.<br>
 * 例：<br>
 * 　dc.setDcVersion("1.0");
 * @param {string} version バージョン番号. サーバーに対し、X-Tritium-Versionヘッダに指定するバージョン文字列.
 */
dc.setDcVersion = function(version) {
    dcjvm.setDcVersion(version);
};

/**
 * personium.ioのバージョン指定.<br>
 * 例：<br>
 * 　dc.setDcVersion("1.0");
 * @param {string} version バージョン番号. サーバーに対し、X-Tritium-Versionヘッダに指定するバージョン文字列.
 */
dc.getServerVersion = function() {
    return dcjvm.getServerVersion();
};

/**
 * サーバー通信を非同期にするかどうか(V0のログ機能のみ対応)<br>
 * 例：<br>
 * 　dc.setThreadable(true);
 * @param {Boolean} value true:非同期、false:同期
 */
dc.setThreadable = function(value) {
    dcjvm.setThreadable(value);
};

/**
 * 新しいAccessorオブジェクトを作成する.
 * @class アクセス主体クラス
 */
dc.Accessor = function(obj) {
    this.core = obj;
};

/**
 * Cell指定.<br>
 * 例：<br>
 * 省略：<br>
 * 　as("client").cell();</blockquote>
 * Cell名指定：<br>
 * 　as("client").cell("cellName");
 * URL指定：<br>
 * 　as("client").cell("http://xxx.com/cellName");
 * @param {string} url CellのID。省略した場合はデフォルトのCellを利用.<br>
 * @returns {dc.Cell} 新しく作成したCellオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.Accessor.prototype.cell = function(url) {
    try {
        if (typeof url == 'string') {
            return new dc.Cell(this.core.cell(url));
        } else {
            return new dc.Cell(this.core.cell());
        }
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Unitユーザへの昇格.<br>
 * @returns {dc.Cell} 新しく作成したOwnerAccessorオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.Accessor.prototype.asCellOwner = function() {
	try {
		return new dc.OwnerAccessor(this.core.asCellOwner());
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * パスワード変更.<br>
 * 例：<br>
 * 省略：<br>
 * 　as("client").changePassword(newPassword);</blockquote>
 * 更新するパスワード：<br>
 * 　as("client").changePassword(newPassword);
 * @param {string} newPassword 新しいパスワード.<br>
 * @exception {dc.DcException} DAO例外
 */
dc.Accessor.prototype.changePassword = function(newPassword) {
    try {
        this.core.changePassword(newPassword);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいOwnerAccessorオブジェクトを作成する.
 * @class オーナーアクセス主体クラス
 * @property {dc.UnitManager} unit Unit操作のためのプロパティ
 */
dc.OwnerAccessor = function(obj) {
    this.core = obj;
    this.unit = new dc.UnitManager(this.core.unit);
};

dc.OwnerAccessor.prototype = new dc.Accessor();

/**
 * ODataのCRUDを行う抽象クラス.
 * @class OData操作クラス
 */
dc.OData = function() {};

// ODataデータを登録する(内部関数).
dc.OData.prototype.internalCreate = function(json) {
//    try {
        return this.core.create(dc.util.obj2javaJson(json));
//    } catch (e) {
//        throw new dc.DcException(e.message);
//    }
};

// ODataデータを更新する(内部関数).
dc.OData.prototype.internalUpdate = function(id, json, etag) {
    if (!etag) {
        etag = "*";
    }
//    try {
        this.core.update(id, dc.util.obj2javaJson(json), etag);
//    } catch (e) {
//        throw new dc.DcException(e.message);
//    }
};

// ODataデータを取得する(内部関数).
dc.OData.prototype.internalRetrieve = function(id) {
//    try {
        return this.core.retrieve(id);
//    } catch (e) {
//        throw new dc.DcException(e.message);
//    }
};

/**
 * ODataデータを削除する.
 * @param {string} id 削除するデータのID
 * @param {string} etag 削除するデータのEtag
 * @exception {dc.DcException} DAO例外
 */
dc.OData.prototype.del = function(id, etag) {
    if (!etag) {
        etag = "*";
    }
    try {
        this.core.del(id, etag);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ODataデータの一覧取得のためのQueryオブジェクトを生成する.<br>
 * 例：<br>
 * 　odate("odata").entitiSet("ent").query().run();
 * @returns {dc.Query} Queryオブジェクト
 */
dc.OData.prototype.query = function() {
    return new dc.Query( this.core.query() );
};

/**
 * ACLの操作を行う抽象クラス.
 * @class ACL操作クラス
 */
dc.AclManager = function() {
	this.core = null;
};

/**
 * ACLを設定する.<br>
 * @param {json} param 設定するJSON
 * 設定するJSONの例）
 * {
 *   "requireSchemaAuthz": "public",
 *   "ace": [
 *     {
 *       "role": {Roleのobject},
 *       "privilege": [
 *         "read",
 *         "write"
 *       ]
 *     },
 *     {
 *       "role": {Roleのobject},
 *       "privilege": [
 *         "read",
 *         "read-acl"
 *       ]
 *     }
 *   ]
 * }
 * 
 * @memberOf Acl#
 */
dc.AclManager.prototype.set = function(param) {
    try {
        var acl = new com.fujitsu.dc.client.Acl();
        
        if (param["requireSchemaAuthz"] !== null
        && typeof param["requireSchemaAuthz"] !== "undefined"
        && (param["requireSchemaAuthz"] !== "")) {
            acl.setRequireSchemaAuthz(param["requireSchemaAuthz"]);
        }
        var aces = param["ace"]

        if (aces != null) {
            for (var i = 0; i < aces.length; i++) {
                aceObj = aces[i];
                if (aceObj != null) {
                    var ace = new com.fujitsu.dc.client.Ace();
                    if ((aceObj["role"] != null) && (aceObj["role"] != "")) {
                        ace.setRole(aceObj["role"].core);
                    }
                    if ((aceObj["privilege"] != null) && (aceObj["privilege"] instanceof Array) && (aceObj["privilege"] != "")) {
                        for (var n = 0; n < aceObj["privilege"].length; n++) {
                            ace.addPrivilege(aceObj["privilege"][n]);
                        }
                    }
                    acl.addAce(ace);
                }
            }
        }
        this.core.set(acl);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ACLを取得する.
 * @returns {string} 取得したACLのjsonオブジェクト. 
 * @exception {dc.DcException} DAO例外
 */
dc.AclManager.prototype.get = function() {

    try {
        var obj = this.core.get();
        var acl = {};
        acl["base"] = obj.base + "";
        acl["requireSchemaAuthz"] = obj.getRequireSchemaAuthz() + "";

        var aces = obj.aceList;
        for (var i = 0; i < aces.length; i++) {
            var ace = {};
            ace["role"] = aces[i].roleName + "";
            var privilegeList = aces[i].privilegeList;
            var privilege = new Array(privilegeList.length);
            for (j = 0; j < privilegeList.length; j++) {
                privilege[j] = privilegeList[j] + "";
            }
            ace["privilege"] = privilege;
            aces[i] = ace;
        }
        acl["ace"] = aces;
        return acl;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * WebDAVの操作を行う抽象クラス.
 * @class WebDAV操作クラス
 */
dc.Webdav = function() {
};

dc.Webdav.prototype.acl = new dc.AclManager();

/**
 * WebDAVコレクションを作成する.<br>
 * 例：<br>
 * 　box().mkCol("col");
 * @param {string} name 作成するコレクション名
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.mkCol = function(name) {
    try {
        this.core.mkCol(name);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ODataコレクションを作成する.<br>
 * 例：<br>
 * 　box().mkOData("col");
 * @param {string} name 作成するコレクション名
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.mkOData = function(name) {
    try {
        this.core.mkOData(name);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Serviceコレクションを作成する.<br>
 * 例：<br>
 * 　box().mkService("col");
 * @param {string} name 作成するコレクション名
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.mkService = function(name) {
    try {
        this.core.mkService(name);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * コレクション内のリソースの一覧を取得する.
 * @returns {string[]} リソースへのWebDAVパス一覧
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.getFileList = function() {
    try {
        return this.core.getFileList();
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * コレクション内のサブコレクションの一覧を取得する.
 * @returns {string[]} サブコレクションへのWebDAVパス一覧
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.getColList = function(name) {
    try {
        return this.core.getColList(name);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * コレクションのプロパティを設定する.
 * @param {string} key 設定項目名
 * @param {string} value 設定項目値
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.setProp = function(key, value) {
    try {
        this.core.setProp(key, value);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * コレクションのプロパティを取得する.
 * @param {string} key 設定項目名
 * @returns {string} 指定したプロパティ値
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.getProp = function(key) {
    try {
        return this.core.getProp(key);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * サブコレクションを指定する.<br>
 * 例：<br>
 * <dd>box().col("col");
 * <dd>box().col("col1").col("col2").col("col3");
 * <dd>box().col("col1/col2/col3");
 * @param {string} name コレクション名
 * @returns {dc.DavCollection} dc.DavCollectionオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.col = function(name) {
    try {
        var dav = new dc.DavCollection(this.core.col(name));
        dav.acl.core = dav.core.acl;
        return dav;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ODataコレクションを取得する.<br>
 * 例：<br>
 * 　box().odata("odata");
 * @param {string} name ODataコレクション名
 * @returns {dc.ODataCollection} dc.ODataCollectionオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.odata = function(name) {
    try {
        return new dc.ODataCollection(this.core.odata(name));
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 現在のコレクションのパスを取得する.
 * @returns {string} 現在のコレクションのパス
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.getPath = function() {
    try {
        return this.core.getPath() + "";
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * DAVリソースを文字列型で取得する.<br>
 * 例：<br>
 * 　box().col("col").getString("index.html", "utf-8");
 * @param {string} path 取得するパス
 * @param {string} charset 文字コード
 * @returns {string} DAVリソース
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.getString = function(path, charset) {
    if (!charset) {
        charset = "utf-8";
    }
    try {
        return this.core.getString(path, charset);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * DAVリソースをストリームで取得する.
 * @param {string} path 取得するパス
 * @returns {stream} DAVリソース
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.getStream = function(path) {
    try {
        return this.core.getStream(path);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Davへファイルを登録する.<br>
 * 例：<br>
 * 1. <br>
 *   .put("test.txt", "text/plain", "test-data", "*");<br>
 * 2.<br>
 *   .put({<br>
 *       path: "test.txt",<br>
 *       contentType: "text/plain",<br>
 *       data: "test-data",<br>
 *       charset: "UTF-8",<br>
 *       etag: "*"<br>
 *    });<br>
 * @param {string または Object} param 対象のDavのパス または、すべてのパラメタを含んだJSONオブジェクト
 * @param {string} contentType 登録するファイルのメディアタイプ
 * @param {string} data 登録するデータ(文字列形式)
 * @param {string} etag 対象のEtag
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.put = function(param, contentType, data, etag) {
    if (typeof param == 'string') {
        try {
            this.core.put(param, contentType, "UTF-8", data, etag?etag:"*");
        } catch (e) {
            throw new dc.DcException(e.message);
        }
    } else {
        if ((param.path) && (param.contentType) && (param.data)) {
            param.charset = param.charset?param.charset:"UTF-8";
            param.etag = param.etag?param.etag:"*";
            try {
                this.core.put(param.path, param.contentType, param.charset, param.data, param.etag);
            } catch (e) {
                throw new dc.DcException(e.message);
            }
        } else {
            throw new dc.DcException("Parameter Invalid");
        }
    }
};

/**
 * 指定Pathのデータを削除.<br>
 * 例：<br>
 * ETag指定：<br>
 * 　box().col("col").del("index.html", "1234567890");<br>
 * ETag省略：<br>
 * 　box().col("col").del("index.html");
 * @param {string} path 削除するパス
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.del = function(path, etag) {
    if (!etag) {
        etag = "*";
    }
    try {
        this.core.del(path, etag);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * URL文字列を取得する.
 * @returns {string} path 自身のURL文字列
 * @exception {dc.DcException} DAO例外
 */
dc.Webdav.prototype.makeUrlString = function() {
    try {
        return this.core.makeUrlString();
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * $linkの作成／削除を行うクラス.
 * @class $link操作クラス
 */
dc.LinkManager = function(obj) {
    this.core = obj;
};

/**
 * linkを作成.
 * @param {Object} param Link先のオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.LinkManager.prototype.link = function(param) {
    try {
        this.core.link(param.core);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * linkを削除.
 * @param {Object} param Link先のオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.LinkManager.prototype.unLink = function(param) {
    try {
        this.core.unLink(param.core);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * $linkの作成／削除する.
 * @class $link操作クラス
 */
dc.MetadataLinkManager = function(obj) {
    this.core = obj;
};

/**
 * linkを作成.
 * @param {Object} param Link先のオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.MetadataLinkManager.prototype.link = function(param) {
    try {
        this.core.link(param.core);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * linkを削除.
 * @param {Object} param Link先のオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.MetadataLinkManager.prototype.unLink = function(param) {
    try {
        this.core.unLink(param.core);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいCellオブジェクトを作成する.
 * @class Cell操作クラス
 * @property {string} name Cellを識別するCell名
 * @property {dc.CellCtl} ctl Cell内のCtl操作のためのプロパティ
 * @property {dc.EventManager} event CellレベルのEvent登録操作のためのプロパティ
 * @property {dc.EventManager} log CellレベルのEvent取得操作のためのプロパティ
 */
dc.Cell = function(obj) {
    this.core = obj;
    this.name = "";
    this.ctl = new dc.CellCtl(obj);
    this.event = new dc.EventManager(this.core.event);
    this.currentLog = new dc.CurrentLogManager(this.core.currentLog);
    this.archiveLog = new dc.ArchiveLogManager(this.core.archiveLog);
    this.acl.core = this.core.acl;
};

dc.Cell.prototype.acl = new dc.AclManager();

/**
 * Boxを指定.
 * @param {string} name Box名
 * @returns {dc.Box} Box操作クラス
 */
dc.Cell.prototype.box = function(name) {
	var dav;
    if (name) {
        dav = new dc.Box(this.core.box(name));
    } else {
        dav = new dc.Box(this.core.box());
    }
    // WebDAVから継承された際、ACLオブジェクトのcoreがセットされていないので、
    // このタイミングでセットする
    dav.acl.core = dav.core.acl;
    return dav;
};

/**
 * CellのUrlを取得.
 * @returns {string} CellのURL
 */
dc.Cell.prototype.getUrl = function() {
    return this.core.getUrl() + "";
}

/**
 * CellのUrlを取得.
 * @returns {string} CellのURL
 */
dc.Cell.prototype.setOwnerRepresentativeAccounts = function(user) {
    this.core.setOwnerRepresentativeAccounts(user);
}

/**
 * Cell.ctl でアクセスされ、関連APIの呼び出しを行う.
 * @class CellCtrl操作クラス
 * @property {dc.BoxManager} box Box操作のためのプロパティ
 * @property {dc.AccountManager} account Account操作のためのプロパティ
 * @property {dc.RelationManager} relation Relation操作のためのプロパティ
 * @property {dc.RoleManager} role Role操作のためのプロパティ
 * @property {dc.ExtRoleManager} extRole ExtRole操作のためのプロパティ
 * @property {dc.ExtCellManager} extCell ExtCell操作のためのプロパティ
 */
dc.CellCtl = function(obj) {
    this.core = obj;
    this.box = new dc.BoxManager(this.core.boxManager);
    this.account = new dc.AccountManager(this.core.account);
    this.relation = new dc.RelationManager(this.core.relation);
    this.role = new dc.RoleManager(this.core.role);
    this.extRole = new dc.ExtRoleManager(this.core.extRole);
    this.extCell = new dc.ExtCellManager(this.core.extCell);
};

/**
 * 新しいBoxオブジェクトを作成する.
 * @class Box操作クラス
 * @augments dc.Webdav
 * @property {string} name Box名
 * @property {string} schema スキーマ
 * @property {dc.BoxCtl} ctl BoxCtlオブジェクト
 */
dc.Box = function(obj) {
    this.core = obj;
    this.name = "";
    this.schema = "";
    this.ctl = new dc.BoxCtl(obj);
};
dc.Box.prototype = new dc.Webdav();

/**
 * Box.ctl でアクセスされ、関連APIの呼び出しを行う.
 * @class BoxCtl操作クラス
 * @property {dc.RoleManager} role Role操作のためのプロパティ
 * @property {dc.EventManager} event Event操作のためのプロパティ
 */
dc.BoxCtl = function(obj) {
    this.core = obj;
    this.role = new dc.RoleManager(this.core.role);
    this.event = new dc.EventManager(this.core.event);
};

/**
 * 新しいAccountオブジェクトを作成する.
 * @class Accountクラス
 * @property {string} name ユーザー名
 */
dc.Account = function(obj) {
    this.core = obj;
    this.name = "";
};

/**
 * 新しいRoleオブジェクトを作成する.
 * @class Roleクラス
 * @property {string} name Role名
 * @property {string} id Role ID値
 * @property {dc.LinkManager} account AccountへのLink操作を行うためのプロパティ
 * @property {dc.LinkManager} relation RelationへのLink操作を行うためのプロパティ
 * @property {dc.LinkManager} extCell ExtCellへのLink操作を行うためのプロパティ
 */
dc.Role = function(obj) {
    this.core = obj;
    this.name = "";
    this.id = "";
    this.account = new dc.LinkManager(this.core.account)
    this.relation = new dc.LinkManager(this.core.relation)
    this.extCell = new dc.LinkManager(this.core.extCell)
    this.extRole = new dc.LinkManager(this.core.extRole)
};

/**
 * 新しいRelationオブジェクトを作成する.
 * @class Relationクラス
 * @property {string} name Relation名
 * @property {string} id Relation ID値
 * @property {dc.LinkManager} role RoleへのLink操作を行うためのプロパティ
 * @property {dc.LinkManager} extCell ExtCellへのLink操作を行うためのプロパティ
 */
dc.Relation = function(obj) {
    this.core = obj;
    this.name = "";
    this.id = "";
    this.role = new dc.LinkManager(this.core.role)
    this.extCell = new dc.LinkManager(this.core.extCell)
};

/**
 * 新しいExtRoleオブジェクトを作成する.
 * @class ExtRoleクラス
 * @property {string} name ExtRole名
 * @property {string} id ExtRole ID値
 */
dc.ExtRole = function(obj) {
    this.core = obj;
    this.name = "";
    this.relationName = "";
    this.relationBoxName = "";
    this.id = "";
    this.role = new dc.LinkManager(this.core.role)
};

/**
 * 新しいExtCellオブジェクトを作成する.
 * @class ExtCellクラス
 * @property {string} name ExtCell名
 * @property {string} id ExtCell ID値
 * @property {dc.LinkManager} role RoleへのLink操作を行うためのプロパティ
 * @property {dc.LinkManager} relation RelationへのLink操作を行うためのプロパティ
 */
dc.ExtCell = function(obj) {
    this.core = obj;
    this.name = "";
    this.id = "";
    this.role = new dc.LinkManager(this.core.role)
    this.relation = new dc.LinkManager(this.core.relation)
};

/**
 * 新しいDavCollectionオブジェクトを作成する.
 * @class DavCollectionクラス
 * @augments dc.Webdav
 * @property {string} name コレクション名
 */
dc.DavCollection = function(obj) {
    this.core = obj;
    this.name = "";
};
dc.DavCollection.prototype = new dc.Webdav();

/**
 * 新しいODataCollectionオブジェクトを作成する.
 * @class ODataCollectionクラス
 * @property {string} name ODataCollection名
 * @property {dc.ODataCollectionCtl} schema $metadata操作を行うためのプロパティ
 */
dc.ODataCollection = function(obj) {
    this.core = obj;
    this.name = "";
    this.schema = new dc.ODataCollectionCtl(obj);
};

/**
 * ユーザーデータ(OData)のEntitySetを指定. 
 * 例：<br>
 * box().odata("odata").schema.entityType.create("entity");<br>
 * box().odata("odata").entitySet("entity");
 * @param {string} name EntitySet名(事前にEntityTypeとして作成されたEntity名を指定)
 * @returns {dc.EntitySet} EntitySetオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.ODataCollection.prototype.entitySet = function(name) {
    try {
        return new dc.EntitySet(this.core.entitySet(name));
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいEntitySetオブジェクトを作成.
 * @class EntitySet操作クラス
 * @augments dc.OData
 */
dc.EntitySet = function(obj) {
    this.core = obj;
};
dc.EntitySet.prototype = new dc.OData();

/**
 * ユーザーデータを登録.<br>
 * 例：box().odata("odata").entitySet("entity").create({"name":"user","age":18});
 * @param {Object} json 登録するJSONデータ
 * @returns {Object} 登録されたJSONデータ
 * @exception {dc.DcException} DAO例外
 */
dc.EntitySet.prototype.create = function(json) {
    try {
    	var ret = this.core.createAsJson(dc.util.obj2javaJson(json));
    	return JSON.parse(ret);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ユーザーデータを取得する.
 * @param {string} id 取得対象のID値
 * @returns {Object} 取得したJSONデータ
 * @exception {dc.DcException} DAO例外
 */
dc.EntitySet.prototype.retrieve = function(id) {
    try {
    	var ret = this.core.retrieveAsJson(id);
    	return JSON.parse(ret);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ユーザーデータを更新する.
 * @param {string} id 更新対象のID
 * @param {Object} json 更新するJSONデータ
 * @param {string} etag Etag
 * @exception {dc.DcException} DAO例外
 */
dc.EntitySet.prototype.update = function(id, json, etag) {
    try {
        this.core.update(id, dc.util.obj2javaJson(json), etag);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ユーザーデータを部分更新する.
 * @param {string} id 部分更新対象のID
 * @param {Object} json 部分更新するJSONデータ
 * @param {string} etag Etag
 * @exception {dc.DcException} DAO例外
 */
dc.EntitySet.prototype.merge = function(id, json, etag) {
    try {
        this.core.merge(id, dc.util.obj2javaJson(json), etag);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};


/**
 * NavigationProperty経由登録のためのkey値を指定する.<br>
 * 例：box().odata("odata").entitySet("entity").key("key").nav("linkEt").create({"name":"user"});
 * @param {string} id keyPredicate
 * @returns {dc.EntitySet} EntitySetオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.EntitySet.prototype.key = function(id) {
    try {
        return new dc.EntitySet(this.core.key(id));
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * navigationPropertyを指定する.<br>
 * 例：box().odata("odata").entitySet("entity").key("key").nav("linkEt").create({"name":"user"});
 * @param {string} id keyPredicate
 * @returns {dc.EntitySet} EntitySetオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.EntitySet.prototype.nav = function(navProp) {
    try {
        return new dc.EntitySet(this.core.nav(navProp));
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ODataCollection.schema でアクセスされ、関連APIの呼び出しを行う.
 * @class ODataCollectionCtlクラス
 * @property {dc.EntityTypeManager} entityType EntityType操作のためのプロパティ
 * @property {dc.AssociationEndManager} associationEnd AssociationEnd操作のためのプロパティ
 */
dc.ODataCollectionCtl = function(obj) {
    this.core = obj;
    this.entityType = new dc.EntityTypeManager(this.core.entityType);
    this.associationEnd = new dc.AssociationEndManager(this.core.associationEnd);
    this.complexType = new dc.ComplexTypeManager(this.core.complexType);
    this.property = new dc.PropertyManager(this.core.property);
    this.complexTypeProperty = new dc.ComplexTypePropertyManager(this.core.complexTypeProperty);
};

/**
 * 新しいEntityTypeオブジェクトを作成する.
 * @class EntityTypeクラス
 * @property {string} name EntityType名
 */
dc.EntityType = function(obj) {
    this.core = obj;
    this.name = "";
};

/**
 * 新しいAssociationEndオブジェクトを作成する.
 * @class AssociationEndクラス
 * @property {string} name Association名
 * @property {string} entityTypeName EntityType名
 * @property {string} multiplicity 多重度
 * @property {dc.MetadataLinkManaer} associationEnd AssociationEndへのリンク操作を行うプロパティ
 */
dc.AssociationEnd = function(obj) {
    this.core = obj;
    this.name = "";
    this.entityTypeName = "";
    this.multiplicity = "";
    this.associationEnd = new dc.MetadataLinkManager(this.core.associationEnd)
};

/**
 * 新しいComplexTypeオブジェクトを作成する.
 * @class ComplexTypeクラス
 * @property {string} name ComplexType名
 */
dc.ComplexType = function(obj) {
    this.core = obj;
    this.name = "";
};

/**
 * 新しいPropertyオブジェクトを作成する.
 * @class Propertyクラス
 * @property {string} name Property名
 * @property {string} entityTypeName 紐付くEntityType名
 * @property {string} type 型定義
 * @property {boolean} nullable Null値許可
 * @property {object} defaultValue デフォルト値
 * @property {string} CollectionKind 配列種別
 * @property {boolean} isKey 主キー設定 
 * @property {string} uniqueKey ユニークキー設定
 */
dc.Property = function(obj) {
    this.core = obj;
    this.name = "";
    this.entityTypeName = "";
    this.type = "";
    this.nullable = true;
    this.defaultValue = null;
    this.collectionKind = "None";
    this.isKey = false;
    this.uniqueKey = null;
};

/**
 * 新しいComplexTypePropertyオブジェクトを作成する.
 * @class ComplexTypePropertyクラス
 * @property {string} name ComplexTypeProperty名
 * @property {string} complexTypeName 紐付くComplexType名
 * @property {string} type 型定義
 * @property {boolean} nullable Null値許可
 * @property {object} defaultValue デフォルト値
 * @property {string} CollectionKind 配列種別
 */
dc.ComplexTypeProperty = function(obj) {
    this.core = obj;
    this.name = "";
    this.complexTypeName = "";
    this.type = "";
    this.nullable = true;
    this.defaultValue = null;
    this.collectionKind = "None";
};

/**
 * 新しいBoxManagerオブジェクトを作成する.
 * @class Box操作クラス
 * @augments dc.OData
 */
dc.BoxManager = function(obj) {
    this.core = obj;
};
dc.BoxManager.prototype = new dc.OData();

/**
 * Boxを登録する.
 * @param {Object} param Box作成に必要なJSON型オブジェクト
 * 例：cell().ctl.box.create({"name":"boxname", "schema":"box-schema"});
 * @returns {dc.Box} 作成したBoxオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.BoxManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var box = new dc.Box(obj);
        box.name = obj.getName() + "";
        box.schema = obj.getSchema() + "";
        box.acl.core = box.core.acl;
        return box;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Boxを取得する.
 * @param {string} name 取得するBoxの名前<br>
 * 例：cell().ctl.box.retrieve("boxname");
 * @returns {dc.Box} 取得したBoxオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.BoxManager.prototype.retrieve = function(name) {
    try {
        var obj = this.internalRetrieve(name);
        var box = new dc.Box(obj);
        box.name = obj.getName() + "";
        box.schema = obj.getSchema() + "";
        box.acl.core = box.core.acl;
        return box;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいAccountManagerオブジェクトを作成する.
 * @class Account操作クラス
 * @augments dc.OData
 */
dc.AccountManager = function(obj) {
    this.core = obj;
};
dc.AccountManager.prototype = new dc.OData();

/**
 * Accountを登録する。<br>
 * 例：account.create({"name":"user01"}, "password");
 * @param {Object} user ユーザー名のJSONオブジェクト
 * @param {string} pass パスワード
 * @returns {dc.Account} 作成したAccountオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.AccountManager.prototype.create = function(user, pass) {
    var obj;
    try {
        obj = this.core.create(dc.util.obj2javaJson(user), pass);
        var account = new dc.Account(obj);
        account.name = obj.getName() + "";
        return account;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Accountを取得する.<br>
 * 例：account.retrieve("user01");
 * @param {string} user ユーザー名<br>
 * @returns {dc.Account} 作成したAccountオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.AccountManager.prototype.retrieve = function(user) {
    var obj;
    try {
        obj  = this.core.retrieve(user);
        var account = new dc.Account(obj);
        account.name = obj.getName() + "";
        return account;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Passwordを変更する.<br>
 * 例：account.changePassword("user01", "newPassword");
 * @param {string} user ユーザー名
 * @param {string} pass パスワード
 * @exception {dc.DcException} DAO例外
 */
dc.AccountManager.prototype.changePassword = function(user, pass) {
    var obj;
    try {
        this.core.changePassword(user, pass);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいEventManagerオブジェクトを作成する.
 * @class Event操作クラス
 */
dc.EventManager = function(obj) {
    this.core = obj;
};

/**
 * Eventを登録する.
 * @param {Object} param イベントオブジェクト<br>
 * @param {String} requestKey X-Dc-RequestKeyヘッダの値
 * 呼び出し例：<br>
 *   event.post({"level":"error", 
 *               "action":"actionData", 
 *               "object":"objectData", 
 *               "result":"resultData"},
 *               "RequestKey");
 * @exception {dc.DcException} DAO例外
 */
dc.EventManager.prototype.post = function(param, requestKey) {
    try {
        if (requestKey === null || typeof requestKey === "undefined") {
            this.core.post(dc.util.obj2javaJson(param));
        }else{
            this.core.post(dc.util.obj2javaJson(param), requestKey);
        }
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいCurrentLogManagerオブジェクトを作成する.
 * @class CurrentLog操作クラス
 */
dc.CurrentLogManager = function(obj){
	this.core = obj;
};

/**
 * ログをString形式で取得する.
 * @param {String} filename 取得するログファイル名
 * @param {String} requestKey X-Dc-RequestKeyヘッダの値
 * 呼び出し例：<br>
 *   currentLog.getString("default.log", "RequestKey");
 * @exception {dc.DcException} DAO例外
 */
dc.CurrentLogManager.prototype.getString = function(filename, requestKey) {
    try {
        if (requestKey === null || typeof requestKey === "undefined") {
            return this.core.getString(filename);
        }else{
            return this.core.getString(filename, requestKey);
        }
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ログをStream形式で取得する.
 * @param {String} filename 取得するログファイル名
 * @param {String} requestKey X-Dc-RequestKeyヘッダの値
 * 呼び出し例：<br>
 *   currentLog.getStream("default.log", "RequestKey");
 * @exception {dc.DcException} DAO例外
 */
dc.CurrentLogManager.prototype.getStream = function(filename, requestKey) {
    try {
        if (requestKey === null || typeof requestKey === "undefined") {
            return this.core.getStream(filename);
        }else{
        	return this.core.getStream(filename, requestKey);
        }
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};
/**
 * 新しいArchiveLogManagerオブジェクトを作成する.
 * @class ArchiveLog操作クラス
 */
dc.ArchiveLogManager = function(obj){
	this.core = obj;
};

/**
 * ローテートされたログをString形式で取得する.
 * @param {String} filename 取得するログファイル名
 * @param {String} requestKey X-Dc-RequestKeyヘッダの値
 * 呼び出し例：<br>
 *   archiveLog.getString("default.log", "RequestKey");
 * @exception {dc.DcException} DAO例外
 */
dc.ArchiveLogManager.prototype.getString = function(filename, requestKey) {
    try {
        if (requestKey === null || typeof requestKey === "undefined") {
        	return this.core.getString(filename);
        }else{
        	return this.core.getString(filename, requestKey);
        }
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ローテートされたログをStream形式で取得する.
 * @param {String} filename 取得するログファイル名
 * @param {String} requestKey X-Dc-RequestKeyヘッダの値
 * 呼び出し例：<br>
 *   archiveLog.getStream("default.log", "RequestKey");
 * @exception {dc.DcException} DAO例外
 */
dc.ArchiveLogManager.prototype.getStream = function(filename, requestKey) {
    try {
        if (requestKey === null || typeof requestKey === "undefined") {
        	return this.core.getStream(filename);
        }else{
        	return this.core.getStream(filename, requestKey);
        }
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいRelationManagerオブジェクトを作成する.
 * @class Relation操作クラス
 * @augments dc.OData
 */
dc.RelationManager = function(obj) {
    this.core = obj;
};
dc.RelationManager.prototype = new dc.OData();

/**
 * Relationを登録<br>
 * 例：cell().ctl.relation.create({"name":"relation","_box.name":"boxName"});
 * @param {Object} param Relation作成に必要なJSON型オブジェクト
 * @returns {dc.Relation} 作成したRelationオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.RelationManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var relation = new dc.Relation(obj);
        relation.name = obj.getName() + "";
        relation.boxName = obj.getBoxName() + "";
        return relation;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};


/**
 * Relationを取得<br>
 * 例：cell().ctl.relation.retrieve({"name":"relation","_box.name":"boxName"});
 * @param {string} param {"name":"xxx", "_box.name":"xx"} というJSONを指定
 * @returns {dc.Relation} 作成したRelationオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.RelationManager.prototype.retrieve = function(param) {
    var obj;
    var name = param["Name"];
    var boxName = param["_Box.Name"];

    try {
        if (boxName === null || typeof boxName === "undefined") {
            obj  = this.core.retrieve(name);
        } else {
            obj  = this.core.retrieve(name, boxName);
        }
        var relation = new dc.Relation(obj);
        relation.name = obj.getName() + "";
        relation.boxName = obj.getBoxName() + "";
        return relation;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Relationデータを削除<br>
 * 例：cell().ctl.relation.retrieve({"name":"relation","_box.name":"boxName"});
 * @param {string} param {"name":"xxx", "_box.name":"xx"} というJSONを指定
 * @exception {dc.DcException} DAO例外
 */
dc.RelationManager.prototype.del = function(param) {
    var name = param["Name"];
    var boxName = param["_Box.Name"];

    try {
        if (boxName == null || typeof boxName === "undefined") {
            this.core.del(name);
        } else {
            this.core.del(name, boxName);
        }
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいRoleManagerオブジェクトを作成する.
 * @class Role操作クラス
 * @augments dc.OData
 */
dc.RoleManager = function(obj) {
    this.core = obj;
};
dc.RoleManager.prototype = new dc.OData();

/**
 * Roleを登録<br>
 * 例：cell().ctl.relation.create({"name":"role","_box.name":"boxName"});
 * @param {Object} param Role作成に必要なJSON型オブジェクト
 * @returns {dc.Role} 作成したRoleオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.RoleManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var role = new dc.Role(obj);
        role.name = obj.getName() + "";
        role.boxName = obj.getBoxName() + "";
        return role;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Roleを取得<br>
 * 例：<br>
 * cell().ctl.relation.retrieve({"name":"role","_box.name":"boxName"});<br>
 * @param {string} param {"name":"xxx", "_box.name":"xx"} というJSONを指定
 * @returns {dc.Role} 作成したRoleオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.RoleManager.prototype.retrieve = function(param) {
    var obj;
    var name = param["Name"];
    var boxName = param["_Box.Name"];

    try {
        if (boxName === null || typeof boxName === "undefined") {
            obj  = this.core.retrieve(name);
        } else {
            obj  = this.core.retrieve(name, boxName);
        }
        var role = new dc.Role(obj);
        role.name = obj.getName() + "";
        role.boxName = obj.getBoxName() + "";
        return role;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Roleデータを削除<br>
 * 例：<br>
 * cell().ctl.relation.del({"name":"role","_box.name":"boxName"});<br>
 * @param {string} param {"name":"xxx", "_box.name":"xx"} というJSONを指定
 * @exception {dc.DcException} DAO例外
 */
dc.RoleManager.prototype.del = function(param) {
    var name = param["Name"];
    var boxName = param["_Box.Name"];

    try {
        if (boxName == null || typeof boxName === "undefined") {
            this.core.del(name);
        } else {
            this.core.del(name, boxName);
        }
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいExtRoleManagerオブジェクトを作成する.
 * @class ExtRole操作クラス
 * @augments dc.OData
 */
dc.ExtRoleManager = function(obj) {
    this.core = obj;
};
dc.ExtRoleManager.prototype = new dc.OData();

/**
 * ExtRoleを登録する.
 * @param {Object} param ExtRole作成に必要なJSON型オブジェクト
 * @returns {dc.ExtRole} 作成したExtRoleオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.ExtRoleManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var extRole = new dc.ExtRole(obj);
        extRole.name = obj.getName() + "";
        extRole.relationName = obj.getRelationName() + "";
        extRole.relationBoxName = obj.getRelationBoxName() + "";
        return extRole;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ExtRoleを取得<br>
 * 例：<br>
 * cell().ctl.extRole.retrieve({"ExtRole":"http://extrole/jp","_Relation.Name":"relation","_Relation._Box.Name":"boxName"});<br>
 * @param {string} param {"ExtRole":"http://extrole/jp","_Relation.Name":"relation","_Relation._Box.Name":"boxName"} というJSONを指定
 * @returns {dc.ExtRole} 作成したExtRoleオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.ExtRoleManager.prototype.retrieve = function(param) {
    var obj;
    var name = param["ExtRole"];
    var relationName = param["_Relation.Name"];
    var relationBoxName = param["_Relation._Box.Name"];

    try {
        if (relationName === null || typeof relationName === "undefined") {
            obj  = this.core.retrieve(name, null, null);
        } else {
            obj  = this.core.retrieve(name, relationName, relationBoxName);
        }
        var extRole = new dc.ExtRole(obj);
        extRole.name = obj.getName() + "";
        extRole.relationName = obj.getRelationName() + "";
        extRole.relationBoxName = obj.getRelationBoxName() + "";
        return extRole;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ExtRoleデータを削除<br>
 * 例：<br>
 * cell().ctl.extRole.del({"ExtRole":"http://extrole/jp","_Relation.Name":"relation","_Relation._Box.Name":"boxName"});<br>
 * @param {string} param {"ExtRole":"http://extrole/jp","_Relation.Name":"relation","_Relation._Box.Name":"boxName"} というJSONを指定
 * @exception {dc.DcException} DAO例外
 */
dc.ExtRoleManager.prototype.del = function(param) {
    var name = param["ExtRole"];
    var relationName = param["_Relation.Name"];
    var relationBoxName = param["_Relation._Box.Name"];

    try {
        if (relationName === null || typeof relationName === "undefined") {
            obj  = this.core.del(name, null, null);
        } else {
            if (relationBoxName === null || typeof relationBoxName === "undefined") {
                obj  = this.core.del(name, relationName, null);
            } else {
                obj  = this.core.del(name, relationName, relationBoxName);
            }
        }
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいExtCellManagerオブジェクトを作成する.
 * @class ExtCellManager操作クラス
 * @augments dc.OData
 */
dc.ExtCellManager = function(obj) {
    this.core = obj;
};
dc.ExtCellManager.prototype = new dc.OData();

/**
 * ExtCellを登録する.
 * @param {Object} param ExtCell作成に必要なJSON型オブジェクト
 * @returns {dc.ExtCell} 作成したExtCellオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.ExtCellManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var extcell = new dc.ExtCell(obj);
        extcell.url = obj.getUrl() + "";
        return extcell;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ExtCellを取得する.
 * @param {string} url ExtCell取得に必要なurl
 * @returns {dc.ExtCell} 取得したExtCellオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.ExtCellManager.prototype.retrieve = function(url) {
    try {
        var obj = this.internalRetrieve(url);
        var extCell = new dc.ExtCell(obj);
        extCell.url = obj.getUrl() + "";
        return extCell;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいUnitManagerオブジェクトを作成する.
 * @class Unit操作クラス
 * @property {dc.UnitManagerCtl} ctl Unit内のCtl操作を行うためのプロパティ
 */
dc.UnitManager = function(obj) {
    this.core = obj;
    this.ctl = new dc.UnitManagerCtl(obj);
};

/**
 * UnitManager.ctl でアクセスされ、関連APIの呼び出しを行う.
 * @class UnitManagerCtl操作クラス
 * @property {dc.CellManager} CellのCRUDを行うためのプロパティ
 */
dc.UnitManagerCtl = function(obj) {
    this.core = obj;
    this.cell = new dc.CellManager(this.core.cell);
};

/**
 * 新しいCellManagerオブジェクトを作成する.
 * @class CellManager操作クラス
 * @augments dc.OData
 */
dc.CellManager = function(obj) {
    this.core = obj;
};
dc.CellManager.prototype = new dc.OData();

/**
 * Cellを登録する.
 * @param {Object} param Cell作成に必要なJSON型オブジェクト
 * @returns {dc.Cell} 作成したCellオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.CellManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var cell = new dc.Cell(obj);
        cell.name = obj.getName() + "";
        return cell;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Cellを更新する.
 * @param {String} id 更新対象のCell ID
 * @param {Object} json Cell更新に必要なJSON型オブジェクト
 * @param {String} etag Etag情報
 * @exception {dc.DcException} DAO例外
 */
dc.CellManager.prototype.update = function(id, json, etag) {
    try {
        this.internalUpdate(id, json, etag);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Cellを取得する.
 * @param {string} id Cell取得に必要なid
 * @returns {dc.Cell} 取得したCellオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.CellManager.prototype.retrieve = function(id) {
    try {
        var obj = this.internalRetrieve(id);
        var cell = new dc.Cell(obj);
        cell.name = obj.getName() + "";
        return cell;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいEntityTypeManagerオブジェクトを作成する.
 * @class EntityType操作クラス
 * @augments dc.OData
 */
dc.EntityTypeManager = function(obj) {
    this.core = obj;
};
dc.EntityTypeManager.prototype = new dc.OData();

/**
 * EntityTypeManagerを登録する.
 * @param {Object} param EntityType作成に必要なJSON型オブジェクト
 * @returns {dc.EntityType} 作成したEntityTypeオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.EntityTypeManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var entityType = new dc.EntityType(obj);
        entityType.name = obj.getName() + "";
        return entityType;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * EntityTypeManagerを取得する.
 * @param {string} name 取得するEntityTypeの名前
 * @returns {dc.EntityType} 取得したEntityTypeオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.EntityTypeManager.prototype.retrieve = function(param) {
    try {
        var obj = this.internalRetrieve(param);
        var entityType = new dc.EntityType(obj);
        entityType.name = obj.getName() + "";
        return entityType;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};


/**
 * 新しいAssociationEndManagerオブジェクトを作成する.
 * @class AssociationEnd操作クラス
 * @augments dc.OData
 */
dc.AssociationEndManager = function(obj) {
    this.core = obj;
}
dc.AssociationEndManager.prototype = new dc.OData();

/**
 * AssociationEndを登録する.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").schema.associationEnd.create({"Name":"name", "_EntityType.Name":"entity"});
 * @param {Object} param AssociationEnd作成に必要なJSON型オブジェクト
 * @returns {dc.AssociationEnd} 作成したAssociationEndオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.AssociationEndManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var AssociationEnd = new dc.AssociationEnd(obj);
        AssociationEnd.name = obj.getName() + "";
        AssociationEnd.entityTypeName = obj.getEntityTypeName() + "";
        AssociationEnd.multiplicity = obj.getMultiplicity() + "";
        return AssociationEnd;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * AssociationEndを取得.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").schema.associationEnd.retrieve({"Name":"name", "_EntityType.Name":"entity"});
 * @param {Object} 取得対象のキー
 * @param {string} entity 取得対象のEntityType.Name値
 * @returns {dc.AssociationEnd} 取得したAssociationEndオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.AssociationEndManager.prototype.retrieve = function(key) {
    var name = key["Name"];
    var entityTypeName = key["_EntityType.Name"];
    try {
        var obj = this.core.retrieve(name, entityTypeName);
        var AssociationEnd = new dc.AssociationEnd(obj);
        AssociationEnd.name = obj.getName() + "";
        AssociationEnd.entityTypeName = obj.getEntityTypeName() + "";
        AssociationEnd.multiplicity = obj.getMultiplicity() + "";
        return AssociationEnd;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * AssociationEndを削除.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").schema.associationEnd.del({"Name":"name", "_EntityType.Name":"entity"});
 * @param {Object} key 削除対象のキー
 * @param {string} entity 削除対象のEntityType.Name値
 * @returns {dc.AssociationEnd} 取得したAssociationEndオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.AssociationEndManager.prototype.del = function(key) {
    var name = key["Name"];
    var entityTypeName = key["_EntityType.Name"];
    try {
        this.core.del(name, entityTypeName);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいComplexTypeManagerオブジェクトを作成する.
 * @class ComplexType操作クラス
 * @augments dc.OData
 */
dc.ComplexTypeManager = function(obj) {
    this.core = obj;
}
dc.ComplexTypeManager.prototype = new dc.OData();

/**
 * ComplexTypeを登録する.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").schema.complexType.create({"Name":"name"});
 * @param {Object} param ComplexType作成に必要なJSON型オブジェクト
 * @returns {dc.ComplexType} 作成したComplexTypeオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.ComplexTypeManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var ComplexType = new dc.ComplexType(obj);
        ComplexType.name = obj.getName() + "";
        return ComplexType;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ComplexTypeを取得.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").schema.complexType.retrieve({"Name":"name"});
 * @param {Object} 取得対象のキー
 * @param {string} entity 取得対象のEntityType.Name値
 * @returns {dc.ComplexType} 取得したComplexTypeオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.ComplexTypeManager.prototype.retrieve = function(key) {
    var name = key["Name"];
    try {
        var obj = this.core.retrieve(name);
        var ComplexType = new dc.ComplexType(obj);
        ComplexType.name = obj.getName() + "";
        return ComplexType;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ComplexTypeを削除.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").schema.complexType.del({"Name":"name"});
 * @param {Object} key 削除対象のキー
 * @param {string} entity 削除対象のComplexType.Name値
 * @returns {dc.ComplexType} 取得したComplexTypeオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.ComplexTypeManager.prototype.del = function(key) {
    var name = key["Name"];
    try {
        this.core.del(name);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいPropertyManagerオブジェクトを作成する.
 * @class Property操作クラス
 * @augments dc.OData
 */
dc.PropertyManager = function(obj) {
    this.core = obj;
}
dc.PropertyManager.prototype = new dc.OData();

/**
 * Propertyを登録する.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").schema.property.create({"Name": "PetName",
 * "_EntityType.Name": "Profile","Type": "Edm.String"});
 * @param {Object} param Property作成に必要なJSON型オブジェクト
 * @returns {dc.Property} 作成したPropertyオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.PropertyManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var Property = new dc.Property(obj);
        Property.name = obj.getName() + "";
        Property.entityTypeName = obj.getEntityTypeName() + "";
        Property.type = obj.getType() + "";
        Property.nullable = obj.getNullable();
        if (Property.type === "Edm.String") {
            Property.defaultValue = obj.getDefaultValue() + "";
        } else {
            Property.defaultValue = obj.getDefaultValue();
        }
        Property.collectionKind = obj.getCollectionKind() + "";
        Property.isKey = obj.getIsKey();
        Property.uniqueKey = obj.getUniqueKey() + "";
        return Property;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Propertyを取得.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").schema.property.retrieve({"Name":"name","_EntityType.Name": "Profile"});
 * @param {Object} 取得対象のキー
 * @returns {dc.Property} 取得したPropertyオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.PropertyManager.prototype.retrieve = function(key) {
    var name = key["Name"];
    var entityTypeName = key["_EntityType.Name"];
    try {
        var obj = this.core.retrieve(name, entityTypeName);
        var Property = new dc.Property(obj);
        Property.name = obj.getName() + "";
        Property.entityTypeName = obj.getEntityTypeName() + "";
        Property.type = obj.getType() + "";
        Property.nullable = obj.getNullable();
        if (Property.type === "Edm.String") {
            Property.defaultValue = obj.getDefaultValue() + "";
        } else {
            Property.defaultValue = obj.getDefaultValue();
        }
        Property.collectionKind = obj.getCollectionKind() + "";
        Property.isKey = obj.getIsKey();
        Property.uniqueKey = obj.getUniqueKey() + "";
        return Property;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * Propertyを削除.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").schema.property.del({"Name":"name","_EntityType.Name": "Profile"});
 * @param {Object} key 削除対象のキー
 * @exception {dc.DcException} DAO例外
 */
dc.PropertyManager.prototype.del = function(key) {
    var name = key["Name"];
    var entityTypeName = key["_EntityType.Name"];
    try {
        this.core.del(name, entityTypeName);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * 新しいComplexTypePropertyManagerオブジェクトを作成する.
 * @class ComplexTypeProperty操作クラス
 * @augments dc.OData
 */
dc.ComplexTypePropertyManager = function(obj) {
    this.core = obj;
}
dc.ComplexTypePropertyManager.prototype = new dc.OData();

/**
 * ComplexTypePropertyを登録する.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").schema.complexTypeProperty.create({"Name": "PetName",
 * "_ComplexType.Name": "Profile","Type": "Edm.String"});
 * @param {Object} param ComplexTypeProperty作成に必要なJSON型オブジェクト
 * @returns {dc.ComplexTypeProperty} 作成したComplexTypePropertyオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.ComplexTypePropertyManager.prototype.create = function(param) {
    try {
        var obj = this.internalCreate(param);
        var Property = new dc.ComplexTypeProperty(obj);
        Property.name = obj.getName() + "";
        Property.complexTypeName = obj.getComplexTypeName() + "";
        Property.type = obj.getType() + "";
        Property.nullable = obj.getNullable();
        if (Property.type === "Edm.String") {
            Property.defaultValue = obj.getDefaultValue() + "";
        } else {
            Property.defaultValue = obj.getDefaultValue();
        }
        Property.collectionKind = obj.getCollectionKind() + "";
        return Property;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ComplexTypePropertyを取得.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").schema.complexTypeProperty.retrieve({"Name":"name","_ComplexType.Name": "Profile"});
 * @param {Object} 取得対象のキー
 * @returns {dc.ComplexTypeProperty} 取得したComplexTypePropertyオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.ComplexTypePropertyManager.prototype.retrieve = function(key) {
    var name = key["Name"];
    var complexTypeName = key["_ComplexType.Name"];
    try {
        var obj = this.core.retrieve(name, complexTypeName);
        var Property = new dc.ComplexTypeProperty(obj);
        Property.name = obj.getName() + "";
        Property.entityTypeName = obj.getComplexTypeName() + "";
        Property.type = obj.getType() + "";
        Property.nullable = obj.getNullable();
        if (Property.type === "Edm.String") {
            Property.defaultValue = obj.getDefaultValue() + "";
        } else {
            Property.defaultValue = obj.getDefaultValue();
        }
        Property.collectionKind = obj.getCollectionKind() + "";
        return Property;
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * ComplexTypePropertyを削除.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").schema.complexTypeProperty.del({"Name":"name","_ComplexType.Name": "Profile"});
 * @param {Object} key 削除対象のキー
 * @exception {dc.DcException} DAO例外
 */
dc.ComplexTypePropertyManager.prototype.del = function(key) {
    var name = key["Name"];
    var complexTypeName = key["_ComplexType.Name"];
    try {
        this.core.del(name, complexTypeName);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * トークンの取得.<br>
 * @returns {Object} 取得したトークン情報<br>
 * 以下のJSON形式<br>
 * <dd>access_token
 * <dd>expires_in
 * <dd>refresh_token
 * <dd>refresh_expires_in
 * <dd>token_type
 */
dc.Cell.prototype.getToken = function() {
    var ret = {};
    ret.access_token = this.core.getAccessToken() + "";
    ret.expires_in = this.core.getExpiresIn() + "";
    ret.refresh_token = this.core.getRefreshToken() + "";
    ret.refresh_token_expires_in = this.core.getRefreshExpiresIn() + "";
    ret.token_type = this.core.getTokenType() + "";
    return ret;
};

/**
 * 新しいQueryオブジェクトを生成する.
 * @class Query操作クラス
 */
dc.Query = function(obj) {
    this.core = obj;
}

/**
 * $filterクエリを指定.<br>
 * 例：<br>
 * <dl>
 * <dt>完全一致：
 * <dd>odata("odata").entitySet("entity").query().filter("name eq 'user'").run();
 * <dt>前方一致：
 * <dd>odata("odata").entitySet("entity").query().filter("startswith(itemKey,'searchValue')").run();
 * <dt>部分一致：
 * <dd>odata("odata").entitySet("entity").query().filter("substringof('searchValue1',itemKey1)").run();
 * <dt>より大きい：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey gt 1000").run();
 * <dt>以上：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey ge 1000").run();
 * <dt>より小さい：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey lt 1000").run();
 * <dt>以下：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey le 1000").run();
 * <dt>論理積：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey1 eq 'searchValue1' and itemKey2 eq 'searchValue2'").run();
 * <dt>論理和：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey1 eq 'searchValue1' or itemKey2 eq 'searchValue2'").run();
 * <dt>優先グループ：
 * <dd>odata("odata").entitySet("entity").query().filter("itemKey eq 'searchValue' or (itemKey gt 500 and itemKey lt 1500)").run();
 * @param {string} filter $filterクエリ
 * @returns {dc.Query} 自分自身(Query)
 */
dc.Query.prototype.filter = function(filter) {
    this.core.filter(filter);
    return this;
};

/**
 * $selectクエリを指定.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").query().select("name,age,type").run();
 * @param {string} select $selectクエリ
 * @returns {dc.Query} 自分自身(Query)
 */
dc.Query.prototype.select = function(select) {
    this.core.select(select);
    return this;
};

/**
 * $topクエリを指定.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").query().top(100).run();
 * @param {number} top $topクエリ
 * @returns {dc.Query} 自分自身(Query)
 */
dc.Query.prototype.top = function(value) {
    this.core.top(value);
    return this;
};

/**
 * $skipクエリを指定.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").query().skip(100).top(100).run();
 * @param {number} skip $skipクエリ
 * @returns {dc.Query} 自分自身(Query)
 */
dc.Query.prototype.skip = function(value) {
    this.core.skip(value);
    return this;
};

/**
 * $expandクエリを指定.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").query().expand("entityname").run();
 * @param {string} expand $expandクエリ
 * @returns {dc.Query} 自分自身(Query)
 */
dc.Query.prototype.expand = function(expand) {
    this.core.expand(expand);
    return this;
};

/**
 * $orderbyクエリを指定.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").query().orderby("fieldName asc").run();<br>
 * 　odata("odata").entitySet("entity").query().orderby("fieldName desc").run();<br>
 * @param {string} type $orderbyクエリ
 * @returns {dc.Query} 自分自身(Query)
 */
dc.Query.prototype.orderby = function(type) {
    this.core.orderby(type);
    return this;
};

/**
 * inlinecountクエリを指定.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").query().inlinecount("allpages").run();<br>
 * 　odata("odata").entitySet("entity").query().inlinecount("none ").run();<br>
 * @param {string} type $inlinecountクエリ
 * @returns {dc.Query} 自分自身(Query)
 */
dc.Query.prototype.inlinecount = function(type) {
    this.core.inlinecount(type);
    return this;
};

/**
 * qクエリを指定.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").query().q("key").run();
 * @param {string} value qクエリ
 * @returns {dc.Query} 自分自身(Query)
 */
dc.Query.prototype.q = function(value) {
     this.core.q(value);
     return this;
};

/**
 * ODataの検索を実行.<br>
 * 例：<br>
 * 　odata("odata").entitySet("entity").query().run();
 * @returns {Object} 検索結果JSONオブジェクト
 * @exception {dc.DcException} DAO例外
 */
dc.Query.prototype.run = function() {
    try {
    	var ret = this.core.run();
    	return JSON.parse(ret);
    } catch (e) {
        throw new dc.DcException(e.message);
    }
};

/**
 * DAO例外
 * @class DAOの例外クラス
 * @param {string} msg メッセージ
 * @property {string} message メッセージ
 * @property {string} code コード
 * @property {string} name 名前
 * @returns {dc.DcException} 例外オブジェクト
 */
dc.DcException = function(msg) {
    // JavaDAOからは、ステータスコードとレスポンスボディがカンマ区切りで通知される
    // よって、最初のカンマまでをステータスコードと判断し、それ以降をExceptionメッセージとする
    // また、以下のように、かならず、Javaのパッケージ名が先頭に含まれる
    // com.fujitsu.dc.client.DaoException: 409,{"code":"PR409-OD-0003","message":{"lang":"en","value":"The entity already exists."}}
    msg = msg.substring(msg.indexOf(" ")+1);
    this.message = msg;
    this.code = 0;
    this.name = "dc.DcException";
    var ar = msg.split(",");
    if (ar.length > 1) {
        this.code = parseInt(ar[0]);
        if (this.code == null) {
          this.code = 0;
        }
        this.message = ar.slice(1).join();
    }
};
dc.DcException.prototype = new Error();
