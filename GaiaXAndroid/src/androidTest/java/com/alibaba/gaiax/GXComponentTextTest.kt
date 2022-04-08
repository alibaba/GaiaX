package com.alibaba.gaiax

import android.graphics.Color
import android.support.test.runner.AndroidJUnit4
import android.widget.TextView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.basic.GXText
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

    @Test
    fun template_text_width_flex_grow() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_width_flex_grow")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_width_100px() {

        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_width_100px")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_width_min_100px_text_less_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_width_min_100px_text_less_100px")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_width_min_100px_text_than_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_width_min_100px_text_than_100px")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_width_max_100px_text_less_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_width_max_100px_text_less_100px")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(0F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_width_max_100px_text_than_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_width_max_100px_text_than_100px")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(0F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_width_aspect_ratio() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_width_aspect_ratio")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_height_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_height_100px")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_height_flex_grow() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_height_flex_grow")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_height_min_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_height_min_100px")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(50F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_height_max_100px() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_height_max_100px")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(0F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_height_aspect_ratio() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_height_aspect_ratio")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
    }

    @Test
    fun template_text_margin() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_margin")

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
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_padding")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
        Assert.assertEquals(10F.dpToPx(), (rootView.child(0) as? TextView)?.paddingLeft?.toFloat())
        Assert.assertEquals(10F.dpToPx(), (rootView.child(0) as? TextView)?.paddingTop?.toFloat())
        Assert.assertEquals(10F.dpToPx(), (rootView.child(0) as? TextView)?.paddingRight?.toFloat())
        Assert.assertEquals(10F.dpToPx(), (rootView.child(0) as? TextView)?.paddingBottom?.toFloat())
    }

    @Test
    fun template_text_text_process() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_text_process")

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
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_property_padding")

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
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_property_padding_and_padding_bottom")

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
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_property_font_size")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(20F.dpToPx(), rootView.child<GXText>(0).textSize)
    }

    @Test
    fun template_text_property_font_family() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_property_font_family")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1, rootView.childCount())

        Assert.assertEquals(true, rootView.child<GXText>(0).typeface != null)
    }

    @Test
    fun template_text_property_font_weight() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_property_font_weight")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(true, rootView.child<GXText>(0).typeface.isBold)
    }

    @Test
    fun template_text_property_font_color_default_opensource_version() {
        val templateItem = GXTemplateEngine.GXTemplateItem(GXMockUtils.context, "text", "template_text_property_font_color_default")

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        val rootView = GXTemplateEngine.instance.createView(templateItem, size)

        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(null, rootView.child<GXText>(0).paint.shader)
        Assert.assertEquals(Color.BLACK, rootView.child<GXText>(0).currentTextColor)

        //        Assert.assertEquals(null, rootView.child<TextView>(0).paint.shader)
//        /**
//         * TODO:
//         * 这个地方开源版本和优酷版本存在差异性。
//         * 对于开源版本，默认色为黑色
//         * 对于优酷版本，默认色为textview自己的颜色，未做强制处理。
//         */
//        Assert.assertEquals(TextView(GaiaXMockUtils.context).currentTextColor, rootView.child<TextView>(0).currentTextColor)
//        // Assert.assertEquals(Color.BLACK, rootView.child<TextView>(0).currentTextColor)
    }

//
//    @Test
//    fun template_text_property_font_color() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_font_color")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        Assert.assertEquals(1, rootView.childCount())
//
//        val value = ColorValue.convertToColorValue("#00ff00").value
//        Assert.assertEquals(value, rootView.child<TextView>(0).currentTextColor)
//    }
//
//    @Test
//    fun template_text_property_text_overflow_clip() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_text_overflow_clip")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        Assert.assertEquals(1, rootView.childCount())
//
//        /**
//         * TODO
//         * 此处有差异
//         * 优酷版本，当处于单行状态下，始终使用...来作为文字溢出的状态
//         * 开源版本，没有区分单行状态，根据设置来决定。
//         */
//        // Assert.assertEquals(null, rootView.child<TextView>(0).ellipsize)
//        Assert.assertEquals(TextUtils.TruncateAt.END, rootView.child<TextView>(0).ellipsize)
//    }
//
//    @Test
//    fun template_text_property_text_overflow() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_text_overflow")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        Assert.assertEquals(1, rootView.childCount())
//
//        Assert.assertEquals(TextUtils.TruncateAt.END, rootView.child<TextView>(0).ellipsize)
//    }
//
//    @Test
//    fun template_text_property_text_overflow_ellipsis() {
//
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_text_overflow_ellipsis")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        Assert.assertEquals(1, rootView.childCount())
//
//        Assert.assertEquals(TextUtils.TruncateAt.END, rootView.child<TextView>(0).ellipsize)
//    }
//
//    @Test
//    fun template_text_property_text_decoration() {
//
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_text_decoration")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        Assert.assertEquals(1, rootView.childCount())
//
//        Assert.assertEquals(1283, rootView.child<TextView>(0).paint.flags)
//    }
//
//    @Test
//    fun template_text_property_text_decoration_line_through() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_text_decoration_line_through")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        Assert.assertEquals(1, rootView.childCount())
//
//        Assert.assertEquals(Paint.STRIKE_THRU_TEXT_FLAG, rootView.child<TextView>(0).paint.flags)
//    }
//
//    @Test
//    fun template_text_property_text_decoration_underline() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_text_decoration_underline")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        Assert.assertEquals(1, rootView.childCount())
//
//        Assert.assertEquals(Paint.UNDERLINE_TEXT_FLAG, rootView.child<TextView>(0).paint.flags)
//    }
//
//    @Test
//    fun template_text_property_line_height() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_line_height")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        Assert.assertEquals(1, rootView.childCount())
//
//        Assert.assertEquals(50F.dpToPx(), rootView.child<TextView>(0).lineHeight.toFloat())
//    }
//
//    @Test
//    fun template_text_property_line_height_same_size_height() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_line_height_same_size_height")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        Assert.assertEquals(1, rootView.childCount())
//
//        /**
//         * TODO:
//         * 此处原本存在差异
//         * 优酷版本，未设置lineHeight高度，直接设置的居中显示
//         * 开源版本，即设置了lineHeight高度，也设置了居中显示
//         **/
//        // Assert.assertEquals(50F.dpToPx(), rootView.child<TextView>(0).lineHeight.toFloat())
//
//        Assert.assertEquals(Gravity.CENTER_VERTICAL, rootView.child<TextView>(0).gravity.and(Gravity.CENTER_VERTICAL))
//    }
//
//    @Test
//    fun template_text_property_lines() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_lines")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val child = rootView.child(0)
//
//        Assert.assertEquals(100F.dpToPx(), child.width())
//        /**
//         * TODO:
//         * 此处有问题，lines计算结果应该是line-height*lines=45px，但是实际却是100px
//         */
//        Assert.assertEquals(100F.dpToPx(), child.height())
//    }
//
//    @Test
//    fun template_text_property_text_align_left() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_text_align_left")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val child = rootView.child(0)
//        Assert.assertEquals(Gravity.LEFT, (child as TextView).gravity.and(Gravity.LEFT))
//    }
//
//    @Test
//    fun template_text_property_text_align_center() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_text_align_left")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val child = rootView.child(0)
//        Assert.assertEquals(Gravity.CENTER, (child as TextView).gravity.and(Gravity.CENTER))
//    }
//
//    @Test
//    fun template_text_property_text_align_right() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_property_text_align_right")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val child = rootView.child(0)
//        Assert.assertEquals(Gravity.RIGHT, (child as TextView).gravity.and(Gravity.RIGHT))
//    }
//
//    @Test
//    fun template_text_fitcontent_case_1_lines_1_width_100_percent_height_100px() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_fitcontent_case_1_lines_1_width_100_percent_height_100px")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val textView = GTextView(GaiaXMockUtils.context)
//        textView.text = "HelloWorld"
//        textView.setFontSize(20F.dpToPx())
//        textView.measure(0, 0)
//
//        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
//        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
//    }
//
//    @Test
//    fun template_text_fitcontent_case_1_lines_1_width_null_height_100px() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_fitcontent_case_1_lines_1_width_null_height_100px")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val textView = GTextView(GaiaXMockUtils.context)
//        textView.text = "HelloWorld"
//        textView.setFontSize(20F.dpToPx())
//        textView.measure(0, 0)
//
//        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
//        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
//    }
//
//    @Test
//    fun template_text_fitcontent_case_1_lines_1_width_null_height_null() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_fitcontent_case_1_lines_1_width_null_height_null")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val textView = GTextView(GaiaXMockUtils.context)
//        textView.text = "HelloWorld"
//        textView.setFontSize(20F.dpToPx())
//        textView.measure(0, 0)
//
//        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
//        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
//    }
//
//    @Test
//    fun template_text_fitcontent_case_1_lines_null_width_100_percent_height_100px() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_fitcontent_case_1_lines_null_width_100_percent_height_100px")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val textView = GTextView(GaiaXMockUtils.context)
//        textView.text = "HelloWorld"
//        textView.setFontSize(20F.dpToPx())
//        textView.measure(0, 0)
//
//        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
//        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
//    }
//
//    @Test
//    fun template_text_fitcontent_case_1_lines_null_width_null_height_100px_repeat_bind_data() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_fitcontent_case_1_lines_null_width_null_height_100px_repeat_bind_data")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject().apply {
//                this["text"] = "0"
//            })
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val textView = GTextView(GaiaXMockUtils.context)
//        textView.text = "0"
//        textView.setFontSize(14F.dpToPx())
//        textView.measure(0, 0)
//
//        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
//        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
//
//        ////////////////////////////////////////////////////////////
//
//        val textView1 = GTextView(GaiaXMockUtils.context)
//        textView1.text = "300"
//        textView1.setFontSize(14F.dpToPx())
//        textView1.measure(0, 0)
//
//        val params2 = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_fitcontent_case_1_lines_null_width_null_height_100px_repeat_bind_data")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject().apply {
//                this["text"] = "300"
//            })
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params2)
//
//        Assert.assertEquals(textView1.measuredWidth.toFloat(), rootView.child(0).width())
//        Assert.assertEquals(100F.dpToPx(), rootView.child(0).height())
//    }
//
//    @Test
//    fun template_text_fitcontent_case_2_lines_0_width_100_percent_height_100px() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_fitcontent_case_2_lines_0_width_100_percent_height_100px")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val textView = GTextView(GaiaXMockUtils.context)
//        textView.text = "HelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorldHelloWorld"
//        textView.setFontSize(20F.dpToPx())
//        val widthSpec = View.MeasureSpec.makeMeasureSpec(MOCK_SCREEN_WIDTH.toInt(), View.MeasureSpec.AT_MOST)
//        textView.measure(widthSpec, 0)
//
//        Assert.assertEquals(1080F.dpToPx(), rootView.child(0).width())
//        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
//    }
//
//    @Test
//    fun template_text_fitcontent_case_2_lines_0_width_100px_height_100px() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_fitcontent_case_2_lines_0_width_100px_height_100px")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val textView = GTextView(GaiaXMockUtils.context)
//        textView.text = "HelloWorld"
//        textView.setFontSize(20F.dpToPx())
//        val widthSpec = View.MeasureSpec.makeMeasureSpec(100F.dpToPx().toInt(), View.MeasureSpec.AT_MOST)
//        textView.measure(widthSpec, 0)
//
//        Assert.assertEquals(100F.dpToPx(), rootView.child(0).width())
//        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
//    }
//
//    @Test
//    fun template_text_fitcontent_case_2_lines_0_width_null_height_100px() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_fitcontent_case_2_lines_0_width_null_height_100px")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val width = rootView.child(0).width()
//        val height = rootView.child(0).height()
//        Assert.assertEquals(0F, width)
//        /**
//         * TODO:
//         * 优酷版本，在这种情况下，给出了错误的处理结果
//         * 开源版本，不处理这种情况，直接抛出异常
//         */
//        Assert.assertEquals(true, height != 100F.dpToPx())
//    }
//
//    @Test
//    fun template_text_fitcontent_case_2_lines_5_width_100_percent_height_100px() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_fitcontent_case_2_lines_5_width_100_percent_height_100px")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val textView = GTextView(GaiaXMockUtils.context)
//        textView.text = "HelloWorld"
//        textView.setFontSize(20F.dpToPx())
//        textView.measure(0, 0)
//
//        /**
//         * TODO:
//         * 优酷版本，文字宽度为实际文字宽度，高度也为实际文字高度
//         * 开源版本包，文字宽度为设置的文字宽度，高度为自适应的高度
//         */
//        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
//        Assert.assertEquals(textView.measuredHeight.toFloat(), rootView.child(0).height())
//    }
//
//    @Test
//    fun template_text_fitcontent_case_1_lines_null_width_null_height_20px_lead_to_error_update_flexbox_size_minsize_maxsize() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_fitcontent_case_1_lines_null_width_null_height_20px_lead_to_error_update_flexbox_size_minsize_maxsize")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject().apply {
//                this["text"] = "0"
//            })
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        val textView = GTextView(GaiaXMockUtils.context)
//        textView.text = "0"
//        textView.setFontSize(14F.dpToPx())
//        textView.measure(0, 0)
//
//        Assert.assertEquals(textView.measuredWidth.toFloat(), rootView.child(0).width())
//        Assert.assertEquals(15F.dpToPx(), rootView.child(0).height())
//
//        //////////////////////////////////////////////////////////////////////////////////////////////////
//
//        val params2 = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_fitcontent_case_1_lines_null_width_null_height_20px_lead_to_error_update_flexbox_size_minsize_maxsize")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject().apply {
//                this["text"] = "300"
//            })
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params2)
//
//        val textView1 = GTextView(GaiaXMockUtils.context)
//        textView1.text = "300"
//        textView1.setFontSize(14F.dpToPx())
//        textView1.measure(0, 0)
//
//        Assert.assertEquals(textView1.measuredWidth.toFloat(), rootView.child(0).width())
//        Assert.assertEquals(15F.dpToPx(), rootView.child(0).height())
//    }
//
//    @Test
//    fun template_text_background_color() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_background_color")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        Assert.assertEquals(true, rootView.child(0).background is GradientDrawable)
//        Assert.assertEquals(Color.parseColor("#e4e4e4"), (rootView.child(0).background as GradientDrawable).colors?.get(0))
//    }
//
//    @Test
//    fun template_text_border() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_border")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        Assert.assertEquals(GradientDrawable.RECTANGLE, (rootView.child(0).background as? GradientDrawable)?.shape)
//        Assert.assertEquals(0F.dpToPx(), (rootView.child(0).background as? GradientDrawable)?.cornerRadii?.get(0))
//    }
//
//    @Test
//    fun template_scroll_radius() {
//        val container = FrameLayout(context)
//
//        val params = GaiaX.Params.Builder()
//            .templateBiz("text")
//            .templateId("template_text_radius")
//            .context(GaiaXMockUtils.context)
//            .container(container)
//            .data(JSONObject())
//            .width(MOCK_SCREEN_WIDTH)
//            .mode(LoadType.SYNC_NORMAL)
//            .build()
//
//        GaiaX.instance.bindView(params)
//
//        val rootView = container.child(0)
//
//        Assert.assertEquals(true, (rootView.child(0).clipToOutline))
//    }

}