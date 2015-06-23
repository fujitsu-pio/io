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
 * Relation - ExtCell リンクテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];

    var data = {Name:"relation"};
    try {
        // Relation作成
        var relation = dc.as("client").cell(cellName).ctl.relation.create(data);

        // Role作成
        data = {Name:"role"};
        var role = dc.as("client").cell(cellName).ctl.role.create(data);
        // Relation リンク
        role.relation.link(relation);
        // Relation リンク (409)
        try {
            role.relation.link(relation);
        } catch (e1) {
            if (e1.code != 409) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
        //アンリンク 
        role.relation.unLink(relation);
        //アンリンク (404)
        try {
            role.relation.unLink(relation);
        } catch (e1) {
            if (e1.code != 404) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // 作成したRelationを削除する
        dc.as("client").cell(cellName).ctl.relation.del({Name:"relation"});
        // 作成したRoleを削除する
        dc.as("client").cell(cellName).ctl.role.del({Name:"role"});
        
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}