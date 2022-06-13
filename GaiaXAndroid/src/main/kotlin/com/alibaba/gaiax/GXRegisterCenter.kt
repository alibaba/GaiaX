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

package com.alibaba.gaiax

import android.graphics.Typeface
import android.content.Context
import android.view.ViewGroup
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXINodeEvent
import com.alibaba.gaiax.render.node.GXTemplateNode
import com.alibaba.gaiax.render.view.GXViewFactory
import com.alibaba.gaiax.render.view.container.GXContainerViewAdapter
import com.alibaba.gaiax.template.*
import com.alibaba.gaiax.template.animation.GXLottieAnimation

/**
 * GaiaX register center. For extended functionality.
 *
 * Basic usage:
 * ```
 * ```
 */
class GXRegisterCenter {

    /**
     * GXTemplateInfo data source interface
     */
    interface GXIExtensionTemplateInfoSource {

        /**
         * To get GXTemplateData from data source
         * @see GXTemplateEngine.GXTemplateItem
         * @see GXTemplateInfo
         */
        fun getTemplateInfo(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplateInfo?
    }

    /**
     * GXTemplate data source interface
     */
    interface GXIExtensionTemplateSource {

        /**
         * To get GXTemplate from data source
         *
         * @see GXTemplate
         */
        fun getTemplate(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplate?
    }

    interface GXIExtensionContainerItemBind {
        fun bindViewHolder(
            tag: Any?,
            childItemContainer: ViewGroup,
            childMeasureSize: GXTemplateEngine.GXMeasureSize,
            childTemplateItem: GXTemplateEngine.GXTemplateItem,
            childItemPosition: Int,
            childVisualNestTemplateNode: GXTemplateNode?,
            childItemData: JSONObject
        ): Any?
    }

    interface GXIExtensionContainerDataUpdate {
        fun update(
            gxTemplateContext: GXTemplateContext,
            gxContainerViewAdapter: GXContainerViewAdapter,
            old: JSONArray,
            new: JSONArray
        )
    }

    interface GXIExtensionNodeEvent {
        fun create(): GXINodeEvent
    }

    interface GXIExtensionExpression {
        fun create(value: Any): GXIExpression
        fun isTrue(value: Any?): Boolean
    }

    interface GXIExtensionDataBinding {
        fun create(value: Any): GXDataBinding?
    }

    interface GXIExtensionColor {
        fun convert(context: Context?, value: String): Int?
    }

    interface GXIExtensionSize {
        fun create(value: String): Float?
        fun convert(value: Float): Float?
    }

    /**
     * Dynamic property have context, but static property haven't context.
     */
    interface GXIExtensionDynamicProperty {

        data class GXParams(val propertyName: String, val value: Any) {
            var gridConfig: GXGridConfig? = null
            var flexBox: GXFlexBox? = null
            var cssStyle: GXStyle? = null
        }

        fun convert(params: GXParams): Any?
    }

    /**
     * Dynamic property have context, but static property haven't context.
     */
    interface GXIExtensionStaticProperty {

        data class GXParams(val propertyName: String, val value: Any)

        fun convert(params: GXParams): Any?
    }

    /**
     * Load Typeface
     *  Basic usage:
     * ```
     * GXRegisterCenter.instance.registerExtensionFontFamily(object :
     *      GXRegisterCenter.GXIExtensionFontFamily {
     *      override fun fontFamily(fontFamilyName: String): Typeface? {
     *          return Typeface.createFromAsset(GXMockUtils.context.assets, fontFamilyName)
     *      }
     * })
     * ```
     */
    interface GXIExtensionFontFamily {

        fun fontFamily(fontFamilyName: String): Typeface?
    }

    interface GXIExtensionBizMap {
        fun convert(item: GXTemplateEngine.GXTemplateItem)
    }

    interface GXIExtensionGrid {
        fun convert(
            propertyName: String,
            gxTemplateContext: GXTemplateContext,
            gridConfig: GXGridConfig
        ): Any?
    }

    interface GXIExtensionScroll {
        fun convert(
            propertyName: String,
            gxTemplateContext: GXTemplateContext,
            scrollConfig: GXScrollConfig
        ): Any?
    }

    interface GXIExtensionLottieAnimation {
        fun create(): GXLottieAnimation?
    }

    interface GXIExtensionCompatibility {

        /**
         * 是否兼容容器数据的传递顺序
         */
        fun isCompatibilityContainerDataPassSequence() = false

        /**
         * 是否兼容容器嵌套模板的判定逻辑
         */
        fun isCompatibilityContainerNestTemplateJudgementCondition() = false

        /**
         * 是否兼容容器数据源抛异常的行为
         */
        fun isPreventContainerDataSourceThrowException() = false

        /**
         * 是否兼容IconFont的字体抛异常行为
         */
        fun isPreventIconFontTypefaceThrowException() = false

        /**
         *
         */
        fun isPreventAccessibilityThrowException() = false

        /**
         *
         */
        fun isPreventFitContentThrowException() = false

        fun isCompatibilityDataBindingFitContent() = false
    }

    internal var extensionBizMap: GXIExtensionBizMap? = null

    fun registerExtensionBizMapRelation(extensionBizMap: GXIExtensionBizMap): GXRegisterCenter {
        this.extensionBizMap = extensionBizMap
        return this
    }

    /**
     * @param source
     * @param priority [0,99]
     */
    fun registerExtensionTemplateSource(
        source: GXIExtensionTemplateSource,
        priority: Int = 0
    ): GXRegisterCenter {
        GXTemplateEngine.instance.data.templateSource.registerByPriority(source, priority)
        return this
    }

    /**
     * @param source
     * @param priority [0,99]
     */
    fun registerExtensionTemplateInfoSource(
        source: GXIExtensionTemplateInfoSource,
        priority: Int = 0
    ): GXRegisterCenter {
        GXTemplateEngine.instance.data.templateInfoSource.registerByPriority(source, priority)
        return this
    }

    internal var extensionDataBinding: GXIExtensionDataBinding? = null

    fun registerExtensionDataBinding(databindingExtensionDataBinding: GXIExtensionDataBinding): GXRegisterCenter {
        this.extensionDataBinding = databindingExtensionDataBinding
        return this
    }

    internal var extensionExpression: GXIExtensionExpression? = null

    fun registerExtensionExpression(extensionExpression: GXIExtensionExpression): GXRegisterCenter {
        this.extensionExpression = extensionExpression
        return this
    }

    internal var extensionColor: GXIExtensionColor? = null

    fun registerExtensionColor(extensionColor: GXIExtensionColor): GXRegisterCenter {
        this.extensionColor = extensionColor
        return this
    }

    internal var extensionSize: GXIExtensionSize? = null

    fun registerExtensionSize(extensionSize: GXIExtensionSize): GXRegisterCenter {
        this.extensionSize = extensionSize
        return this
    }

    internal var extensionDynamicProperty: GXIExtensionDynamicProperty? = null

    fun registerExtensionDynamicProperty(extensionDynamicProperty: GXIExtensionDynamicProperty): GXRegisterCenter {
        this.extensionDynamicProperty = extensionDynamicProperty
        return this
    }

    internal var extensionStaticProperty: GXIExtensionStaticProperty? = null

    fun registerExtensionStaticProperty(extensionStaticProperty: GXIExtensionStaticProperty): GXRegisterCenter {
        this.extensionStaticProperty = extensionStaticProperty
        return this
    }

    internal var extensionGrid: GXIExtensionGrid? = null

    fun registerExtensionGrid(extensionGrid: GXIExtensionGrid): GXRegisterCenter {
        this.extensionGrid = extensionGrid
        return this
    }

    internal var extensionScroll: GXIExtensionScroll? = null

    fun registerExtensionScroll(extensionScroll: GXIExtensionScroll): GXRegisterCenter {
        this.extensionScroll = extensionScroll
        return this
    }

    fun registerExtensionViewSupport(viewType: String, clazz: Class<*>): GXRegisterCenter {
        GXViewFactory.viewSupport[viewType] = clazz
        return this
    }

    internal var extensionCompatibility: GXIExtensionCompatibility? = null

    fun registerExtensionCompatibility(extensionCompatibility: GXIExtensionCompatibility): GXRegisterCenter {
        this.extensionCompatibility = extensionCompatibility
        return this
    }

    internal var extensionNodeEvent: GXIExtensionNodeEvent? = null

    fun registerExtensionNodeEvent(extensionNodeEvent: GXIExtensionNodeEvent): GXRegisterCenter {
        this.extensionNodeEvent = extensionNodeEvent
        return this
    }

    internal var extensionContainerDataUpdate: GXIExtensionContainerDataUpdate? = null

    fun registerExtensionContainerDataUpdate(extensionContainerDataUpdate: GXIExtensionContainerDataUpdate): GXRegisterCenter {
        this.extensionContainerDataUpdate = extensionContainerDataUpdate
        return this
    }

    internal var extensionContainerItemBind: GXIExtensionContainerItemBind? = null

    fun registerExtensionContainerItemBind(extensionContainerItemBind: GXIExtensionContainerItemBind): GXRegisterCenter {
        this.extensionContainerItemBind = extensionContainerItemBind
        return this
    }

    internal var extensionLottieAnimation: GXIExtensionLottieAnimation? = null

    fun registerExtensionLottieAnimation(extensionLottieAnimation: GXIExtensionLottieAnimation): GXRegisterCenter {
        this.extensionLottieAnimation = extensionLottieAnimation
        return this
    }

    fun registerExtensionFontFamily(extensionFontFamily: GXIExtensionFontFamily): GXRegisterCenter {
        this.extensionStaticProperty = object : GXIExtensionStaticProperty {
            override fun convert(params: GXIExtensionStaticProperty.GXParams): Any? {
                if (params.propertyName == GXTemplateKey.STYLE_FONT_FAMILY) {
                    return extensionFontFamily.fontFamily(params.value as String)
                }
                return null
            }
        }
        return this
    }

    fun reset() {
        extensionNodeEvent = null
        extensionCompatibility = null
        extensionScroll = null
        extensionGrid = null
        extensionBizMap = null
        extensionColor = null
        extensionExpression = null
        extensionDynamicProperty = null
        extensionStaticProperty = null
        extensionContainerDataUpdate = null
        extensionContainerItemBind = null
        extensionSize = null
    }

    companion object {

        val instance by lazy {
            GXRegisterCenter()
        }
    }
}