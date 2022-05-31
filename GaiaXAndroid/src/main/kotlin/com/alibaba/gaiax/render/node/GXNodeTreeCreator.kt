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

import app.visly.stretch.Size
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.template.GXLayer
import com.alibaba.gaiax.template.GXTemplateInfo

/**
 * 用于创建虚拟节点树
 * @suppress
 */
object GXNodeTreeCreator {

    fun create(context: GXTemplateContext): GXNode {
        val rootNode = createNode(
            context,
            null,
            context.templateInfo.layer,
            context.visualTemplateNode,
            context.templateInfo
        )
        rootNode.isRoot = true
        GXNodeUtils.computeNodeTreeByCreateView(
            rootNode,
            Size(context.size.width, context.size.height)
        )
        return rootNode
    }

    /**
     * 创建节点树
     *
     * @param gxTemplateContext 模板上下文
     * @param parentNode 当前节点的父节点
     * @param currentLayer 当前节点的层级信息
     * @param visualTemplateNode 当前节点在父模板中的虚拟模板节点信息
     * @param currentTemplateInfo 当前节点的模板数据
     */
    private fun createNode(
        gxTemplateContext: GXTemplateContext,
        parentNode: GXNode?,
        currentLayer: GXLayer,
        visualTemplateNode: GXTemplateNode?,
        currentTemplateInfo: GXTemplateInfo
    ): GXNode {
        val node = GXNode()

        // 设置ID与ID路径
        node.setIdPath(parentNode, currentLayer)

        // 初始化详细数据
        node.templateNode =
            GXTemplateNode.createNode(currentLayer.id, currentTemplateInfo, visualTemplateNode)

        // 初始化节点数据
        node.stretchNode = GXStretchNode.createNode(
            gxTemplateContext,
            node.templateNode, node.id, node.idPath
        )

        // 建立层级关系
        if (parentNode?.children == null) {
            parentNode?.children = mutableListOf()
        }
        parentNode?.children?.add(node)

        // 建立节点的层级关系
        parentNode?.stretchNode?.node?.addChild(node.stretchNode.node)

        // 构建子层级节点
        initChildrenViewData(gxTemplateContext, node, currentLayer.layers, currentTemplateInfo)

        return node
    }

    private fun initChildrenViewData(
        context: GXTemplateContext,
        parentNode: GXNode,
        currentLayers: MutableList<GXLayer>,
        currentTemplateInfo: GXTemplateInfo
    ) {
        currentLayers.forEach { currentLayer ->
            // 嵌套子模板类型，是个虚拟节点
            if (currentLayer.isNestChildTemplateType()) {
                val childTemplateData = currentTemplateInfo.getChildTemplate(currentLayer.id)
                    ?: throw IllegalArgumentException("Child template not found, id = ${currentLayer.id}")

                // 创建一个空节点
                val childTemplateRootLayerVisualNestTemplateNode =
                    GXTemplateNode.createNode(currentLayer.id, currentTemplateInfo)

                val childTemplateRootLayer = childTemplateData.layer

                // 容器模板下的子模板
                if (parentNode.isContainerType() && childTemplateData.isTemplate()) {
                    parentNode.addChildTemplateItems(
                        GXTemplateEngine.GXTemplateItem(
                            context.context,
                            context.templateItem.bizId,
                            childTemplateRootLayer.id
                        ), childTemplateRootLayerVisualNestTemplateNode
                    )
                }
                // 普通模板嵌套的子模板根节点，可能是普通模板也可能是容器模板
                else {
                    createNode(
                        context,
                        parentNode,
                        childTemplateRootLayer,
                        childTemplateRootLayerVisualNestTemplateNode,
                        childTemplateData
                    ).apply { isNestRoot = true }
                }
            }
            // 普通子节点
            else {
                createNode(context, parentNode, currentLayer, null, currentTemplateInfo)
            }
        }
    }
}