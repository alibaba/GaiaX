package com.alibaba.gaiax.js.impl.debug

import com.alibaba.gaiax.js.engine.GXHostEngine
import com.alibaba.gaiax.js.engine.IEngine

/**
 *  @author: shisan.lms
 *  @date: 2023-03-14
 *  Description:
 */
internal class DebugJSEngine(val engine: GXHostEngine): IEngine {

    companion object {
        fun create(engine: GXHostEngine): DebugJSEngine {
            return DebugJSEngine(engine)
        }
    }

    override fun initEngine() {

    }

    override fun destroyEngine() {
        TODO("Not yet implemented")
    }
}