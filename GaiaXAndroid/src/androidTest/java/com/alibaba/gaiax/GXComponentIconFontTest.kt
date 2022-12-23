package com.alibaba.gaiax

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Test
import org.junit.runner.RunWith

/**
 * https://yuque.antfin-inc.com/gaia/document/xsndwb/edit#ornz
 */
@RunWith(AndroidJUnit4::class)
class GXComponentIconFontTest : GXBaseTest() {


    @Test(expected = IllegalArgumentException::class)
    fun template_iconfont_throw_exception() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "iconfont",
            "template_iconfont_throw_exception"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject())
        )
    }

    @Test
    fun template_iconfont_prevent_throw_exception() {
        GXRegisterCenter.instance.registerExtensionCompatibility(
            GXRegisterCenter.GXIExtensionCompatibilityConfig().apply {
                this.isPreventIconFontTypefaceThrowException = true
            }
        )

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "iconfont",
            "template_iconfont_prevent_throw_exception"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject())
        )
    }
}