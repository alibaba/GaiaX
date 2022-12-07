package com.alibaba.gaiax.render.view.basic

import android.content.Context
import android.graphics.*
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
open class GXBlurView(context: Context?) : GXView(context!!) {

    // default 4
    var sampling: Int = 4

    // default 10dp (0 < r <= 25)
    var radius: Float = 25f

    // default #aaffffff
    // Color.parseColor("#70FFFFFF")
    // var color = Color.parseColor("#70FFFFFF")
    var color = Color.TRANSPARENT

    private var renderingCount = 0

    private var isCaptureViewDrawing: Boolean = false

    private val blurImpl = GXBlurImpl()

    private var dirtyToDraw = false

    private var bitmapToBlur: Bitmap? = null

    private var blurredBitmap: Bitmap? = null

    private var blurringCanvas: Canvas? = null

    private var isRendering = false

    private val paint = Paint()

    private val rectSrc = Rect()

    private val rectDst = Rect()

    private var captureView: View? = null

    private fun releaseBitmap() {
        bitmapToBlur?.recycle()
        bitmapToBlur = null
        blurredBitmap?.recycle()
        blurredBitmap = null
    }

    private fun innerRelease() {
        releaseBitmap()
        blurImpl.release()
    }

    private fun prepare(): Boolean {
        val width = width
        val height = height

        if (radius == 0f || width == 0 && height == 0) {
            innerRelease()
            return false
        }

        val scaledWidth = width / sampling
        val scaledHeight = height / sampling

        var dirty = dirtyToDraw
        if (blurringCanvas == null || blurredBitmap == null) {
            dirty = true
            releaseBitmap()
            var r = false
            try {
                bitmapToBlur = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
                if (bitmapToBlur == null) {
                    return false
                }
                blurringCanvas = bitmapToBlur?.let { Canvas(it) }
                blurredBitmap = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)
                if (blurredBitmap == null) {
                    return false
                }
                r = true
            } catch (e: OutOfMemoryError) {
                // Bitmap.createBitmap() may cause OOM error
                // Simply ignore and fallback
            } finally {
                if (!r) {
                    innerRelease()
                }
            }
        }
        if (dirty) {
            dirtyToDraw = blurImpl.prepare(context, bitmapToBlur, radius)
        }
        return true
    }

    private val preDrawListener = ViewTreeObserver.OnPreDrawListener {
        val locations = IntArray(2)
        captureView?.let { captureView ->
            if (isShown && prepare()) {

                captureView.getLocationOnScreen(locations)

                var x = -locations[0]
                var y = -locations[1]

                getLocationOnScreen(locations)

                x += locations[0]
                y += locations[1]

                blurringCanvas?.let { blurringCanvas ->
                    bitmapToBlur?.let { bitmapToBlur ->
                        // just erase transparent
                        bitmapToBlur.eraseColor(color and 0xffffff)
                        val rc = blurringCanvas.save()
                        isRendering = true
                        renderingCount++
                        try {
                            blurringCanvas.scale(1f / sampling.toFloat(), 1f / sampling.toFloat())
                            blurringCanvas.translate(-x.toFloat(), -y.toFloat())
                            if (captureView.background != null) {
                                captureView.background.draw(blurringCanvas)
                            }
                            isCaptureViewDrawing = true
                            captureView.draw(blurringCanvas)
                            isCaptureViewDrawing = false
                        } catch (ignored: Exception) {
                        } finally {
                            isRendering = false
                            renderingCount--
                            blurringCanvas.restoreToCount(rc)
                        }
                        blurImpl.blur(bitmapToBlur, blurredBitmap)

                        invalidate()
                    }
                }
            }
        }
        true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (gxBackdropFilter != null) {
            captureView = gxTemplateContext?.rootView
            if (captureView != null) {
                captureView?.viewTreeObserver?.addOnPreDrawListener(preDrawListener)
            }
        }
    }

    override fun onDetachedFromWindow() {
        if (gxBackdropFilter != null) {
            if (captureView != null) {
                captureView?.viewTreeObserver?.removeOnPreDrawListener(preDrawListener)
            }
            innerRelease()
        }
        super.onDetachedFromWindow()
    }

    override fun draw(canvas: Canvas) {
        if (gxBackdropFilter != null) {
            if (isRendering) {
                // Quit here, don't draw views above me
            } else if (renderingCount > 0) {
                // Doesn't support blurview overlap on another blurview
            } else {
                super.draw(canvas)
            }
        } else {
            super.draw(canvas)
        }
    }

    override fun dispatchDraw(canvas: Canvas) {
        if (gxBackdropFilter != null) {
            if (!isCaptureViewDrawing) {
                blurredBitmap?.let {
                    drawBlurredBitmap(canvas, it)
                }
                super.dispatchDraw(canvas)
            }
        } else {
            super.dispatchDraw(canvas)
        }
    }

    private fun drawBlurredBitmap(canvas: Canvas, bitmap: Bitmap) {
        rectSrc.right = bitmap.width
        rectSrc.bottom = bitmap.height
        rectDst.right = layoutParams.width
        rectDst.bottom = layoutParams.height
        canvas.drawBitmap(bitmap, rectSrc, rectDst, paint)
    }

    init {
        paint.flags = Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
    }
}