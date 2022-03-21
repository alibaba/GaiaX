@file:Suppress("FunctionName", "SpellCheckingInspection")

package com.alibaba.gaiax.analyze

import android.os.Handler
import android.os.Looper

class GXAnalyze {

    // 计算逻辑的扩展
    interface IComputeExtend {

        // Computed value expression
        fun computeValueExpression(valuePath: String, source: Any): Long

        // Computed function expression
        fun computeFunctionExpression(functionName: String, params: LongArray): Long
    }

    val pointer: Long = 0L

    var computeExtend: IComputeExtend? = null

    init {
        initNative(this)
    }

    fun initComputeExtend(computeExtend: IComputeExtend) {
        this.computeExtend = computeExtend
    }

    companion object {
        init {
            System.loadLibrary("GXAnalyzeAndroid")
        }

        external fun getValueTag(value: Long): Int
        external fun getValueString(value: Long): String
        external fun getValueBoolean(value: Long): Boolean
        external fun getValueFloat(value: Long): Float
        external fun getValueArray(value: Long): Any
        external fun getValueMap(value: Long): Any
        external fun createValueFloat64(value: Float): Long
        external fun createValueString(value: String): Long
        external fun createValueBool(value: Boolean): Long
        external fun createValueArray(value: Any): Long
        external fun createValueMap(value: Any): Long
    }

    fun getResult(expression: String, data: Any): Any? {
        val result = this.getResultNative(this, expression, data);

        return GXContext.wrapAsGXValue(result)?.getValue();
    }

    private external fun getResultNative(self: Any, expression: String, data: Any): Long

    private external fun initNative(self: Any)

}