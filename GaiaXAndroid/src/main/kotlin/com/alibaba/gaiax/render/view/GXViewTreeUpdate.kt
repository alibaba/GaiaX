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

package com.alibaba.gaiax.render.view

import android.view.View
import app.visly.stretch.Layout
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode

/**
 * @suppress
 */
open class GXViewTreeUpdate(context: GXTemplateContext, rootNode: GXNode) :
    GXViewTreeMerger<View>(context, rootNode) {

    override fun withRootView(context: GXTemplateContext, node: GXNode, layout: Layout): View? {
        return node.view?.also {
            GXViewLayoutParamsUtils.updateLayoutParams(it, layout, 0F, 0F)
        }
    }

    override fun withChildView(
        context: GXTemplateContext,
        parentMergeView: View,
        childType: String,
        childViewType: String?,
        childNode: GXNode,
        childLayout: Layout,
        mergeX: Float,
        mergeY: Float
    ): View? {
        return childNode.view?.also { targetView ->
            if (childNode.isNeedShadow()) {
                childNode.boxLayoutView?.let { shadowView ->
                    GXViewLayoutParamsUtils.updateLayoutParams(
                        shadowView,
                        childLayout,
                        mergeX,
                        mergeY
                    )
                }
            }
            if (childNode.isNeedLottie()){
                childNode.lottieView?.let { lottieView ->
                    GXViewLayoutParamsUtils.updateLayoutParams(
                        lottieView,
                        childLayout,
                        mergeX,
                        mergeY
                    )
                }
            }
            GXViewLayoutParamsUtils.updateLayoutParams(targetView, childLayout, mergeX, mergeY)
        }
    }
}
