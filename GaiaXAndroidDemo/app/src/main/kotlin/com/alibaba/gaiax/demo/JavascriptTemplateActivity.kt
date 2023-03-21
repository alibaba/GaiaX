package com.alibaba.gaiax.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.gaiaxjs.JSRenderDelegate
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.utils.GXScreenUtils
import com.youku.gaiax.js.GaiaXJS

/**
 *  @author: shisan.lms
 *  @date: 2022-11-17
 *  Description:
 */
class JavascriptTemplateActivity : AppCompatActivity() {

    private var jsId: Long = 0

    private var rootView:View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_normal_template)

        renderTemplate1(this)
    }

    private fun renderTemplate1(activity: JavascriptTemplateActivity) {
        val jsFuncDataPath = "assets_data_source/templates/gx-js-api-demo/index.js"
        val jsTestDataPath = "assets_data_source/templates/gx-with-js/index.js"
        val dataTemplateTest = "assets_data_source/data/gx-with-js.json"
        val dataTemplateFunc = "assets_data_source/data/gx-js-api-demo.json"
        val bizId = "assets_data_source/templates"
        val templateIdForFunction = "gx-js-api-demo"
        val templateIdForTest = "gx-with-js"
        // 初始化
        GXTemplateEngine.instance.init(activity)

        //GaiaXJS初始化
        GaiaXJS.instance.init(activity).initListener(object : GaiaXJS.Listener {
            override fun errorLog(data: JSONObject) {

            }
            override fun monitor(
                scene: String,
                biz: String,
                id: String,
                type: String,
                state: String,
                value: Long,
                jsModuleName: String,
                jsApiName: String,
                jsApiType: String
            ) {
//                GaiaXProxy.instance.monitor?.monitor(scene, biz, id, type, state, value)
            }
        })
        //GaiaXJS引擎启动
        GaiaXJS.instance.startEngine { }

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, bizId, templateIdForTest)

        val gxJSData = AssetsUtils.parseAssetsToString(activity, jsTestDataPath)
        Log.d("lms-13", "renderTemplate1: $gxJSData")
        // TODO: templateVersion是从GaiaXSDK获取吗？
        jsId = GaiaXJS.instance.registerComponent(bizId, templateIdForTest, "1", gxJSData.toString())

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)


        val dataJson = AssetsUtils.parseAssets(activity, dataTemplateTest)
        // 模板数据
        val templateData = GXTemplateEngine.GXTemplateData(dataJson)
        Log.d("lms-13", "renderTemplate1: $dataJson")
        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)
        val delegate = JSRenderDelegate()
        if (view != null) {
            delegate.initDelegate(view,jsId)
            this.rootView = view
        }
        GaiaXJS.instance.initRenderDelegate(delegate)
        // 绑定数据

        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)

        GaiaXJS.instance.onReadyComponent(jsId)
    }

    override fun onPause() {
        super.onPause()
        GaiaXJS.instance.onHiddenComponent(jsId)
    }

    override fun onDestroy() {
        super.onDestroy()
        if(this.rootView == null){
            GaiaXJS.instance.onDestroyComponent(jsId)
        }
    }
}