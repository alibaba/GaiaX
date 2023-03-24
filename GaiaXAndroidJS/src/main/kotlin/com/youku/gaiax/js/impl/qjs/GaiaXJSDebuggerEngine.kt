package com.youku.gaiax.js.impl.qjs

import com.youku.gaiax.js.core.GaiaXEngine
import com.youku.gaiax.js.core.api.IEngine

/**
 *  @author: shisan.lms
 *  @date: 2023-03-14
 *  Description:
 */
internal class GaiaXJSDebuggerEngine(val engine: GaiaXEngine):IEngine {

    companion object {
        fun create(engine: GaiaXEngine): GaiaXJSDebuggerEngine {
            return GaiaXJSDebuggerEngine(engine)
        }
    }

    override fun initEngine() {

    }

    override fun destroyEngine() {
        TODO("Not yet implemented")
    }
}