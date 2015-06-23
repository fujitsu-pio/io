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
 * Accessorのリフレッシュトークン認証テスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");
    var responseBody = "";
    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];

    var json = {cellUrl:cellName, userId:"user001", password:"pass001"};
    try {
        var cell = dc.as(json).cell();
        
        var token = cell.getToken();

        // 認証エラー時のテスト (#14462の確認テスト)
        try {
            json = {cellUrl:cellName, refreshToken:"bad data"};
            // リフレッシュトークン認証時にエラー発生
            dc.as(json).cell();
        } catch (e1) {
            //return util.response().statusCode(e1.code).responseBody(e1.message).build();
        }

        json = {cellUrl:cellName, refreshToken:token.refresh_token};
        // リフレッシュトークン認証
        dc.as(json).cell();

        // レスポンスを返却
        return util.response().responseBody("OK").build();

    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}