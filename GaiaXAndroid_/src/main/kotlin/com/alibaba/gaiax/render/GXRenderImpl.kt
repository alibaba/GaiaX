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
import com.alibaba.gaiax.render.node.GXViewNodeTreeUpdater
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
        if (rootNode != null) {

            templateContext.isDirty = false

            // Update the virtual node tree
            GXViewNodeTreeUpdater(templateContext).build()
        }
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

        onCreate(templateContext)

        return templateContext.rootView?.get() ?: throw IllegalArgumentException("Create template view exception, templateContext = $templateContext")
    }

    fun bindViewData(templateContext: GXTemplateContext) {
        val rootNode = templateContext.rootNode ?: throw IllegalArgumentException("RootNode is null")

        // Resetting the Template Status
        templateContext.isDirty = false

        // Update the virtual node tree
        GXViewNodeTreeUpdater(templateContext).build()

        // Update view layout
        GXViewTreeUpdater(templateContext, rootNode).build()

        onReadyOrReuse(templateContext)
    }

    internal fun onDestroy(templateContext: GXTemplateContext) {
        templateContext.lifeStatus = GXTemplateContext.LIFE_ON_DESTROY
        templateContext.release()
        templateContext.visibleStatus = GXTemplateContext.LIFE_ON_NONE
        templateContext.lifeStatus = GXTemplateContext.LIFE_ON_NONE
    }

    fun onVisible(templateContext: GXTemplateContext) {
        templateContext.visibleStatus = GXTemplateContext.LIFE_ON_VISIBLE
    }

    fun onInvisible(templateContext: GXTemplateContext) {
        templateContext.visibleStatus = GXTemplateContext.LIFE_ON_INVISIBLE
    }

    private fun onCreate(templateContext: GXTemplateContext) {
        templateContext.lifeStatus = GXTemplateContext.LIFE_ON_CREATE
    }

    private fun onReadyOrReuse(templateContext: GXTemplateContext) {
        if (templateContext.lifeStatus == GXTemplateContext.LIFE_ON_READY) {
            onReuse(templateContext)
        } else {
            onReady(templateContext)
        }
    }

    private fun onReady(templateContext: GXTemplateContext) {
        templateContext.lifeStatus = GXTemplateContext.LIFE_ON_READY
    }

    private fun onReuse(templateContext: GXTemplateContext) {
        templateContext.lifeStatus = GXTemplateContext.LIFE_ON_REUSE
    }
}