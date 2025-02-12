package com.alibaba.gaiax.render.view.blur

import android.content.Context
import android.graphics.Bitmap
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RSRuntimeException
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur

class GXBlurImpl {

    private var renderScript: RenderScript? = null
    private var blurScript: ScriptIntrinsicBlur? = null
    private var blurInput: Allocation? = null
    private var blurOutput: Allocation? = null

    fun prepare(context: Context?, buffer: Bitmap?, radius: Float): Boolean {
        try {
            if (context == null || buffer == null || radius <= 0) {
                return false
            }
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
        } catch (e: Exception) {
            e.printStackTrace()
            release()
            return false
        }
    }

    fun release() {
        try {
            blurInput?.destroy()
            blurInput = null
            blurOutput?.destroy()
            blurOutput = null
            blurScript?.destroy()
            blurScript = null
            renderScript?.destroy()
            renderScript = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun blur(input: Bitmap?, output: Bitmap?) {
        try {
            blurInput?.copyFrom(input)
            blurScript?.setInput(blurInput)
            blurScript?.forEach(blurOutput)
            blurOutput?.copyTo(output)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}