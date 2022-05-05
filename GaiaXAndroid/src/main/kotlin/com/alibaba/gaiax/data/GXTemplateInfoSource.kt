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
import com.alibaba.gaiax.data.cache.GXTemplateInfoCacheSource
import com.alibaba.gaiax.template.GXStyleConvert
import com.alibaba.gaiax.template.GXTemplateInfo

/**
 * @suppress
 */
class GXTemplateInfoSource(val context: Context) : GXITemplateInfoSource {

    var sources = mutableListOf<GXITemplateInfoSource>()

    fun init() {
        GXStyleConvert.instance.init(context.assets)
        sources.add(GXTemplateInfoCacheSource(context))
    }

    override fun getTemplateInfo(
        templateSource: GXITemplateSource,
        templateItem: GXTemplateEngine.GXTemplateItem
    ): GXTemplateInfo {
        sources.forEach {
            it.getTemplateInfo(templateSource, templateItem)?.let { return it }
        }
        throw IllegalStateException("Template exist but reference is null")
    }
}