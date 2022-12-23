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

package com.alibaba.gaiax.render.node.text

import android.view.View
import app.visly.stretch.Dimension
import app.visly.stretch.Size
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.render.node.GXStretchNode
import com.alibaba.gaiax.render.node.GXTemplateNode
import com.alibaba.gaiax.render.view.setFontLines
import com.alibaba.gaiax.template.GXCss
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * @suppress
 */
object GXFitContentUtils {

    /**
     * 文字自适应计算逻辑，应该有如下逻辑效果：
     *  1. 自适应，行数=1，有宽度
     *     宽度需要自适应，高度不做处理
     *          文字自适应后的宽度以实际计算宽度为准，不应超过原有宽度设置值。
     *  2. 自适应，行数>1 or 行数=0, 有宽度
     *     高度需要自适应，宽度不做处理
     *          文字自适应的后的高度以实际计算为准，不受到原有高度的影响
     *
     *  前置处理与后置处理：
     *     1. 如果是flexGrow计算出的，那么文字自适应时，需要将flexGrow设置成0
     *     2. 在处理文字自适应时，应先计算过一次布局
     */
    fun fitContent(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        gxTemplateNode: GXTemplateNode,
        gxStretchNode: GXStretchNode,
        templateData: JSONObject
    ): Size<Dimension>? {

        if (!gxTemplateNode.isTextType() && !gxTemplateNode.isRichTextType()) {
            return null
        }

        if (!gxNode.isNodeVisibleInTree()) {
            return null
        }

        val androidContext = gxTemplateContext.context
        val nodeId = gxTemplateNode.getNodeId()
        val nodeDataBinding = gxTemplateNode.dataBinding ?: return null
        val finalCss = gxTemplateNode.finalCss ?: return null
        val finalFlexBox = finalCss.flexBox
        val finalStyle = finalCss.style
        val nodeLayout = gxStretchNode.layoutByBind ?: gxStretchNode.layoutByCreate ?: return null

        val gxCacheText = GXMeasureViewPool.obtain(androidContext)

        gxCacheText.setTextStyle(finalCss)

        val textContent = getMeasureContent(
            gxTemplateContext, nodeId, gxCacheText, finalCss, gxTemplateNode, templateData
        )

        if (textContent == null) {
            GXMeasureViewPool.release(gxCacheText)
            return null
        }

        gxCacheText.text = textContent

        val fontLines: Int? = finalStyle.fontLines

        var result: Size<Dimension>? = null

        var nodeWidth = nodeLayout.width

        var nodeHeight = nodeLayout.height

        // FIX: 如果databinding中引发了flex的改动，那么nodeLayout的结果可能不准确
        val finalFlexBoxHeight = finalFlexBox.size?.height
        if (gxStretchNode.layoutByBind == null && finalFlexBoxHeight is Dimension.Points && nodeHeight != finalFlexBoxHeight.value) {
            nodeHeight = finalFlexBoxHeight.value
        }

        if ((fontLines == null || fontLines == 1)) {
            // 单行状态下，需要定高求宽

            // 在某些机型上使用maxLine=1时，会导致中英混合、中英数字混合等一些文字无法显示
            gxCacheText.setSingleLine(true)

            // 计算宽高
            gxCacheText.measure(0, 0)

            var measuredWidth = gxCacheText.measuredWidth.toFloat()
            val measuredHeight = gxCacheText.measuredHeight.toFloat()

            val isUndefineSize =
                finalCss.flexBox.size?.width == null || finalCss.flexBox.size.width is Dimension.Auto || finalCss.flexBox.size.width is Dimension.Undefined

            val isDefineMinSize =
                finalCss.flexBox.minSize != null && (finalCss.flexBox.minSize.width !is Dimension.Auto || finalCss.flexBox.minSize.width !is Dimension.Undefined)

            // fix: 如果没有定义宽度，但是定义了最小宽度，那么需要做一下修正
            if (isUndefineSize && isDefineMinSize) {
                // 如果测量的宽度，大于最小宽度，那么清除nodeWidth
                if (measuredWidth >= nodeWidth) {
                    nodeWidth = 0F
                }
                // 如果测量的宽度，小于做小宽度，那么使用nodeWidth参与计算
                else {
                    measuredWidth = nodeWidth
                    nodeWidth = 0F
                }
            }

            var textHeight = nodeHeight

            // FIXED: template_text_fitcontent_lines_null_width_fixed_height_null_padding_top_padding_bottom
            val textTopAndBottomPadding =
                (finalFlexBox.padding?.top?.value ?: 0F) + (finalFlexBox.padding?.bottom?.value
                    ?: 0F)
            // 高度等于上下padding之和，也认为是0高度
            if (textHeight == textTopAndBottomPadding) {
                textHeight = 0F
            }

            if (textHeight == 0F) {
                // 兼容处理， 如果未设置高度，那么使用文本默认的高度
                textHeight = measuredHeight
            }

            // FIXED: template_text_fitcontent_lines_null_width_null_height_fixed_padding_left_padding_right
            val textLeftAndRightPadding =
                (finalFlexBox.padding?.start?.value ?: 0F) + (finalFlexBox.padding?.end?.value
                    ?: 0F)

            result = when {
                // 没有设置宽度
                nodeWidth == 0F -> {
                    Size(Dimension.Points(measuredWidth), Dimension.Points(textHeight))
                }
                // 宽度等于左右padding之和，也认为是0宽度
                nodeWidth == textLeftAndRightPadding -> {
                    Size(Dimension.Points(measuredWidth), Dimension.Points(textHeight))
                }
                measuredWidth >= nodeWidth -> {
                    Size(Dimension.Points(nodeWidth), Dimension.Points(textHeight))
                }
                else -> {
                    Size(Dimension.Points(measuredWidth), Dimension.Points(textHeight))
                }
            }
        } else if (fontLines == 0) {
            // 多行状态下，需要定宽求高

            gxCacheText.setFontLines(fontLines)

            if (nodeWidth == 0F) {
                if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isPreventFitContentThrowException == true) {
                    result = null
                } else {
                    throw IllegalArgumentException("If lines = 0 or lines > 1, you must set text width")
                }
            } else if (nodeWidth > 0) {
                val widthSpec = View.MeasureSpec.makeMeasureSpec(
                    nodeWidth.toInt(), View.MeasureSpec.AT_MOST
                )
                gxCacheText.measure(widthSpec, 0)
                result = Size(
                    Dimension.Points(nodeWidth),
                    Dimension.Points(gxCacheText.measuredHeight.toFloat())
                )
            }
        } else if (fontLines > 1) {
            // 多行状态下，需要定宽求高

            gxCacheText.setFontLines(fontLines)

            if (nodeWidth == 0F) {
                if (GXRegisterCenter.instance.extensionCompatibilityConfig?.isPreventFitContentThrowException == true) {
                    result = null
                } else {
                    throw IllegalArgumentException("If lines = 0 or lines > 1, you must set text width")
                }
            } else if (nodeWidth > 0) {
                val widthSpec = View.MeasureSpec.makeMeasureSpec(
                    nodeWidth.toInt(), View.MeasureSpec.AT_MOST
                )
                gxCacheText.measure(widthSpec, 0)
                result = Size(
                    Dimension.Points(gxCacheText.measuredWidth.toFloat()),
                    Dimension.Points(gxCacheText.measuredHeight.toFloat())
                )
            }
        }

        GXMeasureViewPool.release(gxCacheText)

        return result
    }

    /**
     * 获取待测量的文字内容
     */
    private fun getMeasureContent(
        gxTemplateContext: GXTemplateContext,
        nodeId: String,
        view: View,
        css: GXCss,
        gxTemplateNode: GXTemplateNode,
        templateData: JSONObject
    ): CharSequence? {

        val data = gxTemplateNode.getData(templateData)?.get(GXTemplateKey.GAIAX_VALUE)

        // 高亮内容
        if (data is String) {
            val highLightContent =
                GXHighLightUtil.getHighLightContent(view, gxTemplateNode, templateData, data)
            if (highLightContent != null) {
                return highLightContent
            }
        }

        // 管道内容
        val pipelineData =
            getTextData(gxTemplateContext, nodeId, view, css, gxTemplateNode, templateData)
        if (pipelineData != null) {
            if (pipelineData is CharSequence) {
                return pipelineData
            }
            return pipelineData.toString()
        }

        // 普通内容
        if (data != null) {
            return data.toString()
        }

        return null
    }

    private fun getTextData(
        gxTemplateContext: GXTemplateContext,
        id: String,
        view: View,
        css: GXCss,
        gxTemplateNode: GXTemplateNode,
        templateData: JSONObject
    ): Any? {
        if (gxTemplateContext.templateData?.dataListener != null) {
            val nodeData = gxTemplateNode.getData(templateData)
            val gxTextData = GXTemplateEngine.GXTextData().apply {
                this.text = nodeData?.get(GXTemplateKey.GAIAX_VALUE)?.toString()
                this.view = view
                this.nodeId = id
                this.templateItem = gxTemplateContext.templateItem
                this.nodeCss = css
                this.nodeData = nodeData
                this.index = gxTemplateContext.indexPosition
                this.extendData = gxTemplateNode.getExtend(templateData)
            }
            gxTemplateContext.templateData?.dataListener?.onTextProcess(gxTextData)?.let { result ->
                return result
            }
        }
        return null
    }
}