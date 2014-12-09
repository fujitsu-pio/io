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
 * It creates a new object dcc.http.DcResponseAsThenable.
 * @class This class is used to handle DAV Response in scenarios of then able request.
 * @constructor
 */
dcc.http.DcResponseAsThenable = function(){
  this.CLASSNAME = "DcResponseAsThenable";
  this.resolvedValue = null;
  this.errorValue = null;
  this.onFulfilled = null;
  this.onRejected = null;
  this.resolve = function(obj){
    this.resolvedValue = obj;
    if (this.onFulfilled){
      this.onFulfilled(obj);
    }

  };
  this.reject = function(obj){
    this.errorValue = obj;
    if (this.onRejected){
      this.onRejected(obj);
    }
  };
};

/**
 * This method is responsible for calling fulfilled/rejected actions.
 */
dcc.http.DcResponseAsThenable.prototype = {
    then: function(onFulfilled, onRejected){
      this.onFulfilled = onFulfilled;
      this.onRejected = onRejected;
    }
};

/**
 * This method is responsible for returning class name.
 */
dcc.http.DcResponseAsThenable.prototype.getClassName  = function(){
  return this.CLASSNAME;
};
