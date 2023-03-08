package com.youku.gaiax.js.impl.qjs.module

import androidx.annotation.Keep
import com.youku.gaiax.js.GaiaXJS
import com.youku.gaiax.js.utils.IdGenerator
import com.youku.gaiax.js.utils.Log
import com.youku.gaiax.quickjs.*


@Keep
internal object QuickJSTimer {

    @Keep
    class SetTimeout : JSFunctionCallback {

        override fun invoke(context: JSContext?, fromArgs: Array<out JSValue>?): JSValue {
            val jsContext = context!!
            val args = fromArgs!!
            val func = args[0].cast(JSFunction::class.java)
            val delay = args[1].cast(JSInt::class.java).long
            val contextIdProperty = jsContext.globalObject.getProperty("__CONTEXT_ID__")

            if (contextIdProperty is JSInt) {
                val contextId = contextIdProperty.cast(JSInt::class.java).long
                val taskId = IdGenerator.genIntId()
                if (Log.isLog()) {
                    Log.e("createTimeoutFunc() called with: contextId = $contextId, taskId = $taskId")
                }
                GaiaXJS.instance.executeDelayTask(taskId, delay) {
                    func.invoke(jsContext.createJSUndefined(), arrayOf())
                }
                return jsContext.createJSNumber(taskId)
            }
            return jsContext.createJSUndefined()
        }
    }

    @Keep
    class ClearTimeout : JSFunctionCallback {
        override fun invoke(context: JSContext?, fromArgs: Array<out JSValue>?): JSValue {
            val jsContext = context!!
            val args = fromArgs!!
            val taskId = args[0].cast(JSInt::class.java).int
            val contextIdProperty = jsContext.globalObject.getProperty("__CONTEXT_ID__")
            if (contextIdProperty is JSInt) {
                val contextId = contextIdProperty.cast(JSInt::class.java).long
                if (Log.isLog()) {
                    Log.e("createClearTimeoutFunc() called with: contextId = $contextId, taskId = $taskId")
                }
                GaiaXJS.instance.remoteDelayTask(taskId)
            }
            return jsContext.createJSUndefined()
        }
    }

    @Keep
    class ClearInterval : JSFunctionCallback {
        override fun invoke(context: JSContext?, fromArgs: Array<out JSValue>?): JSValue {
            val jsContext = context!!
            val args = fromArgs!!
            val taskId = args[0].cast(JSInt::class.java).int
            val contextIdProperty = jsContext.globalObject.getProperty("__CONTEXT_ID__")
            if (contextIdProperty is JSInt) {
                val contextId = contextIdProperty.cast(JSInt::class.java).long
                if (Log.isLog()) {
                    Log.e("createClearTimeoutFunc() called with: contextId = $contextId, taskId = $taskId")
                }
                GaiaXJS.instance.remoteIntervalTask(taskId)
            }
            return jsContext.createJSUndefined()
        }
    }

    @Keep
    class SetInterval : JSFunctionCallback {
        override fun invoke(context: JSContext?, fromArgs: Array<out JSValue>?): JSValue {
            val jsContext = context!!
            val args = fromArgs!!

            val func = args[0].cast(JSFunction::class.java)
            val interval = args[1].cast(JSInt::class.java).long
            val contextIdProperty = jsContext.globalObject.getProperty("__CONTEXT_ID__")
            if (contextIdProperty is JSInt) {
                val contextId = contextIdProperty.cast(JSInt::class.java).long
                val taskId = IdGenerator.genIntId()
                if (Log.isLog()) {
                    Log.e("createSetIntervalFunc() called with: contextId = $contextId, taskId = $taskId ")
                }
                GaiaXJS.instance.executeIntervalTask(taskId, interval) {
                    func.invoke(jsContext.createJSUndefined(), arrayOf())
                }
                return jsContext.createJSNumber(taskId)
            }

            return jsContext.createJSUndefined()
        }
    }

    @JvmStatic
    fun createClearIntervalFunc(): JSFunctionCallback = ClearInterval()

    @JvmStatic
    fun createSetIntervalFunc(): JSFunctionCallback = SetInterval()

    @JvmStatic
    fun createClearTimeoutFunc(): JSFunctionCallback = ClearTimeout()

    @JvmStatic
    fun createSetTimeoutFunc(): JSFunctionCallback = SetTimeout()

}