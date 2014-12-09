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
package com.fujitsu.dc.core.rs.cell;

import javax.ws.rs.GET;
import javax.ws.rs.OPTIONS;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.odata4j.core.OEntityKey;

import com.fujitsu.dc.core.DcCoreException;
import com.fujitsu.dc.core.auth.AccessContext;
import com.fujitsu.dc.core.auth.AuthUtils;
import com.fujitsu.dc.core.auth.CellPrivilege;
import com.fujitsu.dc.core.auth.Privilege;
import com.fujitsu.dc.core.model.Box;
import com.fujitsu.dc.core.model.DavRsCmp;
import com.fujitsu.dc.core.model.ModelFactory;
import com.fujitsu.dc.core.model.ctl.Account;
import com.fujitsu.dc.core.model.ctl.ExtCell;
import com.fujitsu.dc.core.model.ctl.ExtRole;
import com.fujitsu.dc.core.model.ctl.ReceivedMessage;
import com.fujitsu.dc.core.model.ctl.Relation;
import com.fujitsu.dc.core.model.ctl.Role;
import com.fujitsu.dc.core.model.ctl.SentMessage;
import com.fujitsu.dc.core.odata.OEntityWrapper;
import com.fujitsu.dc.core.rs.odata.ODataResource;

/**
 * JAX-RS Resource handling DC Cell Level Api.
 */
public final class CellCtlResource extends ODataResource {

    String dcCredHeader;
    DavRsCmp davRsCmp;

    /**
     * コンストラクタ.
     * @param accessContext AccessContext
     * @param dcCredHeader X-Dc-Credentialヘッダ
     * @param davRsCmp davRsCmp
     */
    public CellCtlResource(final AccessContext accessContext, final String dcCredHeader, DavRsCmp davRsCmp) {
        super(accessContext, accessContext.getCell().getUrl() + "__ctl/", ModelFactory.ODataCtl.cellCtl(accessContext
                .getCell()));
        this.dcCredHeader = dcCredHeader;
        this.davRsCmp = davRsCmp;
    }

    @Override
    public void checkAccessContext(final AccessContext ac, Privilege privilege) {
        // ユニットユーザトークンチェック
        if (ac.isUnitUserToken()) {
            return;
        }

        // アクセス権チェック
        if (!this.davRsCmp.hasPrivilege(ac, privilege)) {
            // トークンの有効性チェック
            // トークンがINVALIDでもACL設定でPrivilegeがallに設定されているとアクセスを許可する必要があるのでこのタイミングでチェック
            if (AccessContext.TYPE_INVALID.equals(ac.getType())) {
                ac.throwInvalidTokenException();
            } else if (AccessContext.TYPE_ANONYMOUS.equals(ac.getType())) {
                throw DcCoreException.Auth.AUTHORIZATION_REQUIRED;
            }
            throw DcCoreException.Auth.NECESSARY_PRIVILEGE_LACKING;
        }
    }

    @Override
    public boolean hasPrivilege(AccessContext ac, Privilege privilege) {
        return this.davRsCmp.hasPrivilege(ac, privilege);
    }

    @Override
    public void checkSchemaAuth(AccessContext ac) {
    }

    @Override
    public void beforeCreate(final OEntityWrapper oEntityWrapper) {
        String entitySetName = oEntityWrapper.getEntitySet().getName();
        String hPassStr = AuthUtils.checkValidatePassword(dcCredHeader, entitySetName);
        if (hPassStr != null) {
            oEntityWrapper.put("HashedCredential", hPassStr);
        }
    }

    @Override
    public void beforeUpdate(final OEntityWrapper oEntityWrapper, final OEntityKey oEntityKey) {
        String entitySetName = oEntityWrapper.getEntitySet().getName();
        String hPassStr = AuthUtils.checkValidatePassword(dcCredHeader, entitySetName);
        if (hPassStr != null) {
            oEntityWrapper.put("HashedCredential", hPassStr);
        }
    }

    /**
     * サービスメタデータリクエストに対応する.
     * @return JAX-RS 応答オブジェクト
     */
    @GET
    @Path("{first: \\$}metadata")
    public Response getMetadata() {
        return super.doGetMetadata();
    }

    /**
     * OPTIONSメソッド.
     * @return JAX-RS Response
     */
    @OPTIONS
    @Path("{first: \\$}metadata")
    public Response optionsMetadata() {
        return super.doGetOptionsMetadata();
    }

    @Override
    public Privilege getNecessaryReadPrivilege(String entitySetNameStr) {
        // セルレベルはエンティティセットごとに権限が異なる
        if (Account.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.AUTH_READ;
        } else if (Role.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.AUTH_READ;
        } else if (ExtRole.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.AUTH_READ;
        } else if (Relation.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.SOCIAL_READ;
        } else if (ExtCell.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.SOCIAL_READ;
        } else if (Box.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.BOX_READ;
        } else if (ReceivedMessage.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.MESSAGE_READ;
        } else if (SentMessage.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.MESSAGE_READ;
        }
        return null;

    }

    @Override
    public Privilege getNecessaryWritePrivilege(String entitySetNameStr) {
        // セルレベルはエンティティセットごとに権限が異なる
        if (Account.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.AUTH;
        } else if (Role.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.AUTH;
        } else if (ExtRole.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.AUTH;
        } else if (Relation.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.SOCIAL;
        } else if (ExtCell.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.SOCIAL;
        } else if (Box.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.BOX;
        } else if (ReceivedMessage.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.MESSAGE;
        } else if (SentMessage.EDM_TYPE_NAME.equals(entitySetNameStr)) {
            return CellPrivilege.MESSAGE;
        }
        return null;
    }

    @Override
    public Privilege getNecessaryOptionsPrivilege() {
        return CellPrivilege.SOCIAL_READ;
    }
}
