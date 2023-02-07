package com.alibaba.gaiax

import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import app.visly.stretch.Dimension
import app.visly.stretch.Size
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.data.cache.GXTemplateInfoSource
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.template.GXScrollConfig
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.GXExtensionMultiVersionExpression
import com.alibaba.gaiax.utils.GXMockUtils
import com.alibaba.gaiax.utils.GXScreenUtils
import org.junit.After
import org.junit.Before


open class GXBaseTest {

    companion object {
        private const val TAG = "GXBaseTest"
        const val largeFontScale = 1.2F
        const val responsiveLayoutScale = 1.2F
        var MOCK_SCREEN_WIDTH = 1080F.dpToPx()
    }

    val size = GXTemplateEngine.GXMeasureSize(MOCK_SCREEN_WIDTH, null)

    var context: Context = InstrumentationRegistry.getInstrumentation().context

    @Before
    open fun before() {
        GXTemplateEngine.instance.init(GXMockUtils.context)

        // 清除缓存，否则会导致模板复用，引发单测失败
        GXTemplateInfoSource.instance.clean()

        GXRegisterCenter.instance.registerExtensionExpression(GXExtensionMultiVersionExpression())
    }

    @After
    fun after() {
        GXRegisterCenter.instance.reset()
    }

    class GXExtensionScroll : GXRegisterCenter.GXIExtensionScroll {

        override fun scrollIndex(
            gxTemplateContext: GXTemplateContext, container: GXContainer, extend: JSONObject?
        ) {
            // scroll item to position
            gxTemplateContext.templateData?.scrollIndex?.let { scrollPosition ->
                if (scrollPosition <= 0) {
                    val holdingOffset =
                        extend?.getBooleanValue(GXTemplateKey.GAIAX_DATABINDING_HOLDING_OFFSET)
                            ?: false
                    if (holdingOffset) {
                        // no process
                    } else {
                        // when again bind data, should be scroll to position 0
                        container.layoutManager?.scrollToPosition(0)
                    }
                }
                // if (scrollPosition > 0)
                else {
                    container.layoutManager?.scrollToPosition(scrollPosition)
                }
            }
        }

        override fun convert(
            propertyName: String, gxTemplateContext: GXTemplateContext, scrollConfig: GXScrollConfig
        ): Any? {
            Log.d(
                TAG,
                "convertProcessing() called with: propertyName = $propertyName, context = $gxTemplateContext, scrollConfig = $scrollConfig"
            )
            if (propertyName == GXTemplateKey.GAIAX_CUSTOM_PROPERTY_VIEW_PORT_WIDTH) {
                val responsiveRule = scrollConfig.data.getString("responsive-rule") ?: return null
                val padding = gxTemplateContext.rootNode?.getPaddingRect()
                val leftMargin = padding?.left ?: 0
                val rightMargin = padding?.right ?: 0
                val lineSpacing = scrollConfig.itemSpacingFinal
                if (responsiveRule == "response_layout_rule_5") {
                    return (GXScreenUtils.getScreenWidthPx(gxTemplateContext.context) - (leftMargin + rightMargin) - 2 * lineSpacing) / 2.5F
                } else if (responsiveRule == "response_layout_rule_2") {
                    return (GXScreenUtils.getScreenWidthPx(gxTemplateContext.context) - (leftMargin + rightMargin) - 2 * lineSpacing) / 3
                }
            }
            return null
        }

    }

    class GXExtensionDynamicProperty : GXRegisterCenter.GXIExtensionDynamicProperty {

        override fun convert(params: GXRegisterCenter.GXIExtensionDynamicProperty.GXParams): Any? {
            Log.d(TAG, "convertProcessing() called with: params = $params")
            if (params.propertyName == GXTemplateKey.FLEXBOX_SIZE || params.propertyName == GXTemplateKey.FLEXBOX_MIN_SIZE || params.propertyName == GXTemplateKey.FLEXBOX_MAX_SIZE) {
                @Suppress("UNCHECKED_CAST") val newValue = params.value as Size<Dimension>
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

        override fun convert(context: Context?, color: String): Int? {
            Log.d(TAG, "convertProcessing() called with: color = $color")
            if (color == "gaiax_color") {
                return Color.RED
            }
            return null
        }
    }

}
