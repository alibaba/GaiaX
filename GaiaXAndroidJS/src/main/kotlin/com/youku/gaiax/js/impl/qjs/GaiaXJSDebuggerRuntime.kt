package com.youku.gaiax.js.impl.qjs

import com.alibaba.gaiax.studio.GXClientToStudioMultiType
import com.youku.gaiax.js.core.GaiaXRuntime
import com.youku.gaiax.js.core.api.IEngine
import com.youku.gaiax.js.core.api.IRuntime
import com.youku.gaiax.js.impl.qjs.module.GaiaXJSSocketWrapper

/**
 *  @author: shisan.lms
 *  @date: 2023-03-14
 *  Description:
 */
internal class GaiaXJSDebuggerRuntime(val runtime: GaiaXRuntime, val engine: GaiaXJSDebuggerEngine):IRuntime {

    var gxJSSocketClientWrapper:GaiaXJSSocketWrapper = GaiaXJSSocketWrapper()

    companion object {
        fun create(runtime: GaiaXRuntime, engine: IEngine): GaiaXJSDebuggerRuntime {
            return GaiaXJSDebuggerRuntime(runtime, (engine as GaiaXJSDebuggerEngine))
        }
    }

    override fun initRuntime() {
            gxJSSocketClientWrapper.let { GXClientToStudioMultiType.instance.setJSReceiverListener(it) }
    }

    override fun destroyRuntime() {
        TODO("Not yet implemented")
    }
}