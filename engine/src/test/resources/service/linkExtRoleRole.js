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
 * ExtRole - Role リンクテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];

    try {
        // Relation作成
        var relationData = {Name:"relation"};
        var relation = dc.as("client").cell(cellName).ctl.relation.create(relationData);

        // Role作成
        var roleData = {Name:"role"};
        var role = dc.as("client").cell(cellName).ctl.role.create(roleData);

        // ExtRole作成
        var strUrl = dc.as("client").cell(cellName + "1").getUrl();
        var extRoleData = {ExtRole:strUrl,"_Relation.Name":"relation"};
        var extrole = dc.as("client").cell(cellName).ctl.extRole.create(extRoleData);
        // ExtRole リンク
        extrole.role.link(role);
        // ExtRole リンク (409)
        try {
            extrole.role.link(role);
        } catch (e1) {
            if (e1.code != 409) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
        // ExtRoleアンリンク 
        extrole.role.unLink(role);
        // ExtRoleアンリンク (404)
        try {
            extrole.role.unLink(role);
        } catch (e1) {
            if (e1.code != 404) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
        
        // 作成したExtRoleを削除する
        dc.as("client").cell(cellName).ctl.extRole.del(extRoleData);

        // 作成したRoleを削除する
        dc.as("client").cell(cellName).ctl.role.del({Name:"role"});
        // 作成したRelationを削除する
        dc.as("client").cell(cellName).ctl.relation.del({Name:"relation"});
        
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}