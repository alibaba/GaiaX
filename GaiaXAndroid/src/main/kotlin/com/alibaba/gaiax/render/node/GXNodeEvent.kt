package com.alibaba.gaiax.render.node

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.template.GXTemplateKey

class GXNodeEvent : GXINodeEvent {

    override fun addDataBindingEvent(gxTemplateContext: GXTemplateContext, gxNode: GXNode, templateData: JSONObject) {
        val eventBinding = gxNode.templateNode.eventBinding ?: return
        val eventData = eventBinding.event.value(templateData) as? JSONObject ?: return
        val eventType = if (eventData.containsKey(GXTemplateKey.GAIAX_GESTURE_TYPE)) {
            eventData.getString(GXTemplateKey.GAIAX_GESTURE_TYPE) ?: GXTemplateKey.GAIAX_GESTURE_TYPE_TAP
        } else {
            GXTemplateKey.GAIAX_GESTURE_TYPE_TAP
        }
        when (eventType) {
            GXTemplateKey.GAIAX_GESTURE_TYPE_TAP -> {
                gxNode.viewRef?.get()?.setOnClickListener {
                    gxTemplateContext.templateData?.eventListener?.onGestureEvent(GXTemplateEngine.GXGesture().apply {
                        this.gestureType = eventType
                        this.view = gxNode.viewRef?.get()
                        this.eventParams = eventData
                        this.nodeId = gxNode.templateNode.layer.id
                        this.templateItem = gxTemplateContext.templateItem
                        this.index = -1
                    })
                }
            }
            GXTemplateKey.GAIAX_GESTURE_TYPE_LONGPRESS -> {
                gxNode.viewRef?.get()?.setOnLongClickListener {
                    gxTemplateContext.templateData?.eventListener?.onGestureEvent(GXTemplateEngine.GXGesture().apply {
                        this.gestureType = eventType
                        this.view = gxNode.viewRef?.get()
                        this.eventParams = eventData
                        this.nodeId = gxNode.templateNode.layer.id
                        this.templateItem = gxTemplateContext.templateItem
                        this.index = -1
                    })
                    true
                }
            }
        }
    }
}
