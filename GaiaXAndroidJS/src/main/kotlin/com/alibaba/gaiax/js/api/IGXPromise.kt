package com.alibaba.gaiax.js.api

interface IGXPromise {

    /**
     * JS中Promise的resolve回调
     */
    fun resolve(): IGXCallback

    /**
     * JS中Promise的reject回调
     */
    fun reject(): IGXCallback
}