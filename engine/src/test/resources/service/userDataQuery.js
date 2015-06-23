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
    var id01 = null;
    var id02 = null;
    var id03 = null;
    var id04 = null;
    var id05 = null;
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
        json = animal.create({"name":"pochi","species":"doc","age":3,"memo":"aaaa bbb","cute":true,"point":"04","food":"野菜"});
        id01 = json["__id"];

        // ◆ ユーザーデータ登録（2件目)
        json = animal.create({"name":"tama","species":"cat","age":10,"memo":"baaa bbc","cute":false,"point":"3","food":"肉"});
        id02 = json["__id"];

        // ◆ ユーザーデータ登録（3件目)
        json = animal.create({"name":"taro","species":"doc","age":2,"memo":"caaa ccc","cute":true,"point":"2","food":"魚"});
        id03 = json["__id"];

        // ◆ ユーザーデータ登録（4件目)
        json = animal.create({"name":"jiro","species":"doc","age":4,"memo":"daaa add","cute":false,"point":"1","food":"野菜"});
        id04 = json["__id"];

        // 一覧取得($filter)
        // ◆ 一覧を取得(全件)
        json = animal.query().run();



        // ◆ 一覧取得 文字列 startswith
        json = animal.query().filter("startswith(memo,'ca')").run();
        // ◆ 一覧取得 文字列 substringof
        json = animal.query().filter("substringof('ccc',memo)").run();
        // ◆ 一覧取得 文字列 eq
        json = animal.query().filter("point eq '2'").run();
        // ◆ 一覧取得 文字列 gt
        json = animal.query().filter("point gt '2'").run();
        // ◆ 一覧取得 文字列 ge
        json = animal.query().filter("point ge '3'").run();
        // ◆ 一覧取得 文字列 lt
        json = animal.query().filter("point lt '1'").run();
        // ◆ 一覧取得 文字列 le
        json = animal.query().filter("point le '04'").run();

        // ◆ 一覧取得 数値 eq and boolean
        json = animal.query().filter("age eq 2 and cute eq true").run();
        // ◆ 一覧取得 文字列 eq or boolean
        json = animal.query().filter("point le '1' or cute eq true").run();
        
        // ◆ 一覧取得 全文検索 q=taro
        json = animal.query().q("taro").run();

        // ◆ 一覧取得 全文検索 q=野菜
        json = animal.query().q("野菜").run();

        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        // ◆◆ finaly
        // ◆ ユーザデータ削除
        if (id01 != null) {
            box.odata("odata").entitySet("animal").del(id01);
        }
        // ◆ ユーザデータ削除
        if (id02 != null) {
            box.odata("odata").entitySet("animal").del(id02);
        }
        // ◆ ユーザデータ削除
        if (id03 != null) {
            box.odata("odata").entitySet("animal").del(id03);
        }
        // ◆ ユーザデータ削除
        if (id04 != null) {
            box.odata("odata").entitySet("animal").del(id04);
        }
        // ◆ EntityType削除
        if (et != null) {
            box.odata("odata").schema.entityType.del("animal");
        }
        // ◆ ODataコレクション削除
        box.del("odata");
    }
}