package com.alibaba.gaiax.demo

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.js.proxy.GXJSEngineProxy
import com.alibaba.gaiax.utils.GXScreenUtils

class JSTemplateActivity : AppCompatActivity() {

    private var jsCustomsModuleDemoComponentId: Long = 0

    private val jsApiDemoTemplateName = "gx-with-js-api-demo"
    private val jsCustomModuleTemplateName = "gx-with-js"

    private val bizId = "assets_data_source/templates"

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
        // renderJSCustomModuleDemoTemplate(this, jsCustomModuleTemplateName)

        //Demo2：js内置方法一览
        renderJSApiDemoTemplate(this, jsApiDemoTemplateName)

        //
        sendNativeMessage()
    }

    private fun sendNativeMessage() {
        findViewById<Button>(R.id.template_btn).setOnClickListener {
            val nativeData: JSONObject = JSONObject()
            nativeData["key1"] = "Native Message Value1"
            val nativeMessageProtocol: JSONObject = JSONObject()
            nativeMessageProtocol["userData"] = nativeData
            nativeMessageProtocol["type"] = "CustomNotificationNameForNative"
            GXJSEngineProxy.instance.dispatchNativeEvent(nativeMessageProtocol)
        }

    }

    var customGXView: View? = null;
    var apiGXView: View? = null;

    private fun renderJSCustomModuleDemoTemplate(
        activity: JSTemplateActivity, templateId: String
    ) {
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
        customGXView = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(customGXView, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(customGXView, 0)

        // 注册JS组件
        GXJSEngineProxy.instance.registerComponentAndOnReady(customGXView)
    }

    private fun renderJSApiDemoTemplate(activity: JSTemplateActivity, templateId: String) {
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
        apiGXView = GXTemplateEngine.instance.createView(params, size)

        // 绑定数据
        GXTemplateEngine.instance.bindData(apiGXView, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_2).addView(apiGXView, 0)

        //
        GXJSEngineProxy.instance.registerComponentAndOnReady(apiGXView)

    }

    override fun onResume() {
        super.onResume()
        GXJSEngineProxy.instance.onShow(customGXView)
        GXJSEngineProxy.instance.onShow(apiGXView)
    }

    override fun onPause() {
        super.onPause()
        GXJSEngineProxy.instance.onHide(customGXView)
        GXJSEngineProxy.instance.onHide(apiGXView)
    }

    override fun onDestroy() {
        super.onDestroy()
        GXJSEngineProxy.instance.onDestroy(customGXView)
        GXJSEngineProxy.instance.onDestroy(apiGXView)
        GXJSEngineProxy.instance.unregisterComponent(customGXView)
        GXJSEngineProxy.instance.unregisterComponent(apiGXView)
    }
}