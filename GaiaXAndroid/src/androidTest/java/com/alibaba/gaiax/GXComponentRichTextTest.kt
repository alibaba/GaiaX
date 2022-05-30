package com.alibaba.gaiax

import android.graphics.Color
import android.widget.TextView
import android.support.test.runner.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.basic.GXText
import com.alibaba.gaiax.render.view.setFontSize
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXComponentRichTextTest : GXBaseTest() {

    @Test
    fun template_richtext_property_font_color() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "richtext",
            "template_richtext_property_font_color"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        val value = Color.parseColor("#00ff00")
        Assert.assertEquals(value, rootView.child<TextView>(0).currentTextColor)
    }

    @Test
    fun template_richtext_property_font_color_default() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "richtext",
            "template_richtext_property_font_color_default"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(Color.BLACK, rootView.child<TextView>(0).currentTextColor)
    }

    @Test
    fun template_richtext_fitcontent_case_1_lines_1_width_100_percent_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "richtext",
            "template_richtext_fitcontent_case_1_lines_1_width_100_percent_height_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

}