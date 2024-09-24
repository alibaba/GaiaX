package com.alibaba.gaiax.js.proxy.modules

import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.proxy.GXJSRenderProxy
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.annotation.GXSyncMethod
import com.alibaba.gaiax.js.proxy.Log
import com.alibaba.gaiax.js.proxy.runE

@Keep
class GXJSNativeTargetModule : GXJSBaseModule() {

    @GXSyncMethod
    fun getElementByData(data: JSONObject): JSONObject {
        Log.runE { "getElementByData() called with: data = $data" }
        //  {"instanceId":19,"targetId":"phone-demand","templateId":"phone-demand"}
        val targetId = data.getString("targetId")
        val templateId = data.getString("templateId")
        val instanceId = data.getLongValue("instanceId")

        val delegate = GXJSRenderProxy.instance
        return if (delegate != null) {
            val result = delegate.getNodeInfo(targetId, templateId, instanceId)
            Log.runE { "getElementByData() called with: result = $result" }
            result["targetId"] = targetId
            result
        } else {
            JSONObject()
        }
    }

    override val name: String
        get() = "NativeTarget"

}