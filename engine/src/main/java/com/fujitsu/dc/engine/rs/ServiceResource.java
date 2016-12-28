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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fujitsu.dc.engine.DcEngineException;
import com.fujitsu.dc.engine.source.ISourceManager;
import com.fujitsu.dc.engine.source.EsServiceResourceSourceManager;
import com.fujitsu.dc.engine.source.FsServiceResourceSourceManager;

/**
 * DC-Engineサーブレットクラス.
 */
@Path("/{cell}/{scheme}/service/{id : .+}")
public class ServiceResource extends AbstractService {
    /** ログオブジェクト. */
    private static Log log = LogFactory.getLog(AbstractService.class);

    static {
        log.getClass();
    }

    /**
     * コンストラクタ.
     * @throws DcEngineException DcEngine例外
     */
    public ServiceResource() throws DcEngineException {
        super();
    }

    /**
     * Cell名取得.
     * @return Cell名
     */
    @Override
    public final String getCell() {
        return null;
    }

    /**
     * データスキーマURI取得.
     * @return データスキーマURI
     */
    @Override
    public final String getScheme() {
        return null;
    }

    @Override
    public ISourceManager getServiceCollectionManager() throws DcEngineException {
        ISourceManager svcRsSourceManager = null;
        // ソースの管理情報を取得
        if (this.fsPath != null) {
          svcRsSourceManager = new FsServiceResourceSourceManager(this.fsPath);
        } else {
          svcRsSourceManager = new EsServiceResourceSourceManager(
              getIndex(), getType(), getId(), getRoutingId());
        }
        return svcRsSourceManager;
    }
}
