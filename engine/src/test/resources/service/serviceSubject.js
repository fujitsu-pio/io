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
 * MKCOLのテスト.
 */
function(request){
    // 共通モジュール読み込み
    var util = require("testCommon");

    // クエリを解析し、Cell名を取得する
    var query = dc.util.queryParse(request.queryString);
    var cellName = query["cell"];

    var collectionName1 = "col1";
    var collectionName2 = "col2";
    var role;
	var roleData = {Name:"role"}
    var account;
    try {
        // まずはテストの準備
        // コレクション作成
        dc.as("client").cell(cellName).box("boxname").mkCol(collectionName1);
        var col = dc.as("client").cell(cellName).box("boxname").col(collectionName1);

        // ロール作成
        role = dc.as("client").cell(cellName).ctl.role.create(roleData);
    	
    	// engineアカウントにロール結びつけ設定
        account = dc.as("client").cell(cellName).ctl.account.retrieve("engine");
    	role.account.link(account);

    	// boxにACL設定
        var aclData = {"requireSchemaAuthz":"","ace":[{"role":role,"privilege":["read","write"]}]};
        col.acl.set(aclData);
        // テスト準備完了

        // コレクション作成(サービスサブジェクトが使えることの確認)
        dc.as("serviceSubject").cell(cellName).box("boxname").col(collectionName1).mkCol(collectionName2);
 
        // レスポンスを返却
        return util.response().responseBody("OK").build();
        
    } catch (e) {
        return util.response().statusCode(e.code).responseBody(e.message).build();
    } finally {
        // 後処理
    	// 作成したコレクションを削除する
        try{
        	dc.as("client").cell(cellName).box("boxname").col(collectionName1).del(collectionName2);
        } catch (e) {
        }
        try{
        	dc.as("client").cell(cellName).box("boxname").del(collectionName1);
        } catch (e) {
        }
        try{
        	// ロールとアカウントのリンクの削除
        	role.account.unLink(account);
        } catch (e) {
        }
        try{
            // ロール削除
            dc.as("client").cell(cellName).ctl.role.del(roleData);
        } catch (e) {
        }
    }
}