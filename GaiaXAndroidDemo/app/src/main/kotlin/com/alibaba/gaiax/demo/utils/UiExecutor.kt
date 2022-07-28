package com.alibaba.gaiax.demo.utils

import android.os.Handler
import android.os.Looper

object UiExecutor {

    val ui: Handler = Handler(Looper.getMainLooper())

    fun isMainThread(): Boolean {
        // 在单元测试环境下 myLooper为null
        return Looper.myLooper() == null || Looper.myLooper() == Looper.getMainLooper()
    }

    fun action(runnable: Runnable) {
        ui.post(runnable)
    }

    fun action(function: () -> Unit) {
        ui.post { function.invoke() }
    }

    fun removeAction(runnable: Runnable) {
        ui.removeCallbacks(runnable)
    }
}