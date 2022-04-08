package com.alibaba.gaiax

import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.roundToInt

/**
 * https://yuque.antfin-inc.com/gaia/document/xsndwb/edit#ornz
 */
@RunWith(AndroidJUnit4::class)
class GXComponentTextTest : GXBaseTest() {

    @Test
    fun template_text_responsive_scale() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_responsive_scale")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(20F.dpToPx() * responsiveLayoutScale, (rootView.child(0) as TextView).textSize)
    }

    @Test
    fun template_text_line_height_scale() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_line_height_scale")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals((100F.dpToPx() * largeFontScale).roundToInt().toFloat(), rootView.child(0).lineHeight())
    }

    @Test
    fun template_text_font_scale() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_font_scale")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F, rootView.child(0).width())
        Assert.assertEquals((100F * largeFontScale).toInt().toFloat(), rootView.child(0).height())

        GXTemplateEngine.instance.bindData(rootView, templateData)
    }

    @Test
    fun template_text_font_scale_double_bind_data() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_font_scale")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F, rootView.child(0).width())
        Assert.assertEquals((100F * largeFontScale).toInt().toFloat(), rootView.child(0).height())

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F, rootView.child(0).width())
        Assert.assertEquals((100F * largeFontScale).toInt().toFloat(), rootView.child(0).height())
    }

    @Test
    fun template_text_processor_font_family() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_processor_font_family")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(true, rootView.child<TextView>(0).typeface != null)
    }


}