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

package com.alibaba.gaiax.render

import android.view.View
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.render.node.GXNodeTreeCreator
import com.alibaba.gaiax.render.node.GXNodeTreeUpdater
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.GXViewTreeCreator
import com.alibaba.gaiax.render.view.GXViewTreeUpdater

/**
 * @suppress
 */
class GXRenderImpl {

    fun createNode(gxTemplateContext: GXTemplateContext): GXNode {
        val rootNode = GXNodeTreeCreator.create(gxTemplateContext)
        gxTemplateContext.rootNode = rootNode
        return rootNode
    }

    fun bindNodeData(gxTemplateContext: GXTemplateContext) {
        val rootNode = gxTemplateContext.rootNode
            ?: throw IllegalArgumentException("RootNode is null(bindNodeData) gxTemplateContext = $gxTemplateContext")
        gxTemplateContext.isDirty = false

        // Update the virtual node tree
        GXNodeTreeUpdater(gxTemplateContext).buildLayoutAndStyle()
    }

    fun createView(gxTemplateContext: GXTemplateContext): View {
        // Create a virtual node tree
        val rootNode = GXNodeTreeCreator.create(gxTemplateContext)
        gxTemplateContext.rootNode = rootNode

        // Create a view based on the virtual node tree
        val rootView = GXViewTreeCreator(gxTemplateContext, rootNode).build().apply {
            (this as GXIRootView).setTemplateContext(gxTemplateContext)
        }
        gxTemplateContext.rootView = rootView

        return gxTemplateContext.rootView
            ?: throw IllegalArgumentException("Create template view exception, gxTemplateContext = $gxTemplateContext")
    }

    fun createViewOnlyNodeTree(gxTemplateContext: GXTemplateContext): GXNode {
        // Create a virtual node tree
        val rootNode = GXNodeTreeCreator.create(gxTemplateContext)
        gxTemplateContext.rootNode = rootNode
        return rootNode
    }

    fun createViewOnlyViewTree(gxTemplateContext: GXTemplateContext): View {
        val rootNode = gxTemplateContext.rootNode
            ?: throw IllegalArgumentException("Create template view exception, root node null, $gxTemplateContext")

        // Create a view based on the virtual node tree
        val rootView = GXViewTreeCreator(gxTemplateContext, rootNode).build().apply {
            (this as GXIRootView).setTemplateContext(gxTemplateContext)
        }
        gxTemplateContext.rootView = rootView

        return gxTemplateContext.rootView
            ?: throw IllegalArgumentException("Create template view exception, gxTemplateContext = $gxTemplateContext")
    }

    fun bindViewData(gxTemplateContext: GXTemplateContext) {
        val rootNode = gxTemplateContext.rootNode
            ?: throw IllegalArgumentException("RootNode is null(bindViewData) gxTemplateContext = $gxTemplateContext")

        // Resetting the Template Status
        gxTemplateContext.isDirty = false

        // Update the virtual node tree
        GXNodeTreeUpdater(gxTemplateContext).buildLayoutAndStyle()

        // Update view layout
        GXViewTreeUpdater(gxTemplateContext, rootNode).build()
    }

    fun bindViewDataOnlyNodeTree(gxTemplateContext: GXTemplateContext) {
        // Resetting the Template Status
        gxTemplateContext.isDirty = false

        // Update the node tree
        GXNodeTreeUpdater(gxTemplateContext).buildNodeLayout()
    }

    fun bindViewDataOnlyViewTree(gxTemplateContext: GXTemplateContext) {
        val rootNode = gxTemplateContext.rootNode
            ?: throw IllegalArgumentException("RootNode is null(bindViewDataOnlyViewTree) gxTemplateContext = $gxTemplateContext")

        // Update the view tree
        GXNodeTreeUpdater(gxTemplateContext).buildViewStyle()

        // Update view layout
        GXViewTreeUpdater(gxTemplateContext, rootNode).build()
    }
}