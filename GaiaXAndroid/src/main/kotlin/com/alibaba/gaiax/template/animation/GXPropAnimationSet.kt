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
import android.animation.AnimatorSet
import android.view.View
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXColor
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx

class GXPropAnimationSet : GXIPropAnimation {

    fun playAnimation(context: GXTemplateContext, child: GXNode, targetView: View) {
        if (child.propAnimatorSet == null) {
            child.propAnimatorSet = createAnimator(targetView) as AnimatorSet
        }
        if (child.isAnimating || child.propAnimatorSet?.isRunning == true) {
            child.propAnimatorSet?.cancel()
            child.isAnimating = false
        }
        child.propAnimatorSet?.removeAllListeners()
        child.propAnimatorSet?.addListener(object : GXDefaultAnimatorListener() {
            override fun onAnimationStart(animation: Animator) {
                context.templateData?.eventListener?.onAnimationEvent(
                    GXTemplateEngine.GXAnimation().apply {
                        this.state = GXTemplateEngine.GXAnimation.STATE_START
                        this.nodeId = child.id
                        this.view = targetView
                    })
            }

            override fun onAnimationCancel(animation: Animator) {
                child.isAnimating = false
            }

            override fun onAnimationEnd(animation: Animator) {
                child.isAnimating = false
                context.templateData?.eventListener?.onAnimationEvent(
                    GXTemplateEngine.GXAnimation().apply {
                        this.state = GXTemplateEngine.GXAnimation.STATE_END
                        this.nodeId = child.id
                        this.view = targetView
                    })
            }
        })
        child.propAnimatorSet?.start()
    }

    override fun createAnimator(targetView: View): Animator {
        return AnimatorSet().apply {
            when (ordering) {
                GXPropOrderingType.TOGETHER -> {
                    val items = mutableListOf<Animator>()
                    animations.forEach {
                        items.add(it.createAnimator(targetView))
                    }
                    this.playTogether(items)
                }
                GXPropOrderingType.SEQUENTIALLY -> {
                    val items = mutableListOf<Animator>()
                    animations.forEach {
                        items.add(it.createAnimator(targetView))
                    }
                    this.playSequentially(items)
                }
            }
        }
    }

    var ordering: GXPropOrderingType = GXPropOrderingType.TOGETHER

    val animations: MutableList<GXIPropAnimation> = mutableListOf()

    companion object {

        private const val KEY_PROP_ANIMATOR_SET = "propAnimatorSet"
        private const val KEY_PROP_ANIMATOR = "propAnimator"
        private const val KEY_ANIMATORS = "animators"

        fun create(data: JSONObject?): GXPropAnimationSet? {
            if (data != null) {
                val animators = data.getJSONArray(KEY_ANIMATORS)
                if (animators != null && animators.isNotEmpty()) {
                    val set = GXPropAnimationSet()
                    animators.forEach {
                        if (it is JSONObject) {
                            if (it.containsKey(KEY_PROP_ANIMATOR_SET)) {
                                create(it.getJSONObject(KEY_PROP_ANIMATOR_SET))?.apply {
                                    set.animations.add(this)
                                }
                            } else if (it.containsKey(KEY_PROP_ANIMATOR)) {
                                GXPropAnimation.create(it.getJSONObject(KEY_PROP_ANIMATOR))?.apply {
                                    set.animations.add(this)
                                }
                            } else {
                                GXPropAnimation.create(it)?.apply {
                                    set.animations.add(this)
                                }
                            }
                        }
                    }
                    data.getString(GXPropOrderingType.KEY_ORDERING)?.apply {
                        set.ordering = GXPropOrderingType.create(this)
                    }
                    return set
                }
            }
            return null
        }
    }

    enum class GXPropLoopMode {
        RESET,
        REVERSE;

        companion object {

            const val KEY_LOOP_MODE = "loopMode"

            fun create(data: String) = when {
                data.equals("RESET", true) -> RESET
                data.equals("REVERSE", true) -> REVERSE
                else -> RESET
            }
        }
    }

    sealed class GXPropValue {

        companion object {

            private const val KEY_VALUE_TYPE = "valueType"
            private const val KEY_VALUE_FROM = "valueFrom"
            private const val KEY_VALUE_TO = "valueTo"

            fun create(data: JSONObject): GXPropValue? {
                GXPropValueType.create(data.getString(KEY_VALUE_TYPE) ?: "")?.let {
                    return when (it) {
                        GXPropValueType.IntType -> GXPropValueFloat.create(data)
                        GXPropValueType.FloatType -> GXPropValueFloat.create(data)
                        GXPropValueType.ColorType -> GXPropValueColor.create(data)
                    }
                }
                return null
            }
        }

        class GXPropValueColor(val valueFrom: GXColor, val valueTo: GXColor) : GXPropValue() {
            companion object {
                fun create(data: JSONObject): GXPropValueColor? {
                    val valueFrom = data.getString(KEY_VALUE_FROM) ?: ""
                    val valueTo = data.getString(KEY_VALUE_TO) ?: ""
                    if (valueFrom.isNotEmpty() && valueFrom.isNotEmpty()) {
                        val valueFrom1 = GXColor.create(valueFrom)
                        val valueTo1 = GXColor.create(valueTo)
                        if (valueFrom1 != null && valueTo1 != null) {
                            return GXPropValueColor(valueFrom1, valueTo1)
                        }
                    }
                    return null
                }
            }
        }

        class GXPropValueFloat(val valueFrom: Float, val valueTo: Float) : GXPropValue() {
            companion object {
                fun create(data: JSONObject): GXPropValueFloat? {
                    val valueFrom = data.getString(KEY_VALUE_FROM) ?: ""
                    val valueTo = data.getString(KEY_VALUE_TO) ?: ""
                    if (valueFrom.isNotEmpty() && valueFrom.isNotEmpty()) {
                        return GXPropValueFloat(valueFrom.toFloat(), valueTo.toFloat())
                    }
                    return null
                }
            }
        }


    }

    enum class GXPropValueType {
        IntType,
        FloatType,
        ColorType;

        companion object {
            fun create(value: String) = when (value) {
                "intType" -> IntType
                "floatType" -> FloatType
                "colorType" -> ColorType
                else -> null
            }
        }
    }

    enum class GXPropInterpolator {
        LINEAR,
        ACCELERATE,
        DECELERATE,
        STANDARD,
        ANTICIPATE,
        OVERSHOOT,
        SPRING,
        BOUNCE,
        COSINE;

        fun value(): Interpolator {
//                when (this) {
//                    LINEAR -> GaiaMotionCurveLinearInterpolator()
//                    ACCELERATE -> GaiaMotionCurveAccelerateInterpolator()
//                    DECELERATE -> GaiaMotionCurveDecelerateInterpolator()
//                    STANDARD -> GaiaMotionCurveStandardInterpolator()
//                    ANTICIPATE -> GaiaMotionCurveAnticipateInterpolator()
//                    OVERSHOOT -> GaiaMotionCurveOvershootInterpolator()
//                    SPRING -> GaiaMotionCurveSpringInterpolator()
//                    BOUNCE -> GaiaMotionCurveBounceInterpolator()
//                    COSINE -> GaiaMotionCurveCosineInterpolator()
//                }
            return LinearInterpolator()
        }

        companion object {
            private const val KEY_LINEAR = "LINEAR"
            private const val KEY_ACCELERATE = "ACCELERATE"
            private const val KEY_DECELERATE = "DECELERATE"
            private const val KEY_STANDARD = "STANDARD"
            private const val KEY_ANTICIPATE = "ANTICIPATE"
            private const val KEY_OVERSHOOT = "OVERSHOOT"
            private const val KEY_SPRING = "SPRING"
            private const val KEY_BOUNCE = "BOUNCE"
            private const val KEY_COSINE = "COSINE"

            fun create(value: String) = when {
                value.equals(KEY_LINEAR, true) -> LINEAR
                value.equals(KEY_ACCELERATE, true) -> ACCELERATE
                value.equals(KEY_DECELERATE, true) -> DECELERATE
                value.equals(KEY_STANDARD, true) -> STANDARD
                value.equals(KEY_ANTICIPATE, true) -> ANTICIPATE
                value.equals(KEY_OVERSHOOT, true) -> OVERSHOOT
                value.equals(KEY_SPRING, true) -> SPRING
                value.equals(KEY_BOUNCE, true) -> BOUNCE
                value.equals(KEY_COSINE, true) -> COSINE
                else -> STANDARD
            }
        }
    }

    enum class GXPropName {
        POSITION_X,
        POSITION_Y,
        OPACITY,
        SCALE,
        ROTATION,
        RENDER_COLOR;

        fun setValue(targetView: View, finalValue: Float) {
            when (this) {
                POSITION_X -> setPositionX(targetView, finalValue)
                POSITION_Y -> setPositionY(targetView, finalValue)
                OPACITY -> setOpacity(targetView, finalValue)
                SCALE -> {
                    setScaleX(targetView, finalValue)
                    setScaleY(targetView, finalValue)
                }
                ROTATION -> setRotation(targetView, finalValue)
                else -> {
                }
            }
        }

        fun setColorValue(targetView: View, color: Int) {
            setRenderColor(targetView, color)
        }

        private fun setPositionX(targetView: View, value: Float) {
            targetView.translationX = value.dpToPx()
        }

        private fun setPositionY(targetView: View, value: Float) {
            targetView.translationY = value.dpToPx()
        }

        private fun setOpacity(targetView: View, value: Float) {
            targetView.alpha = value
        }

        private fun setScaleX(targetView: View, value: Float) {
            targetView.scaleX = value
        }

        private fun setScaleY(targetView: View, value: Float) {
            targetView.scaleY = value
        }

        private fun setRotation(targetView: View, value: Float) {
            targetView.rotation = value
        }

        private fun setRenderColor(targetView: View, value: Int) {
            if (targetView is TextView) {
                targetView.setTextColor(value)
            } else {
                targetView.setBackgroundColor(value)
            }
        }

        companion object {
            private const val KEY_POSITION_X = "positionX"
            private const val KEY_POSITION_Y = "positionY"
            private const val KEY_OPACITY = "opacity"
            private const val KEY_SCALE = "scale"
            private const val KEY_ROTATION = "rotation"
            private const val KEY_RENDER_COLOR = "renderColor"

            fun create(value: String?) = when {
                value.equals(KEY_POSITION_X, true) -> POSITION_X
                value.equals(KEY_POSITION_Y, true) -> POSITION_Y
                value.equals(KEY_OPACITY, true) -> OPACITY
                value.equals(KEY_SCALE, true) -> SCALE
                value.equals(KEY_ROTATION, true) -> ROTATION
                value.equals(KEY_RENDER_COLOR, true) -> RENDER_COLOR
                else -> null
            }
        }
    }

    enum class GXPropOrderingType {
        TOGETHER,
        SEQUENTIALLY;

        companion object {
            internal const val KEY_ORDERING = "ordering"
            private const val KEY_SEQUENTIALLY = "SEQUENTIALLY"

            fun create(value: String) =
                if (value.equals(KEY_SEQUENTIALLY, true)) SEQUENTIALLY else TOGETHER
        }
    }
}