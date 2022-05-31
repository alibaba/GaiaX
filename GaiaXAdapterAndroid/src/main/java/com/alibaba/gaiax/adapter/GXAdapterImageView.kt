package com.alibaba.gaiax.adapter

import android.content.Context
import android.support.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.basic.GXImageView
import com.alibaba.gaiax.template.GXTemplateKey
import com.bumptech.glide.Glide

@Keep
class GXAdapterImageView(context: Context) : GXImageView(context) {

    override fun bindNetUri(data: JSONObject, uri: String, placeholder: String?) {
        // 占位图仅对网络图生效
        data.getString(GXTemplateKey.GAIAX_PLACEHOLDER)?.let { resUri ->
            bindRes(resUri)
        }
        // Net
        Glide.with(context).load(uri).into(this)
    }
}