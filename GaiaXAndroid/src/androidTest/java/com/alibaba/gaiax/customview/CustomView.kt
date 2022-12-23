package com.alibaba.gaiax.customview

import android.content.Context
import android.view.View
import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine

@Keep
class CustomView(context: Context?) : View(context), GXTemplateEngine.GXICustomViewBindData {

    var data: JSONObject? = null

    override fun onBindData(data: JSONObject?) {
        this.data = data
    }
}