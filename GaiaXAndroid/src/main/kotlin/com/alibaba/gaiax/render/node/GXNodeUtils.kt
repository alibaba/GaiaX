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
import com.alibaba.gaiax.utils.getStringExt
import com.alibaba.gaiax.utils.getStringExtCanNull
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

    fun computeContainerSizeByItemTemplate(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        containerData: JSONArray
    ): Size<Dimension?>? {

        if (gxNode.childTemplateItems?.isEmpty() == true) {
            return null
        }

        // ????????????????????????
        // 1. ????????????????????????????????????
        // 2. ?????????????????????????????????
        //  2.1 ??????????????????
        //  2.2 ??????????????????

        if (gxNode.multiTypeItemComputeCache == null) {
            gxNode.multiTypeItemComputeCache = mutableMapOf()
        }

        if (gxNode.multiTypeItemComputeCache?.isEmpty() == true) {

            // 1. ???????????????ViewPort??????
            val itemViewPort: Size<Float?> = computeItemViewPort(gxTemplateContext, gxNode)

            // case 1
            if (gxNode.childTemplateItems?.size == 1) {

                // 2. ??????????????????????????????
                val itemTemplatePair = gxNode.childTemplateItems?.firstOrNull() ?: return null
                val itemTemplateItem = itemTemplatePair.first
                val itemVisualTemplateNode = itemTemplatePair.second
                val itemLayout: Layout? = computeContainerItemLayout(
                    gxTemplateContext,
                    gxNode,
                    itemViewPort,
                    itemTemplateItem,
                    itemVisualTemplateNode,
                    containerData.firstOrNull() as? JSONObject ?: JSONObject()
                )

                // 3. ?????????????????????????????????
                val containerSize =
                    computeContainerSize(gxTemplateContext, gxNode, itemLayout, containerData)

                gxNode.multiTypeItemComputeCache?.put(
                    itemTemplateItem,
                    GXNode.GXMultiTypeItemComputeCache(containerSize, itemLayout)
                )
            }
            // case 2
            else {

                // init multi type item
                containerData.forEach {
                    val itemData = it as JSONObject
                    gxNode.templateNode.resetData()
                    gxNode.templateNode.getExtend(itemData)?.let { typeData ->
                        val path =
                            typeData.getStringExt("${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE}.${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE_PATH}")
                        val templateId =
                            typeData.getStringExtCanNull("${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE}.${GXTemplateKey.GAIAX_DATABINDING_ITEM_TYPE_CONFIG}.${path}")
                        val items = gxNode.childTemplateItems
                        if (items != null && templateId != null) {
                            items.firstOrNull { it.first.templateId == templateId }
                                ?.let { itemTemplatePair ->

                                    // 2. ??????????????????????????????
                                    val itemTemplateItem = itemTemplatePair.first
                                    val itemVisualTemplateNode = itemTemplatePair.second

                                    if (gxNode.multiTypeItemComputeCache
                                            ?.containsKey(itemTemplateItem) == false
                                    ) {

                                        val itemLayout: Layout? = computeContainerItemLayout(
                                            gxTemplateContext,
                                            gxNode,
                                            itemViewPort,
                                            itemTemplateItem,
                                            itemVisualTemplateNode,
                                            itemData
                                        )

                                        // 3. ?????????????????????????????????
                                        val containerSize =
                                            computeContainerSize(
                                                gxTemplateContext,
                                                gxNode,
                                                itemLayout,
                                                containerData
                                            )

                                        gxNode.multiTypeItemComputeCache?.put(
                                            itemTemplateItem,
                                            GXNode.GXMultiTypeItemComputeCache(
                                                containerSize,
                                                itemLayout
                                            )
                                        )
                                    }
                                }
                        }
                    }
                }
            }
        }

        // ?????????????????????????????????
        var result: Size<Dimension?>? = null
        gxNode.multiTypeItemComputeCache?.forEach { entry ->
            if (result == null) {
                result = entry.value.containerSize
            } else {
                val old = result?.height?.value
                val new = entry.value.containerSize?.height?.value
                if (old != null && new != null && new > old) {
                    result = entry.value.containerSize
                }
            }
        }

        return result
    }

    fun computeContainerFooterItemSize(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        itemViewPort: Size<Float?>,
        gxItemTemplateItem: GXTemplateEngine.GXTemplateItem,
        gxItemVisualTemplateNode: GXTemplateNode?,
        containerData: JSONArray
    ): Layout? {
        when {
            gxNode.isScrollType() -> {
                val itemData = JSONObject()
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
            // ?????????Grid????????????????????????????????????????????????????????????Item?????????
            gxNode.isGridType() -> {
                val itemData = JSONObject()
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

    internal fun computeContainerItemLayout(
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
                    gxItemViewPort.width,
                    gxItemViewPort.height
                )
                val gxTemplateData = GXTemplateEngine.GXTemplateData(gxItemData)
                val stretchNode = computeItemSizeByCreateAndBindNode(
                    gxTemplateContext,
                    gxItemTemplateItem,
                    gxMeasureSize,
                    gxTemplateData,
                    gxItemVisualTemplateNode
                )?.stretchNode
                return stretchNode?.layoutByBind
            }
            // ?????????Grid????????????????????????????????????????????????????????????Item?????????
            gxNode.isGridType() -> {
                val gxMeasureSize =
                    GXTemplateEngine.GXMeasureSize(gxItemViewPort.width, gxItemViewPort.height)
                val gxTemplateData: GXTemplateEngine.GXTemplateData =
                    GXTemplateEngine.GXTemplateData(gxItemData)
                val stretchNode = computeItemSizeByCreateAndBindNode(
                    gxTemplateContext,
                    gxItemTemplateItem,
                    gxMeasureSize,
                    gxTemplateData,
                    gxItemVisualTemplateNode
                )?.stretchNode
                return stretchNode?.layoutByBind
            }
            else -> {
                return null
            }
        }
    }

    fun computeFooterItemViewPort(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode
    ): Size<Float?> {

        // ??????????????????????????????????????????Scroll?????????Grid??????

        // 1. ??????Scroll????????????????????????????????????????????????????????????????????????????????????????????????
        // ????????????????????????????????????
        if (gxNode.isScrollType()) {
            val finalScrollConfig = gxNode.templateNode.finalScrollConfig
                ?: throw IllegalArgumentException("Want to computeItemViewPort, but finalScrollConfig is null")

            GXRegisterCenter.instance
                .extensionScroll?.convert(
                    GXTemplateKey.GAIAX_CUSTOM_PROPERTY_VIEW_PORT_WIDTH,
                    gxTemplateContext,
                    finalScrollConfig
                )?.let {
                    return Size(it as Float, null)
                }

            // ???????????????horizontal??????vertical????????????????????????????????????????????????????????????
            val left: Float = finalScrollConfig.edgeInsets.left.toFloat()
            val right = finalScrollConfig.edgeInsets.right.toFloat()
            gxTemplateContext.size.width?.let {
                return Size(it - left - right, null)
            }
        }
        // 2. ??????Grid?????????????????????????????????GridConfig???Grid???????????????????????????????????????
        // ????????????????????????????????????
        else if (gxNode.isGridType()) {
            val containerWidth = gxNode.stretchNode.layoutByBind?.width
                ?: gxNode.stretchNode.layoutByCreate?.width
                ?: throw IllegalArgumentException("Want to computeFooterItemViewPort, but containerWith is null")
            val gridConfig = gxNode.templateNode.finalGridConfig
                ?: throw IllegalArgumentException("Want to computeFooterItemViewPort, but finalGridConfig is null")

            return when {
                gridConfig.isVertical -> {
                    val padding = gridConfig.edgeInsets.left + gridConfig.edgeInsets.right
                    Size(containerWidth - padding, null)
                }
                gridConfig.isHorizontal -> {
                    // TODO: Grid??????????????????????????????????????????????????????????????????
                    Size(null, null)
                }
                else -> {
                    Size(null, null)
                }
            }
        }

        return Size(null, null)
    }

    fun computeItemViewPort(gxTemplateContext: GXTemplateContext, gxNode: GXNode): Size<Float?> {

        // ??????????????????????????????????????????Scroll?????????Grid??????

        // 1. ??????Scroll????????????????????????????????????????????????????????????????????????????????????????????????
        // ????????????????????????????????????
        if (gxNode.isScrollType()) {
            val finalScrollConfig = gxNode.templateNode.finalScrollConfig
                ?: throw IllegalArgumentException("Want to computeItemViewPort, but finalScrollConfig is null")

            GXRegisterCenter.instance
                .extensionScroll?.convert(
                    GXTemplateKey.GAIAX_CUSTOM_PROPERTY_VIEW_PORT_WIDTH,
                    gxTemplateContext,
                    finalScrollConfig
                )?.let {
                    return Size(it as Float, null)
                }

            // ???????????????horizontal??????vertical????????????????????????????????????????????????????????????
            val left: Float = finalScrollConfig.edgeInsets.left.toFloat()
            val right = finalScrollConfig.edgeInsets.right.toFloat()
            gxTemplateContext.size.width?.let {
                return Size(it - left - right, null)
            }
        }
        // 2. ??????Grid?????????????????????????????????GridConfig???Grid???????????????????????????????????????
        // ????????????????????????????????????
        else if (gxNode.isGridType()) {
            val containerWidth = gxNode.stretchNode.layoutByBind?.width
                ?: gxNode.stretchNode.layoutByCreate?.width
                ?: throw IllegalArgumentException("Want to computeItemViewPort, but containerWith is null")
            val gridConfig = gxNode.templateNode.finalGridConfig
                ?: throw IllegalArgumentException("Want to computeItemViewPort, but finalGridConfig is null")
            return when {
                gridConfig.isVertical -> {
                    val totalItemSpacing = gridConfig.itemSpacing *
                            (gridConfig.column(gxTemplateContext) - 1)
                    val padding = gridConfig.edgeInsets.left + gridConfig.edgeInsets.right
                    val finalWidth = (containerWidth - totalItemSpacing - padding) * 1.0F /
                            gridConfig.column(gxTemplateContext)
                    Size(finalWidth, null)
                }
                gridConfig.isHorizontal -> {
                    // TODO: Grid??????????????????????????????????????????????????????????????????
                    Size(null, null)
                }
                else -> {
                    Size(null, null)
                }
            }
        }
        return Size(null, null)
    }

    private fun computeItemSizeByCreateAndBindNode(
        gxTemplateContext: GXTemplateContext,
        templateItem: GXTemplateEngine.GXTemplateItem,
        measureSize: GXTemplateEngine.GXMeasureSize,
        templateData: GXTemplateEngine.GXTemplateData,
        visualTemplateNode: GXTemplateNode?
    ): GXNode? {

        // TODO ??????????????? ??????????????????SIZE?????????????????????????????????create??????bind???????????????????????????create??????bind?????????????????????????????????
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
            // ?????????????????????????????????Scroll???Grid
            if (gxNode.isScrollType()) {
                val finalScrollConfig = gxNode.templateNode.finalScrollConfig
                    ?: throw IllegalArgumentException("Want to computeContainerHeight, but finalScrollConfig is null")

                // ????????????????????????????????????????????????
                if (finalScrollConfig.isHorizontal) {
                    return Size(
                        Dimension.Points(itemSize.width),
                        Dimension.Points(itemSize.height)
                    )
                }
                // ????????????????????????????????????????????????*??????+?????????
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
                    ?: throw IllegalArgumentException("Want to computeContainerHeight, but finalGridConfig is null")

                // ????????????????????????????????????????????????*??????+?????????
                if (finalGridConfig.isVertical) {

                    // ????????????
                    val lines =
                        ceil((containerTemplateData.size * 1.0F / finalGridConfig.column(context)).toDouble()).toInt()

                    var containerHeight = itemSize.height

                    // ????????????
                    containerHeight *= lines
                    containerHeight += finalGridConfig.rowSpacing * (lines - 1)

                    // ??????padding
                    val edgeInsets = finalGridConfig.edgeInsets
                    containerHeight += edgeInsets.top + edgeInsets.bottom

                    return Size(
                        Dimension.Points(itemSize.width),
                        Dimension.Points(containerHeight)
                    )
                } else if (finalGridConfig.isHorizontal) {
                    // TODO: Grid??????????????????????????????????????????????????????????????????
                    return null
                }
            }
        }
        return null
    }
}