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
 * ユーザデータの$expandクエリのテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];

    var etKeeper = null;
    var etAnimal = null;
    var aeKeeper = null;
    var aeAnimal = null;
    var keeperId = null;
    var animalId = null;
    var json = {};

    try {
        // ◆ ODataコレクション作成
        var box = dc.as("client").cell(cellName).box("boxname");
        box.mkOData("odata");
        
        // ◆ EntityType (keeper)
        etKeeper = box.odata("odata").schema.entityType.create({Name:"etKeeper"});

        // ◆ EntityType (animal)
        etAnimal = box.odata("odata").schema.entityType.create({Name:"etAnimal"});

        // ◆ AssociationEnd (keeper)
        json["Name"] = "aeKeeper";
        json["Multiplicity"] = "1";
        json["_EntityType.Name"] = "etKeeper";
        aeKeeper = box.odata("odata").schema.associationEnd.create(json);

        // ◆ AssociationEnd (animal)
        json = {};
        json["Name"] = "aeAnimal";
        json["Multiplicity"] = "*";
        json["_EntityType.Name"] = "etAnimal";
        aeAnimal = box.odata("odata").schema.associationEnd.create(json);

        // ◆ AssociationEnd $links
        aeKeeper.associationEnd.link(aeAnimal);

        // ◆ keeperユーザデータ登録
        json = box.odata("odata").entitySet("etKeeper").create({"name":"testkeeper","age":"65"});
        keeperId = json["__id"];
        
        // ◆ animalユーザデータ登録
        json = box.odata("odata").entitySet("etKeeper").key(keeperId).nav("etAnimal")
                                            .create({"name":"testanimal","species":"dog"});
        animalId = json["__id"];

        // ◆ animalユーザデータ取得.
        json = box.odata("odata").entitySet("etAnimal").retrieve(animalId);
        
        // ◆ keeperユーザデータ取得($expandでetAnimalを指定).
        json = box.odata("odata").entitySet("etKeeper").query().expand("_etAnimal").run();

        // ◆keeperユーザデータ一件取得($expandでetAnimalを指定).
        json = box.odata("odata").entitySet("etKeeper").key(keeperId).query().expand("_etAnimal").run();
        
        // ◆animalに紐付くkeeperユーザデータ取得($expandでetAnimalを指定).
        json = box.odata("odata").entitySet("etAnimal").key(animalId).nav("etKeeper").query().expand("_etAnimal").run();
        
        // ◆ animalユーザデータ取得($expandでetKeeperを指定).
        json = box.odata("odata").entitySet("etAnimal").query().expand("_etKeeper").run();
        
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        // ◆ animalユーザデータ削除
        if (animalId != null) {
            box.odata("odata").entitySet("etAnimal").del(animalId);
        }
        // ◆ keeperユーザデータ削除
        if (keeperId != null) {
            box.odata("odata").entitySet("etKeeper").del(keeperId);
        }
        // ◆ $unLink AND AssociationEnd (animal)削除
        if (aeAnimal != null) {
            aeKeeper.associationEnd.unLink(aeAnimal);
            json = {};
            json["Name"] = "aeAnimal";
            json["Multiplicity"] = "*";
            json["_EntityType.Name"] = "etAnimal";
            box.odata("odata").schema.associationEnd.del(json);
        }
        // ◆ AssociationEnd (keeper)削除
        if (aeKeeper != null) {
            json = {};
            json["Name"] = "aeKeeper";
            json["Multiplicity"] = "1";
            json["_EntityType.Name"] = "etKeeper";
            box.odata("odata").schema.associationEnd.del(json);
        }
        // ◆ EntityType (animal)削除
        if (etKeeper != null) {
            box.odata("odata").schema.entityType.del("etKeeper");
        }
        // ◆ EntityType (keeper)削除
        if (etAnimal != null) {
            box.odata("odata").schema.entityType.del("etAnimal");
        }
        // ◆ ODataコレクションの削除
        box.del("odata");
    }
}