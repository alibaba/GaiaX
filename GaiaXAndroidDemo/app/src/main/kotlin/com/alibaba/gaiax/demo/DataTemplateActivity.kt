package com.alibaba.gaiax.demo

import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.utils.GXScreenUtils

class DataTemplateActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data)

        renderTemplate1(this)
        renderTemplate2(this)
    }


    private fun renderTemplate1(activity: DataTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-subscribe-item")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                activity,
                "assets_data_source/data/subscribe-item.json"
            )
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)
    }

    private fun renderTemplate2(activity: DataTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-subscribe-item")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                activity,
                "assets_data_source/data/subscribe-item.json"
            )
        )
        templateData.dataListener = object : GXTemplateEngine.GXIDataListener {

            override fun onTextProcess(gxTextData: GXTemplateEngine.GXTextData): CharSequence? {
                if (gxTextData.templateItem?.templateId == "gx-subscribe-item" && gxTextData.nodeId == "title") {
                    val value = "两个光头的故事"
                    val spannableString = SpannableString(value)
                    spannableString.setSpan(
                        ForegroundColorSpan(Color.RED),
                        0,
                        value.length,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                    )
                    return spannableString
                }
                return null
            }
        }

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_2).addView(view, 0)
    }
}