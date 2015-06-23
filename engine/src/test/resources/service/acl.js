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
 * ACLのテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];
    
    try {
        // ボックス作成
        var box = dc.as("client").cell(cellName).ctl.box.create({Name:"acltest", Schema:null});
        
     // Role作成
        var role1 = dc.as("client").cell(cellName).ctl.role.create({Name:"role1"});
        var role2 = dc.as("client").cell(cellName).ctl.role.create({Name:"role2"});

        var aclData = {"requireSchemaAuthz":"public","ace":[{"role":role1,"privilege":["read","write"]},{"role":role2,"privilege":["read","read-acl"]}]};
        var cellAclData = {"ace":[{"role":role1,"privilege":["auth-read","auth"]},{"role":role2,"privilege":["acl","acl-read"]}]};

        // ボックスにACL設定
        box.acl.set(aclData);

        // セルにACL設定
        dc.as("client").cell(cellName).acl.set(cellAclData);
        
        // ボックスのACL取得
        var resBoxAcl = box.acl.get();

        // セルのACL取得
        var resCellAcl = dc.as("client").cell(cellName).acl.get();

        var requireSchemaAuthz1 = resBoxAcl["requireSchemaAuthz"];
        var requireSchemaAuthz2 = resCellAcl["requireSchemaAuthz"];

//        // コレクション作成
//        box.mkCol("col");
//
//        // 作成したコレクションにACLを設定
//        box.col("col").acl.set(acl);
//        
//        // 設定したACLを取得
//        var res = box.col("col").acl.get();
//        
//        // コレクション削除
//        box.del("col");

        // Role削除
        dc.as("client").cell(cellName).ctl.role.del({Name:"role1"});
        dc.as("client").cell(cellName).ctl.role.del({Name:"role2"});
        
        // ボックス削除
        dc.as("client").cell(cellName).ctl.box.del("acltest");
        
        // assertionエラー（後処理を流したいのでこのタイミングでチェック）
        if (requireSchemaAuthz1 != "public"){
            return util.response().responseBody("requireSchemaAuthz value expected:<[public]> but was:<[" + requireSchemaAuthz1 + "]>");
        }

        if (requireSchemaAuthz2 != ""){
            return util.response().responseBody("requireSchemaAuthz value expected:<[]> but was:<[" + requireSchemaAuthz2 + "]>");
        }
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}