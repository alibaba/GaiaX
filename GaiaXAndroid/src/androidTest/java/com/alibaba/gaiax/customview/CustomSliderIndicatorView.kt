package com.alibaba.gaiax.customview

import android.content.Context
import android.util.AttributeSet
import com.alibaba.gaiax.render.view.container.slider.GXSliderBaseIndicatorView

/**
 * @author guaiyu
 * @date 2022/11/23 11:40
 */
class CustomSliderIndicatorView: GXSliderBaseIndicatorView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    override fun setIndicatorCount(count: Int) {
    }

    override fun updateSelectedIndex(index: Int) {
    }

    override fun setIndicatorColor(selectedColor: Int?, unselectedColor: Int?) {
    }
}
