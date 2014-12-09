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
package com.fujitsu.dc.core.model.file;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ファイルシステムに対してWebDAVのバイナリファイルの入出力を行うアクセサクラス.
 */
public class BinaryDataAccessor {

    private static Logger logger = LoggerFactory.getLogger(BinaryDataAccessor.class);

    /**
     * Davファイルの読み書き時、ハードリンク作成/ファイル名改変時の最大リトライ回数.
     * ※本クラスは、Dc-Coreに含まれないため、dc-config.propertiesを参照できないものと考え、システムプロパティで処理を行うものとする
     */
    private static int maxRetryCount = Integer.parseInt(System.getProperty(
            "com.fujitsu.dc.core.binaryData.dav.retry.count", "100"));

    /**
     * Davファイルの読み書き時、ハードリンク作成/ファイル名改変時のリトライ間隔(msec).
     * ※本クラスは、Dc-Coreに含まれないため、dc-config.propertiesを参照できないものと考え、システムプロパティで処理を行うものとする
     */
    private static long retryInterval = Long.parseLong(System.getProperty(
            "com.fujitsu.dc.core.binaryData.dav.retry.interval", "50"));

    private static final int FILE_BUFFER_SIZE = 1024;
    private String baseDir;
    private String unitUserName;
    private boolean isPhysicalDeleteMode = false;

    /**
     * コンストラクタ.
     * @param path 格納ディレクトリ
     */
    public BinaryDataAccessor(String path) {
        this(path, null);
    }

    /**
     * コンストラクタ.
     * @param path 格納ディレクトリ
     * @param unitUserName ユニットユーザ名
     */
    public BinaryDataAccessor(String path, String unitUserName) {
        this.baseDir = path;
        if (!this.baseDir.endsWith("/")) {
            this.baseDir += "/";
        }
        this.unitUserName = unitUserName;
    }

    /**
     * コンストラクタ.
     * @param path 格納ディレクトリ
     * @param unitUserName ユニットユーザ名
     * @param isPhysicalDeleteMode ファイル削除時に物理削除するか（true: 物理削除, false: 論理削除）
     */
    public BinaryDataAccessor(String path, String unitUserName, boolean isPhysicalDeleteMode) {
        this.baseDir = path;
        if (!this.baseDir.endsWith("/")) {
            this.baseDir += "/";
        }
        this.unitUserName = unitUserName;
        this.isPhysicalDeleteMode = isPhysicalDeleteMode;
    }

    /**
     * ファイル削除時に物理削除するかどうかの設定.
     * @return true: 物理削除, false: 論理削除
     */
    public boolean isPhysicalDeleteMode() {
        return isPhysicalDeleteMode;
    }

    /**
     * ストリームから読み込んだデータをファイルに書き込む.
     * @param inputStream 入力元のストリーム
     * @param filename ファイル名
     * @throws BinaryDataAccessException ファイル出力で異常が発生した場合にスローする
     * @return 書き込んだバイト数
     */
    public long create(InputStream inputStream, String filename) throws BinaryDataAccessException {
        String directory = getSubDirectoryName(filename);
        String fullPathName = this.baseDir + directory + filename;
        createSubDirectories(this.baseDir + directory);
        return writeToTmpFile(inputStream, fullPathName);
    }

    /**
     * ストリームから読み込んだデータをファイルに書き込む.
     * @param inputStream 入力元のストリーム
     * @param filename ファイル名
     * @throws BinaryDataAccessException ファイル入出力で異常が発生した場合にスローする
     * @return 書き込んだバイト数
     */
    public long update(InputStream inputStream, String filename) throws BinaryDataAccessException {
        String fullPathName = getFilePath(filename);
        if (!exists(fullPathName)) {
            throw new BinaryDataNotFoundException(fullPathName);
        }
        return writeToTmpFile(inputStream, fullPathName);
    }

    /**
     * ファイルをストリームにコピーする.
     * @param filename ファイル名
     * @param outputStream コピー先ストリーム
     * @throws BinaryDataAccessException ファイル入出力で異常が発生した場合にスローする
     * @return コピーしたバイト数
     */
    public long copy(String filename, OutputStream outputStream) throws BinaryDataAccessException {
        String fullPathName = getFilePath(filename);
        if (!exists(fullPathName)) {
            throw new BinaryDataNotFoundException(fullPathName);
        }
        return writeToStream(fullPathName, outputStream);
    }

    /**
     * ファイルをストリームで取得する.
     * @param filename ファイル名
     * @return ファイルのストリーム
     * @throws BinaryDataAccessException ファイル入出力で異常が発生した場合にスローする
     */
    public InputStream getFileStream(String filename) throws BinaryDataAccessException {
        String fullPathName = getFilePath(filename);
        if (!exists(fullPathName)) {
            throw new BinaryDataNotFoundException(fullPathName);
        }
        try {
            FileInputStream fis = new FileInputStream(fullPathName);
            return new BufferedInputStream(fis);
        } catch (FileNotFoundException e) {
            throw new BinaryDataNotFoundException(fullPathName);
        }
    }

    /**
     * ファイルを削除する. 設定に従い、論理削除(デフォルト)／物理削除を行う 対象ファイルが存在しない場合は何もしない
     * @param filename ファイル名
     * @throws BinaryDataAccessException ファイル入出力で異常が発生した場合にスローする
     */
    public void delete(String filename) throws BinaryDataAccessException {
        String fullPathName = getFilePath(filename);
        deleteWithFullPath(fullPathName);
    }

    /**
     * ファイルを削除する（フルパス指定）. 設定に従い、論理削除(デフォルト)／物理削除を行う 対象ファイルが存在しない場合は何もしない
     * @param filepath ファイルパス
     * @throws BinaryDataAccessException ファイル入出力で異常が発生した場合にスローする
     */
    public void deleteWithFullPath(String filepath) throws BinaryDataAccessException {
        if (exists(filepath)) {
            if (this.isPhysicalDeleteMode) {
                deletePhysicalFileWithFullPath(filepath);
            } else {
                deleteFile(filepath);
            }
        }
    }

    /**
     * ファイルサイズを返す.
     * @param filename ファイル名
     * @return ファイルサイズ(bytes)
     */
    public long getSize(String filename) {
        String fullPathName = getFilePath(filename);
        return getFileSize(fullPathName);
    }

    /**
     * ファイル存在有無チェック.
     * @param filename ファイル名
     * @return true：存在する、false：存在しない
     */
    public boolean existsForFilename(String filename) {
        String fullPathName = getFilePath(filename);
        return exists(fullPathName);
    }

    /**
     * 一時ファイルをリネームする.
     * @param filename ファイル名
     * @throws BinaryDataAccessException BinaryDataAccessException
     */
    public void copyFile(String filename) throws BinaryDataAccessException {
        String fullPathName = getFilePath(filename);
        String tmpName = fullPathName + ".tmp";
        File tmpFile = new File(tmpName);
        File dstFile = new File(fullPathName);

        if (!exists(tmpName)) {
            throw new BinaryDataNotFoundException(tmpName);
        }
        for (int i = 0; i < maxRetryCount; i++) {
            try {
                synchronized (fullPathName) {
                    Files.move(tmpFile.toPath(), dstFile.toPath(), StandardCopyOption.ATOMIC_MOVE);
                }
                // 処理成功すれば、その場で復帰する。
                return;
            } catch (IOException e) {
                logger.debug("Failed to copy file:" + tmpFile + " to " + dstFile + ". Will try again.");
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e2) {
                    logger.debug("Thread interrupted.");
                }
            }
        }
        throw new BinaryDataAccessException("Failed to copy file:" + tmpFile + " to " + dstFile);
    }

    /**
     * ファイルを物理削除する. 対象ファイルが存在しない場合は何もしない
     * @param filename ファイル名
     * @throws BinaryDataAccessException ファイル入出力で異常が発生した場合にスローする
     */
    public void deletePhysicalFile(String filename) throws BinaryDataAccessException {
        String fullPathName = getFilePath(filename);
        if (exists(fullPathName)) {
            deletePhysicalFileWithFullPath(fullPathName);
        }
    }

    /**
     * ファイルを物理削除する.
     * @param filepath ファイル名(フルパス)
     * @throws BinaryDataAccessException ファイル入出力で異常が発生した場合にスローする
     */
    private void deletePhysicalFileWithFullPath(String filepath) throws BinaryDataAccessException {
        Path file = new File(filepath).toPath();
        for (int i = 0; i < maxRetryCount; i++) {
            try {
                synchronized (filepath) {
                    Files.delete(file);
                }
                // 処理成功すれば、その場で復帰する。
                return;
            } catch (IOException e) {
                logger.debug("Failed to delete file: " + filepath + ".  Will retry again.");
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e2) {
                    logger.debug("Thread interrupted.");
                }
            }
        }
        throw new BinaryDataAccessException("Failed to delete file: " + filepath);
    }

    /**
     * ファイル名からファイルのフルパスを取得する.
     * @param filename ファイル名
     * @return ファイルフルパス
     */
    public String getFilePath(String filename) {
        String directory = getSubDirectoryName(filename);
        String fullPathName = this.baseDir + directory + filename;
        return fullPathName;
    }

    private static final int SUBDIR_NAME_LEN = 2;

    private boolean exists(String fullPathFilename) {
        File file = new File(fullPathFilename);
        return file.exists();
    }

    private String getSubDirectoryName(String filename) {
        StringBuilder sb = new StringBuilder("");
        if (this.unitUserName != null) {
            sb.append(this.unitUserName);
            sb.append("/");
        }
        sb.append(splitDirectoryName(filename, 0));
        sb.append("/");
        sb.append(splitDirectoryName(filename, SUBDIR_NAME_LEN));
        sb.append("/");
        return sb.toString();
    }

    private String splitDirectoryName(String filename, int index) {
        return filename.substring(index, index + SUBDIR_NAME_LEN);
    }

    private void createSubDirectories(String directory) throws BinaryDataAccessException {
        File newDir = new File(directory);
        // 既にディレクトリがあれば、何もしない
        if (!newDir.exists()) {
            try {
                Files.createDirectories(newDir.toPath());
            } catch (IOException e) {
                throw new BinaryDataAccessException("DirectoryCreateFailed:" + directory, e);
            }
        }
    }

    private long writeToTmpFile(InputStream inputStream, String fullPathName) throws BinaryDataAccessException {
        FileOutputStream outputStream = null;
        String tmpfileName = fullPathName + ".tmp";
        try {
            outputStream = new FileOutputStream(tmpfileName);
            return copyStream(inputStream, outputStream);
        } catch (IOException ex) {
            throw new BinaryDataAccessException("WriteToFileFailed:" + tmpfileName, ex);
        } finally {
            closeOutputStream(outputStream);
        }
    }

    private long writeToStream(String fullPathName, OutputStream outputStream) throws BinaryDataAccessException {
        FileInputStream inputStream = null;
        try {
            inputStream = new FileInputStream(fullPathName);
            return copyStream(inputStream, outputStream);
        } catch (FileNotFoundException e) {
            throw new BinaryDataNotFoundException(fullPathName);
        } catch (BinaryDataAccessException ex) {
            throw new BinaryDataAccessException("WriteToStreamFailed:" + fullPathName, ex);
        } finally {
            closeInputStream(inputStream);
        }
    }

    private long copyStream(InputStream inputStream, OutputStream outputStream) throws BinaryDataAccessException {
        BufferedInputStream bufferedInput = null;
        BufferedOutputStream bufferedOutput = null;
        try {
            bufferedInput = new BufferedInputStream(inputStream);
            bufferedOutput = new BufferedOutputStream(outputStream);
            byte[] buf = new byte[FILE_BUFFER_SIZE];
            long totalBytes = 0L;
            int len;
            while ((len = bufferedInput.read(buf)) != -1) {
                bufferedOutput.write(buf, 0, len);
                totalBytes += len;
            }
            return totalBytes;
        } catch (IOException ex) {
            throw new BinaryDataAccessException("CopyStreamFailed.", ex);
        } finally {
            closeOutputStream(bufferedOutput);
            closeInputStream(bufferedInput);
        }
    }

    private void closeInputStream(InputStream inputStream) {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException ex) {
            logger.debug("StreamCloseFailed:" + ex.getMessage());
        }
    }

    private void closeOutputStream(OutputStream outputStream) {
        try {
            if (outputStream != null) {
                outputStream.flush();
                outputStream.close();
            }
        } catch (IOException ex) {
            logger.debug("StreamCloseFailed:" + ex.getMessage());
        }
    }

    private void deleteFile(String srcFullPathName) throws BinaryDataAccessException {
        String dstFullPathName = srcFullPathName + ".deleted";
        File srcFile = new File(srcFullPathName);
        File dstFile = new File(dstFullPathName);

        for (int i = 0; i < maxRetryCount; i++) {
            try {
                synchronized (srcFullPathName) {
                    Files.move(srcFile.toPath(), dstFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                }
                // 処理成功すれば、その場で復帰する。
                return;
            } catch (IOException e) {
                logger.debug("Failed to delete file: " + srcFullPathName);
                try {
                    Thread.sleep(retryInterval);
                } catch (InterruptedException e2) {
                    logger.debug("Thread interrupted.");
                }
            }
        }
        throw new BinaryDataAccessException("Failed to delete file: " + srcFullPathName);
    }

    private long getFileSize(String fullPathName) {
        File file = new File(fullPathName);
        return file.length();
    }

}
