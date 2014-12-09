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
package com.fujitsu.dc.client;

///**
// * DAOで発生するException.
// */
/**
 * It creates a new object of DaoException. This class represents the exceptions that occur in DAO.
 */
public class DaoException extends Exception {
    /** Final and unique serial version random ID. */
    private static final long serialVersionUID = 1L;
    /** Status Code. */
    private int code = 0;

    // /**
    // * コンストラクタ.
    // * @param msg メッセージ
    // * @param t thorowable
    // */
    /**
     * This is the parameterized constructor with two arguments and calls its parent constructor internally.
     * @param msg Error Message
     * @param t thorowable
     */
    public DaoException(final String msg, final Throwable t) {
        super(msg, t);
    }

    // /**
    // * コンストラクタ.
    // * @param msg メッセージ
    // */
    /**
     * This is the parameterized constructor with one argument and calls its parent constructor internally.
     * @param msg Error Message
     */
    public DaoException(final String msg) {
        super(msg);
    }

    // /**
    // * コンストラクタ.
    // * @param msg メッセージ
    // * @param c statusCode
    // */
    /**
     * This is the parameterized constructor with two arguments and calls its parent constructor internally.
     * @param msg Error Message
     * @param c statusCode
     */
    public DaoException(final String msg, final int c) {
        super(msg);
        this.code = c;
    }

    // /**
    // * DaoExceptionの生成.
    // * @param msg メッセージ
    // * @param c ステータスコード
    // * @return DaoExceptionオブジェクト
    // */
    /**
     * This method creates and returns a new instance of DaoException.
     * @param msg Error Message
     * @param c Status Code
     * @return DaoException object
     */
    public static DaoException create(final String msg, final int c) {
        return new DaoException(String.format("%s,%s", Integer.toString(c), msg), c);
    }

    // /**
    // * 例外発生時のステータスコードを取得.
    // * @return ステータスコード
    // */
    /**
     * This method gets the status code at the time of the exception.
     * @return Statuc code
     */
    public final String getCode() {
        return Integer.toString(code);
    }
}
