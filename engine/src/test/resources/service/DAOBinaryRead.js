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
 * リクエストで受け付けたデータから１行取得、全行取得を実行しレスポンスとして返却する。
 */
function(request){
	var res = "";
	try{
		// １行取得。改行が失われるので改行追加
		res = request['input'].readLine("utf-8") + "\n";
		// 次の１行取得。改行が失われるので改行追加
		res = res + request['input'].readLine() + "\n";
		// 残り全てを取得
		res = res + request['input'].readAll("utf-8");
		// １行取得と残り全行取得を結合して返すとリクエストとして受け取った文字列にもどってるはず
	} catch (e){
		res = e.message;
	} finally {
	}
	return {
	    status: 200,
	    headers: {"X-Dc-Response":"text/plain"},
	    body: [res]
	}
}
