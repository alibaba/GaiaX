package com.alibaba.gaiax.js.api

interface IGaiaXCallback {

    /**
     * JS中Async的回调
     * @param result：返回至JS引擎中的返回值
     */
    fun invoke(result: Any? = null)
}