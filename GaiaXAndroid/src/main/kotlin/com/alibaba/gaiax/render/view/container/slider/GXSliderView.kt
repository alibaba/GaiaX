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
import android.graphics.Outline
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.viewpager.widget.ViewPager
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.*
import com.alibaba.gaiax.render.view.drawable.GXRoundCornerBorderGradientDrawable
import com.alibaba.gaiax.template.GXSliderConfig
import java.util.*

/**
 * @suppress
 */
@Keep
class GXSliderView : FrameLayout, GXIContainer, GXIViewBindData, GXIRootView,
    GXIRoundCorner, GXIRelease, GXIViewVisibleChange {

    enum class IndicatorPosition(val value: String) {
        TOP_LEFT("top-left"),
        TOP_CENTER("top-center"),
        TOP_RIGHT("top-right"),
        BOTTOM_LEFT("bottom-left"),
        BOTTOM_CENTER("bottom-center"),
        BOTTOM_RIGHT("bottom-right");

        companion object {
            fun fromValue(value: String?): IndicatorPosition = when (value) {
                TOP_LEFT.value -> TOP_LEFT
                TOP_CENTER.value -> TOP_CENTER
                TOP_RIGHT.value -> TOP_RIGHT
                BOTTOM_LEFT.value -> BOTTOM_LEFT
                BOTTOM_CENTER.value -> BOTTOM_CENTER
                else -> BOTTOM_RIGHT
            }
        }
    }

    companion object {
        private const val TAG = "[GaiaX] [GXSliderView]"
    }

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private var gxTemplateContext: GXTemplateContext? = null
    private var config: GXSliderConfig? = null

    var viewPager: ViewPager? = null
    private var indicatorView: GXSliderBaseIndicatorView? = null

    private var pageSize: Int = 0

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    private fun initView() {
        initViewPager()
    }

    private fun initViewPager() {
        viewPager = ViewPager(context)
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (config?.hasIndicatorFinal == true) {
                    indicatorView?.updateSelectedIndex(position % pageSize)
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        viewPager?.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> stopTimer()
                MotionEvent.ACTION_MOVE -> stopTimer()
                MotionEvent.ACTION_UP -> startTimer()
            }
            false
        }

        addView(viewPager, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun initIndicator() {
        if (indicatorView != null) {
            removeView(indicatorView)
        }

        indicatorView = createIndicatorView()

        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        config?.indicatorMarginFinal?.let {
            layoutParams.leftMargin = it.start.valueInt
            layoutParams.topMargin = it.top.valueInt
            layoutParams.rightMargin = it.end.valueInt
            layoutParams.bottomMargin = it.bottom.valueInt
        }

        when (config?.indicatorPositionFinal) {
            IndicatorPosition.TOP_LEFT -> {
                layoutParams.gravity = Gravity.TOP or Gravity.LEFT
            }
            IndicatorPosition.TOP_CENTER -> {
                layoutParams.gravity = Gravity.TOP or Gravity.CENTER
            }
            IndicatorPosition.TOP_RIGHT -> {
                layoutParams.gravity = Gravity.TOP or Gravity.RIGHT
            }
            IndicatorPosition.BOTTOM_LEFT -> {
                layoutParams.gravity = Gravity.BOTTOM or Gravity.LEFT
            }
            IndicatorPosition.BOTTOM_CENTER -> {
                layoutParams.gravity = Gravity.BOTTOM or Gravity.CENTER
            }
            else -> {
                // BOTTOM_RIGHT
                layoutParams.gravity = Gravity.BOTTOM or Gravity.RIGHT
            }
        }

        addView(indicatorView, layoutParams)
    }

    private fun createIndicatorView(): GXSliderBaseIndicatorView {
        config?.indicatorClassFinal?.let { it ->
            try {
                (Class.forName(it).getConstructor(Context::class.java)
                    .newInstance(context) as? GXSliderBaseIndicatorView)?.let { customIndicatorView ->
                    return customIndicatorView
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return GXSliderDefaultIndicatorView(context)
    }

    fun setConfig(config: GXSliderConfig?) {
        this.config = config

        if (config?.hasIndicatorFinal == true) {
            initIndicator()

            indicatorView?.setIndicatorColor(
                config.indicatorSelectedColorFinal.value(context),
                config.indicatorUnselectedColorFinal.value(context)
            )
        }
    }

    override fun onBindData(data: JSONObject?) {
        viewPager?.adapter?.notifyDataSetChanged()
    }

    private fun updateView() {
        stopTimer()

        config?.selectedIndexFinal?.let {
            viewPager?.adapter?.count?.let { count ->
                if (it in 0 until count) {
                    viewPager?.setCurrentItem(it, false)
                    indicatorView?.updateSelectedIndex(it)
                }
            }
        }

        startTimer()
    }

    fun setPageSize(size: Int) {
        pageSize = size
        indicatorView?.setIndicatorCount(size)
    }

    private fun startTimer() {
        stopTimer()

        config?.scrollTimeIntervalFinal?.let {
            if (it > 0) {
                timer = Timer()
                timerTask = object : TimerTask() {
                    override fun run() {
                        viewPager?.currentItem?.let { currentItem ->
                            viewPager?.adapter?.count?.let { count ->
                                mainHandler.post {
                                    viewPager?.setCurrentItem(
                                        (currentItem + 1) % count,
                                        true
                                    )
                                }
                            }
                        }
                    }
                }
                timer?.schedule(timerTask, it, it)
            }
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        timerTask?.cancel()

        timer = null
        timerTask = null
    }

    override fun setTemplateContext(gxContext: GXTemplateContext?) {
        this.gxTemplateContext = gxContext
    }

    override fun getTemplateContext(): GXTemplateContext? = gxTemplateContext

    fun getConfig(): GXSliderConfig? = config

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
        if (radius.size == 8) {
            val shape = GXRoundCornerBorderGradientDrawable()
            shape.shape = GradientDrawable.RECTANGLE
            shape.cornerRadii = radius
            shape.setStroke(borderWidth.toInt(), borderColor)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                foreground = shape
            }
        }
    }

    override fun release() {
        indicatorView = null
        stopTimer()
    }

    override fun onVisibleChanged(visible: Boolean) {
        if (visible) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        updateView()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopTimer()
    }
}
