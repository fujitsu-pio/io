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
 * Javaライブラリが呼び出せないこと.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    var code = 500;
    var message = "NG";
    try {
        new java.lang.String("javaString");
    } catch (e) {
    	if (e.message === "[JavaPackage java.lang.String] is not a function, it is object.") {
    	    // レスポンスを返却
    	    return util.response().responseBody("OK").build();
    	}
    	code = e.code;
    	message = e.message;
    } finally {
    }
    return util.response().statusCode(code).responseBody(message).build();
}