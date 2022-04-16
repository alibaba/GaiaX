package com.alibaba.gaiax

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.render.view.GXViewFactory
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
        fun getTemplateInfo(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplateInfo?
    }

    /**
     * Template data source interface
     */
    interface GXITemplateSource {

        /**
         * Get template raw data
         */
        fun getTemplate(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplate? = null
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

    interface GXIProcessPostPositionProperty {

        data class GXParams(val propertyName: String, val value: Any) {
            var gridConfig: GXGridConfig? = null
            var flexBox: GXFlexBox? = null
            var cssStyle: GXStyle? = null
        }

        fun convert(params: GXParams): Any?
    }

    interface GXIProcessPrePositionProperty {

        data class GXParams(val propertyName: String, val value: Any)

        fun convert(params: GXParams): Any?
    }

    interface GXIProcessBizMap {
        fun convert(templateItem: GXTemplateEngine.GXTemplateItem)
    }

    interface GXIProcessGrid {
        fun convert(propertyName: String, context: GXTemplateContext, gridConfig: GXGridConfig): Any?
    }

    interface GXIProcessScroll {
        fun convert(propertyName: String, context: GXTemplateContext, scrollConfig: GXScrollConfig): Any?
    }

    interface GXIProcessEvent {
        fun strategy(context: GXTemplateContext, node: GXNode, templateData: JSONObject)
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

    internal var databindingProcessDataBinding: GXIProcessDataBinding? = null

    fun registerProcessDataBinding(databindingProcessDataBinding: GXIProcessDataBinding): GXRegisterCenter {
        this.databindingProcessDataBinding = databindingProcessDataBinding
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

    internal var processPostPositionProperty: GXIProcessPostPositionProperty? = null

    fun registerProcessPostPositionProperty(processPostPositionProperty: GXIProcessPostPositionProperty): GXRegisterCenter {
        this.processPostPositionProperty = processPostPositionProperty
        return this
    }

    internal var processPrePositionProperty: GXIProcessPrePositionProperty? = null

    fun registerProcessPrePositionProperty(processPrePositionProperty: GXIProcessPrePositionProperty): GXRegisterCenter {
        this.processPrePositionProperty = processPrePositionProperty
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

    internal var processEvent: GXIProcessEvent? = null

    fun registerProcessEvent(processEvent: GXIProcessEvent): GXRegisterCenter {
        this.processEvent = processEvent
        return this
    }

    companion object {

        val instance by lazy {
            GXRegisterCenter()
        }
    }
}