package com.alibaba.gaiax.render.view.basic

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.template.GXCss

interface GXIImageView : GXIViewBindData {

    override fun onBindData(data: JSONObject)

    fun setImageStyle(gxCss: GXCss) {}

    fun setRoundCornerRadius(radius: FloatArray) {}

    fun setRoundCornerBorder(borderColor: Int, borderWidth: Float, radius: FloatArray) {}
}