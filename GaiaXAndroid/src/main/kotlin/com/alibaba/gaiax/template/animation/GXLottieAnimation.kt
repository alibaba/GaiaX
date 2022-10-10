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
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXIExpression
import com.alibaba.gaiax.template.factory.GXExpressionFactory

abstract class GXLottieAnimation : GXIAnimation {

    override fun executeAnimation(
        gxState: GXIExpression?,
        gxAnimationExpression: GXIExpression?,
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateData: JSONObject
    ) {
        // nothing
    }

    var gxLocalUri: GXIExpression? = null
    var gxRemoteUri: GXIExpression? = null
    var loopCount: Int = 0

    companion object {
        private const val KEY_VALUE = "value"
        private const val KEY_URL = "url"
        private const val KEY_LOOP = "loop"
        private const val KEY_LOOP_COUNT = "loopCount"

        fun create(expVersion: String?, data: JSONObject?): GXLottieAnimation? {
            if (data == null) {
                return null
            }
            val localUri = data.getString(KEY_VALUE)
            val remoteUri = data.getString(KEY_URL)
            if (localUri == null && remoteUri == null) {
                return null
            }
            val animator = GXRegisterCenter.instance.extensionLottieAnimation?.create()
                ?: return null
            if (localUri != null) {
                animator.gxLocalUri = GXExpressionFactory.create(expVersion, localUri)
            }
            if (remoteUri != null) {
                animator.gxRemoteUri = GXExpressionFactory.create(expVersion, remoteUri)
            }
            if (data.containsKey(KEY_LOOP) && data.getBoolean(KEY_LOOP)) {
                animator.loopCount = Int.MAX_VALUE
            } else if (data.containsKey(KEY_LOOP_COUNT)) {
                animator.loopCount = data.getIntValue(KEY_LOOP_COUNT)
            }
            return animator
        }
    }
}