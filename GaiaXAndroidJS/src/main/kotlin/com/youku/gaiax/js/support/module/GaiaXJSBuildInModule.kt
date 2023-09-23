package com.youku.gaiax.js.support.module

import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.GaiaXJSManager
import com.youku.gaiax.js.api.GaiaXJSBaseModule
import com.youku.gaiax.js.api.IGaiaXCallback
import com.youku.gaiax.js.api.annotation.GaiaXAsyncMethod
import com.youku.gaiax.js.api.annotation.GaiaXSyncMethod
import com.youku.gaiax.js.utils.Log

/**
 *  @author: shisan.lms
 *  @date: 2023-03-24
 *  Description:
 */
internal class GaiaXJSBuildInModule : GaiaXJSBaseModule() {

    override val name: String
        get() = "BuildIn"

    @GaiaXAsyncMethod
    fun setData(data: JSONObject, params: JSONObject, callback: IGaiaXCallback) {
        if (Log.isLog()) {
            Log.d("setData() called with: params = $params, callback = $callback")
        }
        val templateId = params.getString("templateId")
        val componentId = params.getLong("instanceId")
        if (templateId != null && componentId != null) {
            GaiaXJSManager.instance.renderDelegate.setDataToRenderEngine(
                componentId,
                templateId,
                data,
                callback
            )
        }
    }

    @GaiaXSyncMethod
    fun getData(params: JSONObject): JSONObject {
        if (Log.isLog()) {
            Log.d("getData() called with: params = $params")
        }
        val templateId = params.getString("templateId")
        val componentId = params.getLong("instanceId")
        if (templateId != null && componentId != null) {
            return GaiaXJSManager.instance.renderDelegate?.getDataFromRenderEngine(componentId) ?: JSONObject()
        }
        return JSONObject()
    }

    @GaiaXSyncMethod
    fun getComponentIndex(params: JSONObject): Int {
        if (Log.isLog()) {
            Log.d("getComponentIndex() called with: params = $params")
        }
        val templateId = params.getString("templateId")
        val componentId = params.getLong("instanceId")
        if (templateId != null && componentId != null) {
            val data = GaiaXJSManager.instance.renderDelegate?.getDataFromRenderEngine(componentId) ?: JSONObject()
            return data.getInteger("scrollIndex") ?: -1
        }
        return -1
    }
}