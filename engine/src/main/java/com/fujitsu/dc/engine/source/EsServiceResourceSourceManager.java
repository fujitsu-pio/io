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
package com.fujitsu.dc.engine.source;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fujitsu.dc.common.es.EsType;
import com.fujitsu.dc.common.es.response.DcGetResponse;
import com.fujitsu.dc.core.model.file.BinaryDataAccessException;
import com.fujitsu.dc.core.model.file.BinaryDataAccessor;
import com.fujitsu.dc.engine.DcEngineException;
import com.fujitsu.dc.engine.EsModel;
import com.fujitsu.dc.engine.utils.DcEngineConfig;

/**
 * サービスコレクションの情報からソースの情報を管理する.
 */
public class EsServiceResourceSourceManager implements ISourceManager {
    /** ログオブジェクト. */
    private static Logger log = LoggerFactory.getLogger(EsServiceResourceSourceManager.class);

    /** ESインデックス. */
    private String index;

    /** ESタイプ. */
    private String type;

    /** ESID. */
    private String id;

    /** ESRoutingID. */
    private String routingId;

    /** ESアクセッサtype. */
    private EsType typ;

    /** ODataコレクションのPROPPATCH情報. */
    private String serviceCollectionInfo;

    /** ESから取得したODataコレクションの配下のソース情報. */
    private Map<?, ?> sourceInfo;

    /**
     * コンストラクタ.
     * @param index 対象サービスコレクションのESのインデックス
     * @param type 対象サービスコレクションのESのタイプ
     * @param id 対象サービスコレクションのESのID
     * @param routingId 対象サービスコレクションのESのルーティングID
     */
    public EsServiceResourceSourceManager(String index, String type, String id, String routingId) {
        this.index = index;
        this.type = type;
        this.id = id;
        this.routingId = routingId;
        log.info("ElasticSearch index: [" + this.index + "] type: [" + this.type + "] "
                + "id: [" + this.id + "] routingId :[" + this.id + "]");
        this.typ = EsModel.type(this.index, this.type, this.routingId, 0, 0);
    }

    /**
     * サービスコレクションの情報を取得.
     * @throws DcEngineException DcEngineException
     */
    private void loadServiceCollectionInfo() throws DcEngineException {
        if (this.sourceInfo != null) {
            return;
        }
        // elasticsearchからPROPを取得する
        // Type 名に # は使えないっぽい。
        if (this.routingId == null) {
            log.info("Routing ID is empty.");
            throw new DcEngineException("404 Not Found (Request Header invalid) ",
                    DcEngineException.STATUSCODE_NOTFOUND);
        }

        // サービスコレクションを取得
        DcGetResponse getResp = this.typ.get(this.id);
        if (!getResp.isExists()) {
            log.info("Service Collection id not found to ElasticSearch (" + this.id + ")");
            throw new DcEngineException("404 Not Found (Service Collection invalid) ",
                    DcEngineException.STATUSCODE_NOTFOUND);
        }

        // スクリプトの情報を取得する
        this.serviceCollectionInfo = (String) ((Map<?, ?>) getResp.getSource().get("d")).get("service@urn:x-dc1:xmlns");
        if (null == this.serviceCollectionInfo) {
            log.info("Service property Invalid ");
            throw new DcEngineException("404 Not Found (Service property invalid) ",
                    DcEngineException.STATUSCODE_NOTFOUND);
        }

        log.debug("scriptPath: [" + this.serviceCollectionInfo + "] ");
        // childrenを取る （__src）
        String children = (String) ((Map<?, ?>) getResp.getSource().get("o")).get("__src");
        // __src の情報を取得
        getResp = this.typ.get(children);
        if (!getResp.isExists()) {
            log.info("Service Source Colleciton(__src) not found (" + children + ")");
            throw new DcEngineException("404 Not Found (Service Source Collection invalid) ",
                    DcEngineException.STATUSCODE_NOTFOUND);
        }
        this.sourceInfo = (Map<?, ?>) getResp.getSource().get("o");
    }

    /**
     * サービスコレクションに設定されたサービスサブジェクトの取得.
     * @return サービスサブジェクト
     * @throws DcEngineException DcEngineException
     */
    public String getServiceSubject() throws DcEngineException {
        this.loadServiceCollectionInfo();
        // サービスサブジェクトの取得
        return getServiceSubject(this.serviceCollectionInfo);
    }

    /**
     * サービス名に対応したスクリプトファイル名の取得.
     * @param servicePath サービス名
     * @return スクリプトファイル名
     * @throws DcEngineException DcEngineException
     */
    public String getScriptNameForServicePath(String servicePath) throws DcEngineException {
        this.loadServiceCollectionInfo();
        return getScriptName(this.serviceCollectionInfo, servicePath);
    }

    /**
     * ソースファイルを取得.
     * @param sourceName ソースファイル名
     * @return ソースファイルの中身
     * @throws DcEngineException DcEngineException
     */
    public String getSource(String sourceName) throws DcEngineException {
        this.loadServiceCollectionInfo();
        // 対象のスクリプトの情報を取得する
        String sourceNodeId = (String) this.sourceInfo.get(sourceName);
        if (sourceNodeId == null) {
            log.info("Service Source not found (" + sourceName + ")");
            throw new DcEngineException("404 Not Found", DcEngineException.STATUSCODE_NOTFOUND);
        }
        DcGetResponse getResp = this.typ.get(sourceNodeId);
        if (!getResp.isExists()) {
            log.info("Service Source not found (" + sourceName + ")");
            throw new DcEngineException("404 Not Found", DcEngineException.STATUSCODE_NOTFOUND);
        }

        BinaryDataAccessor binaryAccessor = new BinaryDataAccessor(DcEngineConfig.getBlobStoreRoot(), this.index
                .substring(DcEngineConfig.getUnitPrefix().length() + 1), DcEngineConfig.getFsyncEnabled());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            binaryAccessor.copy(sourceNodeId, baos);
            return baos.toString("UTF-8");
        } catch (BinaryDataAccessException e) {
            log.info("UserScript Encoding error(UnsupportedEncodingException) ", e);
            throw new DcEngineException("404 UserScript Encoding error", DcEngineException.STATUSCODE_NOTFOUND, e);
        } catch (UnsupportedEncodingException e) {
            log.info("UserScript Encoding error(UnsupportedEncodingException) ", e);
            throw new DcEngineException("404 UserScript Encoding error", DcEngineException.STATUSCODE_NOTFOUND, e);
        }


    }

    /**
     * サービス名からスクリプトファイルのパスを取得する.
     * @param xml XML文字列
     * @param svcName サービス名
     * @return スクリプトファイルパス
     */
    private String getScriptName(final String xml, final String svcName) {
        String scriptName = "";
        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc = null;
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        try {
            doc = builder.parse(is);

            NodeList nl = doc.getElementsByTagNameNS("*", "path");
            for (int i = 0; i < nl.getLength(); i++) {
                NamedNodeMap nnm = nl.item(i).getAttributes();
                if (nnm.getNamedItem("name").getNodeValue().equals(svcName)) {
                    scriptName = nnm.getNamedItem("src").getNodeValue();
                }
            }
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return scriptName;
    }

    /**
     * サービス設定からサービスサブジェクトの値を取得する.
     * @param xml XML文字列
     */
    private String getServiceSubject(final String xml) {
        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc = null;
        InputStream is = new ByteArrayInputStream(xml.getBytes());
        try {
            doc = builder.parse(is);

            Element el = doc.getDocumentElement();
            return el.getAttribute("subject");
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

