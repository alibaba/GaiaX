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
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.template.GXTemplateKey
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
        gxNode.stretchNode.layoutByBind = layout
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
        gxNode.stretchNode.layoutByCreate = layout
        gxNode.children?.forEachIndexed { index, childViewData ->
            composeStretchNodeByCreateView(childViewData, layout.children[index])
        }
    }

    fun computeContainerHeightByItemTemplate(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        containerData: JSONArray
    ): Size<Dimension?>? {
        if (gxNode.childTemplateItems?.isEmpty() == true) {
            return null
        }

        // 1. 获取坑位的ViewPort信息
        val itemViewPort: Size<Float?> = computeItemViewPort(gxTemplateContext, gxNode)

        // 2. 计算坑位实际宽高结果
        val itemTemplatePair = gxNode.childTemplateItems?.firstOrNull() ?: return null
        val itemTemplateItem = itemTemplatePair.first
        val itemVisualTemplateNode = itemTemplatePair.second
        val itemSize: Layout? = computeItemSize(
            gxTemplateContext,
            gxNode,
            itemViewPort,
            itemTemplateItem,
            itemVisualTemplateNode,
            containerData
        )

        // 如果是Scroll容器，那么需要计算所有数据的高度，作为Item的高度
        // TODO: 待处理
        // 3. 计算容器期望的宽高结果
        return computeContainerSize(gxTemplateContext, gxNode, itemSize, containerData)
    }

    private fun computeItemSize(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        itemViewPort: Size<Float?>,
        gxItemTemplateItem: GXTemplateEngine.GXTemplateItem,
        gxItemVisualTemplateNode: GXTemplateNode?,
        containerData: JSONArray
    ): Layout? {
        when {
            gxNode.isScrollType() -> {
                val itemData = containerData.firstOrNull() as? JSONObject ?: return null
                val itemMeasureSize =
                    GXTemplateEngine.GXMeasureSize(itemViewPort.width, itemViewPort.height)
                val itemTemplateData: GXTemplateEngine.GXTemplateData =
                    GXTemplateEngine.GXTemplateData(itemData)
                val stretchNode = computeItemSizeByCreateAndBindNode(
                    gxTemplateContext,
                    gxItemTemplateItem,
                    itemMeasureSize,
                    itemTemplateData,
                    gxItemVisualTemplateNode
                )?.stretchNode
                return stretchNode?.layoutByBind
            }
            // 如果是Grid容器，那么计算第一个数据的高度，然后作为Item的高度
            gxNode.isGridType() -> {
                val itemData = containerData.firstOrNull() as? JSONObject ?: return null
                val itemMeasureSize =
                    GXTemplateEngine.GXMeasureSize(itemViewPort.width, itemViewPort.height)
                val itemTemplateData: GXTemplateEngine.GXTemplateData =
                    GXTemplateEngine.GXTemplateData(itemData)
                val stretchNode = computeItemSizeByCreateAndBindNode(
                    gxTemplateContext,
                    gxItemTemplateItem,
                    itemMeasureSize,
                    itemTemplateData,
                    gxItemVisualTemplateNode
                )?.stretchNode
                return stretchNode?.layoutByBind
            }
            else -> {
                return null
            }
        }
    }

    fun computeContainerItemSize(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxItemTemplateItem: GXTemplateEngine.GXTemplateItem,
        gxItemVisualTemplateNode: GXTemplateNode?,
        containerData: JSONArray
    ): Layout? {
        // 1. 获取坑位的ViewPort信息
        val itemViewPort: Size<Float?> = computeItemViewPort(gxTemplateContext, gxNode)

        // 2. 计算坑位实际宽高结果
        return computeItemSize(
            gxTemplateContext,
            gxNode,
            itemViewPort,
            gxItemTemplateItem,
            gxItemVisualTemplateNode,
            containerData
        )
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
        val finalScrollConfig = node.templateNode.finalScrollConfig
            ?: throw IllegalArgumentException("Want to computeScrollItemViewPortWidth, but finalScrollConfig is null")

        GXRegisterCenter.instance
            .extensionScroll?.convert(
                GXTemplateKey.GAIAX_CUSTOM_PROPERTY_VIEW_PORT_WIDTH,
                context,
                finalScrollConfig
            )?.let {
                return it as Float
            }

        // 这里不区分horizontal或者vertical，因为坑位的最大视口大小是可以直接确定的
        val left: Float = finalScrollConfig.edgeInsets.left.toFloat()
        val right = finalScrollConfig.edgeInsets.right.toFloat()
        context.size.width?.let {
            return it - left - right
        }
        return null
    }

    private fun computeGridItemViewPortWidth(context: GXTemplateContext, gxNode: GXNode): Float? {
        val containerWidth = gxNode.stretchNode.layoutByBind?.width
            ?: gxNode.stretchNode.layoutByCreate?.width
            ?: throw IllegalArgumentException("Want to computeGridItemViewPortWidth, but containerWith is null")
        val gridConfig = gxNode.templateNode.finalGridConfig
            ?: throw IllegalArgumentException("Want to computeGridItemViewPortWidth, but finalGridConfig is null")

        return when {
            gridConfig.isVertical -> {
                val totalItemSpacing = gridConfig.itemSpacing * (gridConfig.column(context) - 1)
                val padding = gridConfig.edgeInsets.left + gridConfig.edgeInsets.right
                (containerWidth - totalItemSpacing - padding) * 1.0F / gridConfig.column(context)
            }
            gridConfig.isHorizontal -> {
                // TODO: Grid横向处理不支持，此种情况暂时不做处理，很少见
                null
            }
            else -> {
                null
            }
        }
    }

    private fun computeItemSizeByCreateAndBindNode(
        gxTemplateContext: GXTemplateContext,
        templateItem: GXTemplateEngine.GXTemplateItem,
        measureSize: GXTemplateEngine.GXMeasureSize,
        templateData: GXTemplateEngine.GXTemplateData,
        visualTemplateNode: GXTemplateNode?
    ): GXNode? {

        // TODO 此处待优化 容器高度计算SIZE的复用粒度问题，是一次create多次bind用完丢弃，还是多次create多次bind在坑位创建时全部复用。
        val templateInfo = GXTemplateEngine.instance.data.getTemplateInfo(templateItem)
        val context = GXTemplateContext.createContext(
            templateItem,
            measureSize,
            templateInfo,
            visualTemplateNode
        )
        val rootNode = GXTemplateEngine.instance.render.createNode(context)
        context.templateData = templateData
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
                val finalScrollConfig = gxNode.templateNode.finalScrollConfig
                    ?: throw IllegalArgumentException("Want to computeContainerSize, but finalScrollConfig is null")

                // 如果是横向，那么高度就是坑位高度
                if (finalScrollConfig.isHorizontal) {
                    return Size(
                        Dimension.Points(itemSize.width),
                        Dimension.Points(itemSize.height)
                    )
                }
                // 如果是竖向，那么高度就是坑位高度*行数+总间距
                else if (finalScrollConfig.isVertical) {
                    val lines = ceil((containerTemplateData.size * 1.0F).toDouble()).toInt()
                    var containerHeight = itemSize.height
                    containerHeight *= lines
                    containerHeight += finalScrollConfig.itemSpacing * (lines - 1)
                    return Size(
                        Dimension.Points(itemSize.width),
                        Dimension.Points(containerHeight)
                    )
                }
            } else if (gxNode.isGridType()) {
                val finalGridConfig = gxNode.templateNode.finalGridConfig
                    ?: throw IllegalArgumentException("Want to computeContainerSize, but finalGridConfig is null")

                // 如果是竖向，那么高度就是坑位高度*行数+总间距
                if (finalGridConfig.isVertical) {

                    // 获取行数
                    val lines =
                        ceil((containerTemplateData.size * 1.0F / finalGridConfig.column(context)).toDouble()).toInt()

                    var containerHeight = itemSize.height

                    // 计算高度
                    containerHeight *= lines
                    containerHeight += finalGridConfig.rowSpacing * (lines - 1)

                    // 处理padding
                    val edgeInsets = finalGridConfig.edgeInsets
                    containerHeight += edgeInsets.top + edgeInsets.bottom

                    return Size(
                        Dimension.Points(itemSize.width),
                        Dimension.Points(containerHeight)
                    )
                } else if (finalGridConfig.isHorizontal) {
                    // TODO: Grid横向处理不支持，此种情况暂时不做处理，很少见
                    return null
                }
            }
        }
        return null
    }
}