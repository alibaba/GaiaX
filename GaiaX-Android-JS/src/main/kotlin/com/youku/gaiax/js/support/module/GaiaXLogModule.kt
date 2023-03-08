package com.youku.gaiax.js.support.module

/**
 *  @author: shisan.lms
 *  @date: 2022-11-17
 *  Description:
 */

import android.util.Log
import androidx.annotation.Keep
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.studio.GXClientToStudioMultiType
//import com.youku.gaiax.GaiaX
import com.youku.gaiax.js.api.GaiaXBaseModule
import com.youku.gaiax.js.api.annotation.GaiaXSyncMethod

@Keep
class GaiaXLogModule : GaiaXBaseModule() {

    companion object {
        private const val TAG = "[GaiaX][JS][LOG]"

        fun errorLog(data: String) {
            sendMsgLog(getLogMsg(data))
        }

        fun errorLog(data: JSONObject) {
            sendMsgLog(getLogMsg(data))
        }

        private fun getLogMsg(data: JSONObject) = data.getString("data") ?: ""

        private fun sendMsgLog(msg: String) {
            sendSocketData("error", msg)
            Log.e(TAG, msg)
//            MotuJSErrorReportUtils.sendError(msg)
        }

        private fun sendSocketData(level: String, data: String) {
            GXClientToStudioMultiType.instance.sendMsgForJSLog(level, data)
        }

        private fun getLogMsg(argData: String): String = try {
            JSONObject.parseObject(argData).getString("data") ?: ""
        } catch (e: Exception) {
            "${e.message}"
        }
    }

    @GaiaXSyncMethod
    fun log(data: String) {
        val msg = getLogMsg(data)
        sendSocketData("log", msg)
        Log.d(TAG, msg)
    }

    @GaiaXSyncMethod
    fun info(data: String) {
        val msg = getLogMsg(data)
        sendSocketData("info", msg)
        Log.i(TAG, msg)
    }

    @GaiaXSyncMethod
    fun warn(data: String) {
        val msg = getLogMsg(data)
        sendSocketData("warn", msg)
        Log.w(TAG, msg)
    }

    @GaiaXSyncMethod
    fun error(data: String) {
        errorLog(data)
    }

    override val name: String
        get() = "NativeLogger"

}