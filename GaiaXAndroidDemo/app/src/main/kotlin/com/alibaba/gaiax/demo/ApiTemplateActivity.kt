package com.alibaba.gaiax.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.template.GXTemplate
import com.alibaba.gaiax.template.GXTemplateKey

class ApiTemplateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_api_template)

        // Init
        GXTemplateEngine.instance.init(this)

        // Register extension net template source
        val netTemplateSource = NetTemplateSource.instance
        GXRegisterCenter.instance.registerExtensionTemplateSource(netTemplateSource, 20)

        // Request Net Data
        NetRequest.instance.requestAsync(object : NetRequest.IDataCallback {
            override fun template(bizId: String, templateId: String, data: JSONObject) {
                // add template to net template source
                NetTemplateSource.instance.addTemplate(bizId, templateId, data)
            }
        })

        // Other
    }

    class NetRequest {

        interface IDataCallback {
            fun template(bizId: String, templateId: String, data: JSONObject)
        }

        fun requestAsync(callback: IDataCallback) {

            // request server

            // parse data

            // callback result
            callback.template("demo", "testId", JSONObject())
        }

        companion object {

            val instance by lazy {
                NetRequest()
            }
        }
    }

    class NetTemplateSource : GXRegisterCenter.GXIExtensionTemplateSource {

        companion object {
            val instance by lazy {
                NetTemplateSource()
            }
        }

        private val templateCache: MutableList<GXTemplate> = mutableListOf()

        override fun getTemplate(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplate? {
            return templateCache.firstOrNull { it.biz == gxTemplateItem.bizId && it.id == gxTemplateItem.templateId }
        }

        fun addTemplate(bizId: String, templateId: String, template: JSONObject) {
            val layer = template.getString(GXTemplateKey.GAIAX_INDEX_JSON) ?: ""
            val css = template.getString(GXTemplateKey.GAIAX_INDEX_CSS) ?: ""
            val dataBind = template.getString(GXTemplateKey.GAIAX_INDEX_DATABINDING) ?: ""
            val js = template.getString(GXTemplateKey.GAIAX_INDEX_JS) ?: ""
            templateCache.add(GXTemplate(templateId, bizId, -1, layer, css, dataBind, js))
        }
    }
}