package com.alibaba.gaiax.demo.fastpreview

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.R
import com.alibaba.gaiax.demo.source.GXFastPreviewSource
import com.alibaba.gaiax.js.GXJSEngine
import com.alibaba.gaiax.js.proxy.GXJSEngineProxy
import com.alibaba.gaiax.studio.GXStudioClient
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXScreenUtils

/**
 * 通过websocket链接gaia studio
 * websocket的通信能力来自三方库（已导入本地websocket）
 * https://github.com/0xZhangKe/WebSocketDemo
 */
class GXFastPreviewActivity : AppCompatActivity(), GXStudioClient.IFastPreviewListener {

    companion object {
        private const val TAG = "[GaiaX]"
        const val GAIA_STUDIO_URL = "GAIA_STUDIO_URL"
        const val GAIA_STUDIO_MODE = "GAIA_STUDIO_MODE"
        const val GAIA_STUDIO_MODE_MULTI = "MULTI"
    }


    lateinit var fastPreviewRoot: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gaiax_fast_preview_activity)

        findViewById<View>(R.id.create).setOnClickListener {
            create()
        }

        findViewById<View>(R.id.show).setOnClickListener {
            show()
        }

        findViewById<View>(R.id.hide).setOnClickListener {
            hide()
        }

        findViewById<View>(R.id.destroy).setOnClickListener {
            destroy()
        }

        findViewById<View>(R.id.reuse).setOnClickListener {
            reuse()
        }

        fastPreviewRoot = findViewById(R.id.fast_preview_layout)

        val mode = intent.getStringExtra(GAIA_STUDIO_MODE)

        if (!TextUtils.isEmpty(mode) && mode == GAIA_STUDIO_MODE_MULTI) {
            GXStudioClient.instance.fastPreviewListener = this
            return
        } else {
            val url = intent.getStringExtra("GAIA_STUDIO_URL")

            val params = GXStudioClient.instance.getParams(url)
            if (params == null) {
                finish()
                return
            }
            if ("auto" == params.getString("TYPE")) {
                GXStudioClient.instance.fastPreviewListener = this
                GXStudioClient.instance.autoConnect(this, params);
            } else if ("manual" == params.getString("TYPE")) {
                GXStudioClient.instance.manualConnect(this, params);
                finish()
            }
        }
    }

    private fun show() {
        Log.d(TAG, "show() called")
        GXTemplateEngine.instance.onAppear(gxView)
        GXJSEngineProxy.instance.onShow(gxView)
    }

    private fun hide() {
        Log.d(TAG, "hide() called")
        GXTemplateEngine.instance.onDisappear(gxView)
        GXJSEngineProxy.instance.onHide(gxView)
    }

    private fun reuse() {
        Log.d(TAG, "reuse() called")

        gxTemplateData?.let { it1 -> GXTemplateEngine.instance.bindData(gxView, it1) }

        // 执行生命周期变化
        GXJSEngineProxy.instance.onReuse(gxView)
    }

    private fun destroy() {
        Log.d(TAG, "destroy() called")

        GXJSEngineProxy.instance.onDestroy(gxView)

        // 解除容器注册
        GXJSEngineProxy.instance.unregisterComponent(gxView)

        fastPreviewRoot.removeAllViews()
    }

    override fun onDestroy() {
        destroy()
        GXStudioClient.instance.fastPreviewListener = null
        super.onDestroy()
    }

    private fun create() {
        fastPreviewRoot.removeAllViews()

        Log.d(TAG, "create() called")
        gxTemplateData?.let { gxTemplateData ->
            gxTemplateItem?.let { gxTemplateItem ->
                gxMeasureSize?.let { gxMeasureSize ->

                    // 创建视图
                    gxView = GXTemplateEngine.instance.createView(gxTemplateItem, gxMeasureSize)

                    gxView?.let {
                        // 绑定数据
                        GXTemplateEngine.instance.bindData(gxView, gxTemplateData)

                        // 将数据加入页面中
                        fastPreviewRoot.addView(gxView, 0)

                        // 获取模板信息
                        val gxTemplateInfo = GXTemplateEngine.instance.getGXTemplateInfo(gxTemplateItem)
                        if (gxTemplateInfo.isJsExist) {

                            // 设置JS异常监听
                            GXJSEngineProxy.instance.jsExceptionListener =
                                object : GXJSEngine.IJsExceptionListener {
                                    override fun exception(data: JSONObject) {
                                        Log.d(TAG, "exception() called with: data = $data")
                                    }
                                }

                            // 注册容器
                            GXJSEngineProxy.instance.registerComponentAndOnReady(gxView)
                        }
                    }
                }
            }
        }
    }

    var gxTemplateItem: GXTemplateEngine.GXTemplateItem? = null
    var gxMeasureSize: GXTemplateEngine.GXMeasureSize? = null
    var gxTemplateData: GXTemplateEngine.GXTemplateData? = null
    var gxView: View? = null

    override fun onAddData(templateId: String, templateData: JSONObject) {
        GXFastPreviewSource.instance.addTemplate(templateId, templateData)
    }

    override fun onUpdate(templateId: String, templateData: JSONObject) {
        val constraintSize = templateData.getJSONObject("index.json")?.getJSONObject("package")
            ?.getJSONObject("constraint-size")

        var data = JSONObject()
        val indexMock = templateData.getJSONObject("index.mock")
        if (indexMock != null) {
            data = indexMock
        }

        val activity = this

        val width = if (constraintSize?.containsKey("width") == true) {
            constraintSize.getFloat("width").dpToPx()
        } else {
            GXScreenUtils.getScreenWidthPx(this)
        }
        val height = if (constraintSize?.containsKey("height") == true) {
            constraintSize.getFloat("height").dpToPx()
        } else {
            null
        }

        gxTemplateItem = GXTemplateEngine.GXTemplateItem(activity, "fastpreview", templateId!!)
        gxMeasureSize = GXTemplateEngine.GXMeasureSize(width, height)
        gxTemplateData = GXTemplateEngine.GXTemplateData(data)

        create()
    }

}
