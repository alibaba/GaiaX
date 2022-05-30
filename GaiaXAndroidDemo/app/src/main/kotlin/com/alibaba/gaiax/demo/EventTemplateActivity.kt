package com.alibaba.gaiax.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.utils.GXScreenUtils

class EventTemplateActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "EventTemplateActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_event_template)

        renderTemplate1(this)
    }


    private fun renderTemplate1(activity: EventTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "templates", "gx-content-uper")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData =
            GXTemplateEngine.GXTemplateData(AssetsUtils.parseAssets(activity, "data/uper.json"))
        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                Log.d(TAG, "onGestureEvent() called with: gxGesture = $gxGesture")
            }

            override fun onScrollEvent(gxScroll: GXTemplateEngine.GXScroll) {
                super.onScrollEvent(gxScroll)
                Log.d(TAG, "onScrollEvent() called with: gxScroll = $gxScroll")
            }
        }

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)
    }
}