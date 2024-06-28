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

package com.alibaba.gaiax.template

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.factory.GXExpressionFactory

/**
 * 只能通过GXTemplateNode来调用
 * @suppress
 */
open class GXDataBinding(
    var value: GXIExpression? = null,
    val accessibilityDesc: GXIExpression? = null,
    val accessibilityEnable: GXIExpression? = null,
    val accessibilityTraits: GXIExpression? = null,
    val placeholder: GXIExpression? = null,
    val extend: MutableMap<String, GXIExpression>? = null
) {

    var expVersion: String? = null

    /**
     * 获取数据绑定的计算结果，其数据结构如下：
    {
    "value":"value",
    "placeholder":"placeholder",
    "accessibilityDesc":"accessibilityDesc",
    "accessibilityTraits":"button",
    "accessibilityEnable":"accessibilityEnable",
    }
     */
    open fun getData(templateData: JSONObject): JSONObject? {

        var result: JSONObject? = null

        // 渲染字段
        value?.value(templateData)?.let {
            result = result ?: JSONObject()
            result?.put(GXTemplateKey.GAIAX_VALUE, it)
        }

        // 图片占位图字段
        placeholder?.value(templateData)?.let {
            result = result ?: JSONObject()
            result?.put(GXTemplateKey.GAIAX_PLACEHOLDER, it)
        }

        // View的无障碍描述
        accessibilityDesc?.value(templateData)?.let {
            result = result ?: JSONObject()
            result?.put(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC, it)
        }

        // View的无障碍状态
        accessibilityEnable?.value(templateData)?.let {
            result = result ?: JSONObject()
            result?.put(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE, it)
        }

        // View的无障碍朗读逻辑
        accessibilityTraits?.value(templateData)?.let {
            result = result ?: JSONObject()
            result?.put(GXTemplateKey.GAIAX_ACCESSIBILITY_TRAITS, it)
        }

        // extend数据
        getExtend(templateData)?.let {
            result = result ?: JSONObject()
            result?.put(GXTemplateKey.GAIAX_EXTEND, it)
        }

        return result
    }

    open fun getExtend(templateData: JSON?): JSONObject? {
        if (extend != null) {
            val result: JSONObject = JSONObject()
            for (entry in extend) {
                if (entry.key == GXTemplateKey.GAIAX_EXTEND) {
                    val extend = entry.value.value(templateData) as? JSONObject
                    extend?.let {
                        for (e in extend) {
                            if (e.key != null && e.value != null) {
                                GXExpressionFactory.create(this.expVersion, e.key, e.value)?.let { it ->
                                    it.value(templateData)?.let {
                                        result[e.key] = it
                                    }
                                }
                            }
                        }
                    }
                } else {
                    result[entry.key] = entry.value.value(templateData)
                }
            }
            return result
        }
        return null
    }
}