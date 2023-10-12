package com.alibaba.gaiax.demo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.utils.GXScreenUtils

/**
 *  @author: shisan.lms
 *  @date: 2022-11-17
 *  Description:
 */
class JSTemplateActivity : AppCompatActivity() {

    private var jsApiDemoId: Long = 0
    private var jsCustomsModuleDemoId: Long = 0


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

        sendNativeMessage()
    }

    private fun sendNativeMessage() {
        findViewById<Button>(R.id.template_btn).setOnClickListener {
            val nativeData: JSONObject = JSONObject()
            nativeData["key1"] = "Native Message Value1"
            val nativeMessageProtocol: JSONObject = JSONObject()
            nativeMessageProtocol["userData"] = nativeData
            nativeMessageProtocol["type"] = "CustomNotificationNameForNative"
            GXJSEngine.Component.dispatcherNativeMessageEventToJS(nativeMessageProtocol)
        }

    }

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
        val view = GXTemplateEngine.instance.createView(params, size)

        if (view != null) {
            jsCustomsModuleDemoId = GXJSEngine.Component.registerComponent(
                bizId, jsCustomModuleTemplateName, "1", getJsFileByTemplateId(templateId), view
            )
        }

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)

        GXJSEngine.Component.onReady(jsCustomsModuleDemoId)
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
        val view = GXTemplateEngine.instance.createView(params, size)

        if (view != null) {
            jsApiDemoId = GXJSEngine.Component.registerComponent(
                bizId, jsApiDemoTemplateName, "1", getJsFileByTemplateId(templateId), view
            )
        }

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_2).addView(view, 0)

        GXJSEngine.Component.onReady(jsApiDemoId)
        GXJSEngine.Component.onDestroy(jsApiDemoId)
    }

    override fun onPause() {
        super.onPause()
        GXJSEngine.Component.onHide(jsApiDemoId)
        GXJSEngine.Component.onHide(jsCustomsModuleDemoId)
    }

    override fun onDestroy() {
        super.onDestroy()
        GXJSEngine.Component.unregisterComponent(jsApiDemoId)
        GXJSEngine.Component.unregisterComponent(jsCustomsModuleDemoId)
    }
}