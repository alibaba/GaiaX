package com.alibaba.gaiax.data.cache

import android.content.Context
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.template.GXTemplateInfo
import java.util.concurrent.ConcurrentHashMap

/**
 * @suppress
 */
class GXTemplateInfoSource(val context: Context) : GXRegisterCenter.GXIExtensionTemplateInfoSource {

    private val dataCache = ConcurrentHashMap<String, ConcurrentHashMap<String, GXTemplateInfo>>()

    private fun exist(templateBiz: String, templateId: String) =
        dataCache[templateBiz]?.get(templateId) != null

    override fun getTemplateInfo(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplateInfo? {
        return if (exist(gxTemplateItem.bizId, gxTemplateItem.templateId)) {
            dataCache[gxTemplateItem.bizId]?.get(gxTemplateItem.templateId)
                ?: throw IllegalArgumentException("Template exist but reference is null")
        } else {
            val template = GXTemplateInfo.createTemplate(gxTemplateItem)
            return template.apply {
                var bizList = dataCache[gxTemplateItem.bizId]
                if (bizList == null) {
                    bizList = ConcurrentHashMap()
                    dataCache[gxTemplateItem.bizId] = bizList
                }
                bizList[gxTemplateItem.templateId] = this
                collectionNestTemplate(bizList, this)
            }
        }
    }

    private fun collectionNestTemplate(
        bizList: ConcurrentHashMap<String, GXTemplateInfo>,
        info: GXTemplateInfo
    ) {
        info.children?.forEach {
            bizList[it.layer.id] = it
            if (it.children?.isNotEmpty() == true) {
                collectionNestTemplate(bizList, it)
            }
        }
    }
}