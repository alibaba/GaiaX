package com.alibaba.gaiax.js.utils

object Log {

    fun isLog(): Boolean {
//        return "1" == SystemProp["debug.gaiax.js.log", "0"]
        return true
    }

    fun d(msg: String) {
        e(msg)
    }

    fun e(msg: String) {
        val maxLogSize = 1000
        for (i in 0..msg.length / maxLogSize) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = if (end > msg.length) msg.length else end
            android.util.Log.e("[GaiaX][JS]", msg.substring(start, end))
        }
    }
}