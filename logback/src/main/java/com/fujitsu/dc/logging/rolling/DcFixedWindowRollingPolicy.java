/**
 * Logback: the reliable, generic, fast and flexible logging framework.
 * Copyright (C) 1999-2011, QOS.ch. All rights reserved.
 *
 * This program and the accompanying materials are dual-licensed under
 * either the terms of the Eclipse Public License v1.0 as published by
 * the Eclipse Foundation
 *
 *   or (per the licensee's choosing)
 *
 * under the terms of the GNU Lesser General Public License version 2.1
 * as published by the Free Software Foundation.
 * --------------------------------------------------
 * This code partially contains our modifications for personium.io.
 * --------------------------------------------------
 */
package com.fujitsu.dc.logging.rolling;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.FileTime;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.CoreConstants;
import ch.qos.logback.core.rolling.RollingPolicyBase;
import ch.qos.logback.core.rolling.RolloverFailure;
import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.Compressor;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import ch.qos.logback.core.rolling.helper.RenameUtil;

/**
 * EventBus用のRollingPolicyクラス.
 * ローテート時に "default.log.{timestamp}.zip" の形式でローテートファイルを作成し、
 * 最大保持世代数を超える場合は、格納ディレクトリに存在する最古の "default.log.{timestamp}.zip" を削除する。 <br />
 * そのため、上記フォーマットに則さないファイルは削除対象とはならない。
 */
public class DcFixedWindowRollingPolicy extends RollingPolicyBase {

    Logger logger = LoggerFactory.getLogger(DcFixedWindowRollingPolicy.class);

    /**
     * Zipファイルのエントリの日付パターン.
     */
    private static final String ZIP_ENTRY_DATE_PATTERN = "yyyy-MM-dd_HHmm";

    /**
     * It's almost always a bad idea to have a large window size, say over 12.
     */
    private static final int MAX_WINDOW_SIZE = 12;

    // エラーメッセージ
    static final String FNP_NOT_SET = "The \"FileNamePattern\" property"
            + " must be set before using FixedWindowRollingPolicy. ";
    static final String PRUDENT_MODE_UNSUPPORTED = "See also"
            + " http://logback.qos.ch/codes.html#tbr_fnp_prudent_unsupported";
    static final String SEE_PARENT_FN_NOT_SET = "Please refer to "
            + "http://logback.qos.ch/codes.html#fwrp_parentFileName_not_set";

    // logback.xmlに定義されたインデックス番号（実際には有効なインデックス数として使用する）
    int maxIndex;
    int minIndex;

    FileNamePattern fileNamePattern;

    // logbackの内部APIを使用するためのインスタンス
    RenameUtil util = new RenameUtil();
    Compressor compressor;
    // use to name files within zip file, i.e. the zipEntry
    FileNamePattern zipEntryFileNamePattern;

    /**
     * コンストラクタ.
     * logback.xmlの定義でminIndex/maxIndexの不備があった場合、ここで設定した値が有効になる。
     * 定義不備に関するログが出力されないようなので、startメソッドにて値の不備をログに出力する。
     */
    public DcFixedWindowRollingPolicy() {
        minIndex = -1;
        maxIndex = -1;
    }

    /**
     * DcFixedWindowRollingPolicyの開始.
     */
    public void start() {
        try {
            util.setContext(this.context);

            // logbackのFixedWindowRollingPolicyの書式に則って値をチェックする。
            // ただし、minIndex/maxIndexに限っては、PCS独自で値チェックを実装する。

            // <fileNamePattern>の値チェック。
            if (fileNamePatternStr != null) {
                fileNamePattern = new FileNamePattern(fileNamePatternStr, this.context);
                determineCompressionMode();
            } else {
                addError(FNP_NOT_SET);
                addError(CoreConstants.SEE_FNP_NOT_SET);
                throw new IllegalStateException(FNP_NOT_SET + CoreConstants.SEE_FNP_NOT_SET);
            }

            // FileAppenderでは<prudent>（ファイル出力時の排他制御)は未サポート。
            if (isParentPrudent()) {
                addError("Prudent mode is not supported with FixedWindowRollingPolicy.");
                addError(PRUDENT_MODE_UNSUPPORTED);
                throw new IllegalStateException("Prudent mode is not supported.");
            }

            // FileAppederの<file>定義は必須。
            if (getParentsRawFileProperty() == null) {
                addError("The File name property must be set before using this rolling policy.");
                addError(SEE_PARENT_FN_NOT_SET);
                throw new IllegalStateException("The \"File\" option must be set.");
            }

            // minIndex/maxIndexの値チェック。
            // logback.xmlの定義に不備があった場合はエラーレベルとして、不正な状態でローテートされないようにする。
            if (0 > maxIndex || 0 > minIndex) {
                String message = "maxIndex (" + maxIndex + ") or minIndex (" + minIndex + ") must be positive integer.";
                addError(message);
                throw new IllegalStateException(message);
            }

            if (maxIndex < minIndex) {
                String message = "maxIndex (" + maxIndex + ") cannot be smaller than minIndex ("
                        + minIndex + ").";
                addError(message);
                throw new IllegalStateException(message);
            }

            if ((maxIndex - minIndex) > MAX_WINDOW_SIZE) {
                String message = "Large window sizes are not allowed. (maxIndex: " + maxIndex + " minIndex:" + minIndex
                        + ").";
                addError(message);
                throw new IllegalStateException(message);
            }

            IntegerTokenConverter itc = fileNamePattern.getIntegerTokenConverter();

            if (itc == null) {
                throw new IllegalStateException("FileNamePattern ["
                        + fileNamePattern.getPattern()
                        + "] does not contain a valid IntegerToken");
            }

            if (compressionMode == CompressionMode.ZIP) {
                String zipEntryFileNamePatternStr = transformFileNamePatternFromInt2Date(fileNamePatternStr);
                zipEntryFileNamePattern = new FileNamePattern(zipEntryFileNamePatternStr, context);
            }
            compressor = new Compressor(compressionMode);
            compressor.setContext(this.context);
            super.start();
        } catch (RuntimeException e) {
            logger.error("Logback Configration Error: " + e.getMessage(), e);
        }
    }

    /**
     * ローテート時にアーカイブするときのエントリ名を"yyyy-MM-dd_HHmm"に変更する.
     * @param fileNamePatternStr
     * @return 生成したエントリ名
     */
    private String transformFileNamePatternFromInt2Date(String fileNamePatternStr) {
        String slashified = FileFilterUtil.slashify(fileNamePatternStr);
        String stemOfFileNamePattern = FileFilterUtil.afterLastSlash(slashified);
        return stemOfFileNamePattern.replace("%i", "%d{" + ZIP_ENTRY_DATE_PATTERN + "}");
    }

    /**
     * ファイルをアーカイブする.
     * @throws RolloverFailure アーカイブに失敗.
     */
    public void rollover() throws RolloverFailure {
        try {
            long timestamp = System.currentTimeMillis(); // アーカイブファイル名に付加する '%i' の値

            // Inside this method it is guaranteed that the hereto active log file is
            // closed.
            // If maxIndex <= 0, then there is no file renaming to be done.
            if (maxIndex >= 0) {
                // Delete the oldest file, to keep Windows happy.
                File file = new File(fileNamePattern.convertInt(maxIndex));
                File parent = file.getParentFile();
                if (parent.isDirectory()) {
                    // logback.xmlのfileNamePatternに"%i"が含まれている場合は、ファイルの世代数管理を行う
                    int fileNameZipPosition = fileNamePattern.toString().lastIndexOf("%i");
                    if (fileNameZipPosition >= 0) {
                        removeOldestArchiveLog(parent);
                    }
                } else {
                    logger.info("parent is not exits.");
                }

                // ローテートファイル名の生成
                // LogbackのfileNamePatternは、%d(date) と %i(index) のみしか指定できない。
                // また、%i の範囲は、minIndex - maxIndex に閉じられている
                // PCSでは、%i の解釈を timestamp とし、ローテートした時刻で timpstamp を生成する。
                // この際、fileNamePatternは使用せずにファイル名を生成する。
                // このため、最大保持世代数を超えた場合、ファイルの modified が最古のファイルを削除する。
                String nativeNewPath = FileFilterUtil.slashify(fileNamePatternStr);
                String newRotateFileName = nativeNewPath.replaceAll("%i", String.valueOf(timestamp));

                switch (compressionMode) {
                case NONE:
                    util.rename(getActiveFileName(), newRotateFileName);
                    break;
                case GZ:
                    compressor.compress(getActiveFileName(), newRotateFileName, null);
                    break;
                case ZIP:
                default:
                    compressor.compress(getActiveFileName(), newRotateFileName,
                            zipEntryFileNamePattern.convert(new Date()));
                    break;
                }
            }
        } catch (RuntimeException e) {
            logger.error("Logback Configration Error: " + e.getMessage(), e);
        }
    }

    /**
     * Return the value of the parent's RawFile property.
     * @return アーカイブファイル名.
     */
    public String getActiveFileName() {
        return getParentsRawFileProperty();
    }

    /**
     * maxIndexを取得する.
     * @return maxIndex.
     */
    public int getMaxIndex() {
        return maxIndex;
    }

    /**
     * minIndexを取得する.
     * @return minIndex.
     */
    public int getMinIndex() {
        return minIndex;
    }

    /**
     * maxIndexを設定する.
     * @param maxIndex maxIndexに設定する値.
     */
    public void setMaxIndex(int maxIndex) {
        this.maxIndex = maxIndex;
    }

    /**
     * minIndexを設定する.
     * @param minIndex minIndexに設定する値.
     */
    public void setMinIndex(int minIndex) {
        this.minIndex = minIndex;
    }

    /**
     * アーカイブされているログが最大保持世代数のときに最古の アーカイブログを削除する.
     * @param parent アーカイブログが格納されているディレクトリの親ディレクトリを示すオブジェクト
     */
    private void removeOldestArchiveLog(File parent) {
        // アーカイブファイル数が保持世代(12世代)を超える場合は、最古のファイルを削除する
        File[] files = parent.listFiles();
        File oldest = null;
        FileTime oldestTime = null;
        if (files.length >= maxIndex - minIndex + 1) {
            for (File archive : files) {
                if (null == oldest) {
                    oldest = archive;
                } else {
                    try {
                        FileTime archiveTime = Files.getLastModifiedTime(archive.toPath());
                        oldestTime = Files.getLastModifiedTime(oldest.toPath());
                        if (archiveTime.toMillis() < oldestTime.toMillis()) {
                            oldest = archive;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (null != oldest && oldest.exists()) {
                logger.info("olddest archive file will be deleted.");
                logger.info("oldestFile Name: " + oldest.getPath() + ", timestamp: " + oldestTime.toMillis());
                logger.info("number of existing files:" + files.length + ", max number of files: "
                        + (maxIndex - minIndex + 1));
                if (oldest.delete()) {
                    logger.warn("Failed to oldest archive file: " + oldest.getPath());
                }
            }
        }
    }
}
