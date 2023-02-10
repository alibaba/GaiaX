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
object GXNodeUtils {

    internal const val ITEM_PATH =
        "${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE}.${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE_PATH}"
    internal const val ITEM_CONFIG =
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
        val itemViewPort: Size<Float?> = computeScrollItemViewPort(gxTemplateContext, gxNode)

        // case 1
        if (templateItems.size == 1) {

            val itemTemplatePair = templateItems.firstOrNull() ?: return null
            val itemTemplateItem = itemTemplatePair.first
            val itemVisualTemplateNode = itemTemplatePair.second

            // 2. 计算坑位实际宽高结果
            gxContainerData.forEachIndexed { itemPosition, value ->
                val itemData = value as JSONObject
                val itemCacheKey = "${itemPosition}-${itemData.hashCode()}"

                computeScrollItemLayoutToCache(
                    gxTemplateContext,
                    itemData,
                    itemViewPort,
                    itemTemplateItem,
                    itemVisualTemplateNode,
                    itemCacheKey
                )
            }

            // 3. 计算容器期望的宽高结果
            val itemLayout = gxTemplateContext.getMaxHeightLayoutForScroll()
            return computeScrollContainerSize(
                gxNode, itemLayout, gxContainerData
            )
        }
        // case 2
        else {
            // init multi type item
            gxContainerData.forEachIndexed { itemPosition, value ->
                val itemData = value as JSONObject
                val itemCacheKey = "${itemPosition}-${itemData.hashCode()}"

                computeScrollItemLayoutForMultiItemType(
                    gxTemplateContext, gxNode, templateItems, itemData, itemViewPort, itemCacheKey
                )
            }

            // 3. 计算容器期望的宽高结果
            val itemLayout = gxTemplateContext.getMaxHeightLayoutForScroll()
            return computeScrollContainerSize(
                gxNode, itemLayout, gxContainerData
            )
        }
    }

    fun computeScrollAndGridItemContainerSize(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        itemData: JSONObject,
        itemPosition: Int
    ): Layout? {
        return when {
            gxNode.isScrollType() -> computeScrollItemContainerSize(
                gxTemplateContext, gxNode, itemPosition, itemData
            )
            gxNode.isGridType() -> computeGridItemContainerSize(
                gxTemplateContext, gxNode, itemData, itemPosition
            )
            else -> null
        }
    }

    private fun computeGridItemContainerSize(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        itemData: JSONObject,
        itemPosition: Int
    ): Layout? {
        val templateItems = gxNode.childTemplateItems ?: return null

        if (templateItems.isEmpty()) {
            return null
        }

        val itemTemplatePair = templateItems.firstOrNull() ?: return null
        val itemCacheKey = "${itemPosition}-${itemData.hashCode()}"

        val itemViewPort: Size<Float?> = computeGridItemViewPort(gxTemplateContext, gxNode)
        val itemTemplateItem = itemTemplatePair.first
        val itemVisualTemplateNode = itemTemplatePair.second

        if (gxTemplateContext.gridItemLayoutCache == null) {
            gxTemplateContext.gridItemLayoutCache = computeGridItemLayout(
                gxTemplateContext,
                itemViewPort,
                itemTemplateItem,
                itemVisualTemplateNode,
                itemData,
                itemCacheKey
            )
        }
        return gxTemplateContext.gridItemLayoutCache
    }

    private fun computeScrollItemContainerSize(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        itemPosition: Int,
        itemData: JSONObject
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
        val itemViewPort: Size<Float?> = computeScrollAndGridItemViewPort(gxTemplateContext, gxNode)

        // case 1
        if (templateItems.size == 1) {

            // 2. 计算坑位实际宽高结果
            val itemTemplatePair = templateItems.firstOrNull() ?: return null
            val itemTemplateItem = itemTemplatePair.first
            val itemVisualTemplateNode = itemTemplatePair.second

            val itemCacheKey = "${itemPosition}-${itemData.hashCode()}"

            computeScrollItemLayoutToCache(
                gxTemplateContext,
                itemData,
                itemViewPort,
                itemTemplateItem,
                itemVisualTemplateNode,
                itemCacheKey
            )

            return gxTemplateContext.getLayoutForScroll(itemCacheKey)
        }
        // case 2
        else {
            // init multi type item
            val itemCacheKey = "${itemPosition}-${itemData.hashCode()}"

            computeScrollItemLayoutForMultiItemType(
                gxTemplateContext, gxNode, templateItems, itemData, itemViewPort, itemCacheKey
            )

            return gxTemplateContext.getLayoutForScroll(itemCacheKey)
        }
    }

    fun computeSliderItemContainerSize(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        itemViewPort: Size<Float?>,
        itemData: JSONObject,
        itemPosition: Int
    ): Layout? {

        val templateItems = gxNode.childTemplateItems ?: return null

        if (templateItems.isEmpty()) {
            return null
        }

        val itemTemplatePair = templateItems.firstOrNull() ?: return null
        val itemCacheKey = "${itemPosition}-${itemData.hashCode()}"
        val itemTemplateItem = itemTemplatePair.first
        val itemVisualTemplateNode = itemTemplatePair.second

        if (gxTemplateContext.sliderItemLayoutCache == null) {
            gxTemplateContext.sliderItemLayoutCache = computeSliderItemLayout(
                gxTemplateContext,
                itemViewPort,
                itemTemplateItem,
                itemVisualTemplateNode,
                itemData,
                itemCacheKey
            )
        }

        return gxTemplateContext.sliderItemLayoutCache
    }

    private fun computeScrollItemLayoutForMultiItemType(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        templateItems: MutableList<Pair<GXTemplateEngine.GXTemplateItem, GXTemplateNode>>,
        itemData: JSONObject,
        itemViewPort: Size<Float?>,
        itemCacheKey: String
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

                    computeScrollItemLayoutToCache(
                        gxTemplateContext,
                        itemData,
                        itemViewPort,
                        itemTemplateItem,
                        itemVisualTemplateNode,
                        itemCacheKey
                    )
                }
        }
    }

    private fun computeScrollItemLayoutToCache(
        gxTemplateContext: GXTemplateContext,
        gxItemData: JSONObject,
        itemViewPort: Size<Float?>,
        itemTemplateItem: GXTemplateEngine.GXTemplateItem,
        itemVisualTemplateNode: GXTemplateNode,
        itemCacheKey: String
    ) {
        gxTemplateContext.initLayoutForScroll()

        if (!gxTemplateContext.isExistForScroll(itemCacheKey)) {
            computeScrollItemLayout(
                gxTemplateContext,
                itemViewPort,
                itemTemplateItem,
                itemVisualTemplateNode,
                gxItemData,
                itemCacheKey
            )?.let { itemLayout ->
                gxTemplateContext.putLayoutForScroll(itemCacheKey, itemLayout)
            }
        }
    }

    fun computeGridSize(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode, gxContainerData: JSONArray
    ): Size<Dimension?>? {

        val templateItems = gxNode.childTemplateItems ?: return null

        if (templateItems.isEmpty()) {
            return null
        }

        val itemTemplatePair = templateItems.firstOrNull() ?: return null
        val itemData = gxContainerData.firstOrNull() as? JSONObject ?: JSONObject()
        val itemPosition = 0
        val itemCacheKey = "${itemPosition}-${itemData.hashCode()}"

        val itemViewPort: Size<Float?> = computeGridItemViewPort(gxTemplateContext, gxNode)
        val itemTemplateItem = itemTemplatePair.first
        val itemVisualTemplateNode = itemTemplatePair.second

        if (gxTemplateContext.gridItemLayoutCache == null) {
            gxTemplateContext.gridItemLayoutCache = computeGridItemLayout(
                gxTemplateContext,
                itemViewPort,
                itemTemplateItem,
                itemVisualTemplateNode,
                itemData,
                itemCacheKey
            )
        }

        return computeGridContainerSize(
            gxTemplateContext, gxNode, gxTemplateContext.gridItemLayoutCache, gxContainerData
        )
    }

    fun computeSliderSize(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode, gxContainerData: JSONArray
    ): Size<Dimension?>? {

        val templateItems = gxNode.childTemplateItems ?: return null

        if (templateItems.isEmpty()) {
            return null
        }

        val itemTemplatePair = templateItems.firstOrNull() ?: return null
        val itemData = gxContainerData.firstOrNull() as? JSONObject ?: JSONObject()
        val itemPosition = 0
        val itemCacheKey = "${itemPosition}-${itemData.hashCode()}"

        val itemViewPort: Size<Float?> = computeSliderItemViewPort(gxTemplateContext, gxNode)
        val itemTemplateItem = itemTemplatePair.first
        val itemVisualTemplateNode = itemTemplatePair.second

        if (gxTemplateContext.sliderItemLayoutCache == null) {
            gxTemplateContext.sliderItemLayoutCache = computeSliderItemLayout(
                gxTemplateContext,
                itemViewPort,
                itemTemplateItem,
                itemVisualTemplateNode,
                itemData,
                itemCacheKey
            )
        }

        return computeSliderContainerSize(
            gxTemplateContext.sliderItemLayoutCache
        )
    }

    fun computeScrollAndGridFooterItemContainerSize(
        gxTemplateContext: GXTemplateContext,
        itemViewPort: Size<Float?>,
        gxItemTemplateItem: GXTemplateEngine.GXTemplateItem,
        gxItemVisualTemplateNode: GXTemplateNode?,
        itemData: JSONObject,
        itemPosition: Int
    ): Layout? {
        val itemCacheKey = "${itemPosition}-${itemData.hashCode()}"
        val itemMeasureSize =
            GXTemplateEngine.GXMeasureSize(itemViewPort.width, itemViewPort.height)
        val itemTemplateData = GXTemplateEngine.GXTemplateData(itemData)
        return computeItemLayoutByCreateAndBindNode(
            gxTemplateContext,
            gxItemTemplateItem,
            itemMeasureSize,
            itemTemplateData,
            gxItemVisualTemplateNode,
            itemCacheKey
        )
    }

    private fun computeSliderItemLayout(
        gxTemplateContext: GXTemplateContext,
        gxItemViewPort: Size<Float?>,
        gxItemTemplateItem: GXTemplateEngine.GXTemplateItem,
        gxItemVisualTemplateNode: GXTemplateNode?,
        gxItemData: JSONObject,
        itemCacheKey: String
    ): Layout? {
        val gxMeasureSize =
            GXTemplateEngine.GXMeasureSize(gxItemViewPort.width, gxItemViewPort.height)
        val gxTemplateData = GXTemplateEngine.GXTemplateData(gxItemData)
        return computeItemLayoutByCreateAndBindNode(
            gxTemplateContext,
            gxItemTemplateItem,
            gxMeasureSize,
            gxTemplateData,
            gxItemVisualTemplateNode,
            itemCacheKey
        )
    }

    private fun computeScrollItemLayout(
        gxTemplateContext: GXTemplateContext,
        gxItemViewPort: Size<Float?>,
        gxItemTemplateItem: GXTemplateEngine.GXTemplateItem,
        gxItemVisualTemplateNode: GXTemplateNode?,
        gxItemData: JSONObject,
        itemCacheKey: String
    ): Layout? {
        val gxMeasureSize =
            GXTemplateEngine.GXMeasureSize(gxItemViewPort.width, gxItemViewPort.height)
        val gxTemplateData = GXTemplateEngine.GXTemplateData(gxItemData)
        return computeItemLayoutByCreateAndBindNode(
            gxTemplateContext,
            gxItemTemplateItem,
            gxMeasureSize,
            gxTemplateData,
            gxItemVisualTemplateNode,
            itemCacheKey
        )
    }

    private fun computeGridItemLayout(
        gxTemplateContext: GXTemplateContext,
        gxItemViewPort: Size<Float?>,
        gxItemTemplateItem: GXTemplateEngine.GXTemplateItem,
        gxItemVisualTemplateNode: GXTemplateNode?,
        gxItemData: JSONObject,
        itemCacheKey: String
    ): Layout? {
        val gxMeasureSize =
            GXTemplateEngine.GXMeasureSize(gxItemViewPort.width, gxItemViewPort.height)
        val gxTemplateData = GXTemplateEngine.GXTemplateData(gxItemData)
        return computeItemLayoutByCreateAndBindNode(
            gxTemplateContext,
            gxItemTemplateItem,
            gxMeasureSize,
            gxTemplateData,
            gxItemVisualTemplateNode,
            itemCacheKey
        )
    }

    fun computeScrollAndGridFooterItemViewPort(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode
    ): Size<Float?> {

        // 对于坑位的视口宽高，需要分为Scroll容器和Grid容器

        // 1. 对于Scroll容器，其坑位的宽高由坑位自身确定，可以直接使用未计算过的视口宽高
        // 其坑位的高度，可以不计算
        if (gxNode.isScrollType()) {
            val gxScrollConfig = gxNode.templateNode.layer.scrollConfig
                ?: throw IllegalArgumentException("Want to computeItemViewPort, but gxScrollConfig is null")

            GXRegisterCenter.instance.extensionScroll?.convert(
                GXTemplateKey.GAIAX_CUSTOM_PROPERTY_VIEW_PORT_WIDTH,
                gxTemplateContext,
                gxScrollConfig
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

            val gxGridConfig = gxNode.templateNode.layer.gridConfig
                ?: throw IllegalArgumentException("Want to computeFooterItemViewPort, but gxGridConfig is null")

            val padding = gxNode.getPaddingRect()

            return when {
                gxGridConfig.isVertical -> {
                    Size(containerWidth - (padding.left + padding.right), null)
                }
                gxGridConfig.isHorizontal -> {
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

    fun computeScrollAndGridItemViewPort(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode
    ): Size<Float?> {
        if (gxNode.isScrollType()) {
            return computeScrollItemViewPort(gxTemplateContext, gxNode)
        }
        if (gxNode.isGridType()) {
            return computeGridItemViewPort(gxTemplateContext, gxNode)
        }
        return Size(null, null)
    }

    private fun computeScrollItemViewPort(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode
    ): Size<Float?> {

        val gxScrollConfig = gxNode.templateNode.layer.scrollConfig
            ?: throw IllegalArgumentException("Want to computeItemViewPort, but gxScrollConfig is null")

        GXRegisterCenter.instance.extensionScroll?.convert(
            GXTemplateKey.GAIAX_CUSTOM_PROPERTY_VIEW_PORT_WIDTH, gxTemplateContext, gxScrollConfig
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

        return Size(null, null)
    }

    private fun computeGridItemViewPort(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode
    ): Size<Float?> {
        val containerWidth = gxNode.stretchNode.layoutByBind?.width ?: gxNode.layoutByPrepare?.width
        ?: throw IllegalArgumentException("Want to computeItemViewPort, but containerWith is null")

        val gxGridConfig = gxNode.templateNode.layer.gridConfig
            ?: throw IllegalArgumentException("Want to computeItemViewPort, but config is null")

        return when {
            gxGridConfig.isVertical -> {
                val column = gxGridConfig.column(gxTemplateContext)

                val totalItemSpacing = gxGridConfig.itemSpacing * (column - 1)

                val paddingRect = gxNode.getPaddingRect()

                val padding = paddingRect.left + paddingRect.right

                val width = (containerWidth - totalItemSpacing - padding) * 1.0F / column

                Size(width, null)
            }
            gxGridConfig.isHorizontal -> {
                // TODO: Grid横向处理不支持，此种情况暂时不做处理，很少见
                Size(null, null)
            }
            else -> {
                Size(null, null)
            }
        }
    }

    fun computeSliderItemViewPort(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode
    ): Size<Float?> {
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
        return Size(null, null)
    }

    private fun computeItemLayoutByCreateAndBindNode(
        gxTemplateContext: GXTemplateContext,
        gxTemplateItem: GXTemplateEngine.GXTemplateItem,
        gxMeasureSize: GXTemplateEngine.GXMeasureSize,
        gxTemplateData: GXTemplateEngine.GXTemplateData,
        gxVisualTemplateNode: GXTemplateNode?,
        itemCacheKey: String
    ): Layout? {

        val gxItemTemplateInfo = GXTemplateEngine.instance.data.getTemplateInfo(gxTemplateItem)

        val gxItemTemplateContext = GXTemplateContext.createContext(
            gxTemplateItem, gxMeasureSize, gxItemTemplateInfo, gxVisualTemplateNode
        )

        if (!GXGlobalCache.instance.isExistForPrepareView(gxTemplateItem)) {
            GXTemplateEngine.instance.render.prepareView(gxItemTemplateContext)
        }

        gxItemTemplateContext.templateData = gxTemplateData

        val gxItemRootNode =
            GXTemplateEngine.instance.render.createViewOnlyNodeTree(gxItemTemplateContext)

        GXTemplateEngine.instance.render.bindViewDataOnlyNodeTree(gxItemTemplateContext)

        gxTemplateContext.initNodeForScroll()
        gxTemplateContext.putNodeForScroll(itemCacheKey, gxItemRootNode)

        return gxItemRootNode.stretchNode.layoutByBind
    }

    private fun computeScrollContainerSize(
        gxNode: GXNode, itemSize: Layout?, containerTemplateData: JSONArray
    ): Size<Dimension?>? {
        if (itemSize != null) {
            val gxScrollConfig = gxNode.templateNode.layer.scrollConfig
                ?: throw IllegalArgumentException("Want to computeContainerHeight, but gxScrollConfig is null")

            // 如果是横向，那么高度就是坑位高度
            if (gxScrollConfig.isHorizontal) {
                return Size(
                    Dimension.Points(itemSize.width), Dimension.Points(itemSize.height)
                )
            }
            // 如果是竖向，那么高度就是坑位高度*行数+总间距
            else if (gxScrollConfig.isVertical) {
                val lines = max(1, ceil((containerTemplateData.size * 1.0F).toDouble()).toInt())
                var containerHeight = itemSize.height
                containerHeight *= lines
                containerHeight += gxScrollConfig.itemSpacing * (lines - 1)
                return Size(
                    Dimension.Points(itemSize.width), Dimension.Points(containerHeight)
                )
            }
        }
        return null
    }

    private fun computeGridContainerSize(
        context: GXTemplateContext,
        gxNode: GXNode,
        itemSize: Layout?,
        containerTemplateData: JSONArray
    ): Size<Dimension?>? {
        if (itemSize != null) {
            val gxGridConfig = gxNode.templateNode.layer.gridConfig
                ?: throw IllegalArgumentException("Want to computeContainerHeight, but gxGridConfig is null")

            // 如果是竖向，那么高度就是坑位高度*行数+总间距
            if (gxGridConfig.isVertical) {

                // 获取行数
                val lines = max(
                    1,
                    ceil((containerTemplateData.size * 1.0F / gxGridConfig.column(context)).toDouble()).toInt()
                )

                var containerHeight = itemSize.height

                // 计算高度
                containerHeight *= lines
                containerHeight += gxGridConfig.rowSpacing * (lines - 1)

                // 处理padding
                val padding = gxNode.getPaddingRect()
                containerHeight += padding.top + padding.bottom

                val containerWidth = itemSize.width - padding.left - padding.right

                return Size(
                    Dimension.Points(containerWidth), Dimension.Points(containerHeight)
                )
            } else if (gxGridConfig.isHorizontal) {
                // TODO: Grid横向处理不支持，此种情况暂时不做处理，很少见
                return null
            }
        }
        return null
    }

    private fun computeSliderContainerSize(
        itemSize: Layout?
    ): Size<Dimension?>? {
        if (itemSize != null) {
            return Size(Dimension.Points(itemSize.width), Dimension.Points(itemSize.height))
        }
        return null
    }
}