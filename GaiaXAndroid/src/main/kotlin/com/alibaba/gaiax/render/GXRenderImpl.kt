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
import com.alibaba.gaiax.render.node.GXNodeTreePrepare
import com.alibaba.gaiax.render.node.GXNodeTreeUpdate
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.GXViewTreeCreator
import com.alibaba.gaiax.render.view.GXViewTreeUpdate
import com.alibaba.gaiax.utils.GXGlobalCache
import com.alibaba.gaiax.utils.GXLog

/**
 * @suppress
 */
class GXRenderImpl {


    fun prepareView(gxTemplateContext: GXTemplateContext) {
        val rootNode = GXNodeTreePrepare.create(gxTemplateContext)
        gxTemplateContext.rootNode = rootNode
        rootNode.stretchNode.layoutByPrepareView?.let {
            GXGlobalCache.instance.putLayoutForPrepareView(
                gxTemplateContext,
                gxTemplateContext.templateItem,
                it
            )
        }
        rootNode.release()
    }

    fun createViewOnlyNodeTree(gxTemplateContext: GXTemplateContext): GXNode {
        val rootLayout =
            GXGlobalCache.instance.getLayoutForPrepareView(
                gxTemplateContext,
                gxTemplateContext.templateItem
            )
                ?: throw IllegalArgumentException("root layout is null")

        if (GXLog.isLog()) {
            GXLog.e(
                gxTemplateContext.tag,
                "traceId=${gxTemplateContext.traceId} tag=createViewOnlyNodeTree rootLayout=${rootLayout}"
            )
        }

        // Create a virtual node tree
        val rootNode = GXNodeTreeCreator.create(gxTemplateContext, rootLayout)
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

    fun bindViewDataOnlyNodeTree(gxTemplateContext: GXTemplateContext) {
        if (GXLog.isLog()) {
            GXLog.e(
                gxTemplateContext.tag,
                "traceId=${gxTemplateContext.traceId} tag=bindViewDataOnlyNodeTree"
            )
        }

        // Resetting the Template Status
        gxTemplateContext.isDirty = false

        // Update the node tree
        GXNodeTreeUpdate.buildNodeLayout(gxTemplateContext)
    }

    fun bindViewDataOnlyViewTree(gxTemplateContext: GXTemplateContext) {
        if (GXLog.isLog()) {
            GXLog.e(
                gxTemplateContext.tag,
                "traceId=${gxTemplateContext.traceId} tag=bindViewDataOnlyViewTree"
            )
        }

        val rootNode = gxTemplateContext.rootNode
            ?: throw IllegalArgumentException("RootNode is null(bindViewDataOnlyViewTree) gxTemplateContext = $gxTemplateContext")

        // Update view layout
        GXViewTreeUpdate(gxTemplateContext, rootNode).build()

        // Update the view tree
        GXNodeTreeUpdate.buildViewStyleAndData(gxTemplateContext)
    }

    fun resetViewDataOnlyViewTree(gxTemplateContext: GXTemplateContext) {
        if (GXLog.isLog()) {
            GXLog.e(
                gxTemplateContext.tag,
                "traceId=${gxTemplateContext.traceId} tag=resetViewDataOnlyViewTree"
            )
        }

        GXNodeTreeUpdate.resetView(gxTemplateContext)
    }

}