package com.alibaba.gaiax.demo.source

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.data.cache.GXTemplateInfoSource
import com.alibaba.gaiax.template.GXTemplate
import com.alibaba.gaiax.template.GXTemplateKey

class GXManualPushSource : GXRegisterCenter.GXIExtensionTemplateSource {


    companion object {
        val instance by lazy {
            GXManualPushSource()
        }
    }

    private val cache = mutableMapOf<String, GXTemplate>()

    override fun getTemplate(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplate? {
        return cache[templateItem.templateId]
    }

    fun addTemplate(templateId: String, data: JSONObject) {
        GXTemplateInfoSource.instance.clean()
        val layer = data.getString(GXTemplateKey.GAIAX_INDEX_JSON) ?: ""
        val css = data.getString(GXTemplateKey.GAIAX_INDEX_CSS) ?: ""
        val dataBind = data.getString(GXTemplateKey.GAIAX_INDEX_DATABINDING) ?: ""
        val js = data.getString(GXTemplateKey.GAIAX_INDEX_JS) ?: ""
        cache[templateId] = GXTemplate(templateId, "manualpush", -1, layer, css, dataBind, js)
    }
}