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
import android.graphics.Canvas
import android.graphics.Outline
import android.graphics.Path
import android.graphics.RectF
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.AbsoluteLayout
import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.utils.GXAccessibilityUtils
import com.alibaba.gaiax.render.view.GXIRelease
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.GXIRoundCorner
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.render.view.blur.GXBlurHelper
import com.alibaba.gaiax.render.view.drawable.GXRoundCornerBorderGradientDrawable
import com.alibaba.gaiax.template.GXBackdropFilter


/**
 * @suppress
 */
@Keep
open class GXView : AbsoluteLayout, GXIViewBindData, GXIRootView, GXIRoundCorner, GXIRelease {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    private var gxBlurHelper: GXBlurHelper? = null

    var gxBackdropFilter: GXBackdropFilter? = null

    var gxTemplateContext: GXTemplateContext? = null

    override fun setTemplateContext(gxContext: GXTemplateContext?) {
        this.gxTemplateContext = gxContext
    }

    override fun getTemplateContext(): GXTemplateContext? {
        return gxTemplateContext
    }

    override fun onBindData(data: JSONObject?) {
        GXAccessibilityUtils.accessibilityOfView(this, data)
    }

    internal var radius: FloatArray? = null
    override fun setRoundCornerRadius(radius: FloatArray) {
        this.radius = radius
        if (radius.size == 8) {
            val tl = radius[0]
            val tr = radius[2]
            val bl = radius[4]
            val br = radius[6]
            if (tl <= 0 && tr <= 0 && bl <= 0 && br <= 0) {
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (tl == tr && tr == bl && bl == br) {
                    path = null
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

                    // 异圆角用path实现
                    if (path == null) {
                        path = Path()
                    } else {
                        path?.reset()
                    }
                    path?.addRoundRect(RectF(0f, 0f, this.layoutParams.width.toFloat(), this.layoutParams.height.toFloat()), radius, Path.Direction.CW)
                }
            }
        }
    }

    var path: Path? = null

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
    }

    override fun dispatchDraw(canvas: Canvas) {
        // 裁剪背景
        path?.let { path ->
            canvas.clipPath(path)
        }

        if (gxBackdropFilter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                gxBlurHelper?.let { gxBlurHelper ->
                    gxBlurHelper.callbackDispatchDraw(canvas) {
                        super.dispatchDraw(canvas)
                    }
                }
            }
        } else {
            super.dispatchDraw(canvas)
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

    private var lastBackdropFilter: GXBackdropFilter? = null

    fun setBackdropFilter(
        gxTemplateContext: GXTemplateContext, gxBackdropFilter: GXBackdropFilter?
    ) {
        this.gxTemplateContext = gxTemplateContext
        if (gxBackdropFilter != lastBackdropFilter) {
            if (gxBackdropFilter is GXBackdropFilter.Blur) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if (gxBlurHelper == null) {
                        this.gxBlurHelper = GXBlurHelper(this)
                    }
                    this.gxBlurHelper?.radius = 25F
                    this.gxBlurHelper?.sampling = 12
                    // this.gxBlurHelper?.color = Color.parseColor("#70FFFFFF")
                }
                this.gxBackdropFilter = gxBackdropFilter
            } else if (gxBackdropFilter is GXBackdropFilter.None) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    this.gxBlurHelper?.innerRelease()
                    this.gxBlurHelper = null
                }
                this.background = null
                this.gxBackdropFilter = null
            }
        }
        this.lastBackdropFilter = gxBackdropFilter
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (gxBackdropFilter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                gxBlurHelper?.let { gxBlurHelper ->
                    gxBlurHelper.onAttachedToWindow()
                }
            }
        }
    }

    override fun onDetachedFromWindow() {
        if (gxBackdropFilter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                gxBlurHelper?.let { gxBlurHelper ->
                    gxBlurHelper.onDetachedFromWindow()
                }
            }
        }
        super.onDetachedFromWindow()
    }

    override fun draw(canvas: Canvas) {
        if (gxBackdropFilter != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                gxBlurHelper?.let { gxBlurHelper ->
                    gxBlurHelper.callbackDraw(canvas) {
                        super.draw(canvas)
                    }
                }
            }
        } else {
            super.draw(canvas)
        }
    }


    override fun release() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            gxBlurHelper?.innerRelease()
            gxBlurHelper = null
        }
        gxTemplateContext = null
    }

}