package com.alibaba.gaiax

import android.content.Context
import androidx.appcompat.widget.AppCompatImageView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.GXViewKey
import com.alibaba.gaiax.render.view.basic.GXIImageView
import com.alibaba.gaiax.render.view.basic.GXImageView
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXComponentImageTest : GXBaseTest() {

    class MockImageView(context: Context) : AppCompatImageView(context), GXIImageView {

        override fun onBindData(data: JSONObject?) {
        }
    }

    @Test
    fun template_image_view_support() {
        GXRegisterCenter.instance.registerExtensionViewSupport(GXViewKey.VIEW_TYPE_IMAGE) {
            MockImageView(it)
        }
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "image",
            "template_image_view_support"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(rootView, GXTemplateEngine.GXTemplateData(JSONObject()))

        Assert.assertEquals(true, rootView.child(0) is MockImageView)

        GXRegisterCenter.instance.registerExtensionViewSupport(GXViewKey.VIEW_TYPE_IMAGE) {
            GXImageView(it)
        }
    }
}