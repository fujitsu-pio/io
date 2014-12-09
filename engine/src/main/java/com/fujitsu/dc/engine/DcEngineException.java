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
package com.fujitsu.dc.engine;

/**
 * DcEngine内での例外クラス.
  */
@SuppressWarnings("serial")
public class DcEngineException extends Exception {
    /** 500. */
    public static final int STATUSCODE_SERVER_ERROR = 500;
    /** 404. */
    public static final int STATUSCODE_NOTFOUND = 404;
    /** 200. */
    public static final int STATUSCODE_SUCCESS = 200;

    /** オリジナル例外オブジェクト. */
    private Exception originalException = null;
    /** ステータスコード. */
    private int statusCode = DcEngineException.STATUSCODE_SUCCESS;

    /**
     * コンストラクタ.
     * @param msg メッセージ
     * @param code ステータスコード
     * @param e オリジナルException
     */
    public DcEngineException(final String msg, final int code, final Exception e) {
        super(msg);
        this.statusCode = code;
        this.originalException = e;
    }

    /**
     * コンストラクタ.
     * @param msg メッセージ
     * @param code ステータスコード
     */
    public DcEngineException(final String msg, final int code) {
        super(msg);
        this.statusCode = code;
        this.originalException = null;
    }

    /**
     * ステータスコードの取得.
     * @return ステータスコード
     */
    public final int getStatusCode() {
        return this.statusCode;
    }

    /**
     * オリジナル例外オブジェクトの取得.
     * @return オリジナル例外オブジェクト
     */
    public final Exception getOriginalException() {
        return this.originalException;
    }
}
