package com.alibaba.gaiax.benchmark

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
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
    }

    @Test
    fun profiler_dianbo_item_single_measure_createview_and_binddata() {

        val bizId = "templates"
        val templateId = "profiler_dianbo_item_single"

        val data = readJsonFromAssets("data/$templateId.json")

        benchmarkRule.measureRepeated {
            val gxTemplateItem = GXTemplateEngine.GXTemplateItem(
                context, bizId, templateId
            )
            val gxMeasureSize = GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
            val rootView = GXTemplateEngine.instance.createView(
                gxTemplateItem, gxMeasureSize
            )
            val gxTemplateData = GXTemplateEngine.GXTemplateData(data)
            GXTemplateEngine.instance.bindData(rootView, gxTemplateData)
        }
    }

    @Test
    fun profiler_dianbo_item_single_measure_createview() {

        val bizId = "templates"
        val templateId = "profiler_dianbo_item_single"

        benchmarkRule.measureRepeated {
            val gxTemplateItem = GXTemplateEngine.GXTemplateItem(
                context, bizId, templateId
            )
            val gxMeasureSize = GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
            // prepare
            GXTemplateEngine.instance.prepareView(gxTemplateItem, gxMeasureSize)
            val rootView = GXTemplateEngine.instance.createView(
                gxTemplateItem, gxMeasureSize
            )
        }
    }

    @Test
    fun profiler_dianbo_item_single_measure_binddata() {

        val bizId = "templates"
        val templateId = "profiler_dianbo_item_single"

        val data = readJsonFromAssets("data/$templateId.json")
        val gxTemplateItem = GXTemplateEngine.GXTemplateItem(
            context, bizId, templateId
        )
        val gxMeasureSize = GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
        val rootView = GXTemplateEngine.instance.createView(
            gxTemplateItem, gxMeasureSize
        )
        benchmarkRule.measureRepeated {
            val gxTemplateData = GXTemplateEngine.GXTemplateData(data)
            GXTemplateEngine.instance.bindData(rootView, gxTemplateData)
        }
    }

    private fun readJsonFromAssets(path: String) = JSONObject.parseObject(
        context.assets.open(path).reader(Charset.forName("utf-8")).readText()
    )
}