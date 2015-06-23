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
exports.response = function() {
    return new JSResponse();
}

/**
 * Response用クラス.
 * @class JSResponse
 * @property {Array} body レスポンスボディ
 * @property {Number} status ステータスコード
 * @returns
 */
function JSResponse() {
    this.body = [];
    this.originalBody = [];
    this.status = 200;
    this.CONTENTTYPE_HTML = "text/html; charset=UTF-8;";
    this.type = this.CONTENTTYPE_HTML; // デフォルト
    this.view = null;
}

/**
 * ReponseBodyを追加.
 * @memberOf JSResponse#
 * @param {String} ReponseBody レスポンスボディ
 * @returns {JSResponse} 自分自身のJSRresponseオブジェクト(メソッドチェーン用)
 */
JSResponse.prototype.responseBody = function(value) {
    this.originalBody.push(value);
    if (typeof value == "string") {
        this.body.push(value);
    } else {
        this.body.push(JSON.stringify(value));
    }
    return this;
};

/**
 * StatusCode をセット.
 * @memberOf JSResponse#
 * @param {Number} value ステータスコード
 * @returns {JSResponse} 自分自身のJSRresponseオブジェクト(メソッドチェーン用)
 */
JSResponse.prototype.statusCode = function(value) {
    if (value) {
        this.status = value;
    }
    return this;
};

/**
 * ContentTypeをセット.
 * @memberOf JSResponse#
 * @param {String} value ContentType値
 * @returns {JSResponse} 自分自身のJSRresponseオブジェクト(メソッドチェーン用)
 */
JSResponse.prototype.contentType = function(value) {
    this.type = value;
    return this;
};

/**
 * JSGIのResponse形式オブジェクトを生成.
 * @memberOf JSResponse#
 * @returns {Object} Response形式のハッシュオブジェクト
 */
JSResponse.prototype.build = function() {
    var ret = {};
    ret.status = this.status;
    ret.headers = {"Content-Type": this.type};
    if (this.view == null) {
        ret.body = this.body;
    } else {
        ret.body =[this.view.render(this.originalBody[0])]
    }
    return ret;
};