package com.alibaba.gaiax.fastpreview

import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.utils.GXScreenUtils
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.util.regex.Pattern

/**
 * 通过websocket链接gaia studio
 * websocket的通信能力来自三方库（已导入本地websocket）
 * https://github.com/0xZhangKe/WebSocketDemo
 */
class GaiaXFastPreviewActivity : AppCompatActivity(), GaiaXFastPreview.Listener {

    companion object {
        private const val TAG = "[GaiaX]"
        const val GAIA_STUDIO_URL = "GAIA_STUDIO_URL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gaiax_fast_preview_activity)
        val url = intent.getStringExtra("GAIA_STUDIO_URL")
        val params = getParams(url)
        if (params == null) {
            finish()
            return
        }
        if ("auto" == params.getString("TYPE")) {
            GaiaXFastPreview.instance.addListener(this);
            GaiaXFastPreview.instance.autoConnect(this, params);
        } else if ("manual" == params.getString("TYPE")) {
            GaiaXFastPreview.instance.manualConnect(this, params);
            finish()
        }
    }

    private fun getParams(url: String?): JSONObject? {
        if (url == null || TextUtils.isEmpty(url)) {
            return null
        }
        val finalUrl = try {
            URLDecoder.decode(url, "UTF-8")
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            return null
        }
        Log.e(TAG, "getParams() called with:  finalUrl = [$finalUrl]")
        val regexUrl = "[ws://]+[\\d+.\\d+.\\d+.\\d+]+[:\\d+]*"
        val pattern = Pattern.compile(regexUrl)
        val matcher = pattern.matcher(finalUrl)
        if (matcher.find()) {
            //局域网下IP
            val targetUrl = matcher.group()
            val templateId = parseTemplateId(finalUrl)
            val type = parseConnectType(finalUrl)
            val result = JSONObject()
            result["URL"] = targetUrl
            result["TYPE"] = type
            result["TEMPLATE_ID"] = templateId
            Log.e(TAG, "getParams() called with:  result = [$result]")
            return result
        } else {
            Log.e(TAG, "Can not find web url through regex.")
        }
        return null
    }

    private fun parseConnectType(url: String): String {
        try {
            return url.split("&".toRegex()).toTypedArray()[2].split("=".toRegex()).toTypedArray()[1]
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    private fun parseTemplateId(url: String): String {
        try {
            return url.split("&".toRegex()).toTypedArray()[1].split("=".toRegex()).toTypedArray()[1]
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return ""
    }

    override fun onDestroy() {
        GaiaXFastPreview.instance.removeListener(this);
        super.onDestroy()
    }

    override fun notifyUpdateUI(
        template: JSONObject,
        templateId: String,
        constraintSize: JSONObject
    ) {
        val fastPreviewRoot = findViewById<ViewGroup>(R.id.fast_preview_layout)
        fastPreviewRoot.removeAllViews()

        var data = JSONObject()
        val indexMock = template.getJSONObject("index.mock")
        if (indexMock != null) {
            data = indexMock
        }

        val activity = this

        val params = GXTemplateEngine.GXTemplateItem(activity, "fastpreview", templateId)

        val width = if (constraintSize.containsKey("width")) {
            constraintSize.getFloat("width")
        } else {
            GXScreenUtils.getScreenWidthPx(this)
        }
        val height = if (constraintSize.containsKey("height")) {
            constraintSize.getFloat("height")
        } else {
            null
        }

        val size = GXTemplateEngine.GXMeasureSize(width, height)
        val templateData = GXTemplateEngine.GXTemplateData(data)
        val view = GXTemplateEngine.instance.createView(params, size)
        GXTemplateEngine.instance.bindData(view, templateData)
        fastPreviewRoot.addView(view, 0)
    }
}