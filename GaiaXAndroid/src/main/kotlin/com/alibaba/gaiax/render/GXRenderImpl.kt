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
import java.lang.ref.SoftReference

/**
 * @suppress
 */
class GXRenderImpl {

    fun createNode(templateContext: GXTemplateContext): GXNode {
        val rootNode = GXNodeTreeCreator.create(templateContext)
        templateContext.rootNode = rootNode
        return rootNode
    }

    fun bindNodeData(templateContext: GXTemplateContext) {
        val rootNode = templateContext.rootNode
            ?: throw IllegalArgumentException("RootNode is null")
        templateContext.isDirty = false

        // Update the virtual node tree
        GXNodeTreeUpdater(templateContext).buildLayoutAndStyle()
    }

    fun createView(templateContext: GXTemplateContext): View {
        // Create a virtual node tree
        val rootNode = GXNodeTreeCreator.create(templateContext)
        templateContext.rootNode = rootNode

        // Create a view based on the virtual node tree
        val rootView = GXViewTreeCreator(templateContext, rootNode).build().apply {
            (this as GXIRootView).setTemplateContext(templateContext)
        }
        templateContext.rootView = SoftReference(rootView)

        return templateContext.rootView?.get()
            ?: throw IllegalArgumentException("Create template view exception, templateContext = $templateContext")
    }

    fun bindViewData(templateContext: GXTemplateContext) {
        val rootNode = templateContext.rootNode
            ?: throw IllegalArgumentException("RootNode is null")

        // Resetting the Template Status
        templateContext.isDirty = false

        // Update the virtual node tree
        GXNodeTreeUpdater(templateContext).buildLayoutAndStyle()

        // Update view layout
        GXViewTreeUpdater(templateContext, rootNode).build()
    }

    fun bindViewDataOnlyNodeTree(templateContext: GXTemplateContext) {
        // Resetting the Template Status
        templateContext.isDirty = false

        // Update the node tree
        GXNodeTreeUpdater(templateContext).buildNodeLayout()
    }

    fun bindViewDataOnlyViewTree(templateContext: GXTemplateContext) {
        val rootNode = templateContext.rootNode
            ?: throw IllegalArgumentException("RootNode is null")

        // Update the view tree
        GXNodeTreeUpdater(templateContext).buildViewStyle()

        // Update view layout
        GXViewTreeUpdater(templateContext, rootNode).build()
    }
}