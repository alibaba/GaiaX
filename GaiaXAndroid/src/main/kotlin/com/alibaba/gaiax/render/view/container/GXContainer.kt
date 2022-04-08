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

package com.alibaba.gaiax.render.view.container

import android.content.Context
import android.graphics.Canvas
import android.support.annotation.Keep
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import com.alibaba.fastjson.JSONObject
import com.alibaba.gaiax.GXTemplateEngine
import com.alibaba.gaiax.context.GXTemplateContext
import com.alibaba.gaiax.render.view.GXIRootView
import com.alibaba.gaiax.render.view.GXIViewBindData
import com.alibaba.gaiax.render.view.GXRoundBorderDelegate


/**
 * @suppress
 */
@Keep
open class GXContainer : RecyclerView, GXIViewBindData, GXIRootView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    private fun finalize() {
        if (gxTemplateContext?.rootView?.get() == this) {
            if (gxTemplateContext?.rootNode?.isRoot == true) {
                GXTemplateEngine.instance.render.onDestroy(gxTemplateContext!!)
                gxTemplateContext = null
            }
        }
    }

    override fun onBindData(data: JSONObject) {
    }

    private var gxTemplateContext: GXTemplateContext? = null

    override fun manualRelease() {
        this.finalize()
    }

    override fun setTemplateContext(gxContext: GXTemplateContext) {
        this.gxTemplateContext = gxContext
    }

    override fun getTemplateContext(): GXTemplateContext? {
        return gxTemplateContext
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