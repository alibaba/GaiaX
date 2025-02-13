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

import android.content.Context
import android.view.View
import com.alibaba.gaiax.render.view.basic.*
import com.alibaba.gaiax.render.view.container.GXGridView
import com.alibaba.gaiax.render.view.container.GXScrollView
import com.alibaba.gaiax.render.view.container.slider.GXSliderView

/**
 * View factory class, used to produce the View corresponding to the Type tag
 * @suppress
 */
object GXViewFactory {

    internal val viewCreatorSupport: MutableMap<String, (Context) -> View> = mutableMapOf()

    @Suppress("UNCHECKED_CAST")
    fun <T : View> createView(
        context: Context, type: String, customViewClass: String? = null
    ): T {

        if (viewCreatorSupport.containsKey(type)) {
            return viewCreatorSupport[type]?.invoke(context) as T
        }

        if (GXViewKey.VIEW_TYPE_CUSTOM == type && customViewClass != null) {
            return newInstance<T>(customViewClass, context) as T
        }

        return when (type) {
            GXViewKey.VIEW_TYPE_GAIA_TEMPLATE -> GXView(context) as T
            GXViewKey.VIEW_TYPE_VIEW -> GXView(context) as T
            GXViewKey.VIEW_TYPE_TEXT -> GXText(context) as T
            GXViewKey.VIEW_TYPE_RICH_TEXT -> GXRichText(context) as T
            GXViewKey.VIEW_TYPE_IMAGE -> GXImageView(context) as T
            GXViewKey.VIEW_TYPE_CONTAINER_SCROLL -> GXScrollView(context) as T
            GXViewKey.VIEW_TYPE_CONTAINER_GRID -> GXGridView(context) as T
            GXViewKey.VIEW_TYPE_ICON_FONT -> GXIconFont(context) as T
            GXViewKey.VIEW_TYPE_SHADOW_LAYOUT -> GXShadowLayout(context) as T
            GXViewKey.VIEW_TYPE_CONTAINER_SLIDER -> GXSliderView(context) as T
            GXViewKey.VIEW_TYPE_PROGRESS -> GXProgressView(context) as T
            else -> throw IllegalArgumentException("unknown type")
        }
    }

    private fun <T : View> newInstance(clazz: String?, context: Context) = if (clazz != null) {
        try {
            val c = Class.forName(clazz).getConstructor(Context::class.java)
            c.newInstance(context) as T
        } catch (e: Exception) {
            e.printStackTrace()
            // 如果没有找到自定义View，则返回默认的View
            GXView(context) as T
        }
    } else {
        GXView(context)
    }
}
