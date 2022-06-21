package com.alibaba.gaiax.fastpreview

import android.content.Context
import android.net.ConnectivityManager
import android.util.Log
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.data.cache.GXTemplateInfoSource
import com.alibaba.gaiax.template.GXTemplate
import com.alibaba.gaiax.template.GXTemplateKey

class GaiaXFastPreview {

    class ManualPushSource : GXRegisterCenter.GXIExtensionTemplateSource {

        private val cache = mutableMapOf<String, GXTemplate>()

        override fun getTemplate(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplate? {
            return cache[templateItem.templateId]
        }

        fun addTemplate(templateId: String, data: JSONObject) {
            GXTemplateInfoSource.instance.clean()
            val layer = data.getString(GXTemplateKey.GAIAX_INDEX_JSON) ?: ""
            val css = data.getString(GXTemplateKey.GAIAX_INDEX_CSS) ?: ""
            val dataBind = data.getString(GXTemplateKey.GAIAX_INDEX_DATABINDING) ?: ""
            val js = data.getString(GXTemplateKey.GAIAX_INDEX_JS) ?: ""
            cache[templateId] = GXTemplate(templateId, "manualpush", -1, layer, css, dataBind, js)
        }
    }

    class FastPreviewSource : GXRegisterCenter.GXIExtensionTemplateSource {

        private val cache = mutableMapOf<String, GXTemplate>()

        override fun getTemplate(templateItem: GXTemplateEngine.GXTemplateItem): GXTemplate? {
            return cache[templateItem.templateId]
        }

        fun addTemplate(templateId: String, data: JSONObject) {
            GXTemplateInfoSource.instance.clean()
            val layer = data.getString(GXTemplateKey.GAIAX_INDEX_JSON) ?: ""
            val css = data.getString(GXTemplateKey.GAIAX_INDEX_CSS) ?: ""
            val dataBind = data.getString(GXTemplateKey.GAIAX_INDEX_DATABINDING) ?: ""
            val js = data.getString(GXTemplateKey.GAIAX_INDEX_JS) ?: ""
            cache[templateId] = GXTemplate(templateId, "fastpreview", -1, layer, css, dataBind, js)
        }
    }

    private var manualPushSource = ManualPushSource()
    private var fastPreviewSource = FastPreviewSource()

    init {
        GXRegisterCenter.instance
            .registerExtensionTemplateSource(manualPushSource, 101)
            .registerExtensionTemplateSource(fastPreviewSource, 102)
    }

    private var socketHelper: GaiaXSocket = GaiaXSocket()

    private var currentAddress: String? = null
    private var currentTemplateId: String? = null
    private var currentType: String = "auto"
    private var isWaitDisconnectMsgThenConnectGaiaStudio = false

    private val socketListener: GaiaXSocket.Listener = object : GaiaXSocket.Listener {

        override fun onSocketConnectFail() {
            Log.e(TAG, "onSocketConnectFail() called")
        }

        override fun onSocketDisconnected() {
            Log.e(TAG, "onSocketDisconnected() called")
            if (isWaitDisconnectMsgThenConnectGaiaStudio) {
                isWaitDisconnectMsgThenConnectGaiaStudio = false
                toConnectGaiaStudio()
            }
        }

        override fun onSocketConnected() {
            Log.e(TAG, "onSocketConnected() called")
            sendInitMsg(currentType)
        }

        override fun onManualPushTemplateDataChanged(templateId: String, template: JSONObject) {
            Log.e(TAG, "onManualPushTemplateDataChanged() called with: templateId = $templateId")
            manualPushSource.addTemplate(templateId, template)
        }

        override fun onFastPreviewInit() {
            Log.e(TAG, "onFastPreviewInit() called")
            if (currentTemplateId != null) {
                socketHelper.sendObtainTemplateDataMsg(currentTemplateId!!)
            }
        }

        override fun onManualPushInit() {
            Log.e(TAG, "onManualPushInit() called")
        }

        override fun onFastPreviewDataChanged(templateId: String, template: JSONObject) {
            fastPreviewSource.addTemplate(templateId, template)
            Log.e(TAG, "onFastPreviewDataChanged() called with: templateId = [$templateId]")
            var constraintSize = JSONObject()
            try {
                constraintSize = template.getJSONObject("index.json")?.getJSONObject("package")
                    ?.getJSONObject("constraint-size") ?: JSONObject()
            } catch (e: Exception) {
                e.printStackTrace()
            }
            listener?.notifyUpdateUI(template, templateId, constraintSize)
        }
    }

    private var listener: Listener? = null

    fun addListener(listener: Listener?) {
        this.listener = listener
    }

    fun removeListener(listener: Listener?) {
        this.listener = null
    }

    fun manualConnect(context: Context, params: JSONObject) {
        if (isConnectVpn(context)) {
            Log.e(TAG, "manualConnect: 请断开手机VPN后重试")
            return
        }
        Log.e(TAG, "onlyConnect() called with: params = [$params]")
        val targetUrl = params.getString("URL")
        val type = params.getString("TYPE")
        tryToConnectGaiaStudio(targetUrl, null, type)
    }

    fun autoConnect(context: Context, params: JSONObject) {
        if (isConnectVpn(context)) {
            Log.e(TAG, "manualConnect: 请断开手机VPN后重试")
            return
        }
        Log.e(TAG, "execute() called with: params = [$params]")
        val targetUrl = params.getString("URL")
        val templateId = params.getString("TEMPLATE_ID")
        val type = params.getString("TYPE")
        tryToConnectGaiaStudio(targetUrl, templateId, type)
    }

    private fun isConnectVpn(context: Context): Boolean {
        val allNetworkInfo =
            (context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).allNetworkInfo
        for (i in allNetworkInfo.indices) {
            val networkInfo = allNetworkInfo[i]
            if (networkInfo.type == ConnectivityManager.TYPE_VPN && networkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    private fun tryToConnectGaiaStudio(address: String, templateId: String?, type: String) {
        Log.e(
            TAG,
            "tryToConnectGaiaStudio() called with: address = [$address], templateId = [$templateId], type = [$type]"
        )
        val tmpAddress = currentAddress
        currentType = type
        currentAddress = address
        currentTemplateId = templateId
        if (tmpAddress != null && tmpAddress != address) {
            // 地址不同，需要先断开链接，再尝试连接GaiaStudio
            isWaitDisconnectMsgThenConnectGaiaStudio = true
            socketHelper.disconnectToServer()
        } else {
            // 链接相同，直接连接GaiaStudio
            toConnectGaiaStudio()
        }
    }

    private fun toConnectGaiaStudio() {
        if (!socketHelper.isConnected) {
            socketHelper.setListener(socketListener)
            socketHelper.connectToServer(currentAddress)
        } else {
            sendInitMsg(currentType)
        }
    }

    private fun toManualPushGaiaXTemplate(templateData: JSONObject?): JSONObject {
        val result = JSONObject()
        result["index.json"] = templateData!!.getString("index.json")
        result["index.js"] = templateData.getString("index.js")
        result["index.css"] = templateData.getString("index.css")
        result["index.databinding"] = templateData.getString("index.databinding")
        return result
    }

    private fun sendInitMsg(type: String) {
        if (socketHelper.isManualPush(type)) {
            socketHelper.sendMsgWithManualPushInit()
        } else if (socketHelper.isFastPreview(type)) {
            socketHelper.sendMsgWithFastPreviewInit()
        }
    }

    interface Listener {
        fun notifyUpdateUI(template: JSONObject, templateId: String, constraintSize: JSONObject)
    }

    companion object {
        const val TAG = "[GaiaX][FastPreview]"

        val instance by lazy {
            GaiaXFastPreview()
        }
    }

}