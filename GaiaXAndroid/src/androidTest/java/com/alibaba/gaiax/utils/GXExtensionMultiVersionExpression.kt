package com.alibaba.gaiax.utils

import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.expression.GXAnalyzeWrapper
import com.alibaba.gaiax.template.GXIExpression
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.template.factory.GXExpressionFactory

class GXExtensionMultiVersionExpression : GXRegisterCenter.GXIExtensionExpression {

    override fun create(expVersion: String?, key: String?, value: Any): GXIExpression {
        if (expVersion == "V1") {
            return if (key == GXTemplateKey.STYLE_FIT_CONTENT) {
                // 兼容fitcontent的转换逻辑
                val newValue = if (GXExpressionFactory.isTrue(expVersion, value) == true) {
                    "true"
                } else {
                    "false"
                }
                val gxTestYKExpression = GXTestYKExpression.create(newValue)
                gxTestYKExpression.expression = newValue
                gxTestYKExpression
            } else {
                val gxTestYKExpression = GXTestYKExpression.create(value)
                gxTestYKExpression.expression = value
                gxTestYKExpression
            }
        }
        return GXAnalyzeWrapper(value)
    }

    override fun isTrue(expVersion: String?, key: String?, value: Any?): Boolean {
        if (expVersion == "V1") {
            return GXTestYKExpression.isCondition(value)
        }
        return value == true
    }

}