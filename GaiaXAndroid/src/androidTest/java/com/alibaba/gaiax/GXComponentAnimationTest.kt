package com.alibaba.gaiax

import android.view.LayoutInflater
import android.widget.AbsoluteLayout
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.UiThreadTestRule
import com.airbnb.lottie.LottieAnimationView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.GXViewKey
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.animation.GXLottieAnimation
import com.alibaba.gaiax.test.R
import com.alibaba.gaiax.utils.GXMockUtils
import com.alibaba.gaiax.utils.GXTestLottieAnimation
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.CountDownLatch

@RunWith(AndroidJUnit4::class)
class GXComponentAnimationTest : GXBaseTest() {

    private val uiThreadTest = UiThreadTestRule()

    override fun before() {
        super.before()
        GXRegisterCenter.instance
            .registerExtensionLottieAnimation(object :
                GXRegisterCenter.GXIExtensionLottieAnimation {
                override fun create(): GXLottieAnimation {
                    return GXTestLottieAnimation()
                }
            })
            .registerExtensionViewSupport(
                GXViewKey.VIEW_TYPE_LOTTIE
            ) {
                val lottieView: LottieAnimationView = LayoutInflater.from(context)
                    .inflate(R.layout.gaiax_inner_lottie_auto_play, null) as LottieAnimationView
                lottieView.layoutParams = AbsoluteLayout.LayoutParams(
                    AbsoluteLayout.LayoutParams.MATCH_PARENT,
                    AbsoluteLayout.LayoutParams.MATCH_PARENT,
                    0,
                    0
                )
                lottieView
            }
    }

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

    @Test
    fun template_animation_lottie_remote() {
        val countDownLatch = CountDownLatch(1)

        val templateItem =
            GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "animation",
                "template_animation_lottie_remote"
            )
        val gxTemplateData = GXTemplateEngine.GXTemplateData(JSONObject())
        gxTemplateData.eventListener = object : GXTemplateEngine.GXIEventListener {
            override fun onAnimationEvent(gxAnimation: GXTemplateEngine.GXAnimation) {
                if (gxAnimation.state == GXTemplateEngine.GXAnimation.STATE_END) {
                    countDownLatch.countDown()
                }
            }
        }
        val gxMeasureSize = GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, gxMeasureSize)

        uiThreadTest.runOnUiThread {
            GXTemplateEngine.instance.bindData(rootView, gxTemplateData)
        }

        countDownLatch.await()

        Assert.assertEquals(true, rootView.child(1) is LottieAnimationView)
    }

    @Test
    fun template_animation_lottie_local() {
        val countDownLatch = CountDownLatch(1)

        val templateItem =
            GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "animation",
                "template_animation_lottie_local"
            )
        val gxTemplateData = GXTemplateEngine.GXTemplateData(JSONObject())
        gxTemplateData.eventListener = object : GXTemplateEngine.GXIEventListener {
            override fun onAnimationEvent(gxAnimation: GXTemplateEngine.GXAnimation) {
                if (gxAnimation.state == GXTemplateEngine.GXAnimation.STATE_END) {
                    countDownLatch.countDown()
                }
            }
        }
        val gxMeasureSize = GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, gxMeasureSize)

        uiThreadTest.runOnUiThread {
            GXTemplateEngine.instance.bindData(rootView, gxTemplateData)
        }

        countDownLatch.await()

        Assert.assertEquals(true, rootView.child(1) is LottieAnimationView)
    }

    @Test
    fun template_animation_lottie_remote_trigger_state_true() {
        val countDownLatch = CountDownLatch(1)

        val templateItem =
            GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "animation",
                "template_animation_lottie_remote_trigger_state"
            )
        val gxTemplateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["data"] = JSONObject().apply {
                this["state"] = true
            }
        })
        gxTemplateData.eventListener = object : GXTemplateEngine.GXIEventListener {
            override fun onAnimationEvent(gxAnimation: GXTemplateEngine.GXAnimation) {
                if (gxAnimation.state == GXTemplateEngine.GXAnimation.STATE_END) {
                    countDownLatch.countDown()
                }
            }
        }
        val gxMeasureSize = GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, gxMeasureSize)

        uiThreadTest.runOnUiThread {
            GXTemplateEngine.instance.bindData(rootView, gxTemplateData)
        }

        countDownLatch.await()

        Assert.assertEquals(false, (rootView.child(1) as LottieAnimationView).composition == null)
    }

    @Test
    fun template_animation_lottie_remote_trigger_state_false() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "animation",
                "template_animation_lottie_remote_trigger_state"
            )
        val gxTemplateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["data"] = JSONObject().apply {
                this["state"] = false
            }
        })

        val gxMeasureSize = GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, gxMeasureSize)

        GXTemplateEngine.instance.bindData(rootView, gxTemplateData)

        Assert.assertEquals(true, (rootView.child(1) as LottieAnimationView).composition == null)
    }

    @Test
    fun template_animation_lottie_old_data_structure() {

        GXRegisterCenter.instance.registerExtensionCompatibility(
            GXRegisterCenter.GXExtensionCompatibilityConfig().apply {
                isCompatibilityLottieOldDataStructure = true
            })

        val countDownLatch = CountDownLatch(1)

        val templateItem =
            GXTemplateEngine.GXTemplateItem(
                GXMockUtils.context,
                "animation",
                "template_animation_lottie_old_data_structure"
            )
        val gxTemplateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["zipFilePath"] =
                "https://ykimg.alicdn.com/product/xfile/2021-12-10/0a027f4c1be329002a7b4cf28e3c7906.zip"
        })
        gxTemplateData.eventListener = object : GXTemplateEngine.GXIEventListener {
            override fun onAnimationEvent(gxAnimation: GXTemplateEngine.GXAnimation) {
                if (gxAnimation.state == GXTemplateEngine.GXAnimation.STATE_START) {
                    countDownLatch.countDown()
                }
            }
        }
        val gxMeasureSize = GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, gxMeasureSize)

        uiThreadTest.runOnUiThread {
            GXTemplateEngine.instance.bindData(rootView, gxTemplateData)
        }

        countDownLatch.await()

        Assert.assertEquals(true, rootView.child(1) is LottieAnimationView)
    }
}