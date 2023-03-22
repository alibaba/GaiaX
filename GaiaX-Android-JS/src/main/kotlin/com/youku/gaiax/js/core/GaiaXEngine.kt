package com.youku.gaiax.js.core

import com.youku.gaiax.js.GaiaXJS
import com.youku.gaiax.js.core.api.IEngine
import com.youku.gaiax.js.impl.qjs.GaiaXJSDebuggerEngine
import com.youku.gaiax.js.impl.qjs.QuickJSEngine

/**
 * GaiaXJS.createEngin->initEngine->
 */
internal class GaiaXEngine private constructor(val engineId: Long, val type: GaiaXJS.GaiaXJSType) {

    enum class State {
        NONE,
        INIT_START,
        INIT_END,
        RUNNING_START,
        RUNNING_END,
        DESTROY_START,
        DESTROY_END,
    }


    companion object {

        fun create(engineId: Long, type: GaiaXJS.GaiaXJSType): GaiaXEngine {
            return GaiaXEngine(engineId, type)
        }
    }

    @Volatile
    private var state: State = State.NONE

    private var engine: IEngine? = null

    private var runtime: GaiaXRuntime? = null

    internal fun initEngine() {
        if (state == State.NONE || state == State.DESTROY_END) {
            state = State.INIT_START

            // 初始化引擎
            if (engine == null) {
                engine = createEngine()
            }
            engine?.initEngine()

            // 初始化运行时
            if (runtime == null) {
                runtime = GaiaXRuntime.create(this, engine!!, type)
            }
            runtime?.initRuntime()

            state = State.INIT_END
        }
    }

    internal fun startEngine(complete: () -> Unit) {
        if (state == State.INIT_END) {
            state = State.RUNNING_START

            runtime?.startRuntime(complete)

            state = State.RUNNING_END
        }
    }

    internal fun destroyEngine() {
        if (state == State.RUNNING_END || state == State.INIT_START) {

            state = State.DESTROY_START

            runtime?.destroyRuntime()
            runtime = null

            engine?.destroyEngine()
            engine = null

            state = State.DESTROY_END
        }
    }

    private fun createEngine(): IEngine {
        return when (type) {
            GaiaXJS.GaiaXJSType.GaiaXJSEngineTypeQuickJS -> QuickJSEngine.create(this)
            GaiaXJS.GaiaXJSType.GaiaXJSEngineTypeDebugger -> GaiaXJSDebuggerEngine.create(this)
        }
    }

    internal fun runtime(): GaiaXRuntime? {
        return runtime
    }

    internal fun getId(): Long {
        return engineId
    }
}