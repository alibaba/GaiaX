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
import android.support.annotation.Keep
import android.support.v4.content.res.ResourcesCompat
import android.support.v7.widget.AppCompatImageView
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.render.view.GXRoundBorderDelegate
import com.alibaba.gaiax.template.GXCss
import com.alibaba.gaiax.template.GXMode
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * @suppress
 */
@Keep
open class GXImageView : AppCompatImageView, GXIImageView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    companion object {
        fun isNetUri(uri: String) = (uri.startsWith(NET_HTTP_PREFIX) || uri.startsWith(NET_HTTPS_PREFIX))

        fun isLocalUri(uri: String) = uri.startsWith(LOCAL_PREFIX)

        fun getLocalUri(uri: String) = uri.replace(LOCAL_PREFIX, "")

        const val NET_HTTP_PREFIX = "http:"
        const val NET_HTTPS_PREFIX = "https:"
        const val LOCAL_PREFIX = "local:"
    }

    override fun onBindData(data: JSONObject) {
        bindUri(data)
        bindDesc(this, data)
    }

    override fun setFrame(l: Int, t: Int, r: Int, b: Int): Boolean {
        updateMatrix(this, drawable)
        return super.setFrame(l, t, r, b)
    }

    private var mode: GXMode? = null

    override fun setImageStyle(gxCss: GXCss) = if (gxCss.style.mode != null) {
        this.mode = gxCss.style.mode
        val scaleType = gxCss.style.mode.getScaleType()
        this.scaleType = scaleType
    } else {
        this.scaleType = ScaleType.FIT_XY
    }

    private fun bindUri(data: JSONObject) {
        val uri = data.getString("value")?.trim() ?: ""
        when {
            isNetUri(uri) -> {
                bindImageUri(data, uri)
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

    private fun bindDefault() {
        setImageDrawable(null)
    }

    private fun bindImageUri(data: JSONObject, uri: String) {
        // 占位图仅对网络图生效
        data.getString(GXTemplateKey.GAIAX_PLACEHOLDER)?.let { resUri ->
            bindRes(resUri)
        }
        // Net
        // Glide.with(context).load(uri).into(this)
    }

    private fun bindRes(resUri: String) {
        try {
            val res: Int = this.resources.getIdentifier(resUri, "drawable", this.context.packageName)
            // 2020 1117
            // 增加主题，用于处理暗黑模式下，资源获取不正确的问题
            val theme: Resources.Theme = this.context.theme
            val drawable = ResourcesCompat.getDrawable(this.resources, res, theme)
            this.setImageDrawable(drawable)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun bindDesc(view: View, data: JSONObject) {

        // 原有无障碍逻辑
        val accessibilityDesc = data.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC)
        if (accessibilityDesc != null && accessibilityDesc.isNotEmpty()) {
            view.contentDescription = accessibilityDesc
            view.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
        } else {
            view.importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
        }

        // 新增Enable逻辑
        data.getBoolean(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)?.let { enable ->
            if (enable) {
                view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_YES
                if (accessibilityDesc == null || accessibilityDesc.isEmpty()) {
                    view.contentDescription = "图片"
                }
            } else {
                view.importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
            }
        }
    }

    private fun updateMatrix(imageView: ImageView, drawable: Drawable?) {
        if (drawable != null && imageView.scaleType == ScaleType.MATRIX) {
            val viewWidth: Int = imageView.layoutParams.width - imageView.paddingLeft - imageView.paddingRight
            val viewHeight: Int = imageView.layoutParams.height - imageView.paddingTop - imageView.paddingBottom
            val drawableWidth = drawable.intrinsicWidth
            val drawableHeight = drawable.intrinsicHeight
            if (drawableWidth > 0 && drawableHeight > 0) {
                val matrix: Matrix? = mode?.getMatrix(viewWidth, viewHeight, drawableWidth, drawableHeight)
                imageView.imageMatrix = matrix
            }
        }
    }

    private var delegate: GXRoundBorderDelegate? = null

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

    fun setRoundCornerRadius(topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
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

    fun setRoundCornerBorder(borderColor: Int, borderWidth: Float, topLeft: Float, topRight: Float, bottomLeft: Float, bottomRight: Float) {
        if (delegate == null) {
            delegate = GXRoundBorderDelegate()
        }
        delegate?.setRoundCornerBorder(borderColor, borderWidth, topLeft, topRight, bottomLeft, bottomRight)
    }

}