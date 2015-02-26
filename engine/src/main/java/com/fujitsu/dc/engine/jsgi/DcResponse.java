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
package com.fujitsu.dc.engine.jsgi;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.StreamingOutput;

import org.apache.http.HttpStatus;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.FunctionObject;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.NativeObject;
import org.mozilla.javascript.ScriptableObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.engine.wrapper.DcInputStream;


/**
 * ユーザスクリプトから返されたJSGIオブジェクト（JavaScript）をJAX-RSで返却する.
 */
@SuppressWarnings("serial")
public final class DcResponse extends ScriptableObject {
    private static final int BUFFER_SIZE = 1024;

    /** ログオブジェクト. */
    private static Logger log = LoggerFactory.getLogger(DcResponse.class);

    int status = 0;
    String charset = "utf-8";
    Map<String, String> headers = new HashMap<String, String>();
    String body = null;
    StreamingOutput streaming;
    OutputStream output;

    /**
     * ユーザスクリプトから返却されたJSGIのレスポンスをチェックし、Javaオブジェクトに変換する.
     * @param jsgiResponse JavaScriptのJSGIレスポンス
     * @return DcResponse
     * @throws Exception Exception
     */
    public static DcResponse parseJsgiResponse(Object jsgiResponse) throws Exception {

        final DcResponse dcResponse = new DcResponse();
        // レスポンス形式のチェック
        if (!(jsgiResponse instanceof NativeObject)) {
            String msg = "not NativeObject";
            log.info(msg);
            throw new Exception(msg);
        }
        NativeObject response = (NativeObject) jsgiResponse;

        // statusのチェック
        Object oStatus = ScriptableObject.getProperty(response, "status");
        if (!(oStatus instanceof Number)) {
            String msg = "response status illegal type.";
            log.info(msg + ":" + oStatus.getClass());
            throw new Exception(msg);
        }
        // レスポンスコードが以下の条件にあてはまる場合はエラーとする
        // ・100番台
        // ・301、303、307
        if (isInvalidResCode((Number) oStatus)) {
            String msg = String.format("response status illegal type. status: %s",
                    String.valueOf(Context.toString(oStatus)));
            log.info(msg + ":" + oStatus);
            throw new Exception(msg);
        }
        dcResponse.setStatus((int) Context.toNumber(oStatus));

        // headersのチェック
        Object oHeaders = ScriptableObject.getProperty(response, "headers");
        if (!(oHeaders instanceof NativeObject)) {
            String msg = "not headers";
            log.info(msg);
            throw new Exception(msg);
        }
        NativeObject nHeaders = (NativeObject) oHeaders;
        Object[] os = nHeaders.getIds();
        for (Object o : os) {
            if (!(o instanceof String)) {
                String msg = "header key format error";
                log.info(msg);
                throw new Exception(msg);
            }
            String key = Context.toString(o);
            // Transfer-Encodingの指定は無効にする
            if ("Transfer-Encoding".equalsIgnoreCase(key)) {
                continue;
            }
            Object value = nHeaders.get(key, nHeaders);
            if (!(value instanceof String)) {
                String msg = "header value format error";
                log.info(msg);
                throw new Exception(msg);
            }
            dcResponse.addHeader(key, Context.toString(value));
        }

        // bodyのチェック
        Object oBody = ScriptableObject.getProperty(response, "body");
        // 復帰値の型チェック
        if (!(oBody instanceof ScriptableObject)) {
            String msg = "response body undefined forEach.";
            log.info(msg);
            throw new Exception(msg);
        }
        final ScriptableObject scriptableBody = (ScriptableObject) oBody;
        // forEachが実装されているかチェック
        if (!ScriptableObject.hasProperty(scriptableBody, "forEach")) {
            String msg = "response body undefined forEach.";
            log.info(msg);
            throw new Exception(msg);
        }
        Method checkMethod = dcResponse.getForEach("bodyCheckFunction");
        final Method responseMethod = dcResponse.getForEach("bodyResponseFunction");

        // JavaのforEach実装をJavaScriptの関数として登録
        ScriptableObject callback = new FunctionObject("bodyCheckFunction", checkMethod, dcResponse);
        // forEach呼び出し（返却データの型チェック用）
        Object[] args = {callback};
        try {
            ScriptableObject.callMethod(scriptableBody, "forEach", args);
        } catch (JavaScriptException e) {
            log.info(e.getMessage());
            throw new Exception(e.getMessage());
        }

        // レスポンスの遅延処理登録
        StreamingOutput stremingOutput = new StreamingOutput() {
            @Override
            public void write(OutputStream resStream) throws IOException {
                // forEachのコールバックを呼び出す準備
                dcResponse.setOutput(resStream);

                // forEachをJavaScriptの関数として登録
                ScriptableObject callback = new FunctionObject("bodyResponseFunction", responseMethod, dcResponse);

                // forEach呼び出し
                Object[] args = {callback};
                ScriptableObject.callMethod(scriptableBody, "forEach", args);
                resStream.close();
            }
        };

        dcResponse.setBody(stremingOutput);

        return dcResponse;
    }

    /**
     * Engineとして許容しないレスポンスコードかどうかを判定する.
     * @param oStatus レスポンスコード(Number型)
     * @return true:Engineとして許容しないレスポンスコードである false:Engineとして許容できるレスポンスコードである
     */
    private static boolean isInvalidResCode(Number oStatus) {
        // 以下のレスポンスコードは許容しない
        // ・3桁ではない
        // ・0番台
        // ・100番台(クライアントの挙動が不安定になるため)
        if (!String.valueOf(Context.toString(oStatus)).matches("^[2-9]\\d{2}$")) {
            return true;
        }
        // 301、303、307はサーブレットコンテナでエラーになるため許容しない
        if (oStatus.intValue() == HttpStatus.SC_MOVED_PERMANENTLY
                || oStatus.intValue() == HttpStatus.SC_SEE_OTHER
                || oStatus.intValue() == HttpStatus.SC_TEMPORARY_REDIRECT) {
            return true;
        }
        return false;
    }

    /**
     * レスポンスヘッダを設定.
     * @param status ステータスコード
     */
    private void setStatus(int status) {
        this.status = status;
    }

    /**
     * レスポンスヘッダーを追加.
     * @param key ヘッダ名
     * @param value 値
     * @throws Exception Exception
     */
    private void addHeader(String key, String value) throws Exception {
        // Content-typeだったらcharsetを抜き出す。出力エンコードを知るため。
      if (key.equalsIgnoreCase("content-type")) {
            // メディアタイプ異常のままJAX-RSフレームワークへ渡すと例外になるのでここでチェック
          if (!checkMediaType(value)) {
              String msg = "Response header parsing media type.";
              log.info(msg);
              throw new Exception(msg);
          }
            // 判定するパターンを生成
          Pattern p = Pattern.compile("charset=([^;]+)");
          Matcher m = p.matcher(value);
          if (m.find()) {
              String tmp = m.group(1);
                // charset異常のままJAX-RSフレームワークへ渡すと例外になるのでここでチェック
              if (!checkCharSet(tmp)) {
                  String msg = "response charset illegal type.";
                  log.info(msg);
                  throw new Exception(msg);
              }
              this.charset = tmp;
          }
      }
      this.headers.put(key, value);
    }

    /**
     * メディアタイプが正常かチェック.
     * @param type チェック対象のメディア・タイプ
     * @return bool
     */
    private static boolean checkMediaType(String type) {
        try {
            MediaType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return false;
        }
        return true;
    }

    /**
     * char-setが正常かチェック.
     * @param value チェック対象のchar-set
     * @return bool
     */
    private static boolean checkCharSet(String value) {
        return Charset.isSupported(value);
    }

    /**
     * JavaScriptからレスポンスで返すStremingOutputを設定する. 直接StreamingOutputを扱わずにラップしたDcStremingOutputを受け取る
     * @param value DcStremingOutputオブジェクト
     */
    private void setBody(StreamingOutput value) {
        this.streaming = value;
    }

    /**
     * レスポンス生成.
     * @return レスポンス
     */
    public Response build() {
        ResponseBuilder builder = Response.status(this.status);
        for (Map.Entry<String, String> header : this.headers.entrySet()) {
            builder.header(header.getKey(), header.getValue());
        }

        builder.entity(this.streaming);
        return builder.build();
    }

    /**
     * レスポンスの出力先のストリームをセットする.
     * @param output 出力先Stream
     */
    private void setOutput(OutputStream output) {
        this.output = output;
    }

    /**
     * JavaScriptのforEachをJavaで処理するためのメソッド.
     * @param element forEachの要素が一件ずつ渡される
     * @param number 渡された要素のindexが渡される
     * @param object forEach対象のオブジェクトの全要素の配列が渡される
     * @throws IOException IOException
     */
    public void bodyResponseFunction(Object element, double number, NativeArray object) throws IOException {
        if (element instanceof DcInputStream) {
            // 現状はEngine上のJavaScriptでバイナリを直接扱わず
            // JavaのストリームをそのままJavaScript内で扱うことで対応
            DcInputStream io = (DcInputStream) element;
            byte[] buf = new byte[BUFFER_SIZE];
            int bufLength;
            while ((bufLength = io.read(buf)) != -1) {
                this.output.write(buf, 0, bufLength);
            }
        } else {
            // 文字列はユーザスクリプトがContent-typeのcharsetで指定した文字エンコーディングで出力。
            this.output.write(((String) element).getBytes(charset));
        }
    }

    /**
     * JavaScriptのforEachをJavaで処理するためのメソッド(レスポンス内容チェック用).
     * @param element forEachの要素が一件ずつ渡される
     * @param number 渡された要素のindexが渡される
     * @param object forEach対象のオブジェクトの全要素の配列が渡される
     * @throws Exception Exception
     */
    public void bodyCheckFunction(Object element, double number, NativeArray object) throws Exception {
        if (!(element instanceof DcInputStream) && !(element instanceof String)) {
            String msg = "response body illegal type.";
            log.info(msg);
            throw new Exception(msg);
        }
    }

    /**
     * JavaScriptのforEach処理をJavaで行うためのメソッド（function）を取得.
     * @param methodName メソッド名
     * @return function
     * @throws Exception Exception
     */
    private Method getForEach(String methodName) throws Exception {
        Method method;
        try {
            method = this.getClass().getMethod(methodName,
                    new Class[] {Object.class, double.class, NativeArray.class});
        } catch (SecurityException e) {
            String msg = "function not allowed.";
            log.warn(msg);
            throw new Exception(msg);
        } catch (NoSuchMethodException e) {
            String msg = "forEach function not found.";
            log.warn(msg);
            throw new Exception(msg);
        }
        return method;
    }

    @Override
    public String getClassName() {
        return "DcResponse";
    }
}
