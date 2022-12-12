package com.alibaba.gaiax

import android.util.Log
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXComponentAnimationTest : GXBaseTest() {

    @Test
    @UiThreadTest
    fun template_animation_prop_youku() {
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
                super.onAnimationEvent(gxAnimation)

                if (gxAnimation.state == GXTemplateEngine.GXAnimation.STATE_END) {
                    Log.d(
                        "GXComponentAnimationTest",
                        "X = "
                    )
                    if (100F.dpToPx() != child.x) {
                        throw IllegalArgumentException("x position error should ${100F.dpToPx()} but actual ${child.x}")
                    }

                    if (150F.dpToPx() != child.y) {
                        throw IllegalArgumentException("y position error should ${150F.dpToPx()} but actual ${child.y}")
                    }
                }
            }
        }
        GXTemplateEngine.instance.bindData(rootView, gxTemplateData)
    }
}