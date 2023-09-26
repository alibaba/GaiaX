package com.alibaba.gaiax.demo.devtools

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.demo.R
import com.alibaba.gaiax.demo.fastpreview.GXFastPreviewActivity
import com.alibaba.gaiax.demo.fastpreview.GXQRCodeActivity
import com.alibaba.gaiax.studio.GXClientToStudioMultiType
import com.alibaba.gaiax.studio.GX_CONNECT_URL
import com.alibaba.gaiax.studio.IDevTools
import com.alibaba.gaiax.studio.saveInLocal
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern
import com.alibaba.gaiax.js.GXJSEngineFactory

/**
 *  @author: shisan.lms
 *  @date: 2023-02-02
 *  Description:
 */
class DevTools : IDevTools {
    companion object {
        const val TAG = "Devtools"

        val instance by lazy {
            return@lazy DevTools()
        }
    }

    private var devtoolsAppContext: Context? = null

    private var currentPreviewMode = GXClientToStudioMultiType.PREVIEW_NONE

    private var currentJSMode = GXClientToStudioMultiType.JS_DEFAULT

    private var connectedStateView: RadioButton? = null

    private var jsDebuggerTypeListener: IDevTools.DevToolsDebuggingTypeListener? = null

    fun createDevToolsFloatWindow(context: Context) {
        devtoolsAppContext = context
        EasyFloat.with(context)
            .setLayout(R.layout.layout_dev_tools) { rootView ->

                val windowWidth = rootView.layoutParams.width
                val windowHeight = rootView.layoutParams.height

                val logoView = rootView.findViewById<ImageView>(R.id.window_gaia_logo)
                connectedStateView = rootView.findViewById(R.id.window_btn_connected_state)

                // 更新UI，当冷启重连时需要更改默认ui
                changeDevToolsConnectedStateView()


                logoView.setOnClickListener { view ->
                    view.visibility = View.INVISIBLE
                    EasyFloat.updateFloat(TAG, width = windowWidth, height = windowHeight)
                }

                rootView.findViewById<AppCompatButton>(R.id.window_btn_scan).setOnClickListener {
                    openQRCodeActivity(context)
                }

                rootView.findViewById<AppCompatButton>(R.id.window_btn_fast_preview)
                    .setOnClickListener {
                        launchAction(context) { context -> openFastPreviewType(context) }
                    }

                rootView.findViewById<AppCompatButton>(R.id.window_btn_js_debug)
                    .setOnClickListener {
                        val jsModeView = rootView.findViewById<RadioButton>(R.id.window_btn_js_type)
                        if (this.currentJSMode == GXClientToStudioMultiType.JS_DEFAULT) {
                            launchAction(context) {
                                launchJsType(GXClientToStudioMultiType.JS_BREAKPOINT)
                                changeJSModeView(jsModeView)
                            }
                        } else {
                            launchAction(context) {
                                launchJsType(GXClientToStudioMultiType.JS_DEFAULT)
                                changeJSModeView(jsModeView)
                            }
                        }
                    }

                rootView.findViewById<AppCompatButton>(R.id.window_btn_cancel_dev)
                    .setOnClickListener {
                        disconnectStudioMultiType()
                    }

                rootView.findViewById<AppCompatButton>(R.id.window_btn_close_window)
                    .setOnClickListener {
                        foldWindowToSmall(logoView)
                    }

            }
            .setLocation(20, 200)
            .setShowPattern(ShowPattern.FOREGROUND)
            .setDragEnable(true)
            .setTag(TAG)
            .registerCallback {}
            .show()
        GXClientToStudioMultiType.instance.init(context)
    }

    fun dismissDevTools() {
        // TODO: 仅隐藏DevTools，未关闭DevTools
        EasyFloat.dismiss(TAG)
    }

    /**
     * @param result e.g. gaiax://gaiax/preview?url=ws%3A%2F%2F30.78.148.174%3A9898&type=connect
     */
    fun connectStudioMultiType(result: String) {
        Log.d(TAG, "connectStudioMultiType called with scanResult: $result")
        val scanResult: String = result
        val params = GXClientToStudioMultiType.instance.getParams(scanResult)
        if (params == null) {
            Toast.makeText(devtoolsAppContext, "地址解析失败，请刷新二维码", Toast.LENGTH_SHORT).show()
            return
        }

        // 连接成功后，将本次的websocket的url记录下来
        devtoolsAppContext?.let {
            saveInLocal(it, GX_CONNECT_URL, params.toString())
            connectReally(it, params)
        }
    }

    fun connectReally(context: Context, params: JSONObject) {
        GXClientToStudioMultiType.instance.setDevTools(this)
        GXClientToStudioMultiType.instance.manualConnect(context, params)
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
        val intent = Intent(devtoolsAppContext, GXFastPreviewActivity::class.java)
        intent.putExtra(GXFastPreviewActivity.GAIA_STUDIO_MODE, GXFastPreviewActivity.GAIA_STUDIO_MODE_MULTI)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
        //修改DevTools状态
        currentPreviewMode = GXClientToStudioMultiType.PREVIEW_AUTO
        GXClientToStudioMultiType.instance.sendMsgForGetTemplateData("")
    }

    private fun launchJsType(mode: String) {
        currentJSMode = mode
        if (jsDebuggerTypeListener == null) {
            jsDebuggerTypeListener = GXJSEngineFactory.instance.devToolsDebuggingTypeListener
        }
        jsDebuggerTypeListener?.onDevToolsJSModeChanged(currentJSMode)
    }

    private fun disconnectStudioMultiType() {
        if (GXClientToStudioMultiType.instance.isGaiaStudioConnected() == true) {
            GXClientToStudioMultiType.instance.sendMsgForDisconnect()
        } else {
            Toast.makeText(devtoolsAppContext, "当前未连接GaiaStudio", Toast.LENGTH_SHORT).show()
        }
    }

    private fun foldWindowToSmall(view: ImageView?) {
        view?.visibility = View.VISIBLE
        EasyFloat.updateFloat(TAG, width = 150, height = 150)
    }

    private fun changeRadioBtnState(view: RadioButton, targetState: Boolean, defaultText: Array<String> = arrayOf("已连接", "未连接")) {
        val currentState = view.isChecked
        if (targetState == currentState) {
            //一致无变化
        } else {
            //不一致，切换为与Socket结果一致
            if (targetState) {
                view.text = defaultText[0]
                view.isChecked = true

                view.setTextColor(Color.WHITE)
            } else {
                view.text = defaultText[1]
                view.isChecked = false
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    devtoolsAppContext?.resources?.getColor(R.color.viewfinder_text_color, devtoolsAppContext?.theme)?.let { view.setTextColor(it) }
                } else {
                    devtoolsAppContext?.resources?.getColor(R.color.viewfinder_text_color)?.let { view.setTextColor(it) }
                }
            }
        }
    }

    private fun changeJSModeView(view: RadioButton) {
        val isJsDebugMode = (this.currentJSMode == GXClientToStudioMultiType.JS_BREAKPOINT)
        val viewTextArray = arrayOf("断点模式", "日志模式")
        if (GXClientToStudioMultiType.instance.isGaiaStudioConnected() == true) {
            changeRadioBtnState(view, isJsDebugMode, viewTextArray)
        }
    }

    override fun changeDevToolsConnectedStateView() {
        if (this.connectedStateView != null) {
            GXClientToStudioMultiType.instance.isGaiaStudioConnected()?.let { this.changeRadioBtnState(this.connectedStateView!!, it) }
        }
    }

    override fun getPreviewCurrentMode(): String {
        return currentPreviewMode
    }

    override fun getJSCurrentMode(): String {
        return currentJSMode
    }
}