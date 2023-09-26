package com.alibaba.gaiax.js.core.api

import com.alibaba.fastjson.JSONArray

interface ICallBridgeListener {

    fun callSync(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray): Any?

    fun callAsync(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray)

    fun callPromise(contextId: Long, moduleId: Long, methodId: Long, args: JSONArray)
}