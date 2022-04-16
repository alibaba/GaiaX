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

/**
 * @suppress
 */
open class GXDataBinding(
    var value: GXIExpression? = null,
    val accessibilityDesc: GXIExpression? = null,
    val accessibilityEnable: GXIExpression? = null,
    val placeholder: GXIExpression? = null,
    val extend: MutableMap<String, GXIExpression>? = null
) {

    var gxDataCache: JSONObject? = null
    var gxDataValueCache: JSON? = null
    var gxExtendCache: JSONObject? = null

    /**
     * reset all cache
     */
    open fun reset() {
        gxExtendCache = null
        gxDataValueCache = null
        gxDataCache = null
    }

    /**
     * 获取数据绑定的计算结果，其数据结构如下：
    {
    "value":"value",
    "placeholder":"placeholder",
    "accessibilityDesc":"accessibilityDesc",
    "accessibilityEnable":"accessibilityEnable"
    }
     */
    open fun getData(templateData: JSONObject): JSONObject? {
        if (gxDataCache == null) {

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
                result?.put(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE, it)
            }

            // View的无障碍状态
            accessibilityEnable?.value(templateData)?.let {
                result = result ?: JSONObject()
                result?.put(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC, it)
            }

            gxDataCache = result
        }
        return gxDataCache
    }

    open fun getDataCache(): JSONObject? {
        return gxDataCache
    }

    open fun getExtend(templateData: JSON?): JSONObject? {
        if (gxExtendCache == null) {
            val result = JSONObject()
            if (extend != null) {
                for (entry in extend) {
                    // 此处不能优化
                    // 需要处理null值逻辑
                    result[entry.key] = entry.value.value(templateData)
                }
            }
            gxExtendCache = result
        }
        return gxExtendCache
    }

    open fun getExtendCache(): JSONObject? {
        return gxExtendCache
    }

    open fun getDataValue(templateData: JSONObject): JSON? {
        if (gxDataValueCache == null) {
            gxDataValueCache = getData(templateData)?.get(GXTemplateKey.GAIAX_VALUE) as? JSON
        }
        return gxDataValueCache
    }

    open fun getDataValueCache(): JSON? {
        return gxDataValueCache
    }
}