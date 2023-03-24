package com.youku.gaiax.js.support.module

import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.api.GaiaXJSBaseModule
import com.youku.gaiax.js.api.IGaiaXPromise
import com.youku.gaiax.js.api.annotation.GaiaXPromiseMethod
import com.youku.gaiax.js.support.GaiaXNativeEventManager
import com.youku.gaiax.js.utils.Log

@Keep
class GaiaXJSNativeMessageEventModule : GaiaXJSBaseModule() {

    override val name: String
        get() = "NativeEvent"

    @GaiaXPromiseMethod
    fun addNativeEventListener(data: JSONObject, promise: IGaiaXPromise) {
        if (Log.isLog()) {
            Log.d("addNativeEventListener() called with: data = $data")
        }
        val isSuccess = GaiaXNativeEventManager.instance.registerMessage(data)
        if (isSuccess) {
            promise.resolve().invoke()
        } else {
            promise.reject().invoke()
        }
    }

    @GaiaXPromiseMethod
    fun removeNativeEventListener(data: JSONObject, promise: IGaiaXPromise) {
        if (Log.isLog()) {
            Log.d("removeNativeEventListener() called with: data = $data")
        }
        val isSuccess = GaiaXNativeEventManager.instance.unRegisterMessage(data)
        if (isSuccess) {
            promise.resolve().invoke()
        } else {
            promise.reject().invoke()
        }
    }
}