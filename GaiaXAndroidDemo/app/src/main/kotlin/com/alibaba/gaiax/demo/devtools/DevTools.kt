package com.alibaba.gaiax.demo.devtools

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.DefaultLifecycleObserver
import com.alibaba.gaiax.demo.R
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
class DevTools : DefaultLifecycleObserver , IDevTools {
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

                it.findViewById<AppCompatButton>(R.id.window_btn_scan).setOnClickListener { view ->
                    var intent = Intent(context, GXQRCodeActivity::class.java)
                    intent.putExtra(TAG, TAG)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    context.startActivity(intent)
                }

                it.findViewById<AppCompatButton>(R.id.window_btn_fast_preview)
                    .setOnClickListener { view ->
                        launchFastPreviewType()
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
                        foldWindowToSmall(view)
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
                touchEvent { view, motionEvent ->

                }
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


    private fun launchFastPreviewType() {
        currentPreviewMode = GXClientToStudioMultiType.PREVIEW_AUTO
    }

    private fun launchPushPreviewType() {
        currentPreviewMode = GXClientToStudioMultiType.PREVIEW_MANUAL
    }

    private fun launchJsDebugType() {
        currentJSMode = GXClientToStudioMultiType.JS_BREAKPOINT
    }

    private fun disconnectStudioMultiType() {

    }

    private fun foldWindowToSmall(view: View?) {
        EasyFloat.updateFloat(TAG, width = 150, height = 150)
    }

    override fun getPreviewCurrentMode(): String {
        return currentPreviewMode
    }

    override fun getJSCurrentMode(): String {
        return currentJSMode
    }
}