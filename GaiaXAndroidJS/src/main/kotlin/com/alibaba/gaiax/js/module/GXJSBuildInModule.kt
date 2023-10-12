package com.alibaba.gaiax.js.module

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.IGXCallback
import com.alibaba.gaiax.js.api.annotation.GXAsyncMethod
import com.alibaba.gaiax.js.api.annotation.GXSyncMethod
import com.alibaba.gaiax.js.utils.Log

/**
 *  @author: shisan.lms
 *  @date: 2023-03-24
 *  Description:
 */
internal class GXJSBuildInModule : GXJSBaseModule() {

    override val name: String
        get() = "BuildIn"

    @GXAsyncMethod
    fun setData(data: JSONObject, params: JSONObject, callback: IGXCallback) {
        if (Log.isLog()) {
            Log.d("setData() called with: params = $params, callback = $callback")
        }
        val templateId = params.getString("templateId")
        val componentId = params.getLong("instanceId")
        if (templateId != null && componentId != null) {
            GXJSEngine.Proxy.instance.renderDelegate.setDataToRenderEngine(
                componentId,
                templateId,
                data,
                callback
            )
        }
    }

    @GXSyncMethod
    fun getData(params: JSONObject): JSONObject {
        if (Log.isLog()) {
            Log.d("getData() called with: params = $params")
        }
        val templateId = params.getString("templateId")
        val componentId = params.getLong("instanceId")
        if (templateId != null && componentId != null) {
            return GXJSEngine.Proxy.instance.renderDelegate?.getDataFromRenderEngine(componentId) ?: JSONObject()
        }
        return JSONObject()
    }

    @GXSyncMethod
    fun getComponentIndex(params: JSONObject): Int {
        if (Log.isLog()) {
            Log.d("getComponentIndex() called with: params = $params")
        }
        val templateId = params.getString("templateId")
        val componentId = params.getLong("instanceId")
        if (templateId != null && componentId != null) {
            val data = GXJSEngine.Proxy.instance.renderDelegate?.getDataFromRenderEngine(componentId) ?: JSONObject()
            return data.getInteger("scrollIndex") ?: -1
        }
        return -1
    }
}