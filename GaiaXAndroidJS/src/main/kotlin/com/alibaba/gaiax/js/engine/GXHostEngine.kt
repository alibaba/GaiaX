package com.alibaba.gaiax.js.engine

import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.impl.debug.DebugJSEngine
import com.alibaba.gaiax.js.impl.qjs.QuickJSEngine

/**
 * Engine -> Runtime -> Context -> Component
 */
internal class GXHostEngine private constructor(
    val engineId: Long, val type: GXJSEngine.EngineType
) {

    enum class State {
        NONE, INIT_START, INIT_END, RUNNING_START, RUNNING_END, DESTROY_START, DESTROY_END,
    }

    companion object {

        fun create(engineId: Long, type: GXJSEngine.EngineType): GXHostEngine {
            return GXHostEngine(engineId, type)
        }
    }

    @Volatile
    private var state: State = State.NONE

    private var hostRuntime: GXHostRuntime? = null

    private var realEngine: IEngine? = null

    internal fun initEngine() {
        if (state == State.NONE || state == State.DESTROY_END) {
            state = State.INIT_START

            // 初始化引擎
            if (realEngine == null) {
                realEngine = createEngine()
            }
            realEngine?.initEngine()

            // 初始化运行时
            if (hostRuntime == null) {
                realEngine?.let { engine ->
                    hostRuntime = GXHostRuntime.create(this, engine, type)
                }
            }
            hostRuntime?.initRuntime()

            state = State.INIT_END
        }
    }

    private fun createEngine() = when (type) {
        GXJSEngine.EngineType.QuickJS -> QuickJSEngine.create(this)
        GXJSEngine.EngineType.DebugJS -> DebugJSEngine(this)
    }

    internal fun startEngine(complete: (() -> Unit)?) {
        if (state == State.INIT_END) {
            state = State.RUNNING_START

            hostRuntime?.startRuntime(complete)

            state = State.RUNNING_END
        }
    }

    internal fun destroyEngine() {
        if (state == State.RUNNING_END || state == State.INIT_START) {

            state = State.DESTROY_START

            hostRuntime?.destroyRuntime()
            hostRuntime = null

            realEngine?.destroyEngine()
            realEngine = null

            state = State.DESTROY_END
        }
    }

    internal fun runtime(): GXHostRuntime? {
        return hostRuntime
    }

    internal fun getId(): Long {
        return engineId
    }
}