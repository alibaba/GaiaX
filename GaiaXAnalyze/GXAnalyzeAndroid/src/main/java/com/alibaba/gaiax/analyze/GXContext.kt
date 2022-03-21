package com.alibaba.gaiax.analyze

class GXContext {
    companion object {
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
        var pointer: Long = 0

        fun wrapAsGXValue(value: Long): GXValue? {
            try {
                check(value != 0L) { "Can't wrap null pointer as GXValue" }
                var gxValue: GXValue? = null
                val type: Int = GXAnalyze.getValueTag(value)
                when (type) {
                    TYPE_NULL -> gxValue = GXNull(value)
                    TYPE_STRING -> gxValue =
                        GXString(value, GXAnalyze.getValueString(value))
                    TYPE_ARRAY -> gxValue =
                        GXArray(value,GXAnalyze.getValueArray(value))
                    TYPE_MAP -> gxValue =
                        GXMap(value,GXAnalyze.getValueMap(value))
                    TYPE_BOOLEAN -> gxValue = GXBool(value, GXAnalyze.getValueBoolean(value))
                    TYPE_FLOAT -> gxValue = GXFloat(value, GXAnalyze.getValueFloat(value))
                }
                return gxValue
            } catch (e: Exception) {
//            JNIBridgeModuleHelper.wrapAsGXValueException(e)
            }
            return null
        }
    }
}