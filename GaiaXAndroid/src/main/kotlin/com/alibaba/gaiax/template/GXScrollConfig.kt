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
data class GXScrollConfig(
    val direction: Int = LinearLayoutManager.VERTICAL,
    val itemSpacing: Int = 0,
    val edgeInsets: Rect = Rect(0, 0, 0, 0),
) {
    companion object {

        fun create(direction: String?, edgeInsets: String?, itemSpacing: String?): GXScrollConfig {
            return GXScrollConfig(
                GXContainerConvert.direction(direction ?: GXTemplateKey.GAIAX_VERTICAL),
                GXContainerConvert.spacing(itemSpacing),
                GXContainerConvert.edgeInsets(edgeInsets) ?: Rect(0, 0, 0, 0)
            )
        }

        fun create(srcConfig: GXScrollConfig, data: JSONObject): GXScrollConfig {
            val edgeInsets = data.getString(GXTemplateKey.GAIAX_LAYER_EDGE_INSETS)
            val itemSpacing = data.getString(GXTemplateKey.GAIAX_LAYER_ITEM_SPACING)
            return GXScrollConfig(
                srcConfig.direction,
                if (itemSpacing != null) GXContainerConvert.spacing(edgeInsets) else srcConfig.itemSpacing,
                if (edgeInsets != null) GXContainerConvert.edgeInsets(edgeInsets) ?: srcConfig.edgeInsets else srcConfig.edgeInsets
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