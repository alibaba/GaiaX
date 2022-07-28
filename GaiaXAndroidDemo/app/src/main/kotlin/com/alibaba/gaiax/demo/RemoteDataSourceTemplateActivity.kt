package com.alibaba.gaiax.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.data.assets.GXBinParser
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.demo.utils.UiExecutor
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.GXTemplate
import com.alibaba.gaiax.template.GXTemplateKey
import java.util.concurrent.Executors

class RemoteDataSourceTemplateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_remote_data_source_template)

        // Init
        GXTemplateEngine.instance.init(this)

        // Register remote data template source to GaiaXSDK
        val netTemplateSource = RemoteDataSourceTemplateSource.instance
        GXRegisterCenter.instance.registerExtensionTemplateSource(netTemplateSource, 20)

        // Request Net Data
        RemoteDataSourceNetRequest.instance.requestAsync(
            this,
            JSONObject(),
            object : RemoteDataSourceNetRequest.IDataCallback {
                override fun template(bizId: String, templateId: String, data: ByteArray?) {
                    // add template to net template source
                    RemoteDataSourceTemplateSource.instance.addTemplate(bizId, templateId, data)
                }

                override fun finish() {
                    // Net finish
                    toBindTemplate()
                }
            })

        // Other
    }

    private fun toBindTemplate() {

        // 模板参数
        // 去加载远程模板业务下的gx-vertical-item模板
        val params = GXTemplateEngine.GXTemplateItem(this, "remote", "gx-vertical-item")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(100F.dpToPx(), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                this,
                "data/vertical-item.json"
            )
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.root).addView(view, 0)
    }

    class RemoteDataSourceNetRequest {

        interface IDataCallback {
            fun template(bizId: String, templateId: String, data: ByteArray?)
            fun finish()
        }

        fun requestAsync(
            activity: RemoteDataSourceTemplateActivity,
            netParams: JSONObject,
            callback: IDataCallback
        ) {
            // Thread
            Executors.newSingleThreadExecutor().execute {
                // request server
                val response = AssetsUtils.parseAssets(
                    activity,
                    "remote_data_source/api_response.json"
                )

                // parse data
                response.getJSONArray("templates")?.forEach {
                    val template = (it as JSONObject)
                    val templateBiz = template.getString("templateBiz")
                    val templateId = template.getString("templateId")
                    val templateBytes = getTemplateContents(activity, templateId)

                    // callback result
                    callback.template(templateBiz, templateId, templateBytes)
                }

                UiExecutor.action {
                    callback.finish()
                }
            }
        }

        // mock net
        private fun getTemplateContents(
            activity: RemoteDataSourceTemplateActivity,
            templateId: String
        ): ByteArray? {
            return try {
                activity.resources?.assets?.open("remote_data_source/templates/${templateId}")
                    ?.use { it.readBytes() }
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

        companion object {

            val instance by lazy {
                RemoteDataSourceNetRequest()
            }
        }
    }

    /**
     * 远程模板的数据源
     */
    class RemoteDataSourceTemplateSource : GXRegisterCenter.GXIExtensionTemplateSource {

        companion object {
            val instance by lazy {
                RemoteDataSourceTemplateSource()
            }
        }

        private val templateCache: MutableList<GXTemplate> = mutableListOf()

        override fun getTemplate(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplate? {
            return templateCache.firstOrNull { it.biz == gxTemplateItem.bizId && it.id == gxTemplateItem.templateId }
        }

        fun addTemplate(templateBiz: String, templateId: String, bytes: ByteArray?) {
            if (bytes != null) {
                val binaryData = GXBinParser.parser(bytes)
                val layer = binaryData.getString(GXTemplateKey.GAIAX_LAYER)
                    ?: throw IllegalArgumentException("Layer mustn't empty, templateBiz = $templateBiz, templateId = $templateId")
                val css = binaryData.getString(GXTemplateKey.GAIAX_CSS) ?: ""
                val dataBind = binaryData.getString(GXTemplateKey.GAIAX_DATABINDING) ?: ""
                val js = binaryData.getString(GXTemplateKey.GAIAX_JS) ?: ""
                val template = GXTemplate(templateId, templateBiz, -1, layer, css, dataBind, js)
                templateCache.add(template)
            }
        }
    }
}