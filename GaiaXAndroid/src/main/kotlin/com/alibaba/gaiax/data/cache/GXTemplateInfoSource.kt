package com.alibaba.gaiax.data.cache

import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.template.GXTemplateInfo
import java.util.concurrent.ConcurrentHashMap

/**
 * @suppress
 */
class GXTemplateInfoSource : GXRegisterCenter.GXIExtensionTemplateInfoSource {

    companion object {
        val instance by lazy {
            GXTemplateInfoSource()
        }
    }

    /**
     * 模板数据锁，为了防止模板被多线程同时加载占用资源，使用锁对象来控制加载顺序
     */
    private val dataLock = ConcurrentHashMap<String, Any>()

    private val dataCache = ConcurrentHashMap<String, ConcurrentHashMap<String, GXTemplateInfo>>()

    private fun exist(templateBiz: String, templateId: String) =
        dataCache[templateBiz]?.get(templateId) != null

    override fun getTemplateInfo(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplateInfo? {
        return if (exist(gxTemplateItem.bizId, gxTemplateItem.templateId)) {
            dataCache[gxTemplateItem.bizId]?.get(gxTemplateItem.templateId)
                ?: throw IllegalArgumentException("Template exist but reference is null")
        } else {

            // 生成模板锁
            val lockName = gxTemplateItem.bizId + gxTemplateItem.templateId
            var templateLockObj = dataLock[lockName]
            if (templateLockObj == null) {
                templateLockObj = Any()
                dataLock[lockName] = templateLockObj
            }

            // 使用锁去加载模板数据
            // 以防止同时多个数据源请求同一个模板导致的数据不一致和性能损耗
            var template: GXTemplateInfo
            synchronized(templateLockObj) {
                template = GXTemplateInfo.createTemplate(gxTemplateItem)
            }
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
        bizList: ConcurrentHashMap<String, GXTemplateInfo>, info: GXTemplateInfo
    ) {
        info.children?.forEach {
            bizList[it.layer.id] = it
            if (it.children?.isNotEmpty() == true) {
                collectionNestTemplate(bizList, it)
            }
        }
    }

    fun clean() {
        dataCache.clear()
        dataLock.clear()
    }
}