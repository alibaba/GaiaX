package com.alibaba.gaiax.demo.utils

import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.expression.GXAnalyzeWrapper
import com.alibaba.gaiax.template.GXIExpression

class GXExtensionMultiVersionExpression : GXRegisterCenter.GXIExtensionExpression {

    override fun create(expVersion: String?, value: Any): GXIExpression {
        if (expVersion == "V1") {
            return GaiaXExpression.create(value)
        }
        return GXAnalyzeWrapper(value)
    }

    override fun isTrue(expVersion: String?, value: Any?): Boolean {
        if (expVersion == "V1") {
            return GaiaXExpression.isCondition(value)
        }
        return value == true
    }

}