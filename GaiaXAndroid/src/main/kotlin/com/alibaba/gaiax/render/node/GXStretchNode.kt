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

import app.visly.stretch.*
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.text.GXDirtyText
import com.alibaba.gaiax.render.node.text.GXFitContentUtils
import com.alibaba.gaiax.template.GXFlexBox
import com.alibaba.gaiax.template.GXStyle
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.template.utils.GXTemplateUtils

/**
 * @suppress
 */
data class GXStretchNode(
    val node: Node,
    var layoutByCreate: Layout? = null,
    var layoutByBind: Layout? = null
) {

    fun reset(gxTemplateNode: GXTemplateNode) {
        resetStyle(gxTemplateNode)
        layoutByBind = null
    }

    private fun resetStyle(gxTemplateNode: GXTemplateNode) {
        val stretchStyle = createStretchStyle(gxTemplateNode)
        val oldStyle = node.getStyle()
        oldStyle.free()
        this.node.setStyle(stretchStyle)
        this.node.markDirty()
    }

    fun initFinal() {
    }

    fun updateContainerLayout(
        gxTemplateContext: GXTemplateContext,
        gxTemplateNode: GXTemplateNode,
        gxNode: GXNode,
        templateData: JSONObject
    ): Boolean {

        //  对于容器嵌套模板，传递给下一层的数据只能是JSONArray
        val containerTemplateData =
            (gxNode.templateNode.getDataValue(templateData) as? JSONArray) ?: JSONArray()

        val style = this.node.getStyle()

        var result = false
        val finalCss = gxNode.templateNode.finalCss
        val finalScrollConfig = gxNode.templateNode.finalScrollConfig
        val finalGridConfig = gxNode.templateNode.finalGridConfig
        val finalFlexBox = finalCss?.flexBox
        val finalCssStyle = finalCss?.style

        if (finalFlexBox == null) {
            throw IllegalArgumentException("final flexbox is null, please check!")
        }

        if (finalCssStyle == null) {
            throw IllegalArgumentException("final css style is null, please check!")
        }

        var isDirty = false

        val height = finalFlexBox.size?.height
        val flexGrow = finalFlexBox.flexGrow

        if (gxNode.isScrollType()) {
            // 当容器节点不是flexGrow时，且容器节点的高度设置，或者是默认，或者是未定义，需要主动计算高度
            var isComputeContainerHeight =
                finalScrollConfig?.isHorizontal == true && flexGrow == null && (height == null || height == Dimension.Auto || height == Dimension.Undefined)

            // 对计算结果进行处理
            GXRegisterCenter
                .instance
                .extensionDynamicProperty
                ?.convert(
                    GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                        GXTemplateKey.GAIAX_CUSTOM_PROPERTY_SCROLL_COMPUTE_CONTAINER_HEIGHT,
                        isComputeContainerHeight
                    ).apply {
                        this.gridConfig = finalGridConfig
                        this.flexBox = finalFlexBox
                    })
                ?.let {
                    isComputeContainerHeight = it as Boolean
                }

            if (isComputeContainerHeight) {
                val containerSize = GXNodeUtils.computeContainerHeightByItemTemplate(
                    gxTemplateContext,
                    gxNode,
                    containerTemplateData
                )
                containerSize?.height?.let {
                    finalFlexBox.size?.height = it
                    isDirty = true
                }
            }
        } else if (gxNode.isGridType()) {

            var isComputeContainerHeight =
                finalGridConfig?.isVertical == true && flexGrow == null && (height == null || height == Dimension.Auto || height == Dimension.Undefined)

            // 对计算结果进行处理
            GXRegisterCenter
                .instance
                .extensionDynamicProperty
                ?.convert(
                    GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                        GXTemplateKey.GAIAX_CUSTOM_PROPERTY_GRID_COMPUTE_CONTAINER_HEIGHT,
                        isComputeContainerHeight
                    ).apply {
                        this.gridConfig = finalGridConfig
                        this.flexBox = finalFlexBox
                    })
                ?.let {
                    isComputeContainerHeight = it as Boolean
                }

            // 当容器节点不是flexGrow时，且容器节点的高度设置，或者是默认，或者是未定义，需要主动计算高度
            if (isComputeContainerHeight) {
                val containerSize = GXNodeUtils.computeContainerHeightByItemTemplate(
                    gxTemplateContext,
                    gxNode,
                    containerTemplateData
                )
                containerSize?.height?.let {
                    finalFlexBox.size?.height = it
                    isDirty = true
                }
            }
        }

        updateLayoutByFlexBox(finalCssStyle, finalFlexBox, style)?.let {
            isDirty = it
        }

        if (isDirty) {
            style.free()
            style.init()
            this.node.setStyle(style)
            this.node.markDirty()
            result = true
        }

        return result
    }

    fun updateNormalLayout(
        gxTemplateContext: GXTemplateContext,
        gxTemplateNode: GXTemplateNode,
        templateData: JSONObject
    ): Boolean {

        val style = this.node.getStyle()

        var result = false
        var isDirty = false

        val finalCss = gxTemplateNode.finalCss
        val finalFlexBox = finalCss?.flexBox
        val finalCssStyle = finalCss?.style

        if (finalFlexBox == null) {
            throw IllegalArgumentException("final flexbox is null, please check!")
        }

        if (finalCssStyle == null) {
            throw IllegalArgumentException("final css style is null, please check!")
        }

        updateLayoutByFlexBox(finalCssStyle, finalFlexBox, style)?.let {
            isDirty = it
        }

        updateLayoutByCssStyle(
            gxTemplateContext,
            finalCssStyle,
            style,
            gxTemplateNode,
            this,
            templateData
        )?.let {
            isDirty = it
        }

        if (isDirty) {
            style.free()
            style.init()
            this.node.setStyle(style)
            this.node.markDirty()
            result = true
        }

        return result
    }

    private fun updateLayoutByFlexBox(
        gxCssStyle: GXStyle,
        gxFlexBox: GXFlexBox,
        style: Style
    ): Boolean? {
        var isDirty: Boolean? = null
        gxFlexBox.display?.let {
            style.display = it
            isDirty = true
        }

        gxFlexBox.aspectRatio?.let {
            style.aspectRatio = it
            isDirty = true
        }

        gxFlexBox.direction?.let {
            style.direction = it
            isDirty = true
        }

        gxFlexBox.flexDirection?.let {
            style.flexDirection = it
            isDirty = true
        }

        gxFlexBox.flexWrap?.let {
            style.flexWrap = it
            isDirty = true
        }

        gxFlexBox.overflow?.let {
            style.overflow = it
            isDirty = true
        }

        gxFlexBox.alignItems?.let {
            style.alignItems = it
            isDirty = true
        }

        gxFlexBox.alignSelf?.let {
            style.alignSelf = it
            isDirty = true
        }

        gxFlexBox.alignContent?.let {
            style.alignContent = it
            isDirty = true
        }

        gxFlexBox.justifyContent?.let {
            style.justifyContent = it
            isDirty = true
        }

        gxFlexBox.positionType?.let {
            style.positionType = it
            isDirty = true
        }

        // 仅在绝对布局下，才能更新position的数据
        if (style.positionType == PositionType.Absolute) {
            gxFlexBox.position?.let {
                GXTemplateUtils.updateDimension(it, style.position)
                isDirty = true
            }
        }

        gxFlexBox.margin?.let {
            GXTemplateUtils.updateDimension(it, style.margin)
            isDirty = true
        }

        gxFlexBox.padding?.let {
            GXTemplateUtils.updateDimension(it, style.padding)
            isDirty = true
        }

        gxFlexBox.border?.let {
            GXTemplateUtils.updateDimension(it, style.border)
            isDirty = true
        }

        gxFlexBox.flexGrow?.let {
            style.flexGrow = it
            isDirty = true
        }

        gxFlexBox.flexShrink?.let {
            style.flexShrink = it
            isDirty = true
        }

        gxFlexBox.size?.let {
            GXTemplateUtils.updateSize(it, style.size)
            GXRegisterCenter.instance
                .extensionDynamicProperty
                ?.convert(
                    GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                        GXTemplateKey.FLEXBOX_SIZE,
                        style.size
                    ).apply {
                        this.cssStyle = gxCssStyle
                    })
            isDirty = true
        }

        gxFlexBox.minSize?.let {
            GXTemplateUtils.updateSize(it, style.minSize)
            GXRegisterCenter.instance
                .extensionDynamicProperty
                ?.convert(
                    GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                        GXTemplateKey.FLEXBOX_MIN_SIZE,
                        style.minSize
                    ).apply {
                        this.cssStyle = gxCssStyle
                    })
            isDirty = true
        }

        gxFlexBox.maxSize?.let {
            GXTemplateUtils.updateSize(it, style.maxSize)
            GXRegisterCenter.instance
                .extensionDynamicProperty
                ?.convert(
                    GXRegisterCenter.GXIExtensionDynamicProperty.GXParams(
                        GXTemplateKey.FLEXBOX_MAX_SIZE,
                        style.maxSize
                    ).apply {
                        this.cssStyle = gxCssStyle
                    })
            isDirty = true
        }

        return isDirty
    }

    private fun updateLayoutByCssStyle(
        gxTemplateContext: GXTemplateContext,
        gxCssStyle: GXStyle,
        stretchStyle: Style,
        gxTemplateNode: GXTemplateNode,
        gxStretchNode: GXStretchNode,
        templateData: JSONObject
    ): Boolean? {

        if (gxCssStyle.fitContent == true) {

            // 如果文字使用了flexGrow，那么fitContent逻辑需要延迟处理
            // 为什么需要延迟处理？因为flexGrow的最终大小还受到了padding、margin等影响, 如果提前计算，会导致结果不正确
            if (stretchStyle.flexGrow != 0F) {
                if (gxTemplateContext.dirtyText == null) {
                    gxTemplateContext.dirtyText = mutableMapOf()
                }
                gxTemplateContext.dirtyText?.put(
                    this,
                    GXDirtyText(
                        gxTemplateContext,
                        gxTemplateNode,
                        gxStretchNode,
                        templateData,
                        stretchStyle
                    )
                )
                return false
            }

            // 处理普通的fitContent逻辑
            return updateLayoutByFitContent(
                gxTemplateContext,
                gxTemplateNode,
                gxStretchNode,
                templateData,
                stretchStyle
            )
        }
        return null
    }

    private fun updateLayoutByFitContent(
        templateContext: GXTemplateContext,
        gxTemplateNode: GXTemplateNode,
        gxStretchNode: GXStretchNode,
        templateData: JSONObject,
        stretchStyle: Style
    ): Boolean? {
        GXFitContentUtils.fitContent(
            templateContext,
            gxTemplateNode,
            gxStretchNode,
            templateData
        )?.let { src ->

            // 自适应之后的宽度，要更新到原有尺寸上
            GXTemplateUtils.updateSize(src, stretchStyle.size)

            // 使用FlexGrow结合FitContent计算出来的宽度，需要将flexGrow重置成0，
            // 否则在Stretch计算的时候会使用FlexGrow计算出的宽度
            if (stretchStyle.flexGrow != 0F) {
                stretchStyle.flexGrow = 0F
            }

            return true
        }

        return null
    }

    override fun toString(): String {
        return "GXStretchNode(node=$node, layout=$layoutByCreate)"
    }

    fun free() {
        layoutByCreate = null
        node.free()
    }

    fun updateTextLayoutByFitContent(
        gxTemplateContext: GXTemplateContext,
        gxTemplateNode: GXTemplateNode,
        gxStretchNode: GXStretchNode,
        templateData: JSONObject,
        stretchStyle: Style
    ): Boolean {

        // 处理fitContent逻辑
        val isDirty = updateLayoutByFitContent(
            gxTemplateContext,
            gxTemplateNode,
            gxStretchNode,
            templateData,
            stretchStyle
        )

        if (isDirty == true) {
            stretchStyle.free()
            stretchStyle.init()
            this.node.setStyle(stretchStyle)
            this.node.markDirty()
            return true
        }

        return false
    }

    companion object {

        fun createNode(gxTemplateNode: GXTemplateNode, id: String, idPath: String): GXStretchNode {
            val stretchStyle = createStretchStyle(gxTemplateNode)
            val stretchNode = Node(id, idPath, stretchStyle, mutableListOf())
            return GXStretchNode(stretchNode, null)
        }

        private fun createStretchStyle(gxTemplateNode: GXTemplateNode): Style {
            val style = Style()

            // Set Self FlexBox Property
            val flexBox = gxTemplateNode.css.flexBox
            updateStyle(flexBox, style)

            // Override Property From Parent FlexBox
            val visualTemplateNodeFlexBox = gxTemplateNode.visualTemplateNode?.css?.flexBox
            visualTemplateNodeFlexBox?.let { updateStyle(it, style) }

            style.init()

            return style
        }

        private fun updateStyle(flexBox: GXFlexBox, style: Style) {
            flexBox.display?.let { style.display = it }

            flexBox.aspectRatio?.let { style.aspectRatio = it }

            flexBox.direction?.let { style.direction = it }

            flexBox.flexDirection?.let { style.flexDirection = it }

            flexBox.flexWrap?.let { style.flexWrap = it }

            flexBox.overflow?.let { style.overflow = it }

            flexBox.alignItems?.let { style.alignItems = it }

            flexBox.alignSelf?.let { style.alignSelf = it }

            flexBox.alignContent?.let { style.alignContent = it }

            flexBox.justifyContent?.let { style.justifyContent = it }

            flexBox.positionType?.let { style.positionType = it }

            flexBox.position?.let { style.position = it }

            flexBox.margin?.let { style.margin = it }

            flexBox.padding?.let { style.padding = it }

            flexBox.border?.let { style.border = it }

            flexBox.flexGrow?.let { style.flexGrow = it }

            flexBox.flexShrink?.let { style.flexShrink = it }

            flexBox.size?.let { style.size = Size(it.width, it.height) }

            flexBox.minSize?.let { style.minSize = Size(it.width, it.height) }

            flexBox.maxSize?.let { style.maxSize = Size(it.width, it.height) }
        }
    }
}