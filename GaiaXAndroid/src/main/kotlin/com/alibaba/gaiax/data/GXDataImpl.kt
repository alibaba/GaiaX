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
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.template.GXTemplate
import com.alibaba.gaiax.template.GXTemplateInfo

/**
 * @suppress
 */
class GXDataImpl(val context: Context) {

    fun getTemplateInfo(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplateInfo {
        GXRegisterCenter.instance.bizMapProcessing?.convertProcessing(templateItem)
        return templateInfoSource.getTemplateInfo(templateItem)
    }

    val templateInfoSource: GXTemplateInfoSource by lazy {
        val dataCache = GXTemplateInfoSource(context)
        dataCache
    }

    val templateSource: GXTemplateSource by lazy {
        val dataSource = GXTemplateSource(context)
        dataSource
    }

    /**
     * @suppress
     */
    class GXTemplateSource(val context: Context) : GXRegisterCenter.GXITemplateSource {

        private val sources = mutableListOf<GXRegisterCenter.GXITemplateSource>()

        override fun getTemplate(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplate {
            sources.forEach { it ->
                it.getTemplate(templateItem)?.let { return it }
            }
            throw IllegalArgumentException("Not found target template path, templateItem = $templateItem")
        }

        fun addPriority(source: GXRegisterCenter.GXITemplateSource, priority: Int) {
            sources.add(0, source)
        }
    }

    /**
     * @suppress
     */
    class GXTemplateInfoSource(val context: Context) : GXRegisterCenter.GXITemplateInfoSource {

        private var source: GXRegisterCenter.GXITemplateInfoSource? = null
        // private val sources = mutableListOf<GXRegisterCenter.GXITemplateInfoSource>()

        override fun getTemplateInfo(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplateInfo {
            source?.getTemplateInfo(templateItem)?.let { return it }
            throw IllegalStateException("Template exist but reference is null")
        }

        fun addPriority(source: GXRegisterCenter.GXITemplateInfoSource, priority: Int) {
            this.source = source
        }
    }

}