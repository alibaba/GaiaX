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
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.viewpager.widget.ViewPager
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.template.GXSliderConfig
import java.util.*

/**
 * @suppress
 */
@Keep
class GXSliderView : FrameLayout, GXIViewBindData, GXIRootView {

    enum class IndicatorPosition(val value: String) {
        LEFT_TOP("left-top"),
        LEFT_BOTTOM("left-bottom"),
        RIGHT_TOP("right-top"),
        RIGHT_BOTTOM("right-bottom"),
        TOP_CENTER("top-center"),
        BOTTOM_CENTER("bottom-center");

        companion object {
            fun fromValue(value: String?): IndicatorPosition = when (value) {
                LEFT_TOP.value -> LEFT_TOP
                LEFT_BOTTOM.value -> LEFT_BOTTOM
                RIGHT_TOP.value -> RIGHT_TOP
                TOP_CENTER.value -> TOP_CENTER
                BOTTOM_CENTER.value -> BOTTOM_CENTER
                else -> RIGHT_BOTTOM
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
                if (config?.hasIndicator == true) {
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
        indicatorView = createIndicatorView()

        val layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        config?.indicatorMargin?.let {
            layoutParams.leftMargin = it.left
            layoutParams.topMargin = it.top
            layoutParams.rightMargin = it.right
            layoutParams.bottomMargin = it.bottom
        }

        when (config?.indicatorPosition) {
            IndicatorPosition.LEFT_TOP -> {
                layoutParams.gravity = Gravity.LEFT or Gravity.TOP
            }
            IndicatorPosition.LEFT_BOTTOM -> {
                layoutParams.gravity = Gravity.LEFT or Gravity.BOTTOM
            }
            IndicatorPosition.RIGHT_TOP -> {
                layoutParams.gravity = Gravity.RIGHT or Gravity.TOP
            }
            IndicatorPosition.TOP_CENTER -> {
                layoutParams.gravity = Gravity.TOP or Gravity.CENTER
            }
            IndicatorPosition.BOTTOM_CENTER -> {
                layoutParams.gravity = Gravity.BOTTOM or Gravity.CENTER
            }
            else -> {
                // RIGHT_BOTTOM
                layoutParams.gravity = Gravity.RIGHT or Gravity.BOTTOM
            }
        }

        addView(indicatorView, layoutParams)
    }

    private fun createIndicatorView(): GXSliderBaseIndicatorView {
        config?.indicatorClass?.let { it ->
            try {
                (Class.forName(it).getConstructor(Context::class.java)
                    .newInstance(context) as? GXSliderBaseIndicatorView)?.let { customIndicatorView ->
                    return customIndicatorView
                }
            } catch (e: Exception) {
                Log.e(TAG, "create custom indicator class fail, class:$it")
                e.printStackTrace()
            }
        }

        return GXSliderDefaultIndicatorView(context)
    }

    fun setConfig(config: GXSliderConfig?) {
        this.config = config

        if (config?.hasIndicator == true) {
            initIndicator()

            indicatorView?.setIndicatorColor(
                config.indicatorSelectedColor.value(context),
                config.indicatorUnselectedColor.value(context)
            )
        }
    }

    override fun onBindData(data: JSONObject?) {
        viewPager?.adapter?.notifyDataSetChanged()
        config?.selectedIndex?.let {
            viewPager?.adapter?.count?.let { count ->
                if (it in 0 until count) {
                    viewPager?.setCurrentItem(it, false)
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
        config?.scrollTimeInterval?.let {
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
}
