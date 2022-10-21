package com.alibaba.gaiax.render.node.text

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXNode

data class GXDirtyText(
    val gxTemplateContext: GXTemplateContext,
    val gxNode: GXNode,
    val templateData: JSONObject
)