package com.alibaba.gaiax.js.adapter.modules

import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.adapter.GXJSRenderDelegate
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.annotation.GXSyncMethod
import com.alibaba.gaiax.js.utils.Log

@Keep
class GXJSNativeTargetModule : GXJSBaseModule() {

    @GXSyncMethod
    fun getElementByData(data: JSONObject): JSONObject {
        if (Log.isLog()) {
            Log.d("getElementByData() called with: data = $data")
        }
        //  {"instanceId":19,"targetId":"phone-demand","templateId":"phone-demand"}
        val targetId = data.getString("targetId")
        val templateId = data.getString("templateId")
        val instanceId = data.getLongValue("instanceId")

        val delegate = GXJSRenderDelegate.instance
        return if (delegate != null) {
            val result = delegate.getNodeInfo(targetId, templateId, instanceId)
            if (Log.isLog()) {
                Log.d("getElementByData() called with: result = $result")
            }
            result["targetId"] = targetId
            result
        } else {
            JSONObject()
        }
    }

    override val name: String
        get() = "NativeTarget"

}