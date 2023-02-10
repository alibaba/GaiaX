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
import com.alibaba.gaiax.context.*
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.GXGlobalCache
import com.alibaba.gaiax.utils.getStringExt
import kotlin.math.ceil
import kotlin.math.max

/**
 * @suppress
 */
@Suppress("UNUSED_PARAMETER")
object GXNodeUtils {

    private const val ITEM_PATH =
        "${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE}.${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE_PATH}"
    private const val ITEM_CONFIG =
        "${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE}.${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE_CONFIG}"

    fun computeNodeTreeByBindData(gxNode: GXNode, size: Size<Float?>) {
        val stretchNode = gxNode.stretchNode.node
            ?: throw IllegalArgumentException("stretch node is null, please check!")
        val layout = stretchNode.computeLayout(size)
        composeStretchNodeByBindData(gxNode, layout)
    }

    private fun composeStretchNodeByBindData(gxNode: GXNode, layout: Layout) {
        val stretchNode = gxNode.stretchNode.node
            ?: throw IllegalArgumentException("stretch node is null, please check!")
        layout.id = stretchNode.id
        gxNode.stretchNode.layoutByBind = layout
        gxNode.children?.forEachIndexed { index, childViewData ->
            composeStretchNodeByBindData(childViewData, layout.children[index])
        }
    }

    fun computeNodeTreeByPrepareView(gxNode: GXNode, size: Size<Float?>) {
        val stretchNode = gxNode.stretchNode.node
            ?: throw IllegalArgumentException("stretch node is null, please check!")
        val layout = stretchNode.computeLayout(size)
        composeStretchNodeByPrepareView(gxNode, layout)
    }

    private fun composeStretchNodeByPrepareView(gxNode: GXNode, layout: Layout) {
        val stretchNode = gxNode.stretchNode.node
            ?: throw IllegalArgumentException("stretch node is null, please check!")
        layout.id = stretchNode.id
        gxNode.stretchNode.layoutByCreate = layout
        gxNode.children?.forEachIndexed { index, childViewData ->
            composeStretchNodeByPrepareView(childViewData, layout.children[index])
        }
    }

    fun composeGXNodeByCreateView(gxNode: GXNode, layout: Layout) {
        gxNode.layoutByPrepare = layout
        gxNode.children?.forEachIndexed { index, childViewData ->
            composeGXNodeByCreateView(childViewData, layout.children[index])
        }
    }

    fun computeScrollSize(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode, gxContainerData: JSONArray
    ): Size<Dimension?>? {

        val templateItems = gxNode.childTemplateItems ?: return null

        if (templateItems.isEmpty()) {
            return null
        }

        // 容器高度的预计算
        // 1. 普通坑位的宽度和高度计算
        // 2. 多坑位的宽度和高度计算
        //  2.1 显示一个坑位
        //  2.2 显示两个坑位

        // 1. 获取坑位的ViewPort信息
        val itemViewPort: Size<Float?> = computeItemViewPort(gxTemplateContext, gxNode)

        // case 1
        if (templateItems.size == 1) {

            val itemTemplatePair = templateItems.firstOrNull() ?: return null
            val itemTemplateItem = itemTemplatePair.first
            val itemVisualTemplateNode = itemTemplatePair.second

            // 2. 计算坑位实际宽高结果
            gxContainerData.forEach {
                val itemData = it as JSONObject
                val itemHashCode = itemData.hashCode()

                computeItemLayoutToCache(
                    gxTemplateContext,
                    gxNode,
                    itemData,
                    itemViewPort,
                    itemTemplateItem,
                    itemVisualTemplateNode,
                    itemHashCode
                )
            }

            // 3. 计算容器期望的宽高结果
            val itemLayout = gxTemplateContext.getMaxHeightLayoutOfLayoutCache()
            return computeContainerSize(gxTemplateContext, gxNode, itemLayout, gxContainerData)
        }
        // case 2
        else {
            // init multi type item
            gxContainerData.forEach {
                val gxItemData = it as JSONObject
                val itemHashCode = gxItemData.hashCode()

                computeItemLayoutForMultiItemType(
                    gxTemplateContext, gxNode, templateItems, gxItemData, itemViewPort, itemHashCode
                )
            }

            // 3. 计算容器期望的宽高结果
            val itemLayout = gxTemplateContext.getMaxHeightLayoutOfLayoutCache()
            return computeContainerSize(gxTemplateContext, gxNode, itemLayout, gxContainerData)
        }
    }

    fun computeItemContainerSize(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode, gxItemData: JSONObject
    ): Layout? {

        val templateItems = gxNode.childTemplateItems ?: return null

        if (templateItems.isEmpty()) {
            return null
        }

        // 容器高度的预计算
        // 1. 普通坑位的宽度和高度计算
        // 2. 多坑位的宽度和高度计算
        //  2.1 显示一个坑位
        //  2.2 显示两个坑位

        // 1. 获取坑位的ViewPort信息
        val itemViewPort: Size<Float?> = computeItemViewPort(gxTemplateContext, gxNode)

        // case 1
        if (templateItems.size == 1) {

            // 2. 计算坑位实际宽高结果
            val itemTemplatePair = templateItems.firstOrNull() ?: return null
            val itemTemplateItem = itemTemplatePair.first
            val itemVisualTemplateNode = itemTemplatePair.second

            val itemHashCode = gxItemData.hashCode()

            computeItemLayoutToCache(
                gxTemplateContext,
                gxNode,
                gxItemData,
                itemViewPort,
                itemTemplateItem,
                itemVisualTemplateNode,
                itemHashCode
            )

            return gxTemplateContext.getLayoutCache(itemHashCode)
        }
        // case 2
        else {
            // init multi type item
            val itemHashCode = gxItemData.hashCode()

            computeItemLayoutForMultiItemType(
                gxTemplateContext, gxNode, templateItems, gxItemData, itemViewPort, itemHashCode
            )

            return gxTemplateContext.getLayoutCache(itemHashCode)
        }
    }

    private fun computeItemLayoutForMultiItemType(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        templateItems: MutableList<Pair<GXTemplateEngine.GXTemplateItem, GXTemplateNode>>,
        itemData: JSONObject,
        itemViewPort: Size<Float?>,
        itemHashCode: Int
    ) {
        gxNode.templateNode.resetDataCache()
        val typeData = gxNode.templateNode.getExtend(itemData)
        if (typeData != null) {
            val itemConfig = "${ITEM_CONFIG}.${typeData.getStringExt(ITEM_PATH)}"
            val templateId = typeData.getStringExt(itemConfig)
            templateItems.firstOrNull { it.first.templateId == templateId }
                ?.let { itemTemplatePair ->

                    // 2. 计算坑位实际宽高结果
                    val itemTemplateItem = itemTemplatePair.first
                    val itemVisualTemplateNode = itemTemplatePair.second

                    computeItemLayoutToCache(
                        gxTemplateContext,
                        gxNode,
                        itemData,
                        itemViewPort,
                        itemTemplateItem,
                        itemVisualTemplateNode,
                        itemHashCode
                    )
                }
        }
    }

    private fun computeItemLayoutToCache(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxItemData: JSONObject,
        itemViewPort: Size<Float?>,
        itemTemplateItem: GXTemplateEngine.GXTemplateItem,
        itemVisualTemplateNode: GXTemplateNode,
        itemHashCode: Int
    ) {
        gxTemplateContext.initLayoutCache()

        if (!gxTemplateContext.isExistOfLayoutCache(itemHashCode)) {
            computeItemLayout(
                gxTemplateContext,
                gxNode,
                itemViewPort,
                itemTemplateItem,
                itemVisualTemplateNode,
                gxItemData
            )?.let { itemLayout ->
                gxTemplateContext.putLayoutCache(itemHashCode, itemLayout)
            }
        }
    }

    fun computeGridAndSliderSize(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode, gxContainerData: JSONArray
    ): Size<Dimension?>? {

        val templateItems = gxNode.childTemplateItems ?: return null

        if (templateItems.isEmpty()) {
            return null
        }

        val itemTemplatePair = templateItems.firstOrNull() ?: return null
        val itemData = gxContainerData.firstOrNull() as? JSONObject ?: JSONObject()

        val itemViewPort: Size<Float?> = computeItemViewPort(gxTemplateContext, gxNode)
        val itemTemplateItem = itemTemplatePair.first
        val itemVisualTemplateNode = itemTemplatePair.second

        val itemLayout = if (GXGlobalCache.instance.layoutCache.containsKey(itemTemplateItem)) {
            GXGlobalCache.instance.layoutCache[itemTemplateItem]
        } else {
            computeItemLayout(
                gxTemplateContext,
                gxNode,
                itemViewPort,
                itemTemplateItem,
                itemVisualTemplateNode,
                itemData
            )?.also {
                GXGlobalCache.instance.layoutCache[itemTemplateItem] = it
            }
        }
        return computeContainerSize(gxTemplateContext, gxNode, itemLayout, gxContainerData)
    }

    fun computeFooterItemContainerSize(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        itemViewPort: Size<Float?>,
        gxItemTemplateItem: GXTemplateEngine.GXTemplateItem,
        gxItemVisualTemplateNode: GXTemplateNode?,
        itemData: JSONObject
    ): Layout? {
        when {
            gxNode.isScrollType() -> {
                val itemMeasureSize =
                    GXTemplateEngine.GXMeasureSize(itemViewPort.width, itemViewPort.height)
                val itemTemplateData: GXTemplateEngine.GXTemplateData =
                    GXTemplateEngine.GXTemplateData(itemData)
                return computeItemLayoutByCreateAndBindNode(
                    gxTemplateContext,
                    gxItemTemplateItem,
                    itemMeasureSize,
                    itemTemplateData,
                    gxItemVisualTemplateNode
                )
            }
            // 如果是Grid容器，那么计算第一个数据的高度，然后作为Item的高度
            gxNode.isGridType() -> {
                val itemMeasureSize =
                    GXTemplateEngine.GXMeasureSize(itemViewPort.width, itemViewPort.height)
                val itemTemplateData: GXTemplateEngine.GXTemplateData =
                    GXTemplateEngine.GXTemplateData(itemData)
                return computeItemLayoutByCreateAndBindNode(
                    gxTemplateContext,
                    gxItemTemplateItem,
                    itemMeasureSize,
                    itemTemplateData,
                    gxItemVisualTemplateNode
                )
            }
            else -> {
                return null
            }
        }
    }

    private fun computeItemLayout(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxItemViewPort: Size<Float?>,
        gxItemTemplateItem: GXTemplateEngine.GXTemplateItem,
        gxItemVisualTemplateNode: GXTemplateNode?,
        gxItemData: JSONObject
    ): Layout? {
        when {
            gxNode.isScrollType() -> {
                val gxMeasureSize = GXTemplateEngine.GXMeasureSize(
                    gxItemViewPort.width, gxItemViewPort.height
                )
                val gxTemplateData = GXTemplateEngine.GXTemplateData(gxItemData)
                return computeItemLayoutByCreateAndBindNode(
                    gxTemplateContext,
                    gxItemTemplateItem,
                    gxMeasureSize,
                    gxTemplateData,
                    gxItemVisualTemplateNode
                )
            }
            // 如果是Grid容器，那么计算第一个数据的高度，然后作为Item的高度
            gxNode.isGridType() -> {
                val gxMeasureSize =
                    GXTemplateEngine.GXMeasureSize(gxItemViewPort.width, gxItemViewPort.height)
                val gxTemplateData: GXTemplateEngine.GXTemplateData =
                    GXTemplateEngine.GXTemplateData(gxItemData)
                return computeItemLayoutByCreateAndBindNode(
                    gxTemplateContext,
                    gxItemTemplateItem,
                    gxMeasureSize,
                    gxTemplateData,
                    gxItemVisualTemplateNode
                )
            }
            gxNode.isSliderType() -> {
                val gxMeasureSize = GXTemplateEngine.GXMeasureSize(
                    gxItemViewPort.width, gxItemViewPort.height
                )
                val gxTemplateData = GXTemplateEngine.GXTemplateData(gxItemData)
                return computeItemLayoutByCreateAndBindNode(
                    gxTemplateContext,
                    gxItemTemplateItem,
                    gxMeasureSize,
                    gxTemplateData,
                    gxItemVisualTemplateNode
                )
            }
            else -> {
                return null
            }
        }
    }

    fun computeFooterItemViewPort(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode
    ): Size<Float?> {

        // 对于坑位的视口宽高，需要分为Scroll容器和Grid容器

        // 1. 对于Scroll容器，其坑位的宽高由坑位自身确定，可以直接使用未计算过的视口宽高
        // 其坑位的高度，可以不计算
        if (gxNode.isScrollType()) {
            val finalScrollConfig = gxNode.templateNode.layer.scrollConfig
                ?: throw IllegalArgumentException("Want to computeItemViewPort, but finalScrollConfig is null")

            GXRegisterCenter.instance.extensionScroll?.convert(
                GXTemplateKey.GAIAX_CUSTOM_PROPERTY_VIEW_PORT_WIDTH,
                gxTemplateContext,
                finalScrollConfig
            )?.let {
                return Size(it as Float, null)
            }

            val padding = gxNode.getPaddingRect()

            // 这里不区分horizontal或者vertical，因为坑位的最大视口大小是可以直接确定的
            val left: Float = padding.left.toFloat()
            val right = padding.right.toFloat()
            gxTemplateContext.size.width?.let {
                return Size(it - left - right, null)
            }
        }
        // 2. 对于Grid容器，其坑位的宽度是由GridConfig和Grid容器自己的宽度计算后决定的
        // 其坑位的高度，可以不计算
        else if (gxNode.isGridType()) {
            val containerWidth =
                gxNode.stretchNode.layoutByBind?.width ?: gxNode.layoutByPrepare?.width
                ?: throw IllegalArgumentException("Want to computeFooterItemViewPort, but containerWith is null")
            val gridConfig = gxNode.templateNode.layer.gridConfig ?: throw IllegalArgumentException(
                "Want to computeFooterItemViewPort, but finalGridConfig is null"
            )

            val padding = gxNode.getPaddingRect()

            return when {
                gridConfig.isVertical -> {
                    val padding = padding.left + padding.right
                    Size(containerWidth - padding, null)
                }
                gridConfig.isHorizontal -> {
                    // TODO: Grid横向处理不支持，此种情况暂时不做处理，很少见
                    Size(null, null)
                }
                else -> {
                    Size(null, null)
                }
            }
        }

        return Size(null, null)
    }

    fun computeItemViewPort(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode
    ): Size<Float?> {

        // 对于坑位的视口宽高，需要分为Scroll容器和Grid容器

        // 1. 对于Scroll容器，其坑位的宽高由坑位自身确定，可以直接使用未计算过的视口宽高
        // 其坑位的高度，可以不计算
        if (gxNode.isScrollType()) {
            val finalScrollConfig = gxNode.templateNode.layer.scrollConfig
                ?: throw IllegalArgumentException("Want to computeItemViewPort, but finalScrollConfig is null")

            GXRegisterCenter.instance.extensionScroll?.convert(
                GXTemplateKey.GAIAX_CUSTOM_PROPERTY_VIEW_PORT_WIDTH,
                gxTemplateContext,
                finalScrollConfig
            )?.let {
                return Size(it as Float, null)
            }

            val padding = gxNode.getPaddingRect()

            // 这里不区分horizontal或者vertical，因为坑位的最大视口大小是可以直接确定的
            val left: Float = padding.left.toFloat()
            val right = padding.right.toFloat()
            gxTemplateContext.size.width?.let {
                return Size(it - left - right, null)
            }
        }
        // 2. 对于Grid容器，其坑位的宽度是由GridConfig和Grid容器自己的宽度计算后决定的
        // 其坑位的高度，可以不计算
        else if (gxNode.isGridType()) {
            val containerWidth =
                gxNode.stretchNode.layoutByBind?.width ?: gxNode.layoutByPrepare?.width
                ?: throw IllegalArgumentException("Want to computeItemViewPort, but containerWith is null")
            val gridConfig = gxNode.templateNode.layer.gridConfig ?: throw IllegalArgumentException(
                "Want to computeItemViewPort, but finalGridConfig is null"
            )
            return when {
                gridConfig.isVertical -> {
                    val totalItemSpacing =
                        gridConfig.itemSpacing * (gridConfig.column(gxTemplateContext) - 1)

                    val paddingRect = gxNode.getPaddingRect()

                    val padding = paddingRect.left + paddingRect.right
                    val finalWidth =
                        (containerWidth - totalItemSpacing - padding) * 1.0F / gridConfig.column(
                            gxTemplateContext
                        )
                    Size(finalWidth, null)
                }
                gridConfig.isHorizontal -> {
                    // TODO: Grid横向处理不支持，此种情况暂时不做处理，很少见
                    Size(null, null)
                }
                else -> {
                    Size(null, null)
                }
            }
        } else if (gxNode.isSliderType()) {
            when (val nodeWith = gxNode.templateNode.css.flexBox.sizeForDimension?.width) {
                is Dimension.Points -> {
                    gxTemplateContext.size.width?.let {
                        return Size(it * nodeWith.value, null)
                    }
                }
                else -> {
                    gxTemplateContext.size.width?.let {
                        return Size(it, null)
                    }
                }
            }
        }
        return Size(null, null)
    }

    private fun computeItemLayoutByCreateAndBindNode(
        gxTemplateContext: GXTemplateContext,
        gxTemplateItem: GXTemplateEngine.GXTemplateItem,
        gxMeasureSize: GXTemplateEngine.GXMeasureSize,
        gxTemplateData: GXTemplateEngine.GXTemplateData,
        gxVisualTemplateNode: GXTemplateNode?
    ): Layout? {

        val gxItemTemplateInfo = GXTemplateEngine.instance.data.getTemplateInfo(gxTemplateItem)

        val gxItemTemplateContext = GXTemplateContext.createContext(
            gxTemplateItem, gxMeasureSize, gxItemTemplateInfo, gxVisualTemplateNode
        )

        if (!GXGlobalCache.instance.layoutFPV.containsKey(gxTemplateItem)) {
            GXTemplateEngine.instance.render.prepareLayoutTree(gxItemTemplateContext)
        }

        gxItemTemplateContext.templateData = gxTemplateData

        val gxItemRootNode = GXTemplateEngine.instance.render.createNode(gxItemTemplateContext)

        GXTemplateEngine.instance.render.bindNodeData(gxItemTemplateContext)

        return gxItemRootNode.stretchNode.layoutByBind
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
                val finalScrollConfig = gxNode.templateNode.layer.scrollConfig
                    ?: throw IllegalArgumentException("Want to computeContainerHeight, but finalScrollConfig is null")

                // 如果是横向，那么高度就是坑位高度
                if (finalScrollConfig.isHorizontal) {
                    return Size(
                        Dimension.Points(itemSize.width), Dimension.Points(itemSize.height)
                    )
                }
                // 如果是竖向，那么高度就是坑位高度*行数+总间距
                else if (finalScrollConfig.isVertical) {
                    val lines = max(1, ceil((containerTemplateData.size * 1.0F).toDouble()).toInt())
                    var containerHeight = itemSize.height
                    containerHeight *= lines
                    containerHeight += finalScrollConfig.itemSpacing * (lines - 1)
                    return Size(
                        Dimension.Points(itemSize.width), Dimension.Points(containerHeight)
                    )
                }
            } else if (gxNode.isGridType()) {
                val finalGridConfig = gxNode.templateNode.layer.gridConfig
                    ?: throw IllegalArgumentException("Want to computeContainerHeight, but finalGridConfig is null")

                // 如果是竖向，那么高度就是坑位高度*行数+总间距
                if (finalGridConfig.isVertical) {

                    // 获取行数
                    val lines = max(
                        1,
                        ceil((containerTemplateData.size * 1.0F / finalGridConfig.column(context)).toDouble()).toInt()
                    )

                    var containerHeight = itemSize.height

                    // 计算高度
                    containerHeight *= lines
                    containerHeight += finalGridConfig.rowSpacing * (lines - 1)

                    // 处理padding
                    val padding = gxNode.getPaddingRect()
                    containerHeight += padding.top + padding.bottom

                    val containerWidth = itemSize.width - padding.left - padding.right

                    return Size(
                        Dimension.Points(containerWidth), Dimension.Points(containerHeight)
                    )
                } else if (finalGridConfig.isHorizontal) {
                    // TODO: Grid横向处理不支持，此种情况暂时不做处理，很少见
                    return null
                }
            } else if (gxNode.isSliderType()) {
                return Size(
                    Dimension.Points(itemSize.width), Dimension.Points(itemSize.height)
                )
            }
        }
        return null
    }
}
