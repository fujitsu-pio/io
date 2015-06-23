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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

import org.junit.Test;

/**
 * ADS書き込み失敗ログ出力情報クラスのユニットテスト.
 */
public class RollingAdsWriteFailureLogTest {

    private static final String PIO_VERSION_DUMMY = "1.3.18-test";
    private static final long CREATED_TIME_DUMMY = 11111111111L;

    /**
     * ローテートされたADS書き込み失敗ログのファイル名書式が正しい場合に作成時刻が取得できること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test
    public void ローテートされたADS書き込み失敗ログのファイル名書式が正しい場合に作成時刻が取得できること() throws AdsWriteFailureLogException {
        final String fileName = String.format("adsWriteFailure_%s.log.%d", PIO_VERSION_DUMMY, CREATED_TIME_DUMMY);
        File file = new File("./", fileName);
        try {
            if (!file.createNewFile()) {
                fail("Failed to create temporary file. " + file.getAbsolutePath());
            }
            long createdTime = RollingAdsWriteFailureLog.getCreatedTimeFromFileName(PIO_VERSION_DUMMY, file);
            assertEquals(CREATED_TIME_DUMMY, createdTime);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (null != file) {
                file.delete();
            }
        }
    }

    /**
     * ローテートされたADS書き込み失敗ログのファイル名書式が正しいがファイルが存在しない場合にエラーとなること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test(expected = AdsWriteFailureLogException.class)
    public void ローテートされたADS書き込み失敗ログのファイル名書式が正しいがファイルが存在しない場合にエラーとなること() throws AdsWriteFailureLogException {
        final String fileName = String.format("adsWriteFailure_%s.log.%d", PIO_VERSION_DUMMY, CREATED_TIME_DUMMY);
        File file = new File("./", fileName);
        RollingAdsWriteFailureLog.getCreatedTimeFromFileName(PIO_VERSION_DUMMY, file);
    }

    /**
     * ローテートされたADS書き込み失敗ログのファイル名が論理削除ファイルの場合にエラーとなること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test(expected = AdsWriteFailureLogException.class)
    public void ローテートされたADS書き込み失敗ログのファイル名が論理削除ファイルの場合にエラーとなること() throws AdsWriteFailureLogException {
        final String fileName = String.format("adsWriteFailure_%s.log.%d.done", PIO_VERSION_DUMMY, CREATED_TIME_DUMMY);
        File file = new File("./", fileName);
        try {
            if (!file.createNewFile()) {
                fail("Failed to create temporary file. " + file.getAbsolutePath());
            }
            RollingAdsWriteFailureLog.getCreatedTimeFromFileName(PIO_VERSION_DUMMY, file);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (null != file) {
                file.delete();
            }
        }
    }

    /**
     * ローテートされたADS書き込み失敗ログのファイル名書式が正しくない場合にエラーとなること1.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test(expected = AdsWriteFailureLogException.class)
    public void ローテートされたADS書き込み失敗ログのファイル名書式が正しくない場合にエラーとなること1() throws AdsWriteFailureLogException {
        final String fileName = String.format("adsWriteFailure_%s.log", PIO_VERSION_DUMMY);
        File file = new File("./", fileName);
        try {
            if (!file.createNewFile()) {
                fail("Failed to create temporary file. " + file.getAbsolutePath());
            }
            RollingAdsWriteFailureLog.getCreatedTimeFromFileName(PIO_VERSION_DUMMY, file);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (null != file) {
                file.delete();
            }
        }
    }

    /**
     * ローテートされたADS書き込み失敗ログのファイル名書式が正しくない場合にエラーとなること2.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test(expected = AdsWriteFailureLogException.class)
    public void ローテートされたADS書き込み失敗ログのファイル名書式が正しくない場合にエラーとなること2() throws AdsWriteFailureLogException {
        final String fileName = String.format("adsWriteFailure_%s.log_%d", PIO_VERSION_DUMMY, CREATED_TIME_DUMMY);
        File file = new File("./", fileName);
        try {
            if (!file.createNewFile()) {
                fail("Failed to create temporary file. " + file.getAbsolutePath());
            }
            RollingAdsWriteFailureLog.getCreatedTimeFromFileName(PIO_VERSION_DUMMY, file);
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (null != file) {
                file.delete();
            }
        }
    }

    /**
     * ローテートされたADS書き込み失敗ログがNULLの場合にログ削除がエラーとなること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test(expected = AdsWriteFailureLogException.class)
    public void ローテートされたADS書き込み失敗ログがNULLの場合にログ削除がエラーとなること() throws AdsWriteFailureLogException {
        File file = null;
        RollingAdsWriteFailureLog rolling = new RollingAdsWriteFailureLog(file, null, PIO_VERSION_DUMMY, true);
        try {
            rolling.deleteRotatedLog();
        } finally {
            if (null != file) {
                file.delete();
            }
        }
    }

    /**
     * ローテートされたADS書き込み失敗ログがファイルではない場合にログ削除が正常終了すること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test
    public void ローテートされたADS書き込み失敗ログがファイルではない場合にログ削除が正常終了すること() throws AdsWriteFailureLogException {
        final String fileName = String.format("adsWriteFailure_%s.log.%d", PIO_VERSION_DUMMY, CREATED_TIME_DUMMY);
        File file = new File("./", fileName);
        RollingAdsWriteFailureLog rolling = new RollingAdsWriteFailureLog(file, null, PIO_VERSION_DUMMY, true);
        try {
            rolling.deleteRotatedLog();
            assertFalse(file.exists());
            file.mkdir();
            rolling.deleteRotatedLog();
            assertTrue(file.isDirectory());
        } finally {
            if (null != file) {
                file.delete();
            }
        }
    }

    /**
     * ローテートされたADS書き込み失敗ログが物理削除モードで削除できること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test
    public void ローテートされたADS書き込み失敗ログが物理削除モードで削除できること() throws AdsWriteFailureLogException {
        final String fileName = String.format("adsWriteFailure_%s.log.%d", PIO_VERSION_DUMMY, CREATED_TIME_DUMMY);
        File file = new File("./", fileName);
        RollingAdsWriteFailureLog rolling = new RollingAdsWriteFailureLog(file, null, PIO_VERSION_DUMMY, true);
        try {
            if (!file.createNewFile()) {
                fail("Failed to create temporary file. " + file.getAbsolutePath());
            }
            rolling.deleteRotatedLog();
            assertFalse(file.exists());
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        } finally {
            if (null != file) {
                file.delete();
            }
        }
    }

    /**
     * ローテートされたADS書き込み失敗ログが論理削除モードで削除できること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test
    public void ローテートされたADS書き込み失敗ログが論理削除モードで削除できること() throws AdsWriteFailureLogException {
        final String fileName = String.format("adsWriteFailure_%s.log.%d", PIO_VERSION_DUMMY, CREATED_TIME_DUMMY);
        File srcFile = new File("./", fileName);
        File dstFile = new File("./", fileName + ".done");
        RollingAdsWriteFailureLog rolling = new RollingAdsWriteFailureLog(srcFile, null, PIO_VERSION_DUMMY, true);
        try {
            if (!srcFile.createNewFile()) {
                fail("Failed to create temporary file. " + srcFile.getAbsolutePath());
            }
        } catch (IOException e) {
            e.printStackTrace();
            fail(e.getMessage());
        }
        Boolean isPhysicalDeleteDefault = true;
        Field isPhysicalDelete = null;
        try {
            Class<?> clazz = AbstractAdsWriteFailureLog.class;
            isPhysicalDelete = clazz.getDeclaredField("isPhysicalDelete");
            isPhysicalDelete.setAccessible(true);
            isPhysicalDeleteDefault = (Boolean) isPhysicalDelete.get(rolling);
            isPhysicalDelete.set(rolling, false);
            rolling.deleteRotatedLog();
            assertFalse(srcFile.exists());
            assertTrue(dstFile.exists());

        } catch (Exception e) {
            e.printStackTrace();
            fail("configuration failed.");
        } finally {
            if (null != srcFile) {
                srcFile.delete();
            }
            if (null != dstFile) {
                dstFile.delete();
            }
            if (null != isPhysicalDelete) {
                try {
                    isPhysicalDelete.set(rolling, isPhysicalDeleteDefault);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
