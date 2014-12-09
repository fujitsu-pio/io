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

import java.util.ArrayList;
import java.util.List;

/**
 * It creates a new object of Ace. This class represents Ace (Access Control Element) in WebDAV ACL implemented in PCS.
 */
public class Ace {

    /** Target Principal . */
    Principal principal;
    /** Granted Privileges List for this Ace. */
    List<String> privilegeList;
    /** Role Name. @deprecated */
    String roleName;

    /**
     * This is the default constructor calling its parent constructor and initializing the privilegeList.
     */
    public Ace() {
        super();
        this.privilegeList = new ArrayList<String>();
    }

    /**
     * This method gets Principal of this Ace in the form of Role. It will throw ClassCastException if the principal is
     * not a role.
     * @deprecated Replaced with {@link #getPrincipal()}
     * @return Role Object
     */
    public Role getRole() {
        return (Role) this.principal;
    }

    /**
     * This method gets the Principal (typically a role) of this Ace.
     * @return Principal of this Ace.
     */
    public Principal getPrincipal() {
        return this.principal;
    }

    /**
     * This method sets the Principal (typically a role) of this Ace in the form of Role.
     * @param role Role Object.
     * @deprecated Replaced with {@link #setPrincipal(Principal)}
     */
    public void setRole(Role role) {
        this.setPrincipal(role);
    }

    /**
     * This method takes Principal All as parameter.
     * @param principalAll Principal All
     * @deprecated
     */
    public void setRole(boolean principalAll) {
    }

    /**
     * This method sets the Principal (typically a role in PCS) for this Ace.
     * @param principal instance of principal
     */
    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    /**
     * This method gets the Role Name.
     * @return Role Name
     * @deprecated
     */
    public String getRoleName() {
        if (this.principal != null) {
            return ((Role) this.principal).getName();
        } else {
            return this.roleName;
        }
    }

    /**
     * This method gets the _Box.Name value of the Role for this Ace.
     * @return the _Box.Name value of the Role
     * @deprecated
     */
    public String getBoxName() {
        if (this.principal != null) {
            return ((Role) this.principal).getBoxName();
        } else {
            return "";
        }
    }

    /**
     * This method sets Role Name.
     * @deprecated
     * @param value Role Name
     */
    public void setRoleName(String value) {
        this.roleName = value;
    }

    /**
     * This method adds a privilege to the privilegeList.
     * @param value privilege name
     */
    public void addPrivilege(String value) {
        this.privilegeList.add(value);
    }

    /**
     * This method returns privilege list.
     * @return list of privileges
     */
    public List<String> getPrivilegeList() {
        return this.privilegeList;
    }
}
