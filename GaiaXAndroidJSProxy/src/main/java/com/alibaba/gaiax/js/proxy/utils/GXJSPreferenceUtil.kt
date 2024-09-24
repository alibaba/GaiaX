package com.alibaba.gaiax.js.proxy.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.alibaba.gaiax.js.GXJSEngine

/**
 *  @author: shisan.lms
 *  @date: 2023-03-24
 *  Description:
 */
class GXJSPreferenceUtil private constructor(context: Context?, sharePreFileName: String) {
    private var mPreferences: SharedPreferences? = null

    init {
        synchronized(GXJSPreferenceUtil::class.java) {
            if (context != null && !TextUtils.isEmpty(sharePreFileName)) {
                mPreferences = context.getSharedPreferences(sharePreFileName, Context.MODE_PRIVATE)
            }
        }
    }

    fun getString(key: String, defValue: String): String? {
        return if (mPreferences != null) {
            mPreferences!!.getString(key, defValue)
        } else ""
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        val result = mPreferences?.getBoolean(key, defValue) ?: defValue
        return result
    }

    fun getFloat(key: String, defValue: Float): Float {
        val result = mPreferences?.getFloat(key, defValue) ?: defValue
        return result
    }


    fun getInt(key: String, defValue: Int): Int {
        val result = mPreferences?.getInt(key, defValue) ?: defValue
        return result
    }

    fun getLong(key: String, defValue: Long): Long {
        val result = mPreferences?.getLong(key, defValue) ?: defValue
        return result
    }

    fun putBoolean(key: String, value: Boolean): Boolean {
        val result = mPreferences?.edit()?.putBoolean(key, value)?.commit() ?: false
        return result
    }

    fun putString(key: String, value: String): Boolean {
        val result = mPreferences?.edit()?.putString(key, value)?.commit() ?: false
        return result
    }

    fun putFloat(key: String, value: Float): Boolean {
        val result = mPreferences?.edit()?.putFloat(key, value)?.commit() ?: false
        return result
    }

    fun putLong(key: String, value: Long): Boolean {
        val result = mPreferences?.edit()?.putLong(key, value)?.commit() ?: false
        return result
    }

    fun putInt(key: String, value: Int): Boolean {
        val result = mPreferences?.edit()?.putInt(key, value)?.commit() ?: false
        return result
    }

    @get:Deprecated("")
    val all: Map<*, *>
        get() = if (mPreferences != null) {
            mPreferences!!.getAll()
        } else emptyMap<Any, Any>()

    operator fun contains(key: String): Boolean {
        val result = mPreferences?.contains(key) ?: false
        return result
    }

    fun delete(key: String): Boolean {
        try {
            val result = mPreferences?.edit()?.remove(key)?.commit() ?: false
            return result
        } catch (ignore: Exception) {
            return false
        }
    }


    fun clear(): Boolean {
        return try {
            val result = mPreferences?.edit()?.clear()?.commit() ?: false
            result
        } catch (ignore: Exception) {
            false
        }
    }

    companion object {

        fun createSharePreference(fileName: String): GXJSPreferenceUtil {
            // TODO: 希望从GaiaXJSManager传入
            return GXJSPreferenceUtil(GXJSEngine.instance.context, fileName)
        }
    }
}
