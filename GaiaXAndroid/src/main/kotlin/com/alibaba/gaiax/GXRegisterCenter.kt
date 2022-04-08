package com.alibaba.gaiax

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
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

    interface GXIExpressionProcessing {
        fun createProcessing(value: Any): GXIExpression
    }

    interface GXIDataBindingProcessing {
        fun createProcessing(value: JSONObject): GXDataBinding?
    }

    interface GXIColorProcessing {
        fun convertProcessing(value: String): Int?
    }

    interface GXISizeProcessing {
        fun createProcessing(value: String): Float?
        fun convertProcessing(value: Float): Float?
    }

    interface GXIPostPositionPropertyProcessing {

        data class GXParams(val propertyName: String, val value: Any) {
            var gridConfig: GXGridConfig? = null
            var flexBox: GXFlexBox? = null
            var cssStyle: GXStyle? = null
        }

        fun convertProcessing(params: GXParams): Any?
    }

    interface GXIPrePositionPropertyProcessing {

        data class GXParams(val propertyName: String, val value: Any) {
        }

        fun convertProcessing(params: GXParams): Any?
    }

    interface GXIBizMapProcessing {
        fun convertProcessing(templateItem: GXTemplateEngine.GXTemplateItem)
    }

    interface GXIGridProcessing {
        fun convertProcessing(propertyName: String, context: GXTemplateContext, gridConfig: GXGridConfig): Any?
    }

    interface GXIScrollProcessing {
        fun convertProcessing(propertyName: String, context: GXTemplateContext, scrollConfig: GXScrollConfig): Any?
    }

    internal var bizMapProcessing: GXIBizMapProcessing? = null

    fun registerBizMapRelationProcessing(bizMapProcessing: GXIBizMapProcessing): GXRegisterCenter {
        this.bizMapProcessing = bizMapProcessing
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

    internal var databindingProcessing: GXIDataBindingProcessing? = null

    fun registerDataBindingProcessing(databindingProcessing: GXIDataBindingProcessing): GXRegisterCenter {
        this.databindingProcessing = databindingProcessing
        return this
    }

    internal var expressionProcessing: GXIExpressionProcessing? = null

    fun registerExpressionProcessing(expressionProcessing: GXIExpressionProcessing): GXRegisterCenter {
        this.expressionProcessing = expressionProcessing
        return this
    }

    internal var colorProcessing: GXIColorProcessing? = null

    fun registerColorProcessing(colorProcessing: GXIColorProcessing): GXRegisterCenter {
        this.colorProcessing = colorProcessing
        return this
    }

    internal var sizeProcessing: GXISizeProcessing? = null

    fun registerSizeProcessing(sizeProcessing: GXISizeProcessing): GXRegisterCenter {
        this.sizeProcessing = sizeProcessing
        return this
    }

    internal var postPositionPropertyProcessing: GXIPostPositionPropertyProcessing? = null

    fun registerPostPositionPropertyProcessing(postPositionPropertyProcessing: GXIPostPositionPropertyProcessing): GXRegisterCenter {
        this.postPositionPropertyProcessing = postPositionPropertyProcessing
        return this
    }

    internal var prePositionPropertyProcessing: GXIPrePositionPropertyProcessing? = null

    fun registerPrePositionPropertyProcessing(prePositionPropertyProcessing: GXIPrePositionPropertyProcessing): GXRegisterCenter {
        this.prePositionPropertyProcessing = prePositionPropertyProcessing
        return this
    }

    internal var gridProcessing: GXIGridProcessing? = null

    fun registerGridProcessing(gridProcessing: GXIGridProcessing): GXRegisterCenter {
        this.gridProcessing = gridProcessing
        return this
    }

    internal var scrollProcessing: GXIScrollProcessing? = null

    fun registerScrollProcessing(scrollProcessing: GXIScrollProcessing): GXRegisterCenter {
        this.scrollProcessing = scrollProcessing
        return this
    }

    companion object {

        val instance by lazy {
            GXRegisterCenter()
        }
    }
}