package com.alibaba.gaiax.demo.gaiaxjs

import android.app.Activity
import android.util.Log
import android.view.View
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.demo.utils.UiExecutor
import com.alibaba.gaiax.render.node.GXINodeEvent
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXTemplateKey
import com.youku.gaiax.js.GXJSComponentDelegate
import com.youku.gaiax.js.IRenderEngineDelegate
import com.youku.gaiax.js.api.IGaiaXCallback
import java.util.concurrent.ConcurrentHashMap

/**
 *  @author: shisan.lms
 *  @date: 2023-03-06
 *  Description:
 */
class JSRenderDelegate : IRenderEngineDelegate {

    companion object {
        val links: MutableMap<Long, View> = ConcurrentHashMap()
    }

    private var rootView: View? = null

    fun initDelegate(view: View, jsId: Long) {
        this.rootView = view

    }

    override fun bindComponentWithView(view: View, jsComponentId: Long) {
        links[jsComponentId] = view
    }


    override fun setDataToGX(componentId: Long, templateId: String, data: JSONObject, callback: IGaiaXCallback) {
        UiExecutor.action {
            val cntView = if (this.rootView == null) {
                links[componentId]
            } else {
                this.rootView
            }
            GXTemplateEngine.instance.bindData(cntView, GXTemplateEngine.GXTemplateData(data))
            callback.invoke()
        }
    }

    override fun getDataFromGX(componentId: Long): JSONObject? {
        return GXTemplateEngine.instance.getGXTemplateContext(links[componentId])?.templateData?.data
    }

    override fun getNodeInfo(targetId: String, templateId: String, instanceId: Long): JSONObject {
        var nodeInfo: GXNode? = null
        if (rootView != null) {
            nodeInfo = GXTemplateEngine.instance.getGXNodeById(this.rootView, targetId)
        } else {
            nodeInfo = GXTemplateEngine.instance.getGXNodeById(links[instanceId], targetId)
        }
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
            GXRegisterCenter.instance.registerExtensionNodeEvent(GXExtensionNodeEvent())
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
        GXJSComponentDelegate.instance.onEventComponent(
            eventParams["jsComponentId"] as Long,
            eventParams["type"] as String,
            eventParams["data"] as JSONObject
        )
    }

    override fun getView(componentId: Long): View? {
        return rootView
    }

    override fun getActivityForDialog(): Activity? {
        links.forEach {
            val topContext = it.value.context as Activity
            if (!topContext.isFinishing && !topContext.isDestroyed) {
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

class GXExtensionNodeEvent : GXRegisterCenter.GXIExtensionNodeEvent {

    override fun create(): GXINodeEvent {
        return GXMixNodeEvent()
    }
}

class GXJSGesture : GXTemplateEngine.GXGesture() {

    var jsOptionLevel: Int = 0

    var jsOptionCover: Boolean = false

    var jsComponentId: Long = -1L
}


class GXMixNodeEvent : GXINodeEvent {

    private var gxTemplateContext: GXTemplateContext? = null

    private var onClickListener: View.OnClickListener? = null
    private var onLongClickListener: View.OnLongClickListener? = null

    private var clickEventByDataBinding: GXTemplateEngine.GXGesture? = null
    private var clickEventByJS: GXJSGesture? = null

    private var longClickEventByDataBinding: GXTemplateEngine.GXGesture? = null
    private var longClickEventByJS: GXJSGesture? = null

    fun addJSEvent(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        componentId: Long,
        eventType: String,
        optionCover: Boolean,
        optionLevel: Int
    ) {

        this.gxTemplateContext = gxTemplateContext
        val gxGesture = GXJSGesture().apply {
            this.gestureType = eventType
            this.view = gxNode.view
            this.eventParams = null
            this.nodeId = gxNode.templateNode.layer.id
            this.templateItem = gxTemplateContext.templateItem
            this.index = -1
            this.jsComponentId = componentId
            this.jsOptionCover = optionCover
            this.jsOptionLevel = optionLevel
        }

        if (eventType == GXTemplateKey.GAIAX_GESTURE_TYPE_TAP) {
            clickEventByJS = gxGesture
        } else if (eventType == GXTemplateKey.GAIAX_GESTURE_TYPE_LONGPRESS) {
            longClickEventByJS = gxGesture
        }
        initViewEventListener(gxGesture)
    }

    override fun addDataBindingEvent(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        templateData: JSONObject
    ) {
        this.gxTemplateContext = gxTemplateContext
        val eventBinding = gxNode.templateNode.eventBinding ?: return
        val eventData = eventBinding.event.value(templateData) as? JSONObject ?: return
        val eventType = if (eventData.containsKey(GXTemplateKey.GAIAX_GESTURE_TYPE)) {
            eventData.getString(GXTemplateKey.GAIAX_GESTURE_TYPE)
                ?: GXTemplateKey.GAIAX_GESTURE_TYPE_TAP
        } else {
            GXTemplateKey.GAIAX_GESTURE_TYPE_TAP
        }
        val gxGesture = GXTemplateEngine.GXGesture().apply {
            this.gestureType = eventType
            this.view = gxNode.view
            this.eventParams = eventData
            this.nodeId = gxNode.templateNode.layer.id
            this.templateItem = gxTemplateContext.templateItem
            this.index = -1
        }
        if (eventType == GXTemplateKey.GAIAX_GESTURE_TYPE_TAP) {
            clickEventByDataBinding = gxGesture
        } else if (eventType == GXTemplateKey.GAIAX_GESTURE_TYPE_LONGPRESS) {
            longClickEventByDataBinding = gxGesture
        }
        initViewEventListener(gxGesture)
    }

    private fun initViewEventListener(gestureParams: GXTemplateEngine.GXGesture) {
        when (gestureParams.gestureType) {
            GXTemplateKey.GAIAX_GESTURE_TYPE_TAP -> {
                initViewClickEventDispatcher(gestureParams)
            }
            GXTemplateKey.GAIAX_GESTURE_TYPE_LONGPRESS -> {
                initViewLongClickEventDispatcher(gestureParams)
            }
        }
    }

    private fun initViewClickEventDispatcher(gestureParams: GXTemplateEngine.GXGesture) {
        if (onClickListener == null) {
            onClickListener = View.OnClickListener {
                dispatcherClick()
            }
        }
        gestureParams.view?.setOnClickListener(onClickListener)
    }

    private fun initViewLongClickEventDispatcher(gestureParams: GXTemplateEngine.GXGesture) {
        if (onLongClickListener == null) {
            onLongClickListener = View.OnLongClickListener {
                dispatcherLongClick()
                true
            }
        }
        gestureParams.view?.setOnLongClickListener(onLongClickListener)
    }

    private fun dispatcherClick() {
        val jsEventParams = clickEventByJS
        val dbEventParams = clickEventByDataBinding
        if (jsEventParams != null) {
            if (jsEventParams.jsOptionCover) {
                jsEventParams.let {
                    gxTemplateContext?.templateData?.eventListener?.onGestureEvent(it)
                }
            } else {
                if (jsEventParams.jsOptionLevel == 0) {
                    dbEventParams?.let {
                        gxTemplateContext?.templateData?.eventListener?.onGestureEvent(it)
                    }

                    jsEventParams.let {
//                        (GaiaXJSManager.instance.renderEngineDelegate as JSRenderDelegate).dispatcherEvent(it)
                        JSRenderDelegate().dispatcherEvent(it)
                    }
                } else {
                    jsEventParams.let {
                        gxTemplateContext?.templateData?.eventListener?.onGestureEvent(it)
                    }
                    dbEventParams?.let {
                        gxTemplateContext?.templateData?.eventListener?.onGestureEvent(it)
                    }
                }
            }
        } else {
            dbEventParams?.let {
                gxTemplateContext?.templateData?.eventListener?.onGestureEvent(it)
            }
        }
    }

    private fun dispatcherLongClick() {
        val jsEventParams = longClickEventByJS
        val dbEventParams = longClickEventByDataBinding
        if (jsEventParams != null) {
            if (jsEventParams.jsOptionCover) {
                jsEventParams.let {
                    gxTemplateContext?.templateData?.eventListener?.onGestureEvent(it)
                }
            } else {
                if (jsEventParams.jsOptionLevel == 0) {
                    dbEventParams?.let {
                        gxTemplateContext?.templateData?.eventListener?.onGestureEvent(it)
                    }
                    jsEventParams.let {
                        gxTemplateContext?.templateData?.eventListener?.onGestureEvent(it)
                    }
                } else {
                    jsEventParams.let {
                        gxTemplateContext?.templateData?.eventListener?.onGestureEvent(it)
                    }
                    dbEventParams?.let {
                        gxTemplateContext?.templateData?.eventListener?.onGestureEvent(it)
                    }
                }
            }
        } else {
            dbEventParams?.let {
                gxTemplateContext?.templateData?.eventListener?.onGestureEvent(it)
            }
        }
    }
}