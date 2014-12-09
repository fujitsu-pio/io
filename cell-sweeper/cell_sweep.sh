#!/bin/sh
#
# personium.io
# Copyright 2014 FUJITSU LIMITED
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

ROOT_DIR=
DATE=`/bin/date +%Y%m%d`
SWEEP_LOG=${ROOT_DIR}/fj/dc1-cell-sweeper/log/dc1-cell-sweeper.log
SWEEP_LOG_OLD=${SWEEP_LOG}.`/bin/date +%Y%m%d -d '1 days ago'`
LOCK_FILE=${ROOT_DIR}/fj/dc1-cell-sweeper/dc1-cell-sweeper.lock

# Cellデータ削除用管理DB及びテーブル
MANAGE_DB=pcs_management;
MANAGE_TABLE=CELL_DELETE;

# 一回の処理で削除するレコード数
DELETE_RECORD_PER_CALL=1000

# 処理休止時間と単位
SLEEP_PERIOD=1
SLEEP_TIMEUNIT=s


# 処理中止（ロックを開放して終了）
function abort() {
  if [ -f ${LOCK_FILE} ]; then
    releaseLock
  fi
  exit $1
}

# 二重起動抑止のためにロックを解放
## ロックの解放に失敗した場合は、ログを出力して終了する
function releaseLock() {
  /bin/rm -f ${LOCK_FILE} >> ${SWEEP_LOG} 2>&1
  if [ $? -ne 0 ]; then
    outputErrorLog "Failed to release lock for the double start control."
    /bin/rm -f ${LOCK_FILE} >> ${SWEEP_LOG} 2>&1
    exit 1
  fi
}

# INFOログ出力
function outputInfoLog() {
  echo "`/bin/date +'%Y/%m/%d %H:%M:%S'` [INFO ] $1" >> ${SWEEP_LOG}
}

# WARNログ出力
function outputWarnLog() {
  echo "`/bin/date +'%Y/%m/%d %H:%M:%S'` [WARN ] $1" >> ${SWEEP_LOG}
}

# ERRORログ出力
function outputErrorLog() {
  echo "`/bin/date +'%Y/%m/%d %H:%M:%S'` [ERROR] $1" >> ${SWEEP_LOG}
}

# スクリプト終了ログ
function outPutScriptEndlog() {
  outputInfoLog "Cell data sweep finished."
  outputInfoLog "------------------------------------------"
}

# プロパティファイルを読む込む
function read_properties() {
  if [ ! -f $1 ]; then
    outputWarnLog "Invalid argument[-p]."
    abort 1
  fi 

  MYSQL_USER=`/bin/grep 'com.fujitsu.dc.core.mysql.master.user.name' $1 | grep -v '^[:blank:]*#'|  /bin/sed -e 's/^.*=//g' | /bin/sed 's/\r//'`
  if [ $? -ne 0 ]; then
    outputWarnLog "property 'com.fujitsu.dc.core.mysql.master.user.name' is not defined."
    abort 1
  fi 
  if [ -z ${MYSQL_USER} ]; then
    outputWarnLog "property 'com.fujitsu.dc.core.mysql.master.user.name' is not defined."
    abort 1
  fi 
  MYSQL_PASS=`/bin/grep 'com.fujitsu.dc.core.mysql.master.user.password' $1 | grep -v '^[:blank:]*#' | /bin/sed -e 's/^.*=//g' | /bin/sed 's/\r//'`
  if [ $? -ne 0 ]; then
    outputWarnLog "property 'com.fujitsu.dc.core.mysql.master.user.password' is not defined."
    abort 1
  fi 
  if [ -z ${MYSQL_PASS} ]; then
    outputWarnLog "property 'com.fujitsu.dc.core.mysql.master.user.password' is not defined."
    abort 1
  fi 
  MYSQL_HOST=`/bin/grep 'com.fujitsu.dc.core.mysql.master.host' $1 | grep -v '^[:blank:]*#' | /bin/sed -e 's/^.*=//g' | /bin/sed 's/\r//'`
  if [ $? -ne 0 ]; then
    outputWarnLog "property 'com.fujitsu.dc.core.mysql.master.host' is not defined."
    abort 1
  fi 
  if [ -z ${MYSQL_HOST} ]; then
    outputWarnLog "property 'com.fujitsu.dc.core.mysql.master.host' is not defined."
    abort 1
  fi 

  # 読み込んだプロパティファイルからmysqlコマンドを生成
  MYSQL_CMD="/usr/bin/mysql -u ${MYSQL_USER} --password=${MYSQL_PASS} -h ${MYSQL_HOST}"
}


# Cellデータの削除管理レコードを消す。
function deleteManagementRecord() {
    DELETE_COUNT_SQL_RESULT=`${MYSQL_CMD} -vv -e "DELETE FROM \\\`${MANAGE_DB}\\\`.${MANAGE_TABLE} WHERE db_name='${CANDIDATE_DB}' and table_name='${CANDIDATE_TBL}' and cell_id='${CANDIDATE_CELL}';" 2>> ${SWEEP_LOG}`
    if [ $? -ne 0 ]; then
         outputErrorLog "Failed to connect to MySQL master server."
         abort 1
    fi

    DELETE_COUNT=`echo "${DELETE_COUNT_SQL_RESULT}" | /bin/grep "Query OK." | /bin/cut -d ' ' -f 3`

    if [ "${DELETE_COUNT}" != "1" ]; then
        outputErrorLog "Failed to delete management record for cell sweep:  Cell: ${CANDIDATE_CELL}   Database: ${CANDIDATE_DB}.${CANDIDATE_TBL}"
        abort 1
    fi

    outputInfoLog "Management record for cell sweep is deleted:  Cell: ${CANDIDATE_CELL}   Database: ${CANDIDATE_DB}.${CANDIDATE_TBL}"
    break;
}


# Cellデータ用 MySQLレコード削除
##  Returns 0 when succeeded, otherwise returns 1.
function sweep_cells() {
    outputInfoLog "Sweeping cell data started."

    while :
    do
        # 削除管理DBから、削除対象の DB及び Cell IDを取得する。
        ## 取得に失敗した場合は処理を終了する
        CANDIDATE_COUNT_SQL_RESULT=`${MYSQL_CMD} -e "SELECT COUNT(*) FROM \\\`${MANAGE_DB}\\\`.${MANAGE_TABLE}\G" 2>> ${SWEEP_LOG}`
        if [ $? -ne 0 ]; then
          outputErrorLog "Failed to connect to MySQL master server."
          abort 1
        fi

        CANDIDATE_COUNT=`echo "${CANDIDATE_COUNT_SQL_RESULT}" | /bin/grep '^COUNT(\*)' | /bin/cut -d' ' -f 2`
        outputInfoLog "Number of management records for cell sweep: ${CANDIDATE_COUNT}"

        if [ "${CANDIDATE_COUNT}" = "0" ]; then
        # 処理対象がないため復帰
          outputInfoLog "No management record for cell sweep is found. Quitting."
          return 0;
        fi


        # 処理対象となる Cellが含まれるDB, Table を１件取得
        CANDIDATE_DATA=`${MYSQL_CMD} -e "SELECT * FROM \\\`${MANAGE_DB}\\\`.${MANAGE_TABLE} ORDER BY create_date LIMIT 1\G" 2>> ${SWEEP_LOG}`
        if [ $? -ne 0 ]; then
          outputErrorLog "Failed to connect to MySQL master server."
          abort 1
        fi

        CANDIDATE_DB=`echo "${CANDIDATE_DATA}" | /bin/grep 'db_name:' | /bin/sed -e "s/.*: //"`
        CANDIDATE_TBL=`echo "${CANDIDATE_DATA}" | /bin/grep 'table_name:' | /bin/sed -e "s/.*: //"`
        CANDIDATE_CELL=`echo "${CANDIDATE_DATA}" | /bin/grep 'cell_id:' | /bin/sed -e "s/.*: //"`

        outputInfoLog "##### Target cell data to delete:  Cell: ${CANDIDATE_CELL}   Database: ${CANDIDATE_DB}.${CANDIDATE_TBL} #####" 

        if [ -n "${CANDIDATE_DB}" -a -n "${CANDIDATE_TBL}" -a -n "${CANDIDATE_CELL}" ]; then
            while :
            do
                # Cellデータを削除する。一度の SQL呼び出しで全部削除するのではなく、規定数のレコード削除を繰り返す（Sleepを挟みながら)
                #   これは大量データを削除するための MySQL呼び出しが、ディスク I/O 負荷を非常に高めることを回避するため。
                AFFECTED_ROWS_SQL_RESULT=`${MYSQL_CMD} -vv -e "DELETE FROM \\\`${CANDIDATE_DB}\\\`.${CANDIDATE_TBL} WHERE cell_id='${CANDIDATE_CELL}' LIMIT ${DELETE_RECORD_PER_CALL};" 2>> ${SWEEP_LOG}`
                if [ $? -ne 0 ]; then
                    outputErrorLog "Failed to connect to MySQL master server."
                    abort 1
                fi
                
                AFFECTED_ROWS=`echo "${AFFECTED_ROWS_SQL_RESULT}" | /bin/grep "Query OK." | /bin/cut -d ' ' -f 3`

                if [ "${AFFECTED_ROWS}" = "0" ]; then
                    # 処理対象がないため復帰. この際管理テーブルから対象レコードを消す。
                    deleteManagementRecord
                    break;
                fi
                outputInfoLog "Part of cell data [${CANDIDATE_CELL}] is deleted from  ${CANDIDATE_DB}.${CANDIDATE_TBL}"
                /bin/sleep ${SLEEP_PERIOD}${SLEEP_TIMEUNIT}
            done
            outputInfoLog "Cell data is deleted: Cell: ${CANDIDATE_CELL}   Database: ${CANDIDATE_DB}.${CANDIDATE_TBL}"
        fi
    done

    outputInfoLog "Sweeping cell data [${CANDIDATE_CELL}] finished.  ${CANDIDATE_DB}.${CANDIDATE_TBL}"

    return 0
}


# 既存のログファイルを前日の日付を付加して退避する
## 前日の日付を付加したファイルが既に存在する場合は退避しない
## 退避に失敗した場合は、ログを出力し、処理を続行する
if [ ! -e ${SWEEP_LOG_OLD} ]; then
  if [ -e ${SWEEP_LOG} ]; then
    mv -f ${SWEEP_LOG} ${SWEEP_LOG_OLD} 2>&1
    if [ $? -ne 0 ]; then
      if [ ! -e ${SWEEP_LOG_OLD} ]; then
        outputWarnLog "Cell sweep log file rename failed."
      fi
    fi
  fi
fi
outputInfoLog "Cell sweep process started."

# パラメータのパースを実施する
while getopts ":p:" ARGS
do
  case $ARGS in
  p )
    # プロパティファイルのパス
    PROPERTIES_FILE_PATH=$OPTARG
    ;;
  :)
    outputWarnLog "[-$OPTARG] requires an argument."
    outPutScriptEndlog
    exit 1
    ;;
  esac
done

# -p パラメタは必須
outputInfoLog "Arguments list. -p [${PROPERTIES_FILE_PATH}]"
if [ -z "${PROPERTIES_FILE_PATH}" ]; then
  outputWarnLog "[-p] arguments is necessary."
  outPutScriptEndlog
  exit 1
fi

# ロックファイルが存在するか確認し、存在しない場合は作成する
## ロックファイルが存在する場合、ログを出力し終了する
if [ -f ${LOCK_FILE} ]; then
  outputInfoLog "Cell sweep process has already been started."
  outPutScriptEndlog
  exit 0
fi
echo $$ > ${LOCK_FILE}

# -pオプションで渡されたdc-config.propertiesファイルから必要なプロパティを読み込む
read_properties ${PROPERTIES_FILE_PATH}

# Cellデータ(MySQL)処理のメイン処理を呼び出す
sweep_cells


# 二重起動抑止のためにロックを解放
## ロックの解放に失敗した場合は、ログを出力して終了する
releaseLock

outPutScriptEndlog
