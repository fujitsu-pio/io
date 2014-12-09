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
package com.fujitsu.dc.core.model;


import java.util.List;

import org.core4j.Enumerable;
import org.joda.time.LocalDateTime;
import org.odata4j.core.OEntity;
import org.odata4j.edm.EdmAnnotation;
import org.odata4j.edm.EdmEntityType;
import org.odata4j.edm.EdmProperty;
import org.odata4j.edm.EdmSimpleType;

import com.fujitsu.dc.core.model.ctl.Common;
import com.fujitsu.dc.core.model.ctl.CtlSchema;
import com.fujitsu.dc.core.odata.OEntityWrapper;


/**
 * Boxのモデル.
 */
public final class Box {
    private Cell cell;
    private String schema;
    private String name;
    private String id;
    private long published;

    /**
     * デフォルトボックス名.
     */
    public static final String DEFAULT_BOX_NAME = "__";

    /**
     * Constructor.
     * @param cell cellオブジェクト
     * @param entity OEntity オブジェクト
     */
    public Box(final Cell cell, final OEntity entity) {
        this.cell = cell;
        if (entity == null) {
            // デフォルトボックス用の処理
            this.name = Box.DEFAULT_BOX_NAME;
            // デフォルトボックスのスキーマURLは自セルのURLになる
            this.schema = cell.getUrl();
            // デフォルトボックの内部IDはセルのIDと一緒にする。
            this.id = cell.getId();
            return;
        }
        this.name = (String) entity.getProperty("Name").getValue();
        this.schema = (String) entity.getProperty(P_SCHEMA.getName()).getValue();
        if (entity instanceof OEntityWrapper) {
            OEntityWrapper oew = (OEntityWrapper) entity;
            this.id = oew.getUuid();
        }
        LocalDateTime dateTime = (LocalDateTime) entity.getProperty(Common.P_PUBLISHED.getName()).getValue();
        this.published = dateTime.toDateTime().getMillis();
    }

    /**
     * コンストラクタ.
     * @param cell cellオブジェクト
     * @param name Box名
     * @param schema Boxスキーマ
     * @param id Boxの内部ID
     * @param published 作成日時
     */
    public Box(final Cell cell, final String name,
            final String schema, final String id, final Long published) {
        this.cell = cell;
        this.name = name;
        this.schema = schema;
        this.id = id;
        this.published = published;
    }

    /**
     * このBoxが属すCellを返す.
     * @return Cell
     */
    public Cell getCell() {
        return this.cell;
    }


    /**
     * このBoxのパス名を返す.
     * @return パス名
     */
    public String getName() {
        return this.name;
    }

    /**
     * このBoxのSchema URLを返す.
     * @return Schema URL文字列
     */
    public String getSchema() {
        return this.schema;
    }
    /**
     * このBoxの管理用内部IDを返す.
     * @return ID文字列
     */
    public String getId() {
        return this.id;
    }

    /**
     * @param cell the cell to set
     */
    public void setCell(Cell cell) {
        this.cell = cell;
    }

    /**
     * @param schema the schema to set
     */
    public void setSchema(String schema) {
        this.schema = schema;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return the published
     */
    public long getPublished() {
        return published;
    }

    // スキーマ情報

    /**
     * Edm.Entity Type名.
     */
    public static final String EDM_TYPE_NAME = "Box";

    /**
     * Schema用のAnnotationを取得する.
     * @param name UK名
     * @return Annotationのリスト
     */
    public static List<EdmAnnotation<?>> createSchemaAnnotation(final String name) {
        List<EdmAnnotation<?>> schemaAnnotation = CtlSchema.createNamedUkAnnotation(name);
        schemaAnnotation.add(Common.createFormatUriAnnotation());
        return schemaAnnotation;
    }

    /**
     * Schema プロパティの定義体.
     */
    public static final EdmProperty.Builder P_SCHEMA = EdmProperty.newBuilder("Schema").setType(EdmSimpleType.STRING)
            .setAnnotations(createSchemaAnnotation("uk_box_schema"))
            .setNullable(true).setDefaultValue("null");
    /**
     * Nameプロパティの定義体.
     */
    public static final EdmProperty.Builder P_PATH_NAME = EdmProperty.newBuilder("Name")
            .setAnnotations(Common.DC_FORMAT_NAME)
            .setNullable(false)
            .setType(EdmSimpleType.STRING);
    /**
     * EntityType Builder.
     */
    public static final EdmEntityType.Builder EDM_TYPE_BUILDER = EdmEntityType.newBuilder()
            .setNamespace(Common.EDM_NS_CELL_CTL).setName(EDM_TYPE_NAME)
            .addProperties(Enumerable.create(P_PATH_NAME, P_SCHEMA,
                    Common.P_PUBLISHED, Common.P_UPDATED).toList())
            .addKeys(P_PATH_NAME.getName());
}
