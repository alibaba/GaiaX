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

package com.alibaba.gaiax.data

import android.content.Context
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.template.GXTemplateInfo

/**
 * @suppress
 */
class GXDataImpl(val context: Context) {

    private var assetsBizMapRelation: MutableMap<String, String>? = null

    fun unregisterAssetsBizMapRelation(templateBiz: String) {
        assetsBizMapRelation?.remove(templateBiz)
    }

    fun registerAssetsBizMapRelation(templateBiz: String, mapRelation: String) {
        if (assetsBizMapRelation == null) {
            assetsBizMapRelation = mutableMapOf()
        }
        assetsBizMapRelation?.put(templateBiz, mapRelation)
    }

    fun getTemplateInfo(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplateInfo {
        assetsBizMapRelation?.get(templateItem.bizId)?.let {
            templateItem.bundle = it
        }
        return templateInfoSource.getTemplateInfo(templateSource, templateItem)
    }

    internal val templateInfoSource: GXTemplateInfoSource by lazy {
        val dataCache = GXTemplateInfoSource(context)
        dataCache.init()
        dataCache
    }

    internal val templateSource: GXTemplateSource by lazy {
        val dataSource = GXTemplateSource(context)
        dataSource.init()
        dataSource
    }
}