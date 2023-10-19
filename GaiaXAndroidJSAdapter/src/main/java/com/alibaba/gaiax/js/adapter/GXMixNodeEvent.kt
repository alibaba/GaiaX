package com.alibaba.gaiax.js.adapter

import android.view.View
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXINodeEvent
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * 带有JS事件与Native事件的混合节点事件
 */
internal class GXMixNodeEvent : GXINodeEvent {

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

    fun removeJSEvent(componentId: Long, eventType: String) {
        if (eventType == GXTemplateKey.GAIAX_GESTURE_TYPE_TAP) {
            clickEventByJS = null
        } else if (eventType == GXTemplateKey.GAIAX_GESTURE_TYPE_LONGPRESS) {
            longClickEventByJS = null
        }
    }

    override fun addDataBindingEvent(
        gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject
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
                        GXJSRenderProxy.instance.dispatchGestureEvent(it)
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

internal class GXExtensionNodeEvent : GXRegisterCenter.GXIExtensionNodeEvent {

    override fun create(): GXINodeEvent {
        return GXMixNodeEvent()
    }
}

internal class GXJSGesture : GXTemplateEngine.GXGesture() {

    var jsOptionLevel: Int = 0

    var jsOptionCover: Boolean = false

    var jsComponentId: Long = -1L
}