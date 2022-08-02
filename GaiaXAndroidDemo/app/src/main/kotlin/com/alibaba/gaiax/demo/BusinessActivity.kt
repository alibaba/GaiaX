package com.alibaba.gaiax.demo

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.demo.utils.GaiaXExpression
import com.alibaba.gaiax.template.GXIExpression
import com.alibaba.gaiax.utils.GXScreenUtils

class BusinessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business)
        renderTemplate()
    }

    class GXExtensionExpression : GXRegisterCenter.GXIExtensionExpression {

        override fun create(value: Any): GXIExpression {
            return GaiaXExpression.create(value)
        }

        override fun isTrue(value: Any?): Boolean {
            return GaiaXExpression.isCondition(value)
        }

    }

    private fun renderTemplate() {
        // 初始化
        GXTemplateEngine.instance.init(this)

        GXRegisterCenter.instance
            .registerExtensionExpression(GXExtensionExpression())

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(
            this,
            "business_data_source/templates",
            "pop-super-trumpet"
        )

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                this,
                "business_data_source/data/pop-super-trumpet.json"
            )
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.root).addView(view, 0)

    }
}