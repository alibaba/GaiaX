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
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.StateListDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.Handler
import android.os.Looper
import android.util.AttributeSet
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.support.annotation.Keep
import android.support.v4.view.ViewPager
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.GXSliderConfig
import java.util.*

/**
 * @suppress
 */
@Keep
class GXSliderView : RelativeLayout, GXIViewBindData, GXIRootView {

    val TAG = "GXSliderView"

    // 指示器左右间距
    private val INDICATOR_HORIZONTAL_MARGIN = 10.0F

    // 指示器底部间距
    private val INDICATOR_BOTTOM_MARGIN = 10.0F

    // 指示器 Item 之间的间距
    private val INDICATOR_ITEM_HORIZONTAL_MARGIN = 5.0F

    // 指示器大小
    private val INDICATOR_ITEM_SIZE = 8.0F


    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private var gxTemplateContext: GXTemplateContext? = null
    private var config: GXSliderConfig? = null

    var viewPager: ViewPager? = null
    private var indicatorContainer: LinearLayout? = null
    private var selectedIndicatorItem: View? = null

    private var indicatorItems = mutableListOf<View>()

    private var timer: Timer? = null
    private var timerTask: TimerTask? = null

    private val mainHandler = Handler(Looper.getMainLooper())

    private fun initView() {
        initViewPager()
        initIndicator()
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
                    if (config?.hasIndicator == true
                        && config?.infinityScroll == true
                        && indicatorItems.size > 0
                    ) {
                        indicatorChanged(position % indicatorItems.size)
                    } else {
                        indicatorChanged(position)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })
        viewPager?.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> stopTimer()
                MotionEvent.ACTION_MOVE -> stopTimer()
                MotionEvent.ACTION_UP -> startTimer()
            }
            false
        })

        addView(viewPager, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
    }

    private fun initIndicator() {
        indicatorContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
        }

        addView(
            indicatorContainer,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                setMargins(
                    INDICATOR_HORIZONTAL_MARGIN.dpToPx().toInt(),
                    0,
                    INDICATOR_HORIZONTAL_MARGIN.dpToPx().toInt(),
                    INDICATOR_BOTTOM_MARGIN.dpToPx().toInt()
                )
            })
    }

    fun setConfig(config: GXSliderConfig?) {
        this.config = config
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

    fun setIndicatorCount(count: Int) {
        indicatorItems.clear()
        indicatorContainer?.removeAllViews()

        if (config?.hasIndicator != false) {
            for (i in 0 until count) {
                val view = View(context)
                view.background = getIndicatorItemDrawable(this)
                if (i == 0) {
                    selectedIndicatorItem = view
                    selectedIndicatorItem?.isSelected = true
                }
                indicatorItems.add(view)

                indicatorContainer?.addView(
                    view,
                    LayoutParams(
                        INDICATOR_ITEM_SIZE.dpToPx().toInt(),
                        INDICATOR_ITEM_SIZE.dpToPx().toInt()
                    ).apply {
                        setMargins(
                            INDICATOR_ITEM_HORIZONTAL_MARGIN.dpToPx().toInt(),
                            0,
                            INDICATOR_ITEM_HORIZONTAL_MARGIN.dpToPx().toInt(),
                            0
                        )
                    })
            }
        }
    }

    private fun indicatorChanged(position: Int) {
        if (position in 0 until indicatorItems.size) {
            val item = indicatorItems[position]
            if (item != selectedIndicatorItem) {
                item.isSelected = true
                selectedIndicatorItem?.isSelected = false
                selectedIndicatorItem = item
            }
        }
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

    private fun getIndicatorItemDrawable(gxSliderView: GXSliderView): StateListDrawable {
        val drawable = StateListDrawable()
        val selectedDrawable = ShapeDrawable(OvalShape())
        val unselectedDrawable = ShapeDrawable(OvalShape())

        selectedDrawable.intrinsicWidth = INDICATOR_ITEM_SIZE.dpToPx().toInt()
        selectedDrawable.intrinsicHeight = INDICATOR_ITEM_SIZE.dpToPx().toInt()
        unselectedDrawable.intrinsicWidth = INDICATOR_ITEM_SIZE.dpToPx().toInt()
        unselectedDrawable.intrinsicHeight = INDICATOR_ITEM_SIZE.dpToPx().toInt()

        drawable.addState(
            intArrayOf(android.R.attr.state_selected),
            selectedDrawable
        )
        drawable.addState(intArrayOf(), unselectedDrawable)
        config?.indicatorSelectedColor?.let {
            selectedDrawable.paint?.color = it.value(gxSliderView.context)
        }
        config?.indicatorUnselectedColor?.let {
            unselectedDrawable.paint?.color = it.value(gxSliderView.context)
        }
        return drawable
    }

    override fun setTemplateContext(gxContext: GXTemplateContext) {
        this.gxTemplateContext = gxContext
    }

    override fun getTemplateContext(): GXTemplateContext? = gxTemplateContext

    fun getConfig(): GXSliderConfig? = config
}
