package com.alibaba.gaiax

import android.content.Context
import android.content.res.Configuration
import android.graphics.Color
import androidx.appcompat.app.AppCompatDelegate
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.drawable.GXColorGradientDrawable
import com.alibaba.gaiax.utils.GXDarkUtils
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class GXDarkModeTest : GXBaseTest() {

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
            GXMockUtils.context,
            "darkmode",
            "template_darkmode_background"
        )
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        kotlin.run {
            val rootView = GXTemplateEngine.instance.createView(templateItem, size)!!
            GXTemplateEngine.instance.bindData(rootView, templateData)
            Assert.assertEquals(
                Color.RED,
                (rootView.background as GXColorGradientDrawable).colors[0]
            )
        }

        context.resources.configuration.uiMode = Configuration.UI_MODE_NIGHT_YES

        kotlin.run {
            val rootView = GXTemplateEngine.instance.createView(templateItem, size)!!
            GXTemplateEngine.instance.bindData(rootView, templateData)
            Assert.assertEquals(
                Color.GREEN,
                (rootView.background as GXColorGradientDrawable).colors[0]
            )
        }

    }

}