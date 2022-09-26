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
import android.graphics.Rect
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.AbsoluteLayout
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.GXIRoundCorner
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.render.view.drawable.GXBlurBitmapDrawable
import com.alibaba.gaiax.render.view.drawable.GXRoundCornerBorderGradientDrawable
import com.alibaba.gaiax.template.GXBackdropFilter
import com.alibaba.gaiax.template.GXTemplateKey
import jp.wasabeef.blurry.Blurry
import kotlin.math.roundToInt

/**
 * @suppress
 */
@Keep
open class GXView : AbsoluteLayout,
    GXIViewBindData,
    GXIRootView,
    GXIRoundCorner {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    )

    private var gxBackdropFilter: GXBackdropFilter? = null

    private var gxTemplateContext: GXTemplateContext? = null

    override fun setTemplateContext(gxContext: GXTemplateContext?) {
        this.gxTemplateContext = gxContext
    }

    override fun getTemplateContext(): GXTemplateContext? {
        return gxTemplateContext
    }

    override fun onBindData(data: JSONObject?) {
        try {
            // 原有无障碍逻辑
            val accessibilityDesc = data?.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC)
            if (accessibilityDesc != null && accessibilityDesc.isNotEmpty()) {
                contentDescription = accessibilityDesc
                importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
            } else {
                importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
            }

            // 新增无障碍Enable逻辑
            data?.getBoolean(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)?.let { enable ->
                importantForAccessibility = if (enable) {
                    View.IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            }
        } catch (e: Exception) {
            if (GXRegisterCenter.instance.extensionCompatibility?.isPreventAccessibilityThrowException() == false) {
                throw e
            }
        }
    }

    override fun setRoundCornerRadius(radius: FloatArray) {
        if (radius.size == 8) {
            val tl = radius[0]
            val tr = radius[2]
            val bl = radius[4]
            val br = radius[6]
            if (tl == tr && tr == bl && bl == br && tl > 0) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    this.clipToOutline = true
                    this.outlineProvider = object : ViewOutlineProvider() {
                        override fun getOutline(view: View, outline: Outline) {
                            if (alpha >= 0.0f) {
                                outline.alpha = alpha
                            }
                            outline.setRoundRect(0, 0, view.width, view.height, tl)
                        }
                    }
                }
            }
        }
    }

    override fun setRoundCornerBorder(borderColor: Int, borderWidth: Float, radius: FloatArray) {
        when (background) {
            null -> {
                val shape = GXRoundCornerBorderGradientDrawable()
                shape.shape = GradientDrawable.RECTANGLE
                shape.cornerRadii = radius
                shape.setStroke(borderWidth.toDouble().roundToInt(), borderColor)
                background = shape
            }
            is GradientDrawable -> {
                (background as GradientDrawable).setStroke(
                    borderWidth.toDouble().roundToInt(), borderColor
                )
            }
            else -> {
                Log.e("[GaiaX]", "setRoundCornerBorder: not support current case")
            }
        }
    }

    fun onBlurChanged(gxTemplateContext: GXTemplateContext, gxImageView: GXImageView) {
        val target = this
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            val rootView = gxTemplateContext.rootView as? ViewGroup
            if (gxBackdropFilter != null && rootView != null) {

                val targetOffsetViewBounds = Rect()
                target.getDrawingRect(targetOffsetViewBounds)
                rootView.offsetDescendantRectToMyCoords(target, targetOffsetViewBounds)

                val imageOffsetViewBounds = Rect()
                gxImageView.getDrawingRect(imageOffsetViewBounds)
                rootView.offsetDescendantRectToMyCoords(gxImageView, imageOffsetViewBounds)

                if (imageOffsetViewBounds.contains(targetOffsetViewBounds)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                        if (target.isAttachedToWindow) {
                            blur(rootView, targetOffsetViewBounds, target)
                            return
                        }
                    }
                    target.post {
                        blur(rootView, targetOffsetViewBounds, target)
                    }
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private fun blur(
        rootView: ViewGroup,
        offsetViewBounds: Rect,
        target: GXView
    ) {
        Blurry.with(target.context)
            .radius(25)
            .sampling(8)
            .captureAcquireRect(offsetViewBounds)
            .color(Color.parseColor("#33FFFFFF"))
            .capture(rootView)
            .getAsync {
                // TODO 有过有异形圆角会有问题
                if (it != null) {
                    target.background = GXBlurBitmapDrawable(resources, it)
                }
            }
    }

    fun setBackdropFilter(
        gxTemplateContext: GXTemplateContext,
        gxBackdropFilter: GXBackdropFilter?
    ) {
        // TODO View高斯模糊和图片渲染有直接关系
        // 如果设置了高斯模糊，但是组件中没有图片，高斯模糊的逻辑也不会执行
        // 该操作主要是为了提高性能
        if (gxBackdropFilter is GXBackdropFilter.Blur) {
            gxTemplateContext.blurViews.add(this)
            this.gxBackdropFilter = gxBackdropFilter
        } else if (gxBackdropFilter is GXBackdropFilter.None) {
            gxTemplateContext.blurViews.remove(this)
            this.background = null
            this.gxBackdropFilter = null
        }
    }

}