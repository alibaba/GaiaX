package com.alibaba.gaiax

import android.util.Size
import com.alibaba.gaiax.data.GXITemplateInfoSource
import com.alibaba.gaiax.template.GXTemplate
import com.alibaba.gaiax.template.GXTemplateInfo

/**
 * GaiaX register center. For extended functionality.
 *
 * Basic usage:
 * ```
 * // Register outside datasource.
 * GXRegisterCenter.instance.registerTemplateSource(object : GXRegisterCenter.GXITemplateSource {
 *      private val cache = mutableMapOf<String, GXTemplate>()
 *
 *      override fun getTemplate(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplate? {
 *          return cache[templateItem.templateId]
 *      }
 * })
 * ```
 */
class GXRegisterCenter {

    /**
     * Business service interface
     *
     * Unused
     * @suppress
     */
    interface GXBizServiceInterface {

        /**
         * Responsive screen size
         */
        fun screenSize(): Size? = null

        /**
         * Gets the width of the reactive view
         */
        fun valueForRule(rule: String, containerWidth: Float, gap: Float, margin: Float): Float? =
            null
    }

    /**
     * Template data source interface
     */
    interface GXITemplateSource : GXITemplateInfoSource, com.alibaba.gaiax.data.GXITemplateSource {

        /**
         * Get template resolution information
         */
        override fun getTemplateInfo(
            templateSource: com.alibaba.gaiax.data.GXITemplateSource,
            templateItem: GXTemplateEngine.GXTemplateItem
        ): GXTemplateInfo? {
            return super.getTemplateInfo(templateSource, templateItem)
        }

        /**
         * Get template raw data
         */
        override fun getTemplate(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplate? = null
    }

    /**
     * Registering a Service Template
     *
     * @param bizId template service id
     * @param directory Resource path，eg: assets/test
     */
    fun registerBizMapRelation(bizId: String, directory: String) {
        GXTemplateEngine.instance.data.registerAssetsBizMapRelation(bizId, directory)
    }

    /**
     * Unregister the service template
     *
     * @param bizId template service id
     */
    fun unregisterBizMapRelation(bizId: String) {
        GXTemplateEngine.instance.data.unregisterAssetsBizMapRelation(bizId)
    }

    /**
     * Register external data sources (higher priority after registration)
     *
     * @param source External data source implementation
     */
    fun registerTemplateSource(source: GXITemplateSource) {
        GXTemplateEngine.instance.data.templateInfoSource.sources.add(0, source)
        GXTemplateEngine.instance.data.templateSource.sources.add(0, source)
    }

    /**
     * Unregister the external data sources
     */
    fun unregisterTemplateSource(source: GXITemplateSource) {
        GXTemplateEngine.instance.data.templateInfoSource.sources.remove(source)
        GXTemplateEngine.instance.data.templateSource.sources.remove(source)
    }

    companion object {

        val instance by lazy {
            GXRegisterCenter()
        }
    }
}