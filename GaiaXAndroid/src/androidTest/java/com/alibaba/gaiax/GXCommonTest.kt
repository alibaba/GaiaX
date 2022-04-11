package com.alibaba.gaiax

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.support.test.runner.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXCommonTest : GXBaseTest() {


    @Test
    fun template_design_token_color() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "common", "template_design_token_color")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(true, rootView.background is GradientDrawable)
        Assert.assertEquals(Color.RED, (rootView.background as GradientDrawable).colors?.get(0))
    }

    @Test
    fun template_design_token_dimen() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "common", "template_design_token_dimen")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F, rootView.height())
    }

}