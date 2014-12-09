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

import java.util.HashMap;

import org.json.simple.JSONObject;

///**
// * AccountのCRUDを行うためのクラス.
// */
/**
 * It creates a new object of AccountManager. This class is used for performing CRUD operations of Account.
 */
public class AccountManager extends ODataManager {

    // /** パスワード用ヘッダーキー. */
    /** Password for header key. */
    private static final String HEADER_KEY_CREDENTIAL = "X-Dc-Credential";

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor for the class.
     * @param as Accessor
     */
    public AccountManager(Accessor as) {
        super(as);
    }

    /**
     * This method creates and returns the URL for performing operations on Account.
     * @return URL In string form
     */
    @Override
    public String getUrl() {
        StringBuilder sb = new StringBuilder(accessor.getCurrentCell().getUrl());
        sb.append("__ctl/Account");
        return sb.toString();
    }

    // /**
    // * Accountを作成.
    // * @param obj Accountオブジェクト
    // * @return Accountオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to create an account when password is not specified.
     * @param obj Account object
     * @return Account object
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public Account create(Account obj) throws DaoException {
        JSONObject body = new JSONObject();
        body.put("Name", obj.getName());
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(HEADER_KEY_CREDENTIAL, obj.getPassword());
        JSONObject json = internalCreate(body, headers);
        obj.initialize(this.accessor, json);
        return obj;
    }

    // /**
    // * Accountを作成.
    // * @param body リクエストボディ
    // * @param password Accountパスワード
    // * @return 作成したAccountオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to create an account when password is also specified.
     * @param body Request Body
     * @param password Account Password
     * @return Account object
     * @throws DaoException Exception thrown
     */
    public Account create(HashMap<String, Object> body, String password) throws DaoException {
        HashMap<String, String> headers = new HashMap<String, String>();
        if (password != null) {
            headers.put(HEADER_KEY_CREDENTIAL, password);
        }
        JSONObject json = internalCreate(body, headers);
        return new Account(accessor, json);
    }

    // /**
    // * Accountを取得.
    // * @param name 取得対象のAccount名
    // * @return 取得したしたAccountオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to retrieve the specified account.
     * @param name Account name
     * @return Account object
     * @throws DaoException Exception thrown
     */
    public Account retrieve(String name) throws DaoException {
        JSONObject json = internalRetrieve(name);
        return new Account(accessor, json);
    }

    // /**
    // * Passwordを変更.
    // * @param name Accountの名前
    // * @param password Accountパスワード
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to change the password for account.
     * @param name Account Name
     * @param password Account Password
     * @throws DaoException Exception thrown
     */
    @SuppressWarnings("unchecked")
    public void changePassword(String name, String password) throws DaoException {
        HashMap<String, String> headers = new HashMap<String, String>();
        headers.put(HEADER_KEY_CREDENTIAL, password);

        JSONObject body = new JSONObject();
        body.put("Name", name);
        internalUpdate(name, body, "*", headers);
    }

}
