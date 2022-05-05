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

package com.alibaba.gaiax.template

import android.animation.Animator
import android.animation.AnimatorSet
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.AbsoluteLayout
import android.widget.TextView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieListener
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.R
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.factory.GXExpressionFactory
import com.alibaba.gaiax.utils.getBooleanExt
import com.alibaba.gaiax.utils.getStringExt
import com.alibaba.gaiax.utils.setValueExt

/**
 * @suppress
 */
interface GXIAnimation {
    fun doAnimation(context: GXTemplateContext, gxNode: GXNode, templateData: JSONObject) {}
}

/**
 * @suppress
 */
class GXAnimationBinding(
    // 动画类型
    // LOTTIE/PROP
    val type: GXAnimationType,
    // 是否手动触发动画
    // true是手动触发；false是自动触发；
    val trigger: Boolean,
    // 动画是否正在被触发，与trigger配合使用，需要业务方传值
    val state: GXIExpression? = null,
    // 动画数据
    val animation: GXIAnimation
) {

    fun executeAnimation(context: GXTemplateContext, child: GXNode, templateData: JSONObject) {

        if (animation is GXLottieAnimation) {
            animation.stateExp = state
        }

        val state = state?.value(templateData)

        // 符合条件触发动画
        if (trigger && (state is Boolean && state == true)) {
            animation.doAnimation(context, child, templateData)
        }
        // 自动触发动画
        else if (!trigger) {
            animation.doAnimation(context, child, templateData)
        }
    }

    companion object {

        private const val KEY_TYPE = "type"
        private const val KEY_TRIGGER = "trigger"
        private const val KEY_STATE = "state"
        private const val KEY_PROP_ANIMATOR_SET = "propAnimatorSet"
        private const val KEY_LOTTIE_ANIMATOR = "lottieAnimator"

        fun create(data: JSONObject): GXAnimationBinding? {
            val type = GXAnimationType.create(data.getString(KEY_TYPE))
            if (type != null) {
                val trigger = data.getBooleanValue(KEY_TRIGGER)
                val state = if (data.containsKey(KEY_STATE)) GXExpressionFactory.create(
                    data.getString(KEY_STATE)
                ) else null
                when (type) {
                    GXAnimationType.LOTTIE -> {
                        val lottieAnimator =
                            GXLottieAnimation.create(data.getJSONObject(KEY_LOTTIE_ANIMATOR))
                        if (lottieAnimator != null) {
                            return GXAnimationBinding(type, trigger, state, lottieAnimator)
                        }
                        return null
                    }
                    GXAnimationType.PROP -> {
                        val animatorSet =
                            GXPropAnimationSet.create(data.getJSONObject(KEY_PROP_ANIMATOR_SET))
                        if (animatorSet != null) {
                            return GXAnimationBinding(type, trigger, state, animatorSet)
                        }
                    }
                }
            }
            return null
        }
    }

    enum class GXAnimationType {
        LOTTIE,
        PROP;

        companion object {

            fun create(value: String?): GXAnimationType? {
                return when {
                    value.equals("LOTTIE", true) -> LOTTIE
                    value.equals("PROP", true) -> PROP
                    else -> null
                }
            }
        }
    }

    interface GXIPropAnimation : GXIAnimation {
        fun createAnimator(targetView: View): Animator
    }

    open class DefaultAnimatorListener : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }
    }

    class GXLottieAnimation : GXIAnimation {

        companion object {

            private const val KEY_VALUE = "value"
            private const val KEY_URL = "url"
            private const val KEY_LOOP = "loop"
            private const val KEY_LOOP_COUNT = "loopCount"

            fun create(data: JSONObject?): GXLottieAnimation? {
                val localUri = data?.getString(KEY_VALUE)
                val remoteUri = data?.getString(KEY_URL)
                if (localUri != null || remoteUri != null) {
                    val animator = GXLottieAnimation()
                    if (localUri != null) {
                        animator.localUriExp = GXExpressionFactory.create(localUri)
                    }
                    if (remoteUri != null) {
                        animator.remoteUriExp = GXExpressionFactory.create(remoteUri)
                    }
                    if (data.containsKey(KEY_LOOP) && data.getBoolean(KEY_LOOP)) {
                        animator.loopCount = Int.MAX_VALUE
                    } else if (data.containsKey(KEY_LOOP_COUNT)) {
                        animator.loopCount = data.getIntValue(KEY_LOOP_COUNT)
                    }
                    return animator
                }
                return null
            }
        }

        var stateExp: GXIExpression? = null

        var localUriExp: GXIExpression? = null

        var remoteUriExp: GXIExpression? = null

        var localUri: String? = null

        var remoteUri: String? = null

        var loopCount: Int = 0

        override fun doAnimation(
            context: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ) {
            val lottieContainer = (gxNode.viewRef?.get() as? ViewGroup) ?: return
            this.remoteUri = this.remoteUriExp?.value(templateData) as? String
            this.localUri = this.localUriExp?.value(templateData) as? String
            when {
                this.remoteUri != null -> GXRemoteLottie(
                    context,
                    lottieContainer,
                    this,
                    gxNode,
                    templateData
                ).play()
                this.localUri != null -> GXLocalLottie(
                    context,
                    lottieContainer,
                    this,
                    gxNode,
                    templateData
                ).play()
            }
        }

        open class GXDefaultAnimatorListener : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
            }

            override fun onAnimationEnd(animation: Animator?) {
            }

            override fun onAnimationCancel(animation: Animator?) {
            }

            override fun onAnimationStart(animation: Animator?) {
            }
        }

        class GXLocalLottie(
            val context: GXTemplateContext,
            private val lottieContainer: ViewGroup,
            val animator: GXLottieAnimation,
            val gxNode: GXNode,
            val templateData: JSONObject
        ) {

            private fun appendJson(value: String?): String? {
                if (value != null && !value.endsWith(".json")) {
                    return "$value.json"
                }
                return value
            }

            private fun createLottieView(context: Context): LottieAnimationView {
                val lottieView: LottieAnimationView = LayoutInflater.from(context)
                    .inflate(R.layout.gaiax_inner_lottie_auto_play, null) as LottieAnimationView
                lottieView.layoutParams = AbsoluteLayout.LayoutParams(
                    AbsoluteLayout.LayoutParams.MATCH_PARENT,
                    AbsoluteLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0
                )
                return lottieView
            }

            private fun initLottieLocalResourceDir(value: String, lottieView: LottieAnimationView) {
                val dirIndex = value.indexOf("/")
                if (dirIndex > 0) {
                    val dir = value.substring(0, dirIndex)
                    if (dir.isNotEmpty()) {
                        lottieView.imageAssetsFolder = "$dir/images/"
                    }
                }
            }

            fun play() {
                val lottieView: LottieAnimationView? = if (lottieContainer.childCount == 0) {
                    createLottieView(lottieContainer.context)
                } else {
                    lottieContainer.getChildAt(0) as? LottieAnimationView
                }

                if (lottieView?.isAnimating == true || gxNode.isAnimating) {
                    return
                }

                val localUri = animator.localUri ?: return

                if (lottieView == null) {
                    return
                }

                initLottieLocalResourceDir(localUri, lottieView)

                lottieView.removeAllAnimatorListeners()
                lottieView.removeAllUpdateListeners()
                lottieView.removeAllLottieOnCompositionLoadedListener()

                lottieView.setAnimation(appendJson(localUri))
                lottieView.repeatCount = animator.loopCount
                lottieView.addAnimatorListener(object : GXDefaultAnimatorListener() {

                    override fun onAnimationEnd(animation: Animator?) {
                        gxNode.isAnimating = false
                        lottieView.removeAllAnimatorListeners()
                        lottieView.removeAllUpdateListeners()
                        lottieView.removeAllLottieOnCompositionLoadedListener()
                        lottieView.progress = 1F
                        val stateExp = animator.stateExp
                        GXExpressionFactory.valuePath(stateExp?.expression())?.let {
                            templateData.setValueExt(it, false)
                        }
                        context.templateData?.eventListener?.onAnimationEvent(
                            GXTemplateEngine.GXAnimation().apply {
                                this.state = "END"
                                this.nodeId = gxNode.id
                                this.view = lottieView
                            })
                    }

                    override fun onAnimationStart(animation: Animator?) {
                        context.templateData?.eventListener?.onAnimationEvent(
                            GXTemplateEngine.GXAnimation().apply {
                                this.state = "START"
                                this.nodeId = gxNode.id
                                this.view = lottieView
                            })
                    }

                })
                lottieView.playAnimation()

                if (lottieContainer.childCount == 0) {
                    lottieView.isClickable = false
                    lottieContainer.isClickable = false
                    lottieContainer.addView(lottieView)
                }
            }
        }

        class GXRemoteLottie(
            val context: GXTemplateContext,
            private val lottieContainer: ViewGroup,
            val animator: GXLottieAnimation,
            val gxNode: GXNode,
            val rawJson: JSON
        ) {

            private fun createLottieView(context: Context): LottieAnimationView {
                val lottieView: LottieAnimationView = LayoutInflater.from(context)
                    .inflate(R.layout.gaiax_inner_lottie_auto_play, null) as LottieAnimationView
                lottieView.layoutParams = AbsoluteLayout.LayoutParams(
                    AbsoluteLayout.LayoutParams.MATCH_PARENT,
                    AbsoluteLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0
                )
                return lottieView
            }

            fun play() {
                val lottieView: LottieAnimationView? = if (lottieContainer.childCount == 0) {
                    createLottieView(lottieContainer.context)
                } else {
                    lottieContainer.getChildAt(0) as? LottieAnimationView
                }

                if (lottieView?.isAnimating == true || gxNode.isAnimating) {
                    return
                }

                val remoteUri = animator.remoteUri ?: return

                if (lottieView == null) {
                    return
                }

                lottieView.removeAllAnimatorListeners()
                lottieView.removeAllUpdateListeners()
                lottieView.removeAllLottieOnCompositionLoadedListener()

                gxNode.isAnimating = true
                val downloadTask = LottieCompositionFactory.fromUrl(lottieView.context, remoteUri)
                downloadTask.addListener(object : LottieListener<LottieComposition> {

                    override fun onResult(composition: LottieComposition?) {
                        downloadTask.removeListener(this)
                        gxNode.isAnimating = composition != null

                        composition?.let { it ->
                            lottieView.setComposition(it)
                            lottieView.repeatCount = animator.loopCount
                            lottieView.addAnimatorListener(object : DefaultAnimatorListener() {
                                override fun onAnimationEnd(animation: Animator?) {
                                    gxNode.isAnimating = false

                                    lottieView.removeAllAnimatorListeners()
                                    lottieView.removeAllUpdateListeners()
                                    lottieView.removeAllLottieOnCompositionLoadedListener()

                                    lottieView.progress = 1F
                                    val stateExp = animator.stateExp
                                    GXExpressionFactory.valuePath(stateExp?.expression())?.let {
                                        rawJson.setValueExt(it, false)
                                    }
                                    context.templateData?.eventListener?.onAnimationEvent(
                                        GXTemplateEngine.GXAnimation().apply {
                                            this.state = "END"
                                            this.nodeId = gxNode.id
                                            this.view = lottieView
                                        })
                                }

                                override fun onAnimationStart(animation: Animator?) {
                                    gxNode.isAnimating = true
                                    context.templateData?.eventListener?.onAnimationEvent(
                                        GXTemplateEngine.GXAnimation().apply {
                                            this.state = "START"
                                            this.nodeId = gxNode.id
                                            this.view = lottieView
                                        })
                                }
                            })
                            lottieView.playAnimation()
                        }
                    }
                })

                if (lottieContainer.childCount == 0) {
                    lottieView.isClickable = false
                    lottieContainer.isClickable = false
                    lottieContainer.addView(lottieView)
                }
            }
        }
    }

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
                valueAnimator.setIntValues(value.valueFrom.value, value.valueTo.value)
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

    class GXPropAnimationSet : GXIAnimation, GXIPropAnimation {

        override fun doAnimation(
            context: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ) {
            gxNode.viewRef?.get()?.let { targetView ->
                playAnimation(context, gxNode, targetView)
            }
        }

        private fun playAnimation(context: GXTemplateContext, child: GXNode, targetView: View) {
            if (child.propAnimatorSet == null) {
                child.propAnimatorSet = createAnimator(targetView) as AnimatorSet
            }
            if (child.isAnimating || child.propAnimatorSet?.isRunning == true) {
                child.propAnimatorSet?.cancel()
                child.isAnimating = false
            }
            child.propAnimatorSet?.removeAllListeners()
            child.propAnimatorSet?.addListener(object : DefaultAnimatorListener() {
                override fun onAnimationStart(animation: Animator?) {
                    context.templateData?.eventListener?.onAnimationEvent(
                        GXTemplateEngine.GXAnimation().apply {
                            this.state = "START"
                            this.nodeId = child.id
                            this.view = targetView
                        })
                }

                override fun onAnimationCancel(animation: Animator?) {
                    child.isAnimating = false
                }

                override fun onAnimationEnd(animation: Animator?) {
                    child.isAnimating = false
                    context.templateData?.eventListener?.onAnimationEvent(
                        GXTemplateEngine.GXAnimation().apply {
                            this.state = "END"
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
                                    GXPropAnimation.create(it.getJSONObject(KEY_PROP_ANIMATOR))
                                        ?.apply {
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
                    GXPropValueType.create(data.getStringExt(KEY_VALUE_TYPE))?.let {
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
                        val valueFrom = data.getStringExt(KEY_VALUE_FROM)
                        val valueTo = data.getStringExt(KEY_VALUE_TO)
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
                        val valueFrom = data.getStringExt(KEY_VALUE_FROM)
                        val valueTo = data.getStringExt(KEY_VALUE_TO)
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

}