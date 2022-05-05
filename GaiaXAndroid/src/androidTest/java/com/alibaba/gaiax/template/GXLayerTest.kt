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

import com.alibaba.gaiax.render.view.GXViewKey
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test

class GXLayerTest {


    @Test
    fun create_Type() {

        Assert.assertEquals(true, GXMockUtils.createViewTypeLayer().isViewType())
        Assert.assertEquals(true, GXMockUtils.createRootTypeLayer().isGaiaTemplate())
        Assert.assertEquals(true, GXMockUtils.createRootTypeLayer().isViewType())

        Assert.assertEquals(true, GXMockUtils.createGaiaTemplateTypeLayer().isGaiaTemplate())

        Assert.assertEquals(true, GXMockUtils.createGridTypeLayer().isGridType())
        Assert.assertEquals(true, GXMockUtils.createGridTypeLayer().isGaiaTemplate())

        Assert.assertEquals(true, GXMockUtils.createScrollTypeLayer().isScrollType())
        Assert.assertEquals(true, GXMockUtils.createScrollTypeLayer().isGaiaTemplate())

        Assert.assertEquals(true, GXMockUtils.createGridTypeLayer().isContainerType())
        Assert.assertEquals(true, GXMockUtils.createScrollTypeLayer().isContainerType())

        Assert.assertEquals(true, GXMockUtils.createIconFontTypeLayer().isIconFontType())

        Assert.assertEquals(true, GXMockUtils.createImageTypeLayer().isImageType())

        Assert.assertEquals(true, GXMockUtils.createLottieTypeLayer().isLottieType())

        Assert.assertEquals(
            true,
            GXMockUtils.createNestChildTemplateTypeLayer().isNestChildTemplateType()
        )

        Assert.assertEquals(true, GXMockUtils.createRichTextTypeLayer().isRichTextType())

        Assert.assertEquals(true, GXMockUtils.createTextTypeLayer().isTextType())

        Assert.assertEquals(true, GXMockUtils.createCustomType().isCustomType())
    }

    @Test
    fun getNodeType() {
        Assert.assertEquals(
            GXViewKey.VIEW_TYPE_VIEW,
            GXMockUtils.createViewTypeLayer().getNodeType()
        )

        Assert.assertEquals(
            GXViewKey.VIEW_TYPE_GAIA_TEMPLATE,
            GXMockUtils.createRootTypeLayer().getNodeType()
        )

        Assert.assertEquals(
            GXViewKey.VIEW_TYPE_GAIA_TEMPLATE,
            GXMockUtils.createGaiaTemplateTypeLayer().getNodeType()
        )

        Assert.assertEquals(
            GXViewKey.VIEW_TYPE_CONTAINER_GRID,
            GXMockUtils.createGridTypeLayer().getNodeType()
        )

        Assert.assertEquals(
            GXViewKey.VIEW_TYPE_CONTAINER_SCROLL,
            GXMockUtils.createScrollTypeLayer().getNodeType()
        )

        Assert.assertEquals(
            GXViewKey.VIEW_TYPE_ICON_FONT,
            GXMockUtils.createIconFontTypeLayer().getNodeType()
        )

        Assert.assertEquals(
            GXViewKey.VIEW_TYPE_IMAGE,
            GXMockUtils.createImageTypeLayer().getNodeType()
        )

        Assert.assertEquals(
            GXViewKey.VIEW_TYPE_LOTTIE,
            GXMockUtils.createLottieTypeLayer().getNodeType()
        )

        Assert.assertEquals(
            GXViewKey.VIEW_TYPE_RICH_TEXT,
            GXMockUtils.createRichTextTypeLayer().getNodeType()
        )

        Assert.assertEquals(
            GXViewKey.VIEW_TYPE_TEXT,
            GXMockUtils.createTextTypeLayer().getNodeType()
        )

        Assert.assertEquals(
            GXViewKey.VIEW_TYPE_CUSTOM,
            GXMockUtils.createCustomType().getNodeType()
        )

        Assert.assertEquals(
            GXViewKey.VIEW_TYPE_CUSTOM,
            GXMockUtils.createNestChildTemplateTypeLayer().getNodeType()
        )


    }
}