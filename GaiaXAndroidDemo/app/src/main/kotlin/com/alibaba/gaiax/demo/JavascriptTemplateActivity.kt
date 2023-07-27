package com.alibaba.gaiax.demo

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.LinearLayoutCompat
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.utils.AssetsUtils
import com.alibaba.gaiax.utils.GXScreenUtils
import com.youku.gaiax.js.GXJSComponentDelegate
import com.youku.gaiax.js.GXJSEngineFactory

/**
 *  @author: shisan.lms
 *  @date: 2022-11-17
 *  Description:
 */
class JavascriptTemplateActivity : AppCompatActivity() {

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
            GXJSComponentDelegate.instance.dispatcherNativeMessageEvent(nativeMessageProtocol)
        }

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

        if (view != null) {
            jsCustomsModuleDemoId = GXJSComponentDelegate.instance.registerComponent(
                bizId,
                jsCustomModuleTemplateName,
                "1",
                getJsFileByTemplateId(templateId),
                view
            )
        }

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_1).addView(view, 0)

        val component = GXJSComponentDelegate.instance.getComponentByInstanceId(jsCustomsModuleDemoId)
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

        if (view != null) {
            jsApiDemoId = GXJSComponentDelegate.instance.registerComponent(
                bizId,
                jsApiDemoTemplateName,
                "1",
                getJsFileByTemplateId(templateId),
                view
            )
        }

        // 绑定数据
        GXTemplateEngine.instance.bindData(view, templateData)

        // 插入模板View
        findViewById<LinearLayoutCompat>(R.id.template_2).addView(view, 0)

        val component = GXJSComponentDelegate.instance.getComponentByInstanceId(jsApiDemoId)
        component?.onReady()
        component?.onReady()
        component?.onReady()
        component?.onDestroy()
    }

    override fun onPause() {
        super.onPause()
        GXJSComponentDelegate.instance.onHiddenComponent(jsApiDemoId)
        GXJSComponentDelegate.instance.onHiddenComponent(jsCustomsModuleDemoId)
    }

    override fun onDestroy() {
        super.onDestroy()
        GXJSComponentDelegate.instance.unregisterComponent(jsApiDemoId)
        GXJSComponentDelegate.instance.unregisterComponent(jsCustomsModuleDemoId)
        GXJSEngineFactory.instance
        //todo 直接执行停止Engine可能会导致部分异步线程任务未执行完毕
//        GXJSComponentDelegate.instance.stopEngine()
    }
}