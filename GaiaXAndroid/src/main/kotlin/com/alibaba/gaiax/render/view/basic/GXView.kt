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
import android.widget.AbsoluteLayout
import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.utils.GXAccessibilityUtils
import com.alibaba.gaiax.render.view.GXIRelease
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.GXIRoundCorner
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.render.view.drawable.GXRoundCornerBorderGradientDrawable
import com.alibaba.gaiax.template.GXBackdropFilter
import kotlin.math.roundToInt

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


    fun setBackdropFilter(
            gxTemplateContext: GXTemplateContext,
            gxBackdropFilter: GXBackdropFilter?
    ) {
        this.gxTemplateContext = gxTemplateContext
        if (gxBackdropFilter is GXBackdropFilter.Blur) {
            this.gxBackdropFilter = gxBackdropFilter
        } else if (gxBackdropFilter is GXBackdropFilter.None) {
            this.background = null
            this.gxBackdropFilter = null
        }
    }

//    private var preDrawListener: ViewTreeObserver.OnPreDrawListener? = null
//
//    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
//    private fun blur(
//            srcView: View,
//            offsetViewBounds: Rect,
//            target: GXView
//    ) {
//        Blurry
//                .with(target.context)
//                .radius(25)
//                .sampling(12)
//                .captureTargetRect(offsetViewBounds)
//                .color(Color.parseColor("#70FFFFFF"))
//                .capture(srcView)
//                .getAsync {
//                    // TODO 有过有异形圆角会有问题
//                    if (it != null) {
//                        target.background = GXBlurBitmapDrawable(resources, it)
//                    }
//                }
//    }
//
//
//    protected open fun getActivityDecorView(): View? {
//        var ctx = context
//        var i = 0
//        while (i < 4 && ctx != null && ctx !is Activity && ctx is ContextWrapper) {
//            ctx = (ctx as ContextWrapper).getBaseContext()
//            i++
//        }
//        return if (ctx is Activity) {
//            (ctx as Activity).getWindow().getDecorView()
//        } else {
//            null
//        }
//    }
//
//    override fun onAttachedToWindow() {
//        super.onAttachedToWindow()
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (gxBackdropFilter != null) {
//                preDrawListener = ViewTreeObserver.OnPreDrawListener {
//                    val blurView = this
//                    val capture = getActivityDecorView() as? ViewGroup
//                    if (capture != null) {
//                        val targetOffsetViewBounds = Rect()
//                        blurView.getDrawingRect(targetOffsetViewBounds)
//                        capture.offsetDescendantRectToMyCoords(blurView, targetOffsetViewBounds)
//
//                        val captureOffsetViewBounds = Rect()
//                        capture.getDrawingRect(captureOffsetViewBounds)
//                        capture.offsetDescendantRectToMyCoords(capture, captureOffsetViewBounds)
//
//                        blurView.post {
//                            val bitmap = Blurry
//                                    .with(blurView.context)
//                                    .radius(25)
//                                    .sampling(12)
//                                    .captureTargetRect(targetOffsetViewBounds)
////                                    .color(Color.parseColor("#70FFFFFF"))
//                                    .capture(capture)
//                                    .get()
//                            if (bitmap != null) {
//                                blurView.blurBitmap = bitmap
//                                blurView.invalidate()
//                            }
//                        }
//
//                    }
//                    true
//                }
//                preDrawListener?.let {
//                    this.viewTreeObserver.addOnPreDrawListener(it)
//                }
//            }
//        }
//    }
//
//    private var blurBitmap: Bitmap? = null
//
//    override fun onDetachedFromWindow() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            if (gxBackdropFilter != null) {
//                preDrawListener?.let {
//                    this.viewTreeObserver.removeOnPreDrawListener(it)
//                }
//            }
//        }
//        super.onDetachedFromWindow()
//    }
//
//    private val mRectDst = Rect()
//    private val mRectSrc = Rect()
//    override fun dispatchDraw(canvas: Canvas?) {
//        blurBitmap?.let {
//            mRectSrc.right = it.width
//            mRectSrc.bottom = it.height
//            mRectDst.top = 0
//            mRectDst.left = 0
//            mRectDst.right = layoutParams.width
//            mRectDst.bottom = layoutParams.height
//            canvas?.drawBitmap(it, mRectSrc, mRectDst, null)
//        }
//        super.dispatchDraw(canvas)
//    }

    override fun release() {
        gxTemplateContext = null
    }

}