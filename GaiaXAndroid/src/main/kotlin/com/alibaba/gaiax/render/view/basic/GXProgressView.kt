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

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.template.GXProgressConfig
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * @suppress
 */
@Keep
class GXProgressView : View, GXIViewBindData {

    companion object {
        private const val PROGRESS_WIDTH_VALUE_HOLDER = "PROGRESS_WIDTH_VALUE_HOLDER"
        private const val PADDING = 1.0f
    }

    private var percent: Float = 0f
        get() {
            return if (field < 0) {
                0f
            } else field.coerceAtMost(1f)
        }
    private var currentProgressWidth: Float = 0f

    private val mPaint = Paint()
    private val bgPath = Path()
    private val progressPath = Path()

    private var animator: Animator? = null

    private var config: GXProgressConfig? = null

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private fun initView() {
        mPaint.strokeJoin = Paint.Join.ROUND
        mPaint.strokeCap = Paint.Cap.ROUND
        mPaint.isAntiAlias = true
        mPaint.strokeWidth = 1f
    }

    override fun onBindData(data: JSONObject?) {
        data?.getFloat(GXTemplateKey.GAIAX_VALUE)?.let {
            percent = it
            updateProgressPath()
            invalidate()
        }
    }

    fun setConfig(config: GXProgressConfig?) {
        this.config = config
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        mPaint.pathEffect = CornerPathEffect((h - PADDING * 2) / 2)
        bgPath.reset()
        bgPath.moveTo(PADDING, PADDING)
        bgPath.lineTo(w - PADDING, PADDING)
        bgPath.lineTo(w - PADDING, h - PADDING)
        bgPath.lineTo(PADDING, h - PADDING)
        bgPath.close()

        updateProgressPath()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        mPaint.color = config?.trailColorFinal?.value(context) ?: Color.GRAY
        canvas?.drawPath(bgPath, mPaint)

        if (percent > 0) {
            mPaint.color = config?.strokeColorFinal?.value(context) ?: Color.BLUE
            canvas?.drawPath(progressPath, mPaint)
        }
    }

    private fun updateProgressPath() {
        val progressWidth = (measuredWidth - PADDING * 2) * percent

        if (config?.animatedFinal == false) {
            progressPath.reset()
            progressPath.moveTo(PADDING, PADDING)
            progressPath.lineTo(PADDING + progressWidth, PADDING)
            progressPath.lineTo(PADDING + progressWidth, measuredHeight - PADDING)
            progressPath.lineTo(PADDING, measuredHeight - PADDING)
            progressPath.close()
        } else {
            animator?.cancel()
            animator = ValueAnimator().apply {
                setValues(
                    PropertyValuesHolder.ofFloat(
                        PROGRESS_WIDTH_VALUE_HOLDER, currentProgressWidth, progressWidth
                    )
                )
                duration = 300
                interpolator = AccelerateDecelerateInterpolator()

                addUpdateListener {
                    val width = it.getAnimatedValue(PROGRESS_WIDTH_VALUE_HOLDER) as Float
                    progressPath.reset()
                    progressPath.moveTo(PADDING, PADDING)
                    progressPath.lineTo(PADDING + width, PADDING)
                    progressPath.lineTo(PADDING + width, measuredHeight - PADDING)
                    progressPath.lineTo(PADDING, measuredHeight - PADDING)
                    progressPath.close()

                    invalidate()
                }
                start()
            }
        }
        currentProgressWidth = progressWidth
    }

    fun getConfig(): GXProgressConfig? {
        return config
    }
}
