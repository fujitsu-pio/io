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

/**
 * Principal Interface http://tools.ietf.org/html/rfc3744#section-5.5.1.
 */
public interface Principal {
    /** constant expressing DAV:all pseudo-principal defined in rfc3744#section-5.5.1. */
    Principal ALL = new Principal() {
    };
    /** constant expressing DAV:authenticated pseudo-principal defined in rfc3744#section-5.5.1. */
    Principal AUTHENTICATED = new Principal() {
    };
    /** constant expressing DAV:unauthenticated pseudo-principal defined in rfc3744#section-5.5.1. */
    Principal UNAUTHENTICATED = new Principal() {
    };
}
