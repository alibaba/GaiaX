package com.alibaba.gaiax

import android.graphics.Color
import android.graphics.Paint
import android.support.test.runner.AndroidJUnit4
import android.text.TextUtils
import android.view.Gravity
import android.view.View
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.basic.GXText
import com.alibaba.gaiax.render.view.setFontSize
import com.alibaba.gaiax.template.GXColor
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.Charset
import kotlin.math.roundToInt
import android.graphics.drawable.GradientDrawable as GradientDrawable1

/**
 * https://yuque.antfin-inc.com/gaia/document/xsndwb/edit#ornz
 */
@RunWith(AndroidJUnit4::class)
class GXComponentTextTest : GXBaseTest() {

    @Test
    fun template_text_background_color() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_background_color"
        )
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(true, rootView.child(0).background is GradientDrawable1)
        Assert.assertEquals(
            Color.parseColor("#e4e4e4"),
            (rootView.child(0).background as GradientDrawable1).colors?.get(0)
        )
    }

    @Test
    fun template_text_border() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_border")
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(null, (rootView.child(0).background as? GradientDrawable1)?.shape)
        Assert.assertEquals(
            null,
            (rootView.child(0).background as? GradientDrawable1)?.cornerRadii?.get(0)
        )
    }

    @Test
    fun template_text_radius() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_radius")
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(true, (rootView.child(0).clipToOutline))
    }

    @Test
    fun template_text_responsive_scale() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_responsive_scale"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(
            20F.dpToPx() * responsiveLayoutScale,
            (rootView.child(0) as TextView).textSize
        )
    }

    @Test
    fun template_text_line_height_scale() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_line_height_scale"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(
            (100F.dpToPx() * largeFontScale).roundToInt().toFloat(),
            rootView.child(0).lineHeight()
        )
    }

    @Test
    fun template_text_font_scale() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_font_scale")
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
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_font_scale")
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
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_processor_font_family"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(true, rootView.child<TextView>(0).typeface != null)
    }

    @Test
    fun template_text_width_flex_grow() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_width_flex_grow"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_width_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_width_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_width_min_100px_text_less_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_width_min_100px_text_less_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_width_min_100px_text_than_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_width_min_100px_text_than_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_width_max_100px_text_less_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_width_max_100px_text_less_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(0F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_width_max_100px_text_than_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_width_max_100px_text_than_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(0F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_width_aspect_ratio() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_width_aspect_ratio"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_height_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_height_flex_grow() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_height_flex_grow"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_height_min_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_height_min_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(50F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_height_max_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_height_max_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(0F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_height_aspect_ratio() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_height_aspect_ratio"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_margin() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_margin")
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).x())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).y())
    }

    @Test
    fun template_text_padding() {
        val templateItem =
            GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_padding")
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(10F.dpToPx(), (rootView.child(0) as? TextView)?.paddingLeft?.toFloat())
        Assert.assertEquals(10F.dpToPx(), (rootView.child(0) as? TextView)?.paddingTop?.toFloat())
        Assert.assertEquals(10F.dpToPx(), (rootView.child(0) as? TextView)?.paddingRight?.toFloat())
        Assert.assertEquals(
            10F.dpToPx(),
            (rootView.child(0) as? TextView)?.paddingBottom?.toFloat()
        )
    }

    @Test
    fun template_text_text_process() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_text_process"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["text"] = "HelloWorld"
        })
        templateData.dataListener = object : GXTemplateEngine.GXIDataListener {

            override fun onTextProcess(gxTextData: GXTemplateEngine.GXTextData): CharSequence? {
                Assert.assertEquals("HelloWorld", gxTextData.text)
                return "GaiaX"
            }
        }
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals("GaiaX", (rootView.child(0) as GXText).text)
    }

    @Test
    fun template_text_property_padding() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_padding"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingLeft.toFloat())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingTop.toFloat())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingRight.toFloat())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingBottom.toFloat())

    }

    @Test
    fun template_text_property_padding_and_padding_bottom() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_padding_and_padding_bottom"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingLeft.toFloat())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingTop.toFloat())
        Assert.assertEquals(10F.dpToPx(), rootView.child(0).paddingRight.toFloat())
        Assert.assertEquals(20F.dpToPx(), rootView.child(0).paddingBottom.toFloat())

    }

    @Test
    fun template_text_property_font_size() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_font_size"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        Assert.assertEquals(20F.dpToPx(), rootView.child<GXText>(0).textSize)
    }

    @Test
    fun template_text_property_font_family() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_font_family"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        Assert.assertEquals(true, rootView.child<GXText>(0).typeface != null)
    }

    @Test
    fun template_text_property_font_weight() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_font_weight"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(true, rootView.child<GXText>(0).typeface.isBold)
    }

    /**
     * 这里或许可以直接使用开源版本兼容优酷版本
     *
     * 这个地方开源版本和优酷版本存在差异性。
     * 对于开源版本，默认色为黑色
     * 对于优酷版本，默认色为textview自己的颜色，未做强制处理。
     */
    @Test
    fun template_text_property_font_color_default_opensource_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_font_color_default"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(null, rootView.child<GXText>(0).paint.shader)
        Assert.assertEquals(Color.BLACK, rootView.child<GXText>(0).currentTextColor)
    }

    /**
     * 这里或许可以直接使用开源版本兼容优酷版本
     *
     * 这个地方开源版本和优酷版本存在差异性。
     * 对于开源版本，默认色为黑色
     * 对于优酷版本，默认色为textview自己的颜色，未做强制处理。
     */
    @Test
    fun template_text_property_font_color_default_youku_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_font_color_default"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(null, rootView.child<GXText>(0).paint.shader)
        Assert.assertEquals(Color.BLACK, rootView.child<GXText>(0).currentTextColor)
    }

    @Test
    fun template_text_property_font_color() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_font_color"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        val value = GXColor.create("#00ff00")?.value
        Assert.assertEquals(value, rootView.child<GXText>(0).currentTextColor)
    }

    /**
     * TODO
     * 此处有差异
     * 优酷版本，当处于单行状态下，始终使用...来作为文字溢出的状态
     * 开源版本，没有区分单行状态，根据设置来决定。
     */
    @Test
    fun template_text_property_text_overflow_clip_opensource_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_text_overflow_clip"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(null, rootView.child<GXText>(0).ellipsize)
    }

    /**
     * TODO
     * 此处有差异
     * 优酷版本，当处于单行状态下，始终使用...来作为文字溢出的状态
     * 开源版本，没有区分单行状态，根据设置来决定。
     */
    @Test
    fun template_text_property_text_overflow_clip_youku_version() {
        GXRegisterCenter.instance.registerExtensionStaticProperty(object :
            GXRegisterCenter.GXIExtensionStaticProperty {
            override fun convert(params: GXRegisterCenter.GXIExtensionStaticProperty.GXParams): Any? {
                if (params.propertyName == GXTemplateKey.STYLE_FONT_TEXT_OVERFLOW && params.value == "clip") {
                    return TextUtils.TruncateAt.END
                }
                return null
            }

        })
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_text_overflow_clip"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        Assert.assertEquals(TextUtils.TruncateAt.END, rootView.child<TextView>(0).ellipsize)
    }

    @Test
    fun template_text_property_text_overflow() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_text_overflow"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        Assert.assertEquals(TextUtils.TruncateAt.END, rootView.child<GXText>(0).ellipsize)
    }

    @Test
    fun template_text_property_text_overflow_ellipsis() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_text_overflow_ellipsis"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        Assert.assertEquals(TextUtils.TruncateAt.END, rootView.child<GXText>(0).ellipsize)
    }

    @Test
    fun template_text_property_text_decoration() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_text_decoration"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        Assert.assertEquals(1283, rootView.child<GXText>(0).paint.flags)
    }

    @Test
    fun template_text_property_text_decoration_line_through() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_text_decoration_line_through"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        Assert.assertEquals(Paint.STRIKE_THRU_TEXT_FLAG, rootView.child<GXText>(0).paint.flags)
    }

    @Test
    fun template_text_property_text_decoration_underline() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_text_decoration_underline"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        Assert.assertEquals(Paint.UNDERLINE_TEXT_FLAG, rootView.child<GXText>(0).paint.flags)
    }

    @Test
    fun template_text_property_line_height() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_line_height"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())
        Assert.assertEquals(50F.dpToPx(), rootView.child<GXText>(0).lineHeight.toFloat())
    }

    /**
     * TODO:
     * 此处原本存在差异
     * 优酷版本，未设置lineHeight高度，直接设置的居中显示
     * 开源版本，即设置了lineHeight高度，也设置了居中显示
     **/
    @Test
    fun template_text_property_line_height_same_size_height_opensource_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_line_height_same_size_height"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(50F.dpToPx(), rootView.child<GXText>(0).lineHeight.toFloat())
        Assert.assertEquals(
            Gravity.CENTER_VERTICAL,
            rootView.child<GXText>(0).gravity.and(Gravity.CENTER_VERTICAL)
        )
    }

    /**
     * TODO:
     * 此处原本存在差异
     * 优酷版本，未设置lineHeight高度，直接设置的居中显示
     * 开源版本，即设置了lineHeight高度，也设置了居中显示
     **/
    @Test
    fun template_text_property_line_height_same_size_height_youku_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_line_height_same_size_height"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        // TODO: 此处暂时这么直接使用开源版本兼容优酷版本，但是可能会导致低端机型显示不正确。
        Assert.assertEquals(50F.dpToPx(), rootView.child<GXText>(0).lineHeight.toFloat())
        Assert.assertEquals(
            Gravity.CENTER_VERTICAL,
            rootView.child<GXText>(0).gravity.and(Gravity.CENTER_VERTICAL)
        )
    }


    @Test
    fun template_text_property_lines() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_lines"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        /**
         * TODO:
         * 此处可能有问题，lines计算结果应该是line-height*lines=45px，但是实际却是100px
         */
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_property_text_align_left() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_text_align_left"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(Gravity.LEFT, (rootView.child(0) as TextView).gravity.and(Gravity.LEFT))
    }

    @Test
    fun template_text_property_text_align_center() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_text_align_center"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(
            Gravity.CENTER,
            (rootView.child(0) as TextView).gravity.and(Gravity.CENTER)
        )
    }

    @Test
    fun template_text_property_text_align_right() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_property_text_align_right"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(
            Gravity.RIGHT,
            (rootView.child(0) as TextView).gravity.and(Gravity.RIGHT)
        )
    }

    @Test
    fun template_text_fitcontent_lines_null_width_null_height_fixed_padding_left_padding_right() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_null_width_null_height_fixed_padding_left_padding_right"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        val padding = 10F.dpToPx().roundToInt()
        textView.setPadding(padding, 0, padding, 0)
        textView.setFontSize(13F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(24F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_lines_1_width_100_percent_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_1_width_100_percent_height_100px"
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

    @Test
    fun template_text_fitcontent_lines_1_width_null_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_1_width_null_height_100px"
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

    @Test
    fun template_text_fitcontent_lines_1_width_null_height_null() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_1_width_null_height_null"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_lines_null_width_100_percent_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_null_width_100_percent_height_100px"
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

    @Test
    fun template_text_fitcontent_lines_null_width_null_height_100px_repeat_bind_data() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_null_width_null_height_100px_repeat_bind_data"
        )
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["text"] = "0"
            })
        )

        val textView = GXText(GXMockUtils.context)
        textView.text = "0"
        textView.setFontSize(14F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        ////////////////////////////////////////////////////////////

        val textView1 = GXText(GXMockUtils.context)
        textView1.text = "300"
        textView1.setFontSize(14F.dpToPx())
        textView1.measure(0, 0)

        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["text"] = "300"
            })
        )

        Assert.assertEquals(textView1.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_lines_0_width_100_percent_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_0_width_100_percent_height_100px"
        )
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        val textView = GXText(GXMockUtils.context)
        textView.text =
            "HelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorld"
        textView.setFontSize(20F.dpToPx())
        val widthSpec =
            View.MeasureSpec.makeMeasureSpec(MOCK_SCREEN_WIDTH.toInt(), View.MeasureSpec.AT_MOST)
        textView.measure(widthSpec, 0)

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_lines_0_width_100px_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_0_width_100px_height_100px"
        )
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        val widthSpec =
            View.MeasureSpec.makeMeasureSpec(100F.dpToPx().toInt(), View.MeasureSpec.AT_MOST)
        textView.measure(widthSpec, 0)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
    }

    /**
     * TODO:
     * 优酷版本，在这种情况下，给出了错误的处理结果
     * 开源版本，不处理这种情况，直接抛出异常
     */
    @Test(expected = IllegalArgumentException::class)
    fun template_text_fitcontent_lines_0_width_null_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_0_width_null_height_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)
    }

    /**
     * TODO:
     * 优酷版本，在这种情况下，给出了错误的处理结果
     * 开源版本，不处理这种情况，直接抛出异常
     */
    @Test
    fun template_text_fitcontent_lines_0_width_null_height_100px_youku_version() {
        GXRegisterCenter.instance.registerExtensionCompatibility(object :
            GXRegisterCenter.GXIExtensionCompatibility {
            override fun isPreventFitContentThrowException(): Boolean {
                return true
            }
        })
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_0_width_null_height_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)
        GXRegisterCenter.instance.registerExtensionCompatibility(object :
            GXRegisterCenter.GXIExtensionCompatibility {
            override fun isPreventFitContentThrowException(): Boolean {
                return false
            }
        })
    }

    /**
     * TODO:
     * 优酷版本，文字宽度为实际文字宽度，高度也为实际文字高度
     * 开源版本包，文字宽度为设置的文字宽度，高度为自适应的高度
     */
    @Test
    fun template_text_fitcontent_lines_5_width_100_percent_height_100px_opensource_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_5_width_100_percent_height_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
    }

    /**
     * TODO:
     * 优酷版本，文字宽度为实际文字宽度，高度也为实际文字高度
     * 开源版本包，文字宽度为设置的文字宽度，高度为自适应的高度
     */
    @Test
    fun template_text_fitcontent_lines_5_width_100_percent_height_100px_youku_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_5_width_100_percent_height_100px"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        textView.measure(0, 0)

        // 这里或许可直接使用开源的逻辑兼容优酷的逻辑，并不会有什么负面影响
        // Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_lines_null_width_null_height_20px_lead_to_error_update_flexbox_size_minsize_maxsize() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_null_width_null_height_20px_lead_to_error_update_flexbox_size_minsize_maxsize"
        )
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["text"] = "0"
            })
        )

        val textView = GXText(GXMockUtils.context)
        textView.text = "0"
        textView.setFontSize(14F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(15F.dpToPx(), rootView.child(0).height())

        //////////////////////////////////////////////////////////////////////////////////////////////////

        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["text"] = "300"
            })
        )

        val textView1 = GXText(GXMockUtils.context)
        textView1.text = "300"
        textView1.setFontSize(14F.dpToPx())
        textView1.measure(0, 0)

        Assert.assertEquals(textView1.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(15F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_lines_null_width_flex_grow_height_fixed() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_null_width_flex_grow_height_fixed"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val size = GXTemplateEngine.GXMeasureSize(1080F, null)
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, templateData)

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_dynamic_modify_padding_case_flex_grow_invalid_child() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_dynamic_modify_padding_case_flex_grow_invalid_child"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(1080F, null)
        )
        val templateData = GXTemplateEngine.GXTemplateData(
            JSONObject.parseObject(
                context.assets.open("text/template_text_fitcontent_dynamic_modify_padding_case_flex_grow_invalid_child.json")
                    .reader(Charset.forName("utf-8")).readText()
            )
        )
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F, rootView.width())
        Assert.assertEquals(36F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(
            1080F - 36F.dpToPx() - 14F.dpToPx() - 14F.dpToPx(),
            rootView.child(1).width()
        )
    }

    @Test
    fun template_text_fitcontent_lines_null_width_flex_grow_height_fixed_repeat_bind_data() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_null_width_flex_grow_height_fixed_repeat_bind_data"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(1080F, null)
        )

        Assert.assertEquals(1080F, rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())

        Assert.assertEquals(1080F - 100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())

        Assert.assertEquals(100F.dpToPx(), rootView.child(1).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(1).height())

        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["text"] = "哈哈"
            })
        )

        val textView = GXText(GXMockUtils.context)
        textView.text = "哈哈"
        textView.setFontSize(14F.dpToPx())
        textView.measure(0, 0)

        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())

        ////////////////////////////////////////////////////////////

        val textView1 = GXText(GXMockUtils.context)
        textView1.text = "哈哈哈哈哈哈哈哈"
        textView1.setFontSize(14F.dpToPx())
        textView1.measure(0, 0)

        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["text"] = "哈哈哈哈哈哈哈哈"
            })
        )

        Assert.assertEquals(textView1.measuredWidth.toFloat(), rootView.child(0).width())
    }

    @Test
    fun template_text_fitcontent_lines_0_width_100px_height_100px_databinding_fitcontent() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_0_width_100px_height_100px_databinding_fitcontent"
        )
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        val widthSpec = View.MeasureSpec.makeMeasureSpec(
            100F.dpToPx().toInt(), View.MeasureSpec.AT_MOST
        )
        textView.measure(widthSpec, 0)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
    }

    @Test
    fun template_text_fitcontent_lines_0_width_100px_height_100px_databinding_fitcontent_youku_version() {
        GXRegisterCenter.instance.registerExtensionCompatibility(object :
            GXRegisterCenter.GXIExtensionCompatibility {
            override fun isCompatibilityDataBindingFitContent(): Boolean {
                return true
            }
        })
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "text",
            "template_text_fitcontent_lines_0_width_100px_height_100px_databinding_fitcontent"
        )
        val rootView = GXTemplateEngine.instance.createView(templateItem, size)
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        val textView = GXText(GXMockUtils.context)
        textView.text = "HelloWorld"
        textView.setFontSize(20F.dpToPx())
        val widthSpec = View.MeasureSpec.makeMeasureSpec(
            100F.dpToPx().toInt(), View.MeasureSpec.AT_MOST
        )
        textView.measure(widthSpec, 0)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())

        GXRegisterCenter.instance.registerExtensionCompatibility(object :
            GXRegisterCenter.GXIExtensionCompatibility {
            override fun isCompatibilityDataBindingFitContent(): Boolean {
                return false
            }
        })
    }
}