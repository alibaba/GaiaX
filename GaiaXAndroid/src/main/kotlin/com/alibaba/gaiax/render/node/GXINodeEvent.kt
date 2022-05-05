package com.alibaba.gaiax.render.node

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext

interface GXINodeEvent {

    fun addDataBindingEvent(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        templateData: JSONObject
    )
}