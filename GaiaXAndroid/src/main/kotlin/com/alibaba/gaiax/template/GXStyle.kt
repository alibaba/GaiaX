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

/**
 * @suppress
 */
data class GXStyle(
    private var fontSizeForTemplate: GXSize? = null,
    private var fontFamilyForTemplate: Typeface? = null,
    private var fontWeightForTemplate: Typeface? = null,
    private var fontLinesForTemplate: Int? = null,
    private var fontColorForTemplate: GXColor? = null,
    private var fontTextOverflowForTemplate: TextUtils.TruncateAt? = null,
    private var fontTextAlignForTemplate: Int? = null,
    private var backgroundColorForTemplate: GXColor? = null,
    private var backgroundImageForTemplate: GXLinearColor? = null,
    private var opacityForTemplate: Float? = null,
    private var overflowForTemplate: Boolean? = null,
    private var displayForTemplate: Int? = null,
    private var hiddenForTemplate: Int? = null,
    private var paddingForTemplate: Rect<GXSize>? = null,
    private var borderWidthForTemplate: GXSize? = null,
    private var borderColorForTemplate: GXColor? = null,
    private var borderRadiusForTemplate: GXRoundedCorner? = null,
    private var fontLineHeightForTemplate: GXSize? = null,
    private var fontTextDecorationForTemplate: Int? = null,
    private var modeForTemplate: GXMode? = null,
    private var boxShadowForTemplate: GXBoxShadow? = null,
    private var backdropFilterForTemplate: GXBackdropFilter? = null,
    private var fitContentForTemplate: Boolean? = null
) {

    private var fontSizeForExtend: GXSize? = null
    private var fontFamilyForExtend: Typeface? = null
    private var fontWeightForExtend: Typeface? = null
    private var fontLinesForExtend: Int? = null
    private var fontColorForExtend: GXColor? = null
    private var fontTextOverflowForExtend: TextUtils.TruncateAt? = null
    private var fontTextAlignForExtend: Int? = null
    private var backgroundColorForExtend: GXColor? = null
    private var backgroundImageForExtend: GXLinearColor? = null
    private var opacityForExtend: Float? = null
    private var overflowForExtend: Boolean? = null
    private var displayForExtend: Int? = null
    private var hiddenForExtend: Int? = null
    private var paddingForExtend: Rect<GXSize>? = null
    private var borderWidthForExtend: GXSize? = null
    private var borderColorForExtend: GXColor? = null
    private var borderRadiusForExtend: GXRoundedCorner? = null
    private var fontLineHeightForExtend: GXSize? = null
    private var fontTextDecorationForExtend: Int? = null
    private var modeForExtend: GXMode? = null
    private var boxShadowForExtend: GXBoxShadow? = null
    private var backdropFilterForExtend: GXBackdropFilter? = null
    private var fitContentForExtend: Boolean? = null

    val fontSize: GXSize?
        get() {
            return fontSizeForExtend ?: fontSizeForTemplate
        }

    val fontFamily: Typeface?
        get() {
            return fontFamilyForExtend ?: fontFamilyForTemplate
        }

    val fontWeight: Typeface?
        get() {
            return fontWeightForExtend ?: fontWeightForTemplate
        }

    val fontLines: Int?
        get() {
            return fontLinesForExtend ?: fontLinesForTemplate
        }

    val fontColor: GXColor?
        get() {
            return fontColorForExtend ?: fontColorForTemplate
        }

    val fontTextOverflow: TextUtils.TruncateAt?
        get() {
            return fontTextOverflowForExtend ?: fontTextOverflowForTemplate
        }

    val fontTextAlign: Int?
        get() {
            return fontTextAlignForExtend ?: fontTextAlignForTemplate
        }

    val backgroundColor: GXColor?
        get() {
            return backgroundColorForExtend ?: backgroundColorForTemplate
        }

    val backgroundImage: GXLinearColor?
        get() {
            return backgroundImageForExtend ?: backgroundImageForTemplate
        }

    val opacity: Float?
        get() {
            return opacityForExtend ?: opacityForTemplate
        }

    val overflow: Boolean?
        get() {
            return overflowForExtend ?: overflowForTemplate
        }

    val display: Int?
        get() {
            return displayForExtend ?: displayForTemplate
        }

    val hidden: Int?
        get() {
            return hiddenForExtend ?: hiddenForTemplate
        }

    val padding: Rect<GXSize>?
        get() {
            return paddingForExtend ?: paddingForTemplate
        }

    private var _paddingForAndroid: android.graphics.Rect? = null

    val paddingForAndroid: android.graphics.Rect
        get() {
            val target = padding
            if (target != null) {
                if (_paddingForAndroid == null) {
                    _paddingForAndroid = android.graphics.Rect(
                        target.start.valueInt,
                        target.top.valueInt,
                        target.end.valueInt,
                        target.bottom.valueInt
                    )
                }
            }
            return _paddingForAndroid ?: android.graphics.Rect()
        }

    val borderWidth: GXSize?
        get() {
            return borderWidthForExtend ?: borderWidthForTemplate
        }

    val borderColor: GXColor?
        get() {
            return borderColorForExtend ?: borderColorForTemplate
        }

    val borderRadius: GXRoundedCorner?
        get() {
            return borderRadiusForExtend ?: borderRadiusForTemplate
        }

    val fontLineHeight: GXSize?
        get() {
            return fontLineHeightForExtend ?: fontLineHeightForTemplate
        }

    val fontTextDecoration: Int?
        get() {
            return fontTextDecorationForExtend ?: fontTextDecorationForTemplate
        }

    val mode: GXMode?
        get() {
            return modeForExtend ?: modeForTemplate
        }

    val boxShadow: GXBoxShadow?
        get() {
            return boxShadowForExtend ?: boxShadowForTemplate
        }

    val backdropFilter: GXBackdropFilter?
        get() {
            return backdropFilterForExtend ?: backdropFilterForTemplate
        }

    val fitContent: Boolean?
        get() {
            return if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isCompatibilityDataBindingFitContent == true) {
                // 对优酷的逻辑特殊兼容
                if (fitContentForTemplate == true && fitContentForExtend == false) {
                    true
                } else {
                    fitContentForExtend ?: fitContentForTemplate
                }
            } else {
                fitContentForTemplate
            }
        }

    fun isEmptyStyle(): Boolean {
        return backgroundColor == null && backgroundImage == null && opacity == null && backdropFilter == null && overflow == null && borderWidth == null && borderColor == null && borderRadius == null && boxShadow == null
    }

    fun isInvisible(): Boolean {
        return display == View.INVISIBLE || display == View.GONE || hidden == View.INVISIBLE
    }

    fun updateByExtend(extendCssData: JSONObject) {
        val gxStyle = this
        val convertStyle = GXStyleConvert.instance

        extendCssData.forEach {
            val key: String = it.key
            val value = it.value
            when (key) {
                GXTemplateKey.STYLE_FONT_SIZE -> gxStyle.fontSizeForExtend =
                    convertStyle.font(value.toString())
                GXTemplateKey.STYLE_FONT_FAMILY -> gxStyle.fontFamilyForExtend =
                    convertStyle.fontFamily(value.toString())
                GXTemplateKey.STYLE_FONT_WEIGHT -> gxStyle.fontWeightForExtend =
                    convertStyle.fontWeight(value.toString())
                GXTemplateKey.STYLE_FONT_LINES -> gxStyle.fontLinesForExtend =
                    convertStyle.fontLines(value.toString())
                GXTemplateKey.STYLE_FONT_COLOR -> gxStyle.fontColorForExtend =
                    convertStyle.fontColor(value.toString())
                GXTemplateKey.STYLE_FONT_TEXT_OVERFLOW -> gxStyle.fontTextOverflowForExtend =
                    convertStyle.fontTextOverflow(value.toString())
                GXTemplateKey.STYLE_FONT_TEXT_ALIGN -> gxStyle.fontTextAlignForExtend =
                    convertStyle.fontTextAlign(value.toString())
                GXTemplateKey.STYLE_FONT_TEXT_DECORATION -> gxStyle.fontTextDecorationForExtend =
                    convertStyle.textDecoration(value.toString())
                GXTemplateKey.STYLE_BACKGROUND_COLOR -> gxStyle.backgroundColorForExtend =
                    convertStyle.backgroundColor(value.toString())
                GXTemplateKey.STYLE_BACKGROUND_IMAGE -> gxStyle.backgroundImageForExtend =
                    convertStyle.backgroundImage(value.toString())
                GXTemplateKey.STYLE_MODE -> if (gxStyle.modeForExtend == null) gxStyle.modeForExtend =
                    convertStyle.mode(extendCssData)
                GXTemplateKey.STYLE_OPACITY -> gxStyle.opacityForTemplate =
                    convertStyle.opacity(value.toString())
                GXTemplateKey.STYLE_BORDER_RADIUS, GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS, GXTemplateKey.STYLE_BORDER_TOP_RIGHT_RADIUS, GXTemplateKey.STYLE_BORDER_BOTTOM_LEFT_RADIUS, GXTemplateKey.STYLE_BORDER_BOTTOM_RIGHT_RADIUS -> if (gxStyle.borderRadiusForExtend == null) gxStyle.borderRadiusForExtend =
                    convertStyle.borderRadius(extendCssData)
                GXTemplateKey.FLEXBOX_OVERFLOW -> gxStyle.overflowForExtend =
                    convertStyle.overflow(value.toString())
                GXTemplateKey.FLEXBOX_DISPLAY -> gxStyle.displayForExtend =
                    convertStyle.display(value.toString())
                GXTemplateKey.STYLE_HIDDEN -> gxStyle.hiddenForExtend =
                    convertStyle.hidden(value.toString())
                GXTemplateKey.GAIAX_LAYER_EDGE_INSETS, GXTemplateKey.FLEXBOX_PADDING, GXTemplateKey.FLEXBOX_PADDING_LEFT, GXTemplateKey.FLEXBOX_PADDING_RIGHT, GXTemplateKey.FLEXBOX_PADDING_TOP, GXTemplateKey.FLEXBOX_PADDING_BOTTOM -> if (gxStyle.paddingForExtend == null) gxStyle.paddingForExtend =
                    convertStyle.padding(extendCssData)
                GXTemplateKey.STYLE_BORDER_WIDTH -> gxStyle.borderWidthForExtend =
                    convertStyle.borderWidth(value.toString())
                GXTemplateKey.STYLE_BORDER_COLOR -> gxStyle.borderColorForExtend =
                    convertStyle.borderColor(value.toString())
                GXTemplateKey.STYLE_FONT_LINE_HEIGHT -> gxStyle.fontLineHeightForExtend =
                    convertStyle.fontLineHeight(value.toString())
                GXTemplateKey.STYLE_BOX_SHADOW -> gxStyle.boxShadowForExtend =
                    convertStyle.boxShadow(value.toString())
                GXTemplateKey.STYLE_BACKDROP_FILTER -> gxStyle.backdropFilterForExtend =
                    convertStyle.backdropFilter(value.toString())
                GXTemplateKey.STYLE_FIT_CONTENT -> gxStyle.fitContentForExtend =
                    convertStyle.fitContent(value.toString())
            }
        }
    }

    fun reset() {
        fontSizeForExtend = null
        fontFamilyForExtend = null
        fontWeightForExtend = null
        fontLinesForExtend = null
        fontColorForExtend = null
        fontTextOverflowForExtend = null
        fontTextAlignForExtend = null
        backgroundColorForExtend = null
        backgroundImageForExtend = null
        opacityForExtend = null
        overflowForExtend = null
        displayForExtend = null
        hiddenForExtend = null
        paddingForExtend = null
        borderWidthForExtend = null
        borderColorForExtend = null
        borderRadiusForExtend = null
        fontLineHeightForExtend = null
        fontTextDecorationForExtend = null
        modeForExtend = null
        boxShadowForExtend = null
        backdropFilterForExtend = null
        fitContentForExtend = null
        _paddingForAndroid = null
    }

    fun updateByVisual(visual: GXStyle) {
        visual.fontSize?.let {
            fontSizeForExtend = it
        }
        visual.fontFamily?.let {
            fontFamilyForExtend = it
        }
        visual.fontWeight?.let {
            fontWeightForExtend = it
        }
        visual.fontLines?.let {
            fontLinesForExtend = it
        }
        visual.fontColor?.let {
            fontColorForExtend = it
        }
        visual.fontTextOverflow?.let {
            fontTextOverflowForExtend = it
        }
        visual.fontTextAlign?.let {
            fontTextAlignForExtend = it
        }
        visual.backgroundColor?.let {
            backgroundColorForExtend = it
        }
        visual.backgroundImage?.let {
            backgroundImageForExtend = it
        }
        visual.mode?.let {
            modeForExtend = it
        }
        visual.opacity?.let {
            opacityForExtend = it
        }
        visual.overflow?.let {
            overflowForExtend = it
        }
        visual.display?.let {
            displayForExtend = it
        }
        visual.hidden?.let {
            hiddenForExtend = it
        }
        visual.padding?.let {
            paddingForExtend = it
        }
        visual.borderWidth?.let {
            borderWidthForExtend = it
        }
        visual.borderColor?.let {
            borderColorForExtend = it
        }
        visual.fontLineHeight?.let {
            fontLineHeightForExtend = it
        }
        visual.fontTextDecoration?.let {
            fontTextDecorationForExtend = it
        }
        visual.borderRadius?.let {
            borderRadiusForExtend = it
        }
        visual.boxShadow?.let {
            boxShadowForExtend = it
        }
        visual.backdropFilter?.let {
            backdropFilterForExtend = it
        }
    }

    companion object {

        fun create(css: JSONObject): GXStyle {
            if (css.isEmpty()) {
                return GXStyle()
            }
            val gxStyle = GXStyle()

            // 默认值
            gxStyle.fontTextOverflowForTemplate = TextUtils.TruncateAt.END

            val convertStyle = GXStyleConvert.instance

            // 处理值
            css.forEach {
                val key: String = it.key
                val value = it.value
                when (key) {
                    GXTemplateKey.STYLE_FONT_SIZE -> gxStyle.fontSizeForTemplate =
                        convertStyle.font(value.toString())
                    GXTemplateKey.STYLE_FONT_FAMILY -> gxStyle.fontFamilyForTemplate =
                        convertStyle.fontFamily(value.toString())
                    GXTemplateKey.STYLE_FONT_WEIGHT -> gxStyle.fontWeightForTemplate =
                        convertStyle.fontWeight(value.toString())
                    GXTemplateKey.STYLE_FONT_LINES -> gxStyle.fontLinesForTemplate =
                        convertStyle.fontLines(value.toString())
                    GXTemplateKey.STYLE_FONT_COLOR -> gxStyle.fontColorForTemplate =
                        convertStyle.fontColor(value.toString())
                    GXTemplateKey.STYLE_FONT_TEXT_OVERFLOW -> gxStyle.fontTextOverflowForTemplate =
                        convertStyle.fontTextOverflow(value.toString())
                    GXTemplateKey.STYLE_FONT_TEXT_ALIGN -> gxStyle.fontTextAlignForTemplate =
                        convertStyle.fontTextAlign(value.toString())
                    GXTemplateKey.STYLE_FONT_TEXT_DECORATION -> gxStyle.fontTextDecorationForTemplate =
                        convertStyle.textDecoration(value.toString())
                    GXTemplateKey.STYLE_BACKGROUND_COLOR -> gxStyle.backgroundColorForTemplate =
                        convertStyle.backgroundColor(value.toString())
                    GXTemplateKey.STYLE_BACKGROUND_IMAGE -> gxStyle.backgroundImageForTemplate =
                        convertStyle.backgroundImage(value.toString())
                    GXTemplateKey.STYLE_MODE -> if (gxStyle.modeForTemplate == null) gxStyle.modeForTemplate =
                        convertStyle.mode(css)
                    GXTemplateKey.STYLE_OPACITY -> gxStyle.opacityForTemplate =
                        convertStyle.opacity(value.toString())
                    GXTemplateKey.STYLE_BORDER_RADIUS, GXTemplateKey.STYLE_BORDER_TOP_LEFT_RADIUS, GXTemplateKey.STYLE_BORDER_TOP_RIGHT_RADIUS, GXTemplateKey.STYLE_BORDER_BOTTOM_LEFT_RADIUS, GXTemplateKey.STYLE_BORDER_BOTTOM_RIGHT_RADIUS -> if (gxStyle.borderRadiusForTemplate == null) gxStyle.borderRadiusForTemplate =
                        convertStyle.borderRadius(css)
                    GXTemplateKey.FLEXBOX_OVERFLOW -> gxStyle.overflowForTemplate =
                        convertStyle.overflow(value.toString())
                    GXTemplateKey.FLEXBOX_DISPLAY -> gxStyle.displayForTemplate =
                        convertStyle.display(value.toString())
                    GXTemplateKey.STYLE_HIDDEN -> gxStyle.hiddenForTemplate =
                        convertStyle.hidden(value.toString())
                    GXTemplateKey.GAIAX_LAYER_EDGE_INSETS, GXTemplateKey.FLEXBOX_PADDING, GXTemplateKey.FLEXBOX_PADDING_LEFT, GXTemplateKey.FLEXBOX_PADDING_RIGHT, GXTemplateKey.FLEXBOX_PADDING_TOP, GXTemplateKey.FLEXBOX_PADDING_BOTTOM -> if (gxStyle.paddingForTemplate == null) gxStyle.paddingForTemplate =
                        convertStyle.padding(css)
                    GXTemplateKey.STYLE_BORDER_WIDTH -> gxStyle.borderWidthForTemplate =
                        convertStyle.borderWidth(value.toString())
                    GXTemplateKey.STYLE_BORDER_COLOR -> gxStyle.borderColorForTemplate =
                        convertStyle.borderColor(value.toString())
                    GXTemplateKey.STYLE_FONT_LINE_HEIGHT -> gxStyle.fontLineHeightForTemplate =
                        convertStyle.fontLineHeight(value.toString())
                    GXTemplateKey.STYLE_BOX_SHADOW -> gxStyle.boxShadowForTemplate =
                        convertStyle.boxShadow(value.toString())
                    GXTemplateKey.STYLE_BACKDROP_FILTER -> gxStyle.backdropFilterForTemplate =
                        convertStyle.backdropFilter(value.toString())
                    GXTemplateKey.STYLE_FIT_CONTENT -> gxStyle.fitContentForTemplate =
                        convertStyle.fitContent(value.toString())
                }
            }
            return gxStyle
        }
    }
}

