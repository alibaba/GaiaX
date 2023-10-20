package com.alibaba.gaiax.js.impl.debug

import android.text.TextUtils
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.api.IGXCallback
import com.alibaba.gaiax.js.api.IGXPromise
import com.alibaba.gaiax.js.engine.GXHostContext
import com.alibaba.gaiax.js.utils.Log

/**
 *  @author: shisan.lms
 *  @date: 2023-03-15
 *  Description:
 *      1.封装发送协议
 *          "sendInitEnvJSScript":400
 *          "sendCreateJSComponent":401
 *          "sendEvalScript":402
 *      2.处理SocketMethod的方法：
 *          a.对callSync/Async/Promise的方法执行Native对应的原生方法
 *          b.将对应的QuickJS实现通过Websocket转交Worker实现
 */
internal class DebugJSBridgeModule(
    private val hostContext: GXHostContext,
    private val debugJSContext: DebugJSContext
) : ISocketBridgeListener {

    enum class WebsocketJSMethodName(val methodName: String) {
        InitEnv("js/initJSEnv"), CreateComponent("js/createComponent"), Eval("js/eval"), CallSync("js/callSync"), CallAsync(
            "js/callAsync"
        ),
        CallPromise("js/callPromise")
    }

    override fun callSync(socketId: Int, params: JSONObject) {
        val conTextId = params["contextId"] as Int
        val moduleId = params["moduleId"] as Int
        val methodId = params["methodId"] as Int
        val args = params["args"] as JSONArray
        val script = hostContext.bridge.callSync(
            conTextId.toLong(), moduleId.toLong(), methodId.toLong(), args
        )
        this.sendWorkerMethodResult(WebsocketJSMethodName.CallSync, socketId, script.toString())
    }

    override fun callAsync(socketId: Int, params: JSONObject) {
        val conTextId = params["contextId"] as Int
        val moduleId = params["moduleId"] as Int
        val methodId = params["methodId"] as Int
        val callBackId = params["callbackId"] as Int
        val args = params["args"] as JSONArray
        args.add(object : IGXCallback {
            override fun invoke(result: Any?) {
                if (Log.isLog()) {
                    Log.e("callAsync() called with: IGaiaXAsyncCallback result = $result")
                    val script = if (result != null && !TextUtils.isEmpty(result.toString())) {
                        "Bridge.invokeCallback(${callBackId}, $result)"
                    } else {
                        "Bridge.invokeCallback(${callBackId})"
                    }
                    this@DebugJSBridgeModule.sendWorkerMethodResult(
                        WebsocketJSMethodName.CallAsync, socketId, script
                    )
                }

            }
        })
        hostContext.bridge.callAsync(conTextId.toLong(), moduleId.toLong(), methodId.toLong(), args)
    }

    override fun callPromise(socketId: Int, params: JSONObject) {
        val conTextId = params["contextId"] as Int
        val moduleId = params["moduleId"] as Int
        val methodId = params["methodId"] as Int
        val callBackId = params["callbackId"] as Int
        val args = params["args"] as JSONArray
        args.add(object : IGXPromise {
            override fun resolve(): IGXCallback {
                return object : IGXCallback {
                    override fun invoke(result: Any?) {
                        val script = if (result != null && !TextUtils.isEmpty(result.toString())) {
                            "Bridge.invokePromiseSuccess(${callBackId}, $result)"
                        } else {
                            "Bridge.invokePromiseSuccess(${callBackId})"
                        }
                        this@DebugJSBridgeModule.sendWorkerMethodResult(
                            WebsocketJSMethodName.CallPromise, socketId, script
                        )
                    }
                }
            }

            override fun reject(): IGXCallback {
                return object : IGXCallback {
                    override fun invoke(result: Any?) {
                        val script = if (result != null && !TextUtils.isEmpty(result.toString())) {
                            "Bridge.invokePromiseFailure(${callBackId}, $result)"
                        } else {
                            "Bridge.invokePromiseFailure(${callBackId})"
                        }
                        this@DebugJSBridgeModule.sendWorkerMethodResult(
                            WebsocketJSMethodName.CallPromise, socketId, script
                        )
                    }
                }
            }
        })
        hostContext.bridge.callPromise(
            conTextId.toLong(), moduleId.toLong(), methodId.toLong(), args
        )
    }

    override fun callGetLibrary(socketId: Int, methodName: String) {
        if (!TextUtils.isEmpty(debugJSContext.bootstrap)) {
            val message = JSONObject()
            message["jsonrpc"] = "2.0"
            message["id"] = socketId
            val param = JSONObject()
            param["script"] = debugJSContext.bootstrap
            message["result"] = param
            GXJSEngine.instance.socketSender?.onSendMsg(message)
        } else {
            throw IllegalArgumentException("initialized GaiaXStudio message is Empty")
        }
    }


    fun sendInitEnvJSScript(data: String) {
        if (!TextUtils.isEmpty(data)) {
            val message = JSONObject()
            message["jsonrpc"] = "2.0"
            message["method"] = WebsocketJSMethodName.InitEnv.methodName
            message["id"] = 400
            val param = JSONObject()
            param["script"] = data
            message["params"] = param
            GXJSEngine.instance.socketSender?.onSendMsg(message)
        } else {
            throw java.lang.IllegalArgumentException("initialized GaiaXStudio message is Empty")
        }
    }

    fun sendCreateJSComponent(
        templateId: String, instanceId: String, templateVersion: String, bizId: String
    ) {
        val message = JSONObject()
        message["jsonrpc"] = "2.0"
        message["method"] = WebsocketJSMethodName.CreateComponent.methodName
        message["id"] = 401
        val param = JSONObject()
        param["templateId"] = templateId
        param["instanceId"] = instanceId
        param["templateVersion"] = templateVersion
        param["bizId"] = bizId
        message["params"] = param
        GXJSEngine.instance.socketSender?.onSendMsg(message)
    }

    fun sendEvalScript(script: String) {
        val message = JSONObject()
        message["jsonrpc"] = "2.0"
        message["method"] = WebsocketJSMethodName.Eval.methodName
        message["id"] = 402
        val param = JSONObject()
        param["script"] = script
        message["params"] = param
        GXJSEngine.instance.socketSender?.onSendMsg(message)
    }

    fun sendWorkerMethodResult(methodName: WebsocketJSMethodName, socketId: Int, script: String) {
        val message = JSONObject()
        message["jsonrpc"] = "2.0"
        message["id"] = socketId
        val param = JSONObject()
        if (methodName.methodName == WebsocketJSMethodName.CallSync.methodName) {
            param["value"] = script
        } else {
            param["script"] = script
        }
        message["result"] = param

        GXJSEngine.instance.socketSender?.onSendMsg(message)
    }

}