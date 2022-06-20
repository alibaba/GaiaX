package com.alibaba.gaiax

import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal


@RunWith(AndroidJUnit4::class)
class GXCommonExpressionTest : GXBaseTest() {

    @Test
    fun template_double() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "expression",
            "template_double"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["title"] = 1.1
            })
        )

        Assert.assertEquals("1.1", (rootView.child(0) as TextView).text)
    }

    @Test
    fun template_bigdecimal() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "expression",
            "template_bigdecimal"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["title"] = BigDecimal("1.1")
            })
        )

        Assert.assertEquals("1.1", (rootView.child(0) as TextView).text)
    }
}