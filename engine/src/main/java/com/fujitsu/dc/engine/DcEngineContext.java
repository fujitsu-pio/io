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

import java.io.Closeable;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.apache.http.HttpStatus;
import org.mozilla.javascript.ContextFactory;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.Script;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;
import org.mozilla.javascript.WrappedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.client.utils.DcLoggerFactory;
import com.fujitsu.dc.engine.adapter.DcEngineDao;
import com.fujitsu.dc.engine.adapter.DcRequestBodyStream;
import com.fujitsu.dc.engine.adapter.Require;
import com.fujitsu.dc.engine.extension.support.AbstractExtensionScriptableObject;
import com.fujitsu.dc.engine.extension.support.ExtensionJarLoader;
import com.fujitsu.dc.engine.extension.support.ExtensionLogger;
import com.fujitsu.dc.engine.extension.support.IExtensionLogger;
import com.fujitsu.dc.engine.extension.support.JavaClassRevealFilter;
import com.fujitsu.dc.engine.jsgi.DcResponse;
import com.fujitsu.dc.engine.jsgi.JSGIRequest;
import com.fujitsu.dc.engine.source.ISourceManager;
import com.fujitsu.dc.engine.utils.DcEngineConfig;
import com.fujitsu.dc.engine.utils.DcEngineLoggerFactory;

/**
 * DC-Engineのメインクラス.
 */
public class DcEngineContext implements Closeable {
    /** ログオブジェクト. */
    private static Logger log = LoggerFactory.getLogger(DcEngineContext.class);

    private static final String DC_SCOPE = "dc";
    private static final String DC_EXTENSION_SCOPE = "extension";
    private static Map<String, Script> engineLibCache = new ConcurrentHashMap<String, Script>();


    /** Cell名. */
    private String currentCellName;
    /** Box名. */
    private String currentBoxName;
    /** データスキーマURI. */
    private String currentSchemeUri;

    /** RhinoのContext. */
    private org.mozilla.javascript.Context cx;
    /** Rhino、ContextFactory. */
    private DcJsContextFactory factory;
    /** Rhino、Scope. */
    private Scriptable scope;

    /** 基底URL. */
    private String baseUrl;

    /** ソース情報管理. */
    private ISourceManager sourceManager;

    static {
        ContextFactory.initGlobal(new DcJsContextFactory());
    }

    /**
     * コンストラクタ.
     * @throws DcEngineException DcEngine例外
     */
    public DcEngineContext() throws DcEngineException {
        // Rhinoの実行環境を作成する
        this.factory = new DcJsContextFactory();
        this.cx = factory.enterContext();

        this.scope = cx.initStandardObjects();

    }

    /**
     * Extensionクラスを JavaScriptに公開する.
     * この際、ロガークラス実体を Extensionクラス側に設定する。
     * @throws DcEngineException 公開失敗時
     */
    /**
     * @throws DcEngineException
     */
    private void prepareExtensionClass() throws DcEngineException {
        // Extension用 jarのロード
        ExtensionJarLoader extLoader = null;
        try {
            extLoader = ExtensionJarLoader.getInstance(this.cx.getApplicationClassLoader(),
                    new JavaClassRevealFilter());
            factory.initApplicationClassLoader(extLoader.getClassLoader());
        } catch (IOException e) {
            throw new DcEngineException("Server Error", DcEngineException.STATUSCODE_SERVER_ERROR, e);
        } catch (DcEngineException e) {
            throw e;
        }

        // Javascript内でプロトタイプとして使用可能な Javaクラスを定義する。
        // スコープの設定
        NativeObject dcScope = (NativeObject) this.scope.get(DC_SCOPE, this.scope);
        NativeObject declaringClass = (NativeObject) dcScope.get(DC_EXTENSION_SCOPE, dcScope);

        for (Class<? extends Scriptable> clazz : extLoader.getPrototypeClassSet()) {
            try {
                if (AbstractExtensionScriptableObject.class.isAssignableFrom(clazz)) {
                    // AbstractExtensionScriptableObjectであれば、ロガー設定を行う。
                    @SuppressWarnings("unchecked")
                    Class<? extends AbstractExtensionScriptableObject> extensionClazz
                        = (Class<? extends AbstractExtensionScriptableObject>) clazz;
                    // Extensionクラス内で利用可能なロガーを渡す。
                    // この処理の間に例外が発生しても、何も行わず無視する。（ロガー設定はしないまま正常に動作させる。)
                    try {
                        Method setLoggerMethod = extensionClazz.getMethod(
                                "setLogger",
                                new Class[] {
                                        Class.class,
                                        IExtensionLogger.class });
                        setLoggerMethod.setAccessible(true);
                        setLoggerMethod.invoke(null,
                                new Object[] {extensionClazz, new ExtensionLogger(extensionClazz)});
                    } catch (Exception e) {
                        log.info("setLogger method cannot be called.", e);
                    }
                }

                // ############################################################################3
                // 以下のメソッドから例外が出力された場合、スクリプト実行の障害となるが、複数の extensionが導入されている場合、
                // 問題となる extensionを利用していない UserScriptまで実行できなくなるのを防ぐため、ここからは例外は投げない。
                // 問題のプロトタイプにアクセスした場合、Script実行時のエラーとなる。
                // ############################################################################3
                ScriptableObject.defineClass(declaringClass, clazz);
            } catch (RuntimeException e) {
                log.warn(String.format("Warn: Extension class(%s) could not be revealed to javascript.: %s",
                        clazz.getCanonicalName(), e.getMessage()));
            } catch (Exception e) {
                log.warn(String.format("Warn: Extension class(%s) could not be revealed to javascript.: %s",
                        clazz.getCanonicalName(), e.getMessage()));
            }
        }
    }

    /**
     * ソース情報を設定する.
     * @param value the ISourceManager
     */
    public final void setSourceManager(final ISourceManager value) {
        this.sourceManager = value;
    }

    /**
     * グローバルオブジェクトをロード. 過去はグローバルオブジェクトを作成する関数だったが、現状は単なるsetterになっている。
     * @param url 基底URL
     * @param cell Cell名
     * @param scheme データスキーマURI
     * @param box Box名
     * @param service サービス名
     */
    public final void loadGlobalObject(final String url,
            final String cell,
            final String scheme,
            final String box,
            final String service) {
        this.baseUrl = url;
        this.currentCellName = cell;
        this.currentBoxName = box;
        this.currentSchemeUri = scheme;
    }

    /**
     * JSGIを実行.
     * @param source 実行するユーザースクリプト
     * @param req Requestオブジェクト
     * @param res Responseオブジェクト
     * @param is リクエストストリームオブジェクト
     * @param serviceSubject サービスサブジェクト
     * @return Response
     * @throws DcEngineException DcEngine例外
     */
    public final Response runJsgi(final String source,
            final HttpServletRequest req,
            final HttpServletResponse res,
            final InputStream is,
            final String serviceSubject) throws DcEngineException {
        // JSGI実行準備
        // DAOオブジェクトを生成
        DcEngineDao dc = createDao(req, serviceSubject);

        // DAOオブジェクトをJavaScriptプロパティへ設定
        javaToJs(dc, "dcjvm");

        // RequireオブジェクトをJavaScriptプロパティへ設定
        javaToJs(createRequireObject(), "dcrequire");

        // dc-dao.js を読み込み
        try {
            loadJs("dc-dao");
        } catch (IOException e1) {
            log.info("runJsgi error (DAO load io error) ", e1);
            throw new DcEngineException("Server Error", DcEngineException.STATUSCODE_SERVER_ERROR, e1);
        }

        // dc-lib.js を読み込み
        try {
            loadJs("dc-lib");
        } catch (IOException e1) {
            log.info("runJsgi error (dc-lib load io error) ", e1);
            throw new DcEngineException("Server Error", DcEngineException.STATUSCODE_SERVER_ERROR, e1);
        }

        // jsgi-lib.jsを読み込み
        try {
            loadJs("jsgi-lib");
        } catch (IOException e1) {
            log.info("runJsgi error (jsgi-lib load io error) ", e1);
            throw new DcEngineException("Server Error", DcEngineException.STATUSCODE_SERVER_ERROR, e1);
        }

        // dc名前空間に、Extensionのクラス群を定義する。
        prepareExtensionClass();

        // RequestオブジェクトをJavaScriptプロパティへ設定
        JSGIRequest dcReq = new JSGIRequest(req, new DcRequestBodyStream(is));

        // JSGI実行
        // ユーザースクリプトを実行(eval)する
        try {
            Object ret;
            log.info("eval user script : script size = " + source.length());
            ret = evalUserScript(source, dcReq);
            log.info("[" + DcEngineConfig.getVersion() + "] " + "<<< Request Ended ");

            DcResponse dcRes = DcResponse.parseJsgiResponse(ret);

            return dcRes.build();
        } catch (Error e) {
            // ユーザースクリプトのタイムアウトはINFOレベルでログ出力
            log.info("UserScript TimeOut", e);
            throw new DcEngineException("Script TimeOut", HttpStatus.SC_SERVICE_UNAVAILABLE);
        } catch (Exception e) {
            if (e instanceof WrappedException) {
                e = (Exception) ((WrappedException) e).getWrappedException();
            }

            // ユーザースクリプト内でのエラーはINFOレベルでログ出力
            log.info("User Script Evalucation Error : " + e.getMessage(), e);
            throw new DcEngineException("Server Error : " + e.getMessage(), DcEngineException.STATUSCODE_SERVER_ERROR,
                    e);
        }
    }

    /**
     * UserScript実行.
     * @param source ユーザースクリプトソース
     * @throws IOException IO例外
     * @throws DcEngineException DcEngineException
     */
    private Object evalUserScript(final String source, JSGIRequest dcReq) throws DcEngineException {
        cx.evaluateString(scope, "fn_jsgi = " + source, null, 1, null);

        Object fObj = scope.get("fn_jsgi", scope);
        Object result = null;
        if (!(fObj instanceof Function)) {
            log.warn("fn_jsgi not found");
            throw new DcEngineException("Server Error", DcEngineException.STATUSCODE_SERVER_ERROR);
        }

        Object[] functionArgs = {dcReq.getRequestObject() };
        Function f = (Function) fObj;
        result = f.call(cx, scope, scope, functionArgs);
        return result;
    }

    /**
     * DAOオブジェクトを作成.
     * @param req Requestオブジェクト
     * @param serviceSubject サービスサブジェクト
     * @return DAOオブジェクト
     */
    private DcEngineDao createDao(final HttpServletRequest req, final String serviceSubject) {
        DcEngineLoggerFactory engLogFactory = new DcEngineLoggerFactory();
        DcLoggerFactory.setDefaultFactory(engLogFactory);

        DcEngineDao dccx = new DcEngineDao(baseUrl, currentCellName, currentSchemeUri, currentBoxName);
        dccx.setServiceSubject(serviceSubject);
        dccx.setBoxSchema(req.getHeader("X-Dc-Box-Schema"));
        String auth = req.getHeader(HttpHeaders.AUTHORIZATION);
        String version = req.getHeader(DcEngineDao.DC_VERSION);
        if (version != null && !(version.equals(""))) {
            dccx.setDcVersion(version);
        }
        log.debug("auth : --------------------------------------------------------------------------");
        log.debug(auth);
        if (auth != null && auth.length() > "Bearer".length()) {
            dccx.setClientToken(auth.substring("Bearer".length()).trim());
        }
        return dccx;
    }

    /**
     * Requireオブジェクトを作成.
     * @param localPath ローカル実行時のソースパス
     * @return 生成したRequireオブジェクト
     */
    private Require createRequireObject() {
        Require requireComp = new Require(this);
        requireComp.setSourceManager(this.sourceManager);
        log.debug("RequireObject created");
        return requireComp;
    }

    /**
     * JavaScriptファイルを解析し、オブジェクトに登録.
     * @param name JavaScriptソース名
     * @throws IOException IO例外
     */
    private Object loadJs(final String name) throws IOException {
        URL path = getClass().getResource("/js-lib/" + name + ".js");

        Script jsBuildObject = null;
        if (engineLibCache.containsKey(path.toString())) {
            jsBuildObject = engineLibCache.get(path.toString());
        } else {
            FileInputStream fis = new FileInputStream(path.getFile());
            InputStreamReader isr = new InputStreamReader(fis, "UTF-8");
            jsBuildObject = cx.compileReader(isr, path.getPath(), 1, null);
            engineLibCache.put(path.toString(), jsBuildObject);
        }
        if (jsBuildObject == null) {
            return null;
        }
        Object ret = jsBuildObject.exec(cx, scope);
        log.debug("Load JavaScript from Local Resource : " + path);
        return ret;
    }

    /**
     * JavaのオブジェクトをJavaScriptオブジェクトに変換.
     * @param obj Javaオブジェクト
     * @param propertyName JavaScriptオブジェクトの変数名
     */
    private void javaToJs(final Object obj, final String propertyName) {
        log.debug("JavaObject to JavaScriptProperty " + propertyName);
        Object jObj = org.mozilla.javascript.Context.javaToJS(obj, scope);
        ScriptableObject.putProperty(scope, propertyName, jObj);
    }

    /**
     * JavaScriptファイルを解析し、オブジェクトに登録.
     * @param source JavaScriptソースの中身
     * @param path JavaScriptソース名
     * @return オブジェクト
     */
    public Object requireJs(final String source, final String path) {
        Object ret = cx.evaluateString(scope, source, path, 1, null);
        log.debug("Load JavaScript from Require Resource : " + path);
        return ret;
    }

    @Override
    public void close() throws IOException {
        DcJsContext.exit();
    }
}
