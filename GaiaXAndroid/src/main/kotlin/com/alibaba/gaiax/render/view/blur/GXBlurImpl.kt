package com.alibaba.gaiax.render.view.blur

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import android.renderscript.*
import androidx.annotation.RequiresApi

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
class GXBlurImpl {

    private var renderScript: RenderScript? = null
    private var blurScript: ScriptIntrinsicBlur? = null
    private var blurInput: Allocation? = null
    private var blurOutput: Allocation? = null

    fun prepare(context: Context?, buffer: Bitmap?, radius: Float): Boolean {
        if (renderScript == null) {
            try {
                renderScript = RenderScript.create(context)
                blurScript = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript))
            } catch (e: RSRuntimeException) {
                // In release mode, just ignore
                release()
                return false
            }
        }
        blurScript?.setRadius(radius)
        blurInput = Allocation.createFromBitmap(
            renderScript,
            buffer,
            Allocation.MipmapControl.MIPMAP_NONE,
            Allocation.USAGE_SCRIPT
        )
        blurInput?.let {
            blurOutput = Allocation.createTyped(renderScript, it.type)
        }
        return true
    }

    fun release() {
        blurInput?.destroy()
        blurInput = null
        blurOutput?.destroy()
        blurOutput = null
        blurScript?.destroy()
        blurScript = null
        renderScript?.destroy()
        renderScript = null
    }

    fun blur(input: Bitmap?, output: Bitmap?) {
        blurInput?.copyFrom(input)
        blurScript?.setInput(blurInput)
        blurScript?.forEach(blurOutput)
        blurOutput?.copyTo(output)
    }
}