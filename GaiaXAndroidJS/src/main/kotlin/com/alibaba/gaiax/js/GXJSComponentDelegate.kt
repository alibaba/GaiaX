package com.alibaba.gaiax.js

import android.view.View
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.js.core.api.IComponent
import com.alibaba.gaiax.js.support.GaiaXNativeEventManager
import com.alibaba.gaiax.js.utils.TimeUtils

/**
 *  @author: shisan.lms
 *  @date: 2023-03-23
 *  Description:
 */
class GXJSComponentDelegate {

    companion object {
        val instance by lazy {
            return@lazy GXJSComponentDelegate()
        }
    }


    fun onEventComponent(id: Long, type: String, data: JSONObject) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.let {
            it.getComponentByInstanceId(id)?.onEvent(type, data)
        }
    }

    fun onNativeEventComponent(id: Long, data: JSONObject) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.let {
            it.getComponentByInstanceId(id)?.onNativeEvent(data)
        }
    }

    fun onReadyComponent(id: Long) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.let {
            it.getComponentByInstanceId(id)?.onReady()
        }
    }

    fun onReuseComponent(id: Long) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.let {
            it.getComponentByInstanceId(id)?.onReuse()
        }
    }

    fun onShowComponent(id: Long) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.let {
            it.getComponentByInstanceId(id)?.onShow()
        }
    }

    fun onHiddenComponent(id: Long) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.let {
            it.getComponentByInstanceId(id)?.onHide()
        }
    }

    fun onDestroyComponent(id: Long) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.let {
            it.getComponentByInstanceId(id)?.onDestroy()
        }
    }

    fun onLoadMoreComponent(id: Long, data: JSONObject) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.let {
            it.getComponentByInstanceId(id)?.onLoadMore(data)
        }
    }

    fun registerComponent(
        bizId: String,
        templateId: String,
        templateVersion: String,
        script: String,
        view: View
    ): Long {
        val componentId = GXJSEngineFactory.instance.getGaiaXJSContext()?.registerComponent(bizId, templateId, templateVersion, script) ?: -1L
        GXJSEngineFactory.instance.renderEngineDelegate?.bindComponentToView(view, componentId)
        return componentId
    }

    fun unregisterComponent(id: Long) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.unregisterComponent(id)
        GXJSEngineFactory.instance.renderEngineDelegate.unbindComponentAndView(id)
    }

    fun getComponentByInstanceId(instanceId: Long): IComponent? {
        return GXJSEngineFactory.instance.getGaiaXJSContext()?.getComponentByInstanceId(instanceId)
    }

    fun dispatcherNativeMessageEventToJS(data: JSONObject) {
        GaiaXNativeEventManager.instance.eventsData.forEach { componentData ->
            val componentId = componentData.getLongValue("instanceId")
            if (GXJSEngineFactory.instance.getGaiaXJSContext()?.getComponentByInstanceId(componentId) != null) {
                val result = JSONObject().apply {
                    this.putAll(data)
                    this.putAll(componentData)
                    this["timestamp"] = TimeUtils.elapsedRealtime()
                }
                GXJSEngineFactory.instance.getGaiaXJSContext()?.let {
                    it.getComponentByInstanceId(componentId)?.onNativeEvent(result)
                }
            }
        }
    }
}