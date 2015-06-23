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
 * Propertyのテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];
    
    var propertyName = "propertyName";
    var entityTypeName = {Name:"keeper"};
    var json = {};
    json["Name"] = propertyName;
    json["_EntityType.Name"] = "keeper";
    json["Type"] = "Edm.String";
    json["Nullable"] = false;
    json["DefaultValue"] = "testData";
    json["CollectionKind"] = "List";
    json["IsKey"] = false;
    json["UniqueKey"] = "unique";

    try {
        // ODataコレクション作成
        var box = dc.as("client").cell(cellName).box("boxname");
        box.mkOData("col");

        //EntityTypの作成
        box.odata("col").schema.entityType.create(entityTypeName);

        // Propertyの作成
        var property = box.odata("col").schema.property.create(json);

        // 同じ名前のPropertyを登録し、409になることを確認
        try {
            box.odata("col").schema.property.create(json);
        } catch (e1) {
            if (e1.code != 409) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // Propertyの取得
        property = box.odata("col").schema.property.retrieve(json);

        // Propertyの削除
        box.odata("col").schema.property.del(json);

        //EntityTypの削除
        box.odata("col").schema.entityType.del("keeper");

        // 作成したコレクションを削除する
        box.del("col");

        // レスポンスを返却
        return util.response().responseBody("OK").build();

    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}