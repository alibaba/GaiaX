package com.alibaba.gaiax.js.proxy.modules

import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.proxy.GXJSRenderProxy
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.IGXPromise
import com.alibaba.gaiax.js.api.annotation.GXPromiseMethod
import com.alibaba.gaiax.js.proxy.Log
import com.alibaba.gaiax.js.proxy.runE

/**
 * 实现从JS中为Native视图添加事件
 */
@Keep
class GXJSEventModule : GXJSBaseModule() {

    @GXPromiseMethod
    fun addEventListener(data: JSONObject, promise: IGXPromise) {
        Log.runE { "addEventListener() called with: data = $data" }
        // https://yuque.antfin-inc.com/ronghui.zrh/bpnuxl/vc6n4g
        val targetId = data.getString("targetId")
        val templateId = data.getString("templateId")
        val instanceId = data.getLong("instanceId")
        val eventType = data.getString("eventType")
        val optionCover = data.getJSONObject("option")?.getBooleanValue("cover") ?: false
        val optionLevel = data.getJSONObject("option")?.getIntValue("level") ?: 0
        if (targetId != null && templateId != null && instanceId != null && eventType != null) {
            GXJSRenderProxy.instance.addGestureEventListener(
                targetId, instanceId, eventType, optionCover, optionLevel
            )
            promise.resolve().invoke()
        } else {
            promise.reject().invoke()
        }
    }

    @GXPromiseMethod
    fun removeEventListener(data: JSONObject, promise: IGXPromise) {
        Log.runE { "removeEventListener() called with: data = $data" }
        val targetId = data.getString("targetId")
        val templateId = data.getString("templateId")
        val componentId = data.getLong("instanceId")
        val eventType = data.getString("eventType")
        if (targetId != null && templateId != null && componentId != null && eventType != null) {
            GXJSRenderProxy.instance.removeGestureEventListener(
                targetId, componentId, eventType
            )
            promise.resolve().invoke()
        } else {
            promise.reject().invoke()
        }
    }


    override val name: String
        get() = "NativeEvent"

}