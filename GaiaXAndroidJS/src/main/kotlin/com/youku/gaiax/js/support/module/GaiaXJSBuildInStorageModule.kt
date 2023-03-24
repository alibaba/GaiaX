package com.youku.gaiax.js.support.module

import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.api.GaiaXJSBaseModule
import com.youku.gaiax.js.api.IGaiaXPromise
import com.youku.gaiax.js.api.annotation.GaiaXPromiseMethod
import com.youku.gaiax.js.support.JSDataConvert
import com.youku.gaiax.js.utils.GaiaXJSPreferenceUtil
import com.youku.gaiax.js.utils.Log

/**
 *  @author: shisan.lms
 *  @date: 2023-03-24
 *  Description:
 */
class GaiaXJSBuildInStorageModule : GaiaXJSBaseModule() {
    override val name: String
        get() = "BuildIn"

    companion object {
        private const val GAIAX_JS_STORAGE = "GAIAX_JS_STORAGE"
    }

    @GaiaXPromiseMethod
    fun getStorage(key: String, promise: IGaiaXPromise) {
        val prefs = GaiaXJSPreferenceUtil.createSharePreference(GAIAX_JS_STORAGE)
        if (prefs.contains(key)) {
            try {
                val targetStr = prefs.getString(key, "default_value")
                val target = JSONObject.parseObject(targetStr)
                val data = target["data"]
                val type = target.getString("type")
                val value = JSDataConvert.getDataValueByType(type, data)
                if (Log.isLog()) {
                    Log.d("getStorage() called with: key = $key, targetStr = $targetStr, value = $value")
                }
                promise.resolve().invoke(value)
            } catch (e: Exception) {
                promise.reject().invoke(e.message)
            }
        } else {
            // 无值状态下，给一个undefined
            promise.resolve().invoke("Error：Key is Empty")
        }
    }

    @GaiaXPromiseMethod
    fun setStorage(key: String, value: Any, promise: IGaiaXPromise) {
        val prefs = GaiaXJSPreferenceUtil.createSharePreference(GAIAX_JS_STORAGE)
        try {
            val type = JSDataConvert.getDataTypeByValue(value)
            val target = JSONObject().apply {
                this["type"] = type
                this["data"] = value
            }
            if (Log.isLog()) {
                Log.d("setStorage() called with: key = $key, type = $type, target = $value")
            }
            prefs.putString(key, target.toString())
            promise.resolve().invoke()
        } catch (e: Exception) {
            promise.reject().invoke(e.message)
        }
    }

    @GaiaXPromiseMethod
    fun removeStorage(key: String, promise: IGaiaXPromise) {
        if (Log.isLog()) {
            Log.d("removeStorage() called with: key = $key")
        }
        val prefs = GaiaXJSPreferenceUtil.createSharePreference(GAIAX_JS_STORAGE)
        if (prefs.contains(key)) {
            try {
                if (prefs.delete(key)) {
                    promise.resolve().invoke()
                } else {
                    promise.resolve().invoke()
                }
            } catch (e: Exception) {
                promise.reject().invoke(e.message)
            }
        } else {
            promise.resolve().invoke()
        }
    }
}