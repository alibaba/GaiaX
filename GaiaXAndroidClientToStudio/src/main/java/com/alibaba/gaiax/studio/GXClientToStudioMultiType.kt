package com.alibaba.gaiax.studio

import android.content.Context
import android.net.ConnectivityManager
import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.regex.Pattern

/**
 *  @author: shisan.lms
 *  @date: 2023-02-02
 *  Description: Studio扫码三合一通道
 */
class GXClientToStudioMultiType {
    interface GXSocketToStudioListener {
        fun onAddData(templateId: String, templateData: JSONObject)
        fun onUpdate(templateId: String, templateData: JSONObject)
    }

    interface GXSocketJSReceiveListener {
        fun onCallSyncFromStudioWorker(socketId: Int, params: JSONObject)

        fun onCallAsyncFromStudioWorker(socketId: Int, params: JSONObject)

        fun onCallPromiseFromStudioWorker(socketId: Int, params: JSONObject)

        fun onCallGetLibraryFromStudioWorker(socketId: Int,methodName:String)
    }

    var applicationContext: Context? = null

    var gxSocketToStudioListener: GXSocketToStudioListener? = null


    private var socketHelper: GXSocket? = null
    private var currentAddress: String? = null
    private var currentTemplateId: String? = null
    private var currentType: String = "auto"
    private var isWaitDisconnectMsgThenConnectGaiaStudio = false

    private val gxSocketListener: GXSocket.GXSocketListener = object : GXSocket.GXSocketListener {

        override fun onSocketConnected() {
            sendInitMsg()
        }

        override fun onSocketDisconnected() {
            if (isWaitDisconnectMsgThenConnectGaiaStudio) {
                //ip改变时的断开重连
                isWaitDisconnectMsgThenConnectGaiaStudio = false
                toConnectGaiaStudio()
            }
        }

        override fun onStudioConnected() {

            Log.d(TAG, "onStudioConnected() called currentTemplateId = $currentTemplateId")
            if (currentTemplateId != null) {
                socketHelper?.sendGetTemplateData(currentTemplateId)
            }
        }

        override fun onStudioAddData(templateId: String, templateData: JSONObject) {
            gxSocketToStudioListener?.onAddData(templateId, templateData)
        }

        override fun onStudioUpdate(templateId: String, templateJson: JSONObject) {
            gxSocketToStudioListener?.onUpdate(templateId, templateJson)
        }
    }

    fun init(context: Context) {
        applicationContext = context.applicationContext
        if (socketHelper == null) {
            socketHelper = GXSocket()
        }
    }

    fun destroy() {
        socketHelper?.disconnectToServer()
        socketHelper = null
    }

    fun manualConnect1(context: Context, params: JSONObject) {
        if (isConnectVpn(context)) {
            Log.e(TAG, "manualConnect: 请断开手机VPN后重试")
            return
        }
        Log.e(TAG, "onlyConnect() called with: params = [$params]")
        val targetUrl = params.getString("URL")
        val type = params.getString("TYPE")
        tryToConnectGaiaStudio(targetUrl, null, type)
    }

    fun autoConnect(context: Context, params: JSONObject) {
        if (isConnectVpn(context)) {
            Log.e(TAG, "manualConnect: 请断开手机VPN后重试")
            return
        }
        Log.e(TAG, "execute() called with: params = [$params]")
        val targetUrl = params.getString("URL")
        val templateId = params.getString("TEMPLATE_ID")
        val type = params.getString("TYPE")
        tryToConnectGaiaStudio(targetUrl, templateId, type)
    }

    fun manualConnect(context: Context, params: JSONObject) {
        if (isConnectVpn(context)) {
            Log.e(TAG, "manualConnect: 请断开手机VPN后重试")
            return
        }
        Log.e(TAG, "onlyConnect() called with: params = [$params]")
        val targetUrl = params.getString("URL")
        val type = params.getString("TYPE")
        tryToConnectGaiaStudio(targetUrl, null, type)
    }

    fun getParams(url: String?): JSONObject? {
        if (url == null || TextUtils.isEmpty(url)) {
            return null
        }
        val finalUrl = try {
            URLDecoder.decode(url, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return null
        }
        Log.e(TAG, "getParams() called with:  finalUrl = [$finalUrl]")
        val regexUrl = "[ws://]+[\\d+.\\d+.\\d+.\\d+]+[:\\d+]*"
        val pattern = Pattern.compile(regexUrl)
        val matcher = pattern.matcher(finalUrl)
        if (matcher.find()) {
            //局域网下IP
            val targetUrl = matcher.group()
//            val templateId = parseTemplateId(finalUrl)
            val type = parseConnectType(finalUrl)
            val result = JSONObject()
            result["URL"] = targetUrl
            result["TYPE"] = type
//            result["TEMPLATE_ID"] = templateId
            Log.e(TAG, "getParams() called with:  result = [$result]")
            return result
        } else {
            Log.e(TAG, "Can not find web url through regex.")
        }
        return null
    }

//    private fun parseConnectType(url: String): String {
//        try {
//            return url.split("&".toRegex()).toTypedArray()[2].split("=".toRegex()).toTypedArray()[1]
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//        return ""
//    }

    private fun parseConnectType(url: String): String {
        try {
            return url.split("&".toRegex()).toTypedArray()[1].split("=".toRegex()).toTypedArray()[1]
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun isConnectVpn(context: Context): Boolean {
        val allNetworkInfo =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).allNetworkInfo
        for (i in allNetworkInfo.indices) {
            val networkInfo = allNetworkInfo[i]
            if (networkInfo.type == ConnectivityManager.TYPE_VPN && networkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    private fun tryToConnectGaiaStudio(address: String, templateId: String?, type: String) {
        Log.e(TAG, "tryToConnectGaiaStudio() called with: address = [$address], templateId = [$templateId], type = [$type]")
        val tmpAddress = currentAddress
        currentType = type
        currentAddress = address
        currentTemplateId = templateId
        if (tmpAddress != null && tmpAddress != address) {
            // 地址不同，需要先断开链接，再尝试连接GaiaStudio
            isWaitDisconnectMsgThenConnectGaiaStudio = true
            socketHelper?.disconnectToServer()
        } else {
            // 链接相同，直接连接GaiaStudio
            toConnectGaiaStudio()
        }
    }

    private fun toConnectGaiaStudio() {
        if (socketHelper?.gxSocketIsConnected == false) {
            socketHelper?.gxSocketListener = gxSocketListener
            socketHelper?.connectToServer(currentAddress)
        } else {
            sendInitMsg()
        }
    }

    private fun sendInitMsg() {
        socketHelper?.sendMsgWithMultiTypeInit()
    }

    fun sendMessage(data: JSONObject) {
        socketHelper?.sendMessage(data)
    }


    fun sendMsgForObtainMode() {
        Log.d(GXSocket.TAG, "sendMsgForObtainMode() called ")
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["method"] = "mode/get"
        data["id"] = 302
        socketHelper?.sendMessage(data)
    }

    fun sendMsgForDisconnect() {
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["method"] = "mode/get"
        data["id"] = 304
        socketHelper?.sendMessage(data)
    }

    fun sendMsgForGetTemplateData(templateId: String?) {
        socketHelper?.sendGetTemplateData(templateId)
    }

    fun sendMsgForJSLog(logLevel: String, logContent: String) {
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["method"] = "js/console"
        val params = JSONObject()
        params["level"] = logLevel
        params["data"] = logContent
        data["params"] = params
        socketHelper?.sendMessage(data)
    }

    fun setDevTools(dev: IDevTools) {
        socketHelper?.devTools = dev
    }

    fun setJSReceiverListener(listener: GXSocketJSReceiveListener) {
        socketHelper?.gxSocketJSReceiveListener = listener
    }

    fun isGaiaStudioConnected(): Boolean? {
        return socketHelper?.gxSocketIsConnected
    }

    companion object {
        const val TAG = "[GXStudioMulti]"

        const val PREVIEW_AUTO = "auto"
        const val PREVIEW_MANUAL = "manual"
        const val PREVIEW_NONE = "none"

        const val JS_DEFAULT = "default"
        const val JS_BREAKPOINT = "breakpoint"

        val instance by lazy {
            GXClientToStudioMultiType()
        }
    }
}