package com.alibaba.gaiax.js.utils

import android.os.SystemClock

object Aop {
    fun <T> aopTaskTime(task: () -> T, upload: (time: Long) -> Unit): T {
        val startTime = SystemClock.currentThreadTimeMillis()
        val result = task()
        val endTime = SystemClock.currentThreadTimeMillis()
        val diff = endTime - startTime
        if (Log.isLog()) {
            Log.d("aopTaskTime() called with: startTime = $startTime, endTime = $endTime, diff = $diff")
        }
        upload(diff)
        return result
    }
}