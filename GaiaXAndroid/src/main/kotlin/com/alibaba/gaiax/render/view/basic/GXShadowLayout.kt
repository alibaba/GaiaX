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
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import android.support.annotation.Keep
import com.alibaba.gaiax.R
import com.alibaba.gaiax.render.view.basic.boxshadow.GXBlurMaskBitmapShadowDrawable
import com.alibaba.gaiax.render.view.basic.boxshadow.GXBlurMaskShadowDrawable
import com.alibaba.gaiax.render.view.basic.boxshadow.GXShadowDrawable
import com.alibaba.gaiax.template.GXStyle
import kotlin.math.absoluteValue

/**
 * @suppress
 */
@Keep
open class GXShadowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : AbsoluteLayout(context, attrs, defStyleAttr) {

    private var shadowColor = Color.GRAY
    private var shadowBlur = 0f
    private var shadowInset = false
    private var shadowSpread = 0f
    private var boxRadius = 0f
    private var topLeftBoxRadius = 0f
    private var topRightBoxRadius = 0f
    private var bottomLeftBoxRadius = 0f
    private var bottomRightBoxRadius = 0f
    private var shadowVerticalOffset = 0f
    private var shadowHorizontalOffset = 0f

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
        init(attrs, defStyleAttr)
    }

    private fun init(attrs: AttributeSet?, defStyleAttr: Int) {
        // Load attributes
        context.obtainStyledAttributes(attrs, R.styleable.GXBoxShadowLayout, defStyleAttr, 0)
            .apply {

                val vOffset =
                    getDimension(R.styleable.GXBoxShadowLayout_box_shadowOffsetVertical, 0f)
                setShadowYOffset(vOffset)

                val hOffset =
                    getDimension(R.styleable.GXBoxShadowLayout_box_shadowOffsetHorizontal, 0f)
                setShadowXOffset(hOffset)

                setShadowColor(getColor(R.styleable.GXBoxShadowLayout_box_shadowColor, Color.GRAY))

                setShadowBlur(getDimension(R.styleable.GXBoxShadowLayout_box_shadowBlur, 0f))

                setShadowInset(getBoolean(R.styleable.GXBoxShadowLayout_box_shadowInset, false))

                setShadowSpread(getDimension(R.styleable.GXBoxShadowLayout_box_shadowSpread, 0f))

                boxRadius = getDimension(R.styleable.GXBoxShadowLayout_box_radius, 0f)

                if (hasValue(R.styleable.GXBoxShadowLayout_box_radiusTopLeft) ||
                    hasValue(R.styleable.GXBoxShadowLayout_box_radiusTopRight) ||
                    hasValue(R.styleable.GXBoxShadowLayout_box_radiusBottomLeft) ||
                    hasValue(R.styleable.GXBoxShadowLayout_box_radiusBottomRight)
                ) {
                    setBoxRadius(
                        getDimension(R.styleable.GXBoxShadowLayout_box_radiusTopLeft, boxRadius),
                        getDimension(R.styleable.GXBoxShadowLayout_box_radiusTopRight, boxRadius),
                        getDimension(R.styleable.GXBoxShadowLayout_box_radiusBottomLeft, boxRadius),
                        getDimension(R.styleable.GXBoxShadowLayout_box_radiusBottomRight, boxRadius)
                    )
                } else {
                    setBoxRadius(boxRadius)
                }
            }.recycle()
    }


    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        // Tell parent don't clip me. Otherwise the shadow will be erase.
        (parent as? ViewGroup)?.clipChildren = false
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
        setBoxRadius(
            this.topLeftBoxRadius,
            this.topRightBoxRadius,
            this.bottomLeftBoxRadius,
            this.bottomRightBoxRadius
        )
    }

    private fun clipRadius(canvas: Canvas) {
        if (boxRadius > 0 || topLeftBoxRadius > 0 || topRightBoxRadius > 0 || bottomLeftBoxRadius > 0 || bottomRightBoxRadius > 0) {
            canvas.drawPath(clipPath, clipPaint)
        }
    }

    private fun drawShadow(canvas: Canvas) {
        shadowDrawable.draw(canvas)
    }

    fun setShadowYOffset(shadowVerticalOffset: Float) {
        this.shadowVerticalOffset = shadowVerticalOffset
        resetShadowOffset()
        invalidate()
    }


    fun getShadowVerticalOffset(): Float = this.shadowVerticalOffset

    fun setShadowXOffset(shadowHorizontalOffset: Float) {
        this.shadowHorizontalOffset = shadowHorizontalOffset
        resetShadowOffset()
        invalidate()
    }


    fun getShadowHorizontalOffset(): Float = this.shadowHorizontalOffset

    fun setShadowColor(shadowColor: Int) {
        this.shadowColor = shadowColor
        shadowDrawable.setShadowColor(shadowColor)
        invalidate()
    }

    fun getShadowColor(): Int = this.shadowColor

    fun setShadowBlur(shadowBlur: Float) {
        this.shadowBlur = shadowBlur
        shadowDrawable.setShadowBlur(shadowBlur)
        invalidate()
    }

    fun setShadowInset(shadowInset: Boolean) {
        this.shadowInset = shadowInset
        shadowDrawable.setShadowInset(shadowInset)
        invalidate()
    }

    fun isShadowInset(): Boolean = this.shadowInset

    fun setShadowSpread(shadowSpread: Float) {
        this.shadowSpread = shadowSpread
        resetShadowOffset()
        invalidate()
    }


    fun getShadowSpread() = shadowSpread

    fun setBoxRadius(radius: Float) {
        this.boxRadius = radius.absoluteValue
        setBoxRadius(this.boxRadius, this.boxRadius, this.boxRadius, this.boxRadius)
    }

    fun getRadius(): Float = this.boxRadius

    fun setBoxRadius(
        topLeft: Float,
        topRight: Float,
        bottomLeft: Float,
        bottomRight: Float
    ) {
        this.topLeftBoxRadius = topLeft.absoluteValue
        this.topRightBoxRadius = topRight.absoluteValue
        this.bottomLeftBoxRadius = bottomLeft.absoluteValue
        this.bottomRightBoxRadius = bottomRight.absoluteValue

        clipPath.reset()
        clipPath.fillType = Path.FillType.INVERSE_WINDING
        clipPath.addRoundRect2(
            this.topLeftBoxRadius,
            this.topRightBoxRadius,
            this.bottomLeftBoxRadius,
            this.bottomRightBoxRadius,
            width.toFloat(),
            height.toFloat()
        )

        shadowPath.reset()
        shadowPathOffsetX = 0f
        shadowPathOffsetY = 0f
        shadowPath.fillType = Path.FillType.WINDING
        shadowPath.addRoundRect2(
            this.topLeftBoxRadius,
            this.topRightBoxRadius,
            this.bottomLeftBoxRadius,
            this.bottomRightBoxRadius,
            width.toFloat() + shadowSpread * 2,
            height.toFloat() + shadowSpread * 2
        )

        resetShadowOffset()

        invalidate()
    }

    private var shadowPathOffsetX = 0f
    private var shadowPathOffsetY = 0f

    private fun resetShadowOffset() {
        shadowPath.offset(-shadowPathOffsetX, -shadowPathOffsetY)
        shadowPathOffsetX = -shadowSpread + shadowHorizontalOffset
        shadowPathOffsetY = -shadowSpread + shadowVerticalOffset
        shadowPath.offset(shadowPathOffsetX, shadowPathOffsetY)
        shadowDrawable.invalidateCache()
    }

    fun getTopLeftRadius() = topLeftBoxRadius


    fun getTopRightRadius() = topRightBoxRadius


    fun getBottomRightRadius() = bottomRightBoxRadius


    fun getBottomLeftRadius() = bottomLeftBoxRadius

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
    }
}


