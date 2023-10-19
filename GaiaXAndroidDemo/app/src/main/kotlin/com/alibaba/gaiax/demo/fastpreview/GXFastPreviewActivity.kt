package com.alibaba.gaiax.demo.fastpreview

import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.R
import com.alibaba.gaiax.demo.source.GXFastPreviewSource
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


    override fun onDestroy() {
        GXStudioClient.instance.fastPreviewListener = null
        super.onDestroy()
    }

    var gxTemplateItem: GXTemplateEngine.GXTemplateItem? = null
    var gxMeasureSize: GXTemplateEngine.GXMeasureSize? = null
    var gxTemplateData: GXTemplateEngine.GXTemplateData? = null
    var gxView: View? = null
    var gxViewComponentId: Long? = null

    private fun forceCreate() {
        gxTemplateData?.let { gxTemplateData ->
            gxTemplateItem?.let { gxTemplateItem ->
                gxMeasureSize?.let { gxMeasureSize ->

                    // 创建视图
                    gxView = GXTemplateEngine.instance.createView(gxTemplateItem, gxMeasureSize)
                    gxView?.let { gxView ->

                        // 绑定数据
                        GXTemplateEngine.instance.bindData(gxView, gxTemplateData)
                        fastPreviewRoot.addView(gxView, 0)

                        // 获取模板信息
                        val gxTemplateInfo = GXTemplateEngine.instance.getGXTemplateInfo(gxTemplateItem)
                        if (gxTemplateInfo.isJsExist) {
                            // 下一帧执行JS
                            gxView.post {

                                // 注册容器
//                                gxViewComponentId = GXJSEngine.Component.registerComponent(
//                                    gxTemplateItem.bizId,
//                                    gxTemplateItem.templateId,
//                                    gxTemplateItem.templateVersion,
//                                    gxTemplateInfo.js,
//                                    gxView
//                                )
//                                GXJSEngine.Component.onReady()
                            }
                        }
                    }
                }
            }
        }
    }

    fun OnCreate(view: View) {
        forceCreate()
    }

    fun OnVisible(view: View) {

    }

    fun OnInvisible(view: View) {

    }

    fun OnDestroy(view: View) {
        fastPreviewRoot.removeAllViews()
    }

    fun OnReuse(view: View) {
        try {
            GXTemplateEngine.instance.bindData(view, gxTemplateData!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

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

        forceCreate()
    }
}
