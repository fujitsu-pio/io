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
    var colNameParent = "colParent";
    var colNameChild = "colChild";
    try {
        // DavDataコレクション作成
        dc.as("client").cell(cellName).box("boxname").mkCol(colNameParent);
        // DavDataコレクション２階層目の作成
        dc.as("client").cell(cellName).box("boxname").col(colNameParent).mkCol(colNameChild);
        
        // davのput (utf-8)
        dc.as("client").cell(cellName).box("boxname").col(colNameParent).col(colNameChild).put("test.txt", "text/plain", "あいうえお");
        // davのget (utf-8)
        var davString = dc.as("client").cell(cellName).box("boxname").col(colNameParent).col(colNameChild).getString("test.txt", "UTF-8");
        if (davString != "あいうえお") {
            return util.response().statusCode("404").responseBody("Invalid WebDAV").build();
        }

        // davのput (shift-jis)
        dc.as("client").cell(cellName).box("boxname").col(colNameParent).col(colNameChild).put({
            path: "test.txt",
            contentType: "text/plain",
            data: "かきくけこ",
            charset: "MS932",
            etag: "*"
         });
        // davのget (shift-jis)
        davString = dc.as("client").cell(cellName).box("boxname").col(colNameParent).col(colNameChild).getString("test.txt", "MS932");
        if (davString != "かきくけこ") {
            return util.response().statusCode("404").responseBody("Invalid WebDAV").build();
        }

        // putしたDavを削除する
        dc.as("client").cell(cellName).box("boxname").col(colNameParent).col(colNameChild).del("test.txt");

        // 作成したDavコレクションを削除する
        dc.as("client").cell(cellName).box("boxname").col(colNameParent).del(colNameChild);
        // 作成したDavコレクションを削除する
        dc.as("client").cell(cellName).box("boxname").del(colNameParent);
        
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}