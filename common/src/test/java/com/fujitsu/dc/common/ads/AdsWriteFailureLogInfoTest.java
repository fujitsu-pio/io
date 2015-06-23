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
package com.fujitsu.dc.common.ads;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import com.fujitsu.dc.common.ads.AdsWriteFailureLogInfo.OperationKind;;

/**
 * ADS書き込み失敗ログ出力情報クラスのユニットテスト.
 */
public class AdsWriteFailureLogInfoTest {

    /**
     * ログ出力情報を生成して生成時に指定した内容が返却されること.
     */
    @Test
    public void ログ出力情報を生成して生成時に指定した内容が返却されること() {
        long time = System.currentTimeMillis();
        AdsWriteFailureLogInfo info = new AdsWriteFailureLogInfo(
                "u0_anon", "Cell", "odata-lock", "routing-id", "uuid-key", OperationKind.CREATE, 1, time);
        assertEquals("u0_anon", info.getIndexName());
        assertEquals("Cell", info.getType());
        assertEquals("odata-lock", info.getLockKey());
        assertEquals("routing-id", info.getRoutingId());
        assertEquals("uuid-key", info.getUuid());
        assertEquals(OperationKind.CREATE.toString(), info.getOperation());
        assertEquals(1, info.getEsVersion());
        assertEquals(time, info.getUpdated());
    }

    /**
     * ログ出力情報の生成時にnullを指定して空文字が返却されること.
     */
    @Test
    public void ログ出力情報の生成時にnullを指定して空文字が返却されること() {
        AdsWriteFailureLogInfo info = new AdsWriteFailureLogInfo(
                null, null, null, null, null, null, -1, -1);
        assertEquals("", info.getIndexName());
        assertEquals("", info.getType());
        assertEquals("", info.getLockKey());
        assertEquals("", info.getRoutingId());
        assertEquals("", info.getUuid());
        assertEquals("", info.getOperation());
        assertEquals(-1, info.getEsVersion());
        assertEquals(-1, info.getUpdated());
    }

    /**
     * ログ出力情報のパーズ時にnullを指定して空のログ出力情報が返却されること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test
    public void ログ出力情報のパーズ時にnullを指定して空のログ出力情報が返却されること() throws AdsWriteFailureLogException {
        AdsWriteFailureLogInfo info = AdsWriteFailureLogInfo.parse(null);
        assertEquals("", info.getIndexName());
        assertEquals("", info.getType());
        assertEquals("", info.getLockKey());
        assertEquals("", info.getRoutingId());
        assertEquals("", info.getUuid());
        assertEquals("", info.getOperation());
        assertEquals(0, info.getEsVersion());
        assertEquals(0, info.getUpdated());
    }

    /**
     * ログ出力情報のパーズ時に空文字を指定してエラーとなること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test(expected = AdsWriteFailureLogException.class)
    public void ログ出力情報のパーズ時に空文字を指定してエラーとなること() throws AdsWriteFailureLogException {
        AdsWriteFailureLogInfo.parse("");
    }
    /**
     * ログ出力情報のパーズ時にフィールド数の異なる文字列を指定してエラーとなること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test(expected = AdsWriteFailureLogException.class)
    public void ログ出力情報のパーズ時にフィールド数の少ない文字列を指定してエラーとなること() throws AdsWriteFailureLogException {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("u0_690ab80c-af07-4490-8fe2-c68c4b473515\t");
        sbuf.append("ComplexTypeProperty\t");
        sbuf.append("odata-gsX3t2q3Qz6jdIn30fFMaQ\t");
        sbuf.append("aCUuueHzTKCPchE0yxTZZA\t");
        sbuf.append("3LWWu0CFQyCM5zARerPgFg-QdR_YLTsTseImOnF485FUw\t");
        sbuf.append("CREATE\t");
        sbuf.append("1");
        AdsWriteFailureLogInfo.parse(sbuf.toString());
    }

    /**
     * ログ出力情報のパーズ時にフィールド数の異なる文字列を指定してエラーとなること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test(expected = AdsWriteFailureLogException.class)
    public void ログ出力情報のパーズ時にフィールド数の多い文字列を指定してエラーとなること() throws AdsWriteFailureLogException {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("u0_690ab80c-af07-4490-8fe2-c68c4b473515\t");
        sbuf.append("ComplexTypeProperty\t");
        sbuf.append("odata-gsX3t2q3Qz6jdIn30fFMaQ\t");
        sbuf.append("aCUuueHzTKCPchE0yxTZZA\t");
        sbuf.append("3LWWu0CFQyCM5zARerPgFg-QdR_YLTsTseImOnF485FUw\t");
        sbuf.append("CREATE\t");
        sbuf.append("1\t");
        sbuf.append("1408595358931\t");
        sbuf.append("dummy");
        AdsWriteFailureLogInfo.parse(sbuf.toString());
    }

    /**
     * ログ出力情報のパーズ時に操作種別が規定値以外の文字列を指定してエラーとなること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test(expected = AdsWriteFailureLogException.class)
    public void ログ出力情報のパーズ時に操作種別が規定値以外の文字列を指定してエラーとなること() throws AdsWriteFailureLogException {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("u0_690ab80c-af07-4490-8fe2-c68c4b473515\t");
        sbuf.append("ComplexTypeProperty\t");
        sbuf.append("odata-gsX3t2q3Qz6jdIn30fFMaQ\t");
        sbuf.append("aCUuueHzTKCPchE0yxTZZA\t");
        sbuf.append("3LWWu0CFQyCM5zARerPgFg-QdR_YLTsTseImOnF485FUw\t");
        sbuf.append("CREATED\t");
        sbuf.append("1\t");
        sbuf.append("1408595358931");
        AdsWriteFailureLogInfo.parse(sbuf.toString());
    }

    /**
     * ログ出力情報のパーズ時にESバージョンが数値以外の文字列を指定してエラーとなること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test(expected = AdsWriteFailureLogException.class)
    public void ログ出力情報のパーズ時にESバージョンが数値以外の文字列を指定してエラーとなること() throws AdsWriteFailureLogException {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("u0_690ab80c-af07-4490-8fe2-c68c4b473515\t");
        sbuf.append("ComplexTypeProperty\t");
        sbuf.append("odata-gsX3t2q3Qz6jdIn30fFMaQ\t");
        sbuf.append("aCUuueHzTKCPchE0yxTZZA\t");
        sbuf.append("3LWWu0CFQyCM5zARerPgFg-QdR_YLTsTseImOnF485FUw\t");
        sbuf.append("CREATE\t");
        sbuf.append("dummy\t");
        sbuf.append("1408595358931");
        AdsWriteFailureLogInfo.parse(sbuf.toString());
    }

    /**
     * ログ出力情報のパーズ時にupdatedが数値以外の文字列を指定してエラーとなること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test(expected = AdsWriteFailureLogException.class)
    public void ログ出力情報のパーズ時にupdatedが数値以外の文字列を指定してエラーとなること() throws AdsWriteFailureLogException {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("u0_690ab80c-af07-4490-8fe2-c68c4b473515\t");
        sbuf.append("ComplexTypeProperty\t");
        sbuf.append("odata-gsX3t2q3Qz6jdIn30fFMaQ\t");
        sbuf.append("aCUuueHzTKCPchE0yxTZZA\t");
        sbuf.append("3LWWu0CFQyCM5zARerPgFg-QdR_YLTsTseImOnF485FUw\t");
        sbuf.append("CREATE\t");
        sbuf.append("1\t");
        sbuf.append("dummy");
        AdsWriteFailureLogInfo.parse(sbuf.toString());
    }

    /**
     * ログ出力情報のパーズ時に適切な文字列を指定してログ出力情報が返却されること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test
    public void ログ出力情報のパーズ時に適切な文字列を指定してログ出力情報が返却されること() throws AdsWriteFailureLogException {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append("u0_690ab80c-af07-4490-8fe2-c68c4b473515\t");
        sbuf.append("ComplexTypeProperty\t");
        sbuf.append("odata-gsX3t2q3Qz6jdIn30fFMaQ\t");
        sbuf.append("aCUuueHzTKCPchE0yxTZZA\t");
        sbuf.append("3LWWu0CFQyCM5zARerPgFg-QdR_YLTsTseImOnF485FUw\t");
        sbuf.append("CREATE\t");
        sbuf.append("1\t");
        sbuf.append("1408595358931");
        AdsWriteFailureLogInfo info = AdsWriteFailureLogInfo.parse(sbuf.toString());
        assertEquals("u0_690ab80c-af07-4490-8fe2-c68c4b473515", info.getIndexName());
        assertEquals("ComplexTypeProperty", info.getType());
        assertEquals("odata-gsX3t2q3Qz6jdIn30fFMaQ", info.getLockKey());
        assertEquals("aCUuueHzTKCPchE0yxTZZA", info.getRoutingId());
        assertEquals("3LWWu0CFQyCM5zARerPgFg-QdR_YLTsTseImOnF485FUw", info.getUuid());
        assertEquals("CREATE", info.getOperation());
        assertEquals(1, info.getEsVersion());
        assertEquals(1408595358931L, info.getUpdated());
    }
}
