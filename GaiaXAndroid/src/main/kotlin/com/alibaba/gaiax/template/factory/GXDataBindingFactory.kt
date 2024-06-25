package com.alibaba.gaiax.template.factory

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.template.GXDataBinding
import com.alibaba.gaiax.template.GXIExpression
import com.alibaba.gaiax.template.GXTemplateKey

object GXDataBindingFactory {

    fun create(expVersion: String?, data: Any): GXDataBinding? {
        GXRegisterCenter.instance.extensionDataBinding?.create(expVersion, data)?.let {
            return it
        }
        return createDefaultDataBinding(expVersion, data)
    }

    private fun createDefaultDataBinding(expVersion: String?, data: Any): GXDataBinding? {
        if (data !is JSONObject) {
            return null
        }
        val value = data.getString(GXTemplateKey.GAIAX_VALUE)
        val placeholder = data.getString(GXTemplateKey.GAIAX_PLACEHOLDER)
        val accessibilityDesc = data.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC)
        val accessibilityEnable = data.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)
        val accessibilityTraits = data.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_TRAITS)
        val extend = data[GXTemplateKey.GAIAX_EXTEND]

        val valueExp = GXExpressionFactory.create(expVersion, value)
        val placeholderExp = GXExpressionFactory.create(expVersion, placeholder)
        val accessibilityDescExp = GXExpressionFactory.create(expVersion, accessibilityDesc)
        val accessibilityEnableExp = GXExpressionFactory.create(expVersion, accessibilityEnable)
        val accessibilityTraitsExp = GXExpressionFactory.create(expVersion, accessibilityTraits)
        val extendExp: MutableMap<String, GXIExpression>? =
            if (extend != null) {
                val result: MutableMap<String, GXIExpression> = mutableMapOf()

                // as json obj
                if (extend is JSONObject && extend.isNotEmpty()) {
                    for (entry in extend) {
                        if (entry.key != null && entry.value != null) {
                            GXExpressionFactory.create(expVersion, entry.key, entry.value)?.let {
                                result[entry.key] = it
                            }
                        }
                    }
                }
                // as string
                else {
                    GXExpressionFactory.create(expVersion, GXTemplateKey.GAIAX_EXTEND, extend)?.let {
                        result[GXTemplateKey.GAIAX_EXTEND] = it
                    }
                }

                result
            } else {
                null
            }

        return if (valueExp != null || placeholderExp != null || accessibilityDescExp != null || accessibilityEnableExp != null || extendExp != null) {
            GXDataBinding(
                value = valueExp,
                placeholder = placeholderExp,
                accessibilityDesc = accessibilityDescExp,
                accessibilityEnable = accessibilityEnableExp,
                accessibilityTraits = accessibilityTraitsExp,
                extend = extendExp
            ).apply {
                this.expVersion = expVersion
            }
        } else {
            null
        }
    }
}