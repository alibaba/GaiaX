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
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import android.widget.AbsoluteLayout
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXRegisterCenter
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.render.view.GXRoundBorderDelegate
import com.alibaba.gaiax.template.GXTemplateKey

/**
 * @suppress
 */
open class GXView : AbsoluteLayout,
    GXIViewBindData,
    GXIRootView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private var gxTemplateContext: GXTemplateContext? = null

    override fun setTemplateContext(gxContext: GXTemplateContext) {
        this.gxTemplateContext = gxContext
    }

    override fun getTemplateContext(): GXTemplateContext? {
        return gxTemplateContext
    }

    override fun onBindData(data: JSONObject) {
        try {
            // 原有无障碍逻辑
            val accessibilityDesc = data.getString(GXTemplateKey.GAIAX_ACCESSIBILITY_DESC)
            if (accessibilityDesc != null && accessibilityDesc.isNotEmpty()) {
                contentDescription = accessibilityDesc
                importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_YES
            } else {
                importantForAccessibility = IMPORTANT_FOR_ACCESSIBILITY_NO
            }

            // 新增无障碍Enable逻辑
            data.getBoolean(GXTemplateKey.GAIAX_ACCESSIBILITY_ENABLE)?.let { enable ->
                importantForAccessibility = if (enable) {
                    View.IMPORTANT_FOR_ACCESSIBILITY_YES
                } else {
                    View.IMPORTANT_FOR_ACCESSIBILITY_NO
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            if (GXRegisterCenter.instance.processCompatible?.isPreventAccessibilityThrowException() == false) {
                throw e
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

    fun setRoundCornerRadius(radius: FloatArray) {
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

    fun setRoundCornerBorder(borderColor: Int, borderWidth: Float, radius: FloatArray) {
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