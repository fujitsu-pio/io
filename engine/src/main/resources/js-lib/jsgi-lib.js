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
 * サービス呼び出し結果の情報を含むオブジェクト。
 * @this {JSGIResponse}
 */
function JSGIResponse() {

};

JSGIResponse.prototype = {
    /**
     * ステータスコード
     */
    status: 200,
    /**
     * レスポンスヘッダ
     */
    headers: {"Content-Type": "application/json;charset=utf-8"},
    /**
     * 返却データを設定する。
     */
    setResponseData: function(data) {
    	if(data === null) {
    		this.body = [];
    	}else if (this.headers["Content-Type"].indexOf("application/json", 0) != -1) {
            this.body = [JSON.stringify(data)];
        } else {
            this.body = [data];
        }
    },
    /**
     * レスポンスメッセージボディ
     */
    body:[]
};
