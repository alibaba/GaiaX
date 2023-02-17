package com.alibaba.gaiax.benchmark

import android.content.Context
import android.view.View
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.expression.GXAnalyzeWrapper
import com.alibaba.gaiax.render.view.GXViewKey
import com.alibaba.gaiax.render.view.basic.GXImageView
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.Charset

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class GaiaXBenchmark {

    var context: Context = InstrumentationRegistry.getInstrumentation().context

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    @Before
    fun before() {
        GXTemplateEngine.instance.init(context)
        GXRegisterCenter.instance.registerExtensionExpression(GXExtensionMultiVersionExpression())
        GXRegisterCenter.instance.registerExtensionViewSupport(
            GXViewKey.VIEW_TYPE_IMAGE, GXImageView::class.java
        )
        GXRegisterCenter.instance.registerExtensionCompatibility(
            GXRegisterCenter.GXExtensionCompatibilityConfig().apply {
                this.isPreventContainerDataSourceThrowException = true
                this.isPreventIconFontTypefaceThrowException = true
                this.isPreventAccessibilityThrowException = true
                this.isPreventFitContentThrowException = true
            })
    }

    @Test
    fun profiler_dianbo_item_single() {

        val bizId = "templates"
        val templateId = "profiler_dianbo_item_single"

        val data = readJsonFromAssets("data/$templateId.json")

        benchmarkRule.measureRepeated {
            val gxTemplateItem = GXTemplateEngine.GXTemplateItem(
                context, bizId, templateId
            )
            val gxMeasureSize = GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)

            GXTemplateEngine.instance.prepareView(gxTemplateItem, gxMeasureSize)

            val gxTemplateContext = GXTemplateEngine.instance.createViewOnlyNodeTree(
                gxTemplateItem, gxMeasureSize, null
            )!!

            val gxView = GXTemplateEngine.instance.createViewOnlyViewTree(gxTemplateContext)!!

            val gxTemplateData = GXTemplateEngine.GXTemplateData(data)

            GXTemplateEngine.instance.bindDataOnlyNodeTree(gxView, gxTemplateData, gxMeasureSize)

            GXTemplateEngine.instance.bindDataOnlyViewTree(gxView, gxTemplateData, gxMeasureSize)
        }
    }

    @Test
    fun profiler_dianbo_item_single_v2() {

        val bizId = "templates"
        val templateId = "profiler_dianbo_item_single_v2"

        val data = readJsonFromAssets("data/$templateId.json")

        benchmarkRule.measureRepeated {
            val gxTemplateItem = GXTemplateEngine.GXTemplateItem(
                context, bizId, templateId
            )
            val gxMeasureSize = GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)

            GXTemplateEngine.instance.prepareView(gxTemplateItem, gxMeasureSize)

            val gxTemplateContext = GXTemplateEngine.instance.createViewOnlyNodeTree(
                gxTemplateItem, gxMeasureSize, null
            )!!

            val gxView = GXTemplateEngine.instance.createViewOnlyViewTree(gxTemplateContext)!!

            val gxTemplateData = GXTemplateEngine.GXTemplateData(data)

            GXTemplateEngine.instance.bindDataOnlyNodeTree(gxView, gxTemplateData, gxMeasureSize)

            GXTemplateEngine.instance.bindDataOnlyViewTree(gxView, gxTemplateData, gxMeasureSize)
        }
    }

    @Test
    fun profiler_uper_scroll() {
        val bizId = "templates"
        val templateId = "profiler_uper_scroll"
        val data = readJsonFromAssets("data/$templateId.json")

        benchmarkRule.measureRepeated {
            val gxTemplateItem = GXTemplateEngine.GXTemplateItem(
                context, bizId, templateId
            )
            val gxMeasureSize = GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)

            GXTemplateEngine.instance.prepareView(gxTemplateItem, gxMeasureSize)

            val gxTemplateContext = GXTemplateEngine.instance.createViewOnlyNodeTree(
                gxTemplateItem, gxMeasureSize, null
            )!!

            val gxView = GXTemplateEngine.instance.createViewOnlyViewTree(gxTemplateContext)!!

            val gxTemplateData = GXTemplateEngine.GXTemplateData(data)

            GXTemplateEngine.instance.bindDataOnlyNodeTree(gxView, gxTemplateData, gxMeasureSize)

            GXTemplateEngine.instance.bindDataOnlyViewTree(gxView, gxTemplateData, gxMeasureSize)

            val scroll = GXTemplateEngine.instance.getGXViewById(gxView, "profiler_uper_scroll")
            scroll.executeRecyclerView()
        }
    }

    private fun readJsonFromAssets(path: String) = JSONObject.parseObject(
        context.assets.open(path).reader(Charset.forName("utf-8")).readText()
    )

    private fun View?.executeRecyclerView() {
        if (this is GXContainer) {
            this.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST)
            this.layout(0, 0, 375F.dpToPx().toInt(), 750F.dpToPx().toInt())
        }
    }

    @Test
    fun profiler_analyze() {
        val templateData = JSONObject().apply {
            this["title"] = "GaiaX"
        }

        benchmarkRule.measureRepeated {
            GXAnalyzeWrapper("10000").value(templateData)

            GXAnalyzeWrapper("10000 + 1").value(templateData)

            GXAnalyzeWrapper("12.100 + 1.12").value(templateData)

            GXAnalyzeWrapper("10000 - 1").value(templateData)

            GXAnalyzeWrapper("10000 / 1").value(templateData)

            GXAnalyzeWrapper("10000 * 1").value(templateData)

            GXAnalyzeWrapper("10000 % 10000").value(templateData)

            GXAnalyzeWrapper("(1 + 1)/2").value(templateData)

            GXAnalyzeWrapper("(1 + 1)/(2-1)").value(templateData)

            GXAnalyzeWrapper("\$\$").value(templateData)

            GXAnalyzeWrapper("\$data.title").value(templateData)

            GXAnalyzeWrapper("\$data.title + 1000").value(templateData)

            GXAnalyzeWrapper("\$data.title + 'GaiaX").value(templateData)

            GXAnalyzeWrapper("10%5").value(templateData)

            GXAnalyzeWrapper("true ? 1 : 2").value(templateData)

            GXAnalyzeWrapper("\$data.title == 'GaiaX ? \$data.title % 5 : 1'").value(templateData)

            GXAnalyzeWrapper("size('1234')").value(templateData)

            GXAnalyzeWrapper("true ? 1 : 2").value(templateData)

            GXAnalyzeWrapper("(1+1)>1 ? 1>0 : 2<3").value(templateData)

            GXAnalyzeWrapper("5%3 == 2").value(templateData)

            GXAnalyzeWrapper("+1").value(templateData)

            GXAnalyzeWrapper("-1").value(templateData)

            GXAnalyzeWrapper("!false").value(templateData)

            GXAnalyzeWrapper("null != null").value(templateData)

            GXAnalyzeWrapper("'string' > 2").value(templateData)

            GXAnalyzeWrapper("true || false").value(templateData)

        }
    }
}