package com.alibaba.gaiax.js.utils

import java.lang.reflect.Method


/**
 * @suppress
 */
object Log {
    const val FLAG_OPEN = 0x1    // 二进制的0001
    const val FLAG_TLOG = 0x2    // 二进制的0010
    const val FLAG_THREE = 0x4  // 二进制的0100
    const val FLAG_FOUR = 0x8   // 二进制的1000

    const val VERBOSE = 2
    const val DEBUG = 3
    const val INFO = 4
    const val WARN = 5
    const val ERROR = 6

    private const val LOG_MAX_LENGTH = 4000

    const val TAG = "GaiaX.JS"

    fun d(tag: String, msg: String) {
        log(DEBUG, tag, msg)
    }

    fun e(tag: String, msg: String) {
        log(ERROR, tag, msg)
    }

    fun log(type: Int, tag: String, msg: String) {
        val maxLogSize = LOG_MAX_LENGTH
        for (i in 0..msg.length / maxLogSize) {
            val start = i * maxLogSize
            var end = (i + 1) * maxLogSize
            end = if (end > msg.length) msg.length else end
            when (type) {
                DEBUG -> android.util.Log.d(tag, msg.substring(start, end))
                ERROR -> android.util.Log.e(tag, msg.substring(start, end))
                INFO -> android.util.Log.i(tag, msg.substring(start, end))
                VERBOSE -> android.util.Log.v(tag, msg.substring(start, end))
                WARN -> android.util.Log.w(tag, msg.substring(start, end))
            }
        }
    }

    inline fun runIf(type: Int, flags: Int, tag: String, block: () -> String) {
        if (flags and FLAG_OPEN != 0) {
            log(type, if (tag.isEmpty()) TAG else "${TAG}.${tag}", block())
        }
    }
}

fun SystemProp.defaultLog(): Boolean {
    return this["debug.com.alibaba.gaiax.js.log", "0"] == "1" || this["debug.com.alibaba.gaiax.all.log", "0"] == "1"
}

fun SystemProp.defaultLogFlag(): Int {
    return if (defaultLog()) Log.FLAG_OPEN else 0
}

inline fun Log.runE(tag: String = "", flags: Int = 0, block: () -> String) {
    runIf(ERROR, flags or SystemProp.defaultLogFlag(), tag, block)
}

inline fun Log.runD(tag: String = "", flags: Int = 0, block: () -> String) {
    runIf(DEBUG, flags or SystemProp.defaultLogFlag(), tag, block)
}

inline fun Log.runV(tag: String = "", flags: Int = 0, block: () -> String) {
    runIf(VERBOSE, flags or SystemProp.defaultLogFlag(), tag, block)
}

inline fun Log.runI(tag: String = "", flags: Int = 0, block: () -> String) {
    runIf(INFO, flags or SystemProp.defaultLogFlag(), tag, block)
}

inline fun Log.runW(tag: String = "", flags: Int = 0, block: () -> String) {
    runIf(WARN, flags or SystemProp.defaultLogFlag(), tag, block)
}

object SystemProp {

    @Volatile
    private var classType: Class<*>? = null

    @Volatile
    private var getMethod: Method? = null

    private val cache = mutableMapOf<String, String>()

    private fun init() {
        try {
            // 第一次判断是为了避免加锁损耗
            if (classType == null) {
                // 处理多线程安全问题，使用cache对象作为锁对象
                synchronized(cache) {
                    // 第二次判断是为了避免多线程重入
                    if (classType == null) {
                        classType = Class.forName("android.os.SystemProperties")
                        getMethod = classType?.getDeclaredMethod("get", String::class.java, String::class.java)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 线程安全的获取系统属性
     * 性能优化：
     *  使用HasMap作为缓存，在少量数据情况下，性能接近O(1)，避免重复读取
     */
    operator fun get(key: String, defaultValue: String): String {
        init()

        // 读取是多线程安全的
        // Check cache first
        cache[key]?.let { return it }

        // Retrieve from system properties if not cached
        val value = try {
            (getMethod?.invoke(classType, key, defaultValue) as? String)?.also {
                // Cache the value
                // 写入时可能会有多线程问题，所以需要同步
                synchronized(cache) {
                    cache[key] = it
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            defaultValue
        }
        return value ?: defaultValue
    }
}