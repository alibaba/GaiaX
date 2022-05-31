package com.alibaba.gaiax

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import android.support.test.InstrumentationRegistry
import app.visly.stretch.Dimension
import app.visly.stretch.Size
import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONArray
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.analyze.GXAnalyze
import com.alibaba.gaiax.analyze.GXArray
import com.alibaba.gaiax.analyze.GXMap
import com.alibaba.gaiax.analyze.GXString
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.template.GXDataBinding
import com.alibaba.gaiax.template.GXIExpression
import com.alibaba.gaiax.template.GXScrollConfig
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.GXMockUtils
import com.alibaba.gaiax.utils.GXMockUtils.context
import com.alibaba.gaiax.utils.GXScreenUtils
import com.alibaba.gaiax.utils.getAnyExt
import org.junit.After
import org.junit.Before


open class GXBaseTest {

    companion object {

        val largeFontScale = 1.2F
        val responsiveLayoutScale = 1.2F

        private const val TAG = "GXBaseTest"
        var MOCK_SCREEN_WIDTH = 1080F.dpToPx()
    }

    val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

    var context: Context = InstrumentationRegistry.getInstrumentation().context

    @Before
    fun before() {
        GXTemplateEngine.instance.init(GXMockUtils.context)

        GXRegisterCenter.instance
            .registerExtensionExpression(GXExtensionExpression())
            .registerExtensionDataBinding(GXExtensionDataBinding())
            .registerExtensionColor(GXProcessorColor())
            .registerExtensionSize(GXExtensionSize())
            .registerExtensionDynamicProperty(GXExtensionDynamicProperty())
            .registerExtensionStaticProperty(GXExtensionStaticProperty())
            .registerExtensionScroll(GXExtensionScroll())

    }

    @After
    fun after() {
        GXRegisterCenter.instance.reset()
    }

    class GXExtensionScroll : GXRegisterCenter.GXIExtensionScroll {
        override fun convert(
            propertyName: String,
            gxTemplateContext: GXTemplateContext,
            scrollConfig: GXScrollConfig
        ): Any? {
            Log.d(
                TAG,
                "convertProcessing() called with: propertyName = $propertyName, context = $gxTemplateContext, scrollConfig = $scrollConfig"
            )
            if (propertyName == GXTemplateKey.GAIAX_CUSTOM_PROPERTY_VIEW_PORT_WIDTH) {
                val responsiveRule = scrollConfig.data.getString("responsive-rule") ?: return null
                val leftMargin = scrollConfig.edgeInsets.left
                val rightMargin = scrollConfig.edgeInsets.right
                val lineSpacing = scrollConfig.itemSpacing
                if (responsiveRule == "response_layout_rule_5") {
                    return (GXScreenUtils.getScreenWidthPx(gxTemplateContext.context) - (leftMargin + rightMargin) - 2 * lineSpacing) / 2.5F
                } else if (responsiveRule == "response_layout_rule_2") {
                    return (GXScreenUtils.getScreenWidthPx(gxTemplateContext.context) - (leftMargin + rightMargin) - 2 * lineSpacing) / 3
                }
            }
            return null
        }

    }

    class GXExtensionDataBinding : GXRegisterCenter.GXIExtensionDataBinding {

        override fun create(value: Any): GXDataBinding? {
            Log.d(TAG, "createProcessing() called with: data = $value")
            return null
        }
    }

    class GXExtensionDynamicProperty : GXRegisterCenter.GXIExtensionDynamicProperty {

        override fun convert(params: GXRegisterCenter.GXIExtensionDynamicProperty.GXParams): Any? {
            Log.d(TAG, "convertProcessing() called with: params = $params")
            if (params.propertyName == GXTemplateKey.FLEXBOX_SIZE || params.propertyName == GXTemplateKey.FLEXBOX_MIN_SIZE || params.propertyName == GXTemplateKey.FLEXBOX_MAX_SIZE) {
                @Suppress("UNCHECKED_CAST")
                val newValue = params.value as Size<Dimension>
                val fontName = params.cssStyle?.fontSize?.name
                if (fontName == "gaiax_font") {
                    val height = newValue.height
                    if (height is Dimension.Points) {
                        newValue.height = Dimension.Points(height.points * largeFontScale)
                    }
                    return params.value
                }
            } else if (params.propertyName == GXTemplateKey.STYLE_FONT_LINE_HEIGHT) {
                val fontName = params.cssStyle?.fontSize?.name
                if (fontName == "gaiax_font") {
                    val newValue = params.value as Float
                    return newValue * largeFontScale
                }
            }
            return null
        }
    }

    class GXExtensionStaticProperty : GXRegisterCenter.GXIExtensionStaticProperty {

        override fun convert(params: GXRegisterCenter.GXIExtensionStaticProperty.GXParams): Any? {
            if (params.propertyName == GXTemplateKey.STYLE_FONT_FAMILY && params.value == "unknow_fontfamily") {
                return Typeface.createFromAsset(
                    context.assets,
                    "fontfamily3.ttf"
                )
            }
            return null
        }
    }

    class GXExtensionSize : GXRegisterCenter.GXIExtensionSize {
        override fun create(value: String): Float? {
            Log.d(TAG, "createProcessing() called with: size = $value")
            if ("gaiax_font" == value) {
                return 20F
            }
            if (value == "gaiax_dimen") {
                return 100F
            }
            return null
        }

        override fun convert(value: Float): Float? {
            Log.d(TAG, "convertProcessing() called with: value = $value")
            return responsiveLayoutScale
        }

    }

    class GXProcessorColor : GXRegisterCenter.GXIExtensionColor {

        override fun convert(color: String): Int? {
            Log.d(TAG, "convertProcessing() called with: color = $color")
            if (color == "gaiax_color") {
                return Color.RED
            }
            return null
        }
    }

    class GXExtensionExpression : GXRegisterCenter.GXIExtensionExpression {

        override fun create(value: Any): GXIExpression {
            Log.d(TAG, "createProcessing() called with: expression = $value")
            return GXAnalyzeWrapper(value)
        }

        override fun isTrue(value: Any?): Boolean {
            return value == true
        }

        class GXAnalyzeWrapper(private val expression: Any) : GXIExpression {
            override fun expression(): Any {
                return expression
            }

            override fun value(templateData: JSON?): Any? {
                return analyze.getResult(expression, templateData)
            }

            companion object {
                val analyze = GXAnalyze()

                init {
                    analyze.initComputeExtend(object : GXAnalyze.IComputeExtend {

                        /**
                         * 用于处理取值逻辑
                         */
                        override fun computeValueExpression(valuePath: String, source: Any?): Long {
                            if (valuePath == "$$") {
                                if (source is JSONArray) {
                                    return GXAnalyze.createValueArray(source)
                                } else if (source is JSONObject) {
                                    return GXAnalyze.createValueMap(source)
                                }
                            }
                            if (source is JSONObject) {
                                when (val value = source.getAnyExt(valuePath)) {
                                    is JSONArray -> {
                                        return GXAnalyze.createValueArray(value)
                                    }
                                    is JSONObject -> {
                                        return GXAnalyze.createValueMap(value)
                                    }
                                    is Boolean -> {
                                        return GXAnalyze.createValueBool(value)
                                    }
                                    is String -> {
                                        return GXAnalyze.createValueString(value)
                                    }
                                    is Int -> {
                                        return GXAnalyze.createValueFloat64(value.toFloat())
                                    }
                                    is Float -> {
                                        return GXAnalyze.createValueFloat64(value)
                                    }
                                    null -> {
                                        return GXAnalyze.createValueNull()
                                    }
                                }
                            }
                            return 0L
                        }

                        /**
                         * 用于处理函数逻辑
                         */
                        override fun computeFunctionExpression(
                            functionName: String,
                            params: LongArray
                        ): Long {
                            if (functionName == "size" && params.size == 1) {
                                when (val value = GXAnalyze.wrapAsGXValue(params[0])) {
                                    is GXString -> {
                                        value.getString()?.let {
                                            return GXAnalyze.createValueFloat64(it.length.toFloat())
                                        }
                                    }
                                    is GXMap -> {
                                        (value.getValue() as? JSONObject)?.let {
                                            return GXAnalyze.createValueFloat64(it.size.toFloat())
                                        }
                                    }
                                    is GXArray -> {
                                        (value.getValue() as? JSONArray)?.let {
                                            return GXAnalyze.createValueFloat64(it.size.toFloat())
                                        }
                                    }
                                }
                            } else if (functionName == "env") {
                            }
                            return 0L
                        }
                    })
                }
            }
        }
    }
}
