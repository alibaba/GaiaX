package com.alibaba.gaiax

import android.view.View
import android.support.test.runner.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.basic.GXText
import com.alibaba.gaiax.render.view.drawable.GXLinearColorGradientDrawable
import com.alibaba.gaiax.render.view.drawable.GXRoundCornerBorderGradientDrawable
import com.alibaba.gaiax.render.view.setFontSize
import com.alibaba.gaiax.template.GXIExpression
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import com.alibaba.gaiax.utils.GaiaXExpression
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.Charset


@RunWith(AndroidJUnit4::class)
class GXBusinessTest : GXBaseTest() {

    class GXExtensionExpression : GXRegisterCenter.GXIExtensionExpression {

        override fun create(value: Any): GXIExpression {
            return GaiaXExpression.create(value)
        }

        override fun isTrue(value: Any?): Boolean {
            return GaiaXExpression.isCondition(value)
        }

    }

    @Test
    fun yk_vip_ad_task_item() {
        GXRegisterCenter.instance
            .registerExtensionExpression(GXExtensionExpression())

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "business",
            "yk-vip-ad-task-item"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(363F.dpToPx(), 64F.dpToPx())
        )
        val path = "business/yk-vip-ad-task-item.json"
        val templateData = GXTemplateEngine.GXTemplateData(readJsonFromAssets(path))
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(363F.dpToPx(), rootView.width())
        Assert.assertEquals(64F.dpToPx(), rootView.height())
        Assert.assertEquals(true, rootView.child(2).background is GXLinearColorGradientDrawable)
        Assert.assertEquals(
            true,
            rootView.child(2).foreground is GXRoundCornerBorderGradientDrawable
        )
    }

    @Test
    fun yk_vip_VIPPrivilegeItem() {
        GXRegisterCenter.instance
            .registerExtensionExpression(GXExtensionExpression())

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "business",
            "yk-vip-VIPPrivilegeItem"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(117F.dpToPx(), 60F.dpToPx())
        )
        val path = "business/yk-vip-VIPPrivilegeItem.json"
        val templateData = GXTemplateEngine.GXTemplateData(readJsonFromAssets(path))
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(117F.dpToPx(), rootView.width())
        Assert.assertEquals(60F.dpToPx(), rootView.height())

        val textView = GXText(GXMockUtils.context)
        textView.text = "开嘭嘭卡"
        textView.setFontSize(13F.dpToPx())
        textView.measure(0, 0)

        val title = GXTemplateEngine.instance.getGXViewById(rootView, "title")
        Assert.assertEquals(textView.measuredWidth.toFloat(), title?.width())
        Assert.assertEquals(18F.dpToPx(), title?.height())

        val markText = GXTemplateEngine.instance.getGXViewById(rootView, "markText")
        Assert.assertEquals(View.GONE, markText?.visibility)
        Assert.assertEquals(0F.dpToPx(), markText?.width())
        Assert.assertEquals(0F.dpToPx(), markText?.height())

        val subTitle = GXTemplateEngine.instance.getGXViewById(rootView, "subTitle")
        Assert.assertEquals(93F.dpToPx(), subTitle?.width())
        Assert.assertEquals(16F.dpToPx(), subTitle?.height())
    }

    // yk-rec-hot-image-text-card
//    @Test
//    fun yk_rec_hot_image_text_card() {
//        GXRegisterCenter.instance
//            .registerProcessExpression(GXProcessExpression())
//
//        val templateItem = GXTemplateEngine.GXTemplateItem(
//            GXMockUtils.context,
//            "business",
//            "yk-rec-hot-image-text-card"
//        )
//        val rootView = GXTemplateEngine.instance.createView(
//            templateItem,
//            GXTemplateEngine.GXMeasureSize(1080F, null)
//        )
//        val path = "business/yk-rec-hot-image-text-card.json"
//        val templateData = GXTemplateEngine.GXTemplateData(readJsonFromAssets(path))
//        GXTemplateEngine.instance.bindData(rootView, templateData)
//
//        Assert.assertEquals(1080F, rootView.width())
//    }

    @Test
    fun search_component_1011() {
        GXRegisterCenter.instance
            .registerExtensionExpression(GXExtensionExpression())

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "business",
            "search_component_1011"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(1080F.dpToPx(), null)
        )
        val path = "business/search_component_1011.json"
        val templateData = GXTemplateEngine.GXTemplateData(readJsonFromAssets(path))
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(1080F.dpToPx(), rootView.width())
        Assert.assertEquals(60F.dpToPx(), rootView.height())

        val targetView = GXTemplateEngine.instance.getGXViewById(rootView, "third-title")
        Assert.assertEquals(
            1080F.dpToPx() - 15F.dpToPx() - 15F.dpToPx() - 60F.dpToPx() - 9F.dpToPx(),
            targetView?.width()
        )
    }


    private fun readJsonFromAssets(path: String) = JSONObject.parseObject(
        context.assets.open(path).reader(Charset.forName("utf-8")).readText()
    )

}