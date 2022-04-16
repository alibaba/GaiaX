package com.alibaba.gaiax.render.node

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.template.GXTemplateKey

class GXProcessEvent : GXRegisterCenter.GXIProcessEvent {

    override fun strategy(context: GXTemplateContext, node: GXNode, templateData: JSONObject) {
        val templateNode = node.templateNode
        val eventBinding = templateNode.eventBinding ?: return
        val eventData = eventBinding.event.value(templateData) as? JSONObject ?: return
        val eventType = if (eventData.containsKey(GXTemplateKey.GAIAX_GESTURE_TYPE)) {
            eventData.getString(GXTemplateKey.GAIAX_GESTURE_TYPE) ?: GXTemplateKey.GAIAX_GESTURE_TYPE_TAP
        } else {
            GXTemplateKey.GAIAX_GESTURE_TYPE_TAP
        }
        when (eventType) {
            GXTemplateKey.GAIAX_GESTURE_TYPE_TAP -> {
                node.viewRef?.get()?.setOnClickListener {
                    context.templateData?.eventListener?.onGestureEvent(GXTemplateEngine.GXGesture().apply {
                        this.gestureType = eventType
                        this.view = node.viewRef?.get()
                        this.eventParams = eventData
                        this.nodeId = templateNode.layer.id
                        this.templateItem = context.templateItem
                        this.index = -1
                    })
                }
            }
            GXTemplateKey.GAIAX_GESTURE_TYPE_LONGPRESS -> {
                node.viewRef?.get()?.setOnLongClickListener {
                    context.templateData?.eventListener?.onGestureEvent(GXTemplateEngine.GXGesture().apply {
                        this.gestureType = eventType
                        this.view = node.viewRef?.get()
                        this.eventParams = eventData
                        this.nodeId = templateNode.layer.id
                        this.templateItem = context.templateItem
                        this.index = -1
                    })
                    true
                }
            }
        }
    }


}
