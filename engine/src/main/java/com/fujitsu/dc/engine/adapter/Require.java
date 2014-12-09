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
package com.fujitsu.dc.engine.adapter;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fujitsu.dc.engine.DcEngineContext;
import com.fujitsu.dc.engine.DcEngineException;
import com.fujitsu.dc.engine.source.ISourceManager;

/**
 * Require読み込み用クラス.
 */
public class Require {
    /** DcEngineContext. */
    private DcEngineContext context;

    /** ソース情報管理. */
    private ISourceManager sourceManager;

    /** ログオブジェクト. */
    private static Log log = LogFactory.getLog(Require.class);

    static {
        log.getClass();
    }
    /**
     * コンストラクタ.
     * @param context DcContext
     */
    public Require(DcEngineContext context) {
        this.context = context;
    }

    /**
     * Require実行.
     * @param moduleName require対象モジュール名
     * @return Require結果
     * @throws DcEngineException DcEngineException
     */
    public Object doRequire(String moduleName) throws DcEngineException {
        String source = this.sourceManager.getSource(moduleName + ".js");
        Date date = new Date();
        Long time = date.getTime();
        String key = moduleName + time.toString();
        source = key + " = function() {};(function(exports) {" + source + "})(" + key + ")";

        Object require;
        this.context.requireJs(source, moduleName);
        require = this.context.requireJs(key, moduleName);
        return require;
    }

    /**
     * ソース情報を設定する.
     * @param sourceManager the ISourceManager
     */
    public final void setSourceManager(final ISourceManager sourceManager) {
        this.sourceManager = sourceManager;
    }
}
