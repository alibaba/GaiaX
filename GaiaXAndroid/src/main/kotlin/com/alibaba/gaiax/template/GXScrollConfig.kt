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
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import com.alibaba.fastjson.JSONObject

/**
 * @suppress
 */
data class GXScrollConfig(
    val data: JSONObject,
    val direction: Int = LinearLayoutManager.VERTICAL,
    val itemSpacing: Int = 0,
    val edgeInsets: Rect = Rect(0, 0, 0, 0),
    var gravity: Int = Gravity.TOP
) {
    companion object {

        fun create(
            data: JSONObject,
            direction: String?,
            edgeInsets: String?,
            itemSpacing: String?,
            gravity: Int?
        ): GXScrollConfig {
            return GXScrollConfig(
                data,
                GXContainerConvert.direction(direction ?: GXTemplateKey.GAIAX_VERTICAL),
                GXContainerConvert.spacing(itemSpacing),
                GXContainerConvert.edgeInsets(edgeInsets) ?: Rect(0, 0, 0, 0),
                gravity ?: Gravity.TOP
            )
        }

        fun create(srcConfig: GXScrollConfig, data: JSONObject): GXScrollConfig {
            val edgeInsets = data.getString(GXTemplateKey.GAIAX_LAYER_EDGE_INSETS)
            var itemSpacing = data.getString(GXTemplateKey.GAIAX_LAYER_ITEM_SPACING)
            if (itemSpacing == null) {
                itemSpacing = data.getString(GXTemplateKey.GAIAX_LAYER_LINE_SPACING)
            }
            return GXScrollConfig(
                srcConfig.data,
                srcConfig.direction,
                if (itemSpacing != null) GXContainerConvert.spacing(itemSpacing) else srcConfig.itemSpacing,
                if (edgeInsets != null) GXContainerConvert.edgeInsets(edgeInsets)
                    ?: srcConfig.edgeInsets else srcConfig.edgeInsets,
                srcConfig.gravity
            )
        }
    }

    override fun toString(): String {
        return "GXScrollConfig(direction=$direction, itemSpacing=$itemSpacing, edgeInsets=$edgeInsets)"
    }

    val isVertical
        get():Boolean {
            return direction == LinearLayoutManager.VERTICAL
        }

    val isHorizontal
        get():Boolean {
            return direction == LinearLayoutManager.HORIZONTAL
        }
}