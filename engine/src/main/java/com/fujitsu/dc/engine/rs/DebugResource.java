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
package com.fujitsu.dc.engine.rs;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fujitsu.dc.engine.DcEngineException;
import com.fujitsu.dc.engine.source.ISourceManager;
import com.fujitsu.dc.engine.source.TestResourceSourceManager;

/**
 * デバッグ用Serviceクラス.
 */
@Path("{id}")
public class DebugResource extends AbstractService {
    /** ログオブジェクト. */
    private static Log log = LogFactory.getLog(DebugResource.class);
    /** デフォルトCell名. */
    private static final String CELL = "engine_debug_cell";
    /** デフォルトBox名. */
    private static final String SCHEME = "engine_debug_box";

    /**
     * JavaScript リモートデバッグ機能を有効にするかどうかを指定する. <code>true</code>を指定した場合、有効となる。
     */
    private Boolean useScriptDebug;

    // @Path("{path1}")
    // public final Response aaaaa() {
    // System.out.print("aaa");
    // return Response.ok().build();
    // }

    /**
     * デフォルトコンストラクタ.
     * @param useDebug JavaScript リモートデバッグ機能を有効にするかどうかを指定する
     * @throws DcEngineException DcEngine例外
     */
    public DebugResource(@QueryParam("useScriptDebug") final String useDebug) throws DcEngineException {
        // http://xxx/yy?useScriptDebug=true と指定した場合、useScriptDebugの値が、本パラメタのuseDebugに入る。
        super();
        this.useScriptDebug = Boolean.valueOf(useDebug);
        log.info("Create DebugResource. useScriptDebug=" + this.useScriptDebug);
    }

    @Override
    public final String getCell() {
        return CELL;
    }

    @Override
    public final String getScheme() {
        return SCHEME;
    }

    @Override
    public ISourceManager getServiceCollectionManager() throws DcEngineException {
        // ソースの情報を取得
        TestResourceSourceManager sourcemanager = new TestResourceSourceManager();
        return sourcemanager;
    }
}
