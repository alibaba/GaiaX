package com.alibaba.gaiax.demo.custom

import android.animation.Animator
import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.annotation.Keep
import com.alibaba.gaiax.render.view.container.slider.GXSliderBaseIndicatorView
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx

@Keep
class CustomSliderIndicatorView : GXSliderBaseIndicatorView {

    companion object {
        private const val SELECTED_WIDTH_CHANGE_VALUE_HOLDER = "SELECTED_WIDTH_CHANGE_VALUE_HOLDER"

        // 指示器 Item 之间的间距
        private val INDICATOR_SPACING = 4.0F.dpToPx()

        // 指示器大小
        private val INDICATOR_NORMAL_WIDTH = 10.0F.dpToPx()

        private val INDICATOR_SELECTED_WIDTH = 21.0F.dpToPx()

        private val INDICATOR_HEIGHT = 10.0F.dpToPx()
    }

    private var indicatorCount: Int = 0

    private var lastIndex: Int = -1
    private var currentIndex: Int = -1

    private var selectedColor: Int? = null
    private var unselectedColor: Int? = null

    private var selectedWidth: Float = INDICATOR_SELECTED_WIDTH
    private var lastSelectedWidth: Float = INDICATOR_NORMAL_WIDTH

    private val paint = Paint()

    private var animator: Animator? = null

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
        setMeasuredDimension(
            ((indicatorCount - 1) * INDICATOR_NORMAL_WIDTH + INDICATOR_SELECTED_WIDTH
                    + (indicatorCount - 1) * INDICATOR_SPACING).toInt(),
            INDICATOR_HEIGHT.toInt()
        )
    }

    override fun setIndicatorCount(count: Int) {
        indicatorCount = count
    }

    override fun updateSelectedIndex(index: Int) {
        lastIndex = currentIndex
        currentIndex = index

        animator?.cancel()
        animator = ValueAnimator().apply {
            setValues(
                PropertyValuesHolder.ofFloat(
                    SELECTED_WIDTH_CHANGE_VALUE_HOLDER, 0F, INDICATOR_SELECTED_WIDTH - INDICATOR_NORMAL_WIDTH
                )
            )
            duration = 500
            interpolator = AccelerateDecelerateInterpolator()

            addUpdateListener {
                val changeValue = it.getAnimatedValue(SELECTED_WIDTH_CHANGE_VALUE_HOLDER) as Float

                lastSelectedWidth = INDICATOR_SELECTED_WIDTH - changeValue
                selectedWidth = INDICATOR_NORMAL_WIDTH + changeValue

                invalidate()
            }
            start()
        }
    }

    override fun setIndicatorColor(selectedColor: Int?, unselectedColor: Int?) {
        this.selectedColor = selectedColor
        this.unselectedColor = unselectedColor
    }

    override fun onDraw(canvas: Canvas?) {
        var x = 0F
        for (index in 0 until indicatorCount) {
            x += when (index) {
                currentIndex -> {
                    selectedColor?.let {
                        paint.color = it
                    }
                    canvas?.drawRect(x, 0F, x + selectedWidth, INDICATOR_HEIGHT, paint)
                    selectedWidth + INDICATOR_SPACING
                }
                lastIndex -> {
                    unselectedColor?.let {
                        paint.color = it
                    }
                    canvas?.drawRect(x, 0F, x + lastSelectedWidth, INDICATOR_HEIGHT, paint)
                    lastSelectedWidth + INDICATOR_SPACING
                }
                else -> {
                    unselectedColor?.let {
                        paint.color = it
                    }
                    canvas?.drawRect(x, 0F, x + INDICATOR_NORMAL_WIDTH, INDICATOR_HEIGHT, paint)
                    INDICATOR_NORMAL_WIDTH + INDICATOR_SPACING
                }
            }
        }
    }
}
