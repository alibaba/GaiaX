package com.alibaba.gaiax.js.proxy.modules

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.proxy.GXJSRenderProxy
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.IGXCallback
import com.alibaba.gaiax.js.api.annotation.GXAsyncMethod
import com.alibaba.gaiax.js.api.annotation.GXSyncMethod
import com.alibaba.gaiax.js.proxy.Log
import com.alibaba.gaiax.js.proxy.runE

internal class GXJSBuildInModule : GXJSBaseModule() {

    companion object {
        private const val TAG = "GXJSBuildInModule"
    }

    override val name: String
        get() = "BuildIn"

    @GXAsyncMethod
    fun setData(data: JSONObject, params: JSONObject, callback: IGXCallback) {
        Log.runE(TAG) { "setData() called with: params = $params, callback = $callback" }
        val templateId = params.getString("templateId")
        val componentId = params.getLong("instanceId")
        if (templateId != null && componentId != null) {
            GXJSRenderProxy.instance.setData(
                componentId, templateId, data, callback
            )
        }
    }

    @GXSyncMethod
    fun getData(params: JSONObject): JSONObject {
        Log.runE(TAG) { "getData() called with: params = $params" }
        val templateId = params.getString("templateId")
        val componentId = params.getLong("instanceId")
        if (templateId != null && componentId != null) {
            return GXJSRenderProxy.instance.getData(componentId) ?: JSONObject()
        }
        return JSONObject()
    }

    @GXSyncMethod
    fun getComponentIndex(params: JSONObject): Int {
        Log.runE(TAG) { "getComponentIndex() called with: params = $params" }
        val templateId = params.getString("templateId")
        val componentId = params.getLong("instanceId")
        if (templateId != null && componentId != null) {
            val data = GXJSRenderProxy.instance.getData(componentId) ?: JSONObject()
            return data.getInteger("scrollIndex") ?: -1
        }
        return -1
    }
}