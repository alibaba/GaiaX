package com.alibaba.gaiax.js.module

import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.IGXPromise
import com.alibaba.gaiax.js.api.annotation.GaiaXPromiseMethod
import com.alibaba.gaiax.js.support.GXNativeEventManager
import com.alibaba.gaiax.js.utils.Log

@Keep
class GXJSNativeMessageEventModule : GXJSBaseModule() {

    override val name: String
        get() = "NativeEvent"

    @GaiaXPromiseMethod
    fun addNativeEventListener(data: JSONObject, promise: IGXPromise) {
        if (Log.isLog()) {
            Log.d("addNativeEventListener() called with: data = $data")
        }
        val isSuccess = GXNativeEventManager.instance.registerMessage(data)
        if (isSuccess) {
            promise.resolve().invoke()
        } else {
            promise.reject().invoke()
        }
    }

    @GaiaXPromiseMethod
    fun removeNativeEventListener(data: JSONObject, promise: IGXPromise) {
        if (Log.isLog()) {
            Log.d("removeNativeEventListener() called with: data = $data")
        }
        val isSuccess = GXNativeEventManager.instance.unRegisterMessage(data)
        if (isSuccess) {
            promise.resolve().invoke()
        } else {
            promise.reject().invoke()
        }
    }
}