package com.youku.gaiax.js.impl.qjs

import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.GaiaXJSManager
import com.youku.gaiax.js.core.GaiaXRuntime
import com.youku.gaiax.js.core.api.IEngine
import com.youku.gaiax.js.core.api.IRuntime
import com.youku.gaiax.js.utils.Log
import com.youku.gaiax.quickjs.JSRuntime

internal class QuickJSRuntime private constructor(val runtime: GaiaXRuntime, val engine: QuickJSEngine) : IRuntime {

    companion object {
        fun create(runtime: GaiaXRuntime, engine: IEngine): QuickJSRuntime {
            return QuickJSRuntime(runtime, (engine as QuickJSEngine))
        }
    }

    var jsRuntime: JSRuntime? = null

    fun checkRuntime() {
        if (jsRuntime == null) {
            throw IllegalArgumentException("JSRuntime Instance Null")
        }
    }

    override fun initRuntime() {
        engine.checkQuickJS()
        jsRuntime = engine.quickJS?.createJSRuntime()

        // 设置栈Size为无限制
        jsRuntime?.setRuntimeMaxStackSize(0)

        // 设置Promise方法的异常兜底回调
        jsRuntime?.setPromiseRejectionHandler { message ->
            if (Log.isLog()) {
                Log.e("setPromiseRejectionHandler() called with: message = $message")
            }
            GaiaXJSManager.instance.errorListener?.errorLog(JSONObject().apply {
                this["data"] = JSONObject().apply {
                    this["message"] = message
                    this["templateId"] = ""
                    this["templateVersion"] = ""
                    this["bizId"] = ""
                }
            })
        }

        // 设置JS运行时是否需要中断
        jsRuntime?.setInterruptHandler {
            if (Log.isLog()) {
                Log.e("setInterruptHandler() called with:")
            }
            false
        }
    }

    override fun destroyRuntime() {
        jsRuntime?.close()
        jsRuntime = null
    }

}