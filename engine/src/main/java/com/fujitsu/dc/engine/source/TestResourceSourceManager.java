/**
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
package com.fujitsu.dc.engine.source;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.engine.DcEngineException;
import com.fujitsu.dc.engine.utils.DcUtils;

/**
 * サービスコレクションの情報を管理する.
 */
public class TestResourceSourceManager implements ISourceManager {
    /** ログオブジェクト. */
    private static Logger log = LoggerFactory.getLogger(TestResourceSourceManager.class);

    /**
     * コンストラクタ.
     */
    public TestResourceSourceManager() {
    }

    /**
     * サービスコレクションに設定されたサービスサブジェクトの取得.
     * @return サービスサブジェクト
     * @throws DcEngineException DcEngineException
     */
    public String getServiceSubject() throws DcEngineException {
        return "engine";
    }

    /**
     * サービス名に対応したスクリプトファイル名の取得.
     * @param servicePath サービス名
     * @return スクリプトファイル名
     * @throws DcEngineException DcEngineException
     */
    public String getScriptNameForServicePath(String servicePath) throws DcEngineException {
        // テスト用リソース動作時はURLで呼び出されたフィル名と実行スクリプト名が同一
        return servicePath;
    }

    /**
     * ソースファイルを取得.
     * @param sourceName ソースファイル名
     * @return ソースファイルの中身
     * @throws DcEngineException DcEngineException
     */
    public String getSource(String sourceName) throws DcEngineException {
        try {
            URL path = getClass().getResource("/service/" + sourceName);

            return DcUtils.readFile(path.getFile());
        } catch (Exception e) {
            log.info("CouchClientException msg:" + e.getMessage() + ",svcName:" + sourceName);
            log.info("UserScript read error ", e);
            throw new DcEngineException("404 Not Found", DcEngineException.STATUSCODE_NOTFOUND, e);
        }
    }
}

