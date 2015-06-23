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
 * ユーザーデータのテスト
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];
    var pochi = null;
    var tama = null;
    var et = null;
    var box = dc.as("client").cell(cellName).box("boxname");
    try {
        // ◆ ODataコレクション作成
        box.mkOData("odata");

        // ◆ EntityType登録
        data = {"Name":"animal"};
        et = box.odata("odata").schema.entityType.create(data);
        animal = box.odata("odata").entitySet("animal");

        // ◆ ユーザーデータ登録（１件目)
        json = animal.create({"name":"pochi","species":"cat"});
        pochi = json["__id"];

        // ◆ ユーザーデータ登録（2件目)
        json = animal.create({"name":"tama","species":"cat"});
        tama = json["__id"];

        // 登録したデータを取得
        // ◆ 登録したユーザーデータを取得(１件取得)
        json = animal.retrieve(pochi);

        // 更新
        // ◆ユーザーデータを更新
        json = animal.update(pochi, {"name":"pochi","species":"dog"}, "*");

        // 一覧取得($top)
        // ◆ 登録したユーザーデータ一覧を取得(１件($top=1)のみ取得)
        json = animal.query().top(1).run();
            
        // 一覧取得($filter)
        // ◆ 一覧を取得(species eq cat)
        json = animal.query().filter("species eq 'cat'").run();

        // 一覧取得($filter)
        // ◆ 一覧を取得(species eq dog)
        json = animal.query().filter("species eq 'dog'").run();


        // SKIP
        // ◆ 一覧取得 skip=1
        json = animal.query().skip(1).run();

        // inlinecount
        // ◆ 一覧取得 inlinecount=allpages
        json = animal.query().inlinecount("allpages").run();

        // ◆ 一覧取得 inlinecount=none
        json = animal.query().inlinecount("none").run();

//        // ｓｅｌｅｃｔ
//        // ◆ 一覧取得 ｓｅｌｅｃｔ=name
//        json = animal.query().select("name").run();

//        // orderby
//        // ◆ 一覧取得 orderby=name
//        json = animal.query().orderby("name").run();
//
//        // ◆ 一覧取得 orderby=name desc
//        json = animal.query().orderby("name desc").run();
        
        // 部分更新
        // ◆部分更新
        json = animal.merge(pochi, {"name":"hachi"}, "*");
        
        // 部分更新したデータを取得
        // ◆ 登録したユーザーデータを取得(部分更新の確認)
        json = animal.retrieve(pochi);

        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        // ◆ finaly
        // ◆ ユーザデータ削除
        if (pochi != null) {
            box.odata("odata").entitySet("animal").del(pochi);
        }
        // ◆ ユーザデータ削除
        if (tama != null) {
            box.odata("odata").entitySet("animal").del(tama);
        }
        // ◆ EntityType削除
        if (et != null) {
            box.odata("odata").schema.entityType.del("animal");
        }
        // ◆ ODataコレクション削除削除
        box.del("odata");
    }
}