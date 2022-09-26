package com.alibaba.gaiax.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.utils.GXScreenUtils

class StyleTemplateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_style_template)

        renderTemplate1(this)
    }

    private fun renderTemplate1(activity: StyleTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(
            activity,
            "assets_data_source/templates",
            "gx-style-backdrop-filter"
        )

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            JSONObject().apply {
                this["blur_text"] = "我是文本我是文本我是文本我是文本我是文本"
                this["img"] = "https://t7.baidu.com/it/u=376303577,3502948048&fm=193&f=GIF"
            }
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)
    }
}
