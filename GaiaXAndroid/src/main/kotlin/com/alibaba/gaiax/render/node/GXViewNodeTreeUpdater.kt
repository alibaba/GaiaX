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

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import app.visly.stretch.Size
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.text.GXHighLightUtil
import com.alibaba.gaiax.render.view.*
import com.alibaba.gaiax.render.view.basic.GXIImageView
import com.alibaba.gaiax.render.view.basic.GXText
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.render.view.container.GXContainerViewAdapter
import com.alibaba.gaiax.template.GXCss
import com.alibaba.gaiax.template.GXDataBinding
import com.alibaba.gaiax.template.GXLayer
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * @suppress
 */
class GXViewNodeTreeUpdater(val context: GXTemplateContext) {

    fun build() {
        val rootNode = context.rootNode ?: throw IllegalArgumentException("RootNode is null")
        val templateData =
            context.templateData?.data ?: throw IllegalArgumentException("Data is null")
        val size = Size(context.size.width, context.size.height)

        // 更新布局
        updateNodeTreeLayout(rootNode, templateData, size)

        // 如果存在延迟计算文字自适应的情况，需要处理后重新计算
        updateNodeTreeLayoutByDirtyText(rootNode, size)

        // 更新样式
        updateNodeTreeStyle(context, rootNode, templateData)
    }

    private fun updateNodeTreeLayout(
        rootNode: GXNode,
        templateData: JSONObject,
        size: Size<Float?>
    ) {
        // 更新布局
        updateNodeTreeLayout(context, rootNode, templateData)

        // 计算布局
        if (context.isDirty) {
            GXNodeUtils.computeNodeTreeByBindData(rootNode, size)
        }
    }

    private fun updateNodeTreeLayoutByDirtyText(rootNode: GXNode, size: Size<Float?>) {
        if (context.dirtyText?.isNotEmpty() == true) {
            var isTextDirty = false
            context.dirtyText?.forEach {
                val result = it.key.updateTextLayoutByFitContent(
                    it.value.templateContext,
                    it.value.currentNode,
                    it.value.currentStretchNode,
                    it.value.data,
                    it.value.stretchStyle
                )
                if (result) {
                    isTextDirty = true
                }
            }
            context.dirtyText?.clear()
            if (isTextDirty) {
                GXNodeUtils.computeNodeTreeByBindData(rootNode, size)
            }
        }
    }

    private fun updateNodeTreeLayout(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        node.stretchNode.reset()
        node.templateNode.reset()

        if (node.isNestRoot) {
            updateNestNodeLayout(context, node, templateData)
        } else if (node.isContainerType()) {
            updateContainerNodeLayout(context, node, templateData)
        } else {
            updateNormalNodeLayout(context, node, templateData)
        }
    }

    private fun updateNestNodeLayout(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        // 容器节点
        if (node.templateNode.isContainerType()) {
            updateNestContainerNodeLayout(context, node, templateData)
        }
        // 嵌套的子节点
        else {
            updateNestNormalNodeLayout(context, node, templateData)
        }
    }

    private fun updateContainerNodeLayout(
        gxTemplateContext: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        node.stretchNode.initFinal()
        node.templateNode.initFinal(
            gxTemplateContext,
            visualTemplateData = null,
            nodeTemplateData = templateData
        )

        updateNodeLayout(gxTemplateContext, node, templateData)
    }

    private fun updateNormalNodeLayout(
        gxTemplateContext: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        node.stretchNode.initFinal()
        node.templateNode.initFinal(
            gxTemplateContext,
            visualTemplateData = null,
            nodeTemplateData = templateData
        )

        updateNodeLayout(gxTemplateContext, node, templateData)

        node.children?.forEach { childNode ->
            // 使用原有数据为数据源
            updateNodeTreeLayout(gxTemplateContext, childNode, templateData)
        }
    }

    private fun updateNodeLayout(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        // 更新节点布局
        nodeNodeLayout(context, node, templateData)
    }

    private fun updateNestContainerNodeLayout(
        gxTemplateContext: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        val visualDataBinding = node.templateNode.visualTemplateNode?.dataBinding
        val dataBinding = node.templateNode.dataBinding

        // 虚拟节点所在的模板，需要传递数据给下一层子模板
        // 若没有数据需要传递，那么给下一层子模板传递一个空数据源
        // 此处，双端已协商一致

        // 对于普通嵌套模板，传递给下一层的数据只能是JSONObject
        var valueData = visualDataBinding?.getDataValue(templateData)
        if (valueData is JSONArray) {

            if (GXRegisterCenter.instance.processCompatible?.isCompatibleContainerDataPassSequence() == true) {
                // 是否兼容处理先$nodes取数组，再去$$的情况

                val tmp = visualDataBinding?.value
                visualDataBinding?.value = dataBinding?.value
                dataBinding?.value = tmp

                dataBinding?.reset()
                visualDataBinding?.reset()

                valueData = visualDataBinding?.getDataValue(templateData)
            } else {
                throw IllegalArgumentException("update nest container need a JSONObject, but the result is a JSONArray")
            }
        }
        val childTemplateData = (valueData as? JSONObject) ?: JSONObject()

        node.stretchNode.initFinal()
        node.templateNode.initFinal(
            gxTemplateContext,
            visualTemplateData = templateData,
            nodeTemplateData = childTemplateData
        )

        updateNodeLayout(gxTemplateContext, node, childTemplateData)
    }

    private fun updateNestNormalNodeLayout(
        gxTemplateContext: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        val visualDataBinding = node.templateNode.visualTemplateNode?.dataBinding

        // 虚拟节点所在的模板，需要传递数据给下一层子模板
        // 若没有数据需要传递，那么给下一层子模板传递一个空数据源
        // 此处，双端已协商一致

        // 对于普通嵌套模板，传递给下一层的数据只能是JSONObject
        val childTemplateData =
            (visualDataBinding?.getDataValue(templateData) as? JSONObject) ?: JSONObject()

        node.stretchNode.initFinal()
        node.templateNode.initFinal(
            gxTemplateContext,
            visualTemplateData = templateData,
            nodeTemplateData = childTemplateData
        )

        updateNodeLayout(gxTemplateContext, node, childTemplateData)

        node.children?.forEach { childNode ->
            // 使用虚拟节点取值后的数据作为数据源
            updateNodeTreeLayout(gxTemplateContext, childNode, childTemplateData)
        }
    }

    private fun updateNodeTreeStyle(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        if (node.isNestRoot) {
            updateNestNodeStyle(context, node, templateData)
        } else if (node.isContainerType()) {
            updateContainerNodeStyle(context, node, templateData)
        } else {
            updateNormalNodeStyle(context, node, templateData)
        }
    }

    private fun updateNestNodeStyle(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        // 容器节点
        if (node.templateNode.isContainerType()) {
            updateNestContainerNodeStyle(context, node, templateData)
        }
        // 嵌套的子节点
        else {
            updateNestNormalNodeStyle(context, node, templateData)
        }
    }

    private fun updateContainerNodeStyle(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        updateNodeStyle(context, node, templateData)
    }

    private fun updateNestContainerNodeStyle(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        val visualDataBinding = node.templateNode.visualTemplateNode?.dataBinding
        val dataBinding = node.templateNode.dataBinding

        // 虚拟节点所在的模板，需要传递数据给下一层子模板
        // 若没有数据需要传递，那么给下一层子模板传递一个空数据源
        // 此处，双端已协商一致

        // 对于普通嵌套模板，传递给下一层的数据只能是JSONObject
        val valueData = visualDataBinding?.getDataValue(templateData)
        val childTemplateData = (valueData as? JSONObject) ?: JSONObject()

        updateNodeStyle(context, node, childTemplateData)
    }

    private fun updateNestNormalNodeStyle(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        updateNodeStyle(context, node, templateData)

        node.children?.forEach { childNode ->
            // 使用原有数据为数据源
            updateNodeTreeStyle(context, childNode, templateData)
        }
    }

    private fun updateNormalNodeStyle(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        updateNodeStyle(context, node, templateData)

        node.children?.forEach { childNode ->
            // 使用原有数据为数据源
            updateNodeTreeStyle(context, childNode, templateData)
        }
    }

    private fun updateNodeStyle(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        // 更新视图样式
        nodeViewCss(context, node)

        // 更新视图数据
        nodeViewData(context, node, templateData)

        // 更新视图埋点
        nodeViewTrack(context, node, templateData)

        // 更新视图事件
        nodeViewEvent(context, node, templateData)

        // 更新视图动画
        nodeViewAnimation(context, node, templateData)
    }

    private fun nodeViewAnimation(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        node.templateNode.animationBinding?.executeAnimation(context, node, templateData)
    }

    private fun nodeNodeLayout(context: GXTemplateContext, node: GXNode, templateData: JSONObject) {
        // 容器节点
        if (node.isContainerType()) {
            val isDirty = node.stretchNode.updateContainerLayout(
                context,
                node.templateNode,
                node,
                templateData
            )
            if (isDirty) {
                context.isDirty = isDirty
            }
        }
        // 普通节点
        else {
            val isDirty =
                node.stretchNode.updateNormalLayout(context, node.templateNode, templateData)
            if (isDirty) {
                context.isDirty = isDirty
            }
        }
    }

    private fun nodeViewCss(context: GXTemplateContext, node: GXNode) {
        val view = node.viewRef?.get() ?: return
        val gxCss = node.templateNode.finalCss ?: return

        if (view is GXText && (node.isTextType() || node.isRichTextType() || node.isIconFontType())) {
            view.setTextStyle(gxCss)
        } else if (view is GXIImageView && node.isImageType()) {
            view.setImageStyle(gxCss)
        } else if (node.isContainerType()) {
            bindContainerViewCss(context, gxCss, view, node)
        }
        bindCommonViewCss(view, gxCss, node)
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

        val targetView = gxNode.viewRef?.get()

        // 滚动事件
        if (targetView is RecyclerView) {
            if (gxTemplateContext.templateData?.eventListener != null) {
                targetView.clearOnScrollListeners()
                targetView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        gxTemplateContext.templateData?.eventListener?.onScrollEvent(
                            GXTemplateEngine.GXScroll().apply {
                                this.type = "onScrolled"
                                this.view = recyclerView
                                this.dx = dx
                                this.dy = dy
                            })
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        gxTemplateContext.templateData?.eventListener?.onScrollEvent(
                            GXTemplateEngine.GXScroll().apply {
                                this.type = "onScrollStateChanged"
                                this.view = recyclerView
                                this.state = newState
                            })
                    }
                })
            }
        }

        // 数据绑定事件
        if (gxNode.templateNode.eventBinding != null) {

            // 创建事件处理器
            gxNode.event =
                gxNode.event ?: GXRegisterCenter.instance.processNodeEvent?.create()
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

    private fun nodeViewTrack(context: GXTemplateContext, node: GXNode, templateData: JSONObject) {
        val view = node.viewRef?.get() ?: return
        val templateNode = node.templateNode
        val eventBinding = templateNode.eventBinding ?: return
        val invisible = templateNode.finalCss?.style?.isInvisible() ?: false
        if (invisible) {
            return
        }
        val trackData = eventBinding.event.value(templateData) as? JSONObject ?: return
        context.templateData?.trackListener?.onTrackEvent(GXTemplateEngine.GXTrack().apply {
            this.view = view
            this.trackParams = trackData
            this.nodeId = templateNode.layer.id
            this.templateItem = context.templateItem
            this.index = -1
        })
    }

    private fun nodeViewData(context: GXTemplateContext, node: GXNode, templateData: JSONObject) {
        val view = node.viewRef?.get() ?: return
        if (view !is GXIViewBindData) {
            return
        }

        val css = node.templateNode.css
        val layer = node.templateNode.layer

        val dataBinding = node.templateNode.dataBinding ?: return

        when {
            node.isCustomViewType() -> bindCustom(context, view, dataBinding, templateData)
            node.isTextType() -> bindText(context, view, css, layer, dataBinding, templateData)
            node.isRichTextType() -> bindRichText(
                context,
                view,
                css,
                layer,
                dataBinding,
                templateData
            )
            node.isIconFontType() -> bindIconFont(view, dataBinding, templateData)
            node.isImageType() -> bindImage(view, dataBinding, templateData)
            node.isScrollType() || node.isGridType() -> bindScrollAndGrid(
                context,
                view,
                node,
                dataBinding,
                templateData
            )
            node.isViewType() || node.isGaiaTemplateType() -> bindView(
                view,
                dataBinding,
                templateData
            )
        }
    }

    private fun bindScrollAndGrid(
        context: GXTemplateContext,
        view: View,
        node: GXNode,
        dataBinding: GXDataBinding,
        templateData: JSONObject
    ) {

        // 容器数据源
        var containerTemplateData = dataBinding.getDataValue(templateData) as? JSONArray
        if (containerTemplateData == null) {
            if (GXRegisterCenter.instance.processCompatible?.isPreventContainerDataSourceThrowException() == true) {
                containerTemplateData = JSONArray()
            } else {
                throw IllegalArgumentException("Scroll or Grid must be have a array data source")
            }
        }

        val extendData = dataBinding.getExtendCache()

        val container = view as GXContainer

        val adapter: GXContainerViewAdapter?
        if (container.adapter != null) {
            adapter = container.adapter as GXContainerViewAdapter
        } else {
            adapter = GXContainerViewAdapter(context, node, container)
            container.adapter = adapter
        }

        // scroll item to position
        context.templateData?.scrollIndex?.let { scrollPosition ->
            if (scrollPosition <= 0) {
                val holdingOffset =
                    extendData?.getBooleanValue(GXTemplateKey.GAIAX_DATABINDING_HOLDING_OFFSET)
                        ?: false
                if (holdingOffset) {
                    // no process
                } else {
                    // when again bind data, should be scroll to position 0
                    container.layoutManager?.scrollToPosition(0)
                }
            }
            // if (scrollPosition > 0)
            else {
                container.layoutManager?.scrollToPosition(scrollPosition)
            }
        }

        // forbid item animator
        container.itemAnimator = null

        adapter.setContainerData(containerTemplateData)
        adapter.initFooter()
    }

    private fun bindIconFont(
        view: GXIViewBindData,
        binding: GXDataBinding,
        templateData: JSONObject
    ) {
        val nodeData = binding.getData(templateData)
        if (nodeData != null) {
            view.onBindData(nodeData)
        }
    }

    private fun bindImage(view: GXIViewBindData, binding: GXDataBinding, templateData: JSONObject) {
        val nodeData = binding.getData(templateData)
        if (nodeData != null) {
            view.onBindData(nodeData)
        }
    }

    private fun bindView(view: GXIViewBindData, binding: GXDataBinding, templateData: JSONObject) {
        val nodeData = binding.getData(templateData)
        if (nodeData != null) {
            view.onBindData(nodeData)
        }
    }

    private fun bindRichText(
        context: GXTemplateContext,
        view: GXIViewBindData,
        css: GXCss?,
        layer: GXLayer,
        binding: GXDataBinding,
        templateData: JSONObject
    ) {
        val nodeData = binding.getData(templateData)

        val valueData = nodeData?.get(GXTemplateKey.GAIAX_VALUE)

        // 优先处理高亮逻辑
        if (valueData is String) {
            val result: CharSequence? =
                GXHighLightUtil.getHighLightContent(binding, templateData, valueData)
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
        if (context.templateData?.dataListener != null) {
            val gxTextData = GXTemplateEngine.GXTextData().apply {
                this.text = valueData as? CharSequence
                this.view = view as View
                this.nodeId = layer.id
                this.templateItem = context.templateItem
                this.nodeCss = css
                this.nodeData = nodeData
                this.index = context.indexPosition
                this.extendData = binding.getExtend(templateData)
            }
            val result = context.templateData?.dataListener?.onTextProcess(gxTextData)
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

        if (nodeData != null) {
            view.onBindData(nodeData)
        }
    }

    private fun bindText(
        context: GXTemplateContext,
        view: GXIViewBindData,
        css: GXCss?,
        layer: GXLayer,
        binding: GXDataBinding,
        templateData: JSONObject
    ) {

        val nodeData = binding.getData(templateData)

        if (context.templateData?.dataListener != null) {

            val gxTextData = GXTemplateEngine.GXTextData().apply {
                this.text = nodeData?.get(GXTemplateKey.GAIAX_VALUE)?.toString()
                this.view = view as View
                this.nodeId = layer.id
                this.templateItem = context.templateItem
                this.nodeCss = css
                this.nodeData = nodeData
                this.index = context.indexPosition
                this.extendData = binding.getExtend(templateData)
            }

            context.templateData?.dataListener?.onTextProcess(gxTextData)?.let { result ->
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

        if (nodeData != null) {
            view.onBindData(nodeData)
        }
    }

    private fun bindCustom(
        context: GXTemplateContext,
        view: GXIViewBindData,
        binding: GXDataBinding,
        templateData: JSONObject
    ) {
        binding.getData(templateData)?.let {
            view.onBindData(it)
        }
    }

    private fun bindCommonViewCss(view: View, gxCss: GXCss, node: GXNode) {

        view.setDisplay(gxCss.style.display)

        if (!node.isCustomViewType()) {

            view.setHidden(gxCss.style.display, gxCss.style.hidden)

            view.setOpacity(gxCss.style.opacity)

            view.setOverflow(gxCss.style.overflow)

            view.setBackgroundColorAndBackgroundImageAndRadius(gxCss.style)

            view.setRoundCornerRadiusAndRoundCornerBorder(gxCss.style)
        }
    }

    private fun bindContainerViewCss(
        context: GXTemplateContext,
        gxCss: GXCss,
        view: View,
        node: GXNode
    ) {
        if (node.isContainerType()) {
            if (node.isGridType()) {
                bindGridContainerCSS(context, view, node)
            } else if (node.isScrollType()) {
                bindScrollContainerCSS(context, view, node)
            }
        }
    }

    private fun bindGridContainerCSS(context: GXTemplateContext, view: View, node: GXNode) {
        node.templateNode.finalGridConfig?.let {
            view.setGridContainerDirection(context, it, node.stretchNode.finalLayout)
            view.setGridContainerItemSpacingAndRowSpacing(
                it.edgeInsets,
                it.itemSpacing,
                it.rowSpacing
            )
        }
    }

    private fun bindScrollContainerCSS(context: GXTemplateContext, view: View, node: GXNode) {
        node.templateNode.finalScrollConfig?.let { scrollConfig ->

            view.setScrollContainerDirection(scrollConfig.direction, node.stretchNode.finalLayout)

            val edgeInsets = scrollConfig.edgeInsets
            val lineSpacing = scrollConfig.itemSpacing
            if (scrollConfig.direction == LinearLayoutManager.HORIZONTAL) {
                // 设置边距
                if (edgeInsets.top == 0 && edgeInsets.bottom == 0) {
                    view.setHorizontalScrollContainerLineSpacing(
                        edgeInsets.left,
                        edgeInsets.right,
                        lineSpacing
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
}