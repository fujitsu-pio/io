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
package com.fujitsu.dc.client.http;

///**
// * HTTPメソッド定義.
// */
/**
 * This class is used for HTTP method definition.
 */
public class HttpMethods {
    /** POST. */
    public static final String POST = "POST";
    /** GET. */
    public static final String GET = "GET";
    /** PUT. */
    public static final String PUT = "PUT";
    /** DELETE. */
    public static final String DELETE = "DELETE";
    /** MKCOL. */
    public static final String MKCOL = "MKCOL";
    /** ACL. */
    public static final String ACL = "ACL";
    /** PROPFIND. */
    public static final String PROPFIND = "PROPFIND";
    /** PROPPATCH. */
    public static final String PROPPATCH = "PROPPATCH";
    /** MERGE. */
    public static final String MERGE = "MERGE";

    /**
     * This is the default constructor marked as private to disallow its direct instantiation by other classes.
     */
    private HttpMethods() {
    }
}
