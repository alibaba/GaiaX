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
import androidx.test.platform.app.InstrumentationRegistry
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.render.view.basic.*
import com.alibaba.gaiax.render.view.container.GXGridView
import com.alibaba.gaiax.render.view.container.GXScrollView
import com.alibaba.gaiax.render.view.container.slider.GXSliderView
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class GXViewFactoryTest {

    @Test
    fun create() {
        val context = InstrumentationRegistry.getInstrumentation().context
        Assert.assertEquals(
            true,
            GXViewFactory.createView<View>(context, GXViewKey.VIEW_TYPE_GAIA_TEMPLATE) is GXView
        )
        Assert.assertEquals(
            true,
            GXViewFactory.createView<View>(context, GXViewKey.VIEW_TYPE_VIEW) is GXView
        )
        Assert.assertEquals(
            true,
            GXViewFactory.createView<View>(context, GXViewKey.VIEW_TYPE_TEXT) is GXText
        )
        Assert.assertEquals(
            true,
            GXViewFactory.createView<View>(context, GXViewKey.VIEW_TYPE_LOTTIE) is View
        )
        Assert.assertEquals(
            true,
            GXViewFactory.createView<View>(
                context,
                GXViewKey.VIEW_TYPE_CONTAINER_GRID
            ) is GXGridView
        )
        Assert.assertEquals(
            true,
            GXViewFactory.createView<View>(
                context,
                GXViewKey.VIEW_TYPE_CONTAINER_SCROLL
            ) is GXScrollView
        )
        Assert.assertEquals(
            true,
            GXViewFactory.createView<View>(context, GXViewKey.VIEW_TYPE_ICON_FONT) is GXIconFont
        )
        Assert.assertEquals(
            true,
            GXViewFactory.createView<View>(context, GXViewKey.VIEW_TYPE_IMAGE) is GXImageView
        )
        Assert.assertEquals(
            true,
            GXViewFactory.createView<View>(context, GXViewKey.VIEW_TYPE_RICH_TEXT) is GXText
        )
        Assert.assertEquals(
            true,
            GXViewFactory.createView<View>(
                context,
                GXViewKey.VIEW_TYPE_SHADOW_LAYOUT
            ) is GXShadowLayout
        )
        Assert.assertEquals(
            true,
            GXViewFactory.createView<View>(
                context,
                GXViewKey.VIEW_TYPE_CONTAINER_SLIDER
            ) is GXSliderView
        )
    }

    @Before
    fun registerLottie(){
        GXRegisterCenter.instance.registerExtensionViewSupport(GXViewKey.VIEW_TYPE_LOTTIE,::buildLottieView)
    }

    fun buildLottieView(context: Context): View {
        return View(context)
    }

}
