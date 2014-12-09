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
package com.fujitsu.dc.engine.accesscontrol;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.NativeArray;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.WrapFactory;

import com.fujitsu.dc.engine.adapter.DcRequestBodyStream;
import com.fujitsu.dc.engine.wrapper.DcInputStream;
import com.fujitsu.dc.engine.wrapper.DcJSONObject;

/**
 * JavaScriptからJavaメソッド呼び出し時の返却値ラップ動作制御.
 */
public class PrimitiveWrapFactory extends WrapFactory {

    /*
     * 以下の変換処理を実施.
     * ・数値型が渡された場合はラップしないことよってRhinoにJavaScriptのnumber型に変換させる
     * ・Stringはラップせずにそのまま返すことでrhinoにJavaScriptのstring型に変換させる
     * ・InputStreamはDcInputStreamというEngineのラッパークラスに置き換える
     * ・JSONObjectはDcJSONObjectというEngineのラッパークラスに置き換える
     *   （EngineクライアントライブラリでJSONObject前提の処理があるのでその対応）
     * ・ArrayListはJavaScriptのNativeArrayに置き換える
     *   （EngineクライアントライブラリでArrayList前提の処理があるのでその対応）
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object wrap(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
        if (obj instanceof Number) {
            return obj;

        } else if (obj instanceof String) {
            return obj;

        } else if (obj instanceof InputStream && !(obj instanceof DcInputStream)) {
            return new DcInputStream((InputStream) obj);

        } else if (obj instanceof JSONObject && !(obj instanceof DcJSONObject)) {
            return ((JSONObject) obj).toJSONString();

        } else if (obj instanceof ArrayList) {
            // クライアントライブラリからJava配列を直接返された時(ACLなど扱う処理で返される)に、
            // NativeArrayに置き換えてJava配列をJavaScriptに見せないようにする。
            ArrayList<Object> list = (ArrayList<Object>) obj;
            NativeArray na = new NativeArray(list.size());
            for (int i = 0; i < list.size(); i++) {
                na.put(i, na, list.get(i));
            }
            na.setPrototype(scope);
            return na;
        }
        return super.wrap(cx, scope, obj, staticType);
    }

    @Override
    public Scriptable wrapNewObject(Context cx, Scriptable scope, Object obj) {
        // JavaScriptから呼び出しを許してるクラスもコンストラクタは呼び出し不可にする
        if (obj.getClass() == DcInputStream.class
                || obj.getClass() == DcJSONObject.class
                || obj.getClass() == DcRequestBodyStream.class) {
            throw new EvaluatorException("not found");
        }
        return super.wrapNewObject(cx, scope, obj);
    }
}
