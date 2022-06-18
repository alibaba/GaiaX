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

package com.alibaba.gaiax.data.assets

import android.content.Context
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.template.GXTemplate
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * The template file don't have suffix - .gaiax
 * @suppress
 */
class GXAssetsBinaryWithoutSuffixTemplate(val context: Context) :
    GXRegisterCenter.GXIExtensionTemplateSource {

    private val templateCache = mutableMapOf<String, MutableList<GXTemplate>>()

    private fun getTemplateContents(templateItem: GXTemplateEngine.GXTemplateItem): ByteArray? {
        try {
            val bundlePath = templateItem.bundle.ifEmpty { templateItem.bizId }
            return context.resources?.assets?.open("$bundlePath/${templateItem.templateId}")
                ?.use { it.readBytes() }
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    override fun getTemplate(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplate? {

        // 1. Determine if the end product of Assets exists, and if so, use it directly, and return
        val memoryTemplate = getFromCache(gxTemplateItem.bizId, gxTemplateItem.templateId)
        if (memoryTemplate != null) {
            return memoryTemplate
        }

        // 2. Check whether the compressed package exists in Assets. If the package exists, decompress it to the local PC and read data into the memory
        val templateContents = getTemplateContents(gxTemplateItem)
        if (templateContents != null) {
            val gxTemplate = createTemplate(
                templateContents,
                gxTemplateItem.bizId,
                gxTemplateItem.templateId
            )
            gxTemplate.type = "assets_binary"
            addToCache(gxTemplate)
            return getFromCache(gxTemplateItem.bizId, gxTemplateItem.templateId)
        }
        return null
    }

    private fun createTemplate(
        bytes: ByteArray,
        templateBiz: String,
        templateId: String
    ): GXTemplate {
        val binaryData = GXBinParser.parser(bytes)
        val layer = binaryData.getString(GXTemplateKey.GAIAX_LAYER)
            ?: throw IllegalArgumentException("Layer mustn't empty, templateBiz = $templateBiz, templateId = $templateId")
        val css = binaryData.getString(GXTemplateKey.GAIAX_CSS) ?: ""
        val dataBind = binaryData.getString(GXTemplateKey.GAIAX_DATABINDING) ?: ""
        val js = binaryData.getString(GXTemplateKey.GAIAX_JS) ?: ""
        return GXTemplate(templateId, templateBiz, -1, layer, css, dataBind, js)
    }

    private fun getFromCache(templateBiz: String, templateId: String) =
        templateCache[templateBiz]?.filter { it.id == templateId }?.maxBy { it.version }

    private fun addToCache(template: GXTemplate) {
        var bizTemplates = templateCache[template.biz]
        if (bizTemplates == null) {
            bizTemplates = mutableListOf()
            templateCache[template.biz] = bizTemplates
        }
        bizTemplates.add(template)
    }
}