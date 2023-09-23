package com.youku.gaiax.js.core.api

import com.alibaba.fastjson.JSONObject

internal interface IContext {

    fun initContext()

    fun evaluateJS(script: String, argsMap:JSONObject = JSONObject())

    fun destroyContext()

    /**
     * 初始化JS运行所需的模块 Timer, Bridge
     */
    fun initModule(module: String)

    fun initBootstrap()

    fun startBootstrap()

    fun initPendingJob()

    fun destroyPendingJob()
}