package com.alibaba.gaiax.js.impl.debug

import com.alibaba.fastjson.JSONObject

interface ISocketCallBridgeListener {

    fun callSync(socketId: Int, params: JSONObject)

    fun callAsync(socketId: Int, params: JSONObject)

    fun callPromise(socketId: Int, params: JSONObject)

    fun callGetLibrary(socketId: Int, methodName: String)
}