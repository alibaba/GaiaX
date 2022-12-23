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
import com.alibaba.gaiax.template.GXIExpression
import com.alibaba.gaiax.template.factory.GXExpressionFactory

/**
 * @suppress
 */
class GXAnimationBinding(
    val type: String,
    val animation: GXIExpression
) {

    companion object {

        const val KEY_TYPE = "type"
        const val KEY_TRIGGER = "trigger"
        const val KEY_STATE = "state"
        const val KEY_PROP_ANIMATOR_SET = "propAnimatorSet"
        const val KEY_LOTTIE_ANIMATOR = "lottieAnimator"

        fun create(expVersion: String?, data: JSONObject): GXAnimationBinding? {
            val typeSrc = data.getString(KEY_TYPE) ?: return null
            val type = GXExpressionFactory
                .create(expVersion, typeSrc)?.value()?.toString() ?: return null
            GXExpressionFactory.create(expVersion, data)?.let {
                return GXAnimationBinding(type, it)
            }
            return null
        }
    }
}