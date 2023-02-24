package com.alibaba.gaiax

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXMockUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.charset.Charset


@RunWith(AndroidJUnit4::class)
class GXBenchmarkTest : GXBaseTest() {

    @Test
    fun profiler_immutable_ranking_item() {
        val templateItem = GXTemplateEngine.GXTemplateItem(
            GXMockUtils.context, "benchmark", "profiler_immutable_ranking_item"
        )
        val path = "benchmark/profiler_immutable_ranking_item.json"
        val data = readJsonFromAssets(path)
        val gxTemplateData = GXTemplateEngine.GXTemplateData(data)

        repeat(2) {
            val rootView = GXTemplateEngine.instance.createView(
                templateItem, GXTemplateEngine.GXMeasureSize(375F.dpToPx(), null)
            )
            GXTemplateEngine.instance.bindData(rootView, gxTemplateData)

            Assert.assertEquals("东宫", GXTemplateEngine.instance.getGXViewById(rootView,"title").text())
            Assert.assertEquals("55集全", GXTemplateEngine.instance.getGXViewById(rootView,"right").text())
            Assert.assertEquals("55集全", GXTemplateEngine.instance.getGXViewById(rootView,"right-score").text())
        }
    }


    private fun readJsonFromAssets(path: String) = JSONObject.parseObject(
        context.assets.open(path).reader(Charset.forName("utf-8")).readText()
    )


}