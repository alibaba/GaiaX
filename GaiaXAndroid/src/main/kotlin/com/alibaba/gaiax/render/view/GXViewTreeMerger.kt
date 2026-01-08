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
 * Merge View hierarchies based on virtual node trees
 *
 * 0. The root node does not merge
 * 1. The merged hierarchy needs to meet the rules,
 *  1. Is the view - type
 *  2. No style needs to be updated
 *  3. Record the Layout to the list
 * 2. Record the merged level until it encounters a level that does not need to be merged.
 *  1. Traverse the hierarchy of records to record changes in X and y (merge hierarchy only needs to pay attention to xy movement)
 *  2. Combine xy with nodes to be rendered
 * 3. Repeat 0 to 2
 * @suppress
 **/
abstract class GXViewTreeMerger<T>(val gxTemplateContext: GXTemplateContext, val rootNode: GXNode) {

    fun build(): View {
        val rootLayout = getRootLayout()
        val rootView = withRootView(gxTemplateContext, rootNode, rootLayout)
            ?: throw IllegalArgumentException("Create root view error gxTemplateContext = $gxTemplateContext")
        val rootMerges = mutableListOf<Layout>().apply {
            this.add(rootLayout)
        }
        createMergedViewTree(gxTemplateContext, rootNode, rootView, rootMerges)
        return rootNode.view
            ?: throw IllegalArgumentException("Create root view error, not found root view gxTemplateContext = $gxTemplateContext")
    }

    abstract fun getRootLayout(): Layout

    private fun createMergedViewTree(
        context: GXTemplateContext,
        parentNode: GXNode,
        parentView: T,
        parentMerges: MutableList<Layout>
    ) {
        parentNode.children?.forEach { childNode ->

            val childTemplateNode = childNode.templateNode
            val childLayout = getChildLayout(childNode)

            val childNodeType = childTemplateNode.layer.getNodeType()
            val childCustomViewType = childTemplateNode.layer.customNodeClass
            val isChildCanBeMergedType = childTemplateNode.isCanBeMergedType()

            val isCanBeMergedNode = isChildCanBeMergedType
                    // No style
                    && childTemplateNode.css.style.isEmptyStyle()
                    // No nested
                    && (childTemplateNode.visualTemplateNode == null || childTemplateNode.visualTemplateNode.css.style.isEmptyStyle())
                    // No animation
                    && childTemplateNode.animationBinding == null
                    // No event
                    && childTemplateNode.eventBinding == null
                    // No dataBinding
                    && childTemplateNode.dataBinding == null
                    // No track
                    && childTemplateNode.trackBinding == null

            if (isCanBeMergedNode && false) {
                // This hierarchy needs to be merged
                val nextMerges = mutableListOf<Layout>().apply {
                    this.addAll(parentMerges)
                    this.add(childLayout)
                }
                createMergedViewTree(context, childNode, parentView, nextMerges)
            } else {

                // Merge XY
                var mergedX = 0.0F
                var mergedY = 0.0F

                parentMerges.forEach {
                    mergedX += it.x
                    mergedY += it.y
                }

                val childView = withChildView(
                    context,
                    parentView,
                    childNodeType,
                    childCustomViewType,
                    childNode,
                    childLayout,
                    mergedX,
                    mergedY
                ) ?: throw IllegalArgumentException("Create child view error")

                // Recurse to their own children
                if (childNode.children?.isNotEmpty() == true) {

                    // If you are view type, we need to pass yourself in as the root View
                    if (isChildCanBeMergedType) {
                        val nextMerges = mutableListOf<Layout>().apply {
                            childLayout.copy().let {
                                // If you use yourself as the root layout, there is no offset
                                it.x = 0F
                                it.y = 0F
                                this.add(it)
                            }
                        }
                        createMergedViewTree(context, childNode, childView, nextMerges)
                    } else {
                        val nextMerges = mutableListOf<Layout>().apply {
                            this.addAll(parentMerges)
                        }
                        createMergedViewTree(context, childNode, parentView, nextMerges)
                    }
                }
            }
        }
    }

    abstract fun getChildLayout(childNode: GXNode): Layout

    internal abstract fun withRootView(
        context: GXTemplateContext,
        node: GXNode,
        layout: Layout
    ): T?

    internal abstract fun withChildView(
        context: GXTemplateContext,
        parentMergeView: T,
        childType: String,
        childViewType: String?,
        childNode: GXNode,
        childLayout: Layout,
        mergeX: Float,
        mergeY: Float
    ): T?
}

