package com.alibaba.gaiax.utils

import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.FloatRange

object GXColorUtils {


    fun blendARGB(@ColorInt color1: Int, @ColorInt color2: Int, @FloatRange(from = 0.0, to = 1.0) ratio: Float): Int {
        val inverseRatio = 1.0f - ratio
        val a = Color.alpha(color1).toFloat() * inverseRatio + Color.alpha(color2).toFloat() * ratio
        val r = Color.red(color1).toFloat() * inverseRatio + Color.red(color2).toFloat() * ratio
        val g = Color.green(color1).toFloat() * inverseRatio + Color.green(color2).toFloat() * ratio
        val b = Color.blue(color1).toFloat() * inverseRatio + Color.blue(color2).toFloat() * ratio
        return Color.argb(a.toInt(), r.toInt(), g.toInt(), b.toInt())
    }
}