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

import android.graphics.Shader
import android.graphics.drawable.GradientDrawable
import android.widget.TextView
import com.alibaba.gaiax.render.view.drawable.GXLinearColorGradientDrawable

/**
 * @suppress
 */
class GXLinearColor(val direction: GradientDrawable.Orientation, val colors: MutableList<GXColor>) {

    fun createShader(view: TextView): Shader? {
        val height = view.layoutParams.height.toFloat()
        val width = view.layoutParams.width.toFloat()
        return if (colors.size == 1) {
            val value = colors[0].value(view.context)
            val result = IntArray(2)
            result[0] = value
            result[1] = value
            GXStyleConvert.instance.createLinearGradient(width, height, direction, result)
        } else {
            val result = IntArray(colors.size)
            colors.forEachIndexed { index, color ->
                result[index] = color.value(view.context)
            }
            GXStyleConvert.instance.createLinearGradient(width, height, direction, result)
        }
    }

    fun createDrawable(): GradientDrawable {
        return if (colors.size == 1) {
            val value = colors[0].value()
            val result = IntArray(2)
            result[0] = value
            result[1] = value
            GXLinearColorGradientDrawable(direction, result)
        } else {
            val result = IntArray(colors.size)
            colors.forEachIndexed { index, color ->
                result[index] = color.value()
            }
            GXLinearColorGradientDrawable(direction, result)
        }
    }
}

