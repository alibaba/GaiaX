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

package com.alibaba.gaiax.render.node.text

import android.app.Activity
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import android.view.ViewGroup
import androidx.core.util.Pools
import com.alibaba.gaiax.render.view.GXViewFactory
import com.alibaba.gaiax.render.view.GXViewKey
import com.alibaba.gaiax.render.view.basic.GXText
import com.alibaba.gaiax.render.view.setFontSize
import com.alibaba.gaiax.render.view.setTextFontFamily
import java.lang.ref.WeakReference

/**
 * @suppress
 */
object GXMeasureViewPool {

    private val CPU_COUNT = Runtime.getRuntime().availableProcessors()
    private val MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1

    private val pool = Pools.SynchronizedPool<WeakReference<GXText>>(MAXIMUM_POOL_SIZE)

    private var cacheInit = false
    private var cacheTypeFace: Typeface? = null
    private var cacheTextSize: Float = 0f
    private var cacheLineSpacingExtra: Float = 0f
    private var cacheLineSpacingMultiplier: Float = 0f

    fun obtain(context: Context): GXText {
        val view = obtainInner(context)
        if (!cacheInit) {
            cacheInit = true
            cacheTypeFace = view.typeface
            cacheTextSize = view.textSize
            cacheLineSpacingExtra = view.lineSpacingExtra
            cacheLineSpacingMultiplier = view.lineSpacingMultiplier
        }
        return view
    }

    private fun obtainInner(context: Context): GXText {
        val acquire = pool.acquire()
        if (acquire != null) {
            val oldText = acquire.get()

            if (oldText != null && oldText.context is Activity) {

                // 如果不是同一个activity，那么应该释放之前的GXText，以免造成activity的内存泄漏
                // pool.acquire会导致oldText退出缓存，不再使用时等在GC回收
                if (oldText.context != context) {
                    return createTv(context)
                }

                // 如果缓存池中的GXText的宿主（activity）已经被销毁，那么应该创建新的GXText
                // https://github.com/alibaba/GaiaX/issues/220
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    if ((oldText.context as Activity).isDestroyed) {
                        return createTv(context)
                    }
                }

                // 使用旧的GXText缓存，用于计算FitContent
                return oldText
            }

            return createTv(context)
        }

        return createTv(context)
    }

    private fun createTv(context: Context): GXText {
        val gxText = GXViewFactory.createView<GXText>(context, GXViewKey.VIEW_TYPE_TEXT)
        gxText.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        return gxText
    }

    fun release(gxText: GXText) {
        try {
            resetTv(gxText)
            pool.release(WeakReference(gxText))
        } catch (e: Exception) {
        }
    }

    private fun resetTv(view: GXText) {
        // reset property
        view.setTextFontFamily(cacheTypeFace)
        view.setFontSize(cacheTextSize)
        view.setLineSpacing(cacheLineSpacingExtra, cacheLineSpacingMultiplier)
        view.setSingleLine(false)
        view.maxLines = Integer.MAX_VALUE
        view.setPadding(0, 0, 0, 0)
    }
}