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

import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import app.visly.stretch.Dimension
import app.visly.stretch.Display
import app.visly.stretch.PositionType
import app.visly.stretch.Size
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.text.GXDirtyText
import com.alibaba.gaiax.render.node.text.GXFitContentUtils
import com.alibaba.gaiax.render.node.text.GXHighLightUtil
import com.alibaba.gaiax.render.view.*
import com.alibaba.gaiax.render.view.basic.GXIImageView
import com.alibaba.gaiax.render.view.basic.GXProgressView
import com.alibaba.gaiax.render.view.basic.GXText
import com.alibaba.gaiax.render.view.basic.GXView
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.render.view.container.GXContainerViewAdapter
import com.alibaba.gaiax.render.view.container.slider.GXSliderView
import com.alibaba.gaiax.render.view.container.slider.GXSliderViewAdapter
import com.alibaba.gaiax.template.GXCss
import com.alibaba.gaiax.template.GXLayer
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.template.animation.GXAnimationBinding
import com.alibaba.gaiax.template.animation.GXLottieAnimation
import com.alibaba.gaiax.template.animation.GXPropAnimationSet
import com.alibaba.gaiax.template.factory.GXExpressionFactory
import com.alibaba.gaiax.template.utils.GXTemplateUtils
import com.alibaba.gaiax.utils.GXGlobalCache

/**
 * @suppress
 */
object GXNodeTreeUpdate {

    fun buildNodeLayout(gxTemplateContext: GXTemplateContext) {
        val rootNode = gxTemplateContext.rootNode
            ?: throw IllegalArgumentException("RootNode is null(buildNodeLayout)")
        val templateData =
            gxTemplateContext.templateData?.data ?: throw IllegalArgumentException("Data is null")
        val size = Size(gxTemplateContext.size.width, gxTemplateContext.size.height)

        // 更新布局
        Layout.updateNodeTreeLayout(gxTemplateContext, rootNode, templateData, size)

        // 如果存在延迟计算文字自适应的情况，需要处理后重新计算
        Layout.updateNodeTreeLayoutByDirtyText(gxTemplateContext, rootNode, size)
    }


    fun buildViewStyleAndData(gxTemplateContext: GXTemplateContext) {
        val rootNode = gxTemplateContext.rootNode
            ?: throw IllegalArgumentException("RootNode is null(buildViewStyle)")
        val templateData =
            gxTemplateContext.templateData?.data ?: throw IllegalArgumentException("Data is null")

        // 更新样式
        Style.updateNodeTreeStyleAndData(gxTemplateContext, rootNode, templateData)
    }

    fun resetView(gxTemplateContext: GXTemplateContext) {
        val rootNode = gxTemplateContext.rootNode
            ?: throw IllegalArgumentException("RootNode is null(resetView)")

        // 更新样式
        Style.resetViewTree(rootNode)
    }

    object Layout {

        internal fun updateNodeTreeLayout(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject,
            size: Size<Float?>
        ) {
            // 更新布局
            updateNodeTreeLayout(gxTemplateContext, gxNode, templateData)

            // 计算布局
            if (gxTemplateContext.isDirtyForCompute()) {
                GXNodeUtils.computeNodeTreeByBindData(gxNode, size)
            }
        }

        internal fun updateNodeTreeLayoutByDirtyText(
            gxTemplateContext: GXTemplateContext, rootNode: GXNode, size: Size<Float?>
        ) {
            if (gxTemplateContext.dirtyTexts?.isNotEmpty() == true) {
                gxTemplateContext.dirtyTexts?.forEach {
                    updateTextLayoutByFitContentByDirtyText(
                        it.gxTemplateContext,
                        it.gxNode,
                        it.templateData,
                    )
                }
                gxTemplateContext.dirtyTexts?.clear()
                if (gxTemplateContext.isDirtyForText()) {
                    GXNodeUtils.computeNodeTreeByBindData(rootNode, size)
                }
            }
        }

        private fun updateNodeTreeLayout(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {

            gxNode.resetFromUpdate(gxTemplateContext)

            if (gxNode.isNestRoot) {
                updateNestNodeLayout(gxTemplateContext, gxNode, templateData)
            } else if (gxNode.isContainerType()) {
                updateContainerNodeLayout(gxTemplateContext, gxNode, templateData)
            } else {
                updateNormalNodeLayout(gxTemplateContext, gxNode, templateData)
            }
        }

        private fun updateNestNodeLayout(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {
            // 容器节点
            if (gxNode.templateNode.isContainerType) {
                updateNestContainerNodeLayout(gxTemplateContext, gxNode, templateData)
            }
            // 嵌套的子节点
            else {
                updateNestNormalNodeLayout(gxTemplateContext, gxNode, templateData)
            }
        }

        private fun updateContainerNodeLayout(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {
            gxNode.stretchNode.initFinal()
            gxNode.templateNode.initFinal(
                gxTemplateContext, visualTemplateData = null, nodeTemplateData = templateData
            )

            updateNodeLayout(gxTemplateContext, gxNode, templateData)
        }

        private fun updateNormalNodeLayout(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {
            gxNode.stretchNode.initFinal()
            gxNode.templateNode.initFinal(
                gxTemplateContext, visualTemplateData = null, nodeTemplateData = templateData
            )

            updateNodeLayout(gxTemplateContext, gxNode, templateData)

            gxNode.children?.forEach { childNode ->
                // 使用原有数据为数据源
                updateNodeTreeLayout(gxTemplateContext, childNode, templateData)
            }
        }

        private fun updateNestContainerNodeLayout(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {

            // 虚拟节点所在的模板，需要传递数据给下一层子模板
            // 若没有数据需要传递，那么给下一层子模板传递一个空数据源
            // 此处，双端已协商一致

            // 对于普通嵌套模板，传递给下一层的数据只能是JSONObject
            var valueData = gxNode.templateNode.visualTemplateNode?.getDataValue(templateData)
            if (valueData is JSONArray) {

                if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isCompatibilityContainerDataPassSequence == true) {
                    // 是否兼容处理先$nodes取数组，再去$$的情况

                    val tmp = gxNode.templateNode.visualTemplateNode?.dataBinding
                    gxNode.templateNode.visualTemplateNode?.dataBinding =
                        gxNode.templateNode.dataBinding
                    gxNode.templateNode.dataBinding = tmp

                    gxNode.templateNode.resetDataCache()

                    valueData = gxNode.templateNode.visualTemplateNode?.getDataValue(templateData)
                } else {
                    throw IllegalArgumentException("update nest container need a JSONObject, but the result is a JSONArray")
                }
            }
            val childTemplateData = (valueData as? JSONObject) ?: JSONObject()

            gxNode.stretchNode.initFinal()
            gxNode.templateNode.initFinal(
                gxTemplateContext,
                visualTemplateData = templateData,
                nodeTemplateData = childTemplateData
            )

            updateNodeLayout(gxTemplateContext, gxNode, childTemplateData)
        }

        private fun updateNestNormalNodeLayout(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {

            // 虚拟节点所在的模板，需要传递数据给下一层子模板
            // 若没有数据需要传递，那么给下一层子模板传递一个空数据源
            // 此处，双端已协商一致

            // 对于普通嵌套模板，传递给下一层的数据只能是JSONObject
            val childTemplateData =
                (gxNode.templateNode.visualTemplateNode?.getDataValue(templateData) as? JSONObject)
                    ?: JSONObject()

            gxNode.stretchNode.initFinal()
            gxNode.templateNode.initFinal(
                gxTemplateContext,
                visualTemplateData = templateData,
                nodeTemplateData = childTemplateData
            )

            updateNodeLayout(gxTemplateContext, gxNode, childTemplateData)

            gxNode.children?.forEach { childNode ->
                // 使用虚拟节点取值后的数据作为数据源
                updateNodeTreeLayout(gxTemplateContext, childNode, childTemplateData)
            }
        }


        private fun updateNodeLayout(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {
            // 容器节点
            if (gxNode.isContainerType()) {
                updateContainerLayout(
                    gxTemplateContext, gxNode, templateData
                )
            }
            // 普通节点
            else {
                updateNormalLayout(
                    gxTemplateContext, gxNode, templateData
                )
            }
        }

        private fun updateContainerLayout(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {

            //  对于容器嵌套模板，传递给下一层的数据只能是JSONArray
            val containerData =
                (gxNode.templateNode.getDataValue(templateData) as? JSONArray) ?: JSONArray()

            val stretchNode = gxNode.stretchNode.node
                ?: throw IllegalArgumentException("stretch node is null, please check!")

            val stretchStyle = stretchNode.getStyle()

            val gxCss = gxNode.templateNode.css
            val gxFlexBox = gxCss.flexBox

            val height = gxFlexBox.sizeForDimension?.height
            val flexGrow = gxFlexBox.flexGrow

            if (gxNode.isScrollType()) {
                val gxScrollConfig = gxNode.templateNode.layer.scrollConfig
                    ?: throw IllegalArgumentException("Want to updateContainerLayout, but gxScrollConfig is null")

                // 当容器节点不是flexGrow时，且容器节点的高度设置，或者是默认，或者是未定义，需要主动计算高度
                var isComputeContainerHeight =
                    gxScrollConfig.isHorizontal && flexGrow == null && (height == null || height == Dimension.Auto || height == Dimension.Undefined)

                // 对计算结果进行处理
                GXRegisterCenter.instance.extensionDynamicProperty?.convert(GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                    GXTemplateKey.GAIAX_CUSTOM_PROPERTY_SCROLL_COMPUTE_CONTAINER_HEIGHT,
                    isComputeContainerHeight
                ).apply {
                    this.flexBox = gxFlexBox
                })?.let {
                    isComputeContainerHeight = it as Boolean
                }

                if (isComputeContainerHeight) {
                    val containerSize = GXNodeUtils.computeScrollSize(
                        gxTemplateContext, gxNode, containerData
                    )
                    containerSize?.height?.let {
                        gxFlexBox.sizeForDimension?.height = it
                    }
                }

            } else if (gxNode.isGridType()) {

                val gxGridConfig = gxNode.templateNode.layer.gridConfig
                    ?: throw IllegalArgumentException("Want to updateContainerLayout, but gxGridConfig is null")

                var isComputeContainerHeight =
                    gxGridConfig.isVertical && flexGrow == null && (height == null || height == Dimension.Auto || height == Dimension.Undefined)

                // 对计算结果进行处理
                GXRegisterCenter.instance.extensionDynamicProperty?.convert(GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                    GXTemplateKey.GAIAX_CUSTOM_PROPERTY_GRID_COMPUTE_CONTAINER_HEIGHT,
                    isComputeContainerHeight
                ).apply {
                    this.gridConfig = gxGridConfig
                    this.flexBox = gxFlexBox
                })?.let {
                    isComputeContainerHeight = it as Boolean
                }

                // 当容器节点不是flexGrow时，且容器节点的高度设置，或者是默认，或者是未定义，需要主动计算高度
                if (isComputeContainerHeight) {
                    val containerSize = GXNodeUtils.computeGridSize(
                        gxTemplateContext, gxNode, containerData
                    )
                    containerSize?.height?.let {
                        gxFlexBox.sizeForDimension?.height = it
                    }
                }
            } else if (gxNode.isSliderType()) {
                val isComputeContainerHeight =
                    height == null || height == Dimension.Auto || height == Dimension.Undefined

                // 容器节点没有设置高度
                if (isComputeContainerHeight) {
                    val containerSize = GXNodeUtils.computeSliderSize(
                        gxTemplateContext, gxNode, containerData
                    )
                    containerSize?.height?.let {
                        gxFlexBox.sizeForDimension?.height = it
                    }
                }
            }

            updateLayoutByFlexBox(gxTemplateContext, gxNode)

            if (gxTemplateContext.isDirtyForStyle()) {
                stretchStyle.free()
                stretchStyle.init()
                stretchNode.setStyle(stretchStyle)
                stretchNode.markDirty()
            }

        }

        private fun updateNormalLayout(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {

            updateLayoutByFlexBox(
                gxTemplateContext, gxNode
            )

            updateLayoutByCssStyle(
                gxTemplateContext, gxNode, templateData
            )

            val stretchNode = gxNode.stretchNode.node
                ?: throw IllegalArgumentException("stretch node is null, please check!")
            val stretchStyle = stretchNode.getStyle()

            if (gxTemplateContext.isDirtyForStyle()) {
                stretchStyle.free()
                stretchStyle.init()
                stretchNode.setStyle(stretchStyle)
                stretchNode.markDirty()
            }
        }

        private fun updateLayoutByFlexBox(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode
        ) {

            val gxFlexBox = gxNode.templateNode.css.flexBox
            val gxCssStyle = gxNode.templateNode.css.style
            val stretchStyle: app.visly.stretch.Style = gxNode.stretchNode.node?.getStyle()
                ?: throw IllegalArgumentException("stretch node is null, please check!")

            gxFlexBox.display?.let {
                if (it != stretchStyle.display) {
                    stretchStyle.display = it
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.aspectRatio?.let {
                if (it != stretchStyle.aspectRatio) {
                    stretchStyle.aspectRatio = it
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.direction?.let {
                if (it != stretchStyle.direction) {
                    stretchStyle.direction = it
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.flexDirection?.let {
                if (it != stretchStyle.flexDirection) {
                    stretchStyle.flexDirection = it
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.flexWrap?.let {
                if (it != stretchStyle.flexWrap) {
                    stretchStyle.flexWrap = it
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.overflow?.let {
                if (it != stretchStyle.overflow) {
                    stretchStyle.overflow = it
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.alignItems?.let {
                if (it != stretchStyle.alignItems) {
                    stretchStyle.alignItems = it
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.alignSelf?.let {
                if (it != stretchStyle.alignSelf) {
                    stretchStyle.alignSelf = it
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.alignContent?.let {
                if (it != stretchStyle.alignContent) {
                    stretchStyle.alignContent = it
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.justifyContent?.let {
                if (it != stretchStyle.justifyContent) {
                    stretchStyle.justifyContent = it
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.positionType?.let {
                if (it != stretchStyle.positionType) {
                    stretchStyle.positionType = it
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            // 仅在绝对布局下，才能更新position的数据
            if (stretchStyle.positionType == PositionType.Absolute) {
                gxFlexBox.positionForDimension?.let {
                    if (it != stretchStyle.position) {
                        GXTemplateUtils.updateDimension(it, stretchStyle.position)
                        gxTemplateContext.dirtyForStyle()
                    }
                    gxTemplateContext.dirtyForCompute()
                }
            }

            gxFlexBox.marginForDimension?.let {
                if (it != stretchStyle.margin) {
                    GXTemplateUtils.updateDimension(it, stretchStyle.margin)
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.paddingForDimension?.let {
                if (it != stretchStyle.padding) {
                    GXTemplateUtils.updateDimension(it, stretchStyle.padding)
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.borderForDimension?.let {
                if (it != stretchStyle.border) {
                    GXTemplateUtils.updateDimension(it, stretchStyle.border)
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.flexGrow?.let {
                if (it != stretchStyle.flexGrow) {
                    stretchStyle.flexGrow = it
                    gxTemplateContext.isFlexGrowLayout = true
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.flexShrink?.let {
                if (it != stretchStyle.flexShrink) {
                    stretchStyle.flexShrink = it
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.sizeForDimension?.let {
                if (it != stretchStyle.size && !gxNode.isFitContentModify) {
                    GXTemplateUtils.updateSize(it, stretchStyle.size)
                    gxTemplateContext.dirtyForStyle()
                }

                val extensionDynamicProperty = GXRegisterCenter.instance.extensionDynamicProperty
                if (extensionDynamicProperty != null) {
                    val width = stretchStyle.size.width
                    val height = stretchStyle.size.height
                    val gxParams = GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                        GXTemplateKey.FLEXBOX_SIZE, stretchStyle.size
                    ).apply {
                        this.cssStyle = gxCssStyle
                    }
                    extensionDynamicProperty.convert(gxParams)
                    if (stretchStyle.size.width != width || stretchStyle.size.height != height) {
                        gxTemplateContext.dirtyForStyle()
                    }
                }

                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.minSizeForDimension?.let {
                if (it != stretchStyle.minSize) {
                    GXTemplateUtils.updateSize(it, stretchStyle.minSize)
                    GXRegisterCenter.instance.extensionDynamicProperty?.convert(GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                        GXTemplateKey.FLEXBOX_MIN_SIZE, stretchStyle.minSize
                    ).apply {
                        this.cssStyle = gxCssStyle
                    })
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

            gxFlexBox.maxSizeForDimension?.let {
                if (it != stretchStyle.maxSize) {
                    GXTemplateUtils.updateSize(it, stretchStyle.maxSize)
                    GXRegisterCenter.instance.extensionDynamicProperty?.convert(GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                        GXTemplateKey.FLEXBOX_MAX_SIZE, stretchStyle.maxSize
                    ).apply {
                        this.cssStyle = gxCssStyle
                    })
                    gxTemplateContext.dirtyForStyle()
                }
                gxTemplateContext.dirtyForCompute()
            }

        }

        private fun updateLayoutByCssStyle(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {

            val stretchNode = gxNode.stretchNode.node
                ?: throw IllegalArgumentException("stretch node is null, please check!")

            val gxStyle = gxNode.templateNode.css.style

            if (gxStyle.fitContent == true && isSelfAndParentNodeTreeFlex(gxNode)) {

                // 如果布局中存在flexGrow，那么文字在自适应的时候需要延迟处理
                // 因为flexGrow的最终大小还受到了databinding文件中的padding、margin等动态属性的影响,
                // 如果提前计算，会导致结果不正确
                if (gxTemplateContext.isFlexGrowLayout) {
                    if (gxTemplateContext.dirtyTexts == null) {
                        gxTemplateContext.dirtyTexts = mutableSetOf()
                    }
                    gxTemplateContext.dirtyTexts?.add(
                        GXDirtyText(
                            gxTemplateContext, gxNode, templateData
                        )
                    )
                    return
                }

                // 处理普通的fitContent逻辑
                val stretchStyle = stretchNode.getStyle()
                GXFitContentUtils.fitContent(
                    gxTemplateContext, gxNode, gxNode.templateNode, templateData
                )?.let { src ->

                    if (src != stretchStyle.size) {
                        // 自适应之后的宽度，要更新到原有尺寸上
                        GXTemplateUtils.updateSize(src, stretchStyle.size)

                        gxNode.isFitContentModify = true

                        // 使用FlexGrow结合FitContent计算出来的宽度，需要将flexGrow重置成0，
                        // 否则在Stretch计算的时候会使用FlexGrow计算出的宽度
                        if (stretchStyle.flexGrow != 0F) {
                            stretchStyle.flexGrow = 0F
                        }

                        gxTemplateContext.dirtyForStyle()

                    }

                    val extensionDynamicProperty =
                        GXRegisterCenter.instance.extensionDynamicProperty
                    if (extensionDynamicProperty != null) {
                        val width = stretchStyle.size.width
                        val height = stretchStyle.size.height
                        val gxParams = GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                            GXTemplateKey.FLEXBOX_SIZE, stretchStyle.size
                        ).apply {
                            cssStyle = gxStyle
                        }
                        extensionDynamicProperty.convert(gxParams)
                        if (stretchStyle.size.width != width || stretchStyle.size.height != height) {
                            gxTemplateContext.dirtyForStyle()

                        }
                    }

                    gxTemplateContext.dirtyForCompute()
                }
            }
        }

        private fun isSelfAndParentNodeTreeFlex(gxNode: GXNode?): Boolean {
            // 根节点的父节点
            if (gxNode == null) {
                return true
            }
            val stretchNode = gxNode.stretchNode.node
                ?: throw IllegalArgumentException("stretch node is null, please check!")
            val selfIsFlex = stretchNode.getStyle().display == Display.Flex
            return selfIsFlex && isSelfAndParentNodeTreeFlex(gxNode.parentNode)
        }

        private fun updateTextLayoutByFitContentByDirtyText(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject,
        ) {

            val gxTemplateNode = gxNode.templateNode
            val gxStretchNode = gxNode.stretchNode
            val stretchNode = gxNode.stretchNode.node
                ?: throw IllegalArgumentException("stretch node is null, please check!")
            val stretchStyle = stretchNode.getStyle()
            val gxStyle = gxNode.templateNode.css.style

            // 处理fitContent逻辑
            GXFitContentUtils.fitContent(
                gxTemplateContext, gxNode, gxTemplateNode, templateData
            )?.let { src ->

                if (src != stretchStyle.size) {
                    // 自适应之后的宽度，要更新到原有尺寸上
                    GXTemplateUtils.updateSize(src, stretchStyle.size)

                    // 使用FlexGrow结合FitContent计算出来的宽度，需要将flexGrow重置成0，
                    // 否则在Stretch计算的时候会使用FlexGrow计算出的宽度
                    if (stretchStyle.flexGrow != 0F) {
                        stretchStyle.flexGrow = 0F
                    }

                    gxTemplateContext.dirtyForStyle()
                    gxTemplateContext.dirtyForText()
                }

                val extensionDynamicProperty = GXRegisterCenter.instance.extensionDynamicProperty
                if (extensionDynamicProperty != null) {
                    val width = stretchStyle.size.width
                    val height = stretchStyle.size.height
                    val gxParams = GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                        GXTemplateKey.FLEXBOX_SIZE, stretchStyle.size
                    ).apply {
                        cssStyle = gxStyle
                    }
                    extensionDynamicProperty.convert(gxParams)
                    if (stretchStyle.size.width != width || stretchStyle.size.height != height) {
                        gxTemplateContext.dirtyForStyle()
                        gxTemplateContext.dirtyForText()
                    }
                }
            }

            if (gxTemplateContext.isDirtyForStyle()) {
                stretchStyle.free()
                stretchStyle.init()
                stretchNode.setStyle(stretchStyle)
                stretchNode.markDirty()
            }
        }
    }

    object Style {

        internal fun resetViewTree(gxNode: GXNode) {
            resetViewData(gxNode)
            gxNode.children?.forEach { childNode ->
                resetViewTree(childNode)
            }
        }

        internal fun updateNodeTreeStyleAndData(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {
            if (gxNode.isNestRoot) {
                updateNestNodeStyleAndData(gxTemplateContext, gxNode, templateData)
            } else if (gxNode.isContainerType()) {
                updateContainerNodeStyleAndData(gxTemplateContext, gxNode, templateData)
            } else {
                updateNormalNodeStyleAndData(gxTemplateContext, gxNode, templateData)
            }
        }

        private fun updateNestNodeStyleAndData(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {
            // 容器节点
            if (gxNode.templateNode.isContainerType) {
                updateNestContainerNodeStyle(gxTemplateContext, gxNode, templateData)
            }
            // 嵌套的子节点
            else {
                updateNestNormalNodeStyle(gxTemplateContext, gxNode, templateData)
            }
        }

        private fun updateContainerNodeStyleAndData(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {
            updateNodeStyleAndData(gxTemplateContext, gxNode, templateData)
        }

        private fun updateNestContainerNodeStyle(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {

            // 虚拟节点所在的模板，需要传递数据给下一层子模板
            // 若没有数据需要传递，那么给下一层子模板传递一个空数据源
            // 此处，双端已协商一致

            // 对于普通嵌套模板，传递给下一层的数据只能是JSONObject
            val valueData = gxNode.templateNode.visualTemplateNode?.getDataValue(templateData)
            val childTemplateData = (valueData as? JSONObject) ?: JSONObject()

            updateNodeStyleAndData(gxTemplateContext, gxNode, childTemplateData)
        }

        private fun updateNestNormalNodeStyle(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {

            // 对于普通嵌套模板，传递给下一层的数据只能是JSONObject
            val childTemplateData =
                (gxNode.templateNode.visualTemplateNode?.getDataValue(templateData) as? JSONObject)
                    ?: JSONObject()

            updateNodeStyleAndData(gxTemplateContext, gxNode, childTemplateData)

            gxNode.children?.forEach { childNode ->
                // 使用原有数据为数据源
                updateNodeTreeStyleAndData(gxTemplateContext, childNode, childTemplateData)
            }
        }

        private fun updateNormalNodeStyleAndData(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {
            updateNodeStyleAndData(gxTemplateContext, gxNode, templateData)

            gxNode.children?.forEach { childNode ->
                // 使用原有数据为数据源
                updateNodeTreeStyleAndData(gxTemplateContext, childNode, templateData)
            }
        }

        private fun updateNodeStyleAndData(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {
            // 更新视图样式
            nodeViewCss(gxTemplateContext, gxNode)

            // 更新视图数据
            nodeViewData(gxTemplateContext, gxNode, templateData)

            // 更新视图埋点
            nodeViewTrack(gxTemplateContext, gxNode, templateData)

            // 更新视图事件
            nodeViewEvent(gxTemplateContext, gxNode, templateData)

            // 更新视图动画
            nodeViewAnimation(gxTemplateContext, gxNode, templateData)
        }

        private fun nodeViewAnimation(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {

            val gxAnimationExpression =
                gxNode.templateNode.animationBinding?.animation?.expression() as? JSONObject
                    ?: return

            val gxAnimationValue =
                gxNode.templateNode.animationBinding?.animation?.value(templateData) as? JSONObject
                    ?: return

            val type = gxAnimationValue.getString(GXAnimationBinding.KEY_TYPE) ?: return

            val trigger = gxAnimationValue.getBooleanValue(GXAnimationBinding.KEY_TRIGGER)

            // 手动触发动画
            if (trigger) {

                val state = gxAnimationValue[GXAnimationBinding.KEY_STATE]

                // 符合条件触发动画
                val isState = GXExpressionFactory.isTrue(
                    gxTemplateContext.templateInfo.expVersion, state
                ) == true
                if (isState) {
                    playAnimation(
                        gxTemplateContext, gxNode, gxAnimationValue, gxAnimationExpression, type
                    )
                }
            }
            // 自动触发动画
            else {
                playAnimation(
                    gxTemplateContext, gxNode, gxAnimationExpression, gxAnimationValue, type
                )
            }
        }

        private fun playAnimation(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            gxAnimationExpression: JSONObject,
            gxAnimationValue: JSONObject,
            type: String
        ) {
            val animation = if (GXTemplateKey.GAIAX_ANIMATION_TYPE_LOTTIE.equals(type, true)) {
                val lottieData =
                    gxAnimationValue.getJSONObject(GXAnimationBinding.KEY_LOTTIE_ANIMATOR)
                        ?: if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isCompatibilityLottieOldDataStructure == true) {
                            gxAnimationValue
                        } else {
                            return
                        }
                GXLottieAnimation.create(lottieData)
            } else if (GXTemplateKey.GAIAX_ANIMATION_TYPE_PROP.equals(type, true)) {
                val animatorData =
                    gxAnimationValue.getJSONObject(GXAnimationBinding.KEY_PROP_ANIMATOR_SET)
                        ?: return
                GXPropAnimationSet.create(animatorData)
            } else {
                null
            }

            if (animation is GXPropAnimationSet) {
                gxNode.view?.let { targetView ->
                    animation.playAnimation(gxTemplateContext, gxNode, targetView)
                }
            } else if (animation is GXLottieAnimation) {
                animation.playAnimation(
                    gxTemplateContext, gxNode, gxAnimationExpression, gxAnimationValue
                )
            }
        }

        private fun nodeViewCss(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode
        ) {
            val gxView = gxNode.view ?: return
            val gxCss = gxNode.templateNode.css

            // 对高斯模糊前置处理
            bindBackdropFilter(gxTemplateContext, gxNode, gxCss, gxView)

            // 对BoxShadow处理
            bindBoxShadow(gxNode, gxCss)

            if (gxView is GXText && (gxNode.isTextType() || gxNode.isRichTextType() || gxNode.isIconFontType())) {
                gxView.setTextStyle(gxCss)
            } else if (gxView is GXIImageView && gxNode.isImageType()) {
                gxView.setImageStyle(gxTemplateContext, gxCss)
            } else if (gxNode.isContainerType()) {
                bindContainerViewCss(gxTemplateContext, gxView, gxNode)
            }

            bindCommonViewCss(gxView, gxCss, gxNode)
        }

        private fun bindBoxShadow(
            gxNode: GXNode,
            gxCss: GXCss,
        ) {
            if (gxNode.isViewType() || gxNode.isImageType()) {
                gxNode.boxLayoutView?.setStyle(gxCss.style)
            }
        }

        private fun bindBackdropFilter(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, gxCss: GXCss, gxView: View
        ) {
            if (gxNode.isViewType()) {
                if (gxCss.style.backdropFilter != null) {
                    (gxView as GXView).setBackdropFilter(
                        gxTemplateContext, gxCss.style.backdropFilter
                    )
                }
            }
        }

        private fun nodeViewEvent(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSON
        ) {
            if (templateData !is JSONObject) {
                return
            }
            val invisible = gxNode.templateNode.css.style.isInvisible()
            if (invisible) {
                return
            }

            val targetView = gxNode.view

            // 滚动事件
            if (targetView is RecyclerView) {
                if (gxTemplateContext.templateData?.eventListener != null) {
                    targetView.clearOnScrollListeners()
                    targetView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                        override fun onScrolled(
                            recyclerView: RecyclerView, dx: Int, dy: Int
                        ) {
                            val gxScroll = GXTemplateEngine.GXScroll().apply {
                                this.type = GXTemplateEngine.GXScroll.TYPE_ON_SCROLLED
                                this.view = recyclerView
                                this.dx = dx
                                this.dy = dy
                            }
                            gxTemplateContext.templateData?.eventListener?.onScrollEvent(gxScroll)
                        }

                        override fun onScrollStateChanged(
                            recyclerView: RecyclerView, newState: Int
                        ) {
                            val gxScroll = GXTemplateEngine.GXScroll().apply {
                                this.type = GXTemplateEngine.GXScroll.TYPE_ON_SCROLL_STATE_CHANGED
                                this.view = recyclerView
                                this.state = newState
                            }
                            gxTemplateContext.templateData?.eventListener?.onScrollEvent(gxScroll)
                        }
                    })
                }
            }

            // 数据绑定事件
            if (gxNode.templateNode.eventBinding != null) {

                // 创建事件处理器
                gxNode.event =
                    gxNode.event ?: GXRegisterCenter.instance.extensionNodeEvent?.create()
                            ?: GXNodeEvent()

                val gxNodeEvent = gxNode.event
                if (gxNodeEvent is GXINodeEvent) {
                    // 添加事件
                    gxNodeEvent.addDataBindingEvent(gxTemplateContext, gxNode, templateData)
                } else {
                    throw IllegalArgumentException("Not support the event")
                }
            }
        }

        private fun nodeViewTrack(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {

            val view = gxNode.view ?: return
            val gxTemplateNode = gxNode.templateNode

            val invisible = gxTemplateNode.css.style.isInvisible()
            if (invisible) {
                return
            }

            val gxTrackBinding = gxTemplateNode.trackBinding
            if (gxTrackBinding != null) {
                // 如果track域存在，那么不在走之前的埋点逻辑
                // https://www.yuque.com/biezhihua/gaiax/ld6iie
                val trackData = gxTrackBinding.track.value(templateData) as? JSONObject ?: return
                val gxTrack = GXTemplateEngine.GXTrack().apply {
                    this.view = view
                    this.trackParams = trackData
                    this.nodeId = gxTemplateNode.layer.id
                    this.templateItem = gxTemplateContext.templateItem
                    this.index = -1
                }
                if (gxTemplateContext.manualTrackMap == null) {
                    gxTemplateContext.manualTrackMap = mutableMapOf()
                }
                gxTemplateContext.manualTrackMap?.put(gxTemplateNode.getNodeId, gxTrack)
            } else {
                val gxEventBinding = gxTemplateNode.eventBinding ?: return
                val trackData = gxEventBinding.event.value(templateData) as? JSONObject ?: return
                val gxTrack = GXTemplateEngine.GXTrack().apply {
                    this.view = view
                    this.trackParams = trackData
                    this.nodeId = gxTemplateNode.layer.id
                    this.templateItem = gxTemplateContext.templateItem
                    this.index = -1
                }
                gxTemplateContext.templateData?.trackListener?.onTrackEvent(gxTrack)
            }
        }

        private fun resetViewData(
            gxNode: GXNode
        ) {
            gxNode.templateNode.dataBinding ?: return
            val view = gxNode.view ?: return
            if (view is GXIViewBindData) {
                view.onResetData()
            }
        }

        private fun nodeViewData(
            gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
        ) {
            gxNode.templateNode.dataBinding ?: return
            val view = gxNode.view ?: return
            if (view !is GXIViewBindData) {
                return
            }

            val css = gxNode.templateNode.css
            val layer = gxNode.templateNode.layer

            when {
                gxNode.isCustomViewType() -> bindCustom(
                    view, gxNode.templateNode, templateData
                )
                gxNode.isTextType() -> bindText(
                    gxTemplateContext, view, css, layer, gxNode.templateNode, templateData
                )
                gxNode.isRichTextType() -> bindRichText(
                    gxTemplateContext, view, css, layer, gxNode.templateNode, templateData
                )
                gxNode.isIconFontType() -> bindIconFont(view, gxNode.templateNode, templateData)
                gxNode.isImageType() -> bindImage(view, gxNode.templateNode, templateData)
                gxNode.isProgressType() -> bindProgress(view, gxNode.templateNode, templateData)
                gxNode.isScrollType() || gxNode.isGridType() -> bindScrollAndGrid(
                    gxTemplateContext, view, gxNode, gxNode.templateNode, templateData
                )
                gxNode.isSliderType() -> bindSlider(
                    gxTemplateContext, view, gxNode, gxNode.templateNode, templateData
                )
                gxNode.isViewType() || gxNode.isGaiaTemplateType() -> bindView(
                    view, gxNode.templateNode, templateData
                )
            }

            gxTemplateContext.bindDataCount++
        }

        private fun bindScrollAndGrid(
            gxTemplateContext: GXTemplateContext,
            view: View,
            gxNode: GXNode,
            gxTemplateNode: GXTemplateNode,
            templateData: JSONObject
        ) {

            // 容器数据源
            var containerTemplateData = gxTemplateNode.getDataValue(templateData) as? JSONArray
            if (containerTemplateData == null) {
                if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isPreventContainerDataSourceThrowException == true) {
                    containerTemplateData = JSONArray()
                } else {
                    throw IllegalArgumentException("Scroll or Grid must be have a array data source")
                }
            }

            val extendData = gxTemplateNode.getExtend(templateData)

            val container = view as GXContainer

            gxTemplateContext.initContainers()
            gxTemplateContext.containers?.add(container)

            val adapter: GXContainerViewAdapter?
            if (container.adapter != null) {
                adapter = container.adapter as GXContainerViewAdapter
            } else {
                adapter = GXContainerViewAdapter(gxTemplateContext, container)
                container.adapter = adapter
            }

            adapter.gxNode = gxNode

            GXRegisterCenter.instance.extensionScroll?.scrollIndex(
                gxTemplateContext, container, extendData
            )

            // forbid item animator
            container.itemAnimator = null

            adapter.setContainerData(containerTemplateData)
            adapter.initFooter()
            if (adapter.hasFooter()) {
                container.setSpanSizeLookup()
            }
        }

        private fun bindIconFont(
            view: GXIViewBindData, gxTemplateNode: GXTemplateNode, templateData: JSONObject
        ) {
            val nodeData = gxTemplateNode.getData(templateData)
            view.onBindData(nodeData)
        }

        private fun bindImage(
            view: GXIViewBindData, gxTemplateNode: GXTemplateNode, templateData: JSONObject
        ) {
            val nodeData = gxTemplateNode.getData(templateData)
            view.onBindData(nodeData)
        }

        private fun bindView(
            view: GXIViewBindData, gxTemplateNode: GXTemplateNode, templateData: JSONObject
        ) {
            val nodeData = gxTemplateNode.getData(templateData)
            view.onBindData(nodeData)
        }

        private fun bindRichText(
            gxTemplateContext: GXTemplateContext,
            view: GXIViewBindData,
            css: GXCss?,
            layer: GXLayer,
            gxTemplateNode: GXTemplateNode,
            templateData: JSONObject
        ) {
            val nodeData = gxTemplateNode.getData(templateData)

            val valueData = nodeData?.get(GXTemplateKey.GAIAX_VALUE)

            // 优先处理高亮逻辑
            if (valueData is String) {
                val result: CharSequence? = GXHighLightUtil.getHighLightContent(
                    view as View, gxTemplateNode, templateData, valueData
                )
                if (result != null) {
                    val data = JSONObject()
                    data[GXTemplateKey.GAIAX_VALUE] = result
                    data[GXTemplateKey.GAIAX_ACCESSIBILITY_DESC] =
                        nodeData[GXTemplateKey.GAIAX_ACCESSIBILITY_DESC]
                    data[GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE] =
                        nodeData[GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE]
                    view.onBindData(data)
                    return
                }
            }

            // 处理数据逻辑
            if (gxTemplateContext.templateData?.dataListener != null) {
                val gxTextData = GXTemplateEngine.GXTextData().apply {
                    this.text = valueData as? CharSequence
                    this.view = view as View
                    this.nodeId = layer.id
                    this.templateItem = gxTemplateContext.templateItem
                    this.nodeCss = css
                    this.nodeData = nodeData
                    this.index = gxTemplateContext.indexPosition
                    this.extendData = gxTemplateNode.getExtend(templateData)
                }
                val result = gxTemplateContext.templateData?.dataListener?.onTextProcess(gxTextData)
                if (result != null) {
                    val data = JSONObject()
                    data[GXTemplateKey.GAIAX_VALUE] = result
                    data[GXTemplateKey.GAIAX_ACCESSIBILITY_DESC] =
                        nodeData?.get(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC)
                    data[GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE] =
                        nodeData?.get(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)
                    view.onBindData(data)
                }
                return
            }

            view.onBindData(nodeData)
        }

        private fun bindText(
            gxTemplateContext: GXTemplateContext,
            view: GXIViewBindData,
            css: GXCss?,
            layer: GXLayer,
            gxTemplateNode: GXTemplateNode,
            templateData: JSONObject
        ) {

            val nodeData = gxTemplateNode.getData(templateData)

            if (gxTemplateContext.templateData?.dataListener != null) {

                val gxTextData = GXTemplateEngine.GXTextData().apply {
                    this.text = nodeData?.get(GXTemplateKey.GAIAX_VALUE)?.toString()
                    this.view = view as View
                    this.nodeId = layer.id
                    this.templateItem = gxTemplateContext.templateItem
                    this.nodeCss = css
                    this.nodeData = nodeData
                    this.index = gxTemplateContext.indexPosition
                    this.extendData = gxTemplateNode.getExtend(templateData)
                }

                gxTemplateContext.templateData?.dataListener?.onTextProcess(gxTextData)
                    ?.let { result ->
                        val data = JSONObject()
                        data[GXTemplateKey.GAIAX_VALUE] = result
                        data[GXTemplateKey.GAIAX_ACCESSIBILITY_DESC] =
                            nodeData?.get(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC)
                        data[GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE] =
                            nodeData?.get(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)
                        view.onBindData(data)
                        return
                    }
            }

            view.onBindData(nodeData)
        }

        private fun bindCustom(
            view: GXIViewBindData, gxTemplateNode: GXTemplateNode, templateData: JSONObject
        ) {
            val data = gxTemplateNode.getData(templateData)
            view.onBindData(data)
        }

        private fun bindCommonViewCss(
            gxView: View, gxCss: GXCss, gxNode: GXNode
        ) {

            gxView.setDisplay(gxCss.style.display)

            if (!gxNode.isCustomViewType()) {

                gxView.setHidden(gxCss.style.display, gxCss.style.hidden)

                gxView.setOpacity(gxCss.style.opacity)

                gxView.setOverflow(gxCss.style.overflow)

                gxView.setBackgroundColorAndBackgroundImageWithRadius(gxCss.style)

                gxView.setRoundCornerRadiusAndRoundCornerBorder(gxCss.style)

            }
        }

        private fun bindContainerViewCss(
            gxTemplateContext: GXTemplateContext, view: View, gxNode: GXNode
        ) {
            if (gxNode.isContainerType()) {
                if (gxNode.isGridType()) {
                    bindGridContainerCSS(gxTemplateContext, view, gxNode)
                } else if (gxNode.isScrollType()) {
                    bindScrollContainerCSS(view, gxNode)
                }
            }
        }

        private fun bindGridContainerCSS(
            gxTemplateContext: GXTemplateContext, view: View, gxNode: GXNode
        ) {
            gxNode.templateNode.layer.gridConfig?.let {
                view.setGridContainerDirection(
                    gxTemplateContext, it, gxNode.layoutByBind
                )
                view.setGridContainerItemSpacingAndRowSpacing(
                    gxNode.getPaddingRect(), it.itemSpacing, it.rowSpacing
                )
            }
        }

        private fun bindScrollContainerCSS(
            view: View, gxNode: GXNode
        ) {
            gxNode.templateNode.layer.scrollConfig?.let { scrollConfig ->

                view.setScrollContainerDirection(
                    scrollConfig.direction, gxNode.layoutByBind
                )

                val padding = gxNode.getPaddingRect()
                val lineSpacing = scrollConfig.itemSpacing
                if (scrollConfig.direction == LinearLayoutManager.HORIZONTAL) {
                    // 设置边距
                    if (padding.top == 0 && padding.bottom == 0) {
                        view.setHorizontalScrollContainerLineSpacing(
                            padding.left, padding.right, lineSpacing
                        )
                    } else {
                        if (lineSpacing != 0) {
                            view.setHorizontalScrollContainerLineSpacing(lineSpacing)
                        }
                        view.setScrollContainerPadding(padding)
                    }
                } else {
                    if (lineSpacing != 0) {
                        view.setVerticalScrollContainerLineSpacing(lineSpacing)
                    }
                    view.setScrollContainerPadding(padding)
                }
            }
        }

        private fun bindSlider(
            gxTemplateContext: GXTemplateContext,
            view: View,
            gxNode: GXNode,
            gxTemplateNode: GXTemplateNode,
            templateData: JSONObject
        ) {

            // 容器数据源
            var containerTemplateData = gxTemplateNode.getDataValue(templateData) as? JSONArray
            if (containerTemplateData == null) {
                if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isPreventContainerDataSourceThrowException == true) {
                    containerTemplateData = JSONArray()
                } else {
                    throw IllegalArgumentException("Slider or Grid must be have a array data source")
                }
            }

            val container = view as GXSliderView

            gxTemplateContext.initContainers()
            gxTemplateContext.containers?.add(container)

            container.setTemplateContext(gxTemplateContext)

            val adapter: GXSliderViewAdapter?
            if (container.viewPager?.adapter != null) {
                adapter = container.viewPager?.adapter as GXSliderViewAdapter
            } else {
                adapter = GXSliderViewAdapter(gxTemplateContext, gxNode)
                container.viewPager?.adapter = adapter
            }
            adapter.setConfig(gxNode.templateNode.layer.sliderConfig)
            container.setConfig(gxNode.templateNode.layer.sliderConfig)

            adapter.setData(containerTemplateData)
            container.setPageSize(containerTemplateData.size)

            container.onBindData(templateData)
        }

        private fun bindProgress(
            view: GXIViewBindData, gxTemplateNode: GXTemplateNode, templateData: JSONObject
        ) {
            val progressView = view as? GXProgressView
            progressView?.setConfig(gxTemplateNode.layer.progressConfig)
            progressView?.onBindData(gxTemplateNode.getData(templateData))
        }
    }
}
