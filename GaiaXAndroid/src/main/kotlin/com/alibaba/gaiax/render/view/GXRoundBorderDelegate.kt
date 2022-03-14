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

package com.alibaba.gaiax.render.view

import android.graphics.*

/**
 * For handling rounded corners and border logic
 * @suppress
 */
class GXRoundBorderDelegate {

    private var measureWidth = 0F
    private var measureHeight = 0F

    private var roundCornerRadiusPathIsDirty = false
    private var roundCornerRadiusPath: Path? = null
    private var roundCornerRadiusPaint: Paint? = null
    private var roundCornerRadiusArray: FloatArray? = null

    private var roundCornerBorderPath: Path? = null
    private var roundCornerBorderPaint: Paint? = null

    private var roundCornerBorderPathIsDirty = false
    private var roundCornerBorderRadiusArray: FloatArray? = null
    private var roundCornerBorderColor: Int? = null
    private var roundCornerBorderWidth: Float? = null

    fun isNeedRound(canvas: Canvas?, measureWidth: Float, measureHeight: Float): Boolean {
        return canvas != null && measureWidth > 0F && measureHeight > 0F && (roundCornerRadiusArray != null ||
                (roundCornerBorderRadiusArray != null && roundCornerBorderColor != null && roundCornerBorderWidth != null))
    }

    fun draw(canvas: Canvas?, measureWidth: Float, measureHeight: Float, callback: () -> Unit) {
        canvas?.saveLayer(0F, 0F, measureWidth, measureHeight, null)
        callback()
        canvas?.restore()
    }

    fun onDraw(canvas: Canvas?, measureWidth: Float, measureHeight: Float) {
        this.measureWidth = measureWidth
        this.measureHeight = measureHeight
        canvas?.let {
            drawRoundCorner(it, measureWidth, measureHeight)
            drawRoundBorder(it, measureWidth, measureHeight)
        }
    }

    private fun drawRoundBorder(canvas: Canvas, measureWidth: Float, measureHeight: Float) {
        val borderColor = roundCornerBorderColor
        val borderWidth = roundCornerBorderWidth
        val borderRadiusArray = roundCornerBorderRadiusArray
        if (borderColor != null && borderWidth != null && borderRadiusArray != null) {
            if (roundCornerBorderPaint == null) {
                roundCornerBorderPaint = Paint(Paint.ANTI_ALIAS_FLAG)
                roundCornerBorderPaint?.style = Paint.Style.STROKE
            }

            if (roundCornerBorderPath == null) {
                roundCornerBorderPath = Path()
            }

            roundCornerBorderPaint?.strokeWidth = borderWidth
            roundCornerBorderPaint?.color = borderColor

            if (roundCornerBorderPathIsDirty) {
                val rect = RectF(borderWidth / 2, borderWidth / 2, measureWidth - borderWidth / 2, measureHeight - borderWidth / 2)
                roundCornerBorderPath?.addRoundRect(rect, borderRadiusArray, Path.Direction.CW)
                roundCornerBorderPathIsDirty = false
            }

            canvas.drawPath(roundCornerBorderPath!!, roundCornerBorderPaint!!)
        }
    }

    private fun drawRoundCorner(canvas: Canvas, measureWidth: Float, measureHeight: Float) {
        val roundCornerRadiusArray = roundCornerRadiusArray
        if (roundCornerRadiusArray != null) {

            if (roundCornerRadiusPaint == null) {
                roundCornerRadiusPaint = Paint()
                roundCornerRadiusPaint?.isAntiAlias = true
                roundCornerRadiusPaint?.style = Paint.Style.FILL
                roundCornerRadiusPaint?.color = Color.RED
                roundCornerRadiusPaint?.xfermode = PorterDuffXfermode(PorterDuff.Mode.DST_OUT)
            }

            if (roundCornerRadiusPath == null) {
                roundCornerRadiusPath = Path()
            }

            if (roundCornerRadiusPathIsDirty) {
                val rect = RectF(0F, 0F, measureWidth, measureHeight)
                roundCornerRadiusPath?.addRect(rect, Path.Direction.CW)
                roundCornerRadiusPath?.addRoundRect(rect, roundCornerRadiusArray, Path.Direction.CCW)
                roundCornerRadiusPathIsDirty = false
            }

            canvas.drawPath(roundCornerRadiusPath!!, roundCornerRadiusPaint!!)
        }
    }

    fun setRoundCornerRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        this.setRoundCornerRadius(FloatArray(8).apply {
            this[0] = topLeft
            this[1] = topLeft
            this[2] = topRight
            this[3] = topRight
            this[4] = bottomLeft
            this[5] = bottomLeft
            this[6] = bottomRight
            this[7] = bottomRight
        })
    }

    fun setRoundCornerRadius(radius: FloatArray) {
        roundCornerRadiusArray = radius
        roundCornerRadiusPath = null
        roundCornerRadiusPathIsDirty = true
    }

    fun setRoundCornerBorder(borderColor: Int, borderWidth: Float, radius: FloatArray) {
        roundCornerBorderColor = borderColor
        roundCornerBorderWidth = borderWidth
        roundCornerBorderRadiusArray = radius
        roundCornerBorderPath = null
        roundCornerBorderPathIsDirty = true
    }

    fun setRoundCornerBorder(borderColor: Int, borderWidth: Float, topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        this.setRoundCornerBorder(borderColor, borderWidth, FloatArray(8).apply {
            this[0] = topLeft
            this[1] = topLeft
            this[2] = topRight
            this[3] = topRight
            this[4] = bottomLeft
            this[5] = bottomLeft
            this[6] = bottomRight
            this[7] = bottomRight
        })
    }
}