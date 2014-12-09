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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

/**
 * ADS書き込み失敗ログへの出力処理を司るクラス. <br />
 * 本クラスは singleton で実装し、スレッド間での出力を排他する。<br />
 * また、プロセス間(PCSバージョン間)での出力は、別ファイルへの出力とすることで排他を考慮しない。<br />
 * なお、ADS書き込み失敗ログは、実行サーバのローカルファイルシステムへ出力するため、複数サーバ間での排他は考慮しない。
 */
public final class RollingAdsWriteFailureLog extends AbstractAdsWriteFailureLog {

    private File rotatedAdsWriteFailureLog;
    private LineNumberReader reader;

    /**
     * デフォルトコンストラクタ（使用不可）.
     */
    private RollingAdsWriteFailureLog() {
        super();
    }

    /**
     * コンストラクタ.
     * @param rotatedAdsWriteFailureLog ローテートされたADS書き込み失敗ログファイル
     * @param baseDir ログ出力用ディレクトリ
     * @param pcsVersion PCSバージョン
     * @param physicalDelete ADS書き込み失敗ログを物理削除するか否かを示すフラグ（default： true）
     */
    public RollingAdsWriteFailureLog(File rotatedAdsWriteFailureLog,
            String baseDir,
            String pcsVersion,
            boolean physicalDelete) {
        this();
        this.rotatedAdsWriteFailureLog = rotatedAdsWriteFailureLog;
        this.baseDir = baseDir;
        this.pcsVersion = pcsVersion;
        this.isPhysicalDelete = physicalDelete;
    }

    /**
     * ローテートされたADS書き込み失敗ログファイル名からファイルの作成時刻を取得する.
     * @param pcsVersion PCSのバージョン
     * @param rotatedAdsWriteFailureLog ローテートされたADS書き込み失敗ログファイル
     * @return 取得したファイルの作成時刻
     * @throws AdsWriteFailureLogException ローテートされたADS書き込み失敗ログファイルが存在しない、またはファイル名の書式が正しくない場合
     */
    public static long getCreatedTimeFromFileName(String pcsVersion, File rotatedAdsWriteFailureLog)
            throws AdsWriteFailureLogException {
        final String messageFormat = "Illegal Rotated adsWriteFailureLog file name format. [%s]";

        if (!rotatedAdsWriteFailureLog.isFile()) {
            // ファイルが存在しない場合は異常状態とみなしてエラーとする。
            String message = String.format("Rotated adsWriteFailureLog is not found. [%s]",
                    rotatedAdsWriteFailureLog.getAbsolutePath());
            throw new AdsWriteFailureLogException(message);
        }
        String fileName = rotatedAdsWriteFailureLog.getName();
        String fileFormat = String.format(LOGNAME_FORMAT_ROTATE, pcsVersion, 0);
        String filePattern = fileFormat.substring(0, fileFormat.length() - 1);
        long createdTime = -1L;
        if (fileName.startsWith(filePattern)) {
            String createdTimeStr = fileName.replace(filePattern, "");
            try {
                createdTime = Long.parseLong(createdTimeStr);
            } catch (NumberFormatException e) {
                String message = String.format(messageFormat, rotatedAdsWriteFailureLog.getAbsolutePath());
                if (fileName.endsWith(LOGICAL_DELETED_LOGNAME_SUFFIX)) {
                    message = String.format("Logical deleted adsWriteFailureLog file. [%s]",
                            rotatedAdsWriteFailureLog.getAbsolutePath());
                }
                throw new AdsWriteFailureLogException(message);
            }
        } else {
            String message = String.format(messageFormat, rotatedAdsWriteFailureLog.getAbsolutePath());
            throw new AdsWriteFailureLogException(message);
        }
        return createdTime;
    }

    /**
     * ローテートされたADS書き込み失敗ログをオープンする.
     * @throws AdsWriteFailureLogException ADS書き込み失敗ログのオープンに失敗した場合
     */
    public void openRotatedFile() throws AdsWriteFailureLogException {
        try {
            // default encoding(UTF-8)
            reader = new LineNumberReader(new BufferedReader(new FileReader(this.rotatedAdsWriteFailureLog)));
        } catch (FileNotFoundException e) {
            String messsage = String.format("Failed to open rotated adsWriteFailureLog. [%s]",
                    rotatedAdsWriteFailureLog.getAbsolutePath());
            throw new AdsWriteFailureLogException(messsage, e);
        }
    }

    /**
     * ローテートされたADS書き込み失敗ログをクローズする.
     */
    public synchronized void closeRotatedFile() {
        if (null == reader) {
            logger.info("Acitve adsWriteFailureLog is not opened.");
            return;
        }
        IOUtils.closeQuietly(reader);
    }

    /**
     * 引数で渡されたバッファにADS書き込み失敗ログを読み込む.
     * @param recordNum 読み込む行数
     * @return 読み込んだログデータのリスト
     * @throws AdsWriteFailureLogException ADS書き込み失敗ログの読み込みに失敗した場合
     */
    public List<String> readAdsFailureLog(int recordNum) throws AdsWriteFailureLogException {
        try {
            if (null == this.reader) {
                reader = new LineNumberReader(new BufferedReader(new FileReader(this.rotatedAdsWriteFailureLog)));
            }
            List<String> logRecords = new ArrayList<String>();
            for (int i = 0; i < recordNum; i++) {
                String aRecord = reader.readLine();
                if (null == aRecord) {
                    break;
                }
                logRecords.add(aRecord);
            }
            return logRecords;
        } catch (IOException e) {
            String messsage = String.format("Failed to read rotated adsWriteFailureLog. [%s]",
                    rotatedAdsWriteFailureLog.getAbsolutePath());
            throw new AdsWriteFailureLogException(messsage, e);
        }
    }

    /**
     * ローテートされたADS書き込み失敗ログを削除する. <br />
     * @throws AdsWriteFailureLogException ログの削除に失敗した場合
     */
    public synchronized void deleteRotatedLog() throws AdsWriteFailureLogException {
        closeRotatedFile();
        super.deleteRotatedLog(rotatedAdsWriteFailureLog);
    }

}
