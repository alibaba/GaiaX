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

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.render.view.GXViewKey
import com.alibaba.gaiax.template.animation.GXAnimationBinding
import com.alibaba.gaiax.template.factory.GXDataBindingFactory
import com.alibaba.gaiax.template.factory.GXExpressionFactory
import com.alibaba.gaiax.template.utils.GXCssFileParserUtils
import com.alibaba.gaiax.utils.Log
import com.alibaba.gaiax.utils.runE
import com.alibaba.gaiax.utils.safeParseToJson

/**
 * Status of the template after it has been fully parsed
 * @suppress
 */
data class GXTemplateInfo(
    val layer: GXLayer,
    val css: MutableMap<String, GXCss> = mutableMapOf(),
    val data: MutableMap<String, GXDataBinding>? = null,
    val event: MutableMap<String, GXEventBinding>? = null,
    val track: MutableMap<String, GXTrackBinding>? = null,
    val animation: MutableMap<String, GXAnimationBinding>? = null,
    val config: MutableMap<String, GXIExpression>? = null,
    val js: String? = null
) {

    var preload: Boolean = false

    var expVersion: String? = null

    var children: MutableList<GXTemplateInfo>? = null

    lateinit var template: GXTemplate

    lateinit var rawCssJson: JSONObject

    lateinit var rawDataBindingJson: JSONObject

    lateinit var rawLayerJson: JSONObject

    lateinit var rawConfigJson: JSONObject

    fun getConfig(data: JSONObject?): JSONObject {
        val result = JSONObject()
        val rawJson = data ?: JSONObject()
        this.config?.forEach {
            result[it.key] = it.value.value(rawJson) ?: ""
        }
        return result
    }

    fun getChildTemplateInfo(id: String): GXTemplateInfo? {
        children?.forEach {
            if (it.layer.id == id) {
                return it
            }
        }
        return null
    }

    val isJsExist: Boolean by lazy {
        checkJS()
    }

    private fun checkJS(): Boolean {
        val result = js?.isNotEmpty()
        if (result == true) {
            return true
        }
        children?.forEach {
            if (it.checkJS()) {
                return true
            }
        }
        return false
    }

    fun isTemplate() = layer.type == GXViewKey.VIEW_TYPE_GAIA_TEMPLATE

    fun findAnimation(id: String): GXAnimationBinding? {
        return animation?.get(id)
    }

    fun findEvent(id: String): GXEventBinding? {
        return event?.get(id)
    }

    fun findTrack(id: String): GXTrackBinding? {
        return track?.get(id)
    }

    fun findData(id: String): GXDataBinding? {
        return data?.get(id)
    }

    fun findCss(id: String): GXCss? {
        return css[id]
    }

    fun findLayer(id: String): GXLayer? {
        return findLayerCache[id] ?: findLayer(id, layer)?.apply {
            findLayerCache[id] = this@apply
        }
    }

    private val findLayerCache = mutableMapOf<String, GXLayer>()

    private fun findLayer(id: String, layer: GXLayer): GXLayer? {
        if (id == layer.id) {
            return layer
        }
        layer.layers.forEach {
            val nextLayer = findLayer(id, it)
            if (nextLayer != null) {
                return nextLayer
            }
        }
        return null
    }

    /**
     * 重置模板中的缓存
     */
    fun reset() {
        reset(this)
    }

    private fun reset(gxTemplateInfo: GXTemplateInfo) {
        gxTemplateInfo.css.forEach {
            it.value.flexBox.reset()
        }
        gxTemplateInfo.children?.forEach {
            reset(it)
        }
    }

    /**
     * TODO 未实现
     */
    fun isFlexibleHeight(): Boolean {
        return false
    }

    companion object {

        private const val TAG = "GXTemplateInfo"

        fun parseCss(value: String): JSONObject {
            return GXCssFileParserUtils.instance.parseToJson(value)
        }

        fun parseLayer(value: String): JSONObject {
            return value.safeParseToJson()
        }

        fun parseDataBinding(value: String): JSONObject {
            return value.safeParseToJson()
        }

        fun createTemplate(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplateInfo {
            val template = GXTemplateEngine.instance.data.templateSource.getTemplate(templateItem)
            val templateInfo = createTemplate(template)
            return templateInfo.apply { initChildren(this, templateItem) }
        }

        private fun createTemplate(template: GXTemplate): GXTemplateInfo {

            Log.runE(TAG) { "createTemplate biz=${template.biz} id=${template.id} " }
            Log.runE(TAG) { "createTemplate layer=${template.layer}" }
            Log.runE(TAG) { "createTemplate css=${template.css}" }
            Log.runE(TAG) { "createTemplate dataBind=${template.dataBind}" }
            Log.runE(TAG) { "createTemplate js=${template.js}" }

            // Hierarchical data
            val layerJson = template.layer.safeParseToJson()

            // If the core data is invalid, throw exception directly
            if (layerJson.isEmpty()) {
                throw IllegalArgumentException("Template layer mustn't empty")
            }

            // Style data
            val cssJson = GXCssFileParserUtils.instance.parseToJson(template.css)

            // Data binding data
            val dataBindFileJson = template.dataBind.safeParseToJson()

            // JS code content
            val jsSrc = template.js

            // Data expression
            val dataExpJson =
                dataBindFileJson.getJSONObject(GXTemplateKey.GAIAX_DATA) ?: JSONObject()

            // Data expression
            val eventJson =
                dataBindFileJson.getJSONObject(GXTemplateKey.GAIAX_EVENT) ?: JSONObject()

            // Data expression
            val trackJson =
                dataBindFileJson.getJSONObject(GXTemplateKey.GAIAX_TRACK) ?: JSONObject()

            // The configuration data
            val configJson =
                dataBindFileJson.getJSONObject(GXTemplateKey.GAIAX_CONFIG) ?: JSONObject()

            // Animation data
            val animationJson =
                dataBindFileJson.getJSONObject(GXTemplateKey.GAIAX_ANIMATION) ?: JSONObject()

            // 兼容 edge-insets
            layerJson.getString(GXTemplateKey.GAIAX_LAYER_EDGE_INSETS)?.let {
                if (!cssJson.containsKey(template.id)) {
                    cssJson[template.id] = JSONObject()
                }
                cssJson.getJSONObject(template.id)?.put(GXTemplateKey.GAIAX_LAYER_EDGE_INSETS, it)
            }

            val preload = layerJson.getBooleanValue(GXTemplateKey.GAIAX_LAYER_PRELOAD)

            val expVersion: String? = layerJson.getString(GXTemplateKey.GAIAX_LAYER_EXP_VERSION)

            val layer = GXLayer.create(layerJson)
            val css = createCss(layer, cssJson)
            val data = createData(expVersion, dataExpJson)
            val event = createEvent(expVersion, eventJson)
            val track = createTrack(expVersion, trackJson)
            val config = createConfig(expVersion, configJson)
            val animation = createAnimation(expVersion, animationJson)
            val js = jsSrc.ifEmpty { null }

            return GXTemplateInfo(layer, css, data, event, track, animation, config, js).apply {
                this.template = template
                this.rawCssJson = cssJson
                this.rawDataBindingJson = dataBindFileJson
                this.rawLayerJson = layerJson
                this.rawConfigJson = configJson
                this.expVersion = expVersion
                this.preload = preload
            }
        }

        private fun createCss(layer: GXLayer, srcCssJson: JSONObject): MutableMap<String, GXCss> {
            val value: MutableMap<String, GXCss> = mutableMapOf()
            return createCss(value, srcCssJson, layer)
        }

        private fun createCss(
            value: MutableMap<String, GXCss>, srcCssJson: JSONObject, layer: GXLayer
        ): MutableMap<String, GXCss> {
            val layerId = layer.id
            val cssId = layer.css
            val cssByCssId = srcCssJson.getJSONObject(cssId) ?: JSONObject()
            val cssByLayerId = srcCssJson.getJSONObject(layerId) ?: JSONObject()

            // NOTE: If a style ID value does not exist in the style file, no longer generate a useless CssCompose
            // Otherwise, the Grid height calculation will result in a reference to an undefined CssCompose and the height calculation will be incorrect.
            if (cssByCssId.isNotEmpty() || cssByLayerId.isNotEmpty()) {
                val targetCss = JSONObject()
                targetCss.putAll(cssByCssId)
                targetCss.putAll(cssByLayerId)
                value[layerId] = GXCss.create(targetCss)
            }

            // Continue to recursively traverse the current layout tree
            layer.layers.forEach {
                createCss(value, srcCssJson, it)
            }
            return value
        }

        private fun createData(
            expVersion: String?, dataJson: JSONObject
        ): MutableMap<String, GXDataBinding>? {
            return if (!dataJson.isEmpty()) {
                val data: MutableMap<String, GXDataBinding> = mutableMapOf()
                for (entry in dataJson) {
                    val entryId = entry.key
                    val entryValue = entry.value
                    if (entryId != null && entryValue != null) {
                        val valueBinding = GXDataBindingFactory.create(expVersion, entryValue)
                        if (valueBinding != null) {
                            data[entryId] = valueBinding
                        }
                    }
                }
                data
            } else {
                null
            }
        }

        private fun createEvent(
            expVersion: String?, eventJson: JSONObject
        ): MutableMap<String, GXEventBinding>? {
            return if (!eventJson.isEmpty()) {
                val value: MutableMap<String, GXEventBinding> = mutableMapOf()
                for (entry in eventJson) {
                    val id = entry.key
                    val expression = entry.value
                    if (id != null && expression != null) {
                        if (id.isNotEmpty()) {
                            GXExpressionFactory.create(expVersion, expression)?.let {
                                value[id] = GXEventBinding(it)
                            }
                        }
                    }
                }
                value
            } else {
                null
            }
        }

        private fun createTrack(
            expVersion: String?, eventJson: JSONObject
        ): MutableMap<String, GXTrackBinding>? {
            return if (!eventJson.isEmpty()) {
                val value: MutableMap<String, GXTrackBinding> = mutableMapOf()
                for (entry in eventJson) {
                    val id = entry.key
                    val expression = entry.value
                    if (id != null && expression != null) {
                        if (id.isNotEmpty()) {
                            GXExpressionFactory.create(expVersion, expression)?.let {
                                value[id] = GXTrackBinding(it)
                            }
                        }
                    }
                }
                value
            } else {
                null
            }
        }

        private fun createAnimation(
            expVersion: String?, animationJson: JSONObject
        ): MutableMap<String, GXAnimationBinding>? {
            return if (!animationJson.isEmpty()) {
                val value: MutableMap<String, GXAnimationBinding> = mutableMapOf()

                for (entry in animationJson) {
                    val id = entry.key
                    val expression = entry.value
                    if (id != null && expression != null && id.isNotEmpty() && expression is JSONObject) {
                        GXAnimationBinding.create(expVersion, expression)?.let {
                            value[id] = it
                        }
                    }
                }

                value
            } else {
                null
            }
        }

        private fun createConfig(
            expVersion: String?, configJson: JSONObject
        ): MutableMap<String, GXIExpression>? {
            return if (!configJson.isEmpty()) {
                val value: MutableMap<String, GXIExpression> = mutableMapOf()

                for (entry in configJson) {
                    val id = entry.key
                    val expression = entry.value
                    if (id != null && expression != null && id.isNotEmpty()) {
                        GXExpressionFactory.create(expVersion, expression)?.let { value[id] = it }
                    }
                }
                value
            } else {
                null
            }

        }

        private fun forChildrenTemplate(layer: GXLayer, function: (layer: GXLayer) -> Unit) {
            layer.layers.forEach {
                if (it.isNestChildTemplateType()) {
                    function(it)
                }
                forChildrenTemplate(it, function)
            }
        }

        private fun initChildren(
            templateInfo: GXTemplateInfo, templateItem: GXTemplateEngine.GXTemplateItem
        ) {
            forChildrenTemplate(templateInfo.layer) {
                val gxTemplateItem = GXTemplateEngine.GXTemplateItem(
                    templateItem.context, templateItem.bizId, it.id
                ).apply {
                    this.isLocal = templateItem.isLocal
                    this.templateVersion = templateItem.templateVersion
                    this.isPageMode = templateItem.isPageMode
                }
                val childTemplate = createTemplate(gxTemplateItem)
                if (templateInfo.children == null) {
                    templateInfo.children = mutableListOf()
                }
                templateInfo.children?.add(childTemplate)
            }
        }
    }

}
