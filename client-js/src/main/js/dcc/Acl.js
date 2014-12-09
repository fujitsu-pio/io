/*
=================================================================
personium.io
Copyright 2014 FUJITSU LIMITED

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and limitations under the License.
=================================================================
 */
/*global dcc:false,window:false,DOMParser:false */

///**
//* @class Aceのアクセスクラス.
//* @constructor
//*/
/**
 * It creates a new object dcc.Acl.
 * @class Acl class for setting access control information.
 * @constructor
 */
dcc.Acl = function() {
    this.initializeProperties(this);
};

///**
//* プロパティを初期化する.
//* @param {dcc.Acl} self
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.Acl} self
 */
dcc.Acl.prototype.initializeProperties = function(self) {
    /** ACE. */
    self.aceList = [];
//  /** base属性値. */
    /** Base URL. */
    self.roleBaseUrl = null;
//  /** requireSchemaAuthz属性値. */
    /** requireSchemaAuthz attribute value. */
    self.requireSchemaAuthz = null;
};

///**
//* base属性値を設定.
//* @param {String} value base属性値
//*/
/**
 * This method sets the base attribute value.
 * @param {String} value base URL
 */
dcc.Acl.prototype.setBase = function(value) {
    this.roleBaseUrl = value;
};

///**
//* base属性値を取得.
//* @return {String} base属性値
//*/
/**
 * This method gets the base attribute value.
 * @return {String} base URL
 */
dcc.Acl.prototype.getBase = function() {
    return this.roleBaseUrl;
};

///**
//* requireSchemaAuthz属性値を設定.
//* @param {String} value requireSchemaAuthz属性値
//*/
/**
 * This method sets the requireSchemaAuthz attribute value.
 * @param {String} value requireSchemaAuthz attribute value
 */
dcc.Acl.prototype.setRequireSchemaAuthz = function(value) {
    this.requireSchemaAuthz = value;
};

///**
//* requireSchemaAuthz属性値を取得.
//* @return {String} requireSchemaAuthz属性値
//*/
/**
 * This method gets the requireSchemaAuthz attribute value.
 * @return {String} requireSchemaAuthz attribute value
 */
dcc.Acl.prototype.getRequireSchemaAuthz = function() {
    return this.requireSchemaAuthz;
};

///**
//* ACEを追加.
//* @param {String} value ACEオブジェクト
//*/
/**
 * This method adds the Ace object in the list.
 * @param {String} value ACE object
 */
dcc.Acl.prototype.addAce = function(value) {
    this.aceList.push(value);
};

///**
//* Aceオブジェクトの一覧を返却.
//* @return {dcc.Ace} Aceオブジェクト一覧
//*/
/**
 * This method fetches the Ace object from the list.
 * @return {dcc.Ace} Ace object
 */
dcc.Acl.prototype.getAceList = function() {
    return this.aceList;
};

///**
//* XML形式の文字列としてACL情報を取得する.
//* @return {String} XML文字列
//* @throws {ClientException} DAO例外
//*/
/**
 * This methods gets the ACL information as a string of XML format.
 * @return {String} XML string
 * @throws {dcc.ClientException} DAO exception
 */
//public String toXmlString() throws ClientException {
dcc.Acl.prototype.toXmlString = function() {
    var arr = [];

    var roleBaseUrlStr = this.roleBaseUrl;
    var baseRoleBoxName = "";
    var schemaAuth = this.getRequireSchemaAuthz();
    // if roleBaseUrl is not specified, then infer it
    // from the Ace Roles.Also infer baseRoleBoxName.
    if (roleBaseUrlStr === null) {
        if ((this.aceList.length > 0) && (this.aceList[0] !== null)) {
            var baseRole = this.aceList[0].getRole();
            if (baseRole === null) {
                roleBaseUrlStr = "";
                baseRoleBoxName = "";
            } else {
                roleBaseUrlStr = baseRole.getResourceBaseUrl();
                baseRoleBoxName = baseRole.getBoxName();
            }
        } else {
            roleBaseUrlStr = "";
            baseRoleBoxName = "";
        }
    }

    // root element created
    arr.push("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
    if(!schemaAuth){
        schemaAuth = "none";
    }
    arr.push("<D:acl xmlns:D=\"DAV:\" xmlns:dc=\"urn:x-dc1:xmlns\"  dc:requireSchemaAuthz=\""+ schemaAuth +"\" xml:base=\"");
    arr.push(roleBaseUrlStr);
    arr.push("\">");

    // ace element
    for ( var i = 0; i < this.aceList.length; i++) {
        var ace = this.aceList[i];
        arr.push("<D:ace>");

        // acl/ace/principal
        arr.push("<D:principal>");
        // if (dcc.cellctl.Principal !== undefined) {
        if (dcc.cellctl.Principal !== undefined && ace.getPrincipal() === dcc.cellctl.Principal.ALL) {
            // acl/ace/principal/all
            arr.push("<D:all>");
            arr.push("</D:all>");
        }
        // }
        else {
            // acl/ace/principal/href
            arr.push("<D:href>");
            var hrefStr = "";
            var aceRoleName = ace.getRoleName();
            var aceRoleBoxName = ace.getBoxName();

            if (aceRoleBoxName === null) {
                if (baseRoleBoxName !== null) {
                    hrefStr = "../__/" + aceRoleName;
                } else {
                    hrefStr = aceRoleName;
                }
            } else {
                if (aceRoleBoxName === baseRoleBoxName) {
                    hrefStr = aceRoleName;
                } else {
                    hrefStr = "../" + aceRoleBoxName + "/" + aceRoleName;
                }
            }
            arr.push(hrefStr);
            arr.push("</D:href>");
        }
        arr.push("</D:principal>");

        // acl/ace/grant
        arr.push("<D:grant>");

        var privilegeList = ace.getPrivilegeList();
        for ( var j = 0; j < privilegeList.length; j++) {
            var privilege = privilegeList[j];

            // acl/ace/grant/privilege
            arr.push("<D:privilege>");
            arr.push("<D:" + privilege + "/>");
            arr.push("</D:privilege>");
        }
        arr.push("</D:grant>");
        arr.push("</D:ace>");
    }
    arr.push("</D:acl>");
    var xml = arr.join("");
    return xml;

    //    // XML DOM 初期設定
    //    String nsD = "DAV:";
    //    String roleBaseUrlStr = this.roleBaseUrl;
    //    String nsDefault = "http://www.w3.org/XML/1998/namespace";
    //
    //    String baseRoleBoxName = "";
    //    if (roleBaseUrlStr == null) {
    //        Role baseRole = aceList.get(0).getRole();
    //        if (baseRole == null) {
    //            return "";
    //        }
    //
    //        roleBaseUrlStr = baseRole.getResourceBaseUrl();
    //        baseRoleBoxName = baseRole.getBoxName();
    //    }
    //
    //    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    //    factory.setNamespaceAware(true);
    //    DocumentBuilder builder;
    //    try {
    //        builder = factory.newDocumentBuilder();
    //    } catch (ParserConfigurationException e1) {
    //        throw new ClientException(e1.getMessage(), e1);
    //    }
    //    DOMImplementation domImpl = builder.getDOMImplementation();
    //    Document document = domImpl.createDocument(nsD, "D:acl", null);
    //
    //    // root 要素作成
    //    Element acl = document.getDocumentElement();
    //    Attr attrBase = document.createAttributeNS(nsDefault, "xml:base");
    //    attrBase.setValue(roleBaseUrlStr);
    //    acl.setAttributeNodeNS(attrBase);
    //    Attr attrRequireSchemaAuthz = document.createAttributeNS("urn:x-dc1:xmlns", "dc:requireSchemaAuthz");
    //    attrRequireSchemaAuthz.setValue(requireSchemaAuthz);
    //    acl.setAttributeNodeNS(attrRequireSchemaAuthz);
    //
    //    // ace要素
    //    for (Ace ace : aceList) {
    //        Element elmAce = document.createElementNS(nsD, "D:ace");
    //        acl.appendChild(elmAce);
    //
    //        // acl/ace/principal
    //        Element elmPrincipal = document.createElementNS(nsD, "D:principal");
    //        elmAce.appendChild(elmPrincipal);
    //
    //        // acl/ace/principal/href
    //        Element elmHref = document.createElementNS(nsD, "D:href");
    //        elmPrincipal.appendChild(elmHref);
    //
    //        String hrefStr = "";
    //
    //        // Role aceRole = ace.getRole();
    //        String aceRoleName = ace.getRoleName();
    //        String aceRoleBoxName = ace.getBoxName();
    //
    //        if (aceRoleBoxName == null) {
    //            if (baseRoleBoxName != null) {
    //                hrefStr = "../__/" + aceRoleName;
    //            } else {
    //                hrefStr = aceRoleName;
    //            }
    //        } else {
    //            if (aceRoleBoxName.equals(baseRoleBoxName)) {
    //                hrefStr = aceRoleName;
    //            } else {
    //                hrefStr = "../" + aceRoleBoxName + "/" + aceRoleName;
    //            }
    //        }
    //        Text text = document.createTextNode(hrefStr);
    //        elmHref.appendChild(text);
    //
    //        // acl/ace/grant
    //        Element elmGrant = document.createElementNS(nsD, "D:grant");
    //        elmAce.appendChild(elmGrant);
    //
    //        for (String privilege : ace.getPrivilegeList()) {
    //
    //            // acl/ace/grant/privilege
    //            Element elmPrivilege = document.createElementNS(nsD, "D:privilege");
    //            elmGrant.appendChild(elmPrivilege);
    //
    //            // 各権限
    //            Element elm = document.createElementNS(nsD, "D:" + privilege);
    //            elmPrivilege.appendChild(elm);
    //        }
    //    }
    //
    //    // XML を 文字列化する
    //    StringWriter sw = new StringWriter();
    //    TransformerFactory tfactory = TransformerFactory.newInstance();
    //    Transformer transformer = null;
    //    try {
    //        transformer = tfactory.newTransformer();
    //    } catch (TransformerConfigurationException e) {
    //        throw new RuntimeException(e);
    //    }
    //    try {
    //        if (transformer != null) {
    //            transformer.transform(new DOMSource(acl), new StreamResult(sw));
    //        }
    //    } catch (TransformerException e) {
    //        throw new RuntimeException(e);
    //    }
    //    return sw.toString();
};

/**
 * Parse the WebDAV ACL XML String and generate Acl object.
 * @param {String} xmlStr representation of WebDAV ACL XML
 * @returns {dcc.Acl} Acl class instance
 * @throws {dcc.ClientException} ClientException
 */
dcc.Acl.prototype.parse = function(xmlStr) {
    var grant = "";
    var privilegeNodeList = "";
    var roleName = null;
    var privilege = null;
    var xmlDoc = null;

    // Converting the XML String to XML document through DOMParser
    if (window.DOMParser) {
        var parser = new DOMParser();
        xmlDoc = parser.parseFromString(xmlStr, "text/xml");
    }
    var objAcl = new dcc.Acl();
    if (xmlDoc === null || xmlDoc === undefined) {
        throw new dcc.ClientException("DOM Parser is unavailable");
    }
    var nl = xmlDoc.getElementsByTagName("response");
    var elm = nl[0];
    /** base and requireSchemaAuthz are not fetched since they were not set while creating the xml and are therefore unavailable*/
    // var acl = elm.getElementsByTagName("acl");
    // Get Ace list
    var ace = elm.getElementsByTagName("ace");
    var objAce = "";
    for ( var aceCount = 0; aceCount < ace.length; aceCount++) {

        // Ace object creation
        objAce = new dcc.Ace();
        objAcl.addAce(objAce);
        if (ace[aceCount].firstElementChild !== null) {
            if (ace[aceCount].firstElementChild.childNodes[1] === "all") {
                objAce.setPrincipal(dcc.cellctl.Principal.ALL);
            } else {

                // Get Role name (href attribute value), and sets the object Ace
                // The principal is a Role.
                roleName = ace[aceCount].firstElementChild.childNodes[1].firstChild.data;
                var objRole = new dcc.cellctl.Role();
                objRole.setName(roleName);
                objAce.setPrincipal(objRole);
            }

            // Privilege element
            grant = ace[aceCount].lastElementChild;
            privilegeNodeList = grant.childNodes;
            for ( var prvNodeCount = 0; prvNodeCount < privilegeNodeList.length - 1; prvNodeCount = prvNodeCount + 2) {

                // set to Ace object privilege value as the element name of the child element of the privilege element
                privilege = privilegeNodeList[prvNodeCount + 1].firstElementChild.nodeName;
                objAce.addPrivilege(privilege);
            }
        }
    }
    return objAcl;
};

///**
//* XML文字列からAcl/Aceオブジェクトを生成する.
//* @param {String} xmlStr XML文字列
//* @return {jACL} Aclオブジェクト
//*/
//public static Acl parse(String xmlStr) {
//dcc.Acl.prototype.parse = function(xmlStr) {
//String nsD = "DAV:";
//String roleBaseUrl = "";
//String requireSchemaAuthz = "";
//String nsDefault = "http://www.w3.org/XML/1998/namespace";
//DocumentBuilder builder = null;
//DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
//factory.setNamespaceAware(true);
//try {
//builder = factory.newDocumentBuilder();
//} catch (ParserConfigurationException e) {
//throw new RuntimeException(e);
//}
//Document document = null;
//InputStream is = new ByteArrayInputStream(xmlStr.getBytes());
//try {
//if (builder != null) {
//document = builder.parse(is);
//}
//} catch (SAXException e) {
//throw new RuntimeException(e);
//} catch (IOException e) {
//throw new RuntimeException(e);
//}

//Acl acl = new Acl();
//if (document != null) {
////Root要素取得
//Element elmAcl = (Element) document.getElementsByTagNameNS(nsD, "acl").item(0);
////Base属性値を取得し、Aclオブジェクトにセット
//roleBaseUrl = elmAcl.getAttributeNS(nsDefault, "base");
//acl.setBase(roleBaseUrl);

////requireSchemaAuthz属性値を取得し、Aclオブジェクトにセット
//requireSchemaAuthz = elmAcl.getAttributeNS("urn:x-dc1:xmlns", "requireSchemaAuthz");
//acl.setRequireSchemaAuthz(requireSchemaAuthz);

////子Aceのリストを取得
//NodeList nl = document.getElementsByTagNameNS(nsD, "ace");
//Ace ace = null;
//Element elmAce = null;
//for (int i = 0; i < nl.getLength(); i++) {
////Aceオブジェクト生成
//ace = new Ace();
//acl.addAce(ace);

////Role名(href属性値)を取得し、Aceオブジェクトにセット
//elmAce = (Element) nl.item(i);
//NodeList nodeList = elmAce.getElementsByTagNameNS(nsD, "href");
//String roleUrl = nodeList.item(0).getFirstChild().getNodeValue();
//ace.setRoleName(roleUrl);

////privilege要素
//NodeList privilegeList = elmAce.getElementsByTagNameNS(nsD, "privilege");
//for (int n = 0; n < privilegeList.getLength(); n++) {
//Node elmPrivilege = privilegeList.item(n);
////privilege要素の子要素の要素名をprivilege値としてAceオブジェクトにセットする
//ace.addPrivilege(getChildElementName(elmPrivilege));
//}
//}
//}
//return acl;
//};
//static String getChildElementName(Node elm) {
//dcc.Acl.prototype.getChildElementName = function(elm) {
//NodeList nl = elm.getChildNodes();
//String name = "";
//for (int i = 0; i < nl.getLength(); i++) {
//Node node = nl.item(i);
//if (node.getNodeType() == Node.ELEMENT_NODE) {
//name = node.getLocalName();
//break;
//}
//}
//return name;
//};
