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
import com.alibaba.gaiax.data.assets.GXAssetsBinaryTemplate
import com.alibaba.gaiax.data.assets.GXAssetsTemplate
import com.alibaba.gaiax.template.GXTemplate

/**
 * @suppress
 */
open class GXTemplateSource(val context: Context) : GXITemplateSource {

    var sources = mutableListOf<GXITemplateSource>()

    fun init() {
        sources.add(GXAssetsTemplate(context))
        sources.add(GXAssetsBinaryTemplate(context))
    }

    override fun getTemplate(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplate? {
        sources.forEach { it ->
            it.getTemplate(templateItem)?.let { return it }
        }
        throw IllegalArgumentException("Not found target template path, templateItem = $templateItem")
    }

}
