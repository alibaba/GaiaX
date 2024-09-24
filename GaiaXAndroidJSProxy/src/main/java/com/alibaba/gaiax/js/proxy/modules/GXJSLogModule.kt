package com.alibaba.gaiax.js.proxy.modules

import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.api.GXJSBaseModule
import com.alibaba.gaiax.js.api.annotation.GXSyncMethod
import com.alibaba.gaiax.js.proxy.Log
import com.alibaba.gaiax.js.proxy.runE


@Keep
class GXJSLogModule : GXJSBaseModule() {

    companion object {

        private const val TAG = "GXJSLogModule"

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

                    Log.runE(TAG) { "sendJSLogMsg() called with: $data" }

                    it.onSendMsg(data)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Log.runE(TAG) { "sendJSLogMsg() called with: ${e.message}" }
            }
        }
    }

    @GXSyncMethod
    fun log(data: String) {
        val msg = getLogMsg(data)
        sendJSLogMsg("log", msg)
        Log.runE(TAG) { "log() called with: msg = $msg" }
    }

    @GXSyncMethod
    fun info(data: String) {
        val msg = getLogMsg(data)
        sendJSLogMsg("info", msg)
        Log.runE(TAG) { "info() called with: msg = $msg" }
    }

    @GXSyncMethod
    fun warn(data: String) {
        val msg = getLogMsg(data)
        sendJSLogMsg("warn", msg)
        Log.runE(TAG) { "warn() called with: msg = $msg" }
    }

    @GXSyncMethod
    fun error(data: String) {
        val msg = getLogMsg(data)
        sendJSLogMsg("error", msg)
        Log.runE(TAG) { "error() called with: msg = $msg" }
    }

    override val name: String
        get() = "NativeLogger"

}