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

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.view.ViewGroup
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXINodeEvent
import com.alibaba.gaiax.render.view.GXViewFactory
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.render.view.container.GXContainerViewAdapter
import com.alibaba.gaiax.template.GXDataBinding
import com.alibaba.gaiax.template.GXFlexBox
import com.alibaba.gaiax.template.GXGridConfig
import com.alibaba.gaiax.template.GXIExpression
import com.alibaba.gaiax.template.GXScrollConfig
import com.alibaba.gaiax.template.GXStyle
import com.alibaba.gaiax.template.GXTemplate
import com.alibaba.gaiax.template.GXTemplateInfo
import com.alibaba.gaiax.template.GXTemplateKey
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
     * 用于页面GaiaX监听内部创建坑位视图可见性监听
     */
    interface GXIPageItemViewLifecycleListener : GXIItemViewLifecycleListener {

        override fun onCreate(gxView: View?) {
        }

        override fun onVisible(gxView: View?) {
        }

        override fun onInvisible(gxView: View?) {
        }

        override fun onReuse(gxView: View?) {
        }

        override fun onDestroy(gxView: View?) {
        }
    }


    /**
     * 内部创建坑位视图可见性监听
     */
    interface GXIItemViewLifecycleListener {

        /**
         * 坑位创建事件，当坑位View创建成功时调用，此时还未绑定数据；与onReuse互斥。
         */
        fun onCreate(gxView: View?)

        /**
         * 坑位复用事件，当坑位的View被复用时调用，此时还未绑定数据；与onCreate互斥。
         */
        fun onReuse(gxView: View?)

        /**
         * 坑位开始事件，当坑位准备绑定数据时调用
         */
        fun onStart(gxView: View?, gxTemplateData: GXTemplateEngine.GXTemplateData)

        /**
         * 坑位数据绑定结束事件，当坑位数据绑定结束时调用
         */
        fun onStarted(gxView: View?)

        /**
         * 坑位可见事件，@see GXContainerViewAdapter，根据 onViewAttachedToWindow 回调
         */
        fun onVisible(gxView: View?)

        /**
         * 坑位不可见事件，@see GXContainerViewAdapter，根据 onViewDetachedFromWindow 回调
         */
        fun onInvisible(gxView: View?)

        /**
         * 坑位销毁事件，@see GXItemContainer，当触发GC回收时销毁
         */
        fun onDestroy(gxView: View?)
    }

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

    /**
     * 页面模板数据源，需要配合 GXTemplateItem.isPageMode=true使用
     */
    interface GXIExtensionPageTemplateSource {

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
            itemContainer: ViewGroup,
            gxMeasureSize: GXTemplateEngine.GXMeasureSize,
            gxTemplateItem: GXTemplateEngine.GXTemplateItem,
            gxExtendParams: GXTemplateEngine.GXExtendParams
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
        fun create(expVersion: String?, key: String?, value: Any): GXIExpression
        fun isTrue(expVersion: String?, key: String?, value: Any?): Boolean
    }

    interface GXIExtensionFunctionExpression {
        fun execute(functionName: String, params: LongArray): Long?
    }

    interface GXIExtensionDataBinding {
        fun create(expVersion: String?, value: Any): GXDataBinding?
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
     * Process some internal exception to prevent app crash.
     *
     * If you implement this extension, GXEngine not to crash at anywhere, it will return a default value.
     */
    interface GXIExtensionException {
        fun exception(exception: Exception)
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
        fun convert(propertyName: String, gxTemplateContext: GXTemplateContext, gridConfig: GXGridConfig): Any?
    }

    interface GXIExtensionScroll {
        fun convert(propertyName: String, gxTemplateContext: GXTemplateContext, scrollConfig: GXScrollConfig): Any? = null

        fun scrollIndex(gxTemplateContext: GXTemplateContext, container: GXContainer, extend: JSONObject?) {}
    }

    interface GXIExtensionLottieAnimation {
        fun create(): GXLottieAnimation?
    }

    class GXExtensionCompatibilityConfig {

        /**
         * 是否兼容容器数据的传递顺序
         */
        var isCompatibilityContainerDataPassSequence = false

        /**
         * 是否兼容容器嵌套模板的判定逻辑
         */
        var isCompatibilityContainerNestTemplateJudgementCondition = false

        /**
         * 是否兼容容器数据源抛异常的行为
         */
        var isPreventContainerDataSourceThrowException = false

        /**
         * 是否兼容IconFont的字体抛异常行为
         */
        var isPreventIconFontTypefaceThrowException = false

        /**
         *
         */
        var isPreventAccessibilityThrowException = false

        /**
         *
         */
        var isPreventFitContentThrowException = false

        var isCompatibilityDataBindingFitContent = false

        var isCompatibilityLottieOldDataStructure = false
    }

    internal var extensionBizMap: GXIExtensionBizMap? = null
    internal var extensionFunctionExpression: GXIExtensionFunctionExpression? = null
    internal var extensionDataBinding: GXIExtensionDataBinding? = null
    internal var extensionExpression: GXIExtensionExpression? = null
    internal var extensionColor: GXIExtensionColor? = null
    internal var extensionSize: GXIExtensionSize? = null
    internal var extensionDynamicProperty: GXIExtensionDynamicProperty? = null
    internal var extensionStaticProperty: GXIExtensionStaticProperty? = null
    internal var extensionGrid: GXIExtensionGrid? = null
    internal var extensionScroll: GXIExtensionScroll? = null
    internal var extensionException: GXIExtensionException? = null
    internal var extensionCompatibilityConfig: GXExtensionCompatibilityConfig? = null
    internal var extensionNodeEvent: GXIExtensionNodeEvent? = null
    internal var extensionContainerDataUpdate: GXIExtensionContainerDataUpdate? = null
    internal var extensionContainerItemBind: GXIExtensionContainerItemBind? = null
    internal var extensionLottieAnimation: GXIExtensionLottieAnimation? = null
    internal var gxItemViewLifecycleListener: GXIItemViewLifecycleListener? = null
    internal var gxPageItemViewLifecycleListener: GXIPageItemViewLifecycleListener? = null

    /**
     * 页面数据源
     */
    internal var pageSource: GXRegisterCenter.GXIExtensionPageTemplateSource? = null

    fun registerExtensionBizMapRelation(extensionBizMap: GXIExtensionBizMap): GXRegisterCenter {
        this.extensionBizMap = extensionBizMap
        return this
    }

    fun registerExtensionFunctionExpression(extension: GXIExtensionFunctionExpression): GXRegisterCenter {
        this.extensionFunctionExpression = extension
        return this
    }

    fun setExtensionPageTemplateSource(source: GXIExtensionPageTemplateSource) {
        pageSource = source
    }

    fun getExtensionPageTemplateSource(): GXIExtensionPageTemplateSource? {
        return pageSource
    }

    /**
     * @param source
     * @param priority [0,99]
     */
    fun registerExtensionTemplateSource(
        source: GXIExtensionTemplateSource, priority: Int = 0
    ): GXRegisterCenter {
        GXTemplateEngine.instance.data.templateSource.registerByPriority(source, priority)
        return this
    }

    /**
     * @param source
     * @param priority [0,99]
     */
    fun registerExtensionTemplateInfoSource(
        source: GXIExtensionTemplateInfoSource, priority: Int = 0
    ): GXRegisterCenter {
        GXTemplateEngine.instance.data.templateInfoSource.registerByPriority(source, priority)
        return this
    }

    fun registerExtensionDataBinding(databindingExtensionDataBinding: GXIExtensionDataBinding): GXRegisterCenter {
        this.extensionDataBinding = databindingExtensionDataBinding
        return this
    }

    fun registerExtensionExpression(extensionExpression: GXIExtensionExpression): GXRegisterCenter {
        this.extensionExpression = extensionExpression
        return this
    }

    fun registerExtensionColor(extensionColor: GXIExtensionColor): GXRegisterCenter {
        this.extensionColor = extensionColor
        return this
    }

    fun registerExtensionSize(extensionSize: GXIExtensionSize): GXRegisterCenter {
        this.extensionSize = extensionSize
        return this
    }

    fun registerExtensionDynamicProperty(extensionDynamicProperty: GXIExtensionDynamicProperty): GXRegisterCenter {
        this.extensionDynamicProperty = extensionDynamicProperty
        return this
    }

    fun registerExtensionStaticProperty(extensionStaticProperty: GXIExtensionStaticProperty): GXRegisterCenter {
        this.extensionStaticProperty = extensionStaticProperty
        return this
    }

    fun registerExtensionGrid(extensionGrid: GXIExtensionGrid): GXRegisterCenter {
        this.extensionGrid = extensionGrid
        return this
    }

    fun registerExtensionScroll(extensionScroll: GXIExtensionScroll): GXRegisterCenter {
        this.extensionScroll = extensionScroll
        return this
    }

    fun registerExtensionViewSupport(
        viewType: String, viewCreator: (Context) -> View
    ): GXRegisterCenter {
        GXViewFactory.viewCreatorSupport[viewType] = viewCreator
        return this
    }

    fun registerExtensionCompatibility(extensionCompatibility: GXExtensionCompatibilityConfig): GXRegisterCenter {
        this.extensionCompatibilityConfig = extensionCompatibility
        return this
    }

    fun registerExtensionNodeEvent(extensionNodeEvent: GXIExtensionNodeEvent): GXRegisterCenter {
        this.extensionNodeEvent = extensionNodeEvent
        return this
    }

    fun registerExtensionContainerDataUpdate(extensionContainerDataUpdate: GXIExtensionContainerDataUpdate): GXRegisterCenter {
        this.extensionContainerDataUpdate = extensionContainerDataUpdate
        return this
    }

    fun registerExtensionContainerItemBind(extensionContainerItemBind: GXIExtensionContainerItemBind): GXRegisterCenter {
        this.extensionContainerItemBind = extensionContainerItemBind
        return this
    }

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

    fun registerExtensionException(extensionException: GXIExtensionException): GXRegisterCenter {
        this.extensionException = extensionException
        return this
    }

    fun registerExtensionItemViewLifecycleListener(extension: GXIItemViewLifecycleListener): GXRegisterCenter {
        this.gxItemViewLifecycleListener = extension
        return this
    }

    fun registerExtensionPageItemViewLifecycleListener(extension: GXIPageItemViewLifecycleListener): GXRegisterCenter {
        this.gxPageItemViewLifecycleListener = extension
        return this
    }


    fun reset() {
        gxItemViewLifecycleListener = null
        gxPageItemViewLifecycleListener = null
        extensionBizMap = null
        extensionDataBinding = null
        extensionExpression = null
        extensionColor = null
        extensionSize = null
        extensionDynamicProperty = null
        extensionStaticProperty = null
        extensionScroll = null
        extensionGrid = null
        extensionNodeEvent = null
        extensionCompatibilityConfig = null
        extensionExpression = null
        extensionContainerDataUpdate = null
        extensionContainerItemBind = null
        extensionException = null
    }

    companion object {

        val instance by lazy {
            GXRegisterCenter()
        }
    }
}