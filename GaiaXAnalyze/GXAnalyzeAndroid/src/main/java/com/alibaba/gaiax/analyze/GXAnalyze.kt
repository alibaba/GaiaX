@file:Suppress("FunctionName", "SpellCheckingInspection")

package com.alibaba.gaiax.analyze

class GXAnalyze {

    // 计算逻辑的扩展
    interface IComputeExtend {

        // Computed value expression
        fun computeValueExpression(valuePath: String, source: Any?): Long

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
        external fun getValueArray(value: Long): Any?
        external fun getValueMap(value: Long): Any?
        external fun createValueFloat64(value: Float): Long
        external fun createValueString(value: String): Long
        external fun createValueBool(value: Boolean): Long
        external fun createValueArray(value: Any?): Long
        external fun createValueMap(value: Any?): Long


        val TYPE_FLOAT = 0
        val TYPE_BOOLEAN = 1
        val TYPE_NULL = 2
        val TYPE_VALUE = 3
        val TYPE_STRING = 4
        val TYPE_OBJECT = 5
        val TYPE_ARRAY = 6
        val TYPE_MAP = 7
        val TYPE_INT = 8
        val TYPE_EXCEPTION = 9

        fun wrapAsGXValue(value: Long): GXValue? {
            try {
                check(value != 0L) { "Can't wrap null pointer as GXValue" }
                var gxValue: GXValue? = null
                val type: Int = getValueTag(value)
                when (type) {
                    TYPE_NULL -> gxValue = GXNull(value)
                    TYPE_STRING -> gxValue = GXString(value, getValueString(value))
                    TYPE_ARRAY -> gxValue = GXArray(value, getValueArray(value))
                    TYPE_MAP -> gxValue = GXMap(value, getValueMap(value))
                    TYPE_BOOLEAN -> gxValue = GXBool(value, getValueBoolean(value))
                    TYPE_FLOAT -> gxValue = GXFloat(value, getValueFloat(value))
                }
                return gxValue
            } catch (e: Exception) {
            }
            return null
        }
    }

    fun getResult(expression: String, data: Any?): Any? {
        val result = this.getResultNative(this, expression, data);
        return GXAnalyze.wrapAsGXValue(result)?.getValue();
    }

    private external fun getResultNative(self: Any, expression: String, data: Any?): Long

    private external fun initNative(self: Any)

}