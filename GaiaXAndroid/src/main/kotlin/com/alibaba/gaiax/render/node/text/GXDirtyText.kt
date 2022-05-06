package com.alibaba.gaiax.render.node.text

import app.visly.stretch.Style
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXStretchNode
import com.alibaba.gaiax.render.node.GXTemplateNode

data class GXDirtyText(
    val templateContext: GXTemplateContext,
    val stretchStyle: Style,
    val currentNode: GXTemplateNode,
    val currentStretchNode: GXStretchNode,
    val data: JSONObject
)