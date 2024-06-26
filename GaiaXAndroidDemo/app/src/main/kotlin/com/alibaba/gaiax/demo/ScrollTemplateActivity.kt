package com.alibaba.gaiax.demo

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.recyclerview.widget.RecyclerView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.render.utils.GXGravitySmoothScroller
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.template.GXTemplateKey
import com.alibaba.gaiax.utils.GXScreenUtils


class ScrollTemplateActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "ContainerTemplateActivi"
    }

    class GXExtensionScroll : GXRegisterCenter.GXIExtensionScroll {

        override fun scrollIndex(
            gxTemplateContext: GXTemplateContext,
            container: GXContainer,
            extend: JSONObject?
        ) {
            Log.d(TAG, "scrollIndex() called with: extend = $extend")

            val recyclerView = container as RecyclerView
            val holdingOffset = extend?.getBooleanValue(GXTemplateKey.GAIAX_DATABINDING_HOLDING_OFFSET) ?: false
            if (holdingOffset) {
                val scrollGravity = extend?.getString(GXTemplateKey.GAIAX_SCROLL_GRAVITY)
                val scrollIndex = extend?.getInteger(GXTemplateKey.GAIAX_SCROLL_INDEX) ?: -1
                if (scrollIndex != -1) {
                    if (scrollGravity != null) {
                        // 默认是平滑滚动的
                        val scroller = when (scrollGravity) {
                            "left" -> GXGravitySmoothScroller(recyclerView.context, GXGravitySmoothScroller.ALIGN_LEFT)
                            "right" -> GXGravitySmoothScroller(recyclerView.context, GXGravitySmoothScroller.ALIGN_RIGHT)
                            "center" -> GXGravitySmoothScroller(recyclerView.context, GXGravitySmoothScroller.ALIGN_CENTER)
                            else -> GXGravitySmoothScroller(recyclerView.context, GXGravitySmoothScroller.ALIGN_ANY)
                        }
                        scroller.targetPosition = scrollIndex
                        recyclerView.layoutManager?.startSmoothScroll(scroller)
                    } else {
                        val smooth = extend?.getBooleanValue(GXTemplateKey.GAIAX_SCROLL_ANIMATED) ?: false
                        if (smooth) {
                            recyclerView.smoothScrollToPosition(scrollIndex)
                        } else {
                            recyclerView.scrollToPosition(scrollIndex)
                        }
                    }
                } else {
                    // no process
                }
            } else {
                // when again bind data, should be scroll to position 0
                recyclerView.scrollToPosition(0)
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scroll_template)

        GXRegisterCenter.instance.registerExtensionScroll(GXExtensionScroll())
        renderTemplate1(this)
    }

    private fun renderTemplate1(activity: ScrollTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)


        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-content-uper-scroll2")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val data = AssetsUtils.parseAssets(activity, "assets_data_source/data/scroll-uper.json")
        val templateData = GXTemplateEngine.GXTemplateData(data)

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!

        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {

            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
                gxGesture.index?.let {
                    Log.d(TAG, "onGestureEvent() called with: gxGesture = ${gxGesture.index}")
                    if (it >= 0) {
                        val extend = templateData.data.getJSONObject("extend")
                        extend["scroll-index"] = it
                        GXTemplateEngine.instance.bindData(view, templateData)
                    }
                }
            }
        }

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)


        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)
        findViewById<AppCompatButton>(R.id.btn_scroll_index).setOnClickListener {

            findViewById<AppCompatEditText>(R.id.et_scroll_index).text.toString().toIntOrNull()?.let {
                val extend = templateData.data.getJSONObject("extend")
                extend["scroll-index"] = it
                GXTemplateEngine.instance.bindData(view, templateData)
            }
        }


    }

}
