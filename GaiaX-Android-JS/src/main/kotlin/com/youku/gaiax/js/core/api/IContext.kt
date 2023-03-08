package com.youku.gaiax.js.core.api

internal interface IContext {

    fun initContext()

    fun evaluateJS(script: String)

    fun destroyContext()

    fun initModule(module: String)

    fun initBootstrap()

    fun startBootstrap()

    fun initPendingJob()

    fun destroyPendingJob()
}