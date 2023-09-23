package com.alibaba.gaiax

import android.widget.TextView
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.analyze.GXAnalyze
import com.alibaba.gaiax.analyze.GXString
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.math.BigDecimal


@RunWith(AndroidJUnit4::class)
class GXCommonExpressionTest : GXBaseTest() {

    @Test
    fun template_env() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "expression",
            "template_env"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject())
        )

        Assert.assertEquals("GaiaXAndroid", (rootView.child(0) as TextView).text)
    }


    @Test
    fun template_env_ext() {
        GXRegisterCenter.instance.registerExtensionFunctionExpression(object :
            GXRegisterCenter.GXIExtensionFunctionExpression {
            override fun execute(functionName: String, params: LongArray): Long? {
                if (functionName == "gaiax" && params.size == 1) {
                    val value = GXAnalyze.wrapAsGXValue(params[0])
                    if (value is GXString) {
                        val envValue = value.getString()
                        if ("screenWidth".equals(envValue, ignoreCase = true)) {
                            return GXAnalyze.createValueFloat64(100F)
                        } else if ("screenHeight".equals(envValue, ignoreCase = true)) {
                            return GXAnalyze.createValueFloat64(200F)
                        }
                    }
                    return null
                }
                return null
            }

        })
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "expression",
            "template_env_ext"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject())
        )

        Assert.assertEquals(100F.toString(), (rootView.child(0) as TextView).text)
    }

    @Test
    fun template_double() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "expression",
            "template_double"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["title"] = 1.1
            })
        )

        Assert.assertEquals("1.1", (rootView.child(0) as TextView).text)
    }

    @Test
    fun template_bigdecimal() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context,
            "expression",
            "template_bigdecimal"
        )
        val rootView = GXTemplateEngine.instance.createView(
            templateItem,
            GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)
        )
        GXTemplateEngine.instance.bindData(
            rootView,
            GXTemplateEngine.GXTemplateData(JSONObject().apply {
                this["title"] = BigDecimal("1.1")
            })
        )

        Assert.assertEquals("1.1", (rootView.child(0) as TextView).text)
    }
}