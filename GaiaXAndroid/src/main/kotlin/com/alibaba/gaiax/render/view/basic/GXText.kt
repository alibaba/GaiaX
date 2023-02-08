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
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.TextView
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatTextView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.utils.GXAccessibilityUtils
import com.alibaba.gaiax.render.view.*
import com.alibaba.gaiax.render.view.drawable.GXRoundCornerBorderGradientDrawable
import com.alibaba.gaiax.template.GXCss
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * @suppress
 */
@Keep
open class GXText : AppCompatTextView, GXIViewBindData, GXIRoundCorner {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    override fun onBindData(data: JSONObject?) {
        val content = getContent(data)
        bindText(this, content)
        bindDesc(this, content, data)
    }

    open fun bindText(textView: TextView, content: CharSequence) {
        textView.text = content
    }

    private fun bindDesc(textView: TextView, content: CharSequence, data: JSONObject?) {
        GXAccessibilityUtils.accessibilityOfText(textView, data, content)
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

    private var lastRadius: FloatArray? = null

    override fun setRoundCornerRadius(radius: FloatArray) {
        if (!this.lastRadius.contentEquals(radius) && radius.size == 8) {
            val tl = radius[0]
            val tr = radius[2]
            val bl = radius[4]
            val br = radius[6]
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (tl == tr && tr == bl && bl == br && tl > 0) {
                    this.clipToOutline = true
                    this.outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View, outline: Outline) {
                            if (alpha >= 0.0f) {
                                outline.alpha = alpha
                            }
                            outline.setRoundRect(0, 0, view.width, view.height, tl)
                        }
                    }
                } else {
                    this.clipToOutline = false
                    this.outlineProvider = null
                }
            }
            this.lastRadius = radius
        }
    }

    override fun setRoundCornerBorder(borderColor: Int, borderWidth: Float, radius: FloatArray) {
        if (background == null) {
            val target = GXRoundCornerBorderGradientDrawable()
            target.shape = GradientDrawable.RECTANGLE
            target.cornerRadii = radius
            target.setStroke(borderWidth.toInt(), borderColor)
            background = target
        } else if (background is GradientDrawable) {
            val target = background as GradientDrawable
            target.setStroke(borderWidth.toInt(), borderColor)
            target.cornerRadii = radius
        }
    }
}