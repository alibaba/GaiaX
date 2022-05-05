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

package com.alibaba.gaiax.data.cache

import android.content.Context
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.data.GXITemplateInfoSource
import com.alibaba.gaiax.data.GXITemplateSource
import com.alibaba.gaiax.template.GXStyleConvert
import com.alibaba.gaiax.template.GXTemplateInfo
import java.util.concurrent.ConcurrentHashMap

/**
 * @suppress
 */
class GXTemplateInfoCacheSource(val context: Context) : GXITemplateInfoSource {

    init {
        GXStyleConvert.instance.init(context.assets)
    }

    private val dataCache = ConcurrentHashMap<String, ConcurrentHashMap<String, GXTemplateInfo>>()

    private fun exist(templateBiz: String, templateId: String) =
        dataCache[templateBiz]?.get(templateId) != null

    override fun getTemplateInfo(
        templateSource: GXITemplateSource,
        templateItem: GXTemplateEngine.GXTemplateItem
    ): GXTemplateInfo? {
        return if (exist(templateItem.bizId, templateItem.templateId)) {
            dataCache[templateItem.bizId]?.get(templateItem.templateId)
                ?: throw IllegalStateException("Template exist but reference is null")
        } else {
            val template = GXTemplateInfo.createTemplate(templateSource, templateItem)
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