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
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.text.GXHighLightUtil
import com.alibaba.gaiax.render.view.*
import com.alibaba.gaiax.render.view.basic.GXIImageView
import com.alibaba.gaiax.render.view.basic.GXProgressView
import com.alibaba.gaiax.render.view.basic.GXText
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.render.view.container.GXContainerViewAdapter
import com.alibaba.gaiax.render.view.container.slider.GXSliderView
import com.alibaba.gaiax.render.view.container.slider.GXSliderViewAdapter
import com.alibaba.gaiax.template.GXCss
import com.alibaba.gaiax.template.GXLayer
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * @suppress
 */
class GXNodeTreeUpdater(val context: GXTemplateContext) {

    fun buildNodeLayout() {
        val rootNode = context.rootNode
            ?: throw IllegalArgumentException("RootNode is null(buildNodeLayout)")
        val templateData = context.templateData?.data
            ?: throw IllegalArgumentException("Data is null")
        val size = Size(context.size.width, context.size.height)

        // 更新布局
        updateNodeTreeLayout(rootNode, templateData, size)

        // 如果存在延迟计算文字自适应的情况，需要处理后重新计算
        updateNodeTreeLayoutByDirtyText(rootNode, size)
    }

    fun buildViewStyle() {
        val rootNode = context.rootNode
            ?: throw IllegalArgumentException("RootNode is null(buildViewStyle)")
        val templateData = context.templateData?.data
            ?: throw IllegalArgumentException("Data is null")
        // 更新样式
        updateNodeTreeStyle(context, rootNode, templateData)
    }

    fun buildLayoutAndStyle() {
        val rootNode = context.rootNode
            ?: throw IllegalArgumentException("RootNode is null(buildLayoutAndStyle)")
        val templateData = context.templateData?.data
            ?: throw IllegalArgumentException("Data is null")
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
                    it.value.gxTemplateContext,
                    it.value.gxTemplateNode,
                    it.value.gxStretchNode,
                    it.value.gxCssStyle,
                    it.value.templateData,
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
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        templateData: JSONObject
    ) {
        gxNode.templateNode.reset()
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
            gxTemplateContext,
            visualTemplateData = null,
            nodeTemplateData = templateData
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
            gxTemplateContext,
            visualTemplateData = null,
            nodeTemplateData = templateData
        )

        updateNodeLayout(gxTemplateContext, gxNode, templateData)

        gxNode.children?.forEach { childNode ->
            // 使用原有数据为数据源
            updateNodeTreeLayout(gxTemplateContext, childNode, templateData)
        }
    }

    private fun updateNodeLayout(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        templateData: JSONObject
    ) {
        // 更新节点布局
        nodeNodeLayout(gxTemplateContext, gxNode, templateData)
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

            if (GXRegisterCenter.instance.extensionCompatibility?.isCompatibilityContainerDataPassSequence() == true) {
                // 是否兼容处理先$nodes取数组，再去$$的情况

                val tmp = gxNode.templateNode.visualTemplateNode?.dataBinding
                gxNode.templateNode.visualTemplateNode?.dataBinding =
                    gxNode.templateNode.dataBinding
                gxNode.templateNode.dataBinding = tmp

                gxNode.templateNode.visualTemplateNode?.resetData()
                gxNode.templateNode.resetData()

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

    private fun updateNodeTreeStyle(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        templateData: JSONObject
    ) {
        if (gxNode.isNestRoot) {
            updateNestNodeStyle(gxTemplateContext, gxNode, templateData)
        } else if (gxNode.isContainerType()) {
            updateContainerNodeStyle(gxTemplateContext, gxNode, templateData)
        } else {
            updateNormalNodeStyle(gxTemplateContext, gxNode, templateData)
        }
    }

    private fun updateNestNodeStyle(
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

    private fun updateContainerNodeStyle(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        templateData: JSONObject
    ) {
        updateNodeStyle(gxTemplateContext, gxNode, templateData)
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

        updateNodeStyle(gxTemplateContext, gxNode, childTemplateData)
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

        updateNodeStyle(gxTemplateContext, gxNode, childTemplateData)

        gxNode.children?.forEach { childNode ->
            // 使用原有数据为数据源
            updateNodeTreeStyle(gxTemplateContext, childNode, childTemplateData)
        }
    }

    private fun updateNormalNodeStyle(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        templateData: JSONObject
    ) {
        updateNodeStyle(gxTemplateContext, gxNode, templateData)

        gxNode.children?.forEach { childNode ->
            // 使用原有数据为数据源
            updateNodeTreeStyle(gxTemplateContext, childNode, templateData)
        }
    }

    private fun updateNodeStyle(
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
        gxNode.templateNode.animationBinding?.executeAnimation(
            gxTemplateContext,
            gxNode,
            templateData
        )
    }

    private fun nodeNodeLayout(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        templateData: JSONObject
    ) {
        // 容器节点
        if (gxNode.isContainerType()) {
            val isDirty = gxNode.stretchNode.updateContainerLayout(
                gxTemplateContext,
                gxNode.templateNode,
                gxNode,
                templateData
            )
            if (isDirty) {
                gxTemplateContext.isDirty = isDirty
            }
        }
        // 普通节点
        else {
            val isDirty =
                gxNode.stretchNode.updateNormalLayout(
                    gxTemplateContext,
                    gxNode.templateNode,
                    templateData
                )
            if (isDirty) {
                gxTemplateContext.isDirty = isDirty
            }
        }
    }

    private fun nodeViewCss(gxTemplateContext: GXTemplateContext, gxNode: GXNode) {
        val view = gxNode.view ?: return
        val gxCss = gxNode.templateNode.finalCss ?: return

        if (view is GXText && (gxNode.isTextType() || gxNode.isRichTextType() || gxNode.isIconFontType())) {
            view.setTextStyle(gxCss)
        } else if (view is GXIImageView && gxNode.isImageType()) {
            view.setImageStyle(gxCss)
        } else if (gxNode.isContainerType()) {
            bindContainerViewCss(gxTemplateContext, gxCss, view, gxNode)
        }
        bindCommonViewCss(view, gxCss, gxNode)
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
                        recyclerView: RecyclerView,
                        dx: Int,
                        dy: Int
                    ) {
                        gxTemplateContext.templateData?.eventListener?.onScrollEvent(
                            GXTemplateEngine.GXScroll().apply {
                                this.type = GXTemplateEngine.GXScroll.TYPE_ON_SCROLLED
                                this.view = recyclerView
                                this.dx = dx
                                this.dy = dy
                            })
                    }

                    override fun onScrollStateChanged(
                        recyclerView: RecyclerView,
                        newState: Int
                    ) {
                        gxTemplateContext.templateData?.eventListener?.onScrollEvent(
                            GXTemplateEngine.GXScroll().apply {
                                this.type = GXTemplateEngine.GXScroll.TYPE_ON_SCROLL_STATE_CHANGED
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
            val trackData = gxTrackBinding.value.value(templateData) as? JSONObject ?: return
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
            val trackData = gxEventBinding.value.value(templateData) as? JSONObject ?: return
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
                gxTemplateContext,
                view,
                gxNode.templateNode,
                templateData
            )
            gxNode.isTextType() -> bindText(
                gxTemplateContext,
                view,
                css,
                layer,
                gxNode.templateNode,
                templateData
            )
            gxNode.isRichTextType() -> bindRichText(
                gxTemplateContext,
                view,
                css,
                layer,
                gxNode.templateNode,
                templateData
            )
            gxNode.isIconFontType() -> bindIconFont(view, gxNode.templateNode, templateData)
            gxNode.isImageType() -> bindImage(view, gxNode.templateNode, templateData)
            gxNode.isProgressType() -> bindProgress(view, gxNode.templateNode, templateData)
            gxNode.isScrollType() || gxNode.isGridType() -> bindScrollAndGrid(
                gxTemplateContext,
                view,
                gxNode,
                gxNode.templateNode,
                templateData
            )
            gxNode.isSliderType() -> bindSlider(
                gxTemplateContext,
                view,
                gxNode,
                gxNode.templateNode,
                templateData
            )
            gxNode.isViewType() || gxNode.isGaiaTemplateType() -> bindView(
                view,
                gxNode.templateNode,
                templateData
            )
        }
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
            if (GXRegisterCenter.instance.extensionCompatibility?.isPreventContainerDataSourceThrowException() == true) {
                containerTemplateData = JSONArray()
            } else {
                throw IllegalArgumentException("Scroll or Grid must be have a array data source")
            }
        }

        val extendData = gxTemplateNode.getExtend(templateData)

        val container = view as GXContainer

        gxTemplateContext.containers.add(container)

        val adapter: GXContainerViewAdapter?
        if (container.adapter != null) {
            adapter = container.adapter as GXContainerViewAdapter
        } else {
            adapter = GXContainerViewAdapter(gxTemplateContext, container)
            container.adapter = adapter
        }

        adapter.gxNode = gxNode

        // scroll item to position
        gxTemplateContext.templateData?.scrollIndex?.let { scrollPosition ->
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
            val result: CharSequence? =
                GXHighLightUtil.getHighLightContent(
                    view as View,
                    gxTemplateNode,
                    templateData,
                    valueData
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

            gxTemplateContext.templateData?.dataListener?.onTextProcess(gxTextData)?.let { result ->
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

    private fun bindCommonViewCss(view: View, gxCss: GXCss, node: GXNode) {

        view.setDisplay(gxCss.style.display)

        if (!node.isCustomViewType()) {

            view.setHidden(gxCss.style.display, gxCss.style.hidden)

            view.setOpacity(gxCss.style.opacity)

            view.setOverflow(gxCss.style.overflow)

            view.setBackgroundColorAndBackgroundImageWithRadius(gxCss.style)

            view.setRoundCornerRadiusAndRoundCornerBorder(gxCss.style)
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
            view.setGridContainerDirection(gxTemplateContext, it, gxNode.stretchNode.layoutByBind)
            view.setGridContainerItemSpacingAndRowSpacing(
                it.edgeInsets,
                it.itemSpacing,
                it.rowSpacing
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
                scrollConfig.direction,
                gxNode.stretchNode.layoutByBind
            )

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
            if (GXRegisterCenter.instance.extensionCompatibility?.isPreventContainerDataSourceThrowException() == true) {
                containerTemplateData = JSONArray()
            } else {
                throw IllegalArgumentException("Slider or Grid must be have a array data source")
            }
        }

        val container = view as GXSliderView
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
        container.setIndicatorCount(containerTemplateData.size)

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
