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
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.context.GXTemplateContext

/**
 * @suppress
 */
data class GXGridConfig(
    val data: JSONObject, val column: Int = 1, val direction: Int = RecyclerView.VERTICAL,
    /**
     * Item spacing
     */
    val itemSpacing: Int = 0,
    /**
     * Spacing perpendicular to the item spacing
     */
    val rowSpacing: Int = 0,
    /**
     * scrollable
     */
    val scrollEnable: Boolean = false
) {

    private var _column: Int? = null
    private var _direction: Int? = null
    private var _itemSpacing: Int? = null
    private var _rowSpacing: Int? = null
    private var _scrollEnable: Boolean? = null

    fun reset() {
        _column = null
        _direction = null
        _itemSpacing = null
        _rowSpacing = null
        _scrollEnable = null
    }

    private val columnFinal: Int
        get() {
            return _column ?: column
        }

    val directionFinal: Int
        get() {
            return _direction ?: direction
        }

    val itemSpacingFinal: Int
        get() {
            return _itemSpacing ?: itemSpacing
        }

    val rowSpacingFinal: Int
        get() {
            return _rowSpacing ?: rowSpacing
        }

    val scrollEnableFinal: Boolean
        get() {
            return _scrollEnable ?: scrollEnable
        }

    fun column(context: GXTemplateContext): Int {
        GXRegisterCenter.instance.extensionGrid?.convert(
            GXTemplateKey.GAIAX_LAYER_COLUMN, context, this
        )?.let {
            return it as Int
        }
        return columnFinal
    }

    val isVertical
        get():Boolean {
            return directionFinal == LinearLayoutManager.VERTICAL
        }

    val isHorizontal
        get():Boolean {
            return directionFinal == LinearLayoutManager.HORIZONTAL
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
            _column = Math.max(gridColumn, 1)
        }
        if (itemSpacing != null) {
            _itemSpacing = GXContainerConvert.spacing(itemSpacing)
        }
        if (rowSpacing != null) {
            _rowSpacing = GXContainerConvert.spacing(rowSpacing)
        }
        if (scrollEnable != null) {
            _scrollEnable = scrollEnable
        }
    }


    override fun toString(): String {
        return "GXGridConfig(column=$column, direction=$direction, itemSpacing=$itemSpacing, rowSpacing=$rowSpacing)"
    }


}
