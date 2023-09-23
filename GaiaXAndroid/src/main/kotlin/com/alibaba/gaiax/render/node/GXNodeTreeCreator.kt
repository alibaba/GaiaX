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

package com.alibaba.gaiax.render.node

import app.visly.stretch.Layout
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.template.GXLayer
import com.alibaba.gaiax.template.GXTemplateInfo
import com.alibaba.gaiax.utils.GXLog

/**
 * 用于创建虚拟节点树
 * @suppress
 */
object GXNodeTreeCreator {

    fun create(gxTemplateContext: GXTemplateContext, rootLayout: Layout): GXNode {
        val rootNode = createNode(
            gxTemplateContext,
            null,
            gxTemplateContext.templateInfo.layer,
            gxTemplateContext.visualTemplateNode,
            gxTemplateContext.templateInfo
        )
        rootNode.isRoot = true
        GXNodeUtils.composeGXNodeByCreateView(rootNode, rootLayout)
        return rootNode
    }

    /**
     * 创建节点树
     *
     * @param gxTemplateContext 模板上下文
     * @param gxParentNode 当前节点的父节点
     * @param gxLayer 当前节点的层级信息
     * @param gxVisualTemplateNode 当前节点在父模板中的虚拟模板节点信息
     * @param gxTemplateInfo 当前节点的模板数据
     */
    private fun createNode(
        gxTemplateContext: GXTemplateContext,
        gxParentNode: GXNode?,
        gxLayer: GXLayer,
        gxVisualTemplateNode: GXTemplateNode?,
        gxTemplateInfo: GXTemplateInfo
    ): GXNode {

        // 创建新节点
        val gxNode = GXNode()

        gxNode.id = gxLayer.id

        gxNode.parentNode = gxParentNode

        // 初始化详细数据
        gxNode.templateNode = GXTemplateNode.createNode(
            gxLayer.id, gxTemplateInfo, gxVisualTemplateNode
        )

        // 初始化节点数据
        gxNode.stretchNode = GXStretchNode.createEmptyNode(
            gxTemplateContext, gxNode.templateNode, gxNode.id
        )

        // 构建子层级节点
        gxLayer.layers.forEach { currentLayer ->
            // 嵌套子模板类型，是个虚拟节点
            if (currentLayer.isNestChildTemplateType()) {
                val gxChildTemplateInfo = gxTemplateInfo.getChildTemplateInfo(currentLayer.id)
                    ?: throw IllegalArgumentException("Child template not found, id = ${currentLayer.id}")

                // 创建一个空节点
                val gxChildVisualTemplateNode = GXTemplateNode.createNode(
                    currentLayer.id, gxTemplateInfo
                )

                val gxChildLayer = gxChildTemplateInfo.layer

                // 容器模板下的子模板
                if (gxNode.isContainerType() && gxChildTemplateInfo.isTemplate()) {
                    gxNode.addChildTemplateItems(
                        GXTemplateEngine.GXTemplateItem(
                            gxTemplateContext.context,
                            gxTemplateContext.templateItem.bizId,
                            gxChildLayer.id
                        ), gxChildVisualTemplateNode
                    )
                }
                // 普通模板嵌套的子模板根节点，可能是普通模板也可能是容器模板
                else {
                    val gxChildNode = createNode(
                        gxTemplateContext,
                        gxNode,
                        gxChildLayer,
                        gxChildVisualTemplateNode,
                        gxChildTemplateInfo
                    ).apply { isNestRoot = true }

                    // 建立层级关系
                    if (gxNode.children == null) {
                        gxNode.children = mutableListOf()
                    }
                    gxNode.children?.add(gxChildNode)

                }
            }
            // 普通子节点
            else {
                val gxChildNode = createNode(
                    gxTemplateContext, gxNode, currentLayer, null, gxTemplateInfo
                )

                // 建立层级关系
                if (gxNode.children == null) {
                    gxNode.children = mutableListOf()
                }
                gxNode.children?.add(gxChildNode)

            }
        }

        return gxNode
    }
}