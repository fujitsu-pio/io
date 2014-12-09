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
/*global dcc:false */

/**
 * It creates a new object dcc.Ace.
 * @class class representing Ace (Access Control Element) in WebDAV ACL implemented in PCS.
 * @constructor
 */
dcc.Ace = function() {
  this.initializeProperties(this);
};

///**
//* プロパティを初期化する.
//* @param {dcc.Ace} self
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.Ace} self
 */
dcc.Ace.prototype.initializeProperties = function(self) {
  /** Granted Privileges List for this Ace. */
  self.privilegeList = [];
  /** Role Name. @deprecated */
  self.roleName = null;
  /** Target Principal . */
  self.principal = null;
};

/**
 * Get Principal of this Ace in the form of Role.
 * @return {dcc.cellctl.Principal} Principal
 * @deprecated Use getPrincipal()
 */
dcc.Ace.prototype.getRole = function() {
  return this.principal;
};

/**
 * Set the Principal (typically a role) of this Ace in the form of Role.
 * @param {dcc.cellctl.Role} obj Role object
 * @deprecated Use setPrincipal(principal)
 */
dcc.Ace.prototype.setRole = function(obj) {
  this.setPrincipal(obj);
};

/**
 * Get the Role Name.
 * @return {String} Role Name
 * @deprecated
 */
dcc.Ace.prototype.getRoleName = function() {
  if (this.principal !== null) {
    return this.principal.getName();
  } else {
    return this.roleName;
  }
};

/**
 * Get the _Box.Name value of the Role for this Ace.
 * @return {String} the _Box.Name value of the Role
 * @deprecated
 */
dcc.Ace.prototype.getBoxName = function() {
  if (this.principal !== null) {
    return this.principal.getBoxName();
  } else {
    return "";
  }
};

/**
 * Set Role Name.
 * @param {String} value Role Name
 * @deprecated
 */
dcc.Ace.prototype.setRoleName = function(value) {
  this.roleName = value;
};

/**
 * Add a privilege.
 * @param {String} value privilege name
 */
dcc.Ace.prototype.addPrivilege = function(value) {
  this.privilegeList.push(value);
};

/**
 * Gives the privilege list.
 * @return {Array.<String>} list of privileges
 */
dcc.Ace.prototype.getPrivilegeList = function() {
  return this.privilegeList;
};

/**
 * Set the Principal (typically a role or string value ALL) for this Ace.
 * @param {dcc.cellctl.Principal} principal
 */
dcc.Ace.prototype.setPrincipal = function(principal){
  this.principal = principal;
};

/**
 * Get the Principal (typically a role or string value ALL) of this Ace.
 * @returns {dcc.cellctl.Principal.principalType}
 */
dcc.Ace.prototype.getPrincipal = function(){
  return this.principal;
};