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
package com.fujitsu.dc.common.es.impl;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Map;

import org.elasticsearch.action.index.IndexRequest.OpType;
import org.elasticsearch.action.search.SearchPhaseExecutionException;
import org.elasticsearch.common.settings.SettingsException;
import org.elasticsearch.index.Index;
import org.elasticsearch.index.engine.VersionConflictEngineException;
import org.elasticsearch.index.mapper.MapperParsingException;
import org.elasticsearch.indices.IndexMissingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fujitsu.dc.common.es.response.DcGetResponse;
import com.fujitsu.dc.common.es.response.DcSearchResponse;
import com.fujitsu.dc.common.es.response.EsClientException;
import com.fujitsu.dc.common.es.response.EsClientException.DcSearchPhaseExecutionException;

/**
 * EsType, EsIndexにおける初回リクエスト時の例外ハンドリングのテスト.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(EsClientException.class)
public class EsRetryOnParticularErrorTest {

    /**
     * EsType_getメソッドで初回にIndexMissingExceptionが投げられた場合のテスト.
     */
    @Test
    public void EsType_getメソッドで初回にIndexMissingExceptionが投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncGet()が呼ばれた場合に、IndexMissingExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        IndexMissingException toBeThrown = Mockito.mock(IndexMissingException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncGet(Mockito.anyString(), Mockito.anyBoolean());
        // メソッド呼び出し
        DcGetResponse result = esTypeObject.get("dummyId", true);
        assertNull(result);
    }

    /**
     * EsType_getメソッドで初回にIndexMissingExceptionを根本原因に持つ例外が投げられた場合のテスト.
     */
    @Test
    public void EsType_getメソッドで初回にIndexMissingExceptionを根本原因に持つ例外が投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncGet()が呼ばれた場合に、IndexMissingExceptionを根本原因に持つ例外を投げる。
        // 送出する例外オブジェクトを作成
        SettingsException toBeThrown = new SettingsException("foo", new IndexMissingException(new Index("dummy")));
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncGet(Mockito.anyString(), Mockito.anyBoolean());
        // メソッド呼び出し
        DcGetResponse result = esTypeObject.get("dummyId", true);
        assertNull(result);
    }

    /**
     * EsType_updateメソッドで初回にIndexMissingExceptionが投げられた場合のテスト.
     */
    @Test
    public void EsType_updateメソッドで初回にIndexMissingExceptionが投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncIndex()が呼ばれた場合に、IndexMissingExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        IndexMissingException toBeThrown = Mockito.mock(IndexMissingException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncIndex(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                        (OpType) Mockito.anyObject(), Mockito.anyLong());
        // メソッド呼び出し
        try {
            esTypeObject.update("dummyId", null, 1);
            fail("EsClientException should be thrown.");
        } catch (EsClientException.EsIndexMissingException e) {
            assertTrue(e.getCause() instanceof IndexMissingException);
        }
    }

    /**
     * EsType_updateメソッドで初回にIndexMissingExceptionを根本原因に持つ例外が投げられた場合のテスト.
     */
    @Test
    public void EsType_updateメソッドで初回にIndexMissingExceptionを根本原因に持つ例外が投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncIndex()が呼ばれた場合に、IndexMissingExceptionを投げる。
        // 送出する例外オブジェクトを作成
        SettingsException toBeThrown = new SettingsException("foo", new IndexMissingException(new Index("dummy")));
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncIndex(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                        (OpType) Mockito.anyObject(), Mockito.anyLong());
        // メソッド呼び出し
        try {
            esTypeObject.update("dummyId", null, 1);
            fail("EsClientException should be thrown.");
        } catch (EsClientException.EsIndexMissingException e) {
            assertTrue(e.getCause() instanceof SettingsException);
            assertTrue(e.getCause().getCause() instanceof IndexMissingException);
        }
    }

    /**
     * EsType_updateメソッドで初回にVersionConflictEngineExceptionが投げられた場合のテスト.
     */
    @Test
    public void EsType_updateメソッドで初回にVersionConflictEngineExceptionが投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncIndex()が呼ばれた場合に、VersionConflictEngineExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        VersionConflictEngineException toBeThrown = Mockito.mock(VersionConflictEngineException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncIndex(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                        (OpType) Mockito.anyObject(), Mockito.anyLong());
        // メソッド呼び出し
        try {
            esTypeObject.update("dummyId", null, 1);
            fail("EsClientException should be thrown.");
        } catch (EsClientException.EsVersionConflictException e) {
            assertTrue(e.getCause() instanceof VersionConflictEngineException);
        }
    }

    /**
     * EsType_updateメソッドで初回にMapperParsingExceptionが投げられた場合のテスト.
     */
    @Test
    public void EsType_updateメソッドで初回にMapperParsingExceptionが投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncIndex()が呼ばれた場合に、MapperParsingExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        MapperParsingException toBeThrown = Mockito.mock(MapperParsingException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncIndex(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class),
                        (OpType) Mockito.anyObject(), Mockito.anyLong());
        // メソッド呼び出し
        try {
            esTypeObject.update("dummyId", null, 1);
            fail("EsClientException should be thrown.");
        } catch (EsClientException.EsSchemaMismatchException e) {
            assertTrue(e.getCause() instanceof MapperParsingException);
        }
    }

    /**
     * EsType_searchメソッドで初回にIndexMissingExceptionが投げられた場合のテスト.
     */
    @Test
    public void EsType_searchメソッドで初回にIndexMissingExceptionが投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncSearch()が呼ばれた場合に、IndexMissingExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        IndexMissingException toBeThrown = Mockito.mock(IndexMissingException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncSearch(Mockito.anyMapOf(String.class, Object.class));
        // メソッド呼び出し
        DcSearchResponse result = esTypeObject.search(null);
        assertTrue(result.isNullResponse());
    }

    /**
     * EsType_searchメソッドで初回にIndexMissingExceptionを根本原因に持つ例外が投げられた場合のテスト.
     */
    @Test
    public void EsType_searchメソッドで初回にIndexMissingExceptionを根本原因に持つ例外が投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncSearch()が呼ばれた場合に、IndexMissingExceptionを根本原因に持つ例外を投げる。
        // 送出する例外オブジェクトを作成
        SettingsException toBeThrown = new SettingsException("foo", new IndexMissingException(new Index("dummy")));
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncSearch(Mockito.anyMapOf(String.class, Object.class));
        // メソッド呼び出し
        DcSearchResponse result = esTypeObject.search(null);
        assertTrue(result.isNullResponse());
    }

    /**
     * EsType_searchメソッドで初回にDcSearchPhaseExecutionExceptionが投げられた場合のテスト.
     */
    @Test
    public void EsType_searchメソッドで初回にDcSearchPhaseExecutionExceptionが投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncSearch()が呼ばれた場合に、SearchPhaseExecutionExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        SearchPhaseExecutionException toBeThrown = Mockito.mock(SearchPhaseExecutionException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncSearch(Mockito.anyMapOf(String.class, Object.class));
        // メソッド呼び出し
        try {
            esTypeObject.search(null);
            fail("EsClientException should be thrown.");
        } catch (EsClientException e) {
            assertTrue(e.getCause() instanceof DcSearchPhaseExecutionException);
        }
    }

    /**
     * EsType_deleteメソッドで初回にIndexMissingExceptionが投げられた場合のテスト.
     */
    @Test
    public void EsType_deleteメソッドで初回にIndexMissingExceptionが投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncDelete()が呼ばれた場合に、IndexMissingExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        IndexMissingException toBeThrown = Mockito.mock(IndexMissingException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncDelete(Mockito.anyString(), Mockito.anyLong());
        // メソッド呼び出し
        try {
            esTypeObject.delete("dummyId", 1);
            fail("EsClientException should be thrown.");
        } catch (EsClientException.EsIndexMissingException e) {
            assertTrue(e.getCause() instanceof IndexMissingException);
        }
    }

    /**
     * EsType_deleteメソッドで初回にIndexMissingExceptionを根本原因に持つ例外が投げられた場合のテスト.
     */
    @Test
    public void EsType_deleteメソッドで初回にIndexMissingExceptionを根本原因に持つ例外が投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncDelete()が呼ばれた場合に、IndexMissingExceptionを根本原因に持つ例外を投げる。
        // 送出する例外オブジェクトを作成
        SettingsException toBeThrown = new SettingsException("foo", new IndexMissingException(new Index("dummy")));
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncDelete(Mockito.anyString(), Mockito.anyLong());
        // メソッド呼び出し
        try {
            esTypeObject.delete("dummyId", 1);
            fail("EsClientException should be thrown.");
        } catch (EsClientException.EsIndexMissingException e) {
            assertTrue(e.getCause() instanceof SettingsException);
            assertTrue(e.getCause().getCause() instanceof IndexMissingException);
        }
    }

    /**
     * EsType_deleteメソッドで初回にVersionConflictEngineExceptionが投げられた場合のテスト.
     */
    @Test
    public void EsType_deleteメソッドで初回にVersionConflictEngineExceptionが投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncDelete()が呼ばれた場合に、VersionConflictEngineExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        VersionConflictEngineException toBeThrown = Mockito.mock(VersionConflictEngineException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncDelete(Mockito.anyString(), Mockito.anyLong());
        // メソッド呼び出し
        try {
            esTypeObject.delete("dummyId", 1);
            fail("EsClientException should be thrown.");
        } catch (EsClientException.EsVersionConflictException e) {
            assertTrue(e.getCause() instanceof VersionConflictEngineException);
        }
    }

    /**
     * EsType_putMappingメソッドで初回にIndexMissingExceptionが投げられた場合のテスト.
     */
    @Test
    public void EsType_putMappingメソッドで初回にIndexMissingExceptionが投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncPutMapping()が呼ばれた場合に、IndexMissingExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        IndexMissingException toBeThrown = Mockito.mock(IndexMissingException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncPutMapping(Mockito.anyMapOf(String.class, Object.class));
        // メソッド呼び出し
        try {
            esTypeObject.putMapping(null);
            fail("EsClientException should be thrown.");
        } catch (EsClientException.EsIndexMissingException e) {
            assertTrue(e.getCause() instanceof IndexMissingException);
        }
    }

    /**
     * EsType_putMappingメソッドで初回にIndexMissingExceptionを根本原因に持つ例外が投げられた場合のテスト.
     */
    @Test
    public void EsType_putMappingメソッドで初回にIndexMissingExceptionを根本原因に持つ例外が投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncPutMapping()が呼ばれた場合に、IndexMissingExceptionを根本原因に持つ例外投げる。
        // 送出する例外オブジェクトを作成
        SettingsException toBeThrown = new SettingsException("foo", new IndexMissingException(new Index("dummy")));
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncPutMapping(Mockito.anyMapOf(String.class, Object.class));
        // メソッド呼び出し
        try {
            esTypeObject.putMapping(null);
            fail("EsClientException should be thrown.");
        } catch (EsClientException.EsIndexMissingException e) {
            assertTrue(e.getCause() instanceof SettingsException);
            assertTrue(e.getCause().getCause() instanceof IndexMissingException);
        }
    }

    /**
     * EsType_putMappingメソッドで初回にMapperParsingExceptionが投げられた場合のテスト.
     */
    @Test
    public void EsType_putMappingメソッドで初回にMapperParsingExceptionが投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsTypeImpl esTypeObject = Mockito.spy(new EsTypeImpl("dummy", "Test", "TestRoutingId", 0, 0, null));

        // EsType#asyncPutMapping()が呼ばれた場合に、MapperParsingExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        MapperParsingException toBeThrown = Mockito.mock(MapperParsingException.class);
        Mockito.doThrow(toBeThrown)
                .when(esTypeObject)
                .asyncPutMapping(Mockito.anyMapOf(String.class, Object.class));
        // メソッド呼び出し
        try {
            esTypeObject.putMapping(null);
            fail("EsClientException should be thrown.");
        } catch (EsClientException.EsSchemaMismatchException e) {
            assertTrue(e.getCause() instanceof MapperParsingException);
        }
    }

    /**
     * EsIndex_searchメソッドで初回にIndexMissingExceptionが投げられた場合のテスト.
     */
    @Test
    public void EsIndex_searchメソッドで初回にIndexMissingExceptionが投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsIndexImpl esIndexObject = Mockito.spy(new EsIndexImpl("dummy", "", 0, 0, null));

        // EsIndex#asyncIndexSearch()が呼ばれた場合に、IndexMissingExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        IndexMissingException toBeThrown = Mockito.mock(IndexMissingException.class);
        Mockito.doThrow(toBeThrown)
                .when(esIndexObject)
                .asyncIndexSearch(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class));
        // メソッド呼び出し
        DcSearchResponse result = esIndexObject.search("dummyRoutingId", (Map<String, Object>) null);
        assertNull(result);
    }

    /**
     * EsIndex_searchメソッドで初回にIndexMissingExceptionを根本原因に持つ例外が投げられた場合のテスト.
     */
    @Test
    public void EsIndex_searchメソッドで初回にIndexMissingExceptionを根本原因に持つ例外が投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsIndexImpl esIndexObject = Mockito.spy(new EsIndexImpl("dummy", "", 0, 0, null));

        // EsIndex#asyncIndexSearch()が呼ばれた場合に、IndexMissingExceptionを根本原因に持つ例外を投げる。
        // 送出する例外オブジェクトを作成
        SettingsException toBeThrown = new SettingsException("foo", new IndexMissingException(new Index("dummy")));
        Mockito.doThrow(toBeThrown)
                .when(esIndexObject)
                .asyncIndexSearch(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class));
        // メソッド呼び出し
        DcSearchResponse result = esIndexObject.search("dummyRoutingId", (Map<String, Object>) null);
        assertNull(result);
    }

    /**
     * EsIndex_searchメソッドで初回にSearchPhaseExecutionExceptionが投げられた場合のテスト.
     */
    @Test
    public void EsIndex_searchメソッドで初回にSearchPhaseExecutionExceptionが投げられた場合のテスト() {
        PowerMockito.mockStatic(EsClientException.class);
        EsIndexImpl esIndexObject = Mockito.spy(new EsIndexImpl("dummy", "", 0, 0, null));

        // EsIndex#asyncIndexSearch()が呼ばれた場合に、SearchPhaseExecutionExceptionを投げる。
        // 送出する例外オブジェクトのモックを作成
        SearchPhaseExecutionException toBeThrown = Mockito.mock(SearchPhaseExecutionException.class);
        Mockito.doThrow(toBeThrown)
                .when(esIndexObject)
                .asyncIndexSearch(Mockito.anyString(), Mockito.anyMapOf(String.class, Object.class));
        // メソッド呼び出し
        try {
            esIndexObject.search("dummyRoutingId", (Map<String, Object>) null);
            fail("EsClientException should be thrown.");
        } catch (EsClientException e) {
            assertTrue(e.getCause() instanceof SearchPhaseExecutionException);
        }
    }

}
