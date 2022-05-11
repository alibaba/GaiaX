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
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import androidx.viewpager.widget.ViewPager
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.R
import com.alibaba.gaiax.template.GXSliderConfig
import java.util.*

/**
 * @suppress
 */
class GXSliderView : RelativeLayout, GXIViewBindData, GXIRootView {

    // 指示器左右间距
    private val INDICATOR_HORIZONTAL_MARGIN = 70

    // 指示器底部间距
    private val INDICATOR_BOTTOM_MARGIN = 30

    // 指示器 Item 之间的间距
    private val INDICATOR_ITEM_HORIZONTAL_MARGIN = 10


    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private var gxTemplateContext: GXTemplateContext? = null
    private var mConfig: GXSliderConfig? = null

    var viewPager: ViewPager? = null
    private var mIndicatorContainer: LinearLayout? = null
    private var mSelectedIndicatorItem: View? = null

    private var mIndicatorItems = mutableListOf<View>()

    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null

    private val mMainHandler = Handler(Looper.getMainLooper())

    private fun initView() {
        viewPager = ViewPager(context)
        mIndicatorContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.END
        }

        viewPager?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {
                if (mConfig?.hasIndicator == true) {
                    if (mConfig?.hasIndicator == true
                        && mConfig?.infinityScroll == true
                        && mIndicatorItems.size > 0
                    ) {
                        indicatorChanged(position % mIndicatorItems.size)
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
        addView(
            mIndicatorContainer,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                setMargins(
                    INDICATOR_HORIZONTAL_MARGIN,
                    0,
                    INDICATOR_HORIZONTAL_MARGIN,
                    INDICATOR_BOTTOM_MARGIN
                )
            })
    }

    fun setConfig(config: GXSliderConfig?) {
        mConfig = config
    }

    override fun onBindData(data: JSONObject) {
        viewPager?.adapter?.notifyDataSetChanged()
        mConfig?.selectedIndex?.let {
            viewPager?.adapter?.count?.let { count ->
                if (it in 0 until count) {
                    viewPager?.setCurrentItem(it, false)
                }
            }
        }
        startTimer()
    }

    fun setIndicatorCount(count: Int) {
        mIndicatorItems.clear()
        mIndicatorContainer?.removeAllViews()

        if (mConfig?.hasIndicator != false) {
            for (i in 0 until count) {
                val view = ImageView(context)
                view.setImageResource(R.drawable.gaiax_slider_indicator_dot)
                if (i == 0) {
                    mSelectedIndicatorItem = view
                    mSelectedIndicatorItem?.isSelected = true
                }
                mIndicatorItems.add(view)

                mIndicatorContainer?.addView(
                    view,
                    LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                        setMargins(
                            INDICATOR_ITEM_HORIZONTAL_MARGIN,
                            0,
                            INDICATOR_ITEM_HORIZONTAL_MARGIN,
                            0
                        )
                    })
            }
        }
    }

    private fun indicatorChanged(position: Int) {
        if (position in 0 until mIndicatorItems.size) {
            val item = mIndicatorItems[position]
            if (item != mSelectedIndicatorItem) {
                item.isSelected = true
                mSelectedIndicatorItem?.isSelected = false
                mSelectedIndicatorItem = item
            }
        }
    }

    private fun startTimer() {
        mConfig?.scrollTimeInterval?.let {
            if (it > 0) {
                mTimer = Timer()
                mTimerTask = object : TimerTask() {
                    override fun run() {
                        viewPager?.currentItem?.let { currentItem ->
                            viewPager?.adapter?.count?.let { count ->
                                mMainHandler.post {
                                    viewPager?.setCurrentItem(
                                        (currentItem + 1) % count,
                                        true
                                    )
                                }
                            }
                        }
                    }
                }
                mTimer?.schedule(mTimerTask, it, it)
            }
        }
    }

    private fun stopTimer() {
        mTimer?.cancel()
        mTimerTask?.cancel()

        mTimer = null
        mTimerTask = null
    }

    override fun manualRelease() {
        if (gxTemplateContext?.rootView?.get() == this) {
            if (gxTemplateContext?.rootNode?.isRoot == true) {
                GXTemplateEngine.instance.render.onDestroy(gxTemplateContext!!)
                gxTemplateContext = null
            }
        }
    }

    override fun setTemplateContext(gxContext: GXTemplateContext) {
        this.gxTemplateContext = gxContext
    }

    override fun getTemplateContext(): GXTemplateContext? = gxTemplateContext
}
