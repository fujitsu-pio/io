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
 * ACL設定の正常系バリエーションテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];
    var box = null;
    var role1 = null;

    try {
        // ボックス作成
        box = dc.as("client").cell(cellName).ctl.box.create({Name:"acltest", Schema:null});

        // Role作成
        role1 = dc.as("client").cell(cellName).ctl.role.create({Name:"role1"});

        // ACL設定_aceがnullの場合
        var aclData = {"requireSchemaAuthz":"public"};

        // ボックスにACL設定
        box.acl.set(aclData);
        // ボックスのACL取得
        var res = box.acl.get();

        var requireSchemaAuthz = res["requireSchemaAuthz"];

        // ボックスにACL設定
        box.acl.set(aclData);
        // ボックスのACL取得
        res = box.acl.get();

        requireSchemaAuthz = res["requireSchemaAuthz"];

        // assertionエラー（後処理を流したいのでこのタイミングでチェック）
        if (requireSchemaAuthz != "public"){
            return util.response().responseBody("requireSchemaAuthz value expected:<[public]> but was:<[" + requireSchemaAuthz + "]>");
        }
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        if (role1 !== null) {
            // Role削除
            dc.as("client").cell(cellName).ctl.role.del({Name:"role1"});
        }
        if (box !== null) {
            // ボックス削除
            dc.as("client").cell(cellName).ctl.box.del("acltest");
        }
    }
}