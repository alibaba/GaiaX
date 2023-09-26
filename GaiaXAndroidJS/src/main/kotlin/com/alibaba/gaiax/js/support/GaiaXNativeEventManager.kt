package com.alibaba.gaiax.js.support

import com.alibaba.fastjson.JSONObject
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet

class GaiaXNativeEventManager {

    val eventsData = CopyOnWriteArraySet<JSONObject>()

    fun registerMessage(data: JSONObject): Boolean {
        return if (data.containsKey("type") && data.containsKey("contextId") && data.containsKey("instanceId")) {
            var alreadyRegisterMessage = false
            for (item in eventsData) {
                if (data == item) {
                    alreadyRegisterMessage = true
                    break
                }
            }
            if (!alreadyRegisterMessage) {
                eventsData.add(data)
            }
            true
        } else {
            false
        }
    }

    fun unRegisterMessage(data: JSONObject): Boolean {
        return if (data.containsKey("type") && data.containsKey("contextId") && data.containsKey("instanceId")) {
            for (item in eventsData) {
                if (data == item) {
                    eventsData.remove(item)
                    break
                }
            }
            true
        } else {
            false
        }
    }

    companion object {

        val instance by lazy {
            return@lazy GaiaXNativeEventManager()
        }
    }
}