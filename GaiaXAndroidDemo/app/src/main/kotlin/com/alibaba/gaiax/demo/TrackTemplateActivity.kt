package com.alibaba.gaiax.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.utils.GXScreenUtils
import com.alibaba.gaiax.utils.setValueExt

class TrackTemplateActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "TrackTemplateActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_track_template)

        findViewById<AppCompatButton>(R.id.on_appear).setOnClickListener {
            targetView?.let { it1 -> GXTemplateEngine.instance.onAppear(it1) }
        }

        findViewById<AppCompatButton>(R.id.on_disappear).setOnClickListener {
            targetView?.let { it1 -> GXTemplateEngine.instance.onDisappear(it1) }
        }

        renderTemplate1(this)
    }

    var isFollowed = false

    var targetView: View? = null

    private fun renderTemplate1(activity: TrackTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(
            activity,
            "assets_data_source/templates",
            "gx-content-uper"
        )

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val data = AssetsUtils.parseAssets(activity, "assets_data_source/data/uper.json")
        val templateData = GXTemplateEngine.GXTemplateData(data)

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                if (gxGesture.nodeId == "follow") {
                    if (isFollowed) {
                        data.setValueExt("data.isFollowed", false)
                        GXTemplateEngine.instance.bindData(view, templateData)
                        isFollowed = false
                    } else {
                        data.setValueExt("data.isFollowed", true)
                        GXTemplateEngine.instance.bindData(view, templateData)
                        isFollowed = true
                    }
                }
            }
        }

        templateData.trackListener = object : GXTemplateEngine.GXITrackListener {
            override fun onManualClickTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                Log.d(TAG, "onManualClickTrackEvent() called with: gxTrack = $gxTrack")
            }

            override fun onManualExposureTrackEvent(gxTrack: GXTemplateEngine.GXTrack) {
                Log.d(TAG, "onManualExposureTrackEvent() called with: gxTrack = $gxTrack")
            }
        }

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)

        targetView = view
    }
}