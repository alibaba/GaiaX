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

package com.alibaba.gaiax.render.view.container.slider

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx


/**
 * @suppress
 */
class GXYKSliderIndicatorView : GXSliderBaseIndicatorView {

    companion object {
        private const val TAG = "GXYKSliderIndicatorView"

        // 指示器 Item 之间的间距
        private val INDICATOR_SPACING = 3.0F.dpToPx()

        // 指示器大小
        private val INDICATOR_NORMAL_WIDTH = 4.0F.dpToPx()

        private val INDICATOR_SELECTED_WIDTH = 9.0F.dpToPx()

        private val INDICATOR_HEIGHT = 4.0F.dpToPx()

        private val INDICATOR_RADIUS = 3.0F.dpToPx()
    }

    private var indicatorCount: Int = 0

    private var lastIndex: Int = 0
    private var currentIndex: Int = 0

    private var selectedColor: Int = Color.WHITE
    private var unselectedColor: Int = Color.GRAY

    private val paint = Paint()

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        paint.isAntiAlias = true
        paint.strokeWidth = 1f
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val measuredWidth = ((indicatorCount - 1) * INDICATOR_NORMAL_WIDTH + INDICATOR_SELECTED_WIDTH + (indicatorCount - 1) * INDICATOR_SPACING).toInt()
        val measuredHeight = INDICATOR_HEIGHT.toInt()
        setMeasuredDimension(measuredWidth, measuredHeight)
    }

    override fun setIndicatorCount(count: Int) {
        indicatorCount = count
    }

    override fun updateSelectedIndex(index: Int) {
        lastIndex = currentIndex
        currentIndex = index

        invalidate()
    }

    override fun setIndicatorColor(selectedColor: Int?, unselectedColor: Int?) {
        this.selectedColor = 0xffffffff.toInt()
        this.unselectedColor = 0x99ffffff.toInt()
    }

    override fun onDraw(canvas: Canvas?) {
        var x = 0F
        for (index in 0 until indicatorCount) {
            x += if (index == currentIndex) {
                paint.color = selectedColor
                drawIndication(canvas, x, 0F, x + INDICATOR_SELECTED_WIDTH, INDICATOR_HEIGHT, paint)
                INDICATOR_SELECTED_WIDTH + INDICATOR_SPACING
            } else {
                paint.color = unselectedColor
                drawIndication(canvas, x, 0F, x + INDICATOR_NORMAL_WIDTH, INDICATOR_HEIGHT, paint)
                INDICATOR_NORMAL_WIDTH + INDICATOR_SPACING
            }
        }
    }

    private fun drawIndication(canvas: Canvas?, left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        canvas?.drawRoundRect(left, top, right, bottom, INDICATOR_RADIUS, INDICATOR_RADIUS, paint)
    }
}
