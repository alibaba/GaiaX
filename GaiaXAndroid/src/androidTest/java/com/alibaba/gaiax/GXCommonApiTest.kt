package com.alibaba.gaiax

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXCommonApiTest {
    var context: Context = InstrumentationRegistry.getInstrumentation().context

    @Before
    fun before() {
        GXTemplateEngine.instance.init(GXMockUtils.context)
    }

    @Test
    fun template_position_relative_left() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "api",
            "template_bind_data_measure_size"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(1080F, null)
        )

        Assert.assertEquals(1080F, rootView.width())
        Assert.assertEquals(200F.dpToPx(), rootView.height())

        val gxTemplateData = GXTemplateEngine.GXTemplateData(JSONObject())
        GXTemplateEngine.instance.bindData(
            rootView,
            gxTemplateData,
            GXTemplateEngine.GXMeasureSize(1080F * 2, null)
        )

        Assert.assertEquals(1080F * 2, rootView.width())
        Assert.assertEquals(200F.dpToPx(), rootView.height())
    }


}