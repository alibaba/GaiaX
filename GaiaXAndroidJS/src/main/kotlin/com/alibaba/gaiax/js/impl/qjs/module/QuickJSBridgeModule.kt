package com.alibaba.gaiax.js.impl.qjs.module

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.api.IGXCallback
import com.alibaba.gaiax.js.api.IGXPromise
import com.alibaba.gaiax.js.engine.GXHostContext
import com.alibaba.gaiax.js.support.JSDataConvert
import com.alibaba.gaiax.js.utils.GXJSUiExecutor
import com.alibaba.gaiax.js.utils.Log
import com.alibaba.gaiax.quickjs.BridgeModuleListener
import com.alibaba.gaiax.quickjs.JSContext
import com.alibaba.gaiax.quickjs.JSFunction

/**
 * 从JS运行时中调用某个Module的方法时，会从这里中转到HostContext中
 */
internal class QuickJSBridgeModule(private val hostContext: GXHostContext, private val jsContext: JSContext) :
    BridgeModuleListener {

    override fun callSync(contextPointer: Long, argsMap: String): Long {
        if (Log.isLog()) {
            Log.e("callSync() called with: jsContext = $contextPointer, argsMap = $argsMap")
        }
        if (jsContext.pointer == contextPointer) {
            val target = JSONObject.parseObject(argsMap)

            val contextId = target.getLongValue("contextId")
            val moduleId = target.getLongValue("moduleId")
            val methodId = target.getLongValue("methodId")
            val args = target.getJSONArray("args")


            // 处理异常 当args.data.stack存在时，说明是JS异常
            // {"moduleId":5,"methodId":22,"timestamp":788306541,"args":[{"data":{"templateId":"test","templateVersion":-1,"bizId":"fastpreview","message":"'data' is not defined","stack":"    at onReady ()\n    at call (native)\n    at <anonymous> ()\n    at <anonymous> (:5)\n    at <anonymous> (:7)\n"}}],"contextId":9}
            if (args.size >= 1 && args[0] is JSONObject) {
                try {
                    args.getJSONObject(0)?.takeIf { it["data"] is JSONObject }?.let { it ->
                        it.getJSONObject("data")?.let {
                            if (it.containsKey("stack")) {
                                GXJSUiExecutor.action {
                                    GXJSEngine.instance.jsExceptionListener?.exception(it)
                                }
                                jsContext.createJSUndefined().pointer
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    if (Log.isLog()) {
                        Log.e("callSync() called with: e = $e")
                    }
                    return jsContext.createJSUndefined().pointer
                }
            }

            //
            val result = hostContext.bridge.callSync(contextId, moduleId, methodId, args)
                ?: jsContext.createJSNull().pointer

            val jsValue = JSDataConvert.convertToJSValue(jsContext, result)
            return jsValue.pointer
        }
        return jsContext.createJSUndefined().pointer
    }

    override fun callAsync(contextPointer: Long, funPointer: Long, argsMap: String): Long {
        if (Log.isLog()) {
            Log.e("callAsync() called with: jsContext = $contextPointer, argsMap = $argsMap" + ", jsContext.pointer = " + jsContext.pointer + ", contextPointer = " + contextPointer)
        }
        if (jsContext.pointer == contextPointer) {
            try {
                val target = JSONObject.parseObject(argsMap)
                val contextId = target.getLongValue("contextId")
                val moduleId = target.getLongValue("moduleId")
                val methodId = target.getLongValue("methodId")
                val args = target.getJSONArray("args")

                args.add(object : IGXCallback {
                    override fun invoke(result: Any?) {
                        if (Log.isLog()) {
                            Log.e("callAsync() called with: IGaiaXAsyncCallback result = $result")
                        }
                        gxHostContext()?.executeTask {
                            val jsFunction = JSFunction(funPointer, jsContext)
                            jsFunction.dupValue()
                            jsFunction.invoke(null, arrayOfJSValues(result))
                            jsFunction.freeValue()
                        }
                    }
                })
                hostContext.bridge.callAsync(contextId, moduleId, methodId, args)
            } catch (t: Throwable) {
                if (Log.isLog()) {
                    Log.e("callAsync() called with: jsContext = $contextPointer, argsMap = $argsMap" + ", fail" + t.stackTraceToString())
                }
            }
        }
        return jsContext.createJSUndefined().pointer
    }

    private fun arrayOfJSValues(result: Any?) = if (result != null) {
        arrayOf(JSDataConvert.convertToJSValue(jsContext, result))
    } else {
        arrayOf()
    }

    override fun callPromise(contextPointer: Long, argsMap: String): Long {
        if (Log.isLog()) {
//            Log.e("callPromise() called with: jsContext = $contextPointer, argsMap = $argsMap")
        }
        if (jsContext.pointer == contextPointer) {

            val target = JSONObject.parseObject(argsMap)

            val contextId = target.getLongValue("contextId")
            val moduleId = target.getLongValue("moduleId")
            val methodId = target.getLongValue("methodId")
            val args = target.getJSONArray("args")

            var jsResolve: JSFunction? = null
            var jsReject: JSFunction? = null
            val jsPromise = jsContext.createJSPromise { resolve, reject ->
                jsResolve = resolve
                jsReject = reject
            }
            args.add(object : IGXPromise {
                override fun resolve(): IGXCallback {
                    return object : IGXCallback {
                        override fun invoke(result: Any?) {
                            gxHostContext()?.executeTask {
                                jsResolve?.invoke(null, arrayOfJSValues(result))
                            }
                        }
                    }
                }

                override fun reject(): IGXCallback {
                    return object : IGXCallback {
                        override fun invoke(result: Any?) {
                            gxHostContext()?.executeTask {
                                jsReject?.invoke(null, arrayOfJSValues(result))
                            }
                        }
                    }
                }
            })

            hostContext.bridge.callPromise(contextId, moduleId, methodId, args)

            return jsPromise.pointer
        }
        return jsContext.createJSUndefined().pointer
    }

    private fun gxHostContext() = GXJSEngine.instance.quickJSEngine?.runtime()?.context()

    override fun wrapAsJSValueException(e: Exception?) {
        e?.let {
            GXJSEngine.instance.logListener?.errorLog(JSONObject().apply {
                this["data"] = JSONObject().apply {
                    this["message"] = e.stackTrace.toString()
                    this["templateId"] = ""
                    this["templateVersion"] = ""
                    this["bizId"] = ""
                }
            })
        }
    }
}