package com.alibaba.gaiax.js.utils

import java.lang.reflect.Method

internal object SystemProp {

    private var mClassType: Class<*>? = null
    private var mSetMethod: Method? = null
    private var mGetMethod: Method? = null

    private fun init() {
        try {
            if (mClassType == null) {
                mClassType = Class.forName("android.os.SystemProperties")
                mSetMethod = mClassType?.getDeclaredMethod("set", String::class.java, String::class.java)
                mGetMethod = mClassType?.getDeclaredMethod("get", String::class.java, String::class.java)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    operator fun set(key: String, value: String) {
        init()
        try {
            mSetMethod?.invoke(mClassType, key, value)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    operator fun get(key: String, value: String): String? {
        init()
        return try {
            mGetMethod?.invoke(mClassType, key, value) as? String
        } catch (e: Exception) {
            e.printStackTrace()
            value
        }
    }

}