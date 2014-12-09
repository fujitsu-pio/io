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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

///**
// * Aceのアクセスクラス.
// */
/**
 * It creates a new object of Acl. This class is used for setting access control information.
 */
public class Acl {

    /** ACE. */
    ArrayList<Ace> aceList;
    /** base Attribute Value. */
    String roleBaseUrl;
    /** requireSchemaAuthz Attribute Value. */
    String requireSchemaAuthz;

    /**
     * This is the default constructor calling its parent constructor and initializing the aceList.
     */
    public Acl() {
        super();
        aceList = new ArrayList<Ace>();
    }

    // /**
    // * base属性値を設定.
    // * @param value base属性値
    // */
    /**
     * This method sets the base URL value.
     * @param value baseURL
     */
    public void setBase(String value) {
        this.roleBaseUrl = value;
    }

    // /**
    // * base属性値を取得.
    // * @return base属性値
    // */
    /**
     * This method gets the base URL value.
     * @return baseURL value
     */
    public String getBase() {
        return this.roleBaseUrl;
    }

    // /**
    // * requireSchemaAuthz属性値を設定.
    // * @param value requireSchemaAuthz属性値
    // */
    /**
     * This method sets the requireSchemaAuthz attribute value.
     * @param value requireSchemaAuthz value
     */
    public void setRequireSchemaAuthz(String value) {
        this.requireSchemaAuthz = value;
    }

    // /**
    // * requireSchemaAuthz属性値を取得.
    // * @return requireSchemaAuthz属性値
    // */
    /**
     * This method gets the requireSchemaAuthz attribute value.
     * @return requireSchemaAuthz value
     */
    public String getRequireSchemaAuthz() {
        return this.requireSchemaAuthz;
    }

    // /**
    // * ACEを追加.
    // * @param value ACEオブジェクト
    // */
    /**
     * This method adds the specified ace to the aceList.
     * @param value ACE object
     */
    public void addAce(Ace value) {
        this.aceList.add(value);
    }

    // /**
    // * Aceオブジェクトの一覧を返却.
    // * @return Aceオブジェクト一覧
    // */
    /**
     * This method returns the list of Ace objects.
     * @return Ace List
     */
    public ArrayList<Ace> getAceList() {
        return aceList;
    }

    /**
     * This method generates a string form of WebDAV ACL XML.
     * @return String representation of WebDAV ACL XML
     * @throws DaoException DcClientException
     */
    public String toXmlString() throws DaoException {
        // XML DOM 初期設定
        /** XML DOM default. */
        String nsD = "DAV:";
        String roleBaseUrlStr = this.roleBaseUrl;
        String nsDefault = "http://www.w3.org/XML/1998/namespace";

        String baseRoleBoxName = "";
        /** if roleBaseUrl is not specified, then infer it from the Ace Roles.Also infer baseRoleBoxName. */
        if (this.roleBaseUrl == null) {
            roleBaseUrlStr = "";
            baseRoleBoxName = "";
            for (Ace ace : aceList) {
                if (ace != null) {
                    Principal principal = ace.getPrincipal();
                    if (principal != null && principal instanceof Role) {
                        Role role = (Role) principal;
                        roleBaseUrlStr = role.getResourceBaseUrl();
                        baseRoleBoxName = role.getBoxName();
                        break;
                    } else {
                        roleBaseUrlStr = "";
                        baseRoleBoxName = "";
                    }
                }
            }
        }

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e1) {
            throw new DaoException(e1.getMessage(), e1);
        }
        DOMImplementation domImpl = builder.getDOMImplementation();
        Document document = domImpl.createDocument(nsD, "D:acl", null);

        // root 要素作成
        /** Root element created. */
        Element acl = document.getDocumentElement();
        Attr attrBase = document.createAttributeNS(nsDefault, "xml:base");
        attrBase.setValue(roleBaseUrlStr);
        acl.setAttributeNodeNS(attrBase);
        if (this.requireSchemaAuthz != null && !this.requireSchemaAuthz.trim().equals("")) {
            Attr attrRequireSchemaAuthz = document.createAttributeNS("urn:x-dc1:xmlns", "dc:requireSchemaAuthz");
            attrRequireSchemaAuthz.setValue(requireSchemaAuthz);
            acl.setAttributeNodeNS(attrRequireSchemaAuthz);
        }

        // ace要素
        /** Ace element. */
        for (Ace ace : aceList) {
            if (ace == null) {
                continue;
            }
            Element elmAce = document.createElementNS(nsD, "D:ace");
            acl.appendChild(elmAce);

            /** acl/ace/principal */
            Element elmPrincipal = document.createElementNS(nsD, "D:principal");
            elmAce.appendChild(elmPrincipal);

            if (ace.getPrincipal() == Principal.ALL) {
                Element elmHref = document.createElementNS(nsD, "D:all");
                elmPrincipal.appendChild(elmHref);
            } else {
                /** acl/ace/principal/href */
                Element elmHref = document.createElementNS(nsD, "D:href");
                elmPrincipal.appendChild(elmHref);

                if (ace.getPrincipal() != null) {
                    /** href string should be relative url. */
                    Role aceRole = (Role) ace.getPrincipal();
                    String relativeUrl = aceRole.getRelativeUrl(baseRoleBoxName);

                    Text text = document.createTextNode(relativeUrl);
                    elmHref.appendChild(text);
                }
            }

            /** acl/ace/grant */
            Element elmGrant = document.createElementNS(nsD, "D:grant");
            elmAce.appendChild(elmGrant);

            for (String privilege : ace.getPrivilegeList()) {
                if (privilege == null) {
                    continue;
                }
                /** acl/ace/grant/privilege */
                Element elmPrivilege = document.createElementNS(nsD, "D:privilege");
                elmGrant.appendChild(elmPrivilege);

                // 各権限
                /** Each authority. */
                Element elm = document.createElementNS(nsD, "D:" + privilege);
                elmPrivilege.appendChild(elm);
            }
        }

        // XML を 文字列化する
        /** Converts XML to string. */
        StringWriter sw = new StringWriter();
        TransformerFactory tfactory = TransformerFactory.newInstance();
        Transformer transformer = null;
        try {
            transformer = tfactory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }
        try {
            if (transformer != null) {
                transformer.transform(new DOMSource(acl), new StreamResult(sw));
            }
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
        return sw.toString();
    }

    /**
     * This method is used to parse the WebDAV ACL XML String and generate Acl object.
     * @param xmlStr String representation of WebDAV ACL XML
     * @return Acl class instance.
     */
    public static Acl parse(String xmlStr) {
        String nsD = "DAV:";
        String roleBaseUrl = "";
        String requireSchemaAuthz = "";
        String nsDefault = "http://www.w3.org/XML/1998/namespace";
        DocumentBuilder builder = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        Document document = null;
        InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
        try {
            if (builder != null) {
                document = builder.parse(is);
            }
        } catch (SAXException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Acl acl = new Acl();
        if (document == null) {
            return acl;
        }
        // Root要素取得
        /** Root element acquisition. */
        Element elmAcl = (Element) document.getElementsByTagNameNS(nsD, "acl").item(0);
        // Base属性値を取得し、Aclオブジェクトにセット
        /** Get the Base attribute value, and set to Acl object. */
        roleBaseUrl = elmAcl.getAttributeNS(nsDefault, "base");
        acl.setBase(roleBaseUrl);

        // requireSchemaAuthz属性値を取得し、Aclオブジェクトにセット
        /** Get the requireSchemaAuthz attribute value, and set to Acl object. */
        requireSchemaAuthz = elmAcl.getAttributeNS("urn:x-dc1:xmlns", "requireSchemaAuthz");
        acl.setRequireSchemaAuthz(requireSchemaAuthz);

        // 子Aceのリストを取得
        /** Get a list of child Ace. */
        NodeList nl = document.getElementsByTagNameNS(nsD, "ace");
        Ace ace = null;
        Element elmAce = null;
        for (int i = 0; i < nl.getLength(); i++) {
            // Aceオブジェクト生成
            /** Ace object creation. */
            ace = new Ace();
            acl.addAce(ace);

            // Role名(href属性値)を取得し、Aceオブジェクトにセット
            /** Get Role name (href attribute value), and sets the object Ace. */
            elmAce = (Element) nl.item(i);
            NodeList nodeList = elmAce.getElementsByTagNameNS(nsD, "href");
            if (nodeList.getLength() == 0) {
                ace.setPrincipal(Principal.ALL);
            } else {
                /** The principal is a Role.. */
                String roleUrl = nodeList.item(0).getFirstChild().getNodeValue();
                Role role = new Role();
                role.setName(roleUrl);
                ace.setPrincipal(role);
                // ace.setRoleName(roleUrl);
            }

            // privilege要素
            /** The privilege element. */
            NodeList privilegeList = elmAce.getElementsByTagNameNS(nsD, "privilege");
            for (int n = 0; n < privilegeList.getLength(); n++) {
                Node elmPrivilege = privilegeList.item(n);
                // privilege要素の子要素の要素名をprivilege値としてAceオブジェクトにセットする
                /**
                 * set to Ace object privilege as the value element name of the child elements of the privilege element.
                 */
                ace.addPrivilege(getChildElementName(elmPrivilege));
            }
        }
        return acl;
    }

    /**
     * This method is used to fetch the privilege name from XML node.
     * @param elm Node
     * @return privilegeName
     */
    static String getChildElementName(Node elm) {
        NodeList nl = elm.getChildNodes();
        String name = "";
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                name = node.getLocalName();
                break;
            }
        }
        return name;
    }
}
