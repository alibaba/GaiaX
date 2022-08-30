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
@Suppress("UNUSED_PARAMETER")
data class GXStretchNode(
    val node: Node,
    var layoutByCreate: Layout? = null,
    var layoutByBind: Layout? = null
) {

    fun reset(gxTemplateContext: GXTemplateContext, gxTemplateNode: GXTemplateNode) {
        resetStyle(gxTemplateContext, gxTemplateNode)
        layoutByBind = null
    }

    private fun resetStyle(gxTemplateContext: GXTemplateContext, gxTemplateNode: GXTemplateNode) {
        val stretchStyle = createStretchStyle(gxTemplateContext, gxTemplateNode)
        val oldStyle = node.getStyle()
        oldStyle.free()
        this.node.setStyle(stretchStyle)
        this.node.markDirty()
    }

    fun initFinal() {
    }

    override fun toString(): String {
        return "GXStretchNode(node=$node, layout=$layoutByCreate)"
    }

    fun free() {
        layoutByCreate = null
        node.free()
    }

    companion object {

        fun createNode(
            gxTemplateContext: GXTemplateContext,
            gxTemplateNode: GXTemplateNode,
            id: String,
            idPath: String
        ): GXStretchNode {
            val stretchStyle = createStretchStyle(gxTemplateContext, gxTemplateNode)
            val stretchNode = Node(id, idPath, stretchStyle, mutableListOf())
            return GXStretchNode(stretchNode, null)
        }

        private fun createStretchStyle(
            gxTemplateContext: GXTemplateContext,
            gxTemplateNode: GXTemplateNode
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
            gxTemplateContext: GXTemplateContext,
            flexBox: GXFlexBox,
            style: Style
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

            flexBox.position?.let { style.position = it }

            flexBox.margin?.let { style.margin = it }

            flexBox.padding?.let { style.padding = it }

            flexBox.border?.let { style.border = it }

            flexBox.flexGrow?.let {
                style.flexGrow = it
                gxTemplateContext.isFlexGrowLayout = true
            }

            flexBox.flexShrink?.let { style.flexShrink = it }

            flexBox.size?.let { style.size = Size(it.width, it.height) }

            flexBox.minSize?.let { style.minSize = Size(it.width, it.height) }

            flexBox.maxSize?.let { style.maxSize = Size(it.width, it.height) }
        }
    }
}