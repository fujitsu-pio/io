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
package com.fujitsu.dc.client.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fujitsu.dc.client.Accessor;
import com.fujitsu.dc.client.DaoException;

///**
// * ＄Batchアクセスのためのリクエストを作成するクラス.
// */
/**
 * It creates a new object of BatchAdapter. This class is used to create a request for $Batch access .
 */
public class BatchAdapter implements IRestAdapter {
    // /** アクセス主体. */
    /** Reference to Accessor. */
    private Accessor accessor;
    /** Variable for BatchBoundary. */
    private String batchBoundary;

    // /** $Batchリクエスト. */
    // private StringBuilder sbRequest = new StringBuilder();
    /** Variable for Batch. */
    private Batch batch;
    /** Variable for ChangeSet. */
    private ChangeSet changeSet;
    // /** 改行コード. */
    /** Line feed code. */
    private static final String CRLF = "\r\n";

    // /**
    // * コマンドを$Batchフォーマットに生成する.
    // */
    /**
     * This is the inner class used to generate the $Batch format command.
     */
    private class Command {
        /** Variable for URL. */
        private String url = null;
        /** Variable for Method. */
        private String method = null;
        /** Variable for Etag. */
        private String etag = null;
        /** Variable for Body. */
        private StringBuilder body = null;
        /** Variable for ContentLength. */
        private int contentLength = 0;
        /** Variable for Headers. */
        private ArrayList<String> headers = new ArrayList<String>();

        /**
         * This method is used to set Content Length.
         * @param value the body to set.
         */
        void setBody(String value) {
            body = new StringBuilder(value);
            try {
                this.contentLength = value.getBytes("UTF-8").length;
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * This method adds key value pair in header array.
         * @param key Value
         * @param value Value
         */
        void addHeader(final String key, final String value) {
            this.headers.add(key + ": " + value);
        }

        /**
         * This method is used to get the $batch format command.
         * @return $batch format command
         */
        String get() {
            StringBuilder sb = new StringBuilder();
            /** GET. */
            if (this.method.equals(HttpMethods.GET)) {
                sb.append("--" + batchBoundary).append(CRLF);
                sb.append("Content-Type: application/http").append(CRLF);
                sb.append("Content-Transfer-Encoding:binary").append(CRLF);
                sb.append(CRLF);
            }

            /** method url http-ver. */
            sb.append(this.method).append(" ").append(this.url).append(" HTTP/1.1").append(CRLF);
            /** host. */
            sb.append("Host: ").append(CRLF);
            /** header. */
            for (String header : headers) {
                sb.append(header).append(CRLF);
            }
            /** Content-Length. */
            sb.append("Content-Length: " + Integer.toString(this.contentLength)).append(CRLF);
            /** If-Match. */
            if (null != this.etag) {
                sb.append("If-Match: " + this.etag).append(CRLF);
            }
            if (HttpMethods.POST.equals(this.method) || HttpMethods.PUT.equals(this.method)) {
                sb.append(CRLF);
                sb.append(this.body.toString()).append(CRLF);
            }
            return sb.toString();
        }
    }

    // /**
    // * $Batchの複数ChangeSetをまとめる.
    // */
    /**
     * This is the inner class used to put together a multiple ChangeSet of $Batch.
     */
    private class ChangeSet {
        /** Variable for Changeset Boundary. */
        private String changesetBoundary = null;
        /** Variable for Body. */
        private StringBuilder body = null;

        // private int contentLength = 0;
        /**
         * This is the parameterized constructor to initialize changesetBoundary.
         * @param value changesetBoundary Value
         */
        ChangeSet(final String value) {
            this.changesetBoundary = value;
        }

        /**
         * This method is used to create the body for ChangeSet.
         * @param value Value
         * @throws DaoException Exception thrown
         */
        void append(final String value) throws DaoException {
            if (body == null) {
                body = new StringBuilder();
            } else {
                body.append(CRLF);
            }
            /** ChangeSetHeader. */
            body.append("--" + this.changesetBoundary).append(CRLF);
            body.append("Content-Type: application/http").append(CRLF);
            body.append("Content-Transfer-Encoding: binary").append(CRLF);
            body.append(CRLF);

            body.append(value);
            // try {
            // this.contentLength += body.toString().getBytes("UTF-8").length;
            // } catch (UnsupportedEncodingException e) {
            // throw new DaoException(e.getMessage());
            // }
        }

        /**
         * This method is used to get the $Batch ChangeSet command.
         * @return $Batch ChangeSet command
         * @throws DaoException Exception thrown
         */
        String get() throws DaoException {
            this.body.append(CRLF);
            StringBuilder sb = new StringBuilder();
            String changeSetFooter = "--" + this.changesetBoundary + "--";
            sb.append("--" + batchBoundary).append(CRLF);
            sb.append("Content-Type: multipart/mixed; boundary=").append(this.changesetBoundary).append(CRLF);
            // Content-Length
            try {
                sb.append(
                        "Content-Length: "
                                + Integer.toString(this.body.toString().getBytes("UTF-8").length
                                        + changeSetFooter.getBytes("UTF-8").length)).append(CRLF);
            } catch (UnsupportedEncodingException e) {
                throw new DaoException(e.getMessage());
            }
            sb.append(CRLF);
            /** ChangeSetBody. */
            sb.append(this.body);
            sb.append(changeSetFooter).append(CRLF);
            return sb.toString();
        }
    }

    // /**
    // * コマンドを$Batchフォーマットに生成する.
    // */
    /**
     * This is the inner class used to generate the $Batch format command.
     */
    private class Batch {
        /** Variable for Batch. */
        private StringBuilder batch = null;

        /**
         * This is the default constructor to initialize Batch.
         */
        Batch() {
            batch = new StringBuilder();
            // batch.append("--" + batchBoundary).append(BR);
            // batch.append("Content-Type: application/http").append(BR);
        }

        /**
         * This method is used to append value to Batch.
         * @param value the body to set.
         */
        void append(String value) {
            if (batch.length() > 0) {
                batch.append(CRLF);
            }
            batch.append(value);
        }

        /**
         * This method is used to get the Batch value.
         * @return Batch value in String form
         */
        String get() {
            batch.append(CRLF);
            batch.append("--" + batchBoundary + "--");
            return batch.toString();
        }
    }

    // /**
    // * コンストラクタ.
    // * @param as アクセス主体
    // */
    /**
     * This is the parameterized constructor used to initialize various class variables.
     * @param as Accessor object
     */
    public BatchAdapter(Accessor as) {
        this.accessor = as;
        this.batchBoundary = "batch_" + UUID.randomUUID().toString();
        this.batch = new Batch();
    }

    /**
     * This method returns the Accessor.
     * @return the accessor
     */
    public final Accessor getAccessor() {
        return accessor;
    }

    /**
     * This method returns the BatchBoundary.
     * @return the batchBoundary
     */
    public final String getBatchBoundary() {
        return batchBoundary;
    }

    /**
     * This method is used to append value to the ChangeSet.
     * @param value ChangeSet value
     * @throws DaoException Exception thrown
     */
    private void appendChangeSet(String value) throws DaoException {
        if (null == this.changeSet) {
            this.changeSet = new ChangeSet("changeset_" + UUID.randomUUID().toString());
        }
        this.changeSet.append(value);
    }

    /**
     * This method appends value of ChangeSet to Batch and overwrites ChangeSet.
     * @throws DaoException Exception thrown
     */
    private void writeChangeSet() throws DaoException {
        if (null != this.changeSet) {
            batch.append(changeSet.get());
            this.changeSet = null;
        }
    }

    // /**
    // * BatchBoundaryを挿入する.
    // * @throws DaoException Dao例外
    // */
    /**
     * This method is used to insert the BatchBoundary.
     * @throws DaoException Exception thrown
     */
    public void insertBoundary() throws DaoException {
        writeChangeSet();
    }

    /**
     * This method is used to fetch the Command. It calls its overloaded version internally.
     * @param url URL value
     * @param accept Value
     * @return DcResponse
     * @throws DaoException Exception thrown
     */
    @Override
    public DcResponse get(String url, String accept) throws DaoException {
        return get(url, accept, null);
    }

    /**
     * This method appends the ChangeSet. It returns a new instance of DcBatchResponse.
     * @param url Value
     * @param accept Value
     * @param etag Value
     * @return DcResponse
     * @throws DaoException Exception thrown
     */
    @Override
    public DcResponse get(String url, String accept, String etag) throws DaoException {
        // 溜めたChangeSetを吐き出す
        /** Update ChangeSet. */
        this.writeChangeSet();
        Command cmd = new Command();
        cmd.method = HttpMethods.GET;
        cmd.url = url;
        cmd.addHeader("Accept-Encoding", "gzip");
        cmd.addHeader("Accept", accept);
        cmd.etag = etag;
        batch.append(cmd.get());
        return new DcBatchRespose();
    }

    /**
     * This method will be used to return Command when headers are specified. This has not been implemented yet.
     * @param url Value
     * @param headers Map
     * @param etag Value
     * @return DcResponse
     * @throws DaoException Exception thrown
     */
    @Override
    public DcResponse get(String url, Map<String, String> headers, String etag) throws DaoException {
        throw new DaoException("Not Implemented");
    }

    /**
     * This method retrieves the ChangeSet.
     * @param url Value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    @Override
    public DcResponse head(String url) throws DaoException {
        // 溜めたChangeSetを吐き出す
        /** Update ChangeSet. */
        this.writeChangeSet();
        return get(url, CONTENT_TYPE_JSON, null);
    }

    /**
     * This method updates the ChangeSet.
     * @param url Value
     * @param data Value
     * @param etag Value
     * @param contentType Value
     * @return DcBatchRespose object
     * @throws DaoException Exception thrown
     */
    @Override
    public DcResponse put(String url, String data, String etag, String contentType) throws DaoException {
        Command cmd = new Command();
        cmd.method = HttpMethods.PUT;
        cmd.url = url;
        cmd.addHeader("Content-Type", contentType);
        cmd.etag = etag;
        cmd.setBody(data);
        appendChangeSet(cmd.get());
        return new DcBatchRespose();
    }

    /**
     * This method updates the ChangeSet and creates header for Command.
     * @param url Value
     * @param data Value
     * @param etag Value
     * @param map Header Map
     * @param contentType Value
     * @return DcBatchRespose object
     * @throws DaoException Exception thrown
     */
    @Override
    public DcResponse put(String url, String data, String etag, HashMap<String, String> map, String contentType)
            throws DaoException {
        Command cmd = new Command();
        cmd.method = HttpMethods.PUT;
        cmd.url = url;
        cmd.addHeader("Content-Type", contentType);
        cmd.etag = etag;
        cmd.setBody(data);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            cmd.addHeader(entry.getKey(), entry.getValue());
        }
        appendChangeSet(cmd.get());
        return new DcBatchRespose();
    }

    /**
     * This method creates a ChangeSet.
     * @param url Value
     * @param data Value
     * @param contentType Value
     * @return DcBatchRespose object
     * @throws DaoException Exception thrown
     */
    @Override
    public DcResponse post(String url, String data, String contentType) throws DaoException {
        Command cmd = new Command();
        cmd.method = HttpMethods.POST;
        cmd.url = url;
        cmd.addHeader("Content-Type", contentType);
        cmd.setBody(data);
        appendChangeSet(cmd.get());
        return new DcBatchRespose();
    }

    /**
     * This method creates a ChangeSet and creates header for Command.
     * @param url Value
     * @param map Header Map
     * @param data Value
     * @param contentType Value
     * @return DcBatchRespose object
     * @throws DaoException Exception thrown
     */
    @Override
    public DcResponse post(String url, HashMap<String, String> map, String data, String contentType)
            throws DaoException {
        Command cmd = new Command();
        cmd.method = HttpMethods.POST;
        cmd.url = url;
        cmd.addHeader("Content-Type", contentType);
        cmd.setBody(data);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            cmd.addHeader(entry.getKey(), entry.getValue());
        }
        appendChangeSet(cmd.get());
        return new DcBatchRespose();
    }

    /**
     * This method deletes the ChangeSet.
     * @param url Value
     * @return DcBatchResponse object
     * @throws DaoException Exception thrown
     */
    @Override
    public DcResponse del(String url) throws DaoException {
        Command cmd = new Command();
        cmd.method = HttpMethods.DELETE;
        cmd.url = url;
        appendChangeSet(cmd.get());
        return new DcBatchRespose();
    }

    /**
     * This method deletes the ChangeSet using Etag value.
     * @param url Value
     * @param etag Value
     * @return DcBatchResponse object
     * @throws DaoException Exception thrown
     */
    @Override
    public DcResponse del(String url, String etag) throws DaoException {
        Command cmd = new Command();
        cmd.method = HttpMethods.DELETE;
        cmd.url = url;
        cmd.etag = etag;
        appendChangeSet(cmd.get());
        return new DcBatchRespose();
    }

    // /**
    // * $Batchのボディ情報を取得する.
    // * @return Batch登録するボディ.
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to get the body of information $ Batch.
     * @return Batch Registration Body
     * @throws DaoException Exception thrown
     */
    public String get() throws DaoException {
        // 溜めたChangeSetを吐き出す
        /** Update ChangeSet. */
        this.writeChangeSet();
        return this.batch.get();
    }

    // /**
    // * レスポンスボディを受けるMERGEメソッド.
    // * @param url リクエスト対象URL
    // * @param data 書き込むデータ
    // * @param etag ETag
    // * @param contentType CONTENT-TYPE値
    // * @return DcResponseオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method will receive the response body. It has not been implemented yet.
     * @param url Target Request URL
     * @param data Data to be witten
     * @param etag ETag Value
     * @param contentType CONTENT-TYPE value
     * @return DcResponse object
     * @throws DaoException Exception thrown
     */
    public DcResponse merge(String url, String data, String etag, String contentType) throws DaoException {
        // TODO バッチ経由のMERGEメソッドの処理を実装する
        /** TODO Implement the processing of the MERGE method via batch. */
        DcResponse res = null;
        return res;
    }

    /**
     * This method updates the ChangeSet and adds header to Command.
     * @param url Value
     * @param map Header Map
     * @return DcBatchRespose object
     * @throws DaoException Exception thrown
     */
    @Override
    public DcResponse put(String url, HashMap<String, String> map) throws DaoException {
        Command cmd = new Command();
        cmd.method = HttpMethods.PUT;
        cmd.url = url;
        for (Map.Entry<String, String> entry : map.entrySet()) {
            cmd.addHeader(entry.getKey(), entry.getValue());
        }
        appendChangeSet(cmd.get());
        return new DcBatchRespose();
    }
}
