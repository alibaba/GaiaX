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
import android.graphics.Color
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.TextView
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatTextView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.render.utils.GXAccessibilityUtils
import com.alibaba.gaiax.render.view.GXIRoundCorner
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.render.view.drawable.GXRoundCornerBorderGradientDrawable
import com.alibaba.gaiax.render.view.setFontBackgroundImage
import com.alibaba.gaiax.render.view.setFontFamilyAndFontWeight
import com.alibaba.gaiax.render.view.setFontTextAlign
import com.alibaba.gaiax.render.view.setFontTextOverflow
import com.alibaba.gaiax.render.view.setTextLineHeight
import com.alibaba.gaiax.template.GXCss
import com.alibaba.gaiax.template.GXSize
import com.alibaba.gaiax.template.GXStyle
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

    override fun onResetData() {
        this.text = ""
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

        setIncludeFontPadding(style)
    }

    private fun setIncludeFontPadding(style: GXStyle) {
        style.includeFontPadding?.let {
            includeFontPadding = it
        }
    }

    fun reset() {
        lastLineHeight = null
        lastFontLines = null
        lastFontColor = null
        lastFontSize = null
        lastTextDecoration = null
        lastLeftPadding = null
        lastTopPadding = null
        lastRightPadding = null
        lastBottomPadding = null
        setPadding(0, 0, 0, 0)
    }


    private var lastTextDecoration: Int? = null
    private fun setFontTextDecoration(textDecoration: Int?) {
        if (textDecoration != null && textDecoration != lastTextDecoration) {
            this.paint.flags = textDecoration
            lastTextDecoration = textDecoration
        }
    }

    private var lastLeftPadding: Int? = null
    private var lastTopPadding: Int? = null
    private var lastRightPadding: Int? = null
    private var lastBottomPadding: Int? = null

    private fun setFontPadding(padding: app.visly.stretch.Rect<GXSize>?) {
        val leftPadding = padding?.start?.valueInt ?: 0
        val topPadding = padding?.top?.valueInt ?: 0
        val rightPadding = padding?.end?.valueInt ?: 0
        val bottomPadding = padding?.bottom?.valueInt ?: 0
        if (lastLeftPadding != leftPadding || lastTopPadding != topPadding || lastRightPadding != rightPadding || lastBottomPadding != bottomPadding) {
            this.setPadding(
                leftPadding,
                topPadding,
                rightPadding,
                bottomPadding
            )
            lastLeftPadding = leftPadding
            lastTopPadding = topPadding
            lastRightPadding = rightPadding
            lastBottomPadding = bottomPadding
        }
    }


    private var lastFontSize: Float? = null
    fun setFontSize(fontSize: Float?) {
        if (lastFontSize != fontSize && fontSize != null && fontSize >= 0) {
            setTextSize(TypedValue.COMPLEX_UNIT_PX, fontSize)
            lastFontSize = fontSize
        }
    }

    private var lastFontColor: Int? = null
    private fun setFontColor(style: GXStyle) {
        val fontColor = style.fontColor?.value(this.context) ?: Color.BLACK
        if (fontColor != lastFontColor) {
            this.setTextColor(fontColor)
            lastFontColor = fontColor
        }
    }

    private var lastFontLines: Int? = null

    fun setFontLines(fontLiens: Int?) {
        val result = fontLiens ?: 1
        if (lastFontLines != result) {
            when (result) {
                1 -> this.setSingleLine(true)
                0 -> this.maxLines = Int.MAX_VALUE
                else -> this.maxLines = result
            }
            this.lastFontLines = result
        }
    }

    private var lastLineHeight: Float? = null

    private fun setFontTextLineHeight(style: GXStyle) {
        val lineHeight = style.fontLineHeight?.valueFloat
        if (lineHeight != null && lastLineHeight != lineHeight) {
            val result =
                GXRegisterCenter.instance.extensionDynamicProperty?.convert(GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                    GXTemplateKey.STYLE_FONT_LINE_HEIGHT, lineHeight
                ).apply {
                    this.cssStyle = style
                })
            if (result != null) {
                this.setTextLineHeight(result as Float)
            } else {
                this.setTextLineHeight(lineHeight)
            }
            this.lastLineHeight = lineHeight
        }
    }

    override fun setRoundCornerRadius(radius: FloatArray) {
        if (radius.size == 8) {
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