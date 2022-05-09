package com.alibaba.gaiax.render.view.container.banner

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
import androidx.core.view.marginLeft
import androidx.core.view.size
import androidx.viewpager.widget.ViewPager
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.R
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

    private var gxTemplateContext: GXTemplateContext? = null
    var viewPager: ViewPager? = null
    private var indicatorContainer: LinearLayout? = null
    private var selectedIndicatorItem: View? = null

    private var indicatorItems = mutableListOf<View>()

    private var mTimer: Timer? = null
    private var mTimerTask: TimerTask? = null

    private fun initView() {
        viewPager = ViewPager(context)
        indicatorContainer = LinearLayout(context).apply {
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
            indicatorContainer,
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

    override fun onBindData(data: JSONObject) {
        startTimer()
    }

    fun setIndicatorCount(count: Int) {
        indicatorItems.clear()
        for (i in 0 until count) {
            val view = ImageView(context)
            view.setImageResource(R.drawable.gaiax_banner_indicator_dot)
            if (i == 0) {
                selectedIndicatorItem = view
                selectedIndicatorItem?.isSelected = true
            }
            indicatorItems.add(view)

            indicatorContainer?.addView(
                view,
                LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT).apply {
                    setMargins(10, 0, 10, 0)
                })
        }
    }

    private fun indicatorChanged(position: Int) {
        if (position >= 0 && position < indicatorItems.size) {
            val item = indicatorItems[position]
            if (item != selectedIndicatorItem) {
                item.isSelected = true
                selectedIndicatorItem?.isSelected = false
                selectedIndicatorItem = item
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
        mTimer?.schedule(mTimerTask, 4000, 4000)
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
