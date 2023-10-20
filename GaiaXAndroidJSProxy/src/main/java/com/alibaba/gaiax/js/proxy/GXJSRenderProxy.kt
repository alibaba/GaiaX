package com.alibaba.gaiax.js.proxy

import android.app.Activity
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.js.api.IGXCallback
import com.alibaba.gaiax.js.utils.GXJSUiExecutor
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXTemplateKey
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.CopyOnWriteArraySet

internal class GXJSRenderProxy {

    companion object {
        val instance by lazy {
            return@lazy GXJSRenderProxy()
        }
    }

    /**
     * JS组件ID与视图之间的映射关系
     */
    val jsComponentMap: MutableMap<Long, WeakReference<View>> = ConcurrentHashMap()

    /**
     * native事件数据
     */
    val nativeEvents = CopyOnWriteArraySet<JSONObject>()

    fun setData(
        componentId: Long, templateId: String, data: JSONObject, callback: IGXCallback
    ) {
        GXJSUiExecutor.action {
            val cntView = jsComponentMap[componentId]?.get()
            GXTemplateEngine.instance.bindData(cntView, GXTemplateEngine.GXTemplateData(data))
            callback.invoke()
        }
    }

    fun getData(componentId: Long): JSONObject? {
        return GXTemplateEngine.instance.getGXTemplateContext(jsComponentMap[componentId]?.get())?.templateData?.data
    }

    fun getNodeInfo(targetId: String, templateId: String, instanceId: Long): JSONObject {
        val nodeInfo: GXNode? =
            GXTemplateEngine.instance.getGXNodeById(jsComponentMap[instanceId]?.get(), targetId)
        return if (nodeInfo != null) {
            val targetNode = JSONObject()
            targetNode["targetType"] = nodeInfo.templateNode.layer.type
            targetNode["targetSubType"] = nodeInfo.templateNode.layer.subType
            targetNode["targetId"] = targetId
            targetNode
        } else {
            JSONObject()
        }
    }

    fun addGestureEventListener(
        targetId: String,
        componentId: Long,
        eventType: String,
        optionCover: Boolean,
        optionLevel: Int
    ) {
        jsComponentMap[componentId]?.get()?.let { gxView ->
            GXTemplateEngine.instance.getGXTemplateContext(gxView)?.let { gxTemplateContext ->
                val gxNode = GXTemplateEngine.instance.getGXNodeById(gxView, targetId)
                gxNode?.initEventByRegisterCenter()
                var eventTypeForName = ""
                if (eventType == "click") {
                    eventTypeForName = GXTemplateKey.GAIAX_GESTURE_TYPE_TAP
                }
                (gxNode?.event as? GXMixNodeEvent)?.addJSEvent(
                    gxTemplateContext,
                    gxNode,
                    componentId,
                    eventTypeForName,
                    optionCover,
                    optionLevel
                )
            }
        }
    }

    fun removeGestureEventListener(targetId: String, componentId: Long, eventType: String) {
        jsComponentMap[componentId]?.get()?.let { gxView ->
            val gxNode = GXTemplateEngine.instance.getGXNodeById(gxView, targetId)
            gxNode?.initEventByRegisterCenter()
            (gxNode?.event as? GXMixNodeEvent)?.removeJSEvent(componentId, eventType)
        }
    }

    fun getActivity(): Activity? {
        jsComponentMap.forEach {
            val activity = it.value.get()?.context as Activity
            if (!activity.isFinishing) {
                return activity
            }
        }
        return null
    }

    fun dispatchGestureEvent(gestureParams: GXJSGesture) {
        if (gestureParams.jsComponentId != -1L) {

            gestureParams.nodeId.let { targetId ->
                val data = targetId?.let { getNodeInfo(it, "", gestureParams.jsComponentId) }
                val type = gestureParams.gestureType
                data?.set("timeStamp", System.currentTimeMillis())
                if (data != null) {
                    val eventParams = JSONObject()
                    eventParams["jsComponentId"] = gestureParams.jsComponentId
                    eventParams["type"] = when (gestureParams.gestureType) {
                        GXTemplateKey.GAIAX_GESTURE_TYPE_TAP -> "click"
                        GXTemplateKey.GAIAX_GESTURE_TYPE_LONGPRESS -> "longpress"
                        else -> "click"
                    }
                    eventParams["data"] = data
                    dispatchGestureEvent(eventParams)
                }
            }
        }
    }

    private fun dispatchGestureEvent(eventParams: JSONObject) {
        GXJSEngineProxy.instance.onEvent(
            eventParams["jsComponentId"] as Long,
            eventParams["type"] as String,
            eventParams["data"] as JSONObject
        )
    }

    fun registerNativeMessage(data: JSONObject): Boolean {
        return if (data.containsKey("type") && data.containsKey("contextId") && data.containsKey("instanceId")) {
            var alreadyRegisterMessage = false
            for (item in nativeEvents) {
                if (data == item) {
                    alreadyRegisterMessage = true
                    break
                }
            }
            if (!alreadyRegisterMessage) {
                nativeEvents.add(data)
            }
            true
        } else {
            false
        }
    }

    fun unregisterNativeMessage(data: JSONObject): Boolean {
        return if (data.containsKey("type") && data.containsKey("contextId") && data.containsKey("instanceId")) {
            for (item in nativeEvents) {
                if (data == item) {
                    nativeEvents.remove(item)
                    break
                }
            }
            true
        } else {
            false
        }
    }
}