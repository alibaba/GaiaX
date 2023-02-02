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
        val Tag = "devtools"

        val instance by lazy {
            return@lazy DevTools()
        }
    }

    private var devtoolsContext: Context? = null

    private var scanResult = null

    private lateinit var observer: DevLifecycleObserver

//    private var launcher =
//        devtoolsContext?.registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            scanResult = it.data?.getStringExtra("SCAN_RESULT") ?: return@registerForActivityResult
//            Log.d("GaiaXDevTools", "StartActivityForResult() called scanResult = $scanResult")
//            connectStudioMultiType(scanResult)
//
//        }

    fun registerObserver(context: Context){
        observer = DevLifecycleObserver((context as AppCompatActivity).activityResultRegistry)
        (context as AppCompatActivity).lifecycle.addObserver(observer)
    }
    fun createDevToolsFloatWindow(context: Context) {
        devtoolsContext = context
        EasyFloat.with(context)
            .setLayout(R.layout.layout_dev_tools) {

                it.findViewById<AppCompatButton>(R.id.window_btn_scan).setOnClickListener { view ->
//                    val intent = Intent(context, GXQRCodeActivity::class.java)
//                    (context as Activity).startActivityForResult(intent, 0)
//                    (context as Activity)

                    observer.selectImage(context)
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
            .setTag(Tag)
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
        EasyFloat.dismiss(Tag)
    }

    private fun connectStudioMultiType(scanResult: String?) {

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

    class DevLifecycleObserver(private val registry: ActivityResultRegistry) :
        DefaultLifecycleObserver {
        lateinit var getContent: ActivityResultLauncher<Intent>

        override fun onCreate(owner: LifecycleOwner) {
            getContent = registry.register(
                "key",
                owner,
                ActivityResultContracts.StartActivityForResult()
            ) { uri ->
                Log.d(
                    "GaiaXDevTools",
                    "StartActivityForResult() called scanResult Dev = $uri"
                )
            }
        }

        fun selectImage(context: Context) {
            val intent = Intent(context, GXQRCodeActivity::class.java)
            getContent.launch(intent)
        }


    }
}