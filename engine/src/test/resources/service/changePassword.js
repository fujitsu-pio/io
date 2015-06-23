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
 * changePasswordテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");
    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];

    var user = {Name:"user"};
    var password = "password";
    var newPassword = "newpassword";

    var auth = {cellUrl:cellName, userId:"user", password:password};
    var newAuth = {cellUrl:cellName, userId:"user", password:newPassword};

    try {
        // アカウント作成
        var account = dc.as("client").cell(cellName).ctl.account.create(user, password);
        // 認証 asExtCell
        dc.as(auth).cell();
        // Password変更
        dc.as("client").cell(cellName).ctl.account.changePassword("user", newPassword);
        // 認証 asExtCell 400チェック
        try {
            dc.as(auth).cell();
        } catch (e1) {
            if (e1.code != 400) {
                return util.response().statusCode(e1.code).responseBody(e1.message).build();
            }
        }
        // アカウントロックの時間(1秒)だけスリープする
        sleep(1);

        // 認証 asExtCell
        dc.as(newAuth).cell();

        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        // 作成したアカウントを削除する
        dc.as("client").cell(cellName).ctl.account.del(account.name);
        
    }
}

function sleep( T ){ 
    var d1 = new Date().getTime(); 
    var d2 = new Date().getTime(); 
    while( d2 < d1+1000*T ){    //T秒待つ 
        d2=new Date().getTime(); 
    } 
    return; 
}
