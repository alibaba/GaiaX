package com.alibaba.gaiax.js.impl.qjs

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.engine.GXHostRuntime
import com.alibaba.gaiax.js.engine.IEngine
import com.alibaba.gaiax.js.engine.IRuntime
import com.alibaba.gaiax.js.utils.Log
import com.alibaba.gaiax.js.utils.runE
import com.alibaba.gaiax.quickjs.JSRuntime

internal class QuickJSRuntime private constructor(val runtime: GXHostRuntime, val engine: QuickJSEngine) :
    IRuntime {

    companion object {
        private const val TAG = "QuickJSRuntime"

        fun create(runtime: GXHostRuntime, engine: IEngine): QuickJSRuntime {
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

            Log.runE(TAG) { "setPromiseRejectionHandler() called with: message = $message" }

            GXJSEngine.instance.logListener?.errorLog(JSONObject().apply {
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
            Log.runE(TAG) { "setInterruptHandler() called with:" }
            false
        }
    }

    override fun destroyRuntime() {
        jsRuntime?.close()
        jsRuntime = null
    }

}