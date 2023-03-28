package com.youku.gaiax.provider.module.js

import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.GaiaXJSManager
import com.youku.gaiax.js.api.GaiaXJSBaseModule
import com.youku.gaiax.js.api.annotation.GaiaXSyncMethod
import com.youku.gaiax.js.utils.Log

@Keep
class GaiaXJSNativeTargetModule : GaiaXJSBaseModule() {

    @GaiaXSyncMethod
    fun getElementByData(data: JSONObject): JSONObject {
        if (Log.isLog()) {
            Log.d("getElementByData() called with: data = $data")
        }
        //  {"instanceId":19,"targetId":"phone-demand","templateId":"phone-demand"}
        val targetId = data.getString("targetId")
        val templateId = data.getString("templateId")
        val instanceId = data.getLongValue("instanceId")

        val delegate = GaiaXJSManager.instance.renderDelegate
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