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
import com.alibaba.gaiax.render.node.GXStretchNode
import com.alibaba.gaiax.render.node.GXTemplateNode
import com.alibaba.gaiax.render.view.setFontLines
import com.alibaba.gaiax.template.GXCss
import com.alibaba.gaiax.template.GXTemplateKey
import java.lang.ref.SoftReference

/**
 * @suppress
 */
object GXFitContentUtils {

    /**
     * 文字自适应计算逻辑，应该有如下逻辑效果：
     *  1. 自适应，行数=1，有宽度
     *     宽度需要自适应，高度不做处理
     *          文字自适应后的宽度以实际计算宽度为准，不应超过原有宽度设置值。
     *          例如：
     *              输入：lines=1;width=50px;fitcontent=true;text=哈哈(计算后宽度20px)
     *              输出：width=20px
     *
     *              输入：lines=1;width=100px;fitcontent=true;text=哈哈哈哈哈哈哈哈(计算后宽度60px)
     *              输出：width=50px
     *
     *  2. 自适应，行数>1 or 行数=0, 有宽度
     *     高度需要自适应，宽度不做处理
     *          文字自适应的后的高度以实际计算为准，不受到原有高度的影响
     *          例如：
     *              输入：lines=3;width=20px;fitcontent=true;text=哈哈哈哈哈哈哈哈哈哈哈哈(计算后高度为60px)
     *              输出：width=20px; height=60px
     *
     *              输入：lines=0;width=20px;fitcontent=true;text=哈哈哈哈哈哈哈哈哈哈哈哈(计算后高度为60px)
     *              输出：width=20px; height=60px
     *
     *
     *  前置处理与后置处理：
     *     1. 如果是flexGrow计算出的，那么文字自适应时，需要将flexGrow设置成0
     *     2. 在处理文字自适应时，应先计算过一次布局
     *
     */
    fun fitContent(
        templateContext: GXTemplateContext,
        gxTemplateNode: GXTemplateNode,
        gxStretchNode: GXStretchNode,
        templateData: JSONObject
    ): Size<Dimension>? {
        if (!gxTemplateNode.isTextType() && !gxTemplateNode.isRichTextType()) {
            return null
        }

        val androidContext = templateContext.context
        val nodeId = gxTemplateNode.getNodeId()
        val nodeDataBinding = gxTemplateNode.dataBinding ?: return null
        val finalCss = gxTemplateNode.finalCss ?: return null
        val nodeFlexBox = finalCss.flexBox
        val nodeStyle = finalCss.style
        val nodeLayout = gxStretchNode.layoutByBind
            ?: gxStretchNode.layoutByCreate
            ?: return null

        val textView = GXMeasureViewPool.obtain(androidContext)

        textView.setTextStyle(finalCss)

        val textContent = getMeasureContent(
            templateContext,
            nodeId,
            textView,
            finalCss,
            gxTemplateNode,
            templateData
        )

        if (textContent == null) {
            GXMeasureViewPool.release(SoftReference(textView))
            return null
        }

        textView.text = textContent

        val fontLines: Int? = nodeStyle.fontLines

        var result: Size<Dimension>? = null

        if ((fontLines == null || fontLines == 1)) {
            // 单行状态下，需要定高求宽

            // 在某些机型上使用maxLine=1时，会导致中英混合、中英数字混合等一些文字无法显示
            textView.setSingleLine(true)

            // 计算宽高
            textView.measure(0, 0)

            val measuredWidth = textView.measuredWidth.toFloat()
            val measuredHeight = textView.measuredHeight.toFloat()

            val nodeWidth = nodeLayout.width
            val nodeHeight = nodeLayout.height

            var textHeight = nodeHeight
            if (textHeight == 0F) {
                // 兼容处理， 如果未设置高度，那么使用文本默认的高度
                textHeight = measuredHeight
            }

            // 修复template_text_fitcontent_lines_null_width_null_height_fixed_padding_left_padding_right
            val textLeftAndRightPadding =
                (nodeFlexBox.padding?.start?.value ?: 0F) + (nodeFlexBox.padding?.end?.value ?: 0F)

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

            textView.setFontLines(fontLines)

            val nodeWidth = nodeLayout.width

            if (nodeWidth == 0F) {
                if (GXRegisterCenter.instance.extensionCompatibility?.isPreventFitContentThrowException() == true) {
                    result = null
                } else {
                    throw IllegalArgumentException("If lines = 0 or lines > 1, you must set text width")
                }
            } else if (nodeWidth > 0) {
                val widthSpec = View.MeasureSpec.makeMeasureSpec(
                    nodeWidth.toInt(),
                    View.MeasureSpec.AT_MOST
                )
                textView.measure(widthSpec, 0)
                result = Size(
                    Dimension.Points(nodeWidth),
                    Dimension.Points(textView.measuredHeight.toFloat())
                )
            }
        } else if (fontLines > 1) {
            // 多行状态下，需要定宽求高

            textView.setFontLines(fontLines)

            val nodeWidth = nodeLayout.width

            if (nodeWidth == 0F) {
                if (GXRegisterCenter.instance.extensionCompatibility?.isPreventFitContentThrowException() == true) {
                    result = null
                } else {
                    throw IllegalArgumentException("If lines = 0 or lines > 1, you must set text width")
                }
            } else if (nodeWidth > 0) {
                val widthSpec = View.MeasureSpec.makeMeasureSpec(
                    nodeWidth.toInt(),
                    View.MeasureSpec.AT_MOST
                )
                textView.measure(widthSpec, 0)
                result = Size(
                    Dimension.Points(textView.measuredWidth.toFloat()),
                    Dimension.Points(textView.measuredHeight.toFloat())
                )
            }
        }

        GXMeasureViewPool.release(SoftReference(textView))

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