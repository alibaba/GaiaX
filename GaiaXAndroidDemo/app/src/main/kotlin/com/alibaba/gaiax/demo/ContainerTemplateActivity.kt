package com.alibaba.gaiax.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXScreenUtils

class ContainerTemplateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_template)

        renderTemplate1(this)
        renderTemplate2(this)
        renderTemplate3(this)
        renderTemplate4(this)
        renderTemplate5(this)
        renderTemplate6(this)
    }

    private fun renderTemplate1(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params =
            GXTemplateEngine.GXTemplateItem(activity, "templates", "gx-content-uper-scroll")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData =
            GXTemplateEngine.GXTemplateData(AssetsUtils.parseAssets(activity, "data/uper.json"))

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)
    }

    private fun renderTemplate2(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "templates", "gx-recommend-scroll")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                activity,
                "data/recommend.json"
            )
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_2).addView(view, 0)
    }

    private fun renderTemplate3(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "templates", "gx-mutable-scroll")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                activity,
                "data/multi-scroll.json"
            )
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_3).addView(view, 0)
    }

    private fun renderTemplate4(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "templates", "gx-content-uper-grid")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData =
            GXTemplateEngine.GXTemplateData(AssetsUtils.parseAssets(activity, "data/uper.json"))

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_4).addView(view, 0)
    }

    private fun renderTemplate5(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "templates", "gx-grid-with-footer")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData =
            GXTemplateEngine.GXTemplateData(AssetsUtils.parseAssets(activity, "data/grid-with-footer.json"))

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_5).addView(view, 0)
    }

    private fun renderTemplate6(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "templates", "gx-slider")

        // 模板绘制尺寸
        val size =
            GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), 100F.dpToPx())

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                activity,
                "data/gx-slider-item-data.json"
            )
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_6).addView(view, 0)
    }
}
