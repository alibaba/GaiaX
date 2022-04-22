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

package com.alibaba.gaiax.render.view.basic

import android.content.Context
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.support.annotation.Keep
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.render.view.*
import com.alibaba.gaiax.template.GXCss
import com.alibaba.gaiax.template.GXTemplateKey
import kotlin.math.roundToInt

/**
 * @suppress
 */
@Keep
open class GXText : AppCompatTextView, GXIViewBindData {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onBindData(data: JSONObject) {
        val content = getContent(data)
        bindText(this, content)
        bindDesc(this, content, data)
    }

    open fun bindText(textView: TextView, content: CharSequence) {
        textView.text = content
    }

    private fun bindDesc(textView: TextView, content: CharSequence, data: JSONObject) {
        try {
            // 原有无障碍逻辑
            val desc = data[GXTemplateKey.GAIAX_ACCESSIBILITY_DESC] as? String
            if (desc != null && desc.isNotBlank()) {
                textView.contentDescription = desc
                textView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
            } else {
                textView.contentDescription = null
                if (content.isNotEmpty()) {
                    textView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    textView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            }

            // 新增Enable逻辑
            data.getBoolean(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)?.let { enable ->
                if (enable) {
                    textView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    textView.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (GXRegisterCenter.instance.processCompatible?.isPreventAccessibilityThrowException() == false) {
                throw e
            }
        }
    }

    private fun getContent(data: Any?): CharSequence {
        var content: CharSequence = ""
        if (data != null) {
            if (data is String) {
                content = data
            } else if (data is JSONObject) {
                content = getContent(data[GXTemplateKey.GAIAX_VALUE])
            } else if (data is CharSequence) {
                content = data
            } else {
                content = data.toString()
            }
        }
        return content
    }

    fun setTextStyle(css: GXCss) {
        val style = css.style
        val flexbox = css.flexBox

        setFontPadding(style.padding)

        setFontSize(style.fontSize?.valueFloat)

        setFontFamilyAndFontWeight(style)

        setFontColor(style)

        setFontBackgroundImage(style.backgroundImage)

        setFontLines(style.fontLines)

        setFontTextOverflow(style)

        setFontTextAlign(style)

        setFontTextLineHeight(style)

        setFontTextDecoration(style.fontTextDecoration)
    }

    fun setRoundCornerRadius(radiusArray: FloatArray) {
        if (radiusArray[0] == radiusArray[2] && radiusArray[2] == radiusArray[4] && radiusArray[4] == radiusArray[6]) {
            val radius = radiusArray[0]
            this.clipToOutline = radius > 0
            this.outlineProvider = object : ViewOutlineProvider() {
                override fun getOutline(view: View, outline: Outline) {
                    if (alpha >= 0.0f) {
                        outline.alpha = alpha
                    }
                    outline.setRoundRect(0, 0, view.width, view.height, radius)
                }
            }
        } else {
            // 不支持GXText的异圆角设置，请联系开发者
            throw IllegalArgumentException("Not support difference radius for GXText")
        }
    }

    fun setRoundCornerBorder(borderColor: Int, borderWidth: Float, radius: FloatArray) {
        val shape = GradientDrawable()
        shape.shape = GradientDrawable.RECTANGLE
        shape.cornerRadii = radius
        shape.setStroke(borderWidth.toDouble().roundToInt(), borderColor)
        background = shape
    }
}