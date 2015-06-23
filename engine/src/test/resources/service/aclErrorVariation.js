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
 * ACL設定の異常系バリエーションテスト.
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

        // ACL設定_ace_privilegeの配列が空の場合
        aclData = {"requireSchemaAuthz":"public","ace":[{"role":role1,"privilege":[""]}]};

        try {
            // ボックスにACL設定
            box.acl.set(aclData);
        } catch (e1) {
            if (e1.code != 400) {
            	return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // ACL設定_ace_roleがnullの場合
        aclData = {"requireSchemaAuthz":"public","ace":[{"role":null,"privilege":["read","write"]}]};

        try {
            // ボックスにACL設定
            box.acl.set(aclData);
        } catch (e1) {
            if (e1.code != 400) {
            	return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // ACL設定_ace_roleが空の場合
        aclData = {"requireSchemaAuthz":"public","ace":[{"role":"","privilege":["read","write"]}]};

        try {
            // ボックスにACL設定
            box.acl.set(aclData);
        } catch (e1) {
            if (e1.code != 400) {
            	return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // ACL設定_aceが空の場合
        aclData = {"requireSchemaAuthz":"public","ace":""};
    
        try {
            // ボックスにACL設定
            box.acl.set(aclData);
            // ボックスのACL取得
            res = box.acl.get();
        } catch (e1) {
            if (e1.code != 400) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
    
        // ACL設定_aceの配列がnullの場合
        aclData = {"requireSchemaAuthz":"public","ace":[]};
    
        try {
            // ボックスにACL設定
            box.acl.set(aclData);
            // ボックスのACL取得
            res = box.acl.get();
        } catch (e1) {
            if (e1.code != 400) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
    
        // ACL設定_ace_privilegeが空の場合
        aclData = {"requireSchemaAuthz":"public","ace":[{"role":role1,"privilege":""}]};
    
        try {
            // ボックスにACL設定
            box.acl.set(aclData);
            // ボックスのACL取得
            res = box.acl.get();
        } catch (e1) {
            if (e1.code != 400) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
    
        // ACL設定_ace_privilegeの配列がnullの場合
        aclData = {"requireSchemaAuthz":"public","ace":[{"role":role1,"privilege":[]}]};
    
        try {
            // ボックスにACL設定
            box.acl.set(aclData);
            // ボックスのACL取得
            res = box.acl.get();
        } catch (e1) {
            if (e1.code != 400) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
    
        // ACL設定_ace_privilegeが配列ではない場合
        aclData = {"requireSchemaAuthz":"public","ace":[{"role":role1,"privilege":"read"}]};
        try {
            // ボックスにACL設定
            box.acl.set(aclData);
            // ボックスのACL取得
            res = box.acl.get();
        } catch (e1) {
            if (e1.code != 400) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
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