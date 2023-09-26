package com.alibaba.gaiax.js.utils

import android.os.SystemClock

object TimeUtils {

    var DEBUG = false

    fun elapsedRealtime(): Long {
        if (DEBUG) {
            return 0
        }
        return SystemClock.elapsedRealtime()
    }
}