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

///**
//* @class ODataの検索条件を指定し、検索を実行するクラス.
//* @constructor
//*/
/**
 * It creates a new object dcc.box.odata.DcQuery.
 * @class This class specifies the search criteria for OData, to perform the search.
 * @constructor
 * @param {Object} obj
 */
dcc.box.odata.DcQuery = function(obj) {
  this.initializeProperties(obj);
};

///**
//* プロパティを初期化する.
//* @param {dcc.box.odata.DcQuery} obj
//*/
/**
 * This method initializes the properties of this class.
 * @param {dcc.box.odata.DcQuery} obj
 */
dcc.box.odata.DcQuery.prototype.initializeProperties = function(obj) {
  this.target = obj;
};

///**
//* 検索を実行します.
//* @return {Object} 検索結果のJSONオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method is used to execute the search.
 * @return {Object} JSON object of the search results
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.DcQuery.prototype.run = function(options) {
  //if (options !== undefined) {
    return this.target.doSearch(this,options);
    //  this.target.doSearch(this, options);
    /*this.target.doSearch(this, {
      success: function(resp){
        if (options.success !== undefined) {
          var responseBody = resp.bodyAsJson();
          var json = responseBody.d.results;
          options.success(json);
        }
      },
      error: function(resp){
        if (options.error !== undefined) {
          options.error(resp);
        }
      },
      complete: function(resp) {
        if (options.complete !== undefined) {
          options.complete(resp);
        }
      }
    });*/
  /*} else {
    return this.target.doSearch(this);
  }*/
};


///**
//* 検索を実行します.
//* @return {dcc.box.odata.ODataResponse} 検索結果のJSONオブジェクト
//* @throws {ClientException} DAO例外
//*/
/**
 * This method executes the search and returns result as response.
 * @param {Object} options optional parameters
 * @return {dcc.box.odata.ODataResponse} JSON object of the search results
 * @throws {dcc.ClientException} DAO exception
 */
dcc.box.odata.DcQuery.prototype.runAsResponse = function(options) {
/*  if (options !== undefined) {
    this.target.doSearchAsResponse(this,options);
  } else {*/
    return this.target.doSearchAsResponse(this, options);
  //}
};

///**
//* 各クエリを連携し、クエリ文字列を生成します.
//* @return {String} 生成したクエリ文字列
//*/
/**
 * This method generates the query string for query execution.
 * @return {String} Query string that is generated
 */
dcc.box.odata.DcQuery.prototype.makeQueryString = function() {
  var al = this.makeQueryList();

  if (al.length === 0) {
    return "";
  } else {
    return al.join("&");
  }
};

///**
//* 各クエリ値を一旦配列に格納します.
//* @return {?} 各クエリ値を格納した配列
//*/
/**
 * This method is used to create an array for making a query for each value.
 * @return {String[]} An array that contains the value of each query
 */
dcc.box.odata.DcQuery.prototype.makeQueryList = function() {
  var al = [];
  if (this.topVal > 0)  {
    al.push("$top=" + this.topVal);
  }
  if (this.skipVal > 0) {
    al.push("$skip=" + this.skipVal);
  }
  if ((this.filterVal !== null) && (this.filterVal !== undefined)){
    // escape()でシングルクォートをエスケープする
    var filterEnc = encodeURI(this.filterVal);
    al.push("$filter=" + filterEnc.replace(/'/g, "%27"));
  }
  if ((this.selectVal !== null) && (this.selectVal !== undefined)) {
    al.push("$select=" + this.selectVal);
  }
  if ((this.expandVal !== null) && (this.expandVal !== undefined)) {
    al.push("$expand=" + encodeURI(this.expandVal));
  }
  if ((this.inlinecountVal !== null) && (this.inlinecountVal !== undefined)) {
    al.push("$inlinecount=" + encodeURI(this.inlinecountVal));
  }
  if ((this.orderbyVal !== null) && (this.orderbyVal !== undefined)) {
    al.push("$orderby=" + encodeURI(this.orderbyVal));
  }
  if ((this.qVal !== null) && (this.qVal !== undefined)) {
    al.push("q=" + encodeURI(this.qVal));
  }
  return al;
};


///**
//* $filterをセット.
//* @param {String} value $filter値
//* @return {dcc.box.odata.DcQuery} Queryオブジェクト自身
//*/
/**
 * This method is used to set the $ filter.
 * @param {String} value $filter value
 * @return {dcc.box.odata.DcQuery} Query object
 */
dcc.box.odata.DcQuery.prototype.filter = function(value) {
  this.filterVal = value;
  return this;
};

///**
//* $topをセット.
//* @param {Number} value $top値
//* @return {dcc.box.odata.DcQuery} Queryオブジェクト自身
//*/
/**
 * This method is used to set the $ top.
 * @param {Number} value $top value
 * @return {dcc.box.odata.DcQuery} Query object
 */
dcc.box.odata.DcQuery.prototype.top = function(value) {
  this.topVal = value;
  return this;
};

///**
//* $skipをセット.
//* @param {Number} value $skip値
//* @return {dcc.box.odata.DcQuery} Queryオブジェクト自身
//*/
/**
 * This method is used to set the $ skip.
 * @param {Number} value $skip value
 * @return {dcc.box.odata.DcQuery} Query object
 */
dcc.box.odata.DcQuery.prototype.skip = function(value) {
  this.skipVal = value;
  return this;
};

///**
//* $selectをセット.
//* @param {String} value $select値
//* @return {dcc.box.odata.DcQuery} Queryオブジェクト自身
//*/
/**
 * This method is used to set the $ select.
 * @param {String} value $select value
 * @return {dcc.box.odata.DcQuery} Query object
 */
dcc.box.odata.DcQuery.prototype.select = function(value) {
  var values = value.split(",");
  var ar = [];
  for (var i = 0;i < values.length; i++) {
    ar[i] = encodeURI(values[i]);
  }
  this.selectVal = ar.join(",");
  return this;
};

///**
//* $expandをセット.
//* @param {String} value $expand値
//* @return {dcc.box.odata.DcQuery} Queryオブジェクト自身
//*/
/**
 * This method is used to set the $ expand.
 * @param {String} value $expand value
 * @return {dcc.box.odata.DcQuery} Query object
 */
dcc.box.odata.DcQuery.prototype.expand = function(value) {
  this.expandVal = value;
  return this;
};

///**
//* $oderbyをセット.
//* @param {String} value $orderby値
//* @return {dcc.box.odata.DcQuery} Queryオブジェクト自身
//*/
/**
 * This method is used to set the $ orderby.
 * @param {String} value $orderby value
 * @return {dcc.box.odata.DcQuery} Query object
 */
dcc.box.odata.DcQuery.prototype.orderby = function(value) {
  this.orderbyVal = value;
  return this;
};

///**
//* $inlinecountをセット.
//* @param {String} value $inlinecount値
//* @return {dcc.box.odata.DcQuery} Queryオブジェクト自身
//*/
/**
 * This method is used to set the $ inlinecount.
 * @param {String} value $inlinecount value
 * @return {dcc.box.odata.DcQuery} Query object
 */
dcc.box.odata.DcQuery.prototype.inlinecount = function(value) {
  this.inlinecountVal = value;
  return this;
};

///**
//* 検索キーワードをセット.
//* @param {String} value 検索キーワード
//* @return {dcc.box.odata.DcQuery} Queryオブジェクト自身
//*/
/**
 * This method sets the search keyword.
 * @param {String} value Search keyword
 * @return {dcc.box.odata.DcQuery} Query object
 */
dcc.box.odata.DcQuery.prototype.q = function(value) {
  this.qVal = value;
  return this;
};

///**
//* 親EntitySetをセット.
//* @param {String} value 親EntitySet名
//* @return {dcc.box.odata.DcQuery} Queryオブジェクト自身
//*/
/**
 * This method sets the parent EntitySet.
 * @param {String} value EntitySet value
 * @return {dcc.box.odata.DcQuery} Query object
 */
dcc.box.odata.DcQuery.prototype.parentType = function(value) {
  this.parentTypeVal = value;
  return this;
};

///**
//* 親EntitySetのID値をセット.
//* @param {String} value 親EntitySetのID値
//* @return {dcc.box.odata.DcQuery} Queryオブジェクト自身
//*/
/**
 * This method sets the ID value of the parent EntitySet.
 * @param {String} value EntitySet ID value
 * @return {dcc.box.odata.DcQuery} Query object
 */
dcc.box.odata.DcQuery.prototype.parentId = function(value) {
  this.parentIdVal = value;
  return this;
};
