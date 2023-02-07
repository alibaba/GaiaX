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

import android.view.Gravity
import androidx.recyclerview.widget.LinearLayoutManager
import com.alibaba.fastjson.JSONObject

/**
 * @suppress
 */
data class GXScrollConfig(
    val data: JSONObject,
    val direction: Int = LinearLayoutManager.VERTICAL,
    val itemSpacing: Int = 0,
    val gravity: Int = Gravity.TOP
) {

    private var _direction: Int? = null
    private var _itemSpacing: Int? = null
    private var _gravity: Int? = null

    fun reset() {
        _direction = null
        _itemSpacing = null
        _gravity = null
    }

    val directionFinal: Int
        get() {
            return _direction ?: direction
        }

    val itemSpacingFinal: Int
        get() {
            return _itemSpacing ?: itemSpacing
        }

    val gravityFinal: Int
        get() {
            return _gravity ?: gravity
        }

    companion object {

        fun create(
            data: JSONObject, direction: String?, itemSpacing: String?, gravity: Int?
        ): GXScrollConfig {
            return GXScrollConfig(
                data,
                GXContainerConvert.direction(direction ?: GXTemplateKey.GAIAX_VERTICAL),
                GXContainerConvert.spacing(itemSpacing),
                gravity ?: Gravity.TOP
            )
        }
    }

    fun updateByExtend(extendCssData: JSONObject) {
        var itemSpacing = extendCssData.getString(GXTemplateKey.GAIAX_LAYER_ITEM_SPACING)
        if (itemSpacing == null) {
            itemSpacing = extendCssData.getString(GXTemplateKey.GAIAX_LAYER_LINE_SPACING)
        }
        if (itemSpacing != null) {
            _itemSpacing = GXContainerConvert.spacing(itemSpacing)
        }
    }

    override fun toString(): String {
        return "GXScrollConfig(direction=$directionFinal, itemSpacing=$itemSpacingFinal)"
    }

    val isVertical
        get():Boolean {
            return directionFinal == LinearLayoutManager.VERTICAL
        }

    val isHorizontal
        get():Boolean {
            return directionFinal == LinearLayoutManager.HORIZONTAL
        }
}
