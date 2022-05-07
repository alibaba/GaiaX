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

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXIExpression
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.template.factory.GXExpressionFactory

/**
 * @suppress
 */
class GXAnimationBinding(
    // 动画类型
    // LOTTIE/PROP
    val type: String,
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
            val type = data.getString(KEY_TYPE)
            if (type != null) {

                val trigger = data.getBooleanValue(KEY_TRIGGER)

                val state = if (data.containsKey(KEY_STATE)) GXExpressionFactory.create(
                    data.getString(KEY_STATE)
                ) else null

                if (type.equals(GXTemplateKey.GAIAX_ANIMATION_TYPE_LOTTIE, false)) {
                    val lottieAnimator =
                        GXLottieAnimation.create(data.getJSONObject(KEY_LOTTIE_ANIMATOR))
                    if (lottieAnimator != null) {
                        return GXAnimationBinding(type, trigger, state, lottieAnimator)
                    }
                    return null
                } else if (type.equals(GXTemplateKey.GAIAX_ANIMATION_TYPE_PROP, false)) {
                    val animatorSet =
                        GXPropAnimationSet.create(data.getJSONObject(KEY_PROP_ANIMATOR_SET))
                    if (animatorSet != null) {
                        return GXAnimationBinding(type, trigger, state, animatorSet)
                    }
                }
            }
            return null
        }
    }

}