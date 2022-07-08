package com.alibaba.gaiax

import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.basic.GXProgressView
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith

/**
 * @suppress
 */
@RunWith(AndroidJUnit4::class)
class GXComponentProgressTest : GXBaseTest() {
    @Test
    fun template_progress_test() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "view",
            "template_progress"
        )

        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(MOCK_SCREEN_WIDTH, rootView.width())
        Assert.assertEquals(true, rootView.child(0) is GXProgressView)

        val gxProgressView = rootView.child(0) as GXProgressView
        val config = gxProgressView.getConfig()
        Assert.assertEquals(Color.parseColor("#00FFAA"), config?.strokeColor?.value())
        Assert.assertEquals(Color.parseColor("#AAAAAA"), config?.trailColor?.value())
        Assert.assertEquals(false, config?.animated)
        Assert.assertEquals("line", config?.progressType)
    }
}
