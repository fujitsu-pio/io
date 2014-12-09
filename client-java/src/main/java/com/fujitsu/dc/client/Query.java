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
import java.util.HashMap;

import com.fujitsu.dc.client.utils.StringUtils;
import com.fujitsu.dc.client.utils.Utils;

///**
// * ODataの検索条件を指定し、検索を実行するクラス.
// */
/**
 * It creates a new object of DcQuery. This class specifies the search criteria for OData, to perform the search.
 */
public class Query {
    // /** 対象となるODataManagerオブジェクト. */
    /** ODataManager object. */
    private IODataManager target;

    // /** $top値. */
    /** $top value. */
    private String queryTop = "$top";
    // /** $skip値. */
    /** $skip value. */
    private String querySkip = "$skip";
    // /** $filter値. */
    /** $filter value. */
    private String queryFilter = "$filter";
    // /** $select値. */
    /** $select value. */
    private String querySelect = "$select";
    // /** $expand値. */
    /** $expand value. */
    private String queryExpand = "$expand";
    // /** $oderby値. */
    /** $orderby value. */
    private String queryOrderby = "$orderby";
    // /** $inlinecount値. */
    /** $inlinecount value. */
    private String queryInlinecount = "$inlinecount";
    // /** 検索キーワード. */
    /** Search keyword. */
    private String queryQ = "q";
    // /** デフォルトの$top値. */
    /** The default value for $top. */
    private static final int DEFAULT_TOP = 0;
    // /** デフォルトの$skip値. */
    /** The default value for $skip. */
    private static final int DEFAULT_SKIP = 0;

    // /**
    // * ODataの$filter指定. 検索条件 を指定します。文法については、OData Documentを参照してください。
    // */
    /**
     * Specify the $filter search criteria specified for OData. For grammar, please refer to the OData Document.
     */
    private String filter = null;

    // /** ODataの$top指定. 上位何件取得するかを指定します。skip とともに使うことでページングＵＩ等に応用できます。 */
    /**
     * Specify $top-level specification of the OData to get and they can be applied to paging UI or the like be used
     * with skip.
     */
    private int top = DEFAULT_TOP;
    // /** ODataの$skip指定. 上位何件スキップして取得するかを指定します。top とともに使うことでページングＵＩ等に応用できます。 */
    /** Specify by $skip-level specification of the OData, they can be applied to paging UI such as by use with top. */
    private int skip = DEFAULT_SKIP;
    // /** OData の$selectの指定. */
    /** Designation of the OData $select. */
    private String select = null;
    // /** OData の$expandの指定. */
    /** Designation of the OData $expand. */
    private String expand = null;
    // /** OData の$orderbyの指定. */
    /** Designation of the OData $orderby. */
    private String orderby = null;
    // /** OData の$inlinecountの指定. */
    /** Designation of the OData $inlinecount. */
    private String inlinecount = null;
    // /** 全文検索用のクエリ指定. */
    /** Query specification of full-text search. */
    private String q = null;
    // /** 親オブジェクトのタイプ . 親オブジェクトIDとともに指定されたときのみ効果があります。 */
    /** Is only effective when it is specified along with the type. Parent object ID of the parent object. */
    private String parentType = null;
    // /** 親オブジェクトのID. 親オブジェクトのタイプ」とともに指定されたときのみ効果があります。 */
    /** Is only effective when it is specified with type of ID. Parent object of the parent object. */
    private String parentId = null;

    // /**
    // * コンストラクタ.
    // */
    /**
     * This is the default constructor.
     */
    public Query() {
    }

    // /**
    // * コンストラクタ.
    // * @param value 対象となるOData操作オブジェクト
    // */
    /**
     * This is the parameterized constructor with one argument and sets target value.
     * @param value OData operation object
     */
    public Query(IODataManager value) {
        this.target = value;
    }

    // /**
    // * Link先のオブジェクトを指定します.
    // * @param type リンク先のEntitySet名
    // * @param id リンク先のID
    // * @return parent情報を適用した自分自身(Queryインスタンス)
    // */
    /**
     * This method is used to specify the object of the Link destination.
     * @param type EntitySetName
     * @param id ID value
     * @return Its own object(Query instance)
     */
    public final Query parent(final String type, final String id) {
        this.parentType = type;
        this.parentId = id;
        return this;
    }

    // /**
    // * 検索を実行します.
    // * @return 検索結果のJSONオブジェクト
    // * @throws DaoException DAO例外
    // */
    /**
     * This method is used to execute the search.
     * @return JSON object of the search results
     * @throws DaoException Exception thrown
     */
    public final HashMap<String, Object> run() throws DaoException {
        return this.target.doSearch(this);
    }

    // /**
    // * 各クエリを連携し、クエリ文字列を生成します.
    // * @return 生成したクエリ文字列
    // */
    /**
     * This method generates the query string for query execution.
     * @return Query string that is generated
     */
    public final String makeQueryString() {
        ArrayList<String> al = makeQueryList();

        if (al.size() == 0) {
            return "";
        } else {
            return StringUtils.join(al, "&");
        }
    }

    // /**
    // * 各クエリ値を一旦配列に格納します.
    // * @return 各クエリ値を格納した配列
    // */
    /**
     * This method is used to create an array for making a query for each value.
     * @return An ArrayList that contains the value of each query
     */
    protected final ArrayList<String> makeQueryList() {
        ArrayList<String> al = new ArrayList<String>();
        if (this.top > 0) {
            al.add(queryTop + "=" + this.top);
        }
        if (this.skip > 0) {
            al.add(querySkip + "=" + this.skip);
        }
        if (this.filter != null) {
            al.add(queryFilter + "=" + Utils.escapeURI(this.filter));
        }
        if (this.select != null) {
            al.add(querySelect + "=" + this.select);
        }
        if (this.expand != null) {
            al.add(queryExpand + "=" + Utils.escapeURI(this.expand));
        }
        if (this.inlinecount != null) {
            al.add(queryInlinecount + "=" + Utils.escapeURI(this.inlinecount));
        }
        if (this.orderby != null) {
            al.add(queryOrderby + "=" + Utils.escapeURI(this.orderby));
        }
        if (this.q != null) {
            al.add(queryQ + "=" + Utils.escapeURI(this.q));
        }
        return al;
    }

    // /**
    // * $filterを取得.
    // * @return $filter値
    // */
    /**
     * This method returns the filter value.
     * @return $filter value
     */
    public final String filter() {
        return filter;
    }

    // /**
    // * $filterをセット.
    // * @param value $filter値
    // * @return Queryオブジェクト自身
    // */
    /**
     * This method sets the filter value and returns Query object.
     * @param value $filter value
     * @return Own Query object
     */
    public final Query filter(final String value) {
        this.filter = value;
        return this;
    }

    // /**
    // * $topを取得.
    // * @return $top値
    // */
    /**
     * This method returns the top value.
     * @return $top value
     */
    public final int top() {
        return top;
    }

    // /**
    // * $topをセット.
    // * @param value $top値
    // * @return Queryオブジェクト自身
    // */
    /**
     * This method sets the top value and returns Query object.
     * @param value $top value
     * @return Own Query object
     */
    public final Query top(final int value) {
        this.top = value;
        return this;
    }

    // /**
    // * $skipを取得.
    // * @return $skip値
    // */
    /**
     * This method returns the skip value.
     * @return $skip value
     */
    public final int skip() {
        return skip;
    }

    // /**
    // * $skipをセット.
    // * @param value $skip値
    // * @return Queryオブジェクト自身
    // */
    /**
     * This method sets the skip value and returns Query object.
     * @param value $skip value
     * @return Own Query object
     */
    public final Query skip(final int value) {
        this.skip = value;
        return this;
    }

    // /**
    // * $selectを取得.
    // * @return $select値
    // */
    /**
     * This method returns the select value.
     * @return $select value
     */
    public final String select() {
        return select;
    }

    // /**
    // * $selectをセット.
    // * @param value $select値
    // * @return Queryオブジェクト自身
    // */
    /**
     * This method is used to set the $select and return Query object.
     * @param value $select value
     * @return Own Query object
     */
    public final Query select(final String value) {
        String[] values = value.split(",");
        ArrayList<String> ar = new ArrayList<String>();
        for (int i = 0; i < values.length; i++) {
            ar.add(i, Utils.escapeURI(values[i]));
        }
        this.select = StringUtils.join(ar, ",");
        return this;
    }

    // /**
    // * $expandを取得.
    // * @return $expand値
    // */
    /**
     * This method returns the expand value.
     * @return $expand value
     */
    public final String expand() {
        return expand;
    }

    // /**
    // * $expandをセット.
    // * @param value $expand値
    // * @return Queryオブジェクト自身
    // */
    /**
     * This method is used to set the $expand and return Query object.
     * @param value $expand value
     * @return Own Query object
     */
    public final Query expand(final String value) {
        this.expand = value;
        return this;
    }

    // /**
    // * $oderbyを取得.
    // * @return $oderby値
    // */
    /**
     * This method returns the orderby value.
     * @return $oderby value
     */
    public final String orderby() {
        return orderby;
    }

    // /**
    // * $oderbyをセット.
    // * @param value $orderby値
    // * @return Queryオブジェクト自身
    // */
    /**
     * This method is used to set the $orderby and return Query object.
     * @param value $orderby value
     * @return Own Query object
     */
    public final Query orderby(final String value) {
        this.orderby = value;
        return this;
    }

    // /**
    // * $inlinecountを取得.
    // * @return $inlinecount値
    // */
    /**
     * This method returns the inlinecount value.
     * @return $inlinecount value
     */
    public final String inlinecount() {
        return inlinecount;
    }

    // /**
    // * $inlinecountをセット.
    // * @param value $inlinecount値
    // * @return Queryオブジェクト自身
    // */
    /**
     * This method is used to set the $inlinecount and return Query object.
     * @param value $inlinecount value
     * @return Own Query object
     */
    public final Query inlinecount(final String value) {
        this.inlinecount = value;
        return this;
    }

    // /**
    // * 検索キーワードを取得.
    // * @return 検索キーワード
    // */
    /**
     * This method is used to get the search keyword.
     * @return Search
     */
    public final String q() {
        return q;
    }

    // /**
    // * 検索キーワードをセット.
    // * @param value 検索キーワード
    // * @return Queryオブジェクト自身
    // */
    /**
     * This method is used to set the search keyword and return Query object.
     * @param value Search keyword
     * @return Own Query object
     */
    public final Query q(final String value) {
        this.q = value;
        return this;
    }

    // /**
    // * 親EntitySetを取得.
    // * @return 親EntitySet
    // */
    /**
     * This method gets the parent EntitySet.
     * @return Parent EntitySet
     */
    public final String parentType() {
        return parentType;
    }

    // /**
    // * 親EntitySetをセット.
    // * @param value 親EntitySet名
    // * @return Queryオブジェクト自身
    // */
    /**
     * This method is used to set the parent EntitySet and return Query object.
     * @param value Parent EntitySet
     * @return Own Query object
     */
    public final Query parentType(final String value) {
        this.parentType = value;
        return this;
    }

    // /**
    // * 親EntitySetのID値を取得.
    // * @return 親EntitySetのID値
    // */
    /**
     * This method gets the ID value of parent EntitySet.
     * @return ID value of parent EntitySet
     */
    public final String parentId() {
        return parentId;
    }

    // /**
    // * 親EntitySetのID値をセット.
    // * @param value 親EntitySetのID値
    // * @return Queryオブジェクト自身
    // */
    /**
     * This method is used to set the ID value of parent EntitySet and return Query object.
     * @param value ID value of parent EntitySet
     * @return Own Query object
     */
    public final Query parentId(final String value) {
        this.parentId = value;
        return this;
    }
}
