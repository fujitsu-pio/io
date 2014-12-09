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
/*global dcc:false
 */

/*function dcc(){}*/
/**
 * It initializes dcc.DcClass.
 * @class This class represents namespace for all other classes.
 * @constructor
 */
dcc.DcClass = function() {
};

/**
 * This method is used for inheriting one class in another.
 * @param {Object} Child
 * @param {Object} Parent
 */
dcc.DcClass.inherit = function(Child, Parent) {
  var F = function(){};
  F.prototype = Parent.prototype;
  Child.prototype = new F();
  Child.prototype.constructor = Child;
};

