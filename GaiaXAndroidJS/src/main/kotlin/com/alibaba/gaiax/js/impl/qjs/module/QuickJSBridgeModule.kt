package com.alibaba.gaiax.js.impl.qjs.module

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GaiaXJSManager
import com.alibaba.gaiax.js.api.IGaiaXCallback
import com.alibaba.gaiax.js.api.IGaiaXPromise
import com.alibaba.gaiax.js.core.GaiaXContext
import com.alibaba.gaiax.js.support.JSDataConvert
import com.alibaba.gaiax.js.utils.Log
import com.alibaba.gaiax.quickjs.BridgeModuleListener
import com.alibaba.gaiax.quickjs.JSContext
import com.alibaba.gaiax.quickjs.JSFunction

internal class QuickJSBridgeModule(private val hostContext: GaiaXContext, private val jsContext: JSContext) : BridgeModuleListener {

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

            val result = hostContext.bridge.callSync(contextId, moduleId, methodId, args) ?: jsContext.createJSNull().pointer

            val jsValue = JSDataConvert.convertToJSValue(jsContext, result)
            return jsValue.pointer
        }
        return jsContext.createJSUndefined().pointer
    }

    override fun callAsync(contextPointer: Long, funPointer: Long, argsMap: String): Long {
        if (Log.isLog()) {
            Log.e("callAsync() called with: jsContext = $contextPointer, argsMap = $argsMap")
        }
        if (jsContext.pointer == contextPointer) {
            val target = JSONObject.parseObject(argsMap)
            val contextId = target.getLongValue("contextId")
            val moduleId = target.getLongValue("moduleId")
            val methodId = target.getLongValue("methodId")
            val args = target.getJSONArray("args")

            args.add(object : IGaiaXCallback {
                override fun invoke(result: Any?) {
                    if (Log.isLog()) {
                        Log.e("callAsync() called with: IGaiaXAsyncCallback result = $result")
                    }
                    GaiaXJSManager.instance.executeTask {
                        val jsFunction = JSFunction(funPointer, jsContext)
                        jsFunction.dupValue()
                        jsFunction.invoke(null, arrayOfJSValues(result))
                        jsFunction.freeValue()
                    }
                }
            })

            hostContext.bridge.callAsync(contextId, moduleId, methodId, args)
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
            Log.e("callPromise() called with: jsContext = $contextPointer, argsMap = $argsMap")
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
            args.add(object : IGaiaXPromise {
                override fun resolve(): IGaiaXCallback {
                    return object : IGaiaXCallback {
                        override fun invoke(result: Any?) {
                            GaiaXJSManager.instance.executeTask {
                                jsResolve?.invoke(null, arrayOfJSValues(result))
                            }
                        }
                    }
                }

                override fun reject(): IGaiaXCallback {
                    return object : IGaiaXCallback {
                        override fun invoke(result: Any?) {
                            GaiaXJSManager.instance.executeTask {
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

    override fun wrapAsJSValueException(e: Exception?) {
        e?.let {
            GaiaXJSManager.instance.errorListener?.errorLog(JSONObject().apply {
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