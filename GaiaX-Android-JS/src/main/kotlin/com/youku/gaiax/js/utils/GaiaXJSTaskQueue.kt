package com.youku.gaiax.js.utils

import android.os.Handler
import android.os.HandlerThread
import android.os.Looper
import android.os.Message
import java.util.concurrent.FutureTask

internal class GaiaXJSTaskQueue private constructor(val contextId: Long) {

    companion object {
        const val WHAT_INTERVAL_TASK = 0
        const val WHAT_DELAY_TASK = 1
        fun create(contextId: Long): GaiaXJSTaskQueue {
            return GaiaXJSTaskQueue(contextId)
        }
    }

    private var taskQueue: Handler? = null

    private var taskThread: HandlerThread? = null

    private fun createIntervalMsg(taskId: Int, interval: Int, func: () -> Unit): Message {
        val msg = Message()
        msg.what = taskId
        msg.arg1 = interval
        msg.arg2 = WHAT_INTERVAL_TASK
        msg.obj = func
        return msg
    }

    private fun createDelayMsg(taskId: Int, func: () -> Unit): Message {
        val msg = Message()
        msg.what = taskId
        msg.arg2 = WHAT_DELAY_TASK
        msg.obj = func
        return msg
    }

    fun executeIntervalTask(taskId: Int, interval: Long, func: () -> Unit) {
        taskQueue?.let {
            val msg = createIntervalMsg(taskId, interval.toInt(), func)
            it.sendMessageDelayed(msg, interval)
        }
    }

    fun remoteIntervalTask(taskId: Int) {
        taskQueue?.removeMessages(taskId)
    }

    fun executeDelayTask(taskId: Int, delay: Long, func: () -> Unit) {
        taskQueue?.let {
            val msg = createDelayMsg(taskId, func)
            it.sendMessageDelayed(msg, delay)
        }
    }

    fun executeTask(func: () -> Unit): Boolean {
        return taskQueue?.post { func() } ?: false
    }

    fun remoteDelayTask(taskId: Int) {
        taskQueue?.removeMessages(taskId)
    }

    fun initTaskQueue() {
        val taskQueueName = "GaiaXJSQueue-$contextId"
        taskThread = HandlerThread(taskQueueName)
        taskThread!!.start()
        if (Log.isLog()) {
            Log.d("initTaskQueue() called taskQueueName = $taskQueueName")
        }
        taskQueue = object : Handler(taskThread!!.looper) {
            override fun handleMessage(oldMsg: Message) {
                super.handleMessage(oldMsg)
                when (oldMsg.arg2) {
                    WHAT_INTERVAL_TASK -> {
                        val targetFunc = oldMsg.obj as (() -> Unit)
                        targetFunc.invoke()
                        val newMsg = createIntervalMsg(oldMsg.what, oldMsg.arg1, targetFunc)
                        taskQueue?.sendMessageDelayed(newMsg, oldMsg.arg1.toLong())
                    }
                    WHAT_DELAY_TASK -> {
                        val targetFunc = oldMsg.obj as (() -> Unit)
                        targetFunc.invoke()
                    }
                }
            }
        }
    }

    fun destroyTaskQueue() {
        taskQueue = null
        taskThread?.quit()
        taskThread = null
    }

}

object GaiaXJSUiExecutor {

    val ui: Handler = Handler(Looper.getMainLooper())

    fun isMainThread(): Boolean {
        // 在单元测试环境下 myLooper为null
        return Looper.myLooper() == null || Looper.myLooper() == Looper.getMainLooper()
    }

    fun action(runnable: FutureTask<Unit>) {
        ui.post(runnable)
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