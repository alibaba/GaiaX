package com.alibaba.gaiax.utils

import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.expression.GXAnalyzeWrapper
import com.alibaba.gaiax.template.GXIExpression

class GXExtensionMultiVersionExpression : GXRegisterCenter.GXIExtensionExpression {

    override fun create(expVersion: String?, value: Any): GXIExpression {
        if (expVersion == "V1") {
            return GXYKExpression.create(value)
        }
        return GXAnalyzeWrapper(value)
    }

    override fun isTrue(expVersion: String?, value: Any?): Boolean {
        if (expVersion == "V1") {
            return GXYKExpression.isCondition(value)
        }
        return value == true
    }

}