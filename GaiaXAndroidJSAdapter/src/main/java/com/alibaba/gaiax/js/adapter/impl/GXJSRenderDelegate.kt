package com.alibaba.gaiax.js.adapter.impl

import android.app.Activity
import android.util.Log
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.js.IRenderEngineDelegate
import com.alibaba.gaiax.js.adapter.impl.render.GXJSGesture
import com.alibaba.gaiax.js.adapter.impl.render.GXMixNodeEvent
import com.alibaba.gaiax.js.api.IGXCallback
import com.alibaba.gaiax.js.utils.GXJSUiExecutor
import java.util.concurrent.ConcurrentHashMap

/**
 *  @author: shisan.lms
 *  @date: 2023-03-29
 *  Description:
 */
class GXJSRenderDelegate : IRenderEngineDelegate {


    companion object {
        val links: MutableMap<Long, View> = ConcurrentHashMap()
    }

    override fun bindComponentToView(view: View, jsComponentId: Long) {
        links[jsComponentId] = view
    }

    override fun unbindComponentAndView(jsComponentId: Long) {
        links.remove(jsComponentId)
    }


    override fun setDataToRenderEngine(componentId: Long, templateId: String, data: JSONObject, callback: IGXCallback) {
        GXJSUiExecutor.action {
            val cntView = links[componentId]
            GXTemplateEngine.instance.bindData(cntView, GXTemplateEngine.GXTemplateData(data))
            callback.invoke()
        }
    }

    override fun getDataFromRenderEngine(componentId: Long): JSONObject? {
        return GXTemplateEngine.instance.getGXTemplateContext(links[componentId])?.templateData?.data
    }

    override fun getNodeInfo(targetId: String, templateId: String, instanceId: Long): JSONObject {
        var nodeInfo: GXNode? = GXTemplateEngine.instance.getGXNodeById(links[instanceId], targetId)
        if (nodeInfo != null) {
            val targetNode = JSONObject()
            targetNode["targetType"] = nodeInfo.templateNode.layer.type
            targetNode["targetSubType"] = nodeInfo.templateNode.layer.subType
            targetNode["targetId"] = targetId
            return targetNode
        } else {
            return JSONObject()
        }
    }

    override fun addEventListener(targetId: String, componentId: Long, eventType: String, optionCover: Boolean, optionLevel: Int) {
        GXTemplateEngine.instance.getGXTemplateContext(links[componentId])?.let { gxTemplateContext ->
            val gxNode = GXTemplateEngine.instance.getGXNodeById(links[componentId], targetId)
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

    override fun dispatcherEvent(eventParams: JSONObject) {
        GXJSEngine.Component.onEvent(
            eventParams["jsComponentId"] as Long,
            eventParams["type"] as String,
            eventParams["data"] as JSONObject
        )
    }

    override fun getView(componentId: Long): View? {
        return links[componentId]
    }

    override fun getActivityForDialog(): Activity? {
        links.forEach {
            val topContext = it.value.context as Activity
            if (!topContext.isFinishing) {
                return topContext
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