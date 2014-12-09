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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ADS書き込み失敗ログへの出力処理を司るクラス.
 * 本クラスは singleton で実装し、スレッド間での出力を排他する。<br />
 * また、プロセス間(PCSバージョン間)での出力は、別ファイルへの出力とすることで排他を考慮しない。<br />
 * なお、ADS書き込み失敗ログは、実行サーバのローカルファイルシステムへ出力するため、複数サーバ間での排他は考慮しない。
 */
public final class AdsWriteFailureLogWriter extends AbstractAdsWriteFailureLog {

    static Logger logger = LoggerFactory.getLogger(AdsWriteFailureLogWriter.class);

    // ADS書き込み失敗ログ
    private static AdsWriteFailureLogWriter singleton;
    // リトライ可能なエラーが発生した場合にADS書き込み失敗ログの内容を出力するためのログファイル
    private static AdsWriteFailureLogWriter singletonRepair;
    // リトライ不可能なエラーが発生した場合にADS書き込み失敗ログの内容を退避するためのログファイル
    private static AdsWriteFailureLogWriter singletonRepairError;
    static {
        singleton = new AdsWriteFailureLogWriter();
        singletonRepair = new AdsWriteFailureLogWriter();
        singletonRepairError = new AdsWriteFailureLogWriter();
    }

    /**
     * 出力中のADS書き込み失敗ログファイル名.
     */
    private long createdTime = -1L;
    private String fileFormat;
    private String rotateFileFormat;

    private FileOutputStream activeFileOutputStream = null;

    /**
     * デフォルトコンストラクタ（使用不可）.
     */
    private AdsWriteFailureLogWriter() {
        super();
    }

    /**
     * ADS書き込み失敗ログ用インスタンスの取得.
     * @param baseDir ログ出力用ディレクトリ
     * @param pcsVersion PCSバージョン
     * @param physicalDelete ADS書き込み失敗ログを物理削除するか否かを示すフラグ（default： true）
     * @return singletonインスタンス
     */
    public static synchronized AdsWriteFailureLogWriter getInstance(
            String baseDir, String pcsVersion, boolean physicalDelete) {
        if (null == singleton) {
            singleton = new AdsWriteFailureLogWriter();
        }
        // TODO このように singletonのフィールドを置き換えるのは、getInstance()の意味からいうと避けるべき。
        //  MultiThread処理では、片方の getInstance()呼出が、もう一方の処理に影響を及ぼしてしまう。
        //   ただし本メソッドの引数には、DcCoreConfigから得られる情報を渡すことを想定している。 本クラスが属する commonモジュールからは
        //   DcCoreConfigを参照できないため、本クラス/DcCoreConfigの属するモジュールをリファクタする以外の手段がないため、
        //   今回はこのままにしておく。
        synchronized (singleton) {
            singleton.baseDir = baseDir;
            singleton.pcsVersion = pcsVersion;
            singleton.isPhysicalDelete = physicalDelete;
            singleton.fileFormat = LOGNAME_FORMAT_ACTIVE;
            singleton.rotateFileFormat = LOGNAME_FORMAT_ROTATE;
        }
        return singleton;
    }

    /**
     * リトライのためのADS書き込み失敗ログ用インスタンスの取得.
     * @param baseDir ログ出力用ディレクトリ
     * @param pcsVersion PCSバージョン
     * @param physicalDelete ADS書き込み失敗ログを物理削除するか否かを示すフラグ（default： true）
     * @return singletonインスタンス
     */
    public static synchronized AdsWriteFailureLogWriter getInstanceforRetry(
            String baseDir, String pcsVersion, boolean physicalDelete) {
        if (null == singletonRepair) {
            singletonRepair = new AdsWriteFailureLogWriter();
        }
        // TODO このように singletonのフィールドを置き換えるのは、getInstance()の意味からいうと避けるべき。
        //  MultiThread処理では、片方の getInstance()呼出が、もう一方の処理に影響を及ぼしてしまう。
        //   ただし本メソッドの引数には、DcCoreConfigから得られる情報を渡すことを想定している。 本クラスが属する commonモジュールからは
        //   DcCoreConfigを参照できないため、本クラス/DcCoreConfigの属するモジュールをリファクタする以外の手段がないため、
        //   今回はこのままにしておく。
        synchronized (singletonRepair) {
            singletonRepair.baseDir = baseDir;
            singletonRepair.pcsVersion = pcsVersion;
            singletonRepair.isPhysicalDelete = physicalDelete;
            singletonRepair.fileFormat = LOGNAME_FORMAT_ACTIVE + RETRY_LOGNAME_SUFFIX;
            singletonRepair.rotateFileFormat = LOGNAME_FORMAT_ROTATE + RETRY_LOGNAME_SUFFIX;
        }
        return singletonRepair;
    }

    /**
     * エラーのためのADS書き込み失敗ログ用インスタンスの取得.
     * @param baseDir ログ出力用ディレクトリ
     * @param pcsVersion PCSバージョン
     * @param physicalDelete ADS書き込み失敗ログを物理削除するか否かを示すフラグ（default： true）
     * @return singletonインスタンス
     */
    public static synchronized AdsWriteFailureLogWriter getInstanceforError(
            String baseDir, String pcsVersion, boolean physicalDelete) {
        if (null == singletonRepairError) {
            singletonRepairError = new AdsWriteFailureLogWriter();
        }
        // TODO このように singletonのフィールドを置き換えるのは、getInstance()の意味からいうと避けるべき。
        //  MultiThread処理では、片方の getInstance()呼出が、もう一方の処理に影響を及ぼしてしまう。
        //   ただし本メソッドの引数には、DcCoreConfigから得られる情報を渡すことを想定している。 本クラスが属する commonモジュールからは
        //   DcCoreConfigを参照できないため、本クラス/DcCoreConfigの属するモジュールをリファクタする以外の手段がないため、
        //   今回はこのままにしておく。
        synchronized (singletonRepairError) {
            singletonRepairError.baseDir = baseDir;
            singletonRepairError.pcsVersion = pcsVersion;
            singletonRepairError.isPhysicalDelete = physicalDelete;
            singletonRepairError.fileFormat = LOGNAME_FORMAT_ACTIVE + ERROR_LOGNAME_SUFFIX;
            singletonRepairError.rotateFileFormat = LOGNAME_FORMAT_ROTATE + ERROR_LOGNAME_SUFFIX;
        }
        return singletonRepairError;
    }

    /**
     * 出力中のADS書き込み失敗ログをオープンする. <br />
     * ただし、スレッドセーフで実装するため OutputStreamは返却せず、本クラスに対して出力メソッドを呼び出すこと
     * @throws AdsWriteFailureLogException 以下のいずれかのケースでスローされる。
     *         <ul>
     *         <li>既にファイルが存在する場合</li>
     *         <li>ファイルのオープンに失敗した場合</li>
     *         </ul>
     */
    public synchronized void openActiveFile() throws AdsWriteFailureLogException {
        File file = new File(getBaseDir(), String.format(LOGNAME_FORMAT_ACTIVE, getPcsVersion(), createdTime));
        if (null != activeFileOutputStream) {
            String message = String.format("Acitve adsWriteFailureLog is already opened. [%s]", file.getAbsolutePath());
            logger.info(message);
            return;
        }
        synchronized (this) {
            // ADS書き込み失敗ログがオープンされていないがファイルが存在する場合は、異常事態とみなしてエラーにする。
            if (file.isFile()) {
                String message = String.format("Active adsWriteFailureLog already exists, but is not opened. [%s]",
                        file.getAbsolutePath());
                resetFilelds(); // 次の処理のために初期化する。
                throw new AdsWriteFailureLogException(message);
            }
            // ADS書き込み失敗ログは、追記モードでファイルをオープンする。
            File newFile = null;
            try {
                createdTime = System.currentTimeMillis();
                newFile = new File(getBaseDir(), String.format(LOGNAME_FORMAT_ACTIVE, getPcsVersion(), createdTime));
                // default encoding(UTF-8)
                activeFileOutputStream = new FileOutputStream(newFile, true);
            } catch (FileNotFoundException e) {
                String message = String.format("Failed to open acitve adsWriteFailureLog. [%s]",
                        newFile.getAbsolutePath());
                resetFilelds(); // 次の処理のために初期化する。
                throw new AdsWriteFailureLogException(message, e);
            }
        }
    }

    /**
     * 出力中のADS書き込み失敗ログをオープンする. <br />
     * 引数で受け取ったタイムスタンプが、処理中のものと異なる場合は、新しいファイルへのアクセスとみなし、
     * 一旦ファイルをクローズしてからローテートして、新しいファイルを作成する。
     * ただし、スレッドセーフで実装するため OutputStreamは返却せず、本クラスに対して出力メソッドを呼び出すこと
     * @param timeStamp ADS書き込み失敗ファイルに付加するタイムスタンプ
     * @throws AdsWriteFailureLogException 以下のいずれかのケースでスローされる。
     *         <ul>
     *         <li>既にファイルが存在する場合</li>
     *         <li>ファイルのオープンに失敗した場合</li>
     *         </ul>
     */
    public synchronized void openActiveFile(long timeStamp) throws AdsWriteFailureLogException {
        synchronized (this) {
            // 既に開いているファイルと同じファイルを操作する場合は何もしない。
            if (createdTime == timeStamp) {
                return;
            }

            // 既存のファイルを開いていた場合は閉じて、ローテートさせる。
            if (isExistsAdsWriteFailureLogs()) {
                rotateActiveFile();
                closeActiveFile();
            }

            // ADS書き込み失敗ログは、追記モードでファイルをオープンする。
            File newFile = null;
            try {
                createdTime = timeStamp;
                newFile = new File(getBaseDir(), String.format(fileFormat, getPcsVersion(), createdTime));
                // default encoding(UTF-8)
                activeFileOutputStream = new FileOutputStream(newFile, true);
            } catch (FileNotFoundException e) {
                String message = String.format("Failed to open acitve adsWriteFailureLog. [%s]",
                        newFile.getAbsolutePath());
                resetFilelds(); // 次の処理のために初期化する。
                throw new AdsWriteFailureLogException(message, e);
            }
        }
    }


    /**
     * 出力中のADS書き込み失敗ログをクローズする. <br />
     * @throws AdsWriteFailureLogException 以下のいずれかのケースでスローされる。
     *         <ul>
     *         <li>出力バッファに残っているデータの書き込みに失敗した場合</li>
     *         </ul>
     * @throws AdsWriteFailureLogException
     */
    public synchronized void closeActiveFile() throws AdsWriteFailureLogException {
        if (null == activeFileOutputStream) {
            logger.info("Acitve adsWriteFailureLog is not opened.");
            return;
        }
        IOUtils.closeQuietly(activeFileOutputStream);
        resetFilelds();
    }

    /**
     * ADS書き込み失敗ログへの書き込み処理.
     * @param output ログ出力情報
     * @throws AdsWriteFailureLogException ログへの書き込みに失敗した場合
     */
    public synchronized void writeActiveFile(AdsWriteFailureLogInfo output) throws AdsWriteFailureLogException {
        if (null == output) {
            String message = "No data log infomation.";
            throw new AdsWriteFailureLogException(message);
        }
        writeActiveFile(output.toString());
    }

    /**
     * ADS書き込み失敗ログへの書き込み処理.
     * @param output ログ出力情報の文字列表現
     * @throws AdsWriteFailureLogException ログへの書き込みに失敗した場合
     */
    public synchronized void writeActiveFile(String output) throws AdsWriteFailureLogException {
        if (null == output) {
            String message = "No data log infomation.";
            throw new AdsWriteFailureLogException(message);
        }

        byte[] outputBytes = output.getBytes();
        // ファイルが未オープンの場合には、オープンする
        openActiveFile();

        File file = new File(getBaseDir(), String.format(fileFormat, getPcsVersion(), createdTime));
        // ファイルのクローズ、新規オープン処理等とは排他が必要
        synchronized (this) {
            if (null == activeFileOutputStream) {
                String message = String.format("Acitve adsWriteFailureLog does not exist. [%s]",
                        file.getAbsolutePath());
                resetFilelds(); // 次の処理のために初期化する。
                throw new AdsWriteFailureLogException(message);
            }

            try {
                // DISKへの書き込みを確定させるため、ファイルディスクリプタでsyncする。
                // これにより処理性能が劣化するが、書き込み保証を優先する。
                activeFileOutputStream.write(outputBytes);
                activeFileOutputStream.getFD().sync();
            } catch (IOException e) {
                // ここでエラーを検知してもフィールドは初期化しない。初期化したい場合は、closeすること。
                String message = String.format("Failed to write acitve adsWriteFailureLog. [%s]",
                        file.getAbsolutePath());
                throw new AdsWriteFailureLogException(message, e);
            }
        }
    }


    /**
     * ADS書き込み失敗ログをローテートする。
     * @return ローテートしたログのファイル
     * @throws AdsWriteFailureLogException ログのローテートに失敗した場合
     */
    public synchronized File rotateActiveFile() throws AdsWriteFailureLogException {
        File srcFile = new File(getBaseDir(), String.format(fileFormat, getPcsVersion(), createdTime));
        File dstFile = new File(getBaseDir(), String.format(rotateFileFormat, getPcsVersion(), createdTime));

        if (null == activeFileOutputStream) {
            return null;
        }
        // ファイルクローズ直後に新たなファイル書き込みが行われないよう、openActiveFileとは排他を取る。
        synchronized (this) {
            // オープンしているOutputStreamをクローズしてからリネームする。
            closeActiveFile();
            if (!srcFile.renameTo(dstFile)) {
                // ここでエラーを検知してもフィールドは初期化しない。原因を究明してから原因別に対処を行う。
                String message = String.format("Failed to rotate acitve adsWriteFailureLog. [%s,%s]",
                        srcFile.getAbsolutePath(), dstFile.getAbsolutePath());
                throw new AdsWriteFailureLogException(message);
            }
            return dstFile;
        }
    }

    /**
     * 出力中のADS書き込み失敗ログを削除する. <br />
     * @throws AdsWriteFailureLogException ログの削除に失敗した場合
     */
    public synchronized void deleteActiveLog() throws AdsWriteFailureLogException {
        File file = new File(getBaseDir(), String.format(fileFormat, getPcsVersion(), createdTime));
        // rotateAciveFile()/openActiveFile()とは排他を取る。
        synchronized (this) {
            // ファイルを削除する前にOutputStreamをクローズする必要がある。
            // これにより、出力先がなくなるため、本クラス内で管理しているフィールド情報を初期化する必要がある。
            IOUtils.closeQuietly(activeFileOutputStream);
            super.deleteRotatedLog(file);
            resetFilelds();
        }
    }

    /**
     * 出力中のADS書き込み失敗ログが存在しているか否かを返す.
     * @return ログが存在する場合は trueを、それ以外は falseを返す
     */
    public boolean isExistsAdsWriteFailureLogs() {
        if (null != activeFileOutputStream) {
            return true;
        }
        return false;
    }

    /**
     * ADS書き込み失敗ログに関するフィールドの初期化を行う. <br />
     * 異常発生時などでも使用する。
     */
    private void resetFilelds() {
        synchronized (this) {
            createdTime = -1L;
            activeFileOutputStream = null;
        }
    }
}
