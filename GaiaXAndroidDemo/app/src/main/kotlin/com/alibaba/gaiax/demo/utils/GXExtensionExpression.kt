package com.alibaba.gaiax.demo.utils

import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.template.GXIExpression

class GXExtensionExpression : GXRegisterCenter.GXIExtensionExpression {

    override fun create(value: Any): GXIExpression {
        return GaiaXExpression.create(value)
    }

    override fun isTrue(value: Any?): Boolean {
        return GaiaXExpression.isCondition(value)
    }

}