package com.alibaba.gaiax.demo.devtools

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.DefaultLifecycleObserver
import com.alibaba.gaiax.demo.R
import com.alibaba.gaiax.demo.fastpreview.GXFastPreviewActivity
import com.alibaba.gaiax.demo.fastpreview.GXQRCodeActivity
import com.alibaba.gaiax.studio.GXClientToStudioMultiType
import com.alibaba.gaiax.studio.IDevTools
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern

/**
 *  @author: shisan.lms
 *  @date: 2023-02-02
 *  Description:
 */
class DevTools : DefaultLifecycleObserver, IDevTools {
    companion object {
        val TAG = "devtools"

        val instance by lazy {
            return@lazy DevTools()
        }
    }

    private var devtoolsContext: Context? = null

    private var scanResult: String = ""

    private var currentPreviewMode = GXClientToStudioMultiType.PREVIEW_NONE

    private var currentJSMode = GXClientToStudioMultiType.JS_DEFAULT

    fun createDevToolsFloatWindow(context: Context) {
        devtoolsContext = context
        EasyFloat.with(context)
            .setLayout(R.layout.layout_dev_tools) {

                val windowWidth = it.layoutParams.width
                val windowHeight = it.layoutParams.height

                var logoView = it.findViewById<ImageView>(R.id.window_gaia_logo)
                logoView.setOnClickListener { view ->
                    view.visibility = View.INVISIBLE
                    EasyFloat.updateFloat(TAG, width = windowWidth, height = windowHeight)
                }

                it.findViewById<AppCompatButton>(R.id.window_btn_scan).setOnClickListener { view ->
                    openQRCodeActivity(context)
                }

                it.findViewById<AppCompatButton>(R.id.window_btn_fast_preview)
                    .setOnClickListener { view ->
                        launchAction(context) { context ->  openFastPreviewType(context) }
                    }

                it.findViewById<AppCompatButton>(R.id.window_btn_push_preview)
                    .setOnClickListener { view ->
                        launchPushPreviewType()
                    }

                it.findViewById<AppCompatButton>(R.id.window_btn_js_debug)
                    .setOnClickListener { view ->
                        launchJsDebugType()
                    }

                it.findViewById<AppCompatButton>(R.id.window_btn_cancel_dev)
                    .setOnClickListener { view ->
                        disconnectStudioMultiType()
                    }

                it.findViewById<AppCompatButton>(R.id.window_btn_close_window)
                    .setOnClickListener { view ->
                        foldWindowToSmall(logoView)
                    }

            }
            .setShowPattern(ShowPattern.FOREGROUND)
            .setDragEnable(true)
            .setTag(TAG)
            .registerCallback {
                createResult { isCreated, msg, view -> }
                show { }
                hide { }
                dismiss { }
                touchEvent { view, motionEvent -> }
                drag { view, motionEvent -> }
                dragEnd { }
            }
            .show()
        GXClientToStudioMultiType.instance.init(context)
    }

    fun dismissDevTools() {
        EasyFloat.dismiss(TAG)
    }

    fun connectStudioMultiType(result: String) {
        Log.d(TAG, "connectStudioMultiType: $result")
        scanResult = result
        val params = GXClientToStudioMultiType.instance.getParams(scanResult)
        if (params == null) {
            Toast.makeText(devtoolsContext, "地址解析失败，请刷新二维码", Toast.LENGTH_SHORT).show()
            return
        }
        GXClientToStudioMultiType.instance.setDevTools(this)
        GXClientToStudioMultiType.instance.manualConnect(devtoolsContext!!, params)

    }

    private fun openQRCodeActivity(context: Context) {
        val intent = Intent(context, GXQRCodeActivity::class.java)
        intent.putExtra(TAG, TAG)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)

    }

    private fun launchAction(context: Context, action: (Context) -> Unit) {
        val connectedState = GXClientToStudioMultiType.instance.isGaiaStudioConnected() ?: false
        if (connectedState) {
            action(context)
        } else {
            //未连接先扫码连接
            Toast.makeText(context, "请先连接GaiaStudio", Toast.LENGTH_SHORT).show()
            openQRCodeActivity(context)
        }
    }

    private fun openFastPreviewType(context: Context) {
        //开启FastPreviewActivity
        val intent = Intent(devtoolsContext, GXFastPreviewActivity::class.java)
        intent.putExtra(GXFastPreviewActivity.GAIA_STUDIO_MODE, GXFastPreviewActivity.GAIA_STUDIO_MODE_MULTI)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        //修改DevTools状态
        currentPreviewMode = GXClientToStudioMultiType.PREVIEW_AUTO
        GXClientToStudioMultiType.instance.sendMsgForGetTemplateData("")
    }

    private fun launchPushPreviewType() {
        currentPreviewMode = GXClientToStudioMultiType.PREVIEW_MANUAL
        GXClientToStudioMultiType.instance.sendMsgForGetTemplateData("")
    }

    private fun launchJsDebugType() {
        currentJSMode = GXClientToStudioMultiType.JS_BREAKPOINT
    }

    private fun disconnectStudioMultiType() {
        GXClientToStudioMultiType.instance.sendMsgForDisconnect()
    }

    private fun foldWindowToSmall(view: ImageView?) {
        view?.visibility = View.VISIBLE
        EasyFloat.updateFloat(TAG, width = 150, height = 150)
    }

    override fun getPreviewCurrentMode(): String {
        return currentPreviewMode
    }

    override fun getJSCurrentMode(): String {
        return currentJSMode
    }
}