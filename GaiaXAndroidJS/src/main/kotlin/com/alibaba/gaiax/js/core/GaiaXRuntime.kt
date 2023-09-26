package com.alibaba.gaiax.js.core

import com.alibaba.gaiax.js.GXJSEngineFactory
import com.alibaba.gaiax.js.GaiaXJSManager
import com.alibaba.gaiax.js.core.api.IEngine
import com.alibaba.gaiax.js.core.api.IRuntime
import com.alibaba.gaiax.js.impl.qjs.GaiaXJSDebuggerRuntime
import com.alibaba.gaiax.js.impl.qjs.QuickJSRuntime

internal class GaiaXRuntime(val host: GaiaXEngine, val engine: IEngine, val type: GXJSEngineFactory.GaiaXJSEngineType) {

    companion object {
        fun create(host: GaiaXEngine, engine: IEngine, type: GXJSEngineFactory.GaiaXJSEngineType): GaiaXRuntime {
            return GaiaXRuntime(host, engine, type)
        }
    }

    private var runtime: IRuntime? = null

    private var context: GaiaXContext? = null

    fun initRuntime() {

        // 初始化运行时
        if (runtime == null) {
            runtime = createRuntime()
        }
        runtime?.initRuntime()

        // 初始化上下文
        if (context == null) {
            context = GaiaXContext.create(this, runtime!!, type)
        }
        context?.initContext()
    }

    fun startRuntime(complete: () -> Unit) {
        context?.startContext(complete)
    }

    fun destroyRuntime() {
        context?.destroyContext()
        context = null

        runtime?.destroyRuntime()
        runtime = null
    }

    private fun createRuntime(): IRuntime {
        return when (type) {
            GXJSEngineFactory.GaiaXJSEngineType.GaiaXJSEngineTypeQuickJS -> QuickJSRuntime.create(this, engine)
            GXJSEngineFactory.GaiaXJSEngineType.GaiaXJSEngineTypeDebugger -> GaiaXJSDebuggerRuntime.create(this, engine)
        }
    }

    fun context(): GaiaXContext? {
        return context
    }


}