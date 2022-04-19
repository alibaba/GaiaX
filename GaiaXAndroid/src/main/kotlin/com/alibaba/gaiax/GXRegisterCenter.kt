package com.alibaba.gaiax

import android.view.ViewGroup
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXINodeEvent
import com.alibaba.gaiax.render.node.GXTemplateNode
import com.alibaba.gaiax.render.view.GXViewFactory
import com.alibaba.gaiax.render.view.container.GXContainerViewAdapter
import com.alibaba.gaiax.template.*

/**
 * GaiaX register center. For extended functionality.
 *
 * Basic usage:
 * ```
 * ```
 */
class GXRegisterCenter {

    /**
     * Template data info source interface
     */
    interface GXITemplateInfoSource {

        /**
         * Get template info data
         */
        fun getTemplateInfo(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplateInfo?
    }

    /**
     * Template data source interface
     */
    interface GXITemplateSource {

        /**
         * Get template raw data
         */
        fun getTemplate(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplate? = null
    }

    interface GXIProcessContainerItemBind {
        fun bindViewHolder(
            tag: Any?,
            childItemContainer: ViewGroup,
            childMeasureSize: GXTemplateEngine.GXMeasureSize,
            childTemplateItem: GXTemplateEngine.GXTemplateItem,
            childItemPosition: Int,
            childVisualNestTemplateNode: GXTemplateNode,
            childItemData: JSONObject
        ): Any?
    }

    interface GXIProcessContainerDataUpdate {
        fun update(gxTemplateContext: GXTemplateContext, gxContainerViewAdapter: GXContainerViewAdapter, old: JSONArray, new: JSONArray)
    }

    interface GXIProcessNodeEvent {
        fun create(): GXINodeEvent
    }

    interface GXIProcessExpression {
        fun create(value: Any): GXIExpression
    }

    interface GXIProcessDataBinding {
        fun create(value: JSONObject): GXDataBinding?
    }

    interface GXIProcessColor {
        fun convert(value: String): Int?
    }

    interface GXIProcessSize {
        fun create(value: String): Float?
        fun convert(value: Float): Float?
    }

    /**
     * Dynamic property have context, but static property haven't context.
     */
    interface GXIProcessDynamicProperty {

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
    interface GXIProcessStaticProperty {

        data class GXParams(val propertyName: String, val value: Any)

        fun convert(params: GXParams): Any?
    }

    interface GXIProcessBizMap {
        fun convert(item: GXTemplateEngine.GXTemplateItem)
    }

    interface GXIProcessGrid {
        fun convert(propertyName: String, gxTemplateContext: GXTemplateContext, gridConfig: GXGridConfig): Any?
    }

    interface GXIProcessScroll {
        fun convert(propertyName: String, gxTemplateContext: GXTemplateContext, scrollConfig: GXScrollConfig): Any?
    }

    interface GXIProcessCompatible {

        /**
         * 是否兼容容器数据的传递顺序
         */
        fun isCompatibleContainerDataPassSequence() = false

        /**
         * 是否兼容容器数据源抛异常的行为
         */
        fun isPreventContainerDataSourceThrowException() = false

        /**
         * 是否兼容IconFont的字体抛异常行为
         */
        fun isPreventIconFontTypefaceThrowException() = false
    }

    internal var processBizMap: GXIProcessBizMap? = null

    fun registerProcessBizMapRelation(processBizMap: GXIProcessBizMap): GXRegisterCenter {
        this.processBizMap = processBizMap
        return this
    }

    /**
     * @param source
     * @param priority [0,99]
     */
    fun registerTemplateSource(source: GXITemplateSource, priority: Int = 0): GXRegisterCenter {
        GXTemplateEngine.instance.data.templateSource.addPriority(source, priority)
        return this
    }

    /**
     * @param source
     * @param priority [0,99]
     */
    fun registerTemplateInfoSource(source: GXITemplateInfoSource, priority: Int = 0): GXRegisterCenter {
        GXTemplateEngine.instance.data.templateInfoSource.addPriority(source, priority)
        return this
    }

    internal var processDataBinding: GXIProcessDataBinding? = null

    fun registerProcessDataBinding(databindingProcessDataBinding: GXIProcessDataBinding): GXRegisterCenter {
        this.processDataBinding = databindingProcessDataBinding
        return this
    }

    internal var processExpression: GXIProcessExpression? = null

    fun registerProcessExpression(processExpression: GXIProcessExpression): GXRegisterCenter {
        this.processExpression = processExpression
        return this
    }

    internal var processColor: GXIProcessColor? = null

    fun registerProcessColor(processColor: GXIProcessColor): GXRegisterCenter {
        this.processColor = processColor
        return this
    }

    internal var processSize: GXIProcessSize? = null

    fun registerProcessSize(processSize: GXIProcessSize): GXRegisterCenter {
        this.processSize = processSize
        return this
    }

    internal var processDynamicProperty: GXIProcessDynamicProperty? = null

    fun registerProcessDynamicProperty(processDynamicProperty: GXIProcessDynamicProperty): GXRegisterCenter {
        this.processDynamicProperty = processDynamicProperty
        return this
    }

    internal var processStaticProperty: GXIProcessStaticProperty? = null

    fun registerProcessStaticProperty(processStaticProperty: GXIProcessStaticProperty): GXRegisterCenter {
        this.processStaticProperty = processStaticProperty
        return this
    }

    internal var processGrid: GXIProcessGrid? = null

    fun registerProcessGrid(processGrid: GXIProcessGrid): GXRegisterCenter {
        this.processGrid = processGrid
        return this
    }

    internal var processScroll: GXIProcessScroll? = null

    fun registerProcessScroll(processScroll: GXIProcessScroll): GXRegisterCenter {
        this.processScroll = processScroll
        return this
    }

    fun registerViewSupport(viewType: String, clazz: Class<*>): GXRegisterCenter {
        GXViewFactory.viewSupport[viewType] = clazz
        return this
    }

    internal var processCompatible: GXIProcessCompatible? = null

    fun registerProcessCompatible(processCompatible: GXIProcessCompatible): GXRegisterCenter {
        this.processCompatible = processCompatible
        return this
    }

    internal var processNodeEvent: GXIProcessNodeEvent? = null

    fun registerProcessNodeEvent(processNodeEvent: GXIProcessNodeEvent): GXRegisterCenter {
        this.processNodeEvent = processNodeEvent
        return this
    }

    internal var processContainerDataUpdate: GXIProcessContainerDataUpdate? = null

    fun registerProcessContainerDataUpdate(processContainerDataUpdate: GXIProcessContainerDataUpdate): GXRegisterCenter {
        this.processContainerDataUpdate = processContainerDataUpdate
        return this
    }

    internal var processContainerItemBind: GXIProcessContainerItemBind? = null

    fun registerProcessContainerItemBind(processContainerItemBind: GXIProcessContainerItemBind): GXRegisterCenter {
        this.processContainerItemBind = processContainerItemBind
        return this
    }

    fun reset() {
        processNodeEvent = null
        processCompatible = null
        processScroll = null
        processGrid = null
        processBizMap = null
        processColor = null
        processExpression = null
        processDynamicProperty = null
        processStaticProperty = null
        processSize = null
    }

    companion object {

        val instance by lazy {
            GXRegisterCenter()
        }
    }
}