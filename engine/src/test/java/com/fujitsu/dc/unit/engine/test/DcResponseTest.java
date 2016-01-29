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
package com.fujitsu.dc.unit.engine.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Method;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.fujitsu.dc.engine.jsgi.DcResponse;
import com.fujitsu.dc.jersey.engine.test.categories.Integration;

/**
 * DcResponse ユニットテストクラス.
 */
@Category({Integration.class })
public class DcResponseTest {

    /**
     * 数値のレスポンスコードを正しく判定できるかのテスト.
     */
    @Test
    public void 数値のレスポンスコードを正しく判定できるかのテスト() {
        DcResponse dcResponse = new DcResponse();
        try {
            Method method = DcResponse.class.getDeclaredMethod("isInvalidResCode", new Class[] {Number.class });
            method.setAccessible(true);
            // 1桁
            Object obj = method.invoke(dcResponse, (Number) 1);
            assertTrue((Boolean) obj);
            // 2桁
            obj = method.invoke(dcResponse, (Number) 99);
            assertTrue((Boolean) obj);
            // 100番
            obj = method.invoke(dcResponse, (Number) 100);
            assertTrue((Boolean) obj);
            // 105.0番
            obj = method.invoke(dcResponse, (Number) 105.0);
            assertTrue((Boolean) obj);
            // 199番
            obj = method.invoke(dcResponse, (Number) 199);
            assertTrue((Boolean) obj);
            // 200番
            obj = method.invoke(dcResponse, (Number) 200);
            assertFalse((Boolean) obj);
            // 201.0番
            obj = method.invoke(dcResponse, (Number) 201.0);
            assertFalse((Boolean) obj);
            // 201番
            obj = method.invoke(dcResponse, (Number) 201);
            assertFalse((Boolean) obj);
            // 301番
            obj = method.invoke(dcResponse, (Number) 301);
            assertTrue((Boolean) obj);
            // 303番
            obj = method.invoke(dcResponse, (Number) 303);
            assertTrue((Boolean) obj);
            // 307番
            obj = method.invoke(dcResponse, (Number) 307);
            assertTrue((Boolean) obj);
            // 500番
            obj = method.invoke(dcResponse, (Number) 201);
            assertFalse((Boolean) obj);
            // 999番
            obj = method.invoke(dcResponse, (Number) 999);
            assertFalse((Boolean) obj);
            // 4桁
            obj = method.invoke(dcResponse, (Number) 1000);
            assertTrue((Boolean) obj);
        } catch (Exception e) {
            e.printStackTrace();
            fail();
        }
    }
}
