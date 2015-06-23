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
package com.fujitsu.dc.test.jersey.box.odatacol.batch;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fujitsu.dc.test.jersey.box.odatacol.AbstractUserDataTest;
import com.fujitsu.dc.test.setup.Setup;
import com.fujitsu.dc.test.utils.BatchUtils;
import com.fujitsu.dc.test.utils.TResponse;
import com.sun.jersey.test.framework.WebAppDescriptor;

/**
 * UserData $batchテスト用の抽象クラス.
 */
public abstract class AbstractUserDataBatchTest extends AbstractUserDataTest {
    String cellName = Setup.TEST_CELL1;
    String boxName = Setup.TEST_BOX1;
    String colName = Setup.TEST_ODATA;

    /**
     * コンストラクタ.
     */
    public AbstractUserDataBatchTest() {
        super();
    }

    /**
     * コンストラクタ.
     * @param descripter WebAppDescriptor
     */
    public AbstractUserDataBatchTest(WebAppDescriptor descripter) {
        super(descripter);
    }

    /**
     * レスポンスボディのチェック.
     * @param res TResponse
     * @param expectedResBody 期待するレスポンスボディ
     */
    public static void checkBatchResponseBody(TResponse res, String expectedResBody) {
        String[] arrResBody = res.getBody().split("\n");
        String[] arrExpResBody = expectedResBody.split("\n");

        for (int i = 0; i < arrResBody.length; i++) {
            Pattern p = Pattern.compile(arrExpResBody[i]);
            Matcher m = p.matcher(arrResBody[i]);
            assertTrue("expected " + arrExpResBody[i] + " but was " + arrResBody[i], m.matches());
        }

        assertFalse(arrResBody.length < arrExpResBody.length);

    }

    String retrievePostResBodyToSetODataCol(String entitySetName, String id) {
        return BatchUtils.retrievePostResBody(cellName, boxName, colName, entitySetName, id, true);
    }

    String retrievePostResBodyToSetODataCol(String entitySetName, String id, boolean isTerminal) {
        return BatchUtils.retrievePostResBody(cellName, boxName, colName, entitySetName, id, isTerminal);
    }

}
