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
import java.lang.IllegalArgumentException

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

    fun reset() {
        resetData()
        visualTemplateNode?.reset()

        // 此处不能重置config和css，不会产生好处，仅有负面影响
        // 如果外部按异步调用更新逻辑，那么重置config和css可能导致取值为空，从而抛出异常
        // resetBasic()
    }

    var dataCache: JSONObject? = null
    var dataValueCache: JSON? = null
    var dataExtendCache: JSONObject? = null

    fun resetData() {
        dataExtendCache = null
        dataValueCache = null
        dataCache = null
    }

    private fun resetBasic() {
        finalCss = null
        finalGridConfig = null
        finalScrollConfig = null
        finalSliderConfig = null
    }

    var finalGridConfig: GXGridConfig? = null

    var finalScrollConfig: GXScrollConfig? = null

    var finalSliderConfig: GXSliderConfig? = null

    var finalCss: GXCss? = null

    /**
     * @param visualTemplateData 当前节点的虚拟父节点使用的数据源
     * @param nodeTemplateData 当前节点使用的数据源
     */
    fun initFinal(
        gxTemplateContext: GXTemplateContext,
        visualTemplateData: JSONObject?,
        nodeTemplateData: JSONObject?
    ) {

        // 初始化扩展数据
        val extendCssData = dataBinding?.getExtend(nodeTemplateData)

        // 创建FinalStyle
        val selfFinalCss: GXCss = if (extendCssData != null) {
            // 创建Css
            val extendCss = GXCss.createByExtend(extendCssData)

            // 更新除了CSS外的其他节点信息

            // 仅当有Grid配置信息时，才处理更新
            layer.gridConfig?.let {
                finalGridConfig = GXGridConfig.create(it, extendCssData)
            }

            // 仅当有Scroll配置信息时，才处理更新
            layer.scrollConfig?.let {
                finalScrollConfig = GXScrollConfig.create(it, extendCssData)
            }

            layer.sliderConfig?.let {
                finalSliderConfig = GXSliderConfig.create(it, extendCssData)
            }

            // 合并原有CSS和扩展属性的CSS
            GXCss.create(css, extendCss)
        } else {

            layer.gridConfig?.let {
                finalGridConfig = it
            }

            layer.scrollConfig?.let {
                finalScrollConfig = it
            }

            layer.sliderConfig?.let {
                finalSliderConfig = it
            }

            css
        }

        // 初始化虚拟节点的FinalStyle
        visualTemplateNode?.initFinal(gxTemplateContext, null, visualTemplateData)

        // 合并Self和Visual
        this.finalCss = GXCss.create(selfFinalCss, visualTemplateNode?.finalCss)

    }

    fun getNodeType() = layer.getNodeType()

    fun getCustomViewClass() = layer.customNodeClass

    fun isTextType(): Boolean = layer.isTextType()

    fun isRichTextType(): Boolean = layer.isRichTextType()

    fun isViewType(): Boolean = layer.isViewType()

    fun isIconFontType(): Boolean = layer.isIconFontType()

    fun isLottieType(): Boolean = layer.isLottieType()

    fun isImageType(): Boolean = layer.isImageType()

    fun isScrollType(): Boolean = layer.isScrollType()

    fun isContainerType(): Boolean = layer.isContainerType()

    fun isGridType(): Boolean = layer.isGridType()

    fun isSliderType(): Boolean = layer.isSliderType()

    fun isGaiaTemplateType(): Boolean = layer.isGaiaTemplate()

    fun isCustomType(): Boolean = layer.isCustomType()

    fun isCanMergeType(): Boolean = layer.isCanMergeType()

    fun isNestChildTemplateType(): Boolean = layer.isNestChildTemplateType()

    fun getNodeId(): String {
        return layer.id
    }

    override fun toString(): String {
        return "GXTemplateNode(layer=$layer, css=$css, dataBinding=$dataBinding, eventBinding=$eventBinding, animationBinding=$animationBinding, visualTemplateNode=$visualTemplateNode, finalCss=$finalCss)"
    }


    companion object {

        fun createNode(
            viewId: String,
            template: GXTemplateInfo,
            visualTemplateNode: GXTemplateNode? = null
        ): GXTemplateNode {
            val layer = template.findLayer(viewId)
                ?: throw IllegalArgumentException("Not found layer by view id, viewId = $viewId")
            val css = template.findCss(viewId) ?: GXCss.create()
            val dataBinding = template.findData(viewId)
            val eventBinding = template.findEvent(viewId)
            val animationBinding = template.findAnimation(viewId)
            return GXTemplateNode(
                layer,
                css,
                dataBinding,
                eventBinding,
                animationBinding,
                visualTemplateNode
            )
        }
    }
}
