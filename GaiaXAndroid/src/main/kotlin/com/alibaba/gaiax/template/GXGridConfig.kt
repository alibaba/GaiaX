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

import android.graphics.Rect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.context.GXTemplateContext

/**
 * @suppress
 */
data class GXGridConfig(
    val data: JSONObject,
    val column: Int = 1,
    val direction: Int = RecyclerView.VERTICAL,
    /**
     * Item spacing
     */
    val itemSpacing: Int = 0,
    /**
     * Spacing perpendicular to the item spacing
     */
    val rowSpacing: Int = 0,
    val edgeInsets: Rect = Rect(0, 0, 0, 0),
    /**
     * scrollable
     */
    val scrollEnable: Boolean = false
) {

    fun column(context: GXTemplateContext): Int {
        GXRegisterCenter.instance.extensionGrid?.convert(
            GXTemplateKey.GAIAX_LAYER_COLUMN,
            context,
            this
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
            edgeInsets: String?,
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
                GXContainerConvert.edgeInsets(edgeInsets) ?: Rect(0, 0, 0, 0),
                scrollable
            )
        }

        fun create(srcConfig: GXGridConfig, data: JSONObject): GXGridConfig? {
            val gridColumn = data.getInteger(GXTemplateKey.GAIAX_LAYER_COLUMN)
            val scrollEnable = data.getBoolean(GXTemplateKey.GAIAX_LAYER_SCROLL_ENABLE)
            val edgeInsets = data.getString(GXTemplateKey.GAIAX_LAYER_EDGE_INSETS)

            var itemSpacing = data.getString(GXTemplateKey.GAIAX_LAYER_ITEM_SPACING)
            if (itemSpacing == null) {
                itemSpacing = data.getString(GXTemplateKey.GAIAX_LAYER_LINE_SPACING)
            }
            var rowSpacing = data.getString(GXTemplateKey.GAIAX_LAYER_ROW_SPACING)
            if (rowSpacing == null) {
                rowSpacing = data.getString(GXTemplateKey.GAIAX_LAYER_INTERITEM_SPACING)
            }

            return GXGridConfig(
                srcConfig.data,
                if (gridColumn != null) Math.max(gridColumn, 1) else srcConfig.column,
                srcConfig.direction,
                if (itemSpacing != null) GXContainerConvert.spacing(itemSpacing) else srcConfig.itemSpacing,
                if (rowSpacing != null) GXContainerConvert.spacing(rowSpacing) else srcConfig.rowSpacing,
                if (edgeInsets != null) GXContainerConvert.edgeInsets(edgeInsets)
                    ?: srcConfig.edgeInsets else srcConfig.edgeInsets,
                scrollEnable ?: srcConfig.scrollEnable
            )
        }
    }

    override fun toString(): String {
        return "GXGridConfig(column=$column, direction=$direction, itemSpacing=$itemSpacing, rowSpacing=$rowSpacing, edgeInsets=$edgeInsets)"
    }

}