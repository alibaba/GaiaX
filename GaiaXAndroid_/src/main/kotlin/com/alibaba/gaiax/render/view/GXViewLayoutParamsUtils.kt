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

package com.alibaba.gaiax.render.view

import android.view.View
import android.widget.AbsoluteLayout
import app.visly.stretch.Layout
import com.alibaba.gaiax.render.node.GXNode

/**
 * @suppress
 */
object GXViewLayoutParamsUtils {

    fun createLayoutParams(data: GXNode, layout: Layout?, mergeX: Float = 0.0F, mergeY: Float = 0.0F): AbsoluteLayout.LayoutParams {
        return if (layout != null) {
            val width = layout.width.toInt()
            val height = layout.height.toInt()
            AbsoluteLayout.LayoutParams(width, height, layout.x.toInt() + mergeX.toInt(), layout.y.toInt() + mergeY.toInt())
        } else {
            AbsoluteLayout.LayoutParams(AbsoluteLayout.LayoutParams.WRAP_CONTENT, AbsoluteLayout.LayoutParams.WRAP_CONTENT, 0, 0)
        }
    }

    fun updateLayoutParams(view: View, layout: Layout, mergeX: Float = 0.0F, mergeY: Float = 0.0F) {
        val layoutParams = view.layoutParams

        val width = layout.width.toInt()
        val height = layout.height.toInt()

        // Update the width and height
        layoutParams.width = width
        layoutParams.height = height

        // Update layout margin information
        if (layoutParams is AbsoluteLayout.LayoutParams) {
            layoutParams.x = layout.x.toInt() + mergeX.toInt()
            layoutParams.y = layout.y.toInt() + mergeY.toInt()
        }

        // Update the layout
        view.layoutParams = layoutParams
    }
}

