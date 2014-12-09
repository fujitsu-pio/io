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
 * It creates a new object dcc.box.odata.schema.EntityLink.
 * @class This class represents the EntityLink object.
 * @constructor
 * @augments dcc.AbstractODataContext
 * @param {dcc.Accessor} as Accessor
 * @param {String} path
 */
dcc.box.odata.schema.EntityLink = function(as, path) {
    this.initializeProperties(this, as, path);
};
dcc.DcClass.inherit(dcc.box.odata.schema.EntityLink, dcc.AbstractODataContext);

/**
 * This method initializes the properties of this class.
 * @param {dcc.box.odata.schema.EntityLink} self
 * @param {dcc.Accessor} as Accessor
 * @param {String} path URL
 */
dcc.box.odata.schema.EntityLink.prototype.initializeProperties = function(self, as, path) {
    this.uber = dcc.AbstractODataContext.prototype;
    this.uber.initializeProperties(self, as);

    if (as !== undefined) {
        self.accessor = as.clone();
    }

    /** Class name in camel case. */
    self.CLASSNAME = "";
    /** The path of the collection. */
    self.url = path;

};

/**
 * This method gets the URL for the collection.
 * @return {String} URL string
 */
dcc.box.odata.schema.EntityLink.prototype.getPath = function() {
    return this.url;
};

/**
 * This method gets the Odata key.
 * @return {String} OData key
 */
dcc.box.odata.schema.EntityLink.prototype.getKey = function() {
    return "";
};

/**
 * This method gets the class name.
 * @return {String} OData class name
 */
dcc.box.odata.schema.EntityLink.prototype.getClassName = function() {
    return this.CLASSNAME;
};

