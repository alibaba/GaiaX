/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.gaiax.template

import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.context.GXTemplateContext

/**
 * @suppress
 */
data class GXGridConfig(
    val data: JSONObject,
    private val columnForTemplate: Int,
    private val directionForTemplate: Int,
    private val itemSpacingForTemplate: Int,
    private val rowSpacingForTemplate: Int,
    private val scrollEnableForTemplate: Boolean
) {

    private var columnForExtend: Int? = null
    private var directionForExtend: Int? = null
    private var itemSpacingForExtend: Int? = null
    private var rowSpacingForExtend: Int? = null
    private var scrollEnableForExtend: Boolean? = null

    fun reset() {
        columnForExtend = null
        directionForExtend = null
        itemSpacingForExtend = null
        rowSpacingForExtend = null
        scrollEnableForExtend = null
    }

    val column: Int
        get() {
            return columnForExtend ?: columnForTemplate
        }

    val direction: Int
        get() {
            return directionForExtend ?: directionForTemplate
        }

    val itemSpacing: Int
        get() {
            return itemSpacingForExtend ?: itemSpacingForTemplate
        }

    val rowSpacing: Int
        get() {
            return rowSpacingForExtend ?: rowSpacingForTemplate
        }

    val scrollEnable: Boolean
        get() {
            return scrollEnableForExtend ?: scrollEnableForTemplate
        }

    fun column(context: GXTemplateContext): Int {
        GXRegisterCenter.instance.extensionGrid?.convert(
            GXTemplateKey.GAIAX_LAYER_COLUMN, context, this
        )?.let {
            return it as Int
        }
        return column
    }

    val isVertical
        get():Boolean {
            return direction == LinearLayoutManager.VERTICAL
        }

    val isHorizontal
        get():Boolean {
            return direction == LinearLayoutManager.HORIZONTAL
        }

    companion object {

        fun create(
            data: JSONObject,
            direction: String?,
            itemSpacing: String?,
            rowSpacing: String?,
            column: Int,
            scrollable: Boolean
        ): GXGridConfig {
            return GXGridConfig(
                data,
                Math.max(column, 1),
                GXContainerConvert.direction(direction ?: GXTemplateKey.GAIAX_VERTICAL),
                GXContainerConvert.spacing(itemSpacing),
                GXContainerConvert.spacing(rowSpacing),
                scrollable
            )
        }
    }

    fun updateByExtend(extendCssData: JSONObject) {
        val gridColumn = extendCssData.getInteger(GXTemplateKey.GAIAX_LAYER_COLUMN)
        val scrollEnable = extendCssData.getBoolean(GXTemplateKey.GAIAX_LAYER_SCROLL_ENABLE)

        var itemSpacing = extendCssData.getString(GXTemplateKey.GAIAX_LAYER_ITEM_SPACING)
        if (itemSpacing == null) {
            itemSpacing = extendCssData.getString(GXTemplateKey.GAIAX_LAYER_LINE_SPACING)
        }
        var rowSpacing = extendCssData.getString(GXTemplateKey.GAIAX_LAYER_ROW_SPACING)
        if (rowSpacing == null) {
            rowSpacing = extendCssData.getString(GXTemplateKey.GAIAX_LAYER_INTERITEM_SPACING)
        }
        if (gridColumn != null) {
            columnForExtend = Math.max(gridColumn, 1)
        }
        if (itemSpacing != null) {
            itemSpacingForExtend = GXContainerConvert.spacing(itemSpacing)
        }
        if (rowSpacing != null) {
            rowSpacingForExtend = GXContainerConvert.spacing(rowSpacing)
        }
        if (scrollEnable != null) {
            scrollEnableForExtend = scrollEnable
        }
    }
}
