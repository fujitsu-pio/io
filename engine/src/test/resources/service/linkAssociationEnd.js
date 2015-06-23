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
 * AssociationEnd リンクテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];

    var data = {Name:"relation"};

    var associationNameKeeper = "associationNameKeeper";
    var associationNameAnimal = "associationNameAnimal";
    var multiplicity = "*";
    var jsonKeeper = {};
    jsonKeeper["Name"] = associationNameKeeper;
    jsonKeeper["Multiplicity"] = "*";
    jsonKeeper["_EntityType.Name"] = "keeper";

    try {

        // ODataコレクション作成
        var box = dc.as("client").cell(cellName).box("boxname");
        box.mkOData("col");
        //EntityTypの作成 keeper
        box.odata("col").schema.entityType.create({Name:"keeper"});
        //EntityTypの作成 Animal
        box.odata("col").schema.entityType.create({Name:"animal"});

        // AssociationEndの作成
        var aeKeeper = box.odata("col").schema.associationEnd.create(jsonKeeper);

        var jsonAnimal = {};
        jsonAnimal["Name"] = associationNameAnimal;
        jsonAnimal["Multiplicity"] = "*";
        jsonAnimal["_EntityType.Name"] = "animal";
        var aeAnimal = box.odata("col").schema.associationEnd.create(jsonAnimal);
        // リンク
        aeKeeper.associationEnd.link(aeAnimal);
        // リンク (409)
        try {
            aeKeeper.associationEnd.link(aeAnimal);
        } catch (e1) {
            if (e1.code != 409) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
        //アンリンク 
        aeKeeper.associationEnd.unLink(aeAnimal);
        //アンリンク (404)
        try {
            aeKeeper.associationEnd.unLink(aeAnimal);
        } catch (e1) {
            if (e1.code != 404) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }

        // AssociationEndの削除
        box.odata("col").schema.associationEnd.del(jsonAnimal);
        box.odata("col").schema.associationEnd.del(jsonKeeper);

        //EntityTypの削除
        box.odata("col").schema.entityType.del("keeper");
        //EntityTypの削除
        box.odata("col").schema.entityType.del("animal");

        // 作成したコレクションを削除する
        box.del("col");
        
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}