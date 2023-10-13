package com.alibaba.gaiax.js.module

import android.util.Log
import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.annotation.GXSyncMethod

@Keep
class GXJSLogModule : GXJSBaseModule() {

    companion object {
        private const val TAG = "[GaiaX][JS]"

        private fun getLogMsg(argData: String): String = try {
            JSONObject.parseObject(argData).getString("data") ?: ""
        } catch (e: Exception) {
            "${e.message}"
        }

        private fun sendJSLogMsg(level: String, msg: String) {
            val data = JSONObject()
            data["jsonrpc"] = "2.0"
            data["method"] = "js/console"
            val params = JSONObject()
            params["level"] = level
            params["data"] = msg
            data["params"] = params
            GXJSEngine.instance.socketSender?.onSendMsg(data)
        }

    }

    @GXSyncMethod
    fun log(data: String) {
        val msg = getLogMsg(data)
        sendJSLogMsg("log", msg)
        Log.d(TAG, msg)
    }

    @GXSyncMethod
    fun info(data: String) {
        val msg = getLogMsg(data)
        sendJSLogMsg("info", msg)
        Log.i(TAG, msg)
    }

    @GXSyncMethod
    fun warn(data: String) {
        val msg = getLogMsg(data)
        sendJSLogMsg("warn", msg)
        Log.w(TAG, msg)
    }

    @GXSyncMethod
    fun error(data: String) {
        val msg = getLogMsg(data)
        sendJSLogMsg("error", msg)
        Log.e(TAG, msg)
    }

    override val name: String
        get() = "NativeLogger"

}