package com.alibaba.gaiax

import android.support.test.runner.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXComponentEventTest : GXBaseTest() {

    @Test
    fun template_event_tap_listener() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "event", "template_event_tap_listener")

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
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "event", "template_event_tap_listener_display_none")

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
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "event", "template_event_tap_listener_hidden_true")

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
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "event", "template_event_longpress_listener")

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
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "event", "template_event_track_listener")

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
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "event", "template_event_track_listener_display_none")

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
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "event", "template_event_track_listener_hidden_true")

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

}