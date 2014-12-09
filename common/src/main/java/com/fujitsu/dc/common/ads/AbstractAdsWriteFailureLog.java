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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ADS書き込み失敗ログへの入出処理に関する抽象クラス.
 */
public abstract class AbstractAdsWriteFailureLog {

    static Logger logger = LoggerFactory.getLogger(AbstractAdsWriteFailureLog.class);

    /**
     * 出力中のADS書き込み失敗ログファイル名のフォーマット.
     */
    public static final String LOGNAME_FORMAT_ACTIVE = "adsWriteFailure_%s_%d.log";

    /**
     * ローテートされたADS書き込み失敗ログファイル名のフォーマット.
     */
    public static final String LOGNAME_FORMAT_ROTATE = "adsWriteFailure_%s.log.%d";

    /**
     * ローテートされたADS書き込み失敗ログの論理削除ファイル名の拡張子.
     */
    public static final String LOGICAL_DELETED_LOGNAME_SUFFIX = ".done";

    /**
     * リトライ可能なADS書き込み失敗ログの論理削除ファイル名の拡張子.
     */
    public static final String RETRY_LOGNAME_SUFFIX = ".retry";

    /**
     * リトライ不可能なADS書き込み失敗ログを出力するファイル名の拡張子.
     */
    public static final String ERROR_LOGNAME_SUFFIX = ".error";

    /**
     * PCSバージョン情報(default: 1). <br />
     * dc-config.propertiesを参照できないものと考え、システムプロパティで処理を行うものとする.<br />
     * ただし、クラスロード時に設定内容が確定してしまうため、書き換えできない。
     */
    String pcsVersion;

    /**
     * ADS書き込み失敗ログ格納ディレクトリ(default: /fjnfs/dc-core/ads) . <br />
     * dc-config.propertiesを参照できないものと考え、システムプロパティで処理を行うものとする.<br />
     * ただし、クラスロード時に設定内容が確定してしまうため、書き換えできない。
     */
    String baseDir;

    /**
     * ADS書き込み失敗ログを物理削除するか否かを示すフラグ（default： true）. <br />
     * dc-config.propertiesを参照できないものと考え、システムプロパティで処理を行うものとする.<br />
     * ただし、クラスロード時に設定内容が確定してしまうため、書き換えできない。
     */
    boolean isPhysicalDelete;

    /**
     * コンストラクタ. <br />
     * 本クラスを継承して使用する場合、コンストラクタで初期化している変数について、適切な値を再設定すること.
     */
    public AbstractAdsWriteFailureLog() {
        pcsVersion = null;
        baseDir = null;
        isPhysicalDelete = true;
    }

    /**
     * PCSバージョンを取得する.
     * @return PCSバージョン
     */
    public String getPcsVersion() {
        return pcsVersion;
    }

    /**
     * ADS書き込み失敗ログ格納ディレクトリのパスを取得する.
     * @return ADS書き込み失敗ログ格納ディレクトリパス
     */
    public String getBaseDir() {
        return baseDir;
    }

    /**
     * ADS書き込み失敗ログを物理削除するか否かを返す.
     * @return 物理削除の場合は trueを、論理削除の場合は falseを返す
     */
    public boolean isPhysicalDelete() {
        return isPhysicalDelete;
    }

    /**
     * ADS書き込み失敗ログを削除する.
     * @param deleteFile 削除対象のADS書き込み失敗ログ
     * @throws AdsWriteFailureLogException ログの削除に失敗した場合
     */
    public synchronized void deleteRotatedLog(File deleteFile) throws AdsWriteFailureLogException {
        if (null == deleteFile) {
            String message = String.format("adsWriteFailureLog configuration is wrong.");
            throw new AdsWriteFailureLogException(message);
        }
        if (null != deleteFile && !deleteFile.isFile()) {
            String message = String.format("adsWriteFailureLog is not found, or already deleted. [%s]",
                    deleteFile.getAbsolutePath());
            logger.info(message);
            return;
        }
        // ファイルを削除する場合、Java.ioパッケージのメソッドでは、削除対象ファイルにアクセスしているプロセスがあるとfalseが返却される。
        // JDK1.7からサポートされた Java.nioパッケージのメソッドでは、このような状態の場合に例外をスローする仕様となっており、
        // JDK1.7への対応が必要だと思われる。
        if (isPhysicalDelete()) {
            // 物理削除
            String message = String.format("adsWriteFailureLog is going to be deleted physically. [%s]",
                    deleteFile.getAbsolutePath());
            logger.info(message);
            if (!deleteFile.delete()) {
                message = String.format("Failed to delete adsWriteFailureLog. [%b,%s] ", isPhysicalDelete(),
                        deleteFile.getAbsolutePath());
                throw new AdsWriteFailureLogException(message);
            }
        } else {
            // 論理削除
            String message = String.format("adsWriteFailureLog is going to be deleted logically. [%s]",
                    deleteFile.getAbsolutePath());
            logger.info(message);
            String deletedFileName = deleteFile.getName() + LOGICAL_DELETED_LOGNAME_SUFFIX;
            File deletedFile = new File(deleteFile.getParent(), deletedFileName);
            if (!deleteFile.renameTo(deletedFile)) {
                // ここでエラーを検知してもフィールドは初期化しない。原因を究明してから原因別に対処を行う。
                message = String.format("Failed to delete adsWriteFailureLog. [%b,%s] ", isPhysicalDelete(),
                        deleteFile.getAbsolutePath());
                throw new AdsWriteFailureLogException(message);
            }
        }
    }
}
