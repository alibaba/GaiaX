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
import com.alibaba.gaiax.template.*
import com.alibaba.gaiax.template.animation.GXAnimationBinding
import com.alibaba.gaiax.template.animation.GXLottieAnimation
import com.alibaba.gaiax.template.animation.GXPropAnimationSet
import com.alibaba.gaiax.template.factory.GXExpressionFactory
import com.alibaba.gaiax.template.utils.GXTemplateUtils

/**
 * @suppress
 */
class GXNodeTreeUpdate(val gxTemplateContext: GXTemplateContext) {

    fun buildNodeLayout() {
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

    fun buildViewStyleAndData() {
        val rootNode = gxTemplateContext.rootNode
            ?: throw IllegalArgumentException("RootNode is null(buildViewStyle)")
        val templateData =
            gxTemplateContext.templateData?.data ?: throw IllegalArgumentException("Data is null")

        // 更新样式
        Style.updateNodeTreeStyleAndData(gxTemplateContext, rootNode, templateData)
    }

    fun buildLayoutAndStyle() {
        val rootNode = gxTemplateContext.rootNode
            ?: throw IllegalArgumentException("RootNode is null(buildLayoutAndStyle)")
        val templateData =
            gxTemplateContext.templateData?.data ?: throw IllegalArgumentException("Data is null")
        val size = Size(gxTemplateContext.size.width, gxTemplateContext.size.height)

        // 更新布局
        Layout.updateNodeTreeLayout(gxTemplateContext, rootNode, templateData, size)

        // 如计算文字自适应的情况，需要处理后重新计算
        Layout.updateNodeTreeLayoutByDirtyText(gxTemplateContext, rootNode, size)

        // 更新样式
        Style.updateNodeTreeStyleAndData(gxTemplateContext, rootNode, templateData)
    }

    object Layout {

        internal fun updateNodeTreeLayout(
            gxTemplateContext: GXTemplateContext,
            rootNode: GXNode,
            templateData: JSONObject,
            size: Size<Float?>
        ) {
            // 更新布局
            updateNodeTreeLayout(gxTemplateContext, rootNode, templateData)

            // 计算布局
            if (gxTemplateContext.isDirty) {
                GXNodeUtils.computeNodeTreeByBindData(rootNode, size)
            }
        }

        internal fun updateNodeTreeLayoutByDirtyText(
            gxTemplateContext: GXTemplateContext,
            rootNode: GXNode,
            size: Size<Float?>
        ) {
            if (gxTemplateContext.dirtyTexts?.isNotEmpty() == true) {
                var isTextDirty = false
                gxTemplateContext.dirtyTexts?.forEach {
                    val isDirty = updateTextLayoutByFitContentByDirtyText(
                        it.gxTemplateContext,
                        it.gxNode,
                        it.templateData,
                    )
                    if (isDirty) {
                        isTextDirty = true
                    }
                }
                gxTemplateContext.dirtyTexts?.clear()
                if (isTextDirty) {
                    GXNodeUtils.computeNodeTreeByBindData(rootNode, size)
                }
            }
        }

        private fun updateNodeTreeLayout(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ) {
            gxNode.templateNode.resetDataCache()
            gxNode.stretchNode.reset(gxTemplateContext, gxNode.templateNode)

            if (gxNode.isNestRoot) {
                updateNestNodeLayout(gxTemplateContext, gxNode, templateData)
            } else if (gxNode.isContainerType()) {
                updateContainerNodeLayout(gxTemplateContext, gxNode, templateData)
            } else {
                updateNormalNodeLayout(gxTemplateContext, gxNode, templateData)
            }
        }

        private fun updateNestNodeLayout(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ) {
            // 容器节点
            if (gxNode.templateNode.isContainerType()) {
                updateNestContainerNodeLayout(gxTemplateContext, gxNode, templateData)
            }
            // 嵌套的子节点
            else {
                updateNestNormalNodeLayout(gxTemplateContext, gxNode, templateData)
            }
        }

        private fun updateContainerNodeLayout(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ) {
            gxNode.stretchNode.initFinal()
            gxNode.templateNode.initFinal(
                gxTemplateContext, visualTemplateData = null, nodeTemplateData = templateData
            )

            updateNodeLayout(gxTemplateContext, gxNode, templateData)
        }

        private fun updateNormalNodeLayout(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
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
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
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
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
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
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ) {
            // 容器节点
            if (gxNode.isContainerType()) {
                val isDirty = updateContainerLayout(
                    gxTemplateContext, gxNode, templateData
                )
                if (isDirty) {
                    gxTemplateContext.isDirty = isDirty
                }
            }
            // 普通节点
            else {
                val isDirty = updateNormalLayout(
                    gxTemplateContext, gxNode, templateData
                )
                if (isDirty) {
                    gxTemplateContext.isDirty = isDirty
                }
            }
        }

        private fun updateContainerLayout(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ): Boolean {

            //  对于容器嵌套模板，传递给下一层的数据只能是JSONArray
            val containerTemplateData =
                (gxNode.templateNode.getDataValue(templateData) as? JSONArray) ?: JSONArray()

            val stretchStyle = gxNode.stretchNode.node.getStyle()

            val finalCss = gxNode.templateNode.finalCss
            val finalFlexBox = finalCss?.flexBox
            val finalCssStyle = finalCss?.style

            if (finalFlexBox == null) {
                throw IllegalArgumentException("final flexbox is null, please check!")
            }

            if (finalCssStyle == null) {
                throw IllegalArgumentException("final css style is null, please check!")
            }

            var isDirty = false

            val finalHeight = finalFlexBox.sizeForStyle?.height
            val finalFlexGrow = finalFlexBox.flexGrow

            if (gxNode.isScrollType()) {
                val finalScrollConfig = gxNode.templateNode.finalScrollConfig
                    ?: throw IllegalArgumentException("Want to updateContainerLayout, but finalScrollConfig is null")

                // 当容器节点不是flexGrow时，且容器节点的高度设置，或者是默认，或者是未定义，需要主动计算高度
                var isComputeContainerHeight = finalScrollConfig.isHorizontal
                        && finalFlexGrow == null
                        && (finalHeight == null || finalHeight == Dimension.Auto || finalHeight == Dimension.Undefined)

                // 对计算结果进行处理
                GXRegisterCenter.instance.extensionDynamicProperty?.convert(GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                    GXTemplateKey.GAIAX_CUSTOM_PROPERTY_SCROLL_COMPUTE_CONTAINER_HEIGHT,
                    isComputeContainerHeight
                ).apply {
                    this.flexBox = finalFlexBox
                })?.let {
                    isComputeContainerHeight = it as Boolean
                }

                if (isComputeContainerHeight) {
                    val containerSize = GXNodeUtils.computeContainerSizeByItemTemplate(
                        gxTemplateContext, gxNode, containerTemplateData
                    )
                    containerSize?.height?.let {
                        finalFlexBox.sizeForStyle?.height = it
                        isDirty = true
                    }
                }

            } else if (gxNode.isGridType()) {

                val finalGridConfig = gxNode.templateNode.finalGridConfig
                    ?: throw IllegalArgumentException("Want to updateContainerLayout, but finalGridConfig is null")

                var isComputeContainerHeight = finalGridConfig.isVertical
                        && finalFlexGrow == null
                        && (finalHeight == null || finalHeight == Dimension.Auto || finalHeight == Dimension.Undefined)

                // 对计算结果进行处理
                GXRegisterCenter.instance.extensionDynamicProperty?.convert(GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                    GXTemplateKey.GAIAX_CUSTOM_PROPERTY_GRID_COMPUTE_CONTAINER_HEIGHT,
                    isComputeContainerHeight
                ).apply {
                    this.gridConfig = finalGridConfig
                    this.flexBox = finalFlexBox
                })?.let {
                    isComputeContainerHeight = it as Boolean
                }

                // 当容器节点不是flexGrow时，且容器节点的高度设置，或者是默认，或者是未定义，需要主动计算高度
                if (isComputeContainerHeight) {
                    val containerSize = GXNodeUtils.computeContainerSizeByItemTemplate(
                        gxTemplateContext, gxNode, containerTemplateData
                    )
                    containerSize?.height?.let {
                        finalFlexBox.sizeForStyle?.height = it
                        isDirty = true
                    }
                }
            } else if (gxNode.isSliderType()) {
                var isComputeContainerHeight =
                    finalHeight == null || finalHeight == Dimension.Auto || finalHeight == Dimension.Undefined

                // 容器节点没有设置高度
                if (isComputeContainerHeight) {
                    val containerSize = GXNodeUtils.computeContainerSizeByItemTemplate(
                        gxTemplateContext, gxNode, containerTemplateData
                    )
                    containerSize?.height?.let {
                        finalFlexBox.sizeForStyle?.height = it
                        isDirty = true
                    }
                }
            }

            updateLayoutByFlexBox(
                gxTemplateContext, gxNode
            )?.let {
                isDirty = it
            }

            if (isDirty) {
                stretchStyle.free()
                stretchStyle.init()
                gxNode.stretchNode.node.setStyle(stretchStyle)
                gxNode.stretchNode.node.markDirty()
                return true
            }

            return false
        }

        private fun updateNormalLayout(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ): Boolean {

            var isDirty = false

            updateLayoutByFlexBox(
                gxTemplateContext, gxNode
            )?.let {
                isDirty = it
            }

            updateLayoutByCssStyle(
                gxTemplateContext, gxNode, templateData
            )?.let {
                isDirty = it
            }

            val stretchNode = gxNode.stretchNode.node
            val stretchStyle = stretchNode.getStyle()

            if (isDirty) {
                stretchStyle.free()
                stretchStyle.init()
                stretchNode.setStyle(stretchStyle)
                stretchNode.markDirty()
                return true
            }

            return false
        }

        private fun updateLayoutByFlexBox(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode
        ): Boolean? {

            val gxFlexBox = gxNode.templateNode.finalCss?.flexBox ?: return null
            val gxCssStyle = gxNode.templateNode.finalCss?.style ?: return null
            val stretchStyle: app.visly.stretch.Style = gxNode.stretchNode.node.getStyle()

            var isDirty: Boolean? = null
            gxFlexBox.display?.let {
                stretchStyle.display = it
                isDirty = true
            }

            gxFlexBox.aspectRatio?.let {
                stretchStyle.aspectRatio = it
                isDirty = true
            }

            gxFlexBox.direction?.let {
                stretchStyle.direction = it
                isDirty = true
            }

            gxFlexBox.flexDirection?.let {
                stretchStyle.flexDirection = it
                isDirty = true
            }

            gxFlexBox.flexWrap?.let {
                stretchStyle.flexWrap = it
                isDirty = true
            }

            gxFlexBox.overflow?.let {
                stretchStyle.overflow = it
                isDirty = true
            }

            gxFlexBox.alignItems?.let {
                stretchStyle.alignItems = it
                isDirty = true
            }

            gxFlexBox.alignSelf?.let {
                stretchStyle.alignSelf = it
                isDirty = true
            }

            gxFlexBox.alignContent?.let {
                stretchStyle.alignContent = it
                isDirty = true
            }

            gxFlexBox.justifyContent?.let {
                stretchStyle.justifyContent = it
                isDirty = true
            }

            gxFlexBox.positionType?.let {
                stretchStyle.positionType = it
                isDirty = true
            }

            // 仅在绝对布局下，才能更新position的数据
            if (stretchStyle.positionType == PositionType.Absolute) {
                gxFlexBox.positionForStyle?.let {
                    GXTemplateUtils.updateDimension(it, stretchStyle.position)
                    isDirty = true
                }
            }

            gxFlexBox.marginForStyle?.let {
                GXTemplateUtils.updateDimension(it, stretchStyle.margin)
                isDirty = true
            }

            gxFlexBox.paddingForStyle?.let {
                GXTemplateUtils.updateDimension(it, stretchStyle.padding)
                isDirty = true
            }

            gxFlexBox.borderForStyle?.let {
                GXTemplateUtils.updateDimension(it, stretchStyle.border)
                isDirty = true
            }

            gxFlexBox.flexGrow?.let {
                stretchStyle.flexGrow = it
                gxTemplateContext.isFlexGrowLayout = true
                isDirty = true
            }

            gxFlexBox.flexShrink?.let {
                stretchStyle.flexShrink = it
                isDirty = true
            }

            gxFlexBox.sizeForStyle?.let {
                GXTemplateUtils.updateSize(it, stretchStyle.size)
                GXRegisterCenter.instance.extensionDynamicProperty?.convert(GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                    GXTemplateKey.FLEXBOX_SIZE, stretchStyle.size
                ).apply {
                    this.cssStyle = gxCssStyle
                })
                isDirty = true
            }

            gxFlexBox.minSizeForStyle?.let {
                GXTemplateUtils.updateSize(it, stretchStyle.minSize)
                GXRegisterCenter.instance.extensionDynamicProperty?.convert(GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                    GXTemplateKey.FLEXBOX_MIN_SIZE, stretchStyle.minSize
                ).apply {
                    this.cssStyle = gxCssStyle
                })
                isDirty = true
            }

            gxFlexBox.maxSizeForStyle?.let {
                GXTemplateUtils.updateSize(it, stretchStyle.maxSize)
                GXRegisterCenter.instance.extensionDynamicProperty?.convert(GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                    GXTemplateKey.FLEXBOX_MAX_SIZE, stretchStyle.maxSize
                ).apply {
                    this.cssStyle = gxCssStyle
                })
                isDirty = true
            }

            return isDirty
        }

        private fun updateLayoutByCssStyle(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ): Boolean? {

            val gxStyle = gxNode.templateNode.finalCss?.style

            if (gxStyle != null && gxStyle.fitContent == true && isSelfAndParentNodeTreeFlex(gxNode)) {

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
                    return null
                }

                // 处理普通的fitContent逻辑
                return updateLayoutByFitContent(
                    gxTemplateContext,
                    gxNode,
                    gxNode.templateNode,
                    gxNode.stretchNode,
                    gxStyle,
                    templateData,
                    gxNode.stretchNode.node.getStyle()
                )
            }

            return null
        }

        private fun isSelfAndParentNodeTreeFlex(gxNode: GXNode?): Boolean {
            // 根节点的父节点
            if (gxNode == null) {
                return true
            }
            val selfIsFlex = gxNode.stretchNode.node.getStyle().display == Display.Flex
            return selfIsFlex && isSelfAndParentNodeTreeFlex(gxNode.parentNode)
        }

        private fun updateLayoutByFitContent(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            gxTemplateNode: GXTemplateNode,
            gxStretchNode: GXStretchNode,
            gxCssStyle: GXStyle,
            templateData: JSONObject,
            style: app.visly.stretch.Style
        ): Boolean? {

            GXFitContentUtils.fitContent(
                gxTemplateContext, gxNode, gxTemplateNode, gxStretchNode, templateData
            )?.let { src ->

                // 自适应之后的宽度，要更新到原有尺寸上
                GXTemplateUtils.updateSize(src, style.size)

                GXRegisterCenter.instance.extensionDynamicProperty?.convert(GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                    GXTemplateKey.FLEXBOX_SIZE, style.size
                ).apply {
                    this.cssStyle = gxCssStyle
                })

                // 使用FlexGrow结合FitContent计算出来的宽度，需要将flexGrow重置成0，
                // 否则在Stretch计算的时候会使用FlexGrow计算出的宽度
                if (style.flexGrow != 0F) {
                    style.flexGrow = 0F
                }

                return true
            }

            return null
        }

        private fun updateTextLayoutByFitContentByDirtyText(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject,
        ): Boolean {

            val gxTemplateNode = gxNode.templateNode
            val gxStretchNode = gxNode.stretchNode
            val stretchStyle = gxStretchNode.node.getStyle()
            val gxStyle = gxNode.templateNode.finalCss?.style ?: return false

            // 处理fitContent逻辑
            val isDirty = updateLayoutByFitContent(
                gxTemplateContext,
                gxNode,
                gxTemplateNode,
                gxStretchNode,
                gxStyle,
                templateData,
                stretchStyle
            )

            if (isDirty == true) {
                stretchStyle.free()
                stretchStyle.init()
                gxStretchNode.node.setStyle(stretchStyle)
                gxStretchNode.node.markDirty()
                return true
            }

            return false
        }

    }

    object Style {
        internal fun updateNodeTreeStyleAndData(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
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
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ) {
            // 容器节点
            if (gxNode.templateNode.isContainerType()) {
                updateNestContainerNodeStyle(gxTemplateContext, gxNode, templateData)
            }
            // 嵌套的子节点
            else {
                updateNestNormalNodeStyle(gxTemplateContext, gxNode, templateData)
            }
        }

        private fun updateContainerNodeStyleAndData(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ) {
            updateNodeStyleAndData(gxTemplateContext, gxNode, templateData)
        }

        private fun updateNestContainerNodeStyle(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
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
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
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
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ) {
            updateNodeStyleAndData(gxTemplateContext, gxNode, templateData)

            gxNode.children?.forEach { childNode ->
                // 使用原有数据为数据源
                updateNodeTreeStyleAndData(gxTemplateContext, childNode, templateData)
            }
        }

        private fun updateNodeStyleAndData(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
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
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ) {

            val gxAnimationExpression = gxNode
                .templateNode
                .animationBinding
                ?.animation
                ?.expression() as? JSONObject ?: return

            val gxAnimationValue = gxNode
                .templateNode
                .animationBinding
                ?.animation?.value(templateData) as? JSONObject ?: return

            val type = gxAnimationValue.getString(GXAnimationBinding.KEY_TYPE) ?: return

            val trigger = gxAnimationValue.getBooleanValue(GXAnimationBinding.KEY_TRIGGER)

            // 手动触发动画
            if (trigger) {

                val state = gxAnimationValue[GXAnimationBinding.KEY_STATE]

                // 符合条件触发动画
                val isState = GXExpressionFactory
                    .isTrue(gxTemplateContext.templateInfo.expVersion, state) == true
                if (isState) {
                    playAnimation(
                        gxTemplateContext,
                        gxNode,
                        gxAnimationValue,
                        gxAnimationExpression,
                        type
                    )
                }
            }
            // 自动触发动画
            else {
                playAnimation(
                    gxTemplateContext,
                    gxNode,
                    gxAnimationExpression,
                    gxAnimationValue,
                    type
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
                val lottieData = gxAnimationValue
                    .getJSONObject(GXAnimationBinding.KEY_LOTTIE_ANIMATOR)
                    ?: if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isCompatibilityLottieOldDataStructure == true) {
                        gxAnimationValue
                    } else {
                        return
                    }
                GXLottieAnimation.create(lottieData)
            } else if (GXTemplateKey.GAIAX_ANIMATION_TYPE_PROP.equals(type, true)) {
                val animatorData = gxAnimationValue
                    .getJSONObject(GXAnimationBinding.KEY_PROP_ANIMATOR_SET) ?: return
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
                    gxTemplateContext,
                    gxNode,
                    gxAnimationExpression,
                    gxAnimationValue
                )
            }
        }

        private fun nodeViewCss(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode
        ) {
            val gxView = gxNode.view ?: return
            val gxCss = gxNode.templateNode.finalCss ?: return

            // 对高斯模糊前置处理
            bindBackdropFilter(gxTemplateContext, gxNode, gxCss, gxView)

            // 对BoxShadow处理
            bindBoxShadow(gxNode, gxCss)

            if (gxView is GXText && (gxNode.isTextType() || gxNode.isRichTextType() || gxNode.isIconFontType())) {
                gxView.setTextStyle(gxCss)
            } else if (gxView is GXIImageView && gxNode.isImageType()) {
                gxView.setImageStyle(gxTemplateContext, gxCss)
            } else if (gxNode.isContainerType()) {
                bindContainerViewCss(gxTemplateContext, gxCss, gxView, gxNode)
            }

            bindCommonViewCss(gxTemplateContext, gxView, gxCss, gxNode)
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
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            gxCss: GXCss,
            gxView: View
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
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSON
        ) {
            if (templateData !is JSONObject) {
                return
            }
            val invisible = gxNode.templateNode.finalCss?.style?.isInvisible() ?: false
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
                    throw IllegalArgumentException("Not support the event $gxNodeEvent")
                }
            }
        }

        private fun nodeViewTrack(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
        ) {

            val view = gxNode.view ?: return
            val gxTemplateNode = gxNode.templateNode

            val invisible = gxTemplateNode.finalCss?.style?.isInvisible() ?: false
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
                gxTemplateContext.manualTrackMap?.put(gxTemplateNode.getNodeId(), gxTrack)
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

        private fun nodeViewData(
            gxTemplateContext: GXTemplateContext,
            gxNode: GXNode,
            templateData: JSONObject
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
                    gxTemplateContext, view, gxNode.templateNode, templateData
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
            view: GXIViewBindData,
            gxTemplateNode: GXTemplateNode,
            templateData: JSONObject
        ) {
            val nodeData = gxTemplateNode.getData(templateData)
            view.onBindData(nodeData)
        }

        private fun bindImage(
            view: GXIViewBindData,
            gxTemplateNode: GXTemplateNode,
            templateData: JSONObject
        ) {
            val nodeData = gxTemplateNode.getData(templateData)
            view.onBindData(nodeData)
        }

        private fun bindView(
            view: GXIViewBindData,
            gxTemplateNode: GXTemplateNode,
            templateData: JSONObject
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
            gxTemplateContext: GXTemplateContext,
            view: GXIViewBindData,
            gxTemplateNode: GXTemplateNode,
            templateData: JSONObject
        ) {
            val data = gxTemplateNode.getData(templateData)
            view.onBindData(data)
        }

        private fun bindCommonViewCss(
            gxTemplateContext: GXTemplateContext,
            gxView: View,
            gxCss: GXCss,
            gxNode: GXNode
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
            gxTemplateContext: GXTemplateContext,
            gxCss: GXCss,
            view: View,
            gxNode: GXNode
        ) {
            if (gxNode.isContainerType()) {
                if (gxNode.isGridType()) {
                    bindGridContainerCSS(gxTemplateContext, view, gxNode)
                } else if (gxNode.isScrollType()) {
                    bindScrollContainerCSS(gxTemplateContext, view, gxNode)
                }
            }
        }

        private fun bindGridContainerCSS(
            gxTemplateContext: GXTemplateContext,
            view: View,
            gxNode: GXNode
        ) {
            gxNode.templateNode.finalGridConfig?.let {
                view.setGridContainerDirection(
                    gxTemplateContext, it, gxNode.stretchNode.layoutByBind
                )
                view.setGridContainerItemSpacingAndRowSpacing(
                    it.edgeInsets, it.itemSpacing, it.rowSpacing
                )
            }
        }

        private fun bindScrollContainerCSS(
            gxTemplateContext: GXTemplateContext,
            view: View,
            gxNode: GXNode
        ) {
            gxNode.templateNode.finalScrollConfig?.let { scrollConfig ->

                view.setScrollContainerDirection(
                    scrollConfig.direction, gxNode.stretchNode.layoutByBind
                )

                val edgeInsets = scrollConfig.edgeInsets
                val lineSpacing = scrollConfig.itemSpacing
                if (scrollConfig.direction == LinearLayoutManager.HORIZONTAL) {
                    // 设置边距
                    if (edgeInsets.top == 0 && edgeInsets.bottom == 0) {
                        view.setHorizontalScrollContainerLineSpacing(
                            edgeInsets.left, edgeInsets.right, lineSpacing
                        )
                    } else {
                        if (lineSpacing != 0) {
                            view.setHorizontalScrollContainerLineSpacing(lineSpacing)
                        }
                        view.setScrollContainerPadding(edgeInsets)
                    }
                } else {
                    if (lineSpacing != 0) {
                        view.setVerticalScrollContainerLineSpacing(lineSpacing)
                    }
                    view.setScrollContainerPadding(edgeInsets)
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
            adapter.setConfig(gxNode.templateNode.finalSliderConfig)
            container.setConfig(gxNode.templateNode.finalSliderConfig)

            adapter.setData(containerTemplateData)
            container.setPageSize(containerTemplateData.size)

            container.onBindData(templateData)
        }

        private fun bindProgress(
            view: GXIViewBindData,
            gxTemplateNode: GXTemplateNode,
            templateData: JSONObject
        ) {
            val progressView = view as? GXProgressView
            progressView?.setConfig(gxTemplateNode.finalProgressConfig)
            progressView?.onBindData(gxTemplateNode.getData(templateData))
        }
    }
}
