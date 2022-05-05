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

import app.visly.stretch.Dimension
import app.visly.stretch.Layout
import app.visly.stretch.Size
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import kotlin.math.ceil

/**
 * @suppress
 */
object GXNodeUtils {

    fun computeNodeTreeByBindData(gxNode: GXNode, size: Size<Float?>) {
        val layout = gxNode.stretchNode.node.computeLayout(size)
        composeStretchNodeByBindData(gxNode, layout)
    }

    private fun composeStretchNodeByBindData(gxNode: GXNode, layout: Layout) {
        layout.id = gxNode.stretchNode.node.id
        layout.idPath = gxNode.stretchNode.node.idPath
        gxNode.stretchNode.finalLayout = layout
        gxNode.children?.forEachIndexed { index, childViewData ->
            composeStretchNodeByBindData(childViewData, layout.children[index])
        }
    }

    fun computeNodeTreeByCreateView(gxNode: GXNode, size: Size<Float?>) {
        val layout = gxNode.stretchNode.node.computeLayout(size)
        composeStretchNodeByCreateView(gxNode, layout)
    }

    private fun composeStretchNodeByCreateView(gxNode: GXNode, layout: Layout) {
        layout.id = gxNode.stretchNode.node.id
        layout.idPath = gxNode.stretchNode.node.idPath
        gxNode.stretchNode.layout = layout
        gxNode.children?.forEachIndexed { index, childViewData ->
            composeStretchNodeByCreateView(childViewData, layout.children[index])
        }
    }

    fun computeContainerHeightByItemTemplate(
        context: GXTemplateContext,
        gxNode: GXNode,
        templateData: JSONArray
    ): Size<Dimension?>? {
        if (gxNode.childTemplateItems?.isEmpty() == true) {
            return null
        }

        // 1. 获取坑位的ViewPort信息
        val itemViewPort: Size<Float?> = computeItemViewPort(context, gxNode)

        // 2. 计算坑位实际宽高结果
        val itemSize: Layout? = computeItemSize(context, gxNode, itemViewPort, templateData)

        // 3. 计算容器期望的宽高结果
        return computeContainerSize(context, gxNode, itemSize, templateData)
    }

    fun computeItemViewPort(context: GXTemplateContext, gxNode: GXNode): Size<Float?> {

        // 对于坑位的视口宽高，需要分为Scroll容器和Grid容器

        // 1. 对于Scroll容器，其坑位的宽高由坑位自身确定，可以直接使用未计算过的视口宽高
        // 其坑位的高度，可以不计算
        if (gxNode.isScrollType()) {
            val width = computeScrollItemViewPortWidth(context, gxNode)
            return Size(width, null)
        }
        // 2. 对于Grid容器，其坑位的宽度是由GridConfig和Grid容器自己的宽度计算后决定的
        // 其坑位的高度，可以不计算
        else if (gxNode.isGridType()) {
            val width: Float? = computeGridItemViewPortWidth(context, gxNode)
            return Size(width, null)
        }

        return Size(null, null)
    }

    private fun computeScrollItemViewPortWidth(context: GXTemplateContext, node: GXNode): Float? {
        // 这里不区分horizontal或者vertical，因为坑位的最大视口大小是可以直接确定的
        val left: Float = node.templateNode.finalScrollConfig?.edgeInsets?.left?.toFloat() ?: 0F
        val right = node.templateNode.finalScrollConfig?.edgeInsets?.right?.toFloat() ?: 0F
        context.size.width?.let {
            return it - left - right
        }
        return null
    }

    private fun computeGridItemViewPortWidth(context: GXTemplateContext, gxNode: GXNode): Float? {
        val containerWidth = gxNode.stretchNode.finalLayout?.width
        val gridConfig = gxNode.templateNode.finalGridConfig
        return if (containerWidth != null) {
            when {
                gridConfig?.isVertical == true -> {
                    val totalItemSpacing = gridConfig.itemSpacing * (gridConfig.column - 1)
                    val padding = gridConfig.edgeInsets.left + gridConfig.edgeInsets.right
                    (containerWidth - totalItemSpacing - padding) * 1.0F / gridConfig.column
                }
                gridConfig?.isHorizontal == true -> {
                    // TODO: Grid横向处理不支持，此种情况暂时不做处理，很少见
                    null
                }
                else -> {
                    null
                }
            }
        } else {
            null
        }
    }

    private fun computeItemSize(
        context: GXTemplateContext,
        gxNode: GXNode,
        itemViewPort: Size<Float?>,
        containerTemplateData: JSONArray
    ): Layout? {
        when {
            // 如果是Scroll容器，那么需要计算所有数据的高度，作为Item的高度
            // TODO: 待处理
            gxNode.isScrollType() -> {
                val itemTemplatePair = gxNode.childTemplateItems?.first() ?: return null
                val itemData = containerTemplateData.first() as? JSONObject ?: return null
                val itemTemplateItem = itemTemplatePair.first
                val itemTemplateNode = itemTemplatePair.second
                val itemMeasureSize =
                    GXTemplateEngine.GXMeasureSize(itemViewPort.width, itemViewPort.height)
                val itemTemplateData: GXTemplateEngine.GXTemplateData =
                    GXTemplateEngine.GXTemplateData(itemData)
                return computeItemSizeByCreateAndBindNode(
                    itemTemplateItem,
                    itemTemplateNode,
                    itemMeasureSize,
                    itemTemplateData
                )?.stretchNode?.finalLayout
            }
            // 如果是Grid容器，那么计算第一个数据的高度，然后作为Item的高度
            gxNode.isGridType() -> {
                val itemTemplatePair = gxNode.childTemplateItems?.first() ?: return null
                val itemData = containerTemplateData.first() as? JSONObject ?: return null
                val itemTemplateItem = itemTemplatePair.first
                val itemTemplateNode = itemTemplatePair.second
                val itemMeasureSize =
                    GXTemplateEngine.GXMeasureSize(itemViewPort.width, itemViewPort.height)
                val itemTemplateData: GXTemplateEngine.GXTemplateData =
                    GXTemplateEngine.GXTemplateData(itemData)
                return computeItemSizeByCreateAndBindNode(
                    itemTemplateItem,
                    itemTemplateNode,
                    itemMeasureSize,
                    itemTemplateData
                )?.stretchNode?.finalLayout
            }
            else -> {
                return null
            }
        }
    }

    private fun computeItemSizeByCreateAndBindNode(
        templateItem: GXTemplateEngine.GXTemplateItem,
        templateNode: GXTemplateNode,
        measureSize: GXTemplateEngine.GXMeasureSize,
        itemTemplateData: GXTemplateEngine.GXTemplateData
    ): GXNode? {
        // TODO 此处待优化 容器高度计算SIZE的复用粒度问题，是一次create多次bind用完丢弃，还是多次create多次bind在坑位创建时全部复用。
        val templateData = GXTemplateEngine.instance.data.getTemplateInfo(templateItem)
        val context =
            GXTemplateContext.createContext(templateItem, measureSize, templateData, templateNode)
        val rootNode = GXTemplateEngine.instance.render.createNode(context)
        context.updateContext(itemTemplateData)
        GXTemplateEngine.instance.render.bindNodeData(context)
        return rootNode
    }


    private fun computeContainerSize(
        context: GXTemplateContext,
        gxNode: GXNode,
        itemSize: Layout?,
        containerTemplateData: JSONArray
    ): Size<Dimension?>? {
        if (itemSize != null) {
            // 容器的尺寸计算需要氛围Scroll和Grid
            if (gxNode.isScrollType()) {
                gxNode.templateNode.finalScrollConfig?.let { gxScrollConfig ->

                    // 如果是横向，那么高度就是坑位高度
                    if (gxScrollConfig.isHorizontal) {
                        return Size(null, Dimension.Points(itemSize.height))
                    }
                    // 如果是竖向，那么高度就是坑位高度*行数+总间距
                    else if (gxScrollConfig.isVertical) {
                        val lines = ceil((containerTemplateData.size * 1.0F).toDouble()).toInt()
                        var containerHeight = itemSize.height
                        containerHeight *= lines
                        containerHeight += gxScrollConfig.itemSpacing * (lines - 1)
                        return Size(null, Dimension.Points(containerHeight))
                    }
                }
            } else if (gxNode.isGridType()) {
                gxNode.templateNode.finalGridConfig?.let { gxGridConfig ->

                    // 如果是竖向，那么高度就是坑位高度*行数+总间距
                    if (gxGridConfig.isVertical) {

                        // 获取行数
                        val lines =
                            ceil((containerTemplateData.size * 1.0F / gxGridConfig.column).toDouble()).toInt()

                        var containerHeight = itemSize.height

                        // 计算高度
                        containerHeight *= lines
                        containerHeight += gxGridConfig.rowSpacing * (lines - 1)

                        // 处理padding
                        val edgeInsets = gxGridConfig.edgeInsets
                        containerHeight += edgeInsets.top + edgeInsets.bottom

                        return Size(null, Dimension.Points(containerHeight))
                    } else if (gxGridConfig.isHorizontal) {
                        // TODO: Grid横向处理不支持，此种情况暂时不做处理，很少见
                        return null
                    }
                }
            }
        }
        return null
    }
}