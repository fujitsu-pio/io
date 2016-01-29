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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

/**
 * リペア対象ログファイルのファイルフィルターのテストクラス.
 */
public class AdsWriteFailureLogFilterTest {

    /**
     * テスト用ユーティリティメソッド.
     * @param targetFileName ファイル名
     * @return Acceptさるか否かを論理値で返す。
     * @throws IOException テスト用ファイル作成失敗時
     */
    private boolean checkAcceptResult(String targetFileName) throws IOException {
        File file = new File(targetFileName);
        file.deleteOnExit();
        try {
            file.createNewFile();
            AdsWriteFailureLogFilter filter = new AdsWriteFailureLogFilter("000");
            return filter.accept(new File("."), targetFileName);
        } finally {
            file.delete();
        }
    }

    /**
     * アクティブなリペアログファイル名を持つファイルはリジェクトされること.
     * @throws IOException テスト用ファイルが作成できない場合.
     */
    @Test
    public void アクティブなリペアログファイル名を持つファイルはリジェクトされること() throws IOException {
        String targetFileName = String.format("adsWriteFailure_%s_%d.log", "000", 1234567890123L);
        assertFalse(checkAcceptResult(targetFileName));
    }

    /**
     * リペアログファイル名が不正なファイルはリジェクトされること.
     * @throws IOException テスト用ファイルが作成できない場合.
     */
    @Test
    public void リペアログファイル名が不正なファイルはリジェクトされること() throws IOException {
        String targetFileName = String.format("Invalid_%s.log.%d", "000", 1234567890L);
        assertFalse(checkAcceptResult(targetFileName));
    }

    /**
     * 通常のリペアログファイル名で正しくacceptされること.
     * @throws IOException テスト用ファイルが作成できない場合.
     */
    @Test
    public void 通常のリペアログファイル名で正しくacceptされること() throws IOException {
        String targetFileName = String.format("adsWriteFailure_%s.log.%d", "000", 1234567890123L);
        assertTrue(checkAcceptResult(targetFileName));
    }

    /**
     * 通常のリペアログファイル名だがタイムスタンプ部の桁数不正なファイルはリジェクトされること_桁数不足.
     * @throws IOException テスト用ファイルが作成できない場合.
     */
    @Test
    public void 通常のリペアログファイル名だがタイムスタンプ部の桁数不正なファイルはリジェクトされること_桁数不足() throws IOException {
        String targetFileName = String.format("adsWriteFailure_%s.log.%d", "000", 1234567890L);
        assertFalse(checkAcceptResult(targetFileName));
    }

    /**
     * 通常のリペアログファイル名だがタイムスタンプ部の桁数不正なファイルはリジェクトされること_桁数過剰.
     * @throws IOException テスト用ファイルが作成できない場合.
     */
    @Test
    public void 通常のリペアログファイル名だがタイムスタンプ部の桁数不正なファイルはリジェクトされること_桁数過剰() throws IOException {
        String targetFileName = String.format("adsWriteFailure_%s.log.%d", "000", 12345678901234L);
        assertFalse(checkAcceptResult(targetFileName));
    }

    /**
     * Retry用のリペアログファイル名で正しくacceptされること.
     * @throws IOException テスト用ファイルが作成できない場合.
     */
    @Test
    public void Retry用のリペアログファイル名で正しくacceptされること() throws IOException {
        String targetFileName = String.format("adsWriteFailure_%s.log.%d.retry", "000", 1234567890123L);
        assertTrue(checkAcceptResult(targetFileName));
    }

    /**
     * Retry用のリペアログファイル名だがタイムスタンプ部の桁数不正なファイルはリジェクトされること_桁数不足.
     * @throws IOException テスト用ファイルが作成できない場合.
     */
    @Test
    public void Retry用のリペアログファイル名だがタイムスタンプ部の桁数不正なファイルはリジェクトされること_桁数不足() throws IOException {
        String targetFileName = String.format("adsWriteFailure_%s.log.%d.retry", "000", 1234567890L);
        assertFalse(checkAcceptResult(targetFileName));
    }

    /**
     * Retry用のリペアログファイル名だがタイムスタンプ部の桁数不正なファイルはリジェクトされること_桁数過剰.
     * @throws IOException テスト用ファイルが作成できない場合.
     */
    @Test
    public void Retry用のリペアログファイル名だがタイムスタンプ部の桁数不正なファイルはリジェクトされること_桁数過剰() throws IOException {
        String targetFileName = String.format("adsWriteFailure_%s.log.%d.retry", "000", 12345678901234L);
        assertFalse(checkAcceptResult(targetFileName));
    }

    /**
     * Retry用のリペアログファイル名に似ているもののサフィックスが異なるファイルはリジェクトされること.
     * @throws IOException テスト用ファイルが作成できない場合.
     */
    @Test
    public void Retry用のリペアログファイル名に似ているもののサフィックスが異なるファイルはリジェクトされること() throws IOException {
        String targetFileName = String.format("adsWriteFailure_%s.log.%d.retryX", "000", 1234567890123L);
        assertFalse(checkAcceptResult(targetFileName));
    }
}
