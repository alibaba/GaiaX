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
import app.visly.stretch.Size
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.text.GXHighLightUtil
import com.alibaba.gaiax.render.view.*
import com.alibaba.gaiax.render.view.basic.GXImageView
import com.alibaba.gaiax.render.view.basic.GXText
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.render.view.container.GXContainerViewAdapter
import com.alibaba.gaiax.render.view.container.slider.GXSliderView
import com.alibaba.gaiax.render.view.container.slider.GXSliderViewAdapter
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
        val templateData = context.data ?: throw IllegalArgumentException("Data is null")

        // 更新节点
        updateNodeTree(context, rootNode, templateData)

        // 计算节点信息
        if (context.isDirty) {
            GXNodeUtils.computeNodeTreeByBindData(
                rootNode,
                Size(context.size.width, context.size.height)
            )
        }
    }

    private fun updateNodeTree(context: GXTemplateContext, node: GXNode, templateData: JSONObject) {
        node.stretchNode.reset()
        node.templateNode.reset()

        if (node.isNestRoot) {
            updateNestNode(context, node, templateData)
        } else if (node.isContainerType()) {
            updateContainerNode(context, node, templateData)
        } else {
            updateNormalNode(context, node, templateData)
        }
    }

    private fun updateNestNode(context: GXTemplateContext, node: GXNode, templateData: JSONObject) {
        // 容器节点
        if (node.templateNode.isContainerType()) {
            updateNestContainerNode(context, node, templateData)
        }
        // 嵌套的子节点
        else {
            updateNestNormalNode(context, node, templateData)
        }
    }

    private fun updateContainerNode(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        node.stretchNode.initFinal()
        node.templateNode.initFinal(visualTemplateData = null, nodeTemplateData = templateData)

        updateNode(context, node, templateData)
    }

    private fun updateNestContainerNode(
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
        val childTemplateData =
            (visualDataBinding?.getValueData(templateData) as? JSONObject) ?: JSONObject()

        node.stretchNode.initFinal()
        node.templateNode.initFinal(
            visualTemplateData = templateData,
            nodeTemplateData = childTemplateData
        )

        updateNode(context, node, childTemplateData)
    }

    private fun updateNestNormalNode(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        val visualDataBinding = node.templateNode.visualTemplateNode?.dataBinding

        // 虚拟节点所在的模板，需要传递数据给下一层子模板
        // 若没有数据需要传递，那么给下一层子模板传递一个空数据源
        // 此处，双端已协商一致

        // 对于普通嵌套模板，传递给下一层的数据只能是JSONObject
        val childTemplateData =
            (visualDataBinding?.getValueData(templateData) as? JSONObject) ?: JSONObject()

        node.stretchNode.initFinal()
        node.templateNode.initFinal(
            visualTemplateData = templateData,
            nodeTemplateData = childTemplateData
        )

        updateNode(context, node, childTemplateData)

        node.children?.forEach { childNode ->
            // 使用虚拟节点取值后的数据作为数据源
            updateNodeTree(context, childNode, childTemplateData)
        }
    }

    private fun updateNormalNode(
        context: GXTemplateContext,
        node: GXNode,
        templateData: JSONObject
    ) {
        node.stretchNode.initFinal()
        node.templateNode.initFinal(visualTemplateData = null, nodeTemplateData = templateData)

        updateNode(context, node, templateData)

        node.children?.forEach { childNode ->
            // 使用原有数据为数据源
            updateNodeTree(context, childNode, templateData)
        }
    }

    private fun updateNode(context: GXTemplateContext, node: GXNode, templateData: JSONObject) {
        // 更新节点样式
        nodeNodeStyle(context, node, templateData)

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

    private fun nodeNodeStyle(context: GXTemplateContext, node: GXNode, templateData: JSONObject) {
        // 容器节点
        if (node.isContainerType()) {
            val isDirty = node.stretchNode.updateContainerStyle(
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
                node.stretchNode.updateNormalStyle(context, node.templateNode, templateData)
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
        } else if (view is GXImageView && node.isImageType()) {
            view.setImageStyle(gxCss)
        } else if (node.isContainerType()) {
            bindContainerViewCss(context, view, node)
        }
        bindCommonViewCss(view, node)
    }

    private fun nodeViewEvent(context: GXTemplateContext, node: GXNode, templateData: JSON) {
        if (templateData !is JSONObject) {
            return
        }
        val templateNode = node.templateNode
        val eventBinding = templateNode.eventBinding ?: return
        val eventData = eventBinding.event.value(templateData) as? JSONObject ?: return
        val invisible = templateNode.finalCss?.style?.isInvisible() ?: false
        if (invisible) {
            return
        }
        val eventType = if (eventData.containsKey(GXTemplateKey.GAIAX_GESTURE_TYPE)) {
            eventData.getString(GXTemplateKey.GAIAX_GESTURE_TYPE)
                ?: GXTemplateKey.GAIAX_GESTURE_TYPE_TAP
        } else {
            GXTemplateKey.GAIAX_GESTURE_TYPE_TAP
        }
        when (eventType) {
            GXTemplateKey.GAIAX_GESTURE_TYPE_TAP -> {
                node.viewRef?.get()?.setOnClickListener {
                    context.eventListener?.onGestureEvent(GXTemplateEngine.GXGesture().apply {
                        this.gestureType = eventType
                        this.view = node.viewRef?.get()
                        this.eventParams = eventData
                        this.nodeId = templateNode.layer.id
                        this.templateItem = context.templateItem
                        this.index = -1
                    })
                }
            }
            GXTemplateKey.GAIAX_GESTURE_TYPE_LONGPRESS -> {
                node.viewRef?.get()?.setOnLongClickListener {
                    context.eventListener?.onGestureEvent(GXTemplateEngine.GXGesture().apply {
                        this.gestureType = eventType
                        this.view = node.viewRef?.get()
                        this.eventParams = eventData
                        this.nodeId = templateNode.layer.id
                        this.templateItem = context.templateItem
                        this.index = -1
                    })
                    true
                }
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
        context.trackListener?.onTrackEvent(GXTemplateEngine.GXTrack().apply {
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
            node.isSliderType() -> bindSlider(context, view, node, dataBinding, templateData)
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
        val containerTemplateData =
            (dataBinding.getValueData(templateData) as? JSONArray) ?: JSONArray()

        val container = view as GXContainer

        val adapter: GXContainerViewAdapter?
        if (container.adapter != null) {
            adapter = container.adapter as GXContainerViewAdapter
        } else {
            adapter = GXContainerViewAdapter(context, node, container)
            container.adapter = adapter
        }

        container.itemAnimator = null
        adapter.setContainerData(containerTemplateData)
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
        if (context.dataListener != null) {
            val gxTextData = GXTemplateEngine.GXTextData().apply {
                this.text = valueData.toString()
                this.view = view as View
                this.nodeId = layer.id
                this.templateItem = context.templateItem
                this.nodeCss = css
                this.nodeData = nodeData
                this.index = context.indexPosition
            }
            val result = context.dataListener?.onTextProcess(gxTextData)
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
        rawJson: JSONObject
    ) {

        val nodeData = binding.getData(rawJson)

        if (context.dataListener != null) {

            val gxTextData = GXTemplateEngine.GXTextData().apply {
                this.text = nodeData?.get(GXTemplateKey.GAIAX_VALUE)?.toString()
                this.view = view as View
                this.nodeId = layer.id
                this.templateItem = context.templateItem
                this.nodeCss = css
                this.nodeData = nodeData
                this.index = context.indexPosition
            }

            context.dataListener?.onTextProcess(gxTextData)?.let { result ->
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

    private fun bindCommonViewCss(view: View, node: GXNode) {
        if (!node.isCustomViewType()) {

            view.setHidden(node.templateNode.css.style.hidden)

            view.setOpacity(node.templateNode.css.style.opacity)

            view.setOverflow(node.templateNode.css.style.overflow)

            view.setBackgroundColorAndBackgroundImage(node.templateNode.css.style)

            view.setRoundCornerRadiusAndRoundCornerBorder(node.templateNode.css.style)
        }

        view.setDisplay(node.templateNode.css.style.display)
    }

    private fun bindContainerViewCss(context: GXTemplateContext, view: View, node: GXNode) {
        if (node.isContainerType()) {
            if (node.isGridType()) {
                bindGridContainerCSS(view, node)
            } else if (node.isScrollType()) {
                bindScrollContainerCSS(context, view, node)
            }
        }
    }

    private fun bindGridContainerCSS(view: View, node: GXNode) {
        node.templateNode.finalGridConfig?.let {
            view.setGridContainerDirection(it, node.stretchNode.finalLayout)
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

            if (context.eventListener != null && view is RecyclerView) {
                view.clearOnScrollListeners()
                view.addOnScrollListener(object : RecyclerView.OnScrollListener() {

                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        context.eventListener?.onScrollEvent(GXTemplateEngine.GXScroll().apply {
                            this.view = recyclerView
                            this.dx = dx
                            this.dy = dy
                        })
                    }

                    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                        context.eventListener?.onScrollEvent(GXTemplateEngine.GXScroll().apply {
                            this.view = recyclerView
                            this.state = newState
                        })
                    }
                })
            }
        }
    }

    private fun bindSlider(context: GXTemplateContext,
                           view: View,
                           node: GXNode,
                           dataBinding: GXDataBinding,
                           templateData: JSONObject) {

        val containerTemplateData = (dataBinding.getValueData(templateData) as? JSONArray) ?: JSONArray()

        val container = view as GXSliderView

        val adapter: GXSliderViewAdapter?
        if (container.viewPager?.adapter != null) {
            adapter = container.viewPager?.adapter as GXSliderViewAdapter
        } else {
            adapter = GXSliderViewAdapter(context, node)
            container.viewPager?.adapter = adapter
        }
        container.setConfig(node.templateNode.finalSliderConfig)

        adapter.setData(containerTemplateData)
        container.setIndicatorCount(containerTemplateData.size)

        container.onBindData(templateData)
    }
}
