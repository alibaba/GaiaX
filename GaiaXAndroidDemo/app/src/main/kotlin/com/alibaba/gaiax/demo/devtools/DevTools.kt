package com.alibaba.gaiax.demo.devtools

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.alibaba.gaiax.demo.R
import com.alibaba.gaiax.demo.fastpreview.GXFastPreviewActivity
import com.alibaba.gaiax.demo.fastpreview.GXQRCodeActivity
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern

/**
 *  @author: shisan.lms
 *  @date: 2023-02-02
 *  Description:
 */
class DevTools : DefaultLifecycleObserver {
    companion object {
        val TAG = "devtools"

        val instance by lazy {
            return@lazy DevTools()
        }
    }

    private var devtoolsContext: Context? = null

    private var scanResult: String = ""

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
                        foldWindowToSmall(view)
                    }

                it.findViewById<AppCompatButton>(R.id.window_btn_push_preview)
                    .setOnClickListener { view ->
                        foldWindowToSmall(view)
                    }

                it.findViewById<AppCompatButton>(R.id.window_btn_js_debug)
                    .setOnClickListener { view ->
                        foldWindowToSmall(view)
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
    }

    fun dismissDevTools() {
        EasyFloat.dismiss(TAG)
    }

    fun connectStudioMultiType(result: String) {
        Log.d(TAG, "connectStudioMultiType: $result")
        scanResult = result
    }

    private fun launchFastPreviewType(view: View?) {

    }

    private fun launchPushPreviewType(view: View?) {

    }

    private fun launchJsDebugType(view: View?) {

    }

    private fun disconnectStudioMultiType(view: View?) {

    }

    private fun foldWindowToSmall(view: View?) {
        EasyFloat.updateFloat(null, width = 150, height = 150)
    }
}