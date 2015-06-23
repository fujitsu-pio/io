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
 * ExtRoleのCRUDテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];
    var relationData = {Name:"relation","_Box.Name":"boxName"};

    var data = {ExtRole:"http://extrole/jp", "_Relation.Name":"relation","_Relation._Box.Name":"boxName"};
    
    try {
        // Box作成
        dc.as("client").cell(cellName).ctl.box.create({Name:"boxName", Schema:null});
        // Relation作成
        var relation = dc.as("client").cell(cellName).ctl.relation.create(relationData);

        // ExtRole作成
        var extrole = dc.as("client").cell(cellName).ctl.extRole.create(data);
        
        // ExtRoleをRelation指定無しで取得しエラーとなること
        try {
            dc.as("client").cell(cellName).ctl.extRole.retrieve({ExtRole:"http://extrole/jp"});
        } catch (e1) {
            if (e1.code != 404) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // ExtRoleをRelation指定無しで削除しエラーとなること
        try {
            dc.as("client").cell(cellName).ctl.extRole.del({ExtRole:"http://extrole/jp"});
        } catch (e1) {
            if (e1.code != 404) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // 作成したExtRoleを削除する
        dc.as("client").cell(cellName).ctl.extRole.del(data);
        
        // 作成したRelationを削除する
        dc.as("client").cell(cellName).ctl.relation.del(relationData);
        
        // Box削除
        dc.as("client").cell(cellName).ctl.box.del("boxName");

        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}