package com.alibaba.gaiax.js.engine

import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.impl.debug.DebugJSRuntime
import com.alibaba.gaiax.js.impl.qjs.QuickJSRuntime

internal class GXHostRuntime(
    val hostEngine: GXHostEngine, val realEngine: IEngine, val type: GXJSEngine.EngineType
) {

    companion object {
        fun create(
            hostEngine: GXHostEngine, engine: IEngine, type: GXJSEngine.EngineType
        ): GXHostRuntime {
            return GXHostRuntime(hostEngine, engine, type)
        }
    }

    private var hostContext: GXHostContext? = null

    private var realRuntime: IRuntime? = null

    fun initRuntime() {

        // 初始化运行时
        if (realRuntime == null) {
            realRuntime = createRuntime()
        }
        realRuntime?.initRuntime()

        // 初始化上下文
        if (hostContext == null) {
            realRuntime?.let { runtime ->
                hostContext = GXHostContext.create(this, runtime, type)
            }
        }
        hostContext?.initContext()
    }

    fun startRuntime(complete: (() -> Unit)?) {
        hostContext?.startContext(complete)
    }

    fun destroyRuntime() {
        hostContext?.destroyContext()
        hostContext = null

        realRuntime?.destroyRuntime()
        realRuntime = null
    }

    private fun createRuntime(): IRuntime {
        return when (type) {
            GXJSEngine.EngineType.QuickJS -> QuickJSRuntime.create(this, realEngine)
            GXJSEngine.EngineType.DebugJS -> DebugJSRuntime.create(this, realEngine)
        }
    }

    fun context(): GXHostContext? {
        return hostContext
    }

}