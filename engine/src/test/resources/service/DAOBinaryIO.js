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
 * リクエストで受け付けたデータをストリームでpersonium.ioに登録し、ストリームで取得し、リクエスト元に返す。
 */
function(request){
	var res = "";
	try{
	    var stream = request['input'].stream();
	    dc.as("client").cell().box().put({path:"test.txt",contentType:"text/plain",data:stream,etag:"*"});
	    res = dc.as("client").cell().box().getStream("test.txt");
	} catch (e){
		res = "NG";
	} finally {
	    dc.as("client").cell().box().del("test.txt")
	}
	return {
	    status: 200,
	    headers: {"Content-Type":"text/plain"},
	    body: [res]
	}
}
