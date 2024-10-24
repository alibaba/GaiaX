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
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.ViewOutlineProvider
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.viewpager.widget.ViewPager
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXTemplateNode
import com.alibaba.gaiax.render.view.GXIContainer
import com.alibaba.gaiax.render.view.GXIRelease
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.GXIRoundCorner
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.render.view.GXIViewVisibleChange
import com.alibaba.gaiax.render.view.drawable.GXRoundCornerBorderGradientDrawable
import com.alibaba.gaiax.template.GXSliderConfig
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.Log
import com.alibaba.gaiax.utils.runE
import java.util.Timer
import java.util.TimerTask

/**
 * @suppress
 */
@Keep
class GXSliderView : FrameLayout, GXIContainer, GXIViewBindData, GXIRootView, GXIRoundCorner, GXIRelease, GXIViewVisibleChange {

    companion object {
        private var SHOWN_VIEW_COUNT: Int = 0

        private const val TAG = "GXSliderView"
    }

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

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private var isDefaultSelected: Boolean = false
    private var isAttached: Boolean = false
    private var gxTemplateContext: GXTemplateContext? = null
    private var config: GXSliderConfig? = null

    var viewPager: GXViewPager? = null
    private var indicatorView: GXSliderBaseIndicatorView? = null

    private var pageSize: Int = 0

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    private fun initView() {
        initViewPager()
    }

    private fun initViewPager() {
        viewPager = GXViewPager(context)
        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            }

            override fun onPageSelected(position: Int) {
                // 回调事件
                val gxScroll = GXTemplateEngine.GXScroll().apply {
                    this.type = GXTemplateEngine.GXScroll.TYPE_ON_PAGE_SELECTED
                    this.view = this@GXSliderView
                    this.position = position
                }
                gxTemplateContext?.templateData?.eventListener?.onScrollEvent(gxScroll)

                Log.runE(TAG) { "onPageSelected position=$position" }
                setIndicatorIndex(position)
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

        config?.indicatorMargin?.let {
            layoutParams.leftMargin = it.start.valueInt
            layoutParams.topMargin = it.top.valueInt
            layoutParams.rightMargin = it.end.valueInt
            layoutParams.bottomMargin = it.bottom.valueInt
        }

        when (config?.indicatorPosition) {
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
        config?.indicatorClass?.let { it ->
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

        if (config?.hasIndicator == true) {
            initIndicator()

            indicatorView?.setIndicatorColor(
                config.indicatorSelectedColor.value(context),
                config.indicatorUnselectedColor.value(context)
            )
        }
    }

    override fun onBindData(data: JSONObject?) {
    }


    fun setPageSize(size: Int) {
        pageSize = size
        indicatorView?.setIndicatorCount(size)
    }

    private fun startTimer() {

        // FIX
        stopTimer()

        val scrollTimeInterval = config?.scrollTimeInterval ?: return

        if (scrollTimeInterval <= 0) {
            return
        }

        Log.runE(TAG) { "startTimer" }

        timer = Timer()
        timerTask = object : TimerTask() {
            override fun run() {
                val currentItem = viewPager?.currentItem ?: return
                viewPager?.post {
                    val selectedIndex = (currentItem + 1)
                    Log.runE(TAG) { "schedule selectedIndex=$selectedIndex" }
                    setSliderIndex(selectedIndex, true)
                }
            }
        }
        timer?.schedule(timerTask, scrollTimeInterval, scrollTimeInterval)
    }

    private fun stopTimer() {
        Log.runE(TAG) { "stopTimer" }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (radius.size == 8) {
                if (foreground == null) {
                    val target = GXRoundCornerBorderGradientDrawable()
                    target.shape = GradientDrawable.RECTANGLE
                    target.cornerRadii = radius
                    target.setStroke(borderWidth.toInt(), borderColor)
                    foreground = target
                } else if (foreground is GradientDrawable) {
                    val target = foreground as GradientDrawable
                    target.setStroke(borderWidth.toInt(), borderColor)
                    target.cornerRadii = radius
                }
            }
        }
    }

    override fun release() {
        indicatorView = null
        stopTimer()

        // 只有第一个SliderView可以自动滚动，用于解决多频道轮播图同时滚动的问题
        if (SHOWN_VIEW_COUNT > 0) {
            SHOWN_VIEW_COUNT--
        }

        // 释放当前显示的View
        viewPager?.let {
            val adapter = it.adapter
            if (adapter is GXSliderViewAdapter) {
                adapter.getItemView(it.currentItem)?.let { itemView ->
                    (itemView as? ViewGroup)?.getChildAt(0)?.let { gxView ->
                        GXTemplateContext.getContext(gxView)?.let { gxTemplateContext ->
                            if (gxTemplateContext.templateItem.isPageMode) {
                                GXRegisterCenter.instance.gxPageItemViewLifecycleListener?.onDestroy(gxView)
                            } else {
                            }

                            GXTemplateEngine.instance.destroyView(gxView)
                        }
                    }
                }
            }
        }
    }

    override fun onVisibleChanged(visible: Boolean) {
        if (!isAttached) {
            return
        }
        if (visible) {
            startTimer()
        } else {
            stopTimer()
        }
    }

    override fun onAttachedToWindow() {
        isAttached = true
        super.onAttachedToWindow()

        // 只有第一个SliderView可以自动滚动，用于解决多频道轮播图同时滚动的问题
        if (SHOWN_VIEW_COUNT <= 0) {
            SHOWN_VIEW_COUNT = 0
            SHOWN_VIEW_COUNT++
            startTimer()
        }
    }

    override fun onDetachedFromWindow() {
        isAttached = false
        super.onDetachedFromWindow()
    }

    fun onNewBinData(gxTemplateNode: GXTemplateNode, templateData: JSONObject) {
        viewPager?.adapter?.notifyDataSetChanged()
        processIndex(gxTemplateNode, templateData)
    }

    private fun processIndex(gxTemplateNode: GXTemplateNode, templateData: JSONObject) {
        if (!isDefaultSelected) {
            isDefaultSelected = true
            config?.selectedIndex?.takeIf { it >= 0 }?.let { selectedIndex ->
                Log.runE(TAG) { "processIndex selectedIndex=$selectedIndex" }
                setSliderIndex(selectedIndex, false)
                setIndicatorIndex(selectedIndex)
            }
        } else {
            val extend = gxTemplateNode.getExtend(templateData)
            val holdingOffset = extend?.getBooleanValue(GXTemplateKey.GAIAX_DATABINDING_HOLDING_OFFSET) ?: false
            if (holdingOffset) {
                extend?.getInteger(GXTemplateKey.GAIAX_SCROLL_INDEX)?.takeIf { it >= 0 }?.let { scrollIndex ->
                    val smooth = extend.getBooleanValue(GXTemplateKey.GAIAX_SCROLL_ANIMATED)
                    Log.runE(TAG) { "processIndex holdingOffset smooth=$smooth scrollIndex=$scrollIndex" }
                    setSliderIndex(scrollIndex, smooth)
                }
            }
        }
    }

    private fun setIndicatorIndex(scrollIndex: Int) {
       val targetIndex=  calculateTargetIndex(scrollIndex)
        Log.runE(TAG) { "setIndicatorIndex scrollIndex=$scrollIndex targetIndex=$targetIndex pageSize=$pageSize" }
        if (config?.hasIndicator == true) {
            indicatorView?.updateSelectedIndex(targetIndex)
        }
    }

    private fun setSliderIndex(scrollIndex: Int, smooth: Boolean) {
        val currentIndex = this.getCurrentIndex()
        Log.runE(TAG) { "setSliderIndex currentIndex=$currentIndex scrollIndex=$scrollIndex  pageSize=$pageSize smooth=$smooth" }
        viewPager?.post {
            // TODO：如果跨多个索引移动，会导致明显卡顿
            viewPager?.setCurrentItem(scrollIndex, smooth)
            setIndicatorIndex(scrollIndex)
        }
    }

    private fun calculateTargetIndex(index: Int): Int {
        return index % pageSize
    }

    private fun getCurrentIndex(): Int {
        return viewPager?.currentItem ?: 0
    }
}
