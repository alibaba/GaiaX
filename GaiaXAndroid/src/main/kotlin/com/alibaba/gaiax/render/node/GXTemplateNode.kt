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
import com.alibaba.gaiax.template.GXCss
import com.alibaba.gaiax.template.GXDataBinding
import com.alibaba.gaiax.template.GXEventBinding
import com.alibaba.gaiax.template.GXFlexBox
import com.alibaba.gaiax.template.GXGridConfig
import com.alibaba.gaiax.template.GXLayer
import com.alibaba.gaiax.template.GXProgressConfig
import com.alibaba.gaiax.template.GXScrollConfig
import com.alibaba.gaiax.template.GXSliderConfig
import com.alibaba.gaiax.template.GXStyle
import com.alibaba.gaiax.template.GXTemplateInfo
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.template.GXTrackBinding
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
        visualTemplateNode?.reset()
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
            css.updateByExtend(gxTemplateContext, extendCssData)
        }

        visualTemplateNode?.let {
            it.initFinal(gxTemplateContext, null, visualTemplateData)
            css.updateByVisual(it.css)
        }
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

    fun isProgressType(): Boolean = layer.isProgressType()

    fun isGaiaTemplateType(): Boolean = layer.isGaiaTemplate()

    fun isCustomType(): Boolean = layer.isCustomType()

    fun isCanBeMergedType(): Boolean = layer.isCanMergeType()

    fun isNestChildTemplateType(): Boolean = layer.isNestChildTemplateType()

    fun getNodeId(): String {
        return layer.id
    }

    override fun toString(): String {
        return "GXTemplateNode(layer=$layer, css=$css)"
    }

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
                copyLayer(layer),
                copyCss(css),
                dataBinding,
                eventBinding,
                trackBinding,
                animationBinding,
                visualTemplateNode
            )
        }

        private fun copyCss(css: GXCss): GXCss {
            return GXCss(copyStyle(css.style), copyFlexBox(css.flexBox))
        }

        private fun copyFlexBox(flexBox: GXFlexBox): GXFlexBox {
            return GXFlexBox(
                flexBox.displayForTemplate,
                flexBox.positionTypeForTemplate,
                flexBox.directionForTemplate,
                flexBox.flexDirectionForTemplate,
                flexBox.flexWrapForTemplate,
                flexBox.overflowForTemplate,
                flexBox.alignItemsForTemplate,
                flexBox.alignSelfForTemplate,
                flexBox.alignContentForTemplate,
                flexBox.justifyContentForTemplate,
                flexBox.positionForTemplate,
                flexBox.marginForTemplate,
                flexBox.paddingForTemplate,
                flexBox.borderForTemplate,
                flexBox.flexGrowForTemplate,
                flexBox.flexShrinkForTemplate,
                flexBox.flexBasisForTemplate,
                flexBox.sizeForTemplate,
                flexBox.minSizeForTemplate,
                flexBox.maxSizeForTemplate,
                flexBox.aspectRatioForTemplate
            )
        }

        private fun copyStyle(style: GXStyle): GXStyle {
            return GXStyle(
                style.fontSizeForTemplate,
                style.fontFamilyForTemplate,
                style.fontWeightForTemplate,
                style.fontLinesForTemplate,
                style.fontColorForTemplate,
                style.fontTextOverflowForTemplate,
                style.fontTextAlignForTemplate,
                style.backgroundColorForTemplate,
                style.backgroundImageForTemplate,
                style.opacityForTemplate,
                style.overflowForTemplate,
                style.displayForTemplate,
                style.hiddenForTemplate,
                style.paddingForTemplate,
                style.borderWidthForTemplate,
                style.borderColorForTemplate,
                style.borderRadiusForTemplate,
                style.fontLineHeightForTemplate,
                style.fontTextDecorationForTemplate,
                style.modeForTemplate,
                style.boxShadowForTemplate,
                style.backdropFilterForTemplate,
                style.fitContentForTemplate
            )
        }

        private fun copyLayer(layer: GXLayer): GXLayer {
            return GXLayer(
                layer.id,
                layer.css,
                layer.type,
                layer.subType,
                layer.customNodeClass,
                copyScrollConfig(layer.scrollConfig),
                copyGridConfig(layer.gridConfig),
                copySliderConfig(layer.sliderConfig),
                copyProgressConfig(layer.progressConfig)
            )
        }

        private fun copyProgressConfig(progressConfig: GXProgressConfig?): GXProgressConfig? {
            return progressConfig?.let {
                GXProgressConfig(
                    it.strokeColorForTemplate,
                    it.trailColorForTemplate,
                    it.progressTypeForTemplate,
                    it.animatedForTemplate
                )
            }
        }

        private fun copySliderConfig(sliderConfig: GXSliderConfig?): GXSliderConfig? {
            return sliderConfig?.let {
                GXSliderConfig(
                    it.scrollTimeIntervalForTemplate,
                    it.infinityScrollForTemplate,
                    it.hasIndicatorForTemplate,
                    it.selectedIndexForTemplate,
                    it.indicatorSelectedColorForTemplate,
                    it.indicatorUnselectedColorForTemplate,
                    it.indicatorMarginForTemplate,
                    it.indicatorPositionForTemplate,
                    it.indicatorClassForTemplate
                )
            }
        }

        private fun copyGridConfig(gridConfig: GXGridConfig?): GXGridConfig? {
            return gridConfig?.let {
                GXGridConfig(
                    it.data,
                    it.columnForTemplate,
                    it.directionForTemplate,
                    it.itemSpacingForTemplate,
                    it.rowSpacingForTemplate,
                    it.scrollEnableForTemplate
                )
            }
        }

        private fun copyScrollConfig(scrollConfig: GXScrollConfig?): GXScrollConfig? {
            return scrollConfig?.let {
                GXScrollConfig(
                    it.data,
                    it.directionForTemplate,
                    it.itemSpacingForTemplate,
                    it.edgeInsetsForTemplate,
                    it.gravityForTemplate
                )
            }
        }
    }
}
