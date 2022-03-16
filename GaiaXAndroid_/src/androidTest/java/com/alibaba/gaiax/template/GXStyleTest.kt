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

import android.content.Context
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GXStyleTest {

    var context: Context? = null

    @Before
    fun setUp() {
        context = InstrumentationRegistry.getInstrumentation().context
    }

    @Test
    fun create() {

        var style = GXStyle.create(JSONObject())
        Assert.assertEquals(true, style.isEmptyStyle())

        style = GXStyle.create(JSONObject().apply {

            // Text Style
            this[GXTemplateKey.STYLE_FONT_SIZE] = "10px"
            this[GXTemplateKey.STYLE_FONT_COLOR] = "#00FF00"
            this[GXTemplateKey.STYLE_FONT_WEIGHT] = "400"
            this[GXTemplateKey.STYLE_FONT_LINES] = "2"
            this[GXTemplateKey.STYLE_FONT_LINE_HEIGHT] = "20px"
            this[GXTemplateKey.STYLE_FONT_TEXT_OVERFLOW] = "ellipsis"
            this[GXTemplateKey.STYLE_FONT_TEXT_DECORATION] = "line-through"

            // Image Style
            this[GXTemplateKey.STYLE_MODE] = "scaleToFill"
            this[GXTemplateKey.STYLE_MODE_TYPE] = "scale"

            // View Border Style
            this[GXTemplateKey.STYLE_BORDER_WIDTH] = "1px"
            this[GXTemplateKey.STYLE_BORDER_RADIUS] = "1px"
            this[GXTemplateKey.STYLE_BORDER_COLOR] = "#00FF00"

            // View Border Radii
            this[GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS] = "1px"
            this[GXTemplateKey.STYLE_BORDER_TOP_RIGHT_RADIUS] = "2px"
            this[GXTemplateKey.STYLE_BORDER_BOTTOM_LEFT_RADIUS] = "3px"
            this[GXTemplateKey.STYLE_BORDER_BOTTOM_RIGHT_RADIUS] = "4px"

            // View Padding
            this[GXTemplateKey.FLEXBOX_PADDING] = "1px"

            // View Background Style
            this[GXTemplateKey.STYLE_BACKGROUND_IMAGE] = "linear-gradient(to bottom right, red , yellow)"
            this[GXTemplateKey.STYLE_BACKGROUND_COLOR] = "#00FF00"

            // View Common Style
            this[GXTemplateKey.FLEXBOX_DISPLAY] = "flex"
            this[GXTemplateKey.STYLE_HIDDEN] = "true"
            this[GXTemplateKey.STYLE_BOX_SHADOW] = "0px 5px 5px 5px #ff0000"
            this[GXTemplateKey.STYLE_OPACITY] = "0.5"
            this[GXTemplateKey.FLEXBOX_OVERFLOW] = "visible"
        })

        Assert.assertEquals(false, style.isEmptyStyle())

        // Text
        Assert.assertEquals(10F.dpToPx(), style.fontSize?.valueFloat)
        Assert.assertEquals(GXColor.parseColor("#00FF00"), style.fontColor?.value)
        Assert.assertEquals(Typeface.DEFAULT, style.fontWeight)
        Assert.assertEquals(2, style.fontLines)
        Assert.assertEquals(20F.dpToPx(), style.fontLineHeight?.valueFloat)
        Assert.assertEquals(TextUtils.TruncateAt.END, style.fontTextOverflow)
        Assert.assertEquals(Paint.STRIKE_THRU_TEXT_FLAG, style.fontTextDecoration)

        // Image
        Assert.assertEquals("scaleToFill", style.mode?.mode)
        Assert.assertEquals("scale", style.mode?.modeType)

        // Border
        Assert.assertEquals(1F.dpToPx(), style.borderWidth?.valueFloat)
        Assert.assertEquals(1F.dpToPx(), style.borderRadius?.topLeft?.valueFloat)
        Assert.assertEquals(2F.dpToPx(), style.borderRadius?.topRight?.valueFloat)
        Assert.assertEquals(3F.dpToPx(), style.borderRadius?.bottomLeft?.valueFloat)
        Assert.assertEquals(4F.dpToPx(), style.borderRadius?.bottomRight?.valueFloat)

        // Padding
        Assert.assertEquals(1F.dpToPx(), style.padding?.start?.valueFloat)
        Assert.assertEquals(1F.dpToPx(), style.padding?.top?.valueFloat)
        Assert.assertEquals(1F.dpToPx(), style.padding?.end?.valueFloat)
        Assert.assertEquals(1F.dpToPx(), style.padding?.bottom?.valueFloat)

        // Background
        Assert.assertEquals(GradientDrawable.Orientation.TL_BR, style.backgroundImage?.direction)
        Assert.assertEquals(GXColor.parseColor("#00FF00"), style.backgroundColor?.value)

        // Common
        Assert.assertEquals(View.VISIBLE, style.display)
        Assert.assertEquals(View.INVISIBLE, style.hidden)
        Assert.assertEquals(0.5F, style.opacity)
        Assert.assertEquals(false, style.overflow)

    }
}