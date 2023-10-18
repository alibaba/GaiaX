package com.alibaba.gaiax.js.adapter

import android.app.Activity
import android.util.Log
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.api.IGXCallback
import com.alibaba.gaiax.js.utils.GXJSUiExecutor
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXTemplateKey
import java.lang.ref.WeakReference
import java.util.concurrent.ConcurrentHashMap

class GXJSRenderDelegate : GXJSEngine.IRenderDelegate {

    companion object {
        val instance by lazy {
            return@lazy GXJSRenderDelegate()
        }
        val links: MutableMap<Long, WeakReference<View>> = ConcurrentHashMap()
    }

    fun onRegisterComponent(view: View, componentId: Long) {
        links[componentId] = WeakReference(view)
    }

    fun onUnregisterComponent(componentId: Long) {
        links.remove(componentId)
    }

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
        GXTemplateEngine.instance.getGXTemplateContext(links[componentId]?.get())
            ?.let { gxTemplateContext ->
                val gxNode =
                    GXTemplateEngine.instance.getGXNodeById(links[componentId]?.get(), targetId)
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

    fun dispatcherEvent(eventParams: JSONObject) {
        GXJSEngine.Component.onEvent(
            eventParams["jsComponentId"] as Long,
            eventParams["type"] as String,
            eventParams["data"] as JSONObject
        )
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

    fun dispatcherEvent(gestureParams: GXJSGesture) {
        Log.d("lms-13", "dispatcherEvent: GXTemplateEngine.GXGesture $gestureParams")
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
                    dispatcherEvent(eventParams)
                }
            }
        }
    }

}