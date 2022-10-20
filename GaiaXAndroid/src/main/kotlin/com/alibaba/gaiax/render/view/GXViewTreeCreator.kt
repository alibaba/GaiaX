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
import android.view.ViewGroup
import app.visly.stretch.Layout
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.render.view.basic.GXShadowLayout

/**
 * @suppress
 */
class GXViewTreeCreator(gxTemplateContext: GXTemplateContext, rootNode: GXNode) :
    GXViewTreeMerger<View>(gxTemplateContext, rootNode) {

    override fun withRootView(context: GXTemplateContext, node: GXNode, layout: Layout): View {
        val rootView = GXViewFactory.createView<View>(
            context.context,
            rootNode.getType(),
            rootNode.getCustomViewClass()
        ).apply {
            this.layoutParams = GXViewLayoutParamsUtils.createLayoutParams(rootNode, layout)
            rootNode.view = this
        }
        return rootView
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

        // If have the shadow
        // You need to build your own BOXLAYOUT and put it in the same place as the horizontal one
        if (childNode.isNeedShadow()) {
            GXViewFactory.createView<View>(context.context, GXViewKey.VIEW_TYPE_SHADOW_LAYOUT, null)
                .apply {
                    this.layoutParams = GXViewLayoutParamsUtils.createLayoutParams(
                        childNode,
                        childLayout,
                        mergeX,
                        mergeY
                    )
                    childNode.boxLayoutView = this
                    (this as? GXShadowLayout)?.setStyle(childNode.templateNode.css.style)
                    if (parentMergeView is ViewGroup) {
                        parentMergeView.addView(this)
                    }
                }
        }

        // Child nodes
        val childView =
            GXViewFactory.createView<View>(context.context, childType, childViewType).apply {
                this.layoutParams = GXViewLayoutParamsUtils.createLayoutParams(
                    childNode,
                    childLayout,
                    mergeX,
                    mergeY
                )
                childNode.view = this
                if (parentMergeView is ViewGroup) {
                    parentMergeView.addView(this)
                }
            }

        //没有采用注册进ViewFactory的方式创建对象是因为lottie必须用xml形式创建。
        if (childNode.isNeedLottie()){
            GXRegisterCenter.instance.extensionLottieAnimation?.localCreateLottieView(context.context)?.apply {
                this.layoutParams = GXViewLayoutParamsUtils.createLayoutParams(
                    childNode,
                    childLayout,
                    mergeX,
                    mergeY
                )
                childNode.lottieView = this
                if (parentMergeView is ViewGroup) {
                    parentMergeView.addView(this)
                }
            }
        }
        return childView
    }
}