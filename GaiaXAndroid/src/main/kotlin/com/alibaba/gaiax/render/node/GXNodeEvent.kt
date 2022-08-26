package com.alibaba.gaiax.render.node

import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.template.GXTemplateKey

class GXNodeEvent : GXINodeEvent {

    override fun addDataBindingEvent(
        gxTemplateContext: GXTemplateContext,
        gxNode: GXNode,
        templateData: JSONObject
    ) {
        val eventBinding = gxNode.templateNode.eventBinding ?: return
        val eventData = eventBinding.event.value(templateData) as? JSONObject ?: return
        val eventType = if (eventData.containsKey(GXTemplateKey.GAIAX_GESTURE_TYPE)) {
            eventData.getString(GXTemplateKey.GAIAX_GESTURE_TYPE)
                ?: GXTemplateKey.GAIAX_GESTURE_TYPE_TAP
        } else {
            GXTemplateKey.GAIAX_GESTURE_TYPE_TAP
        }
        when (eventType) {
            GXTemplateKey.GAIAX_GESTURE_TYPE_TAP -> {
                gxNode.view?.setOnClickListener {

                    // 发送点击事件
                    gxTemplateContext.templateData?.eventListener?.onGestureEvent(
                        GXTemplateEngine.GXGesture().apply {
                            this.gestureType = eventType
                            this.view = gxNode.view
                            this.eventParams = eventData
                            this.nodeId = gxNode.templateNode.layer.id
                            this.templateItem = gxTemplateContext.templateItem
                            this.index = -1
                        })

                    // 发送点击埋点事件
                    (gxNode.templateNode.trackBinding?.track?.value(templateData) as? JSONObject)?.let { trackData ->
                        gxTemplateContext.templateData?.trackListener?.onManualClickTrackEvent(
                            GXTemplateEngine.GXTrack().apply {
                                this.view = gxNode.view
                                this.trackParams = trackData
                                this.nodeId = gxNode.templateNode.layer.id
                                this.templateItem = gxTemplateContext.templateItem
                                this.index = -1
                            })
                    }
                }
            }
            GXTemplateKey.GAIAX_GESTURE_TYPE_LONGPRESS -> {
                gxNode.view?.setOnLongClickListener {
                    // 发送点击事件
                    gxTemplateContext.templateData?.eventListener?.onGestureEvent(
                        GXTemplateEngine.GXGesture().apply {
                            this.gestureType = eventType
                            this.view = gxNode.view
                            this.eventParams = eventData
                            this.nodeId = gxNode.templateNode.layer.id
                            this.templateItem = gxTemplateContext.templateItem
                            this.index = -1
                        })

                    // 发送点击埋点事件
                    (gxNode.templateNode.trackBinding?.track?.value(templateData) as? JSONObject)?.let { trackData ->
                        gxTemplateContext.templateData?.trackListener?.onManualClickTrackEvent(
                            GXTemplateEngine.GXTrack().apply {
                                this.view = gxNode.view
                                this.trackParams = trackData
                                this.nodeId = gxNode.templateNode.layer.id
                                this.templateItem = gxTemplateContext.templateItem
                                this.index = -1
                            })
                    }
                    true
                }
            }
        }
    }
}
