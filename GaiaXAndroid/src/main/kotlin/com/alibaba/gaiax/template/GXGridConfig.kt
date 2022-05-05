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
import com.alibaba.fastjson.JSONObject

/**
 * @suppress
 */
data class GXGridConfig(
    val column: Int = 1,
    val direction: Int = LinearLayoutManager.VERTICAL,
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
    val scrollEnable: Boolean = false,
) {


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
            direction: String?,
            edgeInsets: String?,
            itemSpacing: String?,
            rowSpacing: String?,
            column: Int,
            scrollable: Boolean
        ): GXGridConfig {
            return GXGridConfig(
                column,
                GXContainerConvert.direction(direction ?: GXTemplateKey.GAIAX_VERTICAL),
                GXContainerConvert.spacing(itemSpacing),
                GXContainerConvert.spacing(rowSpacing),
                GXContainerConvert.edgeInsets(edgeInsets) ?: Rect(0, 0, 0, 0),
                scrollable
            )
        }

        fun create(srcConfig: GXGridConfig, extendCache: JSONObject): GXGridConfig? {
            val gridColumn = extendCache.getInteger(GXTemplateKey.GAIAX_LAYER_COLUMN)
            val scrollEnable = extendCache.getBoolean(GXTemplateKey.GAIAX_LAYER_SCROLL_ENABLE)
            val edgeInsets = extendCache.getString(GXTemplateKey.GAIAX_LAYER_EDGE_INSETS)
            val itemSpacing = extendCache.getString(GXTemplateKey.GAIAX_LAYER_ITEM_SPACING)
            val rowSpacing = extendCache.getString(GXTemplateKey.GAIAX_LAYER_ROW_SPACING)

            return GXGridConfig(
                gridColumn ?: srcConfig.column,
                srcConfig.direction,
                if (itemSpacing != null) GXContainerConvert.spacing(itemSpacing) else srcConfig.itemSpacing,
                if (rowSpacing != null) GXContainerConvert.spacing(rowSpacing) else srcConfig.rowSpacing,
                if (edgeInsets != null) GXContainerConvert.edgeInsets(edgeInsets)
                    ?: srcConfig.edgeInsets else srcConfig.edgeInsets,
                scrollEnable ?: srcConfig.scrollEnable,
            )
        }
    }

    override fun toString(): String {
        return "GXGridConfig(column=$column, direction=$direction, itemSpacing=$itemSpacing, rowSpacing=$rowSpacing, edgeInsets=$edgeInsets)"
    }

}