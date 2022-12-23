package com.alibaba.gaiax.demo.utils

import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.expression.GXAnalyzeWrapper
import com.alibaba.gaiax.template.GXIExpression

class GXExtensionMultiVersionExpression : GXRegisterCenter.GXIExtensionExpression {

    override fun create(expVersion: String?, value: Any): GXIExpression {
        if (expVersion == "V1") {
            val expression = GaiaXYKExpression.create(value)
            expression.expression = value
            return expression
        }
        return GXAnalyzeWrapper(value)
    }

    override fun isTrue(expVersion: String?, value: Any?): Boolean {
        if (expVersion == "V1") {
            return GaiaXYKExpression.isCondition(value)
        }
        return value == true
    }

}