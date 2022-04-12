package com.alibaba.gaiax.template.factory

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.template.GXDataBinding
import com.alibaba.gaiax.template.GXTemplateKey

object GXDataBindingFactory {

    fun create(data: JSONObject): GXDataBinding? {
        GXRegisterCenter.instance.databindingProcessDataBinding?.create(data)?.let {
            return it
        }
        return GXDataBinding.create(
            data.getString(GXTemplateKey.GAIAX_VALUE),
            data.getString(GXTemplateKey.GAIAX_PLACEHOLDER),
            data.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC),
            data.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE),
            data.getJSONObject(GXTemplateKey.GAIAX_EXTEND)
        )
    }
}