package com.youku.gaiax.js.api

interface IGaiaXPromise {

    /**
     * JS中Promise的resolve回调
     */
    fun resolve(): IGaiaXCallback

    /**
     * JS中Promise的reject回调
     */
    fun reject(): IGaiaXCallback
}