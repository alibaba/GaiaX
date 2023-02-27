package com.alibaba.gaiax

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.basic.GXView
import com.alibaba.gaiax.render.view.drawable.GXColorGradientDrawable
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXDarkUtils
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXRefactorTest : GXBaseTest() {

    @Before
    fun init() {
        GXRegisterCenter.instance.registerExtensionColor(object :
            GXRegisterCenter.GXIExtensionColor {
            override fun convert(context: Context?, value: String): Int? {
                if (value == "gaiax_color_background_color") {
                    return if (GXDarkUtils.isDarkMode(context)) {
                        Color.GREEN
                    } else {
                        Color.RED
                    }
                }
                return null
            }

        })
    }

    @Test
    fun template_darkmode_background() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "refactor", "template_darkmode_background"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        kotlin.run {
            val rootView = GXTemplateEngine.instance.createView(templateItem, size)!!
            GXTemplateEngine.instance.bindData(rootView, templateData)
            Assert.assertEquals(
                Color.RED, (rootView.background as GXColorGradientDrawable).colors[0]
            )
        }

        context.resources.configuration.uiMode = Configuration.UI_MODE_NIGHT_YES

        kotlin.run {
            val rootView = GXTemplateEngine.instance.createView(templateItem, size)!!
            GXTemplateEngine.instance.bindData(rootView, templateData)
            Assert.assertEquals(
                Color.GREEN, (rootView.background as GXColorGradientDrawable).colors[0]
            )
        }

    }

    @Test
    fun template_cache_gxcss_reuse() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "refactor", "template_cache_gxcss_reuse"
        )

        val gxTemplateData1 = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["data"] = JSONObject().apply {
                this["title"] = "GaiaX"
            }
        })
        val gxTemplateData2 = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["data"] = JSONObject()
        })

        val gxView1 = GXTemplateEngine.instance.createView(templateItem, size)!!
        val gxView2 = GXTemplateEngine.instance.createView(templateItem, size)!!

        GXTemplateEngine.instance.bindDataOnlyNodeTree(gxView1, gxTemplateData1)
        GXTemplateEngine.instance.bindDataOnlyNodeTree(gxView2, gxTemplateData2)

        GXTemplateEngine.instance.bindDataOnlyViewTree(gxView1, gxTemplateData1)
        GXTemplateEngine.instance.bindDataOnlyViewTree(gxView2, gxTemplateData2)

        Assert.assertEquals(0F, (gxView1.child(0) as? GXView)?.radius?.get(0))

        Assert.assertEquals(7F.dpToPx(), (gxView2.child(0) as? GXView)?.radius?.get(0))

    }

}