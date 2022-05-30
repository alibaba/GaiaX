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

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.support.test.InstrumentationRegistry
import android.support.test.runner.AndroidJUnit4
import app.visly.stretch.Rect
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.GXSize.Companion.ptToPx
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GXStyleConvertTest {

    lateinit var convert: GXStyleConvert

    @Before
    fun before() {
        convert = GXStyleConvert()
        convert.init(InstrumentationRegistry.getInstrumentation().context.assets)
    }

    @Test
    fun fontTextDecoration() {
        Assert.assertEquals(null, convert.textDecoration(JSONObject()))
        Assert.assertEquals(
            Paint.UNDERLINE_TEXT_FLAG,
            convert.textDecoration(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_TEXT_DECORATION] = "underline"
            })
        )
        Assert.assertEquals(
            Paint.STRIKE_THRU_TEXT_FLAG,
            convert.textDecoration(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_TEXT_DECORATION] = "line-through"
            })
        )
        Assert.assertEquals(
            Paint.STRIKE_THRU_TEXT_FLAG,
            convert.textDecoration(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_TEXT_DECORATION] = "line-through"
            })
        )
    }

    @Test
    fun padding() {
        // create
        Assert.assertEquals(null, convert.padding(JSONObject()))
        Assert.assertEquals(
            true,
            convert.padding(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_PADDING] = "14px"
            }) is Rect
        )

        // value
        Assert.assertEquals(
            14F.dpToPx(),
            convert.padding(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_PADDING] = "14px"
            })?.start?.valueFloat
        )
        Assert.assertEquals(
            14F.dpToPx(),
            convert.padding(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_PADDING] = "14px"
            })?.end?.valueFloat
        )
        Assert.assertEquals(
            14F.dpToPx(),
            convert.padding(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_PADDING] = "14px"
            })?.top?.valueFloat
        )
        Assert.assertEquals(
            14F.dpToPx(),
            convert.padding(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_PADDING] = "14px"
            })?.bottom?.valueFloat
        )

        Assert.assertEquals(
            14F.dpToPx(),
            convert.padding(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_PADDING_LEFT] = "14px"
            })?.start?.valueFloat
        )
        Assert.assertEquals(
            14F.dpToPx(),
            convert.padding(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_PADDING_RIGHT] = "14px"
            })?.end?.valueFloat
        )
        Assert.assertEquals(
            14F.dpToPx(),
            convert.padding(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_PADDING_TOP] = "14px"
            })?.top?.valueFloat
        )
        Assert.assertEquals(
            14F.dpToPx(),
            convert.padding(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_PADDING_BOTTOM] = "14px"
            })?.bottom?.valueFloat
        )
    }

    @Test
    fun overflow() {
        Assert.assertEquals(null, convert.overflow(JSONObject()))
        Assert.assertEquals(
            false,
            convert.overflow(JSONObject().apply {
                this[GXTemplateKey.FLEXBOX_OVERFLOW] = "visible"
            })
        )
        Assert.assertEquals(
            true,
            convert.overflow(JSONObject().apply { this[GXTemplateKey.FLEXBOX_OVERFLOW] = "hidden" })
        )
    }

    @Test
    fun opacity() {
        Assert.assertEquals(null, convert.opacity(JSONObject()))
        Assert.assertEquals(
            0.5F,
            convert.opacity(JSONObject().apply { this[GXTemplateKey.STYLE_OPACITY] = "0.5" })
        )
    }

    @Test
    fun mode() {
        // create
        Assert.assertEquals(null, convert.mode(JSONObject()))
    }

    @Test
    fun hidden() {
        Assert.assertEquals(null, convert.hidden(JSONObject()))
        Assert.assertEquals(
            View.INVISIBLE,
            convert.hidden(JSONObject().apply { this[GXTemplateKey.STYLE_HIDDEN] = "true" })
        )
        Assert.assertEquals(
            View.VISIBLE,
            convert.hidden(JSONObject().apply { this[GXTemplateKey.STYLE_HIDDEN] = "false" })
        )
    }

    @Test
    fun fontWeight() {
        Assert.assertEquals(null, convert.fontWeight(JSONObject()))
        Assert.assertEquals(
            Typeface.DEFAULT_BOLD,
            convert.fontWeight(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_WEIGHT] = "bold"
            })
        )
        Assert.assertEquals(
            Typeface.DEFAULT_BOLD,
            convert.fontWeight(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_WEIGHT] = "medium"
            })
        )
        Assert.assertEquals(
            Typeface.DEFAULT_BOLD,
            convert.fontWeight(JSONObject().apply { this[GXTemplateKey.STYLE_FONT_WEIGHT] = "500" })
        )
        Assert.assertEquals(
            Typeface.DEFAULT_BOLD,
            convert.fontWeight(JSONObject().apply { this[GXTemplateKey.STYLE_FONT_WEIGHT] = "600" })
        )
        Assert.assertEquals(
            Typeface.DEFAULT_BOLD,
            convert.fontWeight(JSONObject().apply { this[GXTemplateKey.STYLE_FONT_WEIGHT] = "700" })
        )
        Assert.assertEquals(
            Typeface.DEFAULT,
            convert.fontWeight(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_WEIGHT] = "normal"
            })
        )
        Assert.assertEquals(
            Typeface.DEFAULT,
            convert.fontWeight(JSONObject().apply { this[GXTemplateKey.STYLE_FONT_WEIGHT] = "400" })
        )
    }

    @Test
    fun fontTextOverflow() {
        Assert.assertEquals(TextUtils.TruncateAt.END, convert.fontTextOverflow(JSONObject()))
        Assert.assertEquals(
            null,
            convert.fontTextOverflow(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_TEXT_OVERFLOW] = "clip"
            })
        )
        Assert.assertEquals(
            TextUtils.TruncateAt.END,
            convert.fontTextOverflow(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_TEXT_OVERFLOW] = "ellipsis"
            })
        )
    }

    @Test
    fun fontTextAlign() {
        Assert.assertEquals(null, convert.fontTextAlign(JSONObject()))
        Assert.assertEquals(
            Gravity.LEFT,
            convert.fontTextAlign(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_TEXT_ALIGN] = "left"
            })
        )
        Assert.assertEquals(
            Gravity.RIGHT,
            convert.fontTextAlign(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_TEXT_ALIGN] = "right"
            })
        )
        Assert.assertEquals(
            Gravity.CENTER,
            convert.fontTextAlign(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_TEXT_ALIGN] = "center"
            })
        )
    }

    @Test
    fun fontLiens() {
        Assert.assertEquals(null, convert.fontLines(JSONObject()))
        Assert.assertEquals(
            10,
            convert.fontLines(JSONObject().apply { this[GXTemplateKey.STYLE_FONT_LINES] = "10" })
        )
    }

    @Test
    fun lineHeight() {
        // create
        Assert.assertEquals(null, convert.fontLineHeight(JSONObject()))

        // value
        Assert.assertEquals(
            12F.dpToPx(),
            convert.fontLineHeight(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_LINE_HEIGHT] = "12px"
            })?.valueFloat
        )
    }

    @Test
    fun fontFamily() {
        // create
        Assert.assertEquals(null, convert.fontFamily(JSONObject()))
    }

    @Test
    fun backgroundColor() {
        // create
        Assert.assertEquals(null, convert.backgroundColor(JSONObject()))
        Assert.assertEquals(null, convert.backgroundColor(JSONObject())?.value)
        Assert.assertEquals(
            GXColor.parseColor("#00FF00"),
            convert.backgroundColor(JSONObject().apply {
                this[GXTemplateKey.STYLE_BACKGROUND_COLOR] = "#00FF00"
            })?.value
        )
    }

    @Test
    fun borderColor() {
        // create
        Assert.assertEquals(null, convert.borderColor(JSONObject()))
        Assert.assertEquals(null, convert.borderColor(JSONObject())?.value)
        Assert.assertEquals(
            GXColor.parseColor("#00FF00"),
            convert.borderColor(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_COLOR] = "#00FF00"
            })?.value
        )
    }

    @Test
    fun backgroundImage() {
        // create
        Assert.assertEquals(null, convert.backgroundImage(JSONObject()))
        Assert.assertEquals(
            true,
            convert.backgroundImage(JSONObject().apply {
                this[GXTemplateKey.STYLE_BACKGROUND_IMAGE] =
                    "linear-gradient(to bottom right, red , yellow)"
            }) != null
        )

        // value
        Assert.assertEquals(
            true,
            convert.backgroundImage(JSONObject().apply {
                this[GXTemplateKey.STYLE_BACKGROUND_IMAGE] =
                    "linear-gradient(to bottom right, red , yellow)"
            })?.createDrawable() is GradientDrawable
        )
        Assert.assertEquals(
            GradientDrawable.Orientation.TL_BR,
            convert.backgroundImage(JSONObject().apply {
                this[GXTemplateKey.STYLE_BACKGROUND_IMAGE] =
                    "linear-gradient(to bottom right, red , yellow)"
            })?.direction
        )
        Assert.assertEquals(
            2,
            convert.backgroundImage(JSONObject().apply {
                this[GXTemplateKey.STYLE_BACKGROUND_IMAGE] =
                    "linear-gradient(to bottom right, red , yellow)"
            })?.colors?.size
        )

    }


    @Test
    fun boxShadow() {
        // create
        Assert.assertEquals(
            null,
            convert.boxShadow(JSONObject().apply {
                this[GXTemplateKey.STYLE_BOX_SHADOW] = "0px 5px 5px 5px"
            })
        )
        Assert.assertEquals(
            true,
            convert.boxShadow(JSONObject().apply {
                this[GXTemplateKey.STYLE_BOX_SHADOW] = "0px 5px 5px 5px #ff0000"
            }) != null
        )

        // value
        Assert.assertEquals(
            0F.dpToPx(),
            convert.boxShadow(JSONObject().apply {
                this[GXTemplateKey.STYLE_BOX_SHADOW] = "0px 5px 5px 5px #ff0000"
            })?.xOffset?.valueFloat
        )
        Assert.assertEquals(
            5F.dpToPx(),
            convert.boxShadow(JSONObject().apply {
                this[GXTemplateKey.STYLE_BOX_SHADOW] = "0px 5px 5px 5px #ff0000"
            })?.yOffset?.valueFloat
        )
        Assert.assertEquals(
            5F.dpToPx(),
            convert.boxShadow(JSONObject().apply {
                this[GXTemplateKey.STYLE_BOX_SHADOW] = "0px 5px 5px 5px #ff0000"
            })?.blurOffset?.valueFloat
        )
        Assert.assertEquals(
            5F.dpToPx(),
            convert.boxShadow(JSONObject().apply {
                this[GXTemplateKey.STYLE_BOX_SHADOW] = "0px 5px 5px 5px #ff0000"
            })?.spreadOffset?.valueFloat
        )
        Assert.assertEquals(
            GXColor.parseColor("#ff0000"),
            convert.boxShadow(JSONObject().apply {
                this[GXTemplateKey.STYLE_BOX_SHADOW] = "0px 5px 5px 5px #ff0000"
            })?.color?.value
        )

    }

    @Test
    fun fontColor() {
        Assert.assertEquals(null, convert.fontColor(JSONObject()))
        Assert.assertEquals(
            GXColor.parseColor("#00FF00"),
            convert.fontColor(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_COLOR] = "#00FF00"
            })?.value
        )
    }

    @Test
    fun borderRadius() {
        // create
        Assert.assertEquals(null, convert.borderRadius(JSONObject()))
        Assert.assertEquals(
            true,
            convert.borderRadius(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_RADIUS] = "14px"
            }) is GXRoundedCorner
        )

        // value
        Assert.assertEquals(null, convert.borderRadius(JSONObject())?.topLeft?.valueFloat)
        Assert.assertEquals(
            14F.dpToPx(),
            convert.borderRadius(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_RADIUS] = "14px"
            })?.topLeft?.valueFloat
        )
        Assert.assertEquals(
            14F.ptToPx(),
            convert.borderRadius(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_RADIUS] = "14pt"
            })?.topLeft?.valueFloat
        )

        // create
        Assert.assertEquals(null, convert.borderRadius(JSONObject()))
        Assert.assertEquals(true, convert.borderRadius(JSONObject().apply {
            this[GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS] = "5px"
            this[GXTemplateKey.STYLE_BORDER_TOP_RIGHT_RADIUS] = "5px"
            this[GXTemplateKey.STYLE_BORDER_BOTTOM_LEFT_RADIUS] = "5px"
            this[GXTemplateKey.STYLE_BORDER_BOTTOM_RIGHT_RADIUS] = "5px"
        }) != null)
        Assert.assertEquals(true, convert.borderRadius(JSONObject().apply {
            this[GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS] = "5px"
            this[GXTemplateKey.STYLE_BORDER_TOP_RIGHT_RADIUS] = "5px"
            this[GXTemplateKey.STYLE_BORDER_BOTTOM_LEFT_RADIUS] = "5px"
        }) != null)
        Assert.assertEquals(true, convert.borderRadius(JSONObject().apply {
            this[GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS] = "5px"
            this[GXTemplateKey.STYLE_BORDER_TOP_RIGHT_RADIUS] = "5px"
        }) != null)
        Assert.assertEquals(true, convert.borderRadius(JSONObject().apply {
            this[GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS] = "5px"
        }) != null)

        // value
        Assert.assertEquals(
            5F.dpToPx(),
            convert.borderRadius(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS] = "5px"
            })?.topLeft?.valueFloat
        )
        Assert.assertEquals(
            null,
            convert.borderRadius(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS] = "5px"
            })?.topRight
        )
        Assert.assertEquals(
            null,
            convert.borderRadius(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS] = "5px"
            })?.bottomLeft
        )
        Assert.assertEquals(
            null,
            convert.borderRadius(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS] = "5px"
            })?.bottomRight
        )

    }

    @Test
    fun borderWidth() {
        // create
        Assert.assertEquals(null, convert.borderWidth(JSONObject()))
        Assert.assertEquals(
            true,
            convert.borderWidth(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_WIDTH] = "14px"
            }) is GXSize.PX
        )
        Assert.assertEquals(
            true,
            convert.borderWidth(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_WIDTH] = "14pt"
            }) is GXSize.PT
        )
        Assert.assertEquals(
            true,
            convert.borderWidth(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_WIDTH] = "auto"
            }) is GXSize.Auto
        )
        Assert.assertEquals(
            true,
            convert.borderWidth(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_WIDTH] = ""
            }) is GXSize.Undefined
        )

        // value
        Assert.assertEquals(null, convert.borderWidth(JSONObject())?.valueFloat)
        Assert.assertEquals(
            14F.dpToPx(),
            convert.borderWidth(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_WIDTH] = "14px"
            })?.valueFloat
        )
        Assert.assertEquals(
            14F.ptToPx(),
            convert.borderWidth(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_WIDTH] = "14pt"
            })?.valueFloat
        )
        Assert.assertEquals(
            0F,
            convert.borderWidth(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_WIDTH] = "auto"
            })?.valueFloat
        )
        Assert.assertEquals(
            0F,
            convert.borderWidth(JSONObject().apply {
                this[GXTemplateKey.STYLE_BORDER_WIDTH] = ""
            })?.valueFloat
        )

    }


    @Test
    fun getLinearGradient() {
        var results = convert.getLinearGradient("linear-gradient(to bottom right, red , yellow)")
        Assert.assertEquals(results.size, 3)

        var direction = convert.getDirection(results)
        Assert.assertEquals(direction, GradientDrawable.Orientation.TL_BR)

        results = convert.getLinearGradient("linear-gradient(to right, red , yellow)")
        Assert.assertEquals(results.size, 3)

        direction = convert.getDirection(results)
        Assert.assertEquals(direction, GradientDrawable.Orientation.LEFT_RIGHT)

        results = convert.getLinearGradient("linear-gradient(to left, red , yellow)")
        Assert.assertEquals(results.size, 3)

        direction = convert.getDirection(results)
        Assert.assertEquals(direction, GradientDrawable.Orientation.RIGHT_LEFT)

        results = convert.getLinearGradient("linear-gradient(to top, red , yellow)")
        Assert.assertEquals(results.size, 3)

        direction = convert.getDirection(results)
        Assert.assertEquals(direction, GradientDrawable.Orientation.BOTTOM_TOP)

        results = convert.getLinearGradient("linear-gradient(to bottom, red , yellow)")
        Assert.assertEquals(results.size, 3)

        direction = convert.getDirection(results)
        Assert.assertEquals(direction, GradientDrawable.Orientation.TOP_BOTTOM)

        results =
            convert.getLinearGradient("linear-gradient(to right,#FFFFFF 0%,#ECEAFF 50%,#E6F1FF 50%,#DCFFFF 50%,#FFF8E5 50%,#FFDDF2 100%)")
        Assert.assertEquals(results.size, 7)

        direction = convert.getDirection(results)
        Assert.assertEquals(direction, GradientDrawable.Orientation.LEFT_RIGHT)

        results =
            convert.getLinearGradient("linear-gradient(to right,rgba(235,184,45,0.34) 0%,rgba(255,0,0,0.38) 50%,rgba(143,76,76,0.54) 50%,rgba(36,150,163,0.59) 100%)")
        Assert.assertEquals(results.size, 5)

        direction = convert.getDirection(results)
        Assert.assertEquals(direction, GradientDrawable.Orientation.LEFT_RIGHT)

        results = convert.getLinearGradient("linear-gradient(red , yellow)")
        Assert.assertEquals(results.size, 2)

        direction = convert.getDirection(results)
        Assert.assertEquals(direction, GradientDrawable.Orientation.TOP_BOTTOM)
    }

    @Test
    fun getLinearGradientColors() {
        var results =
            convert.getLinearGradient("linear-gradient(to right,#FFFFFF 0%,#ECEAFF 50%,#E6F1FF 50%,#DCFFFF 50%,#FFF8E5 50%,#FFDDF2 100%)")
        var colors = convert.getLinearGradientColors(results)
        Assert.assertEquals(results.size, 7)
        Assert.assertEquals(colors.size, 6)
        Assert.assertEquals(colors[0], Color.parseColor("#FFFFFF"))
        Assert.assertEquals(colors[1], Color.parseColor("#ECEAFF"))
        Assert.assertEquals(colors[2], Color.parseColor("#E6F1FF"))
        Assert.assertEquals(colors[3], Color.parseColor("#DCFFFF"))
        Assert.assertEquals(colors[4], Color.parseColor("#FFF8E5"))
        Assert.assertEquals(colors[5], Color.parseColor("#FFDDF2"))

        results = convert.getLinearGradient("linear-gradient(to bottom right, red , yellow)")
        colors = convert.getLinearGradientColors(results)
        Assert.assertEquals(results.size, 3)
        Assert.assertEquals(colors.size, 2)
        Assert.assertEquals(colors[0], Color.RED)
        Assert.assertEquals(colors[1], Color.YELLOW)

        results = convert.getLinearGradient("linear-gradient(red , green, yellow)")
        colors = convert.getLinearGradientColors(results)
        Assert.assertEquals(results.size, 3)
        Assert.assertEquals(colors.size, 3)
        Assert.assertEquals(colors[0], Color.RED)
        Assert.assertEquals(colors[1], Color.GREEN)
        Assert.assertEquals(colors[2], Color.YELLOW)

        results = convert.getLinearGradient("linear-gradient(red , green)")
        colors = convert.getLinearGradientColors(results)
        Assert.assertEquals(results.size, 2)
        Assert.assertEquals(colors.size, 2)
        Assert.assertEquals(colors[0], Color.RED)
        Assert.assertEquals(colors[1], Color.GREEN)
    }

    @Test
    fun getLinearGradientColors2() {
        val results =
            convert.getLinearGradient("linear-gradient(to bottom right, red 0% , yellow 100%)")
        val colors = convert.getLinearGradientColors(results)
        Assert.assertEquals(results.size, 3)
        Assert.assertEquals(colors.size, 2)
        Assert.assertEquals(colors[0], Color.RED)
        Assert.assertEquals(colors[1], Color.YELLOW)
    }

    @Test
    fun display() {
        // create
        Assert.assertEquals(null, convert.display(JSONObject()))

        // value
        Assert.assertEquals(
            View.GONE,
            convert.display(JSONObject().apply { this[GXTemplateKey.FLEXBOX_DISPLAY] = "none" })
        )
        Assert.assertEquals(
            View.VISIBLE,
            convert.display(JSONObject().apply { this[GXTemplateKey.FLEXBOX_DISPLAY] = "flex" })
        )
        Assert.assertEquals(
            null,
            convert.display(JSONObject().apply { this[GXTemplateKey.FLEXBOX_DISPLAY] = "" })
        )
    }

    @Test
    fun font() {
        // create
        Assert.assertEquals(null, convert.font(JSONObject()))
        Assert.assertEquals(
            true,
            convert.font(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_SIZE] = "14px"
            }) is GXSize.PX
        )
        Assert.assertEquals(
            true,
            convert.font(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_SIZE] = "14pt"
            }) is GXSize.PT
        )
        Assert.assertEquals(
            true,
            convert.font(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_SIZE] = ""
            }) is GXSize.Undefined
        )

        // value
        Assert.assertEquals(null, convert.font(JSONObject())?.valueFloat)
        Assert.assertEquals(
            14F.dpToPx(),
            convert.font(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_SIZE] = "14px"
            })?.valueFloat
        )
        Assert.assertEquals(
            14F.ptToPx(),
            convert.font(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_SIZE] = "14pt"
            })?.valueFloat
        )
        Assert.assertEquals(
            0F,
            convert.font(JSONObject().apply {
                this[GXTemplateKey.STYLE_FONT_SIZE] = ""
            })?.valueFloat
        )
    }
}