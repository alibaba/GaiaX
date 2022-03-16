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
import com.alibaba.gaiax.template.expression.GXExpressionUtils
import com.alibaba.gaiax.template.expression.GXIExpression

/**
 * @suppress
 */
class GXDataBinding(
    val value: GXIExpression? = null,
    val accessibilityDesc: GXIExpression? = null,
    val accessibilityEnable: GXIExpression? = null,
    val placeholder: GXIExpression? = null,
    val extend: MutableMap<String, GXIExpression>? = null,
) {

    fun reset() {
        extendDataCache = null
        valueCache = null
        dataCache = null
    }

    private var extendDataCache: JSONObject? = null

    fun getExtendData(templateData: JSON?): JSONObject? {
        if (extendDataCache == null) {
            val result = JSONObject()
            if (extend != null) {
                for (entry in extend) {
                    // 此处不能优化
                    // 需要处理null值逻辑
                    result[entry.key] = entry.value.value(templateData)
                }
            }
            extendDataCache = result
        }
        return extendDataCache
    }

    private var dataCache: JSONObject? = null

    /**
     * 获取数据绑定的计算结果，其数据结构如下：
    {
    "value":"value",
    "placeholder":"placeholder",
    "accessibilityDesc":"accessibilityDesc",
    "accessibilityEnable":"accessibilityEnable"
    }
     */
    fun getData(templateData: JSONObject): JSONObject? {
        if (dataCache == null) {
            val result = JSONObject()
            // 此处不能优化
            // 需要处理null值逻辑
            result[GXTemplateKey.GAIAX_VALUE] = value?.value(templateData)
            // 图片占位图字段
            placeholder?.value(templateData)?.let { result[GXTemplateKey.GAIAX_PLACEHOLDER] = it }
            // View的无障碍描述
            accessibilityDesc?.value(templateData)?.let { result[GXTemplateKey.GAIAX_ACCESSIBILITY_DESC] = it }
            // View的无障碍状态
            accessibilityEnable?.value(templateData)?.let { result[GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE] = it }
            dataCache = result
        }
        return dataCache
    }

    private var valueCache: JSON? = null

    fun getValueData(templateData: JSONObject): JSON? {
        if (valueCache == null) {
            valueCache = value?.value(templateData) as? JSON
        }
        return valueCache
    }

    companion object {

        fun createMergeDataBinding(first: GXDataBinding?, second: GXDataBinding?): GXDataBinding? {

            var value: GXIExpression? = null
            if (first?.value != null) {
                value = first.value
            }
            if (second?.value != null) {
                value = second.value
            }

            var placeholder: GXIExpression? = null
            if (first?.placeholder != null) {
                placeholder = first.placeholder
            }
            if (second?.placeholder != null) {
                placeholder = second.placeholder
            }

            var accessibilityDesc: GXIExpression? = null
            if (first?.accessibilityDesc != null) {
                accessibilityDesc = first.accessibilityDesc
            }
            if (second?.accessibilityDesc != null) {
                accessibilityDesc = second.accessibilityDesc
            }

            var accessibilityEnable: GXIExpression? = null
            if (first?.accessibilityEnable != null) {
                accessibilityEnable = first.accessibilityEnable
            }
            if (second?.accessibilityEnable != null) {
                accessibilityEnable = second.accessibilityEnable
            }

            var extend: MutableMap<String, GXIExpression>? = null
            if (first?.extend != null) {
                if (extend == null) {
                    extend = mutableMapOf()
                }
                extend.putAll(first.extend)
            }
            if (second?.extend != null) {
                if (extend == null) {
                    extend = mutableMapOf()
                }
                extend.putAll(second.extend)
            }

            return if (value != null || placeholder != null || accessibilityDesc != null || accessibilityEnable != null || extend != null) {
                GXDataBinding(
                    value = value,
                    placeholder = placeholder,
                    accessibilityDesc = accessibilityDesc,
                    accessibilityEnable = accessibilityEnable,
                    extend = extend,
                )
            } else {
                null
            }
        }

        fun create(value: String? = null, placeholder: String? = null, accessibilityDesc: String? = null, accessibilityEnable: String? = null, extend: JSONObject? = null): GXDataBinding? {
            val extendExp: MutableMap<String, GXIExpression>? = if (extend != null && extend.isNotEmpty()) {
                val result: MutableMap<String, GXIExpression> = mutableMapOf()
                for (entry in extend) {
                    if (entry.key != null && entry.value != null) {
                        GXExpressionUtils.create(entry.value)?.let {
                            result[entry.key] = it
                        }
                    }
                }
                result
            } else {
                null
            }
            val valueExp = GXExpressionUtils.create(value)
            val placeholderExp = GXExpressionUtils.create(placeholder)
            val accessibilityDescExp = GXExpressionUtils.create(accessibilityDesc)
            val accessibilityEnableExp = GXExpressionUtils.create(accessibilityEnable)
            return if (valueExp != null || placeholderExp != null || accessibilityDescExp != null || accessibilityEnableExp != null || extendExp != null) {
                GXDataBinding(
                    value = valueExp,
                    placeholder = placeholderExp,
                    accessibilityDesc = accessibilityDescExp,
                    accessibilityEnable = accessibilityEnableExp,
                    extend = extendExp
                )
            } else {
                null
            }
        }
    }
}
