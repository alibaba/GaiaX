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

import android.graphics.Typeface
import android.text.TextUtils
import android.view.View
import app.visly.stretch.Rect
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.template.utils.GXTemplateUtils

/**
 * @suppress
 */
data class GXStyle(
    var fontSize: GXSize? = null,
    var fontFamily: Typeface? = null,
    var fontWeight: Typeface? = null,
    var fontLines: Int? = null,
    var fontColor: GXColor? = null,
    var fontTextOverflow: TextUtils.TruncateAt? = null,
    var fontTextAlign: Int? = null,
    var backgroundColor: GXColor? = null,
    var backgroundImage: GXLinearColor? = null,
    var opacity: Float? = null,
    var overflow: Boolean? = null,
    var display: Int? = null,
    var hidden: Int? = null,
    var padding: Rect<GXSize>? = null,
    var borderWidth: GXSize? = null,
    var borderColor: GXColor? = null,
    var borderRadius: GXRoundedCorner? = null,
    var fontLineHeight: GXSize? = null,
    var fontTextDecoration: Int? = null,
    var mode: GXMode? = null,
    var boxShadow: GXBoxShadow? = null,
    var backdropFilter: GXBackdropFilter? = null,
    var fitContent: Boolean? = null
) {

    fun isEmpty(): Boolean {
        return fontSize == null && fontFamily == null && fontWeight == null && fontLines == null && fontColor == null && fontTextOverflow == null && fontTextAlign == null && backgroundColor == null && backgroundImage == null && opacity == null && overflow == null && display == null && hidden == null && padding == null && borderWidth == null && borderColor == null && borderRadius == null && fontLineHeight == null && fontTextDecoration == null && mode == null && fitContent == null && boxShadow == null
    }

    fun isEmptyStyle(): Boolean {
        return backgroundColor == null && backgroundImage == null && opacity == null && backdropFilter == null && overflow == null && borderWidth == null && borderColor == null && borderRadius == null && boxShadow == null
    }

    fun isInvisible(): Boolean {
        return display == View.INVISIBLE || display == View.GONE || hidden == View.INVISIBLE
    }

    companion object {

        fun createByExtend(css: JSONObject): GXStyle {
            if (css.isEmpty()) {
                return GXStyle()
            }
            val convertStyle = GXStyleConvert.instance
            return GXStyle(
                fontSize = convertStyle.font(css),
                fontFamily = convertStyle.fontFamily(css),
                fontWeight = convertStyle.fontWeight(css),
                fontLines = convertStyle.fontLines(css),
                fontColor = convertStyle.fontColor(css),
                fontTextOverflow = convertStyle.fontTextOverflow(css),
                fontTextAlign = convertStyle.fontTextAlign(css),
                backgroundColor = convertStyle.backgroundColor(css),
                backgroundImage = convertStyle.backgroundImage(css),
                mode = convertStyle.mode(css),
                opacity = convertStyle.opacity(css),
                borderRadius = convertStyle.borderRadius(css),
                overflow = convertStyle.overflow(css),
                display = convertStyle.display(css),
                hidden = convertStyle.hidden(css),
                padding = convertStyle.padding(css),
                borderWidth = convertStyle.borderWidth(css),
                borderColor = convertStyle.borderColor(css),
                fontLineHeight = convertStyle.fontLineHeight(css),
                fontTextDecoration = convertStyle.textDecoration(css),
                boxShadow = convertStyle.boxShadow(css),
                backdropFilter = convertStyle.backdropFilter(css),
                fitContent = if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isCompatibilityDataBindingFitContent == true) {
                    convertStyle.fitContent(css)
                } else {
                    null
                }
            )
        }

        fun create(css: JSONObject): GXStyle {
            if (css.isEmpty()) {
                return GXStyle()
            }
            val gxStyle = GXStyle()

            // 默认值
            gxStyle.fontTextOverflow = TextUtils.TruncateAt.END

            val convertStyle = GXStyleConvert.instance

            // 处理值
            css.forEach {
                val key: String = it.key
                val value: String = it.value.toString()
                when (key) {
                    GXTemplateKey.STYLE_FONT_SIZE -> gxStyle.fontSize = convertStyle.font(value)
                    GXTemplateKey.STYLE_FONT_FAMILY -> gxStyle.fontFamily =
                        convertStyle.fontFamily(value)
                    GXTemplateKey.STYLE_FONT_WEIGHT -> gxStyle.fontWeight =
                        convertStyle.fontWeight(value)
                    GXTemplateKey.STYLE_FONT_LINES -> gxStyle.fontLines =
                        convertStyle.fontLines(value)
                    GXTemplateKey.STYLE_FONT_COLOR -> gxStyle.fontColor =
                        convertStyle.fontColor(value)
                    GXTemplateKey.STYLE_FONT_TEXT_OVERFLOW -> gxStyle.fontTextOverflow =
                        convertStyle.fontTextOverflow(value)
                    GXTemplateKey.STYLE_FONT_TEXT_ALIGN -> gxStyle.fontTextAlign =
                        convertStyle.fontTextAlign(value)
                    GXTemplateKey.STYLE_FONT_TEXT_DECORATION -> gxStyle.fontTextDecoration =
                        convertStyle.textDecoration(value)
                    GXTemplateKey.STYLE_BACKGROUND_COLOR -> gxStyle.backgroundColor =
                        convertStyle.backgroundColor(value)
                    GXTemplateKey.STYLE_BACKGROUND_IMAGE -> gxStyle.backgroundImage =
                        convertStyle.backgroundImage(value)
                    GXTemplateKey.STYLE_MODE -> if (gxStyle.mode == null) gxStyle.mode =
                        convertStyle.mode(css)
                    GXTemplateKey.STYLE_OPACITY -> gxStyle.opacity = convertStyle.opacity(value)
                    GXTemplateKey.STYLE_BORDER_RADIUS, GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS, GXTemplateKey.STYLE_BORDER_TOP_RIGHT_RADIUS, GXTemplateKey.STYLE_BORDER_BOTTOM_LEFT_RADIUS, GXTemplateKey.STYLE_BORDER_BOTTOM_RIGHT_RADIUS -> if (gxStyle.borderRadius == null) gxStyle.borderRadius =
                        convertStyle.borderRadius(css)
                    GXTemplateKey.FLEXBOX_OVERFLOW -> gxStyle.overflow =
                        convertStyle.overflow(value)
                    GXTemplateKey.FLEXBOX_DISPLAY -> gxStyle.display = convertStyle.display(value)
                    GXTemplateKey.STYLE_HIDDEN -> gxStyle.hidden = convertStyle.hidden(value)
                    GXTemplateKey.GAIAX_LAYER_EDGE_INSETS, GXTemplateKey.FLEXBOX_PADDING, GXTemplateKey.FLEXBOX_PADDING_LEFT, GXTemplateKey.FLEXBOX_PADDING_RIGHT, GXTemplateKey.FLEXBOX_PADDING_TOP, GXTemplateKey.FLEXBOX_PADDING_BOTTOM -> if (gxStyle.padding == null) gxStyle.padding =
                        convertStyle.padding(css)
                    GXTemplateKey.STYLE_BORDER_WIDTH -> gxStyle.borderWidth =
                        convertStyle.borderWidth(value)
                    GXTemplateKey.STYLE_BORDER_COLOR -> gxStyle.borderColor =
                        convertStyle.borderColor(value)
                    GXTemplateKey.STYLE_FONT_LINE_HEIGHT -> gxStyle.fontLineHeight =
                        convertStyle.fontLineHeight(value)
                    GXTemplateKey.STYLE_BOX_SHADOW -> gxStyle.boxShadow =
                        convertStyle.boxShadow(value)
                    GXTemplateKey.STYLE_BACKDROP_FILTER -> gxStyle.backdropFilter =
                        convertStyle.backdropFilter(value)
                    GXTemplateKey.STYLE_FIT_CONTENT -> gxStyle.fitContent =
                        convertStyle.fitContent(css)
                }
            }
            return gxStyle
        }

        fun create(lowPriorityStyle: GXStyle, heightPriorityStyle: GXStyle): GXStyle {
            return GXStyle(
                fontSize = heightPriorityStyle.fontSize ?: lowPriorityStyle.fontSize,
                fontFamily = heightPriorityStyle.fontFamily ?: lowPriorityStyle.fontFamily,
                fontWeight = heightPriorityStyle.fontWeight ?: lowPriorityStyle.fontWeight,
                fontLines = heightPriorityStyle.fontLines ?: lowPriorityStyle.fontLines,
                fontColor = heightPriorityStyle.fontColor ?: lowPriorityStyle.fontColor,
                fontTextOverflow = heightPriorityStyle.fontTextOverflow
                    ?: lowPriorityStyle.fontTextOverflow,
                fontTextAlign = heightPriorityStyle.fontTextAlign ?: lowPriorityStyle.fontTextAlign,
                backgroundColor = heightPriorityStyle.backgroundColor
                    ?: lowPriorityStyle.backgroundColor,
                backgroundImage = heightPriorityStyle.backgroundImage
                    ?: lowPriorityStyle.backgroundImage,
                mode = heightPriorityStyle.mode ?: lowPriorityStyle.mode,
                opacity = heightPriorityStyle.opacity ?: lowPriorityStyle.opacity,
                overflow = heightPriorityStyle.overflow ?: lowPriorityStyle.overflow,
                display = heightPriorityStyle.display ?: lowPriorityStyle.display,
                hidden = heightPriorityStyle.hidden ?: lowPriorityStyle.hidden,
                padding = GXTemplateUtils.createRectGXSizeByPriority(
                    heightPriorityStyle.padding, lowPriorityStyle.padding
                ),
                borderWidth = heightPriorityStyle.borderWidth ?: lowPriorityStyle.borderWidth,
                borderColor = heightPriorityStyle.borderColor ?: lowPriorityStyle.borderColor,
                fontLineHeight = heightPriorityStyle.fontLineHeight
                    ?: lowPriorityStyle.fontLineHeight,
                fontTextDecoration = heightPriorityStyle.fontTextDecoration
                    ?: lowPriorityStyle.fontTextDecoration,
                borderRadius = heightPriorityStyle.borderRadius ?: lowPriorityStyle.borderRadius,
                boxShadow = heightPriorityStyle.boxShadow ?: lowPriorityStyle.boxShadow,
                backdropFilter = heightPriorityStyle.backdropFilter
                    ?: lowPriorityStyle.backdropFilter,
                fitContent = if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isCompatibilityDataBindingFitContent == true) {
                    if (lowPriorityStyle.fitContent == true && heightPriorityStyle.fitContent == false) {
                        lowPriorityStyle.fitContent
                    } else {
                        heightPriorityStyle.fitContent ?: lowPriorityStyle.fitContent
                    }
                } else {
                    heightPriorityStyle.fitContent ?: lowPriorityStyle.fitContent
                }
            )
        }
    }
}

