package com.alibaba.gaiax

import android.view.View
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.basic.GXText
import com.alibaba.gaiax.render.view.setFontSize
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import com.alibaba.gaiax.utils.GXScreenUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.Charset


@RunWith(AndroidJUnit4::class)
class GXBusinessTest : GXBaseTest() {

    /**
     * TODO: bad case
     */
    @Test
    fun yk_vip_channel_identityarea() {

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "business",
            "yk-vip-channel-identityarea"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())
        GXTemplateEngine.instance.bindData(rootView, templateData)

        Assert.assertEquals(375F.dpToPx(), rootView.width())
        Assert.assertEquals(66F.dpToPx(), rootView.height())

        val avatar = GXTemplateEngine.instance.getGXViewById(rootView, "avatar")
        Assert.assertEquals(40F.dpToPx(), avatar?.width())
        Assert.assertEquals(40F.dpToPx(), avatar?.height())

        val button = GXTemplateEngine.instance.getGXViewById(rootView, "button")
        Assert.assertEquals(35F.dpToPx(), button?.width())
        Assert.assertEquals(35F.dpToPx(), button?.height())

        val titleView = GXTemplateEngine.instance.getGXViewById(rootView, "titleView")
        Assert.assertEquals(375F.dpToPx() - 40F.dpToPx() - 35F.dpToPx(), titleView?.width())
        Assert.assertEquals(66F.dpToPx(), titleView?.height())
    }

    @Test
    fun yk_vip_ad_task_item() {

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
    }

    @Test
    fun yk_vip_VIPPrivilegeItem() {

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

    @Test
    fun search_component_1011() {

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

    @Test
    fun yk_vip_channel_sideslip() {
        GXScreenUtils.isDebug = true

        GXScreenUtils.screenWidth = 375F.dpToPx()
        GXScreenUtils.screenHeight = 750F.dpToPx()

        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "business", "yk-vip-channel-sideslip"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem, GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        )
        val path = "business/yk-vip-adsideslip.json"
        val gxTemplateData = GXTemplateEngine.GXTemplateData(readJsonFromAssets(path))
        GXTemplateEngine.instance.bindData(rootView, gxTemplateData)
        rootView.executeRecyclerView()

        Assert.assertEquals(rootView.height(), rootView.child(0).height())
        Assert.assertEquals(rootView.child(0).height(), rootView.child(0).child(0).height())

        GXScreenUtils.screenWidth = 750F.dpToPx()
        GXScreenUtils.screenHeight = 750F.dpToPx()

        GXTemplateEngine.instance.bindData(
            rootView, gxTemplateData, GXTemplateEngine.GXMeasureSize(750F.dpToPx(), null)
        )
        rootView.executeRecyclerView()

        Assert.assertEquals(rootView.height(), rootView.child(0).height())
        Assert.assertEquals(rootView.child(0).height(), rootView.child(0).child(0).height())

        GXScreenUtils.isDebug = false
    }

    private fun readJsonFromAssets(path: String) = JSONObject.parseObject(
        context.assets.open(path).reader(Charset.forName("utf-8")).readText()
    )

}