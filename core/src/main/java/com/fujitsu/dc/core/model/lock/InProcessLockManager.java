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
package com.fujitsu.dc.core.model.lock;

import java.util.HashMap;
import java.util.Map;

/**
 * InProcessのLockManager.
 */
class InProcessLockManager extends LockManager {
    Map<String, Object> inProcessLock = new HashMap<String, Object>();

    @Override
    Lock doGetLock(String fullKey) {
        return (Lock) inProcessLock.get(fullKey);
    }

    @Override
    synchronized Boolean doPutLock(String fullKey, Lock lock) {
        if (inProcessLock.get(fullKey) == null) {
            inProcessLock.put(fullKey, lock);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    void doReleaseLock(String fullKey) {
        inProcessLock.remove(fullKey);
    }

    @Override
    void doDeleteAllLocks() {
        inProcessLock.clear();
    }

    @Override
    String doGetReferenceOnlyLock(String fullKey) {
        return (String) inProcessLock.get(fullKey);
    }

    @Override
    Boolean doPutReferenceOnlyLock(String fullKey, String value) {
        if (inProcessLock.get(fullKey) == null) {
            inProcessLock.put(fullKey, value);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    String doGetAccountLock(String fullKey) {
        return (String) inProcessLock.get(fullKey);
    }

    @Override
    Boolean doPutAccountLock(String fullKey, String value, int expired) {
        // expiredは無視される
        // パスワード認証に失敗してアカウントがロックされた場合はそのアカウントに対してパスワード認証リクエストが不可となる。(coreの再起動が必要)
        inProcessLock.put(fullKey, value);
        return Boolean.TRUE;
    }

    @Override
    String doGetUnituserLock(String fullKey) {
        return (String) inProcessLock.get(fullKey);
    }

    @Override
    Boolean doPutUnituserLock(String fullKey, String value, int expired) {
        if (inProcessLock.get(fullKey) == null) {
            inProcessLock.put(fullKey, value);
            return Boolean.TRUE;
        } else {
            return Boolean.FALSE;
        }
    }

    @Override
    long doGetReferenceCount(String fullKey) {
        Long value = -1L;
        if (inProcessLock.containsKey(fullKey)) {
            value = (Long) inProcessLock.get(fullKey);
        }
        return value;
    }

    @Override
    long doIncrementReferenceCount(String fullKey) {
        Long value = 1L;
        if (inProcessLock.containsKey(fullKey)) {
            value = (Long) inProcessLock.get(fullKey);
            value++;
        }
        inProcessLock.put(fullKey, value);
        return value;
    }

    @Override
    long doDecrementReferenceCount(String fullKey) {
        Long value = 0L;
        if (inProcessLock.containsKey(fullKey)) {
            value = (Long) inProcessLock.get(fullKey);
            value--;
            inProcessLock.put(fullKey, value);
            if (value == 0) {
                inProcessLock.remove(fullKey);
            }
        }
        return value;
    }

    @Override
    long doGetCellStatus(String fullKey) {
        Long value = -1L;
        if (inProcessLock.containsKey(fullKey)) {
            value = (Long) inProcessLock.get(fullKey);
        }
        return value;
    }

    @Override
    Boolean doSetCellStatus(String fullKey, long status) {
        inProcessLock.put(fullKey, status);
        return true;
    }

    @Override
    void doDeleteCellStatus(String fullKey) {
        inProcessLock.remove(fullKey);
    }

    @Override
    String doGetReadDeleteOnlyMode(String fullKey) {
        String value = null;
        if (inProcessLock.containsKey(fullKey)) {
            value = (String) inProcessLock.get(fullKey);
        }
        return value;
    }
}
