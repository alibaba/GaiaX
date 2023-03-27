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

package com.alibaba.gaiax.render.view.basic

import android.content.Context
import android.content.res.Resources
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.widget.ImageView
import androidx.annotation.Keep
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.res.ResourcesCompat
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.utils.GXAccessibilityUtils
import com.alibaba.gaiax.render.view.GXIRelease
import com.alibaba.gaiax.render.view.GXRoundBorderDelegate
import com.alibaba.gaiax.template.GXCss
import com.alibaba.gaiax.template.GXMode
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * @suppress
 */
@Keep
open class GXImageView : AppCompatImageView, GXIImageView, GXIRelease {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    companion object {
        const val NET_HTTP_PREFIX = "http:"
        const val NET_HTTPS_PREFIX = "https:"
        const val LOCAL_PREFIX = "local:"
    }

    private fun getDrawableByResId(imageView: ImageView, resId: Int): Drawable? {
        val theme: Resources.Theme = imageView.context.theme
        return ResourcesCompat.getDrawable(imageView.resources, resId, theme)
    }

    private fun getResIdByUri(imageView: ImageView, uri: String): Int {
        try {
            // drawable resource
            imageView.resources.getIdentifier(uri, "drawable", imageView.context.packageName)
                .takeIf { it != 0 }?.let {
                    return it
                }

            // mipmap resource
            imageView.resources.getIdentifier(uri, "mipmap", imageView.context.packageName)
                .takeIf { it != 0 }?.let {
                    return it
                }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return 0
    }

    private fun isNetUri(uri: String) =
        (uri.startsWith(NET_HTTP_PREFIX) || uri.startsWith(NET_HTTPS_PREFIX))

    private fun isLocalUri(uri: String) = uri.startsWith(LOCAL_PREFIX)

    private fun getLocalUri(uri: String) = uri.replace(LOCAL_PREFIX, "")

    override fun onBindData(data: JSONObject?) {
        bindUri(data)
        bindDesc(data)
    }

    override fun onResetData() {
        this.setImageDrawable(null)
    }

    open fun bindUri(data: JSONObject?) {
        val uri = data?.getString(GXTemplateKey.GAIAX_VALUE)?.trim() ?: ""
        when {
            isNetUri(uri) -> {
                data?.let {
                    // 占位图仅对网络图生效
                    val placeholder = data.getString(GXTemplateKey.GAIAX_PLACEHOLDER)
                    bindNetUri(data, uri, placeholder)
                }
            }
            isLocalUri(uri) -> {
                val finalUri = getLocalUri(uri)
                bindRes(finalUri)
            }
            else -> {
                bindDefault()
            }
        }
    }

    open fun bindDefault() {
        setImageDrawable(null)
    }

    open fun bindNetUri(data: JSONObject, uri: String, placeholder: String?) {
        // throw IllegalArgumentException("GXImageView bindNetUri not implement")
    }

    open fun bindRes(resUri: String) {
        try {
            val res: Int = getResIdByUri(this, resUri)
            val drawable = getDrawableByResId(this, res)
            this.setImageDrawable(drawable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    open fun getRes(resUri: String): Int {
        return try {
            getResIdByUri(this, resUri)
        } catch (e: Exception) {
            e.printStackTrace()
            0
        }
    }

    open fun bindDesc(data: JSONObject?) {
        GXAccessibilityUtils.accessibilityOfImage(this, data)
    }

    var gxTemplateContext: GXTemplateContext? = null
    private var delegate: GXRoundBorderDelegate? = null

    private fun updateMatrix(imageView: ImageView, drawable: Drawable?) {
        if (drawable != null && imageView.scaleType == ScaleType.MATRIX) {
            val viewWidth: Int =
                imageView.layoutParams.width - imageView.paddingLeft - imageView.paddingRight
            val viewHeight: Int =
                imageView.layoutParams.height - imageView.paddingTop - imageView.paddingBottom
            val drawableWidth = drawable.intrinsicWidth
            val drawableHeight = drawable.intrinsicHeight
            if (drawableWidth > 0 && drawableHeight > 0) {
                val matrix: Matrix? =
                    mode?.getMatrix(viewWidth, viewHeight, drawableWidth, drawableHeight)
                imageView.imageMatrix = matrix
            }
        }
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        updateMatrix(this, drawable)
        return super.setFrame(l, t, r, b)
    }

    private var mode: GXMode? = null

    override fun setImageStyle(gxTemplateContext: GXTemplateContext, gxCss: GXCss) {
        val gxMode = gxCss.style.mode
        if (gxMode != null) {
            this.mode = gxMode
            val scaleType = gxMode.getScaleType()
            this.scaleType = scaleType
        } else {
            this.scaleType = ScaleType.FIT_XY
        }
        this.gxTemplateContext = gxTemplateContext
    }

    override fun draw(canvas: Canvas?) {
        val measureWidth = measuredWidth.toFloat()
        val measureHeight = measuredHeight.toFloat()
        if (delegate?.isNeedRound(canvas, measureWidth, measureHeight) == true) {
            delegate?.draw(canvas, measureWidth, measureHeight) {
                super.draw(canvas)
            }
        } else {
            super.draw(canvas)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        val measureWidth = measuredWidth.toFloat()
        val measureHeight = measuredHeight.toFloat()
        if (delegate?.isNeedRound(canvas, measureWidth, measureHeight) == true) {
            delegate?.onDraw(canvas, measureWidth, measureHeight)
        }
    }

    override fun setRoundCornerRadius(radius: FloatArray) {
        if (delegate == null) {
            delegate = GXRoundBorderDelegate()
        }
        delegate?.setRoundCornerRadius(radius)
    }

    fun setRoundCornerRadius(
        topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float
    ) {
        if (delegate == null) {
            delegate = GXRoundBorderDelegate()
        }
        delegate?.setRoundCornerRadius(topLeft, topRight, bottomLeft, bottomRight)
    }

    override fun setRoundCornerBorder(borderColor: Int, borderWidth: Float, radius: FloatArray) {
        if (delegate == null) {
            delegate = GXRoundBorderDelegate()
        }
        delegate?.setRoundCornerBorder(borderColor, borderWidth, radius)
    }

    fun setRoundCornerBorder(
        borderColor: Int,
        borderWidth: Float,
        topLeft: Float,
        topRight: Float,
        bottomLeft: Float,
        bottomRight: Float
    ) {
        if (delegate == null) {
            delegate = GXRoundBorderDelegate()
        }
        delegate?.setRoundCornerBorder(
            borderColor, borderWidth, topLeft, topRight, bottomLeft, bottomRight
        )
    }

    override fun release() {
        this.gxTemplateContext = null
    }
}