package com.youku.gaiax.js

import android.view.View
import com.alibaba.fastjson.JSONObject
import com.youku.gaiax.js.core.api.IComponent

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
        GXJSEngineFactory.instance.renderEngineDelegate?.bindComponentWithView(view, componentId)
        return componentId
    }

    fun unregisterComponent(id: Long) {
        GXJSEngineFactory.instance.getGaiaXJSContext()?.unregisterComponent(id)
    }

    fun getComponentByInstanceId(instanceId: Long): IComponent? {
        return GXJSEngineFactory.instance.getGaiaXJSContext()?.getComponentByInstanceId(instanceId)
    }
}