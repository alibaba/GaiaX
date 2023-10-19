package com.alibaba.gaiax.js.adapter

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

    val links: MutableMap<Long, WeakReference<View>> = ConcurrentHashMap()

    fun setData(
        componentId: Long, templateId: String, data: JSONObject, callback: IGXCallback
    ) {
        GXJSUiExecutor.action {
            val cntView = links[componentId]?.get()
            GXTemplateEngine.instance.bindData(cntView, GXTemplateEngine.GXTemplateData(data))
            callback.invoke()
        }
    }

    fun getData(componentId: Long): JSONObject? {
        return GXTemplateEngine.instance.getGXTemplateContext(links[componentId]?.get())?.templateData?.data
    }

    fun getNodeInfo(targetId: String, templateId: String, instanceId: Long): JSONObject {
        val nodeInfo: GXNode? =
            GXTemplateEngine.instance.getGXNodeById(links[instanceId]?.get(), targetId)
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

    fun addEventListener(
        targetId: String,
        componentId: Long,
        eventType: String,
        optionCover: Boolean,
        optionLevel: Int
    ) {
        links[componentId]?.get()?.let { gxView ->
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

    fun getView(componentId: Long): View? {
        return links[componentId]?.get()
    }

    fun getActivity(): Activity? {
        links.forEach {
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

    fun removeGestureEventListener(targetId: String, componentId: Long, eventType: String) {
        links[componentId]?.get()?.let { gxView ->
            val gxNode = GXTemplateEngine.instance.getGXNodeById(gxView, targetId)
            gxNode?.initEventByRegisterCenter()
            (gxNode?.event as? GXMixNodeEvent)?.removeJSEvent(componentId, eventType)
        }
    }

    val eventsData = CopyOnWriteArraySet<JSONObject>()

    fun registerNativeMessage(data: JSONObject): Boolean {
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

    fun unRegisterNativeMessage(data: JSONObject): Boolean {
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
}