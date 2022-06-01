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

package com.alibaba.gaiax.template.animation

import android.animation.Animator
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXIExpression
import com.alibaba.gaiax.utils.getBooleanExt

class GXPropAnimation(
    val name: GXPropAnimationSet.GXPropName,
    val value: GXPropAnimationSet.GXPropValue
) : GXIPropAnimation {

    override fun createAnimator(targetView: View): Animator {
        val valueAnimator = ValueAnimator()
        valueAnimator.repeatCount = loopCount
        valueAnimator.repeatMode =
            if (loopMode == GXPropAnimationSet.GXPropLoopMode.RESET) ValueAnimator.RESTART else ValueAnimator.REVERSE
        valueAnimator.duration = duration.toLong()
        valueAnimator.interpolator = interpolator.value()
        if (value is GXPropAnimationSet.GXPropValue.GXPropValueFloat) {
            valueAnimator.setFloatValues(value.valueFrom, value.valueTo)
        } else if (value is GXPropAnimationSet.GXPropValue.GXPropValueColor) {
            if (argbEvaluator == null) {
                argbEvaluator = ArgbEvaluator()
            }
            valueAnimator.setEvaluator(argbEvaluator)
            valueAnimator.setIntValues(value.valueFrom.value(), value.valueTo.value())
        }
        valueAnimator.addUpdateListener {
            if (it.animatedValue is Float) {
                name.setValue(targetView, it.animatedValue as Float)
            } else if (it.animatedValue is Int) {
                name.setColorValue(targetView, it.animatedValue as Int)
            }
        }
        valueAnimator.startDelay = delay
        return valueAnimator
    }

    override fun executeAnimation(
        gxState: GXIExpression?,
        gxAnimationExpression: GXIExpression?,
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: JSONObject
    ) {

    }

    var duration: Int = 300
    var interpolator: GXPropAnimationSet.GXPropInterpolator =
        GXPropAnimationSet.GXPropInterpolator.STANDARD
    var loopCount: Int = 0
    var loopMode: GXPropAnimationSet.GXPropLoopMode = GXPropAnimationSet.GXPropLoopMode.RESET
    var delay: Long = 0

    companion object {

        var argbEvaluator: ArgbEvaluator? = null

        private const val KEY_DURATION = "duration"
        private const val KEY_LOOP = "loop"
        private const val KEY_DELAY = "delay"
        private const val KEY_LOOP_COUNT = "loopCount"
        private const val KEY_INTERPOLATOR = "interpolator"
        private const val KEY_PROP_NAME = "propName"

        fun create(data: JSONObject): GXPropAnimation? {
            val propName = GXPropAnimationSet.GXPropName.create(data.getString(KEY_PROP_NAME))
            val propValue = GXPropAnimationSet.GXPropValue.create(data)
            if (propName != null && propValue != null) {
                val animator = GXPropAnimation(propName, propValue)

                data.getString(KEY_DURATION)?.apply {
                    animator.duration = this.toInt()
                }

                data.getString(KEY_INTERPOLATOR)?.apply {
                    animator.interpolator = GXPropAnimationSet.GXPropInterpolator.create(this)
                }

                data.getString(GXPropAnimationSet.GXPropLoopMode.KEY_LOOP_MODE)?.apply {
                    animator.loopMode = GXPropAnimationSet.GXPropLoopMode.create(this)
                }

                if (data.containsKey(KEY_LOOP) && data.getBooleanExt(KEY_LOOP)) {
                    animator.loopCount = Int.MAX_VALUE
                } else if (data.containsKey(KEY_LOOP_COUNT)) {
                    if (animator.loopMode == GXPropAnimationSet.GXPropLoopMode.REVERSE) {
                        animator.loopCount =
                            Math.max(1, data.getIntValue(KEY_LOOP_COUNT) * 2 - 1)
                    } else {
                        animator.loopCount = Math.max(0, data.getIntValue(KEY_LOOP_COUNT) - 1)
                    }
                }

                if (data.containsKey(KEY_DELAY)) {
                    animator.delay = data.getLongValue(KEY_DELAY)
                }

                return animator
            }
            return null
        }
    }
}