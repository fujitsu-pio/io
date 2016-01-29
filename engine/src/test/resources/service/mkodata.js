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
 * MKODATAのテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];

    var collectionName = "odatacol";
    try {
        // コレクション作成
        var account = dc.as("client").cell(cellName).box("boxname").mkOData(collectionName);
        
        // 同じ名前のコレクションを登録し、409になることを確認
        try {
            dc.as("client").cell(cellName).box("boxname").mkOData(collectionName);
        } catch (e1) {
            if (e1.code != 405) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
        
        // 作成したコレクションを削除する
        dc.as("client").cell(cellName).box("boxname").del(collectionName);
        
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}