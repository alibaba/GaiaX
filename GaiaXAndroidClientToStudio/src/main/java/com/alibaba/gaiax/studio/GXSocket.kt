package com.alibaba.gaiax.studio

import android.os.Handler
import android.os.Looper
import android.util.Log
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.zhangke.websocket.SocketListener
import com.zhangke.websocket.WebSocketHandler
import com.zhangke.websocket.WebSocketManager
import com.zhangke.websocket.WebSocketSetting
import com.zhangke.websocket.response.ErrorResponse
import org.java_websocket.framing.Framedata
import java.nio.ByteBuffer

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

    private var updateTask: Runnable? = null
    private var lastTemplateId: String? = null
    private var subTemplateCount = 0
    private var mainTemplateId: String? = null
    private var mainTemplateJson: JSONObject? = null

    private var webSocketManager: WebSocketManager? = null
    private var webSocketSetting: WebSocketSetting? = null
    private var serverAddress: String? = null

    private val uiHandler = Handler(Looper.getMainLooper())

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
        webSocketSetting = WebSocketSetting()
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
        WebSocketHandler.getWebSocket(SOCKET_KEY).send(data.toJSONString())
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
        WebSocketHandler.getWebSocket(SOCKET_KEY).send(data.toJSONString())
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
        WebSocketHandler.getWebSocket(SOCKET_KEY).send(data.toJSONString())
    }

    fun sendGetTemplateData103(templateId: String?) {
        Log.e(TAG, "sendGetTemplateData103() called with: templateId = $templateId")
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["method"] = "template/getTemplateData"
        val params = JSONObject()
        params["id"] = templateId
        data["params"] = params
        data["id"] = 103
        WebSocketHandler.getWebSocket(SOCKET_KEY).send(data.toJSONString())
    }

    private fun createTemplateData(templateData: JSONObject): JSONObject {
        val result = JSONObject()
        val index_json = templateData.getString("index.json")
        val index_css = templateData.getString("index.css")
        val index_js = templateData.getString("index.js")
        val index_databinding = templateData.getString("index.databinding")
        val index_mock = templateData.getString("index.mock")
        val index_data = templateData.getString("index.data")
        if (index_mock != null) {
            result["index.mock"] = JSONObject.parseObject(index_mock)
        }
        if ("{}" != index_databinding && index_mock != null) {
            result["index.databinding"] = index_databinding
        } else if (index_data != null) {
            val index_json_object = JSONObject.parseObject(index_json)
            val new_index_databinding = JSONObject()
            if (index_json_object.containsKey("sub-type")) {
                val id = index_json_object.getString("id")
                val data = JSONObject()
                data[id] = "\${nodes}"
                new_index_databinding["data"] = data
                result["index.databinding"] = new_index_databinding
                val mock = JSONObject()
                val nodes = JSONArray()
                nodes.add(JSONObject())
                nodes.add(JSONObject())
                nodes.add(JSONObject())
                nodes.add(JSONObject())
                nodes.add(JSONObject())
                mock["nodes"] = nodes
                result["index.mock"] = mock
            } else {
                new_index_databinding["data"] = index_data
                result["index.databinding"] = new_index_databinding
            }
        }
        result["index.json"] = JSONObject.parseObject(index_json)
        result["index.css"] = index_css
        result["index.js"] = index_js
        return result
    }

    companion object {
        const val TAG = "[GaiaX][FastPreview]"
        private const val SOCKET_KEY = "GaiaXSocket"
    }
}
