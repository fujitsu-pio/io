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
 * requireしたファイルが存在しなかった場合のテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    var code = 500;
    var message = "NG";
    try {
        // require失敗するスクリプトのrequire
        var ngrequire = require("hoge");
    } catch (e) {
    	if (e.message !== "com.fujitsu.dc.engine.DcEngineException: 404 Not Found") {
        	code = e.code;
        	message = e.message;
    	    return util.response().statusCode(code).responseBody(message).build();
    	}
    }
    // レスポンスを返却
    return util.response().responseBody("OK").build();
}


