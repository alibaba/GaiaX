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

import android.graphics.Color
import android.graphics.ColorFilter
import android.graphics.Path
import android.graphics.PixelFormat
import android.graphics.drawable.Drawable
import android.support.annotation.CallSuper

/**
 * @suppress
 */
internal abstract class GXShadowDrawable(protected val shadowPath: Path) : Drawable() {

    override fun setAlpha(alpha: Int) = Unit

    override fun setColorFilter(colorFilter: ColorFilter?) = Unit

    override fun getOpacity(): Int = PixelFormat.TRANSLUCENT

    protected var shadowBlur: Float = 0f
        private set

    protected var shadowColor: Int = Color.TRANSPARENT
        private set

    protected var shadowInset: Boolean = false
        private set

    @CallSuper
    open fun setShadowBlur(blur: Float) {
        this.shadowBlur = blur
        onShadowChange(this.shadowBlur, this.shadowColor, this.shadowInset)
    }

    @CallSuper
    open fun setShadowColor(color: Int) {
        this.shadowColor = color
        onShadowChange(this.shadowBlur, this.shadowColor, this.shadowInset)
    }

    @CallSuper
    open fun setShadowInset(inset: Boolean) {
        this.shadowInset = inset
        onShadowChange(this.shadowBlur, this.shadowColor, this.shadowInset)
    }

    open fun invalidateCache() = Unit

    abstract fun onShadowChange(blur: Float, color: Int, inset: Boolean)

}