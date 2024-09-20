package com.alibaba.gaiax.js.proxy.modules

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

        fun sendJSLogMsg(level: String, msg: String) {
            try {
                GXJSEngine.instance.getSocketSender()?.let {
                    val data = JSONObject()
                    data["jsonrpc"] = "2.0"
                    data["method"] = "js/console"
                    val params = JSONObject()
                    params["level"] = level
                    params["data"] = msg
                    data["params"] = params

                    if (com.alibaba.gaiax.js.utils.Log.isLog()) {
                        com.alibaba.gaiax.js.utils.Log.d("sendJSLogMsg() called with: $data")
                    }

                    it.onSendMsg(data)
                }
            } catch (e: Exception) {
                if (com.alibaba.gaiax.js.utils.Log.isLog()) {
                    com.alibaba.gaiax.js.utils.Log.d("sendJSLogMsg() called with: ${e.message}")
                }
                e.printStackTrace()
            }
        }
    }

    @GXSyncMethod
    fun log(data: String) {
        val msg = getLogMsg(data)
        sendJSLogMsg("log", msg)
        Log.d(TAG, "log() called with: msg = $msg")
    }

    @GXSyncMethod
    fun info(data: String) {
        val msg = getLogMsg(data)
        sendJSLogMsg("info", msg)
        Log.i(TAG, "info() called with: msg = $msg")
    }

    @GXSyncMethod
    fun warn(data: String) {
        val msg = getLogMsg(data)
        sendJSLogMsg("warn", msg)
        Log.w(TAG, "warn() called with: msg = $msg")
    }

    @GXSyncMethod
    fun error(data: String) {
        val msg = getLogMsg(data)
        sendJSLogMsg("error", msg)
        Log.e(TAG, "error() called with: msg = $msg")
    }

    override val name: String
        get() = "NativeLogger"

}