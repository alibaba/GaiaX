package com.alibaba.gaiax.demo.fastpreview

import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.demo.R
import com.alibaba.gaiax.demo.source.GXFastPreviewSource
import com.alibaba.gaiax.studio.GXClientToStudio
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx
import com.alibaba.gaiax.utils.GXScreenUtils

/**
 * 通过websocket链接gaia studio
 * websocket的通信能力来自三方库（已导入本地websocket）
 * https://github.com/0xZhangKe/WebSocketDemo
 */
class GXFastPreviewActivity : AppCompatActivity(), GXClientToStudio.GXSocketToStudioListener {

    companion object {
        private const val TAG = "[GaiaX]"
        const val GAIA_STUDIO_URL = "GAIA_STUDIO_URL"
    }


    lateinit var fastPreviewRoot: ViewGroup

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gaiax_fast_preview_activity)

        fastPreviewRoot = findViewById(R.id.fast_preview_layout)

        val url = intent.getStringExtra("GAIA_STUDIO_URL")

        val params = GXClientToStudio.instance.getParams(url)
        if (params == null) {
            finish()
            return
        }
        if ("auto" == params.getString("TYPE")) {
            GXClientToStudio.instance.gxSocketToStudioListener = this
            GXClientToStudio.instance.autoConnect(this, params);
        } else if ("manual" == params.getString("TYPE")) {
            GXClientToStudio.instance.manualConnect(this, params);
            finish()
        }
    }


    override fun onDestroy() {
        GXClientToStudio.instance.gxSocketToStudioListener = null
        super.onDestroy()
    }

    var gxTemplateItem: GXTemplateEngine.GXTemplateItem? = null
    var gxMeasureSize: GXTemplateEngine.GXMeasureSize? = null
    var gxTemplateData: GXTemplateEngine.GXTemplateData? = null

    private fun forceCreate() {
        try {
            val view = GXTemplateEngine.instance.createView(gxTemplateItem!!, gxMeasureSize!!)
            if (view != null) {
                GXTemplateEngine.instance.bindData(view, gxTemplateData!!)
                fastPreviewRoot.removeAllViews()
                fastPreviewRoot.addView(view, 0)
            }
        } catch (e: Exception) {
            e.printStackTrace()
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
        val constraintSize = templateData.getJSONObject("index.json")
            ?.getJSONObject("package")
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
