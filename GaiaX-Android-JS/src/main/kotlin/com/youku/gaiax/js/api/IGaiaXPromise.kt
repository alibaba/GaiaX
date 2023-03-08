package com.youku.gaiax.js.api

interface IGaiaXPromise {

    fun resolve(): IGaiaXCallback

    fun reject(): IGaiaXCallback
}