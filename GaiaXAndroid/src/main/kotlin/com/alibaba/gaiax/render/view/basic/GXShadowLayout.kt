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
import android.graphics.*
import android.os.Build
import android.util.AttributeSet
import android.widget.AbsoluteLayout
import androidx.annotation.Keep
import androidx.annotation.RequiresApi
import com.alibaba.gaiax.render.view.basic.boxshadow.GXBlurMaskBitmapShadowDrawable
import com.alibaba.gaiax.render.view.basic.boxshadow.GXBlurMaskShadowDrawable
import com.alibaba.gaiax.render.view.basic.boxshadow.GXShadowDrawable
import com.alibaba.gaiax.template.GXStyle
import kotlin.math.absoluteValue

/**
 * @suppress
 */
@Keep
open class GXShadowLayout : AbsoluteLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    var mShadowColor = Color.GRAY
    private var mShadowBlur = 0f
    private var mShadowInset = false
    private var mShadowSpread = 0f
    private var mBoxRadius = 0f
    private var mTopLeftBoxRadius = 0f
    private var mTopRightBoxRadius = 0f
    private var mBottomLeftBoxRadius = 0f
    private var mBottomRightBoxRadius = 0f
    private var mShadowVerticalOffset = 0f
    private var mShadowHorizontalOffset = 0f

    private val clipPath = Path()
    private val clipPaint = Paint().apply {
        isDither = true
        isAntiAlias = true
        xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    private val shadowPath = Path()

    private val shadowDrawable: GXShadowDrawable =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            GXBlurMaskShadowDrawable(shadowPath)
        } else {
            GXBlurMaskBitmapShadowDrawable(shadowPath)
        }

    init {
        setWillNotDraw(false)
    }

    override fun draw(canvas: Canvas) {
        drawShadow(canvas)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val saveCount = canvas.saveLayer(null, null)
            try {
                super.draw(canvas)
                clipRadius(canvas)
            } finally {
                canvas.restoreToCount(saveCount)
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        shadowDrawable.setBounds(0, 0, w, h)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBoxRadius(
                this.mTopLeftBoxRadius,
                this.mTopRightBoxRadius,
                this.mBottomLeftBoxRadius,
                this.mBottomRightBoxRadius
            )
        }
    }

    private fun clipRadius(canvas: Canvas) {
        if (mBoxRadius > 0 || mTopLeftBoxRadius > 0 || mTopRightBoxRadius > 0 || mBottomLeftBoxRadius > 0 || mBottomRightBoxRadius > 0) {
            canvas.drawPath(clipPath, clipPaint)
        }
    }

    private fun drawShadow(canvas: Canvas) {
        shadowDrawable.draw(canvas)
    }

    fun setShadowYOffset(shadowVerticalOffset: Float) {
        this.mShadowVerticalOffset = shadowVerticalOffset
        resetShadowOffset()
        invalidate()
    }


    fun getShadowVerticalOffset(): Float = this.mShadowVerticalOffset

    fun setShadowXOffset(shadowHorizontalOffset: Float) {
        this.mShadowHorizontalOffset = shadowHorizontalOffset
        resetShadowOffset()
        invalidate()
    }


    fun getShadowHorizontalOffset(): Float = this.mShadowHorizontalOffset

    fun setShadowColor(shadowColor: Int) {
        this.mShadowColor = shadowColor
        shadowDrawable.setShadowColor(shadowColor)
        invalidate()
    }

    fun getShadowColor(): Int = this.mShadowColor

    fun setShadowBlur(shadowBlur: Float) {
        this.mShadowBlur = shadowBlur
        shadowDrawable.setShadowBlur(shadowBlur)
        invalidate()
    }

    fun setShadowInset(shadowInset: Boolean) {
        this.mShadowInset = shadowInset
        shadowDrawable.setShadowInset(shadowInset)
        invalidate()
    }

    fun isShadowInset(): Boolean = this.mShadowInset

    fun setShadowSpread(shadowSpread: Float) {
        this.mShadowSpread = shadowSpread
        resetShadowOffset()
        invalidate()
    }


    fun getShadowSpread() = mShadowSpread

    fun setBoxRadius(radius: Float) {
        this.mBoxRadius = radius.absoluteValue
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setBoxRadius(this.mBoxRadius, this.mBoxRadius, this.mBoxRadius, this.mBoxRadius)
        }
    }

    fun getRadius(): Float = this.mBoxRadius

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun setBoxRadius(
        topLeft: Float,
        topRight: Float,
        bottomLeft: Float,
        bottomRight: Float
    ) {
        this.mTopLeftBoxRadius = topLeft.absoluteValue
        this.mTopRightBoxRadius = topRight.absoluteValue
        this.mBottomLeftBoxRadius = bottomLeft.absoluteValue
        this.mBottomRightBoxRadius = bottomRight.absoluteValue

        clipPath.reset()
        clipPath.fillType = Path.FillType.INVERSE_WINDING
        clipPath.addRoundRect2(
            this.mTopLeftBoxRadius,
            this.mTopRightBoxRadius,
            this.mBottomLeftBoxRadius,
            this.mBottomRightBoxRadius,
            width.toFloat(),
            height.toFloat()
        )

        shadowPath.reset()
        shadowPathOffsetX = 0f
        shadowPathOffsetY = 0f
        shadowPath.fillType = Path.FillType.WINDING
        shadowPath.addRoundRect2(
            this.mTopLeftBoxRadius,
            this.mTopRightBoxRadius,
            this.mBottomLeftBoxRadius,
            this.mBottomRightBoxRadius,
            width.toFloat() + mShadowSpread * 2,
            height.toFloat() + mShadowSpread * 2
        )

        resetShadowOffset()

        invalidate()
    }

    private var shadowPathOffsetX = 0f
    private var shadowPathOffsetY = 0f

    private fun resetShadowOffset() {
        shadowPath.offset(-shadowPathOffsetX, -shadowPathOffsetY)
        shadowPathOffsetX = -mShadowSpread + mShadowHorizontalOffset
        shadowPathOffsetY = -mShadowSpread + mShadowVerticalOffset
        shadowPath.offset(shadowPathOffsetX, shadowPathOffsetY)
        shadowDrawable.invalidateCache()
    }

    fun getTopLeftRadius() = mTopLeftBoxRadius


    fun getTopRightRadius() = mTopRightBoxRadius


    fun getBottomRightRadius() = mBottomRightBoxRadius


    fun getBottomLeftRadius() = mBottomLeftBoxRadius

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun Path.addRoundRect2(tL: Float, tR: Float, bL: Float, bR: Float, w: Float, h: Float) {
        val radii = floatArrayOf(tL, tL, tR, tR, bR, bR, bL, bL)
        addRoundRect(0f, 0f, w, h, radii, Path.Direction.CW)
    }

    fun setStyle(style: GXStyle?) {
        val boxShadow = style?.boxShadow
        if (boxShadow != null) {
            this.setShadowYOffset(boxShadow.yOffset.valueFloat)
            this.setShadowXOffset(boxShadow.xOffset.valueFloat)
            this.setShadowColor(boxShadow.color.value(this.context))
            this.setShadowBlur(boxShadow.blurOffset.valueFloat)
            this.setShadowSpread(boxShadow.spreadOffset.valueFloat)
        }
        val borderRadius = style?.borderRadius
        if (borderRadius != null) {
            val topLeft = borderRadius.topLeft?.valueFloat ?: 0F
            val topRight = borderRadius.topRight?.valueFloat ?: 0F
            val bottomLeft = borderRadius.bottomLeft?.valueFloat ?: 0F
            val bottomRight = borderRadius.bottomRight?.valueFloat ?: 0F
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                this.setBoxRadius(topLeft, topRight, bottomLeft, bottomRight)
            }
        }

    }
}


