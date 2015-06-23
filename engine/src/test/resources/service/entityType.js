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
 * EntityTypeのテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];
    var entityTypeName = {Name:"entityType"};
    var collectionName = "odatacol";
    try {
        // ODataコレクション作成
        dc.as("client").cell(cellName).box("boxname").mkOData(collectionName);
        
        // entityTypeの作成
        dc.as("client").cell(cellName).box("boxname").odata(collectionName).entityTypes.create(entityTypeName);
        
        // 同じ名前のentityTypeを登録し、409になることを確認
        try {
            dc.as("client").cell(cellName).box("boxname").odata(collectionName).entityTypes.create(entityTypeName);
        } catch (e1) {
            if (e1.code != 409) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
        // entityTypeの取得
        dc.as("client").cell(cellName).box("boxname").odata(collectionName).entityTypes.retrieve("entityType");
        // entityTypeの削除
        dc.as("client").cell(cellName).box("boxname").odata(collectionName).entityTypes.del("entityType");

        // 作成したコレクションを削除する
        dc.as("client").cell(cellName).box("boxname").del(collectionName);
        
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}