package com.alibaba.gaiax.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXScreenUtils

class NormalTemplateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_template)

        renderTemplate1(this)
        renderTemplate2(this)
        renderTemplate3(this)
    }

    private fun renderTemplate1(activity: NormalTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(
            activity, "assets_data_source/templates", "gx-vertical-item"
        )

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(100F.dpToPx(), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                activity, "assets_data_source/data/vertical-item.json"
            )
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)
    }

    private fun renderTemplate2(activity: NormalTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val gxTemplateItem = GXTemplateEngine.GXTemplateItem(
            activity, "assets_data_source/templates", "gx-horizontal-item"
        )

        // 模板绘制尺寸
        val gxMeasureSize =
            GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                activity, "assets_data_source/data/horizontal-item.json"
            )
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(gxTemplateItem, gxMeasureSize)!!

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_2).addView(view, 0)
    }

    private fun renderTemplate3(activity: NormalTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params =
            GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-progress")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(JSONObject())

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_3).addView(view, 0)
    }
}
