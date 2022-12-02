package com.alibaba.gaiax

import android.graphics.Color
import android.graphics.Rect
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.viewpager.widget.ViewPager
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.customview.CustomSliderIndicatorView
import com.alibaba.gaiax.render.view.container.slider.GXSliderView
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * https://yuque.antfin-inc.com/gaia/document/xsndwb/edit#ornz
 */
@RunWith(AndroidJUnit4::class)
class GXComponentSliderTest : GXBaseTest() {

    @Test
    fun template_slider_size_aspect_ratio() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "slider", "template_slider_size_aspect_ratio"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                add(JSONObject())
                add(JSONObject())
                add(JSONObject())
            }
        })
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(375F.dpToPx(), rootView.width())
        Assert.assertEquals(375F.dpToPx(), rootView.height())
    }

    @Test
    fun template_slider_root_width_percent_item_size_aspect_ratio() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "slider", "template_slider_root_width_percent_item_size_aspect_ratio"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                add(JSONObject())
                add(JSONObject())
                add(JSONObject())
            }
        })
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(375F.dpToPx() * 0.9F, rootView.width())
        Assert.assertEquals(375F.dpToPx() * 0.9F, rootView.height())
    }

    @Test
    fun template_slider_size_fixed() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "slider", "template-slider-size-fixed"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                add(JSONObject())
                add(JSONObject())
                add(JSONObject())
            }
        })
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, 100F.dpToPx())
        )
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(MOCK_SCREEN_WIDTH, rootView.width())
        Assert.assertEquals(150F.dpToPx(), rootView.height())
        Assert.assertEquals(true, rootView is GXSliderView)
        Assert.assertEquals(2, rootView.childCount())
        Assert.assertEquals(true, rootView.child(0) is ViewPager)
        Assert.assertEquals(true, rootView.child(1) is CustomSliderIndicatorView)

        val gxSliderView = rootView as GXSliderView
        val config = gxSliderView.getConfig()
        Assert.assertEquals(3000L, config?.scrollTimeInterval)
        Assert.assertEquals(true, config?.infinityScroll)
        Assert.assertEquals(true, config?.hasIndicator)
        Assert.assertEquals(1, config?.selectedIndex)
        Assert.assertEquals(Color.parseColor("#FFFFFF"), config?.indicatorSelectedColor?.value())
        Assert.assertEquals(Color.parseColor("#BBBBBB"), config?.indicatorUnselectedColor?.value())
        Assert.assertEquals(
            Rect(
                30F.dpToPx().toInt(),
                10F.dpToPx().toInt(),
                30F.dpToPx().toInt(),
                10F.dpToPx().toInt()
            ), config?.indicatorMargin
        )
        Assert.assertEquals(GXSliderView.IndicatorPosition.TOP_CENTER, config?.indicatorPosition)
        Assert.assertEquals(
            "com.alibaba.gaiax.customview.CustomSliderIndicatorView", config?.indicatorClass
        )
    }
}
