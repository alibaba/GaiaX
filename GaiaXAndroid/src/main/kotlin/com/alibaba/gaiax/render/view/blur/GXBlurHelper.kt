package com.alibaba.gaiax.render.view.blur

import android.graphics.*
import android.os.Build
import android.view.View
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import com.alibaba.gaiax.render.view.basic.GXView

@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
class GXBlurHelper(private val host: GXView) {

    // default 4
    var sampling: Int = 4

    // default 10dp (0 < r <= 25)
    var radius: Float = 25f

    // default #aaffffff
    // Color.parseColor("#70FFFFFF")
    // var color = Color.parseColor("#70FFFFFF")
    var color = Color.TRANSPARENT

    var renderingCount = 0

    var isCaptureViewDrawing: Boolean = false

    private val blurImpl = GXBlurImpl()

    private var bitmapToBlur: Bitmap? = null

    var blurredBitmap: Bitmap? = null

    private var blurringCanvas: Canvas? = null

    var isRendering = false

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

    fun innerRelease() {
        releaseBitmap()
        blurImpl.release()
    }

    private fun prepare(): Boolean {
        val width = host.layoutParams.width
        val height = host.layoutParams.height

        if (radius == 0f || width == 0 && height == 0) {
            innerRelease()
            return false
        }

        val scaledWidth = width / sampling
        val scaledHeight = height / sampling

        if (blurringCanvas == null || blurredBitmap == null) {
            releaseBitmap()
            var r = false
            try {
                bitmapToBlur = Bitmap.createBitmap(
                    scaledWidth,
                    scaledHeight,
                    Bitmap.Config.ARGB_8888
                )
                if (bitmapToBlur == null) {
                    return false
                }
                blurringCanvas = bitmapToBlur?.let { Canvas(it) }
                blurredBitmap = Bitmap.createBitmap(
                    scaledWidth,
                    scaledHeight,
                    Bitmap.Config.ARGB_8888
                )
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

            blurImpl.prepare(host.context, bitmapToBlur, radius)
        }

        host.gxTemplateContext?.let {
            if (it.bindDataCount <= 0) {
                return false
            }
            it.bindDataCount--
        }

        return true
    }

    private val preDrawListener = ViewTreeObserver.OnPreDrawListener {
        val locations = IntArray(2)
        captureView?.let { captureView ->
            if (host.isShown && prepare()) {

                captureView.getLocationOnScreen(locations)

                var x = -locations[0]
                var y = -locations[1]

                host.getLocationOnScreen(locations)

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
                            // 缩放画布
                            blurringCanvas.scale(1f / sampling.toFloat(), 1f / sampling.toFloat())
                            // 平移画布
                            blurringCanvas.translate(-x.toFloat(), -y.toFloat())
                            if (captureView.background != null) {
                                captureView.background.draw(blurringCanvas)
                            }
                            // 绘制父View到画布中，此处要有标记，否则会把BlurView的子View也绘制进去
                            isCaptureViewDrawing = true
                            captureView.draw(blurringCanvas)
                            isCaptureViewDrawing = false
                        } catch (ignored: Exception) {
                        } finally {
                            isRendering = false
                            renderingCount--
                            blurringCanvas.restoreToCount(rc)
                        }

                        // 对画布的Bitmap进行高斯模糊，并将结果copy到blurredBitmap中
                        blurImpl.blur(bitmapToBlur, blurredBitmap)

                        host.invalidate()
                    }
                }
            }
        }
        true
    }

    fun onAttachedToWindow() {
        if (host.gxBackdropFilter != null) {
            captureView = host.gxTemplateContext?.rootView
            if (captureView != null) {
                captureView?.viewTreeObserver?.addOnPreDrawListener(preDrawListener)
            }
        }
    }

    fun onDetachedFromWindow() {
        if (host.gxBackdropFilter != null) {
            if (captureView != null) {
                captureView?.viewTreeObserver?.removeOnPreDrawListener(preDrawListener)
            }
            innerRelease()
        }
    }

    private fun drawBlurredBitmap(canvas: Canvas, bitmap: Bitmap) {
        rectSrc.right = bitmap.width
        rectSrc.bottom = bitmap.height
        rectDst.right = host.layoutParams.width
        rectDst.bottom = host.layoutParams.height
        paint.flags = Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG
        paint.colorFilter = PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP)
        canvas.drawBitmap(bitmap, rectSrc, rectDst, paint)
    }

    fun callbackDispatchDraw(canvas: Canvas, callback: () -> Unit) {
        if (!isCaptureViewDrawing) {
            blurredBitmap?.let {
                drawBlurredBitmap(canvas, it)
            }
            callback()
        }
    }

    fun callbackDraw(canvas: Canvas, callback: () -> Unit) {
        if (isRendering) {
            // Quit here, don't draw views above me
        } else if (renderingCount > 0) {
            // Doesn't support blurview overlap on another blurview
        } else {
            callback()
        }
    }
}