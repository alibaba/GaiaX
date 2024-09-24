package com.alibaba.gaiax.utils

import com.alibaba.gaiax.GXRegisterCenter

object GXExceptionHelper {

    private const val TAG = "GXExceptionHelper"

    fun isException(): Boolean {
        return GXRegisterCenter.instance.extensionException != null
    }

    fun exception(msg: java.lang.Exception) {
        GXRegisterCenter.instance.extensionException?.exception(msg)
        Log.runE(TAG) { "exception ${msg.message}" }
    }
}