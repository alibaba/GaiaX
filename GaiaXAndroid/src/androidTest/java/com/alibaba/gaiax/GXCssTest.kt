package com.alibaba.gaiax

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GXCssTest: GXBaseTest() {

    @Test
    fun template_css_compatible_edge_insets() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "css",
            "template_css_compatible_edge_insets"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject())
        )

        val templateContext = GXTemplateContext.getContext(rootView)
        val rootNodeStylePadding = templateContext?.rootNode?.getPaddingRect()

        Assert.assertNotNull(rootNodeStylePadding)
        Assert.assertEquals(18F.dpToPx().toInt(), rootNodeStylePadding?.left)
        Assert.assertEquals(18F.dpToPx().toInt(), rootNodeStylePadding?.right)
        Assert.assertEquals(18F.dpToPx().toInt(), rootNodeStylePadding?.top)
        Assert.assertEquals(18F.dpToPx().toInt(), rootNodeStylePadding?.bottom)
    }
}
