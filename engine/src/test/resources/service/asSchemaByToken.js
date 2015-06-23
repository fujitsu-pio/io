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
 * Accessorのスキーマ付きトークン認証テスト.
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
    	var cell = dc.as(json).cell(cellName + "1");
    	
    	var token = cell.getToken();

        json = {cellUrl:cellName, accessToken:token.access_token,
        		schemaUrl:cellName + "2", schemaUserId:"user001", schemaPassword:"pass001"};
    	// トークン認証
    	dc.as(json).cell();

    	// レスポンスを返却
        return util.response().responseBody("OK").build();

    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        
    }
}