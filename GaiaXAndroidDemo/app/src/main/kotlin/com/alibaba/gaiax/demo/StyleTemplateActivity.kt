package com.alibaba.gaiax.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.utils.GXScreenUtils

class StyleTemplateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_style_template)

        findViewById<AppCompatButton>(R.id.rebind).setOnClickListener {
            if (view != null && templateData != null) {
                GXTemplateEngine.instance.bindData(view!!, templateData!!)
            }
        }

        renderTemplate1(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        GXTemplateEngine.instance.destroyView(findViewById<LinearLayoutCompat>(R.id.template_1))
    }

    var view: View? = null
    var templateData: GXTemplateEngine.GXTemplateData? = null

    private fun renderTemplate1(activity: StyleTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(
            activity, "assets_data_source/templates", "gx-style-backdrop-filter"
        )

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        templateData = GXTemplateEngine.GXTemplateData(JSONObject().apply {
            this["blur_text"] = "我是文本我是文本我是文本我是文本我是文本"
//                this["img"] = "https://gimg2.baidu.com/image_search/src=http%3A%2F%2Fup.enterdesk.com%2Fphoto%2F2011-10-14%2Fenterdesk.com-2E8A38D0891116035E78DD713EED9637.jpg&refer=http%3A%2F%2Fup.enterdesk.com&app=2002&size=f9999,10000&q=a80&n=0&g=0n&fmt=auto?sec=1666781857&t=595349c20a2e34ceddbd48b130339fbf"
            this["img"] =
                "https://gw.alicdn.com/imgextra/i1/O1CN01KbLBlr1SmrmfDeShB_!!6000000002290-2-tps-1500-756.png"
        })

        // 创建模板View
        view = GXTemplateEngine.instance.createView(params, size)!!

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData!!)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)
    }
}
