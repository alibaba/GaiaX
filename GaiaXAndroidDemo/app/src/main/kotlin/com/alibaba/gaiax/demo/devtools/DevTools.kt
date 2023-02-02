package com.alibaba.gaiax.demo.devtools

import android.content.Context
import android.view.View
import androidx.appcompat.widget.AppCompatButton
import com.alibaba.gaiax.demo.R
import com.lzf.easyfloat.EasyFloat
import com.lzf.easyfloat.enums.ShowPattern

/**
 *  @author: shisan.lms
 *  @date: 2023-02-02
 *  Description:
 */
class DevTools {
    companion object {
        val Tag = "devtools"

        val instance by lazy {
            return@lazy DevTools()
        }
    }

    fun createDevToolsFloatWindow(context: Context) {
        EasyFloat.with(context)
            .setLayout(R.layout.layout_dev_tools) {
                it.findViewById<AppCompatButton>(R.id.window_btn_close_window).setOnClickListener {
                    EasyFloat.updateFloat(null, width = 150, height = 150)
                }

            }
            .setShowPattern(ShowPattern.FOREGROUND)
            .setDragEnable(true)
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

    private fun connectStudioMultiType(view: View?) {

    }

    private fun launchFastPreviewType(view: View?){

    }

    private fun launchPushPreviewType(view: View?){

    }

    private fun launchJsDebugType(view: View?){

    }

    private fun disconnectStudioMultiType(view: View?){

    }

    private fun foldWindowToSmall(view: View?){

    }
}