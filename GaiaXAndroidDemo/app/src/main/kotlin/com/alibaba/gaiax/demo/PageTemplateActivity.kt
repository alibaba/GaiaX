package com.alibaba.gaiax.demo

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.GXTemplate

class PageTemplateActivity : AppCompatActivity() {

    class GXPageSource(val context: Context) : GXRegisterCenter.GXIExtensionPageTemplateSource {


        private val templateCache = mutableMapOf<String, MutableList<GXTemplate>>()

        override fun getTemplate(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplate? {
            // 1. 判断Assets.gaiax最终产物是否存在，如果存在直接使用，并返回
            val memoryTemplate = getFromCache(gxTemplateItem.bizId, gxTemplateItem.templateId)
            if (memoryTemplate != null) {
                return memoryTemplate
            }

            // 2.
            val indexPath = "assets_data_source/${gxTemplateItem.bizId}/${gxTemplateItem.templateId}/index.json"
            val cssPath = "assets_data_source/${gxTemplateItem.bizId}/${gxTemplateItem.templateId}/index.css"
            val databindingPath = "assets_data_source/${gxTemplateItem.bizId}/${gxTemplateItem.templateId}/index.databinding"
            val jsPath = "assets_data_source/${gxTemplateItem.bizId}/${gxTemplateItem.templateId}/index.js"

            val index = readFileFromAssets(indexPath)
            val css = readFileFromAssets(cssPath)
            val databinding = readFileFromAssets(databindingPath)
            val js = readFileFromAssets(jsPath)
            if (index != null) {
                val gxTemplate = GXTemplate(
                    gxTemplateItem.templateId,
                    gxTemplateItem.bizId,
                    -1,
                    index,
                    css ?: "",
                    databinding ?: "",
                    js ?: ""
                )
                gxTemplate.type = "assets"
                addToCache(gxTemplate)
                return gxTemplate
            }

            return null
        }

        private fun readFileFromAssets(path: String): String? {
            return try {
                context.resources?.assets?.open(path)?.bufferedReader(Charsets.UTF_8)
                    .use { it?.readText() }
            } catch (e: Exception) {
                null
            }
        }

        private fun getFromCache(templateBiz: String, templateId: String) =
            templateCache[templateBiz]?.filter { it.id == templateId }?.maxByOrNull { it.version }

        private fun addToCache(gxTemplate: GXTemplate) {
            var bizTemplates = templateCache[gxTemplate.biz]
            if (bizTemplates == null) {
                bizTemplates = mutableListOf()
                templateCache[gxTemplate.biz] = bizTemplates
            }
            bizTemplates.add(gxTemplate)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_page_template)
        GXRegisterCenter.instance.setExtensionPageTemplateSource(GXPageSource(this))

        // 模板参数
        val gxTemplateItem = GXTemplateEngine.GXTemplateItem(this, "templates", "gx-vertical-item")
        gxTemplateItem.isPageMode = true

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(100F.dpToPx(), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(AssetsUtils.parseAssets(this, "page_data_source/data/vertical-item.json"))

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(gxTemplateItem, size)!!

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.root).addView(view, 0)
    }
}