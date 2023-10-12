package com.alibaba.gaiax.js.impl.debug

import com.alibaba.gaiax.js.engine.GXHostRuntime
import com.alibaba.gaiax.js.engine.IEngine
import com.alibaba.gaiax.js.engine.IRuntime
import com.alibaba.gaiax.studio.GXClientToStudioMultiType

/**
 *  @author: shisan.lms
 *  @date: 2023-03-14
 *  Description:
 */
internal class DebugJSRuntime : IRuntime {

    var debugJSSocket: DebugJSSocket = DebugJSSocket()

    companion object {
        fun create(runtime: GXHostRuntime, engine: IEngine): DebugJSRuntime {
            return DebugJSRuntime()
        }
    }

    override fun initRuntime() {
        debugJSSocket.let { GXClientToStudioMultiType.instance.setJSReceiverListener(it) }
    }

    override fun destroyRuntime() {
    }
}