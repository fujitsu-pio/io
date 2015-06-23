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
 * Eventのテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];

    // テスト用データ作成
    var event = {level:"ERROR",action:"actionData",object:"objectData",result:"resultData"};

    try {
        // イベント受付
        dc.as("client").cell(cellName).event.post(event, "DaoTest");

        // --- ログ取得(X-Dc-RequestKeyなし)_String ---
        // ログ取得(X-Dc-RequestKeyなし)_String
        dc.as("client").cell(cellName).currentLog.getString("default.log");
        // --- ログ取得(X-Dc-RequestKeyあり)_String ---
        // ログ取得(X-Dc-RequestKeyあり)_String
        dc.as("client").cell(cellName).currentLog.getString("default.log","DaoTest");
        // --- ログ取得(X-Dc-RequestKeyなし)_Stream ---
        // ログ取得(X-Dc-RequestKeyなし)_Stream
        dc.as("client").cell(cellName).currentLog.getStream("default.log");
        // --- ログ取得(X-Dc-RequestKeyあり)_Stream ---
        // ログ取得(X-Dc-RequestKeyあり)_Stream
        dc.as("client").cell(cellName).currentLog.getStream("default.log","DaoTest");

        // ローテートされたログ取得(X-Dc-RequestKeyなし)_String
        // --- ローテートされたログ取得(X-Dc-RequestKeyなし)_String ---
        try{
            dc.as("client").cell(cellName).archiveLog.getString("default.log");
        } catch (e1) {
            if (e1.code != 404) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
        // ローテートされたログ取得(X-Dc-RequestKeyあり)_String
        // --- ローテートされたログ取得(X-Dc-RequestKeyあり)_String ---
        try{
            dc.as("client").cell(cellName).archiveLog.getString("default.log", "DaoTest");
        } catch (e2) {
            if(e2.code != 404){
                return util.response().statusCode(e2.code).responseBody(e2.message).build();
            }
        }
        // ローテートされたログ取得(X-Dc-RequestKeyなし)_Stream
        // --- ローテートされたログ取得(X-Dc-RequestKeyなし)_Stream ---
        try{
            dc.as("client").cell(cellName).archiveLog.getStream("default.log");
        } catch (e3) {
            if(e3.code != 404){
                return util.response().statusCode(e3.code).responseBody(e3.message).build();
            }
        }
        // ローテートされたログ取得(X-Dc-RequestKeyあり)_Stream
        // --- ローテートされたログ取得(X-Dc-RequestKeyあり)_Stream ---
        try{
            dc.as("client").cell(cellName).archiveLog.getStream("default.log", "DaoTest");
        } catch (e4) {
            if(e4.code != 404){
                return util.response().statusCode(e4.code).responseBody(e4.message).build();
            }
        }

        return util.response().responseBody("OK").build();
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}