package com.alibaba.gaiax.render.node.text

import app.visly.stretch.Style
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXStretchNode
import com.alibaba.gaiax.render.node.GXTemplateNode
import com.alibaba.gaiax.template.GXStyle

data class GXDirtyText(
    val gxTemplateContext: GXTemplateContext,
    val gxTemplateNode: GXTemplateNode,
    val gxStretchNode: GXStretchNode,
    val gxCssStyle: GXStyle,
    val templateData: JSONObject,
    val stretchStyle: Style
)