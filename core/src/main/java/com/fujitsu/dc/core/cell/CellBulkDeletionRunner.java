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
package com.fujitsu.dc.core.cell;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fujitsu.dc.core.DcCoreConfig;
import com.fujitsu.dc.core.event.EventUtils;
import com.fujitsu.dc.core.model.Cell;
import com.fujitsu.dc.core.model.file.BinaryDataAccessException;
import com.fujitsu.dc.core.model.file.BinaryDataAccessor;
import com.fujitsu.dc.core.model.impl.es.EsModel;
import com.fujitsu.dc.core.model.impl.es.accessor.CellAccessor;

/**
 * Cell配下のリソースを削除するスレッドクラス.
 */
public class CellBulkDeletionRunner implements Runnable {

    private static final int DAVFILE_DEFAULT_FETCH_COUNT = 1000;

    /**
     * ログ用オブジェクト.
     */
    static Logger log = LoggerFactory.getLogger(CellBulkDeletionRunner.class);

    Cell cell;

    /**
     * コンストラクタ.
     * @param cell 削除対象のセル
     */
    public CellBulkDeletionRunner(Cell cell) {
        this.cell = cell;
    }

    /**
     * Cell配下のリソースを削除する処理.
     */
    public void run() {
        CellAccessor cellAccessor = (CellAccessor) EsModel.cell();

        String cellId = this.cell.getId();
        String cellName = this.cell.getName();
        String cellOwner = this.cell.getOwner();
        String unitUserName = this.cell.getUnitUserName();
        String unitUserNameWithOutPrefix = this.cell.getUnitUserNameWithOutPrefix();
        String cellInfoLog = String.format(" CellId:[%s], CellName:[%s], CellUnitUserName:[%s]", cellId, cellName,
                unitUserName);

        // セルIDとタイプ情報をクエリに使用してWebDavファイルの管理情報一覧の件数を取得する
        long davfileCount = cellAccessor.getDavFileTotalCount(cellId, unitUserNameWithOutPrefix);

        // 1000件ずつ、WebDavファイルの管理情報件数まで以下を実施する
        int fetchCount = DAVFILE_DEFAULT_FETCH_COUNT;
        BinaryDataAccessor accessor = new BinaryDataAccessor(
                DcCoreConfig.getBlobStoreRoot(), unitUserNameWithOutPrefix, DcCoreConfig.getPhysicalDeleteMode());
        for (int i = 0; i <= davfileCount; i += fetchCount) {
            // WebDavファイルのID一覧を取得する
            List<String> davFileIdList = cellAccessor.getDavFileIdList(
                                                         cellId, unitUserNameWithOutPrefix, fetchCount, i);
            // BinaryDataAccessorのdeleteメソッドにて「.deleted」にリネームする
            for (String davFileId : davFileIdList) {
                try {
                    accessor.delete(davFileId);
                } catch (BinaryDataAccessException e) {
                    // 削除に失敗した場合はログを出力して処理を続行する
                    log.warn(String.format("Delete DavFile Failed DavFileId:[%s].", davFileId) + cellInfoLog, e);
                }
            }
        }
        log.info("DavFile Deletion End.");

        // EventLogのバイナリファイルを削除する
        try {
            EventUtils.deleteEventLog(cellId, cellOwner);
            log.info("EventLog Deletion End.");
        } catch (BinaryDataAccessException e) {
            // 削除に失敗した場合はログを出力して処理を続行する
            log.warn("Delete EventLog Failed." + cellInfoLog, e);
        }

        // Cell配下のエンティティを削除する
        cellAccessor.cellBulkDeletion(cellId, unitUserNameWithOutPrefix);
        log.info("Cell Entity Resource Deletion End.");
    }
}
