package com.youku.gaiax.js.impl.qjs

import com.youku.gaiax.js.core.GaiaXEngine
import com.youku.gaiax.js.core.api.IEngine
import com.youku.gaiax.quickjs.QuickJS

/**
 * QuickJS引擎
 */
internal class QuickJSEngine private constructor(val engine: GaiaXEngine) : IEngine {

    var quickJS: QuickJS? = null

    override fun initEngine() {
        if (quickJS == null) {
            quickJS = QuickJS.Builder().build()
        }
    }

    fun checkQuickJS() {
        if (quickJS == null) {
            throw IllegalArgumentException("QuickJS Instance Null")
        }
    }

    override fun destroyEngine() {
        destroyQuickJS()
    }

    private fun destroyQuickJS() {
        quickJS = null
    }

    companion object {

        fun create(engine: GaiaXEngine): QuickJSEngine {
            return QuickJSEngine(engine)
        }
    }
}