package com.alibaba.gaiax

import android.graphics.Color
import android.support.test.runner.AndroidJUnit4
import android.support.v4.view.ViewPager
import android.widget.LinearLayout
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
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
    fun template_slider_size_fixed() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "slider",
            "template-slider-size-fixed"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["nodes"] = JSONArray().apply {
                add(JSONObject())
                add(JSONObject())
                add(JSONObject())
            }
        })
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, 100F.dpToPx())
        )
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(MOCK_SCREEN_WIDTH, rootView.width())
        Assert.assertEquals(100F.dpToPx(), rootView.height())
        Assert.assertEquals(true, rootView is GXSliderView)
        Assert.assertEquals(2, rootView.childCount())
        Assert.assertEquals(true, rootView.child(0) is ViewPager)
        Assert.assertEquals(true, rootView.child(1) is LinearLayout)
        Assert.assertEquals(3, rootView.child(1).childCount())

        val gxSliderView = rootView as GXSliderView
        val config = gxSliderView.getConfig()
        Assert.assertEquals(3000L, config?.scrollTimeInterval)
        Assert.assertEquals(true, config?.infinityScroll)
        Assert.assertEquals(true, config?.hasIndicator)
        Assert.assertEquals(1, config?.selectedIndex)
        Assert.assertEquals(Color.parseColor("#FFFFFF"), config?.indicatorSelectedColor?.value)
        Assert.assertEquals(Color.parseColor("#BBBBBB"), config?.indicatorUnselectedColor?.value)
    }
}
