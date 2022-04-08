package com.alibaba.gaiax.data.cache

import android.content.Context
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.template.GXTemplateInfo
import java.util.concurrent.ConcurrentHashMap

/**
 * @suppress
 */
class GXTemplateInfoSource(val context: Context) : GXRegisterCenter.GXITemplateInfoSource {

    private val dataCache = ConcurrentHashMap<String, ConcurrentHashMap<String, GXTemplateInfo>>()

    private fun exist(templateBiz: String, templateId: String) = dataCache[templateBiz]?.get(templateId) != null

    override fun getTemplateInfo(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplateInfo? {
        return if (exist(templateItem.bizId, templateItem.templateId)) {
            dataCache[templateItem.bizId]?.get(templateItem.templateId) ?: throw IllegalStateException("Template exist but reference is null")
        } else {
            val template = GXTemplateInfo.createTemplate(templateItem)
            return template.apply {
                var bizList = dataCache[templateItem.bizId]
                if (bizList == null) {
                    bizList = ConcurrentHashMap()
                    dataCache[templateItem.bizId] = bizList
                }
                bizList[templateItem.templateId] = this
                collectionNestTemplate(bizList, this)
            }
        }
    }

    private fun collectionNestTemplate(bizList: ConcurrentHashMap<String, GXTemplateInfo>, info: GXTemplateInfo) {
        info.children?.forEach {
            bizList[it.layer.id] = it
            if (it.children?.isNotEmpty() == true) {
                collectionNestTemplate(bizList, it)
            }
        }
    }
}