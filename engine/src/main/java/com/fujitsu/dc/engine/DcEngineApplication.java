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
package com.fujitsu.dc.engine;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.auth.token.LocalToken;
import com.fujitsu.dc.common.auth.token.TransCellAccessToken;
import com.fujitsu.dc.engine.rs.DebugResource;
import com.fujitsu.dc.engine.rs.ServiceResource;
import com.fujitsu.dc.engine.rs.StatusResource;
import com.fujitsu.dc.engine.rs.TestResource;
import com.fujitsu.dc.engine.utils.DcEngineConfig;

/**
 * DC-Engine.
 */
public class DcEngineApplication extends Application {
    static Logger log = LoggerFactory.getLogger(DcEngineApplication.class);
    /** デバッグフラグ. */
    // private static final String KEY_DCENGINE_DEBUG = "io.personium.engine.debug";
    static {
        try {
        TransCellAccessToken.configureX509(DcEngineConfig.getX509PrivateKey(), DcEngineConfig.getX509Certificate(),
                DcEngineConfig.getX509RootCertificate());
        LocalToken.setKeyString(DcEngineConfig.getTokenSecretKey());
        } catch (Exception e) {
            log.warn("Failed to start server.", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Set.
     * @return Classリスト
     */
    @Override
    public final Set<Class<?>> getClasses() {
        Set<Class<?>> classes = new HashSet<Class<?>>();
        classes.add(ServiceResource.class);
        // if (Boolean.valueOf(this.servletContext.getInitParameter(KEY_DCENGINE_DEBUG))) {
        classes.add(StatusResource.class);
        classes.add(DebugResource.class);
        classes.add(TestResource.class);
        // }
        return classes;
    }
}
