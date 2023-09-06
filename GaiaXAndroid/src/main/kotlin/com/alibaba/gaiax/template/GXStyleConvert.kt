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

import android.content.res.AssetManager
import android.graphics.Paint
import android.graphics.Shader
import android.graphics.Typeface
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import app.visly.stretch.Rect
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter

/**
 * Convert resources in CssJson to values in styles in Android
 *
 * https://css-tricks.com/snippets/css/a-guide-to-flexbox/
 * https://yuque.antfin-inc.com/ronghui.zrh/dkq41b/le4mwr
 *
 *  @see GXTemplateKey
 *
 *  @suppress
 */
class GXStyleConvert {

    lateinit var assets: AssetManager

    fun init(assetManager: AssetManager) {
        this.assets = assetManager
    }

    companion object {
        val instance by lazy {
            val convert = GXStyleConvert()
            convert
        }
    }

    fun textDecoration(css: JSONObject): Int? =
        css.getString(GXTemplateKey.STYLE_FONT_TEXT_DECORATION)?.let {
            return textDecoration(it)
        }


    fun textDecoration(target: String): Int? = when (target) {
        "line-through" -> Paint.STRIKE_THRU_TEXT_FLAG
        "underline" -> Paint.UNDERLINE_TEXT_FLAG
        else -> null
    }

    fun padding(cssJson: JSONObject): Rect<GXSize>? {
        var result: Rect<GXSize>? = null
        val edgeInsets = GXContainerConvert.edgeInsets(
            cssJson.getString(GXTemplateKey.GAIAX_LAYER_EDGE_INSETS)
        )

        cssJson.getString(GXTemplateKey.FLEXBOX_PADDING)?.also {
            val size = GXSize.create(it)
            result = Rect(size, size, size, size)
        }

        cssJson.getString(GXTemplateKey.FLEXBOX_PADDING_LEFT)?.let {
            if (result == null) {
                result =
                    Rect(GXSize.Undefined, GXSize.Undefined, GXSize.Undefined, GXSize.Undefined)
            }
            result?.start = GXSize.create(it)
        }

        cssJson.getString(GXTemplateKey.FLEXBOX_PADDING_RIGHT)?.let {
            if (result == null) {
                result =
                    Rect(GXSize.Undefined, GXSize.Undefined, GXSize.Undefined, GXSize.Undefined)
            }
            result?.end = GXSize.create(it)
        }

        cssJson.getString(GXTemplateKey.FLEXBOX_PADDING_TOP)?.let {
            if (result == null) {
                result =
                    Rect(GXSize.Undefined, GXSize.Undefined, GXSize.Undefined, GXSize.Undefined)
            }
            result?.top = GXSize.create(it)
        }

        cssJson.getString(GXTemplateKey.FLEXBOX_PADDING_BOTTOM)?.let {
            if (result == null) {
                result =
                    Rect(GXSize.Undefined, GXSize.Undefined, GXSize.Undefined, GXSize.Undefined)
            }
            result?.bottom = GXSize.create(it)
        }

        edgeInsets?.let {
            if (result == null) {
                result =
                    Rect(GXSize.Undefined, GXSize.Undefined, GXSize.Undefined, GXSize.Undefined)
            }

            if (edgeInsets.start !is GXSize.Undefined) {
                result?.start = edgeInsets.start
            }
            if (edgeInsets.end !is GXSize.Undefined) {
                result?.end = edgeInsets.end
            }
            if (edgeInsets.top !is GXSize.Undefined) {
                result?.top = edgeInsets.top
            }
            if (edgeInsets.bottom !is GXSize.Undefined) {
                result?.bottom = edgeInsets.bottom
            }
        }

        return result
    }

    fun overflow(css: JSONObject): Boolean? = css.getString(GXTemplateKey.FLEXBOX_OVERFLOW)?.let {
        return overflow(it)
    }


    fun overflow(target: String): Boolean? = when (target) {
        "visible" -> false
        "hidden" -> true
        else -> null
    }

    fun opacity(css: JSONObject): Float? = css.getString(GXTemplateKey.STYLE_OPACITY)?.let {
        return opacity(it)
    }

    fun opacity(target: String): Float = target.toFloat()

    fun mode(css: JSONObject): GXMode? {
        if (css.containsKey(GXTemplateKey.STYLE_MODE)) {
            val mode = css.getString(GXTemplateKey.STYLE_MODE) ?: "scaleToFill"
            val modeType = css.getString(GXTemplateKey.STYLE_MODE_TYPE) ?: GXMode.MODE_TYPE_SCALE
            return GXMode(modeType, mode)
        }
        return null
    }

    fun hidden(css: JSONObject): Int? =
        css.getString(GXTemplateKey.STYLE_HIDDEN)?.let { return hidden(it) }

    fun hidden(target: String): Int? = when (target) {
        "true" -> View.INVISIBLE
        "false" -> View.VISIBLE
        else -> null
    }

    fun fontWeight(css: JSONObject): Typeface? =
        css.getString(GXTemplateKey.STYLE_FONT_WEIGHT)?.let { fontWeight(it) }

    fun fontWeight(value: String?): Typeface? = when (value) {
        "bold", "medium", "500", "600", "700" -> Typeface.DEFAULT_BOLD
        "normal", "100", "200", "300", "400" -> Typeface.DEFAULT
        else -> null
    }

    fun fontTextOverflow(css: JSONObject): TextUtils.TruncateAt? =
        css.getString(GXTemplateKey.STYLE_FONT_TEXT_OVERFLOW)?.let { return fontTextOverflow(it) }
            ?: TextUtils.TruncateAt.END

    fun fontTextOverflow(target: String): TextUtils.TruncateAt? = when (target) {
        "clip" -> {
            GXRegisterCenter.instance.extensionStaticProperty?.convert(
                GXRegisterCenter.GXIExtensionStaticProperty.GXParams(
                    GXTemplateKey.STYLE_FONT_TEXT_OVERFLOW, "clip"
                )
            )?.let {
                return it as TextUtils.TruncateAt
            }
            null
        }
        "ellipsis" -> TextUtils.TruncateAt.END
        "middle" -> TextUtils.TruncateAt.MIDDLE
        else -> TextUtils.TruncateAt.END
    }

    fun fontTextAlign(css: JSONObject): Int? =
        css.getString(GXTemplateKey.STYLE_FONT_TEXT_ALIGN)?.let {
            return fontTextAlign(it)
        }

    fun fontTextAlign(target: String): Int? = when (target) {
        "left" -> Gravity.LEFT
        "right" -> Gravity.RIGHT
        "center" -> Gravity.CENTER
        else -> null
    }

    fun fontLines(css: JSONObject): Int? = css.getString(GXTemplateKey.STYLE_FONT_LINES)?.toInt()
    fun fontLines(target: String): Int = target.toInt()

    fun fontFamily(css: JSONObject): Typeface? {
        return css.getString(GXTemplateKey.STYLE_FONT_FAMILY)?.let { return fontFamily(it) }
    }

    fun fontFamily(fontFamily: String): Typeface? {
        // extend
        GXRegisterCenter.instance.extensionStaticProperty?.convert(
            GXRegisterCenter.GXIExtensionStaticProperty.GXParams(
                GXTemplateKey.STYLE_FONT_FAMILY, fontFamily
            )
        )?.let {
            (it as? Typeface)?.let { return it }
        }
        return null
    }

    fun font(css: JSONObject): GXSize? =
        css.getString(GXTemplateKey.STYLE_FONT_SIZE)?.let { it -> return font(it) }

    fun font(target: String): GXSize = GXSize.create(target)

    fun backgroundColor(css: JSONObject): GXColor? =
        css.getString(GXTemplateKey.STYLE_BACKGROUND_COLOR)
            ?.let { it -> return backgroundColor(it) }

    fun backgroundColor(target: String): GXColor? = GXColor.create(target)

    fun boxShadow(css: JSONObject): GXBoxShadow? =
        css.getString(GXTemplateKey.STYLE_BOX_SHADOW)?.let {
            return boxShadow(it)
        }


    fun boxShadow(target: String): GXBoxShadow? {
        val items = target.split(" ")
        if (items.size == 5) {
            val xOffset = items[0]
            val yOffset = items[1]
            val blurOffset = items[2]
            val spreadOffset = items[3]
            val color = items[4]
            return GXBoxShadow(
                GXSize.create(xOffset),
                GXSize.create(yOffset),
                GXSize.create(blurOffset),
                GXSize.create(spreadOffset),
                GXColor.create(color) ?: GXColor.createUndefine()
            )
        }
        return null
    }

    fun backdropFilter(css: JSONObject): GXBackdropFilter? =
        css.getString(GXTemplateKey.STYLE_BACKDROP_FILTER)?.let {
            return backdropFilter(it)
        }

    fun backdropFilter(target: String): GXBackdropFilter? = GXBackdropFilter.create(target)

    fun fontColor(css: JSONObject): GXColor? =
        css.getString(GXTemplateKey.STYLE_FONT_COLOR)?.let { fontColor ->
            return fontColor(fontColor)
        }

    fun fontColor(target: String): GXColor? = GXColor.create(target)

    fun backgroundImage(css: JSONObject): GXLinearColor? =
        css.getString(GXTemplateKey.STYLE_BACKGROUND_IMAGE)?.let { it ->
            return backgroundImage(it)
        }

    fun backgroundImage(target: String): GXLinearColor? {
        if (target.startsWith("linear-gradient")) {
            val linear = getLinearGradient(target)
            val colors = getLinearGradientColors(linear)
            val direction = getDirection(linear)
            return GXLinearColor(direction, colors)
        } else {
            GXColor.create(target)?.let {
                val colors = mutableListOf<GXColor>()
                colors.add(it)
                return GXLinearColor(GradientDrawable.Orientation.LEFT_RIGHT, colors)
            }
        }
        return null
    }


    fun borderColor(css: JSONObject): GXColor? =
        css.getString(GXTemplateKey.STYLE_BORDER_COLOR)?.let { return borderColor(it) }

    fun borderColor(target: String): GXColor? = GXColor.create(target)

    fun borderRadius(css: JSONObject): GXRoundedCorner? {
        var result: GXRoundedCorner? = null
        css.getString(GXTemplateKey.STYLE_BORDER_RADIUS)?.let {
            val radius = GXSize.create(it)
            result = GXRoundedCorner(radius, radius, radius, radius)
        }

        css.getString(GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS)?.let {
            if (result == null) {
                result = GXRoundedCorner(null, null, null, null)
            }
            result?.topLeft = GXSize.create(it)
        }

        css.getString(GXTemplateKey.STYLE_BORDER_TOP_RIGHT_RADIUS)?.let {
            if (result == null) {
                result = GXRoundedCorner(null, null, null, null)
            }
            result?.topRight = GXSize.create(it)
        }

        css.getString(GXTemplateKey.STYLE_BORDER_BOTTOM_LEFT_RADIUS)?.let {
            if (result == null) {
                result = GXRoundedCorner(null, null, null, null)
            }
            result?.bottomLeft = GXSize.create(it)
        }

        css.getString(GXTemplateKey.STYLE_BORDER_BOTTOM_RIGHT_RADIUS)?.let {
            if (result == null) {
                result = GXRoundedCorner(null, null, null, null)
            }
            result?.bottomRight = GXSize.create(it)
        }
        return result
    }

    fun borderWidth(css: JSONObject): GXSize? =
        css.getString(GXTemplateKey.STYLE_BORDER_WIDTH)?.let { return borderWidth(it) }

    fun borderWidth(target: String): GXSize = GXSize.create(target)

    fun display(css: JSONObject): Int? =
        css.getString(GXTemplateKey.FLEXBOX_DISPLAY)?.let { return display(it) }

    fun display(target: String): Int? = when (target) {
        "none" -> View.GONE
        "flex" -> View.VISIBLE
        else -> null
    }

    fun fontLineHeight(css: JSONObject): GXSize? =
        css.getString(GXTemplateKey.STYLE_FONT_LINE_HEIGHT)?.let { return fontLineHeight(it) }

    fun fontLineHeight(target: String): GXSize = GXSize.create(target)

    fun createLinearGradient(
        width: Float, height: Float, direction: GradientDrawable.Orientation, colors: IntArray
    ): Shader? = when (direction) {
        // draw the gradient from the top to the bottom
        GradientDrawable.Orientation.TOP_BOTTOM -> GXLinearColorGradient(
            0F, 0F, 0F, height, colors, null, Shader.TileMode.CLAMP
        )
        // raw the gradient from the bottom-left to the top-right
        GradientDrawable.Orientation.BOTTOM_TOP -> GXLinearColorGradient(
            0F, height, 0F, 0F, colors, null, Shader.TileMode.CLAMP
        )
        // draw the gradient from the left to the right
        GradientDrawable.Orientation.LEFT_RIGHT -> GXLinearColorGradient(
            0F, 0F, width, 0F, colors, null, Shader.TileMode.CLAMP
        )
        // draw the gradient from the right to the left
        GradientDrawable.Orientation.RIGHT_LEFT -> GXLinearColorGradient(
            width, 0F, 0F, 0F, colors, null, Shader.TileMode.CLAMP
        )
        // draw the gradient from the top-left to the bottom-right
        GradientDrawable.Orientation.TL_BR -> GXLinearColorGradient(
            0F, 0F, width, height, colors, null, Shader.TileMode.CLAMP
        )
        // draw the gradient from the top-right to the bottom-left
        GradientDrawable.Orientation.TR_BL -> GXLinearColorGradient(
            width, 0F, 0F, height, colors, null, Shader.TileMode.CLAMP
        )
        // draw the gradient from the bottom-right to the top-left
        GradientDrawable.Orientation.BR_TL -> GXLinearColorGradient(
            width, height, 0F, 0F, colors, null, Shader.TileMode.CLAMP
        )
        // draw the gradient from the bottom-left to the top-right
        GradientDrawable.Orientation.BL_TR -> GXLinearColorGradient(
            0F, height, width, 0F, colors, null, Shader.TileMode.CLAMP
        )
        else -> null
    }

    fun getLinearGradientColors(linear: List<String>): MutableList<GXColor> {
        val colors = mutableListOf<String>()
        linear.forEach {
            if (!it.startsWith("to")) {
                colors.add(it)
            }
        }
        val intColors = mutableListOf<GXColor>()
        colors.forEach { color ->
            intColors.add(
                GXColor.create(color)
                    ?: throw IllegalArgumentException("linearColor create color error")
            )
        }
        return intColors
    }

    fun getLinearGradient(linear: String): List<String> {
        val result = mutableListOf<String>()
        try {
            val substring = linear.substring(linear.indexOf("(") + 1, linear.lastIndexOf(")"))
            when {
                substring.contains("rgba") -> {
                    substring.split("rgba").forEachIndexed { index, s ->
                        var content = s.trim()
                        if (content.endsWith(",")) {
                            content = content.substring(0, content.length - 1)
                        }
                        if (index > 0) {
                            result.add("rgba$content")
                        } else {
                            result.add(content)
                        }
                    }
                }
                substring.contains("rgb") -> {
                    substring.split("rgb").forEachIndexed { index, s ->
                        var content = s.trim()
                        if (content.endsWith(",")) {
                            content = content.substring(0, content.length - 1)
                        }
                        if (index > 0) {
                            result.add("rgb$content")
                        } else {
                            result.add(content)
                        }
                    }
                }
                else -> {
                    substring.split(",").forEach {
                        result.add(it.trim())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return result
    }

    fun getDirection(linear: List<String>): GradientDrawable.Orientation {
        if (linear.isNotEmpty()) {
            return when (linear[0]) {
                "to right" -> GradientDrawable.Orientation.LEFT_RIGHT
                "toright" -> GradientDrawable.Orientation.LEFT_RIGHT

                "to left" -> GradientDrawable.Orientation.RIGHT_LEFT
                "toleft" -> GradientDrawable.Orientation.RIGHT_LEFT

                "totop" -> GradientDrawable.Orientation.BOTTOM_TOP
                "to top" -> GradientDrawable.Orientation.BOTTOM_TOP

                "tobottom" -> GradientDrawable.Orientation.TOP_BOTTOM
                "to bottom" -> GradientDrawable.Orientation.TOP_BOTTOM

                "totopleft" -> GradientDrawable.Orientation.BR_TL
                "to top left" -> GradientDrawable.Orientation.BR_TL

                "totopright" -> GradientDrawable.Orientation.BL_TR
                "to top right" -> GradientDrawable.Orientation.BL_TR

                "tobottomleft" -> GradientDrawable.Orientation.TR_BL
                "to bottom left" -> GradientDrawable.Orientation.TR_BL

                "tobottomright" -> GradientDrawable.Orientation.TL_BR
                "to bottom right" -> GradientDrawable.Orientation.TL_BR
                else -> GradientDrawable.Orientation.TOP_BOTTOM
            }
        }
        return GradientDrawable.Orientation.TOP_BOTTOM
    }

    fun fitContent(css: JSONObject): Boolean? {
        val value = css.getBoolean(GXTemplateKey.STYLE_FIT_CONTENT)
        if (value != null) {
            return value
        }
        return null
    }

    fun fitContent(target: String): Boolean {
        return target.toBoolean()
    }

    fun includeFontPadding(target: String): Boolean {
        return target.toBoolean()
    }
}

