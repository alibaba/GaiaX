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
import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONObject

/**
 * @suppress
 */
data class GXScrollConfig(
    val data: JSONObject,
    private var directionForTemplate: Int,
    private var itemSpacingForTemplate: Int,
    private var edgeInsetsForTemplate: Rect,
    private var gravityForTemplate: Int
) {

    private var directionForExtend: Int? = null
    private var itemSpacingForExtend: Int? = null
    private var gravityForExtend: Int? = null

    fun reset() {
        directionForExtend = null
        itemSpacingForExtend = null
        gravityForExtend = null
    }

    val edgeInsets: Rect
        get() {
            return edgeInsetsForTemplate
        }

    val direction: Int
        get() {
            return directionForExtend ?: directionForTemplate
        }

    val itemSpacing: Int
        get() {
            return itemSpacingForExtend ?: itemSpacingForTemplate
        }

    val gravity: Int
        get() {
            return gravityForExtend ?: gravityForTemplate
        }

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
                GXContainerConvert.edgeInsets2(edgeInsets) ?: Rect(0, 0, 0, 0),
                gravity ?: Gravity.TOP
            )
        }
    }

    fun updateByExtend(extendCssData: JSONObject) {
        val itemSpacing = extendCssData.getString(GXTemplateKey.GAIAX_LAYER_ITEM_SPACING)
        if (itemSpacing != null) {
            itemSpacingForExtend = GXContainerConvert.spacing(itemSpacing)
        }
    }

    val isVertical
        get():Boolean {
            return direction == LinearLayoutManager.VERTICAL
        }

    val isHorizontal
        get():Boolean {
            return direction == LinearLayoutManager.HORIZONTAL
        }

    override fun toString(): String {
        return "GXScrollConfig(direction=$direction, itemSpacing=$itemSpacing)"
    }
}
