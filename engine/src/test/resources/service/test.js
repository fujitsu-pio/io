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
function(request){
    
    var query = dc.util.queryParse(request.queryString);
    var mode = query["mode"];
    if (mode === "test") {
        return {
            status: 200,
            headers: {"Content-Type":"text/html"},
            body: ["test"]
        };
    }else if (mode === "readAll") {
            var br = request["input"];
            var buff = br.readAll();
            return {
                status: 200,
                headers: {"Content-Type":"text/html"},
                body: [buff]
            };
    }else if (mode === "readLine") {
        var br = request["input"];
        var buff = br.readLine();
        return {
            status: 200,
            headers: {"Content-Type":"text/html"},
            body: [buff]
        };
    }else if(mode === "move"){
        return {
            status: 302,
            headers: {"Location":"/dc-engine/r/jsgi_test"},
            body: [""]
        };
    }else if(mode === "auth"){
        return {
            status: 401,
            headers: {"Content-Type":"text/html"},
            body: [""]
        };
    }else if(mode === "each"){
        var array = ["a","b","c"];
        var tmp = "";
        array.forEach(function(answer){
            tmp += answer;
        });
        return {
            status: 200,
            headers: {"Content-Type":"text/html"},
            body: [tmp]
        };
    }else if(mode === "json"){
        var s = "{\"a\":\"json-value\"}";
        var obj = JSON.parse(s);
        return {
            status: 200,
            headers: {"Content-Type":"text/html"},
            body: [obj.a]
        };
    }else{
        var out = "<html>";
        out = out + print(request);
        out = out + JSON.stringify(request.headers);
        out = out + "</html>";
        return {
            status: 200,
            headers: {"Content-Type":"text/html"},
            body: ["<h1>Hello World</h1>" + out]
        };
    };
};

function print(obj){
    var out = "";
    for (i in obj){
        if ((typeof(obj[i]) === "object") && (i !== "input")){
            out = out + print(obj[i]);
        } else if (i === "input") {
            var br = obj["input"];
            var buff = br.readAll();
            out = out + i + ":" + buff + "<br>";
        } else if (i == "queryString") {
            out = out + i + ":" + obj[i] + " " + typeof(obj[i]) +"<br>";
            var query = dc.util.queryParse(obj.queryString);
            for (i in query) {
                out = out + "query[" + i + "]:" + query[i] + " " + typeof(query[i]) +"<br>";
            };
        }else{
            out = out + i + ":" + obj[i] + " " + typeof(obj[i]) +"<br>";
        };
    };
    return out;
};