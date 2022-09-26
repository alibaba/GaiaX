package com.alibaba.gaiax.render.view.basic

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.GXIRoundCorner
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.template.GXCss

interface GXIImageView : GXIViewBindData, GXIRoundCorner {

    override fun onBindData(data: JSONObject?)

    fun setImageStyle(gxCss: GXCss) {}

    override fun setRoundCornerRadius(radius: FloatArray) {}

    override fun setRoundCornerBorder(borderColor: Int, borderWidth: Float, radius: FloatArray) {}
}