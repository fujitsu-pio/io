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
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.io.Charsets;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fujitsu.dc.engine.DcEngineException;

/**
 * Service resource source management using file system.
 */
public class FsServiceResourceSourceManager implements ISourceManager {
    /** ログオブジェクト. */
    private static Logger log = LoggerFactory.getLogger(FsServiceResourceSourceManager.class);

    private String fsPath;

    /** コレクションのPROPPATCH情報. */
    private String serviceCollectionInfo;

    /** Mapping from path to source file. */
    private Map<String, String> pathMap = new HashMap<>();
    
    private String serviceSubject;

    /**
     * コンストラクタ.
     * @param filePath 対象サービスコレクションのFile System Path.
     * @throws DcEngineException DcEngineException
     */
    public FsServiceResourceSourceManager(String filePath) throws DcEngineException {
        this.fsPath = filePath;
        log.info("Source File Path: [" + this.fsPath + "]");
        this.loadServiceCollectionInfo();
        this.parseServiceTag();
    }

    /**
     * サービスコレクションの情報を取得.
     * @throws DcEngineException DcEngineException
     */
    private void loadServiceCollectionInfo() throws DcEngineException {
        // filePath null check.
        if (this.fsPath == null) {
            log.info("File path is empty.");
            throw new DcEngineException("404 Not Found (Request Header invalid) ",
                    DcEngineException.STATUSCODE_NOTFOUND);
        }

        // サービスコレクションを取得
        File metaFile = new File(this.fsPath + "/.pmeta");
        JSONObject json = null;
        try (Reader reader = Files.newBufferedReader(metaFile.toPath(), Charsets.UTF_8)) {
            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse(reader);
        } catch (IOException | ParseException e) {
            // IO failure or JSON is broken
            log.info("Meta file not found or invalid (" + this.fsPath + ")");
            throw new DcEngineException("500 Server Error",
            DcEngineException.STATUSCODE_SERVER_ERROR);
        }

        // スクリプトの情報を取得する
        this.serviceCollectionInfo = (String) ((Map<?, ?>) json.get("d")).get("service@urn:x-dc1:xmlns");
        if (null == this.serviceCollectionInfo) {
            log.info("Service property Invalid ");
            throw new DcEngineException("404 Not Found (Service property invalid) ",
                    DcEngineException.STATUSCODE_NOTFOUND);
        }

        log.debug("scriptPath: [" + this.serviceCollectionInfo + "] ");
    }

    /**
     * サービス名に対応したスクリプトファイル名の取得.
     * @param servicePath サービス名
     * @return スクリプトファイル名
     */
    public String getScriptNameForServicePath(String servicePath) {
        return this.pathMap.get(servicePath);
    }

    /**
     * ソースファイルを取得.
     * @param sourceName ソースファイル名
     * @return ソースファイルの中身
     * @throws DcEngineException DcEngineException
     */
    public String getSource(String sourceName) throws DcEngineException {
        // 対象のスクリプトの情報を取得する
        String sourcePath = this.fsPath + File.separator + "__src" + File.separator + sourceName
            + File.separator + "content";
        File sourceFile = new File(sourcePath);

        if (!sourceFile.exists()) {
            log.info("Service Source not found (" + sourceName + ")");
            throw new DcEngineException("404 Not Found", DcEngineException.STATUSCODE_NOTFOUND);
        }

        try {
            return new String(Files.readAllBytes(sourceFile.toPath()), Charsets.UTF_8);
        } catch (IOException e) {
          log.info("UserScript Encoding error(UnsupportedEncodingException) ", e);
          throw new DcEngineException("404 UserScript Encoding error", DcEngineException.STATUSCODE_NOTFOUND, e);
        }
    }

    /**
     * サービスコレクションに設定されたサービスサブジェクトの取得.
     * @return サービスサブジェクト
     */
    public String getServiceSubject() {
      return this.serviceSubject;
    }

    /**
     * サービス名からスクリプトファイルのパスを取得する.
     * @param xml XML文字列
     * @param svcName サービス名
     * @return スクリプトファイルパス
     */
    private void parseServiceTag() {
        DocumentBuilder builder = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document doc = null;
        InputStream is = new ByteArrayInputStream(this.serviceCollectionInfo.getBytes());
        try {
            doc = builder.parse(is);
            Element el = doc.getDocumentElement();
            this.serviceSubject = el.getAttribute("subject");
            NodeList nl = doc.getElementsByTagNameNS("*", "path");
            for (int i = 0; i < nl.getLength(); i++) {
                NamedNodeMap nnm = nl.item(i).getAttributes();
                pathMap.put(nnm.getNamedItem("name").getNodeValue(),nnm.getNamedItem("src").getNodeValue());
            }
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
