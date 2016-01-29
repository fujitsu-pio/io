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
package com.fujitsu.dc.jersey.engine.test;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.common.auth.token.LocalToken;
import com.fujitsu.dc.common.auth.token.TransCellAccessToken;
import com.fujitsu.dc.engine.utils.DcEngineConfig;

/**
 * IT用テストランナークラス.
 */
public class DcRunner extends BlockJUnit4ClassRunner {
    /**
     * ログ用オブジェクト.
     */
    private static Logger log = LoggerFactory.getLogger(DcRunner.class);

    /**
     * コンストラクタ.
     * @param klass klass
     * @throws InitializationError InitializationError
     * @throws IOException IOException
     * @throws CertificateException CertificateException
     * @throws InvalidKeySpecException InvalidKeySpecException
     * @throws NoSuchAlgorithmException NoSuchAlgorithmException
     * @throws javax.security.cert.CertificateException CertificateException
     */
    public DcRunner(final Class<?> klass)
            throws InitializationError, NoSuchAlgorithmException,
            InvalidKeySpecException, CertificateException, IOException, javax.security.cert.CertificateException {
        super(klass);
        // トークン処理ライブラリの初期設定.
        TransCellAccessToken.configureX509(DcEngineConfig.getX509PrivateKey(), DcEngineConfig.getX509Certificate(),
                DcEngineConfig.getX509RootCertificate());
        LocalToken.setKeyString(DcEngineConfig.getTokenSecretKey());
    }

    @Override
    protected final void runChild(final FrameworkMethod method, final RunNotifier notifier) {
        log.debug("■■■■ " + method.getName() + " ■■■■");
        super.runChild(method, notifier);
    }
}
