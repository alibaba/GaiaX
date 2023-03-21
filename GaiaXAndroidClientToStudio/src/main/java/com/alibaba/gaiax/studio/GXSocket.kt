package com.alibaba.gaiax.studio

import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.studio.third.socket.java_websocket.framing.Framedata
import com.alibaba.gaiax.studio.third.socket.websocket.SocketListener
import com.alibaba.gaiax.studio.third.socket.websocket.WebSocketHandler
import com.alibaba.gaiax.studio.third.socket.websocket.WebSocketManager
import com.alibaba.gaiax.studio.third.socket.websocket.WebSocketSetting
import com.alibaba.gaiax.studio.third.socket.websocket.response.ErrorResponse
import java.nio.ByteBuffer
import java.util.concurrent.ConcurrentHashMap

class GXSocket : SocketListener {

    interface GXSocketListener {
        fun onSocketDisconnected()
        fun onSocketConnected()
        fun onStudioConnected()
        fun onStudioAddData(templateId: String, templateData: JSONObject)
        fun onStudioUpdate(templateId: String, templateJson: JSONObject)
    }

    var gxSocketIsConnected = false
    var gxSocketListener: GXSocketListener? = null
    var gxSocketJSReceiveListener: GXClientToStudioMultiType.GXSocketJSReceiveListener? = null

    private var updateTask: Runnable? = null
    private var lastTemplateId: String? = null
    private var subTemplateCount = 0
    private var mainTemplateId: String? = null
    private var mainTemplateJson: JSONObject? = null

    private var webSocketManager: WebSocketManager? = null
    private var webSocketSetting: WebSocketSetting? = null
    private var serverAddress: String? = null

    private val uiHandler = Handler(Looper.getMainLooper())

    //管理方法id，明确接收的是来自哪个方法调用的响应 Map<Id,Method>
    private var methodIdManager = ConcurrentHashMap<Int, String>()

    var devTools: IDevTools? = null

    /**
     * 手动推送
     */
    fun isManualPush(type: String): Boolean {
        return "manual" == type
    }

    /**
     * 实时预览
     */
    fun isFastPreview(type: String): Boolean {
        return "auto" == type
    }

    fun disconnectToServer() {
        if (gxSocketIsConnected) {
            webSocketManager?.removeListener(this)
            webSocketManager?.disConnect()
            webSocketManager = null
        }
    }

    fun connectToServer(address: String?) {
        serverAddress = address
        if (serverAddress == null || serverAddress?.isEmpty() == true) {
            gxSocketIsConnected = false
            return
        }
        gxSocketIsConnected = true

        //初始化WebSocket
        webSocketSetting =
            WebSocketSetting()
        webSocketSetting?.connectUrl = serverAddress //必填
        //设置连接超时时间
        webSocketSetting?.connectTimeout = 15 * 1000
        //设置心跳间隔时间
        webSocketSetting?.connectionLostTimeout = 0
        //设置断开后的重连次数，可以设置的很大，不会有什么性能上的影响
        webSocketSetting?.reconnectFrequency = 1
        //网络状态发生变化后是否重连，
        //需要调用 WebSocketHandler.registerNetworkChangedReceiver(context) 方法注册网络监听广播
        webSocketSetting?.setReconnectWithNetworkChanged(true)

        //允许扫描不同的电脑
        WebSocketHandler.registerNetworkChangedReceiver(GXClientToStudio.instance.applicationContext)
        webSocketManager = WebSocketHandler.initGeneralWebSocket(SOCKET_KEY, webSocketSetting)
        webSocketManager?.addListener(this)
    }

    override fun onConnected() {
        Log.e(TAG, "onConnected() called")
        gxSocketIsConnected = true
        gxSocketListener?.onSocketConnected()
    }

    override fun onConnectFailed(e: Throwable) {
        Log.e(TAG, "onConnectFailed() called with: e = [$e]")
        gxSocketIsConnected = false
    }

    override fun onDisconnect() {
        Log.e(TAG, "onDisconnect() called")
        webSocketManager?.destroy()
        webSocketManager = null
        WebSocketHandler.removeWebSocket(SOCKET_KEY)
        serverAddress = null
        webSocketSetting = null
        gxSocketIsConnected = false
        gxSocketListener?.onSocketDisconnected()
        gxSocketListener = null
    }

    override fun onSendDataError(errorResponse: ErrorResponse?) {}

    override fun <T> onMessage(message: String?, data: T) {
        if (message == null || message.isEmpty()) {
            return
        }
        Log.d(TAG, "onMessage: $message")
        val msgData = JSONObject.parseObject(message)
        val socketId = msgData.getString("id")
        val socketMethod = if (msgData.containsKey("method")) msgData.getString("method") else methodIdManager[socketId.toInt()]

        Log.e(TAG, "onMessage() called with: socketId = [$socketId], method = [$socketMethod]")
        when (socketMethod) {
            "initialized" -> {
                gxSocketListener?.onStudioConnected()
            }
            "mode/get" -> {
                responseObtainMode(socketId.toInt())
            }
            "template/get" -> {
                val result = msgData.getJSONObject("result")
                if (result != null) {
                    obtainResultFromGetTemplate(result)
                }
            }
            "template/didChangedNotification" -> {
                val result = msgData.getJSONObject("params")
                if (result != null) {
                    obtainResultFromGetTemplate(result)
                }
            }
            "js/callSync" -> gxSocketJSReceiveListener?.onCallSyncFromStudioWorker(socketId.toInt(), msgData.getJSONObject("params"))
            "js/callAsync" -> gxSocketJSReceiveListener?.onCallAsyncFromStudioWorker(socketId.toInt(), msgData.getJSONObject("params"))
            "js/callPromise" -> gxSocketJSReceiveListener?.onCallPromiseFromStudioWorker(socketId.toInt(), msgData.getJSONObject("params"))
            "js/getLibrary" ->gxSocketJSReceiveListener?.onCallGetLibraryFromStudioWorker(socketId.toInt(),socketMethod)

        }

    }

    override fun <T> onMessage(bytes: ByteBuffer?, data: T) {}

    override fun onPing(framedata: Framedata?) {}

    override fun onPong(framedata: Framedata?) {}

    /**
     * 在GaiaX Studio中初始化成功将会持续接收
     * initialize
     * template/didChangedNotification
     */
    fun sendMsgWithFastPreviewInit() {
        Log.e(TAG, "sendMsgWithFastPreviewInit() called")
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["method"] = "initialize"
        data["id"] = 102
        sendMessage(data)
    }

    /**
     * 发送 ： initializeManual 初始化请求
     * 手动推送通知：  template/didManualChangedNotification
     */
    fun sendMsgWithManualPushInit() {
        Log.e(TAG, "sendMsgWithManualPushInit() called")
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["method"] = "initializeManual"
        data["id"] = 101
        sendMessage(data)
    }

    /**
     * 发送： initialized 初始化三合一请求
     */
    fun sendMsgWithMultiTypeInit() {
        Log.d(TAG, "sendMsgWithMultiTypeInit() called ")
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["method"] = "initialized"
        data["id"] = 301
        val params = JSONObject()
        params["version"] = "2.0"
        params["platform"] = "Android"
        params["deviceName"] = android.os.Build.MANUFACTURER + "-" + android.os.Build.MODEL
        params["systemName"] = "Android"
        params["systemVersion"] = android.os.Build.VERSION.RELEASE
        data["params"] = params
        sendMessage(data)
    }

    private fun sendGetTemplateData104(templateId: String?) {
        Log.e(TAG, "sendGetTemplateData104() called with: templateId = $templateId")
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["method"] = "template/getTemplateData"
        val params = JSONObject()
        params["id"] = templateId
        data["params"] = params
        data["id"] = 104
        sendMessage(data)
    }

    fun sendGetTemplateData103(templateId: String?) {
        Log.e(TAG, "sendGetTemplateData103() called with: templateId = $templateId")
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["method"] = "template/getTemplateData"
        val params = JSONObject()
        params["id"] = templateId
        data["params"] = params
        data["id"] = 104
        sendMessage(data)
    }

    fun sendGetTemplateData(templateId: String?) {
        Log.e(TAG, "sendGetTemplateData called with: templateId = $templateId")
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["method"] = "template/get"
        if (!TextUtils.isEmpty(templateId)) {
            val params = JSONObject()
            params["id"] = templateId
            data["params"] = params
        }
        data["id"] = 103
        sendMessage(data)
    }

    private fun createTemplateData(templateData: JSONObject): JSONObject {
        val result = JSONObject()
        val index_json = templateData.getString("index.json")
        val index_css = templateData.getString("index.css")
        val index_js = templateData.getString("index.js")
        val index_databinding = templateData.getString("index.databinding")
        val index_mock = templateData.getString("index.mock") ?: "{}"
        val index_data = templateData.getString("index.data") ?: ""

        result["index.mock"] = JSONObject.parseObject(index_mock)
        result["index.databinding"] = JSONObject.parseObject(index_databinding)
        result["index.json"] = JSONObject.parseObject(index_json)
        result["index.css"] = index_css
        result["index.js"] = index_js

        return result
    }

    private fun fastPreviewAndPushReceivingMsgProcessor(message: String) {
        val msgData = JSONObject.parseObject(message)
        val socketId = msgData.getString("id")
        val socketMethod = msgData.getString("method")

        Log.e(TAG, "onMessage() called with: socketId = [$socketId], method = [$socketMethod]")

        if ("template/didChangedNotification" == socketMethod || "template/didManualChangedNotification" == socketMethod) {
            val params = msgData.getJSONObject("params")
            val templateId = params.getString("templateId")
            val templateData = params.getJSONObject("data")
            if (templateId != null && templateData != null) {

                Log.e(TAG, "onMessage() called with: templateId = [$templateId]")

                val templateJson: JSONObject = createTemplateData(templateData)

                // 实时预览
                if ("template/didChangedNotification" == socketMethod) {
                    gxSocketListener?.onStudioAddData(templateId, templateJson)
                    lastTemplateId = templateId
                    updateTask?.let { uiHandler.removeCallbacks(it) }
                    updateTask =
                        Runnable { gxSocketListener?.onStudioUpdate(templateId, templateJson) }
                    updateTask?.let { uiHandler.postDelayed(it, 200) }
                }
                // 手动推送
                if ("template/didManualChangedNotification" == socketMethod) {
                    gxSocketListener?.onStudioAddData(templateId, templateJson)
                }
            }
        } else if ("103" == socketId) {
            val resultJson = msgData.getJSONObject("result")
            val templateId = resultJson.getString("id")
            val templateDataSrc = resultJson.getJSONObject("data")
            val templateJson: JSONObject = createTemplateData(templateDataSrc)
            gxSocketListener?.onStudioAddData(templateId, templateJson)
            templateJson
                .getJSONObject("index.json")
                ?.getJSONObject("package")
                ?.getJSONObject("dependencies")?.forEach {
                    subTemplateCount += 1
                    sendGetTemplateData104(it.key)
                }
            mainTemplateId = templateId
            mainTemplateJson = templateJson
            if (subTemplateCount == 0) {
                if (mainTemplateId != null && mainTemplateJson != null) {
                    gxSocketListener?.onStudioUpdate(mainTemplateId!!, mainTemplateJson!!)
                }
                mainTemplateId = ""
                mainTemplateJson = null
            }
        } else if ("104" == socketId) {
            subTemplateCount -= 1
            val resultJson = msgData.getJSONObject("result")
            val templateId = resultJson.getString("id")
            val templateDataSrc = resultJson.getJSONObject("data")
            val templateJson: JSONObject = createTemplateData(templateDataSrc)
            gxSocketListener?.onStudioAddData(templateId, templateJson)
            templateJson.getJSONObject("index.json")
                ?.getJSONObject("package")
                ?.getJSONObject("dependencies")?.forEach {
                    subTemplateCount += 1
                    sendGetTemplateData104(it.key)
                }
            if (subTemplateCount == 0) {
                if (mainTemplateId != null && mainTemplateJson != null) {
                    gxSocketListener?.onStudioUpdate(mainTemplateId!!, mainTemplateJson!!)
                }
                mainTemplateId = ""
                mainTemplateJson = null
            }
        } else if ("102" == socketId) {
            gxSocketListener?.onStudioConnected()
        } else {
            if ("" != lastTemplateId) {
                sendGetTemplateData103(lastTemplateId)
            }
        }
    }

    fun sendMessage(data: JSONObject) {
        if (data.containsKey("method") && data.containsKey("id")) {
            methodIdManager[data.getIntValue("id")] = data.getString("method")
        }
        WebSocketHandler.getWebSocket(SOCKET_KEY).send(data.toJSONString())
    }

    private fun responseObtainMode(socketId: Int) {
        if (devTools != null) {
            sendMsgForChangeMode(socketId, devTools!!.getPreviewCurrentMode(), devTools!!.getJSCurrentMode())
        }
    }

    /**
     * 处理"template/get"和"template/didChangedNotification"
     */
    private fun obtainResultFromGetTemplate(templateData: JSONObject) {
        //解析根模板
        val rootTemplateId = templateData.getString("templateId")
        val rootTemplateData = templateData.getJSONObject("templateData")
        val templateJson: JSONObject = createTemplateData(rootTemplateData)
        gxSocketListener?.onStudioAddData(rootTemplateId, templateJson)
        //解析子模板
        val subTemplates = templateData.getJSONArray("subTemplates")
        subTemplates.forEach {
            val subTemplateItem = it as JSONObject
            val subTemplateId = subTemplateItem.getString("templateId")
            val subTemplateData = subTemplateItem.getJSONObject("templateData")
            val subTemplateJson: JSONObject = createTemplateData(subTemplateData)
            gxSocketListener?.onStudioAddData(subTemplateId, subTemplateJson)
        }
        gxSocketListener?.onStudioUpdate(rootTemplateId, templateJson)
    }

    private fun sendMsgForChangeMode(
        socketId: Int,
        previewMode: String? = GXClientToStudioMultiType.PREVIEW_NONE,
        jsMode: String? = GXClientToStudioMultiType.JS_DEFAULT
    ) {
        Log.d(GXSocket.TAG, "sendMsgForChangeMode() called ")
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["id"] = socketId
        val result = JSONObject()
        result["preview"] = previewMode
        result["js"] = jsMode
        data["result"] = result
        sendMessage(data)
    }


    companion object {
        const val TAG = "[GaiaX][FastPreview]"
        private const val SOCKET_KEY = "GaiaXSocket"
    }
}
