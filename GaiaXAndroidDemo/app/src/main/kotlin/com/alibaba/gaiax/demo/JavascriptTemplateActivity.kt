package com.alibaba.gaiax.demo

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.gaiaxjs.JSRenderDelegate
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.utils.GXScreenUtils
import com.youku.gaiax.js.GaiaXJSManager

/**
 *  @author: shisan.lms
 *  @date: 2022-11-17
 *  Description:
 */
class JavascriptTemplateActivity : AppCompatActivity() {

    private var jsApiDemoId: Long = 0
    private var jsCustomsModuleDemoId: Long = 0

    private var rootView: View? = null

    val jsApiDemoTemplateName = "gx-with-js-api-demo"
    val jsCustomModuleTemplateName = "gx-with-js"

    val bizId = "assets_data_source/templates"

    private fun getJsFileByTemplateId(pathName: String): String {
        val jsDemoPath = "assets_data_source/templates/$pathName/index.js"
        return AssetsUtils.parseAssetsToString(this, jsDemoPath) ?: ""
    }

    private fun getTemplateData(fileName: String): String {
        return "assets_data_source/data/$fileName.json"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_js_template)
        //Demo1：业务使用方自定义module
        renderJSCustomModuleDemoTemplate(this, jsCustomModuleTemplateName)
        //Demo2：js内置方法一览
        renderJSApiDemoTemplate(this, jsApiDemoTemplateName)
    }

    private fun renderJSCustomModuleDemoTemplate(activity: JavascriptTemplateActivity, templateId: String) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, bizId, templateId)

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val dataJson = AssetsUtils.parseAssets(activity, getTemplateData(templateId))
        val templateData = GXTemplateEngine.GXTemplateData(dataJson)

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        jsCustomsModuleDemoId = GaiaXJSManager.instance.registerComponent(
            bizId,
            jsCustomModuleTemplateName,
            "1",
            getJsFileByTemplateId(templateId)
        )

        val delegate = JSRenderDelegate()
        if (view != null) {
            delegate.initDelegate(view, jsCustomsModuleDemoId)
            this.rootView = view
        }
        GaiaXJSManager.instance.initRenderDelegate(delegate)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)

        val component = GaiaXJSManager.instance.getComponentByInstanceId(jsCustomsModuleDemoId)
        component?.onReady()

    }

    private fun renderJSApiDemoTemplate(activity: JavascriptTemplateActivity, templateId: String) {
        // 初始化
        GXTemplateEngine.instance.init(activity)

        // 模板参数
        val params = GXTemplateEngine.GXTemplateItem(activity, bizId, templateId)

        // 模板绘制尺寸
        val size = GXTemplateEngine.GXMeasureSize(GXScreenUtils.getScreenWidthPx(this), null)

        // 模板数据
        val dataJson = AssetsUtils.parseAssets(activity, getTemplateData(templateId))
        val templateData = GXTemplateEngine.GXTemplateData(dataJson)

        // 创建模板View
        val view = GXTemplateEngine.instance.createView(params, size)

        jsApiDemoId = GaiaXJSManager.instance.registerComponent(
            bizId,
            jsApiDemoTemplateName,
            "",
            getJsFileByTemplateId(templateId)
        )

        val delegate = JSRenderDelegate()
        if (view != null) {
            delegate.initDelegate(view, jsApiDemoId)
            this.rootView = view
        }
        GaiaXJSManager.instance.initRenderDelegate(delegate)

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_2).addView(view, 0)

        val component = GaiaXJSManager.instance.getComponentByInstanceId(jsApiDemoId)
        component?.onReady()

    }

    override fun onPause() {
        super.onPause()
        GaiaXJSManager.instance.onHiddenComponent(jsApiDemoId)
    }

    override fun onDestroy() {
        super.onDestroy()
        GaiaXJSManager.instance.onDestroyComponent(jsApiDemoId)
        GaiaXJSManager.instance.unregisterComponent(jsApiDemoId)
        //todo 直接执行停止Engine可能会导致部分异步线程任务未执行完毕
//        GaiaXJSManager.instance.stopEngine()
    }
}