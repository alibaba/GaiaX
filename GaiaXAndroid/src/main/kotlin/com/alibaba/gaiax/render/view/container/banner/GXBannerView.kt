package com.alibaba.gaiax.render.view.container.banner

import android.content.Context
import android.graphics.Canvas
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
import com.alibaba.gaiax.render.view.GXRoundBorderDelegate
import com.alibaba.gaiax.template.GXBannerConfig
import java.util.*

/**
 * @author guaiyu
 * @date 2022/4/13 19:48
 */
class GXBannerView : RelativeLayout, GXIViewBindData, GXIRootView {

    constructor(context: Context) : super(context) {
        initView()
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        initView()
    }

    private var mGxTemplateContext: GXTemplateContext? = null
    private var mConfig: GXBannerConfig? = null

    var viewPager: ViewPager? = null
    private var mIndicatorContainer: LinearLayout? = null
    private var mSelectedIndicatorItem: View? = null

    private var mIndicatorItems = mutableListOf<View>()

    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null

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
                indicatorChanged(position)
            }

            override fun onPageScrollStateChanged(state: Int) {
            }

        })

        addView(viewPager, LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT))
        addView(
            mIndicatorContainer,
            LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT).apply {
                addRule(RelativeLayout.ALIGN_PARENT_BOTTOM)
                setMargins(70, 0, 70, 30)
            })
        viewPager?.setOnTouchListener(OnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> stopTimer()
                MotionEvent.ACTION_MOVE -> stopTimer()
                MotionEvent.ACTION_UP -> startTimer()
            }
            false
        })
    }

    fun setConfig(config: GXBannerConfig?) {
        mConfig = config
    }

    override fun onBindData(data: JSONObject) {
        startTimer()
    }

    fun setIndicatorCount(count: Int) {
        mIndicatorItems.clear()
        for (i in 0 until count) {
            val view = ImageView(context)
            view.setImageResource(R.drawable.gaiax_banner_indicator_dot)
            if (i == 0) {
                mSelectedIndicatorItem = view
                mSelectedIndicatorItem?.isSelected = true
            }
            mIndicatorItems.add(view)

            mIndicatorContainer?.addView(
                view,
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    setMargins(10, 0, 10, 0)
                })
        }
    }

    private fun indicatorChanged(position: Int) {
        if (position >= 0 && position < mIndicatorItems.size) {
            val item = mIndicatorItems[position]
            if (item != mSelectedIndicatorItem) {
                item.isSelected = true
                mSelectedIndicatorItem?.isSelected = false
                mSelectedIndicatorItem = item
            }
        }
    }

    private fun startTimer() {
        mTimer = Timer()
        mTimerTask = object : TimerTask() {
            override fun run() {
                viewPager?.currentItem?.let { currentItem ->
                    viewPager?.adapter?.count?.let { count ->
                        Handler(Looper.getMainLooper()).post {
                            viewPager?.setCurrentItem(
                                (currentItem + 1) % count,
                                true
                            )
                        }
                    }
                }
            }
        }
        mTimer?.schedule(
            mTimerTask,
            mConfig?.scrollTimeInterval ?: 3000,
            mConfig?.scrollTimeInterval ?: 3000
        )
    }

    private fun stopTimer() {
        mTimer?.cancel()
        mTimerTask?.cancel()

        mTimer = null
        mTimerTask = null
    }

    override fun manualRelease() {
        if (mGxTemplateContext?.rootView?.get() == this) {
            if (mGxTemplateContext?.rootNode?.isRoot == true) {
                GXTemplateEngine.instance.render.onDestroy(mGxTemplateContext!!)
                mGxTemplateContext = null
            }
        }
    }

    override fun setTemplateContext(gxContext: GXTemplateContext) {
        this.mGxTemplateContext = gxContext
    }

    override fun getTemplateContext(): GXTemplateContext? = mGxTemplateContext
}
