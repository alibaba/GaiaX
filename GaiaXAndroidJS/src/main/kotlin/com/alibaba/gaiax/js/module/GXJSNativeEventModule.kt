package com.alibaba.gaiax.js.module

import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.IGXPromise
import com.alibaba.gaiax.js.api.annotation.GXPromiseMethod
import com.alibaba.gaiax.js.utils.Log

@Keep
class GXJSNativeEventModule : GXJSBaseModule() {

    @GXPromiseMethod
    fun addEventListener(data: JSONObject, promise: IGXPromise) {
        if (Log.isLog()) {
            Log.d("addEventListener() called with: data = $data")
        }
        // https://yuque.antfin-inc.com/ronghui.zrh/bpnuxl/vc6n4g
        val targetId = data.getString("targetId")
        val templateId = data.getString("templateId")
        val instanceId = data.getLong("instanceId")
        val eventType = data.getString("eventType")
        val optionCover = data.getJSONObject("option")?.getBooleanValue("cover") ?: false
        val optionLevel = data.getJSONObject("option")?.getIntValue("level") ?: 0
        if (targetId != null && templateId != null && instanceId != null && eventType != null) {
            GXJSEngine.Proxy.instance.renderDelegate.addEventListener(
                targetId, instanceId, eventType, optionCover, optionLevel
            )
            promise.resolve().invoke()
        } else {
            promise.reject().invoke()
        }
    }

    @GXPromiseMethod
    fun removeEventListener(data: JSONObject, promise: IGXPromise) {
        if (Log.isLog()) {
            Log.d("removeEventListener() called with: data = $data")
        }

        val targetId = data.getString("targetId")
        val templateId = data.getString("templateId")
        val instanceId = data.getLong("instanceId")
        val eventType = data.getString("eventType")
        if (targetId != null && templateId != null && instanceId != null && eventType != null) {
            // TODO: remove没实现
//            GaiaXJSManager.instance.renderEngineDelegate?.removeEventListener(
//                targetId,
//                instanceId,
//                eventType
//            )
            promise.resolve().invoke()
        } else {
            promise.reject().invoke()
        }
    }

    override val name: String
        get() = "NativeEvent"

}