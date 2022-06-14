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
import java.util.*

/**
 * @suppress
 */
class GXDataImpl(val context: Context) {

    fun getTemplateInfo(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplateInfo {
        GXRegisterCenter.instance.extensionBizMap?.convert(templateItem)
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
    class GXTemplateSource(val context: Context) : GXRegisterCenter.GXIExtensionTemplateSource {

        data class Value(
            val priority: Int,
            val source: GXRegisterCenter.GXIExtensionTemplateSource
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Value

                if (priority != other.priority) return false

                return true
            }

            override fun hashCode(): Int {
                return priority
            }
        }

        private val dataSource: PriorityQueue<Value> =
            PriorityQueue<Value>(11) { o1, o2 -> (o2?.priority ?: 0) - (o1?.priority ?: 0) }
        private val dataSourceSorted: MutableList<Value> = mutableListOf<Value>()

        override fun getTemplate(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplate {
            dataSourceSorted.forEach { it ->
                it.source.getTemplate(gxTemplateItem)?.let { return it }
            }
            throw IllegalArgumentException("Not found target gxTemplate, templateItem = $gxTemplateItem")
        }

        fun registerByPriority(source: GXRegisterCenter.GXIExtensionTemplateSource, priority: Int) {
            var needRemove: Value? = null
            this.dataSource.forEach {
                if (it.priority == priority) {
                    needRemove = it
                }
            }
            this.dataSource.remove(needRemove)
            this.dataSource.add(Value(priority, source))

            val dataSource: PriorityQueue<Value> =
                PriorityQueue(11) { o1, o2 -> (o2?.priority ?: 0) - (o1?.priority ?: 0) }
            dataSource.addAll(this.dataSource)
            dataSourceSorted.clear()
            while (dataSource.isNotEmpty()) {
                dataSourceSorted.add(dataSource.poll())
            }
        }

        fun reset() {
            dataSource.clear()
            dataSourceSorted.clear()
        }
    }

    /**
     * @suppress
     */
    class GXTemplateInfoSource(val context: Context) :
        GXRegisterCenter.GXIExtensionTemplateInfoSource {

        data class Value(
            val priority: Int,
            val source: GXRegisterCenter.GXIExtensionTemplateInfoSource
        ) {
            override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Value

                if (priority != other.priority) return false

                return true
            }

            override fun hashCode(): Int {
                return priority
            }
        }

        private val dataSource: PriorityQueue<Value> =
            PriorityQueue<Value>(11) { o1, o2 -> (o2?.priority ?: 0) - (o1?.priority ?: 0) }

        private val dataSourceSorted: MutableList<Value> = mutableListOf<Value>()

        override fun getTemplateInfo(gxTemplateItem: GXTemplateEngine.GXTemplateItem): GXTemplateInfo {
            dataSourceSorted.forEach {
                it.source.getTemplateInfo(gxTemplateItem)?.let { return it }
            }
            throw IllegalArgumentException("Not found target gxTemplateInfo, templateItem = $gxTemplateItem")
        }

        fun registerByPriority(
            source: GXRegisterCenter.GXIExtensionTemplateInfoSource,
            priority: Int
        ) {
            var needRemove: Value? = null
            this.dataSource.forEach {
                if (it.priority == priority) {
                    needRemove = it
                }
            }
            this.dataSource.remove(needRemove)
            this.dataSource.add(Value(priority, source))

            val dataSource: PriorityQueue<Value> =
                PriorityQueue(11) { o1, o2 -> (o2?.priority ?: 0) - (o1?.priority ?: 0) }
            dataSource.addAll(this.dataSource)

            dataSourceSorted.clear()
            while (dataSource.isNotEmpty()) {
                dataSourceSorted.add(dataSource.poll())
            }
        }

        fun reset() {
            dataSource.clear()
            dataSourceSorted.clear()
        }
    }
}