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

    private val viewSupport: MutableMap<String, Class<*>> = mutableMapOf()

    init {
        viewSupport[GXViewKey.VIEW_TYPE_GAIA_TEMPLATE] = GXView::class.java
        viewSupport[GXViewKey.VIEW_TYPE_VIEW] = GXView::class.java
        viewSupport[GXViewKey.VIEW_TYPE_TEXT] = GXText::class.java
        viewSupport[GXViewKey.VIEW_TYPE_RICH_TEXT] = GXRichText::class.java
        viewSupport[GXViewKey.VIEW_TYPE_IMAGE] = GXImageView::class.java
        viewSupport[GXViewKey.VIEW_TYPE_CONTAINER_SCROLL] = GXScrollView::class.java
        viewSupport[GXViewKey.VIEW_TYPE_CONTAINER_GRID] = GXGridView::class.java
        viewSupport[GXViewKey.VIEW_TYPE_ICON_FONT] = GXIconFont::class.java
        viewSupport[GXViewKey.VIEW_TYPE_LOTTIE] = GXLottie::class.java
        viewSupport[GXViewKey.VIEW_TYPE_SHADOW_LAYOUT] = GXShadowLayout::class.java
        viewSupport[GXViewKey.VIEW_TYPE_SLIDER] = GXSliderView::class.java
    }

    /**
     * Use reflection to create views based on Type
     */
    fun <T : View> createView(context: Context, type: String, customViewClass: String? = null): T {
        val result = if (GXViewKey.VIEW_TYPE_CUSTOM == type && customViewClass != null) {
            newInstance<T>(customViewClass, context)
        } else {
            newInstance<T>(viewSupport[type], context)
        }
        return result as T
    }

    private fun <T : View> newInstance(clazz: String?, context: Context) = if (clazz != null) {
        val c = Class.forName(clazz).getConstructor(Context::class.java)
        c.newInstance(context) as T
    } else {
        GXView(context)
    }

    private fun <T : View> newInstance(clazz: Class<*>?, context: Context) = if (clazz != null) {
        val c = clazz.getConstructor(Context::class.java)
        c.newInstance(context) as T
    } else {
        null
    }
}
