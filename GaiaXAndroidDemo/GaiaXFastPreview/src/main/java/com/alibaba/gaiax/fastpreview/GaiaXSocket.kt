package com.alibaba.gaiax.fastpreview

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

class GaiaXSocket : SocketListener {

    interface Listener {

        fun onSocketConnectFail()

        fun onSocketDisconnected()

        fun onSocketConnected()

        fun onFastPreviewInit()

        fun onFastPreviewDataChanged(templateId: String, template: JSONObject)

        fun onManualPushInit()

        fun onManualPushTemplateDataChanged(templateId: String, template: JSONObject)
    }

    private var currentId = 0

    private var webSocketManager: WebSocketManager? = null

    private var webSocketSetting: WebSocketSetting? = null

    private var serverAddress: String? = null

    var isConnected = false

    private var listener: Listener? = null

    /**
     * 手动推送
     */
    fun isManualPush(type: String) = "manual" == type

    /**
     * 实时预览
     */
    fun isFastPreview(type: String) = "auto" == type

    fun setListener(listener: Listener?) {
        this.listener = listener
    }

    fun disconnectToServer() {
        webSocketManager?.disConnect()
    }

    fun connectToServer(address: String?) {
        serverAddress = address
        if (serverAddress == null || serverAddress?.isEmpty() == true) {
            isConnected = false
            if (listener != null) {
                listener!!.onSocketConnectFail()
            }
            return
        }
        isConnected = true

        //初始化WebSocket
        webSocketSetting = WebSocketSetting()
        webSocketSetting?.connectUrl = serverAddress //必填
        //设置连接超时时间
        webSocketSetting?.connectTimeout = 15 * 1000
        //设置心跳间隔时间
        webSocketSetting?.connectionLostTimeout = 0
        //设置断开后的重连次数，可以设置的很大，不会有什么性能上的影响
        webSocketSetting?.reconnectFrequency = 1
        //
        webSocketManager = WebSocketHandler.initGeneralWebSocket(SOCKET_KEY, webSocketSetting)
        webSocketManager?.addListener(this)
    }

    override fun onConnected() {
        Log.e(TAG, "onConnected() called")
        isConnected = true
        listener?.onSocketConnected()
    }

    override fun onConnectFailed(e: Throwable) {
        Log.e(TAG, "onConnectFailed() called with: e = [$e]")
        isConnected = false
        listener?.onSocketConnectFail()
    }

    override fun onDisconnect() {
        Log.e(TAG, "onDisconnect() called")
        webSocketManager?.destroy()
        webSocketManager = null
        WebSocketHandler.removeWebSocket(SOCKET_KEY)
        serverAddress = null
        webSocketSetting = null
        isConnected = false
        listener?.onSocketDisconnected()
    }

    override fun onSendDataError(errorResponse: ErrorResponse) {}

    override fun <T> onMessage(message: String, data: T) {
        if (message.isEmpty()) {
            return
        }
        val msgData = JSONObject.parseObject(message)
        val socketId = msgData.getString("id")
        val method = msgData.getString("method")

        Log.e(TAG, "onMessage() called with: socketId = [$socketId], method = [$method]")

        if (socketId != null && socketId.isNotEmpty()) {
            // 客户端主动请求
            val targetId = socketId.toInt()

            // 主动获取模板ID的模板数据
            // 仅在FastPreview时会被使用到
            if (JSON_RPC_ID_OBTAIN_TEMPLATE_DATA == targetId) {
                val resultJson = msgData.getJSONObject("result")
                val templateId = resultJson.getString("id")
                val templateData = resultJson.getJSONObject("data")

                val template = toFastPreviewGaiaXTemplate(templateData)
                listener?.onFastPreviewDataChanged(templateId, template)

            } else if (JSON_RPC_ID_OBTAIN_CHILDREN_TEMPLATE_DATA == targetId) {
                val resultJson = msgData.getJSONObject("result")
                val templateId = resultJson.getString("id")
                val templateData = resultJson.getJSONObject("data")

                val template = toFastPreviewGaiaXTemplate(templateData)

                listener?.onFastPreviewDataChanged(templateId, template)
            } else if (JSON_RPC_ID_MANUAL_PUSH_INIT == targetId) {
                listener?.onManualPushInit()
                currentId = JSON_RPC_ID_MANUAL_PUSH_INIT
            } else if (JSON_RPC_ID_FAST_PREVIEW_INIT == targetId) {
                listener?.onFastPreviewInit()
                currentId = JSON_RPC_ID_FAST_PREVIEW_INIT
            }
        } else if (method != null && method.isNotEmpty()) {
            // 被动更新 由GaiaStudio主动推送
            val params = msgData.getJSONObject("params")
            val templateId = params.getString("templateId")

            Log.e(TAG, "onMessage() called with: templateId = [$templateId]")

            if (templateId != null) {
                val templateData = params.getJSONObject("data")
                if (templateData != null) {
                    // 实时预览
                    if (currentId == JSON_RPC_ID_FAST_PREVIEW_INIT && "template/didChangedNotification" == method) {
                        val template = toFastPreviewGaiaXTemplate(templateData)
                        listener?.onFastPreviewDataChanged(templateId, template)
                    } else if (currentId == JSON_RPC_ID_MANUAL_PUSH_INIT && "template/didManualChangedNotification" == method) {
                        listener?.onManualPushTemplateDataChanged(templateId, templateData)
                    }
                }
            }
        }
    }

    override fun <T> onMessage(bytes: ByteBuffer, data: T) {}
    override fun onPing(framedata: Framedata) {}
    override fun onPong(framedata: Framedata) {}

    fun sendMsg(data: JSONObject) {
        Log.e(TAG, "sendMsg() called with: data = [$data]")
        WebSocketHandler.getWebSocket(SOCKET_KEY).send(data.toJSONString())
    }

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
        data["id"] = JSON_RPC_ID_FAST_PREVIEW_INIT
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
        data["id"] = JSON_RPC_ID_MANUAL_PUSH_INIT
        WebSocketHandler.getWebSocket(SOCKET_KEY).send(data.toJSONString())
    }

    /**
     * 嵌套模板需要获取依赖模板，手动输入需要ID
     */
    fun sendObtainTemplateDataMsg(templateId: String) {
        Log.e(TAG, "sendTemplateMsg() called with: templateId = [$templateId]")
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["method"] = "template/getTemplateData"
        val params = JSONObject()
        params["id"] = templateId
        data["params"] = params
        data["id"] = JSON_RPC_ID_OBTAIN_TEMPLATE_DATA
        WebSocketHandler.getWebSocket(SOCKET_KEY).send(data.toJSONString())
    }

    /**
     * 嵌套模板需要获取依赖模板，手动输入需要ID
     */
    fun sendObtainChildrenTemplateDataMsg(templateId: String) {
        Log.e(TAG, "sendTemplateMsg() called with: templateId = [$templateId]")
        val data = JSONObject()
        data["jsonrpc"] = "2.0"
        data["method"] = "template/getTemplateData"
        val params = JSONObject()
        params["id"] = templateId
        data["params"] = params
        data["id"] = JSON_RPC_ID_OBTAIN_CHILDREN_TEMPLATE_DATA
        WebSocketHandler.getWebSocket(SOCKET_KEY).send(data.toJSONString())
    }

    private fun toFastPreviewGaiaXTemplate(templateData: JSONObject): JSONObject {
        val result = JSONObject()
        val layer = templateData.getString("index.json")
        val layerJson = JSONObject.parseObject(layer)
        result["index.json"] = layer
        result["index.css"] = templateData.getString("index.css")
        result["index.js"] = templateData.getString("index.js")
        val indexDatabinding = templateData.getString("index.databinding").trim { it <= ' ' }
        val indexMock = templateData.getString("index.mock")
        val indexData = templateData.getString("index.data").trim { it <= ' ' }
        if ("{}" != indexDatabinding && indexMock != null) {
            result["index.mock"] = JSONObject.parseObject(indexMock)
            result["index.databinding"] = indexDatabinding
        } else {
            val convertDataBinding = if (layerJson.containsKey("sub-type")) {
                val id = layerJson.getString("id")
                val result1 = JSONObject()
                val targetData = JSONObject()
                targetData[id] = JSONObject().apply {
                    this["value"] = "\${nodes}"
                }
                result1["data"] = targetData
                result1.toJSONString()
            } else {
                val result1 = JSONObject()
                result1["data"] = indexData
                result1.toJSONString()
            }
            result["index.mock"] = JSONObject().apply {
                this["nodes"] = JSONArray().apply {
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                    this.add(JSONObject())
                }
            }
            result["index.databinding"] = convertDataBinding
        }
        return result
    }

    companion object {
        const val TAG = "[GaiaX][Socket]"
        private const val JSON_RPC_ID_OBTAIN_TEMPLATE_DATA = 100
        private const val JSON_RPC_ID_OBTAIN_CHILDREN_TEMPLATE_DATA = 103
        private const val JSON_RPC_ID_MANUAL_PUSH_INIT = 101
        private const val JSON_RPC_ID_FAST_PREVIEW_INIT = 102
        private const val SOCKET_KEY = "GaiaXSocket"
    }

}