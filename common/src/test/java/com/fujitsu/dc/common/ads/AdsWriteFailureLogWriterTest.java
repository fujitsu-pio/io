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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.fujitsu.dc.common.ads.AdsWriteFailureLogInfo.OperationKind;

/**
 * ADS書き込み失敗ログ出力情報クラスのユニットテスト.
 */
public class AdsWriteFailureLogWriterTest {

    private static final String PIO_VERSION_DUMMY = "1.3.18-test";

    private AdsWriteFailureLogWriter writer;

    /**
     * すべてのテスト毎に１度実行される処理.
     * @throws InterruptedException InterruptedException
     */
    @Before
    public void before() throws InterruptedException {
        writer = AdsWriteFailureLogWriter.getInstance("./", PIO_VERSION_DUMMY, true);
    }

    /**
     * オープン済みの出力中ADS書き込み失敗ログのパスを取得する.
     * @param writer writer
     * @return ログのパス
     */
    private File getAdsWriteFailureLog() {
        File file = null;
        try {
            Class<?> clazz = AbstractAdsWriteFailureLog.class;
            Field baseDir = clazz.getDeclaredField("baseDir");
            baseDir.setAccessible(true);
            String baseDirV = (String) baseDir.get(writer);
            clazz = writer.getClass();
            Field createdTime = clazz.getDeclaredField("createdTime");
            createdTime.setAccessible(true);
            Long createdTimeV = (Long) createdTime.get(writer);
            final String fileName = String.format(AbstractAdsWriteFailureLog.LOGNAME_FORMAT_ACTIVE, PIO_VERSION_DUMMY,
                    createdTimeV);
            file = new File(baseDirV, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fail("configuration failed.");
        }
        return file;
    }

    /**
     * オープン済みのローテートADS書き込み失敗ログのパスを取得する.
     * @param writer writer
     * @return ログのパス
     */
    private File getRotatedAdsWriteFailureLog() {
        File file = null;
        try {
            Class<?> clazz = AbstractAdsWriteFailureLog.class;
            Field baseDir = clazz.getDeclaredField("baseDir");
            baseDir.setAccessible(true);
            String baseDirV = (String) baseDir.get(writer);
            clazz = writer.getClass();
            Field createdTime = clazz.getDeclaredField("createdTime");
            createdTime.setAccessible(true);
            Long createdTimeV = (Long) createdTime.get(writer);
            final String fileName = String.format(AbstractAdsWriteFailureLog.LOGNAME_FORMAT_ROTATE, PIO_VERSION_DUMMY,
                    createdTimeV);
            file = new File(baseDirV, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            fail("configuration failed.");
        }
        return file;
    }

    /**
     * ADS書き込み失敗ログファイルが正常にオープンできること.
     */
    @Test
    public void ADS書き込み失敗ログファイルが正常にオープンできること() {

        File file = null;
        try {
            writer.openActiveFile();
            file = getAdsWriteFailureLog();
        } catch (AdsWriteFailureLogException e) {
            fail("open failed");
            e.printStackTrace();
        } finally {
            try {
                writer.closeActiveFile();
            } catch (AdsWriteFailureLogException e) {
                e.printStackTrace();
            }
            if (null != file) {
                file.delete();
            }
        }
    }

    /**
     * オープン済みのADS書き込み失敗ログファイルが存在しない場合に再オープン時にエラーとなること.
     */
    @Test
    public void オープン済みのADS書き込み失敗ログファイルが存在しない場合に再オープン時にエラーとなること() {

        File file = getAdsWriteFailureLog();

        try {
            writer.openActiveFile();
            file.delete();
            writer.openActiveFile();
        } catch (AdsWriteFailureLogException e) {
            fail("open failure test failed");
        } finally {
            try {
                writer.closeActiveFile();
            } catch (AdsWriteFailureLogException e) {
                e.printStackTrace();
            } finally {
                file.delete();
            }
        }
    }

    /**
     * オープン済みのADS書き込み失敗ログファイルのOutputStreamが存在しない場合に再オープンでエラーとなること.<br />
     * このテストを有効にした場合、オープンしたファイルが削除できない（Windowsのみ確認）。このため、Ignoreとしておく。
     */
    @Test
    @Ignore
    public void オープン済みのADS書き込み失敗ログファイルのOutputStreamが存在しない場合に再オープンでエラーとなること() {

        try {
            writer.openActiveFile();
        } catch (AdsWriteFailureLogException e) {
            fail("open failure test failed");
        }

        File file = null;
        try {
            Class<?> clazz = writer.getClass();
            Field ostream = clazz.getDeclaredField("activeFileOutputStream");
            ostream.setAccessible(true);
            ostream.set(writer, null);
            file = getAdsWriteFailureLog();
        } catch (Exception e) {
            e.printStackTrace();
            fail("configuration failed.");
        }

        try {
            writer.openActiveFile();
            fail("open failure test failed");
        } catch (AdsWriteFailureLogException e) {
            assertTrue(file.exists());
        } finally {
            if (null != file) {
                file.delete();
            }
        }
    }

    /**
     * ADS書き込み失敗ログファイルへの書き込みが成功すること.
     */
    @Test
    public void ADS書き込み失敗ログファイルへの書き込みが成功すること() {

        long time = System.currentTimeMillis();
        AdsWriteFailureLogInfo loginfo = new AdsWriteFailureLogInfo(
                "u0_anon", "Cell", "odata-lock", "routing-id", "uuid-key", OperationKind.CREATE, 1, time);
        String expected = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\n",
                loginfo.getIndexName(),
                loginfo.getType(),
                loginfo.getLockKey(),
                loginfo.getRoutingId(),
                loginfo.getUuid(),
                loginfo.getOperation(),
                loginfo.getEsVersion(),
                loginfo.getUpdated());
        File file = null;
        try {
            writer.openActiveFile();
            writer.writeActiveFile(loginfo);
            file = getAdsWriteFailureLog();
        } catch (AdsWriteFailureLogException e) {
            fail("open or write failed");
            e.printStackTrace();
        } finally {
            try {
                writer.closeActiveFile();
            } catch (AdsWriteFailureLogException e) {
                e.printStackTrace();
            }
        }
        FileReader reader = null;
        try {
            reader = new FileReader(file);
            char[] cbuf = new char[(int) file.length()];
            reader.read(cbuf, 0, (int) file.length());
            String content = String.valueOf(cbuf);
            assertEquals(expected, content);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IOUtils.closeQuietly(reader);
            if (null != file) {
                file.delete();
            }
        }
    }

    /**
     * ADS書き込み失敗ログファイルが正常にローテートできること.
     */
    @Test
    public void ADS書き込み失敗ログファイルが正常にローテートできること() {

        File srcFile = null;
        File dstFile = null;
        try {
            writer.openActiveFile();
            srcFile = getAdsWriteFailureLog();
            dstFile = writer.rotateActiveFile();
            assertFalse(srcFile.exists());
            assertTrue(dstFile.exists());
        } catch (AdsWriteFailureLogException e) {
            fail("rotate failed");
            e.printStackTrace();
        } finally {
            try {
                writer.closeActiveFile();
            } catch (AdsWriteFailureLogException e) {
                e.printStackTrace();
            }
            if (null != dstFile) {
                dstFile.delete();
            }
        }
    }

    /**
     * ADS書き込み失敗ログファイルが既にディレクトリとして存在する場合にローテートが失敗すること.
     */
    @Test
    public void ADS書き込み失敗ログファイルが既にディレクトリとして存在する場合にローテートが失敗すること() {

        File srcFile = null;
        File dstFile = null;
        try {
            writer.openActiveFile();
            srcFile = getAdsWriteFailureLog();
            dstFile = getRotatedAdsWriteFailureLog();
            dstFile.mkdir();
            writer.rotateActiveFile();
            fail("rotate failed");
        } catch (AdsWriteFailureLogException e) {
            assertTrue(srcFile.exists());
            assertFalse(dstFile.isFile());
        } finally {
            try {
                writer.closeActiveFile();
            } catch (AdsWriteFailureLogException e) {
                e.printStackTrace();
            }
            if (null != srcFile) {
                srcFile.delete();
            }
            if (null != dstFile) {
                dstFile.delete();
            }
        }
    }


    /**
     * 多重度10でADS書き込み失敗ログへ書き込んでもログが正しく出力されること.
     * @throws AdsWriteFailureLogException AdsWriteFailureLogException
     */
    @Test
    public void 多重度10でADS書き込み失敗ログへ書き込んでもログが正しく出力されること() throws AdsWriteFailureLogException {
        // 初期設定
        AdsWriteFailureLogInfo loginfo = new AdsWriteFailureLogInfo(
                "u0_anon", "Cell", "odata-lock", "routing-id", "uuid-key", OperationKind.CREATE, 1, 333333333L);
        String expected = String.format("%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s",
                loginfo.getIndexName(),
                loginfo.getType(),
                loginfo.getLockKey(),
                loginfo.getRoutingId(),
                loginfo.getUuid(),
                loginfo.getOperation(),
                loginfo.getEsVersion(),
                loginfo.getUpdated());

        File dir = new File("./testdir");
        try {
            Class<?> clazz = AbstractAdsWriteFailureLog.class;
            Field baseDir = clazz.getDeclaredField("baseDir");
            baseDir.setAccessible(true);
            baseDir.set(writer, "./testdir");
            if (!dir.mkdir()) {
                fail("mkdir failed(environment error): " + dir.getAbsolutePath());
            }
        } catch (Exception e) {
            e.printStackTrace();
            fail("configuration failed.");
        }
        // ログをオープンして10多重で書き込み
        File file = null;
        try {
            writer.openActiveFile();
            file = getAdsWriteFailureLog(); // 後で出力結果を確認するための情報取得
            List<AdsWriter> threadList = new ArrayList<AdsWriter>();
            for (int i = 0; i < 100; i++) {
                AdsWriter thread = new AdsWriter();
                threadList.add(thread);
                thread.start();
            }
            for (AdsWriter thread : threadList) {
                try {
                    thread.join(); // 各スレッドの終了を待つ
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    fail(e.getMessage());
                }
            }
        } finally {
            // ファイルのクローズ
            try {
                writer.closeActiveFile();
            } catch (AdsWriteFailureLogException e) {
                e.printStackTrace();
            }
            // 出力結果の確認
            // 100スレッドで100回ずつ出力しているため、全部で10000行同じ出力があるはず
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                int count = 0;
                while (reader.ready()) {
                    String line = reader.readLine();
                    assertEquals(expected, line); // 出力結果の確認
                    count++;
                }
                assertEquals(10000, count); // 出力行数の確認
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                IOUtils.closeQuietly(reader);
                if (null != dir) {
                    try {
                        FileUtils.forceDelete(dir);
                    } catch (IOException e) {
                        e.printStackTrace();
                        fail("environment error.");
                    }
                }
            }
        }
    }

    /**
     * ADS書き込み失敗ログを多重実行させるためのクラス.
     */
    class AdsWriter extends Thread {

        /**
         * スレッド実行メソッド.
         */
        public void run() {
            AdsWriteFailureLogWriter logWriter = AdsWriteFailureLogWriter.getInstance("./", PIO_VERSION_DUMMY, true);
            AdsWriteFailureLogInfo loginfo = new AdsWriteFailureLogInfo(
                    "u0_anon", "Cell", "odata-lock", "routing-id", "uuid-key",
                    AdsWriteFailureLogInfo.OperationKind.CREATE, 1, 333333333L);
            try {
                for (int i = 0; i < 100; i++) {
                    logWriter.openActiveFile();
                    logWriter.writeActiveFile(loginfo);
                }
            } catch (AdsWriteFailureLogException e) {
                e.printStackTrace();
                fail("check failed");
            }
        }
    }
}
