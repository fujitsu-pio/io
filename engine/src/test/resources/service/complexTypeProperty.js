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
 * ComplexTypePropertyのテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];
    
    var complexTypePropertyName = "complexTypePropertyName";
    var complexTypeName = {Name:"complexTypeName"};
    var json = {};
    json["Name"] = complexTypePropertyName;
    json["_ComplexType.Name"] = "complexTypeName";
    json["Type"] = "Edm.String";
    json["Nullable"] = false;
    json["DefaultValue"] = "testData";
    json["CollectionKind"] = "List";

    try {
        // ODataコレクション作成
        var box = dc.as("client").cell(cellName).box("boxname");
        box.mkOData("col");

        //ComplexTypeの作成
        box.odata("col").schema.complexType.create(complexTypeName);

        // ComplexTypePropertyの作成
        var property = box.odata("col").schema.complexTypeProperty.create(json);

        // 同じ名前のComplexTypePropertyを登録し、409になることを確認
        try {
            box.odata("col").schema.complexTypeProperty.create(json);
        } catch (e1) {
            if (e1.code != 409) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // ComplexTypePropertyの取得
        property = box.odata("col").schema.complexTypeProperty.retrieve(json);

        // ComplexTypePropertyの削除
        box.odata("col").schema.complexTypeProperty.del(json);

        //ComplexTypeの削除
        box.odata("col").schema.complexType.del(complexTypeName);

        // 作成したコレクションを削除する
        box.del("col");

        // レスポンスを返却
        return util.response().responseBody("OK").build();

    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}