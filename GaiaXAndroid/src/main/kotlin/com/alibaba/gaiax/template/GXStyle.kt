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
    val fontSize: GXSize? = null,
    val fontFamily: Typeface? = null,
    val fontWeight: Typeface? = null,
    val fontLines: Int? = null,
    val fontColor: GXColor? = null,
    val fontTextOverflow: TextUtils.TruncateAt? = null,
    val fontTextAlign: Int? = null,
    val backgroundColor: GXColor? = null,
    val backgroundImage: GXLinearColor? = null,
    val opacity: Float? = null,
    val overflow: Boolean? = null,
    val display: Int? = null,
    val hidden: Int? = null,
    val padding: Rect<GXSize>? = null,
    val borderWidth: GXSize? = null,
    val borderColor: GXColor? = null,
    val borderRadius: GXRoundedCorner? = null,
    val fontLineHeight: GXSize? = null,
    val fontTextDecoration: Int? = null,
    val mode: GXMode? = null,
    val boxShadow: GXBoxShadow? = null,
    val fitContent: Boolean? = null
) {

    fun isEmpty(): Boolean {
        return fontSize == null &&
                fontFamily == null &&
                fontWeight == null &&
                fontLines == null &&
                fontColor == null &&
                fontTextOverflow == null &&
                fontTextAlign == null &&
                backgroundColor == null &&
                backgroundImage == null &&
                opacity == null &&
                overflow == null &&
                display == null &&
                hidden == null &&
                padding == null &&
                borderWidth == null &&
                borderColor == null &&
                borderRadius == null &&
                fontLineHeight == null &&
                fontTextDecoration == null &&
                mode == null &&
                fitContent == null &&
                boxShadow == null
    }

    fun isEmptyStyle(): Boolean {
        return padding == null &&
                backgroundColor == null &&
                backgroundImage == null &&
                opacity == null &&
                overflow == null &&
                display == null &&
                hidden == null &&
                borderWidth == null &&
                borderColor == null &&
                borderRadius == null &&
                boxShadow == null
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
                fitContent = if (GXRegisterCenter.instance.extensionCompatibility?.isCompatibilityDataBindingFitContent() == true) {
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
                fitContent = convertStyle.fitContent(css)
            )
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
                    heightPriorityStyle.padding,
                    lowPriorityStyle.padding
                ),
                borderWidth = heightPriorityStyle.borderWidth ?: lowPriorityStyle.borderWidth,
                borderColor = heightPriorityStyle.borderColor ?: lowPriorityStyle.borderColor,
                fontLineHeight = heightPriorityStyle.fontLineHeight
                    ?: lowPriorityStyle.fontLineHeight,
                fontTextDecoration = heightPriorityStyle.fontTextDecoration
                    ?: lowPriorityStyle.fontTextDecoration,
                borderRadius = heightPriorityStyle.borderRadius ?: lowPriorityStyle.borderRadius,
                boxShadow = heightPriorityStyle.boxShadow ?: lowPriorityStyle.boxShadow,
                fitContent = heightPriorityStyle.fitContent ?: lowPriorityStyle.fitContent
            )
        }
    }
}
