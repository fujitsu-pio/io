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
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

    /**
     * ADS書き込み失敗ログのファイル名フォーマットに合致するかどうかのフィルター用クラス.
     *
     */
    public class AdsWriteFailureLogFilter implements FilenameFilter {
        private String version = null;
        private String filePattern = null;

        private static final String TIMESTAMP_FORMAT = "\\.\\d{13}$";
        private Pattern timestampPattern = null;

        /**
         * コンストラクタ.
         * @param version 稼働中のPCSバージョン
         */
        public AdsWriteFailureLogFilter(String version) {
            this.version = version;
            String fileFormat = String.format(AbstractAdsWriteFailureLog.LOGNAME_FORMAT_ROTATE, this.version, 0);
            filePattern = fileFormat.substring(0, fileFormat.length() - 1);
            timestampPattern = Pattern.compile(TIMESTAMP_FORMAT);
        }

        @Override
        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            if (!file.isFile()) {
                return false;
            }

            // ファイル名がADS書き込み失敗ログのファイルフォーマットに合致しているか
            // 末尾が13桁の数字である、または、末尾が"retry"となっている場合、合致しているとみなす。
            // タイムスタンプを文字列表現にすると13桁の数字となるため、このチェックを行う。
            if (!name.startsWith(filePattern)) {
                return false;
            }
            if (name.endsWith(AbstractAdsWriteFailureLog.RETRY_LOGNAME_SUFFIX)) {
                name = name.substring(0, name.length() - AbstractAdsWriteFailureLog.RETRY_LOGNAME_SUFFIX.length());
            }
            Matcher m = timestampPattern.matcher(name);
            return m.find();
        }
    }
