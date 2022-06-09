package com.alibaba.gaiax

import android.view.View
import android.support.test.runner.AndroidJUnit4
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.node.GXINodeEvent
import com.alibaba.gaiax.render.node.GXNode
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXComponentEventTest : GXBaseTest() {

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

    class GXExtensionNodeEvent : GXRegisterCenter.GXIExtensionNodeEvent {

        override fun create(): GXINodeEvent {
            return GXMixNodeEvent()
        }
    }

    @Test
    fun template_event_register_node_event_js_longpress() {

        GXRegisterCenter.instance
            .registerExtensionNodeEvent(GXExtensionNodeEvent())

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "event",
            "template_event_register_node_event_js_longpress"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        var gesture: GXJSGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture as GXJSGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val gxTemplateContext = GXTemplateEngine.instance.getGXTemplateContext(rootView)
        val targetNode = GXTemplateEngine.instance.getGXNodeById(rootView, "target")
        targetNode?.event = GXRegisterCenter.instance.extensionNodeEvent?.create()
        (targetNode?.event as? GXMixNodeEvent)?.addJSEvent(
            gxTemplateContext!!,
            targetNode,
            999L,
            "longpress",
            false,
            999
        )

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        targetView?.performLongClick()

        Assert.assertEquals(true, targetView != null)
        Assert.assertEquals(true, gesture != null)
        Assert.assertEquals("longpress", gesture?.gestureType)
        Assert.assertEquals(999L, gesture?.jsComponentId)
        Assert.assertEquals(false, gesture?.jsOptionCover)
        Assert.assertEquals(999, gesture?.jsOptionLevel)
        Assert.assertEquals(true, gesture?.view == targetView)
        Assert.assertEquals(-1, gesture?.index)
        Assert.assertEquals("target", gesture?.nodeId)
        Assert.assertEquals(
            "template_event_register_node_event_js_longpress",
            gesture?.templateItem?.templateId
        )
        Assert.assertEquals(null, gesture?.eventParams?.toJSONString())
    }

    @Test
    fun template_event_register_node_event_js_tap() {

        GXRegisterCenter.instance
            .registerExtensionNodeEvent(GXExtensionNodeEvent())

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "event",
            "template_event_register_node_event_js_tap"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        var gesture: GXJSGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture as GXJSGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val gxTemplateContext = GXTemplateEngine.instance.getGXTemplateContext(rootView)
        val targetNode = GXTemplateEngine.instance.getGXNodeById(rootView, "target")
        targetNode?.event = GXRegisterCenter.instance.extensionNodeEvent?.create()
        (targetNode?.event as? GXMixNodeEvent)?.addJSEvent(
            gxTemplateContext!!,
            targetNode,
            999L,
            "tap",
            false,
            999
        )

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        targetView?.performClick()

        Assert.assertEquals(true, targetView != null)
        Assert.assertEquals(true, gesture != null)
        Assert.assertEquals("tap", gesture?.gestureType)
        Assert.assertEquals(999L, gesture?.jsComponentId)
        Assert.assertEquals(false, gesture?.jsOptionCover)
        Assert.assertEquals(999, gesture?.jsOptionLevel)
        Assert.assertEquals(true, gesture?.view == targetView)
        Assert.assertEquals(-1, gesture?.index)
        Assert.assertEquals("target", gesture?.nodeId)
        Assert.assertEquals(
            "template_event_register_node_event_js_tap",
            gesture?.templateItem?.templateId
        )
        Assert.assertEquals(null, gesture?.eventParams?.toJSONString())
    }

    @Test
    fun template_event_register_node_event_longpress() {

        GXRegisterCenter.instance
            .registerExtensionNodeEvent(GXExtensionNodeEvent())

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "event",
            "template_event_register_node_event_longpress"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        var gesture: GXTemplateEngine.GXGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        targetView?.performLongClick()

        Assert.assertEquals(true, targetView != null)
        Assert.assertEquals(true, gesture != null)
        Assert.assertEquals("longpress", gesture?.gestureType)
        Assert.assertEquals(true, gesture?.view == targetView)
        Assert.assertEquals(-1, gesture?.index)
        Assert.assertEquals("target", gesture?.nodeId)
        Assert.assertEquals(
            "template_event_register_node_event_longpress",
            gesture?.templateItem?.templateId
        )
        Assert.assertEquals(JSONObject().apply {
            this["type"] = "longpress"
            this["value"] = null
        }.toJSONString(), gesture?.eventParams?.toJSONString())
    }

    @Test
    fun template_event_register_node_event_tap() {

        GXRegisterCenter.instance
            .registerExtensionNodeEvent(GXExtensionNodeEvent())

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "event",
            "template_event_register_node_event_tap"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        var gesture: GXTemplateEngine.GXGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        targetView?.performClick()

        Assert.assertEquals(true, targetView != null)
        Assert.assertEquals(true, gesture != null)
        Assert.assertEquals("tap", gesture?.gestureType)
        Assert.assertEquals(true, gesture?.view == targetView)
        Assert.assertEquals(-1, gesture?.index)
        Assert.assertEquals("target", gesture?.nodeId)
        Assert.assertEquals(
            "template_event_register_node_event_tap",
            gesture?.templateItem?.templateId
        )
        Assert.assertEquals(JSONObject().apply {
            this["type"] = "tap"
            this["value"] = null
        }.toJSONString(), gesture?.eventParams?.toJSONString())
    }

    @Test
    fun template_event_tap_listener() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "event",
            "template_event_tap_listener"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var gesture: GXTemplateEngine.GXGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        targetView?.performClick()

        Assert.assertEquals(true, targetView != null)
        Assert.assertEquals(true, gesture != null)
        Assert.assertEquals("tap", gesture?.gestureType)
        Assert.assertEquals(true, gesture?.view == targetView)
        Assert.assertEquals(-1, gesture?.index)
        Assert.assertEquals("target", gesture?.nodeId)
        Assert.assertEquals("template_event_tap_listener", gesture?.templateItem?.templateId)
        Assert.assertEquals(JSONObject().apply {
            this["type"] = "tap"
            this["value"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        }.toJSONString(), gesture?.eventParams?.toJSONString())
    }

    @Test
    fun template_event_tap_listener_display_none() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "event",
            "template_event_tap_listener_display_none"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var gesture: GXTemplateEngine.GXGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        targetView?.performClick()

        Assert.assertEquals(null, gesture)
    }

    @Test
    fun template_event_tap_listener_hidden_true() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "event",
            "template_event_tap_listener_hidden_true"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var gesture: GXTemplateEngine.GXGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        targetView?.performClick()

        Assert.assertEquals(null, gesture)
    }

    @Test
    fun template_event_longpress_listener() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "event",
            "template_event_longpress_listener"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var gesture: GXTemplateEngine.GXGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        targetView?.performLongClick()

        Assert.assertEquals(true, targetView != null)
        Assert.assertEquals(true, gesture != null)
        Assert.assertEquals("longpress", gesture?.gestureType)
        Assert.assertEquals(true, gesture?.view == targetView)
        Assert.assertEquals(-1, gesture?.index)
        Assert.assertEquals("target", gesture?.nodeId)
        Assert.assertEquals(JSONObject().apply {
            this["type"] = "longpress"
            this["value"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        }.toJSONString(), gesture?.eventParams?.toJSONString())
    }

    @Test
    fun template_event_track_listener() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "event",
            "template_event_track_listener"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var track: GXTemplateEngine.GXTrack? = null

        templateData.trackListener = object : GXTemplateEngine.GXITrackListener {
            override fun onTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                track = gxTrack
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "target")

        Assert.assertEquals(true, track != null)
        Assert.assertEquals(true, track?.view == targetView)
        Assert.assertEquals(-1, track?.index)
        Assert.assertEquals("target", track?.nodeId)
        Assert.assertEquals(JSONObject().apply {
            this["type"] = "tap"
            this["value"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        }.toJSONString(), track?.trackParams?.toJSONString())
    }

    @Test
    fun template_event_track_listener_display_none() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "event",
            "template_event_track_listener_display_none"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var track: GXTemplateEngine.GXTrack? = null

        templateData.trackListener = object : GXTemplateEngine.GXITrackListener {
            override fun onTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                track = gxTrack
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(null, track)
    }

    @Test
    fun template_event_track_listener_hidden_true() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "event",
            "template_event_track_listener_hidden_true"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["action"] = JSONObject().apply {
                this["value"] = "GaiaX"
            }
        })

        var track: GXTemplateEngine.GXTrack? = null

        templateData.trackListener = object : GXTemplateEngine.GXITrackListener {
            override fun onTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                track = gxTrack
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(null, track)
    }

    @Test
    fun template_event_scroll() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "event",
            "template_event_scroll"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
                this.add(JSONObject())
            }
        })

        var gesture: GXTemplateEngine.GXGesture? = null

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gesture = gxGesture
            }
        }

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)
        rootView.executeRecyclerView()

        rootView.child(0).child(0).performClick()

        Assert.assertEquals(true, gesture != null)
        Assert.assertEquals("tap", gesture?.gestureType)
        Assert.assertEquals(-1, gesture?.index)
        Assert.assertEquals("template_event_scroll_item", gesture?.nodeId)
        Assert.assertEquals(JSONObject().apply {
            this["type"] = "tap"
            this["value"] = "GaiaX"
        }.toJSONString(), gesture?.eventParams?.toJSONString())
    }

}