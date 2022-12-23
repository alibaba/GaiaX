package com.alibaba.gaiax

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.customview.CustomView
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import com.alibaba.gaiax.utils.getStringExt
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXComponentCustomTest : GXBaseTest() {

    @Test
    fun template_customview_binddata() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "custom", "template_customview_binddata"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["title"] = "title"
            this["subtitle"] = "subtitle"

        })
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals("title", rootView.child<CustomView>(0).data?.getString("value"))
        Assert.assertEquals(
            "subtitle",
            rootView.child<CustomView>(0).data?.getStringExt("extend.custom_extend_subtitle")
        )
    }

}