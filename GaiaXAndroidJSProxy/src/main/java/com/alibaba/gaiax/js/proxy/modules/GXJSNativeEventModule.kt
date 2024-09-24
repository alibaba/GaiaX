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
 * 从JS中监听Native发送来的消息
 */
@Keep
class GXJSNativeEventModule : GXJSBaseModule() {

    override val name: String
        get() = "NativeEvent"

    @GXPromiseMethod
    fun addNativeEventListener(data: JSONObject, promise: IGXPromise) {
        Log.runE { "addNativeEventListener() called with: data = $data" }
        val isSuccess = GXJSRenderProxy.instance.registerNativeMessage(data)
        if (isSuccess) {
            promise.resolve().invoke()
        } else {
            promise.reject().invoke()
        }
    }

    @GXPromiseMethod
    fun removeNativeEventListener(data: JSONObject, promise: IGXPromise) {
        Log.runE { "removeNativeEventListener() called with: data = $data" }
        val isSuccess = GXJSRenderProxy.instance.unregisterNativeMessage(data)
        if (isSuccess) {
            promise.resolve().invoke()
        } else {
            promise.reject().invoke()
        }
    }
}