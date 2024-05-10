package com.alibaba.gaiax.studio

import android.content.Context
import android.net.ConnectivityManager
import android.net.Uri
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
class GXStudioClient {

    interface IFastPreviewListener {
        fun onAddData(templateId: String, templateData: JSONObject)
        fun onUpdate(templateId: String, templateData: JSONObject)
    }

    interface ISocketReceiver {
        fun onReceiveCallSync(socketId: Int, params: JSONObject)

        fun onReceiveCallAsync(socketId: Int, params: JSONObject)

        fun onReceiveCallPromise(socketId: Int, params: JSONObject)

        fun onReceiveCallGetLibrary(socketId: Int, methodName: String)
    }

    var applicationContext: Context? = null

    var fastPreviewListener: IFastPreviewListener? = null

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
            socketHelper?.devTools?.changeDevToolsConnectedStateView()
        }

        override fun onStudioConnected() {

            Log.d(TAG, "onStudioConnected() called currentTemplateId = $currentTemplateId")
            if (currentTemplateId != null) {
                socketHelper?.sendGetTemplateData(currentTemplateId)
            }
            socketHelper?.devTools?.changeDevToolsConnectedStateView()
        }

        override fun onStudioAddData(templateId: String, templateData: JSONObject) {
            fastPreviewListener?.onAddData(templateId, templateData)
        }

        override fun onStudioUpdate(templateId: String, templateJson: JSONObject) {
            fastPreviewListener?.onUpdate(templateId, templateJson)
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


    /**
     * url=ws://30.50.242.154:9292&id=A&type=auto
     */
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

        val uri = Uri.parse(url)
        val queryUrl = uri.getQueryParameter("url")
        val id = uri.getQueryParameter("id")
        val type = uri.getQueryParameter("type")

        Log.e(TAG, "getParams() called with: queryUrl = [$queryUrl] id=$id type=$type")

        val regexUrl = "[ws://]+[\\d+.\\d+.\\d+.\\d+]+[:\\d+]*"
        val pattern = Pattern.compile(regexUrl)
        val matcher = pattern.matcher(queryUrl)
        if (matcher.find()) {
            //局域网下IP
            val targetUrl = matcher.group()
            val result = JSONObject()
            result["URL"] = targetUrl
            result["TYPE"] = type
            result["TEMPLATE_ID"] = id
            Log.e(TAG, "getParams() called with:  result = [$result]")
            return result
        } else {
            Log.e(TAG, "Can not find web url through regex.")
        }
        return null
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

    fun sendMsg(data: JSONObject) {
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
        data["method"] = "close"
        socketHelper?.sendMessage(data)
    }

    fun sendMsgForGetTemplateData(templateId: String?) {
        socketHelper?.sendGetTemplateData(templateId)
    }

    fun sendJSLogMsg(logLevel: String, logContent: String) {
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

    fun setSocketReceiver(iSocketReceiver: ISocketReceiver) {
        socketHelper?.socketReceiver = iSocketReceiver
    }

    fun isGaiaStudioConnected(): Boolean? {
        return socketHelper?.gxSocketIsConnected
    }

    companion object {
        const val TAG = "[GaiaX][Studio]"

        const val PREVIEW_AUTO = "auto"
        const val PREVIEW_MANUAL = "manual"
        const val PREVIEW_NONE = "none"

        const val JS_DEFAULT = "default"
        const val JS_BREAKPOINT = "breakpoint"

        val instance by lazy {
            GXStudioClient()
        }
    }
}