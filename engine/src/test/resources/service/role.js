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
 * RoleのCRUDテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];

    var data = {Name:"role"};
    try {
        // Role作成
        var role = dc.as("client").cell(cellName).ctl.role.create(data);

        // 同じ名前のRoleを登録し、409になることを確認
        try {
        	dc.as("client").cell(cellName).ctl.role.create(data);
        } catch (e1) {
            if (e1.code != 409) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // 作成したRoleを取得
        role = dc.as("client").cell(cellName).ctl.role.retrieve({Name:"role"});

        // 作成したRoleを削除する
        dc.as("client").cell(cellName).ctl.role.del({Name:"role"});
        
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}