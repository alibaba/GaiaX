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
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.template.GXFlexBox

/**
 * @suppress
 */
@Suppress("UNUSED_PARAMETER")
data class GXStretchNode(
    var node: Node? = null, var layoutByCreate: Layout? = null, var layoutByBind: Layout? = null
) {

    fun reset(gxTemplateContext: GXTemplateContext, gxNode: GXNode) {
        resetStyle(gxTemplateContext, gxNode)
        layoutByBind = null
    }

    private fun resetStyle(gxTemplateContext: GXTemplateContext, gxNode: GXNode) {
        val stretchStyle = createStretchStyle(gxTemplateContext, gxNode.templateNode)
        if (this.node == null) {
            val stretchNode = Node(gxNode.id, stretchStyle, mutableListOf())
            this.node = stretchNode
            this.node?.let {
                gxNode.parentNode?.stretchNode?.node?.addChild(it)
            }
        } else {
            val oldStyle = this.node?.getStyle()
            this.node?.setStyle(stretchStyle)
            this.node?.markDirty()
            oldStyle?.safeFree()
        }
    }

    fun initFinal() {
    }

    fun free() {
        node?.safeFree()
    }

    override fun toString(): String {
        return "GXStretchNode(node=$node, layoutByBind=$layoutByBind)"
    }

    companion object {

        fun createEmptyNode(
            gxTemplateContext: GXTemplateContext,
            templateNode: GXTemplateNode,
            id: String
        ): GXStretchNode {
            return GXStretchNode()
        }

        fun createNode(
            gxTemplateContext: GXTemplateContext,
            gxTemplateNode: GXTemplateNode,
            id: String
        ): GXStretchNode {
            val stretchStyle = createStretchStyle(gxTemplateContext, gxTemplateNode)
            val stretchNode = Node(id, stretchStyle, mutableListOf())
            return GXStretchNode(stretchNode, null)
        }

        private fun createStretchStyle(
            gxTemplateContext: GXTemplateContext, gxTemplateNode: GXTemplateNode
        ): Style {
            val style = Style()

            // Set Self FlexBox Property
            val flexBox = gxTemplateNode.css.flexBox
            updateStyle(gxTemplateContext, flexBox, style)

            // Override Property From Parent FlexBox
            val visualTemplateNodeFlexBox = gxTemplateNode.visualTemplateNode?.css?.flexBox
            visualTemplateNodeFlexBox?.let { updateStyle(gxTemplateContext, it, style) }

            style.init()

            return style
        }

        private fun updateStyle(
            gxTemplateContext: GXTemplateContext, flexBox: GXFlexBox, style: Style
        ) {
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

            flexBox.positionForStyle?.let { style.position = it }

            flexBox.marginForStyle?.let { style.margin = it }

            flexBox.paddingForStyle?.let { style.padding = it }

            flexBox.borderForStyle?.let { style.border = it }

            flexBox.flexGrow?.let {
                style.flexGrow = it
                gxTemplateContext.isFlexGrowLayout = true
            }

            flexBox.flexShrink?.let { style.flexShrink = it }

            flexBox.sizeForStyle?.let { style.size = Size(it.width, it.height) }

            flexBox.minSizeForStyle?.let { style.minSize = Size(it.width, it.height) }

            flexBox.maxSizeForStyle?.let { style.maxSize = Size(it.width, it.height) }
        }


    }
}