package com.alibaba.gaiax.js.core

import com.alibaba.gaiax.js.GXJSEngineFactory
import com.alibaba.gaiax.js.GaiaXJSManager
import com.alibaba.gaiax.js.core.api.IEngine
import com.alibaba.gaiax.js.impl.qjs.GaiaXJSDebuggerEngine
import com.alibaba.gaiax.js.impl.qjs.QuickJSEngine

/**
 * GaiaXJSManager.createEngin->initEngine->
 */
internal class GaiaXEngine private constructor(
    val engineId: Long,
    val type: GXJSEngineFactory.GaiaXJSEngineType,
    val isDebugging: Boolean
) {

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

        fun create(engineId: Long, type: GXJSEngineFactory.GaiaXJSEngineType, isDebugging: Boolean = false): GaiaXEngine {
            return GaiaXEngine(engineId, type, isDebugging)
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
            GXJSEngineFactory.GaiaXJSEngineType.GaiaXJSEngineTypeQuickJS -> QuickJSEngine.create(this)
            GXJSEngineFactory.GaiaXJSEngineType.GaiaXJSEngineTypeDebugger -> GaiaXJSDebuggerEngine.create(this)
        }
    }

    internal fun runtime(): GaiaXRuntime? {
        return runtime
    }

    internal fun getId(): Long {
        return engineId
    }
}