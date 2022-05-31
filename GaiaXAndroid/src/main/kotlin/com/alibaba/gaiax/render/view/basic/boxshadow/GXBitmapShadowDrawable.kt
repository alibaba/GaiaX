/*
 * Copyright (c) 2021, Alibaba Group Holding Limited;
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.alibaba.gaiax.render.view.basic.boxshadow

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Path
import android.graphics.Rect
import android.support.annotation.CallSuper

/**
 * @suppress
 */
internal abstract class GXBitmapShadowDrawable(shadowPath: Path) : GXShadowDrawable(shadowPath) {

    private var bitmap: Bitmap? = null

    private var bitmapDrawOver = false

    final override fun draw(canvas: Canvas) {
        createBitmap()
        val rawBitmap = bitmap ?: return
        drawBitmap(rawBitmap)
        canvas.drawBitmap(rawBitmap, -shadowBlur * 2, -shadowBlur * 2, null)
    }

    @CallSuper
    override fun onBoundsChange(bounds: Rect) {
        super.onBoundsChange(bounds)
        invalidateCache()
    }

    @CallSuper
    override fun setShadowBlur(blur: Float) {
        super.setShadowBlur(blur)
        invalidateCache()
    }

    private fun createBitmap() {
        val newWidth = ((bounds.width() + shadowBlur * 4)).toInt()
        val newHeight = ((bounds.height() + shadowBlur * 4)).toInt()
        val oldBitmap = bitmap
        if (oldBitmap != null && oldBitmap.width >= newWidth && oldBitmap.height >= newHeight) {
            return
        }
        oldBitmap?.recycle()
        bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)
        invalidateCache()
    }

    override fun invalidateCache() {
        bitmapDrawOver = false
    }

    private fun drawBitmap(bitmap: Bitmap) {
        if (bitmapDrawOver) return

        onDrawBitmap(bitmap)

        bitmapDrawOver = true
    }

    @CallSuper
    override fun onShadowChange(blur: Float, color: Int, inset: Boolean) {
        invalidateCache()
    }

    abstract fun onDrawBitmap(bitmap: Bitmap)


}