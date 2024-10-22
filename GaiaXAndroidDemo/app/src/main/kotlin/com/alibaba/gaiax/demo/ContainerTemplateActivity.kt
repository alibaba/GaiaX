package com.alibaba.gaiax.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXScreenUtils

class ContainerTemplateActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "ContainerTemplate"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_container_template)

//        renderTemplate1(this)
//        renderTemplate2(this)
//        renderTemplate3(this)
        renderTemplate4(this)
//        renderTemplate5(this)
//        renderTemplate6(this)
//        renderTemplate7(this)
    }

    var render1View: View? = null
    var render2View: View? = null
    var render3View: View? = null
    var render4View: View? = null
    var render5View: View? = null
    var render6View: View? = null
    var render7View: View? = null

    override fun onDestroy() {
        GXTemplateEngine.instance.destroyView(render1View)
        GXTemplateEngine.instance.destroyView(render2View)
        GXTemplateEngine.instance.destroyView(render3View)
        GXTemplateEngine.instance.destroyView(render4View)
        GXTemplateEngine.instance.destroyView(render5View)
        GXTemplateEngine.instance.destroyView(render6View)
        GXTemplateEngine.instance.destroyView(render7View)
        super.onDestroy()
    }

    private fun renderTemplate1(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-content-uper-scroll")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(AssetsUtils.parseAssets(activity, "assets_data_source/data/uper.json"))

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!
        render1View = view
        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)
    }

    private fun renderTemplate2(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-recommend-scroll")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                activity,
                "assets_data_source/data/recommend.json"
            )
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!
        render2View = view
        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_2).addView(view, 0)
    }

    private fun renderTemplate3(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-mutable-scroll")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                activity,
                "assets_data_source/data/multi-scroll.json"
            )
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!
        render3View = view
        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_3).addView(view, 0)
    }

    private fun renderTemplate4(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-content-uper-grid")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData =
            GXTemplateEngine.GXTemplateData(AssetsUtils.parseAssets(activity, "assets_data_source/data/uper.json"))

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!
        render4View = view
        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_4).addView(view, 0)
    }

    private fun renderTemplate5(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-grid-with-footer")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val templateData =
            GXTemplateEngine.GXTemplateData(AssetsUtils.parseAssets(activity, "assets_data_source/data/grid-with-footer.json"))

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!
        render5View = view
        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_5).addView(view, 0)
    }


    private fun renderTemplate6(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-slider")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), 100F.dpToPx())

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(
            AssetsUtils.parseAssets(
                activity,
                "assets_data_source/data/gx-slider-item-data.json"
            )
        )

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!
        render6View = view
        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_6).addView(view, 0)
    }

    private fun renderTemplate7(activity: ContainerTemplateActivity) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, "assets_data_source/templates", "gx-slider-multi-type")

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), 100F.dpToPx())

        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(AssetsUtils.parseAssets(activity, "assets_data_source/data/gx-slider-multi-type-data.json"))
        templateData.eventListener = object : GXTemplateEngine.GXIEventListener {
            override fun onGestureEvent(gxGesture: GXTemplateEngine.GXGesture) {
                super.onGestureEvent(gxGesture)
            }

            override fun onScrollEvent(gxScroll: GXTemplateEngine.GXScroll) {
                super.onScrollEvent(gxScroll)
                Log.d(TAG, "onScrollEvent() called with: gxScroll type=${gxScroll.type} position=${gxScroll.position}")
            }

            override fun onAnimationEvent(gxAnimation: GXTemplateEngine.GXAnimation) {
                super.onAnimationEvent(gxAnimation)
            }
        }

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)!!
        render7View = view
        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_7).addView(view, 0)
    }
}
