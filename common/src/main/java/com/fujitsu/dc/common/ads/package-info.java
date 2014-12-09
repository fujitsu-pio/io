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
/**
 * ADS書き込み失敗ログへの出力処理に関するパッケージ.
 * 本パッケージで扱うADS書き込み失敗ログの仕様は以下のとおり。
 * <ul>
 * <li>格納ディレクトリ名：/fjnfs/dc-core/writeFailureLog</li>
 * <li>出力中ログファイル名：adswriteFailureLog_【PCSバージョン】_【ファイル作成時のtimestamp】.log</li>
 * <li>ローテートされたログファイル名：adswriteFailure_【PCSバージョン】.log.【ファイル作成時のtimestamp】</li>
 * </ul>
 * ADSへのリペア処理では、上記「ローテートされたログファイル名」を処理対象としてADSへのリペアを実施する。
 * <p>
 * ログファイル出力中の排他制御は以下のとおり。
 * <ul>
 * <li>スレッド間：スレッドセーフ</li>
 * <li>プロセス間：考慮なし（別ファイルへの出力のため）</li>
 * <li>サーバ間：考慮なし（ローカルファイルシステムへの出力のため）</li>
 * </ul>
 * </p>
 * <p>
 * 本パッケージでは、以下のシステムプロパティを参照するため、予め同プロパティへ設定しておくこと。
 * <ul>
 * <li>PCSバージョン： com.fujitsu.dc.core.version</li>
 * <li>ログ格納ディレクトリ： com.fujitsu.dc.repair.ads.writeFailureLog.dir</li>
 * <li>物理削除フラグ： com.fujitsu.dc.repair.ads.writeFailureLog.delete.physical</li>
 * </ul>
 * </p>
 */
package com.fujitsu.dc.common.ads;

