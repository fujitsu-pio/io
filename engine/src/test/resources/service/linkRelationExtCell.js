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

        // ExtCell作成
        var strUrl = dc.as("client").cell(cellName + "1").getUrl();
        data = {Url:strUrl};
        var extcell = dc.as("client").cell(cellName).ctl.extCell.create(data);
        // Relation リンク
        relation.extCell.link(extcell);
        // Relation リンク (409)
        try {
            relation.extCell.link(extcell);
        } catch (e1) {
            if (e1.code != 409) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
        //アンリンク 
        relation.extCell.unLink(extcell);
        //アンリンク (404)
        try {
            relation.extCell.unLink(extcell);
        } catch (e1) {
            if (e1.code != 404) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // 作成したRelationを削除する
        dc.as("client").cell(cellName).ctl.relation.del({Name:"relation"});
        // 作成したExtCellを削除する
        dc.as("client").cell(cellName).ctl.extCell.del(extcell.url);
        
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}