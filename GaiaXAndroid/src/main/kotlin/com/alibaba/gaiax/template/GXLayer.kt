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

package com.alibaba.gaiax.template

import android.view.Gravity
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.render.view.GXViewKey

/**
 * 节点层级信息
 * @suppress
 */
data class GXLayer constructor(
    /**
     * 节点ID
     */
    val id: String,
    /**
     * 节点样式ID
     */
    val css: String,
    /**
     * 节点类型
     *
     * 1. type = gaia-template 代表模板根节点 实际等同于 type = view
     * 2. type = gaia-template && sub-type = scroll 代表是容器模板
     * 3. type = gaia-template && sub-type = grid 代表是容器模板
     * 4. type = gaia-template && sub-type = custom 代表是个嵌套子模板
     * 4. type = gaia-template && sub-type = custom && view-class-andriod 代表是自定义组件
     * 5. type = gaia-template && sub-type = custom && view-class-ios 代表是自定义组件
     * 6. type = view、image、richtext 代表普通子节点
     */
    val type: String,
    /**
     * @see type
     */
    val subType: String? = null,
    /**
     * 自定义View的渲染类型：view-class-android
     */
    val customNodeClass: String? = null,
    /**
     * scroll配置
     */
    val scrollConfig: GXScrollConfig? = null,
    /**
     * grid配置
     */
    val gridConfig: GXGridConfig? = null,
    /**
     * slider配置
     */
    val sliderConfig: GXSliderConfig? = null,
    /**
     * slider配置
     */
    val progressConfig: GXProgressConfig? = null,
    /**
     * 子节点
     */
    val layers: MutableList<GXLayer> = mutableListOf()
) {

    companion object {

        fun create(data: JSONObject): GXLayer {
            return createLayer(data)
        }

        private fun createLayer(data: JSONObject): GXLayer {
            val id = data.getString(GXTemplateKey.GAIAX_LAYER_ID) ?: throw IllegalArgumentException(
                "Layer must have id property"
            )
            val type = data.getString(GXTemplateKey.GAIAX_LAYER_TYPE)
                ?: throw IllegalArgumentException("Layer must have type property")
            val css = data.getString(GXTemplateKey.GAIAX_LAYER_CLASS)
            val subType = data.getString(GXTemplateKey.GAIAX_LAYER_SUB_TYPE)
            val viewClass = data.getString(GXTemplateKey.GAIAX_LAYER_CUSTOM_VIEW_CLASS)
            val layer = initLayer(id, css, type, subType, viewClass, data)
            initChildrenLayer(data, layer)
            return layer
        }

        private fun initLayer(
            id: String,
            css: String?,
            type: String,
            subType: String?,
            viewClass: String?,
            data: JSONObject
        ): GXLayer {
            val direction = data.getString(GXTemplateKey.GAIAX_LAYER_DIRECTION)
            val edgeInsets = data.getString(GXTemplateKey.GAIAX_LAYER_EDGE_INSETS)
            var itemSpacing = data.getString(GXTemplateKey.GAIAX_LAYER_ITEM_SPACING)
            if (itemSpacing == null) {
                itemSpacing = data.getString(GXTemplateKey.GAIAX_LAYER_LINE_SPACING)
            }
            var rowSpacing = data.getString(GXTemplateKey.GAIAX_LAYER_ROW_SPACING)
            if (rowSpacing == null) {
                rowSpacing = data.getString(GXTemplateKey.GAIAX_LAYER_INTERITEM_SPACING)
            }
            val column = data.getInteger(GXTemplateKey.GAIAX_LAYER_COLUMN) ?: 1
            val scrollable = data.getBoolean(GXTemplateKey.GAIAX_LAYER_SCROLL_ENABLE) ?: false
            val gravity = when (data.getString(GXTemplateKey.GAIAX_LAYER_GRAVITY)) {
                "top" -> Gravity.TOP
                "bottom" -> Gravity.BOTTOM
                "center" -> Gravity.CENTER
                else -> null
            }
            return when {
                isScrollType(type, subType) -> GXLayer(
                    id = id,
                    css = css ?: id,
                    type = type,
                    subType = subType,
                    customNodeClass = viewClass,
                    scrollConfig = GXScrollConfig.create(
                        data,
                        direction,
                        edgeInsets,
                        itemSpacing,
                        gravity
                    ),
                    gridConfig = null
                )
                isGridType(type, subType) -> GXLayer(
                    id = id,
                    css = css ?: id,
                    type = type,
                    subType = subType,
                    customNodeClass = viewClass,
                    scrollConfig = null,
                    gridConfig = GXGridConfig.create(
                        data,
                        direction,
                        edgeInsets,
                        itemSpacing,
                        rowSpacing,
                        column,
                        scrollable
                    )
                )
                isSliderType(type, subType) -> GXLayer(
                    id = id,
                    css = css ?: id,
                    type = type,
                    subType = subType,
                    customNodeClass = viewClass,
                    sliderConfig = GXSliderConfig.create(data)
                )
                isProgressType(type) -> GXLayer(
                    id = id,
                    css = css ?: id,
                    type = type,
                    subType = subType,
                    customNodeClass = viewClass,
                    progressConfig = GXProgressConfig.create(data)
                )
                else -> GXLayer(
                    id = id,
                    css = css ?: id,
                    type = type,
                    subType = subType,
                    customNodeClass = viewClass,
                    scrollConfig = null,
                    gridConfig = null
                )
            }
        }

        private fun isGridType(type: String, subType: String?) =
            type == GXViewKey.VIEW_TYPE_GAIA_TEMPLATE && subType == GXViewKey.VIEW_TYPE_CONTAINER_GRID

        private fun isScrollType(type: String, subType: String?) =
            type == GXViewKey.VIEW_TYPE_GAIA_TEMPLATE && subType == GXViewKey.VIEW_TYPE_CONTAINER_SCROLL

        private fun isSliderType(type: String, subType: String?) =
            type == GXViewKey.VIEW_TYPE_GAIA_TEMPLATE && subType == GXViewKey.VIEW_TYPE_CONTAINER_SLIDER

        private fun isProgressType(type: String) = type == GXViewKey.VIEW_TYPE_PROGRESS

        private fun initChildrenLayer(data: JSONObject, layer: GXLayer) {
            data.getJSONArray(GXTemplateKey.GAIAX_LAYERS)?.forEach {
                val child = it as? JSONObject
                if (child != null) {
                    val childLayer = createLayer(child)
                    layer.layers.add(childLayer)
                }
            }
        }
    }

    /**
     * 节点真实类型
     */
    fun getNodeType(): String {
        if (subType != null) {
            return subType
        }
        return type
    }

    /**
     * 是否是容器类型
     */
    fun isContainerType(): Boolean {
        return isScrollType() || isGridType() || isSliderType()
    }

    /**
     * Scroll容器节点类型
     */
    fun isScrollType(): Boolean {
        return type == GXViewKey.VIEW_TYPE_GAIA_TEMPLATE && subType == GXViewKey.VIEW_TYPE_CONTAINER_SCROLL
    }

    /**
     * Grid容器节点类型
     */
    fun isGridType(): Boolean {
        return type == GXViewKey.VIEW_TYPE_GAIA_TEMPLATE && subType == GXViewKey.VIEW_TYPE_CONTAINER_GRID
    }

    /**
     * 嵌套子模板类型，是个虚拟节点
     */
    fun isNestChildTemplateType(): Boolean {
        if (GXRegisterCenter.instance.extensionCompatibility?.isCompatibilityContainerNestTemplateJudgementCondition() == true) {
            return type == GXViewKey.VIEW_TYPE_GAIA_TEMPLATE && subType == GXViewKey.VIEW_TYPE_CUSTOM && customNodeClass == null
                    || type == GXViewKey.VIEW_TYPE_GAIA_TEMPLATE && subType == null && customNodeClass == null
        }
        return type == GXViewKey.VIEW_TYPE_GAIA_TEMPLATE && subType == GXViewKey.VIEW_TYPE_CUSTOM && customNodeClass == null
    }

    /**
     * 自定义组件类型
     */
    fun isCustomType(): Boolean {
        return type == GXViewKey.VIEW_TYPE_CUSTOM && customNodeClass != null
    }

    /**
     * 文本类型
     */
    fun isTextType(): Boolean = GXViewKey.VIEW_TYPE_TEXT == type

    /**
     * 富文本类型
     */
    fun isRichTextType(): Boolean = GXViewKey.VIEW_TYPE_RICH_TEXT == type

    /**
     * 视图类型
     */
    fun isViewType(): Boolean =
        GXViewKey.VIEW_TYPE_VIEW == type || GXViewKey.VIEW_TYPE_GAIA_TEMPLATE == type && subType == null

    /**
     * 可能是模板根节点、或者嵌套节点、或者自定义视图类型
     */
    fun isGaiaTemplate(): Boolean = GXViewKey.VIEW_TYPE_GAIA_TEMPLATE == type

    /**
     * IconFont类型
     */
    fun isIconFontType(): Boolean = GXViewKey.VIEW_TYPE_ICON_FONT == type

    /**
     * Lottie类型
     */
    fun isLottieType(): Boolean = GXViewKey.VIEW_TYPE_LOTTIE == type

    /**
     * 图片类型
     */
    fun isImageType(): Boolean = GXViewKey.VIEW_TYPE_IMAGE == type

    /**
     * Progress 类型
     */
    fun isProgressType(): Boolean = GXViewKey.VIEW_TYPE_PROGRESS == type

    /**
     * Slider 容器节点类型
     */
    fun isSliderType(): Boolean =
        type == GXViewKey.VIEW_TYPE_GAIA_TEMPLATE && subType == GXViewKey.VIEW_TYPE_CONTAINER_SLIDER

    /**
     * 是否能够被合并
     */
    fun isCanMergeType(): Boolean =
        !isContainerType() && (GXViewKey.VIEW_TYPE_VIEW == type || type == GXViewKey.VIEW_TYPE_GAIA_TEMPLATE)

    override fun toString(): String {
        return "GXLayer(id='$id', css='$css', type='$type', subType=$subType, customNodeClass=$customNodeClass, scrollConfig=$scrollConfig, gridConfig=$gridConfig, sliderConfig=$sliderConfig, layers=$layers)"
    }

}

