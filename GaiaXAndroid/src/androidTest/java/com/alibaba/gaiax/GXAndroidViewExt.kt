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

package com.alibaba.gaiax

import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.widget.AbsoluteLayout
import android.widget.TextView
import com.alibaba.gaiax.render.view.container.GXContainer
import com.alibaba.gaiax.template.GXSize.Companion.dpToPx

fun View.x(): Float {
    return (this.layoutParams as? AbsoluteLayout.LayoutParams)?.x?.toFloat() ?: 0F
}

fun View.y(): Float {
    return (this.layoutParams as? AbsoluteLayout.LayoutParams)?.y?.toFloat() ?: 0F
}

fun View.width(): Float {
    return this.layoutParams.width.toFloat()
}

fun View.height(): Float {
    return this.layoutParams.height.toFloat()
}

fun View.lineHeight(): Float {
    return (this as TextView).lineHeight.toFloat()
}

fun View.child(index: Int): View {
    if (this is ViewGroup) {
        return getChildAt(index)
    }
    throw IllegalArgumentException("Not ViewGroup")
}

fun <T> View.child(index: Int): T {
    if (this is ViewGroup) {
        return this.getChildAt(index) as T
    }
    throw IllegalArgumentException("Not ViewGroup")
}

fun View.childCount(): Int {
    if (this is ViewGroup) {
        return this.childCount
    }
    return 0
}

fun View.executeRecyclerView() {
    if (this is GXContainer) {
        this.measure(View.MeasureSpec.AT_MOST, View.MeasureSpec.AT_MOST)
        this.layout(0, 0, 1080F.dpToPx().toInt(), 1080F.dpToPx().toInt())
    }
}

fun Any.getFieldInt(name: String): Int {
    val declaredField = this.javaClass.getDeclaredField(name)
    declaredField.isAccessible = true
    return declaredField.getInt(this)
}

fun Any.getFieldAny(name: String): Any {
    val declaredField = this.javaClass.getDeclaredField(name)
    declaredField.isAccessible = true
    return declaredField.get(this)
}

fun Any.getSuperFieldAny(name: String): Any {
    val declaredField = this.javaClass.superclass.getDeclaredField(name)
    declaredField.isAccessible = true
    return declaredField.get(this)
}