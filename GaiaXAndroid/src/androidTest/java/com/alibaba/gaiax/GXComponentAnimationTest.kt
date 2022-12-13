package com.alibaba.gaiax

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.UiThreadTestRule
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch


@RunWith(AndroidJUnit4::class)
class GXComponentAnimationTest : GXBaseTest() {

    private val uiThreadTest = UiThreadTestRule()

    @Test
    // @UiThreadTest
    fun template_animation_prop_youku() {
        val countDownLatch = CountDownLatch(1)

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "animation",
            "template_animation_prop_youku"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        val child = rootView.child(0)
        val gxTemplateData = GXTemplateEngine.GXTemplateData(JSONObject())
        gxTemplateData.eventListener = object : GXTemplateEngine.GXIEventListener {
            override fun onAnimationEvent(gxAnimation: GXTemplateEngine.GXAnimation) {
                if (gxAnimation.state == GXTemplateEngine.GXAnimation.STATE_END) {
                    countDownLatch.countDown()
                }
            }
        }

        uiThreadTest.runOnUiThread {
            GXTemplateEngine.instance.bindData(rootView, gxTemplateData)
        }

        countDownLatch.await()

        Assert.assertEquals(100F.dpToPx(), child.x)
        Assert.assertEquals(150F.dpToPx(), child.y)
    }

    @Test
    // @UiThreadTest
    fun template_animation_prop() {
        val countDownLatch = CountDownLatch(1)

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "animation",
            "template_animation_prop"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        val child = rootView.child(0)
        val gxTemplateData = GXTemplateEngine.GXTemplateData(JSONObject())
        gxTemplateData.eventListener = object : GXTemplateEngine.GXIEventListener {
            override fun onAnimationEvent(gxAnimation: GXTemplateEngine.GXAnimation) {
                if (gxAnimation.state == GXTemplateEngine.GXAnimation.STATE_END) {
                    countDownLatch.countDown()
                }
            }
        }

        uiThreadTest.runOnUiThread {
            GXTemplateEngine.instance.bindData(rootView, gxTemplateData)
        }

        countDownLatch.await()

        Assert.assertEquals(100F.dpToPx(), child.x)
        Assert.assertEquals(150F.dpToPx(), child.y)
    }
}