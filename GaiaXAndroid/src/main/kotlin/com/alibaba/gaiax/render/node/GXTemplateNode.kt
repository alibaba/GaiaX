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

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.template.*
import com.alibaba.gaiax.template.animation.GXAnimationBinding

/**
 * @suppress
 */
data class GXTemplateNode(
    /**
     * 节点的层级信息
     */
    val layer: GXLayer,
    /**
     * 节点的样式信息
     */
    val css: GXCss,
    /**
     * 节点的数据绑定信息
     */
    var dataBinding: GXDataBinding? = null,
    /**
     * 节点的事件绑定信息
     */
    val eventBinding: GXEventBinding? = null,
    /**
     * 节点的埋点绑定信息
     */
    val trackBinding: GXTrackBinding? = null,
    /**
     * 节点的动画绑定信息
     */
    val animationBinding: GXAnimationBinding? = null,
    /**
     * 节点的虚拟嵌套父节点信息
     */
    val visualTemplateNode: GXTemplateNode? = null
) {

    fun getData(templateData: JSONObject): JSONObject? {
        if (dataCache == null) {
            dataCache = dataBinding?.getData(templateData)
        }
        return dataCache
    }

    fun getExtend(templateData: JSON?): JSONObject? {
        if (dataExtendCache == null) {
            dataExtendCache = dataBinding?.getExtend(templateData)
        }
        return dataExtendCache
    }

    fun getDataValue(templateData: JSONObject): JSON? {
        if (dataValueCache == null) {
            dataValueCache =
                dataBinding?.getData(templateData)?.get(GXTemplateKey.GAIAX_VALUE) as? JSON
        }
        return dataValueCache
    }

    fun resetDataCache() {
        dataExtendCache = null
        dataValueCache = null
        dataCache = null
        visualTemplateNode?.resetDataCache()
    }

    fun reset() {
        resetDataCache()
        layer.sliderConfig?.reset()
        layer.scrollConfig?.reset()
        layer.gridConfig?.reset()
        layer.progressConfig?.reset()
        css.style.reset()
        css.flexBox.reset()
    }

    /*
     * 数据缓存
     */
    var dataCache: JSONObject? = null
    var dataValueCache: JSON? = null
    var dataExtendCache: JSONObject? = null

    /**
     * @param visualTemplateData 当前节点的虚拟父节点使用的数据源
     * @param nodeTemplateData 当前节点使用的数据源
     */
    fun initFinal(
        gxTemplateContext: GXTemplateContext,
        visualTemplateData: JSONObject?,
        nodeTemplateData: JSONObject?
    ) {

        val extendCssData = dataBinding?.getExtend(nodeTemplateData)

        if (extendCssData != null && extendCssData.isNotEmpty()) {
            layer.scrollConfig?.updateByExtend(extendCssData)
            layer.gridConfig?.updateByExtend(extendCssData)
            layer.sliderConfig?.updateByExtend(extendCssData)
            layer.progressConfig?.updateByExtend(extendCssData)
            css.updateByExtend(extendCssData)
        }

        visualTemplateNode?.initFinal(gxTemplateContext, null, visualTemplateData)

        css.updateByVisual(visualTemplateNode?.css)
    }

    val getNodeType = layer.getNodeType

    val getCustomViewClass = layer.customNodeClass

    val isTextType: Boolean = layer.isTextType

    val isRichTextType: Boolean = layer.isRichTextType

    val isViewType: Boolean = layer.isViewType

    val isIconFontType: Boolean = layer.isIconFontType

    val isLottieType: Boolean = layer.isLottieType

    val isImageType: Boolean = layer.isImageType

    val isScrollType: Boolean = layer.isScrollType

    val isContainerType: Boolean = layer.isContainerType

    val isGridType: Boolean = layer.isGridType

    val isSliderType: Boolean = layer.isSliderType

    val isProgressType: Boolean = layer.isProgressType

    val isGaiaTemplateType: Boolean = layer.isGaiaTemplate

    val isCustomType: Boolean = layer.isCustomType

    val isCanBeMergedType: Boolean = layer.isCanMergeType

    val isNestChildTemplateType: Boolean = layer.isNestChildTemplateType

    val getNodeId: String = layer.id

    companion object {

        fun createNode(
            viewId: String, template: GXTemplateInfo, visualTemplateNode: GXTemplateNode? = null
        ): GXTemplateNode {
            val layer = template.findLayer(viewId)
                ?: throw IllegalArgumentException("Not found layer by view id, viewId = $viewId")
            val css = template.findCss(viewId) ?: GXCss.create()
            val dataBinding = template.findData(viewId)
            val eventBinding = template.findEvent(viewId)
            val trackBinding = template.findTrack(viewId)
            val animationBinding = template.findAnimation(viewId)
            return GXTemplateNode(
                layer,
                css,
                dataBinding,
                eventBinding,
                trackBinding,
                animationBinding,
                visualTemplateNode
            )
        }
    }
}
