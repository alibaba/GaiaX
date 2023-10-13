package com.alibaba.gaiax.js.impl.qjs

import com.alibaba.gaiax.js.engine.GXHostEngine
import com.alibaba.gaiax.js.engine.IEngine
import com.alibaba.gaiax.quickjs.QuickJS

/**
 * QuickJS引擎
 */
internal class QuickJSEngine private constructor(val engine: GXHostEngine) : IEngine {

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

        fun create(engine: GXHostEngine): QuickJSEngine {
            return QuickJSEngine(engine)
        }
    }
}